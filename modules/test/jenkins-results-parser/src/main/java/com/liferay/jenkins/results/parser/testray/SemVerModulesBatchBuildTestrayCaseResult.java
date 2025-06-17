/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.SemVerModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kenji Heigel
 */
public class SemVerModulesBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public SemVerModulesBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup, TestClass testClass) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		_semVerModulesTestClass = (SemVerModulesTestClass)testClass;
	}

	@Override
	public String getComponentName() {
		String componentName =
			_semVerModulesTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		List<TestReport> testResults = getTestReports();

		if (testResults.isEmpty()) {
			return super.getDuration();
		}

		long duration = 0;

		for (TestReport testResult : testResults) {
			duration += testResult.getDuration();
		}

		return duration;
	}

	@Override
	public String getErrors() {
		List<TestReport> testReports = getTestReports();

		BuildReport buildReport = getBuildReport();

		if (testReports.isEmpty()) {
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

		for (TestReport testReport : testReports) {
			if (testReport.isFailing()) {
				sb.append(testReport.getTestName());
				sb.append(": ");
				sb.append(testReport.getErrorDetails());
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

		if (errorMessage.contains("\n")) {
			errorMessage = errorMessage.substring(
				0, errorMessage.indexOf("\n"));
		}

		errorMessage = errorMessage.trim();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		return errorMessage;
	}

	@Override
	public String getName() {
		if (_semVerModulesTestClass == null) {
			return super.getName();
		}

		return getBatchName() + "[" + _semVerModulesTestClass.getName() + "]";
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		List<TestReport> testReports = getTestReports();

		if (testReports.isEmpty()) {
			String result = buildReport.getResult();

			if ((result == null) || result.equals("SUCCESS") ||
				result.equals("UNSTABLE")) {

				return Status.UNTESTED;
			}

			return Status.FAILED;
		}

		for (TestReport testReport : testReports) {
			if (testReport.isFailing()) {
				return Status.FAILED;
			}
		}

		return Status.PASSED;
	}

	public List<TestReport> getTestReports() {
		if (_testReports != null) {
			return _testReports;
		}

		_testReports = new ArrayList<>();

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return _testReports;
		}

		TestClassReport buildTestClassReport = null;

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			if (Objects.equals(
					testClassReport.getTestClassName(), _TEST_CLASS_NAME)) {

				buildTestClassReport = testClassReport;
			}
		}

		if (buildTestClassReport == null) {
			return _testReports;
		}

		for (TestReport testReport : buildTestClassReport.getTestReports()) {
			Matcher matcher = _modulePathPattern.matcher(
				testReport.getTestName());

			if (matcher.find()) {
				String modulePath = matcher.group("modulePath");

				if (modulePath.startsWith(_getModulePath())) {
					_testReports.add(testReport);
				}
			}
		}

		return _testReports;
	}

	private String _getModulePath() {
		String modulePath = _semVerModulesTestClass.getName();

		if (modulePath.startsWith("modules")) {
			modulePath = modulePath.substring(7);
		}

		return modulePath;
	}

	private static final String _TEST_CLASS_NAME =
		"com.liferay.semantic.versioning.SemanticVersioningTest";

	private static final Pattern _modulePathPattern = Pattern.compile(
		"testSemanticVersioning\\[(?<modulePath>[\\w\\/-]+)\\]");

	private final SemVerModulesTestClass _semVerModulesTestClass;
	private List<TestReport> _testReports;

}