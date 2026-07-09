/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class GitWorkingDirectoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testFetchGitCommitParentFromUpstream() throws Exception {
		File gitRepositoryDir = temporaryFolder.getRoot();

		File gitDir = new File(gitRepositoryDir, ".git");

		gitDir.mkdir();

		Shell shell = mockShell();

		setShellCommandOutput(
			"git remote -v", shell,
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");

		setShellCommandOutput("git rev-parse master", shell, _LOCAL_BRANCH_SHA);
		setShellCommandOutput("git fetch -f --depth=2", shell, "");

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				"master", gitRepositoryDir, "liferay-portal");

		gitWorkingDirectory.fetchGitCommitParentFromUpstream(_LOCAL_BRANCH_SHA);

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> {
					if (executionRequest == null) {
						return false;
					}

					String command = executionRequest.getCommands()[0];

					return command.contains("git fetch -f --depth=2") &&
						   command.contains(_LOCAL_BRANCH_SHA);
				})
		);
	}

	@Test
	public void testGetLocalGitBranchSHA() throws Exception {
		File gitRepositoryDir = temporaryFolder.getRoot();

		File gitDir = new File(gitRepositoryDir, ".git");

		gitDir.mkdir();

		Shell shell = mockShell();

		setShellCommandOutput(
			"git remote -v", shell,
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");

		setShellCommandOutput("git rev-parse master", shell, _LOCAL_BRANCH_SHA);

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				"master", gitRepositoryDir, "liferay-portal");

		Assert.assertEquals(
			_LOCAL_BRANCH_SHA,
			gitWorkingDirectory.getLocalGitBranchSHA("master"));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static final String _LOCAL_BRANCH_SHA = "abcdef1234567890";

}