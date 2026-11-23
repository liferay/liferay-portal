/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal.configuration;

import com.liferay.portal.file.install.properties.CFGProperties;
import com.liferay.portal.file.install.properties.ConfigurationProperties;
import com.liferay.portal.file.install.properties.TypedProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Matthew Tambara
 */
public class ConfigurationPropertiesFactory {

	public static ConfigurationProperties create(File file, String encoding)
		throws IOException {

		ConfigurationProperties configurationProperties = null;

		String fileName = file.getName();

		if (fileName.endsWith("cfg")) {
			configurationProperties = new CFGProperties();
		}
		else if (fileName.endsWith("config")) {
			configurationProperties = new TypedProperties();
		}
		else {
			throw new IllegalArgumentException(
				"Unknown configuration type: " + file);
		}

		try (InputStream inputStream = new FileInputStream(file);

			Reader reader = new InputStreamReader(inputStream, encoding)) {

			configurationProperties.load(reader);
		}

		return configurationProperties;
	}

	public static ConfigurationProperties create(
			String fileName, String content, String encoding)
		throws IOException {

		ConfigurationProperties configurationProperties = null;

		if (fileName.endsWith("cfg")) {
			configurationProperties = new CFGProperties();
		}
		else if (fileName.endsWith("config")) {
			configurationProperties = new TypedProperties();
		}
		else {
			throw new IllegalArgumentException(
				"Unknown configuration type: " + fileName);
		}

		try (Reader reader = new StringReader(content)) {
			configurationProperties.load(reader);
		}

		return configurationProperties;
	}

}