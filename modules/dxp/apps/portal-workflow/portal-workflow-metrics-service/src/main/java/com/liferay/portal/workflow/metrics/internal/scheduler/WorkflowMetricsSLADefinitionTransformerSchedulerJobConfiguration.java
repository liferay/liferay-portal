/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.scheduler;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.workflow.metrics.internal.configuration.WorkflowMetricsConfiguration;
import com.liferay.portal.workflow.metrics.internal.sla.transformer.WorkflowMetricsSLADefinitionTransformer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	configurationPid = "com.liferay.portal.workflow.metrics.internal.configuration.WorkflowMetricsConfiguration",
	service = SchedulerJobConfiguration.class
)
public class WorkflowMetricsSLADefinitionTransformerSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _transform(companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _transform(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		WorkflowMetricsConfiguration workflowMetricsConfiguration =
			ConfigurableUtil.createConfigurable(
				WorkflowMetricsConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			workflowMetricsConfiguration.checkSLADefinitionsJobInterval(),
			TimeUnit.MINUTE);
	}

	private BooleanQuery _createBooleanQuery(long companyId) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		return booleanQuery.addMustQueryClauses(
			_queries.term("active", Boolean.TRUE),
			_queries.term("companyId", companyId),
			_queries.term("deleted", Boolean.FALSE));
	}

	private boolean _hasIndex(long companyId) {
		if (!_searchCapabilities.isWorkflowMetricsSupported()) {
			return false;
		}

		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(
				_indexNameBuilder.getIndexName(companyId) +
					WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	private void _transform(long companyId) {
		if (!_hasIndex(companyId)) {
			return;
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS);

		BooleanQuery booleanQuery = _queries.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addFilterQueryClauses(_createBooleanQuery(companyId)));

		searchSearchRequest.setSize(
			GetterUtil.getInteger(
				PropsUtil.get(PropsKeys.INDEX_SEARCH_LIMIT), 10000));

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			Document document = searchHit.getDocument();

			try {
				_workflowMetricsSLADefinitionTransformer.transform(
					document.getLong("companyId"),
					document.getString("version"),
					document.getLong("processId"));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsSLADefinitionTransformerSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	private TriggerConfiguration _triggerConfiguration;

	@Reference
	private WorkflowMetricsSLADefinitionTransformer
		_workflowMetricsSLADefinitionTransformer;

}