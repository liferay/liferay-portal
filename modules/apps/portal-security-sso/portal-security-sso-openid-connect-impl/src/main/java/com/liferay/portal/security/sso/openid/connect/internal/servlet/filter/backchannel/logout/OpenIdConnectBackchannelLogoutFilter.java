/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet.filter.backchannel.logout;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceException;
import com.liferay.portal.security.sso.openid.connect.internal.configuration.OpenIdConnectProviderConfiguration;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URL;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(
	property = {
		"servlet-context-name=",
		"servlet-filter-name=OpenId Connect Backchannel Logout Filter",
		"url-pattern=/open_id_connect/backchannel_logout"
	},
	service = Filter.class
)
public class OpenIdConnectBackchannelLogoutFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _openIdConnect.isEnabled(CompanyThreadLocal.getCompanyId());
	}

	protected String getJWKSURI(Issuer issuer) throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", ConfigurationAdmin.SERVICE_FACTORYPID, "=",
				OpenIdConnectProviderConfiguration.class.getName(),
				".scoped)"));

		if (ArrayUtil.isEmpty(configurations)) {
			return StringPool.BLANK;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			String discoveryEndPoint = (String)properties.get(
				"discoveryEndPoint");
			String issuerURL = (String)properties.get("issuerURL");
			String jwksURI = (String)properties.get("jwksURI");

			if (discoveryEndPoint != null) {
				OIDCProviderMetadata metadata = _resolveOIDCProviderMetadata(
					discoveryEndPoint);

				issuerURL = metadata.getIssuer(
				).getValue();
				jwksURI = metadata.getJWKSetURI(
				).toString();
			}

			if (issuerURL.equals(issuer.getValue())) {
				return jwksURI;
			}
		}

		return StringPool.BLANK;
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

		String logoutToken = ParamUtil.get(
			httpServletRequest, "logout_token", StringPool.BLANK);

		if (Validator.isNull(logoutToken)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Backchannel OIDC logout with token: " + logoutToken);
		}

		try {
			JWT jwt = JWTParser.parse(logoutToken);

			JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();

			OpenIdConnectSession oidcSession =
				_openIdConnectSessionLocalService.fetchOpenIdConnectSession(
					jwtClaimsSet.getClaimAsString("iss") +
						"/.well-known/openid-configuration",
					jwtClaimsSet.getClaimAsString("sid"));

			if (oidcSession == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"There is no active Open ID session for the " +
							"specified sid " +
								jwtClaimsSet.getClaimAsString("sid"));
				}

				return;
			}

			jwt = JWTParser.parse(oidcSession.getIdToken());

			jwtClaimsSet = jwt.getJWTClaimsSet();

			Issuer issuer = new Issuer(jwtClaimsSet.getIssuer());

			String jwksURI = getJWKSURI(issuer);

			if (jwksURI == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("JWKS URI is null");
				}

				return;
			}

			SignedJWT signedJWT = SignedJWT.parse(logoutToken);

			JWSHeader jwsHeader = signedJWT.getHeader();

			LogoutTokenValidator logoutTokenValidator =
				new LogoutTokenValidator(
					issuer, new ClientID(oidcSession.getClientId()),
					jwsHeader.getAlgorithm(), new URL(jwksURI));

			logoutTokenValidator.validate(jwt);

			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				oidcSession);

			httpServletResponse.setStatus(HttpServletResponse.SC_OK);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Terminating oidc session ", oidcSession.getSid(),
						" for userId ", oidcSession.getUserId()));
			}
		}
		catch (Exception exception) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new SystemException(
				"OpenId Connect backchannel logout failed", exception);
		}
	}

	private OIDCProviderMetadata _resolveOIDCProviderMetadata(
			String discoveryEndPointURI)
		throws Exception {

		HTTPRequest httpRequest = new HTTPRequest(
			HTTPRequest.Method.GET, new URL(discoveryEndPointURI));

		HTTPResponse httpResponse = httpRequest.send();

		if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
			throw new OpenIdConnectServiceException.ProviderException(
				httpResponse.getStatusMessage());
		}

		return OIDCProviderMetadata.parse(httpResponse.getBody());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectBackchannelLogoutFilter.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

}