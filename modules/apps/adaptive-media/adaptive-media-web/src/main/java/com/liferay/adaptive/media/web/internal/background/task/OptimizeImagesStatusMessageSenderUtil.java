/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.background.task;

import com.liferay.adaptive.media.constants.AMOptimizeImagesBackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Sergio González
 */
public class OptimizeImagesStatusMessageSenderUtil {

	public static void sendStatusMessage(
		String phase, long companyId, String configurationEntryUuid) {

		BackgroundTaskStatusMessageSender backgroundTaskStatusMessageSender =
			_backgroundTaskStatusMessageSenderSnapshot.get();

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put(
			AMOptimizeImagesBackgroundTaskConstants.COMPANY_ID, companyId);
		message.put(
			AMOptimizeImagesBackgroundTaskConstants.CONFIGURATION_ENTRY_UUID,
			configurationEntryUuid);
		message.put(AMOptimizeImagesBackgroundTaskConstants.PHASE, phase);
		message.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private static final Snapshot<BackgroundTaskStatusMessageSender>
		_backgroundTaskStatusMessageSenderSnapshot = new Snapshot<>(
			OptimizeImagesStatusMessageSenderUtil.class,
			BackgroundTaskStatusMessageSender.class);

}