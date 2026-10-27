/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BaseURLReaderTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testToInputStream() throws Exception {
		mockURLReaders();

		setURLReaderOutput(_STANDARD_OUT, _URL);

		try (InputStream inputStream = JenkinsResultsParserUtil.toInputStream(
				_URL, false)) {

			Assert.assertEquals(
				_STANDARD_OUT,
				JenkinsResultsParserUtil.readInputStream(inputStream));
		}
	}

	@Test
	public void testToJSONArray() throws Exception {
		mockURLReaders();

		JSONArray jsonArray = new JSONArray();

		jsonArray.put("first");
		jsonArray.put("second");

		setURLReaderOutput(String.valueOf(jsonArray), _URL);

		JSONArray readJSONArray = JenkinsResultsParserUtil.toJSONArray(
			_URL, false, _MAX_RETRIES, null, 0, 0);

		Assert.assertEquals("first", readJSONArray.getString(0));
		Assert.assertEquals(2, readJSONArray.length());

		verifyURLReaderAttemptsCount(1, _URL);

		mockURLReaders();

		setURLReaderOutput("not json at all", _URL);

		IOException ioException = Assert.assertThrows(
			IOException.class,
			() -> JenkinsResultsParserUtil.toJSONArray(
				_URL, false, _MAX_RETRIES, null, 0, 0));

		Assert.assertEquals(
			"Unable to create a JSON array from the response body",
			ioException.getMessage());

		verifyURLReaderAttemptsCount(_MAX_RETRIES + 1, _URL);
	}

	@Test
	public void testToJSONObject() throws Exception {
		mockURLReaders();

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("id", 7800);

		setURLReaderOutput(String.valueOf(jsonObject), _URL);

		JSONObject readJSONObject = JenkinsResultsParserUtil.toJSONObject(
			_URL, false, _MAX_RETRIES, 0, 0);

		Assert.assertEquals(7800, readJSONObject.getInt("id"));

		verifyURLReaderAttemptsCount(1, _URL);

		mockURLReaders();

		setURLReaderOutput("not json at all", _URL);

		IOException ioException = Assert.assertThrows(
			IOException.class,
			() -> JenkinsResultsParserUtil.toJSONObject(
				_URL, false, _MAX_RETRIES, 0, 0));

		Assert.assertEquals(
			"Unable to create a JSON object from the response body",
			ioException.getMessage());

		Throwable throwable = ioException.getCause();

		Assert.assertTrue(throwable instanceof JSONException);

		verifyURLReaderAttemptsCount(_MAX_RETRIES + 1, _URL);

		mockURLReaders();

		setURLReaderException(new FileNotFoundException(_URL), _URL);

		FileNotFoundException fileNotFoundException = Assert.assertThrows(
			FileNotFoundException.class,
			() -> JenkinsResultsParserUtil.toJSONObject(
				_URL, false, _MAX_RETRIES, 0, 0));

		Assert.assertEquals(_URL, fileNotFoundException.getMessage());

		verifyURLReaderAttemptsCount(1, _URL);

		JenkinsMasterTestUtil.getJenkinsCohortProperties("test-9", 1);

		mockURLReaders();

		String url = "file:/tmp/" + RandomTestUtil.randomString() + ".json";

		setURLReaderOutput(String.valueOf(jsonObject), url);

		readJSONObject = JenkinsResultsParserUtil.toJSONObject(
			url,
			new JenkinsResultsParserUtil.ClientCredentialsHTTPAuthorization(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new URL("https://test.liferay.com/o/oauth2/token")));

		Assert.assertEquals(7800, readJSONObject.getInt("id"));

		verifyURLReaderAttemptsCount(0, "/o/oauth2/token");
	}

	@Test
	public void testToString() throws Exception {
		_testToString("");
		_testToString(_STANDARD_OUT);

		mockURLReaders();

		setURLReaderOutput("", _URL);

		IOException ioException = Assert.assertThrows(
			IOException.class,
			() -> JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0, true));

		String message = ioException.getMessage();

		Assert.assertTrue(
			message, message.startsWith("Unable to read a response body"));

		verifyURLReaderAttemptsCount(_MAX_RETRIES + 1, _URL);
	}

	@Test
	public void testToStringFailure() throws Exception {
		_testToStringFailure(1, IOException.class, 400);
		_testToStringFailure(_MAX_RETRIES + 1, IOException.class, 403);
		_testToStringFailure(_MAX_RETRIES + 1, IOException.class, 408);

		RuntimeException runtimeException = _testToStringFailure(
			1, RuntimeException.class, 422);

		Throwable throwable = runtimeException.getCause();

		Assert.assertTrue(throwable instanceof IOException);

		_testToStringFailure(_MAX_RETRIES + 1, IOException.class, 429);
		_testToStringFailure(_MAX_RETRIES + 1, IOException.class, 500);

		mockURLReaders();

		setURLReaderException(new FileNotFoundException(_URL), _URL);

		Assert.assertThrows(
			FileNotFoundException.class,
			() -> JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0));

		verifyURLReaderAttemptsCount(1, _URL);

		mockURLReaders();

		setURLReaderException(
			new SocketTimeoutException("Read timed out"), _URL);

		Assert.assertThrows(
			SocketTimeoutException.class,
			() -> JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0));

		verifyURLReaderAttemptsCount(_MAX_RETRIES + 1, _URL);

		List<HttpURLConnection> httpURLConnections = new ArrayList<>();

		mockURLReaders();

		for (BaseURLReader<?> baseURLReader : getBaseURLReaders()) {
			Mockito.doAnswer(
				invocation -> {
					HttpURLConnection httpURLConnection = Mockito.mock(
						HttpURLConnection.class);

					Mockito.doThrow(
						new SocketTimeoutException("Read timed out")
					).when(
						httpURLConnection
					).getInputStream();

					httpURLConnections.add(httpURLConnection);

					return httpURLConnection;
				}
			).when(
				baseURLReader
			).openURLConnection(
				Mockito.any(), Mockito.anyBoolean(), Mockito.any(),
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.argThat(
					readURL -> (readURL != null) && readURL.contains(_URL))
			);
		}

		Assert.assertThrows(
			SocketTimeoutException.class,
			() -> JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0));

		verifyURLReaderAttemptsCount(_MAX_RETRIES + 1, _URL);

		for (HttpURLConnection httpURLConnection : httpURLConnections) {
			Mockito.verify(
				httpURLConnection, Mockito.never()
			).getResponseCode();
		}
	}

	@Test
	public void testToStringRetries() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"github.access.token", RandomTestUtil.randomString());

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		_testToStringRetries(Arrays.asList(5000L, 25000L, 60000L), 3, 5);
		_testToStringRetries(Collections.emptyList(), 0, 0);
	}

	@Test
	public void testToStringWithEncodedURL() throws Exception {
		mockURLReaders();

		String url = JenkinsResultsParserUtil.combine(
			"http://", RandomTestUtil.randomString(),
			"?A=a%2Bb&PORTAL%20BUILD%20NOTES=x#fragment");

		setURLReaderOutput(_STANDARD_OUT, url);

		JenkinsResultsParserUtil.toString(url, false, _MAX_RETRIES, 0, 0);

		verifyURLReaderAttemptsCount(1, url);
	}

	@Test
	public void testToStringWithIllegalURL() throws Exception {
		mockURLReaders();

		String host = RandomTestUtil.randomString();

		String normalizedURL = JenkinsResultsParserUtil.combine(
			"http://", host, "/a%20b");

		setURLReaderOutput(_STANDARD_OUT, normalizedURL);

		JenkinsResultsParserUtil.toString(
			JenkinsResultsParserUtil.combine("http://", host, "/a b"), false,
			_MAX_RETRIES, 0, 0);

		verifyURLReaderAttemptsCount(1, normalizedURL);
	}

	private void _testToString(String standardOut) throws Exception {
		mockURLReaders();

		setURLReaderOutput(standardOut, _URL);

		Assert.assertEquals(
			standardOut,
			JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0, false));

		verifyURLReaderAttemptsCount(1, _URL);
	}

	private <T extends Throwable> T _testToStringFailure(
			int expectedAttemptsCount, Class<T> expectedThrowableClass,
			int responseCode)
		throws Exception {

		mockURLReaders();

		setURLReaderResponseCode(responseCode, _URL);

		T throwable = Assert.assertThrows(
			expectedThrowableClass,
			() -> JenkinsResultsParserUtil.toString(
				_URL, false, _MAX_RETRIES, 0, 0));

		verifyURLReaderAttemptsCount(expectedAttemptsCount, _URL);

		return throwable;
	}

	private void _testToStringRetries(
			List<Long> expectedSleepDurations, int maxRetries, int retryPeriod)
		throws Exception {

		mockURLReaders();

		String url = "https://api.github.com/" + RandomTestUtil.randomString();

		setURLReaderResponseCode(403, url);

		Assert.assertThrows(
			GitHubSecondaryRateLimitRuntimeException.class,
			() -> JenkinsResultsParserUtil.toString(
				url, false, maxRetries, retryPeriod, 0));

		verifyURLReaderAttemptsCount(maxRetries + 1, url);

		verifyURLReaderSleepDurations(expectedSleepDurations);
	}

	private static final int _MAX_RETRIES = 2;

	private static final String _STANDARD_OUT = "Hello, World!\n";

	private static final String _URL = "http://test.liferay.com";

}