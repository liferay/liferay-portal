/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JSPSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseJSPTermsCheck extends BaseFileCheck {

	@Override
	public void setAllFileNames(List<String> allFileNames) {
		_allFileNames = allFileNames;
	}

	protected Map<String, String> getContentsMap() {
		return _contentsMap;
	}

	protected Set<String> getMissingTaglibPrefixes(
		String fileName, Set<String> taglibPrefixes) {

		List<String> dependentfileNames = new ArrayList<>();

		dependentfileNames.add(fileName);

		Map<String, String> contentsMap = getContentsMap();

		dependentfileNames = JSPSourceUtil.addIncludedAndReferencedFileNames(
			dependentfileNames, new HashSet<String>(), contentsMap,
			".*\\.jspf");

		if (fileName.endsWith(".jspf") && (dependentfileNames.size() == 1)) {
			return Collections.emptySet();
		}

		for (String dependentfileName : dependentfileNames) {
			dependentfileName = StringUtil.replace(
				dependentfileName, CharPool.BACK_SLASH, CharPool.SLASH);

			String dependenFileContent = contentsMap.get(dependentfileName);

			if (dependenFileContent == null) {
				return Collections.emptySet();
			}

			Iterator<String> iterator = taglibPrefixes.iterator();

			while (iterator.hasNext()) {
				String prefix = iterator.next();

				if (dependenFileContent.contains("prefix=\"" + prefix + "\"")) {
					iterator.remove();

					if (taglibPrefixes.isEmpty()) {
						return taglibPrefixes;
					}
				}
			}
		}

		return taglibPrefixes;
	}

	protected boolean hasUnusedJSPTerm(
		String fileName, String content, String regex, int lineNumber,
		String type, Set<String> checkedForIncludesFileNames,
		Set<String> includeFileNames, Map<String, String> contentsMap) {

		includeFileNames.add(fileName);

		Set<String> checkedForUnusedJSPTerm = new HashSet<>();

		return !_isJSPTermRequired(
			fileName, content, regex, lineNumber, type, checkedForUnusedJSPTerm,
			checkedForIncludesFileNames, includeFileNames, contentsMap);
	}

	protected boolean hasUnusedJSPTerm(
		String fileName, String content, String regex, String type,
		Set<String> checkedForIncludesFileNames, Set<String> includeFileNames,
		Map<String, String> contentsMap) {

		return hasUnusedJSPTerm(
			fileName, content, regex, -1, type, checkedForIncludesFileNames,
			includeFileNames, contentsMap);
	}

	protected boolean hasVariableReference(
		String content, String value, int pos) {

		if (pos == -1) {
			return false;
		}

		pos = content.indexOf("\n", pos);

		Matcher methodCallMatcher = _methodCallPattern.matcher(value);

		while (methodCallMatcher.find()) {
			Pattern pattern = Pattern.compile(
				"\\b(?<!['\"])" + methodCallMatcher.group(1) + "\\.(\\w+)?\\(");

			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				if (matcher.start() > pos) {
					break;
				}

				String methodName = matcher.group(1);

				if (!methodName.startsWith("get") &&
					!methodName.startsWith("is")) {

					return true;
				}
			}
		}

		return false;
	}

	protected synchronized void populateContentsMap(
			String fileName, String content)
		throws IOException {

		if (_contentsMap != null) {
			return;
		}

		List<String> allJSPFileNames = SourceFormatterUtil.filterFileNames(
			_allFileNames, new String[] {"**/null.jsp", "**/tools/**"},
			new String[] {"**/*.jsp", "**/*.jspf", "**/*.tag"},
			getSourceFormatterExcludes(), true);

		_contentsMap = JSPSourceUtil.getContentsMap(allJSPFileNames);

		// When running tests, the contentsMap is empty, because the file
		// extension of the test files is *.testjsp

		if (_contentsMap.isEmpty()) {
			_contentsMap.put(fileName, content);
		}
	}

	protected void put(String fileName, String content) {
		_contentsMap.put(fileName, content);
	}

	private String _getRangeContent(String content, int lineNumber) {
		String line = getLine(content, lineNumber);

		String indent = SourceUtil.getIndent(line);

		if (indent.length() == 0) {
			return content;
		}

		int curLineNumber = lineNumber - 1;

		while (true) {
			if (curLineNumber == 0) {
				break;
			}

			String trimmedLine = StringUtil.trim(
				getLine(content, curLineNumber));

			if (trimmedLine.equals("<%")) {
				break;
			}

			if (trimmedLine.equals("<%!")) {
				return content;
			}

			curLineNumber--;
		}

		StringBundler sb = new StringBundler();

		sb.append("<%\n");
		sb.append(line);

		while (true) {
			lineNumber++;

			line = getLine(content, lineNumber);

			if (line == null) {
				if (indent.length() == 0) {
					return sb.toString();
				}

				return content;
			}

			if (Validator.isNotNull(line) && !line.startsWith(indent)) {
				String trimmedLine = StringUtil.trim(line);

				if (trimmedLine.startsWith("}")) {
					sb.append("%>/n");

					return sb.toString();
				}

				if (trimmedLine.matches("</[\\w-]+:[\\w-]+>")) {
					return sb.toString();
				}

				if (trimmedLine.matches("</[\\w-]+>")) {
					indent = indent.substring(1);
				}
			}

			sb.append(line);
			sb.append("\n");
		}
	}

	private boolean _isInsideComment(String content, int pos) {
		String s = content.substring(pos);

		int x = s.indexOf("*/");

		if (x == -1) {
			return false;
		}

		String trimmedLine = StringUtil.trim(
			getLine(
				content,
				getLineNumber(content, pos) + getLineNumber(s, x) - 1));

		if (!trimmedLine.endsWith("*/")) {
			return false;
		}

		s = s.substring(0, x);

		return !s.contains("/*");
	}

	private boolean _isJSPTermRequired(
		String fileName, String content, String regex, int lineNumber,
		String type, Set<String> checkedForUnusedJSPTerm,
		Set<String> checkedForIncludesFileNames, Set<String> includeFileNames,
		Map<String, String> contentsMap) {

		if (checkedForUnusedJSPTerm.contains(fileName)) {
			return false;
		}

		checkedForUnusedJSPTerm.add(fileName);

		if (content == null) {
			content = contentsMap.get(fileName);
		}

		if (Validator.isNull(content)) {
			return false;
		}

		if (lineNumber != -1) {
			content = _getRangeContent(content, lineNumber);
		}

		int count = 0;

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (type.equals("taglib")) {
				count++;

				continue;
			}

			int x = matcher.start() + 1;

			if (_isInsideComment(content, x)) {
				continue;
			}

			if (isJavaSource(content, x)) {
				if (!ToolsUtil.isInsideQuotes(content, x)) {
					count++;
				}

				continue;
			}

			String line = StringUtil.trim(
				getLine(content, getLineNumber(content, matcher.start())));

			if (line.startsWith("function ")) {
				continue;
			}

			int y = content.lastIndexOf("<%", x) + 2;
			int z = content.lastIndexOf("%>", x);

			if ((y == 1) || (z > y)) {
				continue;
			}

			z = content.indexOf("%>", x);

			if ((z == -1) ||
				(getLineNumber(content, y) != getLineNumber(content, z)) ||
				!ToolsUtil.isInsideQuotes(
					content.substring(y, z), x - y, false)) {

				count++;
			}
		}

		if ((count > 1) ||
			((count == 1) &&
			 (!type.equals("variable") ||
			  (checkedForUnusedJSPTerm.size() > 1)))) {

			return true;
		}

		if (!checkedForIncludesFileNames.contains(fileName)) {
			includeFileNames.addAll(
				JSPSourceUtil.getJSPIncludeFileNames(
					fileName, includeFileNames, contentsMap, false));
			includeFileNames.addAll(
				JSPSourceUtil.getJSPReferenceFileNames(
					fileName, includeFileNames, contentsMap,
					".*\\.(jsp|jspf|tag)"));
		}

		checkedForIncludesFileNames.add(fileName);

		String[] includeFileNamesArray = includeFileNames.toArray(
			new String[0]);

		for (String includeFileName : includeFileNamesArray) {
			if (!checkedForUnusedJSPTerm.contains(includeFileName) &&
				_isJSPTermRequired(
					includeFileName, null, regex, -1, type,
					checkedForUnusedJSPTerm, checkedForIncludesFileNames,
					includeFileNames, contentsMap)) {

				return true;
			}
		}

		return false;
	}

	private static final Pattern _methodCallPattern = Pattern.compile(
		"\\b(?<!['\"])([a-z]\\w+)\\.(\\w+)?\\(");

	private List<String> _allFileNames;
	private Map<String, String> _contentsMap;

}