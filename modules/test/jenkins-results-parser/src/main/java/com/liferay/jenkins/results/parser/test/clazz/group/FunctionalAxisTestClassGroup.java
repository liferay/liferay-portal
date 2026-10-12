/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.test.clazz.FunctionalTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class FunctionalAxisTestClassGroup extends AxisTestClassGroup {

	@Override
	public List<DownstreamBuildReport> getCachedDownstreamBuildReports() {
		if (!isBuildCachingEnabled() || !isResultsCached()) {
			return null;
		}

		Set<DownstreamBuildReport> cachedDownstreamBuildReports =
			new HashSet<>();

		for (FunctionalTestClass functionalTestClass :
				getFunctionalTestClasses()) {

			DownstreamBuildReport downstreamBuildReport =
				functionalTestClass.getCachedDownstreamBuildReport();

			cachedDownstreamBuildReports.add(downstreamBuildReport);
		}

		return new ArrayList<>(cachedDownstreamBuildReports);
	}

	@Override
	public String getEnhancedBatchName() {
		String batchName = getBatchName();

		Properties poshiProperties = getPoshiProperties();

		String browserChromeVersion = poshiProperties.getProperty(
			"browser.chrome.version");

		if ((browserChromeVersion != null) &&
			browserChromeVersion.equals("139.0")) {

			batchName += "-chrome139";
		}

		return batchName;
	}

	public List<FunctionalTestClass> getFunctionalTestClasses() {
		List<FunctionalTestClass> functionalTestClasses = new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			if (!(testClass instanceof FunctionalTestClass)) {
				continue;
			}

			functionalTestClasses.add((FunctionalTestClass)testClass);
		}

		return functionalTestClasses;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		jsonObject.put(
			"test_analytics_cloud", _testAnalyticsCloud
		).put(
			"test_base_dir",
			JenkinsResultsParserUtil.getCanonicalPath(_testBaseDir)
		);

		return jsonObject;
	}

	@Override
	public Integer getMinimumSlaveRAM() {
		Properties poshiProperties = getPoshiProperties();

		String minimumSlaveRAM = poshiProperties.getProperty(
			"minimum.slave.ram");

		if ((minimumSlaveRAM != null) && minimumSlaveRAM.matches("\\d+")) {
			return Integer.valueOf(minimumSlaveRAM);
		}

		return super.getMinimumSlaveRAM();
	}

	@Override
	public String getOSArchitecture() {
		List<String> propertyOpts = new ArrayList<>();

		Properties poshiProperties = getPoshiProperties();

		String browserChromeVersion = poshiProperties.getProperty(
			"browser.chrome.version");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(browserChromeVersion)) {
			Matcher chromeVersionMatcher = _chromeVersionPattern.matcher(
				browserChromeVersion);

			if (chromeVersionMatcher.find()) {
				propertyOpts.add(
					"chrome" + chromeVersionMatcher.group("majorVersion"));
			}
		}

		if (isTestAnalyticsCloud()) {
			propertyOpts.add("analytics-cloud");
		}

		propertyOpts.add(getBatchName());

		try {
			String osArchitecture = JenkinsResultsParserUtil.getBuildProperty(
				"test.batch.os.architecture",
				propertyOpts.toArray(new String[0]));

			if (!JenkinsResultsParserUtil.isNullOrEmpty(osArchitecture)) {
				return osArchitecture;
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return super.getOSArchitecture();
	}

	public Properties getPoshiProperties() {
		List<FunctionalTestClass> functionalTestClasses =
			getFunctionalTestClasses();

		FunctionalTestClass functionalTestClass = functionalTestClasses.get(0);

		return functionalTestClass.getPoshiProperties();
	}

	@Override
	public File getTestBaseDir() {
		return _testBaseDir;
	}

	public List<String> getTestClassMethodNames() {
		List<String> testClassMethodNames = new ArrayList<>();

		for (FunctionalTestClass functionalTestClass :
				getFunctionalTestClasses()) {

			testClassMethodNames.add(
				functionalTestClass.getTestClassMethodName());
		}

		return testClassMethodNames;
	}

	@Override
	public boolean isResultsCached() {
		if (!isBuildCachingEnabled()) {
			return false;
		}

		for (FunctionalTestClass functionalTestClass :
				getFunctionalTestClasses()) {

			TestReport cachedTestReport =
				functionalTestClass.getCachedTestReport();

			if (cachedTestReport == null) {
				return false;
			}
		}

		return true;
	}

	public boolean isTestAnalyticsCloud() {
		if (_testAnalyticsCloud != null) {
			return _testAnalyticsCloud;
		}

		Properties poshiProperties = getPoshiProperties();

		String analyticsCloudEnabled = poshiProperties.getProperty(
			"analytics.cloud.enabled");

		if ((analyticsCloudEnabled != null) &&
			analyticsCloudEnabled.equals("true")) {

			_testAnalyticsCloud = true;

			return _testAnalyticsCloud;
		}

		_testAnalyticsCloud = false;

		return _testAnalyticsCloud;
	}

	protected FunctionalAxisTestClassGroup(
		FunctionalBatchTestClassGroup functionalBatchTestClassGroup,
		File testBaseDir) {

		super(functionalBatchTestClassGroup);

		_testBaseDir = testBaseDir;
	}

	protected FunctionalAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);

		_testAnalyticsCloud = jsonObject.optBoolean("test_analytics_cloud");
		_testBaseDir = new File(jsonObject.getString("test_base_dir"));
	}

	@Override
	protected String getBaseSlaveLabel() {
		String slaveLabel = JenkinsResultsParserUtil.getProperty(
			getPoshiProperties(), "slave.label");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
			return slaveLabel;
		}

		return super.getBaseSlaveLabel();
	}

	private static final Pattern _chromeVersionPattern = Pattern.compile(
		"(?<majorVersion>\\d+)\\.\\d+");

	private Boolean _testAnalyticsCloud;
	private final File _testBaseDir;

}