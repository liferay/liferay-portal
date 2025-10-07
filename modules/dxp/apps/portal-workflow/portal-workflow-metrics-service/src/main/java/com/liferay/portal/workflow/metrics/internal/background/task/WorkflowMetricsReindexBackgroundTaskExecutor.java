/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.background.task;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.workflow.metrics.internal.background.task.constants.WorkflowMetricsReindexBackgroundTaskConstants;
import com.liferay.portal.workflow.metrics.internal.petra.executor.WorkflowMetricsPortalExecutor;
import com.liferay.portal.workflow.metrics.internal.search.index.WorkflowMetricsIndex;
import com.liferay.portal.workflow.metrics.search.background.task.WorkflowMetricsReindexStatusMessageSender;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexer;
import com.liferay.portal.workflow.metrics.search.index.reindexer.WorkflowMetricsReindexerRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.portal.workflow.metrics.internal.background.task.WorkflowMetricsReindexBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class WorkflowMetricsReindexBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public WorkflowMetricsReindexBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new WorkflowMetricsReindexBackgroundTaskStatusMessageTranslator());
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		String[] indexEntityNames = _getIndexEntityNames(backgroundTask);

		_sendStatusMessage(
			backgroundTask.getCompanyId(),
			WorkflowMetricsReindexBackgroundTaskConstants.START,
			indexEntityNames);

		_workflowMetricsReindexStatusMessageSender.sendStatusMessage(
			0, indexEntityNames.length, StringPool.BLANK);

		for (String indexEntityName : indexEntityNames) {
			WorkflowMetricsIndex workflowMetricsIndex =
				WorkflowMetricsIndex.toWorkflowMetricsIndex(indexEntityName);

			workflowMetricsIndex.deleteAllDocuments(
				_searchCapabilities, _searchEngineAdapter, _queries,
				_indexNameBuilder, backgroundTask.getCompanyId());
		}

		List<NoticeableFuture<?>> noticeableFutures = new ArrayList<>();

		for (int i = 0; i < indexEntityNames.length; i++) {
			int count = i + 1;
			String indexEntityName = indexEntityNames[i];

			noticeableFutures.add(
				_workflowMetricsPortalExecutor.execute(
					() -> {
						WorkflowMetricsReindexer workflowMetricsReindexer =
							_workflowMetricsReindexerRegistry.
								getWorkflowMetricsReindexer(indexEntityName);

						try (SafeCloseable safeCloseable =
								CompanyThreadLocal.
									setCompanyIdWithSafeCloseable(
										backgroundTask.getCompanyId())) {

							workflowMetricsReindexer.reindex(
								backgroundTask.getCompanyId());
						}

						_workflowMetricsReindexStatusMessageSender.
							sendStatusMessage(
								count, indexEntityNames.length,
								StringPool.BLANK);
					}));
		}

		for (NoticeableFuture<?> noticeableFuture : noticeableFutures) {
			noticeableFuture.get();
		}

		_sendStatusMessage(
			backgroundTask.getCompanyId(),
			WorkflowMetricsReindexBackgroundTaskConstants.END);

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	private String[] _getIndexEntityNames(BackgroundTask backgroundTask) {
		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		List<String> indexEntityNames = ListUtil.sort(
			TransformUtil.transformToList(
				(String[])taskContextMap.get(
					"workflow.metrics.index.entity.names"),
				name -> {
					if (_workflowMetricsReindexerRegistry.containsKey(name)) {
						return name;
					}

					return null;
				}),
			Comparator.comparing(
				indexEntityName -> indexEntityName.startsWith("sla")));

		return indexEntityNames.toArray(new String[0]);
	}

	private void _sendStatusMessage(
		long companyId, String phase, String... indexEntityNames) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(
			WorkflowMetricsReindexBackgroundTaskConstants.COMPANY_ID,
			companyId);

		if (ArrayUtil.isNotEmpty(indexEntityNames)) {
			message.put(
				WorkflowMetricsReindexBackgroundTaskConstants.INDEX_ENTITY_NAME,
				indexEntityNames[0]);
			message.put(
				WorkflowMetricsReindexBackgroundTaskConstants.
					INDEX_ENTITY_NAMES,
				indexEntityNames);
		}

		message.put(WorkflowMetricsReindexBackgroundTaskConstants.PHASE, phase);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private SearchCapabilities _searchCapabilities;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private WorkflowMetricsPortalExecutor _workflowMetricsPortalExecutor;

	@Reference
	private WorkflowMetricsReindexerRegistry _workflowMetricsReindexerRegistry;

	@Reference
	private WorkflowMetricsReindexStatusMessageSender
		_workflowMetricsReindexStatusMessageSender;

}