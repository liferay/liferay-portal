/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.FunctionalTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Hashimoto
 */
public class FunctionalBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult<FunctionalTestClass, TestClassMethod> {

	public FunctionalBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(axisTestClassGroup, testClass, testrayBuild, topLevelBuildReport);

		if (!(testClass instanceof FunctionalTestClass)) {
			throw new RuntimeException(
				"Test class is not a functional test class");
		}
	}

	@Override
	public String getComponentName() {
		FunctionalTestClass functionalTestClass = getTestClass();

		return JenkinsResultsParserUtil.getProperty(
			functionalTestClass.getPoshiProperties(),
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
		FunctionalTestClass functionalTestClass = getTestClass();

		return functionalTestClass.getTestClassMethodName();
	}

	@Override
	public int getPriority() {
		FunctionalTestClass functionalTestClass = getTestClass();

		String priority = JenkinsResultsParserUtil.getProperty(
			functionalTestClass.getPoshiProperties(), "priority");

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
		FunctionalTestClass functionalTestClass = getTestClass();

		return JenkinsResultsParserUtil.getProperty(
			functionalTestClass.getPoshiProperties(),
			"testray.component.names");
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.addAll(getLiferayLogTestrayAttachments());
		testrayAttachments.addAll(getLiferayOSGiLogTestrayAttachments());
		testrayAttachments.add(getParentTestrayCaseResultTestrayAttachment());
		testrayAttachments.add(_getPoshiConsoleTestrayAttachment());
		testrayAttachments.add(_getPoshiReportTestrayAttachment());
		testrayAttachments.add(_getPoshiSummaryTestrayAttachment());
		testrayAttachments.add(getWarningsTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected TestReport findTestReport() {
		FunctionalTestClass functionalTestClass = getTestClass();

		if (functionalTestClass.isBuildCachingEnabled()) {
			TestReport cachedTestReport =
				functionalTestClass.getCachedTestReport();

			if (cachedTestReport != null) {
				return cachedTestReport;
			}
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			TestReport testReport = testClassReport.getTestReport(getName());

			if (testReport != null) {
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

	@Override
	protected void initBuildReport() {
		FunctionalTestClass functionalTestClass = getTestClass();

		if (functionalTestClass.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				functionalTestClass.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				setBuildReport(cachedDownstreamBuildReport);

				return;
			}
		}

		super.initBuildReport();
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
				getAxisName(), "/", JenkinsResultsParserUtil.fixURL(name),
				"/console.txt.gz"));
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
				getAxisName(), "/", JenkinsResultsParserUtil.fixURL(name),
				"/index.html.gz"));
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
				getAxisName(), "/", JenkinsResultsParserUtil.fixURL(name),
				"/summary.html.gz"));
	}

}