/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerStatusMessageSender;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.StagedModel;

import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = PortletDataHandlerStatusMessageSender.class)
public class PortletDataHandlerStatusMessageSenderImpl
	implements PortletDataHandlerStatusMessageSender {

	@Override
	public void sendStatusMessage(
		String messageType, String portletId, ManifestSummary manifestSummary) {

		Message message = new Message();

		_init(message, messageType, manifestSummary);

		message.put("portletId", portletId);

		PortletDataHandler portletDataHandler =
			_portletDataHandlerProvider.provide(portletId);

		if (portletDataHandler == null) {
			_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
				message);

			return;
		}

		long portletModelAdditionCountersTotal =
			portletDataHandler.getExportModelCount(manifestSummary);

		if (portletModelAdditionCountersTotal < 0) {
			portletModelAdditionCountersTotal = 0;
		}

		message.put(
			"portletModelAdditionCountersTotal",
			portletModelAdditionCountersTotal);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	@Override
	public void sendStatusMessage(
		String messageType, String[] portletIds,
		ManifestSummary manifestSummary) {

		Message message = new Message();

		_init(message, messageType, manifestSummary);

		message.put("portletIds", portletIds);

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	@Override
	public <T extends StagedModel> void sendStatusMessage(
		String messageType, T stagedModel, ManifestSummary manifestSummary) {

		Message message = new Message();

		_init(message, messageType, manifestSummary);

		StagedModelDataHandler<T> stagedModelDataHandler =
			(StagedModelDataHandler<T>)
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					ExportImportClassedModelUtil.getClassName(stagedModel));

		message.put(
			"stagedModelName",
			stagedModelDataHandler.getDisplayName(stagedModel));

		message.put(
			"stagedModelType",
			String.valueOf(stagedModel.getStagedModelType()));
		message.put("uuid", stagedModel.getUuid());

		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	private void _init(
		Message message, String messageType, ManifestSummary manifestSummary) {

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put("messageType", messageType);

		message.put(
			"modelAdditionCounters",
			new HashMap<>(manifestSummary.getModelAdditionCounters()));

		message.put(
			"modelDeletionCounters",
			new HashMap<>(manifestSummary.getModelDeletionCounters()));
	}

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

}