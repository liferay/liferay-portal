/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Collections;
import java.util.Map;

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
		_testRunMissing(false, true, 0, false, 1);
		_testRunMissing(true, false, 0, false, 1);
		_testRunMissing(true, false, 0, true, 100);
	}

	private BaseBuildUpdater _mockBaseBuildUpdater(
		boolean buildInProgress, boolean buildQueued) {

		String jobName = RandomTestUtil.randomString();

		Map<String, String> parameters = Collections.singletonMap(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

		Mockito.when(
			jenkinsMaster.isBuildInProgress(jobName, parameters)
		).thenReturn(
			buildInProgress
		);

		Mockito.when(
			jenkinsMaster.isBuildQueued(jobName, parameters)
		).thenReturn(
			buildQueued
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
			buildInProgress, buildQueued);

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

}