/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndicesSettingsResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.elasticsearch.indices.OpenRequest;
import co.elastic.clients.elasticsearch.indices.PutAliasRequest;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.AnalysisIndexResponseToken;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexRequest;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexResponse;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CloseIndexResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;
import com.liferay.portal.search.engine.adapter.index.FlushIndexRequest;
import com.liferay.portal.search.engine.adapter.index.FlushIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetFieldMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.OpenIndexResponse;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexRequest;
import com.liferay.portal.search.engine.adapter.index.PutMappingIndexResponse;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexResponse;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexResponse;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.json.spi.JsonProvider;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.EntityUtils;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

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
public class ElasticsearchSearchEngineAdapterIndexRequestTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture();

		_elasticsearchFixture.setUp();

		_searchEngineAdapter = createSearchEngineAdapter(_elasticsearchFixture);

		ElasticsearchClient elasticsearchClient =
			_elasticsearchFixture.getElasticsearchClient();

		_elasticsearchIndicesClient = elasticsearchClient.indices();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() {
		_createIndex(_INDEX_NAME);
	}

	@After
	public void tearDown() {
		_deleteIndex(_INDEX_NAME);
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithAnalyzer() {
		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setAnalyzer("stop");

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"quick,brown,foxes,jumped,over,lazy,dog,s,bone");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithCharFilters() {
		_putSettings(
			JSONUtil.put(
				"settings",
				JSONUtil.put(
					"analysis",
					JSONUtil.put(
						"char_filter",
						JSONUtil.put(
							"custom_cf",
							JSONUtil.put(
								"mappings", JSONUtil.putAll("- => +", "2 => 3")
							).put(
								"type", "mapping"
							))))
			).toString());

		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setCharFilters(Collections.singleton("custom_cf"));

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"The 3 QUICK Brown+Foxes jumped over the lazy dog's bone.");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithExplainAndAnalyzer() {
		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setAnalyzer("stop");
		analyzeIndexRequest.setExplain(true);
		analyzeIndexRequest.setIndexName(_INDEX_NAME);
		analyzeIndexRequest.setTexts(
			"The 2 QUICK Brown-Foxes jumped over the lazy dog's bone.");

		AnalyzeIndexResponse analyzeIndexResponse =
			_searchEngineAdapter.execute(analyzeIndexRequest);

		AnalyzeIndexResponse.DetailsAnalyzer detailsAnalyzer =
			analyzeIndexResponse.getDetailsAnalyzer();

		Assert.assertEquals("stop", detailsAnalyzer.getAnalyzerName());

		_assertAnalysisIndexResponseTokens(
			detailsAnalyzer.getAnalysisIndexResponseTokens(),
			"quick,brown,foxes,jumped,over,lazy,dog,s,bone");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithExplainAndWithoutAnalyzer() {
		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setExplain(true);
		analyzeIndexRequest.setIndexName(_INDEX_NAME);
		analyzeIndexRequest.setTexts(
			"The 2 QUICK Brown-Foxes jumped over the lazy dog's bone.");
		analyzeIndexRequest.setTokenFilters(Collections.singleton("uppercase"));

		AnalyzeIndexResponse analyzeIndexResponse =
			_searchEngineAdapter.execute(analyzeIndexRequest);

		Assert.assertNull(analyzeIndexResponse.getDetailsAnalyzer());

		List<AnalyzeIndexResponse.DetailsTokenFilter> detailsTokenFilters =
			analyzeIndexResponse.getDetailsTokenFilters();

		Assert.assertEquals(
			detailsTokenFilters.toString(), 1, detailsTokenFilters.size());

		AnalyzeIndexResponse.DetailsTokenFilter detailsTokenFilter =
			detailsTokenFilters.get(0);

		Assert.assertEquals(
			"uppercase", detailsTokenFilter.getTokenFilterName());

		_assertAnalysisIndexResponseTokens(
			detailsTokenFilter.getAnalysisIndexResponseTokens(),
			"THE 2 QUICK BROWN-FOXES JUMPED OVER THE LAZY DOG'S BONE.");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithFieldName() {
		_putMapping(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"keywordTestField", JSONUtil.put("type", "keyword"))
			).toString());

		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setFieldName("keywordTestField");

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"The 2 QUICK Brown-Foxes jumped over the lazy dog's bone.");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithNormalizer() {
		_putSettings(
			JSONUtil.put(
				"settings",
				JSONUtil.put(
					"analysis",
					JSONUtil.put(
						"normalizer",
						JSONUtil.put(
							"custom_normalizer",
							JSONUtil.put(
								"filter", JSONUtil.put("uppercase")
							).put(
								"type", "custom"
							))))
			).toString());

		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setNormalizer("custom_normalizer");

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"THE 2 QUICK BROWN-FOXES JUMPED OVER THE LAZY DOG'S BONE.");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithTokenFilters() {
		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setTokenFilters(Collections.singleton("uppercase"));

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"THE 2 QUICK BROWN-FOXES JUMPED OVER THE LAZY DOG'S BONE.");
	}

	@Test
	public void testExecuteAnalyzeIndexRequestWithTokenizer() {
		AnalyzeIndexRequest analyzeIndexRequest = new AnalyzeIndexRequest();

		analyzeIndexRequest.setTokenizer("letter");

		_assertExecuteAnalyzeIndexRequest(
			analyzeIndexRequest,
			"The,QUICK,Brown,Foxes,jumped,over,the,lazy,dog,s,bone");
	}

	@Test
	public void testExecuteCloseIndexRequest() {
		CloseIndexRequest closeIndexRequest = new CloseIndexRequest(
			_INDEX_NAME);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setIgnoreUnavailable(true);

		closeIndexRequest.setIndicesOptions(indicesOptions);

		CloseIndexResponse closeIndexResponse = _searchEngineAdapter.execute(
			closeIndexRequest);

		Assert.assertTrue(
			"Close request not acknowledged",
			closeIndexResponse.isAcknowledged());

		_assertIndexMetadataState(_INDEX_NAME, "close");
	}

	@Test
	public void testExecuteCreateIndexRequest() {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			"test_index_2");

		createIndexRequest.setSource(
			JSONUtil.put(
				"mappings",
				JSONUtil.put(
					"properties",
					JSONUtil.put("field1", JSONUtil.put("type", "text")))
			).put(
				"settings", JSONUtil.put("number_of_shards", 1)
			).toString());

		CreateIndexResponse createIndexResponse = _searchEngineAdapter.execute(
			createIndexRequest);

		Assert.assertTrue(createIndexResponse.isAcknowledged());

		Assert.assertEquals("test_index_2", createIndexResponse.getIndexName());

		Assert.assertTrue(_indiciesExists("test_index_2"));

		_deleteIndex("test_index_2");
	}

	@Test
	public void testExecuteDeleteIndexRequest() {
		_createIndex("test_index_2");

		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			"test_index_2");

		DeleteIndexResponse deleteIndexResponse = _searchEngineAdapter.execute(
			deleteIndexRequest);

		Assert.assertTrue(deleteIndexResponse.isAcknowledged());

		Assert.assertFalse(_indiciesExists("test_index_2"));
	}

	@Test
	public void testExecuteFlushIndexRequest() {
		FlushIndexRequest flushIndexRequest = new FlushIndexRequest(
			_INDEX_NAME);

		FlushIndexResponse flushIndexResponse = _searchEngineAdapter.execute(
			flushIndexRequest);

		Assert.assertEquals(0, flushIndexResponse.getFailedShards());
	}

	@Test
	public void testExecuteGetFieldMappingIndexRequest() throws Exception {
		_putMapping(
			JSONUtil.put(
				"properties",
				JSONUtil.put(
					"otherTestField", JSONUtil.put("type", "keyword")
				).put(
					"testField", JSONUtil.put("type", "keyword")
				)
			).toString());

		String[] fields = {"otherTestField"};

		GetFieldMappingIndexRequest getFieldMappingIndexRequest =
			new GetFieldMappingIndexRequest(new String[] {_INDEX_NAME}, fields);

		GetFieldMappingIndexResponse getFieldMappingIndexResponse =
			_searchEngineAdapter.execute(getFieldMappingIndexRequest);

		Map<String, String> fieldMappings =
			getFieldMappingIndexResponse.getFieldMappings();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			fieldMappings.get(_INDEX_NAME));

		String fieldMapping = jsonObject.getString("otherTestField");

		Assert.assertTrue(
			fieldMapping,
			fieldMapping.equals("{\"otherTestField\":{\"type\":\"keyword\"}}"));
	}

	@Test
	public void testExecuteGetIndexIndexRequest() {
		String mappingSource = JSONUtil.put(
			"properties",
			JSONUtil.put("testField", JSONUtil.put("type", "keyword"))
		).toString();

		_putMapping(mappingSource);

		GetIndexIndexRequest getIndexIndexRequest = new GetIndexIndexRequest(
			_INDEX_NAME);

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(getIndexIndexRequest);

		String[] indexNames = getIndexIndexResponse.getIndexNames();

		Assert.assertEquals(Arrays.toString(indexNames), 1, indexNames.length);
		Assert.assertEquals(_INDEX_NAME, indexNames[0]);

		Map<String, String> indexMappings =
			getIndexIndexResponse.getIndexMappings();

		String responseIndexMappings = indexMappings.get(_INDEX_NAME);

		Assert.assertTrue(
			responseIndexMappings,
			responseIndexMappings.contains(mappingSource));
	}

	@Test
	public void testExecuteGetIndexIndexRequestWithAlias() throws Exception {
		_putAlias();

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(
				new GetIndexIndexRequest(_INDEX_ALIAS_NAME));

		Assert.assertArrayEquals(
			new String[] {_INDEX_NAME}, getIndexIndexResponse.getIndexNames());

		_assertGetIndexIndexResponseKeys(
			getIndexIndexResponse, Collections.singleton(_INDEX_ALIAS_NAME));
	}

	@Test
	public void testExecuteGetIndexIndexRequestWithIndexName()
		throws Exception {

		_putAlias();

		_assertGetIndexIndexResponseKeys(
			_searchEngineAdapter.execute(new GetIndexIndexRequest(_INDEX_NAME)),
			Collections.singleton(_INDEX_NAME));
	}

	@Test
	public void testExecuteGetIndexIndexRequestWithWildcard() throws Exception {
		_putAlias();

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(
				new GetIndexIndexRequest(StringPool.STAR));

		String[] indexNames = getIndexIndexResponse.getIndexNames();

		Assert.assertTrue(ArrayUtil.contains(indexNames, _INDEX_NAME));

		_assertGetIndexIndexResponseKeys(
			getIndexIndexResponse, SetUtil.fromArray(indexNames));
	}

	@Test
	public void testExecuteGetMappingIndexRequest() {
		String mappingSource = JSONUtil.put(
			"properties",
			JSONUtil.put("testField", JSONUtil.put("type", "keyword"))
		).toString();

		_putMapping(mappingSource);

		GetMappingIndexRequest getMappingIndexRequest =
			new GetMappingIndexRequest(new String[] {_INDEX_NAME});

		GetMappingIndexResponse getMappingIndexResponse =
			_searchEngineAdapter.execute(getMappingIndexRequest);

		String string = String.valueOf(
			getMappingIndexResponse.getIndexMappings());

		Assert.assertTrue(string.contains(mappingSource));
	}

	@Test
	public void testExecuteIndicesExistsIndexRequest() {
		IndicesExistsIndexRequest indicesExistsIndexRequest1 =
			new IndicesExistsIndexRequest(_INDEX_NAME);

		IndicesExistsIndexResponse indicesExistsIndexResponse1 =
			_searchEngineAdapter.execute(indicesExistsIndexRequest1);

		Assert.assertTrue(indicesExistsIndexResponse1.isExists());

		IndicesExistsIndexRequest indicesExistsIndexRequest2 =
			new IndicesExistsIndexRequest("test_index_2");

		IndicesExistsIndexResponse indicesExistsIndexResponse2 =
			_searchEngineAdapter.execute(indicesExistsIndexRequest2);

		Assert.assertFalse(indicesExistsIndexResponse2.isExists());
	}

	@Test
	public void testExecuteOpenIndexRequest() {
		_closeIndex(_INDEX_NAME);

		_assertIndexMetadataState(_INDEX_NAME, "close");

		OpenIndexRequest openIndexRequest = new OpenIndexRequest(_INDEX_NAME);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setIgnoreUnavailable(true);

		openIndexRequest.setIndicesOptions(indicesOptions);

		OpenIndexResponse openIndexResponse = _searchEngineAdapter.execute(
			openIndexRequest);

		Assert.assertTrue(
			"Open request not acknowledged",
			openIndexResponse.isAcknowledged());

		_assertIndexMetadataState(_INDEX_NAME, "open");
	}

	@Test
	public void testExecutePutMappingIndexRequest() {
		String mappingSource = JSONUtil.put(
			"properties",
			JSONUtil.put("testField", JSONUtil.put("type", "keyword"))
		).toString();

		PutMappingIndexRequest putMappingIndexRequest =
			new PutMappingIndexRequest(
				new String[] {_INDEX_NAME}, mappingSource);

		PutMappingIndexResponse putMappingIndexResponse =
			_searchEngineAdapter.execute(putMappingIndexRequest);

		Assert.assertTrue(putMappingIndexResponse.isAcknowledged());

		GetMappingResponse getMappingResponse = _getGetMappingResponse(
			_INDEX_NAME);

		Map<String, IndexMappingRecord> indexMappingsRecords =
			getMappingResponse.result();

		IndexMappingRecord indexMappingRecord = indexMappingsRecords.get(
			_INDEX_NAME);

		String mappingMetadataSource = JsonpUtil.toString(
			indexMappingRecord.mappings());

		Assert.assertTrue(mappingMetadataSource.contains(mappingSource));
	}

	@Test
	public void testExecuteRefreshIndexRequest() {
		RefreshIndexRequest refreshIndexRequest = new RefreshIndexRequest(
			_INDEX_NAME);

		RefreshIndexResponse refreshIndexResponse =
			_searchEngineAdapter.execute(refreshIndexRequest);

		Assert.assertEquals(0, refreshIndexResponse.getFailedShards());
	}

	@Test
	public void testExecuteUpdateIndexSettingsIndexRequest() {
		_createIndex("test_index_2");

		UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest =
			new UpdateIndexSettingsIndexRequest("test_index_2");

		updateIndexSettingsIndexRequest.setSettings(
			JSONUtil.put(
				"index", JSONUtil.put("refresh_interval", "2s")
			).toString());

		UpdateIndexSettingsIndexResponse indexSettingsIndexResponse =
			_searchEngineAdapter.execute(updateIndexSettingsIndexRequest);

		Assert.assertTrue(indexSettingsIndexResponse.isAcknowledged());

		GetIndicesSettingsResponse getIndicesSettingsResponse =
			_getGetIndicesSettingsResponse("test_index_2");

		Map<String, IndexState> indexStates =
			getIndicesSettingsResponse.result();

		IndexState indexState = indexStates.get("test_index_2");

		IndexSettings indexSettings1 = indexState.settings();

		IndexSettings indexSettings2 = indexSettings1.index();

		Time refreshInterval = indexSettings2.refreshInterval();

		Assert.assertEquals("2s", refreshInterval.time());

		_deleteIndex("test_index_2");
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

	private void _assertAnalysisIndexResponseTokens(
		List<AnalysisIndexResponseToken> analysisIndexResponseTokens,
		String expectedTokens) {

		List<String> actualTokensList = new ArrayList<>();

		for (AnalysisIndexResponseToken analysisIndexResponseToken :
				analysisIndexResponseTokens) {

			actualTokensList.add(analysisIndexResponseToken.getTerm());
		}

		String actualTokens = StringUtil.merge(
			actualTokensList, StringPool.COMMA);

		Assert.assertEquals(expectedTokens, expectedTokens, actualTokens);
	}

	private void _assertExecuteAnalyzeIndexRequest(
		AnalyzeIndexRequest analyzeIndexRequest, String expectedTokens) {

		analyzeIndexRequest.setIndexName(_INDEX_NAME);
		analyzeIndexRequest.setTexts(
			"The 2 QUICK Brown-Foxes jumped over the lazy dog's bone.");

		AnalyzeIndexResponse analyzeIndexResponse =
			_searchEngineAdapter.execute(analyzeIndexRequest);

		_assertAnalysisIndexResponseTokens(
			analyzeIndexResponse.getAnalysisIndexResponseTokens(),
			expectedTokens);
	}

	private void _assertGetIndexIndexResponseKeys(
		GetIndexIndexResponse getIndexIndexResponse, Set<String> keys) {

		Map<String, String> indexMappings =
			getIndexIndexResponse.getIndexMappings();

		Assert.assertEquals(keys, indexMappings.keySet());

		Map<String, String> settings = getIndexIndexResponse.getSettings();

		Assert.assertEquals(keys, settings.keySet());
	}

	private void _assertIndexMetadataState(
		String indexName, String indexMetadataState) {

		ElasticsearchConnection elasticsearchConnection =
			_elasticsearchFixture.getElasticsearchConnection();

		RestClientTransport restClientTransport =
			elasticsearchConnection.getRestClientTransport();

		RestClient restClient = restClientTransport.restClient();

		Request request = new Request(
			"GET", "/_cluster/state/metadata/" + indexName);

		try {
			Response response = restClient.performRequest(request);

			String responseBody = EntityUtils.toString(response.getEntity());

			JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
				responseBody);

			JSONObject metadataJSONObject = responseJSONObject.getJSONObject(
				"metadata");

			JSONObject indicesJSONObject = metadataJSONObject.getJSONObject(
				"indices");

			JSONObject indexJSONObject = indicesJSONObject.getJSONObject(
				indexName);

			String state = GetterUtil.getString(indexJSONObject.get("state"));

			Assert.assertEquals(indexMetadataState, state);
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _closeIndex(String indexName) {
		try {
			_elasticsearchIndicesClient.close(
				co.elastic.clients.elasticsearch.indices.CloseIndexRequest.of(
					closeIndexRequest -> closeIndexRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _createIndex(String indexName) {
		try {
			_elasticsearchIndicesClient.create(
				co.elastic.clients.elasticsearch.indices.CreateIndexRequest.of(
					createIndexRequest -> createIndexRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _deleteIndex(String indexName) {
		try {
			_elasticsearchIndicesClient.delete(
				co.elastic.clients.elasticsearch.indices.DeleteIndexRequest.of(
					deleteIndexRequest -> deleteIndexRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private GetIndicesSettingsResponse _getGetIndicesSettingsResponse(
		String indexName) {

		try {
			return _elasticsearchIndicesClient.getSettings(
				GetIndicesSettingsRequest.of(
					getIndicesSettingsRequest ->
						getIndicesSettingsRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private GetMappingResponse _getGetMappingResponse(String indexName) {
		try {
			return _elasticsearchIndicesClient.getMapping(
				GetMappingRequest.of(
					getMappingRequest -> getMappingRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private boolean _indiciesExists(String indexName) {
		try {
			BooleanResponse booleanResponse =
				_elasticsearchIndicesClient.exists(
					ExistsRequest.of(
						existsRequest -> existsRequest.index(indexName)));

			return booleanResponse.value();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _openIndex(String indexName) {
		try {
			_elasticsearchIndicesClient.open(
				OpenRequest.of(openRequest -> openRequest.index(indexName)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _putAlias() throws Exception {
		_elasticsearchIndicesClient.putAlias(
			PutAliasRequest.of(
				putAliasRequest -> putAliasRequest.index(
					_INDEX_NAME
				).name(
					_INDEX_ALIAS_NAME
				)));
	}

	private void _putMapping(String mappingSource) {
		try {
			JsonpMapper jsonpMapper = _elasticsearchFixture.getJsonpMapper(
				null);

			JsonProvider jsonProvider = jsonpMapper.jsonProvider();

			PutMappingRequest.Builder builder = new PutMappingRequest.Builder(
			).index(
				_INDEX_NAME
			).withJson(
				jsonProvider.createParser(
					new UnsyncStringReader(mappingSource)),
				jsonpMapper
			);

			_elasticsearchIndicesClient.putMapping(builder.build());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _putSettings(String settingsSource) {
		_closeIndex(_INDEX_NAME);

		JsonpMapper jsonpMapper = _elasticsearchFixture.getJsonpMapper(null);

		JsonProvider jsonProvider = jsonpMapper.jsonProvider();

		try {
			_elasticsearchIndicesClient.putSettings(
				PutIndicesSettingsRequest.of(
					putIndicesSettingsRequest ->
						putIndicesSettingsRequest.index(
							_INDEX_NAME
						).settings(
							IndexSettings._DESERIALIZER.deserialize(
								jsonProvider.createParser(
									new UnsyncStringReader(settingsSource)),
								jsonpMapper)
						)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		_openIndex(_INDEX_NAME);
	}

	private static final String _INDEX_ALIAS_NAME = "test_request_alias";

	private static final String _INDEX_NAME = "test_request_index";

	private static ElasticsearchFixture _elasticsearchFixture;
	private static ElasticsearchIndicesClient _elasticsearchIndicesClient;
	private static SearchEngineAdapter _searchEngineAdapter;

}