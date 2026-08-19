/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.test.util;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.util.ContentTypes;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;

import java.nio.charset.StandardCharsets;

/**
 * @author Eudaldo Alonso
 */
public class AnalyticsCloudHttpServer implements AutoCloseable {

	public AnalyticsCloudHttpServer(
			String path, UnsafeSupplier<String, Exception> unsafeSupplier)
		throws IOException {

		_httpServer = HttpServer.create(
			new InetSocketAddress("127.0.0.1", 0), 0);

		_httpServer.createContext(
			path,
			httpExchange -> {
				URI uri = httpExchange.getRequestURI();

				_location = uri.toString();

				try {
					String content = unsafeSupplier.get();

					_writeBytes(
						content.getBytes(StandardCharsets.UTF_8), httpExchange,
						HttpURLConnection.HTTP_OK);
				}
				catch (Exception exception) {
					String message = String.valueOf(exception.getMessage());

					_writeBytes(
						message.getBytes(StandardCharsets.UTF_8), httpExchange,
						HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			});

		_httpServer.start();

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		_url = "http://127.0.0.1:" + inetSocketAddress.getPort();
	}

	@Override
	public void close() {
		_httpServer.stop(0);
	}

	public String getLocation() {
		return _location;
	}

	public String getURL() {
		return _url;
	}

	private void _writeBytes(
			byte[] bytes, HttpExchange httpExchange, int responseCode)
		throws IOException {

		Headers responseHeaders = httpExchange.getResponseHeaders();

		responseHeaders.set("Content-Type", ContentTypes.APPLICATION_JSON);

		httpExchange.sendResponseHeaders(responseCode, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private final HttpServer _httpServer;
	private volatile String _location;
	private final String _url;

}