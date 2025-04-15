/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrantHandler;
import org.apache.cxf.rs.security.oauth2.grants.code.DigestCodeVerifier;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	service = AccessTokenGrantHandler.class
)
public class LiferayAuthorizationAccessTokenCodeGrantHandler
	extends BaseAccessTokenGrantHandler {

	@Override
	public List<String> getSupportedGrantTypes() {
		return _authorizationCodeGrantHandler.getSupportedGrantTypes();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_authorizationCodeGrantHandler = new AuthorizationCodeGrantHandler();

		_authorizationCodeGrantHandler.setCodeVerifierTransformer(
			new DigestCodeVerifier());
		_authorizationCodeGrantHandler.setDataProvider(
			_liferayOAuthDataProvider);

		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);

		_authorizationCodeGrantHandler.setExpectCodeVerifierForPublicClients(
			_oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant());
	}

	@Override
	protected ServerAccessToken doCreateAccessToken(
		Client client, MultivaluedMap<String, String> params) {

		return _authorizationCodeGrantHandler.createAccessToken(client, params);
	}

	@Override
	protected boolean hasPermission(
		Client client, MultivaluedMap<String, String> params) {

		String code = params.getFirst("code");

		if (code == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No code parameter was provided");
			}

			return false;
		}

		ServerAuthorizationCodeGrant serverAuthorizationCodeGrant =
			_liferayOAuthDataProvider.getServerAuthorizationCodeGrant(code);

		if (serverAuthorizationCodeGrant == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No code grant found for code " + code);
			}

			return false;
		}

		if (!clientsMatch(client, serverAuthorizationCodeGrant.getClient())) {

			// TODO: Inform the audit service that the user is trying to get a
			// code belonging to a client other than the authenticated client

			_liferayOAuthDataProvider.removeCodeGrant(code);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Authenticated client does not match the code's client");
			}

			return false;
		}

		if (client.isConfidential()) {
			if (!_oAuth2ProviderConfiguration.allowAuthorizationCodeGrant()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						OAuthConstants.AUTHORIZATION_CODE_GRANT +
							" grant is not allowed");
				}

				return false;
			}

			List<String> allowedGrantTypes = client.getAllowedGrantTypes();

			if (!allowedGrantTypes.contains(
					OAuthConstants.AUTHORIZATION_CODE_GRANT)) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Client is not allowed to use " +
							OAuthConstants.AUTHORIZATION_CODE_GRANT + " grant");
				}

				return false;
			}
		}
		else {
			if (!_oAuth2ProviderConfiguration.
					allowAuthorizationCodePKCEGrant()) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						OAuthConstants.AUTHORIZATION_CODE_GRANT +
							" with PKCE grant is not allowed");
				}

				return false;
			}

			List<String> allowedGrantTypes = client.getAllowedGrantTypes();

			if (!allowedGrantTypes.contains(
					OAuth2ProviderRESTEndpointConstants.
						AUTHORIZATION_CODE_PKCE_GRANT)) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Client is not allowed to use " +
							OAuthConstants.AUTHORIZATION_CODE_GRANT +
								" with PKCE grant");
				}

				return false;
			}
		}

		OAuth2Application oAuth2Application =
			_liferayOAuthDataProvider.resolveOAuth2Application(
				serverAuthorizationCodeGrant.getClient());

		UserSubject userSubject = serverAuthorizationCodeGrant.getSubject();

		return hasCreateTokenPermission(
			GetterUtil.getLong(userSubject.getId()), oAuth2Application);
	}

	@Override
	protected boolean isGrantHandlerEnabled() {
		if (!_oAuth2ProviderConfiguration.allowAuthorizationCodeGrant() &&
			!_oAuth2ProviderConfiguration.allowAuthorizationCodePKCEGrant()) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayAuthorizationAccessTokenCodeGrantHandler.class);

	private AuthorizationCodeGrantHandler _authorizationCodeGrantHandler;

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	private OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;

}