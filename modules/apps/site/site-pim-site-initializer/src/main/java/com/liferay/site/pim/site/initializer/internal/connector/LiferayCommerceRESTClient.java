/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.site.pim.site.initializer.exception.PIMConnectorException;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Tancredi Covioli
 */
public class LiferayCommerceRESTClient {

	public LiferayCommerceRESTClient(
		String clientId, String clientSecret, Http http,
		JSONFactory jsonFactory, String url) {

		_clientId = clientId;
		_clientSecret = clientSecret;
		_http = http;
		_jsonFactory = jsonFactory;
		_url = url;
	}

	public void authenticate() throws Exception {
		Http.Options options = new Http.Options();

		options.addPart("client_id", _clientId);
		options.addPart("client_secret", _clientSecret);
		options.addPart("grant_type", "client_credentials");
		options.setLocation(_url + "/o/oauth2/token");
		options.setPost(true);

		String response = _invoke(
			JSONUtil.put(
				"client_id", _clientId
			).put(
				"grant_type", "client_credentials"
			),
			options);

		if (!_isSuccessful(options)) {
			throw new PIMConnectorException(
				"unable-to-authenticate-with-the-remote-instance");
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(response);

		_accessToken = jsonObject.getString("access_token");
	}

	public void checkCatalog(String externalReferenceCode) throws Exception {
		Http.Options options = _createOptions(
			"/o/headless-commerce-admin-catalog/v1.0/catalog" +
				"/by-externalReferenceCode/" +
					URLCodec.encodeURL(externalReferenceCode));

		_invoke(null, options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpServletResponse.SC_NOT_FOUND) {
			throw new PIMConnectorException(
				"a-catalog-mapped-to-a-product-does-not-exist-in-the-remote-" +
					"instance");
		}

		_checkResponse(options);
	}

	public JSONArray getLogJSONArray() {
		return _logJSONArray;
	}

	public JSONObject postImportTask(
			String externalReferenceCode, JSONArray jsonArray)
		throws Exception {

		Http.Options options = _createOptions(
			StringBundler.concat(
				"/o/headless-batch-engine/v1.0/import-task/",
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product",
				"?createStrategy=UPSERT&externalReferenceCode=",
				URLCodec.encodeURL(externalReferenceCode),
				"&importStrategy=ON_ERROR_CONTINUE"));

		options.setBody(
			jsonArray.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setPost(true);

		String response = _invoke(jsonArray, options);

		_checkResponse(options);

		return _jsonFactory.createJSONObject(response);
	}

	public void putOption(String externalReferenceCode, JSONObject jsonObject)
		throws Exception {

		Http.Options options = _createOptions(
			"/o/headless-commerce-admin-catalog/v1.0/options" +
				"/by-externalReferenceCode/" +
					URLCodec.encodeURL(externalReferenceCode));

		options.setBody(
			jsonObject.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setMethod(Http.Method.PUT);

		_invoke(jsonObject, options);

		_checkResponse(options);
	}

	private void _checkResponse(Http.Options options) throws Exception {
		Http.Response response = options.getResponse();

		if ((response.getResponseCode() ==
				HttpServletResponse.SC_UNAUTHORIZED) ||
			(response.getResponseCode() == HttpServletResponse.SC_FORBIDDEN)) {

			throw new PIMConnectorException(
				"unable-to-authenticate-with-the-remote-instance");
		}

		if (!_isSuccessful(options)) {
			throw new PIMConnectorException(
				"the-remote-instance-returned-an-error");
		}
	}

	private Http.Options _createOptions(String path) {
		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);
		options.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + _accessToken);
		options.setLocation(_url + path);

		return options;
	}

	private String _invoke(Object body, Http.Options options)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"request",
			JSONUtil.put(
				"body", body
			).put(
				"method", String.valueOf(options.getMethod())
			).put(
				"url", options.getLocation()
			));

		_logJSONArray.put(jsonObject);

		try {
			String response = _http.URLtoString(options);

			Http.Response httpResponse = options.getResponse();

			jsonObject.put(
				"response",
				JSONUtil.put(
					"body", _toLogValue(response)
				).put(
					"status", httpResponse.getResponseCode()
				));

			return response;
		}
		catch (IOException ioException) {
			jsonObject.put(
				"response", JSONUtil.put("error", ioException.getMessage()));

			throw new PIMConnectorException(
				"unable-to-connect-to-the-remote-instance", ioException);
		}
	}

	private boolean _isSuccessful(Http.Options options) {
		Http.Response response = options.getResponse();

		if ((response.getResponseCode() >= HttpServletResponse.SC_OK) &&
			(response.getResponseCode() <
				HttpServletResponse.SC_MULTIPLE_CHOICES)) {

			return true;
		}

		return false;
	}

	private Object _toLogValue(String response) throws Exception {
		if (!JSONUtil.isJSONObject(response)) {
			return response;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(response);

		if (jsonObject.has("access_token")) {
			jsonObject.put("access_token", "***");
		}

		return jsonObject;
	}

	private String _accessToken;
	private final String _clientId;
	private final String _clientSecret;
	private final Http _http;
	private final JSONFactory _jsonFactory;
	private final JSONArray _logJSONArray = JSONUtil.putAll();
	private final String _url;

}
