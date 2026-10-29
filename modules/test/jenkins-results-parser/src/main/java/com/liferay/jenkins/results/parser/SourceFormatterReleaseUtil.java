/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.StringReader;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Calum Ragan
 */
public class SourceFormatterReleaseUtil {

	public static String getIvyVersion(File portalDirectory) {
		try {
			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(
					new File(
						portalDirectory,
						"tools/sdk/dependencies/com.liferay.source.formatter" +
							"/ivy.xml")));

			Element rootElement = document.getRootElement();

			Element dependenciesElement = rootElement.element("dependencies");

			for (Element dependencyElement :
					dependenciesElement.elements("dependency")) {

				if (!Objects.equals(
						dependencyElement.attributeValue("name"),
						"com.liferay.source.formatter")) {

					continue;
				}

				return dependencyElement.attributeValue("rev");
			}

			return null;
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static boolean isReleasedSourceMatch(
		GitWorkingDirectory gitWorkingDirectory, String ref) {

		try {
			Properties artifactProperties = new Properties();

			File portalDirectory = gitWorkingDirectory.getWorkingDirectory();

			artifactProperties.load(
				new StringReader(
					JenkinsResultsParserUtil.read(
						new File(portalDirectory, _PATH_ARTIFACT_PROPERTIES))));

			String artifactSHA = artifactProperties.getProperty(
				"artifact.git.id");
			Matcher matcher = _artifactURLPattern.matcher(
				artifactProperties.getProperty("artifact.url", ""));

			if (JenkinsResultsParserUtil.isNullOrEmpty(artifactSHA) ||
				!matcher.find()) {

				return false;
			}

			String version = matcher.group("version");

			if (!Objects.equals(version, getIvyVersion(portalDirectory))) {
				return false;
			}

			GitUtil.ExecutionResult logExecutionResult =
				gitWorkingDirectory.executeBashCommands(
					GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
					GitUtil.MILLIS_TIMEOUT,
					JenkinsResultsParserUtil.combine(
						"git log --format=%H --max-count=1 ", ref, " -- ",
						_PATH_ARTIFACT_PROPERTIES));

			String artifactPropertiesSHA = logExecutionResult.getStandardOut();

			if ((logExecutionResult.getExitValue() != 0) ||
				artifactPropertiesSHA.isEmpty()) {

				return false;
			}

			GitUtil.ExecutionResult revListExecutionResult =
				gitWorkingDirectory.executeBashCommands(
					GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
					GitUtil.MILLIS_TIMEOUT,
					JenkinsResultsParserUtil.combine(
						"git rev-list ", artifactSHA, "..",
						artifactPropertiesSHA, " -- ", _PATH_SOURCE_FORMATTER));

			if ((revListExecutionResult.getExitValue() != 0) ||
				!Objects.equals(
					revListExecutionResult.getStandardOut(),
					artifactPropertiesSHA)) {

				return false;
			}

			GitUtil.ExecutionResult diffExecutionResult =
				gitWorkingDirectory.executeBashCommands(
					GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
					GitUtil.MILLIS_TIMEOUT,
					JenkinsResultsParserUtil.combine(
						"git diff --name-only ", artifactPropertiesSHA, " ",
						ref, " -- ", _PATH_SOURCE_FORMATTER));

			if ((diffExecutionResult.getExitValue() != 0) ||
				!JenkinsResultsParserUtil.isNullOrEmpty(
					diffExecutionResult.getStandardOut())) {

				return false;
			}

			return true;
		}
		catch (Exception exception) {
			return false;
		}
	}

	private static final String _PATH_ARTIFACT_PROPERTIES =
		"modules/.releng/util/source-formatter/artifact.properties";

	private static final String _PATH_SOURCE_FORMATTER =
		"modules/util/source-formatter";

	private static final Pattern _artifactURLPattern = Pattern.compile(
		"/com\\.liferay\\.source\\.formatter-(?<version>[^/]+)\\.jar$");

}