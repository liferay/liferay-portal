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
import com.liferay.portal.kernel.security.permission.ActionKeys;
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
		"mvc.command.name=/patcher/delete_project_versions"
	},
	service = MVCActionCommand.class
)
public class DeleteProjectVersionsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherProjectVersionId = ParamUtil.getLong(
			actionRequest, "patcherProjectVersionId");

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(),
				_patcherProjectVersionLocalService.getPatcherProjectVersion(
					patcherProjectVersionId),
				ActionKeys.DELETE, themeDisplay.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		_patcherProjectVersionLocalService.deletePatcherProjectVersion(
			patcherProjectVersionId);
	}

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}