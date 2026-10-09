/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.GradleSourceUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 * @author Hugo Huijser
 */
public class GradleDependencyVersionCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath)) {
			return content;
		}

		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		String moduleName = absolutePath.substring(y + 1, x);

		if (!moduleName.contains("test")) {
			for (String dependencies :
					GradleSourceUtil.getDependenciesBlocks(content)) {

				content = _formatDependencies(
					absolutePath, content, dependencies);
			}
		}

		return content;
	}

	private String _formatDependencies(
			String absolutePath, String content, String dependencies)
		throws IOException {

		int x = dependencies.indexOf("\n");
		int y = dependencies.lastIndexOf("\n");

		if (x == y) {
			return content;
		}

		dependencies = dependencies.substring(x, y + 1);

		StringBundler sb = new StringBundler();

		for (String line : StringUtil.splitLines(dependencies)) {
			String dependencyName = _getDependencyName(line);

			if (Objects.isNull(dependencyName) ||
				line.matches(".*\\s+testImplementation\\s+.*")) {

				sb.append(line);
				sb.append("\n");
			}
			else {
				String dependencyVersion = _getDependencyVersion(line);

				if (dependencyVersion.matches("^[0-9.]+") &&
					!_isValidVersion(
						absolutePath, dependencyName, dependencyVersion)) {

					Map<String, Integer> publishedMajorVersionsMap =
						_getPublishedMajorVersionsMap(absolutePath);

					line = StringUtil.replaceFirst(
						line, dependencyVersion,
						publishedMajorVersionsMap.get(dependencyName) + ".0.0");
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		return StringUtil.replace(
			content, StringUtil.trim(dependencies),
			StringUtil.trim(sb.toString()));
	}

	private String _getDependencyName(String dependency) {
		Matcher matcher = _dependencyNamePattern.matcher(dependency);

		if (!matcher.find()) {
			return StringPool.BLANK;
		}

		return matcher.group(1);
	}

	private String _getDependencyVersion(String dependency) {
		Matcher matcher = _dependencyVersionPattern.matcher(dependency);

		if (!matcher.find()) {
			return StringPool.BLANK;
		}

		return matcher.group(1);
	}

	private String _getMajorVersion(String version) {
		Matcher matcher = _majorVersionPattern.matcher(version);

		if (!matcher.find()) {
			return StringPool.BLANK;
		}

		return matcher.group();
	}

	private synchronized Map<String, Integer> _getPublishedMajorVersionsMap(
			String absolutePath)
		throws IOException {

		if (_publishedMajorVersionsMap != null) {
			return _publishedMajorVersionsMap;
		}

		String content = getModulesPropertiesContent(absolutePath);

		if (Validator.isNull(content)) {
			_publishedMajorVersionsMap = Collections.emptyMap();

			return _publishedMajorVersionsMap;
		}

		Map<String, String> bundleVersionsMap = new HashMap<>();

		List<String> lines = ListUtil.fromString(content);

		for (String line : lines) {
			String[] array = StringUtil.split(line, StringPool.EQUAL);

			if (array.length != 2) {
				continue;
			}

			String key = array[0];

			if (!key.startsWith("bundle.version[")) {
				continue;
			}

			bundleVersionsMap.put(
				key.substring(15, key.length() - 1), array[1]);
		}

		_publishedMajorVersionsMap = new HashMap<>();

		for (Map.Entry<String, String> entry : bundleVersionsMap.entrySet()) {
			String bundleVersion = entry.getValue();

			String majorVersion = _getMajorVersion(bundleVersion);

			if (Validator.isNull(majorVersion)) {
				continue;
			}

			int publishedMajorVersion = GetterUtil.getInteger(majorVersion);

			if ((publishedMajorVersion > 1) &&
				bundleVersion.matches(publishedMajorVersion + "[0.]*")) {

				publishedMajorVersion = publishedMajorVersion - 1;
			}

			_publishedMajorVersionsMap.put(
				entry.getKey(), publishedMajorVersion);
		}

		return _publishedMajorVersionsMap;
	}

	private boolean _isValidVersion(
			String absolutePath, String dependencyName,
			String dependencyVersion)
		throws IOException {

		Map<String, Integer> publishedMajorVersionsMap =
			_getPublishedMajorVersionsMap(absolutePath);

		Set<String> bundleSymbolicNames = publishedMajorVersionsMap.keySet();

		if (!dependencyName.startsWith("com.liferay.") ||
			!bundleSymbolicNames.contains(dependencyName) ||
			(GetterUtil.getInteger(_getMajorVersion(dependencyVersion)) ==
				publishedMajorVersionsMap.get(dependencyName))) {

			return true;
		}

		return false;
	}

	private static final Pattern _dependencyNamePattern = Pattern.compile(
		".*, name: \"([^\"]*)\".*");
	private static final Pattern _dependencyVersionPattern = Pattern.compile(
		".*, version: \"([^\"]*)\".*");
	private static final Pattern _majorVersionPattern = Pattern.compile(
		"^[0-9]+");

	private Map<String, Integer> _publishedMajorVersionsMap;

}