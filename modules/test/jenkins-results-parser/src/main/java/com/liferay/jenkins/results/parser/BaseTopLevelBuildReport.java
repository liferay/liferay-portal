/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayCloudBucket;
import com.liferay.jenkins.results.parser.testray.TestrayCloudObject;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTopLevelBuildReport
	extends BaseBuildReport implements TopLevelBuildReport {

	@Override
	public synchronized void addDownstreamBuildReport(
		DownstreamBuildReport downstreamBuildReport) {

		if (downstreamBuildReport == null) {
			return;
		}

		if (downstreamBuildReport.isBuildCached()) {
			_cachedDownstreamBuildReports.add(downstreamBuildReport);

			return;
		}

		_downstreamBuildReports.add(downstreamBuildReport);
	}

	@Override
	public void addDownstreamBuildReports(
		List<DownstreamBuildReport> downstreamBuildReports) {

		if (downstreamBuildReports == null) {
			return;
		}

		for (DownstreamBuildReport downstreamBuildReport :
				downstreamBuildReports) {

			addDownstreamBuildReport(downstreamBuildReport);
		}
	}

	@Override
	public void addTestrayAttachmentURL(URL testrayAttachmentURL) {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return;
		}

		JSONArray jsonArray = buildReportJSONObject.optJSONArray(
			"testrayAttachmentURLs");

		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}

		jsonArray.put(String.valueOf(testrayAttachmentURL));

		buildReportJSONObject.put("testrayAttachmentURLs", jsonArray);

		clearTestrayAttachmentURLCaches();
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		Map<String, String> buildParameters = getBuildParameters();

		String buildProfileString = buildParameters.get(
			"TEST_PORTAL_BUILD_PROFILE");

		Job.BuildProfile buildProfile = Job.BuildProfile.getByString(
			buildProfileString);

		if (buildProfile != null) {
			return buildProfile;
		}

		return Job.BuildProfile.DXP;
	}

	@Override
	public URL getBuildReportJSONTestrayURL() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://storage.cloud.google.com/testray-results/",
					getStartYearMonth(), "/", jenkinsMaster.getName(), "/",
					getJobName(), "/", String.valueOf(getBuildNumber()),
					"/build-report.json.gz"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public URL getBuildReportJSONUserContentURL() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://", jenkinsMaster.getName(),
					".liferay.com/userContent/jobs/", getJobName(), "/builds/",
					String.valueOf(getBuildNumber()), "/build-report.json.gz"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public TestrayCloudObject getBuildReportTestrayCloudObject() {
		if (!TestrayCloudBucket.hasGoogleApplicationCredentials()) {
			return null;
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		TestrayCloudBucket testrayCloudBucket =
			TestrayCloudBucket.getInstance();

		return testrayCloudBucket.getTestrayCloudObject(
			JenkinsResultsParserUtil.combine(
				getStartYearMonth(), "/", jenkinsMaster.getName(), "/",
				getJobName(), "/", String.valueOf(getBuildNumber()),
				"/build-report.json.gz"));
	}

	@Override
	public synchronized ControllerBuildReport getControllerBuildReport() {
		if (_controllerBuildReport != null) {
			return _controllerBuildReport;
		}

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		JSONObject controllerJSONObject = buildReportJSONObject.optJSONObject(
			"controller");

		if ((controllerJSONObject == null) ||
			!controllerJSONObject.has("buildURL")) {

			return null;
		}

		_controllerBuildReport = BuildReportFactory.newControllerBuildReport(
			controllerJSONObject, this);

		return _controllerBuildReport;
	}

	@Override
	public synchronized List<FailureReport> getDistinctFailureReports() {
		if (_distinctFailureReports != null) {
			return _distinctFailureReports;
		}

		List<FailureReport> distinctFailureReports = new ArrayList<>();

		for (FailureReport failureReport : getFailureReports()) {
			boolean hasSimilarFailureReport = false;

			for (FailureReport distinctFailureReport : distinctFailureReports) {
				if (failureReport.isSimilar(distinctFailureReport)) {
					hasSimilarFailureReport = true;

					break;
				}
			}

			if (!hasSimilarFailureReport) {
				distinctFailureReports.add(failureReport);
			}
		}

		_distinctFailureReports = distinctFailureReports;

		return _distinctFailureReports;
	}

	@Override
	public synchronized DownstreamBuildReport getDownstreamBuildReport(
		String axisName) {

		for (DownstreamBuildReport downstreamBuildReport :
				_downstreamBuildReports) {

			if (Objects.equals(downstreamBuildReport.getAxisName(), axisName)) {
				return downstreamBuildReport;
			}
		}

		for (DownstreamBuildReport cachedDownstreamBuildReport :
				_cachedDownstreamBuildReports) {

			if (Objects.equals(
					cachedDownstreamBuildReport.getAxisName(), axisName)) {

				return cachedDownstreamBuildReport;
			}
		}

		return null;
	}

	@Override
	public synchronized List<DownstreamBuildReport>
		getDownstreamBuildReports() {

		List<DownstreamBuildReport> downstreamBuildReports = new ArrayList<>();

		downstreamBuildReports.addAll(_cachedDownstreamBuildReports);
		downstreamBuildReports.addAll(_downstreamBuildReports);

		return downstreamBuildReports;
	}

	@Override
	public synchronized List<FailureReport> getFailureReports() {
		if (_failureReports != null) {
			return _failureReports;
		}

		List<FailureReport> failureReports = new ArrayList<>(
			super.getFailureReports());

		for (DownstreamBuildReport downstreamBuildReport :
				getDownstreamBuildReports()) {

			failureReports.addAll(downstreamBuildReport.getFailureReports());
		}

		_failureReports = failureReports;

		return _failureReports;
	}

	@Override
	public URL getJenkinsReportURL() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://", jenkinsMaster.getName(), ".liferay.com/",
					"userContent/jobs/", getJobName(), "/builds/",
					String.valueOf(getBuildNumber()), "/jenkins-report.html"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(
				"Unable to get Jenkins report URL", malformedURLException);
		}
	}

	@Override
	public synchronized JobReport getJobReport() {
		if (_jobReport != null) {
			return _jobReport;
		}

		Matcher matcher = _buildURLPattern.matcher(
			String.valueOf(getBuildURL()));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL: " + getBuildURL());
		}

		try {
			_jobReport = JobReport.getInstance(
				new URL(matcher.group("jobURL")));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

		return _jobReport;
	}

	@Override
	public synchronized TopLevelBuildReport getPreviousTopLevelBuildReport() {
		if (_previousTopLevelBuildReport != null) {
			return _previousTopLevelBuildReport;
		}

		ControllerBuildReport controllerBuildReport =
			getControllerBuildReport();

		if (controllerBuildReport == null) {
			return null;
		}

		int currentBuildNumber = controllerBuildReport.getBuildNumber();

		if (currentBuildNumber <= 1) {
			return null;
		}

		JenkinsMaster controllerJenkinsMaster =
			controllerBuildReport.getJenkinsMaster();

		String controllerJobURL = JenkinsResultsParserUtil.combine(
			controllerJenkinsMaster.getRemoteURL(), "job/",
			controllerBuildReport.getJobName());

		JSONObject controllerJobJSONObject = JenkinsAPIUtil.getAPIJSONObject(
			controllerJobURL, "builds[description,number]");

		if (controllerJobJSONObject == null) {
			return null;
		}

		JSONArray buildsJSONArray = controllerJobJSONObject.optJSONArray(
			"builds");

		if (buildsJSONArray == null) {
			return null;
		}

		boolean foundCurrentBuild = false;

		String previousTopLevelBuildURL = null;

		for (int i = 0; i < buildsJSONArray.length(); i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			int buildNumber = buildJSONObject.optInt("number", -1);

			if (buildNumber == currentBuildNumber) {
				foundCurrentBuild = true;

				continue;
			}

			if (!foundCurrentBuild) {
				continue;
			}

			Matcher matcher = _controllerBuildDescriptionPattern.matcher(
				buildJSONObject.optString("description"));

			if (!matcher.find()) {
				continue;
			}

			String status = matcher.group("status");

			if (!Objects.equals(status, "FAILURE") &&
				!Objects.equals(status, "SUCCESS") &&
				!Objects.equals(status, "UNSTABLE")) {

				continue;
			}

			previousTopLevelBuildURL = matcher.group("buildURL");

			break;
		}

		if (!JenkinsResultsParserUtil.isURL(previousTopLevelBuildURL)) {
			return null;
		}

		try {
			_previousTopLevelBuildReport =
				BuildReportFactory.newTopLevelBuildReport(
					new URL(previousTopLevelBuildURL));

			return _previousTopLevelBuildReport;
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	@Override
	public URL getTestResultsJSONUserContentURL() {
		try {
			String masterHostname = JenkinsResultsParserUtil.getBuildProperty(
				"jenkins.remote.url[test-1-0]");

			if (JenkinsResultsParserUtil.isNullOrEmpty(masterHostname)) {
				masterHostname = "https://test-1-0.liferay.com/";
			}

			if (!masterHostname.endsWith("/")) {
				masterHostname += "/";
			}

			return new URL(
				JenkinsResultsParserUtil.combine(
					masterHostname, "userContent/testResults/", getJobName(),
					"/builds/", String.valueOf(getBuildNumber()),
					"/test.results.json"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getTestSuiteName() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return buildReportJSONObject.optString("testSuiteName");
	}

	@Override
	public String getTestrayBuildDateString() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles");
	}

	@Override
	public long getTopLevelActiveDuration() {
		long topLevelPassiveBuildDuration = getTopLevelPassiveDuration();

		if (topLevelPassiveBuildDuration == 0L) {
			return 0L;
		}

		return getDuration() - topLevelPassiveBuildDuration;
	}

	@Override
	public long getTopLevelPassiveDuration() {
		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		if (stopWatchRecordsGroup == null) {
			return 0L;
		}

		StopWatchRecord waitForInvokedJobsStopWatchRecord =
			stopWatchRecordsGroup.get("wait.for.invoked.jobs");
		StopWatchRecord waitForInvokedSmokeJobsStopWatchRecord =
			stopWatchRecordsGroup.get("wait.for.invoked.smoke.jobs");

		if ((waitForInvokedJobsStopWatchRecord != null) ||
			(waitForInvokedSmokeJobsStopWatchRecord != null)) {

			long topLevelPassiveBuildDuration = 0L;

			if (waitForInvokedJobsStopWatchRecord != null) {
				topLevelPassiveBuildDuration +=
					waitForInvokedJobsStopWatchRecord.getDuration();
			}

			if (waitForInvokedSmokeJobsStopWatchRecord != null) {
				topLevelPassiveBuildDuration +=
					waitForInvokedSmokeJobsStopWatchRecord.getDuration();
			}

			return topLevelPassiveBuildDuration;
		}

		StopWatchRecord invokeDownstreamBuildsStopWatchRecord =
			stopWatchRecordsGroup.get("invoke.downstream.builds");

		if (invokeDownstreamBuildsStopWatchRecord != null) {
			return invokeDownstreamBuildsStopWatchRecord.getDuration();
		}

		return 0L;
	}

	@Override
	public long getTotalActualDuration() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0L;
		}

		return buildReportJSONObject.optLong("totalActualDuration");
	}

	@Override
	public long getTotalCachedDuration() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0L;
		}

		return buildReportJSONObject.optLong("totalCachedDuration");
	}

	@Override
	public long getTotalDuration() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0L;
		}

		return buildReportJSONObject.optLong("totalDuration");
	}

	@Override
	public synchronized List<FailureReport> getUniqueFailureReports() {
		if (_uniqueFailureReports != null) {
			return _uniqueFailureReports;
		}

		TopLevelBuildReport previousTopLevelBuildReport =
			getPreviousTopLevelBuildReport();

		if (previousTopLevelBuildReport == null) {
			_uniqueFailureReports = new ArrayList<>(
				getDistinctFailureReports());

			return _uniqueFailureReports;
		}

		List<FailureReport> uniqueFailureReports = new ArrayList<>();

		List<FailureReport> previousFailureReports =
			previousTopLevelBuildReport.getDistinctFailureReports();

		for (FailureReport failureReport : getDistinctFailureReports()) {
			boolean hasSimilarFailure = false;

			for (FailureReport previousFailureReport : previousFailureReports) {
				if (failureReport.isSimilar(previousFailureReport)) {
					hasSimilarFailure = true;

					break;
				}
			}

			if (!hasSimilarFailure) {
				uniqueFailureReports.add(failureReport);
			}
		}

		_uniqueFailureReports = uniqueFailureReports;

		return _uniqueFailureReports;
	}

	public void setControllerBuildReport(
		ControllerBuildReport controllerBuildReport) {

		_controllerBuildReport = controllerBuildReport;
	}

	protected BaseTopLevelBuildReport(String buildURLString) {
		super(buildURLString);
	}

	protected BaseTopLevelBuildReport(
		String buildURLString, JobReport jobReport) {

		super(buildURLString);

		_jobReport = jobReport;
	}

	protected String getStartYearMonth() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM", "America/Los_Angeles");
	}

	protected void initialize(JSONObject buildReportJSONObject) {
		JSONArray batchesJSONArray = buildReportJSONObject.optJSONArray(
			"batches");

		if (batchesJSONArray != null) {
			for (int i = 0; i < batchesJSONArray.length(); i++) {
				JSONObject batchJSONObject = batchesJSONArray.getJSONObject(i);

				String batchName = batchJSONObject.optString("batchName");
				JSONArray buildsJSONArray = batchJSONObject.optJSONArray(
					"builds");

				if (JenkinsResultsParserUtil.isNullOrEmpty(batchName) ||
					(buildsJSONArray == null)) {

					continue;
				}

				for (int j = 0; j < buildsJSONArray.length(); j++) {
					addDownstreamBuildReport(
						BuildReportFactory.newDownstreamBuildReport(
							batchName, buildsJSONArray.getJSONObject(j), this));
				}
			}
		}

		JSONObject controllerJSONObject = buildReportJSONObject.optJSONObject(
			"controller");

		if (controllerJSONObject != null) {
			setControllerBuildReport(
				BuildReportFactory.newControllerBuildReport(
					controllerJSONObject, this));
		}
	}

	private static final Pattern _buildURLPattern = Pattern.compile(
		"(?<jobURL>https?://(?<masterHostname>test-\\d+-\\d+)" +
			"(\\.liferay\\.com)?/job/(?<jobName>[^/]+))" +
				"(/AXIS_VARIABLE=(?<axisVariable>\\d+))?/(?<buildNumber>\\d+)");
	private static final Pattern _controllerBuildDescriptionPattern =
		Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"<strong[^>]*>(?<status>[A-Z]+)<\\/strong> - <a href=\\\"",
				"(?<buildURL>[^\\\"]+)\\\">Build URL<\\/a>.*"));

	private final Set<DownstreamBuildReport> _cachedDownstreamBuildReports =
		new HashSet<>();
	private ControllerBuildReport _controllerBuildReport;
	private List<FailureReport> _distinctFailureReports;
	private final Set<DownstreamBuildReport> _downstreamBuildReports =
		new HashSet<>();
	private List<FailureReport> _failureReports;
	private JobReport _jobReport;
	private TopLevelBuildReport _previousTopLevelBuildReport;
	private List<FailureReport> _uniqueFailureReports;

}