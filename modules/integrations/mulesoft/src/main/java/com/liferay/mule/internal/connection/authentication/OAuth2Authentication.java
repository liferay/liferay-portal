/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection.authentication;

import com.fasterxml.jackson.databind.JsonNode;

import com.liferay.mule.internal.error.LiferayError;
import com.liferay.mule.internal.oas.OASURLParser;
import com.liferay.mule.internal.util.JsonNodeReader;

import java.io.IOException;

import java.net.MalformedURLException;

import java.util.concurrent.TimeoutException;

import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
public class OAuth2Authentication implements HttpAuthentication {

	public OAuth2Authentication(
			String consumerKey, String consumerSecret, HttpClient httpClient,
			String openAPISpecPath)
		throws MalformedURLException {

		this.httpClient = httpClient;
		oAuth2AccessTokenURI = getOAuth2AccessTokenURI(openAPISpecPath);

		queryParams.put("client_id", consumerKey);
		queryParams.put("client_secret", consumerSecret);
		queryParams.put("grant_type", "client_credentials");
	}

	@Override
	public String getAuthorizationHeader() throws ModuleException {
		JsonNode authorizationJsonNode = getAuthorizationJsonNode();

		JsonNode tokenTypeJsonNode = authorizationJsonNode.get("token_type");
		JsonNode accessTokenJsonNode = authorizationJsonNode.get(
			"access_token");

		return String.format(
			"%s %s", tokenTypeJsonNode.textValue(),
			accessTokenJsonNode.textValue());
	}

	private JsonNode getAuthorizationJsonNode() throws ModuleException {
		HttpRequestBuilder httpRequestBuilder = HttpRequest.builder();

		HttpResponse httpResponse = null;

		try {
			httpResponse = httpClient.send(
				httpRequestBuilder.addHeader(
					"Content-Type", "application/x-www-form-urlencoded"
				).method(
					HttpConstants.Method.POST
				).queryParams(
					queryParams
				).uri(
					oAuth2AccessTokenURI
				).build(),
				10000, true, null);
		}
		catch (IOException ioException) {
			logger.error(ioException.getMessage(), ioException);

			throw new ModuleException(
				ioException.getMessage(), LiferayError.EXECUTION, ioException);
		}
		catch (TimeoutException timeoutException) {
			logger.error(timeoutException.getMessage(), timeoutException);

			throw new ModuleException(
				timeoutException.getMessage(), LiferayError.CONNECTION_TIMEOUT,
				timeoutException);
		}

		if (httpResponse == null) {
			String message =
				"Unresponsive authorization server's OAuth 2.0 endpoint";

			logger.error(message);

			throw new ModuleException(message, LiferayError.OAUTH2_ERROR);
		}
		else if (httpResponse.getStatusCode() != 200) {
			HttpEntity httpEntity = httpResponse.getEntity();

			String message = String.format(
				"Unable to fetch access token from authorization server. " +
					"Request failed with status %d (%s) and message %s",
				httpResponse.getStatusCode(), httpResponse.getReasonPhrase(),
				IOUtils.toString(httpEntity.getContent()));

			logger.error(message);

			throw new ModuleException(message, LiferayError.OAUTH2_ERROR);
		}

		JsonNodeReader jsonNodeReader = new JsonNodeReader();

		return jsonNodeReader.fromHttpResponse(httpResponse);
	}

	private String getOAuth2AccessTokenURI(String openAPISpecPath)
		throws MalformedURLException {

		OASURLParser oasURLParser = new OASURLParser(openAPISpecPath);

		return oasURLParser.getAuthorityWithScheme() + OAUTH2_ENDPOINT;
	}

	private static final String OAUTH2_ENDPOINT = "/o/oauth2/token";

	private static final Logger logger = LoggerFactory.getLogger(
		OAuth2Authentication.class);

	private final HttpClient httpClient;
	private final String oAuth2AccessTokenURI;
	private final MultiMap<String, String> queryParams = new MultiMap<>();

}