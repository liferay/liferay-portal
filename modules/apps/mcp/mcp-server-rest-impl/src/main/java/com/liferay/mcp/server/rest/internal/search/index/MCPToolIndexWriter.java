/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.mcp.server.rest.internal.search.index.util.MCPToolFactoryUtil;
import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentItemResponse;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = MCPToolIndexWriter.class)
public class MCPToolIndexWriter {

	public void deleteIndex(long companyId) {
		MCPToolIndexState mcpToolIndexState = _getIndexState(companyId);

		synchronized (mcpToolIndexState) {
			_mcpToolIndexCreator.deleteIfExists(companyId);

			_mcpToolIndexStates.remove(companyId, mcpToolIndexState);
		}
	}

	public void invalidate(long companyId) {
		MCPToolIndexState mcpToolIndexState = _getIndexState(companyId);

		mcpToolIndexState.markAllToolSetsStale();
	}

	public void invalidate(long companyId, String toolSetName) {
		MCPToolIndexState mcpToolIndexState = _getIndexState(companyId);

		mcpToolIndexState.markToolSetStale(toolSetName);
	}

	public void updateIfStale(
		long companyId, HttpServletRequest httpServletRequest) {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = ToolSetUtil.getLatestOpenAPIDocuments(companyId);

		openAPIDocuments.remove(ToolSetUtil.TOOL_SET_NAME);

		Map<String, String> toolSetHashes = _getToolSetHashes(openAPIDocuments);

		MCPToolIndexState mcpToolIndexState = _getIndexState(companyId);

		if (!mcpToolIndexState.isStale(toolSetHashes)) {
			return;
		}

		synchronized (mcpToolIndexState) {
			if (!mcpToolIndexState.isStale(toolSetHashes)) {
				return;
			}

			_update(
				companyId, httpServletRequest, mcpToolIndexState,
				openAPIDocuments, toolSetHashes);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		Bundle bundle = bundleContext.getBundle();

		_bundleVersion = String.valueOf(bundle.getVersion());
	}

	private void _deleteDocuments(String indexName, Set<String> toolSetNames) {
		_deleteDocuments(indexName, Collections.emptySet(), toolSetNames);
	}

	private void _deleteDocuments(
		String indexName, Set<String> toolKeys, Set<String> toolSetNames) {

		if (toolSetNames.isEmpty()) {
			return;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		TermsQuery toolSetNamesTermsQuery = QueriesUtil.terms(
			MCPToolFields.TOOL_SET_NAME);

		toolSetNamesTermsQuery.addValues(toolSetNames.toArray());

		booleanQuery.addMustQueryClauses(toolSetNamesTermsQuery);

		if (!toolKeys.isEmpty()) {
			TermsQuery toolKeysTermsQuery = QueriesUtil.terms(
				MCPToolFields.UID);

			toolKeysTermsQuery.addValues(toolKeys.toArray());

			booleanQuery.addMustNotQueryClauses(toolKeysTermsQuery);
		}

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(booleanQuery, indexName);

		deleteByQueryDocumentRequest.setRefresh(true);

		_searchEngineAdapter.execute(deleteByQueryDocumentRequest);
	}

	private Set<String> _getChangedToolSetNames(
		Map<String, String> indexedToolSetHashes,
		Map<String, Object> staleToolSetTokens,
		Map<String, String> toolSetHashes) {

		if (staleToolSetTokens.containsKey(StringPool.STAR)) {
			return new HashSet<>(toolSetHashes.keySet());
		}

		Set<String> changedToolSetNames = new HashSet<>();

		for (Map.Entry<String, String> entry : toolSetHashes.entrySet()) {
			if (Objects.equals(
					entry.getValue(),
					indexedToolSetHashes.get(entry.getKey()))) {

				continue;
			}

			changedToolSetNames.add(entry.getKey());
		}

		for (String staleToolSetName : staleToolSetTokens.keySet()) {
			if (!toolSetHashes.containsKey(staleToolSetName)) {
				continue;
			}

			changedToolSetNames.add(staleToolSetName);
		}

		return changedToolSetNames;
	}

	private Document _getDocument(
		MCPTool mcpTool, String toolSetHash, int toolSetSize) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		String collection = mcpTool.getCollection();

		if (collection != null) {
			documentBuilder.setString(MCPToolFields.COLLECTION, collection);
		}

		documentBuilder.setValue(
			MCPToolFields.DEPRECATED, mcpTool.isDeprecated());
		documentBuilder.setString(
			MCPToolFields.DESCRIPTION, mcpTool.getDescription());
		documentBuilder.setString(
			MCPToolFields.ENTITY_NAME, mcpTool.getEntityName());
		documentBuilder.setStrings(
			MCPToolFields.EXPANSION, mcpTool.getExpansions());
		documentBuilder.setString(MCPToolFields.INTENT, mcpTool.getIntent());
		documentBuilder.setString(MCPToolFields.METHOD, mcpTool.getMethod());
		documentBuilder.setString(
			MCPToolFields.OPERATION_VARIANT, mcpTool.getOperationVariant());
		documentBuilder.setStrings(
			MCPToolFields.PARAMETER_NAMES, mcpTool.getParameterNames());

		String path = mcpTool.getPath();

		documentBuilder.setString(MCPToolFields.PATH, path);
		documentBuilder.setValue(MCPToolFields.PATH_LENGTH, path.length());
		documentBuilder.setValue(
			MCPToolFields.PATH_PARAMETER_COUNT,
			StringUtil.count(path, CharPool.OPEN_CURLY_BRACE));
		documentBuilder.setValue(
			MCPToolFields.PATH_PART_COUNT,
			StringUtil.count(path, CharPool.SLASH));

		documentBuilder.setStrings(
			MCPToolFields.REQUIRED_PROPERTY_COLLECTIONS,
			mcpTool.getRequiredPropertyCollections());
		documentBuilder.setStrings(
			MCPToolFields.SCHEMA_PROPERTY_NAMES,
			mcpTool.getSchemaPropertyNames());
		documentBuilder.setString(
			MCPToolFields.TOOL_NAME, mcpTool.getToolName());
		documentBuilder.setString(MCPToolFields.TOOL_SET_HASH, toolSetHash);
		documentBuilder.setString(
			MCPToolFields.TOOL_SET_NAME, mcpTool.getToolSetName());
		documentBuilder.setValue(MCPToolFields.TOOL_SET_SIZE, toolSetSize);
		documentBuilder.setString(MCPToolFields.UID, mcpTool.getToolKey());

		return documentBuilder.build();
	}

	private MCPToolIndexState _getIndexState(long companyId) {
		return _mcpToolIndexStates.computeIfAbsent(
			companyId, key -> new MCPToolIndexState());
	}

	private String _getIndexedToolSetHash(Bucket toolSetNameBucket) {
		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)toolSetNameBucket.getChildAggregationResult(
				MCPToolFields.TOOL_SET_HASH);

		Collection<Bucket> buckets = termsAggregationResult.getBuckets();

		if (buckets.size() == 1) {
			for (Bucket bucket : buckets) {
				return bucket.getKey();
			}
		}

		return StringPool.BLANK;
	}

	private Map<String, String> _getIndexedToolSetHashes(long companyId) {
		Map<String, String> indexedToolSetHashes = new HashMap<>();

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		TermsAggregation termsAggregation = _aggregations.terms(
			MCPToolFields.TOOL_SET_NAME, MCPToolFields.TOOL_SET_NAME);

		termsAggregation.addChildAggregation(
			_aggregations.terms(
				MCPToolFields.TOOL_SET_HASH, MCPToolFields.TOOL_SET_HASH));
		termsAggregation.setSize(1000);

		searchSearchRequest.addAggregation(termsAggregation);

		searchSearchRequest.setIndexNames(
			_mcpToolIndexCreator.getIndexName(companyId));
		searchSearchRequest.setQuery(QueriesUtil.matchAll());
		searchSearchRequest.setSize(0);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		Map<String, AggregationResult> aggregationResultsMap =
			searchSearchResponse.getAggregationResultsMap();

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResultsMap.get(
				MCPToolFields.TOOL_SET_NAME);

		for (Bucket toolSetNameBucket : termsAggregationResult.getBuckets()) {
			indexedToolSetHashes.put(
				toolSetNameBucket.getKey(),
				_getIndexedToolSetHash(toolSetNameBucket));
		}

		return indexedToolSetHashes;
	}

	private List<MCPTool> _getMCPTools(
		Set<String> changedToolSetNames, Set<String> failedToolSetNames,
		HttpServletRequest httpServletRequest,
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments) {

		List<MCPTool> mcpTools = new ArrayList<>();

		for (String toolSetName : changedToolSetNames) {
			try {
				HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
					openAPIDocuments.get(toolSetName);

				HeadlessApplicationProvider.Application application =
					openAPIDocument.getApplication();

				mcpTools.addAll(
					MCPToolFactoryUtil.getMCPTools(
						application.getBasePath(),
						ToolSetUtil.getOpenAPIJSONObject(
							httpServletRequest, openAPIDocument, toolSetName),
						toolSetName));
			}
			catch (Exception exception) {
				failedToolSetNames.add(toolSetName);

				_log.error(
					StringBundler.concat(
						"Unable to build the tools of tool set \"", toolSetName,
						"\""),
					exception);
			}
		}

		return mcpTools;
	}

	private Set<String> _getRemovedToolSetNames(
		Set<String> indexedToolSetNames, Map<String, String> toolSetHashes) {

		Set<String> removedToolSetNames = new HashSet<>();

		for (String indexedToolSetName : indexedToolSetNames) {
			if (toolSetHashes.containsKey(indexedToolSetName)) {
				continue;
			}

			removedToolSetNames.add(indexedToolSetName);
		}

		return removedToolSetNames;
	}

	private Set<String> _getToolKeys(Collection<MCPTool> mcpTools) {
		Set<String> toolKeys = new HashSet<>();

		for (MCPTool mcpTool : mcpTools) {
			toolKeys.add(mcpTool.getToolKey());
		}

		return toolKeys;
	}

	private String _getToolSetHash(
		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument,
		String toolSetName) {

		CachedToolSetHash cachedToolSetHash = _cachedToolSetHashes.get(
			toolSetName);

		HeadlessApplicationProvider.Application application =
			openAPIDocument.getApplication();

		if ((cachedToolSetHash != null) &&
			(cachedToolSetHash._application == application)) {

			return cachedToolSetHash._toolSetHash;
		}

		List<String> resourceMethodStrings = TransformUtil.transform(
			application.getResourceMethods(),
			resourceMethod ->
				resourceMethod.getMethod() + StringPool.SPACE +
					resourceMethod.getPath());

		Collections.sort(resourceMethodStrings);

		String toolSetHash = DigesterUtil.digest(
			StringBundler.concat(
				_bundleVersion, StringPool.POUND, openAPIDocument.getVersion(),
				StringPool.POUND,
				StringUtil.merge(resourceMethodStrings, StringPool.POUND)));

		_cachedToolSetHashes.put(
			toolSetName, new CachedToolSetHash(application, toolSetHash));

		return toolSetHash;
	}

	private Map<String, String> _getToolSetHashes(
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments) {

		Map<String, String> toolSetHashes = new HashMap<>();

		for (Map.Entry<String, HeadlessApplicationProvider.OpenAPIDocument>
				entry : openAPIDocuments.entrySet()) {

			toolSetHashes.put(
				entry.getKey(),
				_getToolSetHash(entry.getValue(), entry.getKey()));
		}

		return toolSetHashes;
	}

	private Map<String, Integer> _getToolSetSizes(
		Collection<MCPTool> mcpTools) {

		Map<String, Integer> toolSetSizes = new HashMap<>();

		for (MCPTool mcpTool : mcpTools) {
			toolSetSizes.merge(mcpTool.getToolSetName(), 1, Integer::sum);
		}

		return toolSetSizes;
	}

	private Set<String> _index(
		String indexName, Collection<MCPTool> mcpTools,
		Map<String, String> toolSetHashes, Map<String, Integer> toolSetSizes) {

		if (mcpTools.isEmpty()) {
			return Collections.emptySet();
		}

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		Map<String, String> toolKeyToolSetNames = new HashMap<>();

		for (MCPTool mcpTool : mcpTools) {
			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					indexName, mcpTool.getToolKey(),
					_getDocument(
						mcpTool, toolSetHashes.get(mcpTool.getToolSetName()),
						toolSetSizes.get(mcpTool.getToolSetName()))));

			toolKeyToolSetNames.put(
				mcpTool.getToolKey(), mcpTool.getToolSetName());
		}

		bulkDocumentRequest.setRefresh(true);

		BulkDocumentResponse bulkDocumentResponse =
			_searchEngineAdapter.execute(bulkDocumentRequest);

		if (!bulkDocumentResponse.hasErrors()) {
			return Collections.emptySet();
		}

		Set<String> failedToolSetNames = new HashSet<>();

		for (BulkDocumentItemResponse bulkDocumentItemResponse :
				bulkDocumentResponse.getBulkDocumentItemResponses()) {

			String failureMessage =
				bulkDocumentItemResponse.getFailureMessage();

			if (failureMessage == null) {
				continue;
			}

			String toolKey = bulkDocumentItemResponse.getId();

			_log.error(
				StringBundler.concat(
					"Unable to index tool \"", toolKey, "\": ",
					failureMessage));

			failedToolSetNames.add(toolKeyToolSetNames.get(toolKey));
		}

		return failedToolSetNames;
	}

	private void _update(
		long companyId, HttpServletRequest httpServletRequest,
		MCPToolIndexState mcpToolIndexState,
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments,
		Map<String, String> toolSetHashes) {

		Map<String, String> indexedToolSetHashes = new HashMap<>();

		if (_mcpToolIndexCreator.hasIndex(companyId)) {
			indexedToolSetHashes = mcpToolIndexState.getIndexedToolSetHashes();

			if (indexedToolSetHashes.isEmpty()) {
				indexedToolSetHashes = _getIndexedToolSetHashes(companyId);
			}
		}

		_mcpToolIndexCreator.createIfNotExists(companyId);

		Map<String, Object> staleToolSetTokens =
			mcpToolIndexState.getStaleToolSetTokens();

		Set<String> changedToolSetNames = _getChangedToolSetNames(
			indexedToolSetHashes, staleToolSetTokens, toolSetHashes);

		Set<String> failedToolSetNames = new HashSet<>();

		List<MCPTool> mcpTools = _getMCPTools(
			changedToolSetNames, failedToolSetNames, httpServletRequest,
			openAPIDocuments);

		changedToolSetNames.removeAll(failedToolSetNames);

		failedToolSetNames.addAll(
			_updateIndex(
				changedToolSetNames, companyId, indexedToolSetHashes.keySet(),
				mcpTools, toolSetHashes));

		_updateIndexState(
			failedToolSetNames, mcpToolIndexState, staleToolSetTokens,
			toolSetHashes);
	}

	private Set<String> _updateIndex(
		Set<String> changedToolSetNames, long companyId,
		Set<String> indexedToolSetNames, List<MCPTool> mcpTools,
		Map<String, String> toolSetHashes) {

		String indexName = _mcpToolIndexCreator.getIndexName(companyId);

		Set<String> failedToolSetNames = _index(
			indexName, mcpTools, toolSetHashes, _getToolSetSizes(mcpTools));

		_deleteDocuments(
			indexName, _getToolKeys(mcpTools), changedToolSetNames);

		_deleteDocuments(
			indexName,
			_getRemovedToolSetNames(indexedToolSetNames, toolSetHashes));

		return failedToolSetNames;
	}

	private void _updateIndexState(
		Set<String> failedToolSetNames, MCPToolIndexState mcpToolIndexState,
		Map<String, Object> staleToolSetTokens,
		Map<String, String> toolSetHashes) {

		mcpToolIndexState.setIndexedToolSetHashes(toolSetHashes);

		for (Map.Entry<String, Object> entry : staleToolSetTokens.entrySet()) {
			if (failedToolSetNames.contains(entry.getKey())) {
				continue;
			}

			mcpToolIndexState.clearStaleToolSet(
				entry.getKey(), entry.getValue());
		}

		for (String failedToolSetName : failedToolSetNames) {
			mcpToolIndexState.markToolSetFailed(failedToolSetName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPToolIndexWriter.class);

	@Reference
	private Aggregations _aggregations;

	private String _bundleVersion;
	private final Map<String, CachedToolSetHash> _cachedToolSetHashes =
		new ConcurrentHashMap<>();

	@Reference
	private MCPToolIndexCreator _mcpToolIndexCreator;

	private final Map<Long, MCPToolIndexState> _mcpToolIndexStates =
		new ConcurrentHashMap<>();

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	private static class CachedToolSetHash {

		private CachedToolSetHash(
			HeadlessApplicationProvider.Application application,
			String toolSetHash) {

			_application = application;
			_toolSetHash = toolSetHash;
		}

		private final HeadlessApplicationProvider.Application _application;
		private final String _toolSetHash;

	}

	private static class MCPToolIndexState {

		public void clearStaleToolSet(String toolSetName, Object token) {
			_staleToolSetTokens.remove(toolSetName, token);
		}

		public Map<String, String> getIndexedToolSetHashes() {
			return new HashMap<>(_indexedToolSetHashes);
		}

		public Map<String, Object> getStaleToolSetTokens() {
			Map<String, Object> staleToolSetTokens = new HashMap<>();

			long time = System.currentTimeMillis();

			for (Map.Entry<String, Object> entry :
					_staleToolSetTokens.entrySet()) {

				if (!_isRetryDue(entry.getKey(), time)) {
					continue;
				}

				staleToolSetTokens.put(entry.getKey(), entry.getValue());
			}

			return staleToolSetTokens;
		}

		public boolean isStale(Map<String, String> toolSetHashes) {
			if (!_indexedToolSetHashes.equals(toolSetHashes)) {
				return true;
			}

			for (String staleToolSetName : _staleToolSetTokens.keySet()) {
				if (_isRetryDue(staleToolSetName, System.currentTimeMillis())) {
					return true;
				}
			}

			return false;
		}

		public void markAllToolSetsStale() {
			_failedToolSetRetryTimes.clear();

			_staleToolSetTokens.put(StringPool.STAR, new Object());
		}

		public void markToolSetFailed(String toolSetName) {
			_failedToolSetRetryTimes.put(
				toolSetName, System.currentTimeMillis() + Time.MINUTE);

			_staleToolSetTokens.putIfAbsent(toolSetName, new Object());
		}

		public void markToolSetStale(String toolSetName) {
			_failedToolSetRetryTimes.remove(toolSetName);

			_staleToolSetTokens.put(toolSetName, new Object());
		}

		public void setIndexedToolSetHashes(Map<String, String> toolSetHashes) {
			_indexedToolSetHashes.clear();
			_indexedToolSetHashes.putAll(toolSetHashes);
		}

		private boolean _isRetryDue(String toolSetName, long time) {
			Long retryTime = _failedToolSetRetryTimes.get(toolSetName);

			if ((retryTime == null) || (retryTime <= time)) {
				return true;
			}

			return false;
		}

		private final Map<String, Long> _failedToolSetRetryTimes =
			new ConcurrentHashMap<>();
		private final Map<String, String> _indexedToolSetHashes =
			new ConcurrentHashMap<>();
		private final Map<String, Object> _staleToolSetTokens =
			new ConcurrentHashMap<>();

	}

}