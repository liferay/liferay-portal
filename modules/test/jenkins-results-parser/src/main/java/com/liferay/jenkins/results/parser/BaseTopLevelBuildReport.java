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
import java.util.HashMap;
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
	public void addDownstreamBuildReport(
		DownstreamBuildReport downstreamBuildReport) {

		if (downstreamBuildReport == null) {
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
	}

	@Override
	public Map<String, String> getBuildParameters() {
		Map<String, String> buildParameters = new HashMap<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if ((buildReportJSONObject == null) ||
			!buildReportJSONObject.has("buildParameters")) {

			return buildParameters;
		}

		JSONObject buildParametersJSONObject =
			buildReportJSONObject.getJSONObject("buildParameters");

		for (String key : buildParametersJSONObject.keySet()) {
			buildParameters.put(key, buildParametersJSONObject.getString(key));
		}

		return buildParameters;
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
	public ControllerBuildReport getControllerBuildReport() {
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
	public DownstreamBuildReport getDownstreamBuildReport(String axisName) {
		for (DownstreamBuildReport downstreamBuildReport :
				getDownstreamBuildReports()) {

			if (Objects.equals(downstreamBuildReport.getAxisName(), axisName)) {
				return downstreamBuildReport;
			}
		}

		return null;
	}

	@Override
	public List<DownstreamBuildReport> getDownstreamBuildReports() {
		return new ArrayList<>(_downstreamBuildReports);
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
	public JobReport getJobReport() {
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
	public String getTestrayBuildDateString() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles");
	}

	@Override
	public URL getTestResultsJSONUserContentURL() {
		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://test-1-0.liferay.com/userContent/testResults/",
					getJobName(), "/builds/", String.valueOf(getBuildNumber()),
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
	public long getTotalDuration() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0L;
		}

		return buildReportJSONObject.optLong("totalDuration");
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

	private ControllerBuildReport _controllerBuildReport;
	private final Set<DownstreamBuildReport> _downstreamBuildReports =
		new HashSet<>();
	private JobReport _jobReport;

}