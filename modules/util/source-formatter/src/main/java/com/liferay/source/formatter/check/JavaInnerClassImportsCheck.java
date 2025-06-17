/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaInnerClassImportsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		String className = null;
		List<String> imports = null;
		String packageName = null;

		List<String> upperCasePackageNames = getAttributeValues(
			_UPPER_CASE_PACKAGE_NAMES_KEY, absolutePath);

		Matcher matcher = _innerClassImportPattern.matcher(content);

		while (matcher.find()) {
			String outerClassFullyQualifiedName = matcher.group(2);

			if (upperCasePackageNames.contains(outerClassFullyQualifiedName)) {
				continue;
			}

			String innerClassName = matcher.group(4);
			String innerClassFullyQualifiedName = matcher.group(1);
			String outerClassName = matcher.group(3);

			// Skip inner classes with long names, because it causes a lot of
			// cases where we get long lines that are hard to resolve

			if (imports == null) {
				imports = JavaSourceUtil.getImportNames(content);
			}

			if ((innerClassName.length() + outerClassName.length()) > 40) {
				content = _stripRedundantOuterClass(
					content, innerClassName, innerClassFullyQualifiedName,
					imports);

				continue;
			}

			if (className == null) {
				className = JavaSourceUtil.getClassName(fileName);
				packageName = JavaSourceUtil.getPackageName(content);
			}

			if (outerClassFullyQualifiedName.equals(
					packageName + "." + className)) {

				return _removeInnerClassImport(
					content, innerClassFullyQualifiedName,
					outerClassFullyQualifiedName);
			}

			if (outerClassName.equals(className)) {
				continue;
			}

			if (_isRedundantImport(
					content, innerClassName, outerClassName,
					outerClassFullyQualifiedName, packageName, imports)) {

				return _formatInnerClassImport(
					content, innerClassName, innerClassFullyQualifiedName,
					outerClassName, outerClassFullyQualifiedName);
			}

			content = _stripRedundantOuterClass(
				content, innerClassName, innerClassFullyQualifiedName, imports);
		}

		return content;
	}

	private String _formatInnerClassImport(
		String content, String innerClassName,
		String innerClassFullyQualifiedName, String outerClassName,
		String outerClassFullyQualifiedName) {

		content = _removeInnerClassImport(
			content, innerClassFullyQualifiedName,
			outerClassFullyQualifiedName);

		Pattern pattern = Pattern.compile("[^.\\w]" + innerClassName + "\\W");

		while (true) {
			Matcher matcher = pattern.matcher(content);

			if (!matcher.find()) {
				return content;
			}

			content = StringUtil.insert(
				content, outerClassName + StringPool.PERIOD,
				matcher.start() + 1);
		}
	}

	private String _getFullyQualifiedName(
		String s1, String s2, List<String> imports) {

		for (String importLine : imports) {
			if (!importLine.endsWith(StringPool.PERIOD + s1)) {
				continue;
			}

			if (s2 == null) {
				return importLine;
			}

			s2 = StringUtil.replaceLast(
				s2.replaceAll("\\s", StringPool.BLANK), CharPool.PERIOD,
				StringPool.BLANK);

			return StringBundler.concat(importLine, StringPool.PERIOD, s2);
		}

		return null;
	}

	private boolean _isRedundantImport(
		String content, String innerClassName, String outerClassName,
		String outerClassFullyQualifiedName, String packageName,
		List<String> imports) {

		if (content.matches(
				"(?s).*\\.\\s*new\\s+" + innerClassName + "\\(.*")) {

			return false;
		}

		String fullyQualifiedName = _getFullyQualifiedName(
			outerClassName, null, imports);

		if (fullyQualifiedName != null) {
			return fullyQualifiedName.equals(outerClassFullyQualifiedName);
		}

		if (outerClassFullyQualifiedName.equals(
				"java.lang." + outerClassName) ||
			outerClassFullyQualifiedName.equals(
				packageName + "." + outerClassName) ||
			!content.matches("(?s).*[^.\\w]" + outerClassName + "\\W.*")) {

			return true;
		}

		return false;
	}

	private String _removeInnerClassImport(
		String content, String innerClassFullyQualifiedName,
		String outerClassFullyQualifiedName) {

		String replacement = StringPool.BLANK;

		if (!content.contains("import " + outerClassFullyQualifiedName + ";")) {
			replacement = "\nimport " + outerClassFullyQualifiedName + ";";
		}

		return StringUtil.replaceFirst(
			content, "\nimport " + innerClassFullyQualifiedName + ";",
			replacement);
	}

	private String _stripRedundantOuterClass(
		String content, String innerClassName, String outerClassName) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"\n(.*[^\\w\n.])(", outerClassName, "\\.\\s*", innerClassName,
				")\\W"));

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String lineStart = StringUtil.trimLeading(matcher.group(1));

			if (lineStart.contains("//") ||
				ToolsUtil.isInsideQuotes(content, matcher.end(1))) {

				continue;
			}

			return StringUtil.replaceFirst(
				content, matcher.group(2), innerClassName, matcher.end(1));
		}

		return content;
	}

	private String _stripRedundantOuterClass(
		String content, String innerClassName,
		String innerClassFullyQualifiedName, List<String> imports) {

		Matcher matcher = _outerClassPattern.matcher(
			innerClassFullyQualifiedName);

		while (matcher.find()) {
			int x = matcher.end();

			if (x == innerClassFullyQualifiedName.length()) {
				return content;
			}

			String outerClassFullyQualifiedName =
				innerClassFullyQualifiedName.substring(0, x);

			if (imports.contains(outerClassFullyQualifiedName)) {
				content = _stripRedundantOuterClass(
					content, innerClassName, matcher.group(1));
			}
		}

		return content;
	}

	private static final String _UPPER_CASE_PACKAGE_NAMES_KEY =
		"upperCasePackageNames";

	private static final Pattern _innerClassImportPattern = Pattern.compile(
		"\nimport (([\\w.]+\\.([A-Z]\\w+))\\.([A-Z]\\w+));");
	private static final Pattern _outerClassPattern = Pattern.compile(
		"\\.([A-Z]\\w+)");

}