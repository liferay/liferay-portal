/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitJUnitTestResult extends JUnitTestResult {

	@Override
	public String getClassName() {
		String testClassFilePath = _getTestClassFilePath();

		if (testClassFilePath != null) {
			return testClassFilePath;
		}

		return getTestTaskName();
	}

	@Override
	public String getDisplayName() {
		return getTestName();
	}

	@Override
	public String getTestName() {
		String testClassFilePath = _getTestClassFilePath();

		if (testClassFilePath != null) {
			return super.getTestName();
		}

		String testName = JenkinsResultsParserUtil.combine(
			super.getClassName(), ".", super.getTestName());

		int x = testName.indexOf(".modules.");

		if (x > 0) {
			testName = testName.substring(x + 9);
		}

		if (!testName.startsWith("apps") && !testName.startsWith("dxp.apps")) {
			testName = "apps." + testName;
		}

		return testName;
	}

	@Override
	public String getTestResultKey() {
		return JenkinsResultsParserUtil.combine(
			getClassName(), "#", getTestName());
	}

	@Override
	public String getTestTaskName() {
		String testClassFilePath = _getTestClassFilePath();

		if (testClassFilePath != null) {
			String workspaceTestTaskName = _getWorkspaceTestTaskName(
				testClassFilePath);

			if (workspaceTestTaskName != null) {
				return workspaceTestTaskName;
			}

			return _getTestTaskName("/", testClassFilePath);
		}

		return _getTestTaskName(
			".",
			JenkinsResultsParserUtil.combine(
				super.getClassName(), ".", super.getTestName()));
	}

	protected JSUnitJUnitTestResult(Build build, JSONObject caseJSONObject) {
		super(build, caseJSONObject);
	}

	private String _getTestClassFilePath() {
		String className = super.getClassName();

		if ((className == null) || !className.contains("/")) {
			return null;
		}

		return className;
	}

	private String _getTestTaskName(String separator, String testTaskName) {
		String modulesDirPath = "modules" + separator;

		int x = testTaskName.indexOf(separator + modulesDirPath);

		if (x > 0) {
			testTaskName = testTaskName.substring(
				x + separator.length() + modulesDirPath.length());
		}
		else if (testTaskName.startsWith(modulesDirPath)) {
			testTaskName = testTaskName.substring(modulesDirPath.length());
		}

		for (String testDirName : _TEST_DIR_NAMES) {
			String testDirPath = separator + testDirName + separator;

			if (testTaskName.contains(testDirPath)) {
				testTaskName = testTaskName.substring(
					0, testTaskName.indexOf(testDirPath));

				break;
			}
		}

		if (!testTaskName.contains("apps" + separator)) {
			testTaskName = "apps" + separator + testTaskName;
		}

		return ":" + testTaskName.replace(separator, ":") + ":packageRunTest";
	}

	private String _getWorkspaceTestTaskName(String testClassFilePath) {
		Matcher matcher = _workspacePattern.matcher(testClassFilePath);

		if (!matcher.matches()) {
			return null;
		}

		String projectPath = matcher.group("projectPath");

		for (String testDirName : _TEST_DIR_NAMES) {
			String testDirPath = "/" + testDirName + "/";

			if (projectPath.contains(testDirPath)) {
				projectPath = projectPath.substring(
					0, projectPath.indexOf(testDirPath));

				break;
			}
		}

		return JenkinsResultsParserUtil.combine(
			matcher.group("workspaceDir"), projectPath.replaceAll("/", ":"),
			":packageRunTest");
	}

	private static final String[] _TEST_DIR_NAMES = {"clay", "src", "test"};

	private static final Pattern _workspacePattern = Pattern.compile(
		"(?<workspaceDir>.*workspaces/[^/]+)(?<projectPath>/.+)");

}