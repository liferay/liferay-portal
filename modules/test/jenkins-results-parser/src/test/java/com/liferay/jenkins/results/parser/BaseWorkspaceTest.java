/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseWorkspaceTest extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			null);

		Map<String, WorkspaceGitRepository> workspaceGitRepositories =
			ReflectionTestUtil.getFieldValue(
				GitRepositoryFactory.class, "_workspaceGitRepositories");

		workspaceGitRepositories.clear();

		_executorService.shutdownNow();
	}

	@Test
	public void testGetWorkspaceGitRepository() throws Exception {
		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		String gitDirectoryName = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"branch", "master"
				).put(
					"name", gitDirectoryName
				).put(
					"repository", gitDirectoryName
				)
			));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		CountDownLatch releaseCountDownLatch = new CountDownLatch(1);
		CountDownLatch startCountDownLatch = new CountDownLatch(1);
		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doAnswer(
			invocation -> {
				startCountDownLatch.countDown();

				releaseCountDownLatch.await(10, TimeUnit.SECONDS);

				return gitDirectoryName;
			}
		).when(
			workspaceGitRepository
		).getDirectoryName();

		Map<String, WorkspaceGitRepository> workspaceGitRepositories =
			ReflectionTestUtil.getFieldValue(
				GitRepositoryFactory.class, "_workspaceGitRepositories");

		workspaceGitRepositories.put(gitDirectoryName, workspaceGitRepository);

		BaseWorkspace baseWorkspace = Mockito.mock(
			BaseWorkspace.class, Mockito.CALLS_REAL_METHODS);

		ReflectionTestUtil.setFieldValue(
			baseWorkspace, "jsonObject",
			new JSONObject(
			).put(
				"workspace_repository_dir_names", gitDirectoryName
			));

		Future<List<WorkspaceGitRepository>> loadFuture =
			_executorService.submit(baseWorkspace::getWorkspaceGitRepositories);

		Assert.assertTrue(startCountDownLatch.await(10, TimeUnit.SECONDS));

		Future<WorkspaceGitRepository> lookupFuture = _executorService.submit(
			() -> baseWorkspace.getWorkspaceGitRepository(gitDirectoryName));

		try {
			Assert.fail(
				"Returned " + lookupFuture.get(1, TimeUnit.SECONDS) +
					" before the workspace Git repositories were loaded");
		}
		catch (TimeoutException timeoutException) {
		}

		releaseCountDownLatch.countDown();

		testSame(
			workspaceGitRepository, lookupFuture.get(10, TimeUnit.SECONDS));
		testEquals(
			Arrays.asList(workspaceGitRepository),
			loadFuture.get(10, TimeUnit.SECONDS));
	}

	private final ExecutorService _executorService =
		Executors.newFixedThreadPool(2);

}