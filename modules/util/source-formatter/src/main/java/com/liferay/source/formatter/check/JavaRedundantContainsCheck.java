/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaRedundantContainsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		Matcher matcher1 = _ifStatementPattern.matcher(content);

		while (matcher1.find()) {
			IfStatement ifStatement = _getIfStatement(
				content, matcher1.start());

			if (ifStatement == null) {
				continue;
			}

			String body = ifStatement.getBody();

			if (Validator.isBlank(body)) {
				continue;
			}

			String clause = ifStatement.getClause();

			Matcher matcher2 = _containsCallPattern.matcher(clause);

			while (matcher2.find()) {
				String variableName = matcher2.group(2);

				if (!variableName.matches("_?[a-z]\\w+")) {
					continue;
				}

				List<String> parameterList = JavaSourceUtil.getParameterList(
					JavaSourceUtil.getMethodCall(clause, matcher2.start(2)));

				if (parameterList.isEmpty()) {
					continue;
				}

				String parameter = parameterList.get(0);

				if (_hasTopLevelComma(parameter)) {
					continue;
				}

				_checkMethodCallInIfBody(
					fileName, content, body, matcher2, parameter,
					getLineNumber(content, matcher1.start()));
			}
		}

		return content;
	}

	private void _checkMethodCallInIfBody(
		String fileName, String content, String s, Matcher matcher,
		String parameter, int lineNumber) {

		String firstStatement = _getFirstStatement(s);

		if (firstStatement == null) {
			return;
		}

		boolean negated = false;

		if (matcher.group(1) != null) {
			negated = true;
		}

		String variableName = matcher.group(2);

		String variableTypeName = getVariableTypeName(
			content, null, content, fileName, variableName, true, false);

		if (variableTypeName == null) {
			return;
		}

		String operation = null;
		String suggestion = null;

		if (StringUtil.equals(matcher.group(3), "containsKey") &&
			variableTypeName.matches("\\w*Map<.+")) {

			if (negated &&
				_hasOperation(firstStatement, variableName, "put", parameter)) {

				operation = "put";
				suggestion = "a single \"putIfAbsent\" or \"computeIfAbsent\"";
			}
			else if (_hasOperation(
						firstStatement, variableName, "get", parameter)) {

				operation = "get";
				suggestion = "a single \"get\" with a null check";
			}
			else if (_hasOperation(
						firstStatement, variableName, "remove", parameter)) {

				operation = "remove";
				suggestion = "a single \"remove\" with a null check";
			}
		}
		else if (StringUtil.equals(matcher.group(3), "contains") &&
				 variableTypeName.matches("\\w*Set<.*")) {

			if (negated &&
				_hasOperation(firstStatement, variableName, "add", parameter)) {

				operation = "add";
				suggestion = "the boolean result of a single \"add\"";
			}
			else if (_hasOperation(
						firstStatement, variableName, "remove", parameter)) {

				operation = "remove";
				suggestion = "the boolean result of a single \"remove\"";
			}
		}

		if ((operation == null) || (suggestion == null)) {
			return;
		}

		addMessage(
			fileName,
			StringBundler.concat(
				"Combine the \"", matcher.group(3), "\" check on \"",
				variableName, "\" and the following \"", operation, "\" into ",
				suggestion),
			lineNumber);
	}

	private int _getClosePos(
		String content, String openChar, String closeChar, int start) {

		int closePos = start;

		while (true) {
			closePos = content.indexOf(closeChar, closePos + 1);

			if (closePos == -1) {
				return -1;
			}

			String s = content.substring(start, closePos + 1);

			int level = getLevel(s, openChar, closeChar);

			if (level == 0) {
				return closePos;
			}

			if (level == -1) {
				return -1;
			}
		}
	}

	private String _getFirstStatement(String s) {
		int level = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((c == '(') || (c == '{')) {
				level++;
			}
			else if ((c == ')') || (c == '}')) {
				if ((c == '}') && (level == 0)) {
					return s.substring(0, i);
				}

				level--;
			}
			else if ((c == ';') && (level == 0)) {
				return s.substring(0, i + 1);
			}
		}

		return null;
	}

	private IfStatement _getIfStatement(String content, int pos) {
		int x = _getClosePos(content, "(", ")", pos);

		if ((x == -1) || !Objects.equals(content.substring(x, x + 3), ") {")) {
			return null;
		}

		int y = _getClosePos(content, "{", "}", x + 1);

		if (y == -1) {
			return null;
		}

		return new IfStatement(
			StringUtil.trim(content.substring(x + 3, y)),
			content.substring(content.indexOf("(", pos), x + 1));
	}

	private boolean _hasOperation(
		String content, String variableName, String methodName,
		String parameter) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(variableName, "\\.\\s*", methodName, "\\("));

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		String s = content.substring(0, matcher.start());

		if (s.matches("(?s).*\\b" + variableName + "\\b.*")) {
			return false;
		}

		List<String> parameterList = JavaSourceUtil.getParameterList(
			JavaSourceUtil.getMethodCall(content, matcher.start()));

		if (parameterList.isEmpty()) {
			return false;
		}

		return Objects.equals(
			_normalize(parameterList.get(0)), _normalize(parameter));
	}

	private boolean _hasTopLevelComma(String s) {
		int level = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((c == '(') || (c == '<') || (c == '[')) {
				level++;
			}
			else if ((c == ')') || (c == '>') || (c == ']')) {
				level--;
			}
			else if ((c == ',') && (level == 0)) {
				return true;
			}
		}

		return false;
	}

	private String _normalize(String s) {
		return s.replaceAll("\\s+", "");
	}

	private static final Pattern _containsCallPattern = Pattern.compile(
		"\\((!)?(\\w+)\\.(contains(Key)?)\\(");
	private static final Pattern _ifStatementPattern = Pattern.compile(
		"[\n\t]if \\(");

	private static class IfStatement {

		public IfStatement(String body, String clause) {
			_body = body;
			_clause = clause;
		}

		public String getBody() {
			return _body;
		}

		public String getClause() {
			return _clause;
		}

		private final String _body;
		private final String _clause;

	}

}