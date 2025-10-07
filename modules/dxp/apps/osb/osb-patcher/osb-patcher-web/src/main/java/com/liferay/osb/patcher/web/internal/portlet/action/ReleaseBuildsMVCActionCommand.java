/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.web.internal.validator.PatcherBuildValidator;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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
		"mvc.command.name=/patcher/release_builds"
	},
	service = MVCActionCommand.class
)
public class ReleaseBuildsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherBuildId = ParamUtil.getLong(
			actionRequest, "patcherBuildId");
		boolean releaseToHelpCenter = ParamUtil.getBoolean(
			actionRequest, "releaseToHelpCenter");
		int status = ParamUtil.getInteger(actionRequest, "status");

		PatcherBuildValidator patcherBuildValidator = new PatcherBuildValidator(
			_portal.getHttpServletRequest(actionRequest));

		PatcherBuild patcherBuild = _patcherBuildLocalService.fetchPatcherBuild(
			patcherBuildId);

		patcherBuildValidator.validateRelease(patcherBuild);

		if (releaseToHelpCenter) {
			PatcherBuildUtil.releasePatcherBuild(patcherBuild);
		}

		_patcherBuildLocalService.updateStatus(
			themeDisplay.getUserId(), patcherBuildId, status);
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private Portal _portal;

}