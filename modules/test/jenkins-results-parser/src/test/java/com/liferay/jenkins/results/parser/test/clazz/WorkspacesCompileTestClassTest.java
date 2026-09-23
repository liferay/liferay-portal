/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesCompileBatchTestClassGroup;

import java.io.File;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesCompileTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetName() {
		String workspaceName = RandomTestUtil.randomString();

		_testGetName(
			new File(
				"/" + RandomTestUtil.randomString(),
				"workspaces/" + workspaceName),
			workspaceName);
		_testGetName(
			new File(
				JenkinsResultsParserUtil.getGitWorkingDir(new File(".")),
				"workspaces/" + workspaceName),
			workspaceName);
	}

	private void _testGetName(File workspaceDir, String workspaceName) {
		WorkspacesCompileBatchTestClassGroup
			workspacesCompileBatchTestClassGroup = Mockito.mock(
				WorkspacesCompileBatchTestClassGroup.class);

		TestClass defaultTestClass = TestClassFactory.newTestClass(
			workspacesCompileBatchTestClassGroup, workspaceDir);

		testEquals("workspaces/" + workspaceName, defaultTestClass.getName());

		TestClass jsonObjectTestClass = TestClassFactory.newTestClass(
			workspacesCompileBatchTestClassGroup,
			new JSONObject(String.valueOf(defaultTestClass.getJSONObject())));

		testEquals(
			"workspaces/" + workspaceName, jsonObjectTestClass.getName());
	}

}