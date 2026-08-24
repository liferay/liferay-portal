/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brittney Nguyen
 */
public class PrometheusScrape {

	public PrometheusScrape(String content) {
		_samplesMap = _newSamplesMap(content);
	}

	public Double getValue(String labelName, String labelValue, String name) {
		List<Sample> samples = _samplesMap.get(name);

		if (samples == null) {
			return null;
		}

		Double value = null;

		for (Sample sample : samples) {
			if (!sample._hasLabel(labelName, labelValue)) {
				continue;
			}

			if (value != null) {
				return null;
			}

			value = sample._getValue();
		}

		return value;
	}

	private Map<String, String> _newLabels(String labelsString) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(labelsString)) {
			return Collections.emptyMap();
		}

		Map<String, String> labels = new LinkedHashMap<>();

		Matcher matcher = _labelPattern.matcher(labelsString);

		while (matcher.find()) {
			labels.put(
				matcher.group("name"), _unescape(matcher.group("value")));
		}

		return labels;
	}

	private Map<String, List<Sample>> _newSamplesMap(String content) {
		Map<String, List<Sample>> samplesMap = new LinkedHashMap<>();

		if (JenkinsResultsParserUtil.isNullOrEmpty(content)) {
			return samplesMap;
		}

		for (String line : content.split("\n")) {
			String trimmedLine = line.trim();

			if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
				continue;
			}

			Matcher matcher = _samplePattern.matcher(trimmedLine);

			if (!matcher.matches()) {
				continue;
			}

			Double value = _toDouble(matcher.group("value"));

			if (value == null) {
				continue;
			}

			String name = matcher.group("name");

			List<Sample> samples = samplesMap.get(name);

			if (samples == null) {
				samples = new ArrayList<>();

				samplesMap.put(name, samples);
			}

			samples.add(new Sample(_newLabels(matcher.group("labels")), value));
		}

		return samplesMap;
	}

	private Double _toDouble(String value) {
		Double doubleValue = null;

		try {
			doubleValue = Double.valueOf(value);
		}
		catch (NumberFormatException numberFormatException) {
			return null;
		}

		if (doubleValue.isInfinite() || doubleValue.isNaN()) {
			return null;
		}

		return doubleValue;
	}

	private String _unescape(String value) {
		if (value.indexOf('\\') == -1) {
			return value;
		}

		StringBuilder sb = new StringBuilder();

		int length = value.length();

		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);

			if ((c != '\\') || (i == (length - 1))) {
				sb.append(c);

				continue;
			}

			i++;

			char escapedCharacter = value.charAt(i);

			if (escapedCharacter == 'n') {
				sb.append('\n');
			}
			else {
				sb.append(escapedCharacter);
			}
		}

		return sb.toString();
	}

	private static final Pattern _labelPattern = Pattern.compile(
		"(?<name>[a-zA-Z_][a-zA-Z0-9_]*)=\"(?<value>(?:[^\"\\\\]|\\\\.)*)\"");
	private static final Pattern _samplePattern = Pattern.compile(
		"(?<name>[a-zA-Z_:][a-zA-Z0-9_:]*)(\\{(?<labels>.*)\\})?[ \\t]+" +
			"(?<value>[^ \\t]+)([ \\t]+[^ \\t]+)?");

	private final Map<String, List<Sample>> _samplesMap;

	private static class Sample {

		private Sample(Map<String, String> labels, Double value) {
			_labels = labels;
			_value = value;
		}

		private Double _getValue() {
			return _value;
		}

		private boolean _hasLabel(String labelName, String labelValue) {
			String value = _labels.get(labelName);

			if (value == null) {
				return false;
			}

			return value.equals(labelValue);
		}

		private final Map<String, String> _labels;
		private final Double _value;

	}

}