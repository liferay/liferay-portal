/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileUtilTest extends BaseTestClassFileTestCase {

	@Test
	public void testFormatTestResultsFileClassName() throws Exception {
		_testFormat(
			_CLASS_NAME + ".a_test_js", "a b", "a > b", "a.test.js",
			"a.test.js", _TEST_FILE_CONTENT);
		_testFormat(
			_CLASS_NAME + ".b_js", "a b", "a > b", "b.js", "a.js",
			_TEST_FILE_CONTENT, "b.js", _TEST_FILE_CONTENT);

		String name = RandomTestUtil.randomString();

		_testFormat(
			_CLASS_NAME + ".a_js", name, name, "a.js", "a.js",
			"it('b', () => {});");
	}

	@Test
	public void testFormatTestResultsFileDuplicateTestName() throws Exception {
		File projectDir = _createProjectDir(
			"a.js", _TEST_FILE_CONTENT, "b.js", _TEST_FILE_CONTENT);

		List<Element> testCaseElements = _formatTestCaseElements(
			_getTestSuite(
				_getTestCase(_CLASS_NAME, "a b") +
					_getTestCase(_CLASS_NAME, "a b")),
			projectDir);

		testEquals(
			new HashSet<>(
				Arrays.asList(
					_getTestFilePath("a.js"), _getTestFilePath("b.js"))),
			_getClassNames(testCaseElements));
	}

	@Test
	public void testFormatTestResultsFileDuplicateTestNameExhausted()
		throws Exception {

		File projectDir = _createProjectDir("a.js", _TEST_FILE_CONTENT);

		List<Element> testCaseElements = _formatTestCaseElements(
			_getTestSuite(
				_getTestCase(_CLASS_NAME, "a b") +
					_getTestCase(_CLASS_NAME, "a b")),
			projectDir);

		testEquals(
			new HashSet<>(Arrays.asList(_getTestFilePath("a.js"))),
			_getClassNames(testCaseElements));
	}

	@Test
	public void testFormatTestResultsFileDynamic() throws Exception {
		_testFormat(
			_CLASS_NAME, "100abc complete", "100abc complete", "a.js", "a.js",
			"it('100%s complete', () => {});");
		_testFormat(
			_CLASS_NAME, "a value", "a value", "a.js", "a.js",
			"it(`a ${b}`, () => {});");
	}

	@Test
	public void testFormatTestResultsFileExactMatch() throws Exception {
		_testFormat(
			_CLASS_NAME, "c a", "c a", "b.js", "a.js",
			"it(`c ${d}`, () => {});", "b.js", "it('c a', () => {});");
		_testFormat(
			_CLASS_NAME, "c a", "c a", "b.js", "z.js",
			"it(`c ${d}`, () => {});", "b.js", "it('c a', () => {});");
	}

	@Test
	public void testFormatTestResultsFileJest() throws Exception {
		_testFormat(
			_CLASS_NAME, "a b", "a > b", "a.js", "a.js", _TEST_FILE_CONTENT);
	}

	@Test
	public void testFormatTestResultsFileMissing() throws Exception {
		File projectDir = createProjectDir("node-scripts test");

		File testResultsFile = new File(projectDir, "TEST-frontend-js.xml");

		TestClassFileUtil.formatTestResultsFile(
			projectDir.getParentFile(), testResultsFile);

		Assert.assertFalse(testResultsFile.exists());
	}

	@Test
	public void testFormatTestResultsFileNoMatch() throws Exception {
		String name = RandomTestUtil.randomString();

		Element testCaseElement = _format(
			_CLASS_NAME, name, "a.js", "it('b', () => {});");

		testEquals(_CLASS_NAME, testCaseElement.attributeValue("classname"));
		testEquals(name, testCaseElement.attributeValue("name"));
	}

	@Test
	public void testFormatTestResultsFileUnchanged() throws Exception {
		File projectDir = _createProjectDir("a.js", _TEST_FILE_CONTENT);

		File testResultsFile = _createTestResultsFile(
			_getTestSuite(_getTestCase(_CLASS_NAME, "a b")), projectDir);

		File portalDir = projectDir.getParentFile();

		TestClassFileUtil.formatTestResultsFile(portalDir, testResultsFile);

		String content = JenkinsResultsParserUtil.read(testResultsFile);

		TestClassFileUtil.formatTestResultsFile(portalDir, testResultsFile);

		testEquals(content, JenkinsResultsParserUtil.read(testResultsFile));
	}

	@Test
	public void testFormatTestResultsFileVitest() throws Exception {
		File projectDir = createProjectDir("vitest run");

		write(_TEST_FILE_CONTENT, projectDir, "src/tests/api.spec.ts");

		Element testCaseElement = _format(
			JenkinsResultsParserUtil.combine(
				"<testsuites name=\"vitest tests\">",
				_getTestSuite(_getTestCase("src/tests/api.spec.ts", "a > b")),
				"</testsuites>"),
			projectDir);

		testEquals("a > b", testCaseElement.attributeValue("name"));
		testEquals(
			_PROJECT_PATH + "/src/tests/api.spec.ts",
			testCaseElement.attributeValue("classname"));
	}

	private File _createProjectDir(String... testFiles) throws Exception {
		File projectDir = createProjectDir("node-scripts test");

		for (int i = 0; i < testFiles.length; i += 2) {
			write(
				testFiles[i + 1], projectDir,
				_TEST_DIR_PATH + "/" + testFiles[i]);
		}

		return projectDir;
	}

	private File _createTestResultsFile(String content, File projectDir)
		throws Exception {

		write(content, projectDir, "TEST-frontend-js.xml");

		return new File(projectDir, "TEST-frontend-js.xml");
	}

	private Element _format(String content, File projectDir) throws Exception {
		List<Element> testCaseElements = _formatTestCaseElements(
			content, projectDir);

		return testCaseElements.get(0);
	}

	private Element _format(
			String testCaseClassName, String testCaseName, String... testFiles)
		throws Exception {

		return _format(
			_getTestSuite(_getTestCase(testCaseClassName, testCaseName)),
			_createProjectDir(testFiles));
	}

	private List<Element> _formatTestCaseElements(
			String content, File projectDir)
		throws Exception {

		File testResultsFile = _createTestResultsFile(content, projectDir);

		TestClassFileUtil.formatTestResultsFile(
			projectDir.getParentFile(), testResultsFile);

		Document document = Dom4JUtil.parse(
			JenkinsResultsParserUtil.read(testResultsFile));

		Element rootElement = document.getRootElement();

		Element testSuiteElement = rootElement;

		if (!Objects.equals(testSuiteElement.getName(), "testsuite")) {
			List<Element> testSuiteElements = rootElement.elements("testsuite");

			testSuiteElement = testSuiteElements.get(0);
		}

		testEquals(_PROJECT_PATH, testSuiteElement.attributeValue("package"));

		return testSuiteElement.elements("testcase");
	}

	private Set<String> _getClassNames(List<Element> testCaseElements) {
		Set<String> classNames = new HashSet<>();

		for (Element testCaseElement : testCaseElements) {
			classNames.add(testCaseElement.attributeValue("classname"));
		}

		return classNames;
	}

	private String _getTestCase(String className, String name) {
		return JenkinsResultsParserUtil.combine(
			"<testcase classname=\"", className, "\" name=\"", name,
			"\" time=\"1\" />");
	}

	private String _getTestFilePath(String testFileName) {
		return _PROJECT_PATH + "/" + _TEST_DIR_PATH + "/" + testFileName;
	}

	private String _getTestSuite(String testCases) {
		return JenkinsResultsParserUtil.combine(
			"<testsuite name=\"a\" tests=\"1\">", testCases, "</testsuite>");
	}

	private void _testFormat(
			String testCaseClassName, String testCaseName,
			String expectedTestCaseName, String expectedTestFileName,
			String... testFiles)
		throws Exception {

		Element testCaseElement = _format(
			testCaseClassName, testCaseName, testFiles);

		testEquals(
			expectedTestCaseName, testCaseElement.attributeValue("name"));

		testEquals(
			_getTestFilePath(expectedTestFileName),
			testCaseElement.attributeValue("classname"));
	}

	private static final String _CLASS_NAME =
		TestClassFileUtilTest._PROJECT_PATH + ".test.js";

	private static final String _PROJECT_PATH = "project";

	private static final String _TEST_DIR_PATH = "test/js";

	private static final String _TEST_FILE_CONTENT =
		"describe('a', () => {it('b', () => {});});";

}