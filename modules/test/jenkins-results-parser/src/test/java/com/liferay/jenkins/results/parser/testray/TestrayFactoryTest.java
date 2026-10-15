/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuild;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;

import java.net.URL;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestrayFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(
			Collections.singletonMap(
				"WORKSPACE", RandomTestUtil.randomString()));
	}

	@Test
	public void testNewDownstreamBuildTestrayCaseResult() throws Exception {
		_testNewDownstreamBuildTestrayCaseResult(
			DownstreamBatchBuildTestrayCaseResult.class,
			RandomTestUtil.randomString());
		_testNewDownstreamBuildTestrayCaseResult(
			SFBatchBuildTestrayCaseResult.class, "test-portal-source-format");

		Assert.assertNull(
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				Mockito.mock(DownstreamBuild.class), null));
		Assert.assertNull(
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				null, Mockito.mock(TestrayContext.class)));

		DownstreamBuild downstreamBuild = Mockito.mock(DownstreamBuild.class);
		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Assert.assertNull(
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				downstreamBuild, testrayContext));

		Mockito.when(
			downstreamBuild.getAxisTestClassGroup()
		).thenReturn(
			Mockito.mock(AxisTestClassGroup.class)
		);

		Assert.assertNull(
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				downstreamBuild, testrayContext));

		Mockito.when(
			downstreamBuild.getDownstreamBuildReport()
		).thenReturn(
			Mockito.mock(DownstreamBuildReport.class)
		);

		Assert.assertNull(
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				downstreamBuild, testrayContext));
	}

	private void _testNewDownstreamBuildTestrayCaseResult(
			Class<?> expectedClass, String jobName)
		throws Exception {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		File testBaseDir = new File(RandomTestUtil.randomString());

		Mockito.when(
			axisTestClassGroup.getTestBaseDir()
		).thenReturn(
			testBaseDir
		);

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

		Mockito.when(
			axisTestClassGroup.getTestrayRun()
		).thenReturn(
			testrayRun
		);

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.when(
			topLevelBuildReport.getDownstreamBuildReport(Mockito.any())
		).thenReturn(
			Mockito.mock(DownstreamBuildReport.class)
		);

		Mockito.when(
			topLevelBuildReport.getJobName()
		).thenReturn(
			jobName
		);

		TopLevelBuild topLevelBuild = Mockito.mock(TopLevelBuild.class);

		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		Mockito.when(
			topLevelBuild.getTestrayCaseResultURL()
		).thenReturn(
			testrayCaseResultURL
		);

		Mockito.when(
			topLevelBuild.getTopLevelBuildReport()
		).thenReturn(
			topLevelBuildReport
		);

		DownstreamBuild downstreamBuild = Mockito.mock(DownstreamBuild.class);

		Mockito.when(
			downstreamBuild.getAxisTestClassGroup()
		).thenReturn(
			axisTestClassGroup
		);

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.when(
			downstreamBuild.getDownstreamBuildReport()
		).thenReturn(
			downstreamBuildReport
		);

		Mockito.when(
			downstreamBuild.getTopLevelBuild()
		).thenReturn(
			topLevelBuild
		);

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.when(
			testrayBuild.getId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			testrayBuild.getTestrayServer()
		).thenReturn(
			Mockito.mock(TestrayServer.class)
		);

		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Mockito.when(
			testrayContext.getTestrayBuild(testBaseDir)
		).thenReturn(
			testrayBuild
		);

		TestrayCaseResult testrayCaseResult =
			TestrayFactory.newDownstreamBuildTestrayCaseResult(
				downstreamBuild, testrayContext);

		Assert.assertSame(expectedClass, testrayCaseResult.getClass());

		BuildTestrayCaseResult buildTestrayCaseResult =
			(BuildTestrayCaseResult)testrayCaseResult;

		testSame(
			downstreamBuildReport, buildTestrayCaseResult.getBuildReport());

		testSame(testrayBuild, testrayCaseResult.getTestrayBuild());

		TestrayCaseResult parentTestrayCaseResult =
			testrayCaseResult.getParentTestrayCaseResult();

		testSame(
			testrayCaseResultURL,
			parentTestrayCaseResult.getTestrayCaseResultURL());

		testSame(testrayRun, testrayCaseResult.getTestrayRun());
	}

}