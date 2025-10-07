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

import java.lang.reflect.Field;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class StringMethodsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws ReflectiveOperationException {

		content = _checkStringUtilContainsCalls(fileName, content);
		content = _checkStringUtilReplaceCalls(fileName, content);

		if (isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath)) {
			_checkStringReplaceCalls(fileName, content);

			return content;
		}

		_checkInefficientStringMethods(
			fileName, content, "(\\w+)\\.equalsIgnoreCase\\(",
			"equalsIgnoreCase");
		_checkInefficientStringMethods(
			fileName, content, "(\\w+)\\.join\\(", "merge");
		_checkInefficientStringMethods(
			fileName, content, "(\\w+)\\.replace\\(", "replace");
		_checkInefficientStringMethods(
			fileName, content, "(\\w+)\\.toLowerCase\\(\\)", "toLowerCase");
		_checkInefficientStringMethods(
			fileName, content, "(\\w+)\\.toUpperCase\\(\\)", "toUpperCase");

		return content;
	}

	private void _checkInefficientStringMethods(
		String fileName, String content, String regex, String methodName) {

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (!isJavaSource(content, matcher.start())) {
				continue;
			}

			String s = matcher.group(1);

			String variableTypeName = getVariableTypeName(
				content, null, content, fileName, s);

			if (variableTypeName == null) {
				continue;
			}

			if (s.equals("String") ||
				Objects.equals(variableTypeName, "String")) {

				addMessage(
					fileName, "Use StringUtil." + methodName,
					getLineNumber(content, matcher.start(1)));
			}
		}
	}

	private void _checkStringReplaceCalls(String fileName, String content)
		throws ReflectiveOperationException {

		Matcher matcher = _stringReplacePattern.matcher(content);

		while (matcher.find()) {
			String variableName = matcher.group(1);

			String variableTypeName = getVariableTypeName(
				content, null, content, fileName, variableName);

			if ((variableTypeName == null) ||
				!Objects.equals(variableTypeName, "String")) {

				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start()));

			if ((parameterList.size() != 2) ||
				!_isSingleLengthString(parameterList.get(0)) ||
				!_isSingleLengthString(parameterList.get(1))) {

				continue;
			}

			StringBundler sb = new StringBundler(5);

			sb.append("Use ");
			sb.append(variableName);
			sb.append(".replace(char, char) instead of ");
			sb.append(variableName);
			sb.append(".replace(CharSequence, CharSequence)");

			addMessage(
				fileName, sb.toString(),
				getLineNumber(content, matcher.start()));
		}
	}

	private String _checkStringUtilContainsCalls(
		String fileName, String content) {

		int x = -1;

		while (true) {
			x = content.indexOf("StringUtil.contains(", x + 1);

			if (x == -1) {
				return content;
			}

			char c = content.charAt(x - 1);

			if (c == CharPool.QUOTE) {
				continue;
			}

			String methodCall = JavaSourceUtil.getMethodCall(content, x);

			List<String> parameterList = JavaSourceUtil.getParameterList(
				methodCall);

			if (parameterList.size() != 3) {
				continue;
			}

			String lastParameter = parameterList.get(2);

			if (!lastParameter.equals("\"\"") &&
				!lastParameter.equals("StringPool.BLANK")) {

				x = x + methodCall.length();

				continue;
			}

			String firstParameter = parameterList.get(0);

			if (firstParameter.contains("(")) {
				addMessage(
					fileName,
					"No need to use \"StringUtil.contains\" when the last " +
						"parameter is an empty string",
					getLineNumber(content, x));

				continue;
			}

			String newMethodCall = StringBundler.concat(
				parameterList.get(0), ".contains(", parameterList.get(1), ")");

			return StringUtil.replaceFirst(
				content, methodCall, newMethodCall, x);
		}
	}

	private String _checkStringUtilReplaceCalls(String fileName, String content)
		throws ReflectiveOperationException {

		if (content.contains("com.liferay.poshi.runner.util.StringUtil") ||
			content.contains("package com.liferay.poshi.runner.util;")) {

			return content;
		}

		Matcher matcher = _stringUtilReplacePattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start()));

			if (parameterList.size() != 3) {
				continue;
			}

			if (matcher.group(2) == null) {
				String lastParameter = parameterList.get(2);
				String middleParameter = parameterList.get(1);

				if ((lastParameter.equals("\"\"") ||
					 lastParameter.equals("StringPool.BLANK")) &&
					!middleParameter.startsWith(StringPool.APOSTROPHE) &&
					!middleParameter.startsWith("CharPool.")) {

					return _fixReplaceWithBlankCall(
						content, parameterList, matcher.start());
				}
			}

			if (!_isSingleLengthString(parameterList.get(1))) {
				continue;
			}

			String methodName = matcher.group(1);

			StringBundler sb = new StringBundler(5);

			sb.append("Use StringUtil.");
			sb.append(methodName);
			sb.append("(String, char, char) or StringUtil.");
			sb.append(methodName);
			sb.append("(String, char, String) instead");

			addMessage(
				fileName, sb.toString(),
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

	private String _fixReplaceWithBlankCall(
		String content, List<String> parameterList, int pos) {

		int x = pos;

		while (true) {
			x = content.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

			String call = content.substring(pos, x + 1);

			if ((ToolsUtil.getLevel(call, "(", ")") == 0) &&
				(ToolsUtil.getLevel(call, "{", "}") == 0)) {

				String replacement = StringBundler.concat(
					"StringUtil.removeSubstring(", parameterList.get(0), ", ",
					parameterList.get(1), ")");

				return StringUtil.replaceFirst(content, call, replacement);
			}
		}
	}

	private boolean _isSingleLengthString(String s)
		throws ReflectiveOperationException {

		Matcher singleLengthMatcher = _singleLengthStringPattern.matcher(s);

		if (!singleLengthMatcher.find()) {
			return false;
		}

		if (s.startsWith(StringPool.QUOTE)) {
			return true;
		}

		String fieldName = singleLengthMatcher.group(2);

		Field field = StringPool.class.getDeclaredField(fieldName);

		String value = (String)field.get(null);

		if (value.length() == 1) {
			return true;
		}

		return false;
	}

	private static final Pattern _singleLengthStringPattern = Pattern.compile(
		"^(\".\"|StringPool\\.([A-Z_]+))$");
	private static final Pattern _stringReplacePattern = Pattern.compile(
		"(\\w+)\\.replace\\(");
	private static final Pattern _stringUtilReplacePattern = Pattern.compile(
		"StringUtil\\.(replace(First|Last)?)\\(");

}