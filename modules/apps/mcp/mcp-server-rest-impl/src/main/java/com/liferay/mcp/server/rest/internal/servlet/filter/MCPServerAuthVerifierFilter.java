/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet.filter;

import com.liferay.mcp.server.rest.internal.configuration.MCPServerConfiguration;
import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.oauth2.provider.constants.OAuth2AuthorizationConstants;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"filter.init.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/*",
		"osgi.http.whiteboard.filter.name=com.liferay.mcp.server.rest.internal.servlet.filter.MCPServerAuthVerifierFilter",
		"osgi.http.whiteboard.filter.pattern=/mcp",
		"osgi.http.whiteboard.filter.pattern=/mcp/*"
	},
	service = Filter.class
)
public class MCPServerAuthVerifierFilter extends AuthVerifierFilter {

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long companyId = CompanyThreadLocal.getCompanyId();

		if (!_isEnabled(companyId)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String authorization = httpServletRequest.getHeader(
			HttpHeaders.AUTHORIZATION);

		if (Validator.isBlank(authorization)) {
			httpServletResponse.setHeader(
				HttpHeaders.WWW_AUTHENTICATE,
				_getChallenge(httpServletRequest));
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			return;
		}

		authorization = authorization.trim();

		if (StringUtil.startsWith(authorization, "Basic ")) {
			super.processFilter(
				httpServletRequest, httpServletResponse, filterChain);

			return;
		}

		if (!StringUtil.startsWith(authorization, "Bearer ")) {
			_sendInvalidTokenChallenge(
				"Authorization header is not a bearer token",
				httpServletRequest, httpServletResponse);

			return;
		}

		String accessTokenContent = authorization.substring("Bearer ".length());

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					accessTokenContent);

		if ((oAuth2Authorization == null) ||
			(oAuth2Authorization.getCompanyId() != companyId) ||
			OAuth2AuthorizationConstants.ACCESS_TOKEN_CONTENT_EXPIRED_TOKEN.
				equals(oAuth2Authorization.getAccessTokenContent())) {

			_sendInvalidTokenChallenge(
				"Access token is unknown or revoked", httpServletRequest,
				httpServletResponse);

			return;
		}

		Date accessTokenExpirationDate =
			oAuth2Authorization.getAccessTokenExpirationDate();

		if ((accessTokenExpirationDate != null) &&
			(accessTokenExpirationDate.getTime() <
				System.currentTimeMillis())) {

			_sendInvalidTokenChallenge(
				"Access token has expired", httpServletRequest,
				httpServletResponse);

			return;
		}

		List<String> audiences = oAuth2Authorization.getAudiencesList();

		String mcpResourceURI = StringBundler.concat(
			_portal.getPortalURL(httpServletRequest), _portal.getPathContext(),
			Portal.PATH_MODULE, MCPServerConstants.PATH_MCP);

		if ((audiences == null) || !audiences.contains(mcpResourceURI)) {
			_sendInvalidTokenChallenge(
				"Access token is not bound to this MCP server",
				httpServletRequest, httpServletResponse);

			return;
		}

		User user = _userLocalService.fetchUser(
			oAuth2Authorization.getUserId());

		if ((user == null) || user.isGuestUser()) {
			_sendInvalidTokenChallenge(
				"Access token is unknown or revoked", httpServletRequest,
				httpServletResponse);

			return;
		}

		httpServletRequest.setAttribute(WebKeys.USER_ID, user.getUserId());

		Class<?> clazz = getClass();

		processFilter(
			clazz.getName(), httpServletRequest, httpServletResponse,
			filterChain);
	}

	private String _getChallenge(HttpServletRequest httpServletRequest) {
		return StringBundler.concat(
			"Bearer realm=\"mcp\", resource_metadata=\"",
			_portal.getPortalURL(httpServletRequest), _portal.getPathContext(),
			Portal.PATH_MODULE,
			MCPServerConstants.PATH_WELL_KNOWN_PROTECTED_RESOURCE,
			MCPServerConstants.PATH_MCP, "\"");
	}

	private boolean _isEnabled(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311")) {
			return false;
		}

		try {
			MCPServerConfiguration mcpServerConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MCPServerConfiguration.class, companyId);

			return mcpServerConfiguration.enabled();
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private void _sendInvalidTokenChallenge(
		String description, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader(
			HttpHeaders.WWW_AUTHENTICATE,
			StringBundler.concat(
				_getChallenge(httpServletRequest),
				", error=\"invalid_token\", error_description=\"", description,
				"\""));
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}