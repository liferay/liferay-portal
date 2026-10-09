/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node.internal.util;

import com.liferay.gradle.util.Validator;
import com.liferay.gradle.util.hash.HashUtil;
import com.liferay.gradle.util.hash.HashValue;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gradle.api.GradleException;
import org.gradle.api.UncheckedIOException;

/**
 * @author Hugo Huijser
 * @author Andrea Di Giorgi
 */
public class DigestUtil {

	public static String getDigest(File digestFile) {
		if (!digestFile.exists()) {
			return null;
		}

		byte[] bytes = null;

		try {
			bytes = Files.readAllBytes(digestFile.toPath());
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}

		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static String getDigest(Iterable<File> files) {
		SortedSet<File> sortedFiles = null;

		try {
			sortedFiles = _flattenAndSort(files);
		}
		catch (IOException ioException) {
			throw new GradleException("Unable to flatten files", ioException);
		}

		StringBuilder sb = new StringBuilder();

		for (File file : sortedFiles) {
			if (!file.exists()) {
				continue;
			}

			try {
				List<String> lines = Files.readAllLines(
					file.toPath(), StandardCharsets.UTF_8);

				sb.append(Integer.toHexString(lines.hashCode()));
			}
			catch (IOException ioException) {
				HashValue hashValue = HashUtil.sha1(file);

				sb.append(hashValue.asHexString());
			}

			sb.append('-');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String getDigest(String... array) {
		StringBuilder sb = new StringBuilder();

		for (String s : array) {
			if (Validator.isNull(s)) {
				continue;
			}

			sb.append(Integer.toHexString(s.hashCode()));
			sb.append('-');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	private static SortedSet<File> _flattenAndSort(Iterable<File> files)
		throws IOException {

		final SortedSet<File> sortedFiles = new TreeSet<>(new FileComparator());

		if (files == null) {
			return sortedFiles;
		}

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

	private static class FileComparator implements Comparator<File> {

		@Override
		public int compare(File file1, File file2) {
			String canonicalPath1 = _getCanonicalPath(file1);
			String canonicalPath2 = _getCanonicalPath(file2);

			return canonicalPath1.compareTo(canonicalPath2);
		}

		private String _getCanonicalPath(File file) {
			String canonicalPath = null;

			try {
				canonicalPath = file.getCanonicalPath();
			}
			catch (IOException ioException) {
				String message = "Unable to get canonical path of " + file;

				throw new UncheckedIOException(message, ioException);
			}

			if (File.separatorChar != '/') {
				canonicalPath = canonicalPath.replace(File.separatorChar, '/');
			}

			return canonicalPath;
		}

	}

}