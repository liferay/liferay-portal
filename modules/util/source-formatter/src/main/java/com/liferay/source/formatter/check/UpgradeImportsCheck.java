/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.JSPImportsFormatter;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 * @author Nícolas Moura
 */
public class UpgradeImportsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java") && !fileName.endsWith(".jsp") &&
			!fileName.endsWith(".ftl")) {

			return content;
		}

		return _fixImports(fileName, content);
	}

	private static String _fixUtilClassReference(
		String className, String newClassName, String content,
		String variableRegex) {

		if (className.endsWith("Util") || !newClassName.endsWith("Util")) {
			return content;
		}

		content = content.replaceAll(variableRegex, newClassName);

		String regex = StringBundler.concat(
			"\\n?\\t@Reference\\s+\\w+\\s+", newClassName, "\\s+[_a-z]*\\w*",
			newClassName, ";\\n?");

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(content);

		return matcher.replaceAll("");
	}

	private static String _getImportName(String className) {
		for (Map.Entry<String, String> entry : _importsMap.entrySet()) {
			if (StringUtil.endsWith(entry.getValue(), className)) {
				return entry.getValue();
			}
		}

		return null;
	}

	private static List<String> _getImportNames(String fileName, String content)
		throws Exception {

		List<String> importNames = new ArrayList<>();

		if (fileName.endsWith(".java")) {
			JavaClass javaClass = JavaClassParser.parseJavaClass(
				fileName, content);

			importNames = javaClass.getImportNames();
		}
		else if (fileName.endsWith(".jsp")) {
			importNames = JSPImportsFormatter.getImportNames(content);
		}
		else {
			Matcher matcher = _ftlImportNamePattern.matcher(content);

			while (matcher.find()) {
				importNames.add(matcher.group(1));
			}
		}

		return importNames;
	}

	private static boolean _isValidReplacement(
			String content, String fileName, Matcher matcher,
			String newClassName, String newClassNameVariableName,
			String className)
		throws Exception {

		if (!StringUtil.equalsIgnoreCase(matcher.group(), newClassName) &&
			!StringUtil.equals(matcher.group(), newClassNameVariableName) &&
			!StringUtil.equalsIgnoreCase(
				matcher.group(), "_" + newClassNameVariableName) &&
			!StringUtil.equals(
				matcher.group(), matcher.group(1) + newClassName)) {

			List<String> importNames = _getImportNames(fileName, content);

			String importName = _getImportName(
				StringUtil.upperCaseFirstLetter(newClassName));

			if ((fileName.endsWith("java") &&
				 ((importName == null) || !content.contains(importName))) ||
				importNames.stream(
				).anyMatch(
					name -> StringUtil.endsWith(
						name, "." + StringUtil.upperCaseFirstLetter(className))
				)) {

				return false;
			}

			return true;
		}

		return false;
	}

	private static String _replaceVariables(
			String content, Map<String, String> variablesMap, String fileName)
		throws Exception {

		if (variablesMap.isEmpty()) {
			return content;
		}

		JavaClass javaClass = null;

		String newContent = content;

		if (fileName.endsWith(".java")) {
			javaClass = JavaClassParser.parseJavaClass(fileName, content);

			newContent = javaClass.getContent();
		}

		for (Map.Entry<String, String> entry : variablesMap.entrySet()) {
			String className = entry.getKey();

			String regex = StringBundler.concat(
				"(?<!\\w-)(\\w*)", className, "(?!\\w)");

			String newClassName = entry.getValue();

			if (newContent.contains("@Reference")) {
				newContent = _fixUtilClassReference(
					className, newClassName, newContent, regex);
			}

			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(newContent);

			String newClassNameVariableName = StringUtil.lowerCaseFirstLetter(
				newClassName);

			while (matcher.find()) {
				if (!_isValidReplacement(
						content, fileName, matcher, newClassName,
						newClassNameVariableName, className)) {

					continue;
				}

				newContent = StringUtil.replaceFirst(
					newContent, matcher.group(),
					matcher.group(1) + newClassName, matcher.start());

				matcher = pattern.matcher(newContent);
			}
		}

		if (javaClass != null) {
			return StringUtil.replace(
				content, javaClass.getContent(), newContent);
		}

		return newContent;
	}

	private synchronized String _fixImports(String fileName, String content)
		throws Exception {

		_importsMap = _getMap("imports.txt");

		for (String importName : _getImportNames(fileName, content)) {
			String newImportName = _importsMap.get(importName);

			if (newImportName == null) {
				continue;
			}

			content = StringUtil.replace(content, importName, newImportName);
		}

		return _replaceVariables(
			content, _getVariablesMap(_importsMap), fileName);
	}

	private Map<String, String> _getMap(String fileName) throws Exception {
		Map<String, String> map = new HashMap<>();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"dependencies/" + fileName);

		if (inputStream == null) {
			return map;
		}

		String[] lines = StringUtil.splitLines(StringUtil.read(inputStream));

		for (String line : lines) {
			int separatorIndex = line.indexOf(StringPool.EQUAL);

			map.put(
				line.substring(0, separatorIndex),
				line.substring(separatorIndex + 1));
		}

		return map;
	}

	private Map<String, String> _getVariablesMap(
		Map<String, String> importsMap) {

		Map<String, String> variablesMap = new HashMap<>();

		for (Map.Entry<String, String> entry : importsMap.entrySet()) {
			String className = SourceFormatterUtil.getSimpleName(
				entry.getKey());
			String newClassName = SourceFormatterUtil.getSimpleName(
				entry.getValue());

			if (!className.equals(newClassName)) {
				variablesMap.put(className, newClassName);

				if (!className.endsWith("Util") &&
					newClassName.endsWith("Util")) {

					variablesMap.put(
						StringUtil.lowerCaseFirstLetter(className),
						newClassName);

					continue;
				}

				variablesMap.put(
					StringUtil.lowerCaseFirstLetter(className),
					StringUtil.lowerCaseFirstLetter(newClassName));
			}
		}

		return variablesMap;
	}

	private static final Pattern _ftlImportNamePattern = Pattern.compile(
		"(?:findService|staticUtil)[(\\[]\"([^\\s\"]+)\"[)\\]]");
	private static Map<String, String> _importsMap;

}