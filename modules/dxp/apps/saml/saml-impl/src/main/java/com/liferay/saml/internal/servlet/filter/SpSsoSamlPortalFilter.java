/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.servlet.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.helper.SamlHttpRequestHelper;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.servlet.profile.SingleLogoutProfile;
import com.liferay.saml.runtime.servlet.profile.WebSsoProfile;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	property = {
		"after-filter=Virtual Host Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST",
		"init-param.url-regex-ignore-pattern=^/(html|o)/.+\\.(css|gif|html|ico|jpg|js|png)(\\?.*)?$",
		"servlet-context-name=",
		"servlet-filter-name=SP SSO SAML Portal Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class SpSsoSamlPortalFilter extends BaseSamlPortalFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		_servletContext = filterConfig.getServletContext();
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_samlProviderConfigurationHelper.isEnabled() ||
			_samlProviderConfigurationHelper.isRoleIdp()) {

			return false;
		}

		return true;
	}

	@Override
	protected void doProcessFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String requestPath = _samlHttpRequestHelper.getRequestPath(
			httpServletRequest);

		if (requestPath.equals("/c/portal/login")) {
			if (_portal.getUser(httpServletRequest) != null) {
				filterChain.doFilter(httpServletRequest, httpServletResponse);

				return;
			}

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/c/portal/saml/login");

			httpServletResponse.setContentType(ContentTypes.TEXT_HTML_UTF8);

			requestDispatcher.include(httpServletRequest, httpServletResponse);

			Object samlSpIdpConnection = httpServletRequest.getAttribute(
				SamlWebKeys.SAML_SP_IDP_CONNECTION);

			if (samlSpIdpConnection != null) {
				try {
					_login(httpServletRequest, httpServletResponse);
				}
				catch (PortalException portalException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Failed to send Authn request: " +
								portalException.getMessage());
					}
				}
			}
			else {
				SamlProviderConfiguration samlProviderConfiguration =
					_samlProviderConfigurationHelper.
						getSamlProviderConfiguration();

				Object samlSsologinContext = httpServletRequest.getAttribute(
					SamlWebKeys.SAML_SSO_LOGIN_CONTEXT);

				if ((samlSsologinContext == null) &&
					samlProviderConfiguration.allowShowingTheLoginPortlet()) {

					filterChain.doFilter(
						httpServletRequest, httpServletResponse);
				}
			}
		}
		else if (requestPath.equals("/c/portal/logout")) {
			if (_singleLogoutProfile.isSingleLogoutSupported(
					httpServletRequest)) {

				_singleLogoutProfile.processSpLogout(
					httpServletRequest, httpServletResponse);
			}
			else {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
			}
		}
		else {
			User user = null;

			try {
				user = _portal.getUser(httpServletRequest);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}

			if (user != null) {
				_webSsoProfile.updateSamlSpSession(
					httpServletRequest, httpServletResponse);
			}
			else {
				HttpSession httpSession = httpServletRequest.getSession(false);

				if ((httpSession != null) &&
					(httpSession.getAttribute(SamlWebKeys.SAML_SSO_ERROR) !=
						null)) {

					httpServletRequest.setAttribute(
						WebKeys.BLOCK_LOGIN_PROMPT, Boolean.TRUE);
				}
			}

			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private void _login(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		String relayState = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(relayState)) {
			relayState = _portal.escapeRedirect(relayState);
		}

		HttpSession httpSession = httpServletRequest.getSession();

		LastPath lastPath = (LastPath)httpSession.getAttribute(
			WebKeys.LAST_PATH);

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.AUTH_FORWARD_BY_LAST_PATH)) &&
			(lastPath != null) && Validator.isNull(relayState)) {

			relayState = StringBundler.concat(
				_portal.getPortalURL(httpServletRequest),
				lastPath.getContextPath(), lastPath.getPath(),
				lastPath.getParameters());
		}
		else if (Validator.isNull(relayState)) {
			relayState = _portal.getHomeURL(httpServletRequest);
		}

		_webSsoProfile.sendAuthnRequest(
			httpServletRequest, httpServletResponse, relayState);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpSsoSamlPortalFilter.class);

	@Reference
	private Portal _portal;

	@Reference
	private SamlHttpRequestHelper _samlHttpRequestHelper;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	private ServletContext _servletContext;

	@Reference
	private SingleLogoutProfile _singleLogoutProfile;

	@Reference
	private WebSsoProfile _webSsoProfile;

}