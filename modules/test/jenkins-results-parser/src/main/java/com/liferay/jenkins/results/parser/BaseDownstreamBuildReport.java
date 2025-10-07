/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseDownstreamBuildReport
	extends BaseBuildReport implements DownstreamBuildReport {

	@Override
	public String getAxisName() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return null;
		}

		return buildReportJSONObject.optString("axisName", null);
	}

	@Override
	public String getBatchName() {
		return _batchName;
	}

	@Override
	public JSONObject getBuildReportJSONObject() {
		return _buildReportJSONObject;
	}

	@Override
	public int getFailCount() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0;
		}

		return buildReportJSONObject.optInt("failCount", 0);
	}

	@Override
	public int getPassCount() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		return buildReportJSONObject.optInt("passCount", 0);
	}

	@Override
	public int getSkipCount() {
		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return 0;
		}

		return buildReportJSONObject.optInt("skipCount", 0);
	}

	@Override
	public List<TestClassReport> getTestClassReports() {
		if (_testClassReportsMap != null) {
			return new ArrayList<>(_testClassReportsMap.values());
		}

		_testClassReportsMap = new TreeMap<>();

		for (TestReport testReport : getTestReports()) {
			String testClassName = testReport.getTestClassName();

			TestClassReport testClassReport = _testClassReportsMap.get(
				testClassName);

			if (testClassReport == null) {
				testClassReport = TestReportFactory.newTestClassReport(
					this, testClassName);

				_testClassReportsMap.put(testClassName, testClassReport);
			}

			testClassReport.addTestReport(testReport);
		}

		return new ArrayList<>(_testClassReportsMap.values());
	}

	@Override
	public List<TestReport> getTestReports() {
		List<TestReport> testReports = new ArrayList<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			return testReports;
		}

		JSONArray testResultsJSONArray = buildReportJSONObject.optJSONArray(
			"testResults");

		if (testResultsJSONArray == null) {
			return testReports;
		}

		for (int i = 0; i < testResultsJSONArray.length(); i++) {
			testReports.add(
				TestReportFactory.newTestReport(
					this, testResultsJSONArray.getJSONObject(i)));
		}

		return testReports;
	}

	@Override
	public TopLevelBuildReport getTopLevelBuildReport() {
		return _topLevelBuildReport;
	}

	protected BaseDownstreamBuildReport(DownstreamBuild downstreamBuild) {
		super(downstreamBuild.getBuildURL());

		_batchName = downstreamBuild.getBatchName();
		_buildReportJSONObject = downstreamBuild.getBuildReportJSONObject();
		_topLevelBuildReport = null;
	}

	protected BaseDownstreamBuildReport(
		String batchName, JSONObject buildReportJSONObject,
		TopLevelBuildReport topLevelBuildReport) {

		super(buildReportJSONObject.getString("buildURL"));

		_batchName = batchName;
		_buildReportJSONObject = buildReportJSONObject;
		_topLevelBuildReport = topLevelBuildReport;
	}

	private final String _batchName;
	private final JSONObject _buildReportJSONObject;
	private Map<String, TestClassReport> _testClassReportsMap;
	private final TopLevelBuildReport _topLevelBuildReport;

}