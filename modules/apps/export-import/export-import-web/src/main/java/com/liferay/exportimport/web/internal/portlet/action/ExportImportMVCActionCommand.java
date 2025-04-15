/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.exception.LARFileNameException;
import com.liferay.exportimport.kernel.exception.LARFileSizeException;
import com.liferay.exportimport.kernel.exception.LARTypeException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortletIdException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"mvc.command.name=/export_import/export_import"
	},
	service = MVCActionCommand.class
)
public class ExportImportMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Portlet portlet = null;

		try {
			portlet = ActionUtil.getPortlet(actionRequest);
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(actionRequest, principalException.getClass());

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		actionRequest = ActionUtil.getWrappedActionRequest(actionRequest, null);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

			hideDefaultSuccessMessage(actionRequest);

			return;
		}

		long ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();

		if (cmd.equals(Constants.ADD_TEMP) ||
			cmd.equals(Constants.DELETE_TEMP)) {

			long groupId = ParamUtil.getLong(actionRequest, "groupId");

			Group group = _groupLocalService.getGroup(groupId);

			ctCollectionId = group.getCtCollectionId();
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			if (cmd.equals(Constants.ADD_TEMP)) {
				addTempFileEntry(
					actionRequest,
					ExportImportHelper.TEMP_FOLDER_NAME +
						portlet.getPortletId());

				validateFile(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME +
						portlet.getPortletId());

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE_TEMP)) {
				deleteTempFileEntry(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME +
						portlet.getPortletId());

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (cmd.equals(Constants.EXPORT)) {
				hideDefaultSuccessMessage(actionRequest);

				_exportData(actionRequest, portlet);

				sendRedirect(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.IMPORT)) {
				hideDefaultSuccessMessage(actionRequest);

				importData(
					actionRequest,
					ExportImportHelper.TEMP_FOLDER_NAME +
						portlet.getPortletId());

				sendRedirect(actionRequest, actionResponse);
			}
		}
		catch (Exception exception) {
			if (cmd.equals(Constants.ADD_TEMP) ||
				cmd.equals(Constants.DELETE_TEMP)) {

				hideDefaultSuccessMessage(actionRequest);

				handleUploadException(
					actionRequest, actionResponse,
					ExportImportHelper.TEMP_FOLDER_NAME +
						portlet.getPortletId(),
					exception);
			}
			else {
				if (exception instanceof LARFileException ||
					exception instanceof LARFileNameException ||
					exception instanceof LARFileSizeException ||
					exception instanceof LARTypeException ||
					exception instanceof LocaleException ||
					exception instanceof NoSuchLayoutException ||
					exception instanceof PortletIdException ||
					exception instanceof PrincipalException ||
					exception instanceof
						StructureDuplicateStructureKeyException) {

					SessionErrors.add(actionRequest, exception.getClass());
				}
				else {
					_log.error(exception);

					SessionErrors.add(
						actionRequest,
						ExportImportMVCActionCommand.class.getName());
				}
			}
		}
	}

	protected void importData(
			ActionRequest actionRequest, InputStream inputStream)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long plid = ParamUtil.getLong(actionRequest, "plid");
		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		Map<String, Serializable> importPortletSettingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportPortletSettingsMap(
					themeDisplay.getUserId(), plid, groupId,
					portlet.getPortletId(),
					HashMapBuilder.putAll(
						actionRequest.getParameterMap()
					).put(
						"stagingSite",
						() -> {
							Group group = themeDisplay.getScopeGroup();

							return new String[] {
								String.valueOf(group.isStagingGroup())
							};
						}
					).build(),
					themeDisplay.getLocale(), themeDisplay.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					themeDisplay.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					importPortletSettingsMap);

		_exportImportService.importPortletInfoInBackground(
			exportImportConfiguration, inputStream);
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

			importData(actionRequest, inputStream);

			Group group = _groupLocalService.getGroup(groupId);

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						group.getCtCollectionId())) {

				deleteTempFileEntry(groupId, folderName);
			}
		}
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

		long plid = ParamUtil.getLong(actionRequest, "plid");
		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		Map<String, Serializable> importPortletSettingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportPortletSettingsMap(
					themeDisplay.getUserId(), plid, groupId,
					portlet.getPortletId(), actionRequest.getParameterMap(),
					themeDisplay.getLocale(), themeDisplay.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					themeDisplay.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					importPortletSettingsMap);

		return _exportImportService.validateImportPortletInfo(
			exportImportConfiguration, inputStream);
	}

	private void _exportData(ActionRequest actionRequest, Portlet portlet)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long plid = ParamUtil.getLong(actionRequest, "plid");
			long groupId = ParamUtil.getLong(actionRequest, "groupId");
			String fileName = ParamUtil.getString(
				actionRequest, "exportFileName");

			Map<String, Serializable> exportPortletSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildExportPortletSettingsMap(
						themeDisplay.getUserId(), plid, groupId,
						portlet.getPortletId(), actionRequest.getParameterMap(),
						themeDisplay.getLocale(), themeDisplay.getTimeZone(),
						fileName);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						themeDisplay.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
						exportPortletSettingsMap);

			_exportImportService.exportPortletInfoAsFileInBackground(
				exportImportConfiguration);
		}
		catch (Exception exception) {
			if (exception instanceof LARFileNameException) {
				throw exception;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportMVCActionCommand.class);

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
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}