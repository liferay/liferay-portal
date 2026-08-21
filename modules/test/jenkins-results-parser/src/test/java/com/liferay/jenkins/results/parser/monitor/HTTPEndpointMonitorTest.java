/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.SocketTimeoutException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class HTTPEndpointMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.<String, String>emptyMap());
	}

	@Test
	public void testExecuteLatencyMaximum() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_MILLIS_LATENCY, RandomTestUtil.randomString(), _URL, urlReader);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[latency.maximum.millis]", "1");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL, " responded in ",
				metrics.get("latency.millis"),
				" ms, exceeding its maximum latency of 1 ms"),
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteLatencyMaximumWithinBound() throws Exception {
		_testExecuteLatencyMaximumWithinBound("0");
		_testExecuteLatencyMaximumWithinBound("60000");
		_testExecuteLatencyMaximumWithinBound(null);
	}

	@Test
	public void testExecuteMissingFailureMessage() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(new IOException(), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": java.io.IOException"),
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteNotFound() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(new FileNotFoundException(_URL), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL, " was not found"),
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteOK() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(RandomTestUtil.randomString(), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
		testEquals(
			JenkinsResultsParserUtil.combine("Endpoint ", _URL, " is OK"),
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertNotNull(metrics.get("latency.millis"));

		verifyUrlReaderRead(false, 0, 27000, urlReader);
	}

	@Test
	public void testExecuteResponseCode() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(
			new IOException(
				"Server returned HTTP response code: 503 for URL: " + _URL),
			_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _URL, " returned the response code 503"),
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteTimeout() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(
			new SocketTimeoutException("Read timed out"), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": Read timed out"),
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testHTTPEndpointMonitor() {
		_testHTTPEndpointMonitorInvalidProperty(
			"monitor[a].parameter[url]",
			"file:///" + RandomTestUtil.randomString());
		_testHTTPEndpointMonitorInvalidProperty(
			"monitor[a].parameter[url]", RandomTestUtil.randomString());
		_testHTTPEndpointMonitorInvalidProperty(
			"monitor[a].threshold[latency.maximum.millis]", "-1");
		_testHTTPEndpointMonitorInvalidProperty(
			"monitor[a].threshold[latency.maximum.millis]", "not-a-number");

		_testHTTPEndpointMonitorMissingProperty("monitor[a].parameter[url]");
	}

	@Test
	public void testHTTPEndpointMonitorAtSignBeyondAuthority() {
		_testHTTPEndpointMonitorAtSignBeyondAuthority("#");
		_testHTTPEndpointMonitorAtSignBeyondAuthority("/");
		_testHTTPEndpointMonitorAtSignBeyondAuthority("?");
	}

	@Test
	public void testHTTPEndpointMonitorUserInfo() {
		_testHTTPEndpointMonitorUserInfo("");
		_testHTTPEndpointMonitorUserInfo("//");
		_testHTTPEndpointMonitorUserInfo("htps://");
		_testHTTPEndpointMonitorUserInfo("https://");
	}

	private MonitorResult _execute(Properties monitorProperties) {
		HTTPEndpointMonitor httpEndpointMonitor = _newHTTPEndpointMonitor(
			monitorProperties);

		return httpEndpointMonitor.execute();
	}

	private HTTPEndpointMonitor _newHTTPEndpointMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new HTTPEndpointMonitor(monitorConfigs.get(0));
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].parameter[url]", _URL);
		monitorProperties.setProperty("monitor[a].type", "http-endpoint");

		return monitorProperties;
	}

	private void _testExecuteLatencyMaximumWithinBound(
			String latencyMaximumMillis)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_MILLIS_LATENCY, RandomTestUtil.randomString(), _URL, urlReader);

		Properties monitorProperties = _newMonitorProperties();

		if (latencyMaximumMillis != null) {
			monitorProperties.setProperty(
				"monitor[a].threshold[latency.maximum.millis]",
				latencyMaximumMillis);
		}

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	private void _testHTTPEndpointMonitorAtSignBeyondAuthority(
		String separator) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			JenkinsResultsParserUtil.combine(
				"https://", RandomTestUtil.randomString(), separator,
				RandomTestUtil.randomString(), "@",
				RandomTestUtil.randomString()));

		_newHTTPEndpointMonitor(monitorProperties);
	}

	private String _testHTTPEndpointMonitorExpectedIllegalArgumentException(
		Properties monitorProperties) {

		try {
			_newHTTPEndpointMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return illegalArgumentException.getMessage();
		}

		return null;
	}

	private void _testHTTPEndpointMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		_testHTTPEndpointMonitorExpectedIllegalArgumentException(
			monitorProperties);
	}

	private void _testHTTPEndpointMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		_testHTTPEndpointMonitorExpectedIllegalArgumentException(
			monitorProperties);
	}

	private void _testHTTPEndpointMonitorUserInfo(String urlPrefix) {
		Properties monitorProperties = _newMonitorProperties();

		String password = RandomTestUtil.randomString();

		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			JenkinsResultsParserUtil.combine(
				urlPrefix, RandomTestUtil.randomString(), ":", password, "@",
				RandomTestUtil.randomString()));

		String message =
			_testHTTPEndpointMonitorExpectedIllegalArgumentException(
				monitorProperties);

		Assert.assertFalse(message.contains(password));
		Assert.assertTrue(message.contains("[REDACTED]"));
	}

	private static final long _MILLIS_LATENCY = 50;

	private static final String _URL =
		"https://" + RandomTestUtil.randomString();

}