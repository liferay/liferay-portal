/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3.service;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

/**
 * @author Allen Ziegenfus
 */
public class BaseServiceTest {

	@BeforeClass
	public static void setUpClass() {
		_clientAndServer = ClientAndServer.startClientAndServer(0);
	}

	@AfterClass
	public static void tearDownClass() {
		_clientAndServer.stop();
	}

	@Test
	public void testDoGet() {
		_testDoGet(null, 403);
	}

	@Test
	public void testDoGetReusesConnection() throws Exception {
		HttpServer httpServer = HttpServer.create(
			new InetSocketAddress("localhost", 0), 0);
		Set<Integer> remotePorts = ConcurrentHashMap.newKeySet();

		httpServer.createContext(
			"/",
			httpExchange -> {
				InetSocketAddress inetSocketAddress =
					httpExchange.getRemoteAddress();

				remotePorts.add(inetSocketAddress.getPort());

				byte[] bytes = "{}".getBytes(StandardCharsets.UTF_8);

				httpExchange.sendResponseHeaders(200, bytes.length);

				try (OutputStream outputStream =
						httpExchange.getResponseBody()) {

					outputStream.write(bytes);
				}
			});

		httpServer.start();

		try {
			InetSocketAddress inetSocketAddress = httpServer.getAddress();

			URI uri = URI.create(
				"http://localhost:" + inetSocketAddress.getPort());

			TestService testService1 = new TestService();

			testService1.doGet(uri);
			testService1.doGet(uri);

			TestService testService2 = new TestService();

			testService2.doGet(uri);

			Assert.assertEquals(remotePorts.toString(), 1, remotePorts.size());
		}
		finally {
			httpServer.stop(0);
		}
	}

	@Test
	public void testDoGetReusesWebClient() {
		TestService testService = new TestService();

		String path = "/" + UUID.randomUUID();

		String uri = _getEndpoint("GET", path, "{}", 200);

		testService.doGet(URI.create(uri));
		testService.doGet(URI.create(uri));

		List<ExchangeFilterFunction> exchangeFilterFunctions =
			testService.getExchangeFilterFunctions();

		Assert.assertEquals(
			exchangeFilterFunctions.toString(), 2,
			exchangeFilterFunctions.size());
		Assert.assertSame(
			exchangeFilterFunctions.get(0), exchangeFilterFunctions.get(1));
	}

	@Test
	public void testDoGetWithNonstandardStatusCode() {
		_testDoGet(null, 499);
	}

	@Test
	public void testDoGetWithResponseBody() {
		_testDoGet("{\"message\": \"Order is not found\"}", 400);
	}

	@Test
	public void testDoGetWithServerError() {
		_testDoGet(null, 500);
	}

	@Test
	public void testDoGetWithSuccessfulStatusCode() {
		TestService testService = new TestService();

		String path = "/" + UUID.randomUUID();

		String uri = _getEndpoint("GET", path, "{\"total\": 1}", 200);

		Assert.assertEquals(
			"{\"total\": 1}", testService.doGet(URI.create(uri)));
	}

	@Test
	public void testDoPostWithClientError() {
		try {
			TestService testService = new TestService();

			String path = "/" + UUID.randomUUID();

			String uri = _getEndpoint("POST", path, null, 403);

			testService.doPost("{}", URI.create(uri));

			Assert.fail();
		}
		catch (WebClientResponseException webClientResponseException) {
			HttpStatusCode httpStatusCode =
				webClientResponseException.getStatusCode();

			Assert.assertEquals(403, httpStatusCode.value());
		}
	}

	private String _getEndpoint(
		String httpMethod, String path, String responseBody, int statusCode) {

		HttpResponse httpResponse = HttpResponse.response(
		).withStatusCode(
			statusCode
		);

		if (responseBody != null) {
			httpResponse.withBody(responseBody);
		}

		new MockServerClient(
			"localhost", _clientAndServer.getPort()
		).when(
			HttpRequest.request(
			).withMethod(
				httpMethod
			).withPath(
				path
			),
			Times.unlimited()
		).respond(
			httpResponse
		);

		return "http://localhost:" + _clientAndServer.getPort() + path;
	}

	private void _testDoGet(String responseBody, int statusCode) {
		try {
			TestService testService = new TestService();

			String path = "/" + UUID.randomUUID();

			String uri = _getEndpoint("GET", path, responseBody, statusCode);

			testService.doGet(URI.create(uri));

			Assert.fail();
		}
		catch (WebClientResponseException webClientResponseException) {
			HttpStatusCode httpStatusCode =
				webClientResponseException.getStatusCode();

			Assert.assertEquals(statusCode, httpStatusCode.value());

			if (responseBody != null) {
				Assert.assertEquals(
					responseBody,
					webClientResponseException.getResponseBodyAsString());
			}
		}
	}

	private static ClientAndServer _clientAndServer;

	private static class TestService extends BaseService {

		public String doGet(URI uri) {
			return get(Collections.emptyMap(), uri);
		}

		public String doPost(String body, URI uri) {
			return post(body, Collections.emptyMap(), uri);
		}

		public List<ExchangeFilterFunction> getExchangeFilterFunctions() {
			return _exchangeFilterFunctions;
		}

		@Override
		protected ExchangeFilterFunction getWebClientExchangeFilterFunction() {
			ExchangeFilterFunction exchangeFilterFunction =
				super.getWebClientExchangeFilterFunction();

			return new ExchangeFilterFunction() {

				@Override
				public Mono<ClientResponse> filter(
					ClientRequest clientRequest,
					ExchangeFunction exchangeFunction) {

					_exchangeFilterFunctions.add(this);

					return exchangeFilterFunction.filter(
						clientRequest, exchangeFunction);
				}

			};
		}

		private final List<ExchangeFilterFunction> _exchangeFilterFunctions =
			new ArrayList<>();

	}

}