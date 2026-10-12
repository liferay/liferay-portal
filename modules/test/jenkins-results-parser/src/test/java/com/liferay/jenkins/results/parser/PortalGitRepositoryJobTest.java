/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PortalGitRepositoryJobTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetJobPropertiesFiles() throws IOException {
		Properties buildProperties = new Properties();

		File baseGitRepositoryDir = temporaryFolder.newFolder();

		buildProperties.setProperty(
			"base.repository.dir", baseGitRepositoryDir.getPath());

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		String hostname = RandomTestUtil.randomString();

		mockEnvironment(Collections.singletonMap("HOSTNAME", hostname));

		File gitRepositoryDir = temporaryFolder.newFolder();

		String buildHostnamePropertiesFileName =
			JenkinsResultsParserUtil.combine("build.", hostname, ".properties");
		String testHostnamePropertiesFileName =
			JenkinsResultsParserUtil.combine("test.", hostname, ".properties");

		String upstreamBranchName = RandomTestUtil.randomString();

		_testGetJobPropertiesFiles(
			Arrays.asList(
				new File(gitRepositoryDir, "tools/sdk/build.properties"),
				new File(gitRepositoryDir, "build.properties"),
				new File(gitRepositoryDir, buildHostnamePropertiesFileName),
				new File(gitRepositoryDir, testHostnamePropertiesFileName),
				new File(gitRepositoryDir, "test.properties")),
			gitRepositoryDir, upstreamBranchName);

		File privatePortalDir = new File(
			baseGitRepositoryDir,
			JenkinsResultsParserUtil.combine(
				"liferay-portal-", upstreamBranchName, "-private"));

		_testGetJobPropertiesFiles(
			Arrays.asList(
				new File(gitRepositoryDir, "tools/sdk/build.properties"),
				new File(gitRepositoryDir, "build.properties"),
				new File(gitRepositoryDir, buildHostnamePropertiesFileName),
				new File(privatePortalDir, testHostnamePropertiesFileName),
				new File(privatePortalDir, "test-private.properties"),
				new File(gitRepositoryDir, testHostnamePropertiesFileName),
				new File(gitRepositoryDir, "test.properties")),
			gitRepositoryDir, upstreamBranchName + "-private");
	}

	@Test
	public void testGetPortalUpstreamBranchName() {
		String upstreamBranchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(
			new JSONObject(), upstreamBranchName, upstreamBranchName);

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		_testGetPortalUpstreamBranchName(
			new JSONObject(
			).put(
				"upstream_branch_name", portalUpstreamBranchName
			),
			portalUpstreamBranchName, upstreamBranchName);

		_testGetPortalUpstreamBranchName(
			null, upstreamBranchName, upstreamBranchName);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testGetJobPropertiesFiles(
		List<File> expectedJobPropertiesFiles, File gitRepositoryDir,
		String upstreamBranchName) {

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			gitRepositoryDir
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class,
					invocation -> portalGitWorkingDirectory)) {

			PortalGitRepositoryJob portalGitRepositoryJob = Mockito.mock(
				PortalGitRepositoryJob.class,
				Mockito.withSettings(
				).defaultAnswer(
					Mockito.CALLS_REAL_METHODS
				).useConstructor(
					new JSONObject(
					).put(
						"build_profile", "dxp"
					).put(
						"git_repository_dir", RandomTestUtil.randomString()
					).put(
						"job_name", RandomTestUtil.randomString()
					).put(
						"upstream_branch_name", upstreamBranchName
					)
				));

			testEquals(
				expectedJobPropertiesFiles,
				portalGitRepositoryJob.getJobPropertiesFiles());
		}
	}

	private void _testGetPortalUpstreamBranchName(
		JSONObject branchJSONObject, String expectedPortalUpstreamBranchName,
		String upstreamBranchName) {

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			temporaryFolder.getRoot()
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class,
					invocation -> portalGitWorkingDirectory)) {

			Mockito.mock(
				PortalGitRepositoryJob.class,
				Mockito.withSettings(
				).defaultAnswer(
					Mockito.CALLS_REAL_METHODS
				).useConstructor(
					new JSONObject(
					).put(
						"branch", branchJSONObject
					).put(
						"build_profile", "dxp"
					).put(
						"git_repository_dir", RandomTestUtil.randomString()
					).put(
						"job_name", RandomTestUtil.randomString()
					).put(
						"upstream_branch_name", upstreamBranchName
					)
				));

			gitWorkingDirectoryFactoryMockedStatic.verify(
				() -> GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					expectedPortalUpstreamBranchName));
		}
	}

}