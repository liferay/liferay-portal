/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class TestClassResultsUtil {

	public static File getProjectTestClassResultsDir(
		String projectName, File baseDir) {

		Matcher matcher = _projectNamePattern.matcher(projectName);

		if (matcher.find()) {
			String testProjectName = matcher.group(1);

			testProjectName = testProjectName.replaceAll(":", "/");

			return new File(baseDir, "modules/" + testProjectName);
		}

		return null;
	}

	public static String getTestClassResultsFileName(File file) {
		Matcher matcher = _testClassResultsFileNamePattern.matcher(
			file.getAbsolutePath());

		if (matcher.find()) {
			return matcher.group(0);
		}

		return null;
	}

	public static void moveTestClassResultsFiles(
			File testProjectResultsDir, File baseDir)
		throws IOException {

		File testClassResultsDir = new File(baseDir, "modules/test-results");

		List<File> files = JenkinsResultsParserUtil.getIncludedFiles(
			testProjectResultsDir, null, _INCLUDED_FILES_GLOBS);

		for (File file : files) {
			String testClassResultsFileName = getTestClassResultsFileName(file);

			File destinationFile = new File(
				testClassResultsDir, testClassResultsFileName);

			JenkinsResultsParserUtil.move(file, destinationFile);

			if (file.exists()) {
				continue;
			}

			System.out.println("Deleted results file:" + file);
		}
	}

	private static final String[] _INCLUDED_FILES_GLOBS = {"**/TEST-**.xml"};

	private static final Pattern _projectNamePattern = Pattern.compile(
		"(:.*)(:test|testIntegration)");
	private static final Pattern _testClassResultsFileNamePattern =
		Pattern.compile("TEST-.*.xml");

}