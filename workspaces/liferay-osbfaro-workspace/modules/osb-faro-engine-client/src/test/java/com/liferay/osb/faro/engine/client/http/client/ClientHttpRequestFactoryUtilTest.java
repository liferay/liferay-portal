/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * @author Leslie Wong
 */
public class ClientHttpRequestFactoryUtilTest {

	@Test
	public void testCreateHttpComponentsClientHttpRequestFactory()
		throws Exception {

		long connectTimeoutMillis = RandomTestUtil.randomInt();
		int maxConnections = RandomTestUtil.randomInt();
		long responseTimeoutMillis = RandomTestUtil.randomInt();

		HttpComponentsClientHttpRequestFactory
			httpComponentsClientHttpRequestFactory =
				ClientHttpRequestFactoryUtil.
					createHttpComponentsClientHttpRequestFactory(
						connectTimeoutMillis, maxConnections,
						responseTimeoutMillis);

		Configurable configurable =
			(Configurable)
				httpComponentsClientHttpRequestFactory.getHttpClient();

		RequestConfig requestConfig = configurable.getConfig();

		Timeout connectionRequestTimeout =
			requestConfig.getConnectionRequestTimeout();

		Assert.assertEquals(
			connectTimeoutMillis, connectionRequestTimeout.toMilliseconds());

		Timeout responseTimeout = requestConfig.getResponseTimeout();

		Assert.assertEquals(
			responseTimeoutMillis, responseTimeout.toMilliseconds());

		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
			ReflectionTestUtil.getFieldValue(configurable, "connManager");

		Assert.assertEquals(
			maxConnections,
			poolingHttpClientConnectionManager.getDefaultMaxPerRoute());
		Assert.assertEquals(
			maxConnections, poolingHttpClientConnectionManager.getMaxTotal());

		ConnectionConfig connectionConfig = ReflectionTestUtil.invoke(
			poolingHttpClientConnectionManager, "resolveConnectionConfig",
			new Class<?>[] {HttpRoute.class},
			new HttpRoute(
				new HttpHost(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomInt(1, 65535))));

		Timeout connectTimeout = connectionConfig.getConnectTimeout();

		Assert.assertEquals(
			connectTimeoutMillis, connectTimeout.toMilliseconds());

		TimeValue timeValue = connectionConfig.getValidateAfterInactivity();

		Assert.assertEquals(2000, timeValue.toMilliseconds());

		httpComponentsClientHttpRequestFactory.destroy();
	}

}