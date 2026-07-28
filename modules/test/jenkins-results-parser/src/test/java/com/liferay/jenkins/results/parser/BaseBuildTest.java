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

}