/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JSPSourceUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPUnusedTermsCheck extends BaseJSPTermsCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		populateContentsMap(fileName, content);

		content = _removeUnusedImports(fileName, content);

		content = JSPSourceUtil.compressImportsOrTaglibs(
			fileName, content, "<%@ page import=");
		content = JSPSourceUtil.compressImportsOrTaglibs(
			fileName, content, "<%@ tag import=");

		if (isPortalSource() || isSubrepository()) {
			content = _removeUnusedPortletDefineObjects(fileName, content);

			content = _removeDuplicateDefineObjects(fileName, content);

			content = _removeUnusedTaglibs(fileName, content);

			content = JSPSourceUtil.compressImportsOrTaglibs(
				fileName, content, "<%@ taglib uri=");

			content = _removeUnusedVariables(fileName, absolutePath, content);
		}

		put(fileName, content);

		return content;
	}

	private void _addJSPUnusedImports(
		String fileName, String content, List<String> importLines,
		List<String> unneededImports) {

		Set<String> checkedFileNames = new HashSet<>();
		Set<String> includeFileNames = new HashSet<>();

		for (String importLine : importLines) {
			int x = importLine.indexOf(CharPool.QUOTE);

			int y = importLine.indexOf(CharPool.QUOTE, x + 1);

			if ((x == -1) || (y == -1)) {
				continue;
			}

			String className = importLine.substring(x + 1, y);

			className = className.substring(
				className.lastIndexOf(CharPool.PERIOD) + 1);

			if (!hasUnusedJSPTerm(
					fileName, content, "\\W" + className + "[^\\w\"]", "class",
					checkedFileNames, includeFileNames, getContentsMap())) {

				continue;
			}

			unneededImports.add(importLine);
		}
	}

	private void _addJSPUnusedTaglibs(
		String fileName, String content, List<String> taglibLines,
		List<String> unneededTaglibs) {

		Set<String> checkedFileNames = new HashSet<>();
		Set<String> includeFileNames = new HashSet<>();

		for (String taglibLine : taglibLines) {
			int x = taglibLine.indexOf("prefix=\"");

			int y = taglibLine.indexOf(CharPool.QUOTE, x + 8);

			if ((x == -1) || (y == -1)) {
				continue;
			}

			String prefix = taglibLine.substring(x + 8, y);

			String regex = StringBundler.concat(
				StringPool.LESS_THAN, prefix, ":|\\$\\{", prefix,
				StringPool.COLON);

			if (!hasUnusedJSPTerm(
					fileName, content, regex, "taglib", checkedFileNames,
					includeFileNames, getContentsMap())) {

				continue;
			}

			unneededTaglibs.add(taglibLine);
		}
	}

	private List<String> _getJSPDuplicateImports(
		String fileName, String content, List<String> importLines) {

		List<String> duplicateImports = new ArrayList<>();

		for (String importLine : importLines) {
			int x = content.indexOf("<%@ include file=");

			if (x == -1) {
				continue;
			}

			int y = content.indexOf("<%@ page import=");

			if (y == -1) {
				y = content.indexOf("<%@ tag import=");

				if (y == -1) {
					continue;
				}
			}

			if ((x >= y) ||
				!_isJSPDuplicateImport(fileName, importLine, false)) {

				continue;
			}

			duplicateImports.add(importLine);
		}

		return duplicateImports;
	}

	private List<String> _getJSPDuplicateTaglibs(
		String fileName, String content, List<String> taglibLines) {

		List<String> duplicateTaglibs = new ArrayList<>();

		for (String taglibLine : taglibLines) {
			int x = content.indexOf("<%@ include file=");

			if (x == -1) {
				continue;
			}

			int y = content.indexOf("<%@ taglib uri=");

			if ((y == -1) || (x >= y) ||
				!_isJSPDuplicateTaglib(fileName, taglibLine, false)) {

				continue;
			}

			duplicateTaglibs.add(taglibLine);
		}

		return duplicateTaglibs;
	}

	private String _getVariableName(String line) {
		Matcher matcher = _variableDeclarationPattern.matcher(line);

		if (!matcher.find()) {
			return null;
		}

		String typeName = matcher.group(1);

		if (!typeName.equals("break") && !typeName.equals("continue") &&
			!typeName.equals("return") && !typeName.equals("throw")) {

			return matcher.group(2);
		}

		return null;
	}

	private boolean _hasUnusedPortletDefineObjectsProperty(
		String fileName, String content, String portletDefineObjectProperty,
		Set<String> checkedFileNames, Set<String> includeFileNames) {

		return hasUnusedJSPTerm(
			fileName, content, "\\W" + portletDefineObjectProperty + "\\W",
			"portletDefineObjectProperty", checkedFileNames, includeFileNames,
			getContentsMap());
	}

	private boolean _hasUnusedVariable(
		String fileName, String content, String line, int lineNumber,
		Set<String> checkedFileNames, Set<String> includeFileNames) {

		String variableName = _getVariableName(line);

		if (variableName == null) {
			return false;
		}

		return hasUnusedJSPTerm(
			fileName, content, "\\W" + variableName + "\\W", lineNumber,
			"variable", checkedFileNames, includeFileNames, getContentsMap());
	}

	private boolean _isJSPDuplicateDefineObjects(
		String fileName, String defineObjects, boolean checkFile) {

		Map<String, String> contentsMap = getContentsMap();

		String content = contentsMap.get(fileName);

		if (Validator.isNull(content)) {
			return false;
		}

		if (checkFile && content.contains(defineObjects)) {
			return true;
		}

		int x = content.indexOf("<%@ include file=");

		if (x == -1) {
			return false;
		}

		x = content.indexOf(CharPool.QUOTE, x);

		if (x == -1) {
			return false;
		}

		int y = content.indexOf(CharPool.QUOTE, x + 1);

		if (y == -1) {
			return false;
		}

		String includeFileName = content.substring(x + 1, y);

		includeFileName = JSPSourceUtil.buildFullPathIncludeFileName(
			fileName, includeFileName, getContentsMap());

		return _isJSPDuplicateDefineObjects(
			includeFileName, defineObjects, true);
	}

	private boolean _isJSPDuplicateImport(
		String fileName, String importLine, boolean checkFile) {

		Map<String, String> contentsMap = getContentsMap();

		String content = contentsMap.get(fileName);

		if (Validator.isNull(content)) {
			return false;
		}

		int x = importLine.indexOf("page");

		if (x == -1) {
			x = importLine.indexOf("tag");

			if (x == -1) {
				return false;
			}
		}

		if (checkFile && content.contains(importLine.substring(x))) {
			return true;
		}

		int y = content.indexOf("<%@ include file=");

		if (y == -1) {
			return false;
		}

		y = content.indexOf(CharPool.QUOTE, y);

		if (y == -1) {
			return false;
		}

		int z = content.indexOf(CharPool.QUOTE, y + 1);

		if (z == -1) {
			return false;
		}

		String includeFileName = content.substring(y + 1, z);

		includeFileName = JSPSourceUtil.buildFullPathIncludeFileName(
			fileName, includeFileName, getContentsMap());

		return _isJSPDuplicateImport(includeFileName, importLine, true);
	}

	private boolean _isJSPDuplicateTaglib(
		String fileName, String taglibLine, boolean checkFile) {

		Map<String, String> contentsMap = getContentsMap();

		String content = contentsMap.get(fileName);

		if (Validator.isNull(content)) {
			return false;
		}

		int x = taglibLine.indexOf("taglib");

		if (x == -1) {
			return false;
		}

		if (checkFile && content.contains(taglibLine.substring(x))) {
			return true;
		}

		int y = content.indexOf("<%@ include file=");

		if (y == -1) {
			return false;
		}

		y = content.indexOf(CharPool.QUOTE, y);

		if (y == -1) {
			return false;
		}

		int z = content.indexOf(CharPool.QUOTE, y + 1);

		if (z == -1) {
			return false;
		}

		String includeFileName = content.substring(y + 1, z);

		includeFileName = JSPSourceUtil.buildFullPathIncludeFileName(
			fileName, includeFileName, getContentsMap());

		return _isJSPDuplicateTaglib(includeFileName, taglibLine, true);
	}

	private String _removeDuplicateDefineObjects(
		String fileName, String content) {

		Matcher matcher = _defineObjectsPattern.matcher(content);

		while (matcher.find()) {
			if (_isJSPDuplicateDefineObjects(
					fileName, matcher.group(), false)) {

				return StringUtil.replaceFirst(
					content, matcher.group(), StringPool.BLANK,
					matcher.start());
			}
		}

		return content;
	}

	private String _removeUnusedImports(String fileName, String content)
		throws IOException {

		if (fileName.endsWith("init-ext.jsp")) {
			return content;
		}

		Matcher matcher = _compressedJSPImportPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String imports = matcher.group();

		String newImports = StringUtil.replace(
			imports, new String[] {"<%@\r\n", "<%@\n", " %><%@ "},
			new String[] {"\r\n<%@ ", "\n<%@ ", " %>\n<%@ "});

		List<String> importLines = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new UnsyncStringReader(newImports));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (!line.contains("import=")) {
				continue;
			}

			importLines.add(line);
		}

		List<String> unneededImports = _getJSPDuplicateImports(
			fileName, content, importLines);

		_addJSPUnusedImports(fileName, content, importLines, unneededImports);

		for (String unneededImport : unneededImports) {
			newImports = StringUtil.removeSubstring(newImports, unneededImport);
		}

		return StringUtil.replaceFirst(content, imports, newImports);
	}

	private String _removeUnusedPortletDefineObjects(
		String fileName, String content) {

		if (!content.contains("<portlet:defineObjects />\n")) {
			return content;
		}

		Set<String> checkedFileNames = new HashSet<>();
		Set<String> includeFileNames = new HashSet<>();

		for (String portletDefineObjectProperty :
				_PORTLET_DEFINE_OBJECTS_PROPERTIES) {

			if (!_hasUnusedPortletDefineObjectsProperty(
					fileName, content, portletDefineObjectProperty,
					checkedFileNames, includeFileNames)) {

				return content;
			}
		}

		return StringUtil.removeSubstring(content, "<portlet:defineObjects />");
	}

	private String _removeUnusedTaglibs(String fileName, String content)
		throws IOException {

		if (fileName.endsWith("init-ext.jsp")) {
			return content;
		}

		Matcher matcher = _compressedJSPTaglibPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String taglibs = matcher.group();

		String newTaglibs = StringUtil.replace(
			taglibs, new String[] {"<%@\r\n", "<%@\n", " %><%@ "},
			new String[] {"\r\n<%@ ", "\n<%@ ", " %>\n<%@ "});

		List<String> taglibLines = new ArrayList<>();

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new UnsyncStringReader(newTaglibs));

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (!line.contains("uri=")) {
				continue;
			}

			taglibLines.add(line);
		}

		List<String> unneededTaglibs = _getJSPDuplicateTaglibs(
			fileName, content, taglibLines);

		_addJSPUnusedTaglibs(fileName, content, taglibLines, unneededTaglibs);

		for (String unneededTaglib : unneededTaglibs) {
			newTaglibs = StringUtil.removeSubstring(newTaglibs, unneededTaglib);
		}

		return StringUtil.replaceFirst(content, taglibs, newTaglibs);
	}

	private String _removeUnusedVariables(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (absolutePath.contains("/src/main/resources/alloy_mvc/jsp/") &&
			absolutePath.endsWith(".jspf")) {

			return content;
		}

		Set<String> checkedFileNames = new HashSet<>();
		Set<String> includeFileNames = new HashSet<>();

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			int lineNumber = 0;

			String line = null;

			boolean javaSource = false;
			boolean unusedVariable = false;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				String trimmedLine = StringUtil.trimLeading(line);

				if (trimmedLine.equals("<%") || trimmedLine.equals("<%!")) {
					javaSource = true;
				}
				else if (trimmedLine.equals("%>")) {
					javaSource = false;
				}

				if (javaSource &&
					!isExcludedPath(
						_UNUSED_VARIABLES_EXCLUDES, absolutePath, lineNumber) &&
					_hasUnusedVariable(
						fileName, content, trimmedLine, lineNumber,
						checkedFileNames, includeFileNames)) {

					unusedVariable = true;
				}

				if (unusedVariable) {
					if (trimmedLine.endsWith(";")) {
						unusedVariable = false;
					}

					continue;
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private static final String[] _PORTLET_DEFINE_OBJECTS_PROPERTIES = {
		"actionParams", "actionRequest", "actionResponse", "clientDataRequest",
		"contextPath", "cookies", "eventRequest", "eventResponse",
		"liferayPortletRequest", "liferayPortletResponse", "locale", "locales",
		"mutableRenderParams", "namespace", "portletConfig", "portletContext",
		"portletMode", "portletName", "portletPreferences",
		"portletPreferencesValues", "portletRequest", "portletResponse",
		"portletSession", "portletSessionScope", "renderParams",
		"renderResponse", "renderRequest", "resourceParams", "resourceRequest",
		"resourceResponse", "stateAwareResponse", "windowId", "windowState"
	};

	private static final String _UNUSED_VARIABLES_EXCLUDES =
		"jsp.unused.variables.excludes";

	private static final Pattern _compressedJSPImportPattern = Pattern.compile(
		"(<.*\n*(?:page|tag) import=\".*>\n*)+", Pattern.MULTILINE);
	private static final Pattern _compressedJSPTaglibPattern = Pattern.compile(
		"(<.*\n*taglib uri=\".*>\n*)+", Pattern.MULTILINE);
	private static final Pattern _defineObjectsPattern = Pattern.compile(
		"<[\\w-]+:defineObjects />");
	private static final Pattern _variableDeclarationPattern = Pattern.compile(
		"^([\\w<>,\\s]+?)\\s(\\w+)( =\\s|;)");

}