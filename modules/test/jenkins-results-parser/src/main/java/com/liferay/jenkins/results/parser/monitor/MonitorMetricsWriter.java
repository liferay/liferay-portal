/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Locale;

/**
 * @author Brittney Nguyen
 */
public class MonitorMetricsWriter {

	public MonitorMetricsWriter(
		File metricsFile, MonitorResultStore monitorResultStore,
		List<Monitor> monitors) {

		MonitorIdValidator.validate(monitors);

		_metricsFile = metricsFile;
		_monitorResultStore = monitorResultStore;
		_monitors = monitors;
	}

	public void write() throws IOException {
		File temporaryFile = new File(
			_metricsFile.getParentFile(), _metricsFile.getName() + ".tmp");

		JenkinsResultsParserUtil.write(temporaryFile, _getContent());

		Files.move(
			temporaryFile.toPath(), _metricsFile.toPath(),
			StandardCopyOption.ATOMIC_MOVE);
	}

	private String _escapeLabelValue(String labelValue) {
		labelValue = labelValue.replace("\\", "\\\\");

		labelValue = labelValue.replace("\"", "\\\"");

		return labelValue.replace("\n", "\\n");
	}

	private String _getCheckLastRunTimestampLine(Monitor monitor) {
		return JenkinsResultsParserUtil.combine(
			"monitor_check_last_run_timestamp_seconds{", _getLabels(monitor),
			"} ", String.valueOf(_getLastRunTimestampSeconds(monitor)));
	}

	private String _getCheckStatusLine(Monitor monitor) {
		return JenkinsResultsParserUtil.combine(
			"monitor_check_status{", _getLabels(monitor), "} ",
			String.valueOf(_getSeverityRank(monitor)));
	}

	private String _getContent() {
		StringBuilder sb = new StringBuilder();

		sb.append(
			"# HELP monitor_check_status Monitor status severity rank, 0 OK, " +
				"1 UNKNOWN, 2 WARN, 3 CRITICAL\n");
		sb.append("# TYPE monitor_check_status gauge\n");

		for (Monitor monitor : _monitors) {
			sb.append(_getCheckStatusLine(monitor));
			sb.append("\n");
		}

		sb.append(
			"# HELP monitor_check_last_run_timestamp_seconds Unix timestamp " +
				"of the last check run, 0 if never run\n");
		sb.append("# TYPE monitor_check_last_run_timestamp_seconds gauge\n");

		for (Monitor monitor : _monitors) {
			sb.append(_getCheckLastRunTimestampLine(monitor));
			sb.append("\n");
		}

		sb.append(
			"# HELP monitor_heartbeat_timestamp_seconds Unix timestamp of " +
				"the last metrics write\n");
		sb.append("# TYPE monitor_heartbeat_timestamp_seconds gauge\n");
		sb.append("monitor_heartbeat_timestamp_seconds ");
		sb.append(JenkinsResultsParserUtil.getCurrentTimeMillis() / 1000);
		sb.append("\n");

		return sb.toString();
	}

	private String _getLabels(Monitor monitor) {
		MonitorConfig monitorConfig = monitor.getMonitorConfig();

		MonitorConfig.Severity severity = monitorConfig.getSeverity();

		if (severity == null) {
			severity = MonitorConfig.Severity.MEDIUM;
		}

		String severityName = severity.name();

		String type = monitorConfig.getType();

		if (type == null) {
			type = "unknown";
		}

		return JenkinsResultsParserUtil.combine(
			"check=\"", _escapeLabelValue(monitor.getId()), "\",severity=\"",
			severityName.toLowerCase(Locale.ENGLISH), "\",type=\"",
			_escapeLabelValue(type), "\"");
	}

	private long _getLastRunTimestampSeconds(Monitor monitor) {
		MonitorResult monitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if (monitorResult == null) {
			return 0;
		}

		return monitorResult.getTimestamp() / 1000;
	}

	private int _getSeverityRank(Monitor monitor) {
		MonitorResult monitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if (monitorResult == null) {
			return MonitorResult.Status.UNKNOWN.getSeverityRank();
		}

		MonitorResult.Status status = monitorResult.getStatus();

		if (status == null) {
			return MonitorResult.Status.UNKNOWN.getSeverityRank();
		}

		return status.getSeverityRank();
	}

	private final File _metricsFile;
	private final MonitorResultStore _monitorResultStore;
	private final List<Monitor> _monitors;

}