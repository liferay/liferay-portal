/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class JobFactoryTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewJob() {
		mockEnvironment(
			Collections.singletonMap(
				"JENKINS_URL", RandomTestUtil.randomString()));

		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Job job = Mockito.mock(Job.class);

		Mockito.doReturn(
			job
		).when(
			buildDatabase
		).getJob(
			Mockito.anyString()
		);

		Mockito.doReturn(
			true
		).when(
			buildDatabase
		).hasJob(
			Mockito.anyString()
		);

		BuildDatabaseUtil.setBuildDatabase(buildDatabase);

		PullRequestPortalTopLevelBuild pullRequestPortalTopLevelBuild =
			Mockito.mock(PullRequestPortalTopLevelBuild.class);

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		Mockito.doReturn(
			portalUpstreamBranchName
		).when(
			pullRequestPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		_testNewJob(
			job, portalUpstreamBranchName, pullRequestPortalTopLevelBuild);

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild = Mockito.mock(
			UpstreamPortalTopLevelBuild.class);

		Mockito.doReturn(
			portalUpstreamBranchName
		).when(
			upstreamPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		_testNewJob(job, portalUpstreamBranchName, upstreamPortalTopLevelBuild);
	}

	@Test
	public void testNewJobReusesCachedJob() {
		mockEnvironment(
			ImmutableMap.of(
				"BUILD_CACHING_ENABLED", "true", "JENKINS_URL",
				RandomTestUtil.randomString(), "MASTER_NETWORK_NAME",
				"aws-network"));

		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doReturn(
			false
		).when(
			buildDatabase
		).hasJob(
			Mockito.anyString()
		);

		BuildDatabaseUtil.setBuildDatabase(buildDatabase);

		String testSuiteName = RandomTestUtil.randomString();

		try (MockedStatic<JobCacheUtil> jobCacheUtilMockedStatic =
				Mockito.mockStatic(JobCacheUtil.class)) {

			jobCacheUtilMockedStatic.when(
				() -> JobCacheUtil.getCachedJobJSONObject(
					Mockito.anyString(),
					Mockito.nullable(PortalGitWorkingDirectory.class),
					Mockito.anyString())
			).thenReturn(
				null
			);

			Job firstJob = JobFactory.newJob(
				Job.BuildProfile.DXP, "test-poshi-release", testSuiteName);

			Job secondJob = JobFactory.newJob(
				Job.BuildProfile.DXP, "test-poshi-release", testSuiteName);

			testSame(firstJob, secondJob);

			jobCacheUtilMockedStatic.verify(
				() -> JobCacheUtil.getCachedJobJSONObject(
					Mockito.anyString(),
					Mockito.nullable(PortalGitWorkingDirectory.class),
					Mockito.anyString()),
				Mockito.times(1));
		}
	}

	private void _testNewJob(
		Job job, String portalUpstreamBranchName, TopLevelBuild topLevelBuild) {

		Mockito.doReturn(
			topLevelBuild
		).when(
			topLevelBuild
		).getTopLevelBuild();

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class)) {

			testSame(job, JobFactory.newJob(topLevelBuild));

			gitWorkingDirectoryFactoryMockedStatic.verify(
				() -> GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					portalUpstreamBranchName));
		}
	}

}