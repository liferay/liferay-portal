/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 * @author Michael C. Han
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.exportimport.internal.background.task.LayoutExportBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LayoutExportBackgroundTaskExecutor
	extends BaseExportImportBackgroundTaskExecutor {

	public LayoutExportBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new LayoutExportImportBackgroundTaskStatusMessageTranslator());
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws PortalException {

		ExportImportConfiguration exportImportConfiguration =
			getExportImportConfiguration(backgroundTask);

		setPhasesTotal(backgroundTask, exportImportConfiguration);

		long userId = MapUtil.getLong(
			exportImportConfiguration.getSettingsMap(), "userId");
		String name = StringUtil.replace(
			exportImportConfiguration.getName(), CharPool.SPACE,
			CharPool.UNDERLINE);
		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			exportImportConfiguration);

		_backgroundTaskManager.addBackgroundTaskAttachment(
			userId, backgroundTask.getBackgroundTaskId(),
			StringBundler.concat(
				name, StringPool.DASH, Time.getTimestamp(), ".lar"),
			name + ".lar", larFile);

		return BackgroundTaskResult.SUCCESS;
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

}