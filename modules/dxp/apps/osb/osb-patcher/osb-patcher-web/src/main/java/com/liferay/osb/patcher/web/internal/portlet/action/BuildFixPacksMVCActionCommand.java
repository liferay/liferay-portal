/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.web.internal.validator.PatcherFixPackValidator;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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
		"mvc.command.name=/patcher/build_fix_packs"
	},
	service = MVCActionCommand.class
)
public class BuildFixPacksMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixPackId = ParamUtil.getLong(
			actionRequest, "patcherFixPackId");

		PatcherFixPack patcherFixPack =
			_patcherFixPackLocalService.getPatcherFixPack(patcherFixPackId);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), patcherFixPack,
				PatcherActionKeys.BUILD, patcherFixPack.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		PatcherFixPackValidator patcherFixPackValidator =
			new PatcherFixPackValidator(
				_portal.getHttpServletRequest(actionRequest));

		patcherFixPackValidator.validateBuild(patcherFixPack);

		JenkinsUtil.sendDistJenkinsRequest(
			themeDisplay.getUser(),
			_patcherBuildLocalService.getPatcherBuild(
				patcherFixPack.getPatcherBuildId()));
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

	@Reference
	private Portal _portal;

}