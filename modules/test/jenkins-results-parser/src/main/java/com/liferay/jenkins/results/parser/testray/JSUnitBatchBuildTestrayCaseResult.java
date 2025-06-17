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
import com.liferay.jenkins.results.parser.test.clazz.JSUnitModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class JSUnitBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public JSUnitBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup,
		TestClassMethod testClassMethod) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		_testClassMethod = testClassMethod;

		_jsUnitModulesTestClass =
			(JSUnitModulesTestClass)testClassMethod.getTestClass();
	}

	@Override
	public String getComponentName() {
		String componentName =
			_jsUnitModulesTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		List<TestClassReport> testClassResults = _getTestClassReports();

		if (testClassResults == null) {
			return 0;
		}

		long duration = 0;

		for (TestClassReport testClassResult : testClassResults) {
			duration += testClassResult.getDuration();
		}

		return duration;
	}

	@Override
	public String getErrors() {
		BuildReport buildReport = getBuildReport();

		List<TestClassReport> testClassResults = _getTestClassReports();

		if ((testClassResults == null) || testClassResults.isEmpty()) {
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

		if (!_isTestClassResultsFailing()) {
			return null;
		}

		Map<String, String> errorMessages = new HashMap<>();

		for (TestClassReport testClassResult : testClassResults) {
			if ((testClassResult == null) || !testClassResult.isFailing()) {
				continue;
			}

			for (TestReport testResult : testClassResult.getTestReports()) {
				if (!testResult.isFailing()) {
					continue;
				}

				String errorMessage = testResult.getErrorDetails();

				if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
					errorMessage = buildReport.getFailureMessage();
				}

				if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
					errorMessage = "Failed for unknown reason";
				}

				if (errorMessage.contains("\n")) {
					errorMessage = errorMessage.substring(
						0, errorMessage.indexOf("\n"));
				}

				errorMessage = errorMessage.trim();

				if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
					errorMessage = "Failed for unknown reason";
				}

				String testName = testResult.getTestName();

				errorMessages.put(
					testName,
					JenkinsResultsParserUtil.combine(
						testName, ": ", errorMessage));
			}
		}

		if (errorMessages.size() > 1) {
			return JenkinsResultsParserUtil.combine(
				"Failed tests: ",
				JenkinsResultsParserUtil.join(
					", ", new ArrayList<>(errorMessages.keySet())));
		}
		else if (errorMessages.size() == 1) {
			List<String> values = new ArrayList<>(errorMessages.values());

			return values.get(0);
		}

		return "Failed for unknown reason";
	}

	@Override
	public String getName() {
		return _testClassMethod.getName();
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		List<TestClassReport> testClassResults = _getTestClassReports();

		if ((testClassResults == null) || testClassResults.isEmpty()) {
			String result = buildReport.getResult();

			if ((result == null) || result.equals("ABORTED") ||
				result.equals("FAILURE") || result.equals("SUCCESS") ||
				result.equals("UNSTABLE")) {

				return Status.UNTESTED;
			}

			return Status.FAILED;
		}

		if (_isTestClassResultsFailing()) {
			return Status.FAILED;
		}

		return Status.PASSED;
	}

	private List<TestClassReport> _getTestClassReports() {
		if (_testClassReports != null) {
			return _testClassReports;
		}

		_testClassReports = new ArrayList<>();

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return _testClassReports;
		}

		String taskDirectoryName = getName();

		taskDirectoryName = taskDirectoryName.replace(":packageRunTest", "");

		for (TestClassReport testClassResult :
				downstreamBuildReport.getTestClassReports()) {

			String testResultTaskName = _getTestResultTaskName(testClassResult);

			if (testResultTaskName.startsWith(taskDirectoryName)) {
				_testClassReports.add(testClassResult);
			}
		}

		return _testClassReports;
	}

	private String _getTestResultTaskName(TestClassReport testClassReport) {
		String testClassName = testClassReport.getTestClassName();

		if (testClassName.contains(".modules.")) {
			testClassName = testClassName.replaceAll(
				".*\\.modules(\\..+)", "$1");
		}
		else {
			testClassName = ".apps." + testClassName;
		}

		return testClassName.replaceAll("\\.", ":");
	}

	private boolean _isTestClassResultsFailing() {
		for (TestClassReport testClassResult : _getTestClassReports()) {
			if (testClassResult.isFailing()) {
				return true;
			}
		}

		return false;
	}

	private final JSUnitModulesTestClass _jsUnitModulesTestClass;
	private final TestClassMethod _testClassMethod;
	private List<TestClassReport> _testClassReports;

}