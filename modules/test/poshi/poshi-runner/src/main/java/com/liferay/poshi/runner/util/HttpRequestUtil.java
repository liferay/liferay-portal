/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.MathUtil;
import com.liferay.poshi.core.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jodd.util.Base64;

/**
 * @author Michael Hashimoto
 */
public class HttpRequestUtil {

	public static void assertResponseBody(
		HttpResponse httpResponse, String expectedResponseBody) {

		String actualResponseBody = httpResponse.getBody();

		if (!StringUtil.equals(actualResponseBody, expectedResponseBody)) {
			throw new RuntimeException(
				"Expected response body: " + expectedResponseBody +
					"does not match actual response body " +
						actualResponseBody);
		}
	}

	public static void assertResponseBodyContains(
		HttpResponse httpResponse, String expectedText) {

		String actualResponseBody = httpResponse.getBody();

		if (!StringUtil.contains(actualResponseBody, expectedText)) {
			throw new RuntimeException(
				"Expected text \"" + expectedText + "\" was not found in " +
					actualResponseBody);
		}
	}

	public static void assertResponseDuration(
		HttpResponse httpResponse, String expectedResponseDuration) {

		Long actualResponseDuration = httpResponse.getDuration();

		if (MathUtil.isGreaterThan(
				actualResponseDuration,
				GetterUtil.getLong(expectedResponseDuration))) {

			throw new RuntimeException(
				"Actual response duration of " + actualResponseDuration +
					"ms exceeded expected response duration of " +
						expectedResponseDuration + "ms");
		}
	}

	public static void assertResponseStatusCode(
		HttpResponse httpResponse, String expectedStatusCode) {

		Integer actualStatusCode = httpResponse.getStatusCode();

		if (!StringUtil.equals(
				actualStatusCode.toString(), expectedStatusCode)) {

			throw new RuntimeException(
				"Expected status code " + expectedStatusCode +
					" does not match actual status code " + actualStatusCode);
		}
	}

	public static HttpResponse get(
			HttpAuthorization httpAuthorizationHeader, Integer maxRetries,
			Map<String, String> requestHeaders, Integer retryPeriod,
			Integer timeout, String url)
		throws IOException {

		return request(
			httpAuthorizationHeader, maxRetries, null, requestHeaders, "GET",
			retryPeriod, timeout, url);
	}

	public static HttpResponse get(
			HttpAuthorization httpAuthorizationHeader,
			Map<String, String> requestHeaders, String url)
		throws IOException {

		return request(
			httpAuthorizationHeader, _MAX_RETRIES_DEFAULT, null, requestHeaders,
			"GET", _RETRY_PERIOD_DEFAULT, _TIMEOUT_DEFAULT, url);
	}

	public static HttpResponse get(String url) throws IOException {
		return request(
			null, _MAX_RETRIES_DEFAULT, null, null, "GET",
			_RETRY_PERIOD_DEFAULT, _TIMEOUT_DEFAULT, url);
	}

	public static HttpAuthorization getHttpAuthorization(
		String type, String value) {

		if (type.equals("basic")) {
			String[] tokens = value.split(":");

			return new BasicHttpAuthorization(tokens[0], tokens[1]);
		}
		else if (type.equals("token")) {
			return new TokenHttpAuthorization(value);
		}

		throw new IllegalArgumentException(
			"Unsupported authorization type: " + type);
	}

	public static String getResponseBody(HttpResponse httpResponse) {
		return httpResponse.getBody();
	}

	public static Long getResponseDuration(HttpResponse httpResponse) {
		return httpResponse.getDuration();
	}

	public static String getResponseErrorMessage(HttpResponse httpResponse) {
		return httpResponse.getErrorMessage();
	}

	public static List<String> getResponseHeaderFieldValue(
		HttpResponse httpResponse, String headerFieldKey) {

		Map<String, List<String>> responseHeaderFields =
			getResponseHeaderFields(httpResponse);

		return responseHeaderFields.get(headerFieldKey);
	}

	public static Map<String, List<String>> getResponseHeaderFields(
		HttpResponse httpResponse) {

		return httpResponse.getHeaderFields();
	}

	public static Integer getResponseStatusCode(HttpResponse httpResponse) {
		return httpResponse.getStatusCode();
	}

	public static HttpResponse request(
			HttpAuthorization httpAuthorizationHeader, Integer maxRetries,
			String requestBody, Map<String, String> requestHeaders,
			String requestMethod, Integer retryPeriod, Integer timeout,
			String url)
		throws IOException {

		url = _fixURL(url);

		int retryCount = 0;

		long startTimeMillis = System.currentTimeMillis();

		while (true) {
			try {
				System.out.println("Generating HTTP request for URL: " + url);

				URL urlObject = new URL(url);

				URLConnection urlConnection = urlObject.openConnection();

				if (!(urlConnection instanceof HttpURLConnection)) {
					throw new IllegalArgumentException(
						"Connection must be of type HTTP");
				}

				HttpURLConnection httpURLConnection =
					(HttpURLConnection)urlConnection;

				System.out.println("Setting request method: " + requestMethod);

				httpURLConnection.setRequestMethod(requestMethod);

				if (httpAuthorizationHeader != null) {
					System.out.println(
						"Setting authorization: " + httpAuthorizationHeader);

					httpURLConnection.setRequestProperty(
						"Authorization", httpAuthorizationHeader.toString());
				}

				if (requestHeaders != null) {
					for (Map.Entry<String, String> requestHeader :
							requestHeaders.entrySet()) {

						System.out.println(
							"Setting request header key \"" +
								requestHeader.getKey() + "\" with value \"" +
									requestHeader.getValue() + "\"");

						httpURLConnection.setRequestProperty(
							requestHeader.getKey(), requestHeader.getValue());
					}
				}

				if (requestBody != null) {
					if (requestMethod.equals("GET")) {
						throw new IllegalArgumentException(
							"Request method 'GET' cannot have a request body");
					}

					System.out.println("Setting request body: " + requestBody);

					if ((requestHeaders != null) &&
						requestHeaders.containsKey("Content-Type")) {

						String contentType = requestHeaders.get("Content-Type");

						if (contentType.equals("application/json")) {
							JSONUtil.toJSONObject(requestBody);
						}
					}
					else {
						requestBody = URLEncoder.encode(requestBody, "UTF-8");
					}

					httpURLConnection.setDoOutput(true);

					try (OutputStream outputStream =
							httpURLConnection.getOutputStream()) {

						outputStream.write(requestBody.getBytes("UTF-8"));

						outputStream.flush();
					}
				}

				if (timeout != 0) {
					httpURLConnection.setConnectTimeout(timeout);
					httpURLConnection.setReadTimeout(timeout);
				}

				int responseCode = httpURLConnection.getResponseCode();

				if ((responseCode >= 200) && (responseCode <= 399)) {
					try (InputStream inputStream =
							httpURLConnection.getInputStream()) {

						long duration =
							System.currentTimeMillis() - startTimeMillis;

						return new HttpResponse(
							_readInputStream(inputStream, false), null,
							httpURLConnection.getHeaderFields(), responseCode,
							duration);
					}
				}

				try (InputStream errorInputStream =
						httpURLConnection.getErrorStream()) {

					long duration =
						System.currentTimeMillis() - startTimeMillis;

					return new HttpResponse(
						null, _readInputStream(errorInputStream, false),
						httpURLConnection.getHeaderFields(), responseCode,
						duration);
				}
			}
			catch (IOException ioException) {
				retryCount++;

				if ((maxRetries >= 0) && (retryCount >= maxRetries)) {
					throw ioException;
				}

				System.out.println(
					"Retrying " + url + " in " + retryPeriod + " seconds");

				ExecUtil.sleep(1000 * retryPeriod);
			}
		}
	}

	public static HttpResponse request(
			HttpAuthorization httpAuthorizationHeader, String requestBody,
			Map<String, String> requestHeaders, String requestMethod,
			String url)
		throws IOException {

		return request(
			httpAuthorizationHeader, _MAX_RETRIES_DEFAULT, requestBody,
			requestHeaders, requestMethod, _RETRY_PERIOD_DEFAULT,
			_TIMEOUT_DEFAULT, url);
	}

	public static class BasicHttpAuthorization extends HttpAuthorization {

		public BasicHttpAuthorization(String password, String userName) {
			super(Type.BASIC);

			this.password = password;
			this.userName = userName;
		}

		@Override
		public String toString() {
			String authorization = StringUtil.combine(userName, ":", password);

			return StringUtil.combine(
				"Basic ", Base64.encodeToString(authorization.getBytes()));
		}

		protected String password;
		protected String userName;

	}

	public abstract static class HttpAuthorization {

		public Type getType() {
			return type;
		}

		public static enum Type {

			BASIC, TOKEN

		}

		protected HttpAuthorization(Type type) {
			this.type = type;
		}

		protected Type type;

	}

	public static class HttpResponse {

		public HttpResponse(
			String body, String errorMessage,
			Map<String, List<String>> headerFields, int statusCode,
			long duration) {

			_body = body;
			_errorMessage = errorMessage;
			_headerFields = headerFields;
			_statusCode = statusCode;
			_duration = duration;
		}

		public String getBody() {
			return _body;
		}

		public long getDuration() {
			return _duration;
		}

		public String getErrorMessage() {
			return _errorMessage;
		}

		public Map<String, List<String>> getHeaderFields() {
			return _headerFields;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		private final String _body;
		private final long _duration;
		private final String _errorMessage;
		private final Map<String, List<String>> _headerFields;
		private final int _statusCode;

	}

	public static class TokenHttpAuthorization extends HttpAuthorization {

		public TokenHttpAuthorization(String token) {
			super(Type.TOKEN);

			this.token = token;
		}

		@Override
		public String toString() {
			return StringUtil.combine("token ", token);
		}

		protected String token;

	}

	private static String _fixURL(String url) {
		url = StringUtil.replace(url, " ", "%20");
		url = StringUtil.replace(url, "#", "%23");
		url = StringUtil.replace(url, "(", "%28");
		url = StringUtil.replace(url, ")", "%29");
		url = StringUtil.replace(url, "[", "%5B");
		url = StringUtil.replace(url, "]", "%5D");

		return url;
	}

	private static String _readInputStream(
			InputStream inputStream, boolean resetAfterReading)
		throws IOException {

		if (resetAfterReading && !inputStream.markSupported()) {
			Class<?> inputStreamClass = inputStream.getClass();

			System.out.println(
				"Unable to reset after reading input stream " +
					inputStreamClass.getName());
		}

		if (resetAfterReading && inputStream.markSupported()) {
			inputStream.mark(Integer.MAX_VALUE);
		}

		StringBuffer sb = new StringBuffer();

		byte[] bytes = new byte[1024];

		int size = inputStream.read(bytes);

		while (size > 0) {
			sb.append(new String(Arrays.copyOf(bytes, size)));

			size = inputStream.read(bytes);
		}

		if (resetAfterReading && inputStream.markSupported()) {
			inputStream.reset();
		}

		return sb.toString();
	}

	private static final Integer _MAX_RETRIES_DEFAULT = 3;

	private static final Integer _RETRY_PERIOD_DEFAULT = 5;

	private static final Integer _TIMEOUT_DEFAULT = 0;

}