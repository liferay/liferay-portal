/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Assume;
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
	public void testFetchCommitFileSHA() throws Exception {
		_testFetchCommitFileSHA(false);
		_testFetchCommitFileSHA(true);
	}

	@Test
	public void testGetBranchName() throws Exception {
		Assert.assertFalse(
			JenkinsResultsParserUtil.isNullOrEmpty(_getBranchName("", "")));

		String branchName = RandomTestUtil.randomString();

		testEquals(branchName, _getBranchName(branchName, null));

		String startPropertiesBranchName = RandomTestUtil.randomString();

		testEquals(
			branchName, _getBranchName(branchName, startPropertiesBranchName));
		testEquals(
			startPropertiesBranchName,
			_getBranchName(null, startPropertiesBranchName));
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
	public void testGetLocalGitBranch() throws Exception {
		_testGetLocalGitBranch(false, false);
		_testGetLocalGitBranch(false, true);
		_testGetLocalGitBranch(true, true);
	}

	@Test
	public void testGetProperties() throws Exception {
		_testGetProperties("7.4.x", "7.4.x");
		_testGetProperties("base", RandomTestUtil.randomString());
		_testGetProperties("master", "master");
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
	public void testPartitionLocalGitCommits() throws Exception {
		_testPartitionLocalGitCommits(0, true, _newLocalGitCommits(5));
		_testPartitionLocalGitCommits(1, true, _newLocalGitCommits(5));
		_testPartitionLocalGitCommits(3, false, _newLocalGitCommits(0));
		_testPartitionLocalGitCommits(3, false, _newLocalGitCommits(10));
		_testPartitionLocalGitCommits(3, false, null);
		_testPartitionLocalGitCommits(5, false, _newLocalGitCommits(3));
	}

	@Test
	public void testPrepareGitWorkingDirectory() throws Exception {
		_testPrepareGitWorkingDirectory(false, "merge-portal-subrepository");
		_testPrepareGitWorkingDirectory(false, false, false);
		_testPrepareGitWorkingDirectory(false, false, true);
		_testPrepareGitWorkingDirectory(false, true, false);
		_testPrepareGitWorkingDirectory(false, true, true);
		_testPrepareGitWorkingDirectory(true, "test-portal-upstream");
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
	public void testStoreCommitHistory() throws Exception {
		List<String> expectedCommitSHAs = new ArrayList<>();

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		List<LocalGitCommit> localGitCommits = _newLocalGitCommits(5);

		Mockito.doReturn(
			localGitCommits
		).when(
			gitWorkingDirectory
		).log(
			0, WorkspaceGitRepository.COMMITS_HISTORY_GROUP_SIZE
		);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		for (LocalGitCommit localGitCommit : localGitCommits.subList(0, 4)) {
			expectedCommitSHAs.add(localGitCommit.getSHA());
		}

		defaultWorkspaceGitRepository.storeCommitHistory(
			Collections.singletonList(
				expectedCommitSHAs.get(expectedCommitSHAs.size() - 1)));

		List<String> actualCommitSHAs = new ArrayList<>();

		for (LocalGitCommit historicalLocalGitCommit :
				defaultWorkspaceGitRepository.getHistoricalLocalGitCommits()) {

			actualCommitSHAs.add(historicalLocalGitCommit.getSHA());
		}

		testEquals(expectedCommitSHAs, actualCommitSHAs);
	}

	@Test
	public void testTearDown() throws Exception {
		_testTearDown(false, false, false);
		_testTearDown(false, true, true);
		_testTearDown(true, false, true);
		_testTearDown(true, true, false);
		_testTearDown(true, true, true);
	}

	@Test
	public void testUploadGitArchives() throws Exception {
		String jobName = "downstream-job";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(false, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(false, jobName, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, true));

		jobName = "root-cause-analysis-tool";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(false, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(false, jobName, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(true, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, true));

		jobName = "root-cause-analysis-tool-batch";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(false, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(false, jobName, true));
		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(true, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, true));

		jobName = "top-level-job";

		Assert.assertFalse(
			_isSnapshotAfterUploadGitArchives(false, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(false, jobName, true));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, false));
		Assert.assertTrue(
			_isSnapshotAfterUploadGitArchives(true, jobName, true));
	}

	@Test
	public void testValidateSHAInRemoteGitRef() throws Exception {
		_testValidateSHAInRemoteGitRef(false, false, true, true);
		_testValidateSHAInRemoteGitRef(false, true, false, false);
		_testValidateSHAInRemoteGitRef(true, false, false, false);
		_testValidateSHAInRemoteGitRef(true, false, false, true);
		_testValidateSHAInRemoteGitRef(true, true, false, true);
	}

	private String _getBranchName(
			String branchName, String startPropertiesBranchName)
		throws Exception {

		Map<String, String> environmentMap = new HashMap<>();

		if (branchName != null) {
			environmentMap.put("TOP_LEVEL_BRANCH_NAME", branchName);
		}

		Environment environment = mockEnvironment(environmentMap);

		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Properties startProperties = new Properties();

		if (startPropertiesBranchName != null) {
			startProperties.setProperty(
				"TOP_LEVEL_BRANCH_NAME", startPropertiesBranchName);
		}

		Mockito.doReturn(
			startProperties
		).when(
			buildDatabase
		).getProperties(
			"start.properties"
		);

		BuildDatabaseUtil.setBuildDatabase(buildDatabase);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		String actualBranchName = defaultWorkspaceGitRepository.getBranchName();

		testEquals(
			actualBranchName, defaultWorkspaceGitRepository.getBranchName());

		Mockito.verify(
			environment, Mockito.times(1)
		).doGet(
			"TOP_LEVEL_BRANCH_NAME"
		);

		return actualBranchName;
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
			boolean gitArchiveEnabled, String jobName, boolean snapshot)
		throws Exception {

		Map<String, String> environmentMap = new HashMap<>();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			environmentMap.put("JOB_NAME", jobName);
		}

		environmentMap.put("MASTER_NETWORK_NAME", "aws-network");

		mockEnvironment(environmentMap);

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

		VerificationMode verificationMode = getVerificationMode(
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

		return _newDefaultWorkspaceGitRepository(false);
	}

	private DefaultWorkspaceGitRepository _newDefaultWorkspaceGitRepository(
			boolean pullRequest)
		throws Exception {

		File workingDirectory = File.createTempFile("workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		JSONObject jsonObject = new JSONObject();

		String baseBranchSHA = RandomTestUtil.randomSHA();
		String baseBranchUsername = RandomTestUtil.randomString();
		String repositoryName = RandomTestUtil.randomString();
		String senderBranchSHA = RandomTestUtil.randomSHA();

		String gitHubURL = JenkinsResultsParserUtil.combine(
			"https://github.com/", baseBranchUsername, "/", repositoryName);

		if (pullRequest) {
			gitHubURL = gitHubURL + "/pull/1";
		}

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
			"git_hub_url", gitHubURL
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

	private List<LocalGitCommit> _newLocalGitCommits(int count) {
		List<LocalGitCommit> localGitCommits = new ArrayList<>();

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		for (int i = 0; i < count; i++) {
			localGitCommits.add(
				GitCommitFactory.newLocalGitCommit(
					RandomTestUtil.randomString(), gitWorkingDirectory,
					RandomTestUtil.randomString(), RandomTestUtil.randomSHA(),
					RandomTestUtil.randomLong()));
		}

		return localGitCommits;
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
			getVerificationMode(dotGitDirArchiveRequired)
		).downloadDotGitArchive();

		Mockito.verify(
			defaultWorkspaceGitRepository, Mockito.times(1)
		).downloadGitArchive();
	}

	private void _testFetchCommitFileSHA(boolean commitFileIsSHA)
		throws Exception {

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		defaultWorkspaceGitRepository.setCommitFileIsSHA(commitFileIsSHA);

		String senderBranchSHA =
			defaultWorkspaceGitRepository.getSenderBranchSHA();

		String fetchCommand = "git fetch -f --depth=1 upstream";

		Shell shell = mockShell();

		setShellCommandOutput(fetchCommand, shell, "");

		ReflectionTestUtil.invoke(
			defaultWorkspaceGitRepository, "_fetchCommitFileSHA",
			new Class<?>[0]);

		Mockito.verify(
			shell, getVerificationMode(commitFileIsSHA)
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, fetchCommand, senderBranchSHA))
		);
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

	private void _testGetLocalGitBranch(
			boolean pullRequest, boolean senderBranchSHAExists)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput("git ls-remote", shell, "");

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository(pullRequest);
		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		Mockito.doReturn(
			"liferay-portal"
		).when(
			gitWorkingDirectory
		).getGitRepositoryName();

		Mockito.doReturn(
			"master"
		).when(
			gitWorkingDirectory
		).getUpstreamBranchName();

		String senderBranchSHA =
			defaultWorkspaceGitRepository.getSenderBranchSHA();

		Mockito.doReturn(
			senderBranchSHAExists
		).when(
			gitWorkingDirectory
		).localSHAExists(
			senderBranchSHA
		);

		if (pullRequest) {
			try {
				defaultWorkspaceGitRepository.getLocalGitBranch();

				Assert.fail();
			}
			catch (RuntimeException runtimeException) {
				String message = runtimeException.getMessage();

				Assert.assertTrue(
					message.contains(
						defaultWorkspaceGitRepository.getUpstreamBranchName()));
			}

			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).validateSHAInRemoteGitRef(
				Mockito.anyString(), Mockito.nullable(RemoteGitRef.class),
				Mockito.anyString()
			);

			return;
		}

		if (senderBranchSHAExists) {
			defaultWorkspaceGitRepository.getLocalGitBranch();

			Mockito.verify(
				defaultWorkspaceGitRepository, Mockito.never()
			).validateSHAInRemoteGitRef(
				Mockito.anyString(), Mockito.nullable(RemoteGitRef.class),
				Mockito.anyString()
			);

			return;
		}

		try {
			defaultWorkspaceGitRepository.getLocalGitBranch();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message.contains(
					defaultWorkspaceGitRepository.getSenderBranchName()));
			Assert.assertTrue(message.contains(senderBranchSHA));
		}
	}

	private void _testGetProperties(
			String expectedPropertyValue, String upstreamBranchName)
		throws Exception {

		mockEnvironment(Collections.<String, String>emptyMap());

		Properties buildProperties = new Properties();

		String propertyName = RandomTestUtil.randomString();
		String propertyType = RandomTestUtil.randomString();

		String basePropertyName = propertyType + "[" + propertyName + "]";

		buildProperties.setProperty(basePropertyName + "[7.4.x]", "7.4.x");
		buildProperties.setProperty(basePropertyName + "[master]", "master");
		buildProperties.setProperty(basePropertyName, "base");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		defaultWorkspaceGitRepository.addPropertyOption(upstreamBranchName);

		Properties properties = defaultWorkspaceGitRepository.getProperties(
			propertyType);

		testEquals(expectedPropertyValue, properties.getProperty(propertyName));
	}

	private void _testPartitionLocalGitCommits(
			int count, boolean exceptionThrown,
			List<LocalGitCommit> localGitCommits)
		throws Exception {

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		if (exceptionThrown) {
			try {
				defaultWorkspaceGitRepository.partitionLocalGitCommits(
					localGitCommits, count);

				Assert.fail("Expected IllegalArgumentException");
			}
			catch (IllegalArgumentException illegalArgumentException) {
				testEquals(
					"Invalid count " + count,
					illegalArgumentException.getMessage());
			}

			return;
		}

		List<LocalGitCommit> expectedLocalGitCommits = new ArrayList<>();

		if (localGitCommits != null) {
			expectedLocalGitCommits.addAll(localGitCommits);
		}

		List<List<LocalGitCommit>> localGitCommitsLists =
			defaultWorkspaceGitRepository.partitionLocalGitCommits(
				localGitCommits, count);

		Assert.assertTrue(
			localGitCommitsLists.size() <= Math.min(
				count, expectedLocalGitCommits.size()));

		if (localGitCommits != null) {
			testEquals(expectedLocalGitCommits, localGitCommits);
		}

		List<LocalGitCommit> actualLocalGitCommits = new ArrayList<>();

		for (List<LocalGitCommit> localGitCommitsPartition :
				localGitCommitsLists) {

			Assert.assertFalse(localGitCommitsPartition.isEmpty());

			actualLocalGitCommits.addAll(localGitCommitsPartition);
		}

		testEquals(expectedLocalGitCommits, actualLocalGitCommits);
	}

	private void _testPrepareGitWorkingDirectory(
			boolean gitArchiveEnabled, boolean snapshot)
		throws Exception {

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

		Mockito.doReturn(
			snapshot
		).when(
			defaultWorkspaceGitRepository
		).isSnapshot();

		Mockito.doNothing(
		).when(
			defaultWorkspaceGitRepository
		).promoteGitArchive();

		defaultWorkspaceGitRepository.prepareGitWorkingDirectory();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			getVerificationMode(gitArchiveEnabled && snapshot)
		).downloadGitArchives();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			getVerificationMode(!gitArchiveEnabled || !snapshot)
		).initializeGitWorkingDirectory();

		Mockito.verify(
			defaultWorkspaceGitRepository,
			getVerificationMode(gitArchiveEnabled)
		).promoteGitArchive();
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

		_testPrepareGitWorkingDirectory(gitArchiveEnabled, snapshot);
	}

	private void _testPrepareGitWorkingDirectory(
			boolean gitArchiveEnabled, String jobName)
		throws Exception {

		File commandsDir = new File(
			JenkinsResultsParserUtil.getJenkinsRepositoryDir(), "commands");

		Assume.assumeTrue(
			JenkinsResultsParserUtil.getCanonicalPath(commandsDir) +
				" does not exist",
			commandsDir.isDirectory());

		JenkinsResultsParserUtil.setBuildProperties(
			JenkinsResultsParserUtil.getProperties(
				new File(commandsDir, "build.properties"),
				new File(commandsDir, "build-aws.properties")));

		_setUpEnvironment(jobName, null);

		_testPrepareGitWorkingDirectory(gitArchiveEnabled, true);
	}

	private void _testTearDown(
			boolean dotGitFolderExists, boolean gitArchiveEnabled,
			boolean snapshot)
		throws Exception {

		_setUpEnvironment(RandomTestUtil.randomString(), null);

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"git.archive.enabled", String.valueOf(gitArchiveEnabled));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Shell shell = mockShell();

		setShellCommandOutput("rm -fr", shell, "");

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.doReturn(
			Mockito.mock(LocalGitBranch.class)
		).when(
			gitWorkingDirectory
		).getUpstreamLocalGitBranch();

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		ReflectionTestUtil.setFieldValue(
			defaultWorkspaceGitRepository, "_gitWorkingDirectory",
			gitWorkingDirectory);

		IOException ioException = new IOException();

		Mockito.doThrow(
			ioException
		).when(
			defaultWorkspaceGitRepository
		).prepareGitWorkingDirectory();

		defaultWorkspaceGitRepository.setSnapshot(snapshot);

		if (dotGitFolderExists) {
			File dotGitFolder = new File(
				defaultWorkspaceGitRepository.getDirectory(), ".git");

			dotGitFolder.mkdir();
		}

		try {
			defaultWorkspaceGitRepository.setUp();

			Assert.fail("Expected RuntimeException");
		}
		catch (RuntimeException runtimeException) {
			testSame(ioException, runtimeException.getCause());
		}

		Assert.assertFalse(defaultWorkspaceGitRepository.isSetUp());

		defaultWorkspaceGitRepository.tearDown();

		if (gitArchiveEnabled && snapshot) {
			Mockito.verify(
				shell
			).doExecute(
				Mockito.argThat(
					executionRequest -> hasCommand(
						executionRequest, "rm -fr",
						String.valueOf(
							defaultWorkspaceGitRepository.getDirectory())))
			);

			return;
		}

		VerificationMode verificationMode = getVerificationMode(
			dotGitFolderExists);

		Mockito.verify(
			gitWorkingDirectory, verificationMode
		).clean();

		Mockito.verify(
			gitWorkingDirectory, verificationMode
		).cleanTempBranches();

		Mockito.verify(
			gitWorkingDirectory, verificationMode
		).deleteLockFiles();
	}

	private void _testValidateSHAInRemoteGitRef(
			boolean exceptionThrown, boolean localSHAExists,
			boolean refContainsSHA, boolean remoteGitRefFound)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);
		LocalGitBranch localGitBranch = null;
		RemoteGitRef remoteGitRef = null;
		String remoteURL = RandomTestUtil.randomString();

		if (remoteGitRefFound) {
			localGitBranch = Mockito.mock(LocalGitBranch.class);
			remoteGitRef = Mockito.mock(RemoteGitRef.class);

			Mockito.when(
				gitWorkingDirectory.fetch(remoteGitRef)
			).thenReturn(
				localGitBranch
			);

			Mockito.when(
				remoteGitRef.getRemoteURL()
			).thenReturn(
				remoteURL
			);
		}

		String sha = RandomTestUtil.randomSHA();

		Mockito.when(
			gitWorkingDirectory.localSHAExists(sha)
		).thenReturn(
			localSHAExists
		);

		Mockito.when(
			gitWorkingDirectory.refContainsSHA(localGitBranch, sha)
		).thenReturn(
			refContainsSHA
		);

		DefaultWorkspaceGitRepository defaultWorkspaceGitRepository =
			_newDefaultWorkspaceGitRepository();

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			defaultWorkspaceGitRepository
		).getGitWorkingDirectory();

		String branchName = RandomTestUtil.randomString();

		try {
			defaultWorkspaceGitRepository.validateSHAInRemoteGitRef(
				branchName, remoteGitRef, sha);

			Assert.assertFalse(exceptionThrown);
		}
		catch (RuntimeException runtimeException) {
			Assert.assertTrue(exceptionThrown);

			String message = runtimeException.getMessage();

			Assert.assertTrue(message.contains(branchName));
			Assert.assertTrue(message.contains(sha));

			if (remoteGitRefFound) {
				Assert.assertTrue(message.contains(remoteURL));
			}
		}

		InOrder inOrder = Mockito.inOrder(gitWorkingDirectory);

		if (remoteGitRefFound) {
			inOrder.verify(
				gitWorkingDirectory
			).fetch(
				remoteGitRef
			);

			inOrder.verify(
				gitWorkingDirectory
			).refContainsSHA(
				localGitBranch, sha
			);

			return;
		}

		Mockito.verify(
			gitWorkingDirectory, Mockito.never()
		).fetch(
			Mockito.nullable(RemoteGitRef.class)
		);

		inOrder.verify(
			gitWorkingDirectory
		).localSHAExists(
			sha
		);
	}

}