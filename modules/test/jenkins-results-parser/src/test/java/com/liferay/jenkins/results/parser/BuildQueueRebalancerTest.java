/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		try {
			Map<String, JenkinsMaster> jenkinsMasters = _getStaticFieldValue(
				JenkinsMaster.class, "_jenkinsMasters");

			jenkinsMasters.remove(_AVAILABLE_JENKINS_MASTER_NAME);
			jenkinsMasters.remove(_BLACK_LISTED_JENKINS_MASTER_NAME);

			Map<String, JenkinsCohort> jenkinsCohorts = _getStaticFieldValue(
				JenkinsCohort.class, "_jenkinsCohorts");

			jenkinsCohorts.remove(_JENKINS_COHORT_NAME);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}

		JenkinsResultsParserUtil.setBuildProperties(
			(Hashtable<Object, Object>)null);

		super.tearDown();
	}

	@Test
	public void testRebalanceBlackListedJenkinsMasters() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("jenkins.load.balancer.blacklist", "");

		_setJenkinsMasterBuildProperties(
			buildProperties, _AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterBuildProperties(
			buildProperties, _BLACK_LISTED_JENKINS_MASTER_NAME);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			JenkinsResultsParserUtil.combine(
				"{\"items\":[{\"id\":101,\"inQueueSince\":1,\"task\":",
				"{\"name\":\"test-portal-acceptance-pullrequest(master)\",",
				"\"url\":\"http://", _BLACK_LISTED_JENKINS_MASTER_NAME,
				".liferay.com/job/test-portal-acceptance-pullrequest",
				"(master)/\"},\"url\":\"queue/item/101/\",\"why\":\"\"},",
				"{\"id\":102,\"inQueueSince\":2,\"task\":{\"name\":",
				"\"test-portal-acceptance-pullrequest(master)\",\"url\":",
				"\"http://", _BLACK_LISTED_JENKINS_MASTER_NAME,
				".liferay.com/job/test-portal-acceptance-pullrequest",
				"(master)/\"},\"url\":\"queue/item/102/\",\"why\":\"\"}]}"),
			_BLACK_LISTED_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);
		setUrlReaderOutput(
			"{\"mode\":\"NORMAL\"}",
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/api/json?tree=mode",
			urlReader);
		setUrlReaderOutput(
			"{\"items\":[]}",
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);

		_setJenkinsMasterAWSFleetClouds(_AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterAWSFleetClouds(_BLACK_LISTED_JENKINS_MASTER_NAME);

		_setFieldValue(
			JenkinsMaster.getInstance(_BLACK_LISTED_JENKINS_MASTER_NAME),
			"_blacklisted", true);

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(
			_JENKINS_COHORT_NAME);

		List<JenkinsMaster> availableJenkinsMasters =
			jenkinsCohort.getAvailableJenkinsMasters();

		testEquals(1, availableJenkinsMasters.size());

		JenkinsMaster availableJenkinsMaster = availableJenkinsMasters.get(0);

		testEquals(
			_AVAILABLE_JENKINS_MASTER_NAME, availableJenkinsMaster.getName());

		List<JenkinsMaster> blackListedJenkinsMasters =
			jenkinsCohort.getBlackListedJenkinsMasters();

		testEquals(1, blackListedJenkinsMasters.size());

		JenkinsMaster blackListedJenkinsMaster = blackListedJenkinsMasters.get(
			0);

		testEquals(
			_BLACK_LISTED_JENKINS_MASTER_NAME,
			blackListedJenkinsMaster.getName());

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
	public void testRebalanceUnreachableBlackListedJenkinsMaster()
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty("jenkins.load.balancer.blacklist", "");

		_setJenkinsMasterBuildProperties(
			buildProperties, _AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterBuildProperties(
			buildProperties, _BLACK_LISTED_JENKINS_MASTER_NAME);

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
						_BLACK_LISTED_JENKINS_MASTER_NAME +
							".liferay.com/queue/api/json"))
		);

		setUrlReaderOutput(
			"{\"mode\":\"NORMAL\"}",
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/api/json?tree=mode",
			urlReader);
		setUrlReaderOutput(
			"{\"items\":[]}",
			_AVAILABLE_JENKINS_MASTER_NAME + ".liferay.com/queue/api/json",
			urlReader);

		_setJenkinsMasterAWSFleetClouds(_AVAILABLE_JENKINS_MASTER_NAME);
		_setJenkinsMasterAWSFleetClouds(_BLACK_LISTED_JENKINS_MASTER_NAME);

		_setFieldValue(
			JenkinsMaster.getInstance(_BLACK_LISTED_JENKINS_MASTER_NAME),
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

	@SuppressWarnings("unchecked")
	private <T> T _getStaticFieldValue(Class<?> clazz, String fieldName)
		throws ReflectiveOperationException {

		Field field = clazz.getDeclaredField(fieldName);

		field.setAccessible(true);

		return (T)field.get(null);
	}

	private void _setFieldValue(Object object, String fieldName, Object value)
		throws Exception {

		Class<?> clazz = object.getClass();

		Field field = clazz.getDeclaredField(fieldName);

		field.setAccessible(true);

		field.set(object, value);
	}

	private void _setJenkinsMasterAWSFleetClouds(String jenkinsMasterName)
		throws Exception {

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			jenkinsMasterName);

		_setFieldValue(
			jenkinsMaster, "_awsFleetCloudLastUpdateTimestamp", Long.MAX_VALUE);
		_setFieldValue(jenkinsMaster, "_awsFleetClouds", new ArrayList<>());
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

	private static final String _BLACK_LISTED_JENKINS_MASTER_NAME = "test-9-1";

	private static final String _JENKINS_COHORT_NAME = "test-9";

}