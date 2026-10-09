/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.get.file.set;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Lily Chi
 */
public class GetFileSetTask extends Task {

	@Override
	public void execute() throws BuildException {
		List<String> classNames = new ArrayList<>();

		Collections.addAll(classNames, _classNames.split(","));

		Set<String> invalidClassNames = new HashSet<>();

		if (!_hasInvalidClassNames(classNames, invalidClassNames)) {
			_logger.log(
				Level.WARNING,
				"Input class names instead of test class names: {0}",
				invalidClassNames.toString());

			return;
		}

		List<Path> classPaths = new ArrayList<>();
		List<Path> srcPaths = new ArrayList<>();

		File baseDir = new File(_rootDirName);

		Set<String> missingClassNames = _getMissingClassNames(
			baseDir, classNames, classPaths, srcPaths);

		if (!missingClassNames.isEmpty()) {
			_logger.log(
				Level.WARNING, "No class files found for: {0}",
				missingClassNames.toString());

			if (classPaths.isEmpty()) {
				return;
			}
		}

		Project project = getProject();

		FileSet classFileSet = new FileSet();

		classFileSet.setDir(baseDir);

		for (Path classPath : classPaths) {
			String classPathString = String.valueOf(classPath);

			classPathString = classPathString.substring(
				_rootDirName.length() + 1);

			classFileSet.setIncludes(classPathString);
		}

		classFileSet.setProject(getProject());

		project.addReference("get.file.set.class.set", classFileSet);

		DirSet srcDirSet = new DirSet();

		srcDirSet.setDir(baseDir);

		for (Path srcPath : srcPaths) {
			String srcPathString = String.valueOf(srcPath);

			if (!_isInvalidClassName(srcPathString, invalidClassNames)) {
				_logger.log(
					Level.WARNING, "Invalid class name: {0}",
					invalidClassNames.toString());

				return;
			}

			int index = srcPathString.indexOf("src") + 3;

			if (srcPathString.contains(_PATH_STRING_SRC_MAIN_JAVA)) {
				index =
					srcPathString.indexOf(_PATH_STRING_SRC_MAIN_JAVA) +
						_PATH_STRING_SRC_MAIN_JAVA.length();
			}

			srcPathString = srcPathString.substring(
				_rootDirName.length() + 1, index);

			srcDirSet.setIncludes(srcPathString);
		}

		srcDirSet.setProject(project);

		project.addReference("get.file.set.src.set", srcDirSet);
	}

	public void setClassNames(String classNames) {
		_classNames = classNames;
	}

	public void setRootDir(String rootDirName) {
		_rootDirName = rootDirName;
	}

	private Set<String> _getMissingClassNames(
		File baseDir, List<String> classNames, List<Path> classPaths,
		List<Path> srcPaths) {

		if (!baseDir.exists() || !baseDir.isDirectory()) {
			throw new BuildException();
		}

		Set<String> missingClassNames = new HashSet<>();

		missingClassNames.addAll(classNames);

		try {
			Files.walkFileTree(
				baseDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						String fileName = String.valueOf(path.getFileName());

						Path parentDir = path.getParent();

						if (_isSkip(fileName, parentDir.toString())) {
							return FileVisitResult.SKIP_SUBTREE;
						}

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						String fileName = String.valueOf(path.getFileName());

						for (String className : classNames) {
							if (_isClass(className, fileName)) {
								classPaths.add(path);

								missingClassNames.remove(className);
							}
							else if (fileName.equals(
										className.concat(".java"))) {

								srcPaths.add(path);
							}
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new BuildException(ioException);
		}

		return missingClassNames;
	}

	private boolean _hasInvalidClassNames(
		List<String> classNames, Set<String> invalidClassNames) {

		for (String className : classNames) {
			if (!className.endsWith("Test")) {
				continue;
			}

			invalidClassNames.add(className);
		}

		return invalidClassNames.isEmpty();
	}

	private boolean _isClass(String className, String fileName) {
		if (fileName.equals(className.concat(".class")) ||
			(fileName.startsWith(className.concat("$")) &&
			 fileName.endsWith(".class"))) {

			return true;
		}

		return false;
	}

	private boolean _isInvalidClassName(
		String absoluteFileName, Set<String> invalidClassNames) {

		if (!absoluteFileName.contains("src")) {
			int x = absoluteFileName.lastIndexOf(File.separator);
			int y = absoluteFileName.lastIndexOf(".");

			invalidClassNames.add(absoluteFileName.substring(x + 1, y));

			return false;
		}

		return true;
	}

	private boolean _isSkip(String fileName, String parentDirName) {
		if (fileName.startsWith(".") ||
			_skipModuleFileNames.contains(fileName) ||
			(_skipFileNames.contains(fileName) &&
			 parentDirName.equals(_rootDirName))) {

			return true;
		}

		return false;
	}

	private static final String _PATH_STRING_SRC_MAIN_JAVA = String.valueOf(
		Paths.get("src", "main", "java"));

	private static final Logger _logger = Logger.getLogger(
		GetFileSetTask.class.getName());

	private static final List<String> _skipFileNames = Arrays.asList(
		"benchmarks", "definitions", "gradle", "lib", "nbproject",
		"oss-licenses", "readme", "sql", "tools");
	private static final List<String> _skipModuleFileNames = Arrays.asList(
		"node_modules");

	private String _classNames;
	private String _rootDirName;

}