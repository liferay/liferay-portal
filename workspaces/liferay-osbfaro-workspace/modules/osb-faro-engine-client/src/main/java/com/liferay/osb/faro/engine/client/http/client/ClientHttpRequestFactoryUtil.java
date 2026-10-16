/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import java.net.URI;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author Leslie Wong
 */
public class ClientHttpRequestFactoryUtil {

	public static HttpComponentsClientHttpRequestFactory
		createHttpComponentsClientHttpRequestFactory() {

		return new GetHttpComponentsClientHttpRequestFactory();
	}

	public static HttpComponentsClientHttpRequestFactory
		createHttpComponentsClientHttpRequestFactory(
			long connectTimeoutMillis, int maxConnections,
			long responseTimeoutMillis) {

		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		PoolingHttpClientConnectionManagerBuilder
			poolingHttpClientConnectionManagerBuilder =
				PoolingHttpClientConnectionManagerBuilder.create();

		ConnectionConfig.Builder connectionConfigBuilder =
			ConnectionConfig.custom();

		connectionConfigBuilder.setConnectTimeout(
			connectTimeoutMillis, TimeUnit.MILLISECONDS);
		connectionConfigBuilder.setValidateAfterInactivity(2, TimeUnit.SECONDS);

		poolingHttpClientConnectionManagerBuilder.setDefaultConnectionConfig(
			connectionConfigBuilder.build());

		poolingHttpClientConnectionManagerBuilder.setMaxConnPerRoute(
			maxConnections);
		poolingHttpClientConnectionManagerBuilder.setMaxConnTotal(
			maxConnections);
		poolingHttpClientConnectionManagerBuilder.useSystemProperties();

		httpClientBuilder.setConnectionManager(
			poolingHttpClientConnectionManagerBuilder.build());

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

		requestConfigBuilder.setConnectionRequestTimeout(
			connectTimeoutMillis, TimeUnit.MILLISECONDS);
		requestConfigBuilder.setResponseTimeout(
			responseTimeoutMillis, TimeUnit.MILLISECONDS);

		httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

		httpClientBuilder.useSystemProperties();

		return new GetHttpComponentsClientHttpRequestFactory(
			httpClientBuilder.build());
	}

	private static class GetHttpComponentsClientHttpRequestFactory
		extends HttpComponentsClientHttpRequestFactory {

		@Override
		protected ClassicHttpRequest createHttpUriRequest(
			HttpMethod httpMethod, URI uri) {

			if (httpMethod == HttpMethod.GET) {
				return new HttpUriRequestBase(HttpMethod.GET.name(), uri);
			}

			return super.createHttpUriRequest(httpMethod, uri);
		}

		private GetHttpComponentsClientHttpRequestFactory() {
		}

		private GetHttpComponentsClientHttpRequestFactory(
			HttpClient httpClient) {

			super(httpClient);
		}

	}

}