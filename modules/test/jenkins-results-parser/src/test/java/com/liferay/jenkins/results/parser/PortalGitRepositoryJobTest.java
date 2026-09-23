/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PortalGitRepositoryJobTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetPortalUpstreamBranchName() {
		String upstreamBranchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(
			new JSONObject(), upstreamBranchName, upstreamBranchName);

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(
			new JSONObject(
			).put(
				"upstream_branch_name", portalUpstreamBranchName
			),
			portalUpstreamBranchName, upstreamBranchName);

		_testGetPortalUpstreamBranchName(
			null, upstreamBranchName, upstreamBranchName);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testGetPortalUpstreamBranchName(
		JSONObject branchJSONObject, String expectedPortalUpstreamBranchName,
		String upstreamBranchName) {

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			temporaryFolder.getRoot()
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class,
					invocation -> portalGitWorkingDirectory)) {

			Mockito.mock(
				PortalGitRepositoryJob.class,
				Mockito.withSettings(
				).defaultAnswer(
					Mockito.CALLS_REAL_METHODS
				).useConstructor(
					new JSONObject(
					).put(
						"branch", branchJSONObject
					).put(
						"build_profile", "dxp"
					).put(
						"git_repository_dir", RandomTestUtil.randomString()
					).put(
						"job_name", RandomTestUtil.randomString()
					).put(
						"upstream_branch_name", upstreamBranchName
					)
				));

			gitWorkingDirectoryFactoryMockedStatic.verify(
				() -> GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					expectedPortalUpstreamBranchName));
		}
	}

}