/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class GenerateReportsControllerBuildRunner
	extends BaseBuildRunner<BuildData> {

	@Override
	public Workspace getWorkspace() {
		if (_workspace != null) {
			return _workspace;
		}

		_workspace = WorkspaceFactory.newWorkspace();

		return _workspace;
	}

	@Override
	public void run() {
		invokeGenerateReportsBuilds();
	}

	@Override
	public void tearDown() {
	}

	protected GenerateReportsControllerBuildRunner(BuildData buildData) {
		super(buildData);
	}

	protected void invokeGenerateReportsBuilds() {
		List<String> reportNames = _getSelectedReportNames();

		if (reportNames.isEmpty()) {
			System.out.println("There are no reports to create at this time.");

			keepJenkinsBuild(false);

			return;
		}

		BuildData buildData = getBuildData();

		String jenkinsGitHubURL = buildData.getJenkinsGitHubURL();

		List<String> groupedReportNames = new ArrayList<>();

		for (String reportName : reportNames) {
			if (reportName.startsWith("Flaky Test")) {
				_invoke(jenkinsGitHubURL, reportName, "slave");

				continue;
			}

			if (reportName.equals("Spot Interruption")) {
				_invoke(jenkinsGitHubURL, reportName, "slave-pco");

				continue;
			}

			groupedReportNames.add(reportName);
		}

		if (!groupedReportNames.isEmpty()) {
			_invoke(
				jenkinsGitHubURL, String.join(",", groupedReportNames), null);
		}

		_updateBuildDescription(reportNames);
	}

	private Map<String, JSONObject> _getBuildJSONObjectsMap() {
		Map<String, JSONObject> buildJSONObjectsMap = new HashMap<>();

		BuildData buildData = getBuildData();

		StringBuilder sb = new StringBuilder();

		sb.append(JenkinsResultsParserUtil.getLocalURL(buildData.getJobURL()));
		sb.append("/api/json?tree=builds[description,timestamp,url]");

		try {
			JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
				sb.toString(), false);

			JSONArray buildsJSONArray = jsonObject.getJSONArray("builds");

			for (int i = 0; i < buildsJSONArray.length(); i++) {
				JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

				buildJSONObjectsMap.put(
					buildJSONObject.getString("url"), buildJSONObject);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException("Unable to get job JSON", ioException);
		}

		return buildJSONObjectsMap;
	}

	private Map<String, Long> _getLatestReportInvocationTimes() {
		Map<String, Long> latestReportInvocationTimes = new HashMap<>();

		Map<String, JSONObject> buildJSONObjectsMap = _getBuildJSONObjectsMap();

		BuildData buildData = getBuildData();

		buildJSONObjectsMap.remove(buildData.getBuildURL());

		for (GenerateReportsBuildRunner.Report report :
				GenerateReportsBuildRunner.Report.values()) {

			String reportName = report.toString();

			for (JSONObject buildJSONObject : buildJSONObjectsMap.values()) {
				String buildDescription = buildJSONObject.optString(
					"description", "");

				if (!buildDescription.contains(reportName)) {
					continue;
				}

				long timestamp = buildJSONObject.getLong("timestamp");

				Long latestReportInvocationTime =
					latestReportInvocationTimes.get(reportName);

				if ((latestReportInvocationTime != null) &&
					(latestReportInvocationTime >= timestamp)) {

					continue;
				}

				latestReportInvocationTimes.put(reportName, timestamp);
			}
		}

		return latestReportInvocationTimes;
	}

	private long _getReportGeneratedTime(
		GenerateReportsBuildRunner.Report report) {

		File dataFile = report.getDataFile();

		if (dataFile == null) {
			return 0;
		}

		try {
			return JenkinsResultsParserUtil.getGeneratedTimestamp(
				JenkinsResultsParserUtil.read(dataFile));
		}
		catch (IOException ioException) {
			System.out.println("Unable to read " + dataFile);

			return 0;
		}
	}

	private long _getReportStaleDuration(String reportName) {
		Properties buildProperties;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String reportStaleDuration = buildProperties.getProperty(
			JenkinsResultsParserUtil.combine(
				"report.stale.duration[", reportName, "]"));

		if (reportStaleDuration == null) {
			return _DEFAULT_REPORT_STALE_DURATION;
		}

		return TimeUnit.MINUTES.toMillis(Long.parseLong(reportStaleDuration));
	}

	private List<String> _getSelectedReportNames() {
		if (_selectedReportNames != null) {
			return _selectedReportNames;
		}

		_selectedReportNames = new ArrayList<>();

		Map<String, Long> latestReportInvocationTimes =
			_getLatestReportInvocationTimes();

		BuildData buildData = getBuildData();

		long startTime = buildData.getStartTime();

		for (GenerateReportsBuildRunner.Report report :
				GenerateReportsBuildRunner.Report.values()) {

			String reportName = report.toString();

			Long latestReportInvocationTime = latestReportInvocationTimes.get(
				reportName);

			long reportGeneratedTime = _getReportGeneratedTime(report);

			long reportStaleDuration = _getReportStaleDuration(reportName);

			if (!_isReportStale(
					startTime, latestReportInvocationTime, reportGeneratedTime,
					reportStaleDuration)) {

				continue;
			}

			if (reportGeneratedTime <= 0) {
				System.out.println(reportName + " has no generated date");
			}
			else {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						reportName, " was last generated ",
						JenkinsResultsParserUtil.toDurationString(
							startTime - reportGeneratedTime),
						" ago which exceeds the stale duration of ",
						JenkinsResultsParserUtil.toDurationString(
							reportStaleDuration)));
			}

			_selectedReportNames.add(reportName);
		}

		Collections.sort(_selectedReportNames);

		return _selectedReportNames;
	}

	private void _invoke(Map<String, String> invocationParameters) {
		BuildData buildData = getBuildData();

		invocationParameters.put("PARENT_BUILD_URL", buildData.getBuildURL());

		String jobURL = JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getMostAvailableMasterURL(
				"http://" + buildData.getCohortName() + ".liferay.com", null, 1,
				"generate-reports"),
			"/job/generate-reports");

		try {
			long queueId = JenkinsResultsParserUtil.invokeJenkinsBuild(
				jobURL, invocationParameters);

			if (queueId == 0) {
				throw new RuntimeException("Unable to invoke " + jobURL);
			}

			System.out.println(
				"The " + invocationParameters.get("REPORT_NAMES") +
					" report(s) will be generated at: " + jobURL);
		}
		catch (Exception exception) {
			System.out.println(
				"Unable to invoke a new build to generate reports");

			exception.printStackTrace();
		}
	}

	private void _invoke(
		String jenkinsGitHubURL, String reportNames, String slaveLabel) {

		Map<String, String> invocationParameters = new HashMap<>();

		invocationParameters.put("JENKINS_GITHUB_URL", jenkinsGitHubURL);
		invocationParameters.put("REPORT_NAMES", reportNames);
		invocationParameters.put("SLAVE_LABEL", slaveLabel);

		_invoke(invocationParameters);
	}

	private boolean _isReportStale(
		long currentTime, Long latestReportInvocationTime,
		long reportGeneratedTime, long reportStaleDuration) {

		long reportStaleTime = reportGeneratedTime + reportStaleDuration;

		if (currentTime < reportStaleTime) {
			return false;
		}

		if ((latestReportInvocationTime == null) ||
			(latestReportInvocationTime < reportStaleTime)) {

			return true;
		}

		long reportRetryDuration = Math.min(
			reportStaleDuration,
			Math.max(
				_REPORT_RETRY_DURATION_MINIMUM,
				latestReportInvocationTime - reportStaleTime));

		if (currentTime < (latestReportInvocationTime + reportRetryDuration)) {
			return false;
		}

		return true;
	}

	private void _updateBuildDescription(List<String> reportNames) {
		StringBuilder sb = new StringBuilder();

		for (String reportName : reportNames) {
			sb.append(reportName);

			sb.append(" - ");
		}

		sb.setLength(sb.length() - 3);

		BuildData buildData = getBuildData();

		buildData.setBuildDescription(sb.toString());

		updateBuildDescription();
	}

	private static final long _DEFAULT_REPORT_STALE_DURATION = 1440 * 60 * 1000;

	private static final long _REPORT_RETRY_DURATION_MINIMUM = 120 * 60 * 1000;

	private List<String> _selectedReportNames;
	private Workspace _workspace;

}