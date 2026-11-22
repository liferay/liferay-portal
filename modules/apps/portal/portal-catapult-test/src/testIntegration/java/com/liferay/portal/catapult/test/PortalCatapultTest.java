/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.catapult.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.catapult.PortalCatapult;
import com.liferay.portal.catapult.PortalCatapultHeaderContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.BundleContextTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class PortalCatapultTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testLaunch() throws Exception {
		_testLaunchWithContributedHeaders();
		_testLaunchWithCrossOriginRedirect();
		_testLaunchWithErrorResponse();
		_testLaunchWithSameOriginRedirect();
		_testLaunchWithoutContributedHeaders();
	}

	private OAuth2Application _addOrUpdateOAuth2Application(String homePageURL)
		throws Exception {

		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());

		return _oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
			RandomTestUtil.randomString(), user.getUserId(), user.getLogin(),
			ListUtil.fromArray(
				GrantType.CLIENT_CREDENTIALS, GrantType.JWT_BEARER),
			"client_secret_post", user.getUserId(),
			RandomTestUtil.randomString(), ClientProfile.HEADLESS_SERVER.id(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ListUtil.fromArray("token.introspection"), homePageURL, 0, null,
			RandomTestUtil.randomString(),
			"http://" + RandomTestUtil.randomString() + ".com",
			Collections.emptyList(), false, Collections.emptyList(), true,
			new ServiceContext());
	}

	private void _launch(
			ClientExtensionHttpServer clientExtensionHttpServer,
			Http.Method method, JSONObject payloadJSONObject,
			PortalCatapultHeaderContributor portalCatapultHeaderContributor)
		throws Exception {

		BundleContext bundleContext = BundleContextTestUtil.getBundleContext(
			PortalCatapultTest.class);

		ServiceRegistration<PortalCatapultHeaderContributor>
			serviceRegistration = bundleContext.registerService(
				PortalCatapultHeaderContributor.class,
				portalCatapultHeaderContributor, null);

		try {
			OAuth2Application oAuth2Application = _addOrUpdateOAuth2Application(
				clientExtensionHttpServer._getURL());

			try {
				Future<byte[]> future = _portalCatapult.launch(
					oAuth2Application.getCompanyId(), method,
					oAuth2Application.getExternalReferenceCode(),
					payloadJSONObject, "/resource",
					TestPropsValues.getUserId());

				Assert.assertEquals(
					"{}",
					new String(
						future.get(1, TimeUnit.MINUTES),
						StandardCharsets.UTF_8));
			}
			finally {
				_oAuth2ApplicationLocalService.deleteOAuth2Application(
					oAuth2Application);
			}
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testLaunchWithContributedHeaders() throws Exception {
		try (ClientExtensionHttpServer clientExtensionHttpServer =
				new ClientExtensionHttpServer()) {

			String value = RandomTestUtil.randomString();

			JSONObject payloadJSONObject = JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			_launch(
				clientExtensionHttpServer, Http.Method.POST, payloadJSONObject,
				(companyId, headers, homePageURL, location,
				 oAuth2ApplicationFeatures, userId) -> {

					headers.put(HttpHeaders.AUTHORIZATION, value);
					headers.put(HttpHeaders.CONTENT_TYPE, value);
					headers.put(_HEADER_NAME, value);
					headers.put("authorization", value);
					headers.put("content-type", value);
				});

			List<String> headerValues = clientExtensionHttpServer._getHeaders(
				_HEADER_NAME);

			Assert.assertEquals(value, headerValues.get(0));
			Assert.assertEquals(
				headerValues.toString(), 1, headerValues.size());

			List<String> authorizations = clientExtensionHttpServer._getHeaders(
				HttpHeaders.AUTHORIZATION);

			Assert.assertEquals(
				authorizations.toString(), 1, authorizations.size());

			String authorization = authorizations.get(0);

			Assert.assertTrue(
				authorization, authorization.startsWith("Bearer "));

			List<String> contentTypes = clientExtensionHttpServer._getHeaders(
				HttpHeaders.CONTENT_TYPE);

			Assert.assertEquals(
				ContentTypes.APPLICATION_JSON, contentTypes.get(0));
			Assert.assertEquals(
				contentTypes.toString(), 1, contentTypes.size());

			Assert.assertEquals(
				payloadJSONObject.toString(),
				clientExtensionHttpServer._getRequestBody());
		}
	}

	private void _testLaunchWithCrossOriginRedirect() throws Exception {
		try (ClientExtensionHttpServer redirectClientExtensionHttpServer =
				new ClientExtensionHttpServer();

			ClientExtensionHttpServer clientExtensionHttpServer =
				new ClientExtensionHttpServer(
					redirectClientExtensionHttpServer._getURL() + "/resource",
					RandomTestUtil.randomString(),
					HttpURLConnection.HTTP_MOVED_TEMP);

			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.catapult.internal.PortalCatapultImpl",
				LoggerTestUtil.WARN)) {

			String value = RandomTestUtil.randomString();

			Assert.assertThrows(
				ExecutionException.class,
				() -> _launch(
					clientExtensionHttpServer, Http.Method.GET, null,
					(companyId, headers, homePageURL, location,
					 oAuth2ApplicationFeatures, userId) -> headers.put(
						_HEADER_NAME, value)));

			List<String> headerValues = clientExtensionHttpServer._getHeaders(
				_HEADER_NAME);

			Assert.assertEquals(value, headerValues.get(0));

			Assert.assertFalse(redirectClientExtensionHttpServer._hasHeaders());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to follow the redirect to ",
					redirectClientExtensionHttpServer._getURL(),
					"/resource because it leaves the origin of ",
					clientExtensionHttpServer._getURL(), "/resource"),
				logEntry.getMessage());
		}
	}

	private void _testLaunchWithErrorResponse() throws Exception {
		String responseBody = RandomTestUtil.randomString();

		try (ClientExtensionHttpServer clientExtensionHttpServer =
				new ClientExtensionHttpServer(
					null, responseBody, HttpURLConnection.HTTP_CONFLICT)) {

			ExecutionException executionException = Assert.assertThrows(
				ExecutionException.class,
				() -> _launch(
					clientExtensionHttpServer, Http.Method.GET, null,
					(companyId, headers, homePageURL, location,
					 oAuth2ApplicationFeatures, userId) -> {
					}));

			Throwable throwable = executionException.getCause();

			Assert.assertEquals(responseBody, throwable.getMessage());
		}
	}

	private void _testLaunchWithSameOriginRedirect() throws Exception {
		try (ClientExtensionHttpServer clientExtensionHttpServer =
				new ClientExtensionHttpServer(
					"/" + RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					HttpURLConnection.HTTP_MOVED_TEMP)) {

			String value = RandomTestUtil.randomString();

			_launch(
				clientExtensionHttpServer, Http.Method.GET, null,
				(companyId, headers, homePageURL, location,
				 oAuth2ApplicationFeatures, userId) -> headers.put(
					_HEADER_NAME, value));

			List<String> headerValues = clientExtensionHttpServer._getHeaders(
				_HEADER_NAME);

			Assert.assertEquals(value, headerValues.get(0));

			Assert.assertThrows(
				ExecutionException.class,
				() -> _launch(
					clientExtensionHttpServer, Http.Method.POST, null,
					(companyId, headers, homePageURL, location,
					 oAuth2ApplicationFeatures, userId) -> {
					}));
		}
	}

	private void _testLaunchWithoutContributedHeaders() throws Exception {
		try (ClientExtensionHttpServer clientExtensionHttpServer =
				new ClientExtensionHttpServer()) {

			_launch(
				clientExtensionHttpServer, Http.Method.GET, null,
				(companyId, headers, homePageURL, location,
				 oAuth2ApplicationFeatures, userId) -> {
				});

			List<String> authorizations = clientExtensionHttpServer._getHeaders(
				HttpHeaders.AUTHORIZATION);

			Assert.assertEquals(
				authorizations.toString(), 1, authorizations.size());

			String authorization = authorizations.get(0);

			Assert.assertTrue(
				authorization, authorization.startsWith("Bearer "));

			Assert.assertNull(
				clientExtensionHttpServer._getHeaders(_HEADER_NAME));
		}
	}

	private static final String _HEADER_NAME = RandomTestUtil.randomString();

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Inject
	private PortalCatapult _portalCatapult;

	private static class ClientExtensionHttpServer implements AutoCloseable {

		public ClientExtensionHttpServer() throws IOException {
			this(null, "{}", HttpURLConnection.HTTP_OK);
		}

		public ClientExtensionHttpServer(
				String redirectURL, String responseBody, int statusCode)
			throws IOException {

			_httpServer = HttpServer.create(
				new InetSocketAddress("127.0.0.1", 0), 0);

			_httpServer.createContext(
				"/",
				httpExchange -> _handle(
					httpExchange, null, "{}", HttpURLConnection.HTTP_OK));
			_httpServer.createContext(
				"/resource",
				httpExchange -> _handle(
					httpExchange, redirectURL, responseBody, statusCode));

			_httpServer.start();

			InetSocketAddress inetSocketAddress = _httpServer.getAddress();

			_url = "http://127.0.0.1:" + inetSocketAddress.getPort();
		}

		@Override
		public void close() {
			_httpServer.stop(0);
		}

		private List<String> _getHeaders(String name) {
			if (_headers == null) {
				return null;
			}

			return _headers.get(name);
		}

		private String _getRequestBody() {
			return _requestBody;
		}

		private String _getURL() {
			return _url;
		}

		private void _handle(
				HttpExchange httpExchange, String redirectURL,
				String responseBody, int statusCode)
			throws IOException {

			_headers = httpExchange.getRequestHeaders();

			try (InputStream inputStream = httpExchange.getRequestBody()) {
				_requestBody = new String(
					inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}

			byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);

			Headers responseHeaders = httpExchange.getResponseHeaders();

			responseHeaders.set(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

			if (redirectURL != null) {
				responseHeaders.set(HttpHeaders.LOCATION, redirectURL);
			}

			httpExchange.sendResponseHeaders(statusCode, bytes.length);

			try (OutputStream outputStream = httpExchange.getResponseBody()) {
				outputStream.write(bytes);
			}
		}

		private boolean _hasHeaders() {
			if (_headers != null) {
				return true;
			}

			return false;
		}

		private volatile Headers _headers;
		private final HttpServer _httpServer;
		private volatile String _requestBody;
		private final String _url;

	}

}