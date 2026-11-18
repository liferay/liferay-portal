/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import org.json.JSONObject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class PlaywrightJUnitTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			temporaryFolder.getRoot()
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			_batchTestClassGroup
		).getPortalGitWorkingDirectory();
	}

	@Test
	public void testGetJSONObject() throws Exception {
		String workspaceName = RandomTestUtil.randomString();

		_testGetJSONObject(workspaceName, "workspace.name=" + workspaceName);

		_testGetJSONObject(null, RandomTestUtil.randomString() + "=");
	}

	@Test
	public void testGetWorkspaceName() throws Exception {
		String workspaceName = RandomTestUtil.randomString();

		_testGetWorkspaceName(
			null, RandomTestUtil.randomString() + "=",
			"workspace.name=" + workspaceName);

		_testGetWorkspaceName(null, null, RandomTestUtil.randomString() + "=");
		_testGetWorkspaceName(null, null, null);
		_testGetWorkspaceName(
			workspaceName, null, "workspace.name=" + workspaceName);
	}

	@Test
	public void testIsAnalyticsCloudEnabled() throws Exception {
		_testIsAnalyticsCloudEnabled(
			false, RandomTestUtil.randomString() + "=",
			RandomTestUtil.randomString() + "\n\n# start_analytics_cloud");
		_testIsAnalyticsCloudEnabled(
			false, RandomTestUtil.randomString() + "=",
			RandomTestUtil.randomString());
		_testIsAnalyticsCloudEnabled(
			false, RandomTestUtil.randomString() + "=", null);
		_testIsAnalyticsCloudEnabled(
			true, "analytics.cloud.enabled=true", null);
		_testIsAnalyticsCloudEnabled(
			true, RandomTestUtil.randomString() + "=",
			RandomTestUtil.randomString() + "\n\nstart_analytics_cloud");
		_testIsAnalyticsCloudEnabled(
			true, null,
			RandomTestUtil.randomString() + "\n\nstart_analytics_cloud");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private PlaywrightJUnitTestClass _newPlaywrightJUnitTestClass(
			String nestedTestPropertiesContent,
			String projectTestPropertiesContent, String setUpContent)
		throws Exception {

		File playwrightDir = new File(
			temporaryFolder.getRoot(), "modules/test/playwright");

		File projectDir = new File(
			playwrightDir, "tests/" + RandomTestUtil.randomString() + "/main");

		File specDir = new File(projectDir, RandomTestUtil.randomString());

		specDir.mkdirs();

		JenkinsResultsParserUtil.write(
			new File(playwrightDir, "test.properties"),
			RandomTestUtil.randomString() + "=");
		JenkinsResultsParserUtil.write(
			new File(projectDir, "config.ts"), RandomTestUtil.randomString());

		if (nestedTestPropertiesContent != null) {
			JenkinsResultsParserUtil.write(
				new File(specDir, "test.properties"),
				nestedTestPropertiesContent);
		}

		if (projectTestPropertiesContent != null) {
			JenkinsResultsParserUtil.write(
				new File(projectDir, "test.properties"),
				projectTestPropertiesContent);
		}

		if (setUpContent != null) {
			JenkinsResultsParserUtil.write(
				new File(projectDir, "env/set_up.sh"), setUpContent);
		}

		File specFile = new File(
			specDir, RandomTestUtil.randomString() + ".spec.ts");

		return new PlaywrightJUnitTestClass(_batchTestClassGroup, specFile);
	}

	private void _testGetJSONObject(
			String expectedWorkspaceName, String projectTestPropertiesContent)
		throws Exception {

		PlaywrightJUnitTestClass playwrightJUnitTestClass =
			_newPlaywrightJUnitTestClass(
				null, projectTestPropertiesContent, null);

		JSONObject jsonObject = playwrightJUnitTestClass.getJSONObject();

		PlaywrightJUnitTestClass rebuiltPlaywrightJUnitTestClass =
			new PlaywrightJUnitTestClass(
				_batchTestClassGroup, new JSONObject(jsonObject.toString()));

		testEquals(
			expectedWorkspaceName,
			rebuiltPlaywrightJUnitTestClass.getWorkspaceName());
	}

	private void _testGetWorkspaceName(
			String expectedWorkspaceName, String nestedTestPropertiesContent,
			String projectTestPropertiesContent)
		throws Exception {

		PlaywrightJUnitTestClass playwrightJUnitTestClass =
			_newPlaywrightJUnitTestClass(
				nestedTestPropertiesContent, projectTestPropertiesContent,
				null);

		testEquals(
			expectedWorkspaceName, playwrightJUnitTestClass.getWorkspaceName());
	}

	private void _testIsAnalyticsCloudEnabled(
			boolean expectedAnalyticsCloudEnabled,
			String projectTestPropertiesContent, String setUpContent)
		throws Exception {

		PlaywrightJUnitTestClass playwrightJUnitTestClass =
			_newPlaywrightJUnitTestClass(
				null, projectTestPropertiesContent, setUpContent);

		testEquals(
			expectedAnalyticsCloudEnabled,
			playwrightJUnitTestClass.isAnalyticsCloudEnabled());
	}

	private final BatchTestClassGroup _batchTestClassGroup = Mockito.mock(
		BatchTestClassGroup.class);

}