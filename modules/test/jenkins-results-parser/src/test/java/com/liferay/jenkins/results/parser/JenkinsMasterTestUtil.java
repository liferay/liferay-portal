/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Calum Ragan
 */
public class JenkinsMasterTestUtil {

	public static JSONObject getBuiltInComputerJSONObject(
		JSONObject... oneOffExecutorJSONObjects) {

		return _getComputerJSONObject(
			"hudson.model.Hudson$MasterComputer", "Built-In Node",
			new JSONArray(), false, "",
			new JSONArray(oneOffExecutorJSONObjects));
	}

	public static JSONObject getComputerAPIJSONObject(
		int busyExecutorsCount, JSONObject... computerJSONObjects) {

		return new JSONObject(
		).put(
			"busyExecutors", busyExecutorsCount
		).put(
			"computer", new JSONArray(computerJSONObjects)
		);
	}

	public static JSONObject getComputerJSONObject(
		String displayName, JSONObject... executorJSONObjects) {

		return _getComputerJSONObject(
			"hudson.slaves.SlaveComputer", displayName,
			new JSONArray(executorJSONObjects), false, "", new JSONArray());
	}

	public static JSONObject getExecutorJSONObject(
		String buildURL, long estimatedDuration, String fullDisplayName,
		boolean likelyStuck, long timestamp) {

		JSONObject currentExecutableJSONObject = new JSONObject(
		).put(
			"building", true
		).put(
			"estimatedDuration", estimatedDuration
		).put(
			"fullDisplayName", fullDisplayName
		).put(
			"timestamp", timestamp
		).put(
			"url", buildURL
		);

		return new JSONObject(
		).put(
			"currentExecutable", currentExecutableJSONObject
		).put(
			"likelyStuck", likelyStuck
		);
	}

	public static Properties getJenkinsCohortProperties(
		String cohortName, int masterCount) {

		Properties properties = new Properties();

		properties.setProperty(
			"base.invocation.url", "http://" + cohortName + ".liferay.com");

		for (int i = 1; i <= masterCount; i++) {
			String masterName = cohortName + "-" + i;

			properties.setProperty(
				"jenkins.local.url[" + masterName + "]",
				"http://" + masterName);
			properties.setProperty(
				"jenkins.remote.url[" + masterName + "]",
				"http://" + masterName + ".liferay.com");
			properties.setProperty(
				"master.property(" + masterName + "/executors.size)", "8");
		}

		JenkinsResultsParserUtil.setBuildProperties(properties);

		resetCaches();

		return properties;
	}

	public static JenkinsMaster getJenkinsMaster(
		String masterName, String masterURL) {

		Properties properties = new Properties();

		properties.setProperty(
			"jenkins.local.url[" + masterName + "]", masterURL);
		properties.setProperty(
			"jenkins.remote.url[" + masterName + "]",
			"http://" + masterName + ".liferay.com");

		JenkinsResultsParserUtil.setBuildProperties(properties);

		resetCaches();

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(masterName);

		ReflectionTestUtil.setFieldValue(
			jenkinsMaster, "_awsFleetCloudLastUpdateTimestamp",
			JenkinsResultsParserUtil.getCurrentTimeMillis());
		ReflectionTestUtil.setFieldValue(
			jenkinsMaster, "_awsFleetClouds", new ArrayList<>());

		return jenkinsMaster;
	}

	public static Map<String, Map<Long, Integer>> getLabelBatchSizes(
		JenkinsMaster jenkinsMaster) {

		return ReflectionTestUtil.getFieldValue(
			jenkinsMaster, "_labelBatchSizes");
	}

	public static JSONObject getOfflineComputerJSONObject(
		String displayName, String offlineCauseReason,
		long offlineCauseTimestamp, boolean temporarilyOffline,
		JSONObject... executorJSONObjects) {

		JSONObject computerJSONObject = _getComputerJSONObject(
			"hudson.slaves.EC2FleetNodeComputer", displayName,
			new JSONArray(executorJSONObjects), true, offlineCauseReason,
			new JSONArray());

		return computerJSONObject.put(
			"offlineCause",
			new JSONObject(
			).put(
				"timestamp", offlineCauseTimestamp
			)
		).put(
			"temporarilyOffline", temporarilyOffline
		);
	}

	public static void resetCaches() {
		Map<String, ?> jenkinsCohorts = ReflectionTestUtil.getFieldValue(
			JenkinsCohort.class, "_jenkinsCohorts");

		jenkinsCohorts.clear();

		Map<String, ?> jenkinsMasters = ReflectionTestUtil.getFieldValue(
			JenkinsMaster.class, "_jenkinsMasters");

		jenkinsMasters.clear();

		List<String> jenkinsMastersBlacklist = ReflectionTestUtil.getFieldValue(
			JenkinsMaster.class, "_jenkinsMastersBlacklist");

		jenkinsMastersBlacklist.clear();

		Map<String, ?> jenkinsMastersMap = ReflectionTestUtil.getFieldValue(
			LoadBalancerUtil.class, "_jenkinsMastersMap");

		jenkinsMastersMap.clear();

		Map<String, ?> roundRobinCounters = ReflectionTestUtil.getFieldValue(
			LoadBalancerUtil.class, "_roundRobinCounters");

		roundRobinCounters.clear();
	}

	private static JSONObject _getComputerJSONObject(
		String className, String displayName, JSONArray executorsJSONArray,
		boolean offline, String offlineCauseReason,
		JSONArray oneOffExecutorsJSONArray) {

		JSONArray assignedLabelsJSONArray = new JSONArray();

		assignedLabelsJSONArray.put(
			new JSONObject(
			).put(
				"name", displayName
			));

		return new JSONObject(
		).put(
			"_class", className
		).put(
			"assignedLabels", assignedLabelsJSONArray
		).put(
			"displayName", displayName
		).put(
			"executors", executorsJSONArray
		).put(
			"idle", executorsJSONArray.isEmpty()
		).put(
			"offline", offline
		).put(
			"offlineCauseReason", offlineCauseReason
		).put(
			"oneOffExecutors", oneOffExecutorsJSONArray
		);
	}

}