/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroupTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesJUnitBatchTestClassGroup;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class TestClassFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	public void setUpGitRemotes() throws Exception {
		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");
	}

	@Test
	public void testNewTestClass() throws Exception {
		BatchTestClassGroup jUnitBatchTestClassGroup = _mockBatchTestClassGroup(
			"integration-license", JUnitBatchTestClassGroup.class);

		BatchTestClassGroup modulesJUnitBatchTestClassGroup =
			_mockBatchTestClassGroup(
				"modules-integration", ModulesJUnitBatchTestClassGroup.class);

		_testNewTestClass(
			jUnitBatchTestClassGroup, modulesJUnitBatchTestClassGroup);
		_testNewTestClass(
			modulesJUnitBatchTestClassGroup, jUnitBatchTestClassGroup);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private BatchTestClassGroup _mockBatchTestClassGroup(
		String batchName, Class<? extends BatchTestClassGroup> clazz) {

		BatchTestClassGroup batchTestClassGroup = Mockito.mock(clazz);

		Mockito.doReturn(
			batchName
		).when(
			batchTestClassGroup
		).getBatchName();

		PortalTestClassJob portalTestClassJob =
			BatchTestClassGroupTestUtil.getPortalTestClassJob();

		Mockito.doReturn(
			portalTestClassJob.getPortalGitWorkingDirectory()
		).when(
			batchTestClassGroup
		).getPortalGitWorkingDirectory();

		return batchTestClassGroup;
	}

	private File _newTestClassFile() throws Exception {
		File packageDir = new File(temporaryFolder.newFolder(), "com/liferay");

		packageDir.mkdirs();

		return BatchTestClassGroupTestUtil.newTestClassFile(
			"SampleTest", packageDir);
	}

	private void _testNewTestClass(
			BatchTestClassGroup batchTestClassGroup1,
			BatchTestClassGroup batchTestClassGroup2)
		throws Exception {

		TestClassFactory.clear();

		File testClassFile = _newTestClassFile();

		TestClass testClass1 = TestClassFactory.newTestClass(
			batchTestClassGroup1, testClassFile);

		TestClass testClass2 = TestClassFactory.newTestClass(
			batchTestClassGroup2, testClassFile);

		Assert.assertNotEquals(testClass1.getClass(), testClass2.getClass());
	}

}