/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Properties;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspaceGitRepositoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testIsFullDotGitDirArchiveRequired() throws Exception {
		Assert.assertTrue(
			_isFullDotGitDirArchiveRequired("liferay-plugins-ee-6.2.x"));
		Assert.assertTrue(
			_isFullDotGitDirArchiveRequired("liferay-portal-ee-6.2.x"));
		Assert.assertFalse(_isFullDotGitDirArchiveRequired("liferay-portal"));
		Assert.assertFalse(
			_isFullDotGitDirArchiveRequired("liferay-portal-7.0.x"));
	}

	@Test
	public void testPrepareGitWorkingDirectory() throws Exception {
		_testPrepareGitWorkingDirectory(true, true);
		_testPrepareGitWorkingDirectory(true, false);
		_testPrepareGitWorkingDirectory(false, true);
		_testPrepareGitWorkingDirectory(false, false);
	}

	@Test
	public void testSetUp() throws Exception {
		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository(gitWorkingDirectory);

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).prepareGitWorkingDirectory();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).setUpAdditionalCaches();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).uploadGitArchives();

		defaultWorkspaceGitRepository.setUp();
		defaultWorkspaceGitRepository.setUp();

		InOrder inOrder = Mockito.inOrder(defaultWorkspaceGitRepository);

		inOrder.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).prepareGitWorkingDirectory();

		inOrder.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).setUpAdditionalCaches();

		inOrder.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).uploadGitArchives();
	}

	@Test
	public void testSnapshotStateAfterPromoteGitArchive() throws Exception {
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, true));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, false));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(false, true));
		Assert.assertFalse(_isSnapshotAfterPromoteGitArchive(false, false));
	}

	private boolean _isFullDotGitDirArchiveRequired(String workingDirectoryName)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.doReturn(
			new File(workingDirectoryName)
		).when(
			gitWorkingDirectory
		).getWorkingDirectory();

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository(gitWorkingDirectory);

		return defaultWorkspaceGitRepository.isFullDotGitDirArchiveRequired();
	}

	private boolean _isSnapshotAfterPromoteGitArchive(
			boolean gitArchiveAvailable, boolean snapshot)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository(gitWorkingDirectory);

		Mockito.doReturn(
			gitArchiveAvailable
		).when(
			defaultWorkspaceGitRepository
		).isGitArchivesAvailable();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).touchGitArchives();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).updateBuildDatabase();

		defaultWorkspaceGitRepository.setSnapshot(snapshot);

		defaultWorkspaceGitRepository.promoteGitArchive();

		return defaultWorkspaceGitRepository.isSnapshot();
	}

	private DefaultWorkspaceGitRepository _newDefaultWorkspaceGitRepository(
			GitWorkingDirectory gitWorkingDirectory)
		throws Exception {

		File workingDirectory = File.createTempFile("workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"base_branch_head_sha", "1234567890123456789012345678901234567890"
		).put(
			"base_branch_sha", "1234567890123456789012345678901234567890"
		).put(
			"base_branch_username", "liferay"
		).put(
			"directory",
			JenkinsResultsParserUtil.getCanonicalPath(workingDirectory)
		).put(
			"directory_name", "test-repository"
		).put(
			"git_hub_url", "https://github.com/liferay/test-repository"
		).put(
			"name", "test-repository"
		).put(
			"sender_branch_head_sha", "0987654321098765432109876543210987654321"
		).put(
			"sender_branch_name", "master"
		).put(
			"sender_branch_sha", "0987654321098765432109876543210987654321"
		).put(
			"sender_branch_username", "test"
		).put(
			"upstream_branch_name", "master"
		);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			Mockito.spy(new DefaultWorkspaceGitRepository(jsonObject));

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		return defaultWorkspaceGitRepository;
	}

	private void _testPrepareGitWorkingDirectory(
			boolean gitArchiveEnabled, boolean snapshot)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository(gitWorkingDirectory);

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).downloadGitArchives();

		Mockito.doReturn(
			snapshot
		).when(
			defaultWorkspaceGitRepository
		).isSnapshot();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).initializeGitWorkingDirectory();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).promoteGitArchive();

		defaultWorkspaceGitRepository.prepareGitWorkingDirectory();

		if (!gitArchiveEnabled || !snapshot) {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).downloadGitArchives();
		}
		else {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.times(1)
			).downloadGitArchives();
		}

		if (gitArchiveEnabled && snapshot) {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).initializeGitWorkingDirectory();
		}
		else {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.times(1)
			).initializeGitWorkingDirectory();
		}

		if (!gitArchiveEnabled) {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).promoteGitArchive();
		}
		else {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.times(1)
			).promoteGitArchive();
		}
	}

}