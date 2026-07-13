/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
 * @author Peter Yoo
 */
public class BaseLocalGitCommitTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetPatchParentPresent() throws Exception {
		Shell shell = _mockGitWorkingDirectoryShell();

		setShellCommandOutput(
			"git cat-file -t " + _SHA + "^", shell, "commit\n");
		setShellCommandOutput(
			"git show " + _SHA + " --patch --stat", shell, _PATCH);

		LocalGitCommit localGitCommit = _newLocalGitCommit();

		Assert.assertEquals(_PATCH, localGitCommit.getPatch());
	}

	@Test
	public void testGetPatchParentUnavailable() throws Exception {
		Shell shell = _mockGitWorkingDirectoryShell();

		_setShellExitValue(shell, "git cat-file -t " + _SHA + "^", 128);
		setShellCommandOutput("git fetch -f --depth=2", shell, "");

		LocalGitCommit localGitCommit = _newLocalGitCommit();

		Assert.assertNull(localGitCommit.getPatch());

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, "git fetch -f --depth=2", _SHA))
		);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Shell _mockGitWorkingDirectoryShell() throws Exception {
		Shell shell = mockShell();

		setShellCommandOutput(
			"git remote -v", shell,
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");
		setShellCommandOutput("git rev-parse master", shell, _SHA);

		return shell;
	}

	private LocalGitCommit _newLocalGitCommit() {
		File gitRepositoryDir = temporaryFolder.getRoot();

		File gitDir = new File(gitRepositoryDir, ".git");

		gitDir.mkdir();

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				"master", gitRepositoryDir, "liferay-portal");

		return GitCommitFactory.newLocalGitCommit(
			"test@liferay.com", gitWorkingDirectory, "LRCI-7850 Test commit",
			_SHA, 0);
	}

	private void _setShellExitValue(Shell shell, String command, int exitValue)
		throws Exception {

		Mockito.doReturn(
			new Shell.ExecutionResult(exitValue, "", "")
		).when(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(executionRequest, command))
		);
	}

	private static final String _PATCH = "diff --git a/foo.txt b/foo.txt\n+bar";

	private static final String _SHA =
		"abcdef1234567890abcdef1234567890abcdef12";

}