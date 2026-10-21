/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.BulkableDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class MCPToolIndexWriterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_aggregations.terms(Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			Mockito.mock(TermsAggregation.class)
		);

		Snapshot<HeadlessApplicationProvider>
			headlessApplicationProviderSnapshot = Mockito.mock(Snapshot.class);

		HeadlessApplicationProvider headlessApplicationProvider = Mockito.mock(
			HeadlessApplicationProvider.class);

		List<HeadlessApplicationProvider.Application> applications =
			Arrays.asList(
				_openAPIDocument1.getApplication(),
				_openAPIDocument2.getApplication());

		Mockito.when(
			headlessApplicationProvider.getApplications(_COMPANY_ID)
		).thenReturn(
			applications
		);

		Mockito.when(
			headlessApplicationProviderSnapshot.get()
		).thenReturn(
			headlessApplicationProvider
		);

		ReflectionTestUtil.setFieldValue(
			ToolSetUtil.class, "_headlessApplicationProviderSnapshot",
			headlessApplicationProviderSnapshot);

		Mockito.when(
			_mcpToolIndexCreator.hasIndex(_COMPANY_ID)
		).thenReturn(
			true
		);

		_mockIndexedToolSetHashes(Collections.emptyMap());

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCompanyId(_httpServletRequest)
		).thenReturn(
			_COMPANY_ID
		);

		portalUtil.setPortal(portal);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(BulkDocumentRequest.class))
		).thenAnswer(
			invocation -> {
				_bulkDocumentRequests.add(invocation.getArgument(0));

				return _bulkDocumentResponse;
			}
		);

		Mockito.when(
			_searchEngineAdapter.execute(
				Mockito.any(DeleteByQueryDocumentRequest.class))
		).thenAnswer(
			invocation -> {
				_deleteByQueryDocumentRequests.add(invocation.getArgument(0));

				return null;
			}
		);

		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexWriter, "_aggregations", _aggregations);
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexWriter, "_bundleVersion", "1.0.0");
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexWriter, "_mcpToolIndexCreator", _mcpToolIndexCreator);
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexWriter, "_searchEngineAdapter", _searchEngineAdapter);
	}

	@Test
	public void testGetToolSetHashes() {
		String toolSetHash = _getToolSetHash(_openAPIDocument1);

		Assert.assertEquals(
			toolSetHash,
			_getToolSetHash(
				_mockOpenAPIDocument(
					_BASE_PATH_1, _TOOL_NAME_2, _TOOL_NAME_1)));
		Assert.assertNotEquals(toolSetHash, _getToolSetHash(_openAPIDocument2));

		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexWriter, "_bundleVersion", "1.0.1");

		Assert.assertNotEquals(
			toolSetHash,
			_getToolSetHash(
				_mockOpenAPIDocument(
					_BASE_PATH_1, _TOOL_NAME_1, _TOOL_NAME_2)));
	}

	@Test
	public void testUpdateIfStale() {
		_updateIfStale();

		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_2));

		Map<String, Document> documents = _getIndexedDocuments();

		Assert.assertEquals(
			_getToolKeys(_TOOL_SET_NAME_1, _TOOL_SET_NAME_2),
			documents.keySet());

		Document document = documents.get(
			ToolSetUtil.getToolKey(_TOOL_NAME_1, _TOOL_SET_NAME_1));

		Assert.assertEquals(
			Integer.valueOf(2),
			document.getInteger(MCPToolFields.TOOL_SET_SIZE));
		Assert.assertEquals(
			_getToolSetHash(_openAPIDocument1),
			document.getString(MCPToolFields.TOOL_SET_HASH));
	}

	@Test
	public void testUpdateIfStaleWhenAllToolSetsAreInvalidated() {
		_updateIfStale();

		_mcpToolIndexWriter.invalidate(_COMPANY_ID);

		_updateIfStale();

		Assert.assertEquals(2, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(2, _getUpdatesCount(_TOOL_SET_NAME_2));
	}

	@Test
	public void testUpdateIfStaleWhenIndexIsDeleted() {
		_updateIfStale();

		_mcpToolIndexWriter.deleteIndex(_COMPANY_ID);

		Mockito.verify(
			_mcpToolIndexCreator
		).deleteIfExists(
			_COMPANY_ID
		);

		_updateIfStale();

		Assert.assertEquals(2, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(2, _getUpdatesCount(_TOOL_SET_NAME_2));
	}

	@Test
	public void testUpdateIfStaleWhenNothingIsStale() {
		_updateIfStale();
		_updateIfStale();

		Mockito.verify(
			_mcpToolIndexCreator
		).createIfNotExists(
			_COMPANY_ID
		);
	}

	@Test
	public void testUpdateIfStaleWhenToolCannotBeIndexed() {
		String failureMessage = RandomTestUtil.randomString();
		String toolKey = ToolSetUtil.getToolKey(_TOOL_NAME_1, _TOOL_SET_NAME_2);

		BulkDocumentItemResponse bulkDocumentItemResponse =
			new BulkDocumentItemResponse();

		bulkDocumentItemResponse.setFailureMessage(failureMessage);
		bulkDocumentItemResponse.setId(toolKey);

		_bulkDocumentResponse.addBulkDocumentItemResponse(
			bulkDocumentItemResponse);

		_bulkDocumentResponse.setErrors(true);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				MCPToolIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			_updateIfStale();

			_assertLogEntry(
				logCapture,
				StringBundler.concat(
					"Unable to index tool \"", toolKey, "\": ",
					failureMessage));
		}

		_bulkDocumentRequests.clear();

		_bulkDocumentResponse = new BulkDocumentResponse(0);

		_updateIfStale();

		Assert.assertEquals(
			_bulkDocumentRequests.toString(), 0, _bulkDocumentRequests.size());

		_mcpToolIndexWriter.invalidate(_COMPANY_ID, _TOOL_SET_NAME_2);

		_updateIfStale();

		Assert.assertEquals(0, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_2));
	}

	@Test
	public void testUpdateIfStaleWhenToolSetCannotBeBuilt() {
		Mockito.when(
			_openAPIDocument2.getContentString(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			null
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				MCPToolIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			_updateIfStale();

			_assertLogEntry(
				logCapture,
				"Unable to build the tools of tool set \"" + _TOOL_SET_NAME_2 +
					"\"");
		}

		Map<String, Document> documents = _getIndexedDocuments();

		Assert.assertEquals(_getToolKeys(_TOOL_SET_NAME_1), documents.keySet());

		_assertDeleteByQueryDocumentRequest(
			_getToolKeys(_TOOL_SET_NAME_1),
			SetUtil.fromArray(_TOOL_SET_NAME_1));

		_updateIfStale();

		Mockito.verify(
			_openAPIDocument2
		).getContentString(
			Mockito.anyString(), Mockito.any()
		);
	}

	@Test
	public void testUpdateIfStaleWhenToolSetHashChanged() {
		_mockIndexedToolSetHashes(
			HashMapBuilder.put(
				_TOOL_SET_NAME_1, Arrays.asList(RandomTestUtil.randomString())
			).put(
				_TOOL_SET_NAME_2,
				Arrays.asList(
					_getToolSetHash(_openAPIDocument2),
					RandomTestUtil.randomString())
			).build());

		_updateIfStale();

		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_2));

		_assertDeleteByQueryDocumentRequest(
			_getToolKeys(_TOOL_SET_NAME_1, _TOOL_SET_NAME_2),
			SetUtil.fromArray(_TOOL_SET_NAME_1, _TOOL_SET_NAME_2));
	}

	@Test
	public void testUpdateIfStaleWhenToolSetIsInvalidatedDuringUpdate() {
		_updateIfStale();

		ToolSetUtil.clearOpenAPIJSONObjectCache(_COMPANY_ID);

		_mcpToolIndexWriter.invalidate(_COMPANY_ID, _TOOL_SET_NAME_1);

		String openAPIDocumentContent = _getOpenAPIDocumentContent(
			_toolSetToolNames.get(_TOOL_SET_NAME_1));

		Mockito.when(
			_openAPIDocument1.getContentString(
				Mockito.anyString(), Mockito.any())
		).thenAnswer(
			invocation -> {
				_mcpToolIndexWriter.invalidate(_COMPANY_ID, _TOOL_SET_NAME_1);

				return openAPIDocumentContent;
			}
		);

		_updateIfStale();
		_updateIfStale();

		Assert.assertEquals(3, _getUpdatesCount(_TOOL_SET_NAME_1));
		Assert.assertEquals(1, _getUpdatesCount(_TOOL_SET_NAME_2));
	}

	@Test
	public void testUpdateIfStaleWhenToolSetWasRemoved() {
		String removedToolSetName = RandomTestUtil.randomString();

		_mockIndexedToolSetHashes(
			HashMapBuilder.put(
				_TOOL_SET_NAME_1,
				Arrays.asList(_getToolSetHash(_openAPIDocument1))
			).put(
				_TOOL_SET_NAME_2,
				Arrays.asList(_getToolSetHash(_openAPIDocument2))
			).put(
				removedToolSetName, Arrays.asList(RandomTestUtil.randomString())
			).build());

		_updateIfStale();

		Assert.assertEquals(
			_bulkDocumentRequests.toString(), 0, _bulkDocumentRequests.size());

		_assertDeleteByQueryDocumentRequest(
			Collections.emptySet(), SetUtil.fromArray(removedToolSetName));
	}

	private void _assertDeleteByQueryDocumentRequest(
		Set<String> toolKeys, Set<String> toolSetNames) {

		Assert.assertEquals(
			_deleteByQueryDocumentRequests.toString(), 1,
			_deleteByQueryDocumentRequests.size());

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			_deleteByQueryDocumentRequests.get(0);

		BooleanQuery booleanQuery =
			(BooleanQuery)deleteByQueryDocumentRequest.getPortalSearchQuery();

		List<?> mustQueryClauses = booleanQuery.getMustQueryClauses();

		TermsQuery toolSetNamesTermsQuery = (TermsQuery)mustQueryClauses.get(0);

		Assert.assertEquals(
			toolSetNames,
			SetUtil.fromArray(toolSetNamesTermsQuery.getValues()));

		List<?> mustNotQueryClauses = booleanQuery.getMustNotQueryClauses();

		if (toolKeys.isEmpty()) {
			Assert.assertEquals(
				mustNotQueryClauses.toString(), 0, mustNotQueryClauses.size());

			return;
		}

		TermsQuery toolKeysTermsQuery = (TermsQuery)mustNotQueryClauses.get(0);

		Assert.assertEquals(
			toolKeys, SetUtil.fromArray(toolKeysTermsQuery.getValues()));
	}

	private void _assertLogEntry(LogCapture logCapture, String message) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(message, logEntry.getMessage());
	}

	private Map<String, Document> _getIndexedDocuments() {
		Map<String, Document> documents = new HashMap<>();

		for (BulkDocumentRequest bulkDocumentRequest : _bulkDocumentRequests) {
			for (BulkableDocumentRequest<?> bulkableDocumentRequest :
					bulkDocumentRequest.getBulkableDocumentRequests()) {

				IndexDocumentRequest indexDocumentRequest =
					(IndexDocumentRequest)bulkableDocumentRequest;

				documents.put(
					indexDocumentRequest.getUid(),
					indexDocumentRequest.getDocument());
			}
		}

		return documents;
	}

	private String _getOpenAPIDocumentContent(String... toolNames) {
		JSONObject pathsJSONObject = JSONFactoryUtil.createJSONObject();

		for (String toolName : toolNames) {
			pathsJSONObject.put(
				StringPool.SLASH + toolName,
				JSONUtil.put("post", JSONUtil.put("operationId", toolName)));
		}

		return JSONUtil.put(
			"paths", pathsJSONObject
		).toString();
	}

	private Set<String> _getToolKeys(String... toolSetNames) {
		Set<String> toolKeys = new HashSet<>();

		for (String toolSetName : toolSetNames) {
			for (String toolName : _toolSetToolNames.get(toolSetName)) {
				toolKeys.add(ToolSetUtil.getToolKey(toolName, toolSetName));
			}
		}

		return toolKeys;
	}

	private String _getToolSetHash(
		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument) {

		Map<String, String> toolSetHashes = ReflectionTestUtil.invoke(
			_mcpToolIndexWriter, "_getToolSetHashes",
			new Class<?>[] {Map.class},
			Collections.singletonMap("toolSet", openAPIDocument));

		return toolSetHashes.get("toolSet");
	}

	private int _getUpdatesCount(String toolSetName) {
		int updatesCount = 0;

		for (BulkDocumentRequest bulkDocumentRequest : _bulkDocumentRequests) {
			if (!_hasToolSet(bulkDocumentRequest, toolSetName)) {
				continue;
			}

			updatesCount++;
		}

		return updatesCount;
	}

	private boolean _hasToolSet(
		BulkDocumentRequest bulkDocumentRequest, String toolSetName) {

		for (BulkableDocumentRequest<?> bulkableDocumentRequest :
				bulkDocumentRequest.getBulkableDocumentRequests()) {

			IndexDocumentRequest indexDocumentRequest =
				(IndexDocumentRequest)bulkableDocumentRequest;

			Document document = indexDocumentRequest.getDocument();

			if (Objects.equals(
					document.getString(MCPToolFields.TOOL_SET_NAME),
					toolSetName)) {

				return true;
			}
		}

		return false;
	}

	private void _mockIndexedToolSetHashes(
		Map<String, List<String>> indexedToolSetHashes) {

		TermsAggregationResult termsAggregationResult =
			new TermsAggregationResult(MCPToolFields.TOOL_SET_NAME, 0, 0);

		for (Map.Entry<String, List<String>> entry :
				indexedToolSetHashes.entrySet()) {

			Bucket bucket = termsAggregationResult.addBucket(entry.getKey(), 1);

			TermsAggregationResult toolSetHashesTermsAggregationResult =
				new TermsAggregationResult(MCPToolFields.TOOL_SET_HASH, 0, 0);

			for (String toolSetHash : entry.getValue()) {
				toolSetHashesTermsAggregationResult.addBucket(toolSetHash, 1);
			}

			bucket.addChildAggregationResult(
				toolSetHashesTermsAggregationResult);
		}

		SearchSearchResponse searchSearchResponse = new SearchSearchResponse();

		searchSearchResponse.addAggregationResult(termsAggregationResult);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(SearchSearchRequest.class))
		).thenReturn(
			searchSearchResponse
		);
	}

	private HeadlessApplicationProvider.OpenAPIDocument _mockOpenAPIDocument(
		String basePath, String... toolNames) {

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			Mockito.mock(HeadlessApplicationProvider.OpenAPIDocument.class);

		HeadlessApplicationProvider.Application application = Mockito.mock(
			HeadlessApplicationProvider.Application.class);

		Mockito.when(
			application.getBasePath()
		).thenReturn(
			basePath
		);

		Mockito.when(
			openAPIDocument.getApplication()
		).thenReturn(
			application
		);

		Mockito.when(
			openAPIDocument.getContentString(Mockito.anyString(), Mockito.any())
		).thenReturn(
			_getOpenAPIDocumentContent(toolNames)
		);

		Mockito.when(
			openAPIDocument.getPath(Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			openAPIDocument.getVersion()
		).thenReturn(
			"v1.0"
		);

		Mockito.when(
			application.getOpenAPIDocuments()
		).thenReturn(
			Collections.singletonList(openAPIDocument)
		);

		List<HeadlessApplicationProvider.ResourceMethod> resourceMethods =
			TransformUtil.transformToList(
				toolNames,
				toolName -> _mockResourceMethod(StringPool.SLASH + toolName));

		Mockito.when(
			application.getResourceMethods()
		).thenReturn(
			resourceMethods
		);

		return openAPIDocument;
	}

	private HeadlessApplicationProvider.ResourceMethod _mockResourceMethod(
		String path) {

		HeadlessApplicationProvider.ResourceMethod resourceMethod =
			Mockito.mock(HeadlessApplicationProvider.ResourceMethod.class);

		Mockito.when(
			resourceMethod.getMethod()
		).thenReturn(
			"POST"
		);

		Mockito.when(
			resourceMethod.getPath()
		).thenReturn(
			path
		);

		return resourceMethod;
	}

	private void _updateIfStale() {
		_mcpToolIndexWriter.updateIfStale(_COMPANY_ID, _httpServletRequest);
	}

	private static final String _BASE_PATH_1 =
		StringPool.SLASH + RandomTestUtil.randomString();

	private static final String _BASE_PATH_2 =
		StringPool.SLASH + RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _TOOL_NAME_1 = RandomTestUtil.randomString();

	private static final String _TOOL_NAME_2 = RandomTestUtil.randomString();

	private static final String _TOOL_SET_NAME_1 =
		_BASE_PATH_1.substring(1) + "-v1.0";

	private static final String _TOOL_SET_NAME_2 =
		_BASE_PATH_2.substring(1) + "-v1.0";

	private static final Map<String, String[]> _toolSetToolNames =
		HashMapBuilder.put(
			_TOOL_SET_NAME_1, new String[] {_TOOL_NAME_1, _TOOL_NAME_2}
		).put(
			_TOOL_SET_NAME_2, new String[] {_TOOL_NAME_1}
		).build();

	private final Aggregations _aggregations = Mockito.mock(Aggregations.class);
	private final List<BulkDocumentRequest> _bulkDocumentRequests =
		new ArrayList<>();
	private BulkDocumentResponse _bulkDocumentResponse =
		new BulkDocumentResponse(0);
	private final List<DeleteByQueryDocumentRequest>
		_deleteByQueryDocumentRequests = new ArrayList<>();
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MCPToolIndexCreator _mcpToolIndexCreator = Mockito.mock(
		MCPToolIndexCreator.class);
	private final MCPToolIndexWriter _mcpToolIndexWriter =
		new MCPToolIndexWriter();
	private final HeadlessApplicationProvider.OpenAPIDocument
		_openAPIDocument1 = _mockOpenAPIDocument(
			_BASE_PATH_1, _toolSetToolNames.get(_TOOL_SET_NAME_1));
	private final HeadlessApplicationProvider.OpenAPIDocument
		_openAPIDocument2 = _mockOpenAPIDocument(
			_BASE_PATH_2, _toolSetToolNames.get(_TOOL_SET_NAME_2));
	private final SearchEngineAdapter _searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);

}