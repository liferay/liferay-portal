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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

		List<DownstreamBuildReport> cachedDownstreamBuildReports =
			new ArrayList<>();

		for (FunctionalTestClass functionalTestClass :
				getFunctionalTestClasses()) {

			DownstreamBuildReport downstreamBuildReport =
				functionalTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReports.contains(downstreamBuildReport)) {
				continue;
			}

			cachedDownstreamBuildReports.add(downstreamBuildReport);
		}

		return cachedDownstreamBuildReports;
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
			"test_base_dir",
			JenkinsResultsParserUtil.getCanonicalPath(_testBaseDir));

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

	public Properties getPoshiProperties() {
		List<FunctionalTestClass> functionalTestClasses =
			getFunctionalTestClasses();

		FunctionalTestClass functionalTestClass = functionalTestClasses.get(0);

		return functionalTestClass.getPoshiProperties();
	}

	@Override
	public String getSlaveLabel() {
		String slaveLabel = JenkinsResultsParserUtil.getProperty(
			getPoshiProperties(), "slave.label");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
			return slaveLabel;
		}

		return super.getSlaveLabel();
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

	protected FunctionalAxisTestClassGroup(
		FunctionalBatchTestClassGroup functionalBatchTestClassGroup,
		File testBaseDir) {

		super(functionalBatchTestClassGroup);

		_testBaseDir = testBaseDir;
	}

	protected FunctionalAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);

		_testBaseDir = new File(jsonObject.getString("test_base_dir"));
	}

	private final File _testBaseDir;

}