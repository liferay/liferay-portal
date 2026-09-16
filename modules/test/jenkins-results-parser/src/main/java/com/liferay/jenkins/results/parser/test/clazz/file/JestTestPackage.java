/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JestTestPackage extends BaseTestPackage {

	@Override
	public TestClassFile getTestClassFile(String classPath) throws IOException {
		Map<String, TestClassFile> classPathTestClassFilesMap =
			getClassPathTestClassFilesMap();

		TestClassFile testClassFile = classPathTestClassFilesMap.get(classPath);

		if (testClassFile != null) {
			return testClassFile;
		}

		Map<String, TestClassFile> classNameTestClassFilesMap =
			getClassNameTestClassFilesMap();

		return classNameTestClassFilesMap.get(_formatParentDirPath(classPath));
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

	@Override
	public boolean isTestClassFileIgnored(File file) {
		List<Pattern> testPathIgnorePatterns = _getTestPathIgnorePatterns();

		if (testPathIgnorePatterns.isEmpty()) {
			return false;
		}

		String filePath = JenkinsResultsParserUtil.getCanonicalPath(file);

		for (Pattern testPathIgnorePattern : testPathIgnorePatterns) {
			Matcher matcher = testPathIgnorePattern.matcher(filePath);

			if (matcher.find()) {
				return true;
			}
		}

		return false;
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

	private synchronized List<Pattern> _getTestPathIgnorePatterns() {
		if (_testPathIgnorePatterns != null) {
			return _testPathIgnorePatterns;
		}

		_testPathIgnorePatterns = new ArrayList<>();

		JSONObject packageJSONObject = getPackageJSONObject();

		JSONObject jestJSONObject = packageJSONObject.optJSONObject("jest");

		if (jestJSONObject == null) {
			return _testPathIgnorePatterns;
		}

		JSONArray testPathIgnorePatternsJSONArray = jestJSONObject.optJSONArray(
			"testPathIgnorePatterns");

		if (testPathIgnorePatternsJSONArray == null) {
			return _testPathIgnorePatterns;
		}

		String projectDirPath = JenkinsResultsParserUtil.getCanonicalPath(
			getProjectDir());

		for (int i = 0; i < testPathIgnorePatternsJSONArray.length(); i++) {
			String testPathIgnorePattern =
				testPathIgnorePatternsJSONArray.optString(i, null);

			if (JenkinsResultsParserUtil.isNullOrEmpty(testPathIgnorePattern)) {
				continue;
			}

			testPathIgnorePattern = testPathIgnorePattern.replace(
				_ROOT_DIR, Pattern.quote(projectDirPath));

			try {
				_testPathIgnorePatterns.add(
					Pattern.compile(testPathIgnorePattern));
			}
			catch (PatternSyntaxException patternSyntaxException) {
				System.out.println(
					"WARNING: Unable to compile test path ignore pattern " +
						testPathIgnorePattern);
			}
		}

		return _testPathIgnorePatterns;
	}

	private static final String _ROOT_DIR = "<rootDir>";

	private List<Pattern> _testPathIgnorePatterns;

}