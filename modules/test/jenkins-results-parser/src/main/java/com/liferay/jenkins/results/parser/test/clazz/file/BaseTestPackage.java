/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestPackage implements TestPackage {

	@Override
	public String getName() {
		String name = _packageJSONObject.optString("name", null);

		if (name != null) {
			return name;
		}

		File dir = _packageJSONFile.getParentFile();

		return dir.getName();
	}

	@Override
	public File getPackageJSONFile() {
		return _packageJSONFile;
	}

	@Override
	public JSONObject getPackageJSONObject() {
		return _packageJSONObject;
	}

	@Override
	public String getTestScript() {
		JSONObject scriptsJSONObject = _packageJSONObject.optJSONObject(
			"scripts");

		if (scriptsJSONObject == null) {
			return null;
		}

		return scriptsJSONObject.optString("test", null);
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.getCanonicalPath(_packageJSONFile);
	}

	protected BaseTestPackage(File packageJSONFile) throws IOException {
		_packageJSONFile = packageJSONFile;

		_packageJSONObject = new JSONObject(
			JenkinsResultsParserUtil.read(packageJSONFile));
	}

	protected File getProjectDir() {
		return _packageJSONFile.getParentFile();
	}

	private final File _packageJSONFile;
	private final JSONObject _packageJSONObject;

}