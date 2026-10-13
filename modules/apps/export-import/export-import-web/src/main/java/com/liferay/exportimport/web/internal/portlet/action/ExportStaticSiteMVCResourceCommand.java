/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT,
		"jakarta.portlet.name=" + PortletKeys.COMPANY_EXPORT,
		"mvc.command.name=/export_import/export_static_site"
	},
	service = MVCResourceCommand.class
)
public class ExportStaticSiteMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long groupId = ParamUtil.getLong(
				resourceRequest, "groupId", themeDisplay.getScopeGroupId());

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (!GroupPermissionUtil.contains(
					permissionChecker, groupId,
					ActionKeys.EXPORT_IMPORT_LAYOUTS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, Group.class.getName(), groupId,
					ActionKeys.EXPORT_IMPORT_LAYOUTS);
			}

			Group group = _groupService.getGroup(groupId);

			BackgroundTask backgroundTask =
				_backgroundTaskManager.addBackgroundTask(
					themeDisplay.getUserId(), groupId,
					_getName(group, resourceRequest),
					StaticSiteExportBackgroundTaskExecutorNames.
						STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR,
					HashMapBuilder.<String, Serializable>put(
						"groupId", groupId
					).put(
						"languageIds",
						() -> ParamUtil.getStringValues(
							resourceRequest, "languageIds", null)
					).put(
						"layoutIds",
						() -> ParamUtil.getLongValues(
							resourceRequest, "layoutIds", null)
					).build(),
					new ServiceContext());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("id", backgroundTask.getBackgroundTaskId()));

			return false;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private String _getName(Group group, ResourceRequest resourceRequest)
		throws PortalException {

		String name = ParamUtil.getString(resourceRequest, "name");

		if (Validator.isNull(name)) {
			return group.getDescriptiveName();
		}

		return name;
	}

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private GroupService _groupService;

}