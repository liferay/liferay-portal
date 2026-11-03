/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class LiferayCommerceRESTClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAuthenticate() throws Exception {
		_testAuthenticate();
		_testAuthenticateWithErrorResponse();
	}

	@Test
	public void testPostImportTask() throws Exception {
		_testPostImportTask();
		_testPostImportTaskWithErrorResponse();
	}

	@Test
	public void testPutOption() throws Exception {
		_testPutOption();
		_testPutOptionWithErrorResponse();
		_testPutOptionWithIOException();
	}

	private void _assertPIMConnectorException(
			String expectedMessage, UnsafeConsumer unsafeConsumer)
		throws Exception {

		try {
			unsafeConsumer.accept(_createLiferayCommerceRESTClient());

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				expectedMessage, pimConnectorException.getMessage());
		}
	}

	private LiferayCommerceRESTClient _createLiferayCommerceRESTClient() {
		return new LiferayCommerceRESTClient(
			"CLIENT", "SECRET", _http, JSONFactoryUtil.getJSONFactory(),
			"http://localhost:8080");
	}

	private void _mockHttp(int responseCode, JSONObject responseJSONObject)
		throws Exception {

		_optionsList.clear();

		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(
					0, Http.Options.class);

				_optionsList.add(options);

				Http.Response response = options.getResponse();

				response.setResponseCode(responseCode);

				return responseJSONObject.toString();
			}
		);
	}

	private void _testAuthenticate() throws Exception {
		_mockHttp(
			200,
			JSONUtil.put(
				"access_token", "TOKEN"
			).put(
				"expires_in", 600
			));

		LiferayCommerceRESTClient liferayCommerceRESTClient =
			_createLiferayCommerceRESTClient();

		liferayCommerceRESTClient.authenticate();

		Http.Options options = _optionsList.get(0);

		Assert.assertEquals(
			"http://localhost:8080/o/oauth2/token", options.getLocation());
		Assert.assertEquals(Http.Method.POST, options.getMethod());

		JSONArray jsonArray = liferayCommerceRESTClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		String log = jsonArray.toString();

		Assert.assertFalse(log, log.contains("SECRET"));
		Assert.assertFalse(log, log.contains("TOKEN"));

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		JSONObject requestJSONObject = jsonObject.getJSONObject("request");

		Assert.assertEquals("POST", requestJSONObject.getString("method"));

		JSONObject bodyJSONObject = requestJSONObject.getJSONObject("body");

		Assert.assertEquals("CLIENT", bodyJSONObject.getString("client_id"));
		Assert.assertEquals(
			"client_credentials", bodyJSONObject.getString("grant_type"));

		JSONObject responseJSONObject = jsonObject.getJSONObject("response");

		Assert.assertEquals(200, responseJSONObject.getInt("status"));

		bodyJSONObject = responseJSONObject.getJSONObject("body");

		Assert.assertFalse(bodyJSONObject.has("access_token"));
		Assert.assertEquals(600, bodyJSONObject.getInt("expires_in"));

		liferayCommerceRESTClient.putOption(
			"color", JSONUtil.put("key", "color"));

		options = _optionsList.get(1);

		Assert.assertEquals(
			"http://localhost:8080/o/headless-commerce-admin-catalog/v1.0" +
				"/options/by-externalReferenceCode/color",
			options.getLocation());
		Assert.assertEquals(Http.Method.PUT, options.getMethod());

		Map<String, String> headers = options.getHeaders();

		Assert.assertEquals(
			"Bearer TOKEN", headers.get(HttpHeaders.AUTHORIZATION));

		jsonArray = liferayCommerceRESTClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		log = jsonArray.toString();

		Assert.assertFalse(log, log.contains("TOKEN"));
	}

	private void _testAuthenticateWithErrorResponse() throws Exception {
		_mockHttp(401, JSONUtil.put("error", "invalid_client"));

		_assertPIMConnectorException(
			"unable-to-authenticate-with-the-remote-instance",
			LiferayCommerceRESTClient::authenticate);
	}

	private void _testPostImportTask() throws Exception {
		_mockHttp(
			200,
			JSONUtil.put(
				"executeStatus", "INITIAL"
			).put(
				"externalReferenceCode", "BATCH-1"
			));

		LiferayCommerceRESTClient liferayCommerceRESTClient =
			_createLiferayCommerceRESTClient();

		JSONObject jsonObject = liferayCommerceRESTClient.postImportTask(
			JSONUtil.putAll(JSONUtil.put("externalReferenceCode", "SKU-1")));

		Assert.assertEquals("INITIAL", jsonObject.getString("executeStatus"));

		Http.Options options = _optionsList.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"http://localhost:8080/o/headless-batch-engine/v1.0",
				"/import-task/com.liferay.headless.commerce.admin.catalog.dto.",
				"v1_0.Product?createStrategy=UPSERT&importStrategy=",
				"ON_ERROR_CONTINUE"),
			options.getLocation());
		Assert.assertEquals(Http.Method.POST, options.getMethod());

		Http.Body body = options.getBody();

		Assert.assertEquals("application/json", body.getContentType());

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			body.getContent());

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
	}

	private void _testPostImportTaskWithErrorResponse() throws Exception {
		_mockHttp(500, JSONUtil.put("status", "INTERNAL_SERVER_ERROR"));

		_assertPIMConnectorException(
			"the-remote-instance-returned-an-error",
			liferayCommerceRESTClient ->
				liferayCommerceRESTClient.postImportTask(JSONUtil.putAll()));
	}

	private void _testPutOption() throws Exception {
		_mockHttp(200, JSONFactoryUtil.createJSONObject());

		LiferayCommerceRESTClient liferayCommerceRESTClient =
			_createLiferayCommerceRESTClient();

		liferayCommerceRESTClient.putOption(
			"color", JSONUtil.put("key", "color"));

		Http.Options options = _optionsList.get(0);

		Assert.assertEquals(
			"http://localhost:8080/o/headless-commerce-admin-catalog/v1.0" +
				"/options/by-externalReferenceCode/color",
			options.getLocation());
		Assert.assertEquals(Http.Method.PUT, options.getMethod());

		Map<String, String> headers = options.getHeaders();

		Assert.assertEquals(
			"Bearer null", headers.get(HttpHeaders.AUTHORIZATION));
		Assert.assertEquals(
			"application/json", headers.get(HttpHeaders.CONTENT_TYPE));
	}

	private void _testPutOptionWithErrorResponse() throws Exception {
		_mockHttp(500, JSONUtil.put("status", "INTERNAL_SERVER_ERROR"));

		_assertPIMConnectorException(
			"the-remote-instance-returned-an-error",
			liferayCommerceRESTClient -> liferayCommerceRESTClient.putOption(
				"color", JSONUtil.put("key", "color")));

		_mockHttp(403, JSONUtil.put("status", "FORBIDDEN"));

		_assertPIMConnectorException(
			"unable-to-authenticate-with-the-remote-instance",
			liferayCommerceRESTClient -> liferayCommerceRESTClient.putOption(
				"color", JSONUtil.put("key", "color")));
	}

	private void _testPutOptionWithIOException() throws Exception {
		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenThrow(
			new IOException("Connection refused")
		);

		LiferayCommerceRESTClient liferayCommerceRESTClient =
			_createLiferayCommerceRESTClient();

		try {
			liferayCommerceRESTClient.putOption(
				"color", JSONUtil.put("key", "color"));

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				"unable-to-connect-to-the-remote-instance",
				pimConnectorException.getMessage());
		}

		JSONArray jsonArray = liferayCommerceRESTClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		JSONObject responseJSONObject = jsonObject.getJSONObject("response");

		Assert.assertEquals(
			"Connection refused", responseJSONObject.getString("error"));
	}

	private final Http _http = Mockito.mock(Http.class);
	private final List<Http.Options> _optionsList = new ArrayList<>();

	private interface UnsafeConsumer {

		public void accept(LiferayCommerceRESTClient liferayCommerceRESTClient)
			throws Exception;

	}

}