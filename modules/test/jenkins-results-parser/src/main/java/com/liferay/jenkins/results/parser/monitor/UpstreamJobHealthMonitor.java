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

		int skippedCount = 0;
		boolean pendingInvocation = false;
		JSONObject invocationJSONObject = null;

		for (int i = 0; i < buildsCount; i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			String description = buildJSONObject.optString("description", "");

			if (_isInvocation(description)) {
				invocationJSONObject = buildJSONObject;

				break;
			}

			if (_isSkippedPendingInvocation(description)) {
				pendingInvocation = true;

				continue;
			}

			if (!_isSkippedAlreadyRan(description)) {
				break;
			}

			skippedCount++;
		}

		metrics.put("controller.skipped.streak", String.valueOf(skippedCount));

		if (invocationJSONObject == null) {
			_checkMissingInvocation(
				buildsCount, messages, pendingInvocation, skippedCount,
				statuses);
		}
		else {
			_checkInvocation(
				currentTimeMillis, invocationJSONObject, messages, metrics,
				pendingInvocation, statuses);
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
		boolean pendingInvocation, List<MonitorResult.Status> statuses) {

		String description = invocationJSONObject.optString("description", "");

		long ageSeconds =
			(currentTimeMillis - invocationJSONObject.optLong("timestamp")) /
				1000;

		metrics.put("last.invocation.age.seconds", String.valueOf(ageSeconds));

		String invocationBuildURL = _getInvocationBuildURL(description);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(invocationBuildURL)) {
			metrics.put("last.invocation.build.url", invocationBuildURL);
		}

		if (_isFailure(description)) {
			metrics.put("last.invocation.result", "FAILURE");

			if (_expectedGreen) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"The upstream testsuite for branch ", _branch,
						" completed with the result \"FAILURE\""));

				statuses.add(MonitorResult.Status.CRITICAL);
			}

			return;
		}

		if (!_isPending(description) && !pendingInvocation) {
			metrics.put("last.invocation.result", "COMPLETED");

			return;
		}

		metrics.put("last.invocation.result", "PENDING");

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
	}

	private void _checkMissingInvocation(
		int buildsCount, List<String> messages, boolean pendingInvocation,
		int skippedCount, List<MonitorResult.Status> statuses) {

		if (skippedCount == buildsCount) {
			return;
		}

		if (pendingInvocation) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"The upstream testsuite for branch ", _branch,
					" has been pending for longer than the last ",
					String.valueOf(buildsCount), " controller builds"));

			statuses.add(MonitorResult.Status.WARN);

			return;
		}

		messages.add(
			JenkinsResultsParserUtil.combine(
				"Unable to determine the last upstream testsuite run for ",
				"branch ", _branch));

		statuses.add(MonitorResult.Status.UNKNOWN);
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

	private boolean _isFailure(String description) {
		return description.contains("FAILURE");
	}

	private boolean _isInvocation(String description) {
		if (_isPending(description)) {
			return true;
		}

		String invocationBuildURL = _getInvocationBuildURL(description);

		if (invocationBuildURL != null) {
			return true;
		}

		return false;
	}

	private boolean _isPending(String description) {
		if (description.contains("IN PROGRESS") ||
			description.contains("IN QUEUE")) {

			return true;
		}

		return false;
	}

	private boolean _isSkippedAlreadyRan(String description) {
		if (description.contains("SKIPPED") &&
			description.contains("was already ran")) {

			return true;
		}

		return false;
	}

	private boolean _isSkippedPendingInvocation(String description) {
		if (!description.contains("SKIPPED")) {
			return false;
		}

		if (description.contains("already invoked") ||
			description.contains("already running")) {

			return true;
		}

		return false;
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