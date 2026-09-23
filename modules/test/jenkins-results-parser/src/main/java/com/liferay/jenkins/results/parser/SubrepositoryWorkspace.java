/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class SubrepositoryWorkspace extends PortalWorkspace {

	protected SubrepositoryWorkspace(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected SubrepositoryWorkspace(
		String primaryRepositoryName, String upstreamBranchName) {

		super(primaryRepositoryName, upstreamBranchName);
	}

	protected SubrepositoryWorkspace(
		String primaryRepositoryName, String upstreamBranchName,
		String jobName) {

		super(primaryRepositoryName, upstreamBranchName, jobName);
	}

	@Override
	protected void updateOSBAsahModule() {
		WorkspaceGitRepository primaryWorkspaceGitRepository =
			getPrimaryWorkspaceGitRepository();

		String repositoryName = primaryWorkspaceGitRepository.getName();

		if (repositoryName.equals("com-liferay-osb-asah-private")) {
			copyLiferayOSBAsahRepositoryToModule();
		}
	}

}