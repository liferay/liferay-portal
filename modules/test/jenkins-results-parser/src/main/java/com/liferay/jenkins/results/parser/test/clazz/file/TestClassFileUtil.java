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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

		for (Element testSuiteElement : testSuiteElements) {
			testSuiteElement.addAttribute("package", projectPath);

			for (Element testCaseElement :
					testSuiteElement.elements("testcase")) {

				_formatTestCaseElement(portalDir, testCaseElement, testPackage);
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
			File portalDir, Element testCaseElement, TestPackage testPackage)
		throws IOException {

		List<TestClassFile> testClassFiles = _getTestClassFiles(
			testCaseElement, testPackage);

		if ((testClassFiles == null) || testClassFiles.isEmpty()) {
			return;
		}

		String testCaseName = testCaseElement.attributeValue("name");

		TestClassFileMethod testClassFileMethod = _getTestClassFileMethod(
			testCaseName, testClassFiles);

		if (testClassFileMethod == null) {
			return;
		}

		TestClassFile testClassFile = testClassFileMethod.getTestClassFile();

		testCaseElement.addAttribute(
			"classname",
			JenkinsResultsParserUtil.getPathRelativeTo(
				testClassFile.getFile(), portalDir));

		if (testClassFileMethod.matches(testCaseName)) {
			testCaseElement.addAttribute(
				"name", testClassFileMethod.getFullName());
		}
	}

	private static TestClassFileMethod _getTestClassFileMethod(
			String testCaseName, List<TestClassFile> testClassFiles)
		throws IOException {

		for (TestClassFile testClassFile : testClassFiles) {
			for (TestClassFileMethod testClassFileMethod :
					testClassFile.getTestClassFileMethods()) {

				if (testClassFileMethod.matches(testCaseName)) {
					return testClassFileMethod;
				}
			}
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

	private static List<TestClassFile> _getTestClassFiles(
			Element testCaseElement, TestPackage testPackage)
		throws IOException {

		String testCaseClassName = testCaseElement.attributeValue("classname");

		TestClassFile testClassFile = testPackage.getTestClassFile(
			testCaseClassName);

		if (testClassFile != null) {
			return Arrays.asList(testClassFile);
		}

		return testPackage.getTestClassFiles(testCaseClassName);
	}

}