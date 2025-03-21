/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.remote.cors.annotation.CORS;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Map;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;

/**
 * @author Tomas Polesovsky
 */
@Path("/token")
public class LiferayAccessTokenService extends AccessTokenService {

	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@CORS(allowMethods = "POST")
	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response handleTokenRequest(MultivaluedMap<String, String> params) {
		Response response = super.handleTokenRequest(params);

		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			ClientAccessToken clientAccessToken = response.readEntity(
				ClientAccessToken.class);

			Map<String, String> parameters = clientAccessToken.getParameters();

			if (parameters.containsKey(
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_REMEMBER_DEVICE)) {

				LiferayOAuthDataProvider liferayOAuthDataProvider =
					(LiferayOAuthDataProvider)getDataProvider();

				liferayOAuthDataProvider.updateRememberDeviceContent(
					clientAccessToken.getRefreshToken(),
					parameters.get(
						OAuth2ProviderRESTEndpointConstants.
							PROPERTY_KEY_REMEMBER_DEVICE));
			}
		}

		return response;
	}

	@Override
	protected Client authenticateClientIfNeeded(
		MultivaluedMap<String, String> params) {

		String clientId = params.getFirst("client_id");

		if ((clientId != null) && clientId.isEmpty()) {
			reportInvalidClient();
		}

		String clientSecret = params.getFirst("client_secret");

		if ((clientSecret != null) && clientSecret.isEmpty()) {
			params.remove("client_secret");
		}

		Client client = super.authenticateClientIfNeeded(params);

		Map<String, String> properties = client.getProperties();

		MessageContext messageContext = getMessageContext();

		HttpServletRequest httpServletRequest =
			messageContext.getHttpServletRequest();

		String remoteAddr = httpServletRequest.getRemoteAddr();

		String remoteHost = httpServletRequest.getRemoteHost();

		try {
			InetAddress inetAddress = InetAddressUtil.getInetAddressByName(
				remoteAddr);

			remoteHost = inetAddress.getCanonicalHostName();
		}
		catch (UnknownHostException unknownHostException) {
			if (_log.isDebugEnabled()) {
				_log.debug(unknownHostException);
			}
		}

		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_REMOTE_ADDR,
			remoteAddr);
		properties.put(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_REMOTE_HOST,
			remoteHost);

		return client;
	}

	@Override
	protected void injectContextIntoOAuthProviders() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayAccessTokenService.class);

}