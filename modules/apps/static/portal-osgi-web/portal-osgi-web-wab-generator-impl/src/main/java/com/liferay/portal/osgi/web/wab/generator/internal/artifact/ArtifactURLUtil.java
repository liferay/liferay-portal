/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.generator.internal.artifact;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Constants;

/**
 * @author Matthew Tambara
 * @author Raymond Augé
 * @author Gregory Amerson
 */
public class ArtifactURLUtil {

	public static String getClientExtensionSymbolicName(String path) {
		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(CharPool.PERIOD);

		return path.substring(x + 1, y);
	}

	public static String getSymbolicName(String path) {
		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(CharPool.PERIOD);

		String symbolicName = path.substring(x + 1, y);

		Matcher matcher = _pattern.matcher(symbolicName);

		if (matcher.matches()) {
			symbolicName = matcher.group(1);
		}

		return symbolicName;
	}

	public static URL transform(URL artifact) throws Exception {
		String contextName = null;

		String path = artifact.getPath();

		String fileExtension = path.substring(
			path.lastIndexOf(CharPool.PERIOD) + 1);

		if (fileExtension.equals("war")) {
			try (ZipFile zipFile = new ZipFile(new File(artifact.toURI()))) {
				contextName = _readServletContextName(zipFile);
			}
		}

		String symbolicName = getSymbolicName(path);

		if (fileExtension.equals("zip")) {
			JSONObject jsonObject = _getClientExtensionConfigJSONObject(path);

			if (jsonObject != null) {
				symbolicName = getClientExtensionSymbolicName(path);
				contextName = _getWebContextPath(jsonObject);
			}
		}

		if (contextName == null) {
			contextName = symbolicName;
		}

		return new URL(
			"webbundle", null,
			StringBundler.concat(
				artifact.getPath(), "?", Constants.BUNDLE_SYMBOLICNAME, "=",
				symbolicName, "&Web-ContextPath=/", contextName,
				"&fileExtension=", fileExtension, "&protocol=file"));
	}

	private static JSONObject _getClientExtensionConfigJSONObject(String path) {
		try (ZipFile zipFile = new ZipFile(path)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if ((name.indexOf(CharPool.SLASH) != -1) ||
					!name.endsWith(".client-extension-config.json")) {

					continue;
				}

				try (InputStream inputStream = zipFile.getInputStream(
						zipEntry)) {

					return JSONFactoryUtil.createJSONObject(
						StringUtil.read(inputStream));
				}
				catch (JSONException jsonException) {
					_log.error(
						"Unable to parse client extension config in " + path,
						jsonException);

					return null;
				}
			}
		}
		catch (Exception exception) {
			_log.error("Path " + path + " is not a valid ZIP", exception);
		}

		return null;
	}

	private static String _getWebContextPath(JSONObject jsonObject) {
		for (String key : jsonObject.keySet()) {
			JSONObject configurationJSONObject = jsonObject.getJSONObject(key);

			if (configurationJSONObject == null) {
				continue;
			}

			String webContextPath = configurationJSONObject.getString(
				"webContextPath");

			if (Validator.isNull(webContextPath)) {
				continue;
			}

			if (webContextPath.startsWith("/")) {
				return webContextPath.substring(1);
			}

			return webContextPath;
		}

		return null;
	}

	private static String _readServletContextName(ZipFile zipFile)
		throws Exception {

		ZipEntry zipEntry = zipFile.getEntry(
			"WEB-INF/liferay-plugin-package.properties");

		if (zipEntry == null) {
			return null;
		}

		Properties properties = new Properties();

		try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
			properties.load(inputStream);
		}

		return properties.getProperty("servlet-context-name");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArtifactURLUtil.class);

	private static final Pattern _pattern = Pattern.compile(
		"(.*?)(-[0-9\\.]+)");

}