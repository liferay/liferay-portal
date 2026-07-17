/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

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
				Map<String, String> invocationParameters = new HashMap<>();

				invocationParameters.put(
					"JENKINS_GITHUB_URL", jenkinsGitHubURL);

				invocationParameters.put("REPORT_NAMES", reportName);
				invocationParameters.put("SLAVE_LABEL", "slave");

				_invoke(invocationParameters);

				continue;
			}

			groupedReportNames.add(reportName);
		}

		if (!groupedReportNames.isEmpty()) {
			Map<String, String> invocationParameters = new HashMap<>();

			invocationParameters.put("JENKINS_GITHUB_URL", jenkinsGitHubURL);

			invocationParameters.put(
				"REPORT_NAMES", String.join(",", groupedReportNames));

			_invoke(invocationParameters);
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

	private Map<String, Long> _getLatestReportUpdateTimes() {
		Map<String, Long> latestReportUpdateTimes = new HashMap<>();

		Map<String, JSONObject> buildJSONObjectsMap = _getBuildJSONObjectsMap();

		BuildData buildData = getBuildData();

		buildJSONObjectsMap.remove(buildData.getBuildURL());

		for (GenerateReportsBuildRunner.Report report :
				GenerateReportsBuildRunner.Report.values()) {

			String reportName = report.toString();

			for (Map.Entry<String, JSONObject> entry :
					buildJSONObjectsMap.entrySet()) {

				JSONObject buildJSONObject = entry.getValue();

				String buildDescription = buildJSONObject.optString(
					"description", "");

				if (buildDescription.contains(reportName)) {
					latestReportUpdateTimes.put(
						reportName, buildJSONObject.getLong("timestamp"));

					break;
				}
			}
		}

		for (GenerateReportsBuildRunner.Report report :
				GenerateReportsBuildRunner.Report.values()) {

			String reportName = report.toString();

			long defaultStartTime =
				buildData.getStartTime() - _getReportStaleDuration(reportName);

			latestReportUpdateTimes.putIfAbsent(reportName, defaultStartTime);
		}

		return latestReportUpdateTimes;
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

		Map<String, Long> latestReportUpdateTimes =
			_getLatestReportUpdateTimes();

		BuildData buildData = getBuildData();

		long startTime = buildData.getStartTime();

		for (Map.Entry<String, Long> entry :
				latestReportUpdateTimes.entrySet()) {

			String reportName = entry.getKey();

			long reportStaleDuration = startTime - entry.getValue();

			if (reportStaleDuration >= _getReportStaleDuration(reportName)) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						reportName, " was last generated ",
						JenkinsResultsParserUtil.toDurationString(
							reportStaleDuration),
						" ago which exceeds the stale duration of ",
						JenkinsResultsParserUtil.toDurationString(
							_getReportStaleDuration(reportName))));

				_selectedReportNames.add(reportName);
			}
		}

		Collections.sort(_selectedReportNames);

		return _selectedReportNames;
	}

	private void _invoke(Map<String, String> invocationParameters) {
		BuildData buildData = getBuildData();

		invocationParameters.put("PARENT_BUILD_URL", buildData.getBuildURL());

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		StringBuilder sb = new StringBuilder();

		String jenkinsMasterName = buildProperties.getProperty(
			"report.generate.reports.jenkins.master");

		String jobURL = "http://" + jenkinsMasterName + "/job/generate-reports";

		sb.append(jobURL);

		sb.append("/buildWithParameters?token=");

		sb.append(buildProperties.getProperty("jenkins.authentication.token"));

		for (Map.Entry<String, String> invocationParameter :
				invocationParameters.entrySet()) {

			String invocationParameterValue = invocationParameter.getValue();

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					invocationParameterValue)) {

				continue;
			}

			sb.append("&");
			sb.append(invocationParameter.getKey());
			sb.append("=");
			sb.append(invocationParameterValue);
		}

		try {
			JenkinsResultsParserUtil.toString(sb.toString());

			System.out.println(
				"The " + invocationParameters.get("REPORT_NAMES") +
					" report(s) will be generated at: " + jobURL);
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to invoke a new build to generate reports");

			ioException.printStackTrace();
		}
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

	private List<String> _selectedReportNames;
	private Workspace _workspace;

}