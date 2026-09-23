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
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			null);

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		workspaces.clear();
	}

	@Test
	public void testGetWorkspace() {
		mockEnvironment(
			Collections.singletonMap(
				"BUILD_DIR", RandomTestUtil.randomString()));

		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray());
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		_testGetWorkspace(portalUpstreamBranchName, portalUpstreamBranchName);

		_testGetWorkspace(null, "");
		_testGetWorkspace(null, null);
	}

	private void _testGetWorkspace(
		String expectedPortalUpstreamBranchName,
		String portalUpstreamBranchName) {

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

		Mockito.doReturn(
			portalUpstreamBranchName
		).when(
			pullRequestPortalTopLevelBuild
		).getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME"
		);

		Mockito.doReturn(
			pullRequest
		).when(
			pullRequestPortalTopLevelBuild
		).getPullRequest();

		Mockito.doCallRealMethod(
		).when(
			pullRequestPortalTopLevelBuild
		).getWorkspace();

		pullRequestPortalTopLevelBuild.getWorkspace();

		Mockito.verify(
			portalWorkspace
		).setPortalUpstreamBranchName(
			expectedPortalUpstreamBranchName
		);
	}

}