/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Tina Tian
 * @author André de Oliveira
 */
public class PathUtil {

	public static void copyDirectory(Path fromPath, Path toPath)
		throws IOException {

		copyDirectory(fromPath, toPath, null);
	}

	public static void copyDirectory(
			Path fromPath, Path toPath,
			Function<Path, Boolean> excludeDirFunction)
		throws IOException {

		if (Files.exists(toPath)) {
			deleteDir(toPath);
		}

		final Map<Path, FileTime> fileTimes = new HashMap<>();

		try {
			Files.walkFileTree(
				fromPath,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult postVisitDirectory(
							Path dir, IOException ioException)
						throws IOException {

						FileTime fileTime = fileTimes.remove(dir);

						if (fileTime == null) {
							return FileVisitResult.CONTINUE;
						}

						Files.setLastModifiedTime(
							toPath.resolve(fromPath.relativize(dir)), fileTime);

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult preVisitDirectory(
							Path dir, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if ((excludeDirFunction != null) &&
							excludeDirFunction.apply(dir)) {

							return FileVisitResult.CONTINUE;
						}

						Files.copy(
							dir, toPath.resolve(fromPath.relativize(dir)),
							StandardCopyOption.COPY_ATTRIBUTES,
							StandardCopyOption.REPLACE_EXISTING);

						fileTimes.put(dir, Files.getLastModifiedTime(dir));

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes basicFileAttributes)
						throws IOException {

						if ((excludeDirFunction != null) &&
							excludeDirFunction.apply(file.getParent())) {

							return FileVisitResult.CONTINUE;
						}

						Path toFile = toPath.resolve(fromPath.relativize(file));

						Files.copy(
							file, toFile, StandardCopyOption.COPY_ATTRIBUTES,
							StandardCopyOption.REPLACE_EXISTING);

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			deleteDir(toPath);

			throw ioException;
		}
	}

	public static void deleteDir(Path dirPath) {
		if (dirPath == null) {
			return;
		}

		try {
			Files.walkFileTree(
				dirPath,
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult postVisitDirectory(
							Path path, IOException ioException)
						throws IOException {

						if (ioException != null) {
							throw ioException;
						}

						Files.delete(path);

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path path, BasicFileAttributes basicFileAttributes)
						throws IOException {

						Files.delete(path);

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(
							Path path, IOException ioException)
						throws IOException {

						if (ioException instanceof NoSuchFileException) {
							return FileVisitResult.CONTINUE;
						}

						throw ioException;
					}

				});
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete " + dirPath, ioException);
			}
		}
	}

	public static void download(URL url, Path path) throws IOException {
		try (InputStream inputStream = url.openStream()) {
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(PathUtil.class);

}