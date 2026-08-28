/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayInputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Kevin Yen
 * @author Peter Yoo
 */
public class JenkinsStopBuildUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_setUpBuildProperties(_USERNAME, _PASSWORD);

		_httpServer = HttpServer.create(
			new InetSocketAddress(InetAddress.getByName("localhost"), 0), 0);

		_httpServer.createContext(
			"/",
			httpExchange -> {
				URI requestURI = httpExchange.getRequestURI();

				_postedPaths.add(requestURI.getPath());

				_requestMethods.add(httpExchange.getRequestMethod());

				Headers headers = httpExchange.getRequestHeaders();

				_authorizations.add(headers.getFirst("Authorization"));

				httpExchange.sendResponseHeaders(_responseCode, -1);

				httpExchange.close();
			});

		_httpServer.start();

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		_buildURL = JenkinsResultsParserUtil.combine(
			"http://localhost:", String.valueOf(inetSocketAddress.getPort()),
			"/job/test-job/1/");
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		_httpServer.stop(0);
	}

	@Test
	public void testAbortBuildAlreadyFinished() throws Exception {
		_setUpResultOutputs(0);

		Assert.assertEquals(
			JenkinsStopBuildUtil.AbortResult.ALREADY_FINISHED, _abortBuild());

		Assert.assertEquals(_postedPaths.toString(), 0, _postedPaths.size());
	}

	@Test
	public void testAbortBuildMissingCredentials() throws Exception {
		_setUpBuildProperties(RandomTestUtil.randomString(), null);

		_setUpResultOutputs(_MAXIMUM_RESULT_READS);

		try {
			_abortBuild();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message, message.contains("jenkins.admin.user.token"));
		}

		Assert.assertEquals(_postedPaths.toString(), 0, _postedPaths.size());
	}

	@Test
	public void testAbortBuildNormalizesRemoteURL() throws Exception {
		_setUpResultOutputs(0);

		JenkinsStopBuildUtil.abortBuild(
			"https://test-1-41.liferay.com/job/test-job/1/");

		Assert.assertEquals(_readURLs.toString(), 1, _readURLs.size());

		String readURL = _readURLs.get(0);

		Assert.assertTrue(readURL, readURL.startsWith("http://test-1-41/"));
	}

	@Test
	public void testAbortBuildResultAbsent() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			String.valueOf(new JSONObject()), "tree=result", urlReader);

		try {
			_abortBuild();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message, message.contains("Unable to determine whether"));
		}

		Assert.assertEquals(_postedPaths.toString(), 0, _postedPaths.size());
	}

	@Test
	public void testAbortBuildResultTerminal() throws Exception {
		for (String result :
				new String[] {
					"ABORTED", "FAILURE", "NOT_BUILT", "SUCCESS", "UNSTABLE"
				}) {

			UrlReader urlReader = mockUrlReader();

			setUrlReaderOutput(
				String.valueOf(
					new JSONObject(
					).put(
						"result", result
					)),
				"tree=result", urlReader);

			Assert.assertEquals(
				result, JenkinsStopBuildUtil.AbortResult.ALREADY_FINISHED,
				_abortBuild());
		}
	}

	@Test
	public void testAbortBuildStillRunning() throws Exception {
		_setUpResultOutputs(_MAXIMUM_RESULT_READS);

		Assert.assertEquals(
			JenkinsStopBuildUtil.AbortResult.STILL_RUNNING, _abortBuild());

		Assert.assertEquals(_postedPaths.toString(), 1, _postedPaths.size());
	}

	@Test
	public void testAbortBuildStopped() throws Exception {
		_setUpResultOutputs(1);

		Assert.assertEquals(
			JenkinsStopBuildUtil.AbortResult.STOPPED, _abortBuild());

		Assert.assertEquals(_postedPaths.toString(), 1, _postedPaths.size());

		String postedPath = _postedPaths.get(0);

		Assert.assertTrue(
			postedPath, postedPath.startsWith("/job/test-job/1/"));
		Assert.assertTrue(postedPath, postedPath.endsWith("/stop"));

		Assert.assertEquals("POST", _requestMethods.get(0));

		String encodedString = JenkinsStopBuildUtil.encodeAuthorizationFields(
			_USERNAME, _PASSWORD);

		Assert.assertEquals("Basic " + encodedString, _authorizations.get(0));
	}

	@Test
	public void testAbortBuildStoppedOnFinalVerification() throws Exception {
		_setUpResultOutputs(_MAXIMUM_RESULT_READS - 1);

		Assert.assertEquals(
			JenkinsStopBuildUtil.AbortResult.STOPPED, _abortBuild());
	}

	@Test
	public void testAbortBuildUnacceptedResponse() throws Exception {
		_responseCode = 500;

		_setUpResultOutputs(_MAXIMUM_RESULT_READS);

		try {
			_abortBuild();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(message, message.contains("500"));
		}

		Assert.assertEquals(_readURLs.toString(), 1, _readURLs.size());
	}

	@Test
	public void testAbortBuildUnacceptedResponseAtBoundary() throws Exception {
		_responseCode = 400;

		_setUpResultOutputs(_MAXIMUM_RESULT_READS);

		try {
			_abortBuild();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(message, message.contains("400"));
		}
	}

	@Test
	public void testAbortBuildVerifiesAfterRedirect() throws Exception {
		_responseCode = 302;

		_setUpResultOutputs(1);

		Assert.assertEquals(
			JenkinsStopBuildUtil.AbortResult.STOPPED, _abortBuild());
	}

	@Test
	public void testEncodeAuthorizationFields() {
		String encodedString = JenkinsStopBuildUtil.encodeAuthorizationFields(
			"test", "test");

		Assert.assertEquals("dGVzdDp0ZXN0", encodedString);
	}

	private JenkinsStopBuildUtil.AbortResult _abortBuild() throws Exception {
		try (MockedStatic<JenkinsResultsParserUtil> mockedStatic =
				Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			return JenkinsStopBuildUtil.abortBuild(_buildURL);
		}
	}

	private void _setUpBuildProperties(String username, String password) {
		Properties properties = new Properties();

		if (username != null) {
			properties.setProperty("jenkins.admin.user.name", username);
		}

		if (password != null) {
			properties.setProperty("jenkins.admin.user.token", password);
		}

		JenkinsResultsParserUtil.setBuildProperties(properties);
	}

	private void _setUpResultOutputs(int buildingResultsCount)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		AtomicInteger readsCount = new AtomicInteger();

		Mockito.doAnswer(
			invocation -> {
				_readURLs.add(invocation.getArgument(7));

				JSONObject jsonObject = new JSONObject();

				if (readsCount.getAndIncrement() < buildingResultsCount) {
					jsonObject.put("result", JSONObject.NULL);
				}
				else {
					jsonObject.put("result", "ABORTED");
				}

				String string = String.valueOf(jsonObject);

				return new ByteArrayInputStream(string.getBytes());
			}
		).when(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.argThat(
				readURL -> (readURL != null) && readURL.contains("tree=result"))
		);
	}

	private static final int _MAXIMUM_RESULT_READS = 7;

	private static final String _PASSWORD = RandomTestUtil.randomString();

	private static final String _USERNAME = RandomTestUtil.randomString();

	private final List<String> _authorizations = new ArrayList<>();
	private String _buildURL;
	private HttpServer _httpServer;
	private final List<String> _postedPaths = new ArrayList<>();
	private final List<String> _readURLs = new ArrayList<>();
	private final List<String> _requestMethods = new ArrayList<>();
	private int _responseCode = 200;

}