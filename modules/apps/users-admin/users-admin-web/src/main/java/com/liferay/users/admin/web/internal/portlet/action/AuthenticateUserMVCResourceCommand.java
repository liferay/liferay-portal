/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/authenticate_user"
	},
	service = MVCResourceCommand.class
)
public class AuthenticateUserMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		int status = HttpServletResponse.SC_UNAUTHORIZED;

		try {
			int authResult = _userLocalService.authenticateByUserId(
				_portal.getCompanyId(resourceRequest),
				ParamUtil.getLong(resourceRequest, "userId"),
				ParamUtil.getString(resourceRequest, "password"),
				new HashMap<>(), new HashMap<>(), new HashMap<>());

			if (authResult == Authenticator.SUCCESS) {
				status = HttpServletResponse.SC_OK;
			}
		}
		catch (Exception exception) {
			throw exception;
		}
		finally {
			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE, String.valueOf(status));
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}