/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.RequiredOrganizationException;
import com.liferay.portal.kernel.exception.RequiredUserException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/delete_organizations_and_users"
	},
	service = MVCActionCommand.class
)
public class DeleteOrganizationsAndUsersMVCActionCommand
	extends BaseMVCActionCommand {

	protected void deleteOrganizations(ActionRequest actionRequest)
		throws Exception {

		long[] deleteOrganizationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "deleteOrganizationIds"), 0L);

		for (long deleteOrganizationId : deleteOrganizationIds) {
			_organizationService.deleteOrganization(deleteOrganizationId);
		}
	}

	protected void deleteUsers(ActionRequest actionRequest) throws Exception {
		long[] deleteUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "deleteUserIds"), 0L);

		for (long deleteUserId : deleteUserIds) {
			_userService.deleteUser(deleteUserId);
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			deleteOrganizations(actionRequest);
			deleteUsers(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchOrganizationException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof RequiredOrganizationException ||
					 exception instanceof RequiredUserException) {

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = _portal.escapeRedirect(
					ParamUtil.getString(actionRequest, "onErrorRedirect"));

				if (Validator.isNotNull(redirect)) {
					sendRedirect(actionRequest, actionResponse, redirect);
				}
			}
			else {
				throw exception;
			}
		}
	}

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private Portal _portal;

	@Reference
	private UserService _userService;

}