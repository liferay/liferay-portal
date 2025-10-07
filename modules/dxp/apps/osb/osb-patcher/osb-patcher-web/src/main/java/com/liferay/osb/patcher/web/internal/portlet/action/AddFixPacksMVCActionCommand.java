/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherFixPackConstants;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
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
		"mvc.command.name=/patcher/add_fix_packs"
	},
	service = MVCActionCommand.class
)
public class AddFixPacksMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), "FIX_PACKS", "ADD")) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		long patcherFixComponentId = ParamUtil.getLong(
			actionRequest, "patcherFixComponentId");
		long patcherProjectVersionId = ParamUtil.getLong(
			actionRequest, "patcherProjectVersionId");
		int version = ParamUtil.getInteger(
			actionRequest, "version",
			PatcherFixPackConstants.PATCHER_FIX_PACK_VERSION_DEFAULT);

		_patcherFixPackLocalService.addPatcherFixPack(
			themeDisplay.getUserId(), patcherFixComponentId,
			patcherProjectVersionId, version,
			WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT);
	}

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

}