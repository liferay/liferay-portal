/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.util;

import groovy.json.JsonSlurper;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Map;

import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * @author Drew Brokke
 */
public class ThemeUtil {

	public static boolean isTheme(File dir) {
		File packageJSONFile = new File(dir, "package.json");

		if (packageJSONFile.exists()) {
			try {
				JsonSlurper jsonSlurper = new JsonSlurper();

				Object object = jsonSlurper.parse(
					packageJSONFile, StandardCharsets.UTF_8.name());

				if (object instanceof Map) {
					Map<?, ?> packageJSONMap = (Map<?, ?>)object;

					if (packageJSONMap.containsKey("liferayTheme")) {
						return true;
					}
				}
			}
			catch (Exception exception) {
				_logger.warn("Unable to parse {}", packageJSONFile, exception);
			}
		}

		// Themes outside the portal may still be built by liferay-theme-tasks

		File gulpFile = new File(dir, "gulpfile.js");

		if (!gulpFile.exists()) {
			return false;
		}

		String gulpFileContent = null;

		try {
			gulpFileContent = new String(
				Files.readAllBytes(gulpFile.toPath()), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}

		return gulpFileContent.contains("require('liferay-theme-tasks')");
	}

	private static final Logger _logger = Logging.getLogger(ThemeUtil.class);

}