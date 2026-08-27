/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.connection;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.zip.GZIPOutputStream;

import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.CountResponse;

/**
 * @author Selena Aungst
 */
public class OpenSearchConnectionContentCompressionTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_httpServer = HttpServer.create(new InetSocketAddress(0), 0);

		HttpContext httpContext = _httpServer.createContext("/");

		httpContext.setHandler(this::_handle);

		_httpServer.start();
	}

	@After
	public void tearDown() {
		if (_openSearchConnection != null) {
			_openSearchConnection.close();
		}

		_httpServer.stop(0);
	}

	@Test
	public void testConnectWithCompressionDisabled() throws Exception {
		_testConnect(false, null);
	}

	@Test
	public void testConnectWithCompressionEnabled() throws Exception {
		_testConnect(true, _ENCODING_GZIP);
	}

	private String _getCountJSON() {
		return JSONUtil.put(
			"_shards",
			JSONUtil.put(
				"failed", 0
			).put(
				"successful", 1
			).put(
				"total", 1
			)
		).put(
			"count", _COUNT
		).toString();
	}

	private byte[] _gzip(byte[] bytes) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
				byteArrayOutputStream)) {

			gzipOutputStream.write(bytes);
		}

		return byteArrayOutputStream.toByteArray();
	}

	private void _handle(HttpExchange httpExchange) throws IOException {
		Headers requestHeaders = httpExchange.getRequestHeaders();

		_acceptEncoding = requestHeaders.getFirst(HttpHeaders.ACCEPT_ENCODING);

		Headers responseHeaders = httpExchange.getResponseHeaders();

		byte[] bytes = _getCountJSON().getBytes(StandardCharsets.UTF_8);

		if ((_acceptEncoding != null) &&
			_acceptEncoding.contains(_ENCODING_GZIP)) {

			bytes = _gzip(bytes);

			responseHeaders.set(HttpHeaders.CONTENT_ENCODING, _ENCODING_GZIP);
		}

		responseHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");

		httpExchange.sendResponseHeaders(HttpStatus.SC_OK, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private void _testConnect(
			boolean compressionEnabled, String expectedAcceptEncoding)
		throws Exception {

		OpenSearchConnection.Builder openSearchConnectionBuilder =
			new OpenSearchConnection.Builder();

		openSearchConnectionBuilder.compressionEnabled(compressionEnabled);

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		openSearchConnectionBuilder.networkHostAddresses(
			new String[] {"http://localhost:" + inetSocketAddress.getPort()});

		_openSearchConnection = openSearchConnectionBuilder.build();

		_openSearchConnection.connect();

		OpenSearchClient openSearchClient =
			_openSearchConnection.getOpenSearchClient();

		CountResponse countResponse = openSearchClient.count();

		Assert.assertEquals(expectedAcceptEncoding, _acceptEncoding);
		Assert.assertEquals(_COUNT, countResponse.count());
	}

	private static final long _COUNT = 7;

	private static final String _ENCODING_GZIP = "gzip";

	private String _acceptEncoding;
	private HttpServer _httpServer;
	private OpenSearchConnection _openSearchConnection;

}