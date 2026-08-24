/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.json.JsonData;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.index.constants.IndexMappingsConstants;
import com.liferay.portal.search.elasticsearch8.internal.util.ConversionUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.IndexUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentResponse;
import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Dylan Rebelak
 */
public class ElasticsearchSearchEngineAdapterDocumentRequestTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			ElasticsearchSearchEngineAdapterDocumentRequestTest.class);

		_elasticsearchFixture.setUp();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() throws Exception {
		_searchEngineAdapter = createSearchEngineAdapter(_elasticsearchFixture);

		_elasticsearchClient = _elasticsearchFixture.getElasticsearchClient();

		_elasticsearchIndicesClient = _elasticsearchClient.indices();

		_documentFixture.setUp();

		_createIndex();
	}

	@After
	public void tearDown() throws Exception {
		_deleteIndex();

		_documentFixture.tearDown();
	}

	@Test
	public void testExecuteBatchModeAfterExactMultipleFlush() throws Exception {
		try (MockedStatic<SearchEngineHelperUtil>
				searchEngineHelperUtilMockedStatic = Mockito.mockStatic(
					SearchEngineHelperUtil.class)) {

			ExecutorService executorService = Mockito.mock(
				ExecutorService.class);

			Mockito.doAnswer(
				invocation -> {
					Runnable runnable = invocation.getArgument(0);

					runnable.run();

					return null;
				}
			).when(
				executorService
			).execute(
				Mockito.any(Runnable.class)
			);

			searchEngineHelperUtilMockedStatic.when(
				SearchEngineHelperUtil::getDocumentsConsumerExecutorService
			).thenReturn(
				executorService
			);

			try (SafeCloseable safeCloseable = SearchContext.openBatchMode(
					false)) {

				for (int i = 0; i < Indexer.DEFAULT_INTERVAL; i++) {
					_searchEngineAdapter.execute(
						_createIndexDocumentRequest(String.valueOf(i)));
				}
			}

			GetResponse getResponse1 = _getDocument("0");

			Assert.assertTrue(getResponse1.found());

			String id = "remainder";

			try (SafeCloseable safeCloseable = SearchContext.openBatchMode(
					false)) {

				_searchEngineAdapter.execute(_createIndexDocumentRequest(id));
			}

			GetResponse getResponse2 = _getDocument(id);

			Assert.assertTrue(getResponse2.found());
		}
	}

	@Test
	public void testExecuteBulkDocumentRequest() throws JSONException {
		Document document1 = new DocumentImpl();

		document1.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());
		document1.addKeyword(Field.UID, "1");

		IndexDocumentRequest indexDocumentRequest1 = new IndexDocumentRequest(
			_INDEX_NAME, document1);

		indexDocumentRequest1.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		BulkDocumentRequest bulkDocumentRequest1 = new BulkDocumentRequest();

		bulkDocumentRequest1.addBulkableDocumentRequest(indexDocumentRequest1);

		Document document2 = new DocumentImpl();

		document2.addKeyword(_FIELD_NAME, Boolean.FALSE.toString());
		document2.addKeyword(Field.UID, "2");

		IndexDocumentRequest indexDocumentRequest2 = new IndexDocumentRequest(
			_INDEX_NAME, document2);

		indexDocumentRequest2.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		bulkDocumentRequest1.addBulkableDocumentRequest(indexDocumentRequest2);

		BulkDocumentResponse bulkDocumentResponse1 =
			_searchEngineAdapter.execute(bulkDocumentRequest1);

		Assert.assertFalse(bulkDocumentResponse1.hasErrors());

		List<BulkDocumentItemResponse> bulkDocumentItemResponses1 =
			bulkDocumentResponse1.getBulkDocumentItemResponses();

		Assert.assertEquals(
			bulkDocumentItemResponses1.toString(), 2,
			bulkDocumentItemResponses1.size());

		BulkDocumentItemResponse bulkDocumentItemResponse1 =
			bulkDocumentItemResponses1.get(0);

		Assert.assertEquals("1", bulkDocumentItemResponse1.getId());

		BulkDocumentItemResponse bulkDocumentItemResponse2 =
			bulkDocumentItemResponses1.get(1);

		Assert.assertEquals("2", bulkDocumentItemResponse2.getId());

		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_INDEX_NAME, "1");

		deleteDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		BulkDocumentRequest bulkDocumentRequest2 = new BulkDocumentRequest();

		bulkDocumentRequest2.addBulkableDocumentRequest(deleteDocumentRequest);

		Document document2Update = new DocumentImpl();

		document2Update.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());
		document2Update.addKeyword(Field.UID, "2");

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, "2", document2Update);

		updateDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		bulkDocumentRequest2.addBulkableDocumentRequest(updateDocumentRequest);

		BulkDocumentResponse bulkDocumentResponse2 =
			_searchEngineAdapter.execute(bulkDocumentRequest2);

		Assert.assertFalse(bulkDocumentResponse2.hasErrors());

		List<BulkDocumentItemResponse> bulkDocumentItemResponses2 =
			bulkDocumentResponse2.getBulkDocumentItemResponses();

		Assert.assertEquals(
			bulkDocumentItemResponses2.toString(), 2,
			bulkDocumentItemResponses2.size());

		BulkDocumentItemResponse bulkDocumentItemResponse3 =
			bulkDocumentItemResponses2.get(0);

		Assert.assertEquals("1", bulkDocumentItemResponse3.getId());

		BulkDocumentItemResponse bulkDocumentItemResponse4 =
			bulkDocumentItemResponses2.get(1);

		Assert.assertEquals("2", bulkDocumentItemResponse4.getId());

		GetResponse getResponse1 = _getDocument("1");

		Assert.assertFalse(getResponse1.found());

		GetResponse getResponse2 = _getDocument("2");

		Assert.assertTrue(getResponse2.found());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			String.valueOf(getResponse2.source()));

		Map<String, JsonData> map2 = ConversionUtil.toJsonDataMap(
			jsonObject.toMap());

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(map2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteBulkDocumentRequestNoUid() throws JSONException {
		Document document1 = new DocumentImpl();

		document1.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());

		IndexDocumentRequest indexDocumentRequest1 = new IndexDocumentRequest(
			_INDEX_NAME, document1);

		indexDocumentRequest1.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		BulkDocumentRequest bulkDocumentRequest1 = new BulkDocumentRequest();

		bulkDocumentRequest1.addBulkableDocumentRequest(indexDocumentRequest1);

		Document document2 = new DocumentImpl();

		document2.addKeyword(_FIELD_NAME, Boolean.FALSE.toString());

		IndexDocumentRequest indexDocumentRequest2 = new IndexDocumentRequest(
			_INDEX_NAME, document2);

		indexDocumentRequest2.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		bulkDocumentRequest1.addBulkableDocumentRequest(indexDocumentRequest2);

		BulkDocumentResponse bulkDocumentResponse1 =
			_searchEngineAdapter.execute(bulkDocumentRequest1);

		Assert.assertFalse(bulkDocumentResponse1.hasErrors());

		List<BulkDocumentItemResponse> bulkDocumentItemResponses1 =
			bulkDocumentResponse1.getBulkDocumentItemResponses();

		Assert.assertEquals(
			bulkDocumentItemResponses1.toString(), 2,
			bulkDocumentItemResponses1.size());

		BulkDocumentItemResponse bulkDocumentItemResponse1 =
			bulkDocumentItemResponses1.get(0);

		Assert.assertFalse(
			Validator.isBlank(bulkDocumentItemResponse1.getId()));

		BulkDocumentItemResponse bulkDocumentItemResponse2 =
			bulkDocumentItemResponses1.get(1);

		Assert.assertFalse(
			Validator.isBlank(bulkDocumentItemResponse2.getId()));

		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_INDEX_NAME, bulkDocumentItemResponse1.getId());

		deleteDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		BulkDocumentRequest bulkDocumentRequest2 = new BulkDocumentRequest();

		bulkDocumentRequest2.addBulkableDocumentRequest(deleteDocumentRequest);

		Document document2Update = new DocumentImpl();

		document2Update.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());
		document2Update.addKeyword(
			Field.UID, bulkDocumentItemResponse2.getId());

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, bulkDocumentItemResponse2.getId(), document2Update);

		updateDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		bulkDocumentRequest2.addBulkableDocumentRequest(updateDocumentRequest);

		BulkDocumentResponse bulkDocumentResponse2 =
			_searchEngineAdapter.execute(bulkDocumentRequest2);

		Assert.assertFalse(bulkDocumentResponse2.hasErrors());

		List<BulkDocumentItemResponse> bulkDocumentItemResponses2 =
			bulkDocumentResponse2.getBulkDocumentItemResponses();

		Assert.assertEquals(
			bulkDocumentItemResponses2.toString(), 2,
			bulkDocumentItemResponses2.size());

		BulkDocumentItemResponse bulkDocumentItemResponse3 =
			bulkDocumentItemResponses2.get(0);

		Assert.assertEquals(
			bulkDocumentItemResponse1.getId(),
			bulkDocumentItemResponse3.getId());

		BulkDocumentItemResponse bulkDocumentItemResponse4 =
			bulkDocumentItemResponses2.get(1);

		Assert.assertEquals(
			bulkDocumentItemResponse2.getId(),
			bulkDocumentItemResponse4.getId());

		GetResponse getResponse1 = _getDocument(
			bulkDocumentItemResponse1.getId());

		Assert.assertFalse(getResponse1.found());

		GetResponse getResponse2 = _getDocument(
			bulkDocumentItemResponse2.getId());

		Assert.assertTrue(getResponse2.found());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			String.valueOf(getResponse2.source()));

		Map<String, JsonData> map2 = ConversionUtil.toJsonDataMap(
			jsonObject.toMap());

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(map2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteDeleteByQueryDocumentRequest() {
		_indexDocument(
			"1",
			JsonData.of(
				HashMapBuilder.<String, Object>put(
					_FIELD_NAME, Boolean.TRUE
				).build()));
		_indexDocument(
			"2",
			JsonData.of(
				HashMapBuilder.<String, Object>put(
					_FIELD_NAME, Boolean.FALSE
				).build()));

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(
				booleanQuery, new String[] {_INDEX_NAME});

		DeleteByQueryDocumentResponse deleteByQueryDocumentResponse =
			_searchEngineAdapter.execute(deleteByQueryDocumentRequest);

		Assert.assertEquals(1, deleteByQueryDocumentResponse.getDeleted());
	}

	@Test
	public void testExecuteDeleteDocumentRequest() {
		String id = "1";

		_indexDocument(
			id,
			JsonData.of(
				HashMapBuilder.<String, Object>put(
					_FIELD_NAME, Boolean.TRUE
				).build()));

		GetResponse getResponse1 = _getDocument(id);

		Assert.assertTrue(getResponse1.found());

		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_INDEX_NAME, id);

		deleteDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		DeleteDocumentResponse deleteDocumentResponse =
			_searchEngineAdapter.execute(deleteDocumentRequest);

		Assert.assertEquals(200, deleteDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Assert.assertFalse(getResponse2.found());
	}

	@Test
	public void testExecuteIndexDocumentRequestNoUid() {
		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, new DocumentImpl());

		Assert.assertEquals(
			Result.Created.jsonValue(),
			indexDocumentResponse.getStatusString());

		Assert.assertNotNull(indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteIndexDocumentRequestNoUidWithUpdate()
		throws Exception {

		Document document = new DocumentImpl();

		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, document);

		document.addKeyword(_FIELD_NAME, true);

		_updateDocumentWithAdapter(indexDocumentResponse.getUid(), document);

		Map<String, JsonData> fields = _getFields(
			indexDocumentResponse.getUid());

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(fields.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteIndexDocumentRequestUidInDocument() {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, "1");

		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, document);

		Assert.assertEquals(
			Result.Created.jsonValue(),
			indexDocumentResponse.getStatusString());

		Assert.assertEquals("1", indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteIndexDocumentRequestUidInRequest() {
		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			"1", new DocumentImpl());

		Assert.assertEquals(
			Result.Created.jsonValue(),
			indexDocumentResponse.getStatusString());

		Assert.assertEquals("1", indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteUpdateByQueryDocumentRequest() {
		_indexDocument(
			"1",
			JsonData.of(
				HashMapBuilder.<String, Object>put(
					_FIELD_NAME, Boolean.TRUE
				).build()));

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				booleanQuery, null, new String[] {_INDEX_NAME});

		UpdateByQueryDocumentResponse updateByQueryDocumentResponse =
			_searchEngineAdapter.execute(updateByQueryDocumentRequest);

		Assert.assertEquals(1, updateByQueryDocumentResponse.getUpdated());
	}

	@Test
	public void testExecuteUpdateByQueryDocumentRequestProceedOnConflicts()
		throws Exception {

		for (int i = 0; i < 50; i++) {
			_indexDocument(
				String.valueOf(i),
				JsonData.of(
					HashMapBuilder.<String, Object>put(
						_FIELD_NAME, Boolean.TRUE
					).build()));
		}

		List<Throwable> throwables = new CopyOnWriteArrayList<>();

		CountDownLatch countDownLatch = new CountDownLatch(1);
		ExecutorService executorService = Executors.newFixedThreadPool(
			_THREAD_COUNT);

		try {
			List<Future<?>> futures = new ArrayList<>();

			for (int i = 0; i < _THREAD_COUNT; i++) {
				futures.add(
					executorService.submit(
						() -> {
							try {
								countDownLatch.await();

								for (int j = 0; j < 30; j++) {
									_updateByQueryProceedOnConflicts();
								}
							}
							catch (Throwable throwable) {
								throwables.add(throwable);
							}
						}));
			}

			countDownLatch.countDown();

			for (Future<?> future : futures) {
				future.get();
			}
		}
		finally {
			executorService.shutdownNow();
		}

		Assert.assertTrue(throwables.toString(), throwables.isEmpty());
	}

	@Test
	public void testExecuteUpdateDocumentRequest() throws Exception {
		Map<String, Object> documentFields = HashMapBuilder.<String, Object>put(
			_FIELD_NAME, Boolean.TRUE
		).build();

		String id = "1";

		_indexDocument(id, JsonData.of(documentFields));

		Map<String, JsonData> fields1 = _getFields(id);

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(fields1.get(_FIELD_NAME)));

		Document document = new DocumentImpl();

		document.addKeyword(_FIELD_NAME, false);
		document.addKeyword(Field.UID, id);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(id, document);

		Assert.assertEquals(200, updateDocumentResponse.getStatus());

		Map<String, JsonData> fields2 = _getFields(id);

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(fields2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteUpdateDocumentRequestNoDocumentUid()
		throws Exception {

		String id = "1";

		_indexDocument(
			id,
			JsonData.of(
				HashMapBuilder.<String, Object>put(
					_FIELD_NAME, Boolean.TRUE
				).build()));

		Map<String, JsonData> map1 = _getFields(id);

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(map1.get(_FIELD_NAME)));

		Document document = new DocumentImpl();

		document.addKeyword(_FIELD_NAME, false);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(id, document);

		Assert.assertEquals(200, updateDocumentResponse.getStatus());

		Map<String, JsonData> fields2 = _getFields(id);

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(fields2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteUpdateDocumentRequestNoRequestId() throws Exception {
		Map<String, Object> documentFields = HashMapBuilder.<String, Object>put(
			_FIELD_NAME, Boolean.TRUE
		).build();

		String id = "1";

		_indexDocument(id, JsonData.of(documentFields));

		Map<String, JsonData> fields1 = _getFields(id);

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(fields1.get(_FIELD_NAME)));

		Document document = new DocumentImpl();

		document.addKeyword(_FIELD_NAME, false);
		document.addKeyword(Field.UID, id);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(null, document);

		Assert.assertEquals(200, updateDocumentResponse.getStatus());

		Map<String, JsonData> fields2 = _getFields(id);

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(fields2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteUpdateDocumentRequestScript() throws Exception {
		Map<String, Object> documentFields = HashMapBuilder.<String, Object>put(
			_FIELD_NAME, Boolean.TRUE
		).build();

		String id = "1";

		_indexDocument(id, JsonData.of(documentFields));

		Map<String, JsonData> fields1 = _getFields(id);

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(fields1.get(_FIELD_NAME)));

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(
				id,
				Scripts.INSTANCE.script(
					StringBundler.concat(
						"ctx._source.", _FIELD_NAME, "=\"false\" ")),
				false);

		Assert.assertEquals(200, updateDocumentResponse.getStatus());

		Map<String, JsonData> fields2 = _getFields(id);

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(fields2.get(_FIELD_NAME)));
	}

	@Test
	public void testExecuteUpdateDocumentRequestScriptedUpsert()
		throws Exception {

		String id = "1";

		_updateDocumentWithAdapter(
			id,
			Scripts.INSTANCE.script(
				StringBundler.concat(
					"ctx._source.", _FIELD_NAME, "=\"true\" ")),
			true);

		Map<String, JsonData> fields = _getFields(id);

		Assert.assertEquals(
			Boolean.TRUE.toString(), String.valueOf(fields.get(_FIELD_NAME)));
	}

	protected static SearchEngineAdapter createSearchEngineAdapter(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		ElasticsearchSearchEngineAdapterImpl
			elasticsearchSearchEngineAdapterImpl =
				new ElasticsearchSearchEngineAdapterImpl();

		ReflectionTestUtil.setFieldValue(
			elasticsearchSearchEngineAdapterImpl,
			"_elasticsearchClientResolver", elasticsearchClientResolver);

		elasticsearchSearchEngineAdapterImpl.activate(Collections.emptyMap());

		return elasticsearchSearchEngineAdapterImpl;
	}

	private void _createIndex() {
		CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder();

		builder.index(_INDEX_NAME);

		builder.mappings(
			TypeMapping.of(
				typeMapping -> typeMapping.properties(
					IndexUtil.getPropertiesMap(
						JSONUtil.put(
							"properties",
							JSONUtil.put(
								"matchDocument",
								JSONUtil.put("type", "boolean")))))));

		try {
			_elasticsearchIndicesClient.create(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private IndexDocumentRequest _createIndexDocumentRequest(String uid) {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, uid);

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_INDEX_NAME, document);

		indexDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		return indexDocumentRequest;
	}

	private void _deleteIndex() {
		try {
			_elasticsearchIndicesClient.delete(
				DeleteIndexRequest.of(
					deleteIndexRequest -> deleteIndexRequest.index(
						_INDEX_NAME)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private GetResponse _getDocument(String id) {
		try {
			return _elasticsearchClient.get(
				GetRequest.of(
					getRequest -> getRequest.id(
						id
					).index(
						_INDEX_NAME
					)),
				JsonData.class);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private Map<String, JsonData> _getFields(String id) throws Exception {
		GetResponse<JsonData> getResponse = _elasticsearchClient.get(
			GetRequest.of(
				getRequest -> getRequest.id(
					id
				).index(
					_INDEX_NAME
				)),
			JsonData.class);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			String.valueOf(getResponse.source()));

		return ConversionUtil.toJsonDataMap(jsonObject.toMap());
	}

	private void _indexDocument(String id, JsonData jsonData) {
		IndexRequest.Builder<JsonData> indexRequestBuilder =
			new IndexRequest.Builder<>();

		indexRequestBuilder.document(jsonData);
		indexRequestBuilder.id(id);
		indexRequestBuilder.index(_INDEX_NAME);
		indexRequestBuilder.refresh(Refresh.True);

		try {
			_elasticsearchClient.index(indexRequestBuilder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private IndexDocumentResponse _indexDocumentWithAdapter(
		String uid, Document document) {

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_INDEX_NAME, uid, document);

		indexDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		return _searchEngineAdapter.execute(indexDocumentRequest);
	}

	private void _updateByQueryProceedOnConflicts() {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				booleanQuery, null, new String[] {_INDEX_NAME});

		updateByQueryDocumentRequest.setProceedOnConflicts(true);

		_searchEngineAdapter.execute(updateByQueryDocumentRequest);
	}

	private UpdateDocumentResponse _updateDocumentWithAdapter(
		String uid, Document document) {

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document);

		updateDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		return _searchEngineAdapter.execute(updateDocumentRequest);
	}

	private UpdateDocumentResponse _updateDocumentWithAdapter(
		String uid, Script script, boolean scriptedUpsert) {

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, script);

		updateDocumentRequest.setScriptedUpsert(scriptedUpsert);
		updateDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		return _searchEngineAdapter.execute(updateDocumentRequest);
	}

	private static final String _FIELD_NAME = "matchDocument";

	private static final String _INDEX_NAME = "test_request_index";

	private static final int _THREAD_COUNT = 6;

	private static ElasticsearchFixture _elasticsearchFixture;

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private ElasticsearchClient _elasticsearchClient;
	private ElasticsearchIndicesClient _elasticsearchIndicesClient;
	private SearchEngineAdapter _searchEngineAdapter;

}