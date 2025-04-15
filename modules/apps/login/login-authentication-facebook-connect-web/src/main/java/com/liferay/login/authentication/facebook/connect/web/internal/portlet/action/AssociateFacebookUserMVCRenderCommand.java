/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.facebook.connect.web.internal.portlet.action;

import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + PortletKeys.LOGIN,
		"mvc.command.name=/login_authentication_facebook_connect/associate_facebook_user"
	},
	service = MVCRenderCommand.class
)
public class AssociateFacebookUserMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!_facebookConnect.isEnabled(themeDisplay.getCompanyId())) {
			throw new PortletException(
				new PrincipalException.MustBeEnabled(
					themeDisplay.getCompanyId(),
					FacebookConnect.class.getName()));
		}

		long facebookIncompleteUserId = ParamUtil.getLong(
			renderRequest, "userId");

		if (facebookIncompleteUserId != 0) {
			return _renderUpdateAccount(
				renderRequest,
				_userLocalService.fetchUser(facebookIncompleteUserId));
		}

		// This return statement may be used if the user presses the browser's
		// back button

		return "/login.jsp";
	}

	private String _renderUpdateAccount(
			PortletRequest portletRequest, User user)
		throws PortletException {

		portletRequest.setAttribute("selUser", user);

		return "/update_account.jsp";
	}

	@Reference
	private FacebookConnect _facebookConnect;

	@Reference
	private UserLocalService _userLocalService;

}