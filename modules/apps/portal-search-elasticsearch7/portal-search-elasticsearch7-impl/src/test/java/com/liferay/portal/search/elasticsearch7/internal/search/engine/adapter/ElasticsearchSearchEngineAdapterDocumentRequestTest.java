/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.index.constants.IndexMappingsConstants;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.DocumentRequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentResponse;
import com.liferay.portal.search.internal.script.ScriptsImpl;
import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

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

		_restHighLevelClient = _elasticsearchFixture.getRestHighLevelClient();

		_indicesClient = _restHighLevelClient.indices();

		_documentFixture.setUp();

		_createIndex();
	}

	@After
	public void tearDown() throws Exception {
		_deleteIndex();

		_documentFixture.tearDown();
	}

	@Test
	public void testExecuteBulkDocumentRequest() {
		Document document1 = new DocumentImpl();

		document1.addKeyword(Field.UID, "1");
		document1.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());

		IndexDocumentRequest indexDocumentRequest1 = new IndexDocumentRequest(
			_INDEX_NAME, document1);

		indexDocumentRequest1.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		BulkDocumentRequest bulkDocumentRequest1 = new BulkDocumentRequest();

		bulkDocumentRequest1.addBulkableDocumentRequest(indexDocumentRequest1);

		Document document2 = new DocumentImpl();

		document2.addKeyword(Field.UID, "2");
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

		document2Update.addKeyword(Field.UID, "2");
		document2Update.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());

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

		Assert.assertFalse(getResponse1.isExists());

		GetResponse getResponse2 = _getDocument("2");

		Assert.assertTrue(getResponse2.isExists());

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteBulkDocumentRequestNoUid() {
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

		document2Update.addKeyword(
			Field.UID, bulkDocumentItemResponse2.getId());
		document2Update.addKeyword(_FIELD_NAME, Boolean.TRUE.toString());

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

		Assert.assertFalse(getResponse1.isExists());

		GetResponse getResponse2 = _getDocument(
			bulkDocumentItemResponse2.getId());

		Assert.assertTrue(getResponse2.isExists());

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteDeleteByQueryDocumentRequest() {
		String documentSource1 = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String documentSource2 = "{\"" + _FIELD_NAME + "\":\"false\"}";

		_indexDocument(documentSource1, "1");
		_indexDocument(documentSource2, "2");

		BooleanQuery query = new BooleanQueryImpl();

		query.addExactTerm(_FIELD_NAME, true);

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(query, new String[] {_INDEX_NAME});

		DeleteByQueryDocumentResponse deleteByQueryDocumentResponse =
			_searchEngineAdapter.execute(deleteByQueryDocumentRequest);

		Assert.assertEquals(1, deleteByQueryDocumentResponse.getDeleted());
	}

	@Test
	public void testExecuteDeleteDocumentRequest() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String id = "1";

		_indexDocument(documentSource, id);

		GetResponse getResponse1 = _getDocument(id);

		Assert.assertTrue(getResponse1.isExists());

		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_INDEX_NAME, id);

		deleteDocumentRequest.setType(
			IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		DeleteDocumentResponse deleteDocumentResponse =
			_searchEngineAdapter.execute(deleteDocumentRequest);

		Assert.assertEquals(
			RestStatus.OK.getStatus(), deleteDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Assert.assertFalse(getResponse2.isExists());
	}

	@Test
	public void testExecuteIndexDocumentRequestNoUid() {
		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, new DocumentImpl());

		Assert.assertEquals(
			RestStatus.CREATED.getStatus(), indexDocumentResponse.getStatus());

		Assert.assertNotNull(indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteIndexDocumentRequestNoUidWithUpdate() {
		Document document = new DocumentImpl();

		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, document);

		document.addKeyword(_FIELD_NAME, true);

		_updateDocumentWithAdapter(indexDocumentResponse.getUid(), document);

		GetResponse getResponse = _getDocument(indexDocumentResponse.getUid());

		Map<String, Object> map = getResponse.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteIndexDocumentRequestUidInDocument() {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, "1");

		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			null, document);

		Assert.assertEquals(
			RestStatus.CREATED.getStatus(), indexDocumentResponse.getStatus());

		Assert.assertEquals("1", indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteIndexDocumentRequestUidInRequest() {
		IndexDocumentResponse indexDocumentResponse = _indexDocumentWithAdapter(
			"1", new DocumentImpl());

		Assert.assertEquals(
			RestStatus.CREATED.getStatus(), indexDocumentResponse.getStatus());

		Assert.assertEquals("1", indexDocumentResponse.getUid());
	}

	@Test
	public void testExecuteUpdateByQueryDocumentRequest() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";

		_indexDocument(documentSource, "1");

		BooleanQuery query = new BooleanQueryImpl();

		query.addExactTerm(_FIELD_NAME, true);

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				query, null, new String[] {_INDEX_NAME});

		UpdateByQueryDocumentResponse updateByQueryDocumentResponse =
			_searchEngineAdapter.execute(updateByQueryDocumentRequest);

		Assert.assertEquals(1, updateByQueryDocumentResponse.getUpdated());
	}

	@Test
	public void testExecuteUpdateDocumentRequest() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String id = "1";

		_indexDocument(documentSource, id);

		GetResponse getResponse1 = _getDocument(id);

		Map<String, Object> map1 = getResponse1.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map1.get(_FIELD_NAME));

		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, id);
		document.addKeyword(_FIELD_NAME, false);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(id, document);

		Assert.assertEquals(
			RestStatus.OK.getStatus(), updateDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.FALSE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteUpdateDocumentRequestNoDocumentUid() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String id = "1";

		_indexDocument(documentSource, id);

		GetResponse getResponse1 = _getDocument(id);

		Map<String, Object> map1 = getResponse1.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map1.get(_FIELD_NAME));

		Document document = new DocumentImpl();

		document.addKeyword(_FIELD_NAME, false);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(id, document);

		Assert.assertEquals(
			RestStatus.OK.getStatus(), updateDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.FALSE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteUpdateDocumentRequestNoRequestId() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String id = "1";

		_indexDocument(documentSource, id);

		GetResponse getResponse1 = _getDocument(id);

		Map<String, Object> map1 = getResponse1.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map1.get(_FIELD_NAME));

		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, id);
		document.addKeyword(_FIELD_NAME, false);

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(null, document);

		Assert.assertEquals(
			RestStatus.OK.getStatus(), updateDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.FALSE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteUpdateDocumentRequestScript() {
		String documentSource = "{\"" + _FIELD_NAME + "\":\"true\"}";
		String id = "1";

		_indexDocument(documentSource, id);

		GetResponse getResponse1 = _getDocument(id);

		Map<String, Object> map1 = getResponse1.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map1.get(_FIELD_NAME));

		UpdateDocumentResponse updateDocumentResponse =
			_updateDocumentWithAdapter(
				id,
				_scripts.script(
					StringBundler.concat(
						"ctx._source.", _FIELD_NAME, "=\"false\" ")),
				false);

		Assert.assertEquals(
			RestStatus.OK.getStatus(), updateDocumentResponse.getStatus());

		GetResponse getResponse2 = _getDocument(id);

		Map<String, Object> map2 = getResponse2.getSource();

		Assert.assertEquals(Boolean.FALSE.toString(), map2.get(_FIELD_NAME));
	}

	@Test
	public void testExecuteUpdateDocumentRequestScriptedUpsert() {
		String id = "1";

		_updateDocumentWithAdapter(
			id,
			_scripts.script(
				StringBundler.concat(
					"ctx._source.", _FIELD_NAME, "=\"true\" ")),
			true);

		GetResponse getResponse = _getDocument(id);

		Map<String, Object> map = getResponse.getSource();

		Assert.assertEquals(Boolean.TRUE.toString(), map.get(_FIELD_NAME));
	}

	protected static SearchEngineAdapter createSearchEngineAdapter(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		SearchEngineAdapter searchEngineAdapter =
			new ElasticsearchSearchEngineAdapterImpl();

		ReflectionTestUtil.setFieldValue(
			searchEngineAdapter, "_documentRequestExecutor",
			_createDocumentRequestExecutor(elasticsearchClientResolver));

		return searchEngineAdapter;
	}

	private static DocumentRequestExecutor _createDocumentRequestExecutor(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		DocumentRequestExecutorFixture documentRequestExecutorFixture =
			new DocumentRequestExecutorFixture() {
				{
					setElasticsearchClientResolver(elasticsearchClientResolver);
				}
			};

		documentRequestExecutorFixture.setUp();

		return documentRequestExecutorFixture.getDocumentRequestExecutor();
	}

	private void _createIndex() {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			_INDEX_NAME);

		createIndexRequest.mapping(_MAPPING_SOURCE, XContentType.JSON);

		try {
			_indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _deleteIndex() {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			_INDEX_NAME);

		try {
			_indicesClient.delete(deleteIndexRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private GetResponse _getDocument(String id) {
		GetRequest getRequest = new GetRequest();

		getRequest.id(id);
		getRequest.index(_INDEX_NAME);

		try {
			return _restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _indexDocument(String documentSource, String id) {
		IndexRequest indexRequest = new IndexRequest(_INDEX_NAME);

		indexRequest.id(id);
		indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
		indexRequest.source(documentSource, XContentType.JSON);
		indexRequest.type(IndexMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		try {
			_restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
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

	private static final String _MAPPING_SOURCE =
		"{\"properties\":{\"matchDocument\":{\"type\":\"boolean\"}}}";

	private static ElasticsearchFixture _elasticsearchFixture;
	private static final Scripts _scripts = new ScriptsImpl();

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private IndicesClient _indicesClient;
	private RestHighLevelClient _restHighLevelClient;
	private SearchEngineAdapter _searchEngineAdapter;

}