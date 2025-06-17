/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayS3Bucket;
import com.liferay.jenkins.results.parser.testray.TestrayS3Object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	public void addTestrayAttachmentURL(URL testrayAttachmentURL) {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

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

		if (!buildReportJSONObject.has("buildParameters")) {
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
		JobReport jobReport = getJobReport();

		JenkinsMaster jenkinsMaster = jobReport.getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://storage.cloud.google.com/testray-results/",
					getStartYearMonth(), "/", jenkinsMaster.getName(), "/",
					jobReport.getJobName(), "/",
					String.valueOf(getBuildNumber()), "/build-report.json.gz"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public URL getBuildReportJSONUserContentURL() {
		JobReport jobReport = getJobReport();

		JenkinsMaster jenkinsMaster = jobReport.getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://", jenkinsMaster.getName(),
					".liferay.com/userContent/jobs/", jobReport.getJobName(),
					"/builds/", String.valueOf(getBuildNumber()),
					"/build-report.json.gz"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public TestrayS3Object getBuildReportTestrayS3Object() {
		JobReport jobReport = getJobReport();

		JenkinsMaster jenkinsMaster = jobReport.getJenkinsMaster();

		TestrayS3Bucket testrayS3Bucket = TestrayS3Bucket.getInstance();

		return testrayS3Bucket.getTestrayS3Object(
			JenkinsResultsParserUtil.combine(
				getStartYearMonth(), "/", jenkinsMaster.getName(), "/",
				jobReport.getJobName(), "/", String.valueOf(getBuildNumber()),
				"/build-report.json.gz"));
	}

	@Override
	public ControllerBuildReport getControllerBuildReport() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (!buildReportJSONObject.has("controller")) {
			return null;
		}

		JSONObject controllerJSONObject = buildReportJSONObject.getJSONObject(
			"controller");

		if (!controllerJSONObject.has("buildURL")) {
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
		if (_downstreamBuildReports != null) {
			return _downstreamBuildReports;
		}

		_downstreamBuildReports = new ArrayList<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		JSONArray batchesJSONArray = buildReportJSONObject.optJSONArray(
			"batches");

		if (batchesJSONArray == null) {
			return _downstreamBuildReports;
		}

		for (int i = 0; i < batchesJSONArray.length(); i++) {
			JSONObject batchJSONObject = batchesJSONArray.optJSONObject(i);

			if (batchJSONObject == null) {
				continue;
			}

			String batchName = batchJSONObject.optString("batchName");
			JSONArray buildsJSONArray = batchJSONObject.optJSONArray("builds");

			if (JenkinsResultsParserUtil.isNullOrEmpty(batchName) ||
				(buildsJSONArray == null)) {

				continue;
			}

			for (int j = 0; j < buildsJSONArray.length(); j++) {
				_downstreamBuildReports.add(
					BuildReportFactory.newDownstreamBuildReport(
						batchName, buildsJSONArray.getJSONObject(j), this));
			}
		}

		_downstreamBuildReports.removeAll(Collections.singleton(null));

		return _downstreamBuildReports;
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
	public String getTestrayBuildDateString() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles");
	}

	@Override
	public URL getTestResultsJSONUserContentURL() {
		JobReport jobReport = getJobReport();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://test-1-0.liferay.com/userContent/testResults/",
					jobReport.getJobName(), "/builds/",
					String.valueOf(getBuildNumber()), "/test.results.json"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getTestSuiteName() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

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

		return buildReportJSONObject.optLong("totalDuration");
	}

	protected BaseTopLevelBuildReport(JSONObject buildReportJSONObject) {
		super(buildReportJSONObject);

		setStartDate(new Date(buildReportJSONObject.getLong("startTime")));
	}

	protected BaseTopLevelBuildReport(
		JSONObject buildJSONObject, JobReport jobReport) {

		super(buildJSONObject, jobReport);

		setStartDate(new Date(buildJSONObject.getLong("timestamp")));
	}

	protected BaseTopLevelBuildReport(TopLevelBuild topLevelBuild) {
		super(topLevelBuild.getBuildURL());

		setStartDate(new Date(topLevelBuild.getStartTime()));
	}

	protected BaseTopLevelBuildReport(URL buildURL) {
		super(buildURL);
	}

	protected JSONObject getBuildReportJSONObject(
		JSONObject buildResultJSONObject) {

		if ((buildResultJSONObject == null) ||
			!buildResultJSONObject.has("duration") ||
			!buildResultJSONObject.has("result") ||
			!buildResultJSONObject.has("stopWatchRecords")) {

			return null;
		}

		JSONObject buildReportJSONObject = new JSONObject();

		buildReportJSONObject.put(
			"batches", _getBatchesJSONArray(buildResultJSONObject)
		).put(
			"buildURL", String.valueOf(getBuildURL())
		);

		if (buildResultJSONObject.has("controller")) {
			buildReportJSONObject.put(
				"controller", buildResultJSONObject.get("controller"));
		}

		buildReportJSONObject.put(
			"duration", buildResultJSONObject.get("duration")
		).put(
			"result", buildResultJSONObject.get("result")
		);

		long startTime = buildResultJSONObject.optLong("startTime", 0L);

		if (startTime == 0L) {
			String startTimeString = _getVariableFromJenkinsConsole(
				"TOP_LEVEL_START_TIME");

			if ((startTimeString != null) && startTimeString.matches("\\d+")) {
				startTime = Integer.parseInt(startTimeString);
			}
		}

		if (startTime == 0L) {
			StopWatchRecordsGroup stopWatchRecordsGroup =
				new StopWatchRecordsGroup(buildResultJSONObject);

			StopWatchRecord stopWatchRecord = stopWatchRecordsGroup.get(
				"start.current.job");

			if (stopWatchRecord == null) {
				return null;
			}

			startTime = stopWatchRecord.getStartTimestamp();
		}

		buildReportJSONObject.put(
			"startTime", startTime
		).put(
			"status", "completed"
		).put(
			"stopWatchRecords", buildResultJSONObject.get("stopWatchRecords")
		);

		String testSuiteName = _getTestSuiteNameFromBuildResult(
			buildResultJSONObject);

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			testSuiteName = _getVariableFromJenkinsConsole("CI_TEST_SUITE");
		}

		buildReportJSONObject.put("testSuiteName", testSuiteName);

		return buildReportJSONObject;
	}

	protected abstract File getJenkinsConsoleLocalFile();

	protected String getStartYearMonth() {
		return JenkinsResultsParserUtil.toDateString(
			getStartDate(), "yyyy-MM", "America/Los_Angeles");
	}

	private JSONArray _getBatchesJSONArray(JSONObject buildResultJSONObject) {
		JSONArray batchResultsJSONArray = buildResultJSONObject.optJSONArray(
			"batchResults");

		if ((batchResultsJSONArray == null) ||
			batchResultsJSONArray.isEmpty()) {

			return new JSONArray();
		}

		Map<String, List<JSONObject>> batchJSONObjectsMap = new HashMap<>();

		for (int i = 0; i < batchResultsJSONArray.length(); i++) {
			JSONObject batchResultJSONObject =
				batchResultsJSONArray.optJSONObject(i);

			if (batchResultJSONObject == null) {
				continue;
			}

			String batchName = batchResultJSONObject.getString("jobVariant");

			batchName = batchName.replaceAll("([^/]+)/.*", "$1");

			List<JSONObject> batchJSONObjects = batchJSONObjectsMap.get(
				batchName);

			if (batchJSONObjects == null) {
				batchJSONObjects = new ArrayList<>();

				batchJSONObjectsMap.put(batchName, batchJSONObjects);
			}

			if (batchResultJSONObject.has("buildResults")) {
				JSONArray buildResultsJSONArray =
					batchResultJSONObject.getJSONArray("buildResults");

				for (int j = 0; j < buildResultsJSONArray.length(); j++) {
					batchJSONObjects.add(
						buildResultsJSONArray.getJSONObject(j));
				}
			}
			else {
				batchJSONObjects.add(batchResultJSONObject);
			}
		}

		JSONArray batchesJSONArray = new JSONArray();

		for (Map.Entry<String, List<JSONObject>> batchJSONObjectsEntry :
				batchJSONObjectsMap.entrySet()) {

			JSONObject batchJSONObject = new JSONObject();

			batchJSONObject.put("batchName", batchJSONObjectsEntry.getKey());

			JSONArray buildsJSONArray = new JSONArray();

			for (JSONObject jsonObject : batchJSONObjectsEntry.getValue()) {
				if (!jsonObject.has("buildURL") ||
					!jsonObject.has("duration") || !jsonObject.has("result") ||
					!jsonObject.has("stopWatchRecords")) {

					continue;
				}

				JSONObject buildJSONObject = new JSONObject();

				buildJSONObject.put(
					"axisName", jsonObject.opt("axisName")
				).put(
					"buildURL", jsonObject.get("buildURL")
				).put(
					"duration", jsonObject.get("duration")
				).put(
					"result", jsonObject.get("result")
				);

				long startTime = jsonObject.optLong("startTime", 0L);

				if (startTime == 0L) {
					StopWatchRecordsGroup stopWatchRecordsGroup =
						new StopWatchRecordsGroup(jsonObject);

					StopWatchRecord stopWatchRecord = stopWatchRecordsGroup.get(
						"start.current.job");

					if (stopWatchRecord == null) {
						stopWatchRecord = stopWatchRecordsGroup.get(
							"run.current.job");
					}

					if (stopWatchRecord == null) {
						continue;
					}

					startTime = stopWatchRecord.getStartTimestamp();
				}

				buildJSONObject.put(
					"startTime", startTime
				).put(
					"status", "completed"
				).put(
					"stopWatchRecords", jsonObject.get("stopWatchRecords")
				).put(
					"testResults", new JSONArray()
				);

				buildsJSONArray.put(buildJSONObject);
			}

			batchJSONObject.put("builds", buildsJSONArray);

			batchesJSONArray.put(batchJSONObject);
		}

		return batchesJSONArray;
	}

	private String _getTestSuiteNameFromBuildResult(
		JSONObject buildResultJSONObject) {

		String testSuiteName = buildResultJSONObject.optString("testSuiteName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
			return testSuiteName;
		}

		JSONArray batchResultsJSONArray = buildResultJSONObject.optJSONArray(
			"batchResults");

		if (batchResultsJSONArray == null) {
			return null;
		}

		for (int i = 0; i < batchResultsJSONArray.length(); i++) {
			JSONObject batchResultJSONObject =
				batchResultsJSONArray.getJSONObject(i);

			testSuiteName = batchResultJSONObject.optString("testSuiteName");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
				return testSuiteName;
			}
		}

		return null;
	}

	private String _getVariableFromJenkinsConsole(String variableName) {
		File jenkinsConsoleLocalFile = getJenkinsConsoleLocalFile();

		if ((jenkinsConsoleLocalFile == null) ||
			!jenkinsConsoleLocalFile.exists()) {

			return null;
		}

		Pattern pattern = _getVariablePattern(variableName);

		BufferedReader bufferedReader = null;

		FileReader fileReader = null;

		try {
			fileReader = new FileReader(jenkinsConsoleLocalFile);

			bufferedReader = new BufferedReader(fileReader);

			String line;

			long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

			while ((line = bufferedReader.readLine()) != null) {
				long end = JenkinsResultsParserUtil.getCurrentTimeMillis();

				long duration = end - start;

				if (duration >= (5 * 1000)) {
					break;
				}

				Matcher matcher = pattern.matcher(line);

				if (!matcher.find()) {
					continue;
				}

				return matcher.group("variableValue");
			}
		}
		catch (Exception exception) {
			return null;
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				}
				catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			if (fileReader != null) {
				try {
					fileReader.close();
				}
				catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		return null;
	}

	private Pattern _getVariablePattern(String variableName) {
		Pattern variablePattern = _variablePatterns.get(variableName);

		if (variablePattern == null) {
			variablePattern = Pattern.compile(
				variableName + "=(?<variableValue>[^\\s]+)");

			_variablePatterns.put(variableName, variablePattern);
		}

		return variablePattern;
	}

	private static final Map<String, Pattern> _variablePatterns =
		new HashMap<>();

	private ControllerBuildReport _controllerBuildReport;
	private List<DownstreamBuildReport> _downstreamBuildReports;

}