/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileFactoryTest extends BaseTestClassFileTestCase {

	@Test
	public void testNewTestClassFileCached() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, "src/a.spec.ts");

		File file = new File(projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = TestClassFileFactory.newTestClassFile(
			file, testPackage);

		Assert.assertNotNull(testClassFile);

		testSame(
			testClassFile,
			TestClassFileFactory.newTestClassFile(file, testPackage));

		testSame(
			testClassFile,
			TestClassFileFactory.newTestClassFile(
				new File(projectDir, "src/./a.spec.ts"), testPackage));
	}

	@Test
	public void testNewTestClassFileExtensions() throws Exception {
		_testNewTestClassFile(false, "a");
		_testNewTestClassFile(false, "a.java");
		_testNewTestClassFile(false, "a.json");
		_testNewTestClassFile(false, "a.txt");
		_testNewTestClassFile(true, "a.js");
		_testNewTestClassFile(true, "a.jsx");
		_testNewTestClassFile(true, "a.spec.ts");
		_testNewTestClassFile(true, "a.ts");
		_testNewTestClassFile(true, "a.tsx");
	}

	@Test
	public void testNewTestClassFileNull() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		Assert.assertNull(
			TestClassFileFactory.newTestClassFile(
				new File(projectDir, "src"), testPackage));

		Assert.assertNull(
			TestClassFileFactory.newTestClassFile(
				new File(projectDir, "src/a.spec.ts"), null));

		Assert.assertNull(
			TestClassFileFactory.newTestClassFile(
				new File(projectDir, "src/b.spec.ts"), testPackage));

		Assert.assertNull(
			TestClassFileFactory.newTestClassFile(null, testPackage));
	}

	private void _testNewTestClassFile(
			boolean expectedTestClassFile, String name)
		throws Exception {

		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, name);

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = TestClassFileFactory.newTestClassFile(
			new File(projectDir, name), testPackage);

		testEquals(expectedTestClassFile, testClassFile != null);
	}

}