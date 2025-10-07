/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
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
		"mvc.command.name=/patcher/update_comments_field_fixes"
	},
	service = MVCActionCommand.class
)
public class UpdateCommentsFieldFixesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixId = ParamUtil.getLong(actionRequest, "patcherFixId");

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(),
				_patcherFixLocalService.getPatcherFix(patcherFixId),
				PatcherActionKeys.UPDATE_COMMENTS_FIELD,
				themeDisplay.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		String comments = ParamUtil.getString(actionRequest, "comments");

		_patcherFixLocalService.updateComments(patcherFixId, comments);
	}

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

}