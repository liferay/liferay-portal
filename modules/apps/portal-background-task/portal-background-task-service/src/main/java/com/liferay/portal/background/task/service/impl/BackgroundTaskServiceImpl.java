/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.background.task.service.base.BackgroundTaskServiceBaseImpl;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"json.web.service.context.name=backgroundtask",
		"json.web.service.context.path=BackgroundTask"
	},
	service = AopService.class
)
public class BackgroundTaskServiceImpl extends BackgroundTaskServiceBaseImpl {

	@Override
	public String getBackgroundTaskStatusJSON(long backgroundTaskId) {
		return backgroundTaskLocalService.getBackgroundTaskStatusJSON(
			backgroundTaskId);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed) {

		return backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName) {

		return backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName);
	}

}