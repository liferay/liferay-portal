/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.connection;

import com.liferay.mule.internal.connection.authentication.BasicAuthentication;
import com.liferay.mule.internal.connection.authentication.HttpAuthentication;
import com.liferay.mule.internal.connection.authentication.OAuth2Authentication;
import com.liferay.mule.internal.error.LiferayError;
import com.liferay.mule.internal.oas.OASURLParser;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.HttpService;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.client.HttpClientConfiguration;
import org.mule.runtime.http.api.client.HttpClientFactory;
import org.mule.runtime.http.api.client.proxy.ProxyConfig;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.entity.multipart.HttpPart;
import org.mule.runtime.http.api.domain.entity.multipart.MultipartHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.request.HttpRequestBuilder;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matija Petanjek
 */
public final class LiferayConnection {

	public static LiferayConnection withBasicAuthentication(
			HttpService httpService, String openApiSpecPath, String userName,
			String password, ProxyConfig proxyConfig)
		throws ConnectionException {

		return new LiferayConnection(
			httpService, openApiSpecPath,
			new BasicAuthentication(userName, password), proxyConfig);
	}

	public static LiferayConnection withOAuth2Authentication(
			HttpService httpService, String openApiSpecPath, String consumerKey,
			String consumerSecret, ProxyConfig proxyConfig)
		throws ConnectionException {

		return new LiferayConnection(
			httpService, openApiSpecPath, consumerKey, consumerSecret,
			proxyConfig);
	}

	public HttpResponse delete(ResourceContext resourceContext)
		throws ModuleException {

		return send(
			HttpConstants.Method.DELETE, resourceContext.getJaxRSAppBase(),
			resourceContext.getEndpoint(), resourceContext.getPathParams(),
			resourceContext.getQueryParams(), resourceContext.getContentType(),
			resourceContext.getConnectionTimeout(),
			resourceContext.getInputStream(), resourceContext.getBytes());
	}

	public HttpResponse get(ResourceContext resourceContext)
		throws ModuleException {

		return send(
			HttpConstants.Method.GET, resourceContext.getJaxRSAppBase(),
			resourceContext.getEndpoint(), resourceContext.getPathParams(),
			resourceContext.getQueryParams(), resourceContext.getContentType(),
			resourceContext.getConnectionTimeout(),
			resourceContext.getInputStream(), resourceContext.getBytes());
	}

	public HttpResponse getOpenAPISpecHttpResponse()
		throws IOException, TimeoutException {

		return httpClient.send(
			getHttpRequest(
				HttpConstants.Method.GET, openAPISpecPath, new MultiMap<>(),
				"application/json", null, null),
			10000, true, null);
	}

	public void invalidate() {
		httpClient.stop();
	}

	public HttpResponse patch(ResourceContext resourceContext)
		throws ModuleException {

		return send(
			HttpConstants.Method.PATCH, resourceContext.getJaxRSAppBase(),
			resourceContext.getEndpoint(), resourceContext.getPathParams(),
			resourceContext.getQueryParams(), resourceContext.getContentType(),
			resourceContext.getConnectionTimeout(),
			resourceContext.getInputStream(), resourceContext.getBytes());
	}

	public HttpResponse post(ResourceContext resourceContext)
		throws ModuleException {

		return send(
			HttpConstants.Method.POST, resourceContext.getJaxRSAppBase(),
			resourceContext.getEndpoint(), resourceContext.getPathParams(),
			resourceContext.getQueryParams(), resourceContext.getContentType(),
			resourceContext.getConnectionTimeout(),
			resourceContext.getInputStream(), resourceContext.getBytes());
	}

	public HttpResponse put(ResourceContext resourceContext)
		throws ModuleException {

		return send(
			HttpConstants.Method.PUT, resourceContext.getJaxRSAppBase(),
			resourceContext.getEndpoint(), resourceContext.getPathParams(),
			resourceContext.getQueryParams(), resourceContext.getContentType(),
			resourceContext.getConnectionTimeout(),
			resourceContext.getInputStream(), resourceContext.getBytes());
	}

	private LiferayConnection(
			HttpService httpService, String openApiSpecPath,
			BasicAuthentication basicAuthentication, ProxyConfig proxyConfig)
		throws ConnectionException {

		openAPISpecPath = openApiSpecPath;
		oasURLParser = getOASURLParser(openApiSpecPath);

		httpAuthentication = basicAuthentication;

		initHttpClient(httpService, proxyConfig);
	}

	private LiferayConnection(
			HttpService httpService, String openApiSpecPath, String consumerKey,
			String consumerSecret, ProxyConfig proxyConfig)
		throws ConnectionException {

		openAPISpecPath = openApiSpecPath;
		oasURLParser = getOASURLParser(openApiSpecPath);

		initHttpClient(httpService, proxyConfig);

		try {
			httpAuthentication = new OAuth2Authentication(
				consumerKey, consumerSecret, httpClient, openAPISpecPath);
		}
		catch (MalformedURLException malformedURLException) {
			throw new ConnectionException(malformedURLException);
		}
	}

	private HttpRequest getHttpRequest(
			HttpConstants.Method method, String uri,
			MultiMap<String, String> queryParams, String contentType,
			InputStream inputStream, byte[] bytes)
		throws ModuleException {

		HttpRequestBuilder httpRequestBuilder = HttpRequest.builder();

		httpRequestBuilder.addHeader(
			"Authorization", httpAuthentication.getAuthorizationHeader()
		).addHeader(
			"Content-Type", contentType
		).method(
			method
		).queryParams(
			queryParams
		).uri(
			uri
		);

		if (inputStream != null) {
			httpRequestBuilder.entity(new InputStreamHttpEntity(inputStream));
		}
		else if (bytes != null) {
			httpRequestBuilder.entity(
				new MultipartHttpEntity(
					Arrays.asList(
						new HttpPart(
							"file", "import.json", bytes, "application/json",
							bytes.length))));
		}

		return httpRequestBuilder.build();
	}

	private OASURLParser getOASURLParser(String openApiSpecPath)
		throws ConnectionException {

		try {
			return new OASURLParser(openApiSpecPath);
		}
		catch (MalformedURLException malformedURLException) {
			throw new ConnectionException(malformedURLException);
		}
	}

	private void initHttpClient(
		HttpService httpService, ProxyConfig proxyConfig) {

		HttpClientConfiguration.Builder builder =
			new HttpClientConfiguration.Builder();

		if (proxyConfig != null) {
			builder.setProxyConfig(proxyConfig);
		}

		builder.setName("Liferay Http Client");

		HttpClientFactory httpClientFactory = httpService.getClientFactory();

		httpClient = httpClientFactory.create(builder.build());

		httpClient.start();
	}

	private void logHttpRequest(
		long connectionTimeout, HttpConstants.Method method,
		Map<String, String> pathParams, MultiMap<String, String> queryParams,
		String uri) {

		logger.debug(
			"Sending {} request to {} with path parameters {}, query " +
				"parameters {} and connection timeout {} ms",
			method, uri, pathParams, queryParams, connectionTimeout);
	}

	private String resolvePathParams(
		String endpoint, Map<String, String> pathParams) {

		for (Map.Entry<String, String> pathParam : pathParams.entrySet()) {
			endpoint = endpoint.replace(
				"{" + pathParam.getKey() + "}", pathParam.getValue());
		}

		return endpoint;
	}

	private HttpResponse send(
			HttpConstants.Method method, String jaxRSAppBase, String endpoint,
			Map<String, String> pathParams,
			MultiMap<String, String> queryParams, String contentType,
			long connectionTimeout, InputStream inputStream, byte[] bytes)
		throws ModuleException {

		if (jaxRSAppBase == null) {
			jaxRSAppBase = oasURLParser.getJaxRSAppBase();
		}

		String uri =
			oasURLParser.getServerBaseURL(jaxRSAppBase) +
				resolvePathParams(endpoint, pathParams);

		HttpRequest httpRequest = getHttpRequest(
			method, uri, queryParams, contentType, inputStream, bytes);

		logHttpRequest(connectionTimeout, method, pathParams, queryParams, uri);

		try {
			return httpClient.send(
				httpRequest, (int)connectionTimeout, true, null);
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
	}

	private static final Logger logger = LoggerFactory.getLogger(
		LiferayConnection.class);

	private final HttpAuthentication httpAuthentication;
	private HttpClient httpClient;
	private final OASURLParser oasURLParser;
	private final String openAPISpecPath;

}