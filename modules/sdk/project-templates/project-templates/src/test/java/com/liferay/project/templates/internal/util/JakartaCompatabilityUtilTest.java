/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.internal.util;

import com.liferay.project.templates.extensions.util.FileUtil;

import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Brian Greenwald
 */
public class JakartaCompatabilityUtilTest {

	@Before
	public void setUp() throws Exception {
		Path sourceTemplatesDir = Paths.get(
			"src/test/resources/com/liferay/project/templates/internal/util" +
				"/templates/dependencies");

		_tempDir = Files.createTempDirectory("jakarta-test");

		_expectedFilesDir = sourceTemplatesDir.resolve("expected");

		Files.walkFileTree(
			sourceTemplatesDir,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (path.equals(_expectedFilesDir)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Files.copy(
						path, _tempDir.resolve(path.getFileName()),
						StandardCopyOption.REPLACE_EXISTING);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@After
	public void tearDown() throws Exception {
		FileUtil.deleteDir(_tempDir);
	}

	@Test
	public void testUpdateForJakarta() throws Exception {
		JakartaCompatabilityUtil.updateForJakarta(_tempDir.toFile());

		Files.walkFileTree(
			_tempDir,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Assert.assertEquals(
						"File content does not match for " + path.getFileName(),
						Files.readString(
							_expectedFilesDir.resolve(path.getFileName())),
						Files.readString(path));

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private Path _expectedFilesDir;
	private Path _tempDir;

}