/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

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
		buildProperties.setProperty(
			"portal.latest.bundle.version", "version.0");

		buildProperties.setProperty("portal.bundle.tomcat[version.1]", "url.1");
		buildProperties.setProperty(
			"portal.latest.bundle.version[master]", "version.1");

		buildProperties.setProperty("portal.bundle.tomcat[version.2]", "url.2");
		buildProperties.setProperty(
			"portal.latest.bundle.version[7.0.x]", "version.2");

		buildProperties.setProperty(
			"portal.bundle.tomcat[version.env]", "url.env");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Map<String, String> environmentValues = Collections.emptyMap();

		_testGetPortalTestProperties(
			"url.0", "version.0", environmentValues, null);
		_testGetPortalTestProperties(
			"url.1", "version.1", environmentValues, "master");
		_testGetPortalTestProperties(
			"url.2", "version.2", environmentValues, "7.0.x");

		environmentValues = Collections.singletonMap(
			"PORTAL_LATEST_BUNDLE_VERSION", "version.env");

		_testGetPortalTestProperties(
			"url.env", "version.env", environmentValues, null);
		_testGetPortalTestProperties(
			"url.env", "version.env", environmentValues, "master");
		_testGetPortalTestProperties(
			"url.env", "version.env", environmentValues, "7.0.x");
	}

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

	@Test
	public void testSetUpAdditionalCaches() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("binaries.cache.enabled", "false");
		buildProperties.setProperty("binaries.cache.enabled[job*]", "true");
		buildProperties.setProperty("binaries.cache.enabled[job-1]", "false");
		buildProperties.setProperty("binaries.cache.enabled[suite*]", "true");
		buildProperties.setProperty("binaries.cache.enabled[suite-1]", "false");
		buildProperties.setProperty("binaries.cache.enabled[one][two]", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		_testSetUpAdditionalCaches(false, null, null);

		_testSetUpAdditionalCaches(true, "suite", null);
		_testSetUpAdditionalCaches(true, "suite-0", null);
		_testSetUpAdditionalCaches(false, "suite-1", null);
		_testSetUpAdditionalCaches(false, "wrong", null);

		_testSetUpAdditionalCaches(true, null, "job");
		_testSetUpAdditionalCaches(true, null, "job-0");
		_testSetUpAdditionalCaches(false, null, "job-1");
		_testSetUpAdditionalCaches(false, null, "wrong");

		_testSetUpAdditionalCaches(false, "one", null);
		_testSetUpAdditionalCaches(false, null, "two");
		_testSetUpAdditionalCaches(true, "one", "two");
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

	private PortalWorkspaceGitRepository _newPortalWorkspaceGitRepository()
		throws Exception {

		File workingDirectory = File.createTempFile("portal-workspace-", null);

		workingDirectory.delete();

		workingDirectory.mkdir();

		File gitDirectory = new File(workingDirectory, ".git");

		gitDirectory.mkdir();

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
			"directory_name", "liferay-portal"
		).put(
			"git_hub_url", "https://github.com/liferay/liferay-portal"
		).put(
			"name", "liferay-portal"
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

		PortalWorkspaceGitRepository portalWorkspaceGitRepository = Mockito.spy(
			new PortalWorkspaceGitRepository(jsonObject));

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.doReturn(
			gitWorkingDirectory
		).when(
			portalWorkspaceGitRepository
		).getGitWorkingDirectory();

		LocalGitBranch localGitBranch = Mockito.mock(LocalGitBranch.class);

		Mockito.doReturn(
			"0987654321098765432109876543210987654321"
		).when(
			localGitBranch
		).getSHA();

		Mockito.doReturn(
			localGitBranch
		).when(
			portalWorkspaceGitRepository
		).getLocalGitBranch();

		return portalWorkspaceGitRepository;
	}

	private void _testGetPortalTestProperties(
			String bundleURL, String bundleVersion,
			Map<String, String> environmentValues, String upstreamBranchName)
		throws Exception {

		mockEnvironment(environmentValues);

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
			bundleVersion,
			portalTestProperties.getProperty(
				"test.released.release.bundle.version"));
		Assert.assertEquals(
			bundleURL,
			portalTestProperties.getProperty(
				"test.released.test.portal.bundle.zip.url"));
	}

	private void _testSetUpAdditionalCaches(
			boolean binariesCacheEnabled, String ciTestSuite, String jobName)
		throws Exception {

		Map<String, String> environmentValues = new HashMap<>();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(ciTestSuite)) {
			environmentValues.put("CI_TEST_SUITE", ciTestSuite);
		}

		environmentValues.put("MASTER_NETWORK_NAME", "aws-network");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			environmentValues.put("JOB_NAME", jobName);
		}

		mockEnvironment(environmentValues);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_newPortalWorkspaceGitRepository();

		portalWorkspaceGitRepository.setUpAdditionalCaches();

		if (binariesCacheEnabled) {
			Assert.assertTrue(
				portalWorkspaceGitRepository.isBinariesCacheEnabled());

			Mockito.verify(
				portalWorkspaceGitRepository, Mockito.times(1)
			).setUpBinariesCache();

			return;
		}

		Assert.assertFalse(
			portalWorkspaceGitRepository.isBinariesCacheEnabled());

		Mockito.verify(
			portalWorkspaceGitRepository, Mockito.never()
		).setUpBinariesCache();
	}

}