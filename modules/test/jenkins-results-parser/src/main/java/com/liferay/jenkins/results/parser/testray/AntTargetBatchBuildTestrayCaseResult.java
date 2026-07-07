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
import com.liferay.jenkins.results.parser.test.clazz.BaseAntTargetTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class AntTargetBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult
		<BaseAntTargetTestClass, TestClassMethod> {

	public AntTargetBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testClass, testrayBuild, topLevelBuildReport);
	}

	@Override
	public String getComponentName() {
		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		String componentName =
			baseAntTargetTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		TestReport testReport = _getAntTargetTestReport();

		if (testReport != null) {
			return testReport.getDuration();
		}

		TestClassReport testClassReport = getTestClassReport();

		if (testClassReport == null) {
			return super.getDuration();
		}

		return testClassReport.getDuration();
	}

	@Override
	public String getErrors() {
		TestReport antTargetTestReport = _getAntTargetTestReport();

		TestClassReport testClassReport = getTestClassReport();

		BuildReport buildReport = getBuildReport();

		if ((antTargetTestReport == null) && (testClassReport == null)) {
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

		if (antTargetTestReport != null) {
			if (antTargetTestReport.isFailing()) {
				sb.append(antTargetTestReport.getTestName());
				sb.append(": ");
				sb.append(antTargetTestReport.getErrorStackTrace());
				sb.append("\n");
			}
		}
		else {
			for (TestReport testReport : testClassReport.getTestReports()) {
				if (testReport.isFailing()) {
					sb.append(testReport.getTestName());
					sb.append(": ");
					sb.append(testReport.getErrorStackTrace());
					sb.append("\n");
				}
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

		errorMessage = formatErrorMessage(errorMessage);

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		return errorMessage;
	}

	@Override
	public String getName() {
		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		if (baseAntTargetTestClass == null) {
			return super.getName();
		}

		String antTargetName = baseAntTargetTestClass.getAntTargetName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(antTargetName)) {
			return JenkinsResultsParserUtil.combine(
				getBatchName(), "[", antTargetName, "]");
		}

		return JenkinsResultsParserUtil.combine(
			getBatchName(), "[", baseAntTargetTestClass.getName(), "]");
	}

	@Override
	public Status getStatus() {
		BuildReport buildReport = getBuildReport();

		if (buildReport == null) {
			return Status.UNTESTED;
		}

		TestReport antTargetTestReport = _getAntTargetTestReport();

		if (antTargetTestReport != null) {
			if (antTargetTestReport.isFailing()) {
				return Status.FAILED;
			}

			if (antTargetTestReport.isSkipped()) {
				return Status.UNTESTED;
			}

			return Status.PASSED;
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

	@Override
	public String getTeamName() {
		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		String teamName = baseAntTargetTestClass.getTestrayTeamName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(teamName)) {
			return super.getTeamName();
		}

		return teamName;
	}

	public TestClassReport getTestClassReport() {
		if (_testClassReport != null) {
			return _testClassReport;
		}

		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		if (baseAntTargetTestClass.isBuildCachingEnabled()) {
			TestClassReport cachedTestClassReport =
				baseAntTargetTestClass.getCachedTestClassReport();

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

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.add(getParentTestrayCaseResultTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected void initBuildReport() {
		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		if (baseAntTargetTestClass.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				baseAntTargetTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				setBuildReport(cachedDownstreamBuildReport);

				return;
			}
		}

		super.initBuildReport();
	}

	private TestReport _getAntTargetTestReport() {
		if (_antTargetTestReportSearched) {
			return _antTargetTestReport;
		}

		_antTargetTestReportSearched = true;

		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		if (baseAntTargetTestClass == null) {
			return _antTargetTestReport;
		}

		String antTargetName = baseAntTargetTestClass.getAntTargetName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(antTargetName)) {
			return _antTargetTestReport;
		}

		TestClassReport testClassReport = getTestClassReport();

		if (testClassReport == null) {
			return _antTargetTestReport;
		}

		for (TestReport testReport : testClassReport.getTestReports()) {
			if (Objects.equals(antTargetName, testReport.getTestName())) {
				_antTargetTestReport = testReport;

				break;
			}
		}

		return _antTargetTestReport;
	}

	private String _getTestClassName() {
		BaseAntTargetTestClass baseAntTargetTestClass = getTestClass();

		String testClassName = baseAntTargetTestClass.getName();

		return testClassName.replaceAll("/", ".");
	}

	private TestReport _antTargetTestReport;
	private boolean _antTargetTestReportSearched;
	private TestClassReport _testClassReport;

}