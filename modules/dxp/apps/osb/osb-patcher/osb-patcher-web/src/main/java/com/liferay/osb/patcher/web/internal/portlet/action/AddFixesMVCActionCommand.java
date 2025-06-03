/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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
		"mvc.command.name=/patcher/add_fixes"
	},
	service = MVCActionCommand.class
)
public class AddFixesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherProductVersionId = ParamUtil.getLong(
			actionRequest, "patcherProductVersionId");
		long patcherProjectVersionId = ParamUtil.getLong(
			actionRequest, "patcherProjectVersionId");
		String name = PatcherUtil.preparePatcherName(
			ParamUtil.getString(actionRequest, "name"));
		String committish = ParamUtil.getString(actionRequest, "committish");
		String gitRemoteURL = ParamUtil.getString(
			actionRequest, "gitRemoteURL");
		boolean workaround = ParamUtil.getBoolean(actionRequest, "workaround");

		int type = PatcherFixConstants.TYPE_PATCH;

		if (workaround) {
			type = PatcherFixConstants.TYPE_WORKAROUND;
		}

		_patcherFixLocalService.addPatcherFix(
			themeDisplay.getUserId(), patcherProductVersionId,
			patcherProjectVersionId, name, committish, gitRemoteURL, type,
			WorkflowConstants.STATUS_FIX_ADDING);
	}

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

}