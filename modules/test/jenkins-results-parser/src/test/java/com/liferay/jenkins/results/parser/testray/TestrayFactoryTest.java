/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.DownstreamBuild;
import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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

	@Test
	public void testNewTestrayRun() throws Exception {
		Job job = _mockJob(
			"modules-integration-db-partition-postgresql163",
			"modules-integration-mysql80", "modules-integration-postgresql163");

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		TestrayRun testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "modules-integration-db-partition-postgresql163",
			job);

		testEquals(
			"PostgreSQL 16.3#modules-integration-db-partition-postgresql163",
			testrayRun.getRunIdString());

		testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "modules-integration-mysql80", job);

		testEquals("MySQL 8.0", testrayRun.getRunIdString());

		testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "modules-integration-postgresql163", job);

		testEquals("PostgreSQL 16.3", testrayRun.getRunIdString());

		testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "unit-postgresql163", job);

		testEquals("PostgreSQL 16.3", testrayRun.getRunIdString());
	}

	@Test
	public void testNewTestrayRunSameLengthBatchNames() throws Exception {
		Job job = _mockJob("a-mysql80", "b-mysql80");

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		TestrayRun testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "a-mysql80", job);

		testEquals("MySQL 8.0", testrayRun.getRunIdString());

		testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, "b-mysql80", job);

		testEquals("MySQL 8.0#b-mysql80", testrayRun.getRunIdString());
	}

	private Job _mockJob(String... batchNames) {
		List<AxisTestClassGroup> axisTestClassGroups = new ArrayList<>();

		for (String batchName : batchNames) {
			AxisTestClassGroup axisTestClassGroup = Mockito.mock(
				AxisTestClassGroup.class);

			Mockito.when(
				axisTestClassGroup.getEnhancedBatchName()
			).thenReturn(
				batchName
			);

			axisTestClassGroups.add(axisTestClassGroup);
		}

		Job job = Mockito.mock(Job.class);

		Mockito.when(
			job.getAxisTestClassGroups()
		).thenReturn(
			axisTestClassGroups
		);

		Mockito.when(
			job.getJobName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Properties properties = new Properties();

		properties.setProperty(
			"testray.environment.factor.name[database]",
			RandomTestUtil.randomString());
		properties.setProperty(
			"testray.environment.factor.value[database][mysql80]", "MySQL 8.0");
		properties.setProperty(
			"testray.environment.factor.value[database][postgresql163]",
			"PostgreSQL 16.3");

		Mockito.when(
			job.getJobProperties()
		).thenReturn(
			properties
		);

		return job;
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