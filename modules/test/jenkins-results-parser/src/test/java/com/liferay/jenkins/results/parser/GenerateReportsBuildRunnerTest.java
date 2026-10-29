/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class GenerateReportsBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testUpdateReport() throws Exception {
		File reportDir = temporaryFolder.newFolder();

		File dataFile = new File(reportDir, RandomTestUtil.randomString());

		String dataFileContent = RandomTestUtil.randomString();

		JenkinsResultsParserUtil.write(dataFile, dataFileContent);

		File reportFile = new File(reportDir, "index.html");

		JenkinsResultsParserUtil.write(
			reportFile, "<script src=\"" + dataFile.getName() + "\"></script>");

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getBuildProperties
			).thenReturn(
				new Properties()
			);

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.rsync(
					Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())
			).thenAnswer(
				invocation -> null
			);

			ReflectionTestUtil.invoke(
				Mockito.mock(GenerateReportsBuildRunner.class), "_updateReport",
				new Class<?>[] {String.class}, reportDir.getPath());
		}

		testEquals(true, dataFile.exists());
		testEquals(
			"<script>" + dataFileContent + "</script>", read(reportFile));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

}