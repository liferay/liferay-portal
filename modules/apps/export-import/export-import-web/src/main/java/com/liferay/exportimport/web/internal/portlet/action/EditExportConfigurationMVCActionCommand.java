/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.trash.service.TrashEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.COMPANY_EXPORT,
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT,
		"mvc.command.name=/export_import/edit_export_configuration"
	},
	service = MVCActionCommand.class
)
public class EditExportConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

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

		Map<String, Serializable> settingsMap =
			exportImportConfigurationSettingsMapFactory.buildSettingsMap(
				actionRequest, groupId,
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT);

		SessionMessages.add(
			actionRequest, portletId + "settingsMap", settingsMap);
	}

	protected void deleteExportImportConfiguration(
			ActionRequest actionRequest, boolean moveToTrash)
		throws PortalException {

		long[] deleteExportImportConfigurationIds = null;

		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		if (exportImportConfigurationId > 0) {
			deleteExportImportConfigurationIds = new long[] {
				exportImportConfigurationId
			};
		}
		else {
			deleteExportImportConfigurationIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteExportImportConfigurationIds"),
				0L);
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteExportImportConfigurationId :
				deleteExportImportConfigurationIds) {

			if (moveToTrash) {
				ExportImportConfiguration exportImportConfiguration =
					exportImportConfigurationService.
						moveExportImportConfigurationToTrash(
							deleteExportImportConfigurationId);

				trashedModels.add(exportImportConfiguration);
			}
			else {
				exportImportConfigurationService.
					deleteExportImportConfiguration(
						deleteExportImportConfigurationId);
			}
		}

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				setLayoutIdMap(actionRequest);

				_updateExportConfiguration(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				deleteExportImportConfiguration(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				deleteExportImportConfiguration(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.RELAUNCH)) {
				_relaunchExportLayoutConfiguration(actionRequest);
			}
			else if (Validator.isNull(cmd)) {
				addSessionMessages(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	protected void setLayoutIdMap(ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
			actionRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		String treeId = ParamUtil.getString(actionRequest, "treeId");

		actionRequest.setAttribute(
			"layoutIdMap",
			exportImportHelper.getSelectedLayoutsJSON(
				groupId, privateLayout,
				SessionTreeJSClicks.getOpenNodes(
					httpServletRequest, treeId + "SelectedNode")));
	}

	@Reference
	protected BackgroundTaskManager backgroundTaskManager;

	@Reference
	protected ExportImportConfigurationLocalService
		exportImportConfigurationLocalService;

	@Reference
	protected ExportImportConfigurationService exportImportConfigurationService;

	@Reference
	protected ExportImportConfigurationSettingsMapFactory
		exportImportConfigurationSettingsMapFactory;

	@Reference
	protected ExportImportHelper exportImportHelper;

	@Reference
	protected ExportImportService exportImportService;

	@Reference
	protected Portal portal;

	@Reference
	protected TrashEntryService trashEntryService;

	private void _relaunchExportLayoutConfiguration(ActionRequest actionRequest)
		throws Exception {

		long backgroundTaskId = ParamUtil.getLong(
			actionRequest, BackgroundTaskConstants.BACKGROUND_TASK_ID);

		BackgroundTask backgroundTask = backgroundTaskManager.getBackgroundTask(
			backgroundTaskId);

		ExportImportConfiguration exportImportConfiguration =
			exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(),
					"exportImportConfigurationId"));

		exportImportConfiguration =
			ExportImportConfigurationFactory.cloneExportImportConfiguration(
				exportImportConfiguration);

		exportImportService.exportLayoutsAsFileInBackground(
			exportImportConfiguration);
	}

	private void _restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private ExportImportConfiguration _updateExportConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		if (exportImportConfigurationId > 0) {
			return ExportImportConfigurationUtil.
				updateExportLayoutExportImportConfiguration(actionRequest);
		}

		return ExportImportConfigurationUtil.
			addExportLayoutExportImportConfiguration(actionRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditExportConfigurationMVCActionCommand.class);

}