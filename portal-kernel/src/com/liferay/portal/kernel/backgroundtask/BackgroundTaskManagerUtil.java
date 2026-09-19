/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.backgroundtask;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class BackgroundTaskManagerUtil {

	public static BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String taskExecutorClassName,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.addBackgroundTask(
			userId, groupId, name, taskExecutorClassName, taskContextMap,
			serviceContext);
	}

	public static BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String[] servletContextNames, Class<?> taskExecutorClass,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.addBackgroundTask(
			userId, groupId, name, servletContextNames, taskExecutorClass,
			taskContextMap, serviceContext);
	}

	public static void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName, File file)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, file);
	}

	public static BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status, ServiceContext serviceContext) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.amendBackgroundTask(
			backgroundTaskId, taskContextMap, status, serviceContext);
	}

	public static BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status, String statusMessage, ServiceContext serviceContext) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.amendBackgroundTask(
			backgroundTaskId, taskContextMap, status, statusMessage,
			serviceContext);
	}

	public static void cleanUpBackgroundTask(
		BackgroundTask backgroundTask, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.cleanUpBackgroundTask(backgroundTask, status);
	}

	public static void cleanUpBackgroundTasks() {
		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.cleanUpBackgroundTasks();
	}

	public static BackgroundTask deleteBackgroundTask(long backgroundTaskId)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.deleteBackgroundTask(backgroundTaskId);
	}

	public static void deleteCompanyBackgroundTasks(long companyId)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.deleteCompanyBackgroundTasks(companyId);
	}

	public static void deleteGroupBackgroundTasks(long groupId)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.deleteGroupBackgroundTasks(groupId);
	}

	public static void deleteGroupBackgroundTasks(
			long groupId, String name, String taskExecutorClassName)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.deleteGroupBackgroundTasks(
			groupId, name, taskExecutorClassName);
	}

	public static BackgroundTask fetchBackgroundTask(long backgroundTaskId) {
		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.fetchBackgroundTask(backgroundTaskId);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		long groupId, String taskExecutorClassName, boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.fetchFirstBackgroundTask(
			groupId, taskExecutorClassName, completed, orderByComparator);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.fetchFirstBackgroundTask(
			taskExecutorClassName, status);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.fetchFirstBackgroundTask(
			taskExecutorClassName, status, orderByComparator);
	}

	public static BackgroundTask getBackgroundTask(long backgroundTaskId)
		throws PortalException {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTask(backgroundTaskId);
	}

	public static String getBackgroundTaskStatusJSON(long backgroundTaskId) {
		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTaskStatusJSON(
			backgroundTaskId);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(groupId, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassName);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassName, completed, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassName, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassName, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassNames);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassNames, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupId, taskExecutorClassNames, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupIds, taskExecutorClassName, completed, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupIds, taskExecutorClassName, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupIds, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String[] taskExecutorClassNames,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			groupIds, name, taskExecutorClassNames, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			taskExecutorClassName, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			taskExecutorClassName, status, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			taskExecutorClassNames, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasks(
			taskExecutorClassNames, status, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassName, boolean completed,
		int start, int end, boolean orderByType) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksByDuration(
			groupIds, taskExecutorClassName, completed, start, end,
			orderByType);
	}

	public static List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassName, int start, int end,
		boolean orderByType) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksByDuration(
			groupIds, taskExecutorClassName, start, end, orderByType);
	}

	public static int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long groupId, String[] taskExecutorClassNames) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, taskExecutorClassNames);
	}

	public static int getBackgroundTasksCount(
		long groupId, String[] taskExecutorClassNames, boolean completed) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupId, taskExecutorClassNames, completed);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String taskExecutorClassName) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupIds, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String taskExecutorClassName, boolean completed) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupIds, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String[] taskExecutorClassNames) {

		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		return backgroundTaskManager.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassNames);
	}

	public static void resumeBackgroundTask(long backgroundTaskId) {
		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.resumeBackgroundTask(backgroundTaskId);
	}

	public static void triggerBackgroundTask(long backgroundTaskId) {
		BackgroundTaskManager backgroundTaskManager =
			_backgroundTaskManagerSnapshot.get();

		backgroundTaskManager.triggerBackgroundTask(backgroundTaskId);
	}

	private static final Snapshot<BackgroundTaskManager>
		_backgroundTaskManagerSnapshot = new Snapshot<>(
			BackgroundTaskManagerUtil.class, BackgroundTaskManager.class);

}