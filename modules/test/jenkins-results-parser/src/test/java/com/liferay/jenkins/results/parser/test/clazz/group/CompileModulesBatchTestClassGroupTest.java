/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Calum Ragan
 */
public class CompileModulesBatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");
	}

	@Test
	public void testSetAxisTestClassGroups() throws Exception {
		Properties jobProperties = new Properties();

		jobProperties.setProperty("test.batch.axis.count", "2");

		CompileModulesBatchTestClassGroup compileModulesBatchTestClassGroup =
			BatchTestClassGroupTestUtil.newCompileModulesBatchTestClassGroup(
				jobProperties, _newModuleDir("aaa-module", 2),
				_newModuleDir("aab-module", 2), _newModuleDir("zzy-module", 3),
				_newModuleDir("zzz-module", 3));

		List<AxisTestClassGroup> axisTestClassGroups =
			compileModulesBatchTestClassGroup.getAxisTestClassGroups();

		testEquals(2, axisTestClassGroups.size());

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			testEquals(5L, _getWeight(axisTestClassGroup));
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private long _getWeight(AxisTestClassGroup axisTestClassGroup) {
		long weight = 0;

		for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
			weight += testClass.getWeight();
		}

		return weight;
	}

	private File _newModuleDir(String moduleDirName, int modulesProjectDirCount)
		throws Exception {

		File moduleDir = temporaryFolder.newFolder(moduleDirName);

		for (int i = 0; i < modulesProjectDirCount; i++) {
			File modulesProjectDir = new File(
				moduleDir, RandomTestUtil.randomString());

			modulesProjectDir.mkdirs();

			File bndBndFile = new File(modulesProjectDir, "bnd.bnd");

			bndBndFile.createNewFile();

			File buildGradleFile = new File(modulesProjectDir, "build.gradle");

			buildGradleFile.createNewFile();
		}

		return moduleDir;
	}

}