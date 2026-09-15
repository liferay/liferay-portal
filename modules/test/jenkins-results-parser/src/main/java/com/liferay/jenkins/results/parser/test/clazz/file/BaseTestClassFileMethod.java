/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestClassFileMethod implements TestClassFileMethod {

	@Override
	public String getFullName() {
		return getFullName(getDefaultSeparator());
	}

	@Override
	public String getFullName(String separator) {
		StringBuilder sb = new StringBuilder();

		for (String parentName : _parentNames) {
			sb.append(parentName);
			sb.append(separator);
		}

		sb.append(_name);

		return sb.toString();
	}

	@Override
	public TestClassFile getTestClassFile() {
		return _testClassFile;
	}

	@Override
	public boolean matches(String name) {
		if (name == null) {
			return false;
		}

		for (String separator : getSeparators()) {
			if (Objects.equals(name, getFullName(separator))) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean matchesPattern(String name) {
		if (name == null) {
			return false;
		}

		for (String separator : getSeparators()) {
			Pattern pattern = getPattern(getFullName(separator));

			Matcher matcher = pattern.matcher(name);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return getFullName();
	}

	protected BaseTestClassFileMethod(
		String name, List<String> parentNames, TestClassFile testClassFile) {

		_name = name;
		_parentNames = new ArrayList<>(parentNames);
		_testClassFile = testClassFile;
	}

	protected abstract String getDefaultSeparator();

	protected abstract Pattern getPattern(String name);

	protected abstract String[] getSeparators();

	private final String _name;
	private final List<String> _parentNames;
	private final TestClassFile _testClassFile;

}