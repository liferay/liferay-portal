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
				executionRequest -> hasCommand(
					executionRequest, "git fetch -f --depth=2",
					_LOCAL_BRANCH_SHA))
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

	@Test
	public void testGetRebasedLocalGitBranch() throws Exception {
		File gitRepositoryDir = temporaryFolder.getRoot();

		File gitDir = new File(gitRepositoryDir, ".git");

		gitDir.mkdir();

		Shell shell = mockShell();

		setShellCommandOutput(
			"git remote -v", shell,
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");

		RemoteGitBranch remoteGitBranch = Mockito.mock(RemoteGitBranch.class);
		LocalGitBranch localGitBranch = Mockito.mock(LocalGitBranch.class);

		GitWorkingDirectory gitWorkingDirectory = Mockito.spy(
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				"master", gitRepositoryDir, "liferay-portal"));

		Mockito.doNothing(
		).when(
			gitWorkingDirectory
		).clean();

		Mockito.doReturn(
			localGitBranch
		).when(
			gitWorkingDirectory
		).createLocalGitBranch(
			Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString()
		);

		Mockito.doReturn(
			localGitBranch
		).when(
			gitWorkingDirectory
		).createLocalGitBranch(
			Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString(),
			Mockito.nullable(RemoteGitBranch.class)
		);

		Mockito.doReturn(
			"other-branch"
		).when(
			gitWorkingDirectory
		).getCurrentBranchName();

		Mockito.doReturn(
			remoteGitBranch
		).when(
			gitWorkingDirectory
		).getRemoteGitBranch(
			Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()
		);

		Mockito.doReturn(
			true
		).when(
			gitWorkingDirectory
		).localSHAExists(
			Mockito.anyString()
		);

		Mockito.doReturn(
			localGitBranch
		).when(
			gitWorkingDirectory
		).rebase(
			Mockito.anyBoolean(), Mockito.any(LocalGitBranch.class),
			Mockito.any(LocalGitBranch.class)
		);

		Mockito.doNothing(
		).when(
			gitWorkingDirectory
		).reset(
			Mockito.anyString()
		);

		gitWorkingDirectory.getRebasedLocalGitBranch(
			"rebased-branch", _SENDER_BRANCH_NAME,
			"git@github.com:sender/liferay-portal.git", _LOCAL_BRANCH_SHA,
			"master", _LOCAL_BRANCH_SHA);

		Mockito.verify(
			gitWorkingDirectory, Mockito.never()
		).getRemoteGitBranch(
			Mockito.eq(_SENDER_BRANCH_NAME), Mockito.anyString(),
			Mockito.anyBoolean()
		);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static final String _LOCAL_BRANCH_SHA = "abcdef1234567890";

	private static final String _SENDER_BRANCH_NAME = "sender-branch";

}