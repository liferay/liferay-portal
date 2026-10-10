/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.staticsite.internal.background.task;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportLayout;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "background.task.executor.class.name=" + StaticSiteExportBackgroundTaskExecutorNames.STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR,
	service = BackgroundTaskExecutor.class
)
public class StaticSiteExportBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try (StaticSiteExport staticSiteExport = _getStaticSiteExport(
				backgroundTask.getTaskContextMap())) {

			List<StaticSiteExportLayout> staticSiteExportLayouts =
				staticSiteExport.getStaticSiteExportLayouts();

			for (StaticSiteExportLayout staticSiteExportLayout :
					staticSiteExportLayouts) {

				zipWriter.addEntry(
					staticSiteExportLayout.getPath(),
					staticSiteExportLayout.getHTML());
			}

			List<StaticSiteExportResource> staticSiteExportResources =
				staticSiteExport.getStaticSiteExportResources();

			for (StaticSiteExportResource staticSiteExportResource :
					staticSiteExportResources) {

				try (InputStream inputStream = new FileInputStream(
						staticSiteExportResource.getFile())) {

					zipWriter.addEntry(
						staticSiteExportResource.getPath(), inputStream);
				}
			}
		}

		String name = StringUtil.replace(
			backgroundTask.getName(), CharPool.SPACE, CharPool.UNDERLINE);

		_backgroundTaskManager.addBackgroundTaskAttachment(
			backgroundTask.getUserId(), backgroundTask.getBackgroundTaskId(),
			StringBundler.concat(
				name, StringPool.DASH, Time.getTimestamp(), ".zip"),
			name + ".zip", zipWriter.getFile());

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	private Set<Locale> _getLocales(
			long groupId, Map<String, Serializable> taskContextMap)
		throws PortalException {

		String[] languageIds = GetterUtil.getStringValues(
			taskContextMap.get("languageIds"));

		if (ArrayUtil.isEmpty(languageIds)) {
			return Collections.singleton(_portal.getSiteDefaultLocale(groupId));
		}

		return SetUtil.fromArray(LocaleUtil.fromLanguageIds(languageIds));
	}

	private StaticSiteExport _getStaticSiteExport(
			Map<String, Serializable> taskContextMap)
		throws PortalException {

		long groupId = MapUtil.getLong(taskContextMap, "groupId");

		Set<Locale> locales = _getLocales(groupId, taskContextMap);

		Serializable layoutIds = taskContextMap.get("layoutIds");

		if (layoutIds == null) {
			return _staticSiteExporter.export(groupId, locales);
		}

		return _staticSiteExporter.export(
			groupId, SetUtil.fromArray(GetterUtil.getLongValues(layoutIds)),
			locales);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private Portal _portal;

	@Reference
	private StaticSiteExporter _staticSiteExporter;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}