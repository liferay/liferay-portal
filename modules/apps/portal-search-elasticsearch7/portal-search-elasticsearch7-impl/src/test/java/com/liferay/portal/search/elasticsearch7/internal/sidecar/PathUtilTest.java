/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class PathUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws IOException {
		_toPath = Files.createTempDirectory("temp_dir");
	}

	@After
	public void tearDown() {
		PathUtil.deleteDir(_toPath);
	}

	@Test
	public void testCopyDirectory() throws Exception {
		Path fromPath = _getResourcePath("root");

		PathUtil.copyDirectory(
			fromPath, _toPath,
			dir -> Objects.equals(
				String.valueOf(dir.getFileName()), "excluded"));

		_assertExists(_toPath, "directory1/file1.txt");
		_assertExists(_toPath, "directory2/file2.txt");
		_assertDoesNotExist(_toPath, "excluded/excluded.txt");
	}

	private void _assertDoesNotExist(Path path, String name) {
		Path fullPath = path.resolve(name);

		Assert.assertFalse(Files.exists(fullPath));
	}

	private void _assertExists(Path path, String name) {
		Path fullPath = path.resolve(name);

		Assert.assertTrue(Files.exists(fullPath));
	}

	private Path _getResourcePath(String name) throws Exception {
		Class<? extends PathUtilTest> clazz = getClass();

		URL url = clazz.getResource(name);

		return Paths.get(url.toURI());
	}

	private Path _toPath;

}