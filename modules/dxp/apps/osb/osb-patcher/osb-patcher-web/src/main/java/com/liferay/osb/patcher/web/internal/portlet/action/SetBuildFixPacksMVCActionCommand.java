/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.util.PatcherFixUtil;
import com.liferay.osb.patcher.util.PatcherScanUtil;
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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/set_build_fix_packs"
	},
	service = MVCActionCommand.class
)
public class SetBuildFixPacksMVCActionCommand extends BaseMVCActionCommand {

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
				PatcherActionKeys.SET_BUILD, patcherFixPack.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		PatcherFixPackValidator patcherFixPackValidator =
			new PatcherFixPackValidator(
				_portal.getHttpServletRequest(actionRequest));

		patcherFixPackValidator.validateSetBuild(patcherFixPack);

		List<Long> patcherFixPackPatcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixPackPatcherFixes =
			_patcherFixLocalService.getPatcherFixPackPatcherFixes(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			patcherFixPackPatcherFixIds.add(
				patcherFixPackPatcherFix.getPatcherFixId());
		}

		List<Long> patcherFixIds = new ArrayList<>(patcherFixPackPatcherFixIds);

		List<PatcherFix> previousFixPackBuildFixes =
			PatcherFixUtil.getPreviousFixPackBuildFixes(patcherFixPack);

		for (PatcherFix previousFixPackBuildFix : previousFixPackBuildFixes) {
			if (PatcherFixUtil.isCoveredPatcherFixTickets(
					previousFixPackBuildFix, patcherFixPackPatcherFixes)) {

				continue;
			}

			patcherFixIds.add(previousFixPackBuildFix.getPatcherFixId());
		}

		PatcherScanUtil.refinePatcherFixIds(patcherFixIds);

		PatcherBuild patcherBuild = PatcherBuildUtil.addPatcherFixPackMainBuild(
			themeDisplay.getUser(), patcherFixPack.getPatcherProjectVersionId(),
			patcherFixPack.getName(),
			PatcherBuildConstants.PATCHER_BUILD_ACCOUNT_ENTRY_NAME_LIFERAY,
			WorkflowConstants.STATUS_BUILD_MERGING_ONLY, patcherFixIds,
			themeDisplay);

		patcherFixPack.setPatcherBuildId(patcherBuild.getPatcherBuildId());

		_patcherFixPackLocalService.updatePatcherFixPack(patcherFixPack);

		_patcherFixLocalService.updateObsolete(
			patcherBuild.getPatcherFixId(), false);
	}

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

	@Reference
	private Portal _portal;

}