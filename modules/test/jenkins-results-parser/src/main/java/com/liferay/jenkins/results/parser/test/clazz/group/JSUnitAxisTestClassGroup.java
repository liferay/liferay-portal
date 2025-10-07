/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.JSUnitModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitAxisTestClassGroup extends AxisTestClassGroup {

	@Override
	public List<DownstreamBuildReport> getCachedDownstreamBuildReports() {
		if (!isBuildCachingEnabled() || !isResultsCached()) {
			return null;
		}

		List<DownstreamBuildReport> cachedDownstreamBuildReports =
			new ArrayList<>();

		for (JSUnitModulesTestClass jsUnitModulesTestClass :
				getJSUnitModulesTestClasses()) {

			DownstreamBuildReport downstreamBuildReport =
				jsUnitModulesTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReports.contains(downstreamBuildReport)) {
				continue;
			}

			cachedDownstreamBuildReports.add(downstreamBuildReport);
		}

		return cachedDownstreamBuildReports;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		jsonObject.put(
			"test_base_dir",
			JenkinsResultsParserUtil.getCanonicalPath(getTestBaseDir()));

		return jsonObject;
	}

	public List<JSUnitModulesTestClass> getJSUnitModulesTestClasses() {
		List<JSUnitModulesTestClass> jsUnitModulesTestClass = new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			if (!(testClass instanceof JSUnitModulesTestClass)) {
				continue;
			}

			jsUnitModulesTestClass.add((JSUnitModulesTestClass)testClass);
		}

		return jsUnitModulesTestClass;
	}

	@Override
	public File getTestBaseDir() {
		if (_testBaseDir != null) {
			return _testBaseDir;
		}

		List<TestClass> testClasses = getTestClasses();

		if (testClasses.isEmpty()) {
			return null;
		}

		TestClass testClass = testClasses.get(0);

		_testBaseDir = testClass.getTestClassFile();

		return _testBaseDir;
	}

	@Override
	public boolean isResultsCached() {
		for (JSUnitModulesTestClass jsUnitModulesTestClass :
				getJSUnitModulesTestClasses()) {

			TestClassReport cachedTestClassReport =
				jsUnitModulesTestClass.getCachedTestClassReport();

			if (cachedTestClassReport != null) {
				return true;
			}
		}

		return false;
	}

	protected JSUnitAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);

		String testBaseDirPath = jsonObject.optString("test_base_dir");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testBaseDirPath)) {
			_testBaseDir = new File(testBaseDirPath);
		}
	}

	protected JSUnitAxisTestClassGroup(
		JSUnitModulesBatchTestClassGroup jsUnitModulesBatchTestClassGroup) {

		super(jsUnitModulesBatchTestClassGroup);
	}

	private File _testBaseDir;

}