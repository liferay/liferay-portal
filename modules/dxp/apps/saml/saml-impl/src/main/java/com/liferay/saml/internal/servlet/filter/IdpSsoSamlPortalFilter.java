/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.servlet.filter;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.helper.SamlHttpRequestHelper;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.servlet.profile.SingleLogoutProfile;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = {
		"after-filter=Virtual Host Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=IDP SSO SAML Portal Filter",
		"url-pattern=/c/portal/logout"
	},
	service = Filter.class
)
public class IdpSsoSamlPortalFilter extends BaseSamlPortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_samlProviderConfigurationHelper.isEnabled() ||
			!_samlProviderConfigurationHelper.isRoleIdp()) {

			return false;
		}

		try {
			User user = _portal.getUser(httpServletRequest);

			if (user != null) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		String requestPath = _samlHttpRequestHelper.getRequestPath(
			httpServletRequest);

		if (requestPath.equals("/c/portal/logout")) {
			return true;
		}

		return false;
	}

	@Override
	protected void doProcessFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String requestPath = _samlHttpRequestHelper.getRequestPath(
			httpServletRequest);

		if (requestPath.equals("/c/portal/logout")) {
			String samlSsoSessionId = CookiesManagerUtil.getCookieValue(
				SamlWebKeys.SAML_SSO_SESSION_ID, httpServletRequest);

			if (Validator.isNotNull(samlSsoSessionId)) {
				_singleLogoutProfile.processIdpLogout(
					httpServletRequest, httpServletResponse);
			}
			else {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
			}
		}
		else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IdpSsoSamlPortalFilter.class);

	@Reference
	private Portal _portal;

	@Reference
	private SamlHttpRequestHelper _samlHttpRequestHelper;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SingleLogoutProfile _singleLogoutProfile;

}