/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brittney Nguyen
 */
public class MonitorConfigLoader {

	public static List<MonitorConfig> getMonitorConfigs() throws IOException {
		return getMonitorConfigs(JenkinsResultsParserUtil.getBuildProperties());
	}

	public static List<MonitorConfig> getMonitorConfigs(
		Properties buildProperties) {

		List<MonitorConfig> monitorConfigs = new ArrayList<>();

		for (String id : _getIds(buildProperties)) {
			monitorConfigs.add(_getMonitorConfig(buildProperties, id));
		}

		return monitorConfigs;
	}

	private static TreeSet<String> _getIds(Properties buildProperties) {
		TreeSet<String> ids = new TreeSet<>();

		for (String propertyName : buildProperties.stringPropertyNames()) {
			Matcher matcher = _monitorPropertyPattern.matcher(propertyName);

			if (matcher.matches()) {
				ids.add(matcher.group("id"));
			}
		}

		return ids;
	}

	private static Map<String, String> _getIndexedProperties(
		Properties buildProperties, String category, String id) {

		Map<String, String> indexedProperties = new LinkedHashMap<>();

		for (String propertyName : buildProperties.stringPropertyNames()) {
			Matcher matcher = _indexedPropertyPattern.matcher(propertyName);

			if (matcher.matches() && id.equals(matcher.group("id")) &&
				category.equals(matcher.group("category"))) {

				indexedProperties.put(
					matcher.group("name"),
					JenkinsResultsParserUtil.getProperty(
						buildProperties, propertyName));
			}
		}

		return indexedProperties;
	}

	private static String _getKey(String id, String suffix) {
		return JenkinsResultsParserUtil.combine("monitor[", id, "].", suffix);
	}

	private static long _getLongProperty(
		Properties buildProperties, long defaultValue, String id,
		String suffix) {

		String propertyValue = JenkinsResultsParserUtil.getProperty(
			buildProperties, _getKey(id, suffix));

		if (JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {
			return defaultValue;
		}

		long value;

		try {
			value = Long.parseLong(propertyValue);
		}
		catch (NumberFormatException numberFormatException) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"Invalid ", suffix, " for ", _getKey(id, suffix), ": ",
					propertyValue),
				numberFormatException);
		}

		if (value < 0) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"Invalid ", suffix, " for ", _getKey(id, suffix), ": ",
					propertyValue));
		}

		return value;
	}

	private static MonitorConfig _getMonitorConfig(
		Properties buildProperties, String id) {

		String type = JenkinsResultsParserUtil.getProperty(
			buildProperties, _getKey(id, "type"));

		if (JenkinsResultsParserUtil.isNullOrEmpty(type)) {
			throw new IllegalArgumentException(
				"Missing required property " + _getKey(id, "type"));
		}

		return new MonitorConfig(
			_getLongProperty(buildProperties, 0, id, "interval"), id,
			_getParameters(buildProperties, id),
			_getSeverity(buildProperties, id),
			_getThresholds(buildProperties, id),
			_getLongProperty(buildProperties, 60, id, "timeout"), type);
	}

	private static Map<String, String> _getParameters(
		Properties buildProperties, String id) {

		return _getIndexedProperties(buildProperties, "parameter", id);
	}

	private static MonitorConfig.Severity _getSeverity(
		Properties buildProperties, String id) {

		String severityString = JenkinsResultsParserUtil.getProperty(
			buildProperties, _getKey(id, "severity"));

		if (JenkinsResultsParserUtil.isNullOrEmpty(severityString)) {
			return MonitorConfig.Severity.MEDIUM;
		}

		try {
			return MonitorConfig.Severity.valueOf(
				severityString.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"Invalid severity for ", _getKey(id, "severity"), ": ",
					severityString),
				illegalArgumentException);
		}
	}

	private static Map<String, String> _getThresholds(
		Properties buildProperties, String id) {

		return _getIndexedProperties(buildProperties, "threshold", id);
	}

	private static final Pattern _indexedPropertyPattern = Pattern.compile(
		"monitor\\[(?<id>[^\\]]+)\\]\\.(?<category>[^\\[]+)\\[" +
			"(?<name>[^\\]]+)\\]");
	private static final Pattern _monitorPropertyPattern = Pattern.compile(
		"monitor\\[(?<id>[^\\]]+)\\]\\..+");

}