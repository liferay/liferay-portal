/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class SpotInterruptionReport extends BaseReport {

	public static JSONObject newDailyDataJSONObject(
		List<JSONObject> bidEvictedEventJSONObjects, String dateString,
		List<JSONObject> runInstancesEventJSONObjects) {

		JSONArray evictionsJSONArray = new JSONArray();

		for (JSONObject bidEvictedEventJSONObject :
				bidEvictedEventJSONObjects) {

			if (!dateString.equals(
					_getEventDateString(bidEvictedEventJSONObject))) {

				continue;
			}

			JSONObject serviceEventDetailsJSONObject =
				bidEvictedEventJSONObject.optJSONObject("serviceEventDetails");

			if (serviceEventDetailsJSONObject == null) {
				continue;
			}

			JSONArray instanceIdSetJSONArray =
				serviceEventDetailsJSONObject.optJSONArray("instanceIdSet");

			if (instanceIdSetJSONArray == null) {
				continue;
			}

			for (int i = 0; i < instanceIdSetJSONArray.length(); i++) {
				evictionsJSONArray.put(instanceIdSetJSONArray.getString(i));
			}
		}

		JSONArray launchesJSONArray = new JSONArray();

		for (JSONObject runInstancesEventJSONObject :
				runInstancesEventJSONObjects) {

			if (!dateString.equals(
					_getEventDateString(runInstancesEventJSONObject)) ||
				runInstancesEventJSONObject.has("errorCode")) {

				continue;
			}

			JSONObject responseElementsJSONObject =
				runInstancesEventJSONObject.optJSONObject("responseElements");

			if (responseElementsJSONObject == null) {
				continue;
			}

			JSONObject instancesSetJSONObject =
				responseElementsJSONObject.optJSONObject("instancesSet");

			if (instancesSetJSONObject == null) {
				continue;
			}

			JSONArray itemsJSONArray = instancesSetJSONObject.optJSONArray(
				"items");

			if (itemsJSONArray == null) {
				continue;
			}

			for (int i = 0; i < itemsJSONArray.length(); i++) {
				JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

				String asgName = _getASGName(itemJSONObject);

				launchesJSONArray.put(
					new JSONObject(
					).put(
						"asgType", _getASGType(asgName)
					).put(
						"instanceId", itemJSONObject.getString("instanceId")
					).put(
						"instanceType", itemJSONObject.getString("instanceType")
					).put(
						"master", _getMasterName(asgName)
					));
			}
		}

		return new JSONObject(
		).put(
			"date", dateString
		).put(
			"evictions", evictionsJSONArray
		).put(
			"launches", launchesJSONArray
		);
	}

	public static SpotInterruptionReport newSpotInterruptionReport(
		File dailyDataBaseDir, long durationDays, File outputDir,
		String startDateString) {

		SpotInterruptionReport spotInterruptionReport =
			new SpotInterruptionReport(outputDir);

		spotInterruptionReport.addFilesFromResource(
			"dependencies/metrics/spot-interruption-report", "/css/main.css",
			"/index.html", "/js/main.js");

		List<JSONObject> dailyDataJSONObjects = new ArrayList<>();

		for (String dateString :
				JenkinsResultsParserUtil.getDateStrings(
					durationDays,
					LocalDate.parse(
						startDateString,
						DateTimeFormatter.ofPattern("yyyyMMdd")))) {

			File dailyDataFile = new File(
				dailyDataBaseDir, dateString + "/spot-interruption.json");

			if (!dailyDataFile.exists()) {
				System.out.println("Unable to find " + dailyDataFile);

				continue;
			}

			try {
				dailyDataJSONObjects.add(
					new JSONObject(
						JenkinsResultsParserUtil.read(dailyDataFile)));
			}
			catch (IOException | JSONException exception) {
				System.out.println("Unable to read " + dailyDataFile);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("var dataGeneratedDate = new Date(");
		sb.append(JenkinsResultsParserUtil.getCurrentTimeMillis());
		sb.append(");\nvar reportName = \"Spot Interruption Report\";\n");
		sb.append("var dailyTally = ");
		sb.append(_newDailyTallyJSONArray(dailyDataJSONObjects));
		sb.append(";");

		spotInterruptionReport.addFile(sb.toString(), "js/table-data.js");

		return spotInterruptionReport;
	}

	public SpotInterruptionReport(File outputDir) {
		super(outputDir);
	}

	private static String _getASGName(JSONObject instanceItemJSONObject) {
		JSONObject tagSetJSONObject = instanceItemJSONObject.optJSONObject(
			"tagSet");

		if (tagSetJSONObject == null) {
			return null;
		}

		JSONArray itemsJSONArray = tagSetJSONObject.optJSONArray("items");

		if (itemsJSONArray == null) {
			return null;
		}

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			String key = itemJSONObject.optString("key");

			if (key.equals("aws:autoscaling:groupName")) {
				return itemJSONObject.optString("value");
			}
		}

		return null;
	}

	private static String _getASGType(String asgName) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(asgName)) {
			return "unknown";
		}

		if (asgName.contains("bundle-builder")) {
			return "bundle-builder";
		}

		if (asgName.contains("-io-")) {
			return "io";
		}

		if (asgName.contains("-mem-")) {
			return "mem";
		}

		if (asgName.contains("-pco-")) {
			return "pco";
		}

		return "slave";
	}

	private static String _getEventDateString(JSONObject eventJSONObject) {
		String eventTime = eventJSONObject.optString("eventTime");

		if (eventTime.length() < 10) {
			return "";
		}

		String dateString = eventTime.substring(0, 10);

		return dateString.replace("-", "");
	}

	private static String _getMasterASGTypeKey(
		String asgType, String masterName) {

		return JenkinsResultsParserUtil.combine(masterName, "/", asgType);
	}

	private static String _getMasterName(String asgName) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(asgName)) {
			return "unknown";
		}

		Matcher matcher = _masterNamePattern.matcher(asgName);

		if (!matcher.find()) {
			return "unknown";
		}

		return matcher.group(1);
	}

	private static void _increment(String key, JSONObject tallyJSONObject) {
		tallyJSONObject.put(key, tallyJSONObject.optInt(key) + 1);
	}

	private static JSONArray _newDailyTallyJSONArray(
		List<JSONObject> dailyDataJSONObjects) {

		JSONArray dailyTallyJSONArray = new JSONArray();

		Map<String, JSONObject> launchJSONObjectsMap = new HashMap<>();

		for (JSONObject dailyDataJSONObject : dailyDataJSONObjects) {
			JSONArray launchesJSONArray = dailyDataJSONObject.getJSONArray(
				"launches");

			for (int i = 0; i < launchesJSONArray.length(); i++) {
				JSONObject launchJSONObject = launchesJSONArray.getJSONObject(
					i);

				launchJSONObjectsMap.put(
					launchJSONObject.getString("instanceId"), launchJSONObject);
			}
		}

		for (JSONObject dailyDataJSONObject : dailyDataJSONObjects) {
			JSONObject launchesByASGTypeJSONObject = new JSONObject();
			JSONObject launchesByInstanceTypeJSONObject = new JSONObject();
			JSONObject launchesByMasterASGTypeJSONObject = new JSONObject();
			JSONObject launchesByMasterJSONObject = new JSONObject();

			JSONArray launchesJSONArray = dailyDataJSONObject.getJSONArray(
				"launches");

			for (int i = 0; i < launchesJSONArray.length(); i++) {
				JSONObject launchJSONObject = launchesJSONArray.getJSONObject(
					i);

				_increment(
					launchJSONObject.getString("asgType"),
					launchesByASGTypeJSONObject);
				_increment(
					launchJSONObject.getString("instanceType"),
					launchesByInstanceTypeJSONObject);
				_increment(
					_getMasterASGTypeKey(
						launchJSONObject.getString("asgType"),
						launchJSONObject.getString("master")),
					launchesByMasterASGTypeJSONObject);
				_increment(
					launchJSONObject.getString("master"),
					launchesByMasterJSONObject);
			}

			JSONObject evictionsByASGTypeJSONObject = new JSONObject();
			JSONObject evictionsByInstanceTypeJSONObject = new JSONObject();
			JSONObject evictionsByMasterASGTypeJSONObject = new JSONObject();
			JSONObject evictionsByMasterJSONObject = new JSONObject();

			JSONArray evictionsJSONArray = dailyDataJSONObject.getJSONArray(
				"evictions");

			for (int i = 0; i < evictionsJSONArray.length(); i++) {
				String asgType = "unknown";
				String instanceType = "unknown";
				String masterName = "unknown";

				JSONObject launchJSONObject = launchJSONObjectsMap.get(
					evictionsJSONArray.getString(i));

				if (launchJSONObject != null) {
					asgType = launchJSONObject.getString("asgType");
					instanceType = launchJSONObject.getString("instanceType");
					masterName = launchJSONObject.getString("master");
				}

				_increment(asgType, evictionsByASGTypeJSONObject);
				_increment(instanceType, evictionsByInstanceTypeJSONObject);
				_increment(
					_getMasterASGTypeKey(asgType, masterName),
					evictionsByMasterASGTypeJSONObject);
				_increment(masterName, evictionsByMasterJSONObject);
			}

			dailyTallyJSONArray.put(
				new JSONObject(
				).put(
					"date", dailyDataJSONObject.getString("date")
				).put(
					"evictionsByASGType", evictionsByASGTypeJSONObject
				).put(
					"evictionsByInstanceType", evictionsByInstanceTypeJSONObject
				).put(
					"evictionsByMaster", evictionsByMasterJSONObject
				).put(
					"evictionsByMasterASGType",
					evictionsByMasterASGTypeJSONObject
				).put(
					"launchesByASGType", launchesByASGTypeJSONObject
				).put(
					"launchesByInstanceType", launchesByInstanceTypeJSONObject
				).put(
					"launchesByMaster", launchesByMasterJSONObject
				).put(
					"launchesByMasterASGType", launchesByMasterASGTypeJSONObject
				));
		}

		return dailyTallyJSONArray;
	}

	private static final Pattern _masterNamePattern = Pattern.compile(
		"(test-\\d+-\\d+)");

}