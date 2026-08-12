/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index.reindexer;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.workflow.metrics.internal.background.task.WorkflowMetricsSLAProcessBackgroundTaskHelper;
import com.liferay.portal.workflow.metrics.internal.search.index.SLAInstanceResultWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = {IndexReindexer.class, WorkflowMetricsReindexer.class})
public class SLAInstanceResultWorkflowMetricsReindexer
	implements IndexReindexer, WorkflowMetricsReindexer {

	@Override
	public String getKey() {
		return "sla-instance-result";
	}

	@Override
	public void reindex(long companyId) throws PortalException {
		_createDefaultDocuments(companyId);

		WorkflowMetricsSLAProcessBackgroundTaskHelper
			workflowMetricsSLAProcessBackgroundTaskHelper =
				_workflowMetricsSLAProcessBackgroundTaskHelperSnapshot.get();

		if (workflowMetricsSLAProcessBackgroundTaskHelper != null) {
			workflowMetricsSLAProcessBackgroundTaskHelper.addBackgroundTasks(
				true);
		}
	}

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		reindex(companyId);
	}

	private void _createDefaultDocuments(long companyId) {
		if (!_searchCapabilities.isWorkflowMetricsSupported() ||
			!_hasIndex(
				_indexNameBuilder.getIndexName(companyId) +
					WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS)) {

			return;
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			QueriesUtil.term("companyId", companyId),
			QueriesUtil.term("deleted", Boolean.FALSE));

		searchSearchRequest.setQuery(booleanQuery);

		searchSearchRequest.setSize(10000);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		if (searchHits.getTotalHits() == 0) {
			return;
		}

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			bulkDocumentRequest.addBulkableDocumentRequest(
				new IndexDocumentRequest(
					_slaInstanceResultWorkflowMetricsIndexer.getIndexName(
						companyId),
					_slaInstanceResultWorkflowMetricsIndexer.
						createDefaultDocument(
							companyId, document.getLong("processId"))));
		}

		if (ListUtil.isNotEmpty(
				bulkDocumentRequest.getBulkableDocumentRequests())) {

			if (PortalRunMode.isTestMode()) {
				bulkDocumentRequest.setRefresh(true);
			}

			_searchEngineAdapter.execute(bulkDocumentRequest);
		}
	}

	private boolean _hasIndex(String indexName) {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(indexName);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	private static final Snapshot<WorkflowMetricsSLAProcessBackgroundTaskHelper>
		_workflowMetricsSLAProcessBackgroundTaskHelperSnapshot = new Snapshot<>(
			SLAInstanceResultWorkflowMetricsReindexer.class,
			WorkflowMetricsSLAProcessBackgroundTaskHelper.class, null, true);

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SLAInstanceResultWorkflowMetricsIndexer
		_slaInstanceResultWorkflowMetricsIndexer;

}