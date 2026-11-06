/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class LiferayCommercePIMClientTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAuthenticate() throws Exception {
		_mockHttp(401, JSONUtil.put("error", RandomTestUtil.randomString()));

		_assertPIMConnectorException(
			"unable-to-authenticate-with-the-remote-instance",
			LiferayCommercePIMClient::authenticate);

		_mockHttp(
			200,
			JSONUtil.put(
				"access_token", "TOKEN"
			).put(
				"expires_in", 600
			));

		LiferayCommercePIMClient liferayCommercePIMClient =
			_createLiferayCommercePIMClient();

		liferayCommercePIMClient.authenticate();

		Http.Options options = _options.get(0);

		Assert.assertEquals(
			"https://example.com/o/oauth2/token", options.getLocation());
		Assert.assertEquals(Http.Method.POST, options.getMethod());

		JSONArray jsonArray = liferayCommercePIMClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		String logJSON = jsonArray.toString();

		Assert.assertFalse(logJSON.contains("SECRET"));
		Assert.assertFalse(logJSON.contains("TOKEN"));

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

		liferayCommercePIMClient.putOptionByExternalReferenceCode(
			"color", JSONUtil.put("key", "color"));

		options = _options.get(1);

		Assert.assertEquals(
			"https://example.com/o/headless-commerce-admin-catalog/v1.0" +
				"/options/by-externalReferenceCode/color",
			options.getLocation());
		Assert.assertEquals(Http.Method.PUT, options.getMethod());

		Map<String, String> headers = options.getHeaders();

		Assert.assertEquals(
			"Bearer TOKEN", headers.get(HttpHeaders.AUTHORIZATION));

		jsonArray = liferayCommercePIMClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 2, jsonArray.length());

		logJSON = jsonArray.toString();

		Assert.assertFalse(logJSON.contains("TOKEN"));
	}

	@Test
	public void testPostImportTask() throws Exception {
		_mockHttp(500, JSONUtil.put("status", RandomTestUtil.randomString()));

		_assertPIMConnectorException(
			"the-remote-instance-returned-an-error",
			liferayCommercePIMClient -> liferayCommercePIMClient.postImportTask(
				JSONUtil.putAll()));

		_mockHttp(
			200,
			JSONUtil.put(
				"executeStatus", "INITIAL"
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			));

		LiferayCommercePIMClient liferayCommercePIMClient =
			_createLiferayCommercePIMClient();

		JSONObject jsonObject = liferayCommercePIMClient.postImportTask(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode", RandomTestUtil.randomString())));

		Assert.assertEquals("INITIAL", jsonObject.getString("executeStatus"));

		Http.Options options = _options.get(0);

		Assert.assertEquals(
			StringBundler.concat(
				"https://example.com/o/headless-batch-engine/v1.0/import-task",
				"/com.liferay.headless.commerce.admin.catalog.dto.v1_0.",
				"Product?createStrategy=UPSERT&importStrategy=",
				"ON_ERROR_CONTINUE"),
			options.getLocation());
		Assert.assertEquals(Http.Method.POST, options.getMethod());

		Http.Body body = options.getBody();

		Assert.assertEquals("application/json", body.getContentType());

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			body.getContent());

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
	}

	@Test
	public void testPutOptionByExternalReferenceCode() throws Exception {
		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenThrow(
			new IOException("Connection refused")
		);

		LiferayCommercePIMClient liferayCommercePIMClient =
			_assertPIMConnectorException(
				"unable-to-connect-to-the-remote-instance",
				curLiferayCommercePIMClient ->
					curLiferayCommercePIMClient.
						putOptionByExternalReferenceCode(
							"color", JSONUtil.put("key", "color")));

		JSONArray jsonArray = liferayCommercePIMClient.getLogJSONArray();

		Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		JSONObject responseJSONObject = jsonObject.getJSONObject("response");

		Assert.assertEquals(
			"Connection refused", responseJSONObject.getString("error"));

		_mockHttp(403, JSONUtil.put("status", RandomTestUtil.randomString()));

		_assertPIMConnectorException(
			"unable-to-authenticate-with-the-remote-instance",
			curLiferayCommercePIMClient ->
				curLiferayCommercePIMClient.putOptionByExternalReferenceCode(
					"color", JSONUtil.put("key", "color")));

		_mockHttp(500, JSONUtil.put("status", RandomTestUtil.randomString()));

		_assertPIMConnectorException(
			"the-remote-instance-returned-an-error",
			curLiferayCommercePIMClient ->
				curLiferayCommercePIMClient.putOptionByExternalReferenceCode(
					"color", JSONUtil.put("key", "color")));

		_mockHttp(200, JSONFactoryUtil.createJSONObject());

		liferayCommercePIMClient = _createLiferayCommercePIMClient();

		liferayCommercePIMClient.putOptionByExternalReferenceCode(
			"color", JSONUtil.put("key", "color"));

		Http.Options options = _options.get(0);

		Assert.assertEquals(
			"https://example.com/o/headless-commerce-admin-catalog/v1.0" +
				"/options/by-externalReferenceCode/color",
			options.getLocation());
		Assert.assertEquals(Http.Method.PUT, options.getMethod());

		Map<String, String> headers = options.getHeaders();

		Assert.assertEquals(
			"Bearer null", headers.get(HttpHeaders.AUTHORIZATION));
		Assert.assertEquals(
			"application/json", headers.get(HttpHeaders.CONTENT_TYPE));
	}

	private LiferayCommercePIMClient _assertPIMConnectorException(
			String expectedMessage,
			UnsafeConsumer<LiferayCommercePIMClient, Exception> unsafeConsumer)
		throws Exception {

		LiferayCommercePIMClient liferayCommercePIMClient =
			_createLiferayCommercePIMClient();

		try {
			unsafeConsumer.accept(liferayCommercePIMClient);

			Assert.fail();
		}
		catch (PIMConnectorException pimConnectorException) {
			Assert.assertEquals(
				expectedMessage, pimConnectorException.getMessage());
		}

		return liferayCommercePIMClient;
	}

	private LiferayCommercePIMClient _createLiferayCommercePIMClient() {
		return new LiferayCommercePIMClient(
			"CLIENT", "SECRET", _http, JSONFactoryUtil.getJSONFactory(),
			"https://example.com");
	}

	private void _mockHttp(int responseCode, JSONObject responseJSONObject)
		throws Exception {

		_options.clear();

		Mockito.when(
			_http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(
					0, Http.Options.class);

				_options.add(options);

				Http.Response response = options.getResponse();

				response.setResponseCode(responseCode);

				return responseJSONObject.toString();
			}
		);
	}

	private final Http _http = Mockito.mock(Http.class);
	private final List<Http.Options> _options = new ArrayList<>();

}