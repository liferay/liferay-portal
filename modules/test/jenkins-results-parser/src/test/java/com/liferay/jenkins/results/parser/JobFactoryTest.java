/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

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