/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
			File ivyXMLFile = new File(
				portalDirectory,
				JenkinsResultsParserUtil.combine(
					"tools/sdk/dependencies/", _PACKAGE_NAME, "/ivy.xml"));

			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(ivyXMLFile));

			Element rootElement = document.getRootElement();

			Element dependenciesElement = rootElement.element("dependencies");

			for (Element dependencyElement :
					dependenciesElement.elements("dependency")) {

				if (!Objects.equals(
						dependencyElement.attributeValue("name"),
						_PACKAGE_NAME)) {

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
						new File(portalDirectory, _ARTIFACT_PROPERTIES_PATH))));

			String artifactGitId = artifactProperties.getProperty(
				"artifact.git.id");

			Matcher matcher = _artifactURLPattern.matcher(
				artifactProperties.getProperty("artifact.url", ""));

			if (JenkinsResultsParserUtil.isNullOrEmpty(artifactGitId) ||
				!matcher.find()) {

				return false;
			}

			String version = matcher.group("version");

			if (!Objects.equals(version, getIvyVersion(portalDirectory))) {
				return false;
			}

			GitUtil.ExecutionResult executionResult =
				gitWorkingDirectory.executeBashCommands(
					GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
					GitUtil.MILLIS_TIMEOUT,
					JenkinsResultsParserUtil.combine(
						"git diff --dst-prefix=b/ --no-color --src-prefix=a/ ",
						"--unified=0 ", artifactGitId, " ", ref, " -- ",
						_SOURCE_FORMATTER_PATH));

			if (executionResult.getExitValue() != 0) {
				return false;
			}

			return _isVersionOnlyDiff(
				executionResult.getStandardOut(), version);
		}
		catch (Exception exception) {
			return false;
		}
	}

	private static boolean _isVersionOnlyDiff(String gitDiff, String version) {
		int index = version.lastIndexOf(".");

		if (index == -1) {
			return false;
		}

		List<String> addedLines = new ArrayList<>();
		List<String> removedLines = new ArrayList<>();

		boolean hunk = false;

		Pattern versionPattern = Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"(?<![\\d.])", Pattern.quote(version.substring(0, index + 1)),
				"\\d+(?![\\d.])"));

		for (String line : gitDiff.split("\n")) {
			if (line.startsWith("diff --git ")) {
				if (!_versionFilePaths.contains(
						line.substring(line.indexOf(" b/") + 3))) {

					return false;
				}

				hunk = false;

				continue;
			}

			if (line.startsWith("@@")) {
				hunk = true;

				continue;
			}

			if (!hunk) {
				continue;
			}

			Matcher matcher = versionPattern.matcher(line.substring(1));

			if (line.startsWith("+")) {
				addedLines.add(matcher.replaceAll(""));
			}
			else if (line.startsWith("-")) {
				removedLines.add(matcher.replaceAll(""));
			}
		}

		return addedLines.equals(removedLines);
	}

	private static final String _ARTIFACT_PROPERTIES_PATH =
		"modules/.releng/util/source-formatter/artifact.properties";

	private static final String _PACKAGE_NAME = "com.liferay.source.formatter";

	private static final String _SOURCE_FORMATTER_PATH =
		"modules/util/source-formatter";

	private static final Pattern _artifactURLPattern = Pattern.compile(
		"/com\\.liferay\\.source\\.formatter-(?<version>[^/]+)\\.jar$");
	private static final List<String> _versionFilePaths = Arrays.asList(
		_SOURCE_FORMATTER_PATH + "/bnd.bnd",
		_SOURCE_FORMATTER_PATH + "/samples/pom.xml",
		JenkinsResultsParserUtil.combine(
			_SOURCE_FORMATTER_PATH, "/src/main/resources/META-INF/maven/",
			"com.liferay/com.liferay.source.formatter/plugin-help.xml"),
		JenkinsResultsParserUtil.combine(
			_SOURCE_FORMATTER_PATH, "/src/main/resources/META-INF/maven/",
			"plugin.xml"));

}