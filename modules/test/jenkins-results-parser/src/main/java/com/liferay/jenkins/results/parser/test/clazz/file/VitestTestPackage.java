/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class VitestTestPackage extends BaseTestPackage {

	@Override
	public TestClassFile getTestClassFile(String classPath) throws IOException {
		Map<String, TestClassFile> classPathTestClassFilesMap =
			getClassPathTestClassFilesMap();

		return classPathTestClassFilesMap.get(classPath);
	}

	@Override
	public List<TestClassFile> getTestClassFiles(String parentDirPath)
		throws IOException {

		Map<String, List<TestClassFile>> parentDirPathTestClassFilesMap =
			getParentDirPathTestClassFilesMap();

		return parentDirPathTestClassFilesMap.get(parentDirPath);
	}

	protected VitestTestPackage(File file) throws IOException {
		super(file);
	}

}