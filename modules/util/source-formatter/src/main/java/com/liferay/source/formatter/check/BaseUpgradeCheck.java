/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public abstract class BaseUpgradeCheck extends BaseFileCheck {

	protected static String addNewImportsJSPHeader(
		String newContent, String[] newImports) {

		Arrays.sort(newImports);

		List<String> missingImports = new ArrayList<>();

		for (String newImport : newImports) {
			if (newContent.contains("import=\"" + newImport)) {
				continue;
			}

			missingImports.add(newImport);
		}

		if (missingImports.isEmpty()) {
			return newContent;
		}

		newImports = missingImports.toArray(new String[0]);

		Matcher includesMatcher = _includesPattern.matcher(newContent);

		int index = -1;

		while (includesMatcher.find()) {
			index = includesMatcher.start();
		}

		if (index != -1) {
			includesMatcher.find(index);

			String lastJSPHeader = includesMatcher.group();

			return StringUtil.replaceFirst(
				newContent, lastJSPHeader,
				getNewImportsJSPHeader(lastJSPHeader, newImports), index);
		}

		Matcher copyrightMatcher = _copyrightPattern.matcher(newContent);

		if (copyrightMatcher.find()) {
			String jspHeader = copyrightMatcher.group(1);

			return StringUtil.replaceFirst(
				newContent, jspHeader,
				getNewImportsJSPHeader(jspHeader, newImports));
		}

		return getNewImportsJSPHeader("", newImports) + newContent;
	}

	protected static String getNewImportsJSPHeader(
		String lastJSPHeader, String[] newImports) {

		StringBundler sb = new StringBundler(4);

		if (!lastJSPHeader.isEmpty()) {
			sb.append(lastJSPHeader);
			sb.append(StringPool.NEW_LINE);
			sb.append(StringPool.NEW_LINE);
		}

		for (int i = 0; i < newImports.length; i++) {
			newImports[i] = "<%@ page import=\"" + newImports[i] + "\" %>";
		}

		sb.append(StringUtil.merge(newImports, StringPool.NEW_LINE));

		return sb.toString();
	}

	protected String addNewImports(String fileName, String newContent) {
		String[] newImports = getNewImports();

		if (newImports == null) {
			return newContent;
		}

		if (fileName.endsWith(".java")) {
			newContent = JavaSourceUtil.addImports(newContent, newImports);
		}
		else if (fileName.endsWith(".jsp")) {
			newContent = addNewImportsJSPHeader(newContent, newImports);
		}

		return newContent;
	}

	protected String afterFormat(
		String fileName, String absolutePath, String content,
		String newContent) {

		return addNewImports(fileName, newContent);
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!isValidExtension(fileName)) {
			return content;
		}

		String newContent = format(fileName, absolutePath, content);

		if (!content.equals(newContent)) {
			newContent = afterFormat(
				fileName, absolutePath, content, newContent);
		}

		return newContent;
	}

	protected abstract String format(
			String fileName, String absolutePath, String content)
		throws Exception;

	protected String[] getNewImports() {
		return null;
	}

	protected String[] getValidExtensions() {
		return new String[] {"java"};
	}

	protected boolean isValidExtension(String fileName) {
		for (String extension : getValidExtensions()) {
			if (fileName.endsWith(CharPool.PERIOD + extension)) {
				return true;
			}
		}

		return false;
	}

	protected String joinLines(String... lines) {
		StringBundler sb = new StringBundler((lines.length * 2) - 1);

		for (String line : lines) {
			if (sb.index() > 0) {
				sb.append(StringPool.NEW_LINE);
			}

			sb.append(line);
		}

		return sb.toString();
	}

	private static final Pattern _copyrightPattern = Pattern.compile(
		"(<%--\\s*(\\/\\*)+(\\n|.)*(\\*\\/)+\\s*--%>)");
	private static final Pattern _includesPattern = Pattern.compile(
		"<%@\\s*include\\s+file\\s*=[\"/\\w\\.]+\\s*%>", Pattern.MULTILINE);

}