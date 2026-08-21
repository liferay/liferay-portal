/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3.service;

import java.net.URI;

import java.util.Collections;
import java.util.UUID;

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
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
		_testDoGet(403, null);
	}

	@Test
	public void testDoGetWithNonstandardStatusCode() {
		_testDoGet(499, null);
	}

	@Test
	public void testDoGetWithResponseBody() {
		_testDoGet(400, "{\"message\": \"Order is not found\"}");
	}

	@Test
	public void testDoGetWithServerError() {
		_testDoGet(500, null);
	}

	@Test
	public void testDoGetWithSuccessfulStatusCode() {
		String path = "/" + UUID.randomUUID();

		String uri = _getEndpoint("GET", path, 200, "{\"total\": 1}");

		TestService testService = new TestService();

		Assert.assertEquals(
			"{\"total\": 1}", testService.doGet(URI.create(uri)));
	}

	@Test
	public void testDoPostWithClientError() {
		String path = "/" + UUID.randomUUID();

		String uri = _getEndpoint("POST", path, 403, null);

		try {
			TestService testService = new TestService();

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
		String httpMethod, String path, int statusCode, String responseBody) {

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

	private void _testDoGet(int statusCode, String responseBody) {
		String path = "/" + UUID.randomUUID();

		String uri = _getEndpoint("GET", path, statusCode, responseBody);

		try {
			TestService testService = new TestService();

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

	}

}