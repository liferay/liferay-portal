/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import com.liferay.poshi.core.util.GetterUtil;
import com.liferay.poshi.core.util.StringPool;
import com.liferay.poshi.core.util.StringUtil;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 */
public class RuntimeVariables {

	public static String evaluateLocator(
			String locator, Map<String, String> context)
		throws Exception {

		String locatorValue = locator;

		if (locatorValue.contains("${") && locatorValue.contains("}")) {
			String regex = "\\$\\{([^}]*?)\\}";

			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(locatorValue);

			while (matcher.find()) {
				String variableKey = matcher.group(1);

				String variableValue = context.get(variableKey);

				if (variableValue != null) {
					locatorValue = locatorValue.replaceFirst(
						regex, variableValue);
				}
				else {
					throw new Exception(
						"Variable \"" + variableKey + "\" found in \"" +
							locator + "\" is not set");
				}
			}
		}

		return locatorValue;
	}

	public static String evaluateVariable(
		String value, Map<String, String> context) {

		String varValue = value;

		Matcher matcher = _variablePattern.matcher(varValue);

		while (matcher.find()) {
			String statement = matcher.group(1);

			Matcher statementMatcher = _variableStatementPattern.matcher(
				statement);

			if (statementMatcher.find()) {
				String operand = statementMatcher.group(1);

				if (!context.containsKey(operand)) {
					continue;
				}

				String[] arguments = StringUtil.split(
					statementMatcher.group(3), "'");

				List<String> argumentsList = new ArrayList<>();

				for (int i = 1; i < arguments.length; i++) {
					if ((i % 2) == 1) {
						argumentsList.add(arguments[i]);
					}
				}

				String method = statementMatcher.group(2);

				String operandValue = context.get(operand);

				String replaceRegex = "\\$\\{([^}]*?)\\}";

				String result = "";

				if (method.startsWith("getFirstNumber")) {
					result = operandValue.replaceFirst("\\D*(\\d*).*", "$1");
				}
				else if (method.startsWith("increment")) {
					int i = GetterUtil.getInteger(operandValue) + 1;

					result = String.valueOf(i);
				}
				else if (method.startsWith("length")) {
					result = String.valueOf(operandValue.length());
				}
				else if (method.startsWith("lowercase")) {
					result = StringUtil.toLowerCase(operandValue);
				}
				else if (method.startsWith("replace")) {
					result = StringUtil.replace(
						operandValue, argumentsList.get(0),
						argumentsList.get(1));
				}
				else if (method.startsWith("uppercase")) {
					result = StringUtil.toUpperCase(operandValue);
				}

				varValue = varValue.replaceFirst(replaceRegex, result);
			}
			else if (statement.equals("getIPAddress()")) {
				try {
					InetAddress inetAddress = InetAddress.getLocalHost();

					String result = inetAddress.getHostAddress();

					varValue = varValue.replaceFirst(
						"\\$\\{([^}]*?)\\}", result);
				}
				catch (Exception exception) {
				}
			}
			else {
				String varName = statement;

				if (!context.containsKey(varName)) {
					continue;
				}

				String replaceRegex = "\\$\\{([^}]*?)\\}";

				String result = context.get(varName);

				result = Matcher.quoteReplacement(result);

				varValue = varValue.replaceFirst(replaceRegex, result);
			}
		}

		varValue = StringUtil.replace(varValue, "\\$", "$");
		varValue = StringUtil.replace(varValue, "\\{", "{");
		varValue = StringUtil.replace(varValue, "\\}", "}");

		return varValue;
	}

	public static String getValue(String key) {
		return _instance._getValue(key);
	}

	public static boolean isVariableSet(
		String varName, Map<String, String> context) {

		if (!context.containsKey(varName)) {
			return false;
		}

		String varValue = context.get(varName);

		varValue = StringUtil.replace(varValue, "${line.separator}", "");

		if (varValue.contains("${") && varValue.contains("}")) {
			return false;
		}

		return true;
	}

	public static String replace(String text) {
		return _instance._replace(text);
	}

	public static String replaceRegularExpression(
		String content, String regex, int group) {

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(group);
		}

		return StringPool.BLANK;
	}

	public static void setValue(String key, String value) {
		_instance._setValue(key, value);
	}

	private String _getValue(String key) {
		return _runtimeVariables.get(key);
	}

	private String _replace(String text) {
		if (_contextReplace == null) {
			return text;
		}

		return _contextReplace.replace(text);
	}

	private void _setValue(String key, String value) {
		_runtimeVariables.put(key, value);

		_contextReplace = new ContextReplace(_runtimeVariables);
	}

	private static final RuntimeVariables _instance = new RuntimeVariables();

	private static final Pattern _variablePattern = Pattern.compile(
		"\\$\\{([^}]*?)\\}");
	private static final Pattern _variableStatementPattern = Pattern.compile(
		"(.*)\\?(.*)\\(([^\\)]*?)\\)");

	private ContextReplace _contextReplace;
	private final Map<String, String> _runtimeVariables = new HashMap<>();

}