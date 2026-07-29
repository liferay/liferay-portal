/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class BuildQueueRebalancerTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		Map<String, JenkinsMaster> jenkinsMasters =
			ReflectionTestUtil.getFieldValue(
				JenkinsMaster.class, "_jenkinsMasters");

		jenkinsMasters.remove(_AVAILABLE_JENKINS_MASTER_NAME);
		jenkinsMasters.remove(_BLACKLISTED_JENKINS_MASTER_NAME);

		Map<String, JenkinsCohort> jenkinsCohorts =
			ReflectionTestUtil.getFieldValue(
				JenkinsCohort.class, "_jenkinsCohorts");

		jenkinsCohorts.remove(_JENKINS_COHORT_NAME);

		super.tearDown();
	}

	@Test
	public void testRebalanceBlacklistedJenkinsMasters() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("jenkins.load.balancer.blacklist", "");

		_setJenkinsMasterBuildProperties(
			buildProperties, _AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterBuildProperties(
			buildProperties, _BLACKLISTED_JENKINS_MASTER_NAME);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		UrlReader urlReader = mockUrlReader();

		JSONObject queueJSONObject = new JSONObject();

		queueJSONObject.put(
			"items",
			new JSONArray(
			).put(
				_getQueueItemJSONObject(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong())
			).put(
				_getQueueItemJSONObject(
					RandomTestUtil.randomLong(), RandomTestUtil.randomLong())
			));

		setUrlReaderOutput(
			String.valueOf(queueJSONObject),
			_BLACKLISTED_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);

		setUrlReaderOutput(
			String.valueOf(
				new JSONObject(
				).put(
					"mode", "NORMAL"
				)),
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/api/json?tree=mode",
			urlReader);
		setUrlReaderOutput(
			String.valueOf(
				new JSONObject(
				).put(
					"items", new JSONArray()
				)),
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);

		_setJenkinsMasterAWSFleetClouds(_AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterAWSFleetClouds(_BLACKLISTED_JENKINS_MASTER_NAME);

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_BLACKLISTED_JENKINS_MASTER_NAME),
			"_blacklisted", true);

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(
			_JENKINS_COHORT_NAME);

		List<JenkinsMaster> availableJenkinsMasters =
			jenkinsCohort.getAvailableJenkinsMasters();

		testEquals(1, availableJenkinsMasters.size());

		JenkinsMaster availableJenkinsMaster = availableJenkinsMasters.get(0);

		testEquals(
			_AVAILABLE_JENKINS_MASTER_NAME, availableJenkinsMaster.getName());

		List<JenkinsMaster> blacklistedJenkinsMasters =
			jenkinsCohort.getBlacklistedJenkinsMasters();

		testEquals(1, blacklistedJenkinsMasters.size());

		JenkinsMaster blacklistedJenkinsMaster = blacklistedJenkinsMasters.get(
			0);

		testEquals(
			_BLACKLISTED_JENKINS_MASTER_NAME,
			blacklistedJenkinsMaster.getName());

		BuildQueueRebalancer buildQueueRebalancer = new BuildQueueRebalancer(
			jenkinsCohort);

		buildQueueRebalancer.rebalance();

		String summary = buildQueueRebalancer.getSummary();

		testEquals(
			true,
			summary.startsWith(
				"Build queue rebalanced by 2 reinvocations and 0 aborts"));
	}

	@Test
	public void testRebalanceUnreachableBlacklistedJenkinsMaster()
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty("jenkins.load.balancer.blacklist", "");

		_setJenkinsMasterBuildProperties(
			buildProperties, _AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterBuildProperties(
			buildProperties, _BLACKLISTED_JENKINS_MASTER_NAME);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		UrlReader urlReader = mockUrlReader();

		Mockito.doThrow(
			new IOException("Connection refused")
		).when(
			urlReader
		).doRead(
			Mockito.anyBoolean(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.argThat(
				readURL ->
					(readURL != null) &&
					readURL.contains(
						_BLACKLISTED_JENKINS_MASTER_NAME +
							".liferay.com/queue/api/json"))
		);

		setUrlReaderOutput(
			String.valueOf(
				new JSONObject(
				).put(
					"mode", "NORMAL"
				)),
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/api/json?tree=mode",
			urlReader);
		setUrlReaderOutput(
			String.valueOf(
				new JSONObject(
				).put(
					"items", new JSONArray()
				)),
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);

		_setJenkinsMasterAWSFleetClouds(_AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterAWSFleetClouds(_BLACKLISTED_JENKINS_MASTER_NAME);

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_BLACKLISTED_JENKINS_MASTER_NAME),
			"_blacklisted", true);

		BuildQueueRebalancer buildQueueRebalancer = new BuildQueueRebalancer(
			JenkinsCohort.getInstance(_JENKINS_COHORT_NAME));

		buildQueueRebalancer.rebalance();

		String summary = buildQueueRebalancer.getSummary();

		testEquals(
			true,
			summary.startsWith(
				"Build queue rebalanced by 0 reinvocations and 0 aborts"));
	}

	private JSONObject _getQueueItemJSONObject(long id, long inQueueSince) {
		JSONObject jsonObject = new JSONObject();

		JSONObject taskJSONObject = new JSONObject();

		taskJSONObject.put(
			"name", _JOB_NAME
		).put(
			"url",
			JenkinsResultsParserUtil.combine(
				"http://", _BLACKLISTED_JENKINS_MASTER_NAME,
				".liferay.com/job/", _JOB_NAME, "/")
		);

		jsonObject.put(
			"id", id
		).put(
			"inQueueSince", inQueueSince
		).put(
			"task", taskJSONObject
		).put(
			"url", "queue/item/" + id + "/"
		).put(
			"why", ""
		);

		return jsonObject;
	}

	private void _setJenkinsMasterAWSFleetClouds(String jenkinsMasterName) {
		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			jenkinsMasterName);

		ReflectionTestUtil.setFieldValue(
			jenkinsMaster, "_awsFleetCloudLastUpdateTimestamp", Long.MAX_VALUE);
		ReflectionTestUtil.setFieldValue(
			jenkinsMaster, "_awsFleetClouds", new ArrayList<>());
	}

	private void _setJenkinsMasterBuildProperties(
		Properties buildProperties, String jenkinsMasterName) {

		buildProperties.setProperty(
			JenkinsResultsParserUtil.combine(
				"jenkins.local.url[", jenkinsMasterName, "]"),
			JenkinsResultsParserUtil.combine(
				"http://", jenkinsMasterName, ".liferay.com"));
		buildProperties.setProperty(
			JenkinsResultsParserUtil.combine(
				"jenkins.remote.url[", jenkinsMasterName, "]"),
			JenkinsResultsParserUtil.combine(
				"https://", jenkinsMasterName, ".liferay.com"));
		buildProperties.setProperty(
			JenkinsResultsParserUtil.combine(
				"master.property(", jenkinsMasterName, "/executors.size)"),
			"2");
	}

	private static final String _AVAILABLE_JENKINS_MASTER_NAME = "test-9-2";

	private static final String _BLACKLISTED_JENKINS_MASTER_NAME = "test-9-1";

	private static final String _JENKINS_COHORT_NAME = "test-9";

	private static final String _JOB_NAME =
		"test-portal-acceptance-pullrequest(master)";

}