/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.CIFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.CompileFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GradleTaskFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.IntegrationTestTimeoutFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.JSUnitTestFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.LocalGitMirrorFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.ModulesCompilationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PMDFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PlaywrightCompilationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PluginGitIDFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SemanticVersioningFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.ServiceBuilderFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SourceFormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.StartupFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.UpgradeFailureMessageGenerator;
import com.liferay.jenkins.results.parser.test.clazz.FunctionalTestClass;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.testray.TestrayCloudBucket;
import com.liferay.jenkins.results.parser.testray.TestrayCloudObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class BaseDownstreamBuild extends BaseBuild implements DownstreamBuild {

	@Override
	public void addTimelineData(TimelineData timelineData) {
		timelineData.addTimelineData(this);
	}

	@Override
	public void generateBuildReport() {
		if (!Objects.equals(getStatus(), "completed")) {
			return;
		}

		_uploadJenkinsConsoleTestrayAttachment();

		if (!isBuildCachingEnabled() ||
			!JenkinsResultsParserUtil.isCloudCINode() || isFailing()) {

			return;
		}

		String distinctTimeStamp =
			JenkinsResultsParserUtil.getDistinctTimeStamp();

		File baseDir = new File(
			System.getProperty("java.io.tmpdir"), "/build-report-files/");

		File buildReportFile = new File(baseDir, distinctTimeStamp + ".json");

		File buildReportGzipFile = new File(
			baseDir, distinctTimeStamp + ".json.gz");

		String s3ObjectPath = _getS3ObjectPath(buildReportGzipFile);

		try {
			JSONObject buildReportJSONObject = getBuildReportJSONObject();

			if (buildReportJSONObject == null) {
				return;
			}

			JenkinsResultsParserUtil.write(
				buildReportFile, String.valueOf(buildReportJSONObject));

			JenkinsResultsParserUtil.gzip(buildReportFile, buildReportGzipFile);

			CloudBucketUtil.uploadS3File(s3ObjectPath, buildReportGzipFile);
		}
		catch (Exception exception) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to upload ", String.valueOf(buildReportGzipFile),
					" to ", s3ObjectPath),
				exception);
		}
		finally {
			JenkinsResultsParserUtil.delete(buildReportFile);
			JenkinsResultsParserUtil.delete(buildReportGzipFile);
		}
	}

	@Override
	public URL getArtifactsBaseURL() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		StringBuilder sb = new StringBuilder();

		sb.append(topLevelBuild.getArtifactsBaseURL());
		sb.append("/");
		sb.append(getJobVariant());
		sb.append("/");
		sb.append(getAxisVariable());

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	@Override
	public long getAverageDuration() {
		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if (axisTestClassGroup == null) {
			return 0L;
		}

		return axisTestClassGroup.getAverageDuration();
	}

	@Override
	public long getAverageOverheadDuration() {
		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if (axisTestClassGroup == null) {
			return 0L;
		}

		return axisTestClassGroup.getAverageOverheadDuration();
	}

	@Override
	public long getAverageTotalTestDuration() {
		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if (axisTestClassGroup == null) {
			return 0L;
		}

		return axisTestClassGroup.getAverageTotalTestDuration();
	}

	@Override
	public String getAxisName() {
		return JenkinsResultsParserUtil.combine(
			getJobVariant(), "/", getAxisVariable());
	}

	@Override
	public AxisTestClassGroup getAxisTestClassGroup() {
		Job job = getJob();

		return job.getAxisTestClassGroup(getAxisName());
	}

	@Override
	public String getAxisVariable() {
		return getParameterValue("AXIS_VARIABLE");
	}

	@Override
	public String getBaseGitRepositoryName() {
		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitRepositoryName)) {
			return gitRepositoryName;
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		gitRepositoryName = topLevelBuild.getParameterValue("REPOSITORY_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitRepositoryName)) {
			return gitRepositoryName;
		}

		String branchName = getBranchName();

		gitRepositoryName = "liferay-portal-ee";

		if (branchName.equals("master")) {
			gitRepositoryName = "liferay-portal";
		}

		return gitRepositoryName;
	}

	@Override
	public String getBatchName() {
		String jobVariant = getJobVariant();

		return jobVariant.replaceAll("([^/]+)/.*", "$1");
	}

	@Override
	public String getBuildName() {
		return getAxisName();
	}

	@Override
	public JSONObject getBuildReportJSONObject() {
		JSONObject buildReportJSONObject = super.getBuildReportJSONObject();

		buildReportJSONObject.put("axisName", getAxisName());

		JSONObject testReportJSONObject = getTestReportJSONObject(false);

		if (testReportJSONObject != null) {
			buildReportJSONObject.put(
				"failCount", testReportJSONObject.optInt("failCount")
			).put(
				"passCount", testReportJSONObject.optInt("passCount")
			).put(
				"skipCount", testReportJSONObject.optInt("skipCount")
			);
		}

		JSONArray testResultsJSONArray = new JSONArray();

		for (TestResult testResult : getTestResults(null)) {
			testResultsJSONArray.put(testResult.getTestReportJSONObject());
		}

		buildReportJSONObject.put("testResults", testResultsJSONArray);

		return buildReportJSONObject;
	}

	@Override
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();

		sb.append(getJobVariant());
		sb.append("/");
		sb.append(getAxisVariable());

		return sb.toString();
	}

	@Override
	public DownstreamBuildReport getDownstreamBuildReport() {
		return _downstreamBuildReport;
	}

	@Override
	public Element getGitHubMessageElement() {
		if (_gitHubMessageElement != null) {
			return _gitHubMessageElement;
		}

		String status = getStatus();

		if (!status.equals("completed") && (getParentBuild() != null)) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"[", getBuildName(), "] Skipped creating a failure GitHub ",
					"message because status is ", status));

			return null;
		}

		String result = getResult();

		if (result.equals("SUCCESS")) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"[", getBuildName(), "] Skipped creating a failure GitHub ",
					"message because result is ", result));

			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewAnchorElement(
				getBuildURL() + "/consoleText", null, getDisplayName()));

		if (result.equals("ABORTED")) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build was aborted"));

			List<Element> untestedElements = getTestResultGitHubElements(
				getUniqueFailureTestResults(), true);

			if (!untestedElements.isEmpty()) {
				Dom4JUtil.getOrderedListElement(
					untestedElements, messageElement, 3);
			}
		}

		String batchName = getBatchName();

		if (batchName.contains("playwright-js")) {
			for (URL url : getTestrayAttachmentURLs()) {
				String urlString = url.toString();

				if (urlString.contains("playwright-report/index.html")) {
					Dom4JUtil.addToElement(
						messageElement, " - ",
						Dom4JUtil.getNewAnchorElement(
							urlString, null, "Playwright Report"));

					break;
				}
			}
		}

		if (result.equals("FAILURE")) {
			Element failureMessageElement = getFailureMessageElement();

			if ((failureMessageElement != null) &&
				!batchName.contains("playwright-js")) {

				messageElement.add(failureMessageElement);
			}

			List<Element> untestedElements = getTestResultGitHubElements(
				getUniqueFailureTestResults(), true);

			if (!untestedElements.isEmpty()) {
				Dom4JUtil.getOrderedListElement(
					untestedElements, messageElement, 3);
			}
		}

		if (result.equals("MISSING")) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build is missing"));
		}

		if (result.equals("UNSTABLE")) {
			List<Element> failureElements = getTestResultGitHubElements(
				getUniqueFailureTestResults(), true);

			Dom4JUtil.getOrderedListElement(failureElements, messageElement, 3);

			if (failureElements.isEmpty()) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"[", getBuildName(),
						"] Skipped creating a failure GitHub message because ",
						"no failure elements were created"));

				return null;
			}
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"[", getBuildName(), "] Created a failure GitHub message: ",
				String.valueOf(hashCode())));

		_gitHubMessageElement = messageElement;

		return _gitHubMessageElement;
	}

	public Map<String, List<String>> getTestClassMethodNamesMap() {
		String batchName = getBatchName();

		if (!batchName.contains("integration") && !batchName.contains("unit")) {
			return Collections.emptyMap();
		}

		Map<String, List<String>> testClassMethodNamesMap = new HashMap<>();

		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if ((axisTestClassGroup == null) ||
			!axisTestClassGroup.hasTestClasses()) {

			return testClassMethodNamesMap;
		}

		List<TestClass> testClasses = axisTestClassGroup.getTestClasses();

		for (TestClass testClass : testClasses) {
			if (!(testClass instanceof JUnitTestClass)) {
				continue;
			}

			JUnitTestClass jUnitTestClass = (JUnitTestClass)testClass;

			List<String> methodNames = new ArrayList<>();

			for (TestClassMethod testClassMethod :
					testClass.getTestClassMethods()) {

				String testMethodName = testClassMethod.getName();

				if (!methodNames.contains(testMethodName)) {
					methodNames.add(testClassMethod.getName());
				}
			}

			testClassMethodNamesMap.put(
				jUnitTestClass.getTestClassName(), methodNames);
		}

		return testClassMethodNamesMap;
	}

	@Override
	public List<TestResult> getTestResults(String testStatus) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(testStatus)) {
			return getTestResults();
		}

		List<TestResult> testResults = new ArrayList<>();

		for (TestResult testResult : getTestResults()) {
			if (testStatus.equals(testResult.getStatus())) {
				testResults.add(testResult);
			}
		}

		return testResults;
	}

	@Override
	public List<TestResult> getUniqueFailureTestResults() {
		List<TestResult> uniqueFailureTestResults = new ArrayList<>();

		for (TestResult testResult : getTestResults(null)) {
			if (!testResult.isFailing()) {
				continue;
			}

			if (testResult.isUniqueFailure()) {
				uniqueFailureTestResults.add(testResult);
			}
		}

		for (TestResult untestedTestResult : getUntestedTestResults()) {
			if (untestedTestResult.isUniqueFailure()) {
				uniqueFailureTestResults.add(untestedTestResult);
			}
		}

		return uniqueFailureTestResults;
	}

	public Map<String, List<String>> getUntestedTestClassMethodNamesMap() {
		Map<String, List<String>> untestedTestClassMethodNamesMap =
			getTestClassMethodNamesMap();

		if (untestedTestClassMethodNamesMap.isEmpty()) {
			return Collections.emptyMap();
		}

		List<TestResult> testResults = getTestResults();

		if (testResults.isEmpty()) {
			return Collections.emptyMap();
		}

		for (TestResult testResult : testResults) {
			String testResultClassName = testResult.getClassName();

			if (untestedTestClassMethodNamesMap.containsKey(
					testResultClassName)) {

				List<String> testClassMethodNames =
					untestedTestClassMethodNamesMap.get(testResultClassName);

				testClassMethodNames.remove(testResult.getTestName());

				untestedTestClassMethodNamesMap.put(
					testResultClassName, testClassMethodNames);
			}
		}

		return untestedTestClassMethodNamesMap;
	}

	public List<TestResult> getUntestedTestResults() {
		Map<String, List<String>> untestedTestsMap =
			getUntestedTestClassMethodNamesMap();

		if (untestedTestsMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<TestResult> untestedTestResults = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry :
				untestedTestsMap.entrySet()) {

			List<String> testClassMethodNames = entry.getValue();

			if (testClassMethodNames.isEmpty()) {
				continue;
			}

			for (String methodName : testClassMethodNames) {
				JSONObject caseJSONObject = new JSONObject();

				String testClassName = entry.getKey();

				caseJSONObject.put(
					"className", testClassName
				).put(
					"duration", 0
				).put(
					"errorDetails", "This test was untested."
				).put(
					"errorStackTrace", ""
				).put(
					"name", methodName
				).put(
					"status", "UNTESTED"
				).put(
					"testName", methodName
				);

				TestResult testResult = TestResultFactory.newTestResult(
					this, caseJSONObject);

				TestClassResult testClassResult =
					testResult.getTestClassResult();

				if (testClassResult != null) {
					String status = testClassResult.getStatus();

					if (status.equals("SKIPPED")) {
						continue;
					}
				}

				untestedTestResults.add(testResult);
			}
		}

		return untestedTestResults;
	}

	@Override
	public List<TestResult> getUpstreamJobFailureTestResults() {
		List<TestResult> upstreamFailureTestResults = new ArrayList<>();

		for (TestResult testResult : getTestResults(null)) {
			if (!testResult.isFailing()) {
				continue;
			}

			if (!testResult.isUniqueFailure()) {
				upstreamFailureTestResults.add(testResult);
			}
		}

		for (TestResult untestedTestResult : getUntestedTestResults()) {
			if (!untestedTestResult.isUniqueFailure()) {
				upstreamFailureTestResults.add(untestedTestResult);
			}
		}

		return upstreamFailureTestResults;
	}

	public List<String> getWarningMessages() {
		List<String> warningMessages = new ArrayList<>();

		URL poshiWarningsURL = null;

		try {
			poshiWarningsURL = new URL(
				getArtifactsBaseURL() + "/poshi-warnings.xml.gz");
		}
		catch (IOException ioException) {
			return warningMessages;
		}

		StringBuilder sb = new StringBuilder();

		try (InputStream inputStream = poshiWarningsURL.openStream();
			GZIPInputStream gzipInputStream = new GZIPInputStream(
				inputStream)) {

			int i = 0;

			while ((i = gzipInputStream.read()) > 0) {
				sb.append((char)i);
			}
		}
		catch (IOException ioException) {
			return warningMessages;
		}

		try {
			Document document = Dom4JUtil.parse(sb.toString());

			Element rootElement = document.getRootElement();

			for (Element valueElement : rootElement.elements("value")) {
				String liferayErrorText = "LIFERAY_ERROR: ";

				String valueElementText = StringEscapeUtils.escapeHtml(
					valueElement.getText());

				if (valueElementText.startsWith(liferayErrorText)) {
					valueElementText = valueElementText.substring(
						liferayErrorText.length());
				}

				warningMessages.add(valueElementText);
			}
		}
		catch (DocumentException documentException) {
			warningMessages.add("Unable to parse Poshi warnings");
		}

		return warningMessages;
	}

	@Override
	public void saveBuildURLInBuildDatabase() {
		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.putProperty(
			BUILD_URLS_PROPERTIES_KEY, getAxisName(), getBuildURL(), false);

		_saveBadBuildURLsInBuildDatabase(getBadBuildURLs());
	}

	protected BaseDownstreamBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	@Override
	protected Element getGitHubMessageJobResultsElement() {
		return null;
	}

	@Override
	protected List<Element> getJenkinsReportBuildDurationsElements() {
		String urlSuffix = "buildDurationsElements";

		if (archiveFileExists(urlSuffix)) {
			return getArchiveFileElements(urlSuffix);
		}

		List<Element> jenkinsReportTableRowElements = new ArrayList<>();

		Element buildDurationsHeaderElement = Dom4JUtil.getNewElement("tr");

		List<String> childStopWatchRows = new ArrayList<>();

		childStopWatchRows.add("build-duration-names");
		childStopWatchRows.add("build-duration-values");
		childStopWatchRows.add("build-overhead-duration-values");
		childStopWatchRows.add("build-test-duration-values");

		buildDurationsHeaderElement.addAttribute(
			"child-stopwatch-rows",
			JenkinsResultsParserUtil.join(",", childStopWatchRows));

		buildDurationsHeaderElement.addAttribute(
			"id", hashCode() + "-build-durations-header");
		buildDurationsHeaderElement.addAttribute("style", "display: none");

		Element buildDurationsElement = Dom4JUtil.getNewElement(
			"td", buildDurationsHeaderElement,
			getExpanderAnchorElement(
				"build-durations-header", String.valueOf(hashCode())),
			Dom4JUtil.getNewElement("u", null, "Build Durations"));

		buildDurationsElement.addAttribute(
			"style",
			JenkinsResultsParserUtil.combine(
				"text-indent: ",
				String.valueOf(getDepth() * PIXELS_WIDTH_INDENT), "px"));

		jenkinsReportTableRowElements.add(buildDurationsHeaderElement);

		Element durationNamesElement = Dom4JUtil.getNewElement("tr");

		durationNamesElement.addAttribute(
			"id", hashCode() + "-build-duration-names");
		durationNamesElement.addAttribute("style", "display: none;");

		Element durationNamesDataElement = Dom4JUtil.getNewElement(
			"th", durationNamesElement, "Name");

		String style = JenkinsResultsParserUtil.combine(
			"text-indent: ",
			String.valueOf((getDepth() + 1) * PIXELS_WIDTH_INDENT), "px");

		durationNamesDataElement.addAttribute("style", style);

		boolean overheadIncluded = false;

		String batchName = getBatchName();

		if (batchName.startsWith("function") ||
			batchName.startsWith("integration") ||
			batchName.startsWith("modules-integration") ||
			batchName.startsWith("modules-unit") ||
			batchName.startsWith("unit")) {

			overheadIncluded = true;
		}

		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration");
		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration (est)");
		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration (+/-)");

		jenkinsReportTableRowElements.add(durationNamesElement);

		Element durationValuesElement = Dom4JUtil.getNewElement("tr");

		durationValuesElement.addAttribute(
			"id", hashCode() + "-build-duration-values");
		durationValuesElement.addAttribute("style", "display: none;");

		Element durationValuesDataElement = Dom4JUtil.getNewElement(
			"td", durationValuesElement, "Total Duration");

		durationValuesDataElement.addAttribute("style", style);

		long duration = getDuration();
		long averageDuration = getAverageDuration();

		Dom4JUtil.getNewElement(
			"td", durationValuesElement,
			JenkinsResultsParserUtil.toDurationString(duration));
		Dom4JUtil.getNewElement(
			"td", durationValuesElement,
			JenkinsResultsParserUtil.toDurationString(averageDuration));
		Dom4JUtil.getNewElement(
			"td", durationValuesElement,
			getDiffDurationString(duration - averageDuration));

		jenkinsReportTableRowElements.add(durationValuesElement);

		if (!overheadIncluded) {
			archiveFileElements(urlSuffix, jenkinsReportTableRowElements);

			return jenkinsReportTableRowElements;
		}

		Element totalTestDurationValuesElement = Dom4JUtil.getNewElement("tr");

		totalTestDurationValuesElement.addAttribute(
			"id", hashCode() + "-build-test-duration-values");
		totalTestDurationValuesElement.addAttribute("style", "display: none;");

		Element totalTestDurationsValuesDataElement = Dom4JUtil.getNewElement(
			"td", totalTestDurationValuesElement, "Total Test Durations");

		totalTestDurationsValuesDataElement.addAttribute("style", style);

		long totalTestDuration = 0L;

		for (TestResult testResult : getTestResults()) {
			totalTestDuration += testResult.getDuration();
		}

		long averageTotalTestDuration = getAverageTotalTestDuration();

		Dom4JUtil.getNewElement(
			"td", totalTestDurationValuesElement,
			JenkinsResultsParserUtil.toDurationString(totalTestDuration));
		Dom4JUtil.getNewElement(
			"td", totalTestDurationValuesElement,
			JenkinsResultsParserUtil.toDurationString(
				averageTotalTestDuration));
		Dom4JUtil.getNewElement(
			"td", totalTestDurationValuesElement,
			getDiffDurationString(
				totalTestDuration - averageTotalTestDuration));

		jenkinsReportTableRowElements.add(totalTestDurationValuesElement);

		Element overheadDurationValuesElement = Dom4JUtil.getNewElement("tr");

		overheadDurationValuesElement.addAttribute(
			"id", hashCode() + "-build-overhead-duration-values");
		overheadDurationValuesElement.addAttribute("style", "display: none;");

		Element overheadDurationValuesDataElement = Dom4JUtil.getNewElement(
			"td", overheadDurationValuesElement, "Overhead Duration");

		overheadDurationValuesDataElement.addAttribute("style", style);

		long overheadDuration = duration - totalTestDuration;

		if (overheadDuration <= 0L) {
			overheadDuration = 0L;
		}

		long averageOverheadDuration = getAverageOverheadDuration();

		Dom4JUtil.getNewElement(
			"td", overheadDurationValuesElement,
			JenkinsResultsParserUtil.toDurationString(overheadDuration));
		Dom4JUtil.getNewElement(
			"td", overheadDurationValuesElement,
			JenkinsResultsParserUtil.toDurationString(averageOverheadDuration));
		Dom4JUtil.getNewElement(
			"td", overheadDurationValuesElement,
			getDiffDurationString(overheadDuration - averageOverheadDuration));

		jenkinsReportTableRowElements.add(overheadDurationValuesElement);

		archiveFileElements(urlSuffix, jenkinsReportTableRowElements);

		return jenkinsReportTableRowElements;
	}

	@Override
	protected List<Element> getJenkinsReportTestDurationsElements() {
		String batchName = getBatchName();

		if (!batchName.startsWith("function") &&
			!batchName.startsWith("integration") &&
			!batchName.startsWith("modules-integration") &&
			!batchName.startsWith("modules-unit") &&
			!batchName.startsWith("unit")) {

			return Collections.emptyList();
		}

		String urlSuffix = "testDurationsElements";

		if (archiveFileExists(urlSuffix)) {
			return getArchiveFileElements(urlSuffix);
		}

		List<Element> jenkinsReportTableRowElements = new ArrayList<>();

		Element testDurationsHeaderElement = Dom4JUtil.getNewElement("tr");

		testDurationsHeaderElement.addAttribute(
			"id", hashCode() + "-test-durations-header");
		testDurationsHeaderElement.addAttribute("style", "display: none");

		List<String> childStopWatchRows = new ArrayList<>();

		childStopWatchRows.add("test-duration-names");

		Element testDurationsElement = Dom4JUtil.getNewElement(
			"td", testDurationsHeaderElement,
			getExpanderAnchorElement(
				"test-durations-header", String.valueOf(hashCode())),
			Dom4JUtil.getNewElement("u", null, "Test Durations"));

		testDurationsElement.addAttribute(
			"style",
			JenkinsResultsParserUtil.combine(
				"text-indent: ",
				String.valueOf(getDepth() * PIXELS_WIDTH_INDENT), "px"));

		jenkinsReportTableRowElements.add(testDurationsHeaderElement);

		Element durationNamesElement = Dom4JUtil.getNewElement("tr");

		durationNamesElement.addAttribute(
			"id", hashCode() + "-test-duration-names");
		durationNamesElement.addAttribute("style", "display: none;");

		Element durationNamesDataElement = Dom4JUtil.getNewElement(
			"th", durationNamesElement, "Name");

		String style = JenkinsResultsParserUtil.combine(
			"text-indent: ",
			String.valueOf((getDepth() + 1) * PIXELS_WIDTH_INDENT), "px");

		durationNamesDataElement.addAttribute("style", style);

		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration");
		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration (est)");
		Dom4JUtil.getNewElement("th", durationNamesElement, "Duration (+/-)");

		jenkinsReportTableRowElements.add(durationNamesElement);

		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

		if (axisTestClassGroup != null) {
			List<TestClass> testClasses = axisTestClassGroup.getTestClasses();

			for (int i = 0; i < testClasses.size(); i++) {
				TestClass testClass = testClasses.get(i);

				TestClassResult testClassResult = null;

				String testClassName = null;

				long duration = 0L;

				if (testClass instanceof JUnitTestClass) {
					JUnitTestClass jUnitTestClass = (JUnitTestClass)testClass;

					testClassName = jUnitTestClass.getTestClassName();

					testClassResult = getTestClassResult(testClassName);

					if (testClassResult != null) {
						duration = testClassResult.getDuration();
					}
				}
				else if (testClass instanceof FunctionalTestClass) {
					FunctionalTestClass functionalTestClass =
						(FunctionalTestClass)testClass;

					testClassName =
						functionalTestClass.getTestClassMethodName();

					testClassResult = getTestClassResult(
						"com.liferay.poshi.runner.PoshiRunner");

					if (testClassResult != null) {
						for (TestResult testResult :
								testClassResult.getTestResults()) {

							String testMethodName =
								"test[" + testClassName + "]";

							if (!Objects.equals(
									testMethodName, testResult.getTestName())) {

								continue;
							}

							duration = testResult.getDuration();

							break;
						}
					}
				}

				Element durationValuesElement = Dom4JUtil.getNewElement("tr");

				childStopWatchRows.add("test-duration-values-" + i);

				durationValuesElement.addAttribute(
					"id", hashCode() + "-test-duration-values-" + i);
				durationValuesElement.addAttribute("style", "display: none;");

				Element durationValuesDataElement = Dom4JUtil.getNewElement(
					"td", durationValuesElement, testClassName);

				durationValuesDataElement.addAttribute("style", style);

				long averageDuration = testClass.getAverageDuration();

				Dom4JUtil.getNewElement(
					"td", durationValuesElement,
					JenkinsResultsParserUtil.toDurationString(duration));
				Dom4JUtil.getNewElement(
					"td", durationValuesElement,
					JenkinsResultsParserUtil.toDurationString(averageDuration));
				Dom4JUtil.getNewElement(
					"td", durationValuesElement,
					getDiffDurationString(duration - averageDuration));

				jenkinsReportTableRowElements.add(durationValuesElement);
			}
		}

		testDurationsHeaderElement.addAttribute(
			"child-stopwatch-rows",
			JenkinsResultsParserUtil.join(",", childStopWatchRows));

		archiveFileElements(urlSuffix, jenkinsReportTableRowElements);

		return jenkinsReportTableRowElements;
	}

	protected long getStopWatchRecordDuration(String stopWatchRecordName) {
		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		if (stopWatchRecordsGroup == null) {
			return 0L;
		}

		StopWatchRecord stopWatchRecord = stopWatchRecordsGroup.get(
			stopWatchRecordName);

		if (stopWatchRecord == null) {
			return 0L;
		}

		Long duration = stopWatchRecord.getDuration();

		if (duration == null) {
			return 0L;
		}

		return duration;
	}

	protected List<Element> getTestResultGitHubElements(
		List<TestResult> testResults, boolean uniqueFailures) {

		List<Element> testResultGitHubElements = new ArrayList<>();

		List<TestClassResult> testClassResults = new ArrayList<>();

		for (TestResult testResult : testResults) {
			if (testResult instanceof PoshiJUnitTestResult) {
				testResultGitHubElements.add(testResult.getGitHubElement());

				continue;
			}

			TestClassResult testClassResult = testResult.getTestClassResult();

			if (testClassResult == null) {
				testResultGitHubElements.add(testResult.getGitHubElement());

				continue;
			}

			if (testClassResults.contains(testClassResult)) {
				continue;
			}

			Element gitHubElement = testClassResult.getGitHubElement(
				uniqueFailures);

			if (gitHubElement == null) {
				continue;
			}

			testResultGitHubElements.add(gitHubElement);

			testClassResults.add(testClassResult);
		}

		return testResultGitHubElements;
	}

	private String _getS3ObjectPath(File file) {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.build.reports.path"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		BuildDatabase buildDatabase = getBuildDatabase();

		List<Workspace> workspaces = buildDatabase.getWorkspaces();

		if (workspaces.isEmpty()) {
			return null;
		}

		Workspace workspace = workspaces.get(0);

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		sb.append("/");
		sb.append(workspaceGitRepository.getName());
		sb.append("/");
		sb.append(workspaceGitRepository.getBaseBranchSHA());
		sb.append("/");
		sb.append(workspaceGitRepository.getSenderBranchSHA());
		sb.append("/");
		sb.append(getBatchName());
		sb.append("/");
		sb.append(file.getName());

		return sb.toString();
	}

	private String _getTestrayAttachmentBaseKey() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		if (topLevelBuild == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(
			JenkinsResultsParserUtil.toDateString(
				new Date(topLevelBuild.getStartTime()), "yyyy-MM",
				"America/Los_Angeles"));

		sb.append("/");

		JenkinsMaster jenkinsMaster = topLevelBuild.getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("/");
		sb.append(topLevelBuild.getJobName());
		sb.append("/");
		sb.append(topLevelBuild.getBuildNumber());
		sb.append("/");
		sb.append(getAxisName());

		return sb.toString();
	}

	private void _saveBadBuildURLsInBuildDatabase(List<String> badBuildURLs) {
		if (badBuildURLs.isEmpty()) {
			return;
		}

		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.putProperty(
			BAD_BUILD_URLS_PROPERTIES_KEY, getAxisName(),
			JenkinsResultsParserUtil.join(",", badBuildURLs), false);
	}

	private void _uploadJenkinsConsoleTestrayAttachment() {
		if (!Objects.equals(getStatus(), "completed")) {
			return;
		}

		String workspace = System.getenv("WORKSPACE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(workspace)) {
			throw new RuntimeException("Please set WORKSPACE");
		}

		File testrayUploadBaseDir = new File(
			workspace, JenkinsResultsParserUtil.getDistinctTimeStamp());

		File jenkinsConsoleFile = new File(
			testrayUploadBaseDir, "jenkins-console.txt");
		File jenkinsConsoleGzFile = new File(
			testrayUploadBaseDir, "jenkins-console.txt.gz");

		try {
			JenkinsConsoleTextLoader jenkinsConsoleTextLoader =
				JenkinsConsoleTextLoader.getInstance(getBuildURL());

			JenkinsResultsParserUtil.write(
				jenkinsConsoleFile, jenkinsConsoleTextLoader.getConsoleText());

			JenkinsResultsParserUtil.gzip(
				jenkinsConsoleFile, jenkinsConsoleGzFile);

			TestrayCloudBucket testrayCloudBucket =
				TestrayCloudBucket.getInstance();

			TestrayCloudObject testrayCloudObject =
				testrayCloudBucket.createTestrayCloudObject(
					JenkinsResultsParserUtil.combine(
						_getTestrayAttachmentBaseKey(), "/",
						jenkinsConsoleGzFile.getName()),
					jenkinsConsoleGzFile);

			addTestrayAttachmentURL(testrayCloudObject.getURL());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			JenkinsResultsParserUtil.delete(testrayUploadBaseDir);
		}
	}

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
	{
		new ModulesCompilationFailureMessageGenerator(),
		//
		new CompileFailureMessageGenerator(),
		new IntegrationTestTimeoutFailureMessageGenerator(),
		new JSUnitTestFailureMessageGenerator(),
		new PMDFailureMessageGenerator(),
		new PlaywrightCompilationFailureMessageGenerator(),
		new PluginGitIDFailureMessageGenerator(),
		new SemanticVersioningFailureMessageGenerator(),
		new ServiceBuilderFailureMessageGenerator(),
		new SourceFormatFailureMessageGenerator(),
		new StartupFailureMessageGenerator(),
		new UpgradeFailureMessageGenerator(),
		//
		new GradleTaskFailureMessageGenerator(),
		new LocalGitMirrorFailureMessageGenerator(),
		//
		new CIFailureMessageGenerator(),
		new GenericFailureMessageGenerator()
	};

	private DownstreamBuildReport _downstreamBuildReport;
	private Element _gitHubMessageElement;

}