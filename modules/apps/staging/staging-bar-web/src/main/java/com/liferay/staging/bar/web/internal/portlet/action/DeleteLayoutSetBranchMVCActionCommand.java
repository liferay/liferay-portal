/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutSetBranchService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.bar.web.internal.portlet.constants.StagingBarPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingBarPortletKeys.STAGING_BAR,
		"mvc.command.name=/staging_bar/delete_layout_set_branch"
	},
	service = MVCActionCommand.class
)
public class DeleteLayoutSetBranchMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetBranchId = ParamUtil.getLong(
			actionRequest, "layoutSetBranchId");

		long currentLayoutBranchId = ParamUtil.getLong(
			actionRequest, "currentLayoutBranchId");

		if (layoutSetBranchId == currentLayoutBranchId) {
			SessionMessages.add(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_PORTLET_NOT_AJAXABLE);
		}

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			_layoutSetBranchService.deleteLayoutSetBranch(
				layout.getPlid(), layoutSetBranchId);

			SessionMessages.add(actionRequest, "sitePageVariationDeleted");

			ActionUtil.addLayoutBranchSessionMessages(
				actionRequest, actionResponse);
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	@Reference
	private LayoutSetBranchService _layoutSetBranchService;

	@Reference
	private Portal _portal;

}