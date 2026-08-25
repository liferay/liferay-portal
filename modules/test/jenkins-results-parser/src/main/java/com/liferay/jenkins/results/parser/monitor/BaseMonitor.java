/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public abstract class BaseMonitor implements Monitor {

	@Override
	public String getId() {
		return _monitorConfig.getId();
	}

	@Override
	public MonitorConfig getMonitorConfig() {
		return _monitorConfig;
	}

	@Override
	public void prepareCycle() {
	}

	protected BaseMonitor(MonitorConfig monitorConfig) {
		_monitorConfig = monitorConfig;
	}

	protected int getAttemptTimeoutMillis() {
		return (int)(_getTimeoutMillis() / 3);
	}

	protected String getInvalidValueMessage(
		String category, String name, String value) {

		return JenkinsResultsParserUtil.combine(
			"Invalid ", name, " for ", _getKey(category, name), ": ", value);
	}

	protected long getLongValue(
		String category, long defaultValue, String name,
		Map<String, String> values) {

		String value = values.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return defaultValue;
		}

		long longValue = 0;

		try {
			longValue = Long.parseLong(value);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(category, name, value),
				numberFormatException);
		}

		if (longValue < 0) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(category, name, value));
		}

		return longValue;
	}

	protected String getRequiredParameter(
		String name, Map<String, String> parameters) {

		String value = parameters.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			throw new IllegalArgumentException(
				"Missing required property " + _getKey("parameter", name));
		}

		return value;
	}

	protected int getSingleAttemptTimeoutMillis() {
		long timeoutMillis = _getTimeoutMillis();

		return (int)((timeoutMillis - (timeoutMillis / 10)) / 2);
	}

	private String _getKey(String category, String name) {
		return JenkinsResultsParserUtil.combine(
			"monitor[", _monitorConfig.getId(), "].", category, "[", name, "]");
	}

	private long _getTimeoutMillis() {
		long timeoutSeconds = _monitorConfig.getTimeoutSeconds();

		if (timeoutSeconds <= 0) {
			timeoutSeconds = MonitorConfig.SECONDS_TIMEOUT_DEFAULT;
		}

		return timeoutSeconds * 1000;
	}

	private final MonitorConfig _monitorConfig;

}