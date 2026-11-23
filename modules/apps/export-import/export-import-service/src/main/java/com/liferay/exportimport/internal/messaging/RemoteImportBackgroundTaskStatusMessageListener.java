/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.messaging;

import com.liferay.exportimport.internal.background.task.RemoteImportBackgroundTaskExecutor;
import com.liferay.exportimport.internal.notifications.RemoteImportUserNotificationUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "destination.name=" + DestinationNames.BACKGROUND_TASK_STATUS,
	service = MessageListener.class
)
public class RemoteImportBackgroundTaskStatusMessageListener
	implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		if (!Objects.equals(
				message.getString("taskExecutorClassName"),
				RemoteImportBackgroundTaskExecutor.class.getName())) {

			return;
		}

		int status = message.getInteger("status");

		if (!ArrayUtil.contains(_FINAL_STATUSES, status)) {
			return;
		}

		BackgroundTask backgroundTask =
			_backgroundTaskManager.fetchBackgroundTask(
				message.getLong(
					BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID));

		if (backgroundTask == null) {
			return;
		}

		try {
			RemoteImportUserNotificationUtil.sendUserNotificationEvent(
				_getObjectEntry(backgroundTask, status));
		}
		catch (Exception exception) {
			throw new MessageListenerException(exception);
		}
	}

	private String _getErrorMessage(Locale locale, int status) {
		if (status == BackgroundTaskConstants.STATUS_CANCELLED) {
			return _language.get(
				locale, "the-remote-import-was-cancelled-before-it-finished");
		}

		if (status == BackgroundTaskConstants.STATUS_FAILED) {
			return _language.get(
				locale, "the-remote-import-stopped-before-it-finished");
		}

		return null;
	}

	private Locale _getLocale(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return LocaleUtil.getDefault();
		}

		return user.getLocale();
	}

	private ObjectEntry _getObjectEntry(
			BackgroundTask backgroundTask, int status)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			MapUtil.getLong(
				backgroundTask.getTaskContextMap(), "objectEntryId"));

		if ((objectEntry == null) ||
			(status == BackgroundTaskConstants.STATUS_SUCCESSFUL) ||
			ArrayUtil.contains(
				_FINAL_STATUSES,
				MapUtil.getInteger(objectEntry.getValues(), "importStatus"))) {

			return objectEntry;
		}

		return _objectEntryLocalService.partialUpdateObjectEntry(
			backgroundTask.getUserId(), objectEntry.getObjectEntryId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"errorMessage",
				_getErrorMessage(_getLocale(objectEntry.getUserId()), status)
			).put(
				"importStatus", status
			).build(),
			new ServiceContext());
	}

	private static final int[] _FINAL_STATUSES = {
		BackgroundTaskConstants.STATUS_CANCELLED,
		BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
		BackgroundTaskConstants.STATUS_FAILED,
		BackgroundTaskConstants.STATUS_SUCCESSFUL
	};

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}