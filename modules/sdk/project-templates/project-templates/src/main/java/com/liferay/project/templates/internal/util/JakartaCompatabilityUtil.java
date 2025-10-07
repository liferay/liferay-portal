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

						if (fileName.endsWith("pom.xml")) {
							_updateMavenDependencies(path);
						}

						if (fileName.endsWith(".tld")) {
							_updateTLDTaglibTag(path);
						}

						if (fileName.endsWith(".xml")) {
							_updateXMLWebappTag(path);
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

	private static void _updateDependencies(
			Path filePath, String searchPattern, String replacementPattern)
		throws IOException {

		String content = FileUtil.read(filePath);

		for (Map.Entry<Object, Object> entry :
				_jakartaDependenciesProperties.entrySet()) {

			String key = String.valueOf(entry.getKey());

			String[] groupAndName = key.split(_DELIMITER_UNDERLINE);

			Pattern pattern = Pattern.compile(
				String.format(searchPattern, groupAndName[0], groupAndName[1]));

			Matcher matcher = pattern.matcher(content);

			if (matcher.find()) {
				String value = String.valueOf(entry.getValue());

				String[] groupAndNameAndVersion = value.split(_DELIMITER_COLON);

				content = matcher.replaceAll(
					String.format(
						replacementPattern, groupAndNameAndVersion[0],
						groupAndNameAndVersion[1], groupAndNameAndVersion[2]));
			}
		}

		Files.writeString(filePath, content);
	}

	private static void _updateGradleDependencies(Path gradleFilePath)
		throws IOException {

		_updateDependencies(
			gradleFilePath, _GRADLE_GAV_PATTERN_WITH_OPTIONAL_VERSION,
			_GRADLE_GAV_PATTERN);
	}

	private static void _updateMavenDependencies(Path pomFilePath)
		throws IOException {

		_updateDependencies(
			pomFilePath, _POM_GAV_PATTERN_WITH_OPTIONAL_VERSION,
			_POM_GAV_PATTERN);
	}

	private static void _updateTLDTaglibTag(Path tldFile) throws IOException {
		String content = FileUtil.read(tldFile);

		Matcher matcher = _tldTaglibTagPattern.matcher(content);

		if (matcher.find()) {
			content = matcher.replaceAll(_TLD_TAGLIB_TAG_NEW);
		}

		Files.writeString(tldFile, content);
	}

	private static void _updateXMLWebappTag(Path xmlFile) throws IOException {
		String content = FileUtil.read(xmlFile);

		Matcher matcher = _xmlWebappTagPattern.matcher(content);

		if (matcher.find()) {
			content = matcher.replaceAll(_XML_WEBAPP_TAG_NEW);
		}

		Files.writeString(xmlFile, content);
	}

	private static final String _DELIMITER_COLON = ":";

	private static final String _DELIMITER_UNDERLINE = "_";

	private static final String _GRADLE_GAV_PATTERN =
		"group: \"%s\", name: \"%s\", version: \"%s\"";

	private static final String _GRADLE_GAV_PATTERN_WITH_OPTIONAL_VERSION =
		"group: \"%s\", name: \"%s\"(, version: \".*\")?";

	private static final String _IMPORT_PACKAGE_NEW = "jakarta";

	private static final String _IMPORT_PACKAGE_OLD = "javax";

	private static final String _JAKARTA_DEPENDENCIES_PROPERTIES_FILE_PATH =
		"jakarta-dependencies/jakarta-dependencies.properties";

	private static final String _POM_GAV_PATTERN =
		"<groupId>%s</groupId>\n\t\t\t<artifactId>%s</artifactId>\n\t\t\t" +
			"<version>%s</version>";

	private static final String _POM_GAV_PATTERN_WITH_OPTIONAL_VERSION =
		"<groupId>%s</groupId>\\n\\s*<artifactId>%s</artifactId>" +
			"(\\n\\s*<version>.*</version>)?";

	private static final String _TAGLIB_URL_NEW = "jakarta.tags.core";

	private static final String _TAGLIB_URL_OLD =
		"http://java.sun.com/jsp/jstl/core";

	private static final String _TLD_TAGLIB_TAG_NEW =
		"<taglib\n\tversion=\"3.0\"\n\txmlns=" +
			"\"https://jakarta.ee/xml/ns/jakartaee\"\n\txmlns:xsi=" +
				"\"http://www.w3.org/2001/XMLSchema-instance\"\n\txsi:" +
					"schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee " +
						"https://jakarta.ee/xml/ns/jakartaee" +
							"/web-jsptaglibrary_3_0.xsd\"\n>";

	private static final String _XML_WEBAPP_TAG_NEW =
		"<web-app version=\"6.0\" " +
			"xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
					"xsi:schemaLocation=\"" +
						"https://jakarta.ee/xml/ns/jakartaee " +
							"https://jakarta.ee/xml/ns/jakartaee" +
								"/web-app_6_0.xsd\">";

	private static final Properties _jakartaDependenciesProperties;
	private static final Pattern _tldTaglibTagPattern = Pattern.compile(
		"<taglib\\n.*\\n\\t" +
			"xmlns=\"http://java.sun.com/xml/ns/javaee\"\\n.*\\n.*\\n>");
	private static final Pattern _xmlWebappTagPattern = Pattern.compile(
		"^<web-app.*xmlns=\".*/javaee\".*>$", Pattern.MULTILINE);

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