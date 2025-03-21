/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.security.auth.verifier;

import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProviderAccessor;
import com.liferay.oauth2.provider.scope.liferay.ScopeContext;
import com.liferay.oauth2.provider.scope.liferay.constants.OAuth2ProviderScopeLiferayConstants;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = "auth.verifier.OAuth2RESTAuthVerifier.urls.includes=#N/A#",
	service = AuthVerifier.class
)
public class OAuth2RESTAuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return OAuth2ProviderScopeLiferayConstants.AUTH_VERIFIER_OAUTH2_TYPE;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			String accessTokenContent = _getAccessTokenContent(
				accessControlContext);

			if (accessTokenContent == null) {
				return authVerifierResult;
			}

			BearerTokenProvider.AccessToken accessToken = _getAccessToken(
				accessTokenContent);

			if (accessToken != null) {
				OAuth2Application oAuth2Application =
					accessToken.getOAuth2Application();

				BearerTokenProvider bearerTokenProvider =
					_bearerTokenProviderAccessor.getBearerTokenProvider(
						oAuth2Application.getCompanyId(),
						oAuth2Application.getClientId());

				if ((bearerTokenProvider == null) ||
					!bearerTokenProvider.isValid(accessToken)) {

					accessToken = null;
				}
			}

			if (accessToken == null) {
				HttpServletResponse httpServletResponse =
					accessControlContext.getResponse();

				httpServletResponse.setStatus(
					HttpServletResponse.SC_UNAUTHORIZED);

				authVerifierResult.setState(
					AuthVerifierResult.State.INVALID_CREDENTIALS);

				return authVerifierResult;
			}

			_scopeContext.setAccessToken(accessToken.getTokenKey());

			Map<String, Object> settings = authVerifierResult.getSettings();

			settings.put(
				BearerTokenProvider.AccessToken.class.getName(), accessToken);

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(accessToken.getUserId());

			return authVerifierResult;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to verify OAuth2 access token", exception);
			}

			return authVerifierResult;
		}
	}

	private BearerTokenProvider.AccessToken _getAccessToken(
			String accessTokenContent)
		throws PortalException {

		if (Validator.isBlank(accessTokenContent)) {
			return null;
		}

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(
					accessTokenContent);

		if (oAuth2Authorization == null) {
			return null;
		}

		accessTokenContent = oAuth2Authorization.getAccessTokenContent();

		if (OAuth2ProviderConstants.EXPIRED_TOKEN.equals(accessTokenContent)) {
			return null;
		}

		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.getOAuth2Application(
				oAuth2Authorization.getOAuth2ApplicationId());

		Date createDate = oAuth2Authorization.getAccessTokenCreateDate();
		Date expirationDate =
			oAuth2Authorization.getAccessTokenExpirationDate();

		long expiresIn =
			(expirationDate.getTime() - createDate.getTime()) / 1000;

		long issuedAt = createDate.getTime() / 1000;

		List<String> scopeAliasesList = Collections.emptyList();

		long oAuth2ApplicationScopeAliasesId =
			oAuth2Authorization.getOAuth2ApplicationScopeAliasesId();

		if (oAuth2ApplicationScopeAliasesId > 0) {
			scopeAliasesList =
				_oAuth2ApplicationScopeAliasesLocalService.getScopeAliasesList(
					oAuth2ApplicationScopeAliasesId);
		}

		return new BearerTokenProvider.AccessToken(
			oAuth2Application, new ArrayList<>(), StringPool.BLANK, expiresIn,
			new HashMap<>(), StringPool.BLANK, StringPool.BLANK, issuedAt,
			StringPool.BLANK, StringPool.BLANK, new HashMap<>(),
			StringPool.BLANK, StringPool.BLANK, scopeAliasesList,
			accessTokenContent, _TOKEN_KEY, oAuth2Authorization.getUserId(),
			oAuth2Authorization.getUserName());
	}

	private String _getAccessTokenContent(
		AccessControlContext accessControlContext) {

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		String authorization = httpServletRequest.getHeader(
			HttpHeaders.AUTHORIZATION);

		if (Validator.isBlank(authorization)) {
			return null;
		}

		String[] authorizationParts = authorization.split("\\s");

		String scheme = authorizationParts[0];

		if (!StringUtil.equalsIgnoreCase(scheme, _TOKEN_KEY)) {
			return null;
		}

		if (authorizationParts.length < 2) {
			return StringPool.BLANK;
		}

		return authorizationParts[1];
	}

	private static final String _TOKEN_KEY = "Bearer";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2RESTAuthVerifier.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile BearerTokenProviderAccessor _bearerTokenProviderAccessor;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private ScopeContext _scopeContext;

}