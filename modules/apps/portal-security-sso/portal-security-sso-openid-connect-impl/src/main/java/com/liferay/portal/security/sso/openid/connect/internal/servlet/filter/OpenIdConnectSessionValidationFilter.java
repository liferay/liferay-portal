/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.internal.session.manager.OfflineOpenIdConnectSessionManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Edward C. Han
 */
@Component(
	property = {
		"servlet-context-name=",
		"servlet-filter-name=OpenId Connect Session Validation Filter",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class OpenIdConnectSessionValidationFilter extends BaseFilter {

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

		if (_offlineOpenIdConnectSessionManager.isOpenIdConnectSession(
				httpSession) &&
			_offlineOpenIdConnectSessionManager.isOpenIdConnectSessionExpired(
				httpSession)) {

			httpSession.invalidate();

			httpServletResponse.sendRedirect(
				_portal.getHomeURL(httpServletRequest));

			return;
		}

		processFilter(
			OpenIdConnectSessionValidationFilter.class.getName(),
			httpServletRequest, httpServletResponse, filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectSessionValidationFilter.class);

	@Reference
	private OfflineOpenIdConnectSessionManager
		_offlineOpenIdConnectSessionManager;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private Portal _portal;

}