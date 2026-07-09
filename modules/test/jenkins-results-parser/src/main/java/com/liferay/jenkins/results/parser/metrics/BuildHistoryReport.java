/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.testray.TestrayBuild;
import com.liferay.jenkins.results.parser.testray.TestrayFactory;
import com.liferay.jenkins.results.parser.testray.TestrayRun;
import com.liferay.jenkins.results.parser.testray.TestrayRunComparison;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class BuildHistoryReport {

	public static BuildHistoryReport newAggregateReport(
		long durationDays, File outputDir, String startDateString) {

		BuildHistoryReport buildHistoryReport = new BuildHistoryReport(
			outputDir);

		buildHistoryReport.addFilesFromResource(
			"dependencies/metrics/aggregate-report", "/index.html");

		StringBuilder sb = new StringBuilder();

		sb.append(_getGeneratedDateJavaScriptVariable());
		sb.append(_getWeeklyServerDurationJavaScriptVariable());

		long duration = TimeUnit.DAYS.toMillis(durationDays);
		long startTime = _getStartTime(startDateString);

		Collection<BuildHistory> buildHistories =
			BuildHistoryProcessor.newAggregateJobHistories(duration, startTime);

		sb.append(
			_getTableDataJavaScriptVariable(
				buildHistories, "Job Category", 1, "[Total]"));

		buildHistoryReport.addFile(sb.toString(), "js/table-data.js");

		buildHistoryReport.addFile(
			_getTimelineDataJavaScriptVariable(
				buildHistories, duration, startTime),
			"js/timeline-data.js");

		return buildHistoryReport;
	}

	public static BuildHistoryReport newAWSBuildComparisonReport(
		long durationDays, File outputDir, String startDateString) {

		BuildHistoryReport buildHistoryReport = new BuildHistoryReport(
			outputDir);

		buildHistoryReport.addFilesFromResource(
			"dependencies/metrics/build-comparison-report", "/css/main.css",
			"/index.html", "/js/main.js");

		File baseDir = BuildHistoryProcessor.getBaseDir();

		BuildHistoryProcessor.setBaseDir(
			new File(baseDir.getParentFile(), "aws/builds"));

		BuildHistory awsBuildHistory =
			BuildHistoryProcessor.mergeBuildHistories(
				BuildHistoryProcessor.newTopLevelBuildHistories(
					TimeUnit.DAYS.toMillis(durationDays),
					_getStartTime(startDateString)),
				"aws");

		Map<String, BuildJSONObject> awsBuildJSONObjectsMap =
			awsBuildHistory.getBuildJSONObjectsMap();

		StringBuilder sb = new StringBuilder();

		sb.append(_getGeneratedDateJavaScriptVariable());
		sb.append("\nvar reportName = \"AWS Build Comparison Report\";");
		sb.append("var tableData = ");

		JSONArray tableJSONArray = new JSONArray();

		List<List<Object>> rows = new ArrayList<>();

		rows.add(
			new ArrayList<Object>() {
				{
					add("Build Identifier");
					add("Testray Comparison URL");
					add("Test Results in Common (%)");
					add("Test Failure Differences");
					add("Untested Test Differences");
					add("Build URL (DB)");
					add("Build URL (AWS)");
					add("DB Build Testray URL");
					add("AWS Build Testray URL");
					add("Top Level Start Time (DB)");
					add("Top Level Start Time (AWS)");
					add("Top Level Duration (DB)");
					add("Top Level Duration (AWS)");
				}
			});

		BuildHistory buildHistory = BuildHistoryProcessor.mergeBuildHistories(
			BuildHistoryProcessor.newTopLevelBuildHistories(
				TimeUnit.DAYS.toMillis(durationDays),
				_getStartTime(startDateString)),
			"");

		Map<String, BuildJSONObject> buildJSONObjectsMap =
			buildHistory.getBuildJSONObjectsMap();

		for (Map.Entry<String, BuildJSONObject> entry :
				buildJSONObjectsMap.entrySet()) {

			String buildIdentifier = entry.getKey();

			if (!awsBuildJSONObjectsMap.containsKey(buildIdentifier)) {
				continue;
			}

			BuildJSONObject awsBuildJSONObject = awsBuildJSONObjectsMap.get(
				buildIdentifier);
			BuildJSONObject buildJSONObject = entry.getValue();

			String awsTestrayBuildURL = awsBuildJSONObject.getTestrayBuildURL();
			String testrayBuildURL = buildJSONObject.getTestrayBuildURL();

			if (JenkinsResultsParserUtil.isNullOrEmpty(awsTestrayBuildURL) ||
				JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildURL)) {

				continue;
			}

			TestrayBuild awsTestrayBuild = TestrayFactory.newTestrayBuild(
				_getURL(awsTestrayBuildURL));

			TestrayRun awsTestrayRun = awsTestrayBuild.getTestrayRun(
				TestrayRun.getDefaultRunIdString());

			TestrayBuild testrayBuild = TestrayFactory.newTestrayBuild(
				_getURL(testrayBuildURL));

			TestrayRun testrayRun = testrayBuild.getTestrayRun(
				TestrayRun.getDefaultRunIdString());

			if ((awsTestrayRun == null) || (testrayRun == null)) {
				continue;
			}

			TestrayRunComparison testrayRunComparison =
				TestrayFactory.newTestrayRunComparison(
					testrayRun, awsTestrayRun);

			rows.add(
				new ArrayList<Object>() {
					{
						add(buildIdentifier);
						add(testrayRunComparison.getComparisonURL());
						add(
							testrayRunComparison.
								getCommonStatusTestCountPercentage());
						add(testrayRunComparison.getNewFailureTestCount());
						add(testrayRunComparison.getNewUntestedTestCount());
						add(buildJSONObject.getURL());
						add(awsBuildJSONObject.getURL());
						add(testrayBuildURL);
						add(awsTestrayBuildURL);
						add(buildJSONObject.getStartTime());
						add(awsBuildJSONObject.getStartTime());
						add(buildJSONObject.getDuration());
						add(awsBuildJSONObject.getDuration());
					}
				});
		}

		for (List<Object> row : rows) {
			tableJSONArray.put(new JSONArray(row));
		}

		sb.append(tableJSONArray);

		sb.append(";");

		buildHistoryReport.addFile(sb.toString(), "js/table-data.js");

		return buildHistoryReport;
	}

	public static BuildHistoryReport newPullRequestTestSuiteReport(
		long durationDays, File outputDir, String startDateString) {

		return _newTestSuiteReport(
			durationDays, _portalMasterPullRequestJobNamePattern, outputDir,
			"liferay-portal/master Pull Request History Report",
			startDateString);
	}

	public static BuildHistoryReport newReleaseTestSuiteReport(
		long durationDays, File outputDir, String startDateString) {

		return _newTestSuiteReport(
			durationDays, _portalReleaseJobNamePattern, outputDir,
			"Portal Release History Report", startDateString);
	}

	public static BuildHistoryReport newUpstreamTestSuiteReport(
		long durationDays, File outputDir, String startDateString) {

		return _newTestSuiteReport(
			durationDays, _portalMasterUpstreamJobNamePattern, outputDir,
			"liferay-portal/master Upstream History Report", startDateString);
	}

	public static BuildHistoryReport newUtilizationReport(
		long durationDays, File outputDir, String startDateString) {

		BuildHistoryReport buildHistoryReport = new BuildHistoryReport(
			outputDir);

		buildHistoryReport.addFilesFromResource(
			"dependencies/metrics/utilization-report", "/css/main.css",
			"/index.html", "/js/main.js");

		Collection<BuildHistory> utilizationBuildHistories =
			BuildHistoryProcessor.newUtilizationBuildHistories(
				TimeUnit.DAYS.toMillis(durationDays),
				_getStartTime(startDateString));

		StringBuilder sb = new StringBuilder();

		sb.append(_getWeeklyServerDurationJavaScriptVariable());

		sb.append(
			_getTableDataJavaScriptVariable(
				utilizationBuildHistories, "Category", 7, "All", null,
				"categoryTableData"));

		Collection<BuildHistory> utilizationTestTypeBuildHistories =
			BuildHistoryProcessor.newUtilizationTestTypeBuildHistories(
				TimeUnit.DAYS.toMillis(durationDays),
				_getStartTime(startDateString));

		sb.append(
			_getTableDataJavaScriptVariable(
				utilizationTestTypeBuildHistories, "Test Batch Type", 7, "All",
				Arrays.asList(
					BuildHistory.TableMetric.AVERAGE_DOWNSTREAM_BUILD_DURATION.
						toString(),
					BuildHistory.TableMetric.INVOKED_BUILDS.toString(),
					BuildHistory.TableMetric.TOTAL_SERVER_DURATION.toString()),
				"testTypeTableData"));

		sb.append("\n");
		sb.append(_getGeneratedDateJavaScriptVariable());
		sb.append("\nvar reportName = \"Utilization Report\";");

		buildHistoryReport.addFile(sb.toString(), "js/table-data.js");

		return buildHistoryReport;
	}

	public BuildHistoryReport(File outputDir) {
		_outputDir = outputDir;
	}

	public void addFile(String fileContent, String fileName) {
		_fileMap.put(new File(_outputDir, fileName), fileContent);
	}

	public void addFilesFromResource(
		String resourceDirPath, String... fileNames) {

		for (String fileName : fileNames) {
			try {
				addFile(
					JenkinsResultsParserUtil.getResourceFileContent(
						resourceDirPath + fileName),
					fileName);
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to get file content from resource: " +
						resourceDirPath + fileName);
			}
		}
	}

	public void write() throws IOException {
		FileUtils.deleteDirectory(_outputDir);

		for (Map.Entry<File, String> entry : _fileMap.entrySet()) {
			File file = entry.getKey();

			String filePath = file.getCanonicalPath();

			if (filePath.contains(".html")) {
				System.out.println("Report created at: file://" + filePath);
			}

			JenkinsResultsParserUtil.write(entry.getKey(), entry.getValue());
		}
	}

	private static String _getGeneratedDateJavaScriptVariable() {
		return "var dataGeneratedDate = new Date(" +
			JenkinsResultsParserUtil.getCurrentTimeMillis() + ");\n";
	}

	private static LocalDateTime _getLocalDateTime(String startDateString) {
		return LocalDateTime.parse(
			startDateString + " 00:00:00",
			DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
	}

	private static long _getStartTime(String startDateString) {
		return JenkinsResultsParserUtil.getMillis(
			_getLocalDateTime(startDateString));
	}

	private static String _getTableDataJavaScriptVariable(
		Collection<BuildHistory> buildHistories, String groupIdentifierName,
		int intervalDays, String mergedBuildHistoryName) {

		return _getTableDataJavaScriptVariable(
			buildHistories, groupIdentifierName, intervalDays,
			mergedBuildHistoryName, null, "tableData");
	}

	private static String _getTableDataJavaScriptVariable(
		Collection<BuildHistory> buildHistories, String groupIdentifierName,
		int intervalDays, String mergedBuildHistoryName,
		List<String> metricNames, String tableName) {

		JSONArray jsonArray = new JSONArray();

		boolean removeHeader = false;

		for (BuildHistory buildHistory : buildHistories) {
			JSONArray tableJSONArray = buildHistory.getTableJSONArray(
				groupIdentifierName, intervalDays, metricNames);

			if (removeHeader) {
				tableJSONArray.remove(0);
			}
			else {
				removeHeader = true;
			}

			jsonArray.putAll(tableJSONArray);
		}

		if (mergedBuildHistoryName != null) {
			BuildHistory mergedBuildHistory =
				BuildHistoryProcessor.mergeBuildHistories(
					buildHistories, mergedBuildHistoryName);

			JSONArray tableJSONArray = mergedBuildHistory.getTableJSONArray(
				groupIdentifierName, intervalDays);

			tableJSONArray.remove(0);

			jsonArray.putAll(tableJSONArray);
		}

		return "var " + tableName + " = " + jsonArray.toString() + ";\n";
	}

	private static String _getTimelineDataJavaScriptVariable(
		Collection<BuildHistory> buildHistories, long duration,
		long startTime) {

		JSONObject jsonObject = new JSONObject();

		JSONArray jsonArray = new JSONArray();

		for (BuildHistory buildHistory : buildHistories) {
			jsonArray.put(buildHistory.getTimelineJSONObject());
		}

		jsonObject.put(
			"jobTimelines", jsonArray
		).put(
			"time", BuildHistory.getTimeJSONArray(duration, startTime)
		);

		return "var timelineData = " + jsonObject.toString();
	}

	private static URL _getURL(String url) {
		if (!JenkinsResultsParserUtil.isURL(url)) {
			return null;
		}

		try {
			return new URL(url);
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	private static String _getWeeklyServerDurationJavaScriptVariable() {
		StringBuilder sb = new StringBuilder();

		try {
			long maxNodeCount = Long.parseLong(
				JenkinsResultsParserUtil.getBuildProperty(
					"report.ci.max.node.count"));

			if (maxNodeCount != 0) {
				sb.append("var maxWeeklyServerDurationMillis = ");
				sb.append(maxNodeCount * 7 * 24 * 60 * 60 * 1000);
				sb.append(";\n");
			}
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to get build property \"report.ci.max.node.count\"");
		}

		return sb.toString();
	}

	private static BuildHistoryReport _newTestSuiteReport(
		long durationDays, Pattern jobNamePattern, File outputDir,
		String reportName, String startDateString) {

		StringBuilder sb = new StringBuilder();

		sb.append(_getGeneratedDateJavaScriptVariable());

		BuildHistoryReport buildHistoryReport = new BuildHistoryReport(
			outputDir);

		buildHistoryReport.addFilesFromResource(
			"dependencies/metrics/test-suite-report", "/index.html");

		long duration = TimeUnit.DAYS.toMillis(durationDays);

		Collection<BuildHistory> buildHistories =
			BuildHistoryProcessor.newTestSuiteJobHistories(
				duration, jobNamePattern, _getStartTime(startDateString));

		sb.append(
			_getTableDataJavaScriptVariable(
				buildHistories, "Test Suite Name", 1, "[Total]"));

		sb.append("\nvar reportName = \"");
		sb.append(reportName);
		sb.append("\";");

		buildHistoryReport.addFile(sb.toString(), "js/table-data.js");

		return buildHistoryReport;
	}

	private static final Pattern _portalMasterPullRequestJobNamePattern =
		Pattern.compile(
			"test-portal-acceptance-pullrequest(|-downstream)\\(master\\)");
	private static final Pattern _portalMasterUpstreamJobNamePattern =
		Pattern.compile(
			"test-portal-(acceptance-upstream-dxp|testsuite-upstream)" +
				"(|-downstream)\\(master\\)");
	private static final Pattern _portalReleaseJobNamePattern = Pattern.compile(
		"test-portal(|-fixpack|-hotfix)-release(|-downstream)");

	private final Map<File, String> _fileMap = new HashMap<>();
	private final File _outputDir;

}