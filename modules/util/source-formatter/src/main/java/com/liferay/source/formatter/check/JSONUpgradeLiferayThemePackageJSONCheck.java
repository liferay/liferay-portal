/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Nícolas Moura
 */
public class JSONUpgradeLiferayThemePackageJSONCheck extends BaseUpgradeCheck {

	public static void setTestMode(boolean testMode) {
		_testMode = testMode;
	}

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith("package.json") ||
			!content.contains("\"liferayTheme\"")) {

			return content;
		}

		JSONObject jsonObject = new JSONObjectImpl(content);

		_formatDevDependencies(jsonObject);
		_formatScripts(jsonObject);
		_formatThemeVersion(jsonObject);

		return JSONUtil.toString(jsonObject);
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"json"};
	}

	private void _formatDevDependencies(JSONObject jsonObject) {
		Map<String, String> devDependenciesLatestVersions =
			_getDevDependenciesLatestVersions();

		if (devDependenciesLatestVersions.isEmpty()) {
			return;
		}

		JSONObject devDependenciesJSONObject = jsonObject.getJSONObject(
			"devDependencies");

		if (devDependenciesJSONObject == null) {
			return;
		}

		for (String packageName : _DEV_DEPENDENCIES_PACKAGES_NAMES) {
			String currentVersion = devDependenciesJSONObject.getString(
				packageName);

			if (currentVersion.equals("") &&
				packageName.equals("liferay-font-awesome")) {

				continue;
			}

			String latestVersion = devDependenciesLatestVersions.get(
				packageName);

			if (currentVersion.equals(latestVersion)) {
				continue;
			}

			if (packageName.equals("liferay-theme-tasks")) {
				devDependenciesJSONObject.put(
					packageName, StringPool.CARET + latestVersion);
			}
			else {
				devDependenciesJSONObject.put(packageName, latestVersion);
			}
		}
	}

	private void _formatScripts(JSONObject jsonObject) {
		JSONObject scriptsJSONObject = jsonObject.getJSONObject("scripts");

		if (scriptsJSONObject == null) {
			return;
		}

		scriptsJSONObject.put(
			"build", "gulp build"
		).put(
			"deploy", "gulp deploy"
		).put(
			"extend", "gulp extend"
		).put(
			"init", "gulp init"
		).put(
			"kickstart", "gulp kickstart"
		).put(
			"status", "gulp status"
		).put(
			"upgrade", "gulp upgrade"
		).put(
			"watch", "gulp watch"
		);
	}

	private void _formatThemeVersion(JSONObject jsonObject) {
		JSONObject liferayThemeJSONObject = jsonObject.getJSONObject(
			"liferayTheme");

		liferayThemeJSONObject.put("version", "7.4");
	}

	private Map<String, String> _getDevDependenciesLatestVersions() {
		Map<String, String> latestVersions = new HashMap<>();

		for (String packageName : _DEV_DEPENDENCIES_PACKAGES_NAMES) {
			try {
				URL url = new URL("https://registry.npmjs.com/" + packageName);

				HttpURLConnection httpURLConnection =
					(HttpURLConnection)url.openConnection();

				httpURLConnection.setConnectTimeout(10000);
				httpURLConnection.setReadTimeout(10000);
				httpURLConnection.setRequestMethod(HttpMethods.GET);

				String content = StringUtil.read(
					httpURLConnection.getInputStream());

				if (Objects.equals(content, StringPool.BLANK)) {
					continue;
				}

				JSONObject jsonObject = new JSONObjectImpl(content);

				if (_testMode) {
					jsonObject = new JSONObjectImpl(
						"{\"versions\": {\"2.0.0\": {}}}");
				}

				String latestVersion = _getLatestVersion(jsonObject);

				if (latestVersion.equals("")) {
					continue;
				}

				latestVersions.put(packageName, latestVersion);
			}
			catch (Exception exception) {
				_log.error(exception);

				return null;
			}
		}

		return latestVersions;
	}

	private String _getLatestVersion(JSONObject jsonObject) {
		JSONObject versionsJSONObject = jsonObject.getJSONObject("versions");

		if (versionsJSONObject == null) {
			return null;
		}

		List<Version> versions = new ArrayList<>();

		Set<String> versionsSet = versionsJSONObject.keySet();

		for (String version : versionsSet) {
			versions.add(Version.parseVersion(version));
		}

		Version latestVersion = Collections.max(versions);

		return latestVersion.toString();
	}

	private static final String[] _DEV_DEPENDENCIES_PACKAGES_NAMES = {
		"compass-mixins", "gulp", "liferay-frontend-css-common",
		"liferay-frontend-theme-styled", "liferay-frontend-theme-unstyled",
		"liferay-theme-tasks", "liferay-font-awesome"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JSONUpgradeLiferayThemePackageJSONCheck.class);

	private static boolean _testMode;

}