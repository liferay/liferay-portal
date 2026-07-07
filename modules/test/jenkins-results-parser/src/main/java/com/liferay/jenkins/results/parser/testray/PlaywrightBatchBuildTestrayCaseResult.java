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
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightTestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kenji Heigel
 */
public class PlaywrightBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult
		<PlaywrightJUnitTestClass, PlaywrightTestClassMethod> {

	public PlaywrightBatchBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestClassMethod testClassMethod, TestrayBuild testrayBuild,
		TopLevelBuildReport topLevelBuildReport) {

		super(
			axisTestClassGroup, testClass, testClassMethod, testrayBuild,
			topLevelBuildReport);
	}

	@Override
	public String getComponentName() {
		PlaywrightJUnitTestClass playwrightJUnitTestClass = getTestClass();

		String componentName =
			playwrightJUnitTestClass.getTestrayMainComponentName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(componentName)) {
			return super.getComponentName();
		}

		return componentName;
	}

	@Override
	public long getDuration() {
		return getTestResultDuration();
	}

	@Override
	public String getErrors() {
		String errors = null;

		BuildReport buildReport = getBuildReport();

		TestReport testReport = getTestReport();

		if (testReport == null) {
			if (buildReport == null) {
				return "Unable to run build on CI";
			}

			errors = "Failed prior to running test";

			String result = buildReport.getResult();

			if (result == null) {
				errors = "Unable to finish build on CI";
			}

			if (result.equals("ABORTED")) {
				errors = buildReport.getJobName() + " timed out after 2 hours";
			}

			if (result.equals("SUCCESS") || result.equals("UNSTABLE")) {
				errors = "Unable to run test on CI";
			}

			String failureMessage = formatErrorMessage(
				buildReport.getFailureMessage());

			if (JenkinsResultsParserUtil.isNullOrEmpty(failureMessage)) {
				return errors;
			}

			return errors + ": " + failureMessage;
		}

		if (testReport.isSkipped()) {
			PlaywrightTestClassMethod playwrightTestClassMethod =
				getTestClassMethod();

			if (playwrightTestClassMethod.isIgnored()) {
				return "Test run skipped on CI";
			}

			return "Failed to run test on CI";
		}

		if (!testReport.isFailing()) {
			return null;
		}

		errors = testReport.getErrorDetails();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			errors = buildReport.getFailureMessage();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			return "Failed for unknown reason";
		}

		String stackTrace = testReport.getErrorStackTrace();

		if (stackTrace.length() > 500) {
			int index = stackTrace.indexOf("›");

			return stackTrace.substring(index, 500);
		}

		errors = formatErrorMessage(errors);

		if (JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			return "Failed for unknown reason";
		}

		return errors;
	}

	@Override
	public String getIssues() {
		PlaywrightTestClassMethod playwrightTestClassMethod =
			getTestClassMethod();

		return playwrightTestClassMethod.getIssues();
	}

	@Override
	public String getName() {
		PlaywrightJUnitTestClass playwrightJUnitTestClass = getTestClass();

		if (playwrightJUnitTestClass == null) {
			return super.getName();
		}

		PlaywrightTestClassMethod playwrightTestClassMethod =
			getTestClassMethod();

		return playwrightTestClassMethod.getName();
	}

	@Override
	public Status getStatus() {
		return getTestResultStatus();
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.addAll(getLiferayLogTestrayAttachments());
		testrayAttachments.add(getParentTestrayCaseResultTestrayAttachment());
		testrayAttachments.add(getPlaywrightReportTestrayAttachment());
		testrayAttachments.add(getPlaywrightTraceViewerTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	protected TestReport findTestReport() {
		PlaywrightTestClassMethod playwrightTestClassMethod =
			getTestClassMethod();

		if (playwrightTestClassMethod.isBuildCachingEnabled()) {
			TestReport cachedTestReport =
				playwrightTestClassMethod.getCachedTestReport();

			if (cachedTestReport != null) {
				return cachedTestReport;
			}
		}

		DownstreamBuildReport downstreamBuildReport =
			getDownstreamBuildReport();

		if (downstreamBuildReport == null) {
			return null;
		}

		TestClassReport playwrightTestClassReport = null;

		PlaywrightJUnitTestClass playwrightJUnitTestClass = getTestClass();

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			if (Objects.equals(
					playwrightJUnitTestClass.getSpecFilePath(),
					testClassReport.getTestClassName())) {

				playwrightTestClassReport = testClassReport;
			}
		}

		if (playwrightTestClassReport == null) {
			return null;
		}

		for (TestReport testReport :
				playwrightTestClassReport.getTestReports()) {

			String fullTestName = JenkinsResultsParserUtil.combine(
				testReport.getTestClassName(), " > ", testReport.getTestName());

			if (fullTestName.equals(getName())) {
				return testReport;
			}
		}

		System.out.println("Unable to find test result for: " + getName());

		return null;
	}

	protected TestrayAttachment getPlaywrightReportTestrayAttachment() {
		return getTestrayAttachment(
			getBuildReport(), "Playwright Report",
			getAxisName() + "/playwright-report/index.html");
	}

	protected TestrayAttachment getPlaywrightTraceViewerTestrayAttachment() {
		TestReport testReport = getTestReport();

		if (testReport == null) {
			return null;
		}

		Matcher matcher = _traceZipPattern.matcher(
			testReport.getErrorStackTrace());

		if (!matcher.find()) {
			return null;
		}

		BuildReport buildReport = getBuildReport();

		String traceZipFilePath = matcher.group("traceZipFilePath");

		URL traceZipURL = buildReport.getTestrayAttachmentURLBySuffix(
			traceZipFilePath);

		if (traceZipURL == null) {
			return null;
		}

		String traceZipURLPath = String.valueOf(traceZipURL);

		try {
			traceZipURLPath = traceZipURLPath.replace(
				JenkinsResultsParserUtil.getBuildProperty(
					"build.base.artifact.url"),
				"https://playwright.liferay.com/testray-results");
		}
		catch (IOException ioException) {
			return null;
		}

		try {
			URL url = new URL(
				"https://playwright.liferay.com/?trace=" + traceZipURLPath);

			return new DefaultTestrayAttachment(
				this, "Trace Viewer", traceZipURLPath, url);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Override
	protected void initBuildReport() {
		PlaywrightTestClassMethod playwrightTestClassMethod =
			getTestClassMethod();

		if (playwrightTestClassMethod.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				playwrightTestClassMethod.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				setBuildReport(cachedDownstreamBuildReport);

				return;
			}
		}

		super.initBuildReport();
	}

	private static final Pattern _traceZipPattern = Pattern.compile(
		"npx playwright show-trace " +
			"(?<traceZipFilePath>test-results/[^/]+/trace.zip)");

}