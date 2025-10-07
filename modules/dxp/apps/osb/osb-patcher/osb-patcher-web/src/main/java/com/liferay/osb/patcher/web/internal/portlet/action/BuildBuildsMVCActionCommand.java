/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherAccountLocalService;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.web.internal.validator.PatcherBuildValidator;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
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
		"mvc.command.name=/patcher/build_builds"
	},
	service = MVCActionCommand.class
)
public class BuildBuildsMVCActionCommand extends BaseMVCActionCommand {

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
				PatcherActionKeys.BUILD, patcherBuild.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		PatcherBuildValidator patcherBuildValidator = new PatcherBuildValidator(
			_portal.getHttpServletRequest(actionRequest));

		patcherBuildValidator.validateBuild(patcherBuild);

		patcherBuild = PatcherBuildUtil.versionPatcherBuild(patcherBuild);

		if (patcherBuild.isNew()) {
			PatcherAccount patcherAccount =
				_patcherAccountLocalService.getPatcherAccount(
					patcherBuild.getPatcherAccountId());

			PatcherBuildUtil.savePatcherBuild(
				themeDisplay.getUser(), patcherBuild,
				StringUtil.toUpperCase(patcherAccount.getAccountEntryCode()),
				patcherBuild.getSupportTicket(),
				PatcherBuildUtil.isSmokeTestOnly(patcherBuild),
				PatcherBuildUtil.isMergeOnly(patcherBuild));
		}
		else {
			JenkinsUtil.sendDistJenkinsRequest(
				themeDisplay.getUser(), patcherBuild);

			patcherBuild = _patcherBuildLocalService.updateStatus(
				themeDisplay.getUserId(), patcherBuild.getPatcherBuildId(),
				WorkflowConstants.STATUS_BUILD_COMPILING);

			PatcherBuildUtil.workflowParentPatcherBuild(
				themeDisplay.getUser(), patcherBuild);
		}
	}

	@Reference
	private PatcherAccountLocalService _patcherAccountLocalService;

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private Portal _portal;

}