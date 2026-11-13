/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.http.invoker;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

/**
 * @author Drew Brokke
 */
@RunWith(Parameterized.class)
public class HttpInvokerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Parameterized.Parameters(name = "Testcase-{index}: testing {1} {0}")
	public static Iterable<Object[]> data() {
		List<Object[]> data = new ArrayList<>();

		HttpInvoker.HttpMethod[] httpMethods = {
			HttpInvoker.HttpMethod.GET, HttpInvoker.HttpMethod.DELETE,
			HttpInvoker.HttpMethod.PUT, HttpInvoker.HttpMethod.POST,
			HttpInvoker.HttpMethod.PATCH
		};
		String[] protocols = {"http", "https"};

		for (String protocol : protocols) {
			for (HttpInvoker.HttpMethod httpMethod : httpMethods) {
				data.add(new Object[] {httpMethod, protocol});
			}
		}

		return data;
	}

	public HttpInvokerTest(HttpInvoker.HttpMethod httpMethod, String protocol) {
		_httpMethod = httpMethod;
		_protocol = protocol;
	}

	@Test
	public void testGetHttpURLConnection() throws IOException {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		HttpURLConnection httpURLConnection = httpInvoker.getHttpURLConnection(
			_httpMethod, String.format("%s://foo.bar", _protocol));

		Assert.assertEquals(
			_protocol, _httpMethod.name(),
			httpURLConnection.getRequestMethod());
	}

	@Test
	public void testInvoke() throws Exception {
		Set<Integer> remotePorts = ConcurrentHashMap.newKeySet();

		HttpServer httpServer = HttpServer.create(
			new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);

		httpServer.createContext(
			"/",
			httpExchange -> {
				InetSocketAddress inetSocketAddress =
					httpExchange.getRemoteAddress();

				remotePorts.add(inetSocketAddress.getPort());

				try (InputStream inputStream = httpExchange.getRequestBody()) {
					inputStream.readAllBytes();
				}

				String path = String.valueOf(httpExchange.getRequestURI());

				byte[] bytes = path.getBytes();

				httpExchange.sendResponseHeaders(
					Integer.valueOf(path.substring(1)), bytes.length);

				try (OutputStream outputStream =
						httpExchange.getResponseBody()) {

					outputStream.write(bytes);
				}
			});

		httpServer.start();

		try {
			InetSocketAddress inetSocketAddress = httpServer.getAddress();

			for (int statusCode : new int[] {200, 300, 404, 500}) {
				for (int i = 0; i < 3; i++) {
					HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

					httpInvoker.httpMethod(_httpMethod);
					httpInvoker.path(
						String.format(
							"http://localhost:%s/%s",
							inetSocketAddress.getPort(), statusCode));

					HttpInvoker.HttpResponse httpResponse =
						httpInvoker.invoke();

					Assert.assertEquals(
						"/" + statusCode, httpResponse.getContent());
					Assert.assertEquals(
						statusCode, httpResponse.getStatusCode());
				}
			}

			Assert.assertEquals(remotePorts.toString(), 1, remotePorts.size());
		}
		finally {
			httpServer.stop(0);
		}
	}

	@Test
	public void testPathReplacement() {
		_testPathReplacement("$");
		_testPathReplacement("\\\\");
	}

	@Test
	public void testReadResponse() throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		Class<?> clazz = httpInvoker.getClass();

		Method method = clazz.getDeclaredMethod(
			"_readResponse", HttpURLConnection.class);

		method.setAccessible(true);

		byte[] bytes = (byte[])method.invoke(
			httpInvoker, Mockito.mock(HttpURLConnection.class));

		Assert.assertEquals(Arrays.toString(bytes), 0, bytes.length);
	}

	private void _testPathReplacement(String specialCharacter) {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.path("/api/users/{name}");
		httpInvoker.path("name", "value" + specialCharacter);

		Assert.assertEquals(
			"/api/users/value" + specialCharacter,
			ReflectionTestUtil.getFieldValue(httpInvoker, "_path"));
	}

	private static final Field _httpMethodField;

	static {
		try {
			_httpMethodField = HttpURLConnection.class.getDeclaredField(
				"method");

			_httpMethodField.setAccessible(true);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private final HttpInvoker.HttpMethod _httpMethod;
	private final String _protocol;

}