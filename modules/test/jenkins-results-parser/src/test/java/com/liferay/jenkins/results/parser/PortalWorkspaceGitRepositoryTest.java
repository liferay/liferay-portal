/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class PortalWorkspaceGitRepositoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetPortalTestProperties() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("portal.bundle.tomcat[version.0]", "url.0");
		buildProperties.setProperty("portal.bundle.tomcat[version.1]", "url.1");
		buildProperties.setProperty("portal.bundle.tomcat[version.2]", "url.2");
		buildProperties.setProperty(
			"portal.bundle.tomcat[version.env]", "url.env");
		buildProperties.setProperty(
			"portal.latest.bundle.version", "version.0");
		buildProperties.setProperty(
			"portal.latest.bundle.version[7.0.x]", "version.2");
		buildProperties.setProperty(
			"portal.latest.bundle.version[master]", "version.1");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Map<String, String> environmentMap = Collections.emptyMap();

		_testGetPortalTestProperties(
			"url.0", "version.0", environmentMap, null);
		_testGetPortalTestProperties(
			"url.1", "version.1", environmentMap, "master");
		_testGetPortalTestProperties(
			"url.2", "version.2", environmentMap, "7.0.x");

		environmentMap = Collections.singletonMap(
			"PORTAL_LATEST_BUNDLE_VERSION", "version.env");

		_testGetPortalTestProperties(
			"url.env", "version.env", environmentMap, null);
		_testGetPortalTestProperties(
			"url.env", "version.env", environmentMap, "7.0.x");
		_testGetPortalTestProperties(
			"url.env", "version.env", environmentMap, "master");
	}

	@Test
	public void testSetUp() throws Exception {
		_testSetUp(false);
		_testSetUp(true);
	}

	@Test
	public void testSetUpAdditionalCaches() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("binaries.cache.enabled", "false");
		buildProperties.setProperty("binaries.cache.enabled[job*]", "true");
		buildProperties.setProperty("binaries.cache.enabled[job-1]", "false");
		buildProperties.setProperty("binaries.cache.enabled[one][two]", "true");
		buildProperties.setProperty("binaries.cache.enabled[suite*]", "true");
		buildProperties.setProperty("binaries.cache.enabled[suite-1]", "false");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		_testSetUpAdditionalCaches(false, "one", null);
		_testSetUpAdditionalCaches(false, "suite-1", null);
		_testSetUpAdditionalCaches(false, "wrong", null);
		_testSetUpAdditionalCaches(false, null, "job-1");
		_testSetUpAdditionalCaches(false, null, "two");
		_testSetUpAdditionalCaches(false, null, "wrong");
		_testSetUpAdditionalCaches(false, null, null);
		_testSetUpAdditionalCaches(true, "one", "two");
		_testSetUpAdditionalCaches(true, "suite", null);
		_testSetUpAdditionalCaches(true, "suite-0", null);
		_testSetUpAdditionalCaches(true, null, "job");
		_testSetUpAdditionalCaches(true, null, "job-0");
	}

	@Test
	public void testSetUpWorkspaceYarnMirrors() throws Exception {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		portalWorkspaceGitRepository.setUpWorkspaceYarnMirrors();

		File workspacesDirectory = new File(
			portalWorkspaceGitRepository.getDirectory(), "workspaces");

		Assert.assertFalse(workspacesDirectory.exists());

		File gradleDirectory = new File(workspacesDirectory, ".gradle");

		gradleDirectory.mkdirs();

		File workspaceDirectory1 = _createWorkspace(workspacesDirectory);
		File workspaceDirectory2 = _createWorkspace(workspacesDirectory);

		portalWorkspaceGitRepository.setUpWorkspaceYarnMirrors();

		Assert.assertEquals(
			Paths.get("..", "node_modules_cache"),
			Files.readSymbolicLink(
				_getNodeModulesCachePath(workspaceDirectory1)));
		Assert.assertEquals(
			Paths.get("..", "node_modules_cache"),
			Files.readSymbolicLink(
				_getNodeModulesCachePath(workspaceDirectory2)));

		Assert.assertFalse(
			Files.exists(_getNodeModulesCachePath(gradleDirectory)));
		Assert.assertTrue(
			Files.isDirectory(_getNodeModulesCachePath(workspacesDirectory)));
	}

	@Test
	public void testSetUpYarn() throws Exception {
		_testSetUpYarn(false);
		_testSetUpYarn(true);
	}

	@Test
	public void testSetUpYarnCache() throws Exception {
		_testSetUpYarnCache(false, false);
		_testSetUpYarnCache(false, true);
		_testSetUpYarnCache(true, false);
		_testSetUpYarnCache(true, true);
	}

	private File _createWorkspace(File workspacesDirectory) throws Exception {
		File workspaceDirectory = new File(
			workspacesDirectory, RandomTestUtil.randomString());

		workspaceDirectory.mkdirs();

		File yarnRCFile = new File(workspaceDirectory, ".yarnrc");

		yarnRCFile.createNewFile();

		return workspaceDirectory;
	}

	private Path _getNodeModulesCachePath(File directory) {
		return Paths.get(directory.getPath(), "node_modules_cache");
	}

	private PortalWorkspaceGitRepository _newPortalWorkspaceGitRepository()
		throws Exception {

		File workingDirectory = File.createTempFile("portal-workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		String baseBranchSHA = RandomTestUtil.randomSHA();
		String senderBranchSHA = RandomTestUtil.randomSHA();

		JSONObject jsonObject = new JSONObject(
		).put(
			"base_branch_head_sha", baseBranchSHA
		).put(
			"base_branch_sha", baseBranchSHA
		).put(
			"base_branch_username", RandomTestUtil.randomString()
		).put(
			"directory",
			JenkinsResultsParserUtil.getCanonicalPath(workingDirectory)
		).put(
			"directory_name", "liferay-portal"
		).put(
			"git_hub_url", "https://github.com/liferay/liferay-portal"
		).put(
			"name", "liferay-portal"
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

		return Mockito.spy(new PortalWorkspaceGitRepository(jsonObject));
	}

	private void _testGetPortalTestProperties(
			String bundleURL, String bundleVersion,
			Map<String, String> environmentMap, String upstreamBranchName)
		throws Exception {

		mockEnvironment(environmentMap);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		Mockito.doReturn(
			upstreamBranchName
		).when(
			portalWorkspaceGitRepository
		).getUpstreamBranchName();

		Properties portalTestProperties =
			portalWorkspaceGitRepository.getPortalTestProperties();

		Assert.assertEquals(
			bundleURL,
			portalTestProperties.getProperty(
				"test.released.test.portal.bundle.zip.url"));
		Assert.assertEquals(
			bundleVersion,
			portalTestProperties.getProperty(
				"test.released.release.bundle.version"));
	}

	private void _testSetUp(boolean snapshot) throws Exception {
		mockEnvironment(
			Collections.singletonMap("MASTER_NETWORK_NAME", "aws-network"));

		Properties buildProperties = new Properties();

		buildProperties.setProperty("binaries.cache.enabled", "true");
		buildProperties.setProperty("yarn.cache.enabled", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).downloadYarnCache();

		Mockito.doReturn(
			true
		).when(
			portalWorkspaceGitRepository
		).isYarnCacheAvailable();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).prepareGitWorkingDirectory();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).setUpBinariesCache();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).touchYarnCache();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).uploadGitArchives();

		portalWorkspaceGitRepository.setSnapshot(snapshot);

		portalWorkspaceGitRepository.setUp();

		Assert.assertTrue(portalWorkspaceGitRepository.isSetUp());

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.times(1)
		).downloadYarnCache();

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.times(1)
		).setUpBinariesCache();

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.times(1)
		).touchYarnCache();
	}

	private void _testSetUpAdditionalCaches(
			boolean binariesCacheEnabled, String ciTestSuite, String jobName)
		throws Exception {

		Map<String, String> environmentMap = new HashMap<>();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(ciTestSuite)) {
			environmentMap.put("CI_TEST_SUITE", ciTestSuite);
		}

		environmentMap.put("MASTER_NETWORK_NAME", "aws-network");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			environmentMap.put("JOB_NAME", jobName);
		}

		mockEnvironment(environmentMap);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).setUpBinariesCache();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).setUpWorkspaceYarnMirrors();

		portalWorkspaceGitRepository.setUpAdditionalCaches();

		if (binariesCacheEnabled) {
			Mockito.verify(
				portalWorkspaceGitRepository, Mockito.times(1)
			).setUpBinariesCache();

			Mockito.verify(
				portalWorkspaceGitRepository, Mockito.times(1)
			).setUpWorkspaceYarnMirrors();

			return;
		}

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.never()
		).setUpBinariesCache();

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.never()
		).setUpWorkspaceYarnMirrors();
	}

	private void _testSetUpYarn(boolean yarnInstalled) throws Exception {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			portalWorkspaceGitRepository
		).getGitWorkingDirectory();

		if (yarnInstalled) {
			File nodeModulesDirectory = new File(
				portalWorkspaceGitRepository.getDirectory(),
				"modules/node_modules");

			nodeModulesDirectory.mkdirs();

			File yarnIntegrityFile = new File(
				nodeModulesDirectory, ".yarn-integrity");

			yarnIntegrityFile.createNewFile();
		}

		portalWorkspaceGitRepository.setUpYarn();

		Mockito.verify(
			portalGitWorkingDirectory, getVerificationMode(!yarnInstalled)
		).setUpYarn();
	}

	private void _testSetUpYarnCache(
			boolean snapshot, boolean yarnCacheAvailable)
		throws Exception {

		mockEnvironment(
			Collections.singletonMap("MASTER_NETWORK_NAME", "aws-network"));

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).downloadYarnCache();

		Mockito.doReturn(
			yarnCacheAvailable
		).when(
			portalWorkspaceGitRepository
		).isYarnCacheAvailable();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).setUpYarn();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).touchYarnCache();

		Mockito.doNothing(
		).when(
			portalWorkspaceGitRepository
		).uploadYarnCache();

		portalWorkspaceGitRepository.setSnapshot(snapshot);

		portalWorkspaceGitRepository.setUpYarnCache();

		Mockito.verify(
			portalWorkspaceGitRepository,
			getVerificationMode(yarnCacheAvailable)
		).downloadYarnCache();

		Mockito.verify(
			portalWorkspaceGitRepository,
			getVerificationMode(!yarnCacheAvailable)
		).setUpYarn();

		Mockito.verify(
			portalWorkspaceGitRepository,
			getVerificationMode(yarnCacheAvailable)
		).touchYarnCache();

		Mockito.verify(
			portalWorkspaceGitRepository,
			getVerificationMode(!yarnCacheAvailable)
		).uploadYarnCache();
	}

}