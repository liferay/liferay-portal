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
 * @author Brittney Nguyen
 */
public class ControllerResultMonitor extends BaseMonitor {

	public ControllerResultMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		Map<String, String> parameters = monitorConfig.getParameters();

		_jenkinsMaster = JenkinsMaster.getInstance(
			getRequiredParameter("master.name", parameters));
		_jobName = getRequiredParameter("job.name", parameters);

		_jobURL = JenkinsResultsParserUtil.combine(
			_jenkinsMaster.getURL(), "/job/", _jobName);

		_thresholds = monitorConfig.getThresholds();

		_buildsMaximum = getLongValue(
			"threshold", 30, "builds.maximum", _thresholds);
		_pendingMaximumSeconds = getLongValue(
			"threshold", 24 * 60 * 60, "pending.maximum", _thresholds);
	}

	@Override
	public MonitorResult execute() {
		JSONArray buildsJSONArray = null;

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			buildsJSONArray = _getBuildsJSONArray();
		}
		catch (Exception exception) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Unable to read ", _jobURL, ": ",
					JenkinsResultsParserUtil.getMessage(exception)),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (buildsJSONArray == null) {
			return new MonitorResult(
				"Unable to read " + _jobURL, null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (buildsJSONArray.length() == 0) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " has never run"),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		List<String> messages = new ArrayList<>();
		Map<String, String> metrics = new LinkedHashMap<>();
		List<MonitorResult.Status> statuses = new ArrayList<>();

		_checkSchedule(
			currentTimeMillis, buildsJSONArray.getJSONObject(0), messages,
			metrics, statuses);

		JSONObject resultBuildJSONObject = null;
		int skippedBuildsCount = 0;

		for (int i = 0; i < buildsJSONArray.length(); i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			if (_hasResult(buildJSONObject)) {
				resultBuildJSONObject = buildJSONObject;

				break;
			}

			skippedBuildsCount++;
		}

		metrics.put("skipped.builds.count", String.valueOf(skippedBuildsCount));

		if (resultBuildJSONObject != null) {
			_checkResultBuild(
				resultBuildJSONObject, currentTimeMillis, messages, metrics,
				statuses);
		}
		else if (!_isBranchUnchanged(buildsJSONArray)) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to find a result for job ", _jobName, " in its ",
					"last ", String.valueOf(buildsJSONArray.length()),
					" builds"));

			statuses.add(MonitorResult.Status.UNKNOWN);
		}

		return _newMonitorResult(
			currentTimeMillis, messages, metrics, statuses);
	}

	@Override
	public void prepareCycle() {
		MasterResourceReader.clearInstances();
	}

	@Override
	protected int getReadsCount() {
		return 3;
	}

	private void _checkDownstreamBuild(
		long ageSeconds, String buildURL, List<String> messages, int number,
		List<MonitorResult.Status> statuses) {

		if (buildURL == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the downstream build of job ",
					_jobName, " build ", String.valueOf(number)));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		JSONObject buildJSONObject = null;

		try {
			buildJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(
					buildURL + "/api/json?tree=building,result"),
				false, _RETRIES_SIZE_MAX, null, null, _SECONDS_RETRY_PERIOD,
				getAttemptTimeoutMillis(_RETRIES_SIZE_MAX), null);
		}
		catch (Exception exception) {
			buildJSONObject = null;
		}

		if (buildJSONObject == null) {
			messages.add("Unable to read " + buildURL);

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		if (buildJSONObject.optBoolean("building")) {
			if (ageSeconds > _pendingMaximumSeconds) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Job ", _jobName, " build ", String.valueOf(number),
						" has been in progress for ",
						JenkinsResultsParserUtil.toDurationString(
							ageSeconds * 1000),
						" at ", buildURL));

				statuses.add(MonitorResult.Status.WARN);
			}

			return;
		}

		messages.add(
			JenkinsResultsParserUtil.combine(
				"Job ", _jobName, " build ", String.valueOf(number),
				" invoked ", buildURL, ", which finished with the result ",
				buildJSONObject.optString("result"),
				" without reporting back"));

		statuses.add(MonitorResult.Status.CRITICAL);
	}

	private void _checkResultBuild(
		JSONObject buildJSONObject, long currentTimeMillis,
		List<String> messages, Map<String, String> metrics,
		List<MonitorResult.Status> statuses) {

		int number = buildJSONObject.optInt("number");

		metrics.put("last.result.build.number", String.valueOf(number));

		String description = buildJSONObject.optString("description");

		String status = _getStatus(description);

		if (status == null) {
			String result = buildJSONObject.optString("result");

			metrics.put("last.result", result);

			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " build ", String.valueOf(number),
					" completed with the result ", result,
					" without invoking a downstream build"));

			if (result.equals("ABORTED")) {
				statuses.add(MonitorResult.Status.WARN);
			}
			else {
				statuses.add(MonitorResult.Status.CRITICAL);
			}

			return;
		}

		metrics.put("last.result", status);

		if (status.equals("SUCCESS")) {
			return;
		}

		long ageSeconds =
			(currentTimeMillis - buildJSONObject.optLong("timestamp")) / 1000;

		if (status.equals("IN QUEUE")) {
			if (ageSeconds > _pendingMaximumSeconds) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Job ", _jobName, " build ", String.valueOf(number),
						" invoked its downstream build ",
						JenkinsResultsParserUtil.toDurationString(
							ageSeconds * 1000),
						" ago and it has not started"));

				statuses.add(MonitorResult.Status.WARN);
			}

			return;
		}

		String buildURL = _getBuildURL(description);

		if (status.equals("IN PROGRESS")) {
			_checkDownstreamBuild(
				ageSeconds, buildURL, messages, number, statuses);

			return;
		}

		if (buildURL == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " build ", String.valueOf(number),
					" reported ", status));
		}
		else {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " build ", String.valueOf(number),
					" reported ", status, " for ", buildURL));
		}

		statuses.add(MonitorResult.Status.CRITICAL);
	}

	private void _checkSchedule(
		long currentTimeMillis, JSONObject lastBuildJSONObject,
		List<String> messages, Map<String, String> metrics,
		List<MonitorResult.Status> statuses) {

		long lastBuildTimestamp = lastBuildJSONObject.optLong("timestamp");

		if (lastBuildTimestamp <= 0) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to determine the last build timestamp for job ",
					_jobName));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		long lastBuildAgeSeconds =
			(currentTimeMillis - lastBuildTimestamp) / 1000;

		metrics.put(
			"last.build.age.seconds", String.valueOf(lastBuildAgeSeconds));

		CronSchedule cronSchedule = null;

		try {
			cronSchedule = getCronSchedule(_jobName, _jenkinsMaster.getName());
		}
		catch (Exception exception) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Unable to read the schedule for job ", _jobName, ": ",
					JenkinsResultsParserUtil.getMessage(exception)));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		if (cronSchedule == null) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " has no schedule, so it is not ",
					"checked for being on time"));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		long overdueGraceSeconds = getOverdueGraceSeconds(
			cronSchedule, currentTimeMillis, _thresholds);

		long overdueDeadlineTimestamp = getOverdueDeadlineTimestamp(
			cronSchedule, currentTimeMillis, overdueGraceSeconds);

		if (overdueDeadlineTimestamp <= 0) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " has the schedule ",
					cronSchedule.getSpec(), ", which never comes round"));

			statuses.add(MonitorResult.Status.UNKNOWN);

			return;
		}

		if (lastBuildTimestamp < overdueDeadlineTimestamp) {
			messages.add(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " last ran ",
					JenkinsResultsParserUtil.toDurationString(
						lastBuildAgeSeconds * 1000),
					" ago, exceeding its schedule of ", cronSchedule.getSpec(),
					" plus a grace period of ",
					JenkinsResultsParserUtil.toDurationString(
						overdueGraceSeconds * 1000)));

			statuses.add(MonitorResult.Status.WARN);
		}
	}

	private String _getBuildURL(String description) {
		Matcher matcher = _buildURLPattern.matcher(description);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("buildURL");
	}

	private JSONArray _getBuildsJSONArray() throws IOException {
		JSONObject jobJSONObject = JenkinsResultsParserUtil.toJSONObject(
			JenkinsResultsParserUtil.combine(
				_jobURL, "/api/json?tree=",
				"builds[description,number,result,timestamp]{0,",
				String.valueOf(_buildsMaximum), "}"),
			false, _RETRIES_SIZE_MAX, null, null, _SECONDS_RETRY_PERIOD,
			getAttemptTimeoutMillis(_RETRIES_SIZE_MAX), null);

		if (jobJSONObject == null) {
			return null;
		}

		return jobJSONObject.optJSONArray("builds");
	}

	private String _getStatus(String description) {
		Matcher matcher = _statusPattern.matcher(description);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("status");
	}

	private boolean _hasResult(JSONObject buildJSONObject) {
		String status = _getStatus(buildJSONObject.optString("description"));

		if (status == null) {
			String result = buildJSONObject.optString("result");

			if (JenkinsResultsParserUtil.isNullOrEmpty(result) ||
				result.equals("SUCCESS")) {

				return false;
			}

			return true;
		}

		if (status.equals("EXPIRE") || status.equals("SKIPPED")) {
			return false;
		}

		return true;
	}

	private boolean _isBranchUnchanged(JSONArray buildsJSONArray) {
		for (int i = 0; i < buildsJSONArray.length(); i++) {
			JSONObject buildJSONObject = buildsJSONArray.getJSONObject(i);

			String description = buildJSONObject.optString("description");

			String status = _getStatus(description);

			if (status == null) {
				continue;
			}

			if (status.equals("SKIPPED") &&
				description.endsWith(" was already ran")) {

				return true;
			}

			return false;
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

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final Pattern _buildURLPattern = Pattern.compile(
		"(?<buildURL>https?://[^/\"\\s]+/job/[^/\"\\s]+/\\d+)");
	private static final Pattern _statusPattern = Pattern.compile(
		"<strong[^>]*>(?<status>[A-Z ]+)</strong>");

	private final long _buildsMaximum;
	private final JenkinsMaster _jenkinsMaster;
	private final String _jobName;
	private final String _jobURL;
	private final long _pendingMaximumSeconds;
	private final Map<String, String> _thresholds;

}