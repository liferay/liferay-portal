/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;

/**
 * @author Luis Miguel Barcos
 * @author Alberto Javier Moreno Lage
 */
public class HTTPTestUtil {

	public static HTTPTestUtilCustomizer customize() {
		return new HTTPTestUtilCustomizer();
	}

	public static int invokeToHttpCode(
			String body, String endpoint, Http.Method httpMethod)
		throws Exception {

		Http.Options options = _getHttpOptions(
			body, endpoint, null, httpMethod);

		HttpUtil.URLtoString(options);

		Http.Response response = options.getResponse();

		return response.getResponseCode();
	}

	public static InputStream invokeToInputStream(
			String body, String endpoint, Http.Method httpMethod)
		throws Exception {

		Http.Options options = _getHttpOptions(
			body, endpoint, null, httpMethod);

		return HttpUtil.URLtoInputStream(options);
	}

	public static JSONObject invokeToJSONObject(
			String body, String endpoint, Http.Method httpMethod)
		throws Exception {

		return invokeToJSONObject(body, endpoint, null, httpMethod);
	}

	public static JSONObject invokeToJSONObject(
			String body, String endpoint, Map<String, String> headers,
			Http.Method httpMethod)
		throws Exception {

		return JSONFactoryUtil.createJSONObject(
			invokeToString(body, endpoint, headers, httpMethod));
	}

	public static String invokeToString(
			String body, String endpoint, Http.Method httpMethod)
		throws Exception {

		return invokeToString(body, endpoint, null, httpMethod);
	}

	public static String invokeToString(
			String body, String endpoint, Map<String, String> headers,
			Http.Method httpMethod)
		throws Exception {

		Http.Options options = _getHttpOptions(
			body, endpoint, headers, httpMethod);

		return HttpUtil.URLtoString(options);
	}

	public static class HTTPTestUtilCustomizer {

		public <T extends Throwable> void apply(
				UnsafeRunnable<T> unsafeRunnable)
			throws T {

			String defaultBaseURL = _baseURL;

			_baseURL = _newBaseURL;

			String defaultCredentials = _credentials;

			_credentials = _newCredentials;

			boolean defaultModulePath = _modulePath;

			_modulePath = _newModulePath;

			try {
				unsafeRunnable.run();
			}
			finally {
				_baseURL = defaultBaseURL;
				_credentials = defaultCredentials;
				_modulePath = defaultModulePath;
			}
		}

		public HTTPTestUtilCustomizer withBaseURL(String baseURL) {
			_newBaseURL = baseURL;

			return this;
		}

		public HTTPTestUtilCustomizer withCredentials(
			String emailAddress, String password) {

			_newCredentials = emailAddress + StringPool.COLON + password;

			return this;
		}

		public HTTPTestUtilCustomizer withGuest() {
			_newCredentials = null;

			return this;
		}

		public HTTPTestUtilCustomizer withoutModulePath() {
			_newModulePath = false;

			return this;
		}

		private String _newBaseURL = _baseURL;
		private String _newCredentials = _credentials;
		private boolean _newModulePath = _modulePath;

	}

	private static Http.Options _getHttpOptions(
		String body, String endpoint, Map<String, String> headers,
		Http.Method httpMethod) {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

		if (_credentials != null) {
			options.addHeader(
				"Authorization",
				"Basic " + Base64.encode(_credentials.getBytes()));
		}

		if (MapUtil.isNotEmpty(headers)) {
			headers.forEach(options::addHeader);
		}

		options.setCookieSpec(Http.CookieSpec.STANDARD);

		if (_modulePath) {
			options.setLocation(_baseURL + "/o/" + endpoint);
		}
		else {
			options.setLocation(_baseURL + "/" + endpoint);
		}

		options.setMethod(httpMethod);

		if (body != null) {
			options.setBody(
				body, ContentTypes.APPLICATION_JSON,
				StandardCharsets.UTF_8.name());
		}

		return options;
	}

	private static String _baseURL = "http://localhost:8080";
	private static String _credentials =
		"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;
	private static boolean _modulePath = true;

}