/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class DefaultBuildUpdaterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testIsBuildRunning() {
		String buildURL = RandomTestUtil.randomString();
		long queueId = Math.abs(RandomTestUtil.randomLong());

		JSONObject buildJSONObject = new JSONObject(
		).put(
			"queueId", queueId
		).put(
			"url", buildURL
		);

		_testIsBuildRunning(Arrays.asList(buildJSONObject), true, 0, false);
		_testIsBuildRunning(
			Arrays.asList(buildJSONObject), true, queueId, true);

		_testIsBuildRunning(Collections.emptyList(), false, queueId, false);
		_testIsBuildRunning(null, false, queueId, false);
	}

	private Build _mockBuild(
		JenkinsMaster jenkinsMaster, String jobName, long queueId) {

		Build build = Mockito.mock(Build.class);

		Mockito.when(
			build.getCurrentInvocation()
		).thenReturn(
			new Build.Invocation(build, jenkinsMaster, queueId)
		);

		Mockito.when(
			build.getJobName()
		).thenReturn(
			jobName
		);

		return build;
	}

	private JenkinsMaster _mockJenkinsMaster(
		List<JSONObject> buildJSONObjects, String jobName) {

		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		if (buildJSONObjects == null) {
			Mockito.when(
				jenkinsMaster.getBuildJSONObjects(jobName)
			).thenThrow(
				new RuntimeException()
			);

			return jenkinsMaster;
		}

		Mockito.when(
			jenkinsMaster.getBuildJSONObjects(jobName)
		).thenReturn(
			buildJSONObjects
		);

		return jenkinsMaster;
	}

	private void _testIsBuildRunning(
		List<JSONObject> buildJSONObjects, boolean expectedBuildRunning,
		long invocationQueueId, boolean saveFailure) {

		String jobName = RandomTestUtil.randomString();

		Build build = _mockBuild(
			_mockJenkinsMaster(buildJSONObjects, jobName), jobName,
			invocationQueueId);

		if (saveFailure) {
			Mockito.doThrow(
				new NullPointerException()
			).when(
				build
			).saveBuildURLInBuildDatabase();
		}

		DefaultBuildUpdater defaultBuildUpdater = new DefaultBuildUpdater(
			build);

		Assert.assertEquals(
			expectedBuildRunning, defaultBuildUpdater.isBuildRunning());

		if (!expectedBuildRunning) {
			return;
		}

		JSONObject buildJSONObject = buildJSONObjects.get(0);

		Build.Invocation buildInvocation = build.getCurrentInvocation();

		Assert.assertEquals(
			buildJSONObject.getLong("queueId"), buildInvocation.getQueueId());

		Mockito.verify(
			build
		).setBuildURL(
			buildJSONObject.getString("url")
		);
	}

}