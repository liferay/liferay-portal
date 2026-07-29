/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.suite;

import com.liferay.jenkins.results.parser.GitWorkingDirectory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class AntAllRelevantRuleTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("com.liferay.jenkins.results.parser.GitWorkingDirectory");
	}

	@After
	public void tearDown() {
		RelevantRuleEngine.clear();
	}

	@Test
	public void testMatches() throws Exception {
		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class)) {

			GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
				GitWorkingDirectory.class);

			File workingDirectory = new File(".");

			Mockito.doReturn(
				workingDirectory
			).when(
				gitWorkingDirectory
			).getWorkingDirectory();

			new RelevantRuleEngine(gitWorkingDirectory, "test-rule");

			AntAllRelevantRule antAllRelevantRule = Mockito.spy(
				new AntAllRelevantRule(
					null, gitWorkingDirectory, Mockito.mock(Job.class),
					"test-rule", new Properties()));

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.isFileIncluded(
					Mockito.any(), Mockito.any(), Mockito.any(File.class))
			).thenReturn(
				true
			);

			List<File> modifiedModuleProjectDirsList = new ArrayList<>();

			Mockito.doReturn(
				modifiedModuleProjectDirsList
			).when(
				antAllRelevantRule
			).getModifiedModuleProjectDirsList();

			File modulesDir = new File(workingDirectory, "modules");

			File modifiedFile = new File(modulesDir, "test.txt");

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.isFileInDirectory(
					Mockito.eq(modulesDir), Mockito.eq(modifiedFile))
			).thenReturn(
				true
			);

			Assert.assertFalse(antAllRelevantRule.matches(modifiedFile));

			for (int i = 0; i < 6; i++) {
				modifiedModuleProjectDirsList.add(
					new File("modules/test-" + i));
			}

			Assert.assertTrue(antAllRelevantRule.matches(modifiedFile));

			File nonModulesFile = new File("portal-impl/test.txt");

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.isFileInDirectory(
					Mockito.eq(modulesDir), Mockito.eq(nonModulesFile))
			).thenReturn(
				false
			);

			Assert.assertTrue(antAllRelevantRule.matches(nonModulesFile));
		}
	}

}