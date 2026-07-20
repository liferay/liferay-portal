/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Calum Ragan
 */
public class JenkinsMasterTestUtil {

	public static Map<String, Map<Long, Integer>> getLabelBatchSizes(
		JenkinsMaster jenkinsMaster) {

		return (Map<String, Map<Long, Integer>>)Test.getDeclaredFieldValue(
			JenkinsMaster.class, jenkinsMaster, "_labelBatchSizes");
	}

	public static void resetCaches() {
		Map<String, ?> jenkinsMastersMap =
			(Map<String, ?>)Test.getDeclaredFieldValue(
				LoadBalancerUtil.class, null, "_jenkinsMastersMap");

		jenkinsMastersMap.clear();

		Map<String, ?> roundRobinCounters =
			(Map<String, ?>)Test.getDeclaredFieldValue(
				LoadBalancerUtil.class, null, "_roundRobinCounters");

		roundRobinCounters.clear();

		Map<String, ?> jenkinsMasters =
			(Map<String, ?>)Test.getDeclaredFieldValue(
				JenkinsMaster.class, null, "_jenkinsMasters");

		jenkinsMasters.clear();

		List<String> jenkinsMastersBlacklist =
			(List<String>)Test.getDeclaredFieldValue(
				JenkinsMaster.class, null, "_jenkinsMastersBlacklist");

		jenkinsMastersBlacklist.clear();
	}

	public static Properties stageFleet(String cohortName, int masterCount) {
		Hashtable<Object, Object> buildProperties = new Hashtable<>();

		buildProperties.put(
			"base.invocation.url", "http://" + cohortName + ".liferay.com");

		for (int i = 1; i <= masterCount; i++) {
			String masterName = cohortName + "-" + i;

			buildProperties.put(
				"jenkins.local.url[" + masterName + "]",
				"http://" + masterName);
			buildProperties.put(
				"jenkins.remote.url[" + masterName + "]",
				"http://" + masterName + ".liferay.com");
			buildProperties.put(
				"master.property(" + masterName + "/executors.size)", "8");
		}

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		resetCaches();

		Properties properties = new Properties();

		properties.putAll(buildProperties);

		return properties;
	}

	public static JenkinsMaster stageMaster(
		String masterName, String masterURL) {

		Hashtable<Object, Object> buildProperties = new Hashtable<>();

		buildProperties.put("jenkins.local.url[" + masterName + "]", masterURL);
		buildProperties.put(
			"jenkins.remote.url[" + masterName + "]",
			"http://" + masterName + ".liferay.com");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		resetCaches();

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(masterName);

		Test.setDeclaredFieldValue(
			JenkinsMaster.class, jenkinsMaster,
			"_awsFleetCloudLastUpdateTimestamp",
			JenkinsResultsParserUtil.getCurrentTimeMillis());
		Test.setDeclaredFieldValue(
			JenkinsMaster.class, jenkinsMaster, "_awsFleetClouds",
			new ArrayList<>());

		return jenkinsMaster;
	}

}