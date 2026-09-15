/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestPackageFactory {

	public static TestPackage newTestPackage(File projectDir)
		throws IOException {

		if ((projectDir == null) || !projectDir.isDirectory()) {
			return null;
		}

		projectDir = JenkinsResultsParserUtil.getCanonicalFile(projectDir);

		TestPackage testPackage = _testPackages.get(projectDir);

		if (testPackage != null) {
			return testPackage;
		}

		File packageJSONFile = new File(projectDir, "package.json");

		if (!packageJSONFile.isFile()) {
			return null;
		}

		String testScript = _getTestScript(packageJSONFile);

		if (JenkinsResultsParserUtil.isNullOrEmpty(testScript)) {
			return null;
		}

		if (testScript.contains(_JEST) ||
			testScript.contains(_NODE_SCRIPTS_TEST)) {

			testPackage = new JestTestPackage(packageJSONFile);
		}
		else if (testScript.contains(_VITEST)) {
			testPackage = new VitestTestPackage(packageJSONFile);
		}

		if (testPackage == null) {
			return null;
		}

		_testPackages.put(projectDir, testPackage);

		return testPackage;
	}

	private static String _getTestScript(File packageJSONFile)
		throws IOException {

		JSONObject packageJSONObject = new JSONObject(
			JenkinsResultsParserUtil.read(packageJSONFile));

		JSONObject scriptsJSONObject = packageJSONObject.optJSONObject(
			"scripts");

		if (scriptsJSONObject == null) {
			return null;
		}

		return scriptsJSONObject.optString("test");
	}

	private static final String _JEST = "jest";

	private static final String _NODE_SCRIPTS_TEST = "node-scripts test";

	private static final String _VITEST = "vitest";

	private static final Map<File, TestPackage> _testPackages =
		new ConcurrentHashMap<>();

}