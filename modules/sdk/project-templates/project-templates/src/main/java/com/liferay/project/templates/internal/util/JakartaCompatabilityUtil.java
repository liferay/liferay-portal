/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.internal.util;

import com.liferay.project.templates.extensions.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Greenwald
 */
public class JakartaCompatabilityUtil {

	public static void updateForJakarta(File destinationDir) throws Exception {
		Files.walkFileTree(
			destinationDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (basicFileAttributes.isRegularFile()) {
						File file = path.toFile();

						String fileName = file.getName();

						if (fileName.endsWith(".gradle")) {
							_updateGradleDependencies(path);
						}

						if (fileName.endsWith(".jsp")) {
							FileUtil.replaceString(
								file, _TAGLIB_URL_OLD, _TAGLIB_URL_NEW);
						}

						FileUtil.replaceString(
							file, _IMPORT_PACKAGE_OLD, _IMPORT_PACKAGE_NEW);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private static void _updateGradleDependencies(Path gradleFilePath)
		throws IOException {

		String content = FileUtil.read(gradleFilePath);

		for (Map.Entry<Object, Object> entry :
				_jakartaDependenciesProperties.entrySet()) {

			String key = String.valueOf(entry.getKey());

			String[] groupAndName = key.split(_DELIMITER_UNDERLINE);

			Pattern pattern = Pattern.compile(
				String.format(
					_GRADLE_GAV_PATTERN, groupAndName[0], groupAndName[1],
					".*"));

			Matcher matcher = pattern.matcher(content);

			if (matcher.find()) {
				String value = String.valueOf(entry.getValue());

				String[] groupAndNameAndVersion = value.split(_DELIMITER_COLON);

				content = matcher.replaceAll(
					String.format(
						_GRADLE_GAV_PATTERN, groupAndNameAndVersion[0],
						groupAndNameAndVersion[1], groupAndNameAndVersion[2]));
			}
		}

		Files.writeString(gradleFilePath, content);
	}

	private static final String _DELIMITER_COLON = ":";

	private static final String _DELIMITER_UNDERLINE = "_";

	private static final String _GRADLE_GAV_PATTERN =
		"group: \"%s\", name: \"%s\", version: \"%s\"";

	private static final String _IMPORT_PACKAGE_NEW = "jakarta";

	private static final String _IMPORT_PACKAGE_OLD = "javax";

	private static final String _JAKARTA_DEPENDENCIES_PROPERTIES_FILE_PATH =
		"jakarta-dependencies/jakarta-dependencies.properties";

	private static final String _TAGLIB_URL_NEW = "jakarta.tags.core";

	private static final String _TAGLIB_URL_OLD =
		"http://java.sun.com/jsp/jstl/core";

	private static final Properties _jakartaDependenciesProperties;

	static {
		_jakartaDependenciesProperties = new Properties();

		ClassLoader classLoader =
			JakartaCompatabilityUtil.class.getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(
				_JAKARTA_DEPENDENCIES_PROPERTIES_FILE_PATH)) {

			_jakartaDependenciesProperties.load(inputStream);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

}