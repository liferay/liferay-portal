/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
	public void testPromoteGitArchive() throws Exception {
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, true));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, false));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(false, true));
		Assert.assertFalse(_isSnapshotAfterPromoteGitArchive(false, false));
	}

	@Test
	public void testSetUp() throws Exception {
		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

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
			defaultWorkspaceGitRepository
		).prepareGitWorkingDirectory();

		inOrder.verify(
			defaultWorkspaceGitRepository
		).setUpAdditionalCaches();

		inOrder.verify(
			defaultWorkspaceGitRepository
		).uploadGitArchives();

		Mockito.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).prepareGitWorkingDirectory();

		Mockito.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).setUpAdditionalCaches();

		Mockito.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).uploadGitArchives();
	}

	@Test
	public void testUploadGitArchives() throws Exception {
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("top-level-job", true, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("top-level-job", true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("top-level-job", false, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives("top-level-job", false, false));

		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("downstream-job", true, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("downstream-job", true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives("downstream-job", false, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives("downstream-job", false, false));

		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool", true, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool", true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool", false, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool", false, false));

		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool-batch", true, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool-batch", true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool-batch", false, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(
				"root-cause-analysis-tool-batch", false, false));
	}

	@Test
	public void testValidateSHAInRemoteGitRef() throws Exception {
		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		LocalGitBranch localGitBranch = Mockito.mock(LocalGitBranch.class);
		RemoteGitRef remoteGitRef = Mockito.mock(RemoteGitRef.class);

		Mockito.when(
			gitWorkingDirectory.fetch(remoteGitRef)
		).thenReturn(
			localGitBranch
		);

		Mockito.when(
			gitWorkingDirectory.refContainsSHA(localGitBranch, null)
		).thenReturn(
			true
		);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		defaultWorkspaceGitRepository.validateSHAInRemoteGitRef(
			"master", remoteGitRef, null);

		InOrder inOrder = Mockito.inOrder(gitWorkingDirectory);

		inOrder.verify(
			gitWorkingDirectory
		).fetch(
			remoteGitRef
		);

		inOrder.verify(
			gitWorkingDirectory
		).refContainsSHA(
			localGitBranch, null
		);
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
			_newDefaultWorkspaceGitRepository();

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		return defaultWorkspaceGitRepository.isFullDotGitDirArchiveRequired();
	}

	private boolean _isSnapshotAfterPromoteGitArchive(
			boolean gitArchiveAvailable, boolean snapshot)
		throws Exception {

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

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

	private boolean _isSnapshotAfterUploadGitArchives(
			String jobName, boolean gitArchiveEnabled, boolean snapshot)
		throws Exception {

		Map<String, String> environmentValues = new HashMap<>();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			environmentValues.put("JOB_NAME", jobName);
		}

		environmentValues.put("MASTER_NETWORK_NAME", "aws-network");

		mockEnvironment(environmentValues);

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Set<String> topLevelJobNames = new HashSet<>();

		topLevelJobNames.add("root-cause-analysis-tool");
		topLevelJobNames.add("top-level-job");

		JenkinsResultsParserUtil.setTopLevelJobNames(topLevelJobNames);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).uploadGitArchive();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).uploadDotGitArchive();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).updateBuildDatabase();

		defaultWorkspaceGitRepository.setSnapshot(snapshot);

		defaultWorkspaceGitRepository.uploadGitArchives();

		if (gitArchiveEnabled && !snapshot &&
			topLevelJobNames.contains(jobName)) {

			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.times(1)
			).uploadGitArchive();

			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.times(1)
			).uploadDotGitArchive();
		}
		else {
			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).uploadGitArchive();

			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).uploadDotGitArchive();
		}

		return defaultWorkspaceGitRepository.isSnapshot();
	}

	private DefaultWorkspaceGitRepository _newDefaultWorkspaceGitRepository()
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

		return Mockito.spy(new DefaultWorkspaceGitRepository(jsonObject));
	}

	private void _testPrepareGitWorkingDirectory(
			boolean gitArchiveEnabled, boolean snapshot)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

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