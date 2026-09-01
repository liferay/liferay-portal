/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.FileNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brittney Nguyen
 */
public class HTTPEndpointMonitor extends BaseMonitor {

	public HTTPEndpointMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		_endpointURL = getRequiredURLParameter(
			"url", monitorConfig.getParameters(), "http://", "https://");
		_latencyMaximumMillis = getLongValue(
			"threshold", 0, "latency.maximum.millis",
			monitorConfig.getThresholds());
	}

	@Override
	public MonitorResult execute() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		long startTimestamp = System.currentTimeMillis();

		try {
			JenkinsResultsParserUtil.toString(
				_endpointURL, false, 0, 0, getSingleAttemptTimeoutMillis());
		}
		catch (Exception exception) {
			return new MonitorResult(
				_getFailureMessage(exception), null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		long latencyMillis = System.currentTimeMillis() - startTimestamp;

		Map<String, String> metrics = new LinkedHashMap<>();

		metrics.put("latency.millis", String.valueOf(latencyMillis));

		if ((_latencyMaximumMillis > 0) &&
			(latencyMillis > _latencyMaximumMillis)) {

			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Endpoint ", _endpointURL, " responded in ",
					String.valueOf(latencyMillis),
					" ms, exceeding its maximum latency of ",
					String.valueOf(_latencyMaximumMillis), " ms"),
				metrics, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.combine(
				"Endpoint ", _endpointURL, " is OK"),
			metrics, MonitorResult.Status.OK, currentTimeMillis);
	}

	private String _getFailureMessage(Exception exception) {
		if (exception instanceof FileNotFoundException) {
			return JenkinsResultsParserUtil.combine(
				"Endpoint ", _endpointURL, " was not found");
		}

		String message = exception.getMessage();

		if (message == null) {
			Class<?> clazz = exception.getClass();

			message = clazz.getName();
		}

		Matcher matcher = _responseCodePattern.matcher(message);

		if (matcher.find()) {
			return JenkinsResultsParserUtil.combine(
				"Endpoint ", _endpointURL, " returned the response code ",
				matcher.group("responseCode"));
		}

		return JenkinsResultsParserUtil.combine(
			"Unable to read ", _endpointURL, ": ", message);
	}

	private static final Pattern _responseCodePattern = Pattern.compile(
		"HTTP response code: (?<responseCode>\\d+)");

	private final String _endpointURL;
	private final long _latencyMaximumMillis;

}