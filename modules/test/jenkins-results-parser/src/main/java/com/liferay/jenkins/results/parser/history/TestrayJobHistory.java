/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.TestrayTopLevelBuildReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.testray.TestrayBuild;
import com.liferay.jenkins.results.parser.testray.TestrayProject;
import com.liferay.jenkins.results.parser.testray.TestrayRoutine;
import com.liferay.jenkins.results.parser.testray.TestrayServer;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayJobHistory extends BaseJobHistory {

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		JSONArray batchesJSONArray = new JSONArray();

		for (BatchHistory batchHistory : getBatchHistories()) {
			batchesJSONArray.put(batchHistory.getJSONObject());
		}

		jsonObject.put(
			"batches", batchesJSONArray
		).put(
			"testray_url", String.valueOf(getTestrayURL())
		).put(
			"upstream_branch_name", getPortalUpstreamBranchName()
		);

		return jsonObject;
	}

	public TestrayProject getTestrayProject() {
		if (_testrayRoutines.isEmpty()) {
			return null;
		}

		TestrayRoutine testrayRoutine = _testrayRoutines.get(0);

		return testrayRoutine.getTestrayProject();
	}

	@Override
	public URL getTestrayURL() {
		if (_testrayURL != null) {
			return _testrayURL;
		}

		if ((_testrayRoutines == null) || _testrayRoutines.isEmpty()) {
			return null;
		}

		TestrayRoutine testrayRoutine = _testrayRoutines.get(0);

		TestrayServer testrayServer = testrayRoutine.getTestrayServer();

		if (testrayServer == null) {
			return null;
		}

		_testrayURL = testrayServer.getURL();

		return _testrayURL;
	}

	public void populate() {
		if (_populated) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		if ((_testrayRoutines == null) || _testrayRoutines.isEmpty()) {
			return;
		}

		for (TestrayRoutine testrayRoutine : _testrayRoutines) {
			TestrayBuild latestTestrayBuild = null;

			List<TestrayBuild> testrayBuilds = testrayRoutine.getTestrayBuilds(
				_maxBuildCount);

			if (testrayBuilds.size() > _maxBuildCount) {
				testrayBuilds = testrayBuilds.subList(0, _maxBuildCount);
			}

			for (TestrayBuild testrayBuild : testrayBuilds) {
				if (!Objects.equals(testrayBuild.getImportStatus(), "DONE")) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"SKIPPED: ",
							String.valueOf(testrayBuild.getURL())));

					continue;
				}

				TopLevelBuildReport topLevelBuildReport =
					testrayBuild.getTopLevelBuildReport();

				if ((topLevelBuildReport == null) ||
					JenkinsResultsParserUtil.isNullOrEmpty(
						topLevelBuildReport.getResult())) {

					continue;
				}

				boolean latestBuild = false;

				if (latestTestrayBuild == null) {
					latestTestrayBuild = testrayBuild;

					latestBuild = true;

					_latestTopLevelBuildReports.put(
						testrayRoutine.getId(), topLevelBuildReport);
				}

				for (DownstreamBuildReport downstreamBuildReport :
						topLevelBuildReport.getDownstreamBuildReports()) {

					String batchName = downstreamBuildReport.getBatchName();

					BatchHistory batchHistory = getBatchHistory(batchName);

					if (batchHistory == null) {
						batchHistory = HistoryFactory.newBatchHistory(
							batchName, this, null);

						addBatchHistory(batchHistory);
					}

					if (!(batchHistory instanceof TestrayBatchHistory)) {
						continue;
					}

					TestrayBatchHistory testrayBatchHistory =
						(TestrayBatchHistory)batchHistory;

					testrayBatchHistory.addBuildReport(
						downstreamBuildReport, latestBuild);
				}
			}
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Test history map populated in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start)));

		_populated = true;
	}

	public void validate() {
		if (!_populated) {
			populate();
		}

		Map<String, Integer> testCounts = new HashMap<>();

		for (BatchHistory batchHistory : getBatchHistories()) {
			List<TestClassHistory> testClassHistories =
				batchHistory.getTestClassHistories();

			testCounts.put(
				batchHistory.getBatchName(), testClassHistories.size());
		}

		int testsCountMinimum = _getTestsCountMinimum();

		for (TestrayRoutine testrayRoutine : _testrayRoutines) {
			TopLevelBuildReport topLevelBuildReport =
				_latestTopLevelBuildReports.get(testrayRoutine.getId());

			if (topLevelBuildReport == null) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to find a build report for routine ",
						String.valueOf(testrayRoutine.getURL())));
			}

			Map<String, Integer> expectedTestCounts = _getExpectedTestCounts(
				topLevelBuildReport);

			if (expectedTestCounts == null) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to find a build database for ",
						String.valueOf(topLevelBuildReport.getBuildURL())));
			}

			for (Map.Entry<String, Integer> entry :
					expectedTestCounts.entrySet()) {

				String batchName = entry.getKey();

				if (_isExcludedBatchName(batchName)) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Skipping CI history validation for batch ",
							batchName));

					continue;
				}

				Integer testsCount = _getTestsCount(batchName, testCounts);

				if (testsCount == null) {
					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Batch ", batchName,
							" is missing from the CI history in ",
							String.valueOf(topLevelBuildReport.getBuildURL())));
				}

				int expectedTestsCount = entry.getValue();

				if (expectedTestsCount <= testsCountMinimum) {
					continue;
				}

				int testsCountThreshold = _getTestsCountThreshold(batchName);

				if ((testsCount * 100L) >=
						(expectedTestsCount * (long)testsCountThreshold)) {

					continue;
				}

				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Batch ", batchName, " has ",
						String.valueOf(testsCount), " of ",
						String.valueOf(expectedTestsCount),
						" expected tests, below the ",
						String.valueOf(testsCountThreshold), "% threshold in ",
						String.valueOf(topLevelBuildReport.getBuildURL())));
			}
		}
	}

	public void writeCIHistoryJSONFile(String filePath) throws IOException {
		if (!_populated) {
			populate();
		}

		File file = new File(filePath);

		File tempFile = new File(
			file.getParentFile(),
			JenkinsResultsParserUtil.getDistinctTimeStamp());

		try {
			JenkinsResultsParserUtil.write(
				tempFile, String.valueOf(getJSONObject()));

			JenkinsResultsParserUtil.gzip(tempFile, file);
		}
		finally {
			if (tempFile.exists()) {
				JenkinsResultsParserUtil.delete(tempFile);
			}
		}
	}

	public void writeFlakyTestDataJavaScriptFile(String filePath)
		throws IOException {

		if (!_populated) {
			populate();
		}

		JSONArray flakyTestDataJSONArray = new JSONArray();

		flakyTestDataJSONArray.put(
			new String[] {"Name", "Batch Type", "Results", "Status Changes"});

		for (BatchHistory batchHistory : getBatchHistories()) {
			for (TestClassHistory testClassHistory :
					batchHistory.getTestClassHistories()) {

				if (!testClassHistory.isFlaky()) {
					continue;
				}

				JSONArray jsonArray = new JSONArray();

				jsonArray.put(testClassHistory.getTestClassName());

				jsonArray.put(testClassHistory.getBatchName());

				JSONArray statusesJSONArray = new JSONArray();

				if (testClassHistory instanceof TestrayTestClassHistory) {
					TestrayTestClassHistory testrayTestClassHistory =
						(TestrayTestClassHistory)testClassHistory;

					for (TestClassReport testClassReport :
							testrayTestClassHistory.getTestClassReports()) {

						JSONArray statusJSONArray = new JSONArray();

						statusJSONArray.put(
							_fixStatus(testClassReport.getStatus()));

						DownstreamBuildReport downstreamBuildReport =
							testClassReport.getDownstreamBuildReport();

						statusJSONArray.put(
							downstreamBuildReport.getBuildURL());

						statusesJSONArray.put(statusJSONArray);
					}
				}

				jsonArray.put(statusesJSONArray);

				jsonArray.put(testClassHistory.getStatusChanges());

				flakyTestDataJSONArray.put(jsonArray);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("var flakyTestData = ");
		sb.append(flakyTestDataJSONArray);
		sb.append(";\nvar flakyTestDataGeneratedDate = new Date(");
		sb.append(JenkinsResultsParserUtil.getCurrentTimeMillis());
		sb.append(");\n");

		JSONArray testrayRoutinesJSONArray = new JSONArray();

		if ((_testrayRoutines != null) && !_testrayRoutines.isEmpty()) {
			for (TestrayRoutine testrayRoutine : _testrayRoutines) {
				JSONObject testrayRoutineJSONObject = new JSONObject();

				testrayRoutineJSONObject.put(
					"name", testrayRoutine.getName()
				).put(
					"url", testrayRoutine.getURL()
				);

				testrayRoutinesJSONArray.put(testrayRoutineJSONObject);
			}
		}

		sb.append("var testrayRoutines = ");
		sb.append(testrayRoutinesJSONArray);
		sb.append(";");

		JenkinsResultsParserUtil.write(filePath, sb.toString());
	}

	protected static Map<String, Integer> getExpectedTestCounts(
		JSONObject buildDatabaseJSONObject) {

		Map<String, Integer> expectedTestCounts = new HashMap<>();

		JSONObject jobsJSONObject = buildDatabaseJSONObject.optJSONObject(
			"jobs");

		if (jobsJSONObject == null) {
			return expectedTestCounts;
		}

		Map<String, Set<String>> batchTestClassNames = new HashMap<>();

		for (String jobKey : jobsJSONObject.keySet()) {
			JSONObject jobJSONObject = jobsJSONObject.getJSONObject(jobKey);

			JSONArray batchesJSONArray = jobJSONObject.optJSONArray("batches");

			if (batchesJSONArray == null) {
				continue;
			}

			for (int i = 0; i < batchesJSONArray.length(); i++) {
				JSONObject batchJSONObject = batchesJSONArray.getJSONObject(i);

				Set<String> testClassNames =
					batchTestClassNames.computeIfAbsent(
						batchJSONObject.getString("batch_name"),
						batchName -> new HashSet<>());

				JSONArray segmentsJSONArray = batchJSONObject.optJSONArray(
					"segments");

				if (segmentsJSONArray == null) {
					continue;
				}

				for (int j = 0; j < segmentsJSONArray.length(); j++) {
					JSONObject segmentJSONObject =
						segmentsJSONArray.getJSONObject(j);

					_addTestClassNames(
						segmentJSONObject.optJSONArray("axes"), testClassNames);
				}
			}
		}

		for (Map.Entry<String, Set<String>> entry :
				batchTestClassNames.entrySet()) {

			Set<String> testClassNames = entry.getValue();

			expectedTestCounts.put(entry.getKey(), testClassNames.size());
		}

		return expectedTestCounts;
	}

	protected TestrayJobHistory(
		int maxBuildCount, String portalUpstreamBranchName,
		List<TestrayRoutine> testrayRoutines) {

		super(portalUpstreamBranchName);

		_maxBuildCount = maxBuildCount;
		_testrayRoutines = testrayRoutines;
	}

	private static void _addTestClassNames(
		JSONArray axesJSONArray, Set<String> testClassNames) {

		if (axesJSONArray == null) {
			return;
		}

		for (int i = 0; i < axesJSONArray.length(); i++) {
			JSONObject axisJSONObject = axesJSONArray.getJSONObject(i);

			_addTestClassNames(
				testClassNames, axisJSONObject.optJSONArray("test_classes"));

			JSONArray testTasksJSONArray = axisJSONObject.optJSONArray(
				"test_tasks");

			if (testTasksJSONArray == null) {
				continue;
			}

			for (int j = 0; j < testTasksJSONArray.length(); j++) {
				JSONObject testTaskJSONObject =
					testTasksJSONArray.getJSONObject(j);

				_addTestClassNames(
					testClassNames,
					testTaskJSONObject.optJSONArray("test_classes"));
			}
		}
	}

	private static void _addTestClassNames(
		Set<String> testClassNames, JSONArray testClassesJSONArray) {

		if (testClassesJSONArray == null) {
			return;
		}

		for (int i = 0; i < testClassesJSONArray.length(); i++) {
			JSONObject testClassJSONObject = testClassesJSONArray.getJSONObject(
				i);

			String testClassName = testClassJSONObject.optString("name");

			if (JenkinsResultsParserUtil.isNullOrEmpty(testClassName)) {
				testClassName = testClassJSONObject.optString("file");
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(testClassName)) {
				continue;
			}

			testClassNames.add(testClassName);
		}
	}

	private String _fixStatus(String status) {
		status = status.replace("FIXED", "PASSED");
		status = status.replace("REGRESSION", "FAILED");

		return status;
	}

	private List<String> _getExcludedBatchNames() {
		if (_excludedBatchNames != null) {
			return _excludedBatchNames;
		}

		List<String> excludedBatchNames = new ArrayList<>();

		try {
			String propertyValue = JenkinsResultsParserUtil.getBuildProperty(
				"test.history.batch.names.excluded",
				getPortalUpstreamBranchName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {
				return excludedBatchNames;
			}

			for (String batchName : propertyValue.split(",")) {
				batchName = batchName.trim();

				if (batchName.isEmpty()) {
					continue;
				}

				excludedBatchNames.add(batchName);
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		_excludedBatchNames = excludedBatchNames;

		return _excludedBatchNames;
	}

	private Map<String, Integer> _getExpectedTestCounts(
		TopLevelBuildReport topLevelBuildReport) {

		String buildURL = String.valueOf(topLevelBuildReport.getBuildURL());

		Map<String, Integer> expectedTestCounts = _expectedTestCountsMap.get(
			buildURL);

		if (expectedTestCounts != null) {
			return expectedTestCounts;
		}

		if (!(topLevelBuildReport instanceof TestrayTopLevelBuildReport)) {
			return null;
		}

		TestrayTopLevelBuildReport testrayTopLevelBuildReport =
			(TestrayTopLevelBuildReport)topLevelBuildReport;

		JSONObject buildDatabaseJSONObject =
			testrayTopLevelBuildReport.getBuildDatabaseJSONObject();

		if (buildDatabaseJSONObject == null) {
			return null;
		}

		expectedTestCounts = getExpectedTestCounts(buildDatabaseJSONObject);

		_expectedTestCountsMap.put(buildURL, expectedTestCounts);

		return expectedTestCounts;
	}

	private int _getIntegerBuildProperty(String propertyName, String... opts) {
		try {
			String propertyValue = JenkinsResultsParserUtil.getBuildProperty(
				propertyName, opts);

			if (JenkinsResultsParserUtil.isInteger(propertyValue)) {
				return Integer.parseInt(propertyValue);
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return -1;
	}

	private Integer _getTestsCount(
		String batchName, Map<String, Integer> testCounts) {

		Integer testsCount = testCounts.get(batchName);

		if ((testsCount != null) || !batchName.endsWith("_stable")) {
			return testsCount;
		}

		return testCounts.get(batchName.substring(0, batchName.length() - 7));
	}

	private int _getTestsCountMinimum() {
		int testsCountMinimum = _getIntegerBuildProperty(
			"test.history.batch.test.count.minimum",
			getPortalUpstreamBranchName());

		if (testsCountMinimum > 0) {
			return testsCountMinimum;
		}

		return _TESTS_COUNT_MINIMUM_DEFAULT;
	}

	private int _getTestsCountThreshold(String batchName) {
		int testsCountThreshold = _getIntegerBuildProperty(
			"test.history.batch.test.count.threshold", batchName,
			getPortalUpstreamBranchName());

		if (testsCountThreshold > 0) {
			return testsCountThreshold;
		}

		return _TESTS_COUNT_THRESHOLD_DEFAULT;
	}

	private boolean _isExcludedBatchName(String batchName) {
		List<String> excludedBatchNames = _getExcludedBatchNames();

		if (excludedBatchNames.contains(batchName) ||
			(batchName.endsWith("_stable") &&
			 excludedBatchNames.contains(
				 batchName.substring(0, batchName.length() - 7)))) {

			return true;
		}

		return false;
	}

	private static final int _TESTS_COUNT_MINIMUM_DEFAULT = 25;

	private static final int _TESTS_COUNT_THRESHOLD_DEFAULT = 90;

	private static final Map<String, Map<String, Integer>>
		_expectedTestCountsMap = new HashMap<>();

	private List<String> _excludedBatchNames;
	private final Map<Long, TopLevelBuildReport> _latestTopLevelBuildReports =
		new HashMap<>();
	private final int _maxBuildCount;
	private boolean _populated;
	private final List<TestrayRoutine> _testrayRoutines;
	private URL _testrayURL;

}