/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bootstrap;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.InputStream;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import org.eclipse.osgi.container.ModuleReadHook;

/**
 * @author Matthew Tambara
 */
public class ModuleReadHookImpl implements ModuleReadHook {

	@Override
	public void process(long bundleId, String location) {
		if ((bundleId == 0) || location.startsWith("webbundle")) {
			return;
		}

		Path bundleRevPath = Paths.get(
			PropsValues.MODULE_FRAMEWORK_STATE_DIR, "org.eclipse.osgi",
			String.valueOf(bundleId), "0");

		if (Files.notExists(bundleRevPath)) {
			return;
		}

		Path path = bundleRevPath.resolve("bundleFile");

		if (Files.exists(path)) {
			return;
		}

		try {
			if (location.startsWith("file")) {
				String jarLocation = _normalizePath(location);

				if (jarLocation.startsWith(
						_normalizePath(
							PropsValues.MODULE_FRAMEWORK_BASE_DIR))) {

					int index = location.indexOf(CharPool.QUESTION);

					if (index != -1) {
						location = location.substring(0, index);
					}

					Files.copy(Paths.get(new URI(location)), path);
				}
			}
			else {
				Matcher matcher = _pattern.matcher(location);

				if (matcher.find()) {
					try (ZipFile zipFile = new ZipFile(
							_normalizePath(matcher.group(2)));
						InputStream inputStream = zipFile.getInputStream(
							zipFile.getEntry(matcher.group(1)))) {

						Files.copy(inputStream, path);
					}
				}
				else if (_log.isWarnEnabled()) {
					_log.warn("No bundle found for location " + location);
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to copy from ", location, " to ", path),
				exception);
		}
	}

	private String _normalizePath(String location) {
		try {
			int index = location.indexOf(CharPool.QUESTION);

			if (index != -1) {
				location = location.substring(0, index);
			}

			URI uri = null;

			Path path = null;

			try {
				uri = new URI(location);

				uri = uri.normalize();

				path = Paths.get(uri);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				path = Paths.get(location);
			}

			uri = path.toUri();

			return uri.getPath();
		}
		catch (Exception exception) {
			throw new IllegalArgumentException(
				"Unable to parse location " + location, exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ModuleReadHookImpl.class);

	private static final Pattern _pattern = Pattern.compile(
		"(.*\\.jar).*lpkgPath=([^&]*)");

}