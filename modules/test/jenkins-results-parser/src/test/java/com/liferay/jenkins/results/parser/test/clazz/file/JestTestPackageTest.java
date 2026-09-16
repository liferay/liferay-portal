/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class JestTestPackageTest extends BaseTestClassFileTestCase {

	@Test
	public void testGetTestClassFiles() throws Exception {
		File projectDir = _createProjectDir(
			"node-scripts test", "<rootDir>/" + _TEST_DIR_PATH + "/b.js");

		write("it('a', () => {});", projectDir, _TEST_DIR_PATH + "/a.js");
		write("it('b', () => {});", projectDir, _TEST_DIR_PATH + "/b.js");

		List<String> fileNames = new ArrayList<>();

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		for (TestClassFile testClassFile : testPackage.getTestClassFiles()) {
			File file = testClassFile.getFile();

			fileNames.add(file.getName());
		}

		testEquals(Arrays.asList("a.js"), fileNames);
	}

	@Test
	public void testIsTestClassFileIgnored() throws Exception {
		File projectDir = _createProjectDir(
			"node-scripts test", "<rootDir>/" + _TEST_DIR_PATH + "/b.js");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		Assert.assertFalse(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/a.js")));
		Assert.assertTrue(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/b.js")));
	}

	@Test
	public void testIsTestClassFileIgnoredNoPatterns() throws Exception {
		File projectDir = createProjectDir("node-scripts test");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		Assert.assertFalse(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/a.js")));
	}

	@Test
	public void testIsTestClassFileIgnoredRegex() throws Exception {
		File projectDir = _createProjectDir(
			"node-scripts test", "<rootDir>/" + _TEST_DIR_PATH + "/[^/]+\\.js");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		Assert.assertFalse(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/components/a.js")));
		Assert.assertTrue(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/a.js")));
	}

	@Test
	public void testIsTestClassFileIgnoredVitest() throws Exception {
		File projectDir = _createProjectDir(
			"vitest run", "<rootDir>/" + _TEST_DIR_PATH + "/b.js");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		Assert.assertFalse(
			testPackage.isTestClassFileIgnored(
				new File(projectDir, _TEST_DIR_PATH + "/b.js")));
	}

	private File _createProjectDir(
			String testScript, String... testPathIgnorePatterns)
		throws Exception {

		File projectDir = createProjectDir(testScript);

		JSONObject jestJSONObject = new JSONObject();

		jestJSONObject.put(
			"testPathIgnorePatterns", new JSONArray(testPathIgnorePatterns));

		JSONObject scriptsJSONObject = new JSONObject();

		scriptsJSONObject.put("test", testScript);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"jest", jestJSONObject
		).put(
			"scripts", scriptsJSONObject
		);

		write(jsonObject.toString(), projectDir, "package.json");

		return projectDir;
	}

	private static final String _TEST_DIR_PATH = "test/js";

}