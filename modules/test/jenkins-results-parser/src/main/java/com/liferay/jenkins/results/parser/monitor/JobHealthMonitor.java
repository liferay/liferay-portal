/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;

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
		_jenkinsMaster = JenkinsMaster.getInstance(
			getRequiredParameter("master.name", parameters));
		_jobName = getRequiredParameter("job.name", parameters);

		_thresholds = monitorConfig.getThresholds();

		_buildDurationMaximumSeconds = getLongValue(
			"threshold", 0, "build.duration.maximum", _thresholds);
		_overdueGraceSeconds = getOverdueGraceSeconds(
			_cadenceSeconds, _thresholds);
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
					"Unable to read ", _jenkinsMaster.getURL(), ": ",
					exception.getMessage()),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		if (jobJSONObject == null) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"Job ", _jobName, " was not found on ",
					_jenkinsMaster.getURL()),
				null, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		Map<String, String> metrics = new LinkedHashMap<>();

		boolean buildable = jobJSONObject.optBoolean("buildable", true);
		boolean disabled = jobJSONObject.optBoolean("disabled");

		metrics.put("job.buildable", String.valueOf(buildable));
		metrics.put("job.disabled", String.valueOf(disabled));

		if (disabled || !buildable) {
			return new MonitorResult(
				_getUntriggerableMessage(disabled), metrics,
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
				metrics, MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

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

		CronSchedule cronSchedule = null;

		long overdueDeadlineTimestamp = -1;
		long overdueGraceSeconds = _overdueGraceSeconds;

		if (_cadenceSeconds > 0) {
			overdueDeadlineTimestamp =
				currentTimeMillis -
					((_cadenceSeconds + overdueGraceSeconds) * 1000);
		}
		else {
			try {
				cronSchedule = _getCronSchedule();

				if (cronSchedule != null) {
					overdueGraceSeconds = _getOverdueGraceSeconds(
						cronSchedule, currentTimeMillis);

					overdueDeadlineTimestamp = _getOverdueDeadlineTimestamp(
						cronSchedule, currentTimeMillis, overdueGraceSeconds);

					if (overdueDeadlineTimestamp <= 0) {
						messages.add(
							_getUnreachableScheduleMessage(cronSchedule));

						statuses.add(MonitorResult.Status.UNKNOWN);
					}
				}
				else {
					messages.add(_getMissingScheduleMessage());

					statuses.add(MonitorResult.Status.UNKNOWN);
				}
			}
			catch (Exception exception) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Unable to read the schedule for job ", _jobName, ": ",
						exception.getMessage()));

				statuses.add(MonitorResult.Status.UNKNOWN);
			}
		}

		if (overdueDeadlineTimestamp > 0) {
			metrics.put(
				"overdue.deadline.timestamp",
				String.valueOf(overdueDeadlineTimestamp));
		}

		if (buildRunning && (_buildDurationMaximumSeconds > 0)) {
			if (lastBuildAgeSeconds > _buildDurationMaximumSeconds) {
				messages.add(_getDurationExceededMessage(lastBuildAgeSeconds));

				statuses.add(MonitorResult.Status.CRITICAL);
			}
		}
		else if ((overdueDeadlineTimestamp > 0) &&
				 (lastBuildTimestamp < overdueDeadlineTimestamp)) {

			messages.add(
				_getOverdueMessage(
					buildRunning, cronSchedule, lastBuildAgeSeconds,
					overdueGraceSeconds));

			statuses.add(MonitorResult.Status.WARN);
		}

		return _newMonitorResult(
			currentTimeMillis, messages, metrics, statuses);
	}

	@Override
	public void prepareCycle() {
		MasterResourceReader.clearInstances();
	}

	private CronSchedule _getCronSchedule() throws Exception {
		if (_cronScheduleLoaded) {
			return _cronSchedule;
		}

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(_jenkinsMaster.getName());

		Document document = masterResourceReader.getJobConfigDocument(
			_jobName,
			getAttemptTimeoutMillis(MasterResourceReader.RETRIES_SIZE_MAX));

		Node specNode = Dom4JUtil.getNodeByXPath(
			document, "//hudson.triggers.TimerTrigger/spec");

		if (specNode != null) {
			String spec = specNode.getText();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(spec)) {
				_cronSchedule = new CronSchedule(spec);
			}
		}

		_cronScheduleLoaded = true;

		return _cronSchedule;
	}

	private String _getDurationExceededMessage(long lastBuildAgeSeconds) {
		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " has been running for ",
			JenkinsResultsParserUtil.toDurationString(
				lastBuildAgeSeconds * 1000),
			", exceeding its maximum duration of ",
			JenkinsResultsParserUtil.toDurationString(
				_buildDurationMaximumSeconds * 1000));
	}

	private JSONObject _getJobJSONObject() throws IOException {
		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(_jenkinsMaster.getName());

		Map<String, JSONObject> jobJSONObjects =
			masterResourceReader.getJobJSONObjects(
				getAttemptTimeoutMillis(MasterResourceReader.RETRIES_SIZE_MAX));

		return jobJSONObjects.get(_jobName);
	}

	private long _getLastBuildTimestamp(
		JSONObject lastBuildJSONObject,
		JSONObject lastCompletedBuildJSONObject) {

		if (lastBuildJSONObject != null) {
			return lastBuildJSONObject.optLong("timestamp");
		}

		return lastCompletedBuildJSONObject.optLong("timestamp");
	}

	private String _getMissingScheduleMessage() {
		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " has no cadence and no schedule, so it is not ",
			"checked for being on time");
	}

	private long _getOverdueDeadlineTimestamp(
		CronSchedule cronSchedule, long currentTimeMillis,
		long overdueGraceSeconds) {

		long toleranceSeconds =
			overdueGraceSeconds + cronSchedule.getHashSpanSeconds();

		return cronSchedule.getPreviousFireTimestamp(
			currentTimeMillis - (toleranceSeconds * 1000));
	}

	private long _getOverdueGraceSeconds(
		CronSchedule cronSchedule, long currentTimeMillis) {

		long periodSeconds = cronSchedule.getPeriodSeconds(currentTimeMillis);

		if (periodSeconds <= 0) {
			return _overdueGraceSeconds;
		}

		return getOverdueGraceSeconds(periodSeconds, _thresholds);
	}

	private String _getOverdueMessage(
		boolean buildRunning, CronSchedule cronSchedule,
		long lastBuildAgeSeconds, long overdueGraceSeconds) {

		if (buildRunning) {
			return JenkinsResultsParserUtil.combine(
				"Job ", _jobName, " has been running for ",
				JenkinsResultsParserUtil.toDurationString(
					lastBuildAgeSeconds * 1000),
				", ", _getScheduleMessage(cronSchedule, overdueGraceSeconds));
		}

		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " last ran ",
			JenkinsResultsParserUtil.toDurationString(
				lastBuildAgeSeconds * 1000),
			" ago, ", _getScheduleMessage(cronSchedule, overdueGraceSeconds));
	}

	private String _getScheduleMessage(
		CronSchedule cronSchedule, long overdueGraceSeconds) {

		if (_cadenceSeconds > 0) {
			return JenkinsResultsParserUtil.combine(
				"exceeding its cadence of ",
				JenkinsResultsParserUtil.toDurationString(
					_cadenceSeconds * 1000),
				" plus a grace period of ",
				JenkinsResultsParserUtil.toDurationString(
					overdueGraceSeconds * 1000));
		}

		return JenkinsResultsParserUtil.combine(
			"exceeding its schedule of ", cronSchedule.getSpec(),
			" plus a grace period of ",
			JenkinsResultsParserUtil.toDurationString(
				overdueGraceSeconds * 1000));
	}

	private String _getUnreachableScheduleMessage(CronSchedule cronSchedule) {
		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " has the schedule ", cronSchedule.getSpec(),
			", which never comes round");
	}

	private String _getUntriggerableMessage(boolean disabled) {
		if (disabled) {
			return JenkinsResultsParserUtil.combine(
				"Job ", _jobName, " is disabled");
		}

		return JenkinsResultsParserUtil.combine(
			"Job ", _jobName, " is not buildable");
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

	private final long _buildDurationMaximumSeconds;
	private final long _cadenceSeconds;
	private CronSchedule _cronSchedule;
	private boolean _cronScheduleLoaded;
	private final boolean _expectedGreen;
	private final JenkinsMaster _jenkinsMaster;
	private final String _jobName;
	private final long _overdueGraceSeconds;
	private final Map<String, String> _thresholds;

}