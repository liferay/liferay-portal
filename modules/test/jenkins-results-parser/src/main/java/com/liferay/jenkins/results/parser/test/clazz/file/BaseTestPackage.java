/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestPackage implements TestPackage {

	@Override
	public String getName() {
		String name = _packageJSONObject.optString("name", null);

		if (name != null) {
			return name;
		}

		File dir = _packageJSONFile.getParentFile();

		return dir.getName();
	}

	@Override
	public File getPackageJSONFile() {
		return _packageJSONFile;
	}

	@Override
	public JSONObject getPackageJSONObject() {
		return _packageJSONObject;
	}

	@Override
	public List<TestClassFile> getTestClassFiles() throws IOException {
		if (_testClassFiles != null) {
			return Collections.unmodifiableList(_testClassFiles);
		}

		_testClassFiles = new ArrayList<>();

		File projectDir = getProjectDir();

		final Path projectDirPath = projectDir.toPath();

		final TestPackage testPackage = this;

		Files.walkFileTree(
			projectDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					if (filePath.equals(projectDirPath)) {
						return FileVisitResult.CONTINUE;
					}

					Path fileNamePath = filePath.getFileName();

					if (_excludedDirNames.contains(fileNamePath.toString())) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					File file = filePath.toFile();

					if (!TestClassFileFactory.isTestClassFile(file) ||
						isTestClassFileIgnored(file)) {

						return FileVisitResult.CONTINUE;
					}

					TestClassFile testClassFile =
						TestClassFileFactory.newTestClassFile(
							file, testPackage);

					if (testClassFile != null) {
						_testClassFiles.add(testClassFile);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(
					Path filePath, IOException ioException) {

					return FileVisitResult.CONTINUE;
				}

			});

		return Collections.unmodifiableList(_testClassFiles);
	}

	@Override
	public String getTestScript() {
		JSONObject scriptsJSONObject = _packageJSONObject.optJSONObject(
			"scripts");

		if (scriptsJSONObject == null) {
			return null;
		}

		return scriptsJSONObject.optString("test", null);
	}

	@Override
	public boolean isTestClassFileIgnored(File file) {
		return false;
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.getCanonicalPath(_packageJSONFile);
	}

	protected BaseTestPackage(File packageJSONFile) throws IOException {
		_packageJSONFile = packageJSONFile;

		_packageJSONObject = new JSONObject(
			JenkinsResultsParserUtil.read(packageJSONFile));
	}

	protected Map<String, TestClassFile> getClassNameTestClassFilesMap()
		throws IOException {

		_initializeTestClassFiles();

		return _classNameTestClassFilesMap;
	}

	protected Map<String, TestClassFile> getClassPathTestClassFilesMap()
		throws IOException {

		_initializeTestClassFiles();

		return _classPathTestClassFilesMap;
	}

	protected Map<String, List<TestClassFile>>
			getParentDirPathTestClassFilesMap()
		throws IOException {

		_initializeTestClassFiles();

		return _parentDirPathTestClassFilesMap;
	}

	protected File getProjectDir() {
		return _packageJSONFile.getParentFile();
	}

	private synchronized void _initializeTestClassFiles() throws IOException {
		if (_testClassFilesCached) {
			return;
		}

		_classNameTestClassFilesMap = new HashMap<>();
		_classPathTestClassFilesMap = new HashMap<>();
		_parentDirPathTestClassFilesMap = new HashMap<>();

		for (TestClassFile testClassFile : getTestClassFiles()) {
			String relativeClassPath =
				JenkinsResultsParserUtil.getPathRelativeTo(
					testClassFile.getFile(), getProjectDir());

			_classPathTestClassFilesMap.put(relativeClassPath, testClassFile);

			int x = relativeClassPath.lastIndexOf("/");

			String relativeParentDirPath = "";

			if (x != -1) {
				relativeParentDirPath = relativeClassPath.substring(0, x);
			}

			File file = testClassFile.getFile();

			String className = file.getName();

			className = className.replace('.', '_');

			if (!relativeParentDirPath.isEmpty()) {
				className = relativeParentDirPath + "/" + className;
			}

			_classNameTestClassFilesMap.put(className, testClassFile);

			List<TestClassFile> testClassFiles =
				_parentDirPathTestClassFilesMap.get(relativeParentDirPath);

			if (testClassFiles == null) {
				testClassFiles = new ArrayList<>();
			}

			testClassFiles.add(testClassFile);

			_parentDirPathTestClassFilesMap.put(
				relativeParentDirPath, testClassFiles);
		}

		_testClassFilesCached = true;
	}

	private static final List<String> _excludedDirNames = Arrays.asList(
		".git", ".gradle", "bin", "build", "classes", "dist", "node_modules",
		"test-classes", "test-coverage", "tmp");

	private Map<String, TestClassFile> _classNameTestClassFilesMap;
	private Map<String, TestClassFile> _classPathTestClassFilesMap;
	private final File _packageJSONFile;
	private final JSONObject _packageJSONObject;
	private Map<String, List<TestClassFile>> _parentDirPathTestClassFilesMap;
	private List<TestClassFile> _testClassFiles;
	private boolean _testClassFilesCached;

}