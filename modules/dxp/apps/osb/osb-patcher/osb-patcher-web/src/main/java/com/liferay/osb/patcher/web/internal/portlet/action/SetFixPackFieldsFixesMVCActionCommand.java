/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
import com.liferay.osb.patcher.web.internal.validator.PatcherFixValidator;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/set_fix_pack_fields_fixes"
	},
	service = MVCActionCommand.class
)
public class SetFixPackFieldsFixesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixId = ParamUtil.getLong(actionRequest, "patcherFixId");

		PatcherFix patcherFix = _patcherFixLocalService.getPatcherFix(
			patcherFixId);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), patcherFix,
				PatcherActionKeys.SET_FIX_PACK_FIELDS,
				patcherFix.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		String dependencies = ParamUtil.getString(
			actionRequest, "dependencies");
		int fixPackStatus = ParamUtil.getInteger(
			actionRequest, "fixPackStatus");
		String requirements = ParamUtil.getString(
			actionRequest, "requirements");
		Set<Long> patcherFixPackIds = SetUtil.fromArray(
			ParamUtil.getLongValues(actionRequest, "patcherFixPackIds"));

		PatcherFixValidator patcherFixValidator = new PatcherFixValidator(
			_portal.getHttpServletRequest(actionRequest));

		patcherFixValidator.validateSetFixPackFields(patcherFix);

		_patcherFixPackLocalService.clearPatcherFixPatcherFixPacks(
			patcherFix.getPatcherFixId());

		for (long patcherFixPackId : patcherFixPackIds) {
			_patcherFixPackLocalService.addPatcherFixPatcherFixPack(
				patcherFix.getPatcherFixId(), patcherFixPackId);
		}

		_patcherFixLocalService.updatePatcherFix(
			patcherFixId, dependencies, fixPackStatus, requirements);
	}

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

	@Reference
	private Portal _portal;

}