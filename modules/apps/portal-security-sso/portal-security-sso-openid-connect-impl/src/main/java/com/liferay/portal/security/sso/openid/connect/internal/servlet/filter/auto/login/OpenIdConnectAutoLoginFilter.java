/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet.filter.auto.login;

import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectAuthenticationHandler;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectConstants;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.internal.exception.StrangersNotAllowedException;
import com.liferay.portal.security.sso.openid.connect.internal.session.manager.OfflineOpenIdConnectSessionManager;
import com.liferay.portal.servlet.filters.autologin.AutoLoginFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Edward C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.openid.connect.configuration.OpenIdConnectConfiguration",
	property = {
		"after-filter=Virtual Host Filter", "servlet-context-name=",
		"servlet-filter-name=SSO OpenId Connect Auto Login Filter",
		"url-pattern=" + OpenIdConnectConstants.REDIRECT_URL_PATTERN
	},
	service = Filter.class
)
public class OpenIdConnectAutoLoginFilter extends AutoLoginFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _openIdConnect.isEnabled(
			_portal.getCompanyId(httpServletRequest));
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession == null) {
			return;
		}

		if (_offlineOpenIdConnectSessionManager.isOpenIdConnectSession(
				httpSession)) {

			if (_log.isDebugEnabled()) {
				_log.debug("User is already authenticated");
			}

			return;
		}

		String actionURL = (String)httpSession.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_ACTION_URL);

		try {
			_openIdConnectAuthenticationHandler.processAuthenticationResponse(
				httpServletRequest, httpServletResponse,
				userId -> _autoLoginUser(
					httpServletRequest, httpServletResponse, userId));
		}
		catch (StrangersNotAllowedException |
			   UserEmailAddressException.MustNotUseCompanyMx exception) {

			Class<?> clazz = exception.getClass();

			actionURL = HttpComponentsUtil.addParameter(
				actionURL, "error", clazz.getSimpleName());

			httpServletResponse.sendRedirect(actionURL);
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}

		if (httpServletResponse.isCommitted()) {
			return;
		}
		else if (actionURL != null) {
			httpServletResponse.sendRedirect(actionURL);

			return;
		}

		processFilter(
			OpenIdConnectAutoLoginFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	private void _autoLoginUser(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Long userId)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_AUTHENTICATING_USER_ID,
			userId);

		super.processFilter(
			httpServletRequest, httpServletResponse,
			(servletRequest, servletResponse) -> {
				long authenticatedUserId = _getRemoteUserId(servletRequest);

				if (authenticatedUserId == userId) {
					return;
				}

				throw new ServletException(
					"Expected user " + userId + " to be authenticated");
			});
	}

	private long _getRemoteUserId(ServletRequest servletRequest) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		return GetterUtil.getLong(httpServletRequest.getRemoteUser());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectAutoLoginFilter.class);

	@Reference
	private OfflineOpenIdConnectSessionManager
		_offlineOpenIdConnectSessionManager;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private OpenIdConnectAuthenticationHandler
		_openIdConnectAuthenticationHandler;

	@Reference
	private Portal _portal;

}