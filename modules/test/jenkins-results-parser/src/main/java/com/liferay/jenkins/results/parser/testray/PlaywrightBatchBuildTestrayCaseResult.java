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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kenji Heigel
 */
public class PlaywrightBatchBuildTestrayCaseResult
	extends BatchBuildTestrayCaseResult {

	public PlaywrightBatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport,
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestClassMethod testClassMethod) {

		super(testrayBuild, topLevelBuildReport, axisTestClassGroup);

		_playwrightJUnitTestClass = (PlaywrightJUnitTestClass)testClass;
		_playwrightTestClassMethod = (PlaywrightTestClassMethod)testClassMethod;
	}

	@Override
	public BuildReport getBuildReport() {
		if (_playwrightTestClassMethod.isBuildCachingEnabled()) {
			DownstreamBuildReport cachedDownstreamBuildReport =
				_playwrightTestClassMethod.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReport != null) {
				return cachedDownstreamBuildReport;
			}
		}

		return super.getBuildReport();
	}

	@Override
	public String getComponentName() {
		String componentName =
			_playwrightJUnitTestClass.getTestrayMainComponentName();

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

			String failureMessage = buildReport.getFailureMessage();

			if (JenkinsResultsParserUtil.isNullOrEmpty(failureMessage)) {
				return errors;
			}

			return errors + ": " + failureMessage;
		}

		if (testReport.isSkipped()) {
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

		if (errors.contains("\n")) {
			errors = errors.substring(0, errors.indexOf("\n"));
		}

		errors = errors.trim();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
			return "Failed for unknown reason";
		}

		return errors;
	}

	@Override
	public String getIssues() {
		return _playwrightTestClassMethod.getIssues();
	}

	@Override
	public String getName() {
		if (_playwrightJUnitTestClass == null) {
			return super.getName();
		}

		return _playwrightTestClassMethod.getName();
	}

	@Override
	public Status getStatus() {
		return getTestResultStatus();
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments =
			super.getTestrayAttachments();

		testrayAttachments.addAll(getLiferayLogTestrayAttachments());

		testrayAttachments.add(getPlaywrightReportTestrayAttachment());
		testrayAttachments.add(getPlaywrightTraceViewerTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	public TestReport getTestReport() {
		if (_playwrightTestClassMethod.isBuildCachingEnabled()) {
			TestReport cachedTestReport =
				_playwrightTestClassMethod.getCachedTestReport();

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

		for (TestClassReport testClassReport :
				downstreamBuildReport.getTestClassReports()) {

			if (Objects.equals(
					_playwrightJUnitTestClass.getSpecFilePath(),
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

		String traceZipFilePath = matcher.group("traceZipFilePath");

		URL traceZipURL = null;

		BuildReport buildReport = getBuildReport();

		for (URL testrayAttachmentURL :
				buildReport.getTestrayAttachmentURLs()) {

			String testrayAttachmentURLString = String.valueOf(
				testrayAttachmentURL);

			if (testrayAttachmentURLString.endsWith(traceZipFilePath)) {
				traceZipURL = testrayAttachmentURL;

				break;
			}
		}

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

	private static final Pattern _traceZipPattern = Pattern.compile(
		"npx playwright show-trace " +
			"(?<traceZipFilePath>test-results/[^/]+/trace.zip)");

	private final PlaywrightJUnitTestClass _playwrightJUnitTestClass;
	private final PlaywrightTestClassMethod _playwrightTestClassMethod;

}