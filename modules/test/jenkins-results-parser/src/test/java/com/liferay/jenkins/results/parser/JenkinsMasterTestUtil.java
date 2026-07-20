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

	public static Properties getJenkinsCohortProperties(
		String cohortName, int masterCount) {

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

	public static JenkinsMaster getJenkinsMaster(
		String masterName, String masterURL) {

		Hashtable<Object, Object> buildProperties = new Hashtable<>();

		buildProperties.put("jenkins.local.url[" + masterName + "]", masterURL);
		buildProperties.put(
			"jenkins.remote.url[" + masterName + "]",
			"http://" + masterName + ".liferay.com");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

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

	public static void resetCaches() {
		Map<String, ?> jenkinsMastersMap = ReflectionTestUtil.getFieldValue(
			LoadBalancerUtil.class, "_jenkinsMastersMap");

		jenkinsMastersMap.clear();

		Map<String, ?> roundRobinCounters = ReflectionTestUtil.getFieldValue(
			LoadBalancerUtil.class, "_roundRobinCounters");

		roundRobinCounters.clear();

		Map<String, ?> jenkinsMasters = ReflectionTestUtil.getFieldValue(
			JenkinsMaster.class, "_jenkinsMasters");

		jenkinsMasters.clear();

		List<String> jenkinsMastersBlacklist = ReflectionTestUtil.getFieldValue(
			JenkinsMaster.class, "_jenkinsMastersBlacklist");

		jenkinsMastersBlacklist.clear();
	}

}