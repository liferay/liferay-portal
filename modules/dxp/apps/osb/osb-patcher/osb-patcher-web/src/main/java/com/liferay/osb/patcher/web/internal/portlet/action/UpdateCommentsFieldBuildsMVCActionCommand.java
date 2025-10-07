/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
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
		"mvc.command.name=/patcher/update_comments_field_builds"
	},
	service = MVCActionCommand.class
)
public class UpdateCommentsFieldBuildsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherBuildId = ParamUtil.getLong(
			actionRequest, "patcherBuildId");

		PatcherBuild patcherBuild = _patcherBuildLocalService.getPatcherBuild(
			patcherBuildId);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), patcherBuild,
				PatcherActionKeys.UPDATE_COMMENTS_FIELD,
				patcherBuild.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		String comments = ParamUtil.getString(actionRequest, "comments");

		_patcherBuildLocalService.updateComments(patcherBuildId, comments);
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

}