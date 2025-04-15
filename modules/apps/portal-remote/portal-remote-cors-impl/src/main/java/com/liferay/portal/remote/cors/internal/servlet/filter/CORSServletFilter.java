/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.internal.servlet.filter;

import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.cors.internal.CORSSupport;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author Carlos Sierra Andrés
 */
public class CORSServletFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return CORSSupport.isCORSRequest(httpServletRequest::getHeader);
	}

	public void processCORSRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (StringUtil.equals(
				HttpMethods.OPTIONS, httpServletRequest.getMethod())) {

			if (corsSupport.isValidCORSPreflightRequest(
					httpServletRequest::getHeader)) {

				corsSupport.writeResponseHeaders(
					httpServletRequest::getHeader,
					httpServletResponse::setHeader);
			}

			return;
		}

		if (corsSupport.isValidCORSRequest(
				httpServletRequest.getMethod(),
				httpServletRequest::getHeader) &&
			(PropsValues.CORS_DISABLE_AUTHORIZATION_CONTEXT_CHECK ||
			 OAuth2ProviderScopeLiferayAccessControlContext.
				 isOAuth2AuthVerified() ||
			 _isGuest())) {

			corsSupport.writeResponseHeaders(
				httpServletRequest::getHeader, httpServletResponse::setHeader);
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	public void setCORSHeaders(Map<String, String> corsHeaders) {
		corsSupport.setCORSHeaders(corsHeaders);
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws ServletException {

		try {
			processCORSRequest(
				httpServletRequest, httpServletResponse, filterChain);
		}
		catch (Exception exception) {
			throw new ServletException(exception);
		}
	}

	protected final CORSSupport corsSupport = new CORSSupport();

	private boolean _isGuest() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return true;
		}

		User user = permissionChecker.getUser();

		return user.isGuestUser();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CORSServletFilter.class);

}