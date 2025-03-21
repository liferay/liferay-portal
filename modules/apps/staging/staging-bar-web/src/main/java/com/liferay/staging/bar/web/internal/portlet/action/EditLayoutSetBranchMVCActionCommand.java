/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.LayoutSetBranchNameException;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
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
		"mvc.command.name=/staging_bar/edit_layout_set_branch"
	},
	service = MVCActionCommand.class
)
public class EditLayoutSetBranchMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetBranchId = ParamUtil.getLong(
			actionRequest, "layoutSetBranchId");

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		long copyLayoutSetBranchId = ParamUtil.getLong(
			actionRequest, "copyLayoutSetBranchId",
			LayoutSetBranchConstants.ALL_BRANCHES);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		try {
			if (layoutSetBranchId <= 0) {
				_layoutSetBranchService.addLayoutSetBranch(
					groupId, privateLayout, name, description, false,
					copyLayoutSetBranchId, serviceContext);

				SessionMessages.add(actionRequest, "sitePageVariationAdded");
			}
			else {
				_layoutSetBranchLocalService.updateLayoutSetBranch(
					layoutSetBranchId, name, description, serviceContext);

				SessionMessages.add(actionRequest, "sitePageVariationUpdated");
			}

			ActionUtil.addLayoutBranchSessionMessages(
				actionRequest, actionResponse);
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);

			if (exception instanceof LayoutSetBranchNameException) {
				actionResponse.setRenderParameter(
					"mvcPath", "/edit_layout_set_branch.jsp");
			}
			else {
				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private LayoutSetBranchService _layoutSetBranchService;

}