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
public class CompileModulesRelevantRuleTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("com.liferay.jenkins.results.parser.GitWorkingDirectory");
	}

	@Test
	public void testGetTestScriptCommands() throws Exception {
		CompileModulesRelevantRule compileModulesRelevantRule = Mockito.spy(
			new CompileModulesRelevantRule(
				null, Mockito.mock(GitWorkingDirectory.class),
				Mockito.mock(Job.class), "test-rule", new Properties()));

		List<File> modifiedModuleProjectDirsList = new ArrayList<>();

		modifiedModuleProjectDirsList.add(new File("modules/apps/test-app"));

		Mockito.doReturn(
			"modules/apps/test-app"
		).when(
			compileModulesRelevantRule
		).getGradlePackageName(
			Mockito.any(File.class)
		);

		Mockito.doReturn(
			modifiedModuleProjectDirsList
		).when(
			compileModulesRelevantRule
		).getModifiedModuleProjectDirsList();

		Mockito.doReturn(
			"compileIntegrationTest"
		).when(
			compileModulesRelevantRule
		).getTestScriptModuleGradleTaskName();

		List<RelevantRule.TestScriptCommand> testScriptCommands =
			compileModulesRelevantRule.getTestScriptCommands();

		Assert.assertEquals(
			testScriptCommands.toString(), 2, testScriptCommands.size());

		RelevantRule.TestScriptCommand testScriptCommand =
			testScriptCommands.get(0);

		Assert.assertEquals(".", testScriptCommand.getCommandDirPath());

		Assert.assertEquals(
			"ant compile install-portal-snapshots",
			testScriptCommand.getCommand());

		testScriptCommand = testScriptCommands.get(1);

		Assert.assertEquals("modules", testScriptCommand.getCommandDirPath());

		Assert.assertEquals(
			"../gradlew modules/apps/test-app:compileIntegrationTest " +
				"--parallel",
			testScriptCommand.getCommand());
	}

}