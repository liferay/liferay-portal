/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitJUnitTestClass extends JUnitTestClass {

	@Override
	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (!_cachedTestClassReportsSearched) {
			getCachedTestClassReports();
		}

		return _cachedDownstreamBuildReport;
	}

	@Override
	public List<TestClassReport> getCachedTestClassReports() {
		if (!isBuildCachingEnabled() || _cachedTestClassReportsSearched) {
			return _cachedTestClassReports;
		}

		List<TestClassReport> cachedTestClassReports = new ArrayList<>();

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		for (String testClassReportName : _getTestClassReportNames()) {
			TestClassReport cachedTestClassReport =
				batchTestClassGroup.getCachedTestClassReport(
					testClassReportName);

			if (cachedTestClassReport == null) {
				return _cachedTestClassReports;
			}

			cachedTestClassReports.add(cachedTestClassReport);
		}

		if (cachedTestClassReports.isEmpty()) {
			return _cachedTestClassReports;
		}

		_cachedTestClassReports = cachedTestClassReports;

		for (TestClassReport cachedTestClassReport : cachedTestClassReports) {
			_cachedDownstreamBuildReport =
				cachedTestClassReport.getDownstreamBuildReport();

			break;
		}

		_cachedTestClassReportsSearched = true;

		return _cachedTestClassReports;
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

	private List<String> _getTestClassReportNames() {
		if (!_testClassFileReported) {
			return Collections.singletonList(getName());
		}

		List<String> testClassReportNames = new ArrayList<>();

		for (TestClassMethod testClassMethod : getTestClassMethods()) {
			testClassReportNames.add(testClassMethod.getName());
		}

		return testClassReportNames;
	}

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private List<TestClassReport> _cachedTestClassReports;
	private boolean _cachedTestClassReportsSearched;
	private boolean _testClassFileReported;

}