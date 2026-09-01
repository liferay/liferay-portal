/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brittney Nguyen
 */
public class ResourceThresholdMonitor extends BaseMonitor {

	public ResourceThresholdMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_metric = getRequiredParameter("metric", parameters);

		if (!_metric.equals("disk") &&
			!_metric.equals("executor.utilization") &&
			!_metric.equals("queue.depth") && !_metric.equals("ram")) {

			throw new IllegalArgumentException(
				getInvalidValueMessage("parameter", "metric", _metric));
		}

		_masterName = getRequiredParameter("master.name", parameters);

		if (_metric.equals("disk")) {
			_selector = getRequiredParameter("file.store", parameters);
		}
		else if (_metric.equals("executor.utilization") ||
				 _metric.equals("queue.depth")) {

			_selector = getRequiredParameter("label", parameters);
		}
		else {
			_selector = null;
		}

		Map<String, String> thresholds = monitorConfig.getThresholds();

		_criticalThreshold = getLongValue(
			"threshold", 0, "critical", thresholds);
		_warnThreshold = getLongValue("threshold", 0, "warn", thresholds);

		if ((_criticalThreshold <= 0) && (_warnThreshold <= 0)) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"Missing required property ", _getThresholdKey("critical"),
					" or ", _getThresholdKey("warn")));
		}
	}

	@Override
	public MonitorResult execute() {
		Double value = null;

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			value = _getValue();
		}
		catch (Exception exception) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Unable to read the ", _getMetricDescription(), " for ",
					_masterName, ": ", exception.getMessage()),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (value == null) {
			return new MonitorResult(
				_getIndeterminateMessage(), null, MonitorResult.Status.UNKNOWN,
				currentTimeMillis);
		}

		Map<String, String> metrics = new LinkedHashMap<>();

		metrics.put(_metric, String.valueOf(value));

		if ((_criticalThreshold > 0) && (value >= _criticalThreshold)) {
			return new MonitorResult(
				_getExceededMessage(_criticalThreshold, value), metrics,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if ((_warnThreshold > 0) && (value >= _warnThreshold)) {
			return new MonitorResult(
				_getExceededMessage(_warnThreshold, value), metrics,
				MonitorResult.Status.WARN, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.combine(
				_getTargetDescription(), " is at ", _toValueString(value),
				", within its thresholds"),
			metrics, MonitorResult.Status.OK, currentTimeMillis);
	}

	@Override
	public void prepareCycle() {
		MasterResourceReader.clearInstances();
	}

	private Double _getDiskUsedPercentage() throws IOException {
		PrometheusScrape prometheusScrape = _getPrometheusScrape();

		Double capacity = prometheusScrape.getValue(
			"file_store", _selector,
			"default_jenkins_file_store_capacity_bytes");

		if ((capacity == null) || (capacity >= _BYTES_CAPACITY_MAXIMUM)) {
			return null;
		}

		Double available = prometheusScrape.getValue(
			"file_store", _selector,
			"default_jenkins_file_store_available_bytes");

		if (available == null) {
			return null;
		}

		return _toUsedPercentage(capacity, capacity - available);
	}

	private String _getExceededMessage(long threshold, Double value) {
		return JenkinsResultsParserUtil.combine(
			_getTargetDescription(), " is at ", _toValueString(value),
			", exceeding its threshold of ", _toThresholdString(threshold));
	}

	private Double _getExecutorUtilization() throws IOException {
		PrometheusScrape prometheusScrape = _getPrometheusScrape();

		Double busy = prometheusScrape.getValue(
			"label", _selector, "default_jenkins_executors_busy");
		Double defined = prometheusScrape.getValue(
			"label", _selector, "default_jenkins_executors_defined");

		if ((busy == null) || (defined == null)) {
			return null;
		}

		return _toUsedPercentage(defined, busy);
	}

	private String _getIndeterminateMessage() {
		if (_selector == null) {
			return JenkinsResultsParserUtil.combine(
				"Unable to determine the ", _getMetricDescription(), " for ",
				_masterName);
		}

		return JenkinsResultsParserUtil.combine(
			"Unable to determine the ", _getMetricDescription(), " for ",
			_selector, " on ", _masterName);
	}

	private Double _getMemoryInfoValue(String memoryInfo, String name) {
		Matcher matcher = _memoryInfoPattern.matcher(memoryInfo);

		while (matcher.find()) {
			String matchedName = matcher.group("name");

			if (matchedName.equals(name)) {
				return Double.valueOf(matcher.group("kilobytes"));
			}
		}

		return null;
	}

	private String _getMetricDescription() {
		if (_metric.equals("disk")) {
			return "disk metric";
		}

		if (_metric.equals("executor.utilization")) {
			return "executor utilization metric";
		}

		if (_metric.equals("queue.depth")) {
			return "queue depth metric";
		}

		if (_metric.equals("ram")) {
			return "RAM metric";
		}

		return _metric + " metric";
	}

	private PrometheusScrape _getPrometheusScrape() throws IOException {
		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(_masterName);

		return masterResourceReader.getPrometheusScrape(
			getAttemptTimeoutMillis(MasterResourceReader.RETRIES_SIZE_MAX));
	}

	private Double _getQueueDepth() throws IOException {
		PrometheusScrape prometheusScrape = _getPrometheusScrape();

		return prometheusScrape.getValue(
			"label", _selector, "default_jenkins_executors_queue_length");
	}

	private Double _getRAMUsedPercentage() {
		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(_masterName);

		String memoryInfo = masterResourceReader.getMemoryInfo();

		if (JenkinsResultsParserUtil.isNullOrEmpty(memoryInfo)) {
			return null;
		}

		Double memoryAvailable = _getMemoryInfoValue(
			memoryInfo, "MemAvailable");
		Double memoryTotal = _getMemoryInfoValue(memoryInfo, "MemTotal");

		if ((memoryAvailable == null) || (memoryTotal == null)) {
			return null;
		}

		return _toUsedPercentage(memoryTotal, memoryTotal - memoryAvailable);
	}

	private String _getTargetDescription() {
		if (_selector == null) {
			return JenkinsResultsParserUtil.combine(
				"The ", _getMetricDescription(), " on ", _masterName);
		}

		return JenkinsResultsParserUtil.combine(
			"The ", _getMetricDescription(), " for ", _selector, " on ",
			_masterName);
	}

	private String _getThresholdKey(String name) {
		return JenkinsResultsParserUtil.combine(
			"monitor[", getId(), "].threshold[", name, "]");
	}

	private Double _getValue() throws IOException {
		if (_metric.equals("disk")) {
			return _getDiskUsedPercentage();
		}

		if (_metric.equals("executor.utilization")) {
			return _getExecutorUtilization();
		}

		if (_metric.equals("queue.depth")) {
			return _getQueueDepth();
		}

		if (_metric.equals("ram")) {
			return _getRAMUsedPercentage();
		}

		return null;
	}

	private boolean _isPercentage() {
		return !_metric.equals("queue.depth");
	}

	private String _toThresholdString(long threshold) {
		if (_isPercentage()) {
			return threshold + "%";
		}

		return String.valueOf(threshold);
	}

	private Double _toUsedPercentage(double total, double used) {
		if (total <= 0) {
			return null;
		}

		return (used * 100) / total;
	}

	private String _toValueString(Double value) {
		if (_isPercentage()) {
			return String.format(Locale.ENGLISH, "%.1f%%", value);
		}

		return String.valueOf(value.intValue());
	}

	private static final long _BYTES_CAPACITY_MAXIMUM = 1L << 50;

	private static final Pattern _memoryInfoPattern = Pattern.compile(
		"(?<name>[A-Za-z_()]+):\\s+(?<kilobytes>\\d+) kB");

	private final long _criticalThreshold;
	private final String _masterName;
	private final String _metric;
	private final String _selector;
	private final long _warnThreshold;

}