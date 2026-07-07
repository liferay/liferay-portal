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
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class JSUnitBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult
		<JSUnitModulesTestClass, TestClassMethod> {

	public JSUnitBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testClass, testrayBuild, topLevelBuildReport);
	}

	@Override
	public String getComponentName() {
		JSUnitModulesTestClass jsUnitModulesTestClass = getTestClass();

		String componentName =
			jsUnitModulesTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		TestClassReport testClassResult = _getTestClassReport();

		if (testClassResult == null) {
			return 0;
		}

		return testClassResult.getDuration();
	}

	@Override
	public String getErrors() {
		BuildReport buildReport = getBuildReport();

		TestClassReport testClassResult = _getTestClassReport();

		if (testClassResult == null) {
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

		if (!_isTestClassResultsFailing() || !testClassResult.isFailing()) {
			return null;
		}

		Map<String, String> errorMessages = new HashMap<>();

		for (TestReport testResult : testClassResult.getTestReports()) {
			if (!testResult.isFailing()) {
				continue;
			}

			String errorMessage = testResult.getErrorDetails();

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
				errorMessage = buildReport.getFailureMessage();
			}

			errorMessage = formatErrorMessage(errorMessage);

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
				errorMessage = "Failed for unknown reason";
			}

			String testName = testResult.getTestName();

			errorMessages.put(
				testName,
				JenkinsResultsParserUtil.combine(testName, ": ", errorMessage));
		}

		if (errorMessages.size() > 1) {
			return JenkinsResultsParserUtil.combine(
				"Failed tests:\n",
				JenkinsResultsParserUtil.join(
					"\n", new ArrayList<>(errorMessages.keySet())));
		}
		else if (errorMessages.size() == 1) {
			List<String> values = new ArrayList<>(errorMessages.values());

			return values.get(0);
		}

		return "Failed for unknown reason";
	}

	@Override
	public String getName() {
		JSUnitModulesTestClass jsUnitModulesTestClass = getTestClass();

		return jsUnitModulesTestClass.getTestTaskName();
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		TestClassReport testClassResult = _getTestClassReport();

		if (testClassResult == null) {
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

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.add(getParentTestrayCaseResultTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected void initBuildReport() {
		JSUnitModulesTestClass jsUnitModulesTestClass = getTestClass();

		if (jsUnitModulesTestClass.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				jsUnitModulesTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				setBuildReport(cachedDownstreamBuildReport);

				return;
			}
		}

		super.initBuildReport();
	}

	private TestClassReport _getTestClassReport() {
		if (_testClassReport != null) {
			return _testClassReport;
		}

		JSUnitModulesTestClass jsUnitModulesTestClass = getTestClass();

		if (jsUnitModulesTestClass.isBuildCachingEnabled()) {
			TestClassReport cachedTestClassReport =
				jsUnitModulesTestClass.getCachedTestClassReport();

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

		for (TestClassReport testClassResult :
				downstreamBuildReport.getTestClassReports()) {

			if (Objects.equals(testClassResult.getTestClassName(), getName())) {
				_testClassReport = testClassResult;

				return _testClassReport;
			}
		}

		return _testClassReport;
	}

	private boolean _isTestClassResultsFailing() {
		TestClassReport testClassReport = _getTestClassReport();

		if ((testClassReport == null) || testClassReport.isFailing()) {
			return true;
		}

		return false;
	}

	private TestClassReport _testClassReport;

}