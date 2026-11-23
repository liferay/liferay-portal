/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test.util;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Leslie Wong
 */
public class ContactsEngineHttpServer implements AutoCloseable {

	public ContactsEngineHttpServer(
			ContactsEngineClient contactsEngineClient, String path, String rel)
		throws IOException {

		_contactsEngineClient = contactsEngineClient;

		_httpServer = HttpServer.create(
			new InetSocketAddress("127.0.0.1", 0), 0);

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		String url = "http://127.0.0.1:" + inetSocketAddress.getPort();

		_httpServer.createContext(
			"/",
			httpExchange -> _writeJSON(
				httpExchange,
				JSONUtil.put(
					"_links",
					JSONUtil.put(
						rel, JSONUtil.put("href", url + path + "/{id}"))
				).toString(),
				HttpURLConnection.HTTP_OK));

		_httpServer.createContext(
			path,
			httpExchange -> {
				URI uri = httpExchange.getRequestURI();

				String json = _jsons.get(
					StringUtil.extractLast(uri.getPath(), CharPool.SLASH));

				if (json == null) {
					_writeJSON(
						httpExchange, "{}", HttpURLConnection.HTTP_NOT_FOUND);
				}
				else {
					_writeJSON(httpExchange, json, HttpURLConnection.HTTP_OK);
				}
			});

		_httpServer.start();

		_contactsEngineClient.setEngineURL(url);
	}

	public void addJSON(String id, String json) {
		_jsons.put(id, json);
	}

	@Override
	public void close() {
		_contactsEngineClient.setEngineURL(null);

		_httpServer.stop(0);
	}

	private void _writeJSON(
			HttpExchange httpExchange, String json, int statusCode)
		throws IOException {

		Headers headers = httpExchange.getResponseHeaders();

		headers.set("Content-Type", "application/hal+json");

		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		httpExchange.sendResponseHeaders(statusCode, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private final ContactsEngineClient _contactsEngineClient;
	private final HttpServer _httpServer;
	private final Map<String, String> _jsons = new ConcurrentHashMap<>();

}