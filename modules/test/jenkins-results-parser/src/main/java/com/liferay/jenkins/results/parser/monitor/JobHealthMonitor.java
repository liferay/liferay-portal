/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class JobHealthMonitor extends BaseMonitor {

	public JobHealthMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_cadenceSeconds = getLongValue("parameter", 0, "cadence", parameters);
		_expectedGreen = _isExpectedGreen(parameters);
		_jobName = getRequiredParameter("job.name", parameters);
		_jobURL = _getJobURL(getRequiredParameter("master.name", parameters));

		Map<String, String> thresholds = monitorConfig.getThresholds();

		_buildDurationMaximumSeconds = getLongValue(
			"threshold", 0, "build.duration.maximum", thresholds);
		_overdueGraceSeconds = getOverdueGraceSeconds(
			_cadenceSeconds, thresholds);
	}

	@Override
	public MonitorResult execute() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		JSONObject jobJSONObject = null;

		try {
			jobJSONObject = _getJobJSONObject();
		}
		catch (Exception exception) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Unable to read ", _jobURL, ": ", exception.getMessage()),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (jobJSONObject == null) {
			return new MonitorResult(
				"Unable to read " + _jobURL, null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		JSONObject lastBuildJSONObject = jobJSONObject.optJSONObject(
			"lastBuild");
		JSONObject lastCompletedBuildJSONObject = jobJSONObject.optJSONObject(
			"lastCompletedBuild");

		if ((lastBuildJSONObject == null) &&
			(lastCompletedBuildJSONObject == null)) {

			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " has never run"),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		Map<String, String> metrics = new LinkedHashMap<>();
		List<String> messages = new ArrayList<>();
		List<MonitorResult.Status> statuses = new ArrayList<>();

		if (lastCompletedBuildJSONObject != null) {
			int number = lastCompletedBuildJSONObject.optInt("number");
			String result = lastCompletedBuildJSONObject.optString("result");

			metrics.put("last.completed.build.number", String.valueOf(number));
			metrics.put("last.completed.build.result", result);

			if (_expectedGreen &&
				!JenkinsResultsParserUtil.isNullOrEmpty(result) &&
				!result.equals("SUCCESS")) {

				messages.add(
					JenkinsResultsParserUtil.combine(
						"Job ", _jobName, " completed build ",
						String.valueOf(number), " with the result ", result));

				if (result.equals("ABORTED")) {
					statuses.add(MonitorResult.Status.WARN);
				}
				else {
					statuses.add(MonitorResult.Status.CRITICAL);
				}
			}
		}

		boolean buildRunning = _isBuildRunning(lastBuildJSONObject);

		metrics.put("last.build.running", String.valueOf(buildRunning));

		long lastBuildTimestamp = _getLastBuildTimestamp(
			lastBuildJSONObject, lastCompletedBuildJSONObject);

		if (lastBuildTimestamp <= 0) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the last build timestamp for job ",
					_jobName));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return _newMonitorResult(
				currentTimeMillis, messages, metrics, statuses);
		}

		long lastBuildAgeSeconds =
			(currentTimeMillis - lastBuildTimestamp) / 1000;

		metrics.put(
			"last.build.age.seconds", String.valueOf(lastBuildAgeSeconds));

		if (buildRunning && (_buildDurationMaximumSeconds > 0)) {
			if (lastBuildAgeSeconds > _buildDurationMaximumSeconds) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Job ", _jobName, " has been running for ",
						JenkinsResultsParserUtil.toDurationString(
							lastBuildAgeSeconds * 1000),
						", exceeding its maximum duration of ",
						JenkinsResultsParserUtil.toDurationString(
							_buildDurationMaximumSeconds * 1000)));

				statuses.add(MonitorResult.Status.CRITICAL);
			}
		}
		else if (_isOverdue(lastBuildAgeSeconds)) {
			messages.add(_getOverdueMessage(buildRunning, lastBuildAgeSeconds));

			statuses.add(MonitorResult.Status.WARN);
		}

		return _newMonitorResult(
			currentTimeMillis, messages, metrics, statuses);
	}

	private String _getCadenceMessage() {
		return JenkinsResultsParserUtil.combine(
			"exceeding its cadence of ",
			JenkinsResultsParserUtil.toDurationString(_cadenceSeconds * 1000),
			" plus a grace period of ",
			JenkinsResultsParserUtil.toDurationString(
				_overdueGraceSeconds * 1000));
	}

	private JSONObject _getJobJSONObject() throws IOException {
		return JenkinsResultsParserUtil.toJSONObject(
			JenkinsResultsParserUtil.combine(
				_jobURL, "/api/json?tree=",
				"lastBuild[building,number,timestamp],",
				"lastCompletedBuild[number,result,timestamp]"),
			false, 1, null, null, _SECONDS_RETRY_PERIOD,
			getAttemptTimeoutMillis(), null);
	}

	private String _getJobURL(String masterName) {
		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(masterName);

		return JenkinsResultsParserUtil.combine(
			jenkinsMaster.getURL(), "/job/", _jobName);
	}

	private long _getLastBuildTimestamp(
		JSONObject lastBuildJSONObject,
		JSONObject lastCompletedBuildJSONObject) {

		if (lastBuildJSONObject != null) {
			return lastBuildJSONObject.optLong("timestamp");
		}

		return lastCompletedBuildJSONObject.optLong("timestamp");
	}

	private String _getOverdueMessage(
		boolean buildRunning, long lastBuildAgeSeconds) {

		if (buildRunning) {
			return JenkinsResultsParserUtil.combine(
				"Job ", _jobName, " has been running for ",
				JenkinsResultsParserUtil.toDurationString(
					lastBuildAgeSeconds * 1000),
				", ", _getCadenceMessage());
		}

		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " last ran ",
			JenkinsResultsParserUtil.toDurationString(
				lastBuildAgeSeconds * 1000),
			" ago, ", _getCadenceMessage());
	}

	private boolean _isBuildRunning(JSONObject lastBuildJSONObject) {
		if (lastBuildJSONObject == null) {
			return false;
		}

		return lastBuildJSONObject.optBoolean("building");
	}

	private boolean _isExpectedGreen(Map<String, String> parameters) {
		String expectedGreen = parameters.get("expected.green");

		if (JenkinsResultsParserUtil.isNullOrEmpty(expectedGreen)) {
			return true;
		}

		if (!expectedGreen.equals("false") && !expectedGreen.equals("true")) {
			throw new IllegalArgumentException(
				getInvalidValueMessage(
					"parameter", "expected.green", expectedGreen));
		}

		return Boolean.parseBoolean(expectedGreen);
	}

	private boolean _isOverdue(long lastBuildAgeSeconds) {
		if (_cadenceSeconds <= 0) {
			return false;
		}

		if (lastBuildAgeSeconds > (_cadenceSeconds + _overdueGraceSeconds)) {
			return true;
		}

		return false;
	}

	private MonitorResult _newMonitorResult(
		long currentTimeMillis, List<String> messages,
		Map<String, String> metrics, List<MonitorResult.Status> statuses) {

		if (statuses.isEmpty()) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine("Job ", _jobName, " is OK"),
				metrics, MonitorResult.Status.OK, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.join(". ", messages), metrics,
			MonitorResult.Status.getMostSevere(statuses), currentTimeMillis);
	}

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private final long _buildDurationMaximumSeconds;
	private final long _cadenceSeconds;
	private final boolean _expectedGreen;
	private final String _jobName;
	private final String _jobURL;
	private final long _overdueGraceSeconds;

}