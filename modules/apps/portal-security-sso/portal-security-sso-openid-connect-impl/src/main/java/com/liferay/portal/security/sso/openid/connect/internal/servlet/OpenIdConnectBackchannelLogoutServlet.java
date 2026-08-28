/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.internal.AuthorizationServerMetadataResolver;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchSessionException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectUserLocalService;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.URL;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/open_id_connect/backchannel_logout",
		"osgi.http.whiteboard.servlet.name=com.liferay.portal.security.sso.openid.connect.internal.servlet.OpenIdConnectBackchannelLogoutServlet",
		"osgi.http.whiteboard.servlet.pattern=/open_id_connect/backchannel_logout/*"
	},
	service = Servlet.class
)
public class OpenIdConnectBackchannelLogoutServlet extends HttpServlet {

	@Override
	protected void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_openIdConnect.isEnabled(CompanyThreadLocal.getCompanyId())) {
			return;
		}

		String logoutToken = ParamUtil.get(
			httpServletRequest, "logout_token", StringPool.BLANK);

		if (Validator.isNull(logoutToken)) {
			return;
		}

		try {
			List<OpenIdConnectSession> openIdConnectSessions = null;

			SignedJWT signedJWT = SignedJWT.parse(logoutToken);

			JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

			String sessionId = jwtClaimsSet.getClaimAsString("sid");

			if (Validator.isNull(sessionId)) {
				OpenIdConnectUser openIdConnectUser =
					_openIdConnectUserLocalService.getOpenIdConnectUser(
						CompanyThreadLocal.getCompanyId(),
						jwtClaimsSet.getIssuer(),
						jwtClaimsSet.getClaimAsString("sub"));

				openIdConnectSessions = Collections.singletonList(
					_openIdConnectSessionLocalService.getOpenIdConnectSession(
						openIdConnectUser.getUserId(),
						jwtClaimsSet.getIssuer()));
			}
			else {
				openIdConnectSessions =
					_openIdConnectSessionLocalService.getOpenIdConnectSessions(
						CompanyThreadLocal.getCompanyId(),
						jwtClaimsSet.getIssuer(), sessionId);
			}

			OpenIdConnectSession audienceOpenIdConnectSession = null;

			List<String> audiences = jwtClaimsSet.getAudience();

			for (OpenIdConnectSession openIdConnectSession :
					openIdConnectSessions) {

				if (audiences.contains(openIdConnectSession.getClientId())) {
					audienceOpenIdConnectSession = openIdConnectSession;

					break;
				}
			}

			if (audienceOpenIdConnectSession == null) {
				throw new NoSuchSessionException(
					"No OpenId Connect session matches the audience of the " +
						"logout token");
			}

			JWSHeader jwsHeader = signedJWT.getHeader();

			LogoutTokenValidator logoutTokenValidator =
				new LogoutTokenValidator(
					new Issuer(jwtClaimsSet.getIssuer()),
					new ClientID(audienceOpenIdConnectSession.getClientId()),
					jwsHeader.getAlgorithm(),
					getJWKSURL(audienceOpenIdConnectSession));

			logoutTokenValidator.validate(signedJWT);

			for (OpenIdConnectSession openIdConnectSession :
					openIdConnectSessions) {

				_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
					openIdConnectSession);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"OpenId Connect session ",
							openIdConnectSession.getSessionId(),
							" has been terminated for user ",
							openIdConnectSession.getUserId()));
				}
			}

			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		}
		catch (Exception exception) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	protected URL getJWKSURL(OpenIdConnectSession openIdConnectSession)
		throws Exception {

		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.getOAuthClientEntry(
				openIdConnectSession.getCompanyId(),
				openIdConnectSession.getAuthServerWellKnownURI(),
				openIdConnectSession.getClientId());

		OIDCProviderMetadata oidcProviderMetadata =
			_authorizationServerMetadataResolver.resolveOIDCProviderMetadata(
				oAuthClientEntry.getAuthServerWellKnownURI(),
				oAuthClientEntry.getCompanyId(),
				oAuthClientEntry.getMetadataCacheInSeconds(),
				oAuthClientEntry.getOAuthClientEntryId());

		URI jwkSetURI = oidcProviderMetadata.getJWKSetURI();

		return jwkSetURI.toURL();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectBackchannelLogoutServlet.class);

	@Reference
	private AuthorizationServerMetadataResolver
		_authorizationServerMetadataResolver;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

	@Reference
	private OpenIdConnectUserLocalService _openIdConnectUserLocalService;

}