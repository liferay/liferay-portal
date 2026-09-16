/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitJUnitTestClass extends JUnitTestClass {

	@Override
	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (!_cachedTestClassReportSearched) {
			getCachedTestClassReport();
		}

		return _cachedDownstreamBuildReport;
	}

	public TestClassReport getCachedTestClassReport() {
		if (!isBuildCachingEnabled() || _cachedTestClassReportSearched) {
			return _cachedTestClassReport;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_cachedTestClassReport = batchTestClassGroup.getCachedTestClassReport(
			getName());

		if (_cachedTestClassReport != null) {
			_cachedDownstreamBuildReport =
				_cachedTestClassReport.getDownstreamBuildReport();
		}

		_cachedTestClassReportSearched = true;

		return _cachedTestClassReport;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		if (_testClassFileReported) {
			jsonObject.put("test_class_file_reported", _testClassFileReported);
		}

		return jsonObject;
	}

	@Override
	public String getName() {
		return getTestTaskName();
	}

	@Override
	public String getTestClassName() {
		return getName();
	}

	@Override
	public String getTestTaskName() {
		String testClassFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			getTestClassFile());

		String testTaskName = testClassFilePath.replaceAll(
			".*/modules(/.+)", "$1");

		return testTaskName.replaceAll("/", ":") + ":" + getTaskName();
	}

	public boolean isTestClassFileReported() {
		return _testClassFileReported;
	}

	public void setTestClassFileReported(boolean testClassFileReported) {
		_testClassFileReported = testClassFileReported;
	}

	protected JSUnitJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile);
	}

	protected JSUnitJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_testClassFileReported = jsonObject.optBoolean(
			"test_class_file_reported");
	}

	@Override
	protected String getTaskName() {
		return "packageRunTest";
	}

	@Override
	protected String getTestName() {
		return getName();
	}

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private TestClassReport _cachedTestClassReport;
	private boolean _cachedTestClassReportSearched;
	private boolean _testClassFileReported;

}