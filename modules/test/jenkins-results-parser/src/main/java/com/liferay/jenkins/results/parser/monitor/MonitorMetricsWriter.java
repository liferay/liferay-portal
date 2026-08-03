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

	private GaugeSnapshot _getCheckLastRunTimestampSnapshot() {
		GaugeSnapshot.Builder builder = GaugeSnapshot.builder();

		builder.name("monitor_check_last_run_timestamp_seconds");
		builder.help("Unix timestamp of the last check run, 0 if never run");

		for (Monitor monitor : _monitors) {
			builder.dataPoint(
				_newGaugeDataPointSnapshot(
					monitor, _getLastRunTimestampSeconds(monitor)));
		}

		return builder.build();
	}

	private GaugeSnapshot _getCheckStatusSnapshot() {
		GaugeSnapshot.Builder builder = GaugeSnapshot.builder();

		builder.name("monitor_check_status");
		builder.help(
			"Monitor status severity rank, 0 OK, 1 UNKNOWN, 2 WARN, 3 " +
				"CRITICAL");

		for (Monitor monitor : _monitors) {
			builder.dataPoint(
				_newGaugeDataPointSnapshot(monitor, _getSeverityRank(monitor)));
		}

		return builder.build();
	}

	private String _getContent() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		PrometheusTextFormatWriter prometheusTextFormatWriter =
			PrometheusTextFormatWriter.create();

		prometheusTextFormatWriter.write(
			byteArrayOutputStream,
			MetricSnapshots.of(
				_getCheckLastRunTimestampSnapshot(), _getCheckStatusSnapshot(),
				_getHeartbeatTimestampSnapshot()),
			EscapingScheme.DEFAULT);

		return byteArrayOutputStream.toString("UTF-8");
	}

	private GaugeSnapshot _getHeartbeatTimestampSnapshot() {
		GaugeSnapshot.Builder builder = GaugeSnapshot.builder();

		builder.name("monitor_heartbeat_timestamp_seconds");
		builder.help("Unix timestamp of the last metrics write");

		GaugeSnapshot.GaugeDataPointSnapshot.Builder dataPointBuilder =
			GaugeSnapshot.GaugeDataPointSnapshot.builder();

		dataPointBuilder.value(
			JenkinsResultsParserUtil.getCurrentTimeMillis() / 1000);

		builder.dataPoint(dataPointBuilder.build());

		return builder.build();
	}

	private Labels _getLabels(Monitor monitor) {
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

		return Labels.of(
			"check", monitor.getId(), "severity",
			severityName.toLowerCase(Locale.ENGLISH), "type", type);
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

	private GaugeSnapshot.GaugeDataPointSnapshot _newGaugeDataPointSnapshot(
		Monitor monitor, double value) {

		GaugeSnapshot.GaugeDataPointSnapshot.Builder dataPointBuilder =
			GaugeSnapshot.GaugeDataPointSnapshot.builder();

		dataPointBuilder.labels(_getLabels(monitor));
		dataPointBuilder.value(value);

		return dataPointBuilder.build();
	}

	private final File _metricsFile;
	private final MonitorResultStore _monitorResultStore;
	private final List<Monitor> _monitors;

}