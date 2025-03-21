/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
		"mvc.command.name=/export_import/edit_publish_configuration"
	},
	service = MVCActionCommand.class
)
public class EditPublishConfigurationMVCActionCommand
	extends EditExportConfigurationMVCActionCommand {

	@Override
	protected void addSessionMessages(ActionRequest actionRequest)
		throws Exception {

		String portletId = portal.getPortletId(actionRequest);
		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		SessionMessages.add(
			actionRequest, portletId + "exportImportConfigurationId",
			exportImportConfigurationId);

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		SessionMessages.add(actionRequest, portletId + "name", name);
		SessionMessages.add(
			actionRequest, portletId + "description", description);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		int exportImportConfigurationType =
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE;

		boolean localPublishing = ParamUtil.getBoolean(
			actionRequest, "localPublishing");

		if (localPublishing) {
			exportImportConfigurationType =
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL;
		}

		Map<String, Serializable> settingsMap =
			exportImportConfigurationSettingsMapFactory.buildSettingsMap(
				actionRequest, groupId, exportImportConfigurationType);

		SessionMessages.add(
			actionRequest, portletId + "settingsMap", settingsMap);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			long exportImportConfigurationId = ParamUtil.getLong(
				actionRequest, "exportImportConfigurationId");

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				setLayoutIdMap(actionRequest);

				_updatePublishConfiguration(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				deleteExportImportConfiguration(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				deleteExportImportConfiguration(actionRequest, true);
			}
			else if (cmd.equals(Constants.PUBLISH_TO_LIVE)) {
				_setBackgroundTaskName(
					actionRequest, exportImportConfigurationId);

				setRedirect(
					actionRequest, actionResponse,
					StagingUtil.publishLayouts(
						themeDisplay.getUserId(), exportImportConfigurationId));
			}
			else if (cmd.equals(Constants.PUBLISH_TO_REMOTE)) {
				_setBackgroundTaskName(
					actionRequest, exportImportConfigurationId);

				setRedirect(
					actionRequest, actionResponse,
					StagingUtil.copyRemoteLayouts(exportImportConfigurationId));
			}
			else if (cmd.equals(Constants.RELAUNCH)) {
				_relaunchPublishLayoutConfiguration(
					themeDisplay.getUserId(), actionRequest);
			}
			else if (Validator.isNull(cmd)) {
				addSessionMessages(actionRequest);
			}

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
	}

	protected void setRedirect(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long backgroundTaskId) {

		actionRequest.setAttribute(
			WebKeys.REDIRECT,
			PortletURLBuilder.createRenderURL(
				portal.getLiferayPortletResponse(actionResponse)
			).setMVCPath(
				"/view_export_import.jsp"
			).setParameter(
				"backgroundTaskId", backgroundTaskId
			).buildString());
	}

	private void _relaunchPublishLayoutConfiguration(
			long userId, ActionRequest actionRequest)
		throws PortalException {

		long backgroundTaskId = ParamUtil.getLong(
			actionRequest, BackgroundTaskConstants.BACKGROUND_TASK_ID);

		BackgroundTask backgroundTask =
			BackgroundTaskManagerUtil.getBackgroundTask(backgroundTaskId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(),
					"exportImportConfigurationId"));

		if (exportImportConfiguration.getType() ==
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL) {

			StagingUtil.publishLayouts(userId, exportImportConfiguration);
		}
		else if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_REMOTE) {

			StagingUtil.copyRemoteLayouts(exportImportConfiguration);
		}
	}

	private void _setBackgroundTaskName(
			ActionRequest actionRequest, long exportImportConfigurationId)
		throws PortalException {

		String name = actionRequest.getParameter("name");

		if (Validator.isBlank(name)) {
			return;
		}

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfigurationId);

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		parameterMap.put("name", new String[] {name});

		exportImportConfiguration.setSettings(
			_jsonFactory.serialize(settingsMap));

		exportImportConfigurationLocalService.updateExportImportConfiguration(
			exportImportConfiguration);
	}

	private ExportImportConfiguration _updatePublishConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		boolean localPublishing = ParamUtil.getBoolean(
			actionRequest, "localPublishing");

		if (exportImportConfigurationId > 0) {
			if (localPublishing) {
				return ExportImportConfigurationUtil.
					updatePublishLayoutLocalExportImportConfiguration(
						actionRequest);
			}

			return ExportImportConfigurationUtil.
				updatePublishLayoutRemoteExportImportConfiguration(
					actionRequest);
		}

		if (localPublishing) {
			return ExportImportConfigurationUtil.
				addPublishLayoutLocalExportImportConfiguration(actionRequest);
		}

		return ExportImportConfigurationUtil.
			addPublishLayoutRemoteExportImportConfiguration(actionRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPublishConfigurationMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

}