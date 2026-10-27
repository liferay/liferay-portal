/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;

import org.junit.After;
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
	public void testGetWorkspace() {
		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray());
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		String branchName = RandomTestUtil.randomString();

		_testGetWorkspace(branchName);
		_testGetWorkspace(branchName + "-private");
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

		String branchName = RandomTestUtil.randomString() + "-private";

		_testGetWorkspaceWithPortalBase(branchName, false, "build");
		_testGetWorkspaceWithPortalBase(branchName, true, null);
		_testGetWorkspaceWithPortalBase(branchName, true, "build");
		_testGetWorkspaceWithPortalBase(branchName, true, "controller");
	}

	private UpstreamPortalTopLevelBuild _getUpstreamPortalTopLevelBuild(
		String branchName) {

		UpstreamPortalTopLevelBuild upstreamPortalTopLevelBuild = Mockito.mock(
			UpstreamPortalTopLevelBuild.class);

		Mockito.doReturn(
			branchName
		).when(
			upstreamPortalTopLevelBuild
		).getBranchName();

		return upstreamPortalTopLevelBuild;
	}

	private void _testGetWorkspace(String branchName) {
		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		String gitRepositoryName = RandomTestUtil.randomString();
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		workspaces.put(gitRepositoryName, portalWorkspace);

		Mockito.doReturn(
			Mockito.mock(PortalWorkspaceGitRepository.class)
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

		upstreamPortalTopLevelBuild.getWorkspace();

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).setPortalUpstreamBranchName(
			Mockito.any()
		);
	}

	private void _testGetWorkspaceWithPortalBase(
		String branchName, boolean hasBasePortalWorkspaceGitRepository,
		String parameterSource) {

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

		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository = null;

		if (hasBasePortalWorkspaceGitRepository) {
			basePortalWorkspaceGitRepository = Mockito.mock(
				PortalWorkspaceGitRepository.class);
		}

		Mockito.doReturn(
			basePortalWorkspaceGitRepository
		).when(
			portalWorkspaceGitRepository
		).getBasePortalWorkspaceGitRepository();

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

		String portalBaseGitCommit = RandomTestUtil.randomSHA();
		String portalBaseGitHubURL =
			"https://github.com/liferay/liferay-portal/tree/" +
				RandomTestUtil.randomString();

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

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.never()
		).setGitHubURL(
			Mockito.any()
		);

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.never()
		).setSenderBranchSHA(
			Mockito.any()
		);

		if (!hasBasePortalWorkspaceGitRepository) {
			Mockito.verify(
				upstreamPortalTopLevelBuild, Mockito.never()
			).getParameterValue(
				"PORTAL_BASE_GITHUB_URL"
			);

			Mockito.verify(
				upstreamPortalTopLevelBuild, Mockito.never()
			).getParameterValue(
				"PORTAL_BASE_GIT_COMMIT"
			);

			return;
		}

		if (parameterBuild == null) {
			Mockito.verifyNoInteractions(basePortalWorkspaceGitRepository);

			return;
		}

		InOrder inOrder = Mockito.inOrder(basePortalWorkspaceGitRepository);

		inOrder.verify(
			basePortalWorkspaceGitRepository
		).setGitHubURL(
			portalBaseGitHubURL
		);

		inOrder.verify(
			basePortalWorkspaceGitRepository
		).setSenderBranchSHA(
			portalBaseGitCommit
		);
	}

}