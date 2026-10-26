/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.connection;

import co.elastic.clients.transport.rest_client.RestClientTransport;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;

import org.elasticsearch.client.RestClient;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Felipe Lorenz
 */
public class RestClientTransportFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testNewRestClientTransport() throws Exception {
		_testNewRestClientTransport(180000, -1);
		_testNewRestClientTransport(180000, 0);
		_testNewRestClientTransport(180000, 600);
		_testNewRestClientTransport(60000, 60);
	}

	private ConnectionKeepAliveStrategy _getConnectionKeepAliveStrategy(
		CloseableHttpClient closeableHttpClient) {

		ClientExecChain clientExecChain = ReflectionTestUtil.getFieldValue(
			closeableHttpClient, "execChain");

		while (true) {
			clientExecChain = ReflectionTestUtil.getFieldValue(
				clientExecChain, "requestExecutor");

			if (clientExecChain instanceof MainClientExec) {
				return ReflectionTestUtil.getFieldValue(
					clientExecChain, "keepAliveStrategy");
			}
		}
	}

	private void _testNewRestClientTransport(
			long expectedKeepAliveDuration, long keepAliveTimeoutHeaderValue)
		throws Exception {

		try (RestClientTransport restClientTransport =
				_restClientTransportFactory.newRestClientTransport()) {

			RestClient restClient = restClientTransport.restClient();

			ConnectionKeepAliveStrategy connectionKeepAliveStrategy =
				_getConnectionKeepAliveStrategy(
					ReflectionTestUtil.getFieldValue(
						restClient.getHttpClient(), "val$closeableHttpClient"));

			HttpResponse httpResponse = new BasicHttpResponse(
				new BasicStatusLine(
					HttpVersion.HTTP_1_1, HttpStatus.SC_OK,
					RandomTestUtil.randomString()));

			if (keepAliveTimeoutHeaderValue > -1) {
				httpResponse.setHeader(
					HttpHeaders.KEEP_ALIVE,
					"timeout=" + keepAliveTimeoutHeaderValue);
			}

			Assert.assertEquals(
				expectedKeepAliveDuration,
				connectionKeepAliveStrategy.getKeepAliveDuration(
					httpResponse, new BasicHttpContext(null)));
		}
	}

	private final RestClientTransportFactory _restClientTransportFactory =
		new RestClientTransportFactory.Builder(
			new String[] {"http://" + RandomTestUtil.randomString()}
		).build();

}