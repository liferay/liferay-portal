/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestPackageFactoryTest extends BaseTestClassFileTestCase {

	@Test
	public void testGetTestClassFile() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, "src/tests/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = testPackage.getTestClassFile(
			"src/tests/a.spec.ts");

		Assert.assertNotNull(testClassFile);

		testEquals("a.spec.ts", testClassFile.getName());

		Assert.assertNull(testPackage.getTestClassFile("src/tests/b.spec.ts"));
	}

	@Test
	public void testGetTestClassFiles() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("export default 1;", projectDir, "src/c.ts");
		write("it('a', () => {});", projectDir, "src/a.spec.ts");
		write("it('b', () => {});", projectDir, "src/nested/b.spec.tsx");
		write("it('d', () => {});", projectDir, "node_modules/e/d.spec.ts");
		write("it('f', () => {});", projectDir, "build/f.spec.ts");

		List<String> names = new ArrayList<>();

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		for (TestClassFile testClassFile : testPackage.getTestClassFiles()) {
			names.add(testClassFile.getName());
		}

		Collections.sort(names);

		testEquals(Arrays.asList("a.spec.ts", "b.spec.tsx", "c.ts"), names);
	}

	@Test
	public void testGetTestClassFilesParentDirPath() throws Exception {
		File projectDir = createProjectDir("node-scripts test");

		write("it('a', () => {});", projectDir, "test/js/a.test.js");
		write("it('b', () => {});", projectDir, "test/js/b.test.js");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		testEquals(0, _size(testPackage.getTestClassFiles("test/css")));
		testEquals(0, _size(testPackage.getTestClassFiles(null)));
		testEquals(2, _size(testPackage.getTestClassFiles("test/js")));

		testEquals(
			2,
			_size(
				testPackage.getTestClassFiles(
					projectDir.getName() + ".test.js")));
	}

	@Test
	public void testNewTestPackage() throws Exception {
		_testNewTestPackage(
			JestTestPackage.class, "USE_REACT_16=true node-scripts test");
		_testNewTestPackage(JestTestPackage.class, "jest");
		_testNewTestPackage(JestTestPackage.class, "node-scripts test");
		_testNewTestPackage(VitestTestPackage.class, "vitest run");
	}

	@Test
	public void testNewTestPackageMissing() throws Exception {
		File projectDir = createDir();

		Assert.assertNull(
			TestPackageFactory.newTestPackage(new File(projectDir, "a")));
		Assert.assertNull(TestPackageFactory.newTestPackage(projectDir));

		Assert.assertNull(TestPackageFactory.newTestPackage(null));
	}

	@Test
	public void testNewTestPackageUnsupported() throws Exception {
		_testNewTestPackage(null, "ava");
		_testNewTestPackage(null, "node --test");
		_testNewTestPackage(null, "playwright test");
	}

	private int _size(List<TestClassFile> testClassFiles) {
		if (testClassFiles == null) {
			return 0;
		}

		return testClassFiles.size();
	}

	private void _testNewTestPackage(Class<?> clazz, String testScript)
		throws Exception {

		File projectDir = createProjectDir(testScript);

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		if (clazz == null) {
			Assert.assertNull(testPackage);

			return;
		}

		Assert.assertTrue(clazz.isInstance(testPackage));

		testEquals(testScript, testPackage.getTestScript());
	}

}