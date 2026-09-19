/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.history;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestTaskReport;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayTestTaskHistory extends BaseTestTaskHistory {

	public void addTestTaskReport(
		boolean latestBuild, TestTaskReport testTaskReport) {

		if (testTaskReport == null) {
			return;
		}

		TopLevelBuildReport topLevelBuildReport =
			testTaskReport.getTopLevelBuildReport();

		if (topLevelBuildReport == null) {
			return;
		}

		String key = JenkinsResultsParserUtil.combine(
			String.valueOf(topLevelBuildReport.getBuildURL()), "__",
			testTaskReport.getName());

		TestrayTestTaskEntry testrayTestTaskEntry =
			_testrayTestTaskEntries.getOrDefault(
				key, new TestrayTestTaskEntry(latestBuild, this));

		testrayTestTaskEntry.addTestTaskReport(testTaskReport);

		_testrayTestTaskEntries.put(key, testrayTestTaskEntry);
	}

	@Override
	public long getAverageDuration() {
		if (_testrayTestTaskEntries.isEmpty()) {
			return 0;
		}

		long totalDuration = 0;

		for (TestrayTestTaskEntry testrayTestTaskEntry :
				_testrayTestTaskEntries.values()) {

			totalDuration += testrayTestTaskEntry.getOverheadDuration();
		}

		return totalDuration / _testrayTestTaskEntries.size();
	}

	@Override
	public long getAverageTotalDuration() {
		if (_testrayTestTaskEntries.isEmpty()) {
			return 0;
		}

		long totalAverageDuration = 0;

		for (TestrayTestTaskEntry testrayTestTaskEntry :
				_testrayTestTaskEntries.values()) {

			totalAverageDuration += testrayTestTaskEntry.getDuration();
		}

		return totalAverageDuration / _testrayTestTaskEntries.size();
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"averageDuration", getAverageDuration()
		).put(
			"averageTotalDuration", getAverageTotalDuration()
		).put(
			"latestReportMissing", isLatestReportMissing()
		).put(
			"longestDuration", getLongestDuration()
		).put(
			"testTaskCount", getTestTaskCount()
		).put(
			"testTaskName", getTestTaskName()
		);

		return jsonObject;
	}

	@Override
	public long getLongestDuration() {
		long longestDuration = 0L;

		for (TestrayTestTaskEntry testrayTestTaskEntry :
				_testrayTestTaskEntries.values()) {

			if (longestDuration <= testrayTestTaskEntry.getDuration()) {
				longestDuration = testrayTestTaskEntry.getDuration();
			}
		}

		return longestDuration;
	}

	@Override
	public long getTestTaskCount() {
		return _testrayTestTaskEntries.size();
	}

	public static class TestrayTestTaskEntry {

		public TestrayTestTaskEntry(
			boolean latestBuild,
			TestrayTestTaskHistory testrayTestTaskHistory) {

			_latestBuild = latestBuild;
			_testrayTestTaskHistory = testrayTestTaskHistory;
		}

		public void addTestTaskReport(TestTaskReport testTaskReport) {
			if ((testTaskReport == null) ||
				_testTaskReports.contains(testTaskReport)) {

				return;
			}

			_testTaskReports.add(testTaskReport);

			if (_latestBuild && testTaskReport.isMissing()) {
				_testrayTestTaskHistory.setLatestReportMissing(true);
			}
		}

		public long getDuration() {
			if (_testTaskReports.isEmpty()) {
				return 0;
			}

			long totalDuration = 0;

			for (TestTaskReport testTaskReport : _testTaskReports) {
				totalDuration += testTaskReport.getDuration();
			}

			return totalDuration;
		}

		public long getOverheadDuration() {
			long totalOverheadDuration = 0;

			for (TestTaskReport testTaskReport : _testTaskReports) {
				totalOverheadDuration += testTaskReport.getOverheadDuration();
			}

			return totalOverheadDuration;
		}

		public boolean isMissing() {
			for (TestTaskReport testTaskReport : _testTaskReports) {
				if (testTaskReport.isMissing()) {
					return true;
				}
			}

			return false;
		}

		private final boolean _latestBuild;
		private final List<TestTaskReport> _testTaskReports = new ArrayList<>();
		private final TestrayTestTaskHistory _testrayTestTaskHistory;

	}

	protected TestrayTestTaskHistory(
		BatchHistory batchHistory, String testTaskName) {

		super(batchHistory, testTaskName);
	}

	private final Map<String, TestrayTestTaskEntry> _testrayTestTaskEntries =
		new HashMap<>();

}