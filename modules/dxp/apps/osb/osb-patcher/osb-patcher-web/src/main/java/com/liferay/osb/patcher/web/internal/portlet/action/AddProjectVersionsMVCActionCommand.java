/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/add_project_versions"
	},
	service = MVCActionCommand.class
)
public class AddProjectVersionsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), "PROJECT_VERSIONS",
				"ADD")) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		long patcherProductVersionId = ParamUtil.getLong(
			actionRequest, "patcherProductVersionId");
		long rootPatcherProjectVersionId = ParamUtil.getLong(
			actionRequest, "rootPatcherProjectVersionId");
		String name = ParamUtil.getString(actionRequest, "name");
		boolean combinedBranch = ParamUtil.getBoolean(
			actionRequest, "combinedBranch");
		boolean hide = ParamUtil.getBoolean(actionRequest, "hide");
		String committish = ParamUtil.getString(actionRequest, "committish");
		String repositoryName = ParamUtil.getString(
			actionRequest, "repositoryName");
		String fixedIssues = ParamUtil.getString(actionRequest, "fixedIssues");

		_patcherProjectVersionLocalService.addPatcherProjectVersion(
			themeDisplay.getUserId(), patcherProductVersionId,
			rootPatcherProjectVersionId, combinedBranch, committish,
			fixedIssues, hide, name, repositoryName);
	}

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}