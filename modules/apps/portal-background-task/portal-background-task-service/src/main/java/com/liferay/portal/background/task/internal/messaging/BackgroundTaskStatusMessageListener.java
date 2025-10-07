/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.messaging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.constants.BackgroundTaskPortletKeys;
import com.liferay.portal.background.task.internal.BackgroundTaskImpl;
import com.liferay.portal.background.task.internal.lock.helper.BackgroundTaskLockHelper;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageTranslator;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistry;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Michael C. Han
 */
public class BackgroundTaskStatusMessageListener extends BaseMessageListener {

	public BackgroundTaskStatusMessageListener(
		BackgroundTaskLocalService backgroundTaskLocalService,
		BackgroundTaskStatusRegistry backgroundTaskStatusRegistry,
		LockManager lockManager, RoleLocalService roleLocalService,
		UserLocalService userLocalService,
		UserNotificationEventLocalService userNotificationEventLocalService) {

		_backgroundTaskLocalService = backgroundTaskLocalService;
		_backgroundTaskStatusRegistry = backgroundTaskStatusRegistry;

		_backgroundTaskLockHelper = new BackgroundTaskLockHelper(lockManager);

		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		_translateBackgroundTaskStatusMessage(message);

		int status = message.getInteger("status");

		if (status == BackgroundTaskConstants.STATUS_CANCELLED) {
			_executeBackgroundTasks(message);
		}
		else if (status == BackgroundTaskConstants.STATUS_FAILED) {
			_executeBackgroundTasks(message);

			_sendUserNotificationEvents(message);
		}
		else if (status == BackgroundTaskConstants.STATUS_QUEUED) {
			long backgroundTaskId = message.getLong(
				BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

			if (!_backgroundTaskLockHelper.isLockedBackgroundTask(
					new BackgroundTaskImpl(
						_backgroundTaskLocalService.fetchBackgroundTask(
							backgroundTaskId)))) {

				_executeBackgroundTasks(message);
			}
		}
		else if (status == BackgroundTaskConstants.STATUS_SUCCESSFUL) {
			_executeBackgroundTasks(message);

			_deleteBackgroundTask(message);
		}
	}

	private void _deleteBackgroundTask(Message message) throws Exception {
		long backgroundTaskId = message.getLong(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId);

		if (backgroundTask == null) {
			return;
		}

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		boolean deleteOnSuccess = GetterUtil.getBoolean(
			taskContextMap.get(
				BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS));

		if (!deleteOnSuccess) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Deleting background task " + backgroundTask.toString());
		}

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTaskId);
	}

	private void _executeBackgroundTasks(Message message) {
		String taskExecutorClassName = message.getString(
			"taskExecutorClassName");

		if (Validator.isNull(taskExecutorClassName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Message " + message +
						" is missing the key \"taskExecutorClassName\"");
			}

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Acquiring next queued background task for " +
					taskExecutorClassName);
		}

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchFirstBackgroundTask(
				taskExecutorClassName, BackgroundTaskConstants.STATUS_QUEUED);

		if (backgroundTask == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No additional queued background tasks for " +
						taskExecutorClassName);
			}

			return;
		}

		_backgroundTaskLocalService.resumeBackgroundTask(
			backgroundTask.getBackgroundTaskId());
	}

	private void _sendUserNotificationEvents(Message message) throws Exception {
		long backgroundTaskId = message.getLong(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId);

		User user = _userLocalService.fetchUser(backgroundTask.getUserId());

		long[] userIds = null;

		if ((user != null) && !user.isGuestUser()) {
			userIds = new long[] {user.getUserId()};
		}
		else {
			Role role = _roleLocalService.fetchRole(
				backgroundTask.getCompanyId(), RoleConstants.ADMINISTRATOR);

			userIds = _userLocalService.getRoleUserIds(role.getRoleId());
		}

		for (long userId : userIds) {
			if (!UserNotificationManagerUtil.isDeliver(
					userId, BackgroundTaskPortletKeys.BACKGROUND_TASK, 0,
					UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY,
					UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

				continue;
			}

			_userNotificationEventLocalService.sendUserNotificationEvents(
				userId, BackgroundTaskPortletKeys.BACKGROUND_TASK,
				UserNotificationDeliveryConstants.TYPE_WEBSITE, false,
				JSONUtil.put(
					"name", backgroundTask.getName()
				).put(
					"taskExecutorClassName",
					backgroundTask.getTaskExecutorClassName()
				));
		}
	}

	private void _translateBackgroundTaskStatusMessage(Message message) {
		long backgroundTaskId = message.getLong(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

		BackgroundTaskStatus backgroundTaskStatus =
			_backgroundTaskStatusRegistry.getBackgroundTaskStatus(
				backgroundTaskId);

		if (backgroundTaskStatus == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to locate status for background task ",
						backgroundTaskId, " to process ", message));
			}

			return;
		}

		BackgroundTaskStatusMessageTranslator
			backgroundTaskStatusMessageTranslator =
				_backgroundTaskStatusRegistry.
					getBackgroundTaskStatusMessageTranslator(backgroundTaskId);

		if (backgroundTaskStatusMessageTranslator != null) {
			backgroundTaskStatusMessageTranslator.translate(
				backgroundTaskStatus, message);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BackgroundTaskStatusMessageListener.class);

	private final BackgroundTaskLocalService _backgroundTaskLocalService;
	private final BackgroundTaskLockHelper _backgroundTaskLockHelper;
	private final BackgroundTaskStatusRegistry _backgroundTaskStatusRegistry;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;
	private final UserNotificationEventLocalService
		_userNotificationEventLocalService;

}