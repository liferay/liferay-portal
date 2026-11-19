/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class JobCacheUtilTest extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Map<String, String> environmentMap = new HashMap<>();

		environmentMap.put("JENKINS_URL", RandomTestUtil.randomString());
		environmentMap.put("MASTER_NETWORK_NAME", "aws-network");

		mockEnvironment(environmentMap);

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"cloud.ci.s3.bucket.job.cache.path", _JOB_CACHE_PATH);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);
	}

	@Test
	public void testCacheJob() throws Exception {
		PortalWorkspace portalWorkspace = WorkspaceTestUtil.newWorkspace(
			PortalWorkspace.class, PortalWorkspaceGitRepository.class);

		WorkspaceTestUtil.setWorkspaces(portalWorkspace);

		PortalGitRepositoryJob portalGitRepositoryJob = Mockito.mock(
			PortalGitRepositoryJob.class,
			Mockito.withSettings(
			).extraInterfaces(
				TestSuiteJob.class
			));

		String jobName = RandomTestUtil.randomString();

		Mockito.doReturn(
			jobName
		).when(
			portalGitRepositoryJob
		).getJobName();

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			portalGitRepositoryJob
		).getPortalGitWorkingDirectory();

		String testSuiteName = RandomTestUtil.randomString();

		Mockito.doReturn(
			testSuiteName
		).when(
			(TestSuiteJob)portalGitRepositoryJob
		).getTestSuiteName();

		try (MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic =
				Mockito.mockStatic(CloudBucketUtil.class)) {

			JobCacheUtil.cacheJob(portalGitRepositoryJob);

			ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
				String.class);

			cloudBucketUtilMockedStatic.verify(
				() -> CloudBucketUtil.uploadS3File(
					argumentCaptor.capture(), Mockito.any(File.class)));

			String s3ObjectPath = argumentCaptor.getValue();

			testEquals(
				JenkinsResultsParserUtil.combine(
					_JOB_CACHE_PATH, "/", jobName, "/",
					portalWorkspace.getS3ObjectPathName(), "/", testSuiteName,
					".json.gz"),
				s3ObjectPath);

			Assert.assertFalse(
				JobCacheUtil.isJobCached(
					jobName, portalGitWorkingDirectory, testSuiteName));

			cloudBucketUtilMockedStatic.verify(
				() -> CloudBucketUtil.isS3ObjectPathAvailable(s3ObjectPath));
		}
	}

	@Test
	public void testGetCachedJobJSONObject() {
		PortalWorkspace portalWorkspace = WorkspaceTestUtil.newWorkspace(
			PortalWorkspace.class, PortalWorkspaceGitRepository.class);

		WorkspaceTestUtil.setWorkspaces(portalWorkspace);

		String jobName = RandomTestUtil.randomString();
		String s3ObjectPathName = portalWorkspace.getS3ObjectPathName();
		String testSuiteName = RandomTestUtil.randomString();

		try (MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic =
				Mockito.mockStatic(CloudBucketUtil.class)) {

			_testGetCachedJobJSONObject(
				cloudBucketUtilMockedStatic,
				JenkinsResultsParserUtil.combine(
					_JOB_CACHE_PATH, "/", jobName, "__", jobName, "/",
					s3ObjectPathName, "/default.json.gz"),
				JenkinsResultsParserUtil.combine(jobName, ")/(", jobName),
				Mockito.mock(PortalGitWorkingDirectory.class), "");
			_testGetCachedJobJSONObject(
				cloudBucketUtilMockedStatic,
				JenkinsResultsParserUtil.combine(
					_JOB_CACHE_PATH, "/", jobName, "__", jobName, "__/",
					s3ObjectPathName, "/", testSuiteName, ".json.gz"),
				JenkinsResultsParserUtil.combine(jobName, "(", jobName, ")"),
				Mockito.mock(PortalGitWorkingDirectory.class), testSuiteName);
			_testGetCachedJobJSONObject(
				cloudBucketUtilMockedStatic,
				JenkinsResultsParserUtil.combine(
					_JOB_CACHE_PATH, "/", jobName, "/", s3ObjectPathName,
					"/default.json.gz"),
				jobName, Mockito.mock(PortalGitWorkingDirectory.class), null);
			_testGetCachedJobJSONObject(
				cloudBucketUtilMockedStatic, null, jobName, null,
				testSuiteName);
		}
	}

	@Test
	public void testGetPortalWorkspace() {
		PluginsWorkspace pluginsWorkspace = WorkspaceTestUtil.newWorkspace(
			PluginsWorkspace.class, PluginsWorkspaceGitRepository.class);
		PortalWorkspace portalWorkspace = WorkspaceTestUtil.newWorkspace(
			PortalWorkspace.class, PortalWorkspaceGitRepository.class);
		SubrepositoryWorkspace subrepositoryWorkspace =
			WorkspaceTestUtil.newWorkspace(
				SubrepositoryWorkspace.class, WorkspaceGitRepository.class);

		try (MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic =
				Mockito.mockStatic(CloudBucketUtil.class)) {

			_testGetPortalWorkspace(
				cloudBucketUtilMockedStatic,
				pluginsWorkspace.getS3ObjectPathName(), pluginsWorkspace,
				subrepositoryWorkspace);
			_testGetPortalWorkspace(
				cloudBucketUtilMockedStatic,
				portalWorkspace.getS3ObjectPathName(), pluginsWorkspace,
				portalWorkspace);
			_testGetPortalWorkspace(
				cloudBucketUtilMockedStatic,
				portalWorkspace.getS3ObjectPathName(), portalWorkspace,
				pluginsWorkspace);
			_testGetPortalWorkspace(
				cloudBucketUtilMockedStatic, null,
				WorkspaceTestUtil.newWorkspace(
					Workspace.class, WorkspaceGitRepository.class));
			_testGetPortalWorkspace(cloudBucketUtilMockedStatic, null);
		}
	}

	private void _testGetCachedJobJSONObject(
		MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic,
		String expectedS3ObjectPath, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName) {

		cloudBucketUtilMockedStatic.clearInvocations();

		Assert.assertNull(
			JobCacheUtil.getCachedJobJSONObject(
				jobName, portalGitWorkingDirectory, testSuiteName));

		if (expectedS3ObjectPath == null) {
			cloudBucketUtilMockedStatic.verify(
				() -> CloudBucketUtil.isS3ObjectPathAvailable(
					Mockito.nullable(String.class)),
				Mockito.never());

			return;
		}

		cloudBucketUtilMockedStatic.verify(
			() -> CloudBucketUtil.isS3ObjectPathAvailable(
				expectedS3ObjectPath));
	}

	private void _testGetPortalWorkspace(
		MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic,
		String expectedS3ObjectPathName, Workspace... workspaces) {

		WorkspaceTestUtil.setWorkspaces(workspaces);

		String expectedS3ObjectPath = null;
		String jobName = RandomTestUtil.randomString();
		String testSuiteName = RandomTestUtil.randomString();

		if (expectedS3ObjectPathName != null) {
			expectedS3ObjectPath = JenkinsResultsParserUtil.combine(
				_JOB_CACHE_PATH, "/", jobName, "/", expectedS3ObjectPathName,
				"/", testSuiteName, ".json.gz");
		}

		_testGetCachedJobJSONObject(
			cloudBucketUtilMockedStatic, expectedS3ObjectPath, jobName,
			Mockito.mock(PortalGitWorkingDirectory.class), testSuiteName);
	}

	private static final String _JOB_CACHE_PATH = RandomTestUtil.randomString();

}