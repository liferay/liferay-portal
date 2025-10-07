/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.client;

import com.liferay.talend.common.util.StringUtil;
import com.liferay.talend.runtime.client.exception.ClientException;
import com.liferay.talend.runtime.client.exception.ConnectionClientException;
import com.liferay.talend.runtime.client.exception.OAuth2AuthorizationClientException;
import com.liferay.talend.runtime.client.exception.RemoteExecutionClientException;

import java.io.File;
import java.io.StringWriter;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Igor Beslic
 */
public class LiferayClient {

	public Response executeDeleteRequest(String targetURIString)
		throws ClientException {

		return _execute(
			HttpMethod.DELETE, _createBuilder(_toURI(targetURIString)));
	}

	public Response executeGetRequest(String targetURIString)
		throws ClientException {

		return _execute(
			HttpMethod.GET, _createBuilder(_toURI(targetURIString)));
	}

	public Response executePatchRequest(
			String targetURIString, JsonValue jsonValue)
		throws ClientException {

		return _execute(
			HttpMethod.PATCH, _createBuilder(_toURI(targetURIString)),
			Entity.json(_jsonObjectToPrettyString(jsonValue)));
	}

	public Response executePostRequest(String targetURIString, File file)
		throws ClientException {

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();

		formDataMultiPart.bodyPart(
			new FileDataBodyPart(
				"file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));

		return _execute(
			HttpMethod.POST, _createBuilder(_toURI(targetURIString)),
			Entity.entity(
				formDataMultiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
	}

	public Response executePostRequest(
			String targetURIString, JsonValue jsonValue)
		throws ClientException {

		return _execute(
			HttpMethod.POST, _createBuilder(_toURI(targetURIString)),
			Entity.json(_jsonObjectToPrettyString(jsonValue)));
	}

	public Response executePutRequest(
			String targetURIString, JsonValue jsonValue)
		throws ClientException {

		return _execute(
			HttpMethod.PUT, _createBuilder(_toURI(targetURIString)),
			Entity.json(_jsonObjectToPrettyString(jsonValue)));
	}

	public static class Builder {

		public LiferayClient build() {
			if (StringUtil.isEmpty(_authorizationIdentityId) ||
				StringUtil.isEmpty(_authorizationIdentitySecret)) {

				throw new ClientException(
					"Authorization credentials identity ID and identity " +
						"secret missing");
			}

			return new LiferayClient(this);
		}

		public Builder setAuthorizationIdentityId(
			String authorizationIdentityId) {

			_authorizationIdentityId = authorizationIdentityId;

			return this;
		}

		public Builder setAuthorizationIdentitySecret(
			String authorizationIdentitySecret) {

			_authorizationIdentitySecret = authorizationIdentitySecret;

			return this;
		}

		public Builder setConnectionTimeoutMills(int connectionTimeoutMills) {
			_connectionTimeoutMills = connectionTimeoutMills;

			return this;
		}

		public Builder setFollowRedirects(boolean followRedirects) {
			_followRedirects = followRedirects;

			return this;
		}

		public Builder setForceHttps(boolean forceHttps) {
			_forceHttps = forceHttps;

			return this;
		}

		public Builder setHostURL(String hostURL) {
			_hostURL = hostURL;

			return this;
		}

		public Builder setOAuthAuthorization(boolean oAuthAuthorization) {
			_oAuthAuthorization = oAuthAuthorization;

			return this;
		}

		public Builder setProxyIdentityId(String proxyIdentityId) {
			_proxyIdentityId = proxyIdentityId;

			return this;
		}

		public Builder setProxyIdentitySecret(String proxyIdentitySecret) {
			_proxyIdentitySecret = proxyIdentitySecret;

			return this;
		}

		public Builder setRadTimeoutMills(int readTimeoutMills) {
			_readTimeoutMills = readTimeoutMills;

			return this;
		}

		private String _authorizationIdentityId;
		private String _authorizationIdentitySecret;
		private int _connectionTimeoutMills;
		private boolean _followRedirects;
		private boolean _forceHttps;
		private String _hostURL;
		private boolean _oAuthAuthorization;
		private String _proxyIdentityId;
		private String _proxyIdentitySecret;
		private int _readTimeoutMills;

	}

	private LiferayClient(Builder builder) {
		_authorizationIdentityId = builder._authorizationIdentityId;
		_authorizationIdentitySecret = builder._authorizationIdentitySecret;
		_forceHttps = builder._forceHttps;
		_followRedirects = builder._followRedirects;

		_hostURL = _toRequiredHttpScheme(builder._hostURL);

		_proxyIdentityId = builder._proxyIdentityId;
		_proxyIdentitySecret = builder._proxyIdentitySecret;
		_readTimeoutMills = builder._readTimeoutMills;
		_connectionTimeoutMills = builder._connectionTimeoutMills;
		_oAuthAuthorization = builder._oAuthAuthorization;

		_client = ClientBuilder.newClient(_getClientConfig());

		_client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

		_client.register(MultiPartFeature.class);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Created new Liferay Client for {}", _hostURL);
		}
	}

	private void _addProxyAuthorizationHeader(Invocation.Builder builder) {
		if (StringUtil.isEmpty(_proxyIdentityId) ||
			StringUtil.isEmpty(_proxyIdentitySecret)) {

			return;
		}

		builder.header(
			"Proxy-Authorization",
			String.format(
				"Basic %s",
				_getBasicToken(_proxyIdentityId, _proxyIdentitySecret)));
	}

	private Invocation.Builder _createBuilder(URI targetURI)
		throws ConnectionClientException, OAuth2AuthorizationClientException {

		WebTarget webTarget = _client.target(targetURI);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Target: {}", targetURI);
		}

		Invocation.Builder builder = webTarget.request(
			MediaType.APPLICATION_JSON_TYPE);

		builder.header("Authorization", _getAuthorizationHeader());

		return builder;
	}

	private Response _execute(String httpMethod, Invocation.Builder builder) {
		return _execute(httpMethod, builder, null);
	}

	private Response _execute(
			String httpMethod, Invocation.Builder builder, Entity<?> entity)
		throws ConnectionClientException {

		Response response = _processRedirects(
			builder.method(httpMethod, entity));

		if (_responseHandler.isSuccess(response)) {
			return response;
		}

		String responseBody = "No response body available";

		if (response.hasEntity()) {
			responseBody = _responseHandler.asText(response);
		}

		throw new RemoteExecutionClientException(
			String.format(
				"Request failed with HTTP status %d and response %s",
				response.getStatus(), responseBody),
			response.getStatus());
	}

	private Response _executeAccessTokenPostRequest()
		throws ConnectionClientException {

		WebTarget webTarget = _client.target(
			_hostURL + _LIFERAY_OAUTH2_ACCESS_TOKEN_ENDPOINT);

		Invocation.Builder builder = webTarget.request(
			MediaType.APPLICATION_JSON_TYPE);

		XWWWFormURLEncoder xWWWFormURLEncoder = new XWWWFormURLEncoder();

		Entity<Form> entity = Entity.form(
			xWWWFormURLEncoder.toForm(
				"client_id", _authorizationIdentityId, "client_secret",
				_authorizationIdentitySecret, "grant_type",
				"client_credentials", "response_type", "code"));

		_addProxyAuthorizationHeader(builder);

		return _execute(HttpMethod.POST, builder, entity);
	}

	private String _getAuthorizationHeader()
		throws ConnectionClientException, OAuth2AuthorizationClientException {

		if (_oAuthAuthorization) {
			return "Bearer " + _getBearerToken();
		}

		return String.format(
			"Basic %s",
			_getBasicToken(
				_authorizationIdentityId, _authorizationIdentitySecret));
	}

	private String _getBasicToken(String identityId, String identitySecret) {
		Base64.Encoder base64Encoder = Base64.getEncoder();

		StringBuilder sb = new StringBuilder();

		sb.append(identityId);
		sb.append(":");
		sb.append(identitySecret);

		String base64Seed = sb.toString();

		return new String(base64Encoder.encode(base64Seed.getBytes()));
	}

	private String _getBearerToken()
		throws ConnectionClientException, OAuth2AuthorizationClientException {

		OAuthAccessToken oAuthAccessToken = _oAuthAccessTokens.get(
			_hostURL + _authorizationIdentityId);

		if ((oAuthAccessToken != null) && !oAuthAccessToken.isExpired()) {
			return oAuthAccessToken.getAccessToken();
		}

		JsonObject authorizationJsonObject = _requestAuthorizationJsonObject();

		String tokenType = authorizationJsonObject.getString("token_type");

		if (!Objects.equals(tokenType, "Bearer")) {
			throw new OAuth2AuthorizationClientException(
				"Unexpected token type received " + tokenType);
		}

		String accessToken = authorizationJsonObject.getString("access_token");
		int expiresIn = authorizationJsonObject.getInt("expires_in");

		_oAuthAccessTokens.put(
			_hostURL + _authorizationIdentityId,
			new OAuthAccessToken(
				accessToken, System.currentTimeMillis() + (expiresIn * 1000)));

		return accessToken;
	}

	private ClientConfig _getClientConfig() {
		ClientConfig clientConfig = new ClientConfig();

		clientConfig = clientConfig.property(
			ClientProperties.CONNECT_TIMEOUT, _connectionTimeoutMills);

		clientConfig = clientConfig.property(
			ClientProperties.READ_TIMEOUT, _readTimeoutMills);

		return clientConfig;
	}

	private String _jsonObjectToPrettyString(JsonValue jsonValue) {
		StringWriter stringWriter = new StringWriter();

		JsonWriter jsonWriter = Json.createWriter(stringWriter);

		jsonWriter.write(jsonValue);

		stringWriter.flush();

		return stringWriter.toString();
	}

	private Response _processRedirects(Response response)
		throws ConnectionClientException {

		if (!_followRedirects) {
			return response;
		}

		int count = 0;
		Response currentResponse = response;

		while (_responseHandler.isRedirect(currentResponse) && (count < 3)) {
			String location = currentResponse.getHeaderString(
				HttpHeaders.LOCATION);

			if (StringUtil.isEmpty(location)) {
				return currentResponse;
			}

			if (_logger.isDebugEnabled()) {
				_logger.debug("Redirect {}# to {}", count, location);
			}

			currentResponse.close();

			try {
				Invocation.Builder builder = _createBuilder(new URI(location));

				currentResponse = builder.get();
			}
			catch (URISyntaxException uriSyntaxException) {
				throw new ConnectionClientException(
					"Unable to redirect to location " + location,
					response.getStatus(), uriSyntaxException);
			}
		}

		return currentResponse;
	}

	private JsonObject _requestAuthorizationJsonObject()
		throws ConnectionClientException, OAuth2AuthorizationClientException {

		Response response = _executeAccessTokenPostRequest();

		if (response == null) {
			throw new OAuth2AuthorizationClientException(
				"Authorization request failed for unresponsive OAuth 2.0 " +
					"endpoint");
		}

		if (response.getStatus() != 200) {
			throw new OAuth2AuthorizationClientException(
				String.format(
					"OAuth 2.0 check failed with response status {%s}",
					response.getStatus()),
				response.getStatus());
		}

		if (_responseHandler.isApplicationJsonContentType(response)) {
			return _responseHandler.asJsonObject(response);
		}

		List<String> contentTypeValues = _responseHandler.getContentType(
			response);

		throw new OAuth2AuthorizationClientException(
			String.format(
				"Unable to extract OAuth 2.0 credentials from response " +
					"content type {%s}",
				contentTypeValues.get(0)),
			response.getStatus());
	}

	private String _toRequiredHttpScheme(String targetUrl) {
		if (_forceHttps && !targetUrl.startsWith(_HTTPS)) {
			return targetUrl.replace(_HTTP, _HTTPS);
		}

		return targetUrl;
	}

	private URI _toURI(String targetURIString) {
		try {
			return new URI(_hostURL + targetURIString);
		}
		catch (URISyntaxException uriSyntaxException) {
			_logger.error(
				"Unable to parse {} as a URI reference",
				_hostURL + targetURIString);
		}

		return null;
	}

	private static final String _HTTP = "http://";

	private static final String _HTTPS = "https://";

	private static final String _LIFERAY_OAUTH2_ACCESS_TOKEN_ENDPOINT =
		"/o/oauth2/token";

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayClient.class);

	private static final Map<String, OAuthAccessToken> _oAuthAccessTokens =
		new ConcurrentHashMap<>();

	private final String _authorizationIdentityId;
	private final String _authorizationIdentitySecret;
	private final Client _client;
	private final int _connectionTimeoutMills;
	private final boolean _followRedirects;
	private final boolean _forceHttps;
	private final String _hostURL;
	private final boolean _oAuthAuthorization;
	private final String _proxyIdentityId;
	private final String _proxyIdentitySecret;
	private final int _readTimeoutMills;
	private final ResponseHandler _responseHandler = new ResponseHandler();

	private class OAuthAccessToken {

		public String getAccessToken() {
			return _accessToken;
		}

		public boolean isExpired() {
			if (_expirationTime <= (System.currentTimeMillis() + (15 * 1000))) {
				return true;
			}

			return false;
		}

		private OAuthAccessToken(String accessToken, long expirationTime) {
			_accessToken = accessToken;
			_expirationTime = expirationTime;
		}

		private final String _accessToken;
		private final long _expirationTime;

	}

}