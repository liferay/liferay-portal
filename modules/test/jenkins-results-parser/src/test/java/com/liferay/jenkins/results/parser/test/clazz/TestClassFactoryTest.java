/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroupTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitBatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesJUnitBatchTestClassGroup;

import java.io.File;

import java.util.Map;

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
		_testNewTestClass(false);
		_testNewTestClass(true);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private BatchTestClassGroup _mockBatchTestClassGroup(
		String batchName, Class<? extends BatchTestClassGroup> clazz) {

		BatchTestClassGroup batchTestClassGroup = Mockito.mock(clazz);

		PortalTestClassJob portalTestClassJob =
			BatchTestClassGroupTestUtil.getPortalTestClassJob();

		Mockito.doReturn(
			batchName
		).when(
			batchTestClassGroup
		).getBatchName();

		Mockito.doReturn(
			portalTestClassJob.getPortalGitWorkingDirectory()
		).when(
			batchTestClassGroup
		).getPortalGitWorkingDirectory();

		return batchTestClassGroup;
	}

	private File _newTestClassFile() throws Exception {
		String randomString = RandomTestUtil.randomString();

		String classNameSuffix = randomString.replaceAll("-", "");

		File packageDir = temporaryFolder.newFolder(
			"com", "liferay", randomString.substring(0, 8));

		return BatchTestClassGroupTestUtil.newTestClassFile(
			"Sample" + classNameSuffix, packageDir);
	}

	private void _resetCaches() {
		Map<File, ?> jUnitTestClasses = ReflectionTestUtil.getFieldValue(
			TestClassFactory.class, "_jUnitTestClasses");

		jUnitTestClasses.clear();

		Map<File, ?> modulesJUnitTestClasses = ReflectionTestUtil.getFieldValue(
			TestClassFactory.class, "_modulesJUnitTestClasses");

		modulesJUnitTestClasses.clear();
	}

	private void _testNewTestClass(boolean modulesFirst) throws Exception {
		_resetCaches();

		BatchTestClassGroup jUnitBatchTestClassGroup = _mockBatchTestClassGroup(
			"integration-license", JUnitBatchTestClassGroup.class);

		BatchTestClassGroup modulesJUnitBatchTestClassGroup =
			_mockBatchTestClassGroup(
				"modules-integration", ModulesJUnitBatchTestClassGroup.class);

		File testClassFile = _newTestClassFile();

		TestClass jUnitTestClass = null;
		TestClass modulesJUnitTestClass = null;

		if (modulesFirst) {
			modulesJUnitTestClass = TestClassFactory.newTestClass(
				modulesJUnitBatchTestClassGroup, testClassFile);
			jUnitTestClass = TestClassFactory.newTestClass(
				jUnitBatchTestClassGroup, testClassFile);
		}
		else {
			jUnitTestClass = TestClassFactory.newTestClass(
				jUnitBatchTestClassGroup, testClassFile);
			modulesJUnitTestClass = TestClassFactory.newTestClass(
				modulesJUnitBatchTestClassGroup, testClassFile);
		}

		testEquals(JUnitTestClass.class, jUnitTestClass.getClass());
		testEquals(
			ModulesJUnitTestClass.class, modulesJUnitTestClass.getClass());
	}

}