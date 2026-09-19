/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.cache.util;

import com.liferay.gradle.util.Validator;
import com.liferay.gradle.util.hash.HashUtil;
import com.liferay.gradle.util.hash.HashValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.process.ExecSpec;
import org.gradle.process.internal.ExecException;

/**
 * @author Andrea Di Giorgi
 */
public class FileUtil extends com.liferay.gradle.util.FileUtil {

	public static SortedSet<File> flattenAndSort(Iterable<File> files)
		throws IOException {

		final SortedSet<File> sortedFiles = new TreeSet<>(new FileComparator());

		for (File file : files) {
			if (file.isDirectory()) {
				Files.walkFileTree(
					file.toPath(),
					new SimpleFileVisitor<Path>() {

						@Override
						public FileVisitResult visitFile(
								Path path,
								BasicFileAttributes basicFileAttributes)
							throws IOException {

							sortedFiles.add(path.toFile());

							return FileVisitResult.CONTINUE;
						}

					});
			}
			else {
				sortedFiles.add(file);
			}
		}

		return sortedFiles;
	}

	public static String getDigest(File file) {
		String digest;

		try {

			// Ignore EOL character differences between operating systems

			List<String> lines = Files.readAllLines(
				file.toPath(), StandardCharsets.UTF_8);

			digest = Integer.toHexString(lines.hashCode());
		}
		catch (IOException ioException) {

			// File is not a text file

			if (_logger.isDebugEnabled()) {
				_logger.debug(file + " is not a text file", ioException);
			}

			HashValue hashValue = HashUtil.sha1(file);

			digest = hashValue.asHexString();
		}

		if (_logger.isInfoEnabled()) {
			_logger.info("Digest of " + file + " is " + digest);
		}

		return digest;
	}

	public static String getDigest(
		Project project, Iterable<File> files, boolean excludeIgnoredFiles) {

		long start = System.currentTimeMillis();

		SortedSet<File> sortedFiles = null;

		try {
			sortedFiles = flattenAndSort(files);
		}
		catch (IOException ioException) {
			throw new GradleException("Unable to flatten files", ioException);
		}

		if (excludeIgnoredFiles) {
			removeIgnoredFiles(project, sortedFiles);
		}

		StringBuilder sb = new StringBuilder();

		for (File file : sortedFiles) {
			if (!file.exists() || Objects.equals(file.getName(), ".DS_Store")) {
				continue;
			}

			sb.append(getDigest(file));
			sb.append(_DIGEST_SEPARATOR);
		}

		if (sb.length() == 0) {
			throw new GradleException("At least one file is required");
		}

		sb.setLength(sb.length() - 1);

		if (_logger.isInfoEnabled()) {
			_logger.info(
				"Getting the digest took " +
					(System.currentTimeMillis() - start) + " ms");
		}

		return sb.toString();
	}

	public static boolean removeIgnoredFiles(
		Project project, SortedSet<File> files) {

		if (files.isEmpty()) {
			return false;
		}

		File rootDir = null;

		File firstFile = files.first();

		if (files.size() == 1) {
			rootDir = firstFile.getParentFile();
		}
		else {
			String dirName = StringUtil.getCommonPrefix(
				'/', _getCanonicalPath(firstFile),
				_getCanonicalPath(files.last()));

			if (Validator.isNotNull(dirName)) {
				rootDir = new File(dirName);
			}
		}

		if (rootDir == null) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"Unable to remove ignored files, common parent directory " +
						"cannot be found");
			}

			return false;
		}

		String result = _getGitResult(
			project, rootDir, "ls-files", "--cached", "--deleted",
			"--exclude-standard", "--modified", "--others", "-z");

		if (Validator.isNull(result)) {
			result = _getGitIgnoredFileNamesResult(project, rootDir);

			if (Validator.isNull(result) && _logger.isWarnEnabled()) {
				_logger.warn(
					"Unable to remove ignored files, Git returned an empty " +
						"result");

				return false;
			}

			String[] ignoredFileNames = result.split("\\000");

			Set<File> ignoredFiles = new HashSet<>();

			for (String fileName : ignoredFileNames) {
				ignoredFiles.add(new File(rootDir, fileName));
			}

			return files.removeAll(ignoredFiles);
		}

		String[] committedFileNames = result.split("\\000");

		Set<File> committedFiles = new HashSet<>();

		for (String fileName : committedFileNames) {
			committedFiles.add(new File(rootDir, fileName));
		}

		return files.retainAll(committedFiles);
	}

	private static String _getCanonicalPath(File file) {
		try {
			String canonicalPath = file.getCanonicalPath();

			if (File.separatorChar != '/') {
				canonicalPath = canonicalPath.replace(File.separatorChar, '/');
			}

			return canonicalPath;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(
				"Unable to get canonical path of " + file, ioException);
		}
	}

	private static List<File> _getGitIgnoreDirs(File dir) {
		try {
			final List<File> gitIgnoreDirs = new ArrayList<>();

			Files.walkFileTree(
				dir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						String dirName = String.valueOf(path.getFileName());

						if (_excludedDirNames.contains(dirName)) {
							return FileVisitResult.SKIP_SUBTREE;
						}

						Path gitIgnorePath = path.resolve(
							_GIT_IGNORE_FILE_NAME);

						if (Files.exists(gitIgnorePath)) {
							gitIgnoreDirs.add(path.toFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});

			return gitIgnoreDirs;
		}
		catch (IOException ioException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Unable to get .gitignore files");
			}

			return Collections.emptyList();
		}
	}

	private static String _getGitIgnoredFileNamesResult(
		Project project, File rootDir) {

		List<File> gitIgnoreDirs = _getGitIgnoreDirs(rootDir);

		for (File gitIgnoreDir : gitIgnoreDirs) {
			File file = new File(gitIgnoreDir, _GIT_IGNORE_FILE_NAME);

			file.renameTo(new File(gitIgnoreDir, _GIT_IGNORE_TEMP_FILE_NAME));
		}

		String result = _getGitResult(
			project, rootDir, "ls-files",
			"--exclude-per-directory=" + _GIT_IGNORE_TEMP_FILE_NAME,
			"--ignored", "--others", "-z");

		for (File gitIgnoreDir : gitIgnoreDirs) {
			File file = new File(gitIgnoreDir, _GIT_IGNORE_TEMP_FILE_NAME);

			file.renameTo(new File(gitIgnoreDir, _GIT_IGNORE_FILE_NAME));
		}

		return result;
	}

	private static String _getGitResult(
		Project project, final File workingDir, final String... args) {

		final ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		try {
			project.exec(
				new Action<ExecSpec>() {

					@Override
					public void execute(ExecSpec execSpec) {
						execSpec.setArgs(Arrays.asList(args));
						execSpec.setExecutable("git");
						execSpec.setStandardOutput(byteArrayOutputStream);
						execSpec.setWorkingDir(workingDir);
					}

				});
		}
		catch (ExecException execException) {
			if (_logger.isInfoEnabled()) {
				_logger.info(execException.getMessage(), execException);
			}
		}

		return byteArrayOutputStream.toString();
	}

	private static final char _DIGEST_SEPARATOR = '-';

	private static final String _GIT_IGNORE_FILE_NAME = ".gitignore";

	private static final String _GIT_IGNORE_TEMP_FILE_NAME = ".gitignore.temp";

	private static final Logger _logger = Logging.getLogger(FileUtil.class);

	private static final List<String> _excludedDirNames = Arrays.asList(
		"bin", "build", "classes", "node_modules", "node_modules_cache",
		"test-classes", "tmp");

	private static class FileComparator implements Comparator<File> {

		@Override
		public int compare(File file1, File file2) {
			String canonicalPath1 = _getCanonicalPath(file1);
			String canonicalPath2 = _getCanonicalPath(file2);

			return canonicalPath1.compareTo(canonicalPath2);
		}

	}

}