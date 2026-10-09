/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Matthew Tambara
 */
public class ConfigurationEnvBuilderTest extends ConfigurationEnvBuilder {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_fileSystem = FileSystems.getDefault();
	}

	@Test
	public void testBuildContent() throws IOException {
		List<String> configurationJavaFileNames = new ArrayList<>();

		Path modulesDirPath = Paths.get("modules");

		PathMatcher excludePathMatcher = _getPathMatcher("glob:**/*-test/**");
		PathMatcher includePathMatcher = _getPathMatcher(
			"glob:**/apps/**/configuration{,/**}/*Configuration.java");

		Files.walkFileTree(
			modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (includePathMatcher.matches(path) &&
						!excludePathMatcher.matches(path)) {

						configurationJavaFileNames.add(path.toString());
					}

					return FileVisitResult.CONTINUE;
				}

			});

		List<String> expectedList = StringUtil.split(
			buildContent(
				getObjectDefs(
					Paths.get("."),
					configurationJavaFileNames.toArray(new String[0]))),
			CharPool.NEW_LINE);

		List<String> actualList = _readPortalOSGiConfigurationProperties();

		Collections.sort(actualList);

		Collections.sort(expectedList);

		actualList = _formatList(actualList);

		expectedList = _formatList(expectedList);

		List<String> leftoverList = new ArrayList<>();

		for (String line : actualList) {
			if (expectedList.remove(line)) {
				continue;
			}

			leftoverList.add(line);
		}

		Assert.assertTrue(
			StringBundler.concat(_MESSAGE, " Leftover: ", leftoverList),
			leftoverList.isEmpty());

		Assert.assertTrue(
			StringBundler.concat(_MESSAGE, " Missing: ", expectedList),
			expectedList.isEmpty());
	}

	private List<String> _formatList(List<String> lines) {
		return TransformUtil.transform(
			lines,
			line -> {
				if (!line.contains("configuration.override")) {
					return null;
				}

				line =
					com.liferay.portal.kernel.util.StringUtil.removeSubstring(
						line, StringPool.POUND);

				return line.trim();
			});
	}

	private PathMatcher _getPathMatcher(String pattern) {
		String separator = _fileSystem.getSeparator();

		return _fileSystem.getPathMatcher(
			StringUtil.replace(pattern, CharPool.SLASH, separator.charAt(0)));
	}

	private List<String> _readPortalOSGiConfigurationProperties()
		throws IOException {

		Path path = Paths.get(
			"portal-impl/src/portal-osgi-configuration.properties");

		List<String> lines = Files.readAllLines(path);

		boolean skip = true;

		List<String> result = new ArrayList<>();

		result.add("##");

		for (String line : lines) {
			if (skip) {
				if (line.equals("## OSGi Configuration Overrides")) {
					skip = false;

					result.add(line);
				}
			}
			else if (!line.isEmpty()) {
				result.add(line);
			}
		}

		return result;
	}

	private static final String _MESSAGE =
		"Run \"ant update-portal-osgi-configuration-properties\".";

	private FileSystem _fileSystem;

}