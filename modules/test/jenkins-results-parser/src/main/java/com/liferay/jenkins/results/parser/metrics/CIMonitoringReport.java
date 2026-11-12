/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.aws.AmazonPrometheusUtil;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class CIMonitoringReport extends BaseReport {

	public static CIMonitoringReport newCIMonitoringReport(
			String instance, File outputDir, String workspaceURL)
		throws IOException {

		CIMonitoringReport ciMonitoringReport = new CIMonitoringReport(
			outputDir);

		ciMonitoringReport.addFilesFromResource(
			"dependencies/metrics/ci-monitoring-report", "/css/main.css",
			"/index.html", "/js/main.js");

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		long endTime = ((currentTimeMillis / 1000) / _STEP) * _STEP;

		long startTime = endTime - (7 * _DURATION_DAY);

		String selector = JenkinsResultsParserUtil.combine(
			"{instance=\"", instance, "\"}");

		Map<String, JSONObject> metricJSONObjects = new TreeMap<>();

		Map<String, Map<Long, Double>> durationsMap = _getValuesMap(
			null,
			AmazonPrometheusUtil.queryRange(
				endTime,
				JenkinsResultsParserUtil.combine(
					"max_over_time(monitor_duration_seconds", selector,
					"[5m])"),
				startTime, _STEP, workspaceURL));
		Map<String, Map<Long, Double>> lastRunTimesMap = _getValuesMap(
			null,
			AmazonPrometheusUtil.queryRange(
				endTime, "monitor_last_run_timestamp_seconds" + selector,
				startTime, _STEP, workspaceURL));
		Map<String, Map<Long, Double>> statusesMap = _getValuesMap(
			metricJSONObjects,
			AmazonPrometheusUtil.queryRange(
				endTime, "monitor_status" + selector, startTime, _STEP,
				workspaceURL));

		Map<String, Double> currentLastRunAges = _getCurrentValues(
			AmazonPrometheusUtil.query(
				"time() - monitor_last_run_timestamp_seconds" + selector,
				workspaceURL));
		Map<String, Double> currentStatuses = _getCurrentValues(
			AmazonPrometheusUtil.query(
				"monitor_status" + selector, workspaceURL));

		JSONArray monitorsJSONArray = new JSONArray();
		Map<String, Map<Long, Double>> typeDurationsMap = new TreeMap<>();

		Map<String, JSONObject> messageInfoJSONObjects =
			_getMessageInfoJSONObjects(
				AmazonPrometheusUtil.query(
					"monitor_message_info" + selector, workspaceURL));

		for (Map.Entry<String, JSONObject> entry :
				metricJSONObjects.entrySet()) {

			String monitor = entry.getKey();

			Map<Long, Double> durations = durationsMap.get(monitor);

			if (durations == null) {
				durations = new HashMap<>();
			}

			JSONObject metricJSONObject = entry.getValue();

			String type = metricJSONObject.optString("type");

			Map<Long, Double> typeDurations = typeDurationsMap.get(type);

			if (typeDurations == null) {
				typeDurations = new HashMap<>();

				typeDurationsMap.put(type, typeDurations);
			}

			for (Map.Entry<Long, Double> durationEntry : durations.entrySet()) {
				Double typeDuration = typeDurations.get(durationEntry.getKey());

				if ((typeDuration != null) &&
					(typeDuration >= durationEntry.getValue())) {

					continue;
				}

				typeDurations.put(
					durationEntry.getKey(), durationEntry.getValue());
			}

			JSONObject messageInfoJSONObject = messageInfoJSONObjects.get(
				monitor);

			if (messageInfoJSONObject == null) {
				messageInfoJSONObject = new JSONObject();
			}

			Object currentStatus = JSONObject.NULL;
			Double currentStatusValue = currentStatuses.get(monitor);

			if (currentStatusValue != null) {
				currentStatus = _getStatus(
					currentLastRunAges.get(monitor), currentStatusValue);
			}

			Map<Long, Double> statuses = statusesMap.get(monitor);

			monitorsJSONArray.put(
				new JSONObject(
				).put(
					"firstSampleTime", Collections.min(statuses.keySet())
				).put(
					"lastSampleTime", Collections.max(statuses.keySet())
				).put(
					"links", messageInfoJSONObject.optJSONArray("links")
				).put(
					"message", messageInfoJSONObject.optString("message", null)
				).put(
					"monitor", monitor
				).put(
					"runs",
					_getRunsJSONArray(
						endTime, lastRunTimesMap.get(monitor), startTime,
						statuses)
				).put(
					"status", currentStatus
				).put(
					"type", type
				));
		}

		JSONObject typeDurationsJSONObject = new JSONObject();

		for (Map.Entry<String, Map<Long, Double>> entry :
				typeDurationsMap.entrySet()) {

			typeDurationsJSONObject.put(
				entry.getKey(),
				new JSONObject(
				).put(
					"day",
					_getSeriesJSONObject(
						endTime, endTime - _DURATION_DAY + _STEP, _STEP,
						entry.getValue())
				).put(
					"week",
					_getSeriesJSONObject(
						endTime, startTime + _DURATION_HOUR - _STEP,
						_DURATION_HOUR, entry.getValue())
				));
		}

		Double heartbeatAge = null;

		JSONArray heartbeatAgeJSONArray = AmazonPrometheusUtil.query(
			"time() - monitor_heartbeat_timestamp_seconds" + selector,
			workspaceURL);

		if (!heartbeatAgeJSONArray.isEmpty()) {
			heartbeatAge = _getValue(heartbeatAgeJSONArray.getJSONObject(0));
		}

		JSONObject reportDataJSONObject = new JSONObject(
		).put(
			"durationsByType", typeDurationsJSONObject
		).put(
			"end", endTime
		).put(
			"heartbeatAge", _round(heartbeatAge)
		).put(
			"monitors", monitorsJSONArray
		).put(
			"staleSeconds", _STALE_SECONDS
		).put(
			"start", startTime
		).put(
			"step", _STEP
		);

		StringBuilder sb = new StringBuilder();

		sb.append("var dataGeneratedDate = new Date(");
		sb.append(currentTimeMillis);
		sb.append(");\nvar reportName = \"CI Monitoring Report\";\n");
		sb.append("var reportData = ");
		sb.append(reportDataJSONObject);
		sb.append(";");

		ciMonitoringReport.addFile(sb.toString(), "js/report-data.js");

		return ciMonitoringReport;
	}

	public CIMonitoringReport(File outputDir) {
		super(outputDir);
	}

	private static Map<String, Double> _getCurrentValues(
		JSONArray resultJSONArray) {

		Map<String, Double> currentValues = new HashMap<>();

		for (int i = 0; i < resultJSONArray.length(); i++) {
			JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);

			JSONObject metricJSONObject = resultJSONObject.getJSONObject(
				"metric");

			currentValues.put(
				metricJSONObject.optString("monitor"),
				_getValue(resultJSONObject));
		}

		return currentValues;
	}

	private static Map<String, JSONObject> _getMessageInfoJSONObjects(
		JSONArray resultJSONArray) {

		Map<String, JSONObject> messageInfoJSONObjects = new HashMap<>();

		for (int i = 0; i < resultJSONArray.length(); i++) {
			JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);

			JSONObject metricJSONObject = resultJSONObject.getJSONObject(
				"metric");

			JSONObject messageInfoJSONObject = new JSONObject();

			String links = metricJSONObject.optString("links");

			if (!links.isEmpty()) {
				messageInfoJSONObject.put(
					"links", new JSONArray(links.split("\n")));
			}

			messageInfoJSONObject.put(
				"message", metricJSONObject.optString("message"));

			messageInfoJSONObjects.put(
				metricJSONObject.optString("monitor"), messageInfoJSONObject);
		}

		return messageInfoJSONObjects;
	}

	private static JSONArray _getRunsJSONArray(
		long endTime, Map<Long, Double> lastRunTimes, long startTime,
		Map<Long, Double> statuses) {

		JSONArray runsJSONArray = new JSONArray();

		long runStartTime = startTime;
		int runStatus = 0;

		for (long time = startTime; time <= endTime; time += _STEP) {
			Double lastRunAge = null;

			if (lastRunTimes != null) {
				Double lastRunTime = lastRunTimes.get(time);

				if (lastRunTime != null) {
					lastRunAge = time - lastRunTime;
				}
			}

			Double status = null;

			if (statuses != null) {
				status = statuses.get(time);
			}

			int currentStatus = _getStatus(lastRunAge, status);

			if (currentStatus == runStatus) {
				continue;
			}

			if (runStatus != 0) {
				runsJSONArray.put(
					new JSONArray(
					).put(
						runStartTime
					).put(
						time
					).put(
						runStatus
					));
			}

			runStartTime = time;
			runStatus = currentStatus;
		}

		if (runStatus != 0) {
			runsJSONArray.put(
				new JSONArray(
				).put(
					runStartTime
				).put(
					endTime + _STEP
				).put(
					runStatus
				));
		}

		return runsJSONArray;
	}

	private static JSONObject _getSeriesJSONObject(
		long endTime, long seriesStartTime, long seriesStep,
		Map<Long, Double> values) {

		JSONArray valuesJSONArray = new JSONArray();

		for (long time = seriesStartTime; time <= endTime; time += seriesStep) {
			Double max = null;

			for (long valueTime = time - seriesStep + _STEP; valueTime <= time;
				 valueTime += _STEP) {

				Double value = values.get(valueTime);

				if ((value == null) || ((max != null) && (max >= value))) {
					continue;
				}

				max = value;
			}

			if (max == null) {
				valuesJSONArray.put(JSONObject.NULL);

				continue;
			}

			valuesJSONArray.put(_round(max));
		}

		return new JSONObject(
		).put(
			"start", seriesStartTime
		).put(
			"step", seriesStep
		).put(
			"values", valuesJSONArray
		);
	}

	private static int _getStatus(Double lastRunAge, Double status) {
		if ((lastRunAge != null) && (lastRunAge > _STALE_SECONDS)) {
			return _STATUS_STALE;
		}

		if (status == null) {
			return 0;
		}

		return status.intValue();
	}

	private static Double _getValue(JSONObject resultJSONObject) {
		JSONArray valueJSONArray = resultJSONObject.getJSONArray("value");

		return Double.valueOf(valueJSONArray.getString(1));
	}

	private static Map<String, Map<Long, Double>> _getValuesMap(
		Map<String, JSONObject> metricJSONObjects, JSONArray resultJSONArray) {

		Map<String, Map<Long, Double>> valuesMap = new HashMap<>();

		for (int i = 0; i < resultJSONArray.length(); i++) {
			JSONObject resultJSONObject = resultJSONArray.getJSONObject(i);

			JSONObject metricJSONObject = resultJSONObject.getJSONObject(
				"metric");

			String monitor = metricJSONObject.optString("monitor");

			if (metricJSONObjects != null) {
				metricJSONObjects.put(monitor, metricJSONObject);
			}

			Map<Long, Double> values = new HashMap<>();

			JSONArray valuesJSONArray = resultJSONObject.getJSONArray("values");

			for (int j = 0; j < valuesJSONArray.length(); j++) {
				JSONArray valueJSONArray = valuesJSONArray.getJSONArray(j);

				values.put(
					valueJSONArray.getLong(0),
					Double.valueOf(valueJSONArray.getString(1)));
			}

			valuesMap.put(monitor, values);
		}

		return valuesMap;
	}

	private static Object _round(Double value) {
		if (value == null) {
			return JSONObject.NULL;
		}

		return Math.round(value * 1000) / 1000.0;
	}

	private static final long _DURATION_DAY = 24 * 60 * 60;

	private static final long _DURATION_HOUR = 60 * 60;

	private static final long _STALE_SECONDS = 22 * 60;

	private static final int _STATUS_STALE = 4;

	private static final long _STEP = 5 * 60;

}