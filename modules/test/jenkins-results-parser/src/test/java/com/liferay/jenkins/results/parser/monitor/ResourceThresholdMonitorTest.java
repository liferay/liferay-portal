/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

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
public class ResourceThresholdMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.<String, String>emptyMap());
	}

	@Test
	public void testExecuteCapacityBound() throws Exception {
		_testExecuteCapacityBound(
			"1.125899906842623E15", MonitorResult.Status.OK);
		_testExecuteCapacityBound(
			"1.125899906842624E15", MonitorResult.Status.UNKNOWN);
	}

	@Test
	public void testExecuteDisk() throws Exception {
		Properties monitorProperties = _newScrapeProperties(
			"disk", _newFileStoreScrape("3.7194141696E10", "7.7849452544E10"),
			"file.store", MonitorTestUtil.FILE_STORE);

		monitorProperties.setProperty("monitor[a].threshold[warn]", "90");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		String disk = metrics.get("disk");

		Assert.assertTrue(disk, disk.startsWith("52.2"));

		String message = monitorResult.getMessage();

		Assert.assertTrue(message, message.contains("52.2%"));
		Assert.assertTrue(message, message.contains("within its thresholds"));
		Assert.assertTrue(
			message, message.contains(MonitorTestUtil.FILE_STORE));
	}

	@Test
	public void testExecuteDiskImplausibleCapacity() throws Exception {
		Properties monitorProperties = _newScrapeProperties(
			"disk",
			_newFileStoreScrape("3.7194141696E10", "9.223372036853727E18"),
			"file.store", MonitorTestUtil.FILE_STORE);

		monitorProperties.setProperty("monitor[a].threshold[warn]", "1");

		_assertUnknown(monitorProperties);
	}

	@Test
	public void testExecuteExecutorUtilization() throws Exception {
		String label = RandomTestUtil.randomString();

		Properties monitorProperties = _newScrapeProperties(
			"executor.utilization",
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, "default_jenkins_executors_busy", "3.0"),
				MonitorTestUtil.newSample(
					"label", label, "default_jenkins_executors_defined",
					"4.0")),
			"label", label);

		monitorProperties.setProperty("monitor[a].threshold[critical]", "90");
		monitorProperties.setProperty("monitor[a].threshold[warn]", "70");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("75.0", metrics.get("executor.utilization"));
	}

	@Test
	public void testExecuteMetricAbsent() throws Exception {
		Properties monitorProperties = _newScrapeProperties(
			"queue.depth",
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", RandomTestUtil.randomString(),
					"default_jenkins_executors_queue_length", "5.0")),
			"label", RandomTestUtil.randomString());

		monitorProperties.setProperty("monitor[a].threshold[warn]", "25");

		_assertUnknown(monitorProperties);
	}

	@Test
	public void testExecuteMetricAbsentPartial() throws Exception {
		String label = RandomTestUtil.randomString();

		_assertUnknownWithThreshold(
			_newScrapeProperties(
				"disk",
				MonitorTestUtil.newScrape(
					MonitorTestUtil.newSample(
						"file_store", MonitorTestUtil.FILE_STORE,
						"default_jenkins_file_store_capacity_bytes",
						"7.7849452544E10")),
				"file.store", MonitorTestUtil.FILE_STORE));
		_assertUnknownWithThreshold(
			_newScrapeProperties(
				"executor.utilization",
				MonitorTestUtil.newScrape(
					MonitorTestUtil.newSample(
						"label", label, "default_jenkins_executors_busy",
						"3.0")),
				"label", label));
	}

	@Test
	public void testExecuteQueueDepth() throws Exception {
		String label = RandomTestUtil.randomString();

		Properties monitorProperties = _newScrapeProperties(
			"queue.depth",
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, "default_jenkins_executors_queue_length",
					"30.0")),
			"label", label);

		monitorProperties.setProperty("monitor[a].threshold[warn]", "25");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("30.0", metrics.get("queue.depth"));

		String message = monitorResult.getMessage();

		Assert.assertFalse(message, message.contains("%"));
		Assert.assertTrue(
			message, message.contains("exceeding its threshold of 25"));
		Assert.assertTrue(message, message.contains("is at 30,"));
	}

	@Test
	public void testExecuteRAM() throws Exception {
		_testExecuteRAM(
			MonitorResult.Status.OK, "26.2", 23791372L, 32249488L, "50");
		_testExecuteRAM(
			MonitorResult.Status.WARN, "90.0", 3224948L, 32249488L, "50");
	}

	@Test
	public void testExecuteRAMMessage() throws Exception {
		_mockMemoryInfo(MonitorTestUtil.newMemoryInfo(3224948L, 32249488L));

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(masterName, "ram");

		monitorProperties.setProperty("monitor[a].threshold[warn]", "50");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message, message.contains("The RAM metric on " + masterName));
		Assert.assertTrue(
			message, message.contains("exceeding its threshold of 50%"));
		Assert.assertTrue(message, message.contains("is at 90.0%"));
	}

	@Test
	public void testExecuteRAMStraySelector() throws Exception {
		_mockMemoryInfo(MonitorTestUtil.newMemoryInfo(23791372L, 32249488L));

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(masterName, "ram");

		monitorProperties.setProperty(
			"monitor[a].parameter[label]", RandomTestUtil.randomString());
		monitorProperties.setProperty("monitor[a].threshold[warn]", "50");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		String ram = metrics.get("ram");

		Assert.assertTrue(ram, ram.startsWith("26.2"));
	}

	@Test
	public void testExecuteRAMUnavailable() throws Exception {
		_assertRAMUnavailable("");
		_assertRAMUnavailable(MonitorTestUtil.newScrape("MemAvailable: 8 kB"));
		_assertRAMUnavailable(MonitorTestUtil.newScrape("MemTotal: 8 kB"));
		_assertRAMUnavailable(RandomTestUtil.randomString());
	}

	@Test
	public void testExecuteReadFailure() throws Exception {
		String failureMessage = RandomTestUtil.randomString();

		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(
			new IOException(failureMessage), "/prometheus", urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(
			masterName, "queue.depth");

		monitorProperties.setProperty(
			"monitor[a].parameter[label]", RandomTestUtil.randomString());
		monitorProperties.setProperty("monitor[a].threshold[warn]", "25");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());

		String message = monitorResult.getMessage();

		Assert.assertTrue(message, message.contains("queue depth metric"));
		Assert.assertTrue(message, message.contains(failureMessage));
		Assert.assertTrue(message, message.contains(masterName));
	}

	@Test
	public void testExecuteThresholds() throws Exception {
		_testExecuteThresholds(
			"90", MonitorResult.Status.CRITICAL, "90.0", "80");
		_testExecuteThresholds(
			"90", MonitorResult.Status.CRITICAL, "95.0", null);
		_testExecuteThresholds(
			"90", MonitorResult.Status.CRITICAL, "99.0", "80");
		_testExecuteThresholds("90", MonitorResult.Status.OK, "20.0", "80");
		_testExecuteThresholds("90", MonitorResult.Status.OK, "79.0", "80");
		_testExecuteThresholds("90", MonitorResult.Status.OK, "85.0", null);
		_testExecuteThresholds("90", MonitorResult.Status.WARN, "80.0", "80");
		_testExecuteThresholds("90", MonitorResult.Status.WARN, "89.0", "80");
		_testExecuteThresholds(null, MonitorResult.Status.WARN, "95.0", "80");
	}

	@Test
	public void testExecuteThresholdsInverted() throws Exception {
		_testExecuteThresholds(
			"50", MonitorResult.Status.CRITICAL, "60.0", "80");
		_testExecuteThresholds(
			"50", MonitorResult.Status.CRITICAL, "90.0", "80");
		_testExecuteThresholds("50", MonitorResult.Status.OK, "40.0", "80");
	}

	@Test
	public void testExecuteUnknownMessage() throws Exception {
		String label = RandomTestUtil.randomString();

		Properties monitorProperties = _newScrapeProperties(
			"queue.depth",
			MonitorTestUtil.newScrape(MonitorTestUtil.newMetricName() + " 1.0"),
			"label", label);

		monitorProperties.setProperty("monitor[a].threshold[warn]", "25");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(message, message.contains("queue depth metric"));
		Assert.assertTrue(message, message.contains(label));
	}

	@Test
	public void testExecuteZeroDenominator() throws Exception {
		_assertUnknownWithThreshold(
			_newScrapeProperties(
				"disk", _newFileStoreScrape("0.0", "0.0"), "file.store",
				MonitorTestUtil.FILE_STORE));

		String label = RandomTestUtil.randomString();

		_assertUnknownWithThreshold(
			_newScrapeProperties(
				"executor.utilization",
				MonitorTestUtil.newScrape(
					MonitorTestUtil.newSample(
						"label", label, "default_jenkins_executors_busy",
						"0.0"),
					MonitorTestUtil.newSample(
						"label", label, "default_jenkins_executors_defined",
						"0.0")),
				"label", label));

		_assertRAMUnavailable(MonitorTestUtil.newMemoryInfo(0L, 0L));
	}

	@Test
	public void testNewMonitorInvalidMetric() {
		_testNewMonitorInvalidMetric("");
		_testNewMonitorInvalidMetric(RandomTestUtil.randomString());
	}

	@Test
	public void testNewMonitorInvalidThreshold() {
		_testNewMonitorInvalidThreshold("critical", "-1");
		_testNewMonitorInvalidThreshold(
			"critical", RandomTestUtil.randomString());
		_testNewMonitorInvalidThreshold("warn", "-1");
		_testNewMonitorInvalidThreshold("warn", RandomTestUtil.randomString());
	}

	@Test
	public void testNewMonitorMissingProperty() {
		_testNewMonitorMissingProperty("disk", "file.store");
		_testNewMonitorMissingProperty("executor.utilization", "label");
		_testNewMonitorMissingProperty("queue.depth", "label");
		_testNewMonitorMissingProperty("ram", "master.name");
	}

	@Test
	public void testNewMonitorMissingThresholds() {
		Properties monitorProperties = _newMonitorProperties(
			MonitorTestUtil.newJenkinsMasterName(), "ram");

		_testNewMonitorExpectedIllegalArgumentException(monitorProperties);
	}

	@Test
	public void testNewMonitorMissingThresholdsMessage() {
		Properties monitorProperties = _newMonitorProperties(
			MonitorTestUtil.newJenkinsMasterName(), "ram");

		try {
			_newMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			String message = illegalArgumentException.getMessage();

			Assert.assertTrue(
				message, message.contains("monitor[a].threshold[critical]"));
			Assert.assertTrue(
				message, message.contains("monitor[a].threshold[warn]"));
		}
	}

	private void _assertRAMUnavailable(String memoryInfo) throws Exception {
		_mockMemoryInfo(memoryInfo);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(masterName, "ram");

		monitorProperties.setProperty("monitor[a].threshold[warn]", "50");

		_assertUnknown(monitorProperties);

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(
			"Unable to determine the RAM metric for " + masterName,
			monitorResult.getMessage());
	}

	private void _assertUnknown(Properties monitorProperties) {
		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertTrue(metrics.isEmpty());
	}

	private void _assertUnknownWithThreshold(Properties monitorProperties) {
		monitorProperties.setProperty("monitor[a].threshold[warn]", "50");

		_assertUnknown(monitorProperties);
	}

	private MonitorResult _execute(Properties monitorProperties) {
		Monitor monitor = _newMonitor(monitorProperties);

		return monitor.execute();
	}

	private void _mockMemoryInfo(String memoryInfo) throws Exception {
		setShellCommandOutput("cat /proc/meminfo", mockShell(), memoryInfo);
	}

	private String _newFileStoreScrape(String available, String capacity) {
		return MonitorTestUtil.newScrape(
			MonitorTestUtil.newSample(
				"file_store", MonitorTestUtil.FILE_STORE,
				"default_jenkins_file_store_available_bytes", available),
			MonitorTestUtil.newSample(
				"file_store", MonitorTestUtil.FILE_STORE,
				"default_jenkins_file_store_capacity_bytes", capacity));
	}

	private Monitor _newMonitor(Properties monitorProperties) {
		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return MonitorFactory.newMonitor(monitorConfigs.get(0));
	}

	private Properties _newMonitorProperties(String masterName, String metric) {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", masterName);
		monitorProperties.setProperty("monitor[a].parameter[metric]", metric);
		monitorProperties.setProperty("monitor[a].type", "resource-threshold");

		return monitorProperties;
	}

	private Properties _newScrapeProperties(
			String metric, String scrape, String selectorName,
			String selectorValue)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(scrape, "/prometheus", urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(
			masterName, metric);

		monitorProperties.setProperty(
			"monitor[a].parameter[" + selectorName + "]", selectorValue);

		return monitorProperties;
	}

	private void _testExecuteCapacityBound(
			String capacity, MonitorResult.Status expectedStatus)
		throws Exception {

		Properties monitorProperties = _newScrapeProperties(
			"disk", _newFileStoreScrape("1.0E15", capacity), "file.store",
			MonitorTestUtil.FILE_STORE);

		monitorProperties.setProperty("monitor[a].threshold[warn]", "99");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testExecuteRAM(
			MonitorResult.Status expectedStatus, String expectedValue,
			long memoryAvailable, long memoryTotal, String warnThreshold)
		throws Exception {

		_mockMemoryInfo(
			MonitorTestUtil.newMemoryInfo(memoryAvailable, memoryTotal));

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		Properties monitorProperties = _newMonitorProperties(masterName, "ram");

		monitorProperties.setProperty(
			"monitor[a].threshold[warn]", warnThreshold);

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedStatus, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		String ram = metrics.get("ram");

		Assert.assertTrue(ram, ram.startsWith(expectedValue));
	}

	private void _testExecuteThresholds(
			String criticalThreshold, MonitorResult.Status expectedStatus,
			String value, String warnThreshold)
		throws Exception {

		String label = RandomTestUtil.randomString();

		Properties monitorProperties = _newScrapeProperties(
			"queue.depth",
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, "default_jenkins_executors_queue_length",
					value)),
			"label", label);

		if (criticalThreshold != null) {
			monitorProperties.setProperty(
				"monitor[a].threshold[critical]", criticalThreshold);
		}

		if (warnThreshold != null) {
			monitorProperties.setProperty(
				"monitor[a].threshold[warn]", warnThreshold);
		}

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testNewMonitorExpectedIllegalArgumentException(
		Properties monitorProperties) {

		try {
			_newMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testNewMonitorInvalidMetric(String value) {
		Properties monitorProperties = _newMonitorProperties(
			MonitorTestUtil.newJenkinsMasterName(), "ram");

		monitorProperties.setProperty("monitor[a].parameter[metric]", value);
		monitorProperties.setProperty("monitor[a].threshold[warn]", "80");

		_testNewMonitorExpectedIllegalArgumentException(monitorProperties);
	}

	private void _testNewMonitorInvalidThreshold(String name, String value) {
		Properties monitorProperties = _newMonitorProperties(
			MonitorTestUtil.newJenkinsMasterName(), "ram");

		monitorProperties.setProperty(
			"monitor[a].threshold[" + name + "]", value);

		_testNewMonitorExpectedIllegalArgumentException(monitorProperties);
	}

	private void _testNewMonitorMissingProperty(String metric, String name) {
		Properties monitorProperties = _newMonitorProperties(
			MonitorTestUtil.newJenkinsMasterName(), metric);

		monitorProperties.remove("monitor[a].parameter[" + name + "]");
		monitorProperties.setProperty("monitor[a].threshold[warn]", "80");

		_testNewMonitorExpectedIllegalArgumentException(monitorProperties);
	}

}