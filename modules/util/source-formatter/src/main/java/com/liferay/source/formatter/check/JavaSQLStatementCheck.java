/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaSQLStatementCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		for (String methodName : _METHOD_NAMES) {
			int x = -1;

			while (true) {
				x = content.indexOf(methodName + "(", x + 1);

				if (x == -1) {
					break;
				}

				if (ToolsUtil.isInsideQuotes(content, x)) {
					continue;
				}

				String methodCall = JavaSourceUtil.getMethodCall(content, x);

				List<String> parameterList = JavaSourceUtil.getParameterList(
					methodCall);

				if (parameterList.isEmpty()) {
					continue;
				}

				List<String> sqlStatementParts = _splitSQLStatementParts(
					content, methodCall, methodName, parameterList, x);

				if (sqlStatementParts.isEmpty()) {
					continue;
				}

				int lineNumber = getLineNumber(content, x);

				_checkSQLBooleanValues(fileName, sqlStatementParts, lineNumber);

				if (!methodName.equals("runSQL") &&
					!methodName.equals("StringBundler.concat")) {

					_checkMissingTransformCall(
						fileName, methodCall, sqlStatementParts, lineNumber);
				}

				if (methodName.equals("connection.prepareStatement") ||
					methodName.startsWith("AutoBatchPreparedStatementUtil")) {

					_checkMissingParameterizedStatement(
						fileName, sqlStatementParts, lineNumber);
				}
			}
		}

		return content;
	}

	private void _checkMissingParameterizedStatement(
		String fileName, List<String> sqlStatementParts, int lineNumber) {

		for (int i = 0; i < (sqlStatementParts.size() - 1); i++) {
			String sqlStatementPart = sqlStatementParts.get(i);

			if (!sqlStatementPart.matches("\".+ *= *'?\"")) {
				continue;
			}

			String nextSQLStatementPart = sqlStatementParts.get(i + 1);

			if (nextSQLStatementPart.contains("\"[$FALSE$]\"") ||
				nextSQLStatementPart.contains("\"[$TRUE$]\"") ||
				nextSQLStatementPart.endsWith(".getNewUuidFunctionName()")) {

				continue;
			}

			if (i < (sqlStatementParts.size() - 2)) {
				String nextNextSQLStatementPart = sqlStatementParts.get(i + 2);

				if (nextNextSQLStatementPart.endsWith("\"") &&
					nextNextSQLStatementPart.startsWith("\".")) {

					continue;
				}
			}

			addMessage(
				fileName,
				"Use \"PreparedStatement.set*\" to parameterize \"" +
					nextSQLStatementPart + "\"",
				lineNumber);
		}
	}

	private void _checkMissingTransformCall(
		String fileName, String methodCall, List<String> sqlStatementParts,
		int lineNumber) {

		for (String sqlStatementPart : sqlStatementParts) {
			if (!sqlStatementPart.endsWith("\"") ||
				!sqlStatementPart.startsWith("\"")) {

				continue;
			}

			int index = StringUtil.indexOfAny(
				sqlStatementPart, new String[] {"[$FALSE$]", "[$TRUE$]"});

			if ((index == -1) ||
				methodCall.contains("SQLTransformer.transform(")) {

				return;
			}

			addMessage(
				fileName,
				"Use \"SQLTransformer.transform\" to wrap SQL statement if " +
					"it contains \"[$FALSE$]\" or \"[$TRUE$]\"",
				lineNumber);
		}
	}

	private void _checkSQLBooleanValues(
		String fileName, List<String> sqlStatementParts, int lineNumber) {

		for (String sqlStatementPart : sqlStatementParts) {
			if (!sqlStatementPart.endsWith("\"") ||
				!sqlStatementPart.startsWith("\"")) {

				continue;
			}

			Matcher matcher = _falseTruePattern.matcher(sqlStatementPart);

			while (matcher.find()) {
				String match = matcher.group(1);

				String s1 = sqlStatementPart.substring(0, matcher.start(1));
				String s2 = sqlStatementPart.substring(matcher.start(1));

				int count1 = StringUtil.count(s1, CharPool.APOSTROPHE) % 2;
				int count2 = StringUtil.count(s2, CharPool.APOSTROPHE) % 2;

				if ((count1 == 1) && (count2 == 1)) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Use \"[$", StringUtil.toUpperCase(match),
						"$]\" instead of \"", match, "\" in SQL statements"),
					lineNumber);
			}
		}
	}

	private List<String> _splitSQLStatementParts(String s) {
		String parametersString = s.trim();

		parametersString = parametersString.replaceAll("\n", "");
		parametersString = parametersString.replaceAll("\\s{2,}", " ");

		if (parametersString.endsWith(")") &&
			parametersString.startsWith("SQLTransformer.transform(")) {

			parametersString = parametersString.substring(
				25, parametersString.length() - 1);
		}

		parametersString = parametersString.trim();

		if (parametersString.endsWith(")") &&
			parametersString.startsWith("StringBundler.concat(")) {

			parametersString = parametersString.substring(
				21, parametersString.length() - 1);

			parametersString = StringUtil.removeSubstring(
				parametersString, "\", \"");

			return _splitSQLStatementParts(parametersString, CharPool.COMMA);
		}

		parametersString = StringUtil.removeSubstring(
			parametersString, "\" + \"");

		return _splitSQLStatementParts(parametersString, CharPool.PLUS);
	}

	private List<String> _splitSQLStatementParts(
		String parameters, char delimiter) {

		List<String> parametersList = new ArrayList<>();

		parameters = StringUtil.trim(parameters);

		if (parameters.equals(StringPool.BLANK)) {
			return parametersList;
		}

		int x = -1;

		while (true) {
			x = parameters.indexOf(delimiter, x + 1);

			if (x == -1) {
				parametersList.add(StringUtil.trim(parameters));

				return parametersList;
			}

			if (ToolsUtil.isInsideQuotes(parameters, x)) {
				continue;
			}

			String linePart = parameters.substring(0, x);

			if ((ToolsUtil.getLevel(linePart, "(", ")") == 0) &&
				(ToolsUtil.getLevel(linePart, "{", "}") == 0)) {

				parametersList.add(StringUtil.trim(linePart));

				parameters = parameters.substring(x + 1);

				x = -1;
			}
		}
	}

	private List<String> _splitSQLStatementParts(
		String content, String methodCall, String methodName,
		List<String> parameterList, int index) {

		int parameterSize = parameterList.size();

		if (methodName.startsWith("AutoBatchPreparedStatementUtil")) {
			if (parameterSize != 2) {
				return Collections.emptyList();
			}

			return _splitSQLStatementParts(parameterList.get(1));
		}
		else if (methodName.equals("connection.prepareStatement")) {
			if (parameterSize != 1) {
				return Collections.emptyList();
			}

			return _splitSQLStatementParts(parameterList.get(0));
		}
		else if (methodName.equals("runSQL")) {
			if (parameterSize == 1) {
				return _splitSQLStatementParts(parameterList.get(0));
			}
			else if (parameterSize == 2) {
				return _splitSQLStatementParts(parameterList.get(1));
			}
		}
		else if (methodName.equals("StringBundler.concat")) {
			String s = StringUtil.trimTrailing(content.substring(0, index));

			if (!s.endsWith("=")) {
				return Collections.emptyList();
			}

			String firstParameter = parameterList.get(0);

			if (!firstParameter.startsWith("\"delete ") &&
				!firstParameter.startsWith("\"insert into ") &&
				!firstParameter.startsWith("\"select ") &&
				!firstParameter.startsWith("\"update ") &&
				!firstParameter.startsWith("\"where ")) {

				return Collections.emptyList();
			}

			return _splitSQLStatementParts(methodCall);
		}

		return Collections.emptyList();
	}

	private static final String[] _METHOD_NAMES = {
		"AutoBatchPreparedStatementUtil.autoBatch",
		"AutoBatchPreparedStatementUtil.autoBatchWithResults",
		"AutoBatchPreparedStatementUtil.concurrentAutoBatch",
		"connection.prepareStatement", "runSQL", "StringBundler.concat"
	};

	private static final Pattern _falseTruePattern = Pattern.compile(
		"=\\s*(false|true)\\b", Pattern.CASE_INSENSITIVE);

}