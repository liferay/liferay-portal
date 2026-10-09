/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JakartaTransformGradleCheck extends BaseJakartaTransformCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws IOException {

		Map<String, String> jakartaTransformDependenciesMap =
			_getJakartaTransformDependenciesMap(absolutePath);

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _dependencyPattern.matcher(content);

		while (matcher.find()) {
			String jakartaTransformDependencies =
				jakartaTransformDependenciesMap.get(
					matcher.group(1) + ":" + matcher.group(2));

			if (jakartaTransformDependencies == null) {
				continue;
			}

			String[] dependencies = StringUtil.split(
				jakartaTransformDependencies, "|");

			StringBuilder dependencySB = new StringBuilder(
				dependencies.length * 8);

			for (int i = 0; i < dependencies.length; i++) {
				String[] parts = StringUtil.split(dependencies[i], ":");

				dependencySB.append(
					content.substring(matcher.start(0), matcher.start(1)));
				dependencySB.append(parts[0]);
				dependencySB.append(
					content.substring(matcher.end(1), matcher.start(2)));
				dependencySB.append(parts[1]);
				dependencySB.append(
					content.substring(matcher.end(2), matcher.start(3)));
				dependencySB.append(parts[2]);
				dependencySB.append(
					content.substring(matcher.end(3), matcher.end(0)));

				if (i >= (dependencies.length - 1)) {
					continue;
				}

				dependencySB.append('\n');
			}

			String dependency = dependencySB.toString();

			matcher.appendReplacement(sb, Matcher.quoteReplacement(dependency));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private synchronized Map<String, String>
			_getJakartaTransformDependenciesMap(String absolutePath)
		throws IOException {

		if (_jakartaTransformDependenciesMap != null) {
			return _jakartaTransformDependenciesMap;
		}

		_jakartaTransformDependenciesMap = new HashMap<>();

		String content = null;

		String jakartaTransformDependenciesFilePath = getAttributeValue(
			SourceFormatterUtil.JAKARTA_TRANSFORM_DEPENDENCIES_FILE_PATH,
			absolutePath);

		if (Validator.isNotNull(jakartaTransformDependenciesFilePath)) {
			File file = new File(jakartaTransformDependenciesFilePath);

			if (!file.isFile()) {
				throw new IOException(
					"Unable to read file \"" +
						jakartaTransformDependenciesFilePath + "\"");
			}

			content = FileUtil.read(file);
		}

		if (Validator.isBlank(content)) {
			Class<?> clazz = getClass();

			content = StringUtil.read(
				clazz.getClassLoader(),
				"dependencies/jakarta-transform-dependencies.txt");
		}

		for (String line : StringUtil.splitLines(content)) {
			String[] parts = line.split("=", 2);

			if (parts.length < 2) {
				throw new IOException(
					StringBundler.concat(
						"Invalid line \"", line, "\" in ",
						jakartaTransformDependenciesFilePath));
			}

			_jakartaTransformDependenciesMap.put(parts[0], parts[1]);
		}

		return _jakartaTransformDependenciesMap;
	}

	private static final String[] _VALID_EXTENSIONS = {".gradle"};

	private static final Pattern _dependencyPattern = Pattern.compile(
		"\t\\w+(?:\\s|\\()+group:\\s*['\"](.+)['\"],\\s*" +
			"name:\\s*['\"](.+)['\"],\\s*(?:transitive:\\s*\\w+,\\s*)?" +
				"version:\\s*['\"](.+)['\"]");

	private Map<String, String> _jakartaTransformDependenciesMap;

}