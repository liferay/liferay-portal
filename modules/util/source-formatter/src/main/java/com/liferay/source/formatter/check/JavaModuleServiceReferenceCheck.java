/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaModuleServiceReferenceCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String packageName = JavaSourceUtil.getPackageName(content);

		int pos = packageName.indexOf(".service.");

		if (pos == -1) {
			return content;
		}

		String servicePackageName = packageName.substring(0, pos + 8);

		_checkServiceReferences(
			fileName, content, packageName, servicePackageName);

		return content;
	}

	private void _checkServiceReferences(
		String fileName, String content, String packageName,
		String servicePackageName) {

		Matcher matcher = _serviceReferencePattern.matcher(content);

		while (matcher.find()) {
			String className = _getFullClassName(
				content, matcher.group(1), packageName);

			if (!className.startsWith(servicePackageName)) {
				continue;
			}

			addMessage(
				fileName, "Use @BeanReference instead of @ServiceReference",
				getLineNumber(content, matcher.start()));
		}
	}

	private String _getFullClassName(
		String content, String className, String packageName) {

		if (className.contains(StringPool.PERIOD)) {
			return className;
		}

		Pattern pattern = Pattern.compile("import (.*" + className + ");");

		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return packageName + StringPool.PERIOD + className;
	}

	private static final Pattern _serviceReferencePattern = Pattern.compile(
		"@ServiceReference\\(\\s*type = ([\\w.]+)\\.class\\)\n");

}