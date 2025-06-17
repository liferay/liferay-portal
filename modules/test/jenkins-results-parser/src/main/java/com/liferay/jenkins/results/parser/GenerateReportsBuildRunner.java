/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.metrics.BuildHistoryProcessor;
import com.liferay.jenkins.results.parser.metrics.BuildHistoryReport;
import com.liferay.jenkins.results.parser.testray.TestrayS3Bucket;
import com.liferay.jenkins.results.parser.testray.TestrayS3Object;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class GenerateReportsBuildRunner extends BaseBuildRunner<BuildData> {

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
		_validateBuildParameters();

		_generateReports();
	}

	@Override
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(new File(_TMP_BASE_DIR_PATH));
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to delete directory: " + _TMP_BASE_DIR_PATH);
		}

		try {
			_deleteStaleTestrayBuildReportJSONFiles();
		}
		catch (IOException | TimeoutException exception) {
			System.out.println(
				"Unable to delete stale build-report.json files");
		}

		super.tearDown();
	}

	public enum Report {

		BUILD_HISTORY("Build History"), CI_SYSTEM_HISTORY("CI System History"),
		CI_SYSTEM_STATUS("CI System Status"),
		PULL_REQUEST_HISTORY("Pull Request History"),
		RELEASE_HISTORY("Release History"),
		UPSTREAM_HISTORY("Upstream History"), UTILIZATION("Utilization");

		public String getDirName() {
			return _reportDirNames.get(_string);
		}

		@Override
		public String toString() {
			return _string;
		}

		private Report(String string) {
			_string = string;
		}

		private final String _string;

	}

	protected GenerateReportsBuildRunner(BuildData buildData) {
		super(buildData);
	}

	protected String getBuildParameter(String key) {
		BuildData buildData = getBuildData();

		return buildData.getBuildParameter(key);
	}

	private static String _getBuildProperty(String propertyName) {
		try {
			return JenkinsResultsParserUtil.getBuildProperty(propertyName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _archiveReport(String filePath) {
		_mergeHTMLFiles(filePath);

		JenkinsResultsParserUtil.rsync(
			null, _ARCHIVE_BASE_DIR_PATH + "/reports/" + _CURRENT_DATE_STRING,
			null, filePath);

		try {
			CloudBucketUtil.syncGCPFiles(
				_getGCPBucketBasePath() + "/reports",
				_ARCHIVE_BASE_DIR_PATH + "/reports");
		}
		catch (IOException ioException) {
			System.out.println("Unable to archive report: " + filePath);

			ioException.printStackTrace();
		}
	}

	private void _copyArchivedBuildData(
		long durationDays, String startDateString) {

		String[] dateStrings = JenkinsResultsParserUtil.getDateStrings(
			durationDays, LocalDate.parse(startDateString, _dateTimeFormatter));

		File archivedDataDir = new File(_ARCHIVE_BASE_DIR_PATH + "/data");

		List<Callable<Void>> callables = new ArrayList<>();

		for (final String dateString : dateStrings) {
			callables.add(
				new Callable<Void>() {

					@Override
					public Void call() {
						File archiveFile = new File(
							archivedDataDir, dateString + ".tar.gz");

						File unarchivedDir = new File(
							_TMP_BASE_DIR_PATH, "/builds/" + dateString);

						if (archiveFile.exists() && !unarchivedDir.exists()) {
							System.out.println(
								"Extracting " + archiveFile + " to " +
									unarchivedDir);

							JenkinsResultsParserUtil.unTarGzip(
								archiveFile, unarchivedDir);
						}

						return null;
					}

				});
		}

		ParallelExecutor<Void> parallelExecutor = new ParallelExecutor<>(
			callables, BuildHistoryProcessor.getExecutorService(),
			"_copyArchivedBuildData");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private void _copyArchivedNodeData(
			long durationDays, String startDateString)
		throws IOException {

		String[] dateStrings = JenkinsResultsParserUtil.getDateStrings(
			durationDays, LocalDate.parse(startDateString, _dateTimeFormatter));

		File dataArchiveDir = new File(
			_getBuildProperty("google.cloud.bucket.local.dir[jenkins]"),
			"data");

		File baseArchiveDir = dataArchiveDir.getParentFile();

		for (String dateString : dateStrings) {
			File nodeDataArchiveFile = new File(
				baseArchiveDir, "reports/" + dateString + "/node.json");

			File nodeDataFile = new File(
				_TMP_BASE_DIR_PATH + "/nodes/" + dateString, "node.json");

			if (nodeDataArchiveFile.exists()) {
				FileUtils.copyFile(nodeDataArchiveFile, nodeDataFile);
			}
		}
	}

	private void _deleteEmptyDirs(File dir) {
		if (!dir.isDirectory()) {
			return;
		}

		for (File file : dir.listFiles()) {
			_deleteEmptyDirs(file);
		}

		File[] files = dir.listFiles();

		if (files.length == 0) {
			boolean deleted = dir.delete();

			if (deleted) {
				System.out.println(
					"Deleted empty directory: " + dir.getAbsolutePath());
			}
			else {
				System.out.println(
					"Unable to delete empty directory: " +
						dir.getAbsolutePath());
			}
		}
	}

	private void _deleteStaleTestrayBuildReportJSONFiles()
		throws IOException, TimeoutException {

		File testrayResultsBucketLocalDir = new File(
			_getBuildProperty("google.cloud.bucket.local.dir[testray]"));

		List<File> buildReportFiles = JenkinsResultsParserUtil.findFiles(
			testrayResultsBucketLocalDir, Pattern.quote("build-report.json"));

		for (File buildReportFile : buildReportFiles) {
			long millisSinceLastModification =
				System.currentTimeMillis() - buildReportFile.lastModified();

			if ((millisSinceLastModification > TimeUnit.DAYS.toMillis(60)) &&
				(buildReportFile.delete() == false)) {

				throw new RuntimeException(
					"Unable to delete file " + buildReportFile);
			}
		}

		_deleteEmptyDirs(testrayResultsBucketLocalDir);
	}

	private void _downloadTestrayBuildReportJSONFiles() {
		LocalDate currentLocalDate = LocalDate.now();

		String currentMonthString = currentLocalDate.format(
			DateTimeFormatter.ofPattern("yyyy-MM"));

		String startMonthString = null;

		LocalDate startLocalDate = currentLocalDate.minusDays(15);

		if (currentLocalDate.getMonth() != startLocalDate.getMonth()) {
			startMonthString = startLocalDate.format(
				DateTimeFormatter.ofPattern("yyyy-MM"));
		}

		TestrayS3Bucket testrayS3Bucket = TestrayS3Bucket.getInstance();

		List<String> keys = new ArrayList<>();

		String jobName = "test-portal-acceptance-pullrequest(master)";

		for (int i = 1; i <= 40; i++) {
			String jenkinsMasterName = "test-1-" + i;

			keys.addAll(
				_getTestrayBucketBuildReportJSONFilePaths(
					currentMonthString, jenkinsMasterName, jobName));

			if (startMonthString != null) {
				keys.addAll(
					_getTestrayBucketBuildReportJSONFilePaths(
						startMonthString, jenkinsMasterName, jobName));
			}
		}

		try {
			File testrayResultsBucketLocalDir = new File(
				_getBuildProperty("google.cloud.bucket.local.dir[testray]"));

			testrayS3Bucket.downloadTestrayS3Objects(
				testrayResultsBucketLocalDir, keys);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private void _generateBuildHistoryReport(String reportName)
		throws IOException {

		long reportDurationDays = _getReportDurationDays(reportName);
		String startDateString = _getStartDateString(reportName);

		_copyArchivedBuildData(reportDurationDays, startDateString);

		String filePath = _getReportFilePath(reportName);

		BuildHistoryReport aggregateBuildHistoryReport =
			BuildHistoryReport.newAggregateReport(
				reportDurationDays, new File(filePath), startDateString);

		aggregateBuildHistoryReport.write();

		_updateReport(filePath);

		_archiveReport(filePath);
	}

	private void _generateCISystemHistoryReport(String reportName)
		throws IOException {

		String filePath = _getReportFilePath(reportName);

		_downloadTestrayBuildReportJSONFiles();

		CISystemHistoryReportUtil.generateCISystemHistoryReport(
			filePath, _getBuildProperty("ci.system.history.report.job.name"),
			_getBuildProperty("ci.system.history.report.test.suite.name"));

		_updateReport(filePath);
	}

	private void _generateCISystemStatusReport(String reportName)
		throws IOException {

		String filePath = _getReportFilePath(reportName);

		CISystemStatusReportUtil.copyBaseReportFiles(filePath);

		Files.deleteIfExists(Paths.get(filePath, "js/testray-data.js"));

		long reportDurationDays = _getReportDurationDays(reportName);

		_copyArchivedNodeData(
			_getReportDurationDays(reportName),
			_getStartDateString(reportDurationDays - 1));

		CISystemStatusReportUtil.writeJenkinsDataJavaScriptFile(
			filePath + "/js/jenkins-data.js");

		String testrayDataJSGCPFilePath = JenkinsResultsParserUtil.combine(
			_getGCPBucketBasePath(), "/data/", _getReportDirName(reportName),
			"/testray-data.js");

		if (_isGCPReportFileStale(testrayDataJSGCPFilePath, 60)) {
			_downloadTestrayBuildReportJSONFiles();

			CISystemStatusReportUtil.writeTestrayDataJavaScriptFile(
				filePath + "/js/testray-data.js",
				_getBuildProperty("ci.system.status.report.job.name"),
				_getBuildProperty("ci.system.status.report.test.suite.name"));

			CloudBucketUtil.copyGCPFile(
				JenkinsResultsParserUtil.combine(
					_getGCPBucketBasePath(), "/data/",
					_getReportDirName(reportName), "/testray-data.js"),
				filePath + "/js/testray-data.js");
		}

		String testrayDataJSFilePath = filePath + "/js/testray-data.js";

		File testrayDataJSFile = new File(testrayDataJSFilePath);

		if (!testrayDataJSFile.exists()) {
			CloudBucketUtil.copyGCPFile(
				filePath + "/js/testray-data.js", testrayDataJSGCPFilePath);
		}

		_updateReport(filePath);

		_updateNodeDataFile(filePath);

		_archiveReport(filePath);
	}

	private void _generatePullRequestReport(String reportName)
		throws IOException {

		long reportDurationDays = _getReportDurationDays(reportName);
		String startDateString = _getStartDateString(reportName);

		_copyArchivedBuildData(reportDurationDays, startDateString);

		String filePath = _getReportFilePath(reportName);

		BuildHistoryReport testSuiteBuildHistoryReport =
			BuildHistoryReport.newPullRequestTestSuiteReport(
				reportDurationDays, new File(filePath), startDateString);

		testSuiteBuildHistoryReport.write();

		_updateReport(filePath);

		_archiveReport(filePath);
	}

	private void _generateReleaseReport(String reportName) throws IOException {
		long reportDurationDays = _getReportDurationDays(reportName);
		String startDateString = _getStartDateString(reportName);

		_copyArchivedBuildData(reportDurationDays, startDateString);

		String filePath = _getReportFilePath(reportName);

		BuildHistoryReport testSuiteBuildHistoryReport =
			BuildHistoryReport.newReleaseTestSuiteReport(
				reportDurationDays, new File(filePath), startDateString);

		testSuiteBuildHistoryReport.write();

		_updateReport(filePath);

		_archiveReport(filePath);
	}

	private void _generateReports() {
		String[] reportNames = _getReportNames();

		if (reportNames == null) {
			return;
		}

		try {
			CloudBucketUtil.syncGCPFiles(
				_ARCHIVE_BASE_DIR_PATH + "/reports",
				_getGCPBucketBasePath() + "/reports");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		StringBuilder sb = new StringBuilder();

		for (String reportName : reportNames) {
			try {
				if (reportName.equals(Report.BUILD_HISTORY.toString())) {
					_generateBuildHistoryReport(reportName);
				}

				if (reportName.equals(Report.CI_SYSTEM_HISTORY.toString())) {
					_generateCISystemHistoryReport(reportName);
				}

				if (reportName.equals(Report.CI_SYSTEM_STATUS.toString())) {
					_generateCISystemStatusReport(reportName);
				}

				if (reportName.equals(Report.PULL_REQUEST_HISTORY.toString())) {
					_generatePullRequestReport(reportName);
				}

				if (reportName.equals(Report.RELEASE_HISTORY.toString())) {
					_generateReleaseReport(reportName);
				}

				if (reportName.equals(Report.UPSTREAM_HISTORY.toString())) {
					_generateUpstreamReport(reportName);
				}

				if (reportName.equals(Report.UTILIZATION.toString())) {
					_generateUtilizationReport(reportName);
				}
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to write " + reportName + " to " +
						_getReportFilePath(reportName));

				BuildData buildData = getBuildData();

				NotificationUtil.sendSlackNotification(
					buildData.getBuildURL() + " <@U04GTH03Q>",
					"ci-notifications",
					"Unable to generate " + reportName + " report");

				continue;
			}

			sb.append("<a href=\"");
			sb.append("http://test-1-0.liferay.com/userContent/reports/");

			sb.append(_getReportDirName(reportName));

			sb.append("/index.html\">");

			sb.append(reportName);

			sb.append(" Report</a><br />");
		}

		BuildData buildData = getBuildData();

		buildData.setBuildDescription(sb.toString());

		updateBuildDescription();
	}

	private void _generateUpstreamReport(String reportName) throws IOException {
		long reportDurationDays = _getReportDurationDays(reportName);
		String startDateString = _getStartDateString(reportName);

		_copyArchivedBuildData(reportDurationDays, startDateString);

		String filePath = _getReportFilePath(reportName);

		BuildHistoryReport testSuiteBuildHistoryReport =
			BuildHistoryReport.newUpstreamTestSuiteReport(
				reportDurationDays, new File(filePath), startDateString);

		testSuiteBuildHistoryReport.write();

		_updateReport(filePath);

		_archiveReport(filePath);
	}

	private void _generateUtilizationReport(String reportName)
		throws IOException {

		String startDateString = _getStartDateString(reportName);

		LocalDate localDate = LocalDate.parse(
			startDateString, _dateTimeFormatter);

		DayOfWeek dayOfWeek = localDate.getDayOfWeek();

		while (dayOfWeek.getValue() != 1) {
			localDate = localDate.minusDays(1);

			dayOfWeek = localDate.getDayOfWeek();
		}

		startDateString = localDate.format(_dateTimeFormatter);

		long reportDurationDays = _getReportDurationDays(reportName);

		_copyArchivedBuildData(reportDurationDays, startDateString);

		String filePath = _getReportFilePath(reportName);

		BuildHistoryReport utilizationReport =
			BuildHistoryReport.newUtilizationReport(
				reportDurationDays, new File(filePath), startDateString);

		utilizationReport.write();

		_updateReport(filePath);

		_archiveReport(filePath);
	}

	private String _getGCPBucketBasePath() {
		if (JenkinsResultsParserUtil.isCloudCINode()) {
			return CloudBucketUtil.GCP_BUCKET_PATH_JENKINS_CI_DATA + "/aws";
		}

		return CloudBucketUtil.GCP_BUCKET_PATH_JENKINS_CI_DATA;
	}

	private String _getReportDirName(String reportName) {
		return _reportDirNames.get(reportName);
	}

	private long _getReportDurationDays(String reportName) {
		String reportDurationDays = _getBuildProperty(
			JenkinsResultsParserUtil.combine(
				"report.duration.days[", reportName, "]"));

		if (reportDurationDays == null) {
			reportDurationDays = _getBuildProperty("report.duration.days");
		}

		return Long.parseLong(reportDurationDays);
	}

	private String _getReportFilePath(String reportName) {
		return _TMP_BASE_DIR_PATH + "/reports/" + _getReportDirName(reportName);
	}

	private String[] _getReportNames() {
		String buildParameter = getBuildParameter("REPORT_NAMES");

		if (buildParameter == null) {
			return null;
		}

		return buildParameter.split("\\s*,\\s*");
	}

	private String _getStartDateString(long daysAgo) {
		LocalDate localDate = LocalDate.parse(
			_CURRENT_DATE_STRING, _dateTimeFormatter);

		localDate = localDate.minusDays(daysAgo);

		return localDate.format(_dateTimeFormatter);
	}

	private String _getStartDateString(String reportName) {
		return _getStartDateString(_getReportDurationDays(reportName));
	}

	private List<String> _getTestrayBucketBuildReportJSONFilePaths(
		String monthString, String jenkinsMasterName, String jobName) {

		List<String> filePaths = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		sb.append(monthString);
		sb.append("/");
		sb.append(jenkinsMasterName);
		sb.append("/");
		sb.append(jobName);
		sb.append("/");

		TestrayS3Bucket testrayS3Bucket = TestrayS3Bucket.getInstance();

		for (TestrayS3Object testrayS3Object :
				testrayS3Bucket.getTestrayS3Objects(sb.toString())) {

			String filePath = testrayS3Object.getKey() + "build-report.json.gz";

			filePaths.add(filePath);
		}

		return filePaths;
	}

	private boolean _isGCPReportFileStale(String path, long ageMinutes) {
		JSONArray jsonArray = null;

		try {
			jsonArray = new JSONArray(CloudBucketUtil.listGCPFiles(path, "-j"));
		}
		catch (IOException | TimeoutException exception) {
			System.out.println("Unable to get age of " + path);

			return false;
		}

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		JSONObject metadataJSONObject = jsonObject.getJSONObject("metadata");

		String timeCreatedString = metadataJSONObject.getString("timeCreated");

		Duration duration = Duration.between(
			Instant.from(
				DateTimeFormatter.ISO_INSTANT.parse(timeCreatedString)),
			Instant.now());

		System.out.println(
			path + " was last updated " + duration.toMinutes() +
				" minutes ago");

		if (duration.toMinutes() >= ageMinutes) {
			return true;
		}

		return false;
	}

	private void _mergeHTMLFiles(String reportDirPath) {
		_mergeHTMLFiles(reportDirPath, true);
	}

	private void _mergeHTMLFiles(
		String reportDirPath, boolean deleteFrontendFiles) {

		File reportDir = new File(reportDirPath);

		File reportFile = new File(reportDir, "index.html");

		if (!reportFile.exists()) {
			return;
		}

		try {
			String reportFileContent = JenkinsResultsParserUtil.read(
				reportFile);

			String newReportFileContent = reportFileContent;

			for (String line : reportFileContent.split("\n")) {
				Matcher matcher = _scriptElementPattern.matcher(line);

				if (matcher.find()) {
					String srcValue = matcher.group("srcValue");

					if (srcValue.startsWith("http://") ||
						srcValue.startsWith("https://")) {

						continue;
					}

					File javaScriptFile = new File(reportDir, srcValue);

					if (!javaScriptFile.exists()) {
						continue;
					}

					String scriptElementContent =
						"<script>" +
							JenkinsResultsParserUtil.read(javaScriptFile) +
								"</script>";

					newReportFileContent = newReportFileContent.replace(
						line, scriptElementContent);

					if (deleteFrontendFiles) {
						javaScriptFile.delete();
					}

					continue;
				}

				matcher = _linkElementPattern.matcher(line);

				if (matcher.find()) {
					String hrefValue = matcher.group("hrefValue");

					if (hrefValue.startsWith("http://") ||
						hrefValue.startsWith("https://")) {

						continue;
					}

					File cssFile = new File(reportDir, hrefValue);

					if (!cssFile.exists()) {
						continue;
					}

					String styleElementContent =
						"<style>" + JenkinsResultsParserUtil.read(cssFile) +
							"</style>";

					newReportFileContent = newReportFileContent.replace(
						line, styleElementContent);

					if (deleteFrontendFiles) {
						cssFile.delete();
					}
				}
			}

			if (!reportFileContent.equals(newReportFileContent)) {
				JenkinsResultsParserUtil.write(
					reportFile, newReportFileContent);
			}
		}
		catch (IOException ioException) {
			System.out.println("Unable to merge files in: " + reportDirPath);

			ioException.printStackTrace();
		}
	}

	private void _updateNodeDataFile(String filePath) throws IOException {
		File dataArchiveDir = new File(
			_getBuildProperty("google.cloud.bucket.local.dir[jenkins]"),
			"data");

		File baseArchiveDir = dataArchiveDir.getParentFile();

		File nodeDataArchiveFile = new File(
			baseArchiveDir, "reports/" + _CURRENT_DATE_STRING + "/node.json");

		File nodeDataFile = new File(filePath, "node.json");

		if (nodeDataArchiveFile.exists()) {
			FileUtils.copyFile(nodeDataArchiveFile, nodeDataFile);
		}

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance("test-1");

		jenkinsCohort.writeNodeDataJSONFile(nodeDataFile.getPath());

		FileUtils.copyFile(nodeDataFile, nodeDataArchiveFile);

		FileUtils.delete(nodeDataFile);
	}

	private void _updateReport(String filePath) {
		_mergeHTMLFiles(filePath, false);

		try {
			JenkinsResultsParserUtil.rsync(
				"test-1-0", _REPORT_RSYNC_DESTINATION_DIR_PATH, null, filePath);
		}
		catch (Exception exception) {
			System.out.println(
				"Unable to rsync report " + filePath + " to test-1-0:" +
					_REPORT_RSYNC_DESTINATION_DIR_PATH);

			exception.printStackTrace();
		}
	}

	private void _validateBuildParameters() {
		String[] reportNames = _getReportNames();

		if (reportNames == null) {
			throw new RuntimeException("REPORT_NAMES parameter must be set");
		}

		for (String reportName : reportNames) {
			if (!_validReportNames.contains(reportName)) {
				throw new RuntimeException(
					"REPORT_NAMES parameter contains invalid report type: " +
						reportName);
			}
		}
	}

	private static final String _ARCHIVE_BASE_DIR_PATH = _getBuildProperty(
		"google.cloud.bucket.local.dir[jenkins]");

	private static final String _CURRENT_DATE_STRING;

	private static final String _LINK_ELEMENT_REGEX =
		"(?<linkElement><link.*href=\\\"(?<hrefValue>.*?)\\\".*\\/>)";

	private static final String _REPORT_RSYNC_DESTINATION_DIR_PATH =
		"/opt/java/jenkins/userContent/reports/";

	private static final String _SCRIPT_ELEMENT_REGEX =
		"(?<scriptElement><script.*src=\\\"(?<srcValue>.*?)\\\".*<\\/script>)";

	private static final String _TMP_BASE_DIR_PATH = _getBuildProperty(
		"archive.ci.build.data.tmp.dir");

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final Pattern _linkElementPattern = Pattern.compile(
		_LINK_ELEMENT_REGEX);
	private static final Map<String, String> _reportDirNames =
		new HashMap<String, String>() {
			{
				put(Report.BUILD_HISTORY.toString(), "build-history-report");
				put(Report.CI_SYSTEM_HISTORY.toString(), "ci-system-history");
				put(Report.CI_SYSTEM_STATUS.toString(), "ci-system-status");
				put(
					Report.PULL_REQUEST_HISTORY.toString(),
					"pull-request-report");
				put(Report.RELEASE_HISTORY.toString(), "release-report");
				put(Report.UPSTREAM_HISTORY.toString(), "upstream-report");
				put(Report.UTILIZATION.toString(), "utilization-report");
			}
		};
	private static final Pattern _scriptElementPattern = Pattern.compile(
		_SCRIPT_ELEMENT_REGEX);
	private static final List<String> _validReportNames = Arrays.asList(
		Report.BUILD_HISTORY.toString(), Report.CI_SYSTEM_HISTORY.toString(),
		Report.CI_SYSTEM_STATUS.toString(),
		Report.PULL_REQUEST_HISTORY.toString(),
		Report.RELEASE_HISTORY.toString(), Report.UPSTREAM_HISTORY.toString(),
		Report.UTILIZATION.toString());

	static {
		Instant instant = Instant.now();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		_CURRENT_DATE_STRING = zonedDateTime.format(_dateTimeFormatter);
	}

	private Workspace _workspace;

}