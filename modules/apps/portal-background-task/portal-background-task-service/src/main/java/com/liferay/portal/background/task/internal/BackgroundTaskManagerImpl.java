/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.background.task.util.comparator.BackgroundTaskCompletionDateComparator;
import com.liferay.portal.background.task.util.comparator.BackgroundTaskCreateDateComparator;
import com.liferay.portal.background.task.util.comparator.BackgroundTaskNameComparator;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = BackgroundTaskManager.class)
public class BackgroundTaskManagerImpl implements BackgroundTaskManager {

	@Override
	public BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String taskExecutorClassName,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.addBackgroundTask(
				userId, groupId, name, taskExecutorClassName, taskContextMap,
				serviceContext);

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String[] servletContextNames, Class<?> taskExecutorClass,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.addBackgroundTask(
				userId, groupId, name, servletContextNames, taskExecutorClass,
				taskContextMap, serviceContext);

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName, File file)
		throws PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, file);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			InputStream inputStream)
		throws PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, inputStream);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String sourceFileName,
			String title, File file)
		throws PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, sourceFileName, title, file);
	}

	@Override
	public BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status, ServiceContext serviceContext) {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.amendBackgroundTask(
				backgroundTaskId, taskContextMap, status, serviceContext);

		if (backgroundTask == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status, String statusMessage, ServiceContext serviceContext) {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.amendBackgroundTask(
				backgroundTaskId, taskContextMap, null, status, statusMessage,
				serviceContext);

		if (backgroundTask == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public void cleanUpBackgroundTask(
		BackgroundTask backgroundTask, int status) {

		_backgroundTaskLocalService.cleanUpBackgroundTask(
			backgroundTask.getBackgroundTaskId(), status);
	}

	@Override
	public void cleanUpBackgroundTasks() {
		_backgroundTaskLocalService.cleanUpBackgroundTasks();
	}

	@Override
	public BackgroundTask deleteBackgroundTask(long backgroundTaskId)
		throws PortalException {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.deleteBackgroundTask(backgroundTaskId);

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public void deleteCompanyBackgroundTasks(long companyId)
		throws PortalException {

		_backgroundTaskLocalService.deleteCompanyBackgroundTasks(companyId);
	}

	@Override
	public void deleteGroupBackgroundTasks(long groupId)
		throws PortalException {

		_backgroundTaskLocalService.deleteGroupBackgroundTasks(groupId);
	}

	@Override
	public void deleteGroupBackgroundTasks(
			long groupId, String name, String taskExecutorClassName)
		throws PortalException {

		_backgroundTaskLocalService.deleteGroupBackgroundTasks(
			groupId, name, taskExecutorClassName);
	}

	@Override
	public BackgroundTask fetchBackgroundTask(long backgroundTaskId) {
		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId);

		if (backgroundTask == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public BackgroundTask fetchFirstBackgroundTask(
		long groupId, String taskExecutorClassName, boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchFirstBackgroundTask(
				groupId, taskExecutorClassName, completed,
				_translate(orderByComparator));

		if (backgroundTask == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status) {

		com.liferay.portal.background.task.model.BackgroundTask
			backgroundTaskModel =
				_backgroundTaskLocalService.fetchFirstBackgroundTask(
					taskExecutorClassName, status);

		if (backgroundTaskModel == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTaskModel);
	}

	@Override
	public BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		com.liferay.portal.background.task.model.BackgroundTask backgroundTask =
			_backgroundTaskLocalService.fetchFirstBackgroundTask(
				taskExecutorClassName, status, _translate(orderByComparator));

		if (backgroundTask == null) {
			return null;
		}

		return new BackgroundTaskImpl(backgroundTask);
	}

	@Override
	public BackgroundTask getBackgroundTask(long backgroundTaskId)
		throws PortalException {

		return new BackgroundTaskImpl(
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(long groupId, int status) {
		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(groupId, status));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, taskExecutorClassName));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, taskExecutorClassName, completed, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int status) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, taskExecutorClassName, status));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, taskExecutorClassName, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, name, taskExecutorClassName, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				new long[] {groupId}, taskExecutorClassNames));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int status) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupId, taskExecutorClassNames, status));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				new long[] {groupId}, taskExecutorClassNames, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupIds, new String[] {taskExecutorClassName}, completed,
				start, end, _translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupIds, new String[] {taskExecutorClassName}, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupIds, name, taskExecutorClassName, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String[] taskExecutorClassNames,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				groupIds, name, taskExecutorClassNames, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				taskExecutorClassName, status));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				taskExecutorClassName, status, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				taskExecutorClassNames, status));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _translate(
			_backgroundTaskLocalService.getBackgroundTasks(
				taskExecutorClassNames, status, start, end,
				_translate(orderByComparator)));
	}

	@Override
	public List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassName, boolean completed,
		int start, int end, boolean orderByType) {

		List<com.liferay.portal.background.task.model.BackgroundTask>
			backgroundTasks =
				_backgroundTaskLocalService.getBackgroundTasksByDuration(
					groupIds, taskExecutorClassName, completed, start, end,
					orderByType);

		return _translate(backgroundTasks);
	}

	@Override
	public List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassName, int start, int end,
		boolean orderByType) {

		List<com.liferay.portal.background.task.model.BackgroundTask>
			backgroundTasks =
				_backgroundTaskLocalService.getBackgroundTasksByDuration(
					groupIds, taskExecutorClassName, start, end, orderByType);

		return _translate(backgroundTasks);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String[] taskExecutorClassNames) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			new long[] {groupId}, taskExecutorClassNames);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String[] taskExecutorClassNames, boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			new long[] {groupId}, taskExecutorClassNames, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, new String[] {taskExecutorClassName});
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String taskExecutorClassName, boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, new String[] {taskExecutorClassName}, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String[] taskExecutorClassNames) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassNames);
	}

	@Override
	public String getBackgroundTaskStatusJSON(long backgroundTaskId) {
		return _backgroundTaskLocalService.getBackgroundTaskStatusJSON(
			backgroundTaskId);
	}

	@Override
	public void resumeBackgroundTask(long backgroundTaskId) {
		_backgroundTaskLocalService.resumeBackgroundTask(backgroundTaskId);
	}

	@Override
	public void triggerBackgroundTask(long backgroundTaskId) {
		_backgroundTaskLocalService.triggerBackgroundTask(backgroundTaskId);
	}

	private List<BackgroundTask> _translate(
		List<com.liferay.portal.background.task.model.BackgroundTask>
			backgroundTasks) {

		return TransformUtil.transform(
			backgroundTasks,
			backgroundTask -> new BackgroundTaskImpl(backgroundTask));
	}

	private OrderByComparator
		<com.liferay.portal.background.task.model.BackgroundTask> _translate(
			OrderByComparator<BackgroundTask> orderByComparator) {

		if (orderByComparator instanceof
				BackgroundTaskCompletionDateComparator) {

			return com.liferay.portal.background.task.internal.comparator.
				BackgroundTaskCompletionDateComparator.getInstance(
					orderByComparator.isAscending());
		}
		else if (orderByComparator instanceof
					BackgroundTaskCreateDateComparator) {

			return com.liferay.portal.background.task.internal.comparator.
				BackgroundTaskCreateDateComparator.getInstance(
					orderByComparator.isAscending());
		}
		else if (orderByComparator instanceof BackgroundTaskNameComparator) {
			return com.liferay.portal.background.task.internal.comparator.
				BackgroundTaskNameComparator.getInstance(
					orderByComparator.isAscending());
		}

		throw new IllegalArgumentException(
			"Invalid class " + ClassUtil.getClassName(orderByComparator));
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

}