/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.io.File;
import java.io.IOException;

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
public class ReportFreshnessMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.<String, String>emptyMap());
	}

	@Test
	public void testExecuteAtOverdueBoundary() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newReportData(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (5400 * 1000),
				"dataGeneratedDate"),
			_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("5400", metrics.get("output.age.seconds"));
	}

	@Test
	public void testExecuteFileURL() throws Exception {
		long generatedTimestamp =
			JenkinsResultsParserUtil.getCurrentTimeMillis() - (600 * 1000);

		File reportDataFile = File.createTempFile("jenkins-data", ".js");

		reportDataFile.deleteOnExit();

		JenkinsResultsParserUtil.write(
			reportDataFile,
			_newReportData(generatedTimestamp, "jenkinsDataGeneratedDate"));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			"file://" + reportDataFile.getAbsolutePath());

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(generatedTimestamp),
			metrics.get("generated.timestamp"));
	}

	@Test
	public void testExecuteGeneratedDateFuture() throws Exception {
		UrlReader urlReader = mockUrlReader();

		long generatedTimestamp =
			JenkinsResultsParserUtil.getCurrentTimeMillis() + (3600 * 1000);

		setUrlReaderOutput(
			_newReportData(generatedTimestamp, "dataGeneratedDate"), _URL,
			urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Report ", _REPORT_NAME, " was generated at ",
				String.valueOf(generatedTimestamp), ", which is in the future"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteGeneratedDateOldestRules() throws Exception {
		UrlReader urlReader = mockUrlReader();

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		long oldestTimestamp = currentTimeMillis - (7200 * 1000);

		setUrlReaderOutput(
			JenkinsResultsParserUtil.combine(
				_newReportData(currentTimeMillis, "dataGeneratedDate"), "\n",
				_newReportData(oldestTimestamp, "dataGeneratedDate"), "\n",
				_newReportData(
					currentTimeMillis - (60 * 1000), "dataGeneratedDate")),
			_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(oldestTimestamp),
			metrics.get("generated.timestamp"));
	}

	@Test
	public void testExecuteGeneratedDateVariant() throws Exception {
		_testExecuteGeneratedDateVariant("dataGeneratedDate");
		_testExecuteGeneratedDateVariant("flakyTestDataGeneratedDate");
		_testExecuteGeneratedDateVariant("jenkinsDataGeneratedDate");
		_testExecuteGeneratedDateVariant("testrayDataGeneratedDate");
	}

	@Test
	public void testExecuteMissingFailureMessage() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(new IOException(), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read ", _URL, ": java.io.IOException"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteMissingGeneratedDate() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(RandomTestUtil.randomString(), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to determine the generated date for report ",
				_REPORT_NAME, " from ", _URL),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	@Test
	public void testExecuteModificationDate() throws Exception {
		UrlReader urlReader = mockUrlReader();

		long generatedTimestamp =
			JenkinsResultsParserUtil.getCurrentTimeMillis() - (600 * 1000);

		setUrlReaderOutput(
			JenkinsResultsParserUtil.combine(
				"var allDurations = {\"id\":\"a\",\"modification_date\":",
				String.valueOf(generatedTimestamp), ",\"title\":\"b\"};"),
			_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(generatedTimestamp),
			metrics.get("generated.timestamp"));
	}

	@Test
	public void testExecuteOK() throws Exception {
		UrlReader urlReader = mockUrlReader();

		long generatedTimestamp =
			JenkinsResultsParserUtil.getCurrentTimeMillis() - (600 * 1000);

		setUrlReaderOutput(
			_newReportData(generatedTimestamp, "dataGeneratedDate"), _URL,
			urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine("Report ", _REPORT_NAME, " is OK"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(generatedTimestamp),
			metrics.get("generated.timestamp"));

		Assert.assertNotNull(metrics.get("output.age.seconds"));

		verifyUrlReaderRead(false, 0, 27000, urlReader);
	}

	@Test
	public void testExecuteOverdueGraceBelowFloor() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newReportData(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (3700 * 1000),
				"dataGeneratedDate"),
			_URL, urlReader);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[overdue.grace]", "60");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
	}

	@Test
	public void testExecuteStale() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newReportData(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (7200 * 1000),
				"dataGeneratedDate"),
			_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Report ", _REPORT_NAME, " output was generated 2 hours ago, ",
				"exceeding its cadence of 1 hour plus a grace period of 30 ",
				"minutes"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("7200", metrics.get("output.age.seconds"));
	}

	@Test
	public void testExecuteStaleWithOverdueGrace() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newReportData(
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(10800 * 1000),
				"dataGeneratedDate"),
			_URL, urlReader);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[overdue.grace]", "3600");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Report ", _REPORT_NAME, " output was generated 3 hours ago, ",
				"exceeding its cadence of 1 hour plus a grace period of 1 ",
				"hour"),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
	}

	@Test
	public void testReportFreshnessMonitor() {
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[cadence]", "-1");
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[cadence]", "0");
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[cadence]", "not-a-number");
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[url]",
			"file:" + RandomTestUtil.randomString());
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[url]",
			"https://" + RandomTestUtil.randomString());
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].parameter[url]", RandomTestUtil.randomString());
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].threshold[overdue.grace]", "-1");
		_testReportFreshnessMonitorInvalidProperty(
			"monitor[a].threshold[overdue.grace]", "not-a-number");

		_testReportFreshnessMonitorMissingProperty(
			"monitor[a].parameter[cadence]");
		_testReportFreshnessMonitorMissingProperty(
			"monitor[a].parameter[report.name]");
		_testReportFreshnessMonitorMissingProperty("monitor[a].parameter[url]");
	}

	@Test
	public void testReportFreshnessMonitorUserInfo() {
		Properties monitorProperties = _newMonitorProperties();

		String password = RandomTestUtil.randomString();

		monitorProperties.setProperty(
			"monitor[a].parameter[url]",
			JenkinsResultsParserUtil.combine(
				"file://", RandomTestUtil.randomString(), ":", password, "@",
				RandomTestUtil.randomString()));

		try {
			_newReportFreshnessMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			String message = illegalArgumentException.getMessage();

			Assert.assertFalse(message.contains(password));
			Assert.assertTrue(message.contains("[REDACTED]"));
		}
	}

	private MonitorResult _execute(Properties monitorProperties) {
		ReportFreshnessMonitor reportFreshnessMonitor =
			_newReportFreshnessMonitor(monitorProperties);

		return reportFreshnessMonitor.execute();
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "3600");
		monitorProperties.setProperty(
			"monitor[a].parameter[report.name]", _REPORT_NAME);
		monitorProperties.setProperty("monitor[a].parameter[url]", _URL);
		monitorProperties.setProperty("monitor[a].type", "report-freshness");

		return monitorProperties;
	}

	private String _newReportData(
		long generatedTimestamp, String variableName) {

		return JenkinsResultsParserUtil.combine(
			"var ", variableName, " = new Date(",
			String.valueOf(generatedTimestamp), ");");
	}

	private ReportFreshnessMonitor _newReportFreshnessMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new ReportFreshnessMonitor(monitorConfigs.get(0));
	}

	private void _testExecuteGeneratedDateVariant(String variableName)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		long generatedTimestamp =
			JenkinsResultsParserUtil.getCurrentTimeMillis() - (600 * 1000);

		setUrlReaderOutput(
			_newReportData(generatedTimestamp, variableName), _URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(generatedTimestamp),
			metrics.get("generated.timestamp"));
	}

	private void _testReportFreshnessMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		try {
			_newReportFreshnessMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testReportFreshnessMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		try {
			_newReportFreshnessMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private static final String _REPORT_NAME = "CI System Status";

	private static final String _URL =
		"file:///" + RandomTestUtil.randomString();

}