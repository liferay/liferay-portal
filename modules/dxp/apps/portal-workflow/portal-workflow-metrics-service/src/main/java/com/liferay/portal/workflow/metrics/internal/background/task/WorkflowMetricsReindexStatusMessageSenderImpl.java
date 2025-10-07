/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.workflow.metrics.internal.background.task.constants.WorkflowMetricsReindexBackgroundTaskConstants;
import com.liferay.portal.workflow.metrics.search.background.task.WorkflowMetricsReindexStatusMessageSender;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = WorkflowMetricsReindexStatusMessageSender.class)
public class WorkflowMetricsReindexStatusMessageSenderImpl
	implements WorkflowMetricsReindexStatusMessageSender {

	@Override
	public void sendStatusMessage(
		long count, long total, String workflowMetricsIndexEntityName) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(WorkflowMetricsReindexBackgroundTaskConstants.COUNT, count);
		message.put(
			WorkflowMetricsReindexBackgroundTaskConstants.INDEX_ENTITY_NAME,
			workflowMetricsIndexEntityName);
		message.put(WorkflowMetricsReindexBackgroundTaskConstants.TOTAL, total);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

}