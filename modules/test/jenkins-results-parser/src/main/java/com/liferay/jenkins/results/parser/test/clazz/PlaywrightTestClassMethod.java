/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PlaywrightTestClassMethod extends TestClassMethod {

	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (isBuildCachingEnabled() && !_cachedTestReportSearched) {
			getCachedTestReport();
		}

		return _cachedDownstreamBuildReport;
	}

	public TestReport getCachedTestReport() {
		if (!isBuildCachingEnabled() || _cachedTestReportSearched) {
			return _cachedTestReport;
		}

		BatchTestClassGroup batchTestClassGroup =
			_playwrightJUnitTestClass.getBatchTestClassGroup();

		TestClassReport testClassReport =
			batchTestClassGroup.getCachedTestClassReport(
				_playwrightJUnitTestClass.getSpecFilePath());

		if (testClassReport == null) {
			return null;
		}

		for (TestReport testReport : testClassReport.getTestReports()) {
			String fullTestName = JenkinsResultsParserUtil.combine(
				testReport.getTestClassName(), " > ", testReport.getTestName());

			if (fullTestName.equals(getName())) {
				_cachedDownstreamBuildReport =
					testClassReport.getDownstreamBuildReport();
				_cachedTestReport = testReport;
				_cachedTestReportSearched = true;

				return _cachedTestReport;
			}
		}

		return null;
	}

	@Override
	public String getName() {
		TestClass testClass = getTestClass();

		return JenkinsResultsParserUtil.combine(
			testClass.getName(), " > ", super.getName());
	}

	public String getTestName() {
		return super.getName();
	}

	protected PlaywrightTestClassMethod(
		boolean ignored, String name, String issues, TestClass testClass) {

		super(ignored, name, issues, testClass);

		_playwrightJUnitTestClass = (PlaywrightJUnitTestClass)testClass;
	}

	protected PlaywrightTestClassMethod(
		boolean ignored, String name, TestClass testClass) {

		super(ignored, name, testClass);

		_playwrightJUnitTestClass = (PlaywrightJUnitTestClass)testClass;
	}

	protected PlaywrightTestClassMethod(
		JSONObject jsonObject, TestClass testClass) {

		super(jsonObject, testClass);

		_playwrightJUnitTestClass = (PlaywrightJUnitTestClass)testClass;
	}

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private TestReport _cachedTestReport;
	private boolean _cachedTestReportSearched;
	private final PlaywrightJUnitTestClass _playwrightJUnitTestClass;

}