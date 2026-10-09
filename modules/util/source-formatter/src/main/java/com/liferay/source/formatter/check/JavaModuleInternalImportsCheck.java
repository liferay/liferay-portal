/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.File;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaModuleInternalImportsCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/modules/core/") ||
			absolutePath.contains("/modules/util/") ||
			fileName.contains("/test/") ||
			fileName.contains("/testIntegration/")) {

			return content;
		}

		String packageName = JavaSourceUtil.getPackageName(content);

		if (!packageName.startsWith("com.liferay")) {
			return content;
		}

		_checkInternalImports(fileName, absolutePath, content);

		return content;
	}

	private void _checkInternalImports(
		String fileName, String absolutePath, String content) {

		Matcher matcher = _internalImportPattern.matcher(content);

		int pos = absolutePath.lastIndexOf("/com/liferay/");

		String s = absolutePath.substring(0, pos + 13);

		List<String> renamedSourcePathNames = getAttributeValues(
			_RENAMED_SOURCE_PATH_NAMES_KEY, absolutePath);

		while (matcher.find()) {
			String match = matcher.group(1);

			String expectedImportFileLocation =
				s + StringUtil.replace(match, '.', '/') + ".java";

			for (String renamedSourcePathName : renamedSourcePathNames) {
				String[] parts = StringUtil.split(
					renamedSourcePathName, StringPool.COLON);

				if (match.equals(parts[0])) {
					expectedImportFileLocation = StringUtil.replaceFirst(
						expectedImportFileLocation, "/src/main/java/",
						"/src/main/" + parts[1] + "/");

					break;
				}
			}

			File file = new File(expectedImportFileLocation);

			if (file.exists()) {
				continue;
			}

			addMessage(
				fileName, "Do not import internal class from another module",
				getLineNumber(content, matcher.start(1)));
		}
	}

	private static final String _RENAMED_SOURCE_PATH_NAMES_KEY =
		"renamedSourcePathNames";

	private static final Pattern _internalImportPattern = Pattern.compile(
		"\nimport com\\.liferay\\.(.*\\.internal\\.([a-z].*?\\.)?[A-Z].*?)" +
			"[\\.|;]");

}