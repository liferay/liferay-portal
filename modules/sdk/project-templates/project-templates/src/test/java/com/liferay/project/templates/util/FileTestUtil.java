/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Andrea Di Giorgi
 */
public class FileTestUtil {

	public static final String PROJECT_TEMPLATE_BUNDLE_NAME_PREFIX =
		"Liferay Project Templates";

	public static final String PROJECT_TEMPLATE_DIR_PREFIX =
		"project-templates-";

	public static boolean containsFile(Path rootDirPath, String pattern)
		throws IOException {

		Path path = getFile(rootDirPath, pattern);

		if (path != null) {
			return true;
		}

		return false;
	}

	public static String getExtension(String fileName) {
		int pos = fileName.lastIndexOf('.');

		if (pos == -1) {
			return "";
		}

		return fileName.substring(pos + 1);
	}

	public static Path getFile(Path rootDirPath, String pattern)
		throws IOException {

		return getFile(rootDirPath, pattern, Integer.MAX_VALUE);
	}

	public static Path getFile(Path rootDirPath, String pattern, int maxDepth)
		throws IOException {

		final AtomicReference<Path> foundPath = new AtomicReference<>();

		FileSystem fileSystem = rootDirPath.getFileSystem();

		final PathMatcher pathMatcher = fileSystem.getPathMatcher(
			"glob:" + pattern);

		Files.walkFileTree(
			rootDirPath, Collections.singleton(FileVisitOption.FOLLOW_LINKS),
			maxDepth,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					if (pathMatcher.matches(path.getFileName())) {
						foundPath.set(path);

						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return foundPath.get();
	}

	public static DirectoryStream<Path> getProjectTemplatesDirectoryStream()
		throws IOException {

		return Files.newDirectoryStream(
			Paths.get("../"),
			new DirectoryStream.Filter<Path>() {

				@Override
				public boolean accept(Path path) throws IOException {
					if (!Files.isDirectory(path)) {
						return false;
					}

					String fileName = String.valueOf(path.getFileName());

					if (fileName.startsWith(PROJECT_TEMPLATE_DIR_PREFIX) &&
						!fileName.endsWith("-extensions")) {

						return true;
					}

					return false;
				}

			});
	}

	public static String read(ClassLoader classLoader, String name)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(classLoader.getResourceAsStream(name)))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (sb.length() > 0) {
					sb.append('\n');
				}

				sb.append(line);
			}
		}

		return sb.toString();
	}

	public static String read(String name) throws IOException {
		return read(FileTestUtil.class.getClassLoader(), name);
	}

	public static byte[] readAllBytes(String resource) throws IOException {
		ClassLoader classLoader = FileTestUtil.class.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(resource);

		byte[] buffer = new byte[0xFFFF];

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		for (int length = inputStream.read(buffer); length != -1;
			 length = inputStream.read(buffer)) {

			byteArrayOutputStream.write(buffer, 0, length);
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static List<String> readAllLines(
			ClassLoader classLoader, String resource)
		throws IOException {

		List<String> allLines = new ArrayList<>();

		try (Scanner scanner = new Scanner(
				classLoader.getResourceAsStream(resource))) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line == null) {
					continue;
				}

				allLines.add(line);
			}
		}

		return allLines;
	}

	public static List<String> readAllLines(String resource)
		throws IOException {

		return readAllLines(FileTestUtil.class.getClassLoader(), resource);
	}

	public static Properties readProperties(File file) throws IOException {
		try (InputStream inputStream = Files.newInputStream(file.toPath())) {
			Properties properties = new Properties();

			properties.load(inputStream);

			return properties;
		}
	}

	public static Properties readProperties(String name) throws IOException {
		Properties properties = new Properties();

		ClassLoader classLoader = FileTestUtil.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
			properties.load(inputStream);
		}

		return properties;
	}

	public static void write(Writer writer, String... lines)
		throws IOException {

		for (String line : lines) {
			writer.write(line);
			writer.write(System.lineSeparator());
		}
	}

}