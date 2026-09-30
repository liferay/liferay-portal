/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class UpstreamPortalTopLevelBuildTest
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
	public void testGetPortalUpstreamBranchName() {
		String branchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(branchName, branchName);
		_testGetPortalUpstreamBranchName(branchName + "-private", branchName);
		_testGetPortalUpstreamBranchName(
			"some-private" + branchName, "some-private" + branchName);
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

		String branchName = RandomTestUtil.randomString();

		_testGetWorkspace(branchName, null);
		_testGetWorkspace(branchName + "-private", branchName);
	}

	@Test
	public void testGetWorkspaceWithPortalBase() {
		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray());
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		String branchName = RandomTestUtil.randomString();

		_testGetWorkspaceWithPortalBase(branchName + "-private", false, null);
		_testGetWorkspaceWithPortalBase(branchName + "-private", true, "build");
		_testGetWorkspaceWithPortalBase(
			branchName + "-private", true, "controller");
		_testGetWorkspaceWithPortalBase(branchName, false, "build");
	}

	private UpstreamPortalTopLevelBuild _getUpstreamPortalTopLevelBuild(
		String branchName) {

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild = Mockito.mock(
			UpstreamPortalTopLevelBuild.class);

		Mockito.doCallRealMethod(
		).when(
			upstreamPortalTopLevelBuild
		).getPortalUpstreamBranchName();

		Mockito.doReturn(
			branchName
		).when(
			upstreamPortalTopLevelBuild
		).getBranchName();

		return upstreamPortalTopLevelBuild;
	}

	private void _testGetPortalUpstreamBranchName(
		String branchName, String expectedPortalUpstreamBranchName) {

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild =
			_getUpstreamPortalTopLevelBuild(branchName);

		Assert.assertEquals(
			expectedPortalUpstreamBranchName,
			upstreamPortalTopLevelBuild.getPortalUpstreamBranchName());
	}

	private void _testGetWorkspace(
		String branchName, String expectedPortalUpstreamBranchName) {

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		String gitRepositoryName = RandomTestUtil.randomString();
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		workspaces.put(gitRepositoryName, portalWorkspace);

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild =
			_getUpstreamPortalTopLevelBuild(branchName);

		Mockito.doCallRealMethod(
		).when(
			upstreamPortalTopLevelBuild
		).getWorkspace();

		Mockito.doReturn(
			gitRepositoryName
		).when(
			upstreamPortalTopLevelBuild
		).getBaseGitRepositoryName();

		upstreamPortalTopLevelBuild.getWorkspace();

		if (expectedPortalUpstreamBranchName == null) {
			Mockito.verify(
				portalWorkspace, Mockito.never()
			).setPortalUpstreamBranchName(
				Mockito.any()
			);

			return;
		}

		Mockito.verify(
			portalWorkspace
		).setPortalUpstreamBranchName(
			expectedPortalUpstreamBranchName
		);
	}

	private void _testGetWorkspaceWithPortalBase(
		String branchName, boolean expectedConfigured, String parameterSource) {

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		String gitRepositoryName = RandomTestUtil.randomString();
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		workspaces.put(gitRepositoryName, portalWorkspace);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild =
			_getUpstreamPortalTopLevelBuild(branchName);

		Mockito.doCallRealMethod(
		).when(
			upstreamPortalTopLevelBuild
		).getWorkspace();

		Mockito.doReturn(
			gitRepositoryName
		).when(
			upstreamPortalTopLevelBuild
		).getBaseGitRepositoryName();

		String portalBaseGitCommit = RandomTestUtil.randomSHA();
		String portalBaseGitHubURL =
			"https://github.com/liferay/liferay-portal/tree/" +
				RandomTestUtil.randomString();

		BaseBuild parameterBuild = null;

		if (Objects.equals(parameterSource, "build")) {
			parameterBuild = upstreamPortalTopLevelBuild;
		}
		else if (Objects.equals(parameterSource, "controller")) {
			parameterBuild = Mockito.mock(BaseBuild.class);

			Mockito.doReturn(
				parameterBuild
			).when(
				upstreamPortalTopLevelBuild
			).getControllerBuild();
		}

		if (parameterBuild != null) {
			Mockito.doReturn(
				portalBaseGitHubURL
			).when(
				parameterBuild
			).getParameterValue(
				"PORTAL_BASE_GITHUB_URL"
			);

			Mockito.doReturn(
				portalBaseGitCommit
			).when(
				parameterBuild
			).getParameterValue(
				"PORTAL_BASE_GIT_COMMIT"
			);
		}

		upstreamPortalTopLevelBuild.getWorkspace();

		if (!expectedConfigured) {
			Mockito.verify(
				portalWorkspace, Mockito.never()
			).getPortalWorkspaceGitRepository();

			return;
		}

		InOrder inOrder = Mockito.inOrder(portalWorkspaceGitRepository);

		inOrder.verify(
			portalWorkspaceGitRepository
		).setGitHubURL(
			portalBaseGitHubURL
		);

		inOrder.verify(
			portalWorkspaceGitRepository
		).setSenderBranchSHA(
			portalBaseGitCommit
		);
	}

}