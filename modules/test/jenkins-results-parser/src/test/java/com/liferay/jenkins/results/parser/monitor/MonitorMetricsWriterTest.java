/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class MonitorMetricsWriterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testMonitorMetricsWriterDuplicateIds() {
		try {
			new MonitorMetricsWriter(
				new File(
					temporaryFolder.getRoot(), RandomTestUtil.randomString()),
				new MonitorResultStore(),
				Arrays.<Monitor>asList(
					new TestMonitor(
						_newMonitorConfig(
							"disk", MonitorConfig.Severity.MEDIUM,
							RandomTestUtil.randomString())),
					new TestMonitor(
						_newMonitorConfig(
							"disk", MonitorConfig.Severity.MEDIUM,
							RandomTestUtil.randomString()))));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testWrite() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		String message = RandomTestUtil.randomString();

		monitorResultStore.store(
			"disk",
			new MonitorResult(
				message, null, MonitorResult.Status.CRITICAL, 1750000000999L));

		monitorResultStore.store(
			"queue",
			_newMonitorResult(MonitorResult.Status.OK, 1749999000000L));

		File metricsFile = new File(
			temporaryFolder.getRoot(),
			RandomTestUtil.randomString() + "/" +
				RandomTestUtil.randomString());

		_write(metricsFile, monitorResultStore, _newMonitors());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"# HELP monitor_duration_seconds Seconds the last monitor run ",
				"took, 0 if never run or not measured\n",
				"# TYPE monitor_duration_seconds gauge\n",
				"monitor_duration_seconds{monitor=\"disk\",",
				"severity=\"high\",type=\"resource-threshold\"} 0.0\n",
				"monitor_duration_seconds{monitor=\"queue\",",
				"severity=\"medium\",type=\"job-health\"} 0.0\n",
				"monitor_duration_seconds{monitor=\"testray\",",
				"severity=\"low\",type=\"external-status\"} 0.0\n",
				"# HELP monitor_heartbeat_timestamp_seconds Unix timestamp of ",
				"the last metrics write\n",
				"# TYPE monitor_heartbeat_timestamp_seconds gauge\n",
				"monitor_heartbeat_timestamp_seconds 1.75E9\n",
				"# HELP monitor_last_run_timestamp_seconds Unix ",
				"timestamp of the last monitor run, 0 if never run\n",
				"# TYPE monitor_last_run_timestamp_seconds gauge\n",
				"monitor_last_run_timestamp_seconds{monitor=\"disk\",",
				"severity=\"high\",type=\"resource-threshold\"} 1.75E9\n",
				"monitor_last_run_timestamp_seconds{monitor=\"queue\",",
				"severity=\"medium\",type=\"job-health\"} 1.749999E9\n",
				"monitor_last_run_timestamp_seconds{monitor=\"testray\",",
				"severity=\"low\",type=\"external-status\"} 0.0\n",
				"# HELP monitor_message_info Message of the last monitor run, ",
				"for each monitor that is not OK\n",
				"# TYPE monitor_message_info gauge\n",
				"monitor_message_info{message=\"", message,
				"\",monitor=\"disk\"} 1.0\n",
				"# HELP monitor_status Monitor status severity rank, ",
				"0 OK, 1 UNKNOWN, 2 WARN, 3 CRITICAL\n",
				"# TYPE monitor_status gauge\n",
				"monitor_status{monitor=\"disk\",severity=\"high\",",
				"type=\"resource-threshold\"} 3.0\n",
				"monitor_status{monitor=\"queue\",severity=\"medium\",",
				"type=\"job-health\"} 0.0\n",
				"monitor_status{monitor=\"testray\",severity=\"low\",",
				"type=\"external-status\"} 1.0\n"),
			read(metricsFile));
	}

	@Test
	public void testWriteDurationSeconds() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			_newMonitorResult(
				250, MonitorResult.Status.OK, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					_newMonitorConfig(
						"disk", MonitorConfig.Severity.HIGH,
						"resource-threshold"))));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				"monitor_duration_seconds{monitor=\"disk\",severity=\"high\"," +
					"type=\"resource-threshold\"} 0.25\n"));
	}

	@Test
	public void testWriteEmptyMonitors() throws Exception {
		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, new MonitorResultStore(),
			Collections.<Monitor>emptyList());

		String content = read(metricsFile);

		Assert.assertFalse(content, content.contains("{"));
		Assert.assertTrue(
			content,
			content.contains("\nmonitor_heartbeat_timestamp_seconds 1.75E9\n"));
	}

	@Test
	public void testWriteEmptyType() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			_newMonitorResult(
				MonitorResult.Status.OK, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					_newMonitorConfig(
						"disk", MonitorConfig.Severity.HIGH, ""))));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				"monitor_status{monitor=\"disk\",severity=\"high\"," +
					"type=\"unknown\"} 0.0\n"));
	}

	@Test
	public void testWriteEscapesLabelValues() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"a\"b\\c",
			_newMonitorResult(
				MonitorResult.Status.WARN, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					_newMonitorConfig(
						"a\"b\\c", MonitorConfig.Severity.MEDIUM, "d\ne"))));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				"monitor_status{monitor=\"a\\\"b\\\\c\"," +
					"severity=\"medium\",type=\"d\\ne\"} 2.0\n"));
	}

	@Test
	public void testWriteIgnoresMonitorsAddedAfterConstruction()
		throws Exception {

		List<Monitor> monitors = new ArrayList<>();

		monitors.add(
			new TestMonitor(
				_newMonitorConfig(
					"disk", MonitorConfig.Severity.MEDIUM,
					RandomTestUtil.randomString())));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		MonitorMetricsWriter monitorMetricsWriter = new MonitorMetricsWriter(
			metricsFile, new MonitorResultStore(), monitors);

		monitors.add(
			new TestMonitor(
				_newMonitorConfig(
					"queue", MonitorConfig.Severity.MEDIUM,
					RandomTestUtil.randomString())));

		_write(monitorMetricsWriter);

		String content = read(metricsFile);

		Assert.assertTrue(content, content.contains("monitor=\"disk\""));
		Assert.assertFalse(content, content.contains("monitor=\"queue\""));
	}

	@Test
	public void testWriteLabels() throws Exception {
		Map<String, String> labelNames = ReflectionTestUtil.getFieldValue(
			MonitorMetricsWriter.class, "_labelNames");

		Set<String> uniqueLabelNames = new HashSet<>(labelNames.values());

		testEquals(labelNames.size(), uniqueLabelNames.size());

		List<String> reservedLabelNames = Arrays.asList(
			"instance", "job", "message", "monitor", "severity", "type");

		for (String labelName : labelNames.values()) {
			Assert.assertFalse(
				labelName, reservedLabelNames.contains(labelName));
			Assert.assertTrue(
				labelName, labelName.matches("[a-zA-Z_][a-zA-Z0-9_]*"));
			Assert.assertFalse(labelName, labelName.startsWith("__"));
		}

		_testWriteLabels(
			"monitor=\"disk\",severity=\"high\",type=\"resource-threshold\"",
			"branch", "", "controller.job.name", "", "device", "", "job.name",
			"", "label", "", "master.name", "", "metric", "", "mount", "",
			"report.name", "", "url", "");

		String branch = RandomTestUtil.randomString();
		String controllerJobName = RandomTestUtil.randomString();
		String device = RandomTestUtil.randomString();
		String jobName = RandomTestUtil.randomString();
		String label = RandomTestUtil.randomString();
		String masterName = RandomTestUtil.randomString();
		String metric = RandomTestUtil.randomString();
		String reportName = RandomTestUtil.randomString();
		String url = RandomTestUtil.randomString();

		_testWriteLabels(
			JenkinsResultsParserUtil.combine(
				"branch=\"", branch, "\",controller_job_name=\"",
				controllerJobName, "\",device=\"", device, "\",job_name=\"",
				jobName, "\",master_name=\"", masterName, "\",metric=\"",
				metric, "\",monitor=\"disk\",",
				"mount=\"/opt/java/jenkins (/dev/root)\",node_label=\"", label,
				"\",report_name=\"", reportName,
				"\",severity=\"high\",type=\"resource-threshold\",url=\"", url,
				"\""),
			"branch", branch, "cadence", RandomTestUtil.randomString(),
			"components", RandomTestUtil.randomString(), "controller.job.name",
			controllerJobName, "device", device, "expected.green",
			RandomTestUtil.randomString(), "job.name", jobName, "label", label,
			"master.name", masterName, "metric", metric, "mount",
			"/opt/java/jenkins (/dev/root)", "portal.repository.name",
			RandomTestUtil.randomString(), "portal.username",
			RandomTestUtil.randomString(), "report.name", reportName, "url",
			url);
	}

	@Test
	public void testWriteMessage() throws Exception {
		for (MonitorResult.Status status : MonitorResult.Status.values()) {
			if (status == MonitorResult.Status.OK) {
				continue;
			}

			String message = RandomTestUtil.randomString();

			_testWriteMessage(message, message, status);
		}

		_testWriteMessage(
			"a\\\"b\\\\c\\nd", "a\"b\\c\nd", MonitorResult.Status.CRITICAL);

		String message = _newMessage(1024);

		_testWriteMessage(message, message, MonitorResult.Status.CRITICAL);

		message = RandomTestUtil.randomString();

		_testWriteMessage(message, message, null);

		_testWriteMessage(null, "", MonitorResult.Status.CRITICAL);
		_testWriteMessage(
			null, RandomTestUtil.randomString(), MonitorResult.Status.OK);
		_testWriteMessage(null, null, MonitorResult.Status.CRITICAL);
	}

	@Test
	public void testWriteMessageTruncated() throws Exception {
		String message = _newMessage(1025);

		_testWriteMessageTruncated(message.substring(0, 1021) + "...", message);

		message = _newMessage(1020) + "\uD83D\uDE00" + _newMessage(4);

		_testWriteMessageTruncated(message.substring(0, 1020) + "...", message);
	}

	@Test
	public void testWriteNullSeverityAndType() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			_newMonitorResult(
				MonitorResult.Status.OK, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					new MonitorConfig(
						"disk", RandomTestUtil.randomLong(), null, null, null,
						RandomTestUtil.randomLong(), null))));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				"monitor_status{monitor=\"disk\"," +
					"severity=\"medium\",type=\"unknown\"} 0.0\n"));
	}

	@Test
	public void testWriteNullStatus() throws Exception {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk", _newMonitorResult(null, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					_newMonitorConfig(
						"disk", MonitorConfig.Severity.HIGH,
						"resource-threshold"))));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				"monitor_status{monitor=\"disk\",severity=\"high\"," +
					"type=\"resource-threshold\"} 1.0\n"));
	}

	@Test
	public void testWriteReplacesContent() throws Exception {
		String metricsFileName = RandomTestUtil.randomString();

		File metricsFile = new File(temporaryFolder.getRoot(), metricsFileName);

		String randomString = RandomTestUtil.randomString();

		JenkinsResultsParserUtil.write(metricsFile, randomString);

		_write(metricsFile, new MonitorResultStore(), _newMonitors());

		String content = read(metricsFile);

		Assert.assertFalse(content, content.contains(randomString));

		File temporaryFile = new File(
			temporaryFolder.getRoot(), metricsFileName + ".tmp");

		Assert.assertFalse(temporaryFile.exists());
	}

	@Test
	public void testWriteStatus() throws Exception {
		for (MonitorResult.Status status : MonitorResult.Status.values()) {
			MonitorResultStore monitorResultStore = new MonitorResultStore();

			monitorResultStore.store(
				"disk", _newMonitorResult(status, RandomTestUtil.randomLong()));

			File metricsFile = new File(
				temporaryFolder.getRoot(), RandomTestUtil.randomString());

			_write(
				metricsFile, monitorResultStore,
				Arrays.<Monitor>asList(
					new TestMonitor(
						_newMonitorConfig(
							"disk", MonitorConfig.Severity.HIGH,
							"resource-threshold"))));

			String content = read(metricsFile);

			Assert.assertTrue(
				content,
				content.contains(
					JenkinsResultsParserUtil.combine(
						"monitor_status{monitor=\"disk\",severity=\"high\",",
						"type=\"resource-threshold\"} ",
						String.valueOf((double)status.getSeverityRank()),
						"\n")));
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _newMessage(int length) {
		StringBuilder sb = new StringBuilder();

		while (sb.length() < length) {
			sb.append(RandomTestUtil.randomString());
		}

		sb.setLength(length);

		return sb.toString();
	}

	private MonitorConfig _newMonitorConfig(
		String id, MonitorConfig.Severity severity, String type) {

		return new MonitorConfig(
			id, RandomTestUtil.randomLong(), null, severity, null,
			RandomTestUtil.randomLong(), type);
	}

	private MonitorResult _newMonitorResult(
		long durationMillis, MonitorResult.Status status, long timestamp) {

		return new MonitorResult(
			durationMillis, RandomTestUtil.randomString(), null, status,
			timestamp);
	}

	private MonitorResult _newMonitorResult(
		MonitorResult.Status status, long timestamp) {

		return new MonitorResult(
			RandomTestUtil.randomString(), null, status, timestamp);
	}

	private List<Monitor> _newMonitors() {
		return Arrays.<Monitor>asList(
			new TestMonitor(
				_newMonitorConfig(
					"disk", MonitorConfig.Severity.HIGH, "resource-threshold")),
			new TestMonitor(
				_newMonitorConfig(
					"queue", MonitorConfig.Severity.MEDIUM, "job-health")),
			new TestMonitor(
				_newMonitorConfig(
					"testray", MonitorConfig.Severity.LOW, "external-status")));
	}

	private void _testWriteLabels(
			String expectedLabels, String... parameterNamesAndValues)
		throws Exception {

		Map<String, String> parameters = new HashMap<>();

		for (int i = 0; i < parameterNamesAndValues.length; i += 2) {
			parameters.put(
				parameterNamesAndValues[i], parameterNamesAndValues[i + 1]);
		}

		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			_newMonitorResult(
				MonitorResult.Status.OK, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(
			metricsFile, monitorResultStore,
			Arrays.<Monitor>asList(
				new TestMonitor(
					new MonitorConfig(
						"disk", RandomTestUtil.randomLong(), parameters,
						MonitorConfig.Severity.HIGH,
						Collections.singletonMap(
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString()),
						RandomTestUtil.randomLong(), "resource-threshold"))));

		String content = read(metricsFile);

		for (String metricName :
				Arrays.asList(
					"monitor_duration_seconds",
					"monitor_last_run_timestamp_seconds", "monitor_status")) {

			Assert.assertTrue(
				content,
				content.contains(
					JenkinsResultsParserUtil.combine(
						"\n", metricName, "{", expectedLabels, "} ")));
		}
	}

	private void _testWriteMessage(
			String expectedMessage, String message, MonitorResult.Status status)
		throws Exception {

		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			new MonitorResult(
				message, null, status, RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		_write(metricsFile, monitorResultStore, _newMonitors());

		String content = read(metricsFile);

		int messageInfoCount = 0;

		for (String line : content.split("\n")) {
			if (line.startsWith("monitor_message_info{")) {
				messageInfoCount++;
			}
		}

		if (expectedMessage == null) {
			testEquals(0, messageInfoCount);

			return;
		}

		testEquals(1, messageInfoCount);

		Assert.assertTrue(
			content,
			content.contains(
				JenkinsResultsParserUtil.combine(
					"monitor_message_info{message=\"", expectedMessage,
					"\",monitor=\"disk\"} 1.0\n")));
	}

	private void _testWriteMessageTruncated(
			String expectedMessage, String message)
		throws Exception {

		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store(
			"disk",
			new MonitorResult(
				message, null, MonitorResult.Status.CRITICAL,
				RandomTestUtil.randomLong()));

		File metricsFile = new File(
			temporaryFolder.getRoot(), RandomTestUtil.randomString());

		PrintStream printStream = System.out;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream, true));

		try {
			_write(
				metricsFile, monitorResultStore,
				Arrays.<Monitor>asList(
					new TestMonitor(
						_newMonitorConfig(
							"disk", MonitorConfig.Severity.HIGH,
							"resource-threshold"))));
		}
		finally {
			System.setOut(printStream);
		}

		testEquals(
			JenkinsResultsParserUtil.combine(
				"WARNING: Truncated the message for monitor disk from ",
				String.valueOf(message.length()), " to ",
				String.valueOf(expectedMessage.length()), " characters\n"),
			byteArrayOutputStream.toString("UTF-8"));

		String content = read(metricsFile);

		Assert.assertTrue(
			content,
			content.contains(
				JenkinsResultsParserUtil.combine(
					"monitor_message_info{message=\"", expectedMessage,
					"\",monitor=\"disk\"} 1.0\n")));
	}

	private void _write(
			File metricsFile, MonitorResultStore monitorResultStore,
			List<Monitor> monitors)
		throws Exception {

		_write(
			new MonitorMetricsWriter(
				metricsFile, monitorResultStore, monitors));
	}

	private void _write(MonitorMetricsWriter monitorMetricsWriter)
		throws Exception {

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				1750000000123L
			);

			monitorMetricsWriter.write();
		}
	}

}