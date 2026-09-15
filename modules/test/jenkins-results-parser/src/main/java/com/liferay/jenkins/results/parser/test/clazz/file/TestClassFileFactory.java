/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileFactory {

	public static TestClassFile newTestClassFile(
		File file, TestPackage testPackage) {

		if ((file == null) || !file.isFile() || (testPackage == null)) {
			return null;
		}

		file = JenkinsResultsParserUtil.getCanonicalFile(file);

		TestClassFile testClassFile = _testClassFiles.get(file);

		if (testClassFile != null) {
			return testClassFile;
		}

		if (isTestClassFile(file)) {
			testClassFile = new JSTestClassFile(file, testPackage);
		}

		if (testClassFile == null) {
			return null;
		}

		_testClassFiles.put(file, testClassFile);

		return testClassFile;
	}

	protected static boolean isTestClassFile(File file) {
		String name = file.getName();

		for (String extension : _JS_UNIT_EXTENSIONS) {
			if (name.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	private static final String[] _JS_UNIT_EXTENSIONS = {
		".js", ".jsx", ".ts", ".tsx"
	};

	private static final Map<File, TestClassFile> _testClassFiles =
		new ConcurrentHashMap<>();

}