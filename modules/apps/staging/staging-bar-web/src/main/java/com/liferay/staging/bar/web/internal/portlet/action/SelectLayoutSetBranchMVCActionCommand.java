/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.portlet.action;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.staging.bar.web.internal.portlet.constants.StagingBarPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StagingBarPortletKeys.STAGING_BAR,
		"mvc.command.name=/staging_bar/select_layout_set_branch"
	},
	service = MVCActionCommand.class
)
public class SelectLayoutSetBranchMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			long groupId = ParamUtil.getLong(actionRequest, "groupId");
			boolean privateLayout = ParamUtil.getBoolean(
				actionRequest, "privateLayout");

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				groupId, privateLayout);

			long layoutSetBranchId = ParamUtil.getLong(
				actionRequest, "layoutSetBranchId");

			LayoutSetBranch layoutSetBranch =
				_layoutSetBranchLocalService.getLayoutSetBranch(
					layoutSetBranchId);

			_staging.setRecentLayoutSetBranchId(
				httpServletRequest, layoutSet.getLayoutSetId(),
				layoutSetBranch.getLayoutSetBranchId());

			ActionUtil.addLayoutBranchSessionMessages(
				actionRequest, actionResponse);
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
		}
	}

	@Reference
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private Staging _staging;

}