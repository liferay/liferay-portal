/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PortalWorkspaceTest
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
	}

	@Test
	public void testGetPortalWorkspaceGitRepository() {
		String gitDirectoryName = RandomTestUtil.randomString();
		String upstreamBranchName = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"branch", upstreamBranchName
				).put(
					"name", gitDirectoryName
				).put(
					"repository", "liferay-portal-ee"
				)
			));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		PortalWorkspace portalWorkspace = _getPortalWorkspace(
			new JSONObject(), portalWorkspaceGitRepository);

		testSame(
			portalWorkspaceGitRepository,
			portalWorkspace.getPortalWorkspaceGitRepository());

		_testGetPortalWorkspaceGitRepository(
			"liferay-portal", "master", portalWorkspaceGitRepository);
		_testGetPortalWorkspaceGitRepository(
			gitDirectoryName, upstreamBranchName, portalWorkspaceGitRepository);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			upstreamBranchName
		).when(
			workspaceGitRepository
		).getUpstreamBranchName();

		_testGetPortalWorkspaceGitRepository(
			gitDirectoryName, null, workspaceGitRepository);

		portalWorkspace = _getPortalWorkspace(
			new JSONObject(
			).put(
				"portal_upstream_branch_name", upstreamBranchName
			),
			workspaceGitRepository);

		RuntimeException runtimeException = Assert.assertThrows(
			RuntimeException.class,
			portalWorkspace::getPortalWorkspaceGitRepository);

		testEquals(
			"The portal workspace Git repository is not set",
			runtimeException.getMessage());
	}

	@Test
	public void testSetPortalUpstreamBranchName() {
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getJSONObject();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).setPortalUpstreamBranchName(
			Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject", new JSONObject());

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		portalWorkspace.setPortalUpstreamBranchName(portalUpstreamBranchName);

		JSONObject portalWorkspaceJSONObject = portalWorkspace.getJSONObject();

		testEquals(
			portalUpstreamBranchName,
			portalWorkspaceJSONObject.get("portal_upstream_branch_name"));

		portalWorkspace.setPortalUpstreamBranchName(null);

		Assert.assertFalse(
			portalWorkspaceJSONObject.has("portal_upstream_branch_name"));
	}

	private PortalWorkspace _getPortalWorkspace(
		JSONObject jsonObject, WorkspaceGitRepository workspaceGitRepository) {

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			portalWorkspace
		).getPrimaryWorkspaceGitRepository();

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject", jsonObject);

		return portalWorkspace;
	}

	private void _testGetPortalWorkspaceGitRepository(
		String gitDirectoryName, String portalUpstreamBranchName,
		WorkspaceGitRepository workspaceGitRepository) {

		PortalWorkspace portalWorkspace = _getPortalWorkspace(
			new JSONObject(
			).put(
				"portal_upstream_branch_name", portalUpstreamBranchName
			),
			workspaceGitRepository);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getWorkspaceGitRepository(
			gitDirectoryName
		);

		testSame(
			portalWorkspaceGitRepository,
			portalWorkspace.getPortalWorkspaceGitRepository());
	}

}