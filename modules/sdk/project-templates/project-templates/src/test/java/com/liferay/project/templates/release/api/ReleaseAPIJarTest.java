/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.release.api;

import com.liferay.project.templates.util.FileTestUtil;
import com.liferay.project.templates.util.ZipUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Simon Jiang
 * @author Lawrence Lee
 */
public class ReleaseAPIJarTest implements BaseReleaseAPIJarTestCase {

	public static final String RELEASE_API_JAR_SOURCES_FILE =
		System.getProperty("releaseApiJarSourcesFile");

	public List<String> getIgnoreStrings(String jarName) throws Exception {
		List<String> ignoredStrings = FileTestUtil.readAllLines(
			"com/liferay/project/templates/release/api/dependencies" +
				"/api-jar-ignore-paths.txt");

		if (jarName.contains("7.0")) {
			ignoredStrings.addAll(
				FileTestUtil.readAllLines(
					"com/liferay/project/templates/release/api/dependencies" +
						"/api-jar-ignore-paths_7_0.txt"));
		}
		else if (jarName.contains("7.1")) {
			ignoredStrings.addAll(
				FileTestUtil.readAllLines(
					"com/liferay/project/templates/release/api/dependencies" +
						"/api-jar-ignore-paths_7_1.txt"));
		}
		else if (jarName.contains("7.2")) {
			ignoredStrings.addAll(
				FileTestUtil.readAllLines(
					"com/liferay/project/templates/release/api/dependencies" +
						"/api-jar-ignore-paths_7_2.txt"));
		}

		return ignoredStrings;
	}

	public Path getSourcesDirPath() throws IOException {
		File releaseApiJarSourcesFile = new File(RELEASE_API_JAR_SOURCES_FILE);

		Assert.assertTrue(releaseApiJarSourcesFile.exists());

		File sourcesDir = temporaryFolder.newFolder(
			releaseApiJarSourcesFile.getName() + "-sources");

		ZipUtil.unzip(releaseApiJarSourcesFile, sourcesDir);

		return sourcesDir.toPath();
	}

	@Test
	public void testReleaseApiJar() throws Exception {
		Assume.assumeNotNull(RELEASE_API_JAR_FILE);

		File releaseApiJarFile = new File(RELEASE_API_JAR_FILE);

		Path classesDirPath = getClassesDirPath(temporaryFolder);

		Path sourcesDirPath = getSourcesDirPath();

		Set<String> classFileStrings = getPaths(classesDirPath, ".class");

		Assert.assertFalse(classFileStrings.isEmpty());

		Set<String> sourceFileStrings = getPaths(sourcesDirPath, ".java");

		Assert.assertFalse(sourceFileStrings.isEmpty());

		List<String> missingClassFileStrings = new ArrayList<>();

		List<String> ignoreStrings = getIgnoreStrings(
			releaseApiJarFile.getName());

		for (String classFileString : classFileStrings) {
			if (classFileString.contains("$") ||
				ignoreStrings.contains(classFileString) ||
				sourceFileStrings.contains(classFileString)) {

				continue;
			}

			missingClassFileStrings.add(classFileString);
		}

		Assert.assertTrue(
			"Sources jar missing: " + getFileNames(missingClassFileStrings),
			missingClassFileStrings.isEmpty());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

}