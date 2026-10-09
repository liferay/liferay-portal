/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class FileUtil {

	public static void copyDirectory(File sourceDir, File destinationDir)
		throws IOException {

		FileUtils.copyDirectory(sourceDir, destinationDir);
	}

	public static void copyDirectory(
			String sourceDirName, String destinationDirName)
		throws IOException {

		copyDirectory(new File(sourceDirName), new File(destinationDirName));
	}

	public static File copyFileFromResource(
			String resourceName, String targetFilePath)
		throws IOException {

		ClassLoader classLoader = FileUtil.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				resourceName)) {

			File file = new File(targetFilePath);

			file.mkdirs();

			Files.copy(
				inputStream, file.toPath(),
				StandardCopyOption.REPLACE_EXISTING);

			return file;
		}
	}

	public static boolean delete(File file) {
		return FileUtils.deleteQuietly(file);
	}

	public static boolean delete(String filePath) throws IOException {
		File file = new File(filePath);

		return FileUtils.deleteQuietly(file);
	}

	public static boolean exists(File file) {
		return file.exists();
	}

	public static boolean exists(String fileName) {
		File file = new File(fileName);

		return exists(file);
	}

	public static String fixFilePath(String filePath) {
		if (OSDetector.isWindows()) {
			return StringUtil.replace(filePath, "/", "\\");
		}

		return filePath;
	}

	public static String getCanonicalPath(String filePath) {
		try {
			File file = new File(filePath);

			return file.getCanonicalPath();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return filePath;
	}

	public static String getFileName(String filePath) {
		Path path = Paths.get(filePath);

		return String.valueOf(path.getFileName());
	}

	public static List<URL> getIncludedResourceURLs(
			FileSystem fileSystem, String[] includes, String baseDirName)
		throws IOException {

		final List<PathMatcher> pathMatchers = new ArrayList<>();

		for (String include : includes) {
			pathMatchers.add(fileSystem.getPathMatcher("glob:" + include));
		}

		final List<URL> filePaths = new ArrayList<>();

		if (Validator.isNull(baseDirName)) {
			return filePaths;
		}

		Path path = fileSystem.getPath(baseDirName);

		if (!Files.exists(path)) {
			System.out.println("Directory " + baseDirName + " does not exist.");

			return filePaths;
		}

		Files.walkFileTree(
			path,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					for (PathMatcher pathMatcher : pathMatchers) {
						URI uri = filePath.toUri();

						if (pathMatcher.matches(filePath)) {
							filePaths.add(uri.toURL());

							break;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return filePaths;
	}

	public static List<URL> getIncludedResourceURLs(
			String[] includes, String baseDirName)
		throws IOException {

		return getIncludedResourceURLs(
			FileSystems.getDefault(), includes, baseDirName);
	}

	public static String getSeparator() {
		return File.separator;
	}

	public static URL getURL(File file) throws MalformedURLException {
		URI uri = file.toURI();

		return uri.toURL();
	}

	public static List<String> listFiles(String filePath) {
		List<String> fileNames = new ArrayList<>();

		File baseDir = new File(filePath);

		if (baseDir.isDirectory()) {
			File[] files = baseDir.listFiles();

			for (File file : files) {
				if (!file.isDirectory() && !file.isFile()) {
					continue;
				}

				fileNames.add(file.getName());
			}
		}

		return fileNames;
	}

	public static void move(String sourceFilePath, String newFilePath)
		throws IOException {

		FileUtils.moveFile(new File(sourceFilePath), new File(newFilePath));
	}

	public static String read(File file) throws IOException {
		return read(getURL(file));
	}

	public static String read(String fileName) throws IOException {
		File file = new File(fileName);

		return read(file);
	}

	public static String read(URL url) throws IOException {
		StringBuilder sb = new StringBuilder();

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(url.openStream()));

		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}

		if (sb.length() != 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	public static void replaceStringInFile(
			String filePath, String oldSub, String newSub)
		throws IOException {

		String fileContent = read(filePath);

		write(filePath, StringUtil.replace(fileContent, oldSub, newSub));
	}

	public static void write(File file, byte[] bytes) throws IOException {
		FileUtils.writeByteArrayToFile(file, bytes);
	}

	public static void write(File file, String string) throws IOException {
		FileUtils.writeStringToFile(file, string, "UTF-8");
	}

	public static void write(String fileName, byte[] bytes) throws IOException {
		File file = new File(fileName);

		write(file, bytes);
	}

	public static void write(String fileName, String string)
		throws IOException {

		File file = new File(fileName);

		write(file, string);
	}

}