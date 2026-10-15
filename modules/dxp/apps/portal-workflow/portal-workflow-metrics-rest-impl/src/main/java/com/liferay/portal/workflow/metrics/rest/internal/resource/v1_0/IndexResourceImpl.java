/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.RefreshIndexRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Index;
import com.liferay.portal.workflow.metrics.rest.internal.dto.v1_0.util.IndexUtil;
import com.liferay.portal.workflow.metrics.rest.internal.resource.exception.IndexKeyException;
import com.liferay.portal.workflow.metrics.rest.internal.security.permission.WorkflowMetricsPermissionUtil;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.IndexResource;
import com.liferay.portal.workflow.metrics.search.background.task.WorkflowMetricsBackgroundTaskExecutorNames;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexerRegistry;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rafael Praxedes
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/index.properties",
	scope = ServiceScope.PROTOTYPE, service = IndexResource.class
)
public class IndexResourceImpl extends BaseIndexResourceImpl {

	@Override
	public Page<Index> getIndexesPage() throws Exception {
		WorkflowMetricsPermissionUtil.check();

		return Page.of(
			transform(
				_workflowMetricsReindexerRegistry.getIndexEntityNames(),
				indexEntityName -> IndexUtil.toIndex(
					indexEntityName, _language,
					ResourceBundleUtil.getModuleAndPortalResourceBundle(
						contextAcceptLanguage.getPreferredLocale(),
						IndexResourceImpl.class))));
	}

	@Override
	public void patchIndexRefresh(Index index) throws Exception {
		WorkflowMetricsPermissionUtil.check();

		if (!_searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		if (Objects.isNull(index) || Validator.isNull(index.getKey())) {
			throw new IndexKeyException();
		}

		String[] indexEntityNames = _getIndexEntityNames(index);

		if (ArrayUtil.isEmpty(indexEntityNames)) {
			throw new IndexKeyException();
		}

		_searchEngineAdapter.execute(
			new RefreshIndexRequest(
				transform(
					indexEntityNames,
					indexEntityName -> {
						String indexName = _indexNameBuilder.getIndexName(
							contextCompany.getCompanyId());

						return indexName +
							_indexNameSuffixMap.get(indexEntityName);
					},
					String.class)));
	}

	@Override
	public void patchIndexReindex(Index index) throws Exception {
		WorkflowMetricsPermissionUtil.check();

		if (Objects.isNull(index) || Validator.isNull(index.getKey())) {
			throw new IndexKeyException();
		}

		String[] indexEntityNames = _getIndexEntityNames(index);

		if (ArrayUtil.isEmpty(indexEntityNames)) {
			throw new IndexKeyException();
		}

		_backgroundTaskLocalService.addBackgroundTask(
			contextUser.getUserId(), contextCompany.getGroupId(),
			_getBackgroundTaskName(index),
			WorkflowMetricsBackgroundTaskExecutorNames.
				WORKFLOW_METRICS_REINDEX_BACKGROUND_TASK_EXECUTOR,
			HashMapBuilder.<String, Serializable>put(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS, true
			).put(
				"workflow.metrics.index.entity.names", indexEntityNames
			).put(
				"workflow.metrics.index.key", index.getKey()
			).build(),
			new ServiceContext());
	}

	private String _getBackgroundTaskName(Index index) {
		return StringBundler.concat(
			IndexResourceImpl.class.getSimpleName(), StringPool.DASH,
			contextCompany.getCompanyId(), StringPool.DASH, index.getKey());
	}

	private String[] _getIndexEntityNames(Index index) {
		Set<String> indexEntityNames =
			_workflowMetricsReindexerRegistry.getIndexEntityNames();

		if (Objects.equals(index.getKey(), Index.Group.ALL.getValue())) {
			return indexEntityNames.toArray(new String[0]);
		}
		else if (Objects.equals(
					index.getKey(), Index.Group.METRIC.getValue())) {

			return ArrayUtil.filter(
				indexEntityNames.toArray(new String[0]),
				value -> !value.startsWith("sla"));
		}
		else if (Objects.equals(index.getKey(), Index.Group.SLA.getValue())) {
			return ArrayUtil.filter(
				indexEntityNames.toArray(new String[0]),
				value -> value.startsWith("sla"));
		}
		else if (indexEntityNames.contains(index.getKey())) {
			return new String[] {index.getKey()};
		}

		return new String[0];
	}

	private static final Map<String, String> _indexNameSuffixMap =
		HashMapBuilder.put(
			"instance", WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE
		).put(
			"node", WorkflowMetricsIndexNameConstants.SUFFIX_NODE
		).put(
			"process", WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS
		).put(
			"sla-instance-result",
			WorkflowMetricsIndexNameConstants.SUFFIX_SLA_INSTANCE_RESULT
		).put(
			"sla-task-result",
			WorkflowMetricsIndexNameConstants.SUFFIX_SLA_TASK_RESULT
		).put(
			"task", WorkflowMetricsIndexNameConstants.SUFFIX_TASK
		).put(
			"transition", WorkflowMetricsIndexNameConstants.SUFFIX_TRANSITION
		).build();

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Language _language;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private WorkflowMetricsReindexerRegistry _workflowMetricsReindexerRegistry;

}