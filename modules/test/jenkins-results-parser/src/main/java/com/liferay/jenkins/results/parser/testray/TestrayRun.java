/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Retryable;

import java.io.IOException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayRun {

	public static final String[] FIELD_NAMES = {
		"dateCreated", "dateModified", "id", "name"
	};

	public static String getDefaultRunIdString() {
		try {
			return JenkinsResultsParserUtil.getBuildProperty(
				"testray.environment.default[master]");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static String getRunIdString(
		String batchName, String testSuiteName, Properties properties) {

		List<String> factorValues = new ArrayList<>();

		for (String factorNameKey : _getFactorNameKeys(properties)) {
			String factoryName = _getFactorName(factorNameKey, properties);
			String factoryValue = _getFactorValue(
				batchName, testSuiteName, factorNameKey, properties);

			if (JenkinsResultsParserUtil.isNullOrEmpty(factoryName) ||
				JenkinsResultsParserUtil.isNullOrEmpty(factoryValue)) {

				continue;
			}

			factorValues.add(factoryValue);
		}

		if (factorValues.isEmpty()) {
			return getDefaultRunIdString();
		}

		return JenkinsResultsParserUtil.join("|", factorValues);
	}

	public String getEnvironmentHash() {
		StringBuilder sb = new StringBuilder();

		for (TestrayFactor testrayFactor : getTestrayFactors()) {
			TestrayFactor.Category category = testrayFactor.getCategory();

			sb.append(category.getId());

			TestrayFactor.Option option = testrayFactor.getOption();

			sb.append(option.getId());
		}

		String environment = sb.toString();

		return String.valueOf(environment.hashCode());
	}

	public Element getEnvironmentsElement() {
		Element environmentsElement = Dom4JUtil.getNewElement("environments");

		for (TestrayFactor testrayFactor : getTestrayFactors()) {
			Element environmentElement = environmentsElement.addElement(
				"environment");

			TestrayFactor.Category category = testrayFactor.getCategory();

			environmentElement.addAttribute("type", category.getName());

			TestrayFactor.Option option = testrayFactor.getOption();

			environmentElement.addAttribute("option", option.getName());
		}

		return environmentsElement;
	}

	public long getId() {
		if (_id != null) {
			return _id;
		}

		JSONObject jsonObject = getJSONObject();

		_id = jsonObject.optLong("id");

		return _id;
	}

	public String getRunIdString() {
		return _name;
	}

	public TestrayBuild getTestrayBuild() {
		return _testrayBuild;
	}

	public synchronized List<TestrayFactor> getTestrayFactors() {
		if (_testrayFactors != null) {
			return _testrayFactors;
		}

		List<TestrayFactor> testrayFactors = _getTestrayFactorsByRunId();

		if (testrayFactors == null) {
			testrayFactors = _getTestrayFactorsByName();
		}

		_testrayFactors = testrayFactors;

		return _testrayFactors;
	}

	public TestrayServer getTestrayServer() {
		TestrayBuild testrayBuild = getTestrayBuild();

		if (testrayBuild == null) {
			return null;
		}

		return testrayBuild.getTestrayServer();
	}

	public void setId(long id) {
		_id = id;
	}

	protected TestrayRun(TestrayBuild testrayBuild, JSONObject jsonObject) {
		_testrayBuild = testrayBuild;
		_jsonObject = jsonObject;

		_name = jsonObject.getString("name");
	}

	protected TestrayRun(TestrayBuild testrayBuild, String name) {
		_testrayBuild = testrayBuild;
		_name = name;
	}

	protected synchronized JSONObject getJSONObject() {
		if (_cached || (_jsonObject != null)) {
			return _jsonObject;
		}

		final TestrayBuild testrayBuild = getTestrayBuild();

		final String filterString = JenkinsResultsParserUtil.combine(
			"environmentHash eq '", getEnvironmentHash(), "' and name eq '",
			getRunIdString(), "' and r_buildToRuns_c_buildId eq '",
			String.valueOf(testrayBuild.getId()), "'");

		Retryable<JSONObject> retryable = new Retryable<JSONObject>(
			true, 3, 5, true) {

			@Override
			public JSONObject execute() {
				TestrayServer testrayServer = getTestrayServer();

				try {
					JSONObject existingJSONObject = new JSONObject(
						testrayServer.requestGet(
							"/o/c/runs?filter=" +
								URLEncoder.encode(filterString, "UTF-8")));

					JSONArray existingItemsJSONArray =
						existingJSONObject.optJSONArray("items");

					if ((existingItemsJSONArray != null) &&
						!existingItemsJSONArray.isEmpty()) {

						return existingItemsJSONArray.getJSONObject(0);
					}

					JSONObject postRequestJSONObject = new JSONObject();

					postRequestJSONObject.put(
						"environmentHash", getEnvironmentHash()
					).put(
						"name", getRunIdString()
					).put(
						"number", _getNextRunNumber()
					).put(
						"r_buildToRuns_c_buildId", testrayBuild.getId()
					);

					return new JSONObject(
						testrayServer.requestPost(
							"/o/c/runs", postRequestJSONObject.toString()));
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

		};

		_jsonObject = retryable.executeWithRetries();

		_cached = true;

		return _jsonObject;
	}

	private static String _getFactorName(
		String factorNameKey, Properties properties) {

		String factorName = JenkinsResultsParserUtil.getProperty(
			properties,
			JenkinsResultsParserUtil.combine(
				_PROPERTY_KEY_FACTOR_NAME, "[", factorNameKey, "]"));

		if (!JenkinsResultsParserUtil.isNullOrEmpty(factorName)) {
			return factorName;
		}

		return null;
	}

	private static Set<String> _getFactorNameKeys(Properties properties) {
		Set<String> factorNameKeys = new TreeSet<>();

		for (String propertyName : properties.stringPropertyNames()) {
			Matcher matcher = _factorNamePattern.matcher(propertyName);

			if (!matcher.find()) {
				continue;
			}

			factorNameKeys.add(matcher.group("nameKey"));
		}

		return factorNameKeys;
	}

	private static String _getFactorValue(
		String batchName, String testSuiteName, String factorNameKey,
		Properties properties) {

		if (Objects.equals(factorNameKey, "search_engine")) {
			String factorValue = _getSearchEngineFactorValue(
				batchName, testSuiteName, properties);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(factorValue)) {
				return factorValue;
			}
		}

		String matchingValueKey = null;
		String matchingPropertyName = null;

		for (String propertyName : properties.stringPropertyNames()) {
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
				properties, matchingPropertyName);
		}

		String factorValue = JenkinsResultsParserUtil.getProperty(
			properties,
			JenkinsResultsParserUtil.combine(
				_PROPERTY_KEY_FACTOR_VALUE, "[", factorNameKey, "]"));

		if (JenkinsResultsParserUtil.isNullOrEmpty(factorValue)) {
			return null;
		}

		return factorValue;
	}

	private static String _getSearchEngineFactorValue(
		String batchName, String testSuiteName, Properties properties) {

		if (!JenkinsResultsParserUtil.isNullOrEmpty(batchName) &&
			!(batchName.startsWith("functional") ||
			  batchName.startsWith("modules-integration") ||
			  batchName.startsWith("modules-unit"))) {

			return properties.getProperty("search.engine.default");
		}

		String searchEngine = null;
		String searchEngineVersion = null;

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			searchEngine = properties.getProperty(
				"search.engine[" + testSuiteName + "]");

			searchEngineVersion = properties.getProperty(
				"search.engine.version[" + testSuiteName + "]");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(searchEngine) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(batchName)) {

			if (batchName.contains("opensearch2")) {
				searchEngine = "opensearch2";
			}
			else if (batchName.contains("remote-elasticsearch")) {
				searchEngine = "remote-elasticsearch";
			}
			else if (batchName.contains("solr")) {
				searchEngine = "solr";
			}
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(searchEngineVersion) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(searchEngine)) {

			searchEngineVersion = properties.getProperty(
				"search.engine.version[" + searchEngine + "]");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(searchEngine)) {
			return properties.getProperty("search.engine.default");
		}

		String searchEngineFactorValue = _toTitleCase(searchEngine);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(searchEngineVersion)) {
			searchEngineFactorValue += " " + searchEngineVersion;
		}

		return searchEngineFactorValue;
	}

	private static String _toTitleCase(String name) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			return name;
		}

		String[] words = name.split("-");

		StringBuilder sb = new StringBuilder();

		for (String word : words) {
			if (sb.length() > 0) {
				sb.append(" ");
			}

			if (word.length() > 0) {
				sb.append(Character.toUpperCase(word.charAt(0)));
				sb.append(word.substring(1));
			}
		}

		return sb.toString();
	}

	private synchronized int _getNextRunNumber() {
		TestrayBuild testrayBuild = getTestrayBuild();

		String filterString = JenkinsResultsParserUtil.combine(
			"r_buildToRuns_c_buildId eq '",
			String.valueOf(testrayBuild.getId()), "'");

		try {
			TestrayServer testrayServer = getTestrayServer();

			JSONObject jsonObject = new JSONObject(
				testrayServer.requestGet(
					"/o/c/runs?filter=" +
						URLEncoder.encode(filterString, "UTF-8")));

			return jsonObject.optInt("totalCount") + 1;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private List<TestrayFactor> _getTestrayFactorsByName() {
		List<TestrayFactor> testrayFactors = new ArrayList<>();

		String name = getRunIdString();

		for (String optionName : name.split("\\|")) {
			TestrayFactor.Option testrayFactorOption =
				TestrayFactory.newTestrayFactorOption(
					optionName, _testrayBuild.getTestrayServer());

			testrayFactors.add(
				TestrayFactory.newRunTestrayFactor(testrayFactorOption, this));
		}

		return testrayFactors;
	}

	private List<TestrayFactor> _getTestrayFactorsByRunId() {
		long runId = 0;

		if (_id != null) {
			runId = _id;
		}
		else if (_jsonObject != null) {
			runId = _jsonObject.optLong("id");
		}

		if (runId <= 0) {
			return null;
		}

		String filterString = JenkinsResultsParserUtil.combine(
			"r_runToFactors_c_runId eq '", String.valueOf(runId), "'");

		try {
			TestrayServer testrayServer = _testrayBuild.getTestrayServer();

			JSONObject responseJSONObject = new JSONObject(
				testrayServer.requestGet(
					JenkinsResultsParserUtil.combine(
						"/o/c/factors?filter=",
						URLEncoder.encode(filterString, "UTF-8"),
						"&pageSize=100")));

			JSONArray itemsJSONArray = responseJSONObject.optJSONArray("items");

			if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
				return null;
			}

			List<TestrayFactor> testrayFactors = new ArrayList<>();

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject factorJSONObject = itemsJSONArray.getJSONObject(i);

				JSONObject optionJSONObject = factorJSONObject.optJSONObject(
					"factorOptionToFactors");

				if (optionJSONObject == null) {
					continue;
				}

				TestrayFactor.Option testrayFactorOption =
					TestrayFactory.newTestrayFactorOption(
						optionJSONObject, _testrayBuild.getTestrayServer());

				testrayFactors.add(
					TestrayFactory.newRunTestrayFactor(
						testrayFactorOption, this));
			}

			if (testrayFactors.isEmpty()) {
				return null;
			}

			return testrayFactors;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
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

	private boolean _cached;
	private Long _id;
	private JSONObject _jsonObject;
	private final String _name;
	private final TestrayBuild _testrayBuild;
	private List<TestrayFactor> _testrayFactors;

}