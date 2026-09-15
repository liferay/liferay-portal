/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileMethodTest extends BaseTestClassFileTestCase {

	@Test
	public void testGetFullName() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"describe('a', () => {describe('b', () => {" +
				"it('c > d', () => {});});});");

		testEquals("a > b > c > d", testClassFileMethod.getFullName());
		testEquals("a b c > d", testClassFileMethod.getFullName(" "));
		testEquals("a|b|c > d", testClassFileMethod.getFullName("|"));
	}

	@Test
	public void testGetFullNameNoDescribes() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"it('a', () => {});");

		testEquals("a", testClassFileMethod.getFullName(" "));
		testEquals("a", testClassFileMethod.getFullName());
	}

	@Test
	public void testGetTestClassFile() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = testPackage.getTestClassFile(
			"src/a.spec.ts");

		List<TestClassFileMethod> testClassFileMethods =
			testClassFile.getTestClassFileMethods();

		TestClassFileMethod testClassFileMethod = testClassFileMethods.get(0);

		testSame(testClassFile, testClassFileMethod.getTestClassFile());
	}

	@Test
	public void testMatches() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"describe('a', () => {it('b', () => {});});");

		Assert.assertFalse(testClassFileMethod.matches("a > b > c"));
		Assert.assertFalse(testClassFileMethod.matches("a"));
		Assert.assertFalse(testClassFileMethod.matches("a-b"));
		Assert.assertFalse(
			testClassFileMethod.matches(RandomTestUtil.randomString()));
		Assert.assertFalse(testClassFileMethod.matches(null));
		Assert.assertTrue(testClassFileMethod.matches("a > b"));
		Assert.assertTrue(testClassFileMethod.matches("a b"));
	}

	@Test
	public void testMatchesPatternDynamic() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"describe('a', () => {it(`b ${c} d`, () => {});});");

		Assert.assertFalse(testClassFileMethod.matchesPattern("a > b d e"));
		Assert.assertFalse(testClassFileMethod.matchesPattern("a > b"));
		Assert.assertFalse(testClassFileMethod.matchesPattern(null));
		Assert.assertTrue(
			testClassFileMethod.matchesPattern("a > b anything d"));
		Assert.assertTrue(testClassFileMethod.matchesPattern("a > b  d"));
		Assert.assertTrue(testClassFileMethod.matchesPattern("a b anything d"));
	}

	@Test
	public void testMatchesPatternEach() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"it.each([a])('b %p c', () => {});");

		Assert.assertFalse(testClassFileMethod.matchesPattern("b c"));
		Assert.assertTrue(testClassFileMethod.matchesPattern("b {} c"));
	}

	@Test
	public void testMatchesPatternStatic() throws Exception {
		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			"describe('a', () => {it('b', () => {});});");

		Assert.assertFalse(testClassFileMethod.matchesPattern("a > b > c"));
		Assert.assertFalse(
			testClassFileMethod.matchesPattern(RandomTestUtil.randomString()));
		Assert.assertTrue(testClassFileMethod.matchesPattern("a > b"));
		Assert.assertTrue(testClassFileMethod.matchesPattern("a b"));
	}

	@Test
	public void testToString() throws Exception {
		testEquals(
			"a > b",
			String.valueOf(
				_getTestClassFileMethod(
					"describe('a', () => {it('b', () => {});});")));
	}

	private TestClassFileMethod _getTestClassFileMethod(String content)
		throws Exception {

		File projectDir = createProjectDir("vitest run");

		write(content, projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = testPackage.getTestClassFile(
			"src/a.spec.ts");

		List<TestClassFileMethod> testClassFileMethods =
			testClassFile.getTestClassFileMethods();

		return testClassFileMethods.get(0);
	}

}