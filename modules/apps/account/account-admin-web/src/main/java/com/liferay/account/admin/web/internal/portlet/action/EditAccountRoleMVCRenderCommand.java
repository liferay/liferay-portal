/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.helper.AccountRoleRequestHelper;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_role"
	},
	service = MVCRenderCommand.class
)
public class EditAccountRoleMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		_accountRoleRequestHelper.setRequestAttributes(renderRequest);

		return "/account_entries_admin/edit_account_role.jsp";
	}

	@Reference
	private AccountRoleRequestHelper _accountRoleRequestHelper;

}