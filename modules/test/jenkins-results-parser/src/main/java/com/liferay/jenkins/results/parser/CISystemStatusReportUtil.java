/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.text.DecimalFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class CISystemStatusReportUtil {

	public static void appendNodeHistoryDataToJavaScriptFile(String filePath)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		JSONObject jsonObject = null;

		LocalDate localDate = JenkinsResultsParserUtil.getLocalDate(
			System.currentTimeMillis());

		long durationDays = _getReportDurationDays();

		localDate = localDate.minusDays(durationDays - 1);

		for (String dateString :
				JenkinsResultsParserUtil.getDateStrings(
					durationDays, localDate)) {

			File nodeDataFile = new File(
				_TMP_BASE_DIR, dateString + "/node.json");

			if (!nodeDataFile.exists()) {
				System.out.println(
					"Node data not available in: " + nodeDataFile);

				continue;
			}

			if (jsonObject == null) {
				jsonObject = JenkinsResultsParserUtil.toJSONObject(
					"file://" + nodeDataFile.getPath());

				continue;
			}

			_mergeJSONArraysInJSONObjects(
				jsonObject,
				JenkinsResultsParserUtil.toJSONObject(
					"file://" + nodeDataFile.getPath()),
				_NODE_METRIC_NAMES);
		}

		sb.append("\nvar nodeHistoryData = ");

		sb.append(jsonObject);

		sb.append(";");

		JenkinsResultsParserUtil.append(new File(filePath), sb.toString());
	}

	public static void copyBaseReportFiles(String filePath) throws IOException {
		FileUtils.copyDirectory(
			_CI_SYSTEM_STATUS_REPORT_DIR, new File(filePath));
	}

	public static void writeConfigJSFile(String filePath) throws IOException {
		int maxYAxes = 2370;

		if (JenkinsResultsParserUtil.isCloudCINode()) {
			maxYAxes = 1210;
		}

		String content = String.format("window.MAX_Y_AXES = %d;%n", maxYAxes);

		File configFile = new File(filePath, "/js/config.js");

		JenkinsResultsParserUtil.write(configFile, content);
	}

	public static void writeJenkinsDataJavaScriptFile(String filePath)
		throws IOException {

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(
			JenkinsResultsParserUtil.getBuildProperty(
				"ci.system.status.report.jenkins.cohort"));

		jenkinsCohort.writeDataJavaScriptFile(filePath);

		appendNodeHistoryDataToJavaScriptFile(filePath);
	}

	public static void writeTestrayDataJavaScriptFile(
			String filePath, String jobName, final String testSuiteName)
		throws IOException {

		List<Callable<File>> callables = new ArrayList<>();

		List<File> buildReportJSONFiles = _getBuildReportJSONFiles(jobName);

		for (final File buildReportJSONFile : buildReportJSONFiles) {
			callables.add(
				new Callable<File>() {

					@Override
					public File call() throws Exception {
						long start =
							JenkinsResultsParserUtil.getCurrentTimeMillis();

						JSONObject buildReportJSONObject =
							JenkinsResultsParserUtil.toJSONObject(
								"file://" + buildReportJSONFile.getPath());

						try {
							TopLevelBuildReport topLevelBuildReport =
								BuildReportFactory.newTopLevelBuildReport(
									buildReportJSONObject);

							if ((topLevelBuildReport == null) ||
								!Objects.equals(
									testSuiteName,
									topLevelBuildReport.getTestSuiteName())) {

								return null;
							}

							LocalDate localDate =
								JenkinsResultsParserUtil.getLocalDate(
									topLevelBuildReport.getStartDate());

							if (!_results.containsKey(localDate)) {
								return null;
							}

							List<Result> results = _results.get(localDate);

							results.add(new Result(topLevelBuildReport));

							return buildReportJSONFile;
						}
						catch (Exception exception) {
							RuntimeException runtimeException =
								new RuntimeException(
									JenkinsResultsParserUtil.getCanonicalPath(
										buildReportJSONFile),
									exception);

							runtimeException.printStackTrace();

							return null;
						}
						finally {
							long end =
								JenkinsResultsParserUtil.getCurrentTimeMillis();

							System.out.println(
								JenkinsResultsParserUtil.combine(
									JenkinsResultsParserUtil.getCanonicalPath(
										buildReportJSONFile),
									" processed in ",
									JenkinsResultsParserUtil.toDurationString(
										end - start)));
						}
					}

				});
		}

		ParallelExecutor<File> parallelExecutor = new ParallelExecutor<>(
			callables, _executorService, "writeTestrayDataJavaScriptFile");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("var relevantSuiteBuildData = ");
		sb.append(_getRelevantSuiteBuildDataJSONObject());

		sb.append("\nvar topLevelTotalBuildDurationData = ");
		sb.append(_getTopLevelTotalBuildDurationJSONObject());

		sb.append("\nvar topLevelActiveBuildDurationData = ");
		sb.append(_getTopLevelActiveBuildDurationJSONObject());

		sb.append("\nvar downstreamBuildDurationData = ");
		sb.append(_getDownstreamBuildDurationJSONObject());

		sb.append("\nvar testrayDataGeneratedDate = new Date(");
		sb.append(JenkinsResultsParserUtil.getCurrentTimeMillis());
		sb.append(");");

		sb.append("\nvar successRateData = ");
		sb.append(_getSuccessRateDataJSONArray());
		sb.append(";");

		JenkinsResultsParserUtil.write(filePath, sb.toString());
	}

	protected static String getPercentage(Integer dividend, Integer divisor) {
		double quotient = 0;

		if (divisor != 0) {
			quotient = (double)dividend / (double)divisor;
		}

		DecimalFormat decimalFormat = new DecimalFormat("###.##%");

		return decimalFormat.format(quotient);
	}

	private static List<File> _getBuildReportJSONFiles(String jobName) {
		List<File> buildReportJSONFiles = new ArrayList<>();

		for (String dateString : _dateStrings) {
			File testrayLogsDateDir = new File(_TESTRAY_LOGS_DIR, dateString);

			if (!testrayLogsDateDir.exists()) {
				continue;
			}

			Process process;

			try {
				process = JenkinsResultsParserUtil.executeBashCommands(
					true, _TESTRAY_LOGS_DIR, 1000 * 60 * 60,
					JenkinsResultsParserUtil.combine(
						"find ", dateString, "/*/",
						JenkinsResultsParserUtil.escapeForBash(jobName),
						"/*/build-report.json -mtime -15"));
			}
			catch (IOException | TimeoutException exception) {
				continue;
			}

			int exitValue = process.exitValue();

			if (exitValue != 0) {
				continue;
			}

			String output = null;

			try {
				output = JenkinsResultsParserUtil.readInputStream(
					process.getInputStream());

				output = output.replace(
					"Finished executing Bash commands.\n", "");

				output = output.trim();
			}
			catch (IOException ioException) {
				continue;
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(output)) {
				continue;
			}

			for (String buildReportJSONFilePath : output.split("\n")) {
				Matcher matcher = _buildReportFilePathPattern.matcher(
					buildReportJSONFilePath);

				if (matcher.find()) {
					int masterID = Integer.parseInt(matcher.group("masterID"));

					if (JenkinsResultsParserUtil.isCloudCINode()) {
						if (masterID <= 40) {
							continue;
						}
					}
					else {
						if (masterID > 40) {
							continue;
						}
					}
				}

				buildReportJSONFiles.add(
					new File(_TESTRAY_LOGS_DIR, buildReportJSONFilePath));
			}
		}

		return buildReportJSONFiles;
	}

	private static JSONObject _getDownstreamBuildDurationJSONObject() {
		JSONObject datesDurationsJSONObject = new JSONObject();

		JSONArray datesJSONArray = new JSONArray();
		JSONArray durationsJSONArray = new JSONArray();

		List<LocalDate> localDates = new ArrayList<>(_results.keySet());

		Collections.sort(localDates);

		for (LocalDate localDate : localDates) {
			List<Long> durations = new ArrayList<>();

			for (Result result : _results.get(localDate)) {
				durations.addAll(result.getDownstreamDuration());
			}

			durations.removeAll(Collections.singleton(null));

			if (durations.isEmpty()) {
				datesJSONArray.put(localDate.toString());
			}
			else {
				String meanDuration = JenkinsResultsParserUtil.combine(
					"mean: ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getAverage(durations)));

				datesJSONArray.put(
					new String[] {localDate.toString(), meanDuration});
			}

			Collections.sort(durations);

			durationsJSONArray.put(durations);
		}

		datesDurationsJSONObject.put(
			"dates", datesJSONArray
		).put(
			"durations", durationsJSONArray
		);

		return datesDurationsJSONObject;
	}

	private static JSONObject _getRelevantSuiteBuildDataJSONObject() {
		JSONObject relevantSuiteBuildDataJSONObject = new JSONObject();

		JSONArray datesJSONArray = new JSONArray();
		JSONArray failedBuildsJSONArray = new JSONArray();
		JSONArray passedBuildsJSONArray = new JSONArray();
		JSONArray unstableBuildsJSONArray = new JSONArray();

		List<LocalDate> localDates = new ArrayList<>(_results.keySet());

		Collections.sort(localDates);

		for (LocalDate localDate : localDates) {
			int failedBuilds = 0;
			int passedBuilds = 0;
			int unstableBuilds = 0;

			for (Result result : _results.get(localDate)) {
				String topLevelResult = result.getTopLevelResult();

				if (topLevelResult.equals("FAILURE")) {
					failedBuilds++;

					continue;
				}

				if (topLevelResult.equals("SUCCESS")) {
					passedBuilds++;

					continue;
				}

				if (topLevelResult.equals("APPROVED")) {
					unstableBuilds++;
				}
			}

			datesJSONArray.put(localDate.toString());
			failedBuildsJSONArray.put(failedBuilds);
			passedBuildsJSONArray.put(passedBuilds);
			unstableBuildsJSONArray.put(unstableBuilds);
		}

		relevantSuiteBuildDataJSONObject.put(
			"dates", datesJSONArray
		).put(
			"failed", failedBuildsJSONArray
		).put(
			"succeeded", passedBuildsJSONArray
		).put(
			"unstable", unstableBuildsJSONArray
		);

		return relevantSuiteBuildDataJSONObject;
	}

	private static long _getReportDurationDays() {
		String reportDurationDays = _buildProperties.getProperty(
			"report.duration.days");

		return Long.parseLong(reportDurationDays);
	}

	private static JSONArray _getSuccessRateDataJSONArray() {
		JSONArray successRateDataJSONArray = new JSONArray();

		JSONArray titlesJSONArray = new JSONArray();

		titlesJSONArray.put("Time Period");
		titlesJSONArray.put("Adjusted Success Rate");
		titlesJSONArray.put("Success Rate");
		titlesJSONArray.put("Builds Run");

		successRateDataJSONArray.put(titlesJSONArray);

		LocalDateTime currentLocalDateTime = LocalDateTime.now(ZoneOffset.UTC);

		successRateDataJSONArray.put(
			_getSuccessRateJSONArray(
				"Last 24 Hours", currentLocalDateTime.minusDays(1),
				currentLocalDateTime));
		successRateDataJSONArray.put(
			_getSuccessRateJSONArray(
				"Last 7 Days", currentLocalDateTime.minusDays(_DAYS_PER_WEEK),
				currentLocalDateTime));
		successRateDataJSONArray.put(
			_getSuccessRateJSONArray(
				"Previous 7 Days",
				currentLocalDateTime.minusDays(_DAYS_PER_WEEK * 2),
				currentLocalDateTime.minusDays(_DAYS_PER_WEEK)));

		return successRateDataJSONArray;
	}

	private static JSONArray _getSuccessRateJSONArray(
		String title, LocalDateTime startLocalDateTime,
		LocalDateTime endLocalDateTime) {

		if (startLocalDateTime.compareTo(endLocalDateTime) >= 0) {
			throw new IllegalArgumentException(
				"Start time must preceed end time");
		}

		Set<LocalDate> localDates = new HashSet<>();

		for (int i = 0;
			 startLocalDateTime.compareTo(endLocalDateTime.minusDays(i)) <= 0;
			 i++) {

			LocalDateTime localDateTime = endLocalDateTime.minusDays(i);

			localDates.add(localDateTime.toLocalDate());
		}

		int failedBuilds = 0;
		int passedBuilds = 0;
		int unstableBuilds = 0;

		for (LocalDate localDate : localDates) {
			for (Result result : _results.get(localDate)) {
				LocalDateTime localDateTime =
					JenkinsResultsParserUtil.getLocalDateTime(
						result.getTopLevelStartDate());

				if ((startLocalDateTime.compareTo(localDateTime) >= 0) ||
					(endLocalDateTime.compareTo(localDateTime) <= 0)) {

					continue;
				}

				String topLevelResult = result.getTopLevelResult();

				if (topLevelResult.equals("FAILURE")) {
					failedBuilds++;

					continue;
				}

				if (topLevelResult.equals("SUCCESS")) {
					passedBuilds++;

					continue;
				}

				if (topLevelResult.equals("APPROVED")) {
					unstableBuilds++;
				}
			}
		}

		int totalBuilds = failedBuilds + passedBuilds + unstableBuilds;

		JSONArray successRateJSONArray = new JSONArray();

		successRateJSONArray.put(title);
		successRateJSONArray.put(
			getPercentage(passedBuilds + unstableBuilds, totalBuilds));
		successRateJSONArray.put(getPercentage(passedBuilds, totalBuilds));
		successRateJSONArray.put(totalBuilds);

		return successRateJSONArray;
	}

	private static JSONObject _getTopLevelActiveBuildDurationJSONObject() {
		JSONObject jsonObject = new JSONObject();

		JSONArray datesJSONArray = new JSONArray();
		JSONArray durationsJSONArray = new JSONArray();

		List<LocalDate> dates = new ArrayList<>(_results.keySet());

		Collections.sort(dates);

		for (LocalDate date : dates) {
			List<Long> durations = new ArrayList<>();

			for (Result result : _results.get(date)) {
				long duration = result.getTopLevelActiveDuration();

				if (duration < 0) {
					continue;
				}

				durations.add(duration);
			}

			durations.removeAll(Collections.singleton(null));

			if (durations.isEmpty()) {
				datesJSONArray.put(date.toString());
			}
			else {
				String meanDuration = JenkinsResultsParserUtil.combine(
					"mean: ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getAverage(durations)));

				datesJSONArray.put(
					new String[] {date.toString(), meanDuration});
			}

			Collections.sort(durations);

			durationsJSONArray.put(durations);
		}

		jsonObject.put(
			"dates", datesJSONArray
		).put(
			"durations", durationsJSONArray
		);

		return jsonObject;
	}

	private static JSONObject _getTopLevelTotalBuildDurationJSONObject() {
		JSONObject jsonObject = new JSONObject();

		JSONArray datesJSONArray = new JSONArray();
		JSONArray durationsJSONArray = new JSONArray();

		List<LocalDate> dates = new ArrayList<>(_results.keySet());

		Collections.sort(dates);

		for (LocalDate date : dates) {
			List<Long> durations = new ArrayList<>();

			for (Result result : _results.get(date)) {
				long duration = result.getTopLevelDuration();

				if (duration < 0) {
					continue;
				}

				durations.add(duration);
			}

			durations.removeAll(Collections.singleton(null));

			if (durations.isEmpty()) {
				datesJSONArray.put(date.toString());
			}
			else {
				String meanDuration = JenkinsResultsParserUtil.combine(
					"mean: ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getAverage(durations)));

				datesJSONArray.put(
					new String[] {date.toString(), meanDuration});
			}

			Collections.sort(durations);

			durationsJSONArray.put(durations);
		}

		jsonObject.put(
			"dates", datesJSONArray
		).put(
			"durations", durationsJSONArray
		);

		return jsonObject;
	}

	private static void _mergeJSONArraysInJSONObjects(
		JSONObject jsonObject1, JSONObject jsonObject2, String[] keys) {

		JSONArray timestampsJSONArray1 = jsonObject1.optJSONArray(
			"timestamps", new JSONArray());

		int count1 = timestampsJSONArray1.length();

		JSONArray timestampsJSONArray2 = jsonObject2.optJSONArray(
			"timestamps", new JSONArray());

		int count2 = timestampsJSONArray2.length();

		for (String key : keys) {
			JSONArray jsonArray1 = jsonObject1.optJSONArray(
				key, new JSONArray());

			while (jsonArray1.length() < count1) {
				jsonArray1.put(0);
			}

			JSONArray jsonArray2 = jsonObject2.optJSONArray(
				key, new JSONArray());

			while (jsonArray2.length() < count2) {
				jsonArray2.put(0);
			}

			jsonArray1.putAll(jsonArray2);

			jsonObject1.put(key, jsonArray1);
		}
	}

	private static final File _CI_SYSTEM_STATUS_REPORT_DIR;

	private static final int _DAYS_PER_WEEK = 7;

	private static final String[] _NODE_METRIC_NAMES = {
		"downstream_started_builds", "idle_nodes", "occupied_nodes",
		"offline_nodes", "online_nodes", "queued_builds", "timestamps",
		"top_level_started_builds"
	};

	private static final File _TESTRAY_LOGS_DIR;

	private static final File _TMP_BASE_DIR;

	private static final Properties _buildProperties;
	private static final Pattern _buildReportFilePathPattern = Pattern.compile(
		".*/?(?<dateString>\\d{4}-\\d{2})/test-\\d-(?<masterID>[\\w-]+)/.*");
	private static final List<String> _dateStrings = new ArrayList<>();
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(20, true);
	private static final HashMap<LocalDate, List<Result>> _results;

	private static class Result {

		public List<Long> getDownstreamDuration() {
			return _downstreamDurations;
		}

		public Long getTopLevelActiveDuration() {
			return _topLevelActiveDuration;
		}

		public Long getTopLevelDuration() {
			return _topLevelDuration;
		}

		public String getTopLevelResult() {
			return _topLevelResult;
		}

		public Date getTopLevelStartDate() {
			return _topLevelStartDate;
		}

		private Result(TopLevelBuildReport topLevelBuildReport) {
			_topLevelActiveDuration =
				topLevelBuildReport.getTopLevelActiveDuration();
			_topLevelDuration = topLevelBuildReport.getDuration();
			_topLevelResult = topLevelBuildReport.getResult();
			_topLevelStartDate = topLevelBuildReport.getStartDate();

			for (DownstreamBuildReport downstreamBuildReport :
					topLevelBuildReport.getDownstreamBuildReports()) {

				long downstreamDuration = downstreamBuildReport.getDuration();

				if (downstreamDuration <= 0L) {
					continue;
				}

				_downstreamDurations.add(downstreamDuration);
			}
		}

		private final List<Long> _downstreamDurations = new ArrayList<>();
		private final Long _topLevelActiveDuration;
		private final Long _topLevelDuration;
		private final String _topLevelResult;
		private final Date _topLevelStartDate;

	}

	static {
		_results = new HashMap<LocalDate, List<Result>>() {
			{
				LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

				for (int i = 0; i <= (_DAYS_PER_WEEK * 2); i++) {
					put(localDate.minusDays(i), new ArrayList<Result>());
				}
			}
		};

		for (LocalDate localDate : _results.keySet()) {
			String dateString = localDate.format(
				DateTimeFormatter.ofPattern("yyyy-MM"));

			if (_dateStrings.contains(dateString)) {
				continue;
			}

			_dateStrings.add(dateString);
		}

		_buildProperties = new Properties() {
			{
				try {
					putAll(JenkinsResultsParserUtil.getBuildProperties());
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		};

		_CI_SYSTEM_STATUS_REPORT_DIR = new File(
			_buildProperties.getProperty("ci.system.status.report.dir"));
		_TESTRAY_LOGS_DIR = new File(
			_buildProperties.getProperty(
				"google.cloud.bucket.local.dir[testray]"));
		_TMP_BASE_DIR = new File(
			_buildProperties.getProperty("archive.ci.build.data.tmp.dir"),
			"nodes");
	}

}