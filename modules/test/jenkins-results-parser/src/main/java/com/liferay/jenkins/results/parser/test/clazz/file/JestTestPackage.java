/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class JestTestPackage extends BaseTestPackage {

	@Override
	public TestClassFile getTestClassFile(String classPath) throws IOException {
		Map<String, TestClassFile> classPathTestClassFilesMap =
			getClassPathTestClassFilesMap();

		return classPathTestClassFilesMap.get(classPath);
	}

	@Override
	public List<TestClassFile> getTestClassFiles(String parentDirPath)
		throws IOException {

		if (JenkinsResultsParserUtil.isNullOrEmpty(parentDirPath)) {
			return Collections.emptyList();
		}

		Map<String, List<TestClassFile>> parentDirPathTestClassFilesMap =
			getParentDirPathTestClassFilesMap();

		List<TestClassFile> testClassFiles = parentDirPathTestClassFilesMap.get(
			parentDirPath);

		if (testClassFiles == null) {
			testClassFiles = parentDirPathTestClassFilesMap.get(
				_formatParentDirPath(parentDirPath));
		}

		if (testClassFiles == null) {
			return Collections.emptyList();
		}

		return testClassFiles;
	}

	protected JestTestPackage(File packageJSONFile) throws IOException {
		super(packageJSONFile);
	}

	private String _formatParentDirPath(String parentDirPath) {
		parentDirPath = parentDirPath.replace('.', '/');

		File projectDir = getProjectDir();

		String name = projectDir.getName();

		int index = parentDirPath.indexOf(name + "/");

		if (index != -1) {
			parentDirPath = parentDirPath.substring(index + name.length() + 1);
		}

		return parentDirPath;
	}

}