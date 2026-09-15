/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class JSTestClassFileTest extends BaseTestClassFileTestCase {

	@Test
	public void testGetTestClassFileMethods() throws Exception {
		_testGetTestClassFileMethods("", new String[0]);
		_testGetTestClassFileMethods(
			"describe('a', () => {describe('b', () => {it('c', () => {});});" +
				"});",
			"a > b > c");
		_testGetTestClassFileMethods(
			"describe('a', () => {it('b', () => {});it('c', () => {});});",
			"a > b", "a > c");
		_testGetTestClassFileMethods(
			"describe('a', ({b}) => {it('c', () => {});});", "a > c");
		_testGetTestClassFileMethods("it('a', () => {});", "a");
		_testGetTestClassFileMethods("it(`a`, () => {});", "a");
	}

	@Test
	public void testGetTestClassFileMethodsComments() throws Exception {
		_testGetTestClassFileMethods(
			"/* describe('a', () => {}); */ it('b', () => {});", "b");
		_testGetTestClassFileMethods(
			"// it('a', () => {});\nit('b', () => {});", "b");
		_testGetTestClassFileMethods(
			"describe('a', () => {// comment\nit('b', () => {});});", "a > b");
		_testGetTestClassFileMethods(
			"it('a /* not a comment */ b', () => {});",
			"a /* not a comment */ b");
	}

	@Test
	public void testGetTestClassFileMethodsConcatenated() throws Exception {
		_testGetTestClassFileMethods(
			"it('a ' + 'b' + \n'c', () => {});", "a bc");
		_testGetTestClassFileMethods("it('a' + \"b\", () => {});", "ab");
	}

	@Test
	public void testGetTestClassFileMethodsDeclarationWords() throws Exception {
		String[] describeWords = ReflectionTestUtil.getFieldValue(
			JSTestClassFile.class, "_DESCRIBE_WORDS");

		for (String describeWord : describeWords) {
			_testGetTestClassFileMethods(
				describeWord + "('a', () => {it('b', () => {});});", "a > b");
		}

		String[] testWords = ReflectionTestUtil.getFieldValue(
			JSTestClassFile.class, "_TEST_WORDS");

		for (String testWord : testWords) {
			_testGetTestClassFileMethods(testWord + "('a', () => {});", "a");
		}
	}

	@Test
	public void testGetTestClassFileMethodsEach() throws Exception {
		_testGetTestClassFileMethods(
			"describe.each([['a'], ['b']])('%s', (c) => {" +
				"it(`${c} d`, () => {});});",
			"a > a d", "b > b d");
		_testGetTestClassFileMethods(
			"it.concurrent.each(['a'])('b %s', () => {});", "b a");
		_testGetTestClassFileMethods(
			"it.each(['a', 'b'])('c %s', () => {});", "c a", "c b");
		_testGetTestClassFileMethods(
			"test.each(['a'])('%# %% b %s', () => {});", "0 % b a");
		_testGetTestClassFileMethods(
			"test.each([['a', 'b'], ['c', 'd']])('e %s %s', () => {});",
			"e a b", "e c d");
	}

	@Test
	public void testGetTestClassFileMethodsEachUnexpanded() throws Exception {
		_testGetTestClassFileMethods(
			"it.each(['a'])('b %p', () => {});", "b %p");
		_testGetTestClassFileMethods(
			"it.each([a.b])('c %s', () => {});", "c %s");
		_testGetTestClassFileMethods("it.each(a)('b %s', () => {});", "b %s");
	}

	@Test
	public void testGetTestClassFileMethodsEscapes() throws Exception {
		_testGetTestClassFileMethods("it('a\\'b', () => {});", "a'b");
		_testGetTestClassFileMethods("it('a\\\\b', () => {});", "a\\b");
		_testGetTestClassFileMethods("it('a\\nb', () => {});", "a\nb");
		_testGetTestClassFileMethods("it(\"a\\\"b\", () => {});", "a\"b");
	}

	@Test
	public void testGetTestClassFileMethodsExpressionTitles() throws Exception {
		_testGetTestClassFileMethods("it(() => {}, () => {});", new String[0]);
		_testGetTestClassFileMethods(
			"it(a('b, c'), () => {});", "${a('b, c')}");
		_testGetTestClassFileMethods("it(a, () => {});", "${a}");
		_testGetTestClassFileMethods("it(a.b, () => {});", "${a.b}");
		_testGetTestClassFileMethods("it(a[0], () => {});", "${a[0]}");
	}

	@Test
	public void testGetTestClassFileMethodsInterpolation() throws Exception {
		_testGetTestClassFileMethods(
			"it(`a ${b.c(d)} e`, () => {});", "a ${b.c(d)} e");
		_testGetTestClassFileMethods("it(`a ${b} c`, () => {});", "a ${b} c");
	}

	@Test
	public void testGetTestClassFileMethodsJSX() throws Exception {
		_testGetTestClassFileMethods(
			"it('a', () => {render(<b c={{d: 1}} />);render(<e>f</e>);});",
			"a");
		_testGetTestClassFileMethods(
			"it('a', () => {render(<b>{'</b>'}</b>);});", "a");
	}

	@Test
	public void testGetTestClassFileMethodsNotTests() throws Exception {
		_testGetTestClassFileMethods("a.it('b', () => {});", new String[0]);
		_testGetTestClassFileMethods("items('a', () => {});", new String[0]);
		_testGetTestClassFileMethods("testing('a', () => {});", new String[0]);
	}

	@Test
	public void testGetTestClassFileMethodsRegex() throws Exception {
		_testGetTestClassFileMethods(
			"const a = /alert\\((.*?)\\)/;const b = /'|\"/g;it('c', () => {});",
			"c");
		_testGetTestClassFileMethods(
			"const a = 1 / 2;const b = 3 / 4;it('c', () => {});", "c");
		_testGetTestClassFileMethods(
			"if (a) {return /it\\('b'\\)/;}\nit('c', () => {});", "c");
	}

	@Test
	public void testGetTestClassFileMethodsStrings() throws Exception {
		_testGetTestClassFileMethods(
			"const a = \"it('b', () => {});\";it('c', () => {});", "c");
		_testGetTestClassFileMethods(
			"const a = `it('b', () => {});`;it('c', () => {});", "c");
	}

	@Test
	public void testGetTestPackage() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write("it('a', () => {});", projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = testPackage.getTestClassFile(
			"src/a.spec.ts");

		testSame(testPackage, testClassFile.getTestPackage());

		testEquals("a.spec.ts", testClassFile.getName());
	}

	private List<TestClassFileMethod> _getTestClassFileMethods(String content)
		throws Exception {

		File projectDir = createProjectDir("vitest run");

		write(content, projectDir, "src/a.spec.ts");

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		TestClassFile testClassFile = testPackage.getTestClassFile(
			"src/a.spec.ts");

		return testClassFile.getTestClassFileMethods();
	}

	private void _testGetTestClassFileMethods(
			String content, String... expectedFullNames)
		throws Exception {

		List<String> fullNames = new ArrayList<>();

		for (TestClassFileMethod testClassFileMethod :
				_getTestClassFileMethods(content)) {

			fullNames.add(testClassFileMethod.getFullName());
		}

		testEquals(Arrays.asList(expectedFullNames), fullNames);
	}

}