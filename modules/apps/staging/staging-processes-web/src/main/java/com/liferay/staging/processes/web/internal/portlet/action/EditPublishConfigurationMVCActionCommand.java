/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet.action;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;
import com.liferay.trash.service.TrashEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.net.ConnectException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
		"mvc.command.name=/staging_processes/edit_publish_configuration"
	},
	service = MVCActionCommand.class
)
public class EditPublishConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				setLayoutIdMap(actionRequest);

				_updatePublishConfiguration(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteExportImportConfiguration(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteExportImportConfiguration(actionRequest, true);
			}
			else if (cmd.equals(Constants.RELAUNCH)) {
				_relaunchPublishLayoutConfiguration(
					themeDisplay.getUserId(), actionRequest);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreTrashEntries(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof ConnectException ||
				exception instanceof RemoteExportException) {

				_log.error(
					"Unable to connect to remote live: " +
						exception.getMessage());

				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
			else {
				_log.error(exception);
			}

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
	}

	protected void setLayoutIdMap(ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		String treeId = ParamUtil.getString(actionRequest, "treeId");

		actionRequest.setAttribute(
			"layoutIdMap",
			_exportImportHelper.getSelectedLayoutsJSON(
				groupId, privateLayout,
				SessionTreeJSClicks.getOpenNodes(
					httpServletRequest, treeId + "SelectedNode")));
	}

	private void _deleteExportImportConfiguration(
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
					_exportImportConfigurationService.
						moveExportImportConfigurationToTrash(
							deleteExportImportConfigurationId);

				trashedModels.add(exportImportConfiguration);
			}
			else {
				_exportImportConfigurationService.
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

	private void _relaunchPublishLayoutConfiguration(
			long userId, ActionRequest actionRequest)
		throws PortalException {

		long backgroundTaskId = ParamUtil.getLong(
			actionRequest, BackgroundTaskConstants.BACKGROUND_TASK_ID);

		BackgroundTask backgroundTask =
			_backgroundTaskManager.getBackgroundTask(backgroundTaskId);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(),
					"exportImportConfigurationId"));

		exportImportConfiguration =
			ExportImportConfigurationFactory.cloneExportImportConfiguration(
				exportImportConfiguration);

		if (exportImportConfiguration.getType() ==
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL) {

			_staging.publishLayouts(userId, exportImportConfiguration);
		}
		else if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_REMOTE) {

			_staging.copyRemoteLayouts(exportImportConfiguration);
		}
	}

	private void _restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private ExportImportConfiguration _updatePublishConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		long exportImportConfigurationId = ParamUtil.getLong(
			actionRequest, "exportImportConfigurationId");

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group.isStagedRemotely()) {
			if (exportImportConfigurationId > 0) {
				return ExportImportConfigurationUtil.
					updatePublishLayoutRemoteExportImportConfiguration(
						actionRequest);
			}

			return ExportImportConfigurationUtil.
				addPublishLayoutRemoteExportImportConfiguration(actionRequest);
		}

		if (exportImportConfigurationId > 0) {
			return ExportImportConfigurationUtil.
				updatePublishLayoutLocalExportImportConfiguration(
					actionRequest);
		}

		return ExportImportConfigurationUtil.
			addPublishLayoutLocalExportImportConfiguration(actionRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPublishConfigurationMVCActionCommand.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationService _exportImportConfigurationService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

	@Reference
	private TrashEntryService _trashEntryService;

}