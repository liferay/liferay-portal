/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaStringBundlerInitialCapacityCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		return _fixInitialCapacity(javaTerm.getContent());
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD};
	}

	private int _addCountForStatements(
		int count, String s, String varName, String start,
		String nextStatementString, int level) {

		if (count == -1) {
			return count;
		}

		int x = -1;

		while (true) {
			x = s.indexOf(start, x + 1);

			if (x == -1) {
				break;
			}

			if (getLevel(s.substring(0, x), "{", "}") != level) {
				continue;
			}

			List<String> parts = _getStatementParts(
				s.substring(x), nextStatementString);

			int maxCount = 0;

			for (String part : parts) {
				if (part.contains(varName + ".append(") &&
					part.contains("return")) {

					return -1;
				}

				int partCount = _getCount(part, varName, 1);

				if (partCount <= maxCount) {
					continue;
				}

				maxCount = partCount;
			}

			count += maxCount;
		}

		return count;
	}

	private String _fixInitialCapacity(String content) {
		Matcher matcher = _stringBundlerPattern.matcher(content);

		while (matcher.find()) {
			String tabs = matcher.group(1);
			String varName = matcher.group(3);

			if (matcher.group(2) == null) {
				int x = content.lastIndexOf(
					"StringBundler " + varName, matcher.start());

				String line = getLine(content, getLineNumber(content, x));

				if (!line.startsWith(tabs)) {
					continue;
				}
			}

			int x = content.indexOf(
				"\n" + tabs.substring(1) + "}", matcher.end());

			if (x == -1) {
				continue;
			}

			String s = content.substring(matcher.end(), x);

			x = s.indexOf("\t" + varName + " = new StringBundler");

			if (x != -1) {
				s = s.substring(0, x);
			}

			if (_hasAppendCallInsideLoop(s, varName) ||
				s.matches("(?s).*\\W" + varName + "([,)]|\\.index\\().*")) {

				continue;
			}

			int count = _getCount(s, varName, 0);

			if (count == -1) {
				continue;
			}

			int sbInitialCapacity = GetterUtil.getInteger(matcher.group(5));

			if ((sbInitialCapacity > count) ||
				((sbInitialCapacity != count) &&
				 !s.contains(varName + ".setIndex"))) {

				return StringUtil.replaceFirst(
					content, matcher.group(4), "(" + String.valueOf(count),
					matcher.start());
			}
		}

		return content;
	}

	private int _getCount(String s, String varName, int level) {
		int count = 0;

		int x = -1;

		while (true) {
			x = s.indexOf(varName + ".append(", x + 1);

			if (x == -1) {
				break;
			}

			if (ToolsUtil.isInsideQuotes(s, x) ||
				(getLevel(s.substring(0, x), "{", "}") != level)) {

				continue;
			}

			count++;
		}

		count = _addCountForStatements(
			count, s, varName, "\tif (", "else ", level);
		count = _addCountForStatements(
			count, s, varName, "\ttry (", "catch ", level);
		count = _addCountForStatements(
			count, s, varName, "\ttry {", "catch ", level);
		count = _addCountForStatements(
			count, s, varName, " -> {\n", null, level);

		return count;
	}

	private List<String> _getStatementParts(
		String s, String nextStatementString) {

		List<String> parts = new ArrayList<>();

		int x = -1;

		while (true) {
			x = s.indexOf("}", x + 1);

			if (x == -1) {
				return parts;
			}

			if (ToolsUtil.isInsideQuotes(s, x)) {
				continue;
			}

			String part = s.substring(0, x + 1);

			if (getLevel(part, "{", "}") != 0) {
				continue;
			}

			parts.add(part);

			s = StringUtil.trim(s.substring(x + 1));

			if ((nextStatementString == null) ||
				!s.startsWith(nextStatementString)) {

				return parts;
			}

			x = -1;
		}
	}

	private boolean _hasAppendCallInsideLoop(String s, String varName) {
		Matcher matcher = _loopPattern.matcher(s);

		while (matcher.find()) {
			int x = matcher.start();

			int y = x;

			while (true) {
				y = s.indexOf("}", y + 1);

				if (y == -1) {
					return true;
				}

				String insideLoop = s.substring(x, y + 1);

				if (getLevel(insideLoop, "{", "}") != 0) {
					continue;
				}

				if (insideLoop.contains(varName + ".append(")) {
					return true;
				}

				break;
			}
		}

		return false;
	}

	private static final Pattern _loopPattern = Pattern.compile(
		"\t(do \\{|(for|while) \\()");
	private static final Pattern _stringBundlerPattern = Pattern.compile(
		"\n(\t+)(StringBundler )?(\\w+) = new StringBundler(\\(([0-9]+)?)\\)" +
			";\n");

}