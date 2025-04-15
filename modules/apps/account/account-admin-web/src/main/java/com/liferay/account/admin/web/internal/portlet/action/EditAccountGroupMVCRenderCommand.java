/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.constants.AccountWebKeys;
import com.liferay.account.admin.web.internal.display.AccountGroupDisplay;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Albert Lee
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_GROUPS_ADMIN,
		"mvc.command.name=/account_admin/edit_account_group"
	},
	service = MVCRenderCommand.class
)
public class EditAccountGroupMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long accountGroupId = ParamUtil.getLong(
			renderRequest, "accountGroupId");

		renderRequest.setAttribute(
			AccountWebKeys.ACCOUNT_GROUP_DISPLAY,
			AccountGroupDisplay.of(accountGroupId));

		return "/account_groups_admin/edit_account_group.jsp";
	}

}