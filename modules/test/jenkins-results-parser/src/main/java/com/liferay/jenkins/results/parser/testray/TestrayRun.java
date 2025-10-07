/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayRun {

	public static String getDefaultRunIDString() {
		return _properties.getProperty("testray.environment.default[master]");
	}

	public List<Factor> getFactors() {
		return factors;
	}

	public long getID() {
		if (_jsonObject == null) {
			return 0;
		}

		return _jsonObject.getLong("id");
	}

	public String getRunIDString() {
		if ((_jsonObject != null) && _jsonObject.has("name")) {
			return _jsonObject.getString("name");
		}

		List<String> factorValues = new ArrayList<>();

		for (Factor factor : getFactors()) {
			factorValues.add(factor.getValue());
		}

		return JenkinsResultsParserUtil.join("|", factorValues);
	}

	public TestrayBuild getTestrayBuild() {
		return _testrayBuild;
	}

	public static class Factor {

		public Factor(String name, String value) {
			_name = name;
			_value = value;
		}

		public String getName() {
			return _name;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return getName() + "=" + getValue();
		}

		private final String _name;
		private final String _value;

	}

	protected TestrayRun(TestrayBuild testrayBuild, JSONObject jsonObject) {
		_testrayBuild = testrayBuild;
		_jsonObject = jsonObject;

		try {
			_properties.putAll(JenkinsResultsParserUtil.getBuildProperties());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		initializeFactorsByJSONObject(jsonObject);
	}

	protected TestrayRun(
		TestrayBuild testrayBuild, String batchName,
		List<File> propertiesFiles) {

		_testrayBuild = testrayBuild;

		try {
			_properties.putAll(JenkinsResultsParserUtil.getBuildProperties());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		for (int i = propertiesFiles.size() - 1; i >= 0; i--) {
			_properties.putAll(
				JenkinsResultsParserUtil.getProperties(propertiesFiles.get(i)));
		}

		initializeFactorsByBatchName(batchName);

		JSONObject jsonObject = null;

		String runIDString = getRunIDString();

		for (TestrayRun testrayRun : testrayBuild.getTestrayRuns()) {
			String testrayRunIDString = testrayRun.getRunIDString();

			if (Objects.equals(
					runIDString.toLowerCase(),
					testrayRunIDString.toLowerCase())) {

				jsonObject = testrayRun.getJSONObject();

				break;
			}
		}

		_jsonObject = jsonObject;
	}

	protected JSONObject getJSONObject() {
		return _jsonObject;
	}

	protected Properties getProperties() {
		return _properties;
	}

	protected void initializeFactorsByBatchName(String batchName) {
		factors = new ArrayList<>();

		if (JenkinsResultsParserUtil.isNullOrEmpty(batchName)) {
			return;
		}

		for (String factorNameKey : _getFactorNameKeys()) {
			String factoryName = _getFactorName(factorNameKey);
			String factoryValue = _getFactorValue(batchName, factorNameKey);

			if (JenkinsResultsParserUtil.isNullOrEmpty(factoryName) ||
				JenkinsResultsParserUtil.isNullOrEmpty(factoryValue)) {

				continue;
			}

			factors.add(new Factor(factoryName, factoryValue));
		}
	}

	protected void initializeFactorsByJSONObject(JSONObject jsonObject) {
		factors = new ArrayList<>();

		if (jsonObject == null) {
			return;
		}

		String runIDString = jsonObject.optString("name");

		if (JenkinsResultsParserUtil.isNullOrEmpty(runIDString)) {
			return;
		}

		for (String factorValue : runIDString.split("\\|")) {
			for (String propertyName : _properties.stringPropertyNames()) {
				Matcher factorValueMatcher = _factorValuePattern.matcher(
					propertyName);

				if (!factorValueMatcher.find()) {
					continue;
				}

				if (factorValue.equals(_properties.getProperty(propertyName))) {
					String factorName = _getFactorName(
						factorValueMatcher.group("nameKey"));

					factors.add(new Factor(factorName, factorValue));

					break;
				}
			}
		}
	}

	protected List<Factor> factors;

	private String _getFactorName(String factorNameKey) {
		String factorName = JenkinsResultsParserUtil.getProperty(
			_properties,
			JenkinsResultsParserUtil.combine(
				_PROPERTY_KEY_FACTOR_NAME, "[", factorNameKey, "]"));

		if (!JenkinsResultsParserUtil.isNullOrEmpty(factorName)) {
			return factorName;
		}

		return null;
	}

	private Set<String> _getFactorNameKeys() {
		Set<String> factorNameKeys = new TreeSet<>();

		for (String propertyName : _properties.stringPropertyNames()) {
			Matcher matcher = _factorNamePattern.matcher(propertyName);

			if (!matcher.find()) {
				continue;
			}

			factorNameKeys.add(matcher.group("nameKey"));
		}

		return factorNameKeys;
	}

	private String _getFactorValue(String batchName, String factorNameKey) {
		String matchingValueKey = null;
		String matchingPropertyName = null;

		for (String propertyName : _properties.stringPropertyNames()) {
			Matcher matcher = _factorValuePattern.matcher(propertyName);

			if (!matcher.find()) {
				continue;
			}

			String nameKey = matcher.group("nameKey");

			if (!nameKey.equals(factorNameKey)) {
				continue;
			}

			String valueKey = matcher.group("valueKey");

			if ((valueKey == null) || !batchName.contains(valueKey)) {
				continue;
			}

			if ((matchingValueKey == null) ||
				(valueKey.length() > matchingValueKey.length())) {

				matchingValueKey = valueKey;
				matchingPropertyName = propertyName;
			}
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(matchingPropertyName)) {
			return JenkinsResultsParserUtil.getProperty(
				_properties, matchingPropertyName);
		}

		String factorValue = JenkinsResultsParserUtil.getProperty(
			_properties,
			JenkinsResultsParserUtil.combine(
				_PROPERTY_KEY_FACTOR_VALUE, "[", factorNameKey, "]"));

		if (JenkinsResultsParserUtil.isNullOrEmpty(factorValue)) {
			return null;
		}

		return factorValue;
	}

	private static final String _PROPERTY_KEY_FACTOR_NAME =
		"testray.environment.factor.name";

	private static final String _PROPERTY_KEY_FACTOR_VALUE =
		"testray.environment.factor.value";

	private static final Pattern _factorNamePattern = Pattern.compile(
		_PROPERTY_KEY_FACTOR_NAME + "\\[(?<nameKey>[^\\]]+)\\]");
	private static final Pattern _factorValuePattern = Pattern.compile(
		_PROPERTY_KEY_FACTOR_VALUE +
			"\\[(?<nameKey>[^\\]]+)\\](\\[(?<valueKey>[^\\]]+)\\])?");
	private static final Properties _properties = new Properties();

	private final JSONObject _jsonObject;
	private final TestrayBuild _testrayBuild;

}