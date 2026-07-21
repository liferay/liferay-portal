/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Calum Ragan
 */
public class JenkinsMasterTestUtil {

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

	public static void resetCaches() {
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

}