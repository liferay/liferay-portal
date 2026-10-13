/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import io.prometheus.metrics.config.EscapingScheme;
import io.prometheus.metrics.expositionformats.PrometheusTextFormatWriter;
import io.prometheus.metrics.model.snapshots.GaugeSnapshot;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
		_monitors = Collections.unmodifiableList(new ArrayList<>(monitors));
	}

	public void write() throws IOException {
		File temporaryFile = new File(
			_metricsFile.getParentFile(), _metricsFile.getName() + ".tmp");

		JenkinsResultsParserUtil.write(temporaryFile, _getContent());

		Files.move(
			temporaryFile.toPath(), _metricsFile.toPath(),
			StandardCopyOption.ATOMIC_MOVE);
	}

	private String _getContent() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		PrometheusTextFormatWriter prometheusTextFormatWriter =
			PrometheusTextFormatWriter.create();

		prometheusTextFormatWriter.write(
			byteArrayOutputStream,
			MetricSnapshots.of(
				_newDurationGaugeSnapshot(), _newHeartbeatTimestampSnapshot(),
				_newLastRunTimestampSnapshot(), _newMessageInfoSnapshot(),
				_newStatusSnapshot()),
			EscapingScheme.DEFAULT);

		return byteArrayOutputStream.toString("UTF-8");
	}

	private double _getDurationSeconds(Monitor monitor) {
		MonitorResult monitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if (monitorResult == null) {
			return 0;
		}

		long durationMillis = monitorResult.getDurationMillis();

		if (durationMillis < 0) {
			return 0;
		}

		return durationMillis / 1000.0;
	}

	private Labels _getLabels(Monitor monitor) {
		MonitorConfig monitorConfig = monitor.getMonitorConfig();

		MonitorConfig.Severity severity = monitorConfig.getSeverity();

		if (severity == null) {
			severity = MonitorConfig.Severity.MEDIUM;
		}

		String severityName = severity.name();

		String type = monitorConfig.getType();

		if (JenkinsResultsParserUtil.isNullOrEmpty(type)) {
			type = "unknown";
		}

		Labels labels = Labels.of(
			"monitor", monitor.getId(), "severity",
			severityName.toLowerCase(Locale.ENGLISH), "type", type);

		Map<String, String> parameters = monitorConfig.getParameters();

		for (Map.Entry<String, String> entry : _labelNames.entrySet()) {
			String parameterValue = parameters.get(entry.getKey());

			if (JenkinsResultsParserUtil.isNullOrEmpty(parameterValue)) {
				continue;
			}

			labels = labels.add(entry.getValue(), parameterValue);
		}

		return labels;
	}

	private long _getLastRunTimestampSeconds(Monitor monitor) {
		MonitorResult monitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if (monitorResult == null) {
			return 0;
		}

		return monitorResult.getTimestamp() / 1000;
	}

	private String _getMessage(Monitor monitor) {
		MonitorResult monitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if ((monitorResult == null) ||
			(monitorResult.getStatus() == MonitorResult.Status.OK)) {

			return null;
		}

		String message = monitorResult.getMessage();

		if (JenkinsResultsParserUtil.isNullOrEmpty(message)) {
			return null;
		}

		if (message.length() <= _MESSAGE_LENGTH_MAXIMUM) {
			return message;
		}

		int endIndex = _MESSAGE_LENGTH_MAXIMUM - 3;

		if (Character.isHighSurrogate(message.charAt(endIndex - 1))) {
			endIndex--;
		}

		String truncatedMessage = message.substring(0, endIndex) + "...";

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"WARNING: Truncated the message for monitor ", monitor.getId(),
				" from ", String.valueOf(message.length()), " to ",
				String.valueOf(truncatedMessage.length()), " characters"));

		return truncatedMessage;
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

	private GaugeSnapshot _newDurationGaugeSnapshot() {
		GaugeSnapshot.Builder gaugeSnapshotBuilder = GaugeSnapshot.builder();

		for (Monitor monitor : _monitors) {
			gaugeSnapshotBuilder.dataPoint(
				_newGaugeDataPointSnapshot(
					monitor, _getDurationSeconds(monitor)));
		}

		gaugeSnapshotBuilder.help(
			"Seconds the last monitor run took, 0 if never run or not " +
				"measured");
		gaugeSnapshotBuilder.name("monitor_duration_seconds");

		return gaugeSnapshotBuilder.build();
	}

	private GaugeSnapshot.GaugeDataPointSnapshot _newGaugeDataPointSnapshot(
		Monitor monitor, double value) {

		GaugeSnapshot.GaugeDataPointSnapshot.Builder
			gaugeDataPointSnapshotBuilder =
				GaugeSnapshot.GaugeDataPointSnapshot.builder();

		gaugeDataPointSnapshotBuilder.labels(_getLabels(monitor));
		gaugeDataPointSnapshotBuilder.value(value);

		return gaugeDataPointSnapshotBuilder.build();
	}

	private GaugeSnapshot _newHeartbeatTimestampSnapshot() {
		GaugeSnapshot.Builder gaugeSnapshotBuilder = GaugeSnapshot.builder();

		gaugeSnapshotBuilder.help("Unix timestamp of the last metrics write");
		gaugeSnapshotBuilder.name("monitor_heartbeat_timestamp_seconds");

		GaugeSnapshot.GaugeDataPointSnapshot.Builder
			gaugeDataPointSnapshotBuilder =
				GaugeSnapshot.GaugeDataPointSnapshot.builder();

		gaugeDataPointSnapshotBuilder.value(
			JenkinsResultsParserUtil.getCurrentTimeMillis() / 1000);

		gaugeSnapshotBuilder.dataPoint(gaugeDataPointSnapshotBuilder.build());

		return gaugeSnapshotBuilder.build();
	}

	private GaugeSnapshot _newLastRunTimestampSnapshot() {
		GaugeSnapshot.Builder gaugeSnapshotBuilder = GaugeSnapshot.builder();

		for (Monitor monitor : _monitors) {
			gaugeSnapshotBuilder.dataPoint(
				_newGaugeDataPointSnapshot(
					monitor, _getLastRunTimestampSeconds(monitor)));
		}

		gaugeSnapshotBuilder.help(
			"Unix timestamp of the last monitor run, 0 if never run");
		gaugeSnapshotBuilder.name("monitor_last_run_timestamp_seconds");

		return gaugeSnapshotBuilder.build();
	}

	private GaugeSnapshot _newMessageInfoSnapshot() {
		GaugeSnapshot.Builder gaugeSnapshotBuilder = GaugeSnapshot.builder();

		gaugeSnapshotBuilder.help(
			"Message of the last monitor run, for each monitor that is not OK");
		gaugeSnapshotBuilder.name("monitor_message_info");

		for (Monitor monitor : _monitors) {
			String message = _getMessage(monitor);

			if (message == null) {
				continue;
			}

			GaugeSnapshot.GaugeDataPointSnapshot.Builder
				gaugeDataPointSnapshotBuilder =
					GaugeSnapshot.GaugeDataPointSnapshot.builder();

			gaugeDataPointSnapshotBuilder.labels(
				Labels.of("message", message, "monitor", monitor.getId()));
			gaugeDataPointSnapshotBuilder.value(1);

			gaugeSnapshotBuilder.dataPoint(
				gaugeDataPointSnapshotBuilder.build());
		}

		return gaugeSnapshotBuilder.build();
	}

	private GaugeSnapshot _newStatusSnapshot() {
		GaugeSnapshot.Builder gaugeSnapshotBuilder = GaugeSnapshot.builder();

		gaugeSnapshotBuilder.help(
			"Monitor status severity rank, 0 OK, 1 UNKNOWN, 2 WARN, 3 " +
				"CRITICAL");
		gaugeSnapshotBuilder.name("monitor_status");

		for (Monitor monitor : _monitors) {
			gaugeSnapshotBuilder.dataPoint(
				_newGaugeDataPointSnapshot(monitor, _getSeverityRank(monitor)));
		}

		return gaugeSnapshotBuilder.build();
	}

	private static final int _MESSAGE_LENGTH_MAXIMUM = 1024;

	private static final Map<String, String> _labelNames =
		new HashMap<String, String>() {
			{
				put("branch", "branch");
				put("controller.job.name", "controller_job_name");
				put("device", "device");
				put("job.name", "job_name");
				put("label", "node_label");
				put("master.name", "master_name");
				put("metric", "metric");
				put("mount", "mount");
				put("report.name", "report_name");
				put("url", "url");
			}
		};

	private final File _metricsFile;
	private final MonitorResultStore _monitorResultStore;
	private final List<Monitor> _monitors;

}