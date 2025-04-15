/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.facebook.connect.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.facebook.connect.exception.MustVerifyEmailAddressException;
import com.liferay.portal.security.sso.facebook.connect.exception.StrangersNotAllowedException;
import com.liferay.portal.security.sso.facebook.connect.exception.UnknownErrorException;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + PortletKeys.LOGIN,
		"mvc.command.name=/login_authentication_facebook_connect/facebook_connect_login_error"
	},
	service = MVCRenderCommand.class
)
public class FacebookConnectLoginErrorMVCRenderCommand
	implements MVCRenderCommand {

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

		String error = ParamUtil.getString(renderRequest, "error");

		if (ArrayUtil.contains(_ERRORS, error)) {
			SessionErrors.add(renderRequest, error);
		}
		else {
			SessionErrors.add(
				renderRequest, UnknownErrorException.class.getSimpleName());
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(renderResponse);

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/error.jsp");

			requestDispatcher.forward(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new PortletException(
				"Unable to include error.jsp", exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	private static final String[] _ERRORS = {
		UserEmailAddressException.MustNotUseCompanyMx.class.getSimpleName(),
		MustVerifyEmailAddressException.class.getSimpleName(),
		StrangersNotAllowedException.class.getSimpleName()
	};

	@Reference
	private FacebookConnect _facebookConnect;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.facebook.connect.web)"
	)
	private ServletContext _servletContext;

}