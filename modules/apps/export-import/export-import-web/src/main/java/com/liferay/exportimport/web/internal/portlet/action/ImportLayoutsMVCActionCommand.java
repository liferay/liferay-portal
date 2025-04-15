/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.exception.LARFileSizeException;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.exception.LayoutImportException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.exception.LayoutPrototypeException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.COMPANY_IMPORT,
		"jakarta.portlet.name=" + ExportImportPortletKeys.IMPORT,
		"mvc.command.name=/export_import/import_layouts"
	},
	service = MVCActionCommand.class
)
public class ImportLayoutsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD_TEMP)) {
				addTempFileEntry(
					actionRequest, ExportImportHelper.TEMP_FOLDER_NAME);

				validateFile(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME);

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE_TEMP)) {
				deleteTempFileEntry(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME);

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (cmd.equals(Constants.IMPORT)) {
				hideDefaultSuccessMessage(actionRequest);

				importData(actionRequest, ExportImportHelper.TEMP_FOLDER_NAME);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if (cmd.equals(Constants.ADD_TEMP) ||
				cmd.equals(Constants.DELETE_TEMP)) {

				hideDefaultSuccessMessage(actionRequest);

				handleUploadException(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME, exception);
			}
			else {
				if (exception instanceof LARFileException ||
					exception instanceof LARFileSizeException ||
					exception instanceof LARTypeException) {

					SessionErrors.add(actionRequest, exception.getClass());
				}
				else if (exception instanceof LayoutPrototypeException ||
						 exception instanceof LocaleException) {

					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}
				else {
					_log.error(exception);

					SessionErrors.add(
						actionRequest, LayoutImportException.class.getName());
				}
			}
		}
	}

	protected void importData(ActionRequest actionRequest, String folderName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		FileEntry fileEntry = _exportImportHelper.getTempFileEntry(
			groupId, themeDisplay.getUserId(), folderName);

		try (InputStream inputStream = _dlFileEntryLocalService.getFileAsStream(
				fileEntry.getFileEntryId(), fileEntry.getVersion(), false)) {

			importData(actionRequest, fileEntry.getTitle(), inputStream);

			deleteTempFileEntry(groupId, folderName);
		}
	}

	protected void importData(
			ActionRequest actionRequest, String fileName,
			InputStream inputStream)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		Map<String, Serializable> importLayoutSettingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportLayoutSettingsMap(
					themeDisplay.getUserId(), groupId, privateLayout, null,
					actionRequest.getParameterMap(), themeDisplay.getLocale(),
					themeDisplay.getTimeZone());

		String name = GetterUtil.getString(
			importLayoutSettingsMap.get("portletId"));

		if (Validator.isNull(name)) {
			name = fileName;
		}

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					themeDisplay.getUserId(), name,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		_exportImportService.importLayoutsInBackground(
			exportImportConfiguration, inputStream);
	}

	protected void validateFile(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String folderName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		FileEntry fileEntry = _exportImportHelper.getTempFileEntry(
			groupId, themeDisplay.getUserId(), folderName);

		try (InputStream inputStream = _dlFileEntryLocalService.getFileAsStream(
				fileEntry.getFileEntryId(), fileEntry.getVersion(), false)) {

			MissingReferences missingReferences = validateFile(
				actionRequest, inputStream);

			Map<String, MissingReference> weakMissingReferences =
				missingReferences.getWeakMissingReferences();

			if (weakMissingReferences.isEmpty()) {
				return;
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"warningMessages",
					() -> {
						if (MapUtil.isEmpty(weakMissingReferences)) {
							return null;
						}

						return _staging.getWarningMessagesJSONArray(
							themeDisplay.getLocale(), weakMissingReferences);
					}));
		}
	}

	protected MissingReferences validateFile(
			ActionRequest actionRequest, InputStream inputStream)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		Map<String, Serializable> importLayoutSettingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportLayoutSettingsMap(
					themeDisplay.getUserId(), groupId, privateLayout, null,
					actionRequest.getParameterMap(), themeDisplay.getLocale(),
					themeDisplay.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					themeDisplay.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		return _exportImportService.validateImportLayoutsFile(
			exportImportConfiguration, inputStream);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImportLayoutsMVCActionCommand.class);

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportService _exportImportService;

	@Reference
	private Staging _staging;

}