/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.background.task.constants.LayoutSetPrototypeBackgroundTaskConstants;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.sites.kernel.util.Sites;

import java.io.File;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.exportimport.internal.background.task.LayoutSetPrototypeMergeBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LayoutSetPrototypeMergeBackgroundTaskExecutor
	extends BaseExportImportBackgroundTaskExecutor {

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			getExportImportConfiguration(backgroundTask);

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		long layoutSetId = MapUtil.getLong(parameterMap, "layoutSetId");

		try {
			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				layoutSetId);

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.
					getLayoutSetPrototypeByUuidAndCompanyId(
						layoutSet.getLayoutSetPrototypeUuid(),
						layoutSet.getCompanyId());

			_mergeLayoutPrototypeLayouts(layoutSetPrototype);

			MergeLayoutPrototypesThreadLocal.setInProgress(true);

			File larFile = null;

			String sessionId = MapUtil.getString(
				backgroundTask.getTaskContextMap(),
				LayoutSetPrototypeBackgroundTaskConstants.SESSION_ID);

			if (!Validator.isBlank(sessionId)) {
				File cacheFile = new File(
					StringBundler.concat(_TEMP_DIR, sessionId, ".lar"));

				if (cacheFile.exists()) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Using cached layout set prototype LAR file " +
								cacheFile.getAbsolutePath());
					}

					larFile = cacheFile;
				}
				else {
					larFile = _exportImportLocalService.exportLayoutsAsFile(
						exportImportConfiguration);

					try {
						FileUtil.mkdirs(cacheFile.getParentFile());

						Files.move(
							larFile.toPath(), cacheFile.toPath(),
							StandardCopyOption.REPLACE_EXISTING);

						larFile = cacheFile;

						if (_log.isDebugEnabled()) {
							_log.debug(
								"Moved exported LAR to " +
									cacheFile.getAbsolutePath());
						}
					}
					catch (Exception exception) {
						_log.error(
							StringBundler.concat(
								"Unable to move ", larFile.getAbsolutePath(),
								" to ", cacheFile.getAbsolutePath()),
							exception);
					}
				}
			}
			else {
				larFile = _exportImportLocalService.exportLayoutsAsFile(
					exportImportConfiguration);
			}

			User user = _userLocalService.getDefaultUser(
				layoutSet.getCompanyId());

			Map<String, Serializable> importLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						user.getUserId(), layoutSet.getGroupId(),
						layoutSet.isPrivateLayout(), null, parameterMap,
						user.getLocale(), user.getTimeZone());

			ExportImportConfiguration importExportImportConfiguration =
				_exportImportConfigurationLocalService.
					addExportImportConfiguration(
						user.getUserId(), layoutSet.getGroupId(),
						StringPool.BLANK, StringPool.BLANK,
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						importLayoutSettingsMap, WorkflowConstants.STATUS_DRAFT,
						new ServiceContext());

			_exportImportLocalService.importLayouts(
				importExportImportConfiguration, larFile);

			int count =
				_exportImportReportEntryLocalService.
					getExportImportReportEntriesCount(
						importExportImportConfiguration.getCompanyId(),
						importExportImportConfiguration.
							getExportImportConfigurationId());

			if (count > 0) {
				return BackgroundTaskResult.COMPLETED_WITH_ERRORS;
			}

			return BackgroundTaskResult.SUCCESS;
		}
		catch (Throwable throwable) {
			_log.error(
				"The merge process failed for layout set prototype " +
					MapUtil.getLong(parameterMap, "layoutSetPrototypeId"),
				throwable);

			throw new SystemException(throwable);
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setInProgress(false);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		setBackgroundTaskStatusMessageTranslator(
			new LayoutExportImportBackgroundTaskStatusMessageTranslator());

		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	private void _mergeLayoutPrototypeLayouts(
			LayoutSetPrototype layoutSetPrototype)
		throws Exception {

		Group layoutSetPrototypeGroup = layoutSetPrototype.getGroup();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			layoutSetPrototypeGroup.getGroupId(), true);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(layoutSetPrototype.getCompanyId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			for (Layout layout : layouts) {
				if (Validator.isNull(
						layout.getPortletLayoutPageTemplateEntryERC())) {

					continue;
				}

				_sites.mergeLayoutPrototypeLayout(layout);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static final String _TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) +
			"/liferay/layout_set_prototype/";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeMergeBackgroundTaskExecutor.class);

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private Sites _sites;

	@Reference
	private UserLocalService _userLocalService;

}