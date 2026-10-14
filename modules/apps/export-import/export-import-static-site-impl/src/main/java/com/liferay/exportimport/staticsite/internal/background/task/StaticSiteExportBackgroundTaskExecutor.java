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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
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
import com.liferay.site.staticexport.DisplayPageStaticSiteExportEntry;
import com.liferay.site.staticexport.LayoutStaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExport;
import com.liferay.site.staticexport.StaticSiteExportEntry;
import com.liferay.site.staticexport.StaticSiteExportReport;
import com.liferay.site.staticexport.StaticSiteExportResource;
import com.liferay.site.staticexport.StaticSiteExporter;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.time.Instant;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
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

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		long groupId = MapUtil.getLong(taskContextMap, "groupId");
		Serializable layoutIds = taskContextMap.get("layoutIds");

		Set<Locale> locales = _getLocales(groupId, taskContextMap);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try (StaticSiteExport staticSiteExport = _getStaticSiteExport(
				groupId, layoutIds, locales)) {

			List<StaticSiteExportEntry> staticSiteExportEntries =
				staticSiteExport.getStaticSiteExportEntries();

			for (StaticSiteExportEntry staticSiteExportEntry :
					staticSiteExportEntries) {

				zipWriter.addEntry(
					staticSiteExportEntry.getPath(),
					staticSiteExportEntry.getHTML());
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

			zipWriter.addEntry(
				"manifest.json",
				String.valueOf(
					_getManifestJSONObject(
						layoutIds == null, backgroundTask, groupId, locales,
						staticSiteExport)));
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

	private JSONArray _getFailuresJSONArray(
			List<StaticSiteExportReport.Failure> failures)
		throws Exception {

		return JSONUtil.toJSONArray(
			failures,
			failure -> JSONUtil.put(
				"message", failure.getMessage()
			).put(
				"url", failure.getURL()
			));
	}

	private Set<Locale> _getLocales(
			long groupId, Map<String, Serializable> taskContextMap)
		throws Exception {

		String[] languageIds = GetterUtil.getStringValues(
			taskContextMap.get("languageIds"));

		if (ArrayUtil.isEmpty(languageIds)) {
			return Collections.singleton(_portal.getSiteDefaultLocale(groupId));
		}

		return SetUtil.fromArray(LocaleUtil.fromLanguageIds(languageIds));
	}

	private JSONObject _getManifestJSONObject(
			boolean allLayoutsExported, BackgroundTask backgroundTask,
			long groupId, Set<Locale> locales,
			StaticSiteExport staticSiteExport)
		throws Exception {

		Map<String, JSONObject> displayPageJSONObjects = new LinkedHashMap<>();
		Map<String, JSONObject> layoutJSONObjects = new LinkedHashMap<>();

		for (StaticSiteExportEntry staticSiteExportEntry :
				staticSiteExport.getStaticSiteExportEntries()) {

			Map<String, JSONObject> jsonObjects = layoutJSONObjects;

			if (staticSiteExportEntry instanceof
					DisplayPageStaticSiteExportEntry) {

				jsonObjects = displayPageJSONObjects;
			}

			JSONObject jsonObject = jsonObjects.get(
				staticSiteExportEntry.getKey());

			if (jsonObject == null) {
				jsonObjects.put(
					staticSiteExportEntry.getKey(),
					_getStaticSiteExportEntryJSONObject(staticSiteExportEntry));

				continue;
			}

			JSONObject pathsJSONObject = jsonObject.getJSONObject("paths");

			pathsJSONObject.put(
				LocaleUtil.toLanguageId(staticSiteExportEntry.getLocale()),
				staticSiteExportEntry.getPath());
		}

		StaticSiteExportReport staticSiteExportReport =
			staticSiteExport.getStaticSiteExportReport();

		String[] languageIds = LocaleUtil.toLanguageIds(locales);

		Arrays.sort(languageIds);

		Group group = _groupLocalService.getGroup(groupId);

		return JSONUtil.put(
			"allLayoutsExported", allLayoutsExported
		).put(
			"displayPages",
			JSONUtil.toJSONArray(
				displayPageJSONObjects.values(), jsonObject -> jsonObject)
		).put(
			"exportDate",
			() -> {
				Date createDate = backgroundTask.getCreateDate();

				Instant instant = createDate.toInstant();

				return instant.toString();
			}
		).put(
			"exportProcessId", backgroundTask.getBackgroundTaskId()
		).put(
			"failures",
			JSONUtil.put(
				"layouts",
				_getFailuresJSONArray(
					staticSiteExportReport.getLayoutFailures())
			).put(
				"resources",
				_getFailuresJSONArray(
					staticSiteExportReport.getResourceFailures())
			)
		).put(
			"languageIds", JSONUtil.putAll((Object[])languageIds)
		).put(
			"layouts",
			JSONUtil.toJSONArray(
				layoutJSONObjects.values(), jsonObject -> jsonObject)
		).put(
			"siteExternalReferenceCode", group.getExternalReferenceCode()
		);
	}

	private StaticSiteExport _getStaticSiteExport(
			long groupId, Serializable layoutIds, Set<Locale> locales)
		throws PortalException {

		if (layoutIds == null) {
			return _staticSiteExporter.export(groupId, locales);
		}

		return _staticSiteExporter.export(
			groupId, SetUtil.fromArray(GetterUtil.getLongValues(layoutIds)),
			locales);
	}

	private JSONObject _getStaticSiteExportEntryJSONObject(
		StaticSiteExportEntry staticSiteExportEntry) {

		if (staticSiteExportEntry instanceof DisplayPageStaticSiteExportEntry) {
			DisplayPageStaticSiteExportEntry displayPageStaticSiteExportEntry =
				(DisplayPageStaticSiteExportEntry)staticSiteExportEntry;

			return JSONUtil.put(
				"className", displayPageStaticSiteExportEntry.getClassName()
			).put(
				"classPK", displayPageStaticSiteExportEntry.getClassPK()
			).put(
				"externalReferenceCode",
				displayPageStaticSiteExportEntry.getExternalReferenceCode()
			).put(
				"layoutExternalReferenceCode",
				displayPageStaticSiteExportEntry.
					getLayoutExternalReferenceCode()
			).put(
				"paths",
				JSONUtil.put(
					LocaleUtil.toLanguageId(staticSiteExportEntry.getLocale()),
					staticSiteExportEntry.getPath())
			);
		}

		LayoutStaticSiteExportEntry layoutStaticSiteExportEntry =
			(LayoutStaticSiteExportEntry)staticSiteExportEntry;

		return JSONUtil.put(
			"externalReferenceCode",
			layoutStaticSiteExportEntry.getExternalReferenceCode()
		).put(
			"paths",
			JSONUtil.put(
				LocaleUtil.toLanguageId(staticSiteExportEntry.getLocale()),
				staticSiteExportEntry.getPath())
		);
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StaticSiteExporter _staticSiteExporter;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}