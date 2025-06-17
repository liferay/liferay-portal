/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.FunctionalTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class FunctionalBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public FunctionalBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup, TestClass testClass) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		if (!(testClass instanceof FunctionalTestClass)) {
			throw new RuntimeException(
				"Test class is not a functional test class");
		}

		_functionalTestClass = (FunctionalTestClass)testClass;
	}

	@Override
	public String getComponentName() {
		return JenkinsResultsParserUtil.getProperty(
			_functionalTestClass.getPoshiProperties(),
			"testray.main.component.name");
	}

	@Override
	public long getDuration() {
		return getTestResultDuration();
	}

	@Override
	public String getErrors() {
		return getTestResultErrors();
	}

	@Override
	public String getName() {
		return _functionalTestClass.getTestClassMethodName();
	}

	@Override
	public int getPriority() {
		String priority = JenkinsResultsParserUtil.getProperty(
			_functionalTestClass.getPoshiProperties(), "priority");

		if ((priority != null) && priority.matches("\\d+")) {
			return Integer.parseInt(priority);
		}

		return 5;
	}

	@Override
	public Status getStatus() {
		TestReport testReport = getTestReport();

		if (testReport != null) {
			String errorDetails = testReport.getErrorDetails();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(errorDetails) &&
				errorDetails.contains("TEST_SETUP_ERROR:")) {

				return Status.BLOCKED;
			}
		}

		return getTestResultStatus();
	}

	@Override
	public String getSubcomponentNames() {
		return JenkinsResultsParserUtil.getProperty(
			_functionalTestClass.getPoshiProperties(),
			"testray.component.names");
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments =
			super.getTestrayAttachments();

		testrayAttachments.addAll(getLiferayLogTestrayAttachments());
		testrayAttachments.addAll(getLiferayOSGiLogTestrayAttachments());

		testrayAttachments.add(_getPoshiConsoleTestrayAttachment());
		testrayAttachments.add(_getPoshiReportTestrayAttachment());
		testrayAttachments.add(_getPoshiSummaryTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	public TestReport getTestReport() {
		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		for (TestReport testReport : downstreamBuildReport.getTestReports()) {
			if (Objects.equals(testReport.getTestName(), getName())) {
				return testReport;
			}
		}

		return null;
	}

	@Override
	protected List<TestrayAttachment> getLiferayLogTestrayAttachments() {
		if (getTestReport() == null) {
			return new ArrayList<>();
		}

		return super.getLiferayLogTestrayAttachments();
	}

	@Override
	protected List<TestrayAttachment> getLiferayOSGiLogTestrayAttachments() {
		if (getTestReport() == null) {
			return new ArrayList<>();
		}

		return super.getLiferayOSGiLogTestrayAttachments();
	}

	private TestrayAttachment _getPoshiConsoleTestrayAttachment() {
		if (getTestReport() == null) {
			return null;
		}

		String name = getName();

		name = name.replace("#", "_");

		return getTestrayAttachment(
			getBuildReport(), "Poshi Console",
			JenkinsResultsParserUtil.combine(
				getAxisBuildURLPath(), "/",
				JenkinsResultsParserUtil.fixURL(name), "/console.txt.gz"));
	}

	private TestrayAttachment _getPoshiReportTestrayAttachment() {
		if (getTestReport() == null) {
			return null;
		}

		String name = getName();

		name = name.replace("#", "_");

		return getTestrayAttachment(
			getBuildReport(), "Poshi Report",
			JenkinsResultsParserUtil.combine(
				getAxisBuildURLPath(), "/",
				JenkinsResultsParserUtil.fixURL(name), "/index.html.gz"));
	}

	private TestrayAttachment _getPoshiSummaryTestrayAttachment() {
		if (getTestReport() == null) {
			return null;
		}

		String name = getName();

		name = name.replace("#", "_");

		return getTestrayAttachment(
			getBuildReport(), "Poshi Summary",
			JenkinsResultsParserUtil.combine(
				getAxisBuildURLPath(), "/",
				JenkinsResultsParserUtil.fixURL(name), "/summary.html.gz"));
	}

	private final FunctionalTestClass _functionalTestClass;

}