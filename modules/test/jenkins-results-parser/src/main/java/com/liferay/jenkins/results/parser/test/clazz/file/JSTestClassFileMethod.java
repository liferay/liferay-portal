/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class JSTestClassFileMethod extends BaseTestClassFileMethod {

	protected JSTestClassFileMethod(
		String name, List<String> parentNames, TestClassFile testClassFile) {

		super(name, parentNames, testClassFile);
	}

	@Override
	protected String getDefaultSeparator() {
		return _SEPARATOR_DEFAULT;
	}

	@Override
	protected Pattern getPattern(String name) {
		Pattern pattern = _patterns.get(name);

		if (pattern != null) {
			return pattern;
		}

		StringBuilder sb = new StringBuilder();

		int index = 0;

		Matcher matcher = _placeholderPattern.matcher(name);

		while (matcher.find()) {
			sb.append(Pattern.quote(name.substring(index, matcher.start())));
			sb.append(".*");

			index = matcher.end();
		}

		sb.append(Pattern.quote(name.substring(index)));

		pattern = Pattern.compile(sb.toString(), Pattern.DOTALL);

		_patterns.put(name, pattern);

		return pattern;
	}

	@Override
	protected String[] getSeparators() {
		return _SEPARATORS;
	}

	private static final String _SEPARATOR_DEFAULT = " > ";

	private static final String[] _SEPARATORS = {" > ", " "};

	private static final Pattern _placeholderPattern = Pattern.compile(
		"\\$\\{[^}]*\\}|%[#dfijops]|\\$#|\\$[A-Za-z_][A-Za-z0-9_.]*");

	private final Map<String, Pattern> _patterns = new HashMap<>();

}