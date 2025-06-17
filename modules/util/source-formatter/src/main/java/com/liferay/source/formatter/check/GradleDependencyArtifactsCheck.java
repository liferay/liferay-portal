/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class GradleDependencyArtifactsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = _renameDependencyNames(fileName, absolutePath, content);

		List<String> enforceVersionArtifacts = getAttributeValues(
			_ENFORCE_VERSION_ARTIFACTS_KEY, absolutePath);

		content = _enforceDependencyVersions(
			content, enforceVersionArtifacts, absolutePath);

		Matcher matcher = _artifactPattern.matcher(content);

		while (matcher.find()) {
			String name = matcher.group(3);
			String version = matcher.group(4);

			content = _formatVersion(
				fileName, absolutePath, content, matcher.group(2), name,
				version, matcher.start(3), enforceVersionArtifacts);

			if ((isSubrepository() || absolutePath.contains("/modules/apps/") ||
				 absolutePath.contains("/modules/dxp/apps/") ||
				 absolutePath.contains("/modules/private/apps/")) &&
				!_isTestUtilModule(absolutePath)) {

				content = _fixMicroVersion(
					fileName, content, matcher.group(1), name, version);
			}
		}

		if (absolutePath.endsWith("/lib/development/dependencies.properties") ||
			absolutePath.endsWith("/lib/portal/dependencies.properties")) {

			_checkDependenciesPropertiesFile(
				fileName, content, enforceVersionArtifacts);
		}

		return content;
	}

	private void _checkDependenciesPropertiesFile(
			String fileName, String content,
			List<String> enforceVersionArtifacts)
		throws IOException {

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				String[] parts = StringUtil.split(line, StringPool.EQUAL);

				if ((parts.length != 2) ||
					enforceVersionArtifacts.contains(parts[1])) {

					continue;
				}

				String[] artifactParts = StringUtil.split(
					parts[1], StringPool.COLON);

				if (artifactParts.length != 3) {
					continue;
				}

				String dependencyGroupAndName =
					artifactParts[0] + ":" + artifactParts[1];

				if (!ListUtil.exists(
						enforceVersionArtifacts,
						enforceVersionArtifact ->
							enforceVersionArtifact.startsWith(
								dependencyGroupAndName + ":"))) {

					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"The version of \"", dependencyGroupAndName,
						"\" does not match the version in \"",
						_ENFORCE_VERSION_ARTIFACTS_KEY,
						"\" property in source-formatter.properties"),
					lineNumber);
			}
		}
	}

	private String _enforceDependencyVersions(
		String content, List<String> enforceVersionArtifacts,
		String absolutePath) {

		List<String> allowedVersionArtifacts = getAttributeValues(
			_ALLOWED_VERSION_ARTIFACTS_KEY, absolutePath);

		for (String artifact : enforceVersionArtifacts) {
			String[] artifactParts = StringUtil.split(
				artifact, StringPool.COLON);

			if (allowedVersionArtifacts.contains(
					artifactParts[0] + ":" + artifactParts[1])) {

				continue;
			}

			Pattern pattern = Pattern.compile(
				StringBundler.concat(
					"(group: \"", artifactParts[0], "\", name: \"",
					artifactParts[1], "\",.* version: \").*?(\")"));

			Matcher matcher = pattern.matcher(content);

			content = matcher.replaceAll("$1" + artifactParts[2] + "$2");
		}

		return content;
	}

	private String _fixMicroVersion(
			String fileName, String content, String line, String name,
			String version)
		throws IOException {

		if (!line.startsWith("compileOnly ") && !line.startsWith("provided ")) {
			return content;
		}

		if (!name.startsWith("com.liferay.") ||
			!version.matches("[0-9]+\\.[0-9]+\\.[1-9][0-9]*")) {

			return content;
		}

		int pos = fileName.lastIndexOf(CharPool.SLASH);

		String bndFileLocation = fileName.substring(0, pos + 1) + "bnd.bnd";

		File bndFile = new File(bndFileLocation);

		if (bndFile.exists()) {
			String bndFileContent = FileUtil.read(bndFile);

			Matcher matcher = _bndConditionalPackagePattern.matcher(
				bndFileContent);

			if (matcher.find()) {
				String conditionalPackageContent = matcher.group();

				if (conditionalPackageContent.contains(name)) {
					return content;
				}
			}
		}

		pos = version.lastIndexOf(".");

		String newLine = StringUtil.replaceFirst(
			line, "version: \"" + version + "\"",
			"version: \"" + version.substring(0, pos + 1) + "0\"");

		return StringUtil.replaceFirst(content, line, newLine);
	}

	private String _formatVersion(
			String fileName, String absolutePath, String content,
			String dependency, String name, String version, int pos,
			List<String> enforceVersionArtifacts)
		throws IOException {

		if (isSubrepository() || !fileName.endsWith("build.gradle") ||
			absolutePath.contains("/modules/util/") ||
			absolutePath.contains("/workspaces/")) {

			return content;
		}

		if (!name.equals("com.liferay.portal.impl") &&
			!name.equals("com.liferay.portal.kernel") &&
			!name.equals("com.liferay.portal.test") &&
			!name.equals("com.liferay.portal.test.integration") &&
			!name.equals("com.liferay.util.bridges") &&
			!name.equals("com.liferay.util.java") &&
			!name.equals("com.liferay.util.taglib")) {

			if (version.equals("default")) {
				addMessage(
					fileName,
					"Do not use \"default\" version for \"" + name + "\"",
					getLineNumber(content, pos));
			}
			else if (name.startsWith("com.liferay") &&
					 !name.startsWith("com.liferay.gradle") &&
					 !name.startsWith("com.liferay.jakarta") &&
					 !name.startsWith("com.liferay.portletmvc4spring") &&
					 !_isMasterOnlyFile(absolutePath)) {

				for (String enforceVersionArtifact : enforceVersionArtifacts) {
					if (enforceVersionArtifact.startsWith(
							"com.liferay:" + name + ":") ||
						enforceVersionArtifact.startsWith(
							"com.liferay.arquillian:" + name + ":")) {

						return content;
					}
				}

				Map<String, String> projectNamesMap = _getProjectNamesMap(
					absolutePath);

				if (projectNamesMap.containsKey(name)) {
					return StringUtil.replace(
						content, dependency,
						"project(\"" + projectNamesMap.get(name) + "\")");
				}

				addMessage(
					fileName, "Incorrect dependency \"" + name + "\"",
					getLineNumber(content, pos));
			}
		}
		else if (!version.equals("default") &&
				 !_isMasterOnlyFile(absolutePath)) {

			return StringUtil.replaceFirst(content, version, "default", pos);
		}

		return content;
	}

	private String _getArtifactString(String artifact) {
		String[] array = StringUtil.split(artifact, CharPool.COLON);

		if (array.length != 2) {
			return null;
		}

		return StringBundler.concat(
			"group: \"", array[0], "\", name: \"", array[1], "\"");
	}

	private synchronized Map<String, String> _getProjectNamesMap(
			String absolutePath)
		throws IOException {

		if (_projectNamesMap != null) {
			return _projectNamesMap;
		}

		_projectNamesMap = new HashMap<>();

		String content = getModulesPropertiesContent(absolutePath);

		if (Validator.isNull(content)) {
			return _projectNamesMap;
		}

		List<String> lines = ListUtil.fromString(content);

		for (String line : lines) {
			String[] array = StringUtil.split(line, StringPool.EQUAL);

			if (array.length != 2) {
				continue;
			}

			String key = array[0];

			if (key.startsWith("project.name[")) {
				_projectNamesMap.put(
					key.substring(13, key.length() - 1), array[1]);
			}
		}

		return _projectNamesMap;
	}

	private boolean _isAllowedArtifactFile(
		String fileName, String oldArtifact,
		List<String> allowedArtifactsFileNames) {

		for (String allowedArtifactsFileName : allowedArtifactsFileNames) {
			String[] allowedArtifactArray = StringUtil.split(
				allowedArtifactsFileName, "->");

			if ((allowedArtifactArray.length != 2) ||
				!StringUtil.equals(oldArtifact, allowedArtifactArray[0])) {

				continue;
			}

			if (fileName.endsWith(allowedArtifactArray[1])) {
				return true;
			}
		}

		return false;
	}

	private boolean _isMasterOnlyFile(String absolutePath) {
		int x = absolutePath.length();

		while (true) {
			x = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

			if (x == -1) {
				return false;
			}

			File file = new File(
				absolutePath.substring(0, x + 1) + ".lfrbuild-master-only");

			if (file.exists()) {
				return true;
			}
		}
	}

	private boolean _isTestUtilModule(String absolutePath) {
		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		String moduleName = absolutePath.substring(y + 1, x);

		return moduleName.endsWith("-test-util");
	}

	private String _renameDependencyNames(
		String fileName, String absolutePath, String content) {

		List<String> allowedArtifactsFileNames = getAttributeValues(
			_ALLOWED_ARTIFACTS_FILE_NAMES_KEY, absolutePath);
		List<String> renameArtifacts = getAttributeValues(
			_RENAME_ARTIFACTS_KEY, absolutePath);

		for (String renameArtifact : renameArtifacts) {
			String[] renameArtifactArray = StringUtil.split(
				renameArtifact, "->");

			if (renameArtifactArray.length != 2) {
				continue;
			}

			String newArtifactString = _getArtifactString(
				renameArtifactArray[1]);
			String oldArtifactString = _getArtifactString(
				renameArtifactArray[0]);

			if ((newArtifactString != null) && (oldArtifactString != null) &&
				!_isAllowedArtifactFile(
					fileName, renameArtifactArray[0],
					allowedArtifactsFileNames)) {

				content = StringUtil.replace(
					content, oldArtifactString, newArtifactString);
			}
		}

		return content;
	}

	private static final String _ALLOWED_ARTIFACTS_FILE_NAMES_KEY =
		"allowedArtifactsFileNames";

	private static final String _ALLOWED_VERSION_ARTIFACTS_KEY =
		"allowedVersionArtifacts";

	private static final String _ENFORCE_VERSION_ARTIFACTS_KEY =
		"enforceVersionArtifacts";

	private static final String _RENAME_ARTIFACTS_KEY = "renameArtifacts";

	private static final Pattern _artifactPattern = Pattern.compile(
		"\n\t*(.* (group: \"[^\"]+\", name: \"([^\"]+)\", " +
			"version: \"([^\"]+)\"))");
	private static final Pattern _bndConditionalPackagePattern =
		Pattern.compile(
			"-conditionalpackage:(.*[^\\\\])(\n|\\Z)", Pattern.DOTALL);

	private Map<String, String> _projectNamesMap;

}