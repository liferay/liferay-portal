/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.util;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;

import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class EngineServiceURLUtilTest {

	@Before
	public void setUp() throws Exception {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_httpServer = HttpServer.create(new InetSocketAddress(0), 0);

		_httpServer.createContext(
			"/context",
			httpExchange -> {
				if (_count.incrementAndGet() <= _failures) {
					httpExchange.sendResponseHeaders(500, -1);

					return;
				}

				String json = JSONUtil.put(
					"environment", JSONUtil.put("EXTERNAL_URL", _EXTERNAL_URL)
				).toString();

				byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

				httpExchange.sendResponseHeaders(200, bytes.length);

				try (OutputStream outputStream =
						httpExchange.getResponseBody()) {

					outputStream.write(bytes);
				}
			});

		_httpServer.start();

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		_url = "http://localhost:" + inetSocketAddress.getPort();
	}

	@After
	public void tearDown() {
		_httpServer.stop(0);

		Map<String, String> externalURLs = ReflectionTestUtil.getFieldValue(
			EngineServiceURLUtil.class, "_externalURLs");

		externalURLs.clear();
	}

	@Test
	public void testGetExternalURL() {
		Assert.assertEquals(_EXTERNAL_URL, _getExternalURL());
		Assert.assertEquals(_EXTERNAL_URL, _getExternalURL());
		Assert.assertEquals(1, _count.get());
	}

	@Test
	public void testGetExternalURLWithFailure() {
		_failures = 1;

		Assert.assertEquals(_url, _getExternalURL());
		Assert.assertEquals(_EXTERNAL_URL, _getExternalURL());
		Assert.assertEquals(_EXTERNAL_URL, _getExternalURL());
		Assert.assertEquals(2, _count.get());
	}

	private String _getExternalURL() {
		return ReflectionTestUtil.invoke(
			EngineServiceURLUtil.class, "_getExternalURL",
			new Class<?>[] {String.class}, _url);
	}

	private static final String _EXTERNAL_URL = RandomTestUtil.randomString();

	private final AtomicInteger _count = new AtomicInteger();
	private int _failures;
	private HttpServer _httpServer;
	private String _url;

}