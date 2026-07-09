/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.testray.TestrayProject;
import com.liferay.jenkins.results.parser.testray.TestrayServer;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayTestClassHistory extends BaseTestClassHistory {

	public void addTestClassReport(TestClassReport testClassReport) {
		if ((testClassReport == null) ||
			_testClassReports.contains(testClassReport) ||
			_excludeTestClassReport(testClassReport)) {

			return;
		}

		_testClassReports.add(testClassReport);
	}

	@Override
	public long getAverageDuration() {
		long count = 0;
		long maximumTestDuration = _getMaximumTestDuration();
		long totalDuration = 0;

		for (TestClassReport testClassReport : _testClassReports) {
			DownstreamBuildReport downstreamBuildReport =
				testClassReport.getDownstreamBuildReport();

			long duration = testClassReport.getDuration();

			if ((duration <= 0) || (duration >= maximumTestDuration) ||
				(duration >= downstreamBuildReport.getDuration())) {

				continue;
			}

			count++;
			totalDuration += duration;
		}

		if (count == 0) {
			return 0;
		}

		return totalDuration / count;
	}

	@Override
	public long getAverageOverheadDuration() {
		long count = 0;
		long maximumTestOverheadDuration = _getMaximumTestOverheadDuration();
		long totalOverheadDuration = 0;

		for (TestClassReport testClassReport : _testClassReports) {
			long overheadDuration = testClassReport.getOverheadDuration();

			if (overheadDuration > maximumTestOverheadDuration) {
				continue;
			}

			count++;
			totalOverheadDuration += overheadDuration;
		}

		if (count == 0) {
			return 0;
		}

		return totalOverheadDuration / count;
	}

	@Override
	public BatchHistory getBatchHistory() {
		return _batchHistory;
	}

	public String getBatchName() {
		return _batchHistory.getBatchName();
	}

	@Override
	public int getFailureCount() {
		int failureCount = 0;

		for (TestClassReport testClassReport : _testClassReports) {
			String status = _fixStatus(testClassReport.getStatus());

			if (!Objects.equals(status, "PASSED")) {
				failureCount++;
			}
		}

		return failureCount;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"averageDuration", getAverageDuration()
		).put(
			"averageOverheadDuration", getAverageOverheadDuration()
		).put(
			"failureCount", getFailureCount()
		).put(
			"flaky", isFlaky()
		).put(
			"statusChanges", getStatusChanges()
		).put(
			"testCount", getTestCount()
		).put(
			"testName", getTestClassName()
		).put(
			"testrayCaseURL", getTestrayCaseURL()
		).put(
			"testTaskName", getTestTaskName()
		);

		return jsonObject;
	}

	@Override
	public int getStatusChanges() {
		int statusChanges = 0;

		String lastStatus = null;

		for (TestClassReport testClassReport : _testClassReports) {
			String status = _fixStatus(testClassReport.getStatus());

			if (lastStatus == null) {
				lastStatus = status;

				continue;
			}

			if (!lastStatus.equals(status)) {
				lastStatus = status;

				statusChanges++;
			}
		}

		return statusChanges;
	}

	public String getTestClassName() {
		return _testClassName;
	}

	public List<TestClassReport> getTestClassReports() {
		return _testClassReports;
	}

	@Override
	public long getTestCount() {
		return _testClassReports.size();
	}

	@Override
	public URL getTestrayCaseURL() {
		if (_testrayCaseURLString != null) {
			if (!JenkinsResultsParserUtil.isURL(_testrayCaseURLString)) {
				return null;
			}

			try {
				return new URL(_testrayCaseURLString);
			}
			catch (MalformedURLException malformedURLException) {
				return null;
			}
		}

		TestrayProject testrayProject = _getTestrayProject();

		if (testrayProject != null) {
			long testrayCaseId = testrayProject.getTestrayCaseIdByName(
				getTestClassName());
			TestrayServer testrayServer = testrayProject.getTestrayServer();

			if ((testrayCaseId != 0) && (testrayServer != null)) {
				_testrayCaseURLString = JenkinsResultsParserUtil.combine(
					String.valueOf(testrayServer.getURL()), "/#/project/",
					String.valueOf(testrayProject.getId()), "/cases/",
					String.valueOf(testrayCaseId));
			}
		}

		if (!JenkinsResultsParserUtil.isURL(_testrayCaseURLString)) {
			_testrayCaseURLString = "";

			return null;
		}

		try {
			return new URL(_testrayCaseURLString);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Override
	public TestTaskHistory getTestTaskHistory() {
		return null;
	}

	@Override
	public String getTestTaskName() {
		if (_testClassReports.isEmpty()) {
			return null;
		}

		TestClassReport testClassReport = _testClassReports.get(0);

		if (testClassReport == null) {
			return null;
		}

		return testClassReport.getTestTaskName();
	}

	@Override
	public boolean isFlaky() {
		if (getStatusChanges() >= _getMinimumStatusChanges()) {
			return true;
		}

		return false;
	}

	protected TestrayTestClassHistory(
		BatchHistory batchHistory, String testClassName) {

		super(batchHistory, null);

		_batchHistory = batchHistory;
		_testClassName = testClassName;
	}

	private boolean _excludeTestClassReport(TestClassReport testClassReport) {
		String status = _fixStatus(testClassReport.getStatus());

		if (status.equals("SKIPPED")) {
			return true;
		}

		String testClassName = testClassReport.getTestClassName();

		if (testClassName.contains("JenkinsLogAsserterTest") ||
			testClassName.contains("PortalLogAssertorTest")) {

			return true;
		}

		for (String excludedTestNameRegex : _getExcludedTestNameRegexes()) {
			if (testClassName.matches(".*" + excludedTestNameRegex + ".*")) {
				return true;
			}
		}

		return false;
	}

	private String _fixStatus(String status) {
		status = status.replace("FIXED", "PASSED");
		status = status.replace("REGRESSION", "FAILED");

		return status;
	}

	private List<String> _getExcludedTestNameRegexes() {
		if (_excludedTestNameRegexes != null) {
			return _excludedTestNameRegexes;
		}

		try {
			String excludedTestNames = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"flaky.test.report.test.name.excludes");

			_excludedTestNameRegexes = Arrays.asList(
				excludedTestNames.split("\\s*,\\s*"));
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return _excludedTestNameRegexes;
	}

	private long _getMaximumTestDuration() {
		try {
			String maximumTestDuration =
				JenkinsResultsParserUtil.getBuildProperty(
					"test.history.test.maximum.duration",
					getPortalUpstreamBranchName());

			if (JenkinsResultsParserUtil.isInteger(maximumTestDuration)) {
				return Long.parseLong(maximumTestDuration);
			}

			return _MAXIMUM_TEST_DURATION;
		}
		catch (IOException ioException) {
			return _MAXIMUM_TEST_DURATION;
		}
	}

	private long _getMaximumTestOverheadDuration() {
		try {
			String maximumTestOverheadDuration =
				JenkinsResultsParserUtil.getBuildProperty(
					"test.history.test.maximum.overhead.duration",
					getPortalUpstreamBranchName());

			if (JenkinsResultsParserUtil.isInteger(
					maximumTestOverheadDuration)) {

				return Long.parseLong(maximumTestOverheadDuration);
			}

			return _MAXIMUM_TEST_OVERHEAD_DURATION;
		}
		catch (IOException ioException) {
			return _MAXIMUM_TEST_OVERHEAD_DURATION;
		}
	}

	private int _getMinimumStatusChanges() {
		try {
			String minimumStatusChanges =
				JenkinsResultsParserUtil.getBuildProperty(
					"test.history.test.minimum.status.changes",
					getPortalUpstreamBranchName());

			if (JenkinsResultsParserUtil.isInteger(minimumStatusChanges)) {
				return Integer.parseInt(minimumStatusChanges);
			}

			return _MINIMUM_STATUS_CHANGES;
		}
		catch (IOException ioException) {
			return _MINIMUM_STATUS_CHANGES;
		}
	}

	private TestrayBatchHistory _getTestrayBatchHistory() {
		BatchHistory batchHistory = getBatchHistory();

		if (batchHistory instanceof TestrayBatchHistory) {
			return (TestrayBatchHistory)batchHistory;
		}

		return null;
	}

	private TestrayJobHistory _getTestrayJobHistory() {
		TestrayBatchHistory testrayBatchHistory = _getTestrayBatchHistory();

		if (testrayBatchHistory == null) {
			return null;
		}

		return testrayBatchHistory.getTestrayJobHistory();
	}

	private TestrayProject _getTestrayProject() {
		TestrayJobHistory testrayJobHistory = _getTestrayJobHistory();

		if (testrayJobHistory == null) {
			return null;
		}

		return testrayJobHistory.getTestrayProject();
	}

	private static final long _MAXIMUM_TEST_DURATION = 2 * 60 * 60 * 1000;

	private static final long _MAXIMUM_TEST_OVERHEAD_DURATION =
		2 * 60 * 60 * 1000;

	private static final int _MINIMUM_STATUS_CHANGES = 3;

	private static List<String> _excludedTestNameRegexes;

	private final BatchHistory _batchHistory;
	private final String _testClassName;
	private final List<TestClassReport> _testClassReports = new ArrayList<>();
	private String _testrayCaseURLString;

}