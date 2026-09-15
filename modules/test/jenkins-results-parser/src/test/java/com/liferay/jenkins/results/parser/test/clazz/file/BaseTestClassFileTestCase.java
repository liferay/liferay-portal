/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.Test;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import org.junit.After;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClassFileTestCase extends Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		for (File dir : _dirs) {
			_delete(dir);
		}

		_dirs.clear();
	}

	protected File createDir() throws Exception {
		Path path = Files.createTempDirectory(RandomTestUtil.randomString());

		File dir = path.toFile();

		_dirs.add(dir);

		return dir;
	}

	protected File createProjectDir(String testScript) throws Exception {
		File dir = createDir();

		File projectDir = new File(dir, "project");

		projectDir.mkdirs();

		JSONObject jsonObject = new JSONObject();

		JSONObject scriptsJSONObject = new JSONObject();

		scriptsJSONObject.put("test", testScript);

		jsonObject.put("scripts", scriptsJSONObject);

		write(jsonObject.toString(), projectDir, "package.json");

		return projectDir;
	}

	protected void write(String content, File dir, String name)
		throws Exception {

		File file = new File(dir, name);

		File parentDir = file.getParentFile();

		parentDir.mkdirs();

		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	private void _delete(File file) {
		File[] childFiles = file.listFiles();

		if (childFiles != null) {
			for (File childFile : childFiles) {
				_delete(childFile);
			}
		}

		file.delete();
	}

	private final List<File> _dirs = new ArrayList<>();

}