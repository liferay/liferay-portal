/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.authentication.handler;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Map;

import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;
import org.apache.cxf.rs.security.oauth2.grants.jwt.JwtBearerAuthHandler;
import org.apache.cxf.rs.security.oauth2.provider.ClientRegistrationProvider;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

/**
 * @author Arthur Chan
 */
@Provider
public class LiferayJWTBearerAuthenticationHandler
	extends JwtBearerAuthHandler {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		UriInfo uriInfo = containerRequestContext.getUriInfo();

		if (!StringUtil.startsWith(uriInfo.getPath(), "token")) {
			return;
		}

		Message message = JAXRSUtils.getCurrentMessage();

		HttpServletRequest httpServletRequest = (HttpServletRequest)message.get(
			AbstractHTTPDestination.HTTP_REQUEST);

		if (!_isUsingJWTAssertionForClientAuthentication(httpServletRequest)) {
			return;
		}

		String assertion = ParamUtil.getString(
			httpServletRequest, Constants.CLIENT_AUTH_ASSERTION_PARAM);

		if (assertion == null) {
			throw new NotAuthorizedException("Missing JWT assertion");
		}

		JwtToken jwtToken = super.getJwtToken(assertion);

		String claimSubject = (String)jwtToken.getClaim(
			JwtConstants.CLAIM_SUBJECT);

		String clientId = ParamUtil.getString(
			httpServletRequest, OAuthConstants.CLIENT_ID);

		if (Validator.isNotNull(clientId) && !clientId.equals(claimSubject)) {
			throw new NotAuthorizedException(
				"Client ID parameter does not match JWT subject");
		}

		message.put(OAuthConstants.CLIENT_ID, claimSubject);

		SecurityContext securityContext = configureSecurityContext(jwtToken);

		if (securityContext != null) {
			JAXRSUtils.getCurrentMessage(
			).put(
				SecurityContext.class, securityContext
			);
		}
	}

	public void setClientRegistrationProvider(
		ClientRegistrationProvider clientRegistrationProvider) {

		_clientRegistrationProvider = clientRegistrationProvider;
	}

	@Override
	protected JwsSignatureVerifier getInitializedSignatureVerifier(
		JwtToken jwtToken) {

		Client client = _clientRegistrationProvider.getClient(
			(String)jwtToken.getClaim(JwtConstants.CLAIM_SUBJECT));

		String tokenEndpointAuthMethod = client.getTokenEndpointAuthMethod();

		try {
			if (tokenEndpointAuthMethod.equals("client_secret_jwt")) {
				String clientSecret = client.getClientSecret();

				byte[] bytes = clientSecret.getBytes(StandardCharsets.UTF_8);

				try {
					Base64.Decoder decoder = Base64.getDecoder();

					decoder.decode(bytes);
				}
				catch (IllegalArgumentException illegalArgumentException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Client secret is not Base64 encoded",
							illegalArgumentException);
					}

					Base64.Encoder encoder = Base64.getEncoder();

					clientSecret = new String(encoder.encode(bytes), "UTF-8");
				}

				return new HmacJwsSignatureVerifier(clientSecret);
			}

			if (tokenEndpointAuthMethod.equals("private_key_jwt")) {
				Map<String, String> clientProperties = client.getProperties();

				JsonWebKeys jsonWebKeys = JwkUtils.readJwkSet(
					clientProperties.get(
						OAuth2ProviderRESTEndpointConstants.
							PROPERTY_KEY_CLIENT_JWKS));

				return JwsUtils.getSignatureVerifier(
					jsonWebKeys.getKey(
						(String)jwtToken.getJwsHeader(
							JoseConstants.HEADER_KEY_ID)));
			}

			throw new IllegalArgumentException(
				"Client is configured to not use JWT as a client " +
					"authentication method");
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new NotAuthorizedException(OAuthConstants.INVALID_CLIENT);
		}
	}

	private boolean _isUsingJWTAssertionForClientAuthentication(
		HttpServletRequest httpServletRequest) {

		String assertionType = ParamUtil.getString(
			httpServletRequest, Constants.CLIENT_AUTH_ASSERTION_TYPE);

		if (Validator.isNull(assertionType)) {
			return false;
		}

		return Constants.CLIENT_AUTH_JWT_BEARER.equals(
			HttpUtils.urlDecode(assertionType));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayJWTBearerAuthenticationHandler.class);

	private ClientRegistrationProvider _clientRegistrationProvider;

}