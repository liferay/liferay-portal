/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

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
	public List<FailureReport> getFailureReports() {
		List<FailureReport> failureReports = new ArrayList<>(
			super.getFailureReports());

		for (TestReport testReport : getTestReports()) {
			if (!testReport.isFailing()) {
				continue;
			}

			failureReports.add(
				FailureReportFactory.newFailureReport(this, null, testReport));
		}

		return failureReports;
	}

	@Override
	public String getJobVariant() {
		Map<String, String> buildParameters = getBuildParameters();

		return buildParameters.get("JOB_VARIANT");
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
	public synchronized List<TestClassReport> getTestClassReports() {
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
	public synchronized List<TestReport> getTestReports() {
		if (_testReports != null) {
			return _testReports;
		}

		List<TestReport> testReports = new ArrayList<>();

		JSONObject buildReportJSONObject = getBuildReportJSONObject();

		if (buildReportJSONObject == null) {
			_testReports = testReports;

			return _testReports;
		}

		JSONArray testResultsJSONArray = buildReportJSONObject.optJSONArray(
			"testResults");

		if ((testResultsJSONArray == null) ||
			(testResultsJSONArray.length() == 0)) {

			_testReports = _getLiveTestReports();

			return _testReports;
		}

		for (int i = 0; i < testResultsJSONArray.length(); i++) {
			testReports.add(
				TestReportFactory.newTestReport(
					this, testResultsJSONArray.getJSONObject(i)));
		}

		_testReports = testReports;

		return _testReports;
	}

	@Override
	public TopLevelBuildReport getTopLevelBuildReport() {
		return _topLevelBuildReport;
	}

	@Override
	public boolean isBuildCached() {
		return _buildCached;
	}

	@Override
	public boolean isBuildTimedOut() {
		String result = getResult();

		long jobTimeoutMinutes = JenkinsResultsParserUtil.getJobTimeoutMinutes(
			getJenkinsMaster(), getJobName());

		if (((result == null) || result.equals("ABORTED")) &&
			(getDuration() >= ((jobTimeoutMinutes - 20) * 60 * 1000))) {

			return true;
		}

		return false;
	}

	public void setAxisName(String axisName) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(axisName)) {
			return;
		}

		_buildReportJSONObject.put("axisName", axisName);
	}

	protected BaseDownstreamBuildReport(DownstreamBuild downstreamBuild) {
		super(downstreamBuild.getBuildURL());

		_batchName = downstreamBuild.getBatchName();
		_buildCached = false;
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

		_buildCached = buildReportJSONObject.optBoolean("buildCached", false);
	}

	private List<TestReport> _getLiveTestReports() {
		List<TestReport> testReports = new ArrayList<>();

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if ((jenkinsMaster == null) || !jenkinsMaster.isAvailable()) {
			return testReports;
		}

		String testReportURL = JenkinsResultsParserUtil.getLocalURL(
			JenkinsResultsParserUtil.combine(
				String.valueOf(getBuildURL()), "/testReport/api/json"));

		JSONObject testReportJSONObject = null;

		try {
			if (!JenkinsResultsParserUtil.exists(new URL(testReportURL))) {
				return testReports;
			}

			testReportJSONObject = JenkinsResultsParserUtil.toJSONObject(
				testReportURL, false, 5000);
		}
		catch (Exception exception) {
			return testReports;
		}

		if (testReportJSONObject == null) {
			return testReports;
		}

		JSONArray suitesJSONArray = testReportJSONObject.optJSONArray("suites");

		if (suitesJSONArray == null) {
			return testReports;
		}

		for (int i = 0; i < suitesJSONArray.length(); i++) {
			JSONObject suiteJSONObject = suitesJSONArray.getJSONObject(i);

			JSONArray casesJSONArray = suiteJSONObject.optJSONArray("cases");

			if (casesJSONArray == null) {
				continue;
			}

			for (int j = 0; j < casesJSONArray.length(); j++) {
				testReports.add(
					TestReportFactory.newTestReport(
						this,
						_toTestResultJSONObject(
							casesJSONArray.getJSONObject(j))));
			}
		}

		return testReports;
	}

	private JSONObject _toTestResultJSONObject(JSONObject caseJSONObject) {
		JSONObject testResultJSONObject = new JSONObject();

		testResultJSONObject.put(
			"duration", (long)(caseJSONObject.optDouble("duration", 0) * 1000));

		String errorDetails = caseJSONObject.optString("errorDetails", null);

		if (errorDetails != null) {
			if (errorDetails.contains("\n")) {
				errorDetails = errorDetails.substring(
					0, errorDetails.indexOf("\n"));
			}

			if (errorDetails.length() > 200) {
				errorDetails = errorDetails.substring(0, 200);
			}

			testResultJSONObject.put("errorDetails", errorDetails);
		}

		String status = caseJSONObject.optString("status");

		if (!status.equals("FIXED") && !status.equals("PASSED") &&
			!status.equals("SKIPPED")) {

			testResultJSONObject.put(
				"errorStackTrace", caseJSONObject.optString("errorStackTrace"));
		}

		testResultJSONObject.put(
			"name",
			JenkinsResultsParserUtil.combine(
				caseJSONObject.optString("className"), ".",
				caseJSONObject.optString("name"))
		).put(
			"status", status
		);

		return testResultJSONObject;
	}

	private final String _batchName;
	private final boolean _buildCached;
	private final JSONObject _buildReportJSONObject;
	private Map<String, TestClassReport> _testClassReportsMap;
	private List<TestReport> _testReports;
	private final TopLevelBuildReport _topLevelBuildReport;

}