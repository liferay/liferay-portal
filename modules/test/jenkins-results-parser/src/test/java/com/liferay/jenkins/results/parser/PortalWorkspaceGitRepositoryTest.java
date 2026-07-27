/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Collections;
import java.util.Properties;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class PortalWorkspaceGitRepositoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testSetUp() throws Exception {
		File workingDirectory = File.createTempFile("portal-workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		File gitDirectory = new File(workingDirectory, ".git");

		gitDirectory.mkdir();

		mockEnvironment(
			Collections.singletonMap("MASTER_NETWORK_NAME", "aws-network"));

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"binaries.cache.s3.path",
			"s3://liferayci-file-propagator/binaries-cache/master.tar.gz");
		buildProperties.setProperty("git.archive.enabled", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		BuildDatabaseUtil.setBuildDatabase(
			BuildDatabaseTestUtil.newBuildDatabaseWithPullRequest());

		Shell shell = mockShell();

		Mockito.doReturn(
			new Shell.ExecutionResult(0, "", "")
		).when(
			shell
		).doExecute(
			Mockito.any(Shell.ExecutionRequest.class)
		);

		LocalGitBranch localGitBranch = Mockito.mock(LocalGitBranch.class);

		Mockito.doReturn(
			"1234567890123456789012345678901234567890"
		).when(
			localGitBranch
		).getSHA();

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspaceGitRepository
		).prepareGitWorkingDirectory();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspaceGitRepository
		).setUp();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspaceGitRepository
		).setUpAdditionalCaches();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspaceGitRepository
		).setUpBinariesCache();

		Mockito.doReturn(
			workingDirectory
		).when(
			portalWorkspaceGitRepository
		).getDirectory();

		Mockito.doReturn(
			"liferay-portal"
		).when(
			portalWorkspaceGitRepository
		).getDirectoryName();

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			portalWorkspaceGitRepository
		).getGitWorkingDirectory();

		Mockito.doReturn(
			localGitBranch
		).when(
			portalWorkspaceGitRepository
		).getLocalGitBranch();

		Mockito.doReturn(
			"master"
		).when(
			portalWorkspaceGitRepository
		).getUpstreamBranchName();

		Mockito.doReturn(
			true
		).when(
			portalWorkspaceGitRepository
		).isBinariesCacheEnabled();

		portalWorkspaceGitRepository.setUp();

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> _isCommand(
					executionRequest, "aws s3 cp", "binaries-cache.tar.gz"))
		);

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> _isCommand(
					executionRequest, "tar --directory=",
					"binaries-cache.tar.gz"))
		);
	}

	private boolean _isCommand(
		Shell.ExecutionRequest executionRequest, String... substrings) {

		if (executionRequest == null) {
			return false;
		}

		String command = executionRequest.getCommands()[0];

		for (String substring : substrings) {
			if (!command.contains(substring)) {
				return false;
			}
		}

		return true;
	}

}