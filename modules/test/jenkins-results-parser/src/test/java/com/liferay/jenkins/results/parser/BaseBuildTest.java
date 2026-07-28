/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BaseBuildTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetBadBuildURLs() {
		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);

		Build.Invocation firstInvocation = new Build.Invocation(baseBuild);
		Build.Invocation lastInvocation = new Build.Invocation(baseBuild);

		String firstBuildURL = "https://test-1-1.liferay.com/job/test/1";

		firstInvocation.setBuildURL(firstBuildURL);

		lastInvocation.setBuildURL("https://test-1-1.liferay.com/job/test/3");

		ReflectionTestUtil.setFieldValue(
			baseBuild, "_invocations",
			Arrays.asList(
				firstInvocation, new Build.Invocation(baseBuild),
				lastInvocation));

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).getBadBuildURLs();

		Assert.assertEquals(
			Arrays.asList(firstBuildURL), baseBuild.getBadBuildURLs());
	}

	@Test
	public void testSaveBuildURLInBuildDatabase() {
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), null, false);
		_testSaveBuildURLInBuildDatabase(
			null, RandomTestUtil.randomString(), false);
	}

	private void _testSaveBuildURLInBuildDatabase(
		String buildURL, String jobVariant, boolean saved) {

		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).saveBuildURLInBuildDatabase();

		Mockito.when(
			baseBuild.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			baseBuild.getBuildURL()
		).thenReturn(
			buildURL
		);

		Mockito.when(
			baseBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		baseBuild.saveBuildURLInBuildDatabase();

		if (saved) {
			Mockito.verify(
				buildDatabase
			).putProperty(
				BaseBuild.BUILD_URLS_PROPERTIES_KEY, jobVariant, buildURL, false
			);
		}
		else {
			Mockito.verifyNoInteractions(buildDatabase);
		}
	}

}