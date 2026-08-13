/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Collections;
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
import org.mockito.verification.VerificationMode;

/**
 * @author Michael Hashimoto
 */
public class BaseWorkspaceGitRepositoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testDownloadGitArchives() throws Exception {
		_testDownloadGitArchives(false, "test-portal-stable(7.3.x)", "compile");
		_testDownloadGitArchives(false, "test-portal-stable(master)", null);
		_testDownloadGitArchives(
			true, "test-portal-stable(7.3.x)", "service-builder");
		_testDownloadGitArchives(
			true, "test-portal-stable(master)", "service-builder");
		_testDownloadGitArchives(
			true, "test-portal-upstream(7.4.x)", "rest-builder");

		JenkinsResultsParserUtil.setTopLevelJobNames(
			Collections.singleton("test-portal-release"));

		_testDownloadGitArchives(true, "test-portal-release", "compile");
	}

	@Test
	public void testGetGitWorkingDirectory() throws Exception {
		_testGetGitWorkingDirectory(false, false, false, false);
		_testGetGitWorkingDirectory(false, false, false, true);
		_testGetGitWorkingDirectory(false, false, true, false);
		_testGetGitWorkingDirectory(false, true, true, true);
		_testGetGitWorkingDirectory(true, false, true, true);
	}

	@Test
	public void testIsFullDotGitDirArchiveRequired() throws Exception {
		Assert.assertFalse(_isFullDotGitDirArchiveRequired("liferay-portal"));
		Assert.assertFalse(
			_isFullDotGitDirArchiveRequired("liferay-portal-7.0.x"));
		Assert.assertTrue(
			_isFullDotGitDirArchiveRequired("liferay-plugins-ee-6.2.x"));
		Assert.assertTrue(
			_isFullDotGitDirArchiveRequired("liferay-portal-ee-6.2.x"));
	}

	@Test
	public void testPrepareGitWorkingDirectory() throws Exception {
		_testPrepareGitWorkingDirectory(false, false, false);
		_testPrepareGitWorkingDirectory(false, false, true);
		_testPrepareGitWorkingDirectory(false, true, false);
		_testPrepareGitWorkingDirectory(false, true, true);
		_testPrepareGitWorkingDirectory(true, false, false);
		_testPrepareGitWorkingDirectory(true, false, true);
		_testPrepareGitWorkingDirectory(true, true, false);
		_testPrepareGitWorkingDirectory(true, true, true);
	}

	@Test
	public void testPromoteGitArchive() throws Exception {
		Assert.assertFalse(_isSnapshotAfterPromoteGitArchive(false, false));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(false, true));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, false));
		Assert.assertTrue(_isSnapshotAfterPromoteGitArchive(true, true));
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
		String jobName = "downstream-job";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, false, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, false, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, true));

		jobName = "root-cause-analysis-tool";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, false, false));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, false, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, true));

		jobName = "root-cause-analysis-tool-batch";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, false, false));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, false, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, true));

		jobName = "top-level-job";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(jobName, false, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, false, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(jobName, true, true));
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

	private VerificationMode _getVerificationMode(boolean invoked) {
		if (invoked) {
			return Mockito.times(1);
		}

		return Mockito.never();
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

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).touchGitArchives();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).updateBuildDatabase();

		Mockito.doReturn(
			gitArchiveAvailable
		).when(
			defaultWorkspaceGitRepository
		).isGitArchivesAvailable();

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
		).updateBuildDatabase();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).uploadDotGitArchive();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).uploadGitArchive();

		defaultWorkspaceGitRepository.setSnapshot(snapshot);

		defaultWorkspaceGitRepository.uploadGitArchives();

		VerificationMode verificationMode = _getVerificationMode(
			gitArchiveEnabled && !snapshot &&
			topLevelJobNames.contains(jobName));

		Mockito.verify(
			defaultWorkspaceGitRepository, verificationMode
		).uploadDotGitArchive();

		Mockito.verify(
			defaultWorkspaceGitRepository, verificationMode
		).uploadGitArchive();

		return defaultWorkspaceGitRepository.isSnapshot();
	}

	private DefaultWorkspaceGitRepository _newDefaultWorkspaceGitRepository()
		throws Exception {

		File workingDirectory = File.createTempFile("workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		String baseBranchSHA = RandomTestUtil.randomSHA();
		String baseBranchUsername = RandomTestUtil.randomString();
		String repositoryName = RandomTestUtil.randomString();
		String senderBranchSHA = RandomTestUtil.randomSHA();

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"base_branch_head_sha", baseBranchSHA
		).put(
			"base_branch_sha", baseBranchSHA
		).put(
			"base_branch_username", baseBranchUsername
		).put(
			"directory",
			JenkinsResultsParserUtil.getCanonicalPath(workingDirectory)
		).put(
			"directory_name", repositoryName
		).put(
			"git_hub_url",
			JenkinsResultsParserUtil.combine(
				"https://github.com/", baseBranchUsername, "/", repositoryName)
		).put(
			"name", repositoryName
		).put(
			"sender_branch_head_sha", senderBranchSHA
		).put(
			"sender_branch_name", "master"
		).put(
			"sender_branch_sha", senderBranchSHA
		).put(
			"sender_branch_username", RandomTestUtil.randomString()
		).put(
			"upstream_branch_name", "master"
		);

		return Mockito.spy(new DefaultWorkspaceGitRepository(jsonObject));
	}

	private void _setUpEnvironment(String jobName, String jobVariant) {
		Map<String, String> environmentMap = new HashMap<>();

		if (jobVariant != null) {
			environmentMap.put("JOB_VARIANT", jobVariant);
		}

		environmentMap.put("JOB_NAME", jobName);
		environmentMap.put("MASTER_NETWORK_NAME", "aws-network");

		mockEnvironment(environmentMap);
	}

	private void _testDownloadGitArchives(
			boolean dotGitDirArchiveRequired, String jobName, String jobVariant)
		throws Exception {

		_setUpEnvironment(jobName, jobVariant);

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.dot.git.dir.required[*rest-builder*]", "true");
		buildProperties.setProperty(
			"git.archive.dot.git.dir.required[*service-builder*]", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).downloadDotGitArchive();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).downloadGitArchive();

		defaultWorkspaceGitRepository.downloadGitArchives();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			_getVerificationMode(dotGitDirArchiveRequired)
		).downloadDotGitArchive();

		Mockito.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).downloadGitArchive();
	}

	private void _testGetGitWorkingDirectory(
			boolean dotGitDirArchiveRequired, boolean exceptionThrown,
			boolean gitArchiveEnabled, boolean snapshot)
		throws Exception {

		_setUpEnvironment(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.dot.git.dir.required",
			String.valueOf(dotGitDirArchiveRequired));
		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		ReflectionTestUtil.setFieldValue(
			defaultWorkspaceGitRepository, "_gitWorkingDirectory",
			gitWorkingDirectory);

		defaultWorkspaceGitRepository.setSnapshot(snapshot);

		if (exceptionThrown) {
			try {
				defaultWorkspaceGitRepository.getGitWorkingDirectory();

				Assert.fail("Expected RuntimeException");
			}
			catch (RuntimeException runtimeException) {
				testEquals(
					"Using Git archive, unable to get Git working directory",
					runtimeException.getMessage());
			}

			return;
		}

		testSame(
			gitWorkingDirectory,
			defaultWorkspaceGitRepository.getGitWorkingDirectory());
	}

	private void _testPrepareGitWorkingDirectory(
			boolean buildCachingEnabled, boolean gitArchiveEnabled,
			boolean snapshot)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"build.caching.enabled", String.valueOf(buildCachingEnabled));
		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).downloadGitArchives();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).initializeGitWorkingDirectory();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).promoteGitArchive();

		Mockito.doReturn(
			snapshot
		).when(
			defaultWorkspaceGitRepository
		).isSnapshot();

		defaultWorkspaceGitRepository.prepareGitWorkingDirectory();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			_getVerificationMode(gitArchiveEnabled && snapshot)
		).downloadGitArchives();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			_getVerificationMode(!gitArchiveEnabled || !snapshot)
		).initializeGitWorkingDirectory();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			_getVerificationMode(gitArchiveEnabled)
		).promoteGitArchive();
	}

}