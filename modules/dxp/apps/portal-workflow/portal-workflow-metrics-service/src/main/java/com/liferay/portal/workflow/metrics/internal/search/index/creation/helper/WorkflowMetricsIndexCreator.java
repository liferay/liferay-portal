/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.search.index.creation.helper;

import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.workflow.metrics.internal.background.task.WorkflowMetricsReindexBackgroundTaskExecutor;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = WorkflowMetricsIndexCreator.class)
public class WorkflowMetricsIndexCreator {

	public void createIndex(Company company) throws PortalException {
		for (WorkflowMetricsIndex workflowMetricsIndex :
				WorkflowMetricsIndex.values()) {

			boolean indexCreated = workflowMetricsIndex.createIndex(
				_searchCapabilities, _searchEngineAdapter, _indexNameBuilder,
				company.getCompanyId());

			if (!indexCreated) {
				return;
			}
		}
	}

	public void reindex(Company company) {
		if (!_searchCapabilities.isWorkflowMetricsSupported()) {
			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				CountSearchRequest countSearchRequest =
					new CountSearchRequest();

				countSearchRequest.setIndexNames(
					WorkflowMetricsIndex.getIndexName(
						_indexNameBuilder,
						WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS,
						company.getCompanyId()));
				countSearchRequest.setQuery(QueriesUtil.booleanQuery());

				CountSearchResponse countSearchResponse =
					_searchEngineAdapter.execute(countSearchRequest);

				if (countSearchResponse.getCount() > 0) {
					return null;
				}

				User user = company.getGuestUser();

				String name = PrincipalThreadLocal.getName();

				try {
					PrincipalThreadLocal.setName(user.getUserId());

					_backgroundTaskLocalService.addBackgroundTask(
						user.getUserId(), company.getGroupId(),
						WorkflowMetricsIndexCreator.class.getSimpleName(),
						WorkflowMetricsReindexBackgroundTaskExecutor.class.
							getName(),
						HashMapBuilder.<String, Serializable>put(
							BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS,
							true
						).put(
							"workflow.metrics.index.entity.names",
							new String[] {
								"instance", "node", "process",
								"sla-instance-result", "sla-task-result",
								"task", "transition"
							}
						).build(),
						new ServiceContext());
				}
				finally {
					PrincipalThreadLocal.setName(name);
				}

				return null;
			});
	}

	public void removeIndex(Company company) throws PortalException {
		for (WorkflowMetricsIndex workflowMetricsIndex :
				WorkflowMetricsIndex.values()) {

			boolean indexRemoved = workflowMetricsIndex.removeIndex(
				_searchCapabilities, _searchEngineAdapter, _indexNameBuilder,
				company.getCompanyId());

			if (!indexRemoved) {
				return;
			}
		}
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}