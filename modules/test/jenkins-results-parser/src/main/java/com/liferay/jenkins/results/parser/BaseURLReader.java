/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.BasicHTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.ClientCredentialsHTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HTTPAuthorization;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.TokenHTTPAuthorization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * @author Kenji Heigel
 */
public abstract class BaseURLReader<T> implements URLReader<T> {

	@Override
	public T read(
			boolean checkCache, boolean expectResponse,
			HTTPAuthorization httpAuthorization,
			HttpRequestMethod httpRequestMethod, int maxRetries,
			String postContent, int retryPeriod, int timeout, String url)
		throws IOException {

		if (url.contains("/userContent/") && (timeout == 0)) {
			timeout = 5000;
		}

		if (httpRequestMethod == null) {
			if (postContent != null) {
				httpRequestMethod = HttpRequestMethod.POST;
			}
			else {
				httpRequestMethod = HttpRequestMethod.GET;
			}
		}

		url = JenkinsResultsParserUtil.normalizeURL(url);

		String cacheFileKey = null;

		if (url.startsWith("file:")) {
			url = JenkinsResultsParserUtil.fixFileURL(url);
		}
		else if (checkCache) {
			cacheFileKey = JenkinsResultsParserUtil.getCacheFileKey(
				url, postContent);

			if (JenkinsResultsParserUtil.debug) {
				System.out.println("Loading " + url);
			}

			File cachedFile = JenkinsResultsParserUtil.getCacheFile(
				cacheFileKey);

			if ((cachedFile != null) && cachedFile.exists()) {
				return handleCachedFile(cachedFile);
			}
		}

		boolean gitHubAPICall = false;
		int retryCount = 0;

		while (true) {
			String authorization = null;
			URLConnection urlConnection = null;

			try {
				if (JenkinsResultsParserUtil.debug) {
					System.out.println("Downloading " + url);
				}

				Matcher matcher = _gitHubAPIURLPattern.matcher(url);

				if (matcher.matches()) {
					gitHubAPICall = true;

					if (_updatingHttpRequestMethods.contains(
							httpRequestMethod)) {

						Properties buildProperties =
							JenkinsResultsParserUtil.getBuildProperties();

						url =
							buildProperties.getProperty("github.api.proxy") +
								matcher.group(1);
					}
				}

				if ((httpAuthorization == null) &&
					(gitHubAPICall ||
					 url.startsWith(
						 "https://raw.githubusercontent.com/liferay/"))) {

					Properties buildProperties =
						JenkinsResultsParserUtil.getBuildProperties();

					httpAuthorization = new TokenHTTPAuthorization(
						buildProperties.getProperty("github.access.token"));
				}

				if ((httpAuthorization == null) &&
					url.startsWith("https://release.liferay.com")) {

					httpAuthorization =
						JenkinsResultsParserUtil.getJenkinsHTTPAuthorization();
				}

				if ((httpAuthorization == null) &&
					url.matches(
						"https?:\\/\\/test-[135]-\\d+(?:\\.liferay\\.com)?.*?" +
							"|http:\\/\\/localhost:8081.*?")) {

					if (JenkinsResultsParserUtil.isCINode()) {
						url = JenkinsResultsParserUtil.getLocalURL(url);
					}
					else {
						url = JenkinsResultsParserUtil.getRemoteURL(url);
					}

					httpAuthorization =
						JenkinsResultsParserUtil.getJenkinsHTTPAuthorization();
				}

				boolean testray1Request = false;

				if (url.matches("https://testray-old.liferay.com/?.+")) {
					testray1Request = true;
				}

				if ((httpAuthorization == null) && testray1Request) {
					Properties buildProperties =
						JenkinsResultsParserUtil.getBuildProperties();

					httpAuthorization = new BasicHTTPAuthorization(
						JenkinsResultsParserUtil.getProperty(
							buildProperties, "testray.admin.user.password"),
						JenkinsResultsParserUtil.getProperty(
							buildProperties, "testray.admin.user.name"));
				}

				Matcher testray2URLMatcher = _testray2URLPattern.matcher(url);

				if ((httpAuthorization == null) && testray2URLMatcher.find() &&
					!url.contains("/o/oauth2/token")) {

					String baseURL = testray2URLMatcher.group("baseURL");

					Properties buildProperties =
						JenkinsResultsParserUtil.getBuildProperties();
					String lxcEnvironment = testray2URLMatcher.group(
						"lxcEnvironment");

					String clientId = JenkinsResultsParserUtil.getProperty(
						buildProperties, "testray.oauth2.client.id",
						lxcEnvironment);
					String clientSecret = JenkinsResultsParserUtil.getProperty(
						buildProperties, "testray.oauth2.client.secret",
						lxcEnvironment);

					URL tokenURL = new URL(baseURL + "/o/oauth2/token");

					httpAuthorization =
						_testrayHTTPAuthorizations.computeIfAbsent(
							baseURL,
							testrayBaseURL ->
								new ClientCredentialsHTTPAuthorization(
									clientId, clientSecret, tokenURL));
				}

				if ((httpAuthorization != null) && !url.startsWith("file:")) {
					authorization = httpAuthorization.toString();
				}

				urlConnection = openURLConnection(
					authorization, gitHubAPICall, httpRequestMethod,
					postContent, testray1Request, timeout, url);

				if (gitHubAPICall) {
					try {
						int limit = Integer.parseInt(
							urlConnection.getHeaderField("X-RateLimit-Limit"));
						int remaining = Integer.parseInt(
							urlConnection.getHeaderField(
								"X-RateLimit-Remaining"));
						long reset = Long.parseLong(
							urlConnection.getHeaderField("X-RateLimit-Reset"));

						System.out.println(
							JenkinsResultsParserUtil.combine(
								JenkinsResultsParserUtil.
									getGitHubAPIRateLimitStatusMessage(
										limit, remaining, reset),
								"\n    ", url));
					}
					catch (Exception exception) {
						System.out.println(
							JenkinsResultsParserUtil.combine(
								"Unable to parse GitHub API rate limit headers",
								"\nURL:\n    ", url));

						exception.printStackTrace();
					}
				}

				return handleResponse(
					cacheFileKey, expectResponse, urlConnection);
			}
			catch (IOException ioException1) {
				if (ioException1 instanceof FileNotFoundException ||
					ioException1 instanceof TruncatedResponseException) {

					throw ioException1;
				}

				if ((ioException1 instanceof UnknownHostException) &&
					url.matches("http://test-\\d+-\\d+/.*")) {

					return read(
						checkCache, expectResponse, httpAuthorization,
						httpRequestMethod, maxRetries, postContent, retryPeriod,
						timeout, JenkinsResultsParserUtil.getRemoteURL(url));
				}

				String exceptionMessage = ioException1.getMessage();
				int responseCode = _getResponseCode(
					ioException1, urlConnection);

				if (responseCode == 422) {
					StringBuilder sb = new StringBuilder();

					sb.append(exceptionMessage);
					sb.append("\n");

					if (!JenkinsResultsParserUtil.isNullOrEmpty(postContent)) {
						sb.append("Post content:\n");
						sb.append(postContent);
					}

					System.out.println(sb.toString());

					throw new RuntimeException(exceptionMessage, ioException1);
				}

				Matcher testray2URLMatcher = _testray2URLPattern.matcher(url);

				if ((responseCode == 403) && testray2URLMatcher.find() &&
					(urlConnection instanceof HttpURLConnection)) {

					HttpURLConnection httpURLConnection =
						(HttpURLConnection)urlConnection;

					StringBuilder sb = new StringBuilder();

					sb.append(exceptionMessage);

					String errorResponse = null;

					try (InputStream errorInputStream =
							httpURLConnection.getErrorStream()) {

						if (errorInputStream != null) {
							errorResponse =
								JenkinsResultsParserUtil.readInputStream(
									errorInputStream);

							sb.append("\nError response:\n");
							sb.append(errorResponse);
						}
					}
					catch (IOException ioException2) {
						sb.append("\nUnable to read the error response: ");
						sb.append(ioException2.getMessage());
					}

					if ((maxRetries >= 0) && (retryCount >= maxRetries) &&
						!JenkinsResultsParserUtil.isNullOrEmpty(postContent)) {

						sb.append("\nPost content:\n");
						sb.append(postContent);
					}

					System.out.println(sb.toString());

					if ((httpAuthorization instanceof
							ClientCredentialsHTTPAuthorization) &&
						!_isHTMLErrorResponse(errorResponse)) {

						ClientCredentialsHTTPAuthorization
							clientCredentialsHTTPAuthorization =
								(ClientCredentialsHTTPAuthorization)
									httpAuthorization;

						clientCredentialsHTTPAuthorization.invalidateToken(
							authorization);
					}
				}

				Integer retryPeriodOverride = null;

				if (gitHubAPICall && (responseCode == 403)) {
					try {
						retryPeriodOverride = Integer.parseInt(
							urlConnection.getHeaderField("retry-after"));
					}
					catch (NumberFormatException numberFormatException) {
						retryPeriodOverride = null;
					}

					if ((retryPeriodOverride == null) ||
						(retryPeriodOverride == 0)) {

						retryPeriodOverride = retryPeriod;

						for (int i = 0; i < retryCount; i++) {
							retryPeriodOverride = Math.min(
								retryPeriodOverride * retryPeriodOverride,
								_SECONDS_RETRY_PERIOD_ESCALATION_MAX);
						}
					}

					if (((maxRetries >= 0) && (retryCount >= maxRetries)) ||
						(retryPeriodOverride > _SECONDS_RETRY_PERIOD_MAX)) {

						throw new GitHubSecondaryRateLimitRuntimeException(
							url, retryPeriodOverride, ioException1);
					}
				}

				if ((responseCode >= 400) && (responseCode < 500) &&
					!_retryableResponseCodes.contains(responseCode)) {

					throw ioException1;
				}

				long retryPeriodMillis = 1000 * retryPeriod;

				if ((retryPeriodOverride != null) &&
					(retryPeriodOverride > 0)) {

					retryPeriodMillis = 1000 * retryPeriodOverride;
				}

				if ((maxRetries >= 0) && (retryCount >= maxRetries)) {
					throw ioException1;
				}

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Retrying ", url, " in ",
						JenkinsResultsParserUtil.toDurationString(
							retryPeriodMillis)));

				retryCount++;

				sleep(retryPeriodMillis);
			}
		}
	}

	protected abstract T handleCachedFile(File cachedFile) throws IOException;

	protected abstract T handleResponse(
			String cacheFileKey, boolean expectResponse,
			URLConnection urlConnection)
		throws IOException;

	protected URLConnection openURLConnection(
			String authorization, boolean gitHubAPICall,
			HttpRequestMethod httpRequestMethod, String postContent,
			boolean testray1Request, int timeout, String url)
		throws IOException {

		URL urlObject = new URL(url);

		URLConnection urlConnection = urlObject.openConnection();

		if (urlConnection instanceof HttpURLConnection) {
			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlConnection;

			if (httpRequestMethod == HttpRequestMethod.PATCH) {
				httpURLConnection.setRequestMethod("POST");

				httpURLConnection.setRequestProperty(
					"X-HTTP-Method-Override", "PATCH");
			}
			else {
				httpURLConnection.setRequestMethod(httpRequestMethod.name());
			}

			if (gitHubAPICall &&
				(httpURLConnection instanceof HttpsURLConnection)) {

				SSLContext sslContext = null;

				float javaVersionNumber =
					JenkinsResultsParserUtil.getJavaVersionNumber();

				try {
					if (javaVersionNumber < 1.8F) {
						sslContext = SSLContext.getInstance("TLSv1.2");

						sslContext.init(null, null, null);

						HttpsURLConnection httpsURLConnection =
							(HttpsURLConnection)httpURLConnection;

						httpsURLConnection.setSSLSocketFactory(
							sslContext.getSocketFactory());
					}
				}
				catch (KeyManagementException | NoSuchAlgorithmException
							exception) {

					throw new RuntimeException(
						"Unable to set SSL context to TLS v1.2", exception);
				}
			}

			if (authorization != null) {
				httpURLConnection.setRequestProperty(
					"accept", "application/json");
				httpURLConnection.setRequestProperty(
					"Authorization", authorization);

				if (!testray1Request) {
					httpURLConnection.setRequestProperty(
						"Content-Type", "application/json");
				}
			}

			if (url.contains("/oauth2/")) {
				httpURLConnection.setRequestProperty(
					"accept", "application/json");
				httpURLConnection.setRequestProperty(
					"Content-Type", "application/x-www-form-urlencoded");
			}

			if (url.startsWith("https://releases-cdn.liferay.com")) {
				httpURLConnection.setRequestProperty("User-Agent", "");
			}

			if (postContent != null) {
				if (httpRequestMethod == null) {
					httpURLConnection.setRequestMethod("POST");
				}

				httpURLConnection.setDoOutput(true);

				try (OutputStream outputStream =
						httpURLConnection.getOutputStream()) {

					outputStream.write(postContent.getBytes("UTF-8"));

					outputStream.flush();
				}
			}
		}

		if (timeout != 0) {
			urlConnection.setConnectTimeout(timeout);
			urlConnection.setReadTimeout(timeout);
		}

		urlConnection.connect();

		return urlConnection;
	}

	protected void sleep(long duration) {
		JenkinsResultsParserUtil.sleep(duration);
	}

	private int _getResponseCode(
		IOException ioException1, URLConnection urlConnection) {

		if (ioException1 instanceof SocketException ||
			ioException1 instanceof SocketTimeoutException ||
			!(urlConnection instanceof HttpURLConnection)) {

			return -1;
		}

		HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;

		try {
			return httpURLConnection.getResponseCode();
		}
		catch (IOException ioException2) {
			return -1;
		}
	}

	private boolean _isHTMLErrorResponse(String errorResponse) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(errorResponse)) {
			return false;
		}

		errorResponse = errorResponse.trim();

		errorResponse = errorResponse.toLowerCase();

		if (errorResponse.startsWith("<!doctype html") ||
			errorResponse.startsWith("<html")) {

			return true;
		}

		return false;
	}

	private static final int _SECONDS_RETRY_PERIOD_ESCALATION_MAX = 60;

	private static final int _SECONDS_RETRY_PERIOD_MAX = 60 * 30;

	private static final Pattern _gitHubAPIURLPattern = Pattern.compile(
		"https\\:\\/\\/api\\.github\\.com(.*)");
	private static final List<Integer> _retryableResponseCodes = Arrays.asList(
		403, 408, 429);
	private static final Pattern _testray2URLPattern = Pattern.compile(
		"(?<baseURL>https://webserver-testray2(-(?<lxcEnvironment>.+))?" +
			"\\.lfr\\.cloud|https://testray\\.liferay\\.com).*");
	private static final Map<String, HTTPAuthorization>
		_testrayHTTPAuthorizations = new ConcurrentHashMap<>();
	private static final List<HttpRequestMethod> _updatingHttpRequestMethods =
		Arrays.asList(
			HttpRequestMethod.POST, HttpRequestMethod.PATCH,
			HttpRequestMethod.PUT, HttpRequestMethod.DELETE);

}