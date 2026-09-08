/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.time.Instant;

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
		_portalRepositoryName = _getParameter(
			_PORTAL_REPOSITORY_NAME_DEFAULT, parameters,
			"portal.repository.name");
		_portalUsername = _getParameter(
			_PORTAL_USERNAME_DEFAULT, parameters, "portal.username");

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

		String invocationSHA = _getSHA(description);

		if (invocationSHA == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the commit the upstream testsuite ",
					"for branch ", _branch, " last ran against"));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		metrics.put("last.invocation.sha", invocationSHA);

		JSONObject headCommitJSONObject = null;

		try {
			headCommitJSONObject = _getHeadCommitJSONObject();
		}
		catch (Exception exception) {
			headCommitJSONObject = null;
		}

		if (headCommitJSONObject == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to read the head of branch ", _branch, " from ",
					_portalUsername, "/", _portalRepositoryName));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		String headSHA = headCommitJSONObject.optString("sha");

		metrics.put("branch.head.sha", headSHA);

		if (headSHA.equals(invocationSHA)) {
			return;
		}

		long headCommitTimestamp = _getHeadCommitTimestamp(
			headCommitJSONObject);

		if (headCommitTimestamp <= 0) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine when branch ", _branch,
					" was last merged"));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		long mergeAgeSeconds = (currentTimeMillis - headCommitTimestamp) / 1000;

		metrics.put("branch.head.age.seconds", String.valueOf(mergeAgeSeconds));

		if (mergeAgeSeconds > _triggerLatencySeconds) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Branch ", _branch, " was merged ",
					JenkinsResultsParserUtil.toDurationString(
						mergeAgeSeconds * 1000),
					" ago, but its upstream testsuite last ran against ",
					invocationSHA.substring(0, 7), ", exceeding the expected ",
					"trigger latency of ",
					JenkinsResultsParserUtil.toDurationString(
						_triggerLatencySeconds * 1000)));

			statuses.add(MonitorResult.Status.WARN);
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

	private JSONObject _getHeadCommitJSONObject() throws IOException {
		return JenkinsResultsParserUtil.toJSONObject(
			JenkinsResultsParserUtil.getGitHubAPIURL(
				_portalRepositoryName, _portalUsername, "commits/" + _branch),
			false, _RETRIES_SIZE_MAX, null, null, _SECONDS_RETRY_PERIOD,
			getAttemptTimeoutMillis(_RETRIES_SIZE_MAX), null);
	}

	private long _getHeadCommitTimestamp(JSONObject headCommitJSONObject) {
		JSONObject commitJSONObject = headCommitJSONObject.optJSONObject(
			"commit");

		if (commitJSONObject == null) {
			return 0;
		}

		JSONObject committerJSONObject = commitJSONObject.optJSONObject(
			"committer");

		if (committerJSONObject == null) {
			return 0;
		}

		String date = committerJSONObject.optString("date");

		if (JenkinsResultsParserUtil.isNullOrEmpty(date)) {
			return 0;
		}

		try {
			Instant instant = Instant.parse(date);

			return instant.toEpochMilli();
		}
		catch (Exception exception) {
			return 0;
		}
	}

	private String _getParameter(
		String defaultValue, Map<String, String> parameters, String name) {

		String value = parameters.get(name);

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return defaultValue;
		}

		return value;
	}

	private String _getSHA(String description) {
		Matcher matcher = _commitSHAPattern.matcher(description);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("sha");
	}

	private boolean _isInvocation(String description) {
		Matcher matcher = _gitIDPattern.matcher(description);

		return matcher.find();
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

	private static final String _PORTAL_REPOSITORY_NAME_DEFAULT =
		"liferay-portal";

	private static final String _PORTAL_USERNAME_DEFAULT = "liferay";

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final long _SECONDS_TRIGGER_LATENCY_DEFAULT = 4 * 60 * 60;

	private static final Pattern _commitSHAPattern = Pattern.compile(
		"/commit/(?<sha>[0-9a-f]{40})");
	private static final Pattern _gitIDPattern = Pattern.compile(
		"GIT ID", Pattern.CASE_INSENSITIVE);

	private final String _branch;
	private final long _buildsMaximum;
	private final String _controllerJobName;
	private final String _controllerJobURL;
	private final String _portalRepositoryName;
	private final String _portalUsername;
	private final long _triggerLatencySeconds;

}