/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Collections;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class TestrayRunTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestrayFactors() throws Exception {
		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		Mockito.doReturn(
			"{}"
		).when(
			testrayServer
		).requestGet(
			Mockito.anyString()
		);

		TestrayRun testrayRun = new TestrayRun(
			testrayBuild, RandomTestUtil.randomString());

		testEquals(Collections.emptyList(), testrayRun.getTestrayFactors());
	}

}