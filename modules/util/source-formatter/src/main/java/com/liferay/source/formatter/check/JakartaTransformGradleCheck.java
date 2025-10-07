/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
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
			_getJakartaTransformDependenciesMap();

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

				if (i < (dependencies.length - 1)) {
					dependencySB.append('\n');
				}
			}

			String dependency = dependencySB.toString();

			matcher.appendReplacement(sb, Matcher.quoteReplacement(dependency));
		}

		matcher.appendTail(sb);

		return replace(sb.toString());
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private synchronized Map<String, String>
			_getJakartaTransformDependenciesMap()
		throws IOException {

		if (_jakartaTransformDependenciesMap != null) {
			return _jakartaTransformDependenciesMap;
		}

		_jakartaTransformDependenciesMap = new HashMap<>();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"dependencies/jakarta-transform-dependencies.txt");

		if (inputStream == null) {
			return Collections.emptyMap();
		}

		String[] lines = StringUtil.splitLines(StringUtil.read(inputStream));

		for (String line : lines) {
			String[] parts = line.split("=");

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