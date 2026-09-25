/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;

import org.junit.After;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PullRequestPortalTopLevelBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_ciNode", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			null);

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		workspaces.clear();
	}

	@Test
	public void testGetPortalUpstreamBranchName() {
		_testGetPortalUpstreamBranchName("master-private", "master", null);

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(
			"master-private", portalUpstreamBranchName,
			portalUpstreamBranchName);

		_testGetPortalUpstreamBranchName(
			RandomTestUtil.randomString(), null, "");

		_testGetPortalUpstreamBranchName(
			RandomTestUtil.randomString(), portalUpstreamBranchName,
			portalUpstreamBranchName);
	}

	@Test
	public void testGetStableJob() {
		String branchName = RandomTestUtil.randomString();

		_testGetStableJob("master-private", true, "master");
		_testGetStableJob(branchName, false, branchName);
		_testGetStableJob(branchName, true, branchName);
	}

	@Test
	public void testGetWorkspace() {
		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray());
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		PullRequest pullRequest = Mockito.mock(PullRequest.class);

		String gitRepositoryName = RandomTestUtil.randomString();

		Mockito.doReturn(
			gitRepositoryName
		).when(
			pullRequest
		).getGitRepositoryName();

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			portalWorkspace
		).getPrimaryWorkspaceGitRepository();

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		workspaces.put(gitRepositoryName, portalWorkspace);

		PullRequestPortalTopLevelBuild pullRequestPortalTopLevelBuild =
			Mockito.mock(PullRequestPortalTopLevelBuild.class);

		Mockito.doCallRealMethod(
		).when(
			pullRequestPortalTopLevelBuild
		).getWorkspace();

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		Mockito.doReturn(
			portalUpstreamBranchName
		).when(
			pullRequestPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		Mockito.doReturn(
			pullRequest
		).when(
			pullRequestPortalTopLevelBuild
		).getPullRequest();

		pullRequestPortalTopLevelBuild.getWorkspace();

		Mockito.verify(
			portalWorkspace
		).setPortalUpstreamBranchName(
			portalUpstreamBranchName
		);
	}

	private void _testGetPortalUpstreamBranchName(
		String branchName, String expectedPortalUpstreamBranchName,
		String portalUpstreamBranchName) {

		PullRequestPortalTopLevelBuild pullRequestPortalTopLevelBuild =
			Mockito.mock(PullRequestPortalTopLevelBuild.class);

		Mockito.doCallRealMethod(
		).when(
			pullRequestPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		Mockito.doReturn(
			branchName
		).when(
			pullRequestPortalTopLevelBuild
		).getBranchName();

		Mockito.doReturn(
			portalUpstreamBranchName
		).when(
			pullRequestPortalTopLevelBuild
		).getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME"
		);

		testEquals(
			expectedPortalUpstreamBranchName,
			pullRequestPortalTopLevelBuild.getPortalUpstreamBranchName());
	}

	private void _testGetStableJob(
		String branchName, boolean ciNode,
		String expectedPortalUpstreamBranchName) {

		Map<String, String> environmentMap = Collections.emptyMap();

		if (ciNode) {
			environmentMap = Collections.singletonMap(
				"JENKINS_URL", RandomTestUtil.randomString());
		}

		mockEnvironment(environmentMap);

		JenkinsResultsParserUtil.clearCache();

		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		Job job = Mockito.mock(Job.class);

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		PortalGitWorkingDirectory expectedPortalGitWorkingDirectory =
			ciNode ? portalGitWorkingDirectory : null;

		PullRequestPortalTopLevelBuild pullRequestPortalTopLevelBuild =
			Mockito.mock(PullRequestPortalTopLevelBuild.class);

		Mockito.doCallRealMethod(
		).when(
			pullRequestPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		Mockito.doReturn(
			branchName
		).when(
			pullRequestPortalTopLevelBuild
		).getBranchName();

		Mockito.doReturn(
			"relevant"
		).when(
			pullRequestPortalTopLevelBuild
		).getTestSuiteName();

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class);
			MockedStatic<JobFactory> jobFactoryMockedStatic =
				Mockito.mockStatic(JobFactory.class)) {

			gitWorkingDirectoryFactoryMockedStatic.when(
				() -> GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					expectedPortalUpstreamBranchName)
			).thenReturn(
				portalGitWorkingDirectory
			);

			jobFactoryMockedStatic.when(
				() -> JobFactory.newJob(
					Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
					Mockito.eq(expectedPortalGitWorkingDirectory),
					Mockito.isNull(),
					Mockito.eq(expectedPortalUpstreamBranchName),
					Mockito.isNull(), Mockito.isNull(), Mockito.eq("stable"),
					Mockito.eq(branchName))
			).thenReturn(
				job
			);

			testSame(
				job,
				ReflectionTestUtil.invoke(
					pullRequestPortalTopLevelBuild, "_getStableJob",
					new Class<?>[0]));

			jobFactoryMockedStatic.verify(
				() -> JobFactory.getKey(
					Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
					Mockito.eq(expectedPortalUpstreamBranchName),
					Mockito.isNull(), Mockito.isNull(), Mockito.eq("stable"),
					Mockito.eq(branchName)));
		}
	}

}