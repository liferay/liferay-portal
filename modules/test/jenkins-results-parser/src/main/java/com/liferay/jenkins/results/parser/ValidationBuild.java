/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GradleTaskFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.RebaseFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SourceFormatFailureMessageGenerator;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.dom4j.Element;

import org.json.JSONObject;

/**
 * @author Leslie Wong
 */
public class ValidationBuild extends BaseBuild {

	@Override
	public void addTimelineData(TimelineData timelineData) {
		timelineData.addTimelineData(this);
	}

	@Override
	public URL getArtifactsBaseURL() {
		return null;
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
	public String getBuildName() {
		return "default";
	}

	@Override
	public Element getGitHubMessageElement() {
		if (_gitHubMessageElement != null) {
			return _gitHubMessageElement;
		}

		update();

		Element rootElement = Dom4JUtil.getNewElement(
			"html", null, getResultMessageElement(), getBuildTimeElement(),
			Dom4JUtil.getNewElement("h4", null, "Base Branch:"),
			getBaseBranchDetailsElement());

		String consoleText = getConsoleText();

		String[] consoleSnippets = consoleText.split(
			"Executing subrepository task ");

		if (consoleSnippets.length > 1) {
			Dom4JUtil.addToElement(
				rootElement,
				Dom4JUtil.getNewElement("h6", null, "Task Summary:"));

			Element taskSummaryListElement = Dom4JUtil.getNewElement(
				"ul", rootElement);

			List<String> junitTaskNames = new ArrayList<>();

			for (int i = 1; i < consoleSnippets.length; i++) {
				String consoleSnippet = consoleSnippets[i];

				if (consoleSnippet.contains("merge-test-results:")) {
					junitTaskNames.add(getTaskName(consoleSnippet));

					continue;
				}

				Dom4JUtil.addToElement(
					taskSummaryListElement,
					getTaskSummaryIndexElement(consoleSnippet));
			}

			if (!junitTaskNames.isEmpty()) {
				List<TestResult> testResults = getTestResults(null);

				String taskResult = "SUCCESSFUL";

				Element messageElement = null;

				for (TestResult testResult : testResults) {
					if (testResult.isFailing()) {
						taskResult = "FAILED";

						messageElement = Dom4JUtil.toCodeSnippetElement(
							"Test failures detected. See below for details.");

						break;
					}
				}

				Dom4JUtil.addToElement(
					taskSummaryListElement,
					getTaskSummaryIndexElement(
						StringUtils.join(junitTaskNames, "/"), taskResult,
						messageElement));
			}

			Dom4JUtil.addToElement(
				rootElement, getFullConsoleClickHereElement(),
				Dom4JUtil.getNewElement("hr"), getTestSummaryElement());
		}
		else {
			Dom4JUtil.addToElement(
				rootElement, getFailureMessageElement(),
				getFullConsoleClickHereElement());
		}

		_gitHubMessageElement = rootElement;

		return _gitHubMessageElement;
	}

	@Override
	public JSONObject getTestReportJSONObject(boolean checkCache) {
		String urlSuffix = "testReport/api/json";

		String archiveFileContent = getArchiveFileContent(urlSuffix);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			return new JSONObject(archiveFileContent);
		}

		try {
			return JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(getBuildURL() + urlSuffix),
				checkCache);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get test report JSON object", ioException);
		}
	}

	@Override
	public List<TestResult> getTestResults(String testStatus) {
		String status = getStatus();

		if (!status.equals("completed")) {
			return Collections.emptyList();
		}

		JSONObject testReportJSONObject = getTestReportJSONObject(false);

		if (testReportJSONObject.isEmpty()) {
			return new ArrayList<>();
		}

		return getTestResults(
			this, testReportJSONObject.getJSONArray("suites"), testStatus);
	}

	protected ValidationBuild(String url) {
		this(url, null);
	}

	protected ValidationBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	protected Element getBaseBranchDetailsElement() {
		String baseBranchURL = JenkinsResultsParserUtil.combine(
			"https://github.com/liferay/", getBaseGitRepositoryName(), "/tree/",
			getBranchName());

		String baseGitRepositoryName = getBaseGitRepositoryName();

		String baseGitRepositorySHA = null;

		if (!baseGitRepositoryName.equals("liferay-jenkins-ee") &&
			baseGitRepositoryName.endsWith("-ee")) {

			baseGitRepositorySHA = getBaseGitRepositorySHA(
				baseGitRepositoryName.substring(
					0, baseGitRepositoryName.length() - 3));
		}
		else {
			baseGitRepositorySHA = getBaseGitRepositorySHA(
				baseGitRepositoryName);
		}

		Element baseBranchDetailsElement = Dom4JUtil.getNewElement(
			"p", null, "Branch Name: ",
			Dom4JUtil.getNewAnchorElement(baseBranchURL, getBranchName()));

		if (baseGitRepositorySHA != null) {
			String baseGitRepositoryCommitURL =
				"https://github.com/liferay/" + baseGitRepositoryName +
					"/commit/" + baseGitRepositorySHA;

			Dom4JUtil.addToElement(
				baseBranchDetailsElement, Dom4JUtil.getNewElement("br"),
				"Branch GIT ID: ",
				Dom4JUtil.getNewAnchorElement(
					baseGitRepositoryCommitURL, baseGitRepositorySHA));
		}

		return baseBranchDetailsElement;
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	@Override
	protected Element getGitHubMessageJobResultsElement() {
		return null;
	}

	protected Element getGitHubMessageTestResultsElement() {
		int failCount = getTestCountByStatus("FAILURE");
		int successCount = getTestCountByStatus("SUCCESS");

		return Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"p", null, String.valueOf(successCount),
				JenkinsResultsParserUtil.getNounForm(
					successCount, " Tests", " Test"),
				" Passed.", Dom4JUtil.getNewElement("br"),
				String.valueOf(failCount),
				JenkinsResultsParserUtil.getNounForm(
					failCount, " Tests", " Test"),
				" Failed."));
	}

	protected Element getResultMessageElement() {
		Element resultMessageElement = Dom4JUtil.getNewElement("h1");

		String result = getResult();

		if (!result.equals("SUCCESS")) {
			resultMessageElement.addText("Validation FAILED.");
		}
		else {
			resultMessageElement.addText(
				"Validation PASSED. Running batch tests.");
		}

		return resultMessageElement;
	}

	protected String getTaskName(String console) {
		return console.substring(0, console.indexOf("\n"));
	}

	protected String getTaskResultIcon(String result) {
		if (result.equals("FAILED")) {
			return " :x:";
		}

		if (result.equals("SUCCESSFUL")) {
			return " :heavy_check_mark:";
		}

		return "";
	}

	protected Element getTaskSummaryIndexElement(String console) {
		String taskName = getTaskName(console);

		Matcher matcher = _consoleResultPattern.matcher(console);

		String taskResult = "FAILED";

		if (matcher.find()) {
			taskResult = matcher.group(1);
		}

		Element taskSummaryIndexFailureMessageElement = null;

		if (taskResult.equals("FAILED")) {
			taskSummaryIndexFailureMessageElement =
				getTaskSummaryIndexFailureMessageElement(console, taskName);
		}

		return getTaskSummaryIndexElement(
			taskName, taskResult, taskSummaryIndexFailureMessageElement);
	}

	protected Element getTaskSummaryIndexElement(
		String taskName, String taskResult, Element messageElement) {

		return Dom4JUtil.getNewElement(
			"li", null, taskName, " - ", getTaskResultIcon(taskResult),
			messageElement);
	}

	protected Element getTaskSummaryIndexFailureMessageElement(
		String console, String taskName) {

		Element messageElement = null;

		if (taskName.contains("subrepository-source-format")) {
			SourceFormatFailureMessageGenerator
				sourceFormatFailureMessageGenerator =
					new SourceFormatFailureMessageGenerator();

			messageElement =
				sourceFormatFailureMessageGenerator.getMessageElement(console);

			if (messageElement != null) {
				return messageElement;
			}
		}

		GradleTaskFailureMessageGenerator gradleTaskFailureMessageGenerator =
			new GradleTaskFailureMessageGenerator();

		messageElement = gradleTaskFailureMessageGenerator.getMessageElement(
			console);

		if (messageElement != null) {
			return messageElement;
		}

		GenericFailureMessageGenerator genericFailureMessageGenerator =
			new GenericFailureMessageGenerator();

		return genericFailureMessageGenerator.getMessageElement(console);
	}

	protected Element getTestSummaryElement() {
		Element testSummaryElement = Dom4JUtil.getNewElement(
			"div", null, Dom4JUtil.getNewElement("h6", null, "Test Results:"));

		List<TestResult> testResults = getTestResults(null);

		boolean noTestResults = testResults.isEmpty();

		if (testResults.size() == 1) {
			TestResult testResult = testResults.get(0);

			String className = testResult.getClassName();

			noTestResults = className.equals("com.liferay.jenkins.Jenkins");
		}

		if (noTestResults) {
			Dom4JUtil.addToElement(
				testSummaryElement,
				Dom4JUtil.getNewElement("h5", null, "No tests were run."));
		}
		else {
			Dom4JUtil.addToElement(
				testSummaryElement,
				Dom4JUtil.getNewElement(
					"div", null, getGitHubMessageTestResultsElement()));

			List<Element> failureElements = new ArrayList<>();

			for (TestResult testResult : getTestResults(null)) {
				if (testResult.isFailing()) {
					failureElements.add(testResult.getGitHubElement());
				}
			}

			if (!failureElements.isEmpty()) {
				Dom4JUtil.getOrderedListElement(
					failureElements, testSummaryElement, 5);
			}

			Element testReportElement = Dom4JUtil.getNewElement(
				"h5", null, "For all test results, click ",
				Dom4JUtil.getNewAnchorElement(
					getBuildURL() + "/testReport", "here"),
				".");

			Dom4JUtil.addToElement(testSummaryElement, testReportElement);
		}

		return testSummaryElement;
	}

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
		{
			new FormatFailureMessageGenerator(),
			new RebaseFailureMessageGenerator(),
			new SourceFormatFailureMessageGenerator(),
			//
			new GenericFailureMessageGenerator()
		};

	private static final Pattern _consoleResultPattern = Pattern.compile(
		"Subrepository task (FAILED|SUCCESSFUL)");

	private Element _gitHubMessageElement;

}