/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration;

import com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.model.LiferayClientRegistration;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.remote.cors.annotation.CORS;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;
import org.apache.cxf.rs.security.oauth2.services.DynamicRegistrationService;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

/**
 * @author Jorge García Jiménez
 */
@Path("/register")
public class LiferayDynamicRegistrationService
	extends DynamicRegistrationService {

	@Consumes(MediaType.APPLICATION_JSON)
	@CORS(allowMethods = "POST")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(LiferayClientRegistration clientRegistration) {
		return super.register(clientRegistration);
	}

	@Override
	protected void checkRegistrationAccessToken(Client c, String accessToken) {
	}

	protected void fromClientRegistrationToClient(
		ClientRegistration request, Client client) {

		List<String> grantTypes = client.getAllowedGrantTypes();

		_checkValidGrantResponseTypes(grantTypes, request.getResponseTypes());

		List<String> redirectUris = request.getRedirectUris();
		String clientUri;

		if (redirectUris != null) {
			String appType = request.getApplicationType();

			if (appType == null) {
				appType = "web";
			}

			Iterator<String> iterator = redirectUris.iterator();

			while (iterator.hasNext()) {
				clientUri = iterator.next();

				validateRequestUri(clientUri, appType, grantTypes);
			}

			client.setRedirectUris(redirectUris);
		}

		if (client.getRedirectUris(
			).isEmpty() &&
			(grantTypes.contains("authorization_code") ||
			 grantTypes.contains("implicit"))) {

			OAuthError error = new OAuthError(
				"invalid_request", "A Redirection URI is required");

			_reportInvalidRequestError(error);
		}

		List<String> resourceUris = request.getResourceUris();

		if (resourceUris != null) {
			client.setRegisteredAudiences(resourceUris);
		}

		String scope = request.getScope();

		if (!StringUtils.isEmpty(scope)) {
			client.setRegisteredScopes(OAuthUtils.parseScope(scope));
		}

		clientUri = request.getClientUri();

		if (clientUri != null) {
			client.setApplicationWebUri(clientUri);
		}

		String clientLogoUri = request.getLogoUri();

		if (clientLogoUri != null) {
			client.setApplicationLogoUri(clientLogoUri);
		}
	}

	@Override
	protected String generateClientId() {
		return OAuth2SecureRandomGenerator.generateClientId();
	}

	@Override
	protected String generateClientSecret(ClientRegistration request) {
		return OAuth2SecureRandomGenerator.generateClientSecret();
	}

	private void _checkValidGrantResponseTypes(
		List<String> grantTypes, List<String> responseTypes) {

		List<String> allowedResponseTypeList = new ArrayList<>();

		for (String grantType : grantTypes) {
			String allowedResponseType = _responseTypeAllowedByGrantType.get(
				grantType);

			if (Validator.isNotNull(allowedResponseType)) {
				allowedResponseTypeList.add(allowedResponseType);
			}
		}

		if (responseTypes.isEmpty() && !allowedResponseTypeList.isEmpty()) {
			OAuthError error = new OAuthError(
				"invalid_client_metadata",
				"A response type '" + allowedResponseTypeList.get(0) +
					"' is needed to match provided grant types");

			_reportInvalidRequestError(error);
		}

		for (String responseType : responseTypes) {
			if (!allowedResponseTypeList.contains(responseType)) {
				OAuthError error = new OAuthError(
					"invalid_client_metadata",
					"Invalid response type '" + responseType +
						"' by provided grant types");

				_reportInvalidRequestError(error);
			}
		}
	}

	private void _reportInvalidRequestError(OAuthError error) {
		Response.ResponseBuilder responseBuilder = JAXRSUtils.toResponseBuilder(
			400);

		responseBuilder.type(MediaType.APPLICATION_JSON);

		throw ExceptionUtils.toBadRequestException(
			(Throwable)null,
			responseBuilder.entity(
				error
			).build());
	}

	private static final Map<String, String> _responseTypeAllowedByGrantType =
		HashMapBuilder.put(
			"authorization_code", "code"
		).put(
			"implicit", "token"
		).build();

	/**
	 *
	 *
	 .put(
	 "client_credentials",
	 (BaseMapBuilder.UnsafeSupplier<String, Exception>)null
	 ).put(
	 "password", (BaseMapBuilder.UnsafeSupplier<String, Exception>)null
	 ).put(
	 "refresh_token",
	 (BaseMapBuilder.UnsafeSupplier<String, Exception>)null
	 ).put(
	 "urn:ietf:params:oauth:grant-type:jwt-bearer",
	 (BaseMapBuilder.UnsafeSupplier<String, Exception>)null
	 ).put(
	 "urn:ietf:params:oauth:grant-type:saml2-bearer",
	 (BaseMapBuilder.UnsafeSupplier<String, Exception>)null
	 )
	 * */
}