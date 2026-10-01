/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;

import org.json.JSONArray;

import org.junit.After;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspaceFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			null);

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		workspaces.clear();
	}

	@Test
	public void testNewWorkspaceCached() {
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		BuildDatabaseUtil.setBuildDatabase(buildDatabase);

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray());
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		Map<String, Workspace> workspaces = ReflectionTestUtil.getFieldValue(
			WorkspaceFactory.class, "_workspaces");

		String repositoryName = RandomTestUtil.randomString();
		Workspace workspace = Mockito.mock(Workspace.class);

		workspaces.put(repositoryName, workspace);

		testSame(
			workspace,
			WorkspaceFactory.newWorkspace(
				repositoryName, RandomTestUtil.randomString()));

		Mockito.verify(
			buildDatabase, Mockito.never()
		).putWorkspace(
			Mockito.anyString(), Mockito.any()
		);
	}

}