/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileUtil {

	public static void formatTestResultsFile(
			File portalDir, File testResultsFile)
		throws DocumentException, IOException {

		if (!testResultsFile.exists()) {
			return;
		}

		File projectDir = testResultsFile.getParentFile();

		String projectPath = JenkinsResultsParserUtil.getPathRelativeTo(
			projectDir, portalDir);

		TestPackage testPackage = TestPackageFactory.newTestPackage(projectDir);

		if (testPackage == null) {
			return;
		}

		List<Element> testSuiteElements = new ArrayList<>();

		Document document = Dom4JUtil.parse(
			JenkinsResultsParserUtil.read(testResultsFile));

		Element rootElement = document.getRootElement();

		if (Objects.equals(rootElement.getName(), "testsuites")) {
			testSuiteElements.addAll(rootElement.elements("testsuite"));
		}
		else {
			testSuiteElements.add(rootElement);
		}

		Set<TestClassFileMethod> matchedTestClassFileMethods = new HashSet<>();

		for (Element testSuiteElement : testSuiteElements) {
			testSuiteElement.addAttribute("package", projectPath);

			for (Element testCaseElement :
					testSuiteElement.elements("testcase")) {

				_formatTestCaseElement(
					matchedTestClassFileMethods, portalDir, testCaseElement,
					testPackage);
			}
		}

		Dom4JUtil.removeWhitespaceTextNodes(rootElement);

		JenkinsResultsParserUtil.write(
			testResultsFile,
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				Dom4JUtil.format(rootElement));

		System.out.println("Formatted " + testResultsFile);
	}

	private static void _formatTestCaseElement(
			Set<TestClassFileMethod> matchedTestClassFileMethods,
			File portalDir, Element testCaseElement, TestPackage testPackage)
		throws IOException {

		String testCaseClassName = testCaseElement.attributeValue("classname");

		TestClassFile testClassFile = testPackage.getTestClassFile(
			testCaseClassName);

		List<TestClassFile> testClassFiles;

		if (testClassFile == null) {
			testClassFiles = testPackage.getTestClassFiles(testCaseClassName);
		}
		else {
			testClassFiles = Collections.singletonList(testClassFile);
		}

		if ((testClassFiles == null) || testClassFiles.isEmpty()) {
			return;
		}

		String testCaseName = testCaseElement.attributeValue("name");

		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			matchedTestClassFileMethods, testCaseName, testClassFiles);

		if (testClassFileMethod != null) {
			testClassFile = testClassFileMethod.getTestClassFile();
		}

		if (testClassFile == null) {
			return;
		}

		testCaseElement.addAttribute(
			"classname",
			JenkinsResultsParserUtil.getPathRelativeTo(
				testClassFile.getFile(), portalDir));

		if ((testClassFileMethod != null) &&
			testClassFileMethod.matches(testCaseName)) {

			testCaseElement.addAttribute(
				"name", testClassFileMethod.getFullName());
		}
	}

	private static TestClassFileMethod _getTestClassFileMethod(
			Set<TestClassFileMethod> matchedTestClassFileMethods,
			String testCaseName, List<TestClassFile> testClassFiles)
		throws IOException {

		TestClassFileMethod matchedTestClassFileMethod = null;

		for (TestClassFile testClassFile : testClassFiles) {
			for (TestClassFileMethod testClassFileMethod :
					testClassFile.getTestClassFileMethods()) {

				if (!testClassFileMethod.matches(testCaseName)) {
					continue;
				}

				if (matchedTestClassFileMethods.add(testClassFileMethod)) {
					return testClassFileMethod;
				}

				if (matchedTestClassFileMethod == null) {
					matchedTestClassFileMethod = testClassFileMethod;
				}
			}
		}

		if (matchedTestClassFileMethod != null) {
			return matchedTestClassFileMethod;
		}

		for (TestClassFile testClassFile : testClassFiles) {
			for (TestClassFileMethod testClassFileMethod :
					testClassFile.getTestClassFileMethods()) {

				if (testClassFileMethod.matchesPattern(testCaseName)) {
					return testClassFileMethod;
				}
			}
		}

		return null;
	}

}