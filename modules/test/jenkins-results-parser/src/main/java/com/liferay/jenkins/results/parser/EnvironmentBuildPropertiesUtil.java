/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class EnvironmentBuildPropertiesUtil {

	public static void generateEnvironmentBuildProperties(
			EnvironmentBuildProperties.Environment environment,
			File rootDirectory, boolean deleteBasePropertiesFile)
		throws IOException {

		List<File> sharedPropertiesFiles = JenkinsResultsParserUtil.findFiles(
			rootDirectory, ".*-shared\\.properties");

		for (File sharedPropertiesFile : sharedPropertiesFiles) {
			String sharedPropertiesFileName = sharedPropertiesFile.getName();

			File environmentBuildPropertiesFile = new File(
				sharedPropertiesFile.getParentFile(),
				_getBasePropertiesFileName(sharedPropertiesFileName));

			if (deleteBasePropertiesFile &&
				environmentBuildPropertiesFile.exists()) {

				JenkinsResultsParserUtil.delete(environmentBuildPropertiesFile);
			}

			String urlString = EnvironmentBuildProperties.toURLString(
				environmentBuildPropertiesFile);

			EnvironmentBuildProperties environmentBuildProperties =
				new EnvironmentBuildProperties(environment, urlString);

			environmentBuildProperties.store(environmentBuildPropertiesFile);

			System.out.println("Writing " + environmentBuildPropertiesFile);
		}
	}

	public static void generateEnvironmentBuildProperties(
			File rootDirectory, boolean deleteBasePropertiesFile)
		throws IOException {

		generateEnvironmentBuildProperties(
			EnvironmentBuildProperties.getCurrentEnvironment(), rootDirectory,
			deleteBasePropertiesFile);
	}

	private static String _getBasePropertiesFileName(
		String extendedPropertiesFileName) {

		Matcher matcher = _extendedPropertyFileNamePattern.matcher(
			extendedPropertiesFileName);

		if (!matcher.matches()) {
			throw new RuntimeException(
				"Unable to parse property file name " +
					extendedPropertiesFileName);
		}

		return matcher.group(1) + "." + matcher.group(3);
	}

	private static final Pattern _extendedPropertyFileNamePattern =
		Pattern.compile("(.+)(-.+)\\.(properties)");

}