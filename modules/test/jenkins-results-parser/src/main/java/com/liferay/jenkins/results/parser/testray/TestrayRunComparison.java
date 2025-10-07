/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class TestrayRunComparison {

	public double getCommonStatusTestCountPercentage() {
		if (_totalTestCount == 0) {
			return 0.0;
		}

		return (double)Math.round(
			_totalCommonStatusTestCount * 10000.0 / _totalTestCount) / 100.0;
	}

	public URL getComparisonURL() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://testray.liferay.com/#/compare-runs/");
		sb.append(_testrayRunA.getID());
		sb.append("/");
		sb.append(_testrayRunB.getID());
		sb.append("/teams");

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public long getNewFailureTestCount() {
		return _newFailureTestCount;
	}

	public long getNewUntestedTestCount() {
		return _newUntestedTestCount;
	}

	public long getTotalCommonStatusTestCount() {
		return _totalCommonStatusTestCount;
	}

	public long getTotalTestCount() {
		return _totalTestCount;
	}

	protected TestrayRunComparison(
		TestrayRun testrayRunA, TestrayRun testrayRunB) {

		if (testrayRunA == null) {
			throw new IllegalArgumentException("Testray run A is null");
		}

		if (testrayRunB == null) {
			throw new IllegalArgumentException("Testray run B is null");
		}

		_testrayRunA = testrayRunA;
		_testrayRunB = testrayRunB;

		_initializeTestResultCounts();
	}

	private JSONObject _getRunComparisonsJSONObject() {
		if (_jsonObject != null) {
			return _jsonObject;
		}

		try {
			StringBuilder sb = new StringBuilder();

			TestrayBuild testrayBuild = _testrayRunA.getTestrayBuild();

			TestrayServer testrayServer = testrayBuild.getTestrayServer();

			sb.append(testrayServer.getURL());

			sb.append("/o/testray-rest/v1.0/testray-run-comparisons/");
			sb.append(_testrayRunA.getID());
			sb.append("/");
			sb.append(_testrayRunB.getID());
			sb.append("/runs");

			_jsonObject = JenkinsResultsParserUtil.toJSONObject(sb.toString());

			return _jsonObject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _initializeTestResultCounts() {
		JSONObject jsonObject = _getRunComparisonsJSONObject();

		JSONArray resultsJSONArray = jsonObject.optJSONArray("results");

		if (resultsJSONArray == null) {
			return;
		}

		JSONObject resultJSONObject = resultsJSONArray.optJSONObject(0);

		if (resultJSONObject == null) {
			return;
		}

		JSONObject runsJSONObject = resultJSONObject.optJSONObject("Runs");

		if (runsJSONObject == null) {
			return;
		}

		for (String statusA : _STATUSES) {
			JSONObject statusJSONObject = runsJSONObject.optJSONObject(statusA);

			if (statusJSONObject == null) {
				continue;
			}

			for (String statusB : _STATUSES) {
				long testCount = statusJSONObject.optLong(statusB, 0);

				_totalTestCount += testCount;

				if (statusA.equals(statusB)) {
					_totalCommonStatusTestCount += testCount;

					continue;
				}

				if (_isUntestedStatus(statusA) && _isUntestedStatus(statusB)) {
					continue;
				}

				if (statusA.equals(_STATUS_PASSED)) {
					if (_isFailureStatus(statusB)) {
						_newFailureTestCount += testCount;

						continue;
					}

					_newUntestedTestCount += testCount;

					continue;
				}

				if (statusB.equals(_STATUS_PASSED)) {
					if (_isFailureStatus(statusA)) {
						_newFailureTestCount -= testCount;

						continue;
					}

					_newUntestedTestCount -= testCount;

					continue;
				}

				if (_isFailureStatus(statusA)) {
					if (_isFailureStatus(statusB)) {
						_totalCommonStatusTestCount += testCount;

						continue;
					}

					_newFailureTestCount -= testCount;

					continue;
				}

				_newFailureTestCount += testCount;
			}
		}
	}

	private boolean _isFailureStatus(String status) {
		if (status.equals(_STATUS_BLOCKED) || status.equals(_STATUS_FAILED) ||
			status.equals(_STATUS_TEST_FIX)) {

			return true;
		}

		return false;
	}

	private boolean _isUntestedStatus(String status) {
		if (status.equals(_STATUS_DID_NOT_RUN) ||
			status.equals(_STATUS_UNTESTED)) {

			return true;
		}

		return false;
	}

	private static final String _STATUS_BLOCKED = "BLOCKED";

	private static final String _STATUS_DID_NOT_RUN = "DIDNOTRUN";

	private static final String _STATUS_FAILED = "FAILED";

	private static final String _STATUS_PASSED = "PASSED";

	private static final String _STATUS_TEST_FIX = "TESTFIX";

	private static final String _STATUS_UNTESTED = "UNTESTED";

	private static final String[] _STATUSES = {
		_STATUS_BLOCKED, _STATUS_DID_NOT_RUN, _STATUS_FAILED, _STATUS_PASSED,
		_STATUS_TEST_FIX, _STATUS_UNTESTED
	};

	private JSONObject _jsonObject;
	private long _newFailureTestCount = 0L;
	private long _newUntestedTestCount = 0L;
	private final TestrayRun _testrayRunA;
	private final TestrayRun _testrayRunB;
	private long _totalCommonStatusTestCount = 0L;
	private long _totalTestCount = 0L;

}