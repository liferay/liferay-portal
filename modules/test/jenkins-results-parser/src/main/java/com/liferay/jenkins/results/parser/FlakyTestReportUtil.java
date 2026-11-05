/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.history.HistoryFactory;
import com.liferay.jenkins.results.parser.history.JobHistory;
import com.liferay.jenkins.results.parser.history.TestrayJobHistory;
import com.liferay.jenkins.results.parser.testray.TestrayFactory;
import com.liferay.jenkins.results.parser.testray.TestrayRoutine;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import org.json.JSONObject;

/**
 * @author Calum Ragan
 */
public class FlakyTestReportUtil {

	public static void copyBaseReportFiles(String filePath) throws IOException {
		FileUtils.copyDirectory(_FLAKY_TEST_REPORT_DIR, new File(filePath));
	}

	public static void copyCIHistoryFiles(String filePath) throws IOException {
		FileUtils.copyDirectory(
			_CI_HISTORY_DIR, new File(filePath + "ci-history"));
	}

	public static File createCIHistoryJSONFile(
			String baseReportFilePath, String flakyTestReportFilePath)
		throws IOException {

		List<TestrayRoutine> testrayRoutines = new ArrayList<>();

		String upstreamBranchName = flakyTestReportFilePath.substring(
			flakyTestReportFilePath.lastIndexOf("-") + 1);

		String historyRoutineURLs = _buildProperties.getProperty(
			"test.history.routine.urls[" + upstreamBranchName + "]");

		for (String historyRoutineURL : historyRoutineURLs.split(",")) {
			testrayRoutines.add(
				TestrayFactory.newTestrayRoutine(historyRoutineURL));
		}

		JobHistory jobHistory = HistoryFactory.newJobHistory(
			Integer.parseInt(
				_buildProperties.getProperty(
					"test.history.test.count[" + upstreamBranchName + "]")),
			upstreamBranchName, testrayRoutines);

		if (!(jobHistory instanceof TestrayJobHistory)) {
			return null;
		}

		String ciHistoryJSONFilePath =
			baseReportFilePath + "ci-history.json.gz";

		TestrayJobHistory testrayJobHistory = (TestrayJobHistory)jobHistory;

		if (Boolean.parseBoolean(
				JenkinsResultsParserUtil.getProperty(
					_buildProperties, "test.history.validation.enabled",
					upstreamBranchName))) {

			testrayJobHistory.validate();
		}

		testrayJobHistory.writeCIHistoryJSONFile(ciHistoryJSONFilePath);

		String flakyTestDataFilePath =
			flakyTestReportFilePath + "/js/flaky-test-data.js";

		Files.deleteIfExists(Paths.get(flakyTestDataFilePath));

		testrayJobHistory.writeFlakyTestDataJavaScriptFile(
			flakyTestDataFilePath);

		CloudBucketUtil.copyGCPFile(
			CloudBucketUtil.GCP_BUCKET_PATH_JENKINS_CI_DATA + "/ci-history/" +
				upstreamBranchName + "/ci-history.json.gz",
			ciHistoryJSONFilePath);

		return new File(ciHistoryJSONFilePath);
	}

	public static void writeHTMLFile(
			File ciHistoryJSONFile, String reportsDirPath)
		throws IOException {

		JSONObject ciHistoryJSONObject = new JSONObject(
			JenkinsResultsParserUtil.read(ciHistoryJSONFile));

		File ciHistoryFile = new File(reportsDirPath, "ci-history/index.html");

		String ciHistoryFileContent = JenkinsResultsParserUtil.read(
			ciHistoryFile);

		JenkinsResultsParserUtil.write(
			ciHistoryFile,
			ciHistoryFileContent.replace(
				"const data = {}",
				"const data = " + ciHistoryJSONObject.toString()));
	}

	private static final File _CI_HISTORY_DIR;

	private static final File _FLAKY_TEST_REPORT_DIR;

	private static final Properties _buildProperties;

	static {
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

		_FLAKY_TEST_REPORT_DIR = new File(
			_buildProperties.getProperty("flaky.test.report.dir"));
		_CI_HISTORY_DIR = new File(
			_buildProperties.getProperty("ci.history.dir"));
	}

}