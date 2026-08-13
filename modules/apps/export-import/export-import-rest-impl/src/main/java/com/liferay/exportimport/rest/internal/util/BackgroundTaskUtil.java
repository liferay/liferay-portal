/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Daniel Raposo
 */
public class BackgroundTaskUtil {

	public static String getName(BackgroundTask backgroundTask) {
		String name = backgroundTask.getName();

		String taskExecutorClassName =
			backgroundTask.getTaskExecutorClassName();

		if (!StringUtil.equals(
				taskExecutorClassName,
				BackgroundTaskExecutorNames.
					PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR) &&
			!StringUtil.equals(
				taskExecutorClassName,
				BackgroundTaskExecutorNames.
					PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR)) {

			return name;
		}

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				fetchExportImportConfiguration(
					MapUtil.getLong(
						backgroundTask.getTaskContextMap(),
						"exportImportConfigurationId"));

		if (exportImportConfiguration != null) {
			String configurationName = MapUtil.getString(
				exportImportConfiguration.getSettingsMap(), "name");

			if (!Validator.isBlank(configurationName)) {
				return configurationName;
			}
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(name);

		if (portlet == null) {
			return name;
		}

		return portlet.getDisplayName();
	}

	public static Integer getPercentage(long backgroundTaskId) {
		BackgroundTaskStatus backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
				backgroundTaskId);

		if (backgroundTaskStatus == null) {
			return null;
		}

		long allModelAdditionCountersTotal = GetterUtil.getLong(
			backgroundTaskStatus.getAttribute("allModelAdditionCountersTotal"));
		long allPortletAdditionCounter = GetterUtil.getLong(
			backgroundTaskStatus.getAttribute("allPortletAdditionCounter"));

		long allCounters =
			allModelAdditionCountersTotal + allPortletAdditionCounter;

		if (allCounters <= 0) {
			return 0;
		}

		long currentModelAdditionCountersTotal = GetterUtil.getLong(
			backgroundTaskStatus.getAttribute(
				"currentModelAdditionCountersTotal"));
		long currentPortletAdditionCounter = GetterUtil.getLong(
			backgroundTaskStatus.getAttribute("currentPortletAdditionCounter"));

		long currentAdditionCounters =
			currentModelAdditionCountersTotal + currentPortletAdditionCounter;

		return (int)(currentAdditionCounters * 100 / allCounters);
	}

}