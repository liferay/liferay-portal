/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;

import java.lang.reflect.Method;

import java.net.InetSocketAddress;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class OpenIdConnectHttpUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSend() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.apache.http.client.protocol.ResponseProcessCookies",
				LoggerTestUtil.WARN)) {

			HttpServer httpServer = HttpServer.create(
				new InetSocketAddress("127.0.0.1", 0), 0);

			try {
				httpServer.createContext(
					"/",
					httpExchange -> {
						httpExchange.getResponseHeaders(
						).add(
							"Content-Type", "application/json"
						);

						httpExchange.getResponseHeaders(
						).add(
							"Set-Cookie", _TRACER_COOKIE
						);

						byte[] bodyBytes = "{}".getBytes(
							StandardCharsets.UTF_8);

						httpExchange.sendResponseHeaders(200, bodyBytes.length);

						try (OutputStream outputStream =
								httpExchange.getResponseBody()) {

							outputStream.write(bodyBytes);
						}
					});

				httpServer.start();

				Bundle bundle = FrameworkUtil.getBundle(getClass());

				Bundle implBundle = null;

				for (Bundle candidateBundle :
						bundle.getBundleContext(
						).getBundles()) {

					if (Objects.equals(
							_IMPL_BUNDLE_SYMBOLIC_NAME,
							candidateBundle.getSymbolicName())) {

						implBundle = candidateBundle;

						break;
					}
				}

				Assert.assertNotNull(
					"Unable to locate the OpenID Connect impl bundle",
					implBundle);

				Class<?> openIdConnectHttpUtilClass = implBundle.loadClass(
					"com.liferay.portal.security.sso.openid.connect.internal." +
						"util.OpenIdConnectHttpUtil");

				Method sendMethod = openIdConnectHttpUtilClass.getMethod(
					"send", HTTPRequest.class);

				int port = httpServer.getAddress(
				).getPort();

				sendMethod.invoke(
					null,
					new HTTPRequest(
						HTTPRequest.Method.GET,
						new URL("http://127.0.0.1:" + port + "/userinfo")));
			}
			finally {
				httpServer.stop(0);
			}

			for (LogEntry logEntry : logCapture.getLogEntries()) {
				String message = logEntry.getMessage();

				if (message.contains("Invalid cookie header") &&
					message.contains(_TRACER_COOKIE_NAME)) {

					Assert.fail(message);
				}
			}
		}
	}

	private static final String _IMPL_BUNDLE_SYMBOLIC_NAME =
		"com.liferay.portal.security.sso.openid.connect.impl";

	private static final String _TRACER_COOKIE =
		"openidconnecthttputiltest-tracer=1; expires=Fri, 15 May 2026 " +
			"18:46:54 GMT; path=/; secure; samesite=none; httponly";

	private static final String _TRACER_COOKIE_NAME =
		"openidconnecthttputiltest-tracer";

}