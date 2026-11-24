/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

/**
 * @author Kenji Heigel
 */
public class BaseBuildUpdaterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testRunMissing() {
		_testRunMissing(false, false, 1, false, 1);
		_testRunMissing(false, false, 2, true, 100);
		_testRunMissing(false, true, 0, false, 1);
		_testRunMissing(false, true, 0, false, 100);
		_testRunMissing(true, false, 0, false, 1);
		_testRunMissing(true, false, 0, false, 100);
	}

	@Test
	public void testRunMissingReattaches() {
		_testRunMissingReattaches(false, true, _BUILD_QUEUED_ITEM_ID);
		_testRunMissingReattaches(true, false, _BUILD_IN_PROGRESS_QUEUE_ID);
	}

	@Test
	public void testRunMissingWithoutBuildParameters() {
		BaseBuildUpdater baseBuildUpdater = _mockBaseBuildUpdater(
			true, true, Collections.emptyMap());

		ReflectionTestUtil.setFieldValue(
			baseBuildUpdater, "_missingTickCount", Integer.MAX_VALUE - 1);

		baseBuildUpdater.runMissing();

		Mockito.verify(
			baseBuildUpdater
		).reinvoke();
	}

	@Test
	public void testRunReporting() {
		_testRunReporting(Build.class, true, true);
		_testRunReporting(DefaultTopLevelBuild.class, false, true);
		_testRunReporting(PortalTopLevelBuild.class, false, true);
		_testRunReporting(SourceFormatBuild.class, false, false);
		_testRunReporting(SourceFormatBuild.class, true, true);
	}

	private BaseBuildUpdater _mockBaseBuildUpdater(
		boolean buildInProgress, boolean buildQueued,
		Map<String, String> parameters) {

		String jobName = RandomTestUtil.randomString();

		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		JSONObject inProgressBuildJSONObject = null;

		if (buildInProgress) {
			inProgressBuildJSONObject = new JSONObject();

			inProgressBuildJSONObject.put(
				"queueId", _BUILD_IN_PROGRESS_QUEUE_ID
			).put(
				"url", RandomTestUtil.randomString()
			);
		}

		Mockito.when(
			jenkinsMaster.getInProgressBuildJSONObject(jobName, parameters)
		).thenReturn(
			inProgressBuildJSONObject
		);

		JSONObject queuedBuildJSONObject = null;

		if (buildQueued) {
			queuedBuildJSONObject = new JSONObject();

			queuedBuildJSONObject.put("id", _BUILD_QUEUED_ITEM_ID);
		}

		Mockito.when(
			jenkinsMaster.getQueuedBuildJSONObject(jobName, parameters)
		).thenReturn(
			queuedBuildJSONObject
		);

		Build build = Mockito.mock(Build.class);

		Mockito.when(
			build.getCurrentInvocation()
		).thenReturn(
			new Build.Invocation(
				build, jenkinsMaster, RandomTestUtil.randomLong())
		);

		Mockito.when(
			build.getJobName()
		).thenReturn(
			jobName
		);

		Mockito.when(
			build.getParameters()
		).thenReturn(
			parameters
		);

		BaseBuildUpdater baseBuildUpdater = Mockito.mock(
			BaseBuildUpdater.class);

		Mockito.doCallRealMethod(
		).when(
			baseBuildUpdater
		).getBuild();

		Mockito.doCallRealMethod(
		).when(
			baseBuildUpdater
		).runMissing();

		ReflectionTestUtil.setFieldValue(baseBuildUpdater, "_build", build);

		return baseBuildUpdater;
	}

	private void _testRunMissing(
		boolean buildInProgress, boolean buildQueued,
		int expectedReinvocationCount, boolean expectedReporting,
		int tickCount) {

		BaseBuildUpdater baseBuildUpdater = _mockBaseBuildUpdater(
			buildInProgress, buildQueued,
			Collections.singletonMap(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		for (int i = 0; i < tickCount; i++) {
			ReflectionTestUtil.setFieldValue(
				baseBuildUpdater, "_missingTickCount", Integer.MAX_VALUE - 1);

			baseBuildUpdater.runMissing();
		}

		Mockito.verify(
			baseBuildUpdater, Mockito.times(expectedReinvocationCount)
		).reinvoke();

		VerificationMode verificationMode = Mockito.never();

		if (expectedReporting) {
			verificationMode = Mockito.atLeastOnce();
		}

		Mockito.verify(
			baseBuildUpdater.getBuild(), verificationMode
		).setStatus(
			"reporting"
		);
	}

	private void _testRunMissingReattaches(
		boolean buildInProgress, boolean buildQueued, long expectedQueueId) {

		BaseBuildUpdater baseBuildUpdater = _mockBaseBuildUpdater(
			buildInProgress, buildQueued,
			Collections.singletonMap(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		ReflectionTestUtil.setFieldValue(
			baseBuildUpdater, "_missingTickCount", Integer.MAX_VALUE - 1);

		baseBuildUpdater.runMissing();

		Build build = baseBuildUpdater.getBuild();

		Build.Invocation currentInvocation = build.getCurrentInvocation();

		Assert.assertEquals(expectedQueueId, currentInvocation.getQueueId());

		Mockito.verify(
			baseBuildUpdater, Mockito.never()
		).reinvoke();
	}

	private void _testRunReporting(
		Class<? extends Build> buildClass, boolean expectedReinvoked,
		boolean hasParentBuild) {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"reinvoke.rule[" + RandomTestUtil.randomString() + "]",
			"priority=1");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Build build = Mockito.mock(buildClass);

		Mockito.when(
			build.getBuildURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Build.Invocation invocation = Mockito.mock(Build.Invocation.class);

		Mockito.when(
			build.getCurrentInvocation()
		).thenReturn(
			invocation
		);

		if (hasParentBuild) {
			Build parentBuild = Mockito.mock(Build.class);

			Mockito.when(
				build.getParentBuild()
			).thenReturn(
				parentBuild
			);

			Mockito.when(
				parentBuild.getStatus()
			).thenReturn(
				"running"
			);
		}

		BaseBuildUpdater baseBuildUpdater = Mockito.mock(
			BaseBuildUpdater.class);

		Mockito.doCallRealMethod(
		).when(
			baseBuildUpdater
		).getBuild();

		Mockito.doCallRealMethod(
		).when(
			baseBuildUpdater
		).runReporting();

		Mockito.when(
			baseBuildUpdater.isBuildCompleted()
		).thenReturn(
			true
		);

		Mockito.when(
			baseBuildUpdater.isBuildFailing()
		).thenReturn(
			true
		);

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_ciNode", true);
		ReflectionTestUtil.setFieldValue(
			ReinvokeRule.class, "_reinvokeRules", null);
		ReflectionTestUtil.setFieldValue(baseBuildUpdater, "_build", build);
		ReflectionTestUtil.setFieldValue(
			baseBuildUpdater, "_reinvokeRulesMap", new HashMap<>());

		try {
			baseBuildUpdater.runReporting();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				JenkinsResultsParserUtil.class, "_ciNode", null);
			ReflectionTestUtil.setFieldValue(
				ReinvokeRule.class, "_reinvokeRules", null);
		}

		String expectedStatus = "completed";
		VerificationMode verificationMode = Mockito.never();

		if (expectedReinvoked) {
			expectedStatus = "queued";
			verificationMode = Mockito.times(1);
		}

		Mockito.verify(
			baseBuildUpdater, verificationMode
		).reinvoke();

		Mockito.verify(
			invocation, verificationMode
		).setReinvokeRule(
			Mockito.any(ReinvokeRule.class)
		);

		Mockito.verify(
			build
		).setStatus(
			expectedStatus
		);
	}

	private static final long _BUILD_IN_PROGRESS_QUEUE_ID = 1234;

	private static final long _BUILD_QUEUED_ITEM_ID = 5678;

}