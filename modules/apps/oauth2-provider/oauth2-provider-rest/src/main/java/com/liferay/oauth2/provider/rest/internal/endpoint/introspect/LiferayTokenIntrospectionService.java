/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.introspect;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.remote.cors.annotation.CORS;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.services.AbstractTokenService;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;

/**
 * @author Tomas Polesovsky
 */
@Path("introspect")
public class LiferayTokenIntrospectionService extends AbstractTokenService {

	public LiferayTokenIntrospectionService(
		LiferayOAuthDataProvider liferayOAuthDataProvider,
		boolean canSupportPublicClients) {

		_liferayOAuthDataProvider = liferayOAuthDataProvider;

		setCanSupportPublicClients(canSupportPublicClients);
		setDataProvider(liferayOAuthDataProvider);
	}

	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@CORS(allowMethods = "POST")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTokenIntrospection(
		@Encoded MultivaluedMap<String, String> params) {

		Client client = authenticateClientIfNeeded(params);

		String tokenId = params.getFirst(OAuthConstants.TOKEN_ID);
		String tokenTypeHint = params.getFirst(OAuthConstants.TOKEN_TYPE_HINT);

		if (tokenTypeHint == null) {
			ServerAccessToken serverAccessToken =
				_liferayOAuthDataProvider.getAccessToken(tokenId);

			if (serverAccessToken != null) {
				return _handleAccessToken(client, serverAccessToken);
			}

			RefreshToken refreshToken =
				_liferayOAuthDataProvider.getRefreshToken(tokenId);

			if (refreshToken != null) {
				return _handleRefreshToken(client, refreshToken);
			}
		}
		else if (OAuthConstants.ACCESS_TOKEN.equals(tokenTypeHint)) {
			ServerAccessToken serverAccessToken =
				_liferayOAuthDataProvider.getAccessToken(tokenId);

			if (serverAccessToken != null) {
				return _handleAccessToken(client, serverAccessToken);
			}
		}
		else if (OAuthConstants.REFRESH_TOKEN.equals(tokenTypeHint)) {
			RefreshToken refreshToken =
				_liferayOAuthDataProvider.getRefreshToken(tokenId);

			if (refreshToken != null) {
				return _handleRefreshToken(client, refreshToken);
			}
		}
		else {
			return createErrorResponseFromErrorCode(
				OAuthConstants.UNSUPPORTED_TOKEN_TYPE);
		}

		return Response.ok(
			new TokenIntrospection(false)
		).build();
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

		return super.authenticateClientIfNeeded(params);
	}

	private boolean _clientsMatch(Client client1, Client client2) {
		if (!Objects.equals(client1.getClientId(), client2.getClientId())) {
			return false;
		}

		String companyId1 = MapUtil.getString(
			client1.getProperties(),
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID);
		String companyId2 = MapUtil.getString(
			client2.getProperties(),
			OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID);

		return Objects.equals(companyId1, companyId2);
	}

	private TokenIntrospection _createTokenIntrospection(
		ServerAccessToken serverAccessToken) {

		TokenIntrospection tokenIntrospection = new TokenIntrospection(true);

		List<String> audiences = serverAccessToken.getAudiences();

		if (ListUtil.isNotEmpty(audiences)) {
			tokenIntrospection.setAud(audiences);
		}

		Client client = serverAccessToken.getClient();

		tokenIntrospection.setClientId(client.getClientId());

		tokenIntrospection.setExp(
			serverAccessToken.getIssuedAt() + serverAccessToken.getExpiresIn());

		Map<String, String> extraProperties =
			serverAccessToken.getExtraProperties();

		if (extraProperties != null) {
			Map<String, String> extensions = tokenIntrospection.getExtensions();

			extensions.putAll(extraProperties);
		}

		String issuer = serverAccessToken.getIssuer();

		if (issuer != null) {
			tokenIntrospection.setIss(issuer);
		}

		tokenIntrospection.setIat(serverAccessToken.getIssuedAt());

		List<OAuthPermission> oAuthPermissions = serverAccessToken.getScopes();

		if (ListUtil.isNotEmpty(oAuthPermissions)) {
			tokenIntrospection.setScope(
				OAuthUtils.convertPermissionsToScope(oAuthPermissions));
		}

		UserSubject userSubject = serverAccessToken.getSubject();

		if (userSubject != null) {
			tokenIntrospection.setUsername(userSubject.getLogin());
			tokenIntrospection.setSub(userSubject.getId());
		}

		tokenIntrospection.setTokenType(serverAccessToken.getTokenType());

		return tokenIntrospection;
	}

	private Response _handleAccessToken(
		Client client, ServerAccessToken serverAccessToken) {

		if (!_verifyClient(client, serverAccessToken)) {
			return createErrorResponseFromErrorCode(
				OAuthConstants.UNAUTHORIZED_CLIENT);
		}

		if (!_verifyServerAccessToken(serverAccessToken)) {
			return Response.ok(
				new TokenIntrospection(false)
			).build();
		}

		BearerTokenProvider.AccessToken bearerAccessToken =
			_liferayOAuthDataProvider.fromCXFAccessToken(serverAccessToken);

		OAuth2Application oAuth2Application =
			bearerAccessToken.getOAuth2Application();

		BearerTokenProvider bearerTokenProvider =
			_liferayOAuthDataProvider.getBearerTokenProvider(
				oAuth2Application.getCompanyId(),
				oAuth2Application.getClientId());

		if (!bearerTokenProvider.isValid(bearerAccessToken)) {
			return Response.ok(
				new TokenIntrospection(false)
			).build();
		}

		return Response.ok(
			_createTokenIntrospection(serverAccessToken)
		).build();
	}

	private Response _handleRefreshToken(
		Client client, RefreshToken refreshToken) {

		if (!_verifyClient(client, refreshToken)) {
			return createErrorResponseFromErrorCode(
				OAuthConstants.UNAUTHORIZED_CLIENT);
		}

		if (!_verifyServerAccessToken(refreshToken)) {
			return Response.ok(
				new TokenIntrospection(false)
			).build();
		}

		BearerTokenProvider.RefreshToken bearerRefreshToken =
			_liferayOAuthDataProvider.fromCXFRefreshToken(refreshToken);

		OAuth2Application oAuth2Application =
			bearerRefreshToken.getOAuth2Application();

		BearerTokenProvider bearerTokenProvider =
			_liferayOAuthDataProvider.getBearerTokenProvider(
				oAuth2Application.getCompanyId(),
				oAuth2Application.getClientId());

		if (!bearerTokenProvider.isValid(bearerRefreshToken)) {
			return Response.ok(
				new TokenIntrospection(false)
			).build();
		}

		return Response.status(
			Response.Status.OK
		).entity(
			_createTokenIntrospection(refreshToken)
		).build();
	}

	private boolean _verifyClient(
		Client client, ServerAccessToken serverAccessToken) {

		if (!_clientsMatch(client, serverAccessToken.getClient())) {
			return false;
		}

		Map<String, String> properties = client.getProperties();

		return properties.containsKey(
			OAuth2ProviderRESTEndpointConstants.
				PROPERTY_KEY_CLIENT_FEATURE_PREFIX +
					OAuth2ProviderRESTEndpointConstants.
						PROPERTY_KEY_CLIENT_FEATURE_TOKEN_INTROSPECTION);
	}

	private boolean _verifyServerAccessToken(
		ServerAccessToken serverAccessToken) {

		return !OAuthUtils.isExpired(
			serverAccessToken.getIssuedAt(), serverAccessToken.getExpiresIn());
	}

	private final LiferayOAuthDataProvider _liferayOAuthDataProvider;

}