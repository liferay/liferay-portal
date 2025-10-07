/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.background.task;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrew Betts
 */
@Component(service = ReindexStatusMessageSender.class)
public class ReindexStatusMessageSenderImpl
	implements ReindexStatusMessageSender {

	@Override
	public void sendStatusMessage(String className, long count, long total) {
		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(ReindexBackgroundTaskConstants.CLASS_NAME, className);
		message.put(ReindexBackgroundTaskConstants.COUNT, count);
		message.put(ReindexBackgroundTaskConstants.TOTAL, total);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_sendBackgroundTaskStatusMessage(message);
	}

	@Override
	public void sendStatusMessage(
		String phase, long companyId, long[] companyIds) {

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(ReindexBackgroundTaskConstants.COMPANY_ID, companyId);
		message.put(ReindexBackgroundTaskConstants.COMPANY_IDS, companyIds);
		message.put(ReindexBackgroundTaskConstants.PHASE, phase);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		_sendBackgroundTaskStatusMessage(message);
	}

	private void _sendBackgroundTaskStatusMessage(Message message) {
		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Sent reindex background task status message: " + message);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexStatusMessageSenderImpl.class);

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

}