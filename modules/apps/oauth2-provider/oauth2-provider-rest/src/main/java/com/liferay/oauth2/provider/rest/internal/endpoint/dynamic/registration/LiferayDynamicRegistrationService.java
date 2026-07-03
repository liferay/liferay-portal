/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.internal.constants.OAuth2ProviderRESTWebKeys;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.model.LiferayClientRegistration;
import com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.model.LiferayClientRegistrationResponse;
import com.liferay.oauth2.provider.rest.internal.endpoint.util.OAuth2ErrorUtil;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;
import org.apache.cxf.rs.security.oauth2.services.DynamicRegistrationService;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

/**
 * @author Jorge García Jiménez
 */
@Path("/register")
public class LiferayDynamicRegistrationService
	extends DynamicRegistrationService {

	@DELETE
	@Path("{clientId}")
	public Response deleteClientRegistration(
		@PathParam("clientId") String clientId) {

		super.deleteClientRegistration(clientId);

		return JAXRSUtils.toResponseBuilder(
			204
		).build();
	}

	@GET
	@Override
	@Path("{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientRegistration readClientRegistrationWithPath(
		@PathParam("clientId") String clientId) {

		return super.readClientRegistrationWithPath(clientId);
	}

	@GET
	@Override
	@Produces(MediaType.APPLICATION_JSON)
	public ClientRegistration readClientRegistrationWithQuery(
		@QueryParam("client_id") String clientId) {

		return super.readClientRegistrationWithQuery(clientId);
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(
		LiferayClientRegistration liferayClientRegistration) {

		try {
			Response response = super.register(liferayClientRegistration);

			_routeAuditMessage(_buildAddAuditMessage(response));

			return response;
		}
		catch (RuntimeException runtimeException) {
			_routeAuditMessage(
				_buildRejectAuditMessage(
					runtimeException, liferayClientRegistration));

			throw runtimeException;
		}
	}

	public void setPortal(Portal portal) {
		_portal = portal;
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PUT
	public ClientRegistration updateClientRegistration(
		@PathParam("clientId") String clientId,
		LiferayClientRegistration liferayClientRegistration) {

		return super.updateClientRegistration(
			clientId, liferayClientRegistration);
	}

	@Override
	protected void checkRegistrationAccessToken(
		Client client, String accessToken) {
	}

	@Override
	protected String createRegAccessToken(Client client) {
		String registrationAccessToken = OAuthUtils.generateRandomTokenKey();

		Map<String, String> properties = client.getProperties();

		properties.put(
			"registration_access_token", "reg-" + registrationAccessToken);

		return registrationAccessToken;
	}

	@Override
	protected void fromClientRegistrationToClient(
		ClientRegistration clientRegistration, Client client) {

		_validate(client, clientRegistration);

		_setAllowedGrantTypes(client);

		client.setApplicationName(clientRegistration.getClientName());

		clientRegistration.setApplicationType(
			_getApplicationType(clientRegistration));

		Map<String, String> properties = client.getProperties();

		properties.put(
			"application_type", clientRegistration.getApplicationType());

		String jwks = clientRegistration.getStringProperty(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS);

		if (Validator.isNotNull(jwks)) {
			properties.put(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS,
				jwks);
		}

		String jwksURI = clientRegistration.getStringProperty(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS_URI);

		if (Validator.isNotNull(jwksURI)) {
			properties.put(
				OAuth2ProviderRESTEndpointConstants.
					PROPERTY_KEY_CLIENT_JWKS_URI,
				jwksURI);
		}

		String softwareId = clientRegistration.getStringProperty(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_SOFTWARE_ID);

		if (Validator.isNotNull(softwareId)) {
			properties.put(
				OAuth2ProviderRESTEndpointConstants.
					PROPERTY_KEY_CLIENT_SOFTWARE_ID,
				softwareId);
		}

		String tosURI = clientRegistration.getTosUri();

		if (Validator.isNotNull(tosURI)) {
			properties.put("tos_uri", tosURI);
		}

		String logoURI = clientRegistration.getLogoUri();

		if (Validator.isNotNull(logoURI)) {
			client.setApplicationLogoUri(logoURI);
		}

		String clientURI = clientRegistration.getClientUri();

		if (clientURI != null) {
			client.setApplicationWebUri(clientURI);
		}

		List<String> redirectURIs = clientRegistration.getRedirectUris();

		if (redirectURIs != null) {
			client.setRedirectUris(redirectURIs);
		}

		List<String> resourceURIs = clientRegistration.getResourceUris();

		if (resourceURIs != null) {
			client.setRegisteredAudiences(resourceURIs);
		}

		String scope = clientRegistration.getScope();

		if (!Validator.isBlank(scope)) {
			client.setRegisteredScopes(OAuthUtils.parseScope(scope));
		}
	}

	@Override
	protected LiferayClientRegistrationResponse
		fromClientToRegistrationResponse(Client client) {

		LiferayClientRegistrationResponse liferayClientRegistrationResponse =
			new LiferayClientRegistrationResponse();

		liferayClientRegistrationResponse.setClientId(client.getClientId());
		liferayClientRegistrationResponse.setClientIdIssuedAt(
			client.getRegisteredAt());

		if (Validator.isNotNull(client.getApplicationName())) {
			liferayClientRegistrationResponse.setClientName(
				client.getApplicationName());
		}

		if (client.getClientSecret() != null) {
			liferayClientRegistrationResponse.setClientSecret(
				client.getClientSecret());
			liferayClientRegistrationResponse.setClientSecretExpiresAt(0L);
		}

		liferayClientRegistrationResponse.setGrantTypes(
			_toResponseGrantTypes(client.getAllowedGrantTypes()));
		liferayClientRegistrationResponse.setLogoUri(
			client.getApplicationLogoUri());
		liferayClientRegistrationResponse.setRedirectUris(
			client.getRedirectUris());

		Map<String, String> properties = client.getProperties();

		String jwks = properties.get(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS);

		if (jwks != null) {
			liferayClientRegistrationResponse.setJwks(jwks);
		}

		String jwksURI = properties.get(
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS_URI);

		if (jwksURI != null) {
			liferayClientRegistrationResponse.setJwksUri(jwksURI);
		}

		liferayClientRegistrationResponse.setRegistrationAccessToken(
			properties.get("registration_access_token"));

		MessageContext messageContext = getMessageContext();

		UriInfo uriInfo = messageContext.getUriInfo();

		UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

		liferayClientRegistrationResponse.setRegistrationClientUri(
			uriBuilder.path(
				client.getClientId()
			).build(
				new Object[0]
			).toString());

		if (ListUtil.isNotEmpty(client.getRegisteredScopes())) {
			liferayClientRegistrationResponse.setScope(
				StringUtil.merge(
					client.getRegisteredScopes(), StringPool.SPACE));
		}

		String softwareId = properties.get(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_SOFTWARE_ID);

		if (softwareId != null) {
			liferayClientRegistrationResponse.setSoftwareId(softwareId);
		}

		if (properties.get("tos_uri") != null) {
			liferayClientRegistrationResponse.setTosUri(
				properties.get("tos_uri"));
		}

		return liferayClientRegistrationResponse;
	}

	@Override
	protected String generateClientId() {
		return OAuth2SecureRandomGenerator.generateClientId();
	}

	@Override
	protected String generateClientSecret(
		ClientRegistration clientRegistration) {

		return OAuth2SecureRandomGenerator.generateClientSecret();
	}

	private AuditMessage _buildAddAuditMessage(Response response) {
		if (response == null) {
			return null;
		}

		Object entity = response.getEntity();

		if (!(entity instanceof
				LiferayClientRegistrationResponse
					liferayClientRegistrationResponse)) {

			return null;
		}

		JSONObject additionalInfoJSONObject = _getBaseAdditionalInfoJSONObject(
		).put(
			"clientId", liferayClientRegistrationResponse.getClientId()
		).put(
			"clientName", liferayClientRegistrationResponse.getClientName()
		).put(
			"grantTypes",
			JSONFactoryUtil.createJSONArray(
				liferayClientRegistrationResponse.getGrantTypes())
		).put(
			"redirectUris",
			JSONFactoryUtil.createJSONArray(
				liferayClientRegistrationResponse.getRedirectUris())
		).put(
			"scope", liferayClientRegistrationResponse.getScope()
		);

		return new AuditMessage(
			0, _getCompanyId(), 0, StringPool.BLANK, null,
			additionalInfoJSONObject, OAuth2Application.class.getName(),
			GetterUtil.getString(
				liferayClientRegistrationResponse.getClientId()),
			OAuth2ProviderRESTEndpointConstants.
				EVENT_TYPE_DYNAMIC_REGISTRATION_ADD,
			StringPool.BLANK);
	}

	private AuditMessage _buildRejectAuditMessage(
		Exception exception,
		LiferayClientRegistration liferayClientRegistration) {

		String clientName = StringPool.BLANK;

		if (liferayClientRegistration != null) {
			clientName = GetterUtil.getString(
				liferayClientRegistration.getClientName());
		}

		String error = OAuth2ProviderRESTEndpointConstants.ERROR_SERVER_ERROR;
		String errorDescription = GetterUtil.getString(exception.getMessage());

		if (exception instanceof
				WebApplicationException webApplicationException) {

			Object entity = null;

			Response response = webApplicationException.getResponse();

			if (response != null) {
				entity = response.getEntity();
			}

			if (entity instanceof OAuthError oAuthError) {
				error = GetterUtil.getString(oAuthError.getError(), error);

				if (Validator.isNotNull(oAuthError.getErrorDescription())) {
					errorDescription = oAuthError.getErrorDescription();
				}
			}
		}

		JSONArray grantTypesJSONArray = JSONFactoryUtil.createJSONArray();
		JSONArray redirectURIsJSONArray = JSONFactoryUtil.createJSONArray();
		String scope = StringPool.BLANK;

		if (liferayClientRegistration != null) {
			grantTypesJSONArray = JSONFactoryUtil.createJSONArray(
				liferayClientRegistration.getGrantTypes());
			redirectURIsJSONArray = JSONFactoryUtil.createJSONArray(
				liferayClientRegistration.getRedirectUris());
			scope = GetterUtil.getString(liferayClientRegistration.getScope());
		}

		JSONObject additionalInfoJSONObject = _getBaseAdditionalInfoJSONObject(
		).put(
			"clientName", clientName
		).put(
			"error", error
		).put(
			"errorDescription", errorDescription
		).put(
			"grantTypes", grantTypesJSONArray
		).put(
			"redirectUris", redirectURIsJSONArray
		).put(
			"scope", scope
		);

		return new AuditMessage(
			0, _getCompanyId(), 0, StringPool.BLANK, null,
			additionalInfoJSONObject, OAuth2Application.class.getName(),
			StringPool.BLANK,
			OAuth2ProviderRESTEndpointConstants.
				EVENT_TYPE_DYNAMIC_REGISTRATION_REJECT,
			StringPool.BLANK);
	}

	private String _getApplicationType(ClientRegistration clientRegistration) {
		return GetterUtil.getString(
			clientRegistration.getApplicationType(), "web");
	}

	private JSONObject _getBaseAdditionalInfoJSONObject() {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		String clientHost = StringPool.BLANK;
		String userAgent = StringPool.BLANK;

		if (httpServletRequest != null) {
			clientHost = GetterUtil.getString(
				httpServletRequest.getAttribute(
					OAuth2ProviderRESTWebKeys.DYNAMIC_REGISTRATION_CLIENT_HOST),
				httpServletRequest.getRemoteAddr());

			userAgent = GetterUtil.getString(
				httpServletRequest.getHeader("User-Agent"));
		}

		return JSONUtil.put(
			"clientHost", clientHost
		).put(
			"mode",
			OAuth2ProviderRESTEndpointConstants.
				DYNAMIC_REGISTRATION_MODE_AUTHENTICATED
		).put(
			"userAgent", userAgent
		);
	}

	private long _getCompanyId() {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		if ((httpServletRequest == null) || (_portal == null)) {
			return 0;
		}

		return _portal.getCompanyId(httpServletRequest);
	}

	private HttpServletRequest _getHttpServletRequest() {
		MessageContext messageContext = getMessageContext();

		if (messageContext == null) {
			return null;
		}

		return messageContext.getHttpServletRequest();
	}

	private void _routeAuditMessage(AuditMessage auditMessage) {
		if (auditMessage == null) {
			return;
		}

		try {
			AuditRouterUtil.route(auditMessage);
		}
		catch (AuditException auditException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", auditException);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _setAllowedGrantTypes(Client client) {
		if (!OAuthConstants.TOKEN_ENDPOINT_AUTH_NONE.equals(
				client.getTokenEndpointAuthMethod())) {

			return;
		}

		List<String> allowedGrantTypes = client.getAllowedGrantTypes();

		if (allowedGrantTypes == null) {
			return;
		}

		int index = allowedGrantTypes.indexOf(
			OAuthConstants.AUTHORIZATION_CODE_GRANT);

		if (index < 0) {
			return;
		}

		allowedGrantTypes = new ArrayList<>(allowedGrantTypes);

		allowedGrantTypes.set(
			index,
			OAuth2ProviderRESTEndpointConstants.AUTHORIZATION_CODE_PKCE_GRANT);

		client.setAllowedGrantTypes(allowedGrantTypes);
	}

	private List<String> _toResponseGrantTypes(List<String> allowedGrantTypes) {
		if (allowedGrantTypes == null) {
			return null;
		}

		List<String> responseGrantTypes = new ArrayList<>(
			allowedGrantTypes.size());

		for (String allowedGrantType : allowedGrantTypes) {
			String responseGrantType = allowedGrantType;

			if (OAuth2ProviderRESTEndpointConstants.
					AUTHORIZATION_CODE_PKCE_GRANT.equals(allowedGrantType)) {

				responseGrantType = OAuthConstants.AUTHORIZATION_CODE_GRANT;
			}

			if (!responseGrantTypes.contains(responseGrantType)) {
				responseGrantTypes.add(responseGrantType);
			}
		}

		return responseGrantTypes;
	}

	private void _validate(
		Client client, ClientRegistration clientRegistration) {

		List<String> allowedGrantTypes = client.getAllowedGrantTypes();

		if (allowedGrantTypes == null) {
			return;
		}

		List<String> redirectURIs = clientRegistration.getRedirectUris();

		if (redirectURIs != null) {
			String applicationType = _getApplicationType(clientRegistration);

			for (String redirectURI : redirectURIs) {
				validateRequestUri(
					redirectURI, applicationType,
					client.getAllowedGrantTypes());
			}
		}

		if ((allowedGrantTypes.contains(
				OAuthConstants.AUTHORIZATION_CODE_GRANT) ||
			 allowedGrantTypes.contains(OAuthConstants.IMPLICIT_GRANT)) &&
			ListUtil.isEmpty(redirectURIs)) {

			OAuth2ErrorUtil.reportInvalidRequestError(
				StringBundler.concat(
					"At least one redirect URI is required for the provided ",
					"grant types ", allowedGrantTypes),
				OAuthConstants.INVALID_REQUEST, Response.Status.BAD_REQUEST);
		}

		List<String> allowedResponseTypes = TransformUtil.transform(
			allowedGrantTypes, _allowedResponseTypes::get);
		List<String> responseTypes = clientRegistration.getResponseTypes();

		if (ListUtil.isNotEmpty(allowedResponseTypes) &&
			ListUtil.isEmpty(responseTypes)) {

			OAuth2ErrorUtil.reportInvalidRequestError(
				StringBundler.concat(
					"At least one response type is required for the provided ",
					"grant types ", allowedGrantTypes),
				OAuth2ProviderRESTEndpointConstants.
					ERROR_INVALID_CLIENT_METADATA,
				Response.Status.BAD_REQUEST);
		}

		if (responseTypes != null) {
			for (String responseType : responseTypes) {
				if (!allowedResponseTypes.contains(responseType)) {
					OAuth2ErrorUtil.reportInvalidRequestError(
						"Invalid response type " + responseType,
						OAuth2ProviderRESTEndpointConstants.
							ERROR_INVALID_CLIENT_METADATA,
						Response.Status.BAD_REQUEST);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayDynamicRegistrationService.class);

	private static final Map<String, String> _allowedResponseTypes =
		HashMapBuilder.put(
			OAuthConstants.AUTHORIZATION_CODE_GRANT,
			OAuthConstants.CODE_RESPONSE_TYPE
		).put(
			OAuthConstants.IMPLICIT_GRANT, OAuthConstants.TOKEN_RESPONSE_TYPE
		).build();

	private Portal _portal;

}