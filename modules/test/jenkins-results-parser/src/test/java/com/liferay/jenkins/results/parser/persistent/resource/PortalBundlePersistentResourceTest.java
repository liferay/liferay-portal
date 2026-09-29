/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.PortalWorkspace;
import com.liferay.jenkins.results.parser.PortalWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.SubrepositoryWorkspace;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class PortalBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetBundleWorkspaceGitRepository() {
		String portalTopLevelBuildURL = _getTopLevelBuildURL(
			"test-portal-acceptance-pullrequest(master-private)");

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		_testGetBundleWorkspaceGitRepository(
			portalWorkspaceGitRepository, portalTopLevelBuildURL,
			portalWorkspace);

		Workspace workspace = Mockito.mock(Workspace.class);

		WorkspaceGitRepository primaryWorkspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			primaryWorkspaceGitRepository
		).when(
			workspace
		).getPrimaryWorkspaceGitRepository();

		_testGetBundleWorkspaceGitRepository(
			primaryWorkspaceGitRepository, portalTopLevelBuildURL, workspace);

		SubrepositoryWorkspace subrepositoryWorkspace = Mockito.mock(
			SubrepositoryWorkspace.class);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			subrepositoryWorkspace
		).getWorkspaceGitRepository(
			"liferay-portal"
		);

		_testGetBundleWorkspaceGitRepository(
			workspaceGitRepository,
			_getTopLevelBuildURL("test-subrepository-acceptance-pullrequest"),
			subrepositoryWorkspace);

		Mockito.verify(
			subrepositoryWorkspace, Mockito.never()
		).getPortalWorkspaceGitRepository();
	}

	private String _getTopLevelBuildURL(String jobName) {
		return "https://" + RandomTestUtil.randomString() + "/job/" + jobName +
			"/1/";
	}

	private void _testGetBundleWorkspaceGitRepository(
		WorkspaceGitRepository expectedWorkspaceGitRepository,
		String topLevelBuildURL, Workspace workspace) {

		PortalBundlePersistentResource portalBundlePersistentResource =
			Mockito.mock(PortalBundlePersistentResource.class);

		Mockito.doCallRealMethod(
		).when(
			portalBundlePersistentResource
		).getBundleWorkspaceGitRepository();

		Mockito.doReturn(
			topLevelBuildURL
		).when(
			portalBundlePersistentResource
		).getCurrentTopLevelBuildURL();

		Mockito.doReturn(
			workspace
		).when(
			portalBundlePersistentResource
		).getWorkspace();

		testSame(
			expectedWorkspaceGitRepository,
			portalBundlePersistentResource.getBundleWorkspaceGitRepository());
	}

}