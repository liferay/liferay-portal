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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Calum Ragan
 */
public class UpstreamJobHealthMonitor extends BaseMonitor {

	public UpstreamJobHealthMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_branch = getRequiredParameter("branch", parameters);
		_controllerJobName = getRequiredParameter(
			"controller.job.name", parameters);
		_expectedGreen = getBooleanValue(
			"parameter", true, "expected.green", parameters);

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			getRequiredParameter("master.name", parameters));

		_controllerJobURL = JenkinsResultsParserUtil.combine(
			jenkinsMaster.getURL(), "/job/", _controllerJobName);

		Map<String, String> thresholds = monitorConfig.getThresholds();

		_buildsMaximum = getLongValue(
			"threshold", _BUILDS_MAXIMUM_DEFAULT, "builds.maximum", thresholds);
		_triggerLatencySeconds = getLongValue(
			"threshold", _SECONDS_TRIGGER_LATENCY_DEFAULT, "trigger.latency",
			thresholds);
	}

	@Override
	public MonitorResult execute() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		JSONArray buildsJSONArray = null;

		try {
			buildsJSONArray = _getBuildsJSONArray();
		}
		catch (Exception exception) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Unable to read ", _controllerJobURL, ": ",
					exception.getMessage()),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (buildsJSONArray == null) {
			return new MonitorResult(
				"Unable to read " + _controllerJobURL, null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		int buildsCount = buildsJSONArray.length();

		if (buildsCount == 0) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Controller job ", _controllerJobName, " has never run"),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		Map<String, String> metrics = new LinkedHashMap<>();
		List<String> messages = new ArrayList<>();
		List<MonitorResult.Status> statuses = new ArrayList<>();

		JSONObject lastBuildJSONObject = buildsJSONArray.getJSONObject(0);

		_checkControllerAge(
			currentTimeMillis, lastBuildJSONObject.optLong("timestamp"),
			messages, metrics, statuses);

		JSONObject invocationJSONObject = null;

		for (int i = 0; i < buildsCount; i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			String description = buildJSONObject.optString("description", "");

			if (_isInvocation(description)) {
				invocationJSONObject = buildJSONObject;

				break;
			}
		}

		if (invocationJSONObject == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the last upstream testsuite run for ",
					"branch ", _branch));

			statuses.add(MonitorResult.Status.UNKNOWN);
		}
		else {
			_checkInvocation(
				currentTimeMillis, invocationJSONObject, messages, metrics,
				statuses);
		}

		return _newMonitorResult(
			currentTimeMillis, messages, metrics, statuses);
	}

	private void _checkControllerAge(
		long currentTimeMillis, long lastBuildTimestamp, List<String> messages,
		Map<String, String> metrics, List<MonitorResult.Status> statuses) {

		if (lastBuildTimestamp <= 0) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the last build timestamp for ",
					"controller job ", _controllerJobName));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		long ageSeconds = (currentTimeMillis - lastBuildTimestamp) / 1000;

		metrics.put(
			"controller.last.build.age.seconds", String.valueOf(ageSeconds));

		if (ageSeconds > _triggerLatencySeconds) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Controller job ", _controllerJobName, " last ran ",
					JenkinsResultsParserUtil.toDurationString(
						ageSeconds * 1000),
					" ago, leaving the upstream testsuite for branch ", _branch,
					" unevaluated"));

			statuses.add(MonitorResult.Status.WARN);
		}
	}

	private void _checkInvocation(
		long currentTimeMillis, JSONObject invocationJSONObject,
		List<String> messages, Map<String, String> metrics,
		List<MonitorResult.Status> statuses) {

		String description = invocationJSONObject.optString("description", "");

		long ageSeconds =
			(currentTimeMillis - invocationJSONObject.optLong("timestamp")) /
				1000;

		metrics.put("last.invocation.age.seconds", String.valueOf(ageSeconds));

		String invocationBuildURL = _getInvocationBuildURL(description);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(invocationBuildURL)) {
			metrics.put("last.invocation.build.url", invocationBuildURL);
		}

		String result = _getInvocationResult(description);

		metrics.put("last.invocation.result", result);

		if (result.equals(_RESULT_EXPIRE)) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Branch ", _branch, " was merged ",
					JenkinsResultsParserUtil.toDurationString(
						ageSeconds * 1000),
					" ago, but its upstream testsuite was expired before it ",
					"completed"));

			statuses.add(MonitorResult.Status.WARN);

			return;
		}

		if (result.equals(_RESULT_IN_PROGRESS) ||
			result.equals(_RESULT_IN_QUEUE)) {

			if (ageSeconds > _triggerLatencySeconds) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Branch ", _branch, " was merged ",
						JenkinsResultsParserUtil.toDurationString(
							ageSeconds * 1000),
						" ago, but its upstream testsuite has not run, ",
						"exceeding the expected trigger latency of ",
						JenkinsResultsParserUtil.toDurationString(
							_triggerLatencySeconds * 1000)));

				statuses.add(MonitorResult.Status.WARN);
			}

			return;
		}

		if (!_expectedGreen) {
			return;
		}

		if (result.equals(_RESULT_ABORTED)) {
			messages.add(_getNotGreenMessage(result));

			statuses.add(MonitorResult.Status.WARN);

			return;
		}

		if (result.equals(_RESULT_FAILURE)) {
			messages.add(_getNotGreenMessage(result));

			statuses.add(MonitorResult.Status.CRITICAL);
		}
	}

	private JSONArray _getBuildsJSONArray() throws IOException {
		JSONObject jobJSONObject = JenkinsResultsParserUtil.toJSONObject(
			JenkinsResultsParserUtil.combine(
				_controllerJobURL, "/api/json?tree=",
				"builds[description,timestamp]{0,",
				String.valueOf(_buildsMaximum), "}"),
			false, _RETRIES_SIZE_MAX, null, null, _SECONDS_RETRY_PERIOD,
			getAttemptTimeoutMillis(_RETRIES_SIZE_MAX), null);

		if (jobJSONObject == null) {
			return null;
		}

		return jobJSONObject.optJSONArray("builds");
	}

	private String _getInvocationBuildURL(String description) {
		Matcher matcher = _buildURLPattern.matcher(description);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group();
	}

	private String _getInvocationResult(String description) {
		if (description.contains(_RESULT_EXPIRE)) {
			return _RESULT_EXPIRE;
		}

		if (description.contains(_RESULT_IN_QUEUE)) {
			return _RESULT_IN_QUEUE;
		}

		if (description.contains(_RESULT_IN_PROGRESS)) {
			return _RESULT_IN_PROGRESS;
		}

		if (description.contains(_RESULT_FAILURE)) {
			return _RESULT_FAILURE;
		}

		if (description.contains(_RESULT_ABORTED)) {
			return _RESULT_ABORTED;
		}

		if (description.contains(_RESULT_UNSTABLE)) {
			return _RESULT_UNSTABLE;
		}

		if (description.contains(_RESULT_SUCCESS)) {
			return _RESULT_SUCCESS;
		}

		return _RESULT_COMPLETED;
	}

	private String _getNotGreenMessage(String result) {
		return JenkinsResultsParserUtil.combine(
			"The upstream testsuite for branch ", _branch,
			" completed with the result \"", result, "\"");
	}

	private boolean _isInvocation(String description) {
		return description.contains(_MARKER_GIT_ID);
	}

	private MonitorResult _newMonitorResult(
		long currentTimeMillis, List<String> messages,
		Map<String, String> metrics, List<MonitorResult.Status> statuses) {

		if (statuses.isEmpty()) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"The upstream testsuite for branch ", _branch, " is OK"),
				metrics, MonitorResult.Status.OK, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.join(". ", messages), metrics,
			MonitorResult.Status.getMostSevere(statuses), currentTimeMillis);
	}

	private static final long _BUILDS_MAXIMUM_DEFAULT = 24;

	private static final String _MARKER_GIT_ID = "Git ID:";

	private static final String _RESULT_ABORTED = "ABORTED";

	private static final String _RESULT_COMPLETED = "COMPLETED";

	private static final String _RESULT_EXPIRE = "EXPIRE";

	private static final String _RESULT_FAILURE = "FAILURE";

	private static final String _RESULT_IN_PROGRESS = "IN PROGRESS";

	private static final String _RESULT_IN_QUEUE = "IN QUEUE";

	private static final String _RESULT_SUCCESS = "SUCCESS";

	private static final String _RESULT_UNSTABLE = "UNSTABLE";

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final long _SECONDS_TRIGGER_LATENCY_DEFAULT = 4 * 60 * 60;

	private static final Pattern _buildURLPattern = Pattern.compile(
		"https?://test-\\d+-\\d+(-aws)?\\.liferay\\.com/job/[^/\\s\"]+/" +
			"\\d+/?");

	private final String _branch;
	private final long _buildsMaximum;
	private final String _controllerJobName;
	private final String _controllerJobURL;
	private final boolean _expectedGreen;
	private final long _triggerLatencySeconds;

}