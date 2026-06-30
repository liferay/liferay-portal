/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.exportimport.internal.messaging;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.background.task.constants.LayoutSetPrototypeBackgroundTaskConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.model.BackgroundTaskTable;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.site.internal.exportimport.internal.notifications.LayoutSetPrototypeNotificationUtil;

import java.io.File;
import java.io.Serializable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(
	property = "destination.name=" + DestinationNames.BACKGROUND_TASK_STATUS,
	service = MessageListener.class
)
public class LayoutSetPrototypeMergeBackgroundTaskStatusMessageListener
	implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		if (!Objects.equals(
				message.getString("taskExecutorClassName"),
				BackgroundTaskExecutorNames.
					LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR)) {

			return;
		}

		int backgroundTaskStatus = message.getInteger("status");

		if ((backgroundTaskStatus !=
				BackgroundTaskConstants.STATUS_CANCELLED) &&
			(backgroundTaskStatus !=
				BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS) &&
			(backgroundTaskStatus != BackgroundTaskConstants.STATUS_FAILED) &&
			(backgroundTaskStatus !=
				BackgroundTaskConstants.STATUS_SUCCESSFUL)) {

			return;
		}

		long backgroundTaskId = message.getLong("backgroundTaskId");

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId);

		if (backgroundTask == null) {
			return;
		}

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		String sessionId = MapUtil.getString(
			taskContextMap,
			LayoutSetPrototypeBackgroundTaskConstants.SESSION_ID);

		if (Validator.isNull(sessionId)) {
			if (backgroundTaskStatus ==
					BackgroundTaskConstants.STATUS_SUCCESSFUL) {

				_deleteBackgroundTask(backgroundTask);
			}

			return;
		}

		Long[] layoutSetGroupIds =
			LayoutSetPrototypeNotificationUtil.getLayoutSetGroupIds(
				taskContextMap);

		if (layoutSetGroupIds == null) {
			return;
		}

		BackgroundTask[] incompletedBackgroundTasks = _getBackgroundTasks(
			false, layoutSetGroupIds, sessionId);

		if (incompletedBackgroundTasks.length > 0) {
			return;
		}

		Lock lock = _acquireLock(sessionId);

		if (lock == null) {
			return;
		}

		try {
			BackgroundTask[] completedBackgroundTasks = _getBackgroundTasks(
				true, layoutSetGroupIds, sessionId);

			if (completedBackgroundTasks.length < layoutSetGroupIds.length) {
				return;
			}

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
					LayoutSetPrototypeNotificationUtil.getLayoutSetPrototypeId(
						taskContextMap));

			if (layoutSetPrototype == null) {
				return;
			}

			Set<Integer> backgroundTaskStatuses = new HashSet<>(
				TransformUtil.transformToList(
					completedBackgroundTasks, BackgroundTask::getStatus));

			if (LayoutSetPrototypeNotificationUtil.hasPreValidationErrors(
					taskContextMap)) {

				backgroundTaskStatuses.add(
					BackgroundTaskConstants.STATUS_FAILED);
			}

			LayoutSetPrototypeNotificationUtil.sendMergeCompletedNotification(
				backgroundTaskStatuses, layoutSetPrototype,
				LayoutSetPrototypeNotificationUtil.getUserId(taskContextMap));

			for (BackgroundTask completedBackgroundTask :
					completedBackgroundTasks) {

				if (completedBackgroundTask.getStatus() ==
						BackgroundTaskConstants.STATUS_SUCCESSFUL) {

					_deleteBackgroundTask(completedBackgroundTask);
				}
				else {
					_markNotificationProcessed(completedBackgroundTask);
				}
			}

			_deleteCacheFile(sessionId);
		}
		finally {
			_lockManager.unlock(
				lock.getClassName(), lock.getKey(), lock.getOwner());
		}
	}

	private Lock _acquireLock(String sessionId) {
		String lockClassName = LayoutSetPrototype.class.getName();
		String lockKey = "merge-notify:" + sessionId;
		String owner = PortalUUIDUtil.generate();

		Lock lock = _lockManager.lock(lockClassName, lockKey, owner);

		if (!owner.equals(lock.getOwner())) {
			Date createDate = lock.getCreateDate();

			if ((System.currentTimeMillis() - createDate.getTime()) <
					_LOCK_EXPIRATION_TIME) {

				return null;
			}

			lock = _lockManager.lock(
				lockClassName, lockKey, lock.getOwner(), owner);

			if (!owner.equals(lock.getOwner())) {
				return null;
			}
		}

		return lock;
	}

	private void _deleteBackgroundTask(BackgroundTask backgroundTask) {
		try {
			_backgroundTaskLocalService.deleteBackgroundTask(
				backgroundTask.getBackgroundTaskId());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to delete background task " +
					backgroundTask.getBackgroundTaskId(),
				exception);
		}
	}

	private void _deleteCacheFile(String sessionId) {
		File file = new File(_TEMP_DIR + sessionId + ".lar");

		if ((file != null) && file.exists()) {
			try {
				file.delete();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to delete cache file " + file.getAbsolutePath(),
						exception);
				}
			}
		}
	}

	private BackgroundTask[] _getBackgroundTasks(
		boolean completed, Long[] layoutSetGroupIds, String sessionId) {

		List<BackgroundTask> backgroundTasks =
			_backgroundTaskLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					BackgroundTaskTable.INSTANCE
				).from(
					BackgroundTaskTable.INSTANCE
				).where(
					BackgroundTaskTable.INSTANCE.taskExecutorClassName.eq(
						BackgroundTaskExecutorNames.
							LAYOUT_SET_PROTOTYPE_MERGE_BACKGROUND_TASK_EXECUTOR
					).and(
						BackgroundTaskTable.INSTANCE.completed.eq(completed)
					).and(
						BackgroundTaskTable.INSTANCE.groupId.in(
							layoutSetGroupIds)
					)
				));

		return TransformUtil.transformToArray(
			backgroundTasks,
			backgroundTask -> {
				Map<String, Serializable> taskContextMap =
					backgroundTask.getTaskContextMap();

				String backgroundTaskSessionId = MapUtil.getString(
					taskContextMap,
					LayoutSetPrototypeBackgroundTaskConstants.SESSION_ID);

				if (!Objects.equals(backgroundTaskSessionId, sessionId) ||
					(completed &&
					 LayoutSetPrototypeNotificationUtil.isNotificationProcessed(
						 taskContextMap))) {

					return null;
				}

				return backgroundTask;
			},
			BackgroundTask.class);
	}

	private void _markNotificationProcessed(BackgroundTask backgroundTask) {
		try {
			backgroundTask.setTaskContextMap(
				LayoutSetPrototypeNotificationUtil.putNotificationProcessed(
					backgroundTask.getTaskContextMap()));

			backgroundTask = _backgroundTaskLocalService.updateBackgroundTask(
				backgroundTask);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to mark background task " +
					backgroundTask.getBackgroundTaskId() +
						" as notification processed",
				exception);
		}
	}

	private static final long _LOCK_EXPIRATION_TIME = 5 * 60 * 1000;

	private static final String _TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) +
			"/liferay/layout_set_prototype/";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeMergeBackgroundTaskStatusMessageListener.class);

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private LockManager _lockManager;

}