/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestrayServerTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testDeleteCaseResultFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		File file = new File(temporaryFolder.getRoot(), fileName);

		JenkinsResultsParserUtil.write(file, RandomTestUtil.randomString());

		TestrayServer testrayServer = _mockTestrayServer();

		testrayServer.deleteCaseResultFile("");
		testrayServer.deleteCaseResultFile(null);

		Assert.assertTrue(file.exists());

		testrayServer.deleteCaseResultFile(fileName);

		Assert.assertFalse(file.exists());
	}

	@Test
	public void testIsCaseResultFileAvailable() throws Exception {
		TestrayServer testrayServer = _mockTestrayServer();

		String fileName = RandomTestUtil.randomString();

		Assert.assertFalse(testrayServer.isCaseResultFileAvailable(fileName));

		JenkinsResultsParserUtil.write(
			new File(temporaryFolder.getRoot(), fileName),
			RandomTestUtil.randomString());

		Assert.assertTrue(testrayServer.isCaseResultFileAvailable(fileName));

		Assert.assertFalse(testrayServer.isCaseResultFileAvailable(""));
		Assert.assertFalse(testrayServer.isCaseResultFileAvailable(null));
	}

	@Test
	public void testReadCaseResultFile() throws Exception {
		String fileContent = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();

		JenkinsResultsParserUtil.write(
			new File(temporaryFolder.getRoot(), fileName), fileContent);

		TestrayServer testrayServer = _mockTestrayServer();

		testEquals(fileContent, testrayServer.readCaseResultFile(fileName));
	}

	@Test
	public void testWriteCaseResult() throws Exception {
		TestrayServer testrayServer = _mockTestrayServer();

		String fileName = RandomTestUtil.randomString();

		testrayServer.writeCaseResult(fileName, "");
		testrayServer.writeCaseResult(fileName, null);

		testrayServer.writeCaseResult(null, RandomTestUtil.randomString());

		File resultsDir = temporaryFolder.getRoot();

		String[] resultsDirFileNames = resultsDir.list();

		testEquals(0, resultsDirFileNames.length);

		testrayServer.writeCaseResult(fileName, RandomTestUtil.randomString());

		String fileContent = RandomTestUtil.randomString();

		testrayServer.writeCaseResult(fileName, fileContent);

		resultsDirFileNames = resultsDir.list();

		testEquals(1, resultsDirFileNames.length);
		testEquals(fileName, resultsDirFileNames[0]);

		testEquals(fileContent, read(resultsDir, fileName));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private TestrayServer _mockTestrayServer() throws Exception {
		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		Mockito.doCallRealMethod(
		).when(
			testrayServer
		).deleteCaseResultFile(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			testrayServer
		).isCaseResultFileAvailable(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			testrayServer
		).readCaseResultFile(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			testrayServer
		).writeCaseResult(
			Mockito.any(), Mockito.any()
		);

		Mockito.doReturn(
			temporaryFolder.getRoot()
		).when(
			testrayServer
		).getResultsDir();

		return testrayServer;
	}

}