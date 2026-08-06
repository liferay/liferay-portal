/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		monitorResultStore.store(
			"disk",
			_newMonitorResult(MonitorResult.Status.CRITICAL, 1750000000000L));
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

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private MonitorConfig _newMonitorConfig(
		String id, MonitorConfig.Severity severity, String type) {

		return new MonitorConfig(
			id, RandomTestUtil.randomLong(), null, severity, null,
			RandomTestUtil.randomLong(), type);
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