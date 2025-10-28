/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.BaseImportsFormatter;
import com.liferay.portal.tools.ImportPackage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class FTLImportsFormatter extends BaseImportsFormatter {

	protected static List<String> getImportsList(String content) {
		List<String> importsList = new ArrayList<>();

		Matcher matcher = _importsPattern.matcher(content);

		while (matcher.find()) {
			importsList.add(matcher.group());
		}

		return importsList;
	}

	@Override
	protected ImportPackage createImportPackage(String line) {
		return createJavaImportPackage(line);
	}

	@Override
	protected String doFormat(
			String content, Pattern importPattern, String packageDir,
			String className)
		throws IOException {

		List<String> importsList = getImportsList(content);

		for (String imports : importsList) {
			content = doFormat(content, packageDir, className, imports);
		}

		return content;
	}

	protected String doFormat(
			String content, String packageDir, String className, String imports)
		throws IOException {

		if (Validator.isNull(imports)) {
			return content;
		}

		String newImports = sortAndGroupImports(imports);

		if (!imports.equals(newImports)) {
			content = StringUtil.replaceFirst(content, imports, newImports);
		}

		content = content.replaceAll(
			"(?m)^([ \t]*package .*;|</#.*>)\\s*^([ \t]*import)", "$1\n\n$2");

		content = content.replaceAll(
			"(?m)^([ \t]*(?:package|import) .*;)\\s*^([ \t]*/\\*\\*|@|<#" +
				"(?!else))",
			"$1\n\n$2");

		return content;
	}

	private static final Pattern _importsPattern = Pattern.compile(
		"(^[ \t]*import\\s+[^$].*;\n+)+", Pattern.MULTILINE);

}