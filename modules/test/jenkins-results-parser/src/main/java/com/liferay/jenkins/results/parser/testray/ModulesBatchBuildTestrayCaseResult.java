/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.ModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class ModulesBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	@Override
	public BuildReport getBuildReport() {
		if (_modulesTestClass.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				_modulesTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				return cachedDownstreamBuildReport;
			}
		}

		return super.getBuildReport();
	}

	@Override
	public String getComponentName() {
		String componentName = _modulesTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		TestClassReport testClassReport = getTestClassReport();

		if (testClassReport == null) {
			return super.getDuration();
		}

		return testClassReport.getDuration();
	}

	@Override
	public String getErrors() {
		TestClassReport testClassReport = getTestClassReport();

		BuildReport buildReport = getBuildReport();

		if (testClassReport == null) {
			if (buildReport == null) {
				return "Unable to run build on CI";
			}

			String result = buildReport.getResult();

			if (result == null) {
				return "Unable to finish build on CI";
			}

			if (result.equals("ABORTED")) {
				return buildReport.getJobName() + " timed out after 2 hours";
			}

			if (result.equals("SUCCESS") || result.equals("UNSTABLE")) {
				return "Unable to run test on CI";
			}

			return "Failed prior to running test";
		}

		StringBuilder sb = new StringBuilder();

		for (TestReport testReport : testClassReport.getTestReports()) {
			if (testReport.isFailing()) {
				sb.append(testReport.getTestName());
				sb.append(": ");
				sb.append(testReport.getErrorStackTrace());
				sb.append("\n");
			}
		}

		if (sb.length() == 0) {
			return null;
		}

		sb.setLength(sb.length() - 1);

		String errorMessage = sb.toString();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			errorMessage = buildReport.getFailureMessage();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		errorMessage = errorMessage.trim();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		return errorMessage;
	}

	@Override
	public String getName() {
		if (_modulesTestClass == null) {
			return super.getName();
		}

		return getBatchName() + "[" + _modulesTestClass.getName() + "]";
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		TestClassReport testClassReport = getTestClassReport();

		if (testClassReport == null) {
			String result = buildReport.getResult();

			if ((result == null) || result.equals("SUCCESS") ||
				result.equals("UNSTABLE")) {

				return Status.UNTESTED;
			}

			return Status.FAILED;
		}

		if (testClassReport.isFailing()) {
			return Status.FAILED;
		}

		return Status.PASSED;
	}

	public TestClassReport getTestClassReport() {
		if (_testClassReport != null) {
			return _testClassReport;
		}

		if (_modulesTestClass.isBuildCachingEnabled()) {
			TestClassReport cachedTestClassReport =
				_modulesTestClass.getCachedTestClassReport();

			if (cachedTestClassReport != null) {
				_testClassReport = cachedTestClassReport;

				return _testClassReport;
			}
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return _testClassReport;
		}

		String testClassName = _getTestClassName();

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			if (Objects.equals(
					testClassName, testClassReport.getTestClassName())) {

				_testClassReport = testClassReport;
			}
		}

		return _testClassReport;
	}

	protected ModulesBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup, TestClass testClass) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		_modulesTestClass = (ModulesTestClass)testClass;
	}

	private String _getTestClassName() {
		String testClassName = _modulesTestClass.getName();

		return JenkinsResultsParserUtil.combine(
			testClassName.replaceAll("/", "."));
	}

	private final ModulesTestClass _modulesTestClass;
	private TestClassReport _testClassReport;

}