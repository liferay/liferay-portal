/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.web.internal.validator.PatcherBuildValidator;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
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
		"mvc.command.name=/patcher/update_builds"
	},
	service = MVCActionCommand.class
)
public class UpdateBuildsMVCActionCommand extends BaseMVCActionCommand {

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
				ActionKeys.UPDATE, themeDisplay.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		int type = ParamUtil.getInteger(actionRequest, "type");
		String supportTicket = ParamUtil.getString(
			actionRequest, "supportTicket");

		PatcherBuildValidator patcherBuildValidator = new PatcherBuildValidator(
			_portal.getHttpServletRequest(actionRequest));

		patcherBuildValidator.validateUpdate(patcherBuild);

		if (type != patcherBuild.getType()) {
			_patcherBuildLocalService.updatePatcherBuild(
				themeDisplay.getUserId(), patcherBuildId,
				PatcherBuildUtil.workflowCompletedPatcherBuildQAStatus(
					patcherBuild),
				supportTicket, type);

			PatcherBuildUtil.sendTestJenkinsRequest(
				themeDisplay.getUser(), patcherBuild);

			return;
		}

		String accountEntryCode = StringUtil.toUpperCase(
			ParamUtil.getString(actionRequest, "accountEntryCode"));
		boolean smokeTestOnly = ParamUtil.getBoolean(
			actionRequest, "smokeTestOnly", true);
		boolean mergeOnly = ParamUtil.getBoolean(actionRequest, "mergeOnly");

		patcherBuild = PatcherBuildUtil.versionPatcherBuild(patcherBuild);

		PatcherBuildUtil.savePatcherBuild(
			themeDisplay.getUser(), patcherBuild, accountEntryCode,
			supportTicket, smokeTestOnly, mergeOnly);
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private Portal _portal;

}