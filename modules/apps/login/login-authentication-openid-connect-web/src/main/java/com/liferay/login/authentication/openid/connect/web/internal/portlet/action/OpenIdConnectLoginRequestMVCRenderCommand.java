/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.openid.connect.web.internal.portlet.action;

import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;

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
 * Enables the Sign In portlet to render a prompt for users to enter their
 * OpenID Connect IDs.
 *
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + PortletKeys.LOGIN,
		"mvc.command.name=" + OpenIdConnectWebKeys.OPEN_ID_CONNECT_REQUEST_ACTION_NAME
	},
	service = MVCRenderCommand.class
)
public class OpenIdConnectLoginRequestMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_openIdConnect.isEnabled(themeDisplay.getCompanyId()) ||
			themeDisplay.isSignedIn()) {

			return "/login.jsp";
		}

		httpServletRequest.setAttribute(
			OpenIdConnectWebKeys.OAUTH_CLIENT_ENTRIES,
			_oAuthClientEntryLocalService.
				getAuthServerWellKnownURISuffixOAuthClientEntries(
					themeDisplay.getCompanyId(), "openid-configuration"));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(_JSP_PATH);

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(renderResponse);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to include JSP ", _JSP_PATH, ": ",
					exception.getMessage()),
				exception);

			throw new PortletException(
				"Unable to include JSP " + _JSP_PATH, exception);
		}

		return "/navigation.jsp";
	}

	private static final String _JSP_PATH =
		"/com.liferay.login.web/openid_connect.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectLoginRequestMVCRenderCommand.class);

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.openid.connect.web)"
	)
	private ServletContext _servletContext;

}