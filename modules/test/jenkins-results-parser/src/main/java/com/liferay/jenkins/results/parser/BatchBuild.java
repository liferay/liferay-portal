/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.ClosedChannelExceptionFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kevin Yen
 */
public class BatchBuild extends BaseParentBuild {

	@Override
	public void addTimelineData(TimelineData timelineData) {
		addDownstreamBuildsTimelineData(timelineData);
	}

	@Override
	public URL getArtifactsBaseURL() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		StringBuilder sb = new StringBuilder();

		sb.append(topLevelBuild.getArtifactsBaseURL());
		sb.append("/");
		sb.append(getParameterValue("JOB_VARIANT"));

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	public String getBatchName() {
		return batchName;
	}

	@Override
	public String getBuildName() {
		String buildName = getJobVariant();

		if (JenkinsResultsParserUtil.isNullOrEmpty(buildName)) {
			buildName = getJobName();
		}

		return buildName;
	}

	public List<AxisBuild> getDownstreamAxisBuilds() {
		List<AxisBuild> downstreamAxisBuilds = new ArrayList<>();

		List<Build> downstreamBuilds = getDownstreamBuilds(null);

		for (Build downstreamBuild : downstreamBuilds) {
			if (!(downstreamBuild instanceof AxisBuild)) {
				continue;
			}

			downstreamAxisBuilds.add((AxisBuild)downstreamBuild);
		}

		Collections.sort(
			downstreamAxisBuilds, new BaseBuild.BuildDisplayNameComparator());

		return downstreamAxisBuilds;
	}

	@Override
	public Element getGitHubMessageElement() {
		sortDownstreamBuilds();

		Element messageElement = super.getGitHubMessageElement();

		if (messageElement == null) {
			return null;
		}

		String result = getResult();

		if (result.equals("ABORTED") && (getDownstreamBuildCount(null) == 0)) {
			_gitHubMessageElement = messageElement;

			return _gitHubMessageElement;
		}

		List<Build> failedDownstreamBuilds = getFailedDownstreamBuilds();

		List<Element> downstreamBuildMessageElements =
			getDownstreamBuildMessageElements(failedDownstreamBuilds);

		if (result.equals("FAILURE") &&
			downstreamBuildMessageElements.isEmpty()) {

			_gitHubMessageElement = messageElement;

			return _gitHubMessageElement;
		}

		List<Element> failureElements = new ArrayList<>();
		List<Element> upstreamJobFailureElements = new ArrayList<>();

		for (Build failedDownstreamBuild : failedDownstreamBuilds) {
			Element gitHubMessageElement =
				failedDownstreamBuild.getGitHubMessageElement();

			if (gitHubMessageElement == null) {
				continue;
			}

			if (failedDownstreamBuild.isUniqueFailure()) {
				failureElements.add(gitHubMessageElement);
			}
			else {
				upstreamJobFailureElements.add(gitHubMessageElement);
			}
		}

		if (!upstreamJobFailureElements.isEmpty()) {
			Dom4JUtil.getOrderedListElement(
				upstreamJobFailureElements, getGitHubMessageElement(true), 4);
		}

		Dom4JUtil.getOrderedListElement(failureElements, messageElement, 4);

		if (failureElements.size() >= 4) {
			Dom4JUtil.getNewElement(
				"strong", messageElement, "Click ",
				Dom4JUtil.getNewAnchorElement(
					getBuildURL() + "testReport", "here"),
				" for more failures.");
		}

		if (failureElements.isEmpty()) {
			return null;
		}

		_gitHubMessageElement = messageElement;

		return _gitHubMessageElement;
	}

	@Override
	public Long getInvokedTime() {
		if (invokedTime != null) {
			return invokedTime;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("\\s*\\[echo\\]\\s*");

		sb.append(Pattern.quote(getJobName()));

		String jobVariant = getJobVariant();

		if ((jobVariant != null) && !jobVariant.isEmpty()) {
			sb.append("/");

			sb.append(Pattern.quote(jobVariant));
		}

		sb.append("\\s*invoked time: (?<invokedTime>[^\\n]*)");

		Pattern pattern = Pattern.compile(sb.toString());

		Build parentBuild = getParentBuild();

		String parentConsoleText = parentBuild.getConsoleText();

		for (String line : parentConsoleText.split("\n")) {
			Matcher matcher = pattern.matcher(line);

			if (!matcher.find()) {
				continue;
			}

			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to get build properties", ioException);
			}

			SimpleDateFormat sdf = new SimpleDateFormat(
				buildProperties.getProperty("jenkins.report.date.format"));

			Date date = null;

			try {
				date = sdf.parse(matcher.group("invokedTime"));
			}
			catch (ParseException parseException) {
				throw new RuntimeException(
					"Unable to get invoked time", parseException);
			}

			invokedTime = date.getTime();

			return invokedTime;
		}

		return getStartTime();
	}

	@Override
	public Map<String, String> getMetricLabels() {
		Map<String, String> metricLabels = super.getMetricLabels();

		metricLabels.put("job_type", batchName);

		return metricLabels;
	}

	@Override
	public synchronized List<TestClassResult> getTestClassResults() {
		List<TestClassResult> testClassResults = new ArrayList<>();

		for (AxisBuild axisBuild : getDownstreamAxisBuilds()) {
			testClassResults.addAll(axisBuild.getTestClassResults());
		}

		return testClassResults;
	}

	@Override
	public synchronized List<TestResult> getTestResults() {
		List<TestResult> testResults = new ArrayList<>();

		for (AxisBuild axisBuild : getDownstreamAxisBuilds()) {
			testResults.addAll(axisBuild.getTestResults());
		}

		return testResults;
	}

	@Override
	public long getTotalDuration() {
		long totalDuration = super.getTotalDuration();

		return totalDuration - getDuration();
	}

	@Override
	public int getTotalSlavesUsedCount() {
		return getTotalSlavesUsedCount(null, false);
	}

	@Override
	public int getTotalSlavesUsedCount(
		String status, boolean modifiedBuildsOnly) {

		return getTotalSlavesUsedCount(status, modifiedBuildsOnly, true);
	}

	@Override
	public void saveBuildURLInBuildDatabase() {
		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.putProperty(
			BUILD_URLS_PROPERTIES_KEY, getBatchName(), getBuildURL(), false);
	}

	protected BatchBuild(String url) {
		this(url, null);
	}

	protected BatchBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);

		String jobVariant = getJobVariant();

		if ((jobVariant != null) && !jobVariant.isEmpty()) {
			Matcher matcher = _jobVariantPattern.matcher(jobVariant);

			if (!matcher.matches()) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to find batch name of batch build from ",
						"job variant '", jobVariant,
						"'. Job variant must match pattern '",
						_jobVariantPattern.pattern(), "'."));
			}

			batchName = matcher.group("batchName");
		}
		else {
			batchName = null;
		}
	}

	@Override
	protected void findDownstreamBuilds() {
		List<String> downstreamBuildURLs = new ArrayList<>();

		JSONObject buildJSONObject = getBuildJSONObject("runs[number,url]");

		if ((buildJSONObject != null) && buildJSONObject.has("runs")) {
			JSONArray runsJSONArray = buildJSONObject.getJSONArray("runs");

			if (runsJSONArray != null) {
				for (int i = 0; i < runsJSONArray.length(); i++) {
					JSONObject runJSONObject = runsJSONArray.getJSONObject(i);

					if (runJSONObject.getInt("number") != getBuildNumber()) {
						continue;
					}

					String url = runJSONObject.getString("url");

					if (hasBuildURL(url) || downstreamBuildURLs.contains(url)) {
						continue;
					}

					downstreamBuildURLs.add(url);
				}
			}
		}

		addDownstreamBuilds(downstreamBuildURLs.toArray(new String[0]));
	}

	protected AxisBuild getAxisBuild(String axisVariable) {
		for (AxisBuild downstreamAxisBuild : getDownstreamAxisBuilds()) {
			if (axisVariable.equals(downstreamAxisBuild.getAxisVariable())) {
				return downstreamAxisBuild;
			}
		}

		return null;
	}

	@Override
	protected ExecutorService getExecutorService() {
		return _executorService;
	}

	@Override
	protected Element getFailureMessageElement() {
		for (FailureMessageGenerator failureMessageGenerator :
				getFailureMessageGenerators()) {

			Element failureMessage = failureMessageGenerator.getMessageElement(
				this);

			if (failureMessage != null) {
				return failureMessage;
			}
		}

		return null;
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	@Override
	protected Element getGitHubMessageJobResultsElement() {
		return getGitHubMessageJobResultsElement(false);
	}

	@Override
	protected Element getGitHubMessageJobResultsElement(
		boolean showCommonFailuresCount) {

		String result = getResult();

		int failCount = getDownstreamBuildCountByResult("FAILURE");
		int successCount = getDownstreamBuildCountByResult("SUCCESS");

		if (result.equals("UNSTABLE")) {
			failCount = getTestCountByStatus("FAILURE");
			successCount = getTestCountByStatus("SUCCESS");

			if (isCompareToUpstream()) {
				List<TestResult> upstreamJobFailureTestResults =
					getUpstreamJobFailureTestResults();

				int upstreamFailCount = upstreamJobFailureTestResults.size();

				if (showCommonFailuresCount) {
					failCount = upstreamFailCount;
				}
				else {
					failCount = failCount - upstreamFailCount;
				}
			}
		}

		return Dom4JUtil.getNewElement(
			"div", null, Dom4JUtil.getNewElement("h6", null, "Job Results:"),
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

	@Override
	protected String getJenkinsReportBuildInfoCellElementTagName() {
		return "th";
	}

	@Override
	protected List<Element> getJenkinsReportTableRowElements(
		String result, String status) {

		List<Element> tableRowElements = new ArrayList<>();

		tableRowElements.add(getJenkinsReportTableRowElement());

		for (AxisBuild downstreamAxisBuild : getDownstreamAxisBuilds()) {
			tableRowElements.addAll(
				downstreamAxisBuild.getJenkinsReportTableRowElements(
					downstreamAxisBuild.getResult(),
					downstreamAxisBuild.getStatus()));
		}

		return tableRowElements;
	}

	@Override
	protected int getTestCountByStatus(String status) {
		JSONObject testReportJSONObject = getTestReportJSONObject(false);

		int failCount = testReportJSONObject.optInt("failCount");

		if (status.equals("SUCCESS")) {
			int totalCount = testReportJSONObject.optInt("totalCount");
			int skipCount = testReportJSONObject.optInt("skipCount");

			return totalCount - skipCount - failCount;
		}

		if (status.equals("FAILURE")) {
			return failCount;
		}

		throw new IllegalArgumentException("Invalid status: " + status);
	}

	protected final String batchName;

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
		{new ClosedChannelExceptionFailureMessageGenerator()};

	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(10, true);
	private static final Pattern _jobVariantPattern = Pattern.compile(
		"(?<batchName>[^/]+)(/.*)?");

	private Element _gitHubMessageElement;

}