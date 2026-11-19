/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspaceTestUtil {

	public static <T extends Workspace> T newWorkspace(
		Class<T> workspaceClass,
		Class<? extends WorkspaceGitRepository> workspaceGitRepositoryClass) {

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			workspaceGitRepositoryClass);

		Mockito.doReturn(
			RandomTestUtil.randomSHA()
		).when(
			workspaceGitRepository
		).getBaseBranchSHA();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			workspaceGitRepository
		).getName();

		Mockito.doReturn(
			RandomTestUtil.randomSHA()
		).when(
			workspaceGitRepository
		).getSenderBranchSHA();

		T workspace = Mockito.mock(workspaceClass);

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			workspace
		).getPrimaryWorkspaceGitRepository();

		if (workspace instanceof PortalWorkspace) {
			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			Mockito.doReturn(
				RandomTestUtil.randomString()
			).when(
				portalWorkspace
			).getS3ObjectPathName();
		}

		return workspace;
	}

	public static void setWorkspaces(Workspace... workspaces) {
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doReturn(
			Arrays.asList(workspaces)
		).when(
			buildDatabase
		).getWorkspaces();

		BuildDatabaseUtil.setBuildDatabase(buildDatabase);
	}

}