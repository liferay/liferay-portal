/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class PlaywrightJUnitTestClass extends JUnitTestClass {

	@Override
	public long getAverageDuration() {
		if (_averageDuration != null) {
			return _averageDuration;
		}

		for (TestClassMethod testClassMethod : getTestClassMethods()) {
			PlaywrightTestClassMethod playwrightTestClassMethod =
				(PlaywrightTestClassMethod)testClassMethod;

			BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

			long averageDuration = batchTestClassGroup.getAverageTestDuration(
				JenkinsResultsParserUtil.combine(
					getName(), ".", playwrightTestClassMethod.getTestName()));

			if (_averageDuration == null) {
				_averageDuration = averageDuration;
			}
			else {
				_averageDuration += averageDuration;
			}
		}

		return _averageDuration;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		jsonObject.put(
			"minimum_slave_ram", _minimumSlaveRAM
		).put(
			"slave_label", _slaveLabel
		);

		return jsonObject;
	}

	public Integer getMinimumSlaveRAM() {
		return _minimumSlaveRAM;
	}

	@Override
	public String getName() {
		return getSpecFilePath();
	}

	public String getSlaveLabel() {
		return _slaveLabel;
	}

	public String getSpecFilePath() {
		Matcher matcher = _testFilePathPattern.matcher(
			JenkinsResultsParserUtil.getCanonicalPath(getTestClassFile()));

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("specFilePath");
	}

	public boolean isAnalyticsCloudEnabled() {
		return _analyticsCloudEnabled;
	}

	protected PlaywrightJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile);

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		if ((testPropertiesBaseDir != null) && testPropertiesBaseDir.exists()) {
			File testPropertiesFile = new File(
				testPropertiesBaseDir, "test.properties");

			Properties testProperties = JenkinsResultsParserUtil.getProperties(
				testPropertiesFile);

			String analyticsCloudEnabled = JenkinsResultsParserUtil.getProperty(
				testProperties, "analytics.cloud.enabled");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					analyticsCloudEnabled) &&
				analyticsCloudEnabled.equals("true")) {

				_analyticsCloudEnabled = true;
			}

			String minimumSlaveRAM = JenkinsResultsParserUtil.getProperty(
				testProperties, "test.batch.minimum.slave.ram");

			if ((minimumSlaveRAM == null) || !minimumSlaveRAM.matches("\\d+")) {
				minimumSlaveRAM = _MINIMUM_SLAVE_RAM_DEFAULT;
			}

			_minimumSlaveRAM = Integer.valueOf(minimumSlaveRAM);

			String slaveLabel = JenkinsResultsParserUtil.getProperty(
				testProperties, "test.batch.slave.label");

			if (JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
				slaveLabel = getSlaveLabel();
			}

			_slaveLabel = slaveLabel;
		}
		else {
			_minimumSlaveRAM = null;
			_slaveLabel = null;
		}
	}

	protected PlaywrightJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_minimumSlaveRAM = jsonObject.optInt("minimum_slave_ram");
		_slaveLabel = jsonObject.optString("slave_label");
	}

	private static final String _MINIMUM_SLAVE_RAM_DEFAULT = "12";

	private static final Pattern _testFilePathPattern = Pattern.compile(
		".+/playwright/(setup|tests)/(?<specFilePath>.+)");

	private boolean _analyticsCloudEnabled;
	private Long _averageDuration;
	private final Integer _minimumSlaveRAM;
	private final String _slaveLabel;

}