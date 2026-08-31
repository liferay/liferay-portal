/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.Job;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class ModulesRelevantRuleTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("com.liferay.jenkins.results.parser.GitWorkingDirectory");
	}

	@Test
	public void testGetTestScriptCommandsWithNonTestTask() throws Exception {
		ModulesRelevantRule modulesRelevantRule = Mockito.spy(
			new ModulesRelevantRule(
				null, Mockito.mock(GitWorkingDirectory.class),
				Mockito.mock(Job.class), "test-rule", new Properties()));

		List<File> modifiedModuleProjectDirsList = new ArrayList<>();

		modifiedModuleProjectDirsList.add(new File("modules/apps/test-app"));

		Mockito.doReturn(
			"modules/apps/test-app"
		).when(
			modulesRelevantRule
		).getGradlePackageName(
			Mockito.any(File.class)
		);

		Mockito.doReturn(
			modifiedModuleProjectDirsList
		).when(
			modulesRelevantRule
		).getModifiedModuleProjectDirsList();

		Mockito.doReturn(
			"integrationTest"
		).when(
			modulesRelevantRule
		).getTestScriptModuleGradleTaskName();

		List<RelevantRule.TestScriptCommand> testScriptCommands =
			modulesRelevantRule.getTestScriptCommands();

		Assert.assertEquals(
			testScriptCommands.toString(), 1, testScriptCommands.size());

		RelevantRule.TestScriptCommand testScriptCommand =
			testScriptCommands.get(0);

		String command = testScriptCommand.getCommand();

		Assert.assertTrue(
			command.contains(
				"../gradlew modules/apps/test-app:integrationTest"));

		Assert.assertFalse(
			command.contains(" --continue -Dtest.ignore.failures=false"));

		Assert.assertTrue(command.contains(" --parallel"));
	}

	@Test
	public void testGetTestScriptCommandsWithTestTask() throws Exception {
		ModulesRelevantRule modulesRelevantRule = Mockito.spy(
			new ModulesRelevantRule(
				null, Mockito.mock(GitWorkingDirectory.class),
				Mockito.mock(Job.class), "test-rule", new Properties()));

		List<File> modifiedModuleProjectDirsList = new ArrayList<>();

		modifiedModuleProjectDirsList.add(new File("modules/apps/test-app"));

		Mockito.doReturn(
			"modules/apps/test-app"
		).when(
			modulesRelevantRule
		).getGradlePackageName(
			Mockito.any(File.class)
		);

		Mockito.doReturn(
			modifiedModuleProjectDirsList
		).when(
			modulesRelevantRule
		).getModifiedModuleProjectDirsList();

		Mockito.doReturn(
			"test"
		).when(
			modulesRelevantRule
		).getTestScriptModuleGradleTaskName();

		List<RelevantRule.TestScriptCommand> testScriptCommands =
			modulesRelevantRule.getTestScriptCommands();

		Assert.assertEquals(
			testScriptCommands.toString(), 1, testScriptCommands.size());

		RelevantRule.TestScriptCommand testScriptCommand =
			testScriptCommands.get(0);

		String command = testScriptCommand.getCommand();

		Assert.assertTrue(
			command.contains("../gradlew modules/apps/test-app:test"));

		Assert.assertTrue(
			command.contains(" --continue -Dtest.ignore.failures=false"));

		Assert.assertTrue(command.contains(" --parallel"));
	}

}