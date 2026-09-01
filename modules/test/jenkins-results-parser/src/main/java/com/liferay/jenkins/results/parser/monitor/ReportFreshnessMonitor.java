/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brittney Nguyen
 */
public class ReportFreshnessMonitor extends BaseMonitor {

	public ReportFreshnessMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_cadenceSeconds = getLongValue("parameter", 0, "cadence", parameters);

		if (_cadenceSeconds <= 0) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(
					"parameter", "cadence", parameters.get("cadence")));
		}

		_reportDataURL = getRequiredURLParameter("url", parameters, "file:///");
		_reportName = getRequiredParameter("report.name", parameters);
		_overdueGraceSeconds = getOverdueGraceSeconds(
			_cadenceSeconds, monitorConfig.getThresholds());
	}

	@Override
	public MonitorResult execute() {
		String reportData = null;

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			reportData = JenkinsResultsParserUtil.toString(
				_reportDataURL, false, 0, 0, getAttemptTimeoutMillis(0));
		}
		catch (Exception exception) {
			return new MonitorResult(
				_getReadFailureMessage(exception), null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		long generatedTimestamp = _getGeneratedTimestamp(reportData);

		if (generatedTimestamp <= 0) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the generated date for report ",
					_reportName, " from ", _reportDataURL),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (generatedTimestamp > currentTimeMillis) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Report ", _reportName, " was generated at ",
					String.valueOf(generatedTimestamp),
					", which is in the future"),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		long outputAgeSeconds = (currentTimeMillis - generatedTimestamp) / 1000;

		Map<String, String> metrics = new LinkedHashMap<>();

		metrics.put("generated.timestamp", String.valueOf(generatedTimestamp));
		metrics.put("output.age.seconds", String.valueOf(outputAgeSeconds));

		if (outputAgeSeconds > (_cadenceSeconds + _overdueGraceSeconds)) {
			return new MonitorResult(
				_getStaleMessage(outputAgeSeconds), metrics,
				MonitorResult.Status.WARN, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.combine("Report ", _reportName, " is OK"),
			metrics, MonitorResult.Status.OK, currentTimeMillis);
	}

	private long _getGeneratedTimestamp(String reportData) {
		long generatedTimestamp = 0;

		for (Pattern pattern : _generatedTimestampPatterns) {
			Matcher matcher = pattern.matcher(reportData);

			while (matcher.find()) {
				long timestamp = Long.parseLong(matcher.group("timestamp"));

				if ((generatedTimestamp == 0) ||
					(timestamp < generatedTimestamp)) {

					generatedTimestamp = timestamp;
				}
			}
		}

		return generatedTimestamp;
	}

	private String _getReadFailureMessage(Exception exception) {
		String message = exception.getMessage();

		if (message == null) {
			Class<?> clazz = exception.getClass();

			message = clazz.getName();
		}

		return JenkinsResultsParserUtil.combine(
			"Unable to read ", _reportDataURL, ": ", message);
	}

	private String _getStaleMessage(long outputAgeSeconds) {
		return JenkinsResultsParserUtil.combine(
			"Report ", _reportName, " output was generated ",
			JenkinsResultsParserUtil.toDurationString(outputAgeSeconds * 1000),
			" ago, exceeding its cadence of ",
			JenkinsResultsParserUtil.toDurationString(_cadenceSeconds * 1000),
			" plus a grace period of ",
			JenkinsResultsParserUtil.toDurationString(
				_overdueGraceSeconds * 1000));
	}

	private static final List<Pattern> _generatedTimestampPatterns =
		Arrays.asList(
			Pattern.compile(
				"var\\s+\\w*GeneratedDate\\s*=\\s*new Date\\(" +
					"(?<timestamp>\\d+)\\)"),
			Pattern.compile(
				"\"modification_date\"\\s*:\\s*(?<timestamp>\\d+)"));

	private final long _cadenceSeconds;
	private final long _overdueGraceSeconds;
	private final String _reportDataURL;
	private final String _reportName;

}