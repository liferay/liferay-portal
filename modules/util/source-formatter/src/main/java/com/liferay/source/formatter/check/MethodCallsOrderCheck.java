/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.comparator.ParameterNameComparator;
import com.liferay.source.formatter.check.util.JSPSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.processor.JSPSourceProcessor;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class MethodCallsOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _sortMethodCalls(content);
	}

	private String _getMethodCall(String content, int start) {
		int end = start;

		while (true) {
			end = content.indexOf(");\n", end + 1);

			if (end == -1) {
				return null;
			}

			String methodCall = content.substring(start, end + 3);

			if (getLevel(methodCall) == 0) {
				return methodCall;
			}
		}
	}

	private String _getSortedCodeBlock(String codeBlock, String methodCall) {
		String previousParameterName = null;
		String previousParameters = null;

		ParameterNameComparator parameterNameComparator =
			new ParameterNameComparator();

		int x = 0;

		while (true) {
			String s = StringUtil.trim(codeBlock.substring(x));

			if (!s.startsWith(methodCall)) {
				return codeBlock;
			}

			String parameters = null;

			x = codeBlock.indexOf(CharPool.OPEN_PARENTHESIS, x + 1);

			int y = x;

			while (true) {
				y = codeBlock.indexOf(CharPool.CLOSE_PARENTHESIS, y + 1);

				if (y == -1) {
					return codeBlock;
				}

				s = codeBlock.substring(x, y + 1);

				if ((ToolsUtil.getLevel(s, "(", ")") == 0) &&
					(ToolsUtil.getLevel(s, "{", "}") == 0)) {

					if (codeBlock.charAt(y + 1) != CharPool.SEMICOLON) {
						return codeBlock;
					}

					parameters = codeBlock.substring(x + 1, y);

					x = y + 2;

					break;
				}
			}

			List<String> parametersList = JavaSourceUtil.splitParameters(
				parameters);

			String parameterName = parametersList.get(0);

			if (previousParameterName != null) {
				int compare = parameterNameComparator.compare(
					previousParameterName, parameterName);

				if (compare > 0) {
					String sortedCodeBlock = StringUtil.replaceFirst(
						codeBlock, previousParameters, parameters);

					return StringUtil.replaceLast(
						sortedCodeBlock, parameters, previousParameters);
				}
			}

			previousParameterName = parameterName;
			previousParameters = parameters;
		}
	}

	private String _getTypeName(String content, int lineNumber) {
		int level = 0;

		while (true) {
			if (lineNumber == 0) {
				return null;
			}

			String trimmedLine = StringUtil.trim(getLine(content, lineNumber));

			if (trimmedLine.endsWith("(") && (level == 0)) {
				if (trimmedLine.startsWith(").")) {
					level -= getLevel(trimmedLine);
					lineNumber--;

					continue;
				}

				Matcher matcher = _typeNamePattern.matcher(trimmedLine);

				if (matcher.find()) {
					return matcher.group(2);
				}

				return null;
			}

			level -= getLevel(trimmedLine);
			lineNumber--;
		}
	}

	private boolean _isAllowedVariableType(
		String content, String variableName, String[] variableTypeNames) {

		if (variableName == null) {
			return false;
		}

		if (variableTypeNames.length == 0) {
			return true;
		}

		for (String variableTypeName : variableTypeNames) {
			if (variableName.matches(variableTypeName)) {
				return true;
			}

			StringBundler sb = new StringBundler(5);

			sb.append("\\W");
			sb.append(variableTypeName);
			sb.append("(<.*>|\\(\\))?\\s+");
			sb.append(variableName);
			sb.append("\\W");

			Pattern pattern = Pattern.compile(sb.toString());

			Matcher matcher = pattern.matcher(content);

			if (matcher.find()) {
				return true;
			}

			sb = new StringBundler(5);

			sb.append("\\W");
			sb.append(variableName);
			sb.append(" =\\s+new ");
			sb.append(variableTypeName);
			sb.append("(<.*>|\\(\\))");

			pattern = Pattern.compile(sb.toString());

			matcher = pattern.matcher(content);

			if (matcher.find()) {
				return true;
			}
		}

		return false;
	}

	private String _sortAnonymousClassMethodCalls(
		String content, String methodName, String... variableTypeNames) {

		for (String variableTypeName : variableTypeNames) {
			Pattern pattern = Pattern.compile(
				"\\Wnew " + variableTypeName + "[(<][^;]*?\\) \\{\n");

			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				int lineNumber = getLineNumber(content, matcher.end() - 1);

				if (!Objects.equals(
						StringUtil.trim(getLine(content, lineNumber + 1)),
						"{")) {

					continue;
				}

				int x = getLineStartPos(content, lineNumber + 2);

				int y = content.indexOf("\t}\n", x);

				if (y == -1) {
					continue;
				}

				int z = content.indexOf("\n\n", x);

				if ((z != -1) && (z < y)) {
					y = z;
				}

				String codeBlock = content.substring(x, y);

				String sortedCodeBlock = _getSortedCodeBlock(
					codeBlock, methodName + "(");

				if (!codeBlock.equals(sortedCodeBlock)) {
					return StringUtil.replaceFirst(
						content, codeBlock, sortedCodeBlock, matcher.start());
				}
			}
		}

		return content;
	}

	private String _sortChainedMethodCalls(
		String content, String methodName, int expectedParameterCount,
		String... variableTypeNames) {

		if (!content.contains("." + methodName + "(")) {
			return content;
		}

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(\\W(\\w+)(\\(\\s*\\))?\\.(", _GENERIC_TYPE_NAMES_PATTERN,
				")?|[\n\t]\\)\\.)", methodName, "\\("));

		Matcher matcher = pattern.matcher(content);

		ParameterNameComparator parameterNameComparator =
			new ParameterNameComparator();

		while (matcher.find()) {
			String typeName = matcher.group(2);

			if (typeName == null) {
				typeName = _getTypeName(
					content, getLineNumber(content, matcher.start()));
			}

			if (!_isAllowedVariableType(content, typeName, variableTypeNames)) {
				continue;
			}

			String previousParameterName = null;
			String previousParameters = null;

			int x = matcher.end() - 1;

			while (true) {
				String parameters = null;

				int y = x;

				while (true) {
					y = content.indexOf(")", y + 1);

					if (y == -1) {
						return content;
					}

					if (getLevel(content.substring(x, y + 1)) == 0) {
						parameters = content.substring(x + 1, y);

						break;
					}
				}

				List<String> parametersList = JavaSourceUtil.splitParameters(
					parameters);

				if (parametersList.size() != expectedParameterCount) {
					break;
				}

				String parameterName = parametersList.get(0);

				if (previousParameterName != null) {
					int compare = parameterNameComparator.compare(
						previousParameterName, parameterName);

					if (compare > 0) {
						String codeBlock = content.substring(
							matcher.start(), y + 1);

						String newCodeBlock = StringUtil.replaceFirst(
							codeBlock, previousParameters, parameters);

						newCodeBlock = StringUtil.replaceLast(
							newCodeBlock, parameters, previousParameters);

						return StringUtil.replaceFirst(
							content, codeBlock, newCodeBlock, matcher.start());
					}
				}

				String s = StringUtil.trim(content.substring(y + 1));

				if (!s.startsWith("." + methodName + "(")) {
					break;
				}

				previousParameterName = parameterName;
				previousParameters = parameters;

				x = content.indexOf("(", y + 1);
			}
		}

		return content;
	}

	private String _sortMethodCalls(String content) {
		content = _sortChainedMethodCalls(
			content, "put", 2, "ConcurrentHashMapBuilder", "HashMapBuilder",
			"HashMapDictionaryBuilder", "JSONObject", "JSONUtil", "SoyContext",
			"TreeMapBuilder", "UnicodePropertiesBuilder");
		content = _sortChainedMethodCalls(
			content, "putAll", 1, "ConcurrentHashMapBuilder", "HashMapBuilder",
			"HashMapDictionaryBuilder", "JSONUtil", "SoyContext",
			"TreeMapBuilder", "UnicodePropertiesBuilder");
		content = _sortChainedMethodCalls(
			content, "setGlobalParameter", 2, "PortletURLBuilder");
		content = _sortChainedMethodCalls(
			content, "setParameter", 2, "PortletURLBuilder");
		content = _sortChainedMethodCalls(
			content, "setProperty", 2, "UnicodePropertiesBuilder");

		content = _sortMethodCallsByMethodName(
			content, "DDMFormFieldRenderingContext", "DropdownItem",
			"LabelItem", "NavigationItem", "SearchContainer", "SearchContext",
			"SearchEntry", "ServiceContext", "SiteItemSelectorCriterion",
			"ThemeDisplay");

		content = _sortMethodCallsByParameter(
			content, "add", "ConcurrentSkipListSet", "HashSet", "TreeSet");
		content = _sortMethodCallsByParameter(
			content, "addPart", "Http.Options");
		content = _sortMethodCallsByParameter(
			content, "put", "ConcurrentHashMap", "HashMap", "JSONObject",
			"Properties", "SortedMap", "TreeMap");
		content = _sortMethodCallsByParameter(content, "setAttribute");

		return content;
	}

	private String _sortMethodCallsByMethodName(
		String content, String... variableTypeNames) {

		MethodCallComparator methodCallComparator = new MethodCallComparator();

		for (String variableTypeName : variableTypeNames) {
			Pattern pattern = Pattern.compile(
				"\n(\t+\\w*" + variableTypeName + "\\.)(\\w+)\\(",
				Pattern.CASE_INSENSITIVE);

			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				if ((getSourceProcessor() instanceof JSPSourceProcessor) &&
					JSPSourceUtil.isJSSource(content, matcher.start())) {

					continue;
				}

				String methodCall1 = _getMethodCall(content, matcher.start(1));

				if (methodCall1 == null) {
					continue;
				}

				int x = matcher.start(1) + methodCall1.length();

				String followingContent = content.substring(x);

				if (!followingContent.startsWith(matcher.group(1))) {
					continue;
				}

				String methodCall2 = _getMethodCall(content, x);

				if ((methodCall2 != null) &&
					(methodCallComparator.compare(methodCall1, methodCall2) >
						0)) {

					content = StringUtil.replaceFirst(
						content, methodCall2, methodCall1, matcher.start());

					return StringUtil.replaceFirst(
						content, methodCall1, methodCall2, matcher.start());
				}
			}
		}

		return content;
	}

	private String _sortMethodCallsByParameter(
		String content, String methodName, String... variableTypeNames) {

		content = _sortAnonymousClassMethodCalls(
			content, methodName, variableTypeNames);

		if (!content.contains("." + methodName + "(")) {
			return content;
		}

		Pattern pattern = Pattern.compile(
			"[^;]\n\t+((\\w*)\\." + methodName + "\\()");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (!_isAllowedVariableType(
					content, matcher.group(2), variableTypeNames) ||
				!isJavaSource(content, matcher.start())) {

				continue;
			}

			int x = content.indexOf("\n\n", matcher.end());

			if (x == -1) {
				x = content.length();
			}

			String codeBlock = content.substring(matcher.start() + 2, x);

			String sortedCodeBlock = _getSortedCodeBlock(
				codeBlock, matcher.group(1));

			if (!codeBlock.equals(sortedCodeBlock)) {
				return StringUtil.replaceFirst(
					content, codeBlock, sortedCodeBlock, matcher.start());
			}
		}

		return content;
	}

	private static final String _GENERIC_TYPE_NAMES_PATTERN;

	private static final Pattern _typeNamePattern;

	static {
		_GENERIC_TYPE_NAMES_PATTERN = "<[\\w\\[\\]\\?<>, ]*>";

		_typeNamePattern = Pattern.compile(
			"(\\A|\\W)(\\w+)\\.(" + _GENERIC_TYPE_NAMES_PATTERN + ")?\\w+\\(");
	}

	private class MethodCallComparator extends ParameterNameComparator {

		@Override
		public int compare(String methodCall1, String methodCall2) {
			String methodName1 = _getMethodName(methodCall1);
			String methodName2 = _getMethodName(methodCall2);

			if (!methodName1.equals(methodName2)) {
				return methodName1.compareTo(methodName2);
			}

			List<String> parameterList1 = JavaSourceUtil.getParameterList(
				methodCall1);
			List<String> parameterList2 = JavaSourceUtil.getParameterList(
				methodCall2);

			return super.compare(parameterList1.get(0), parameterList2.get(0));
		}

		private String _getMethodName(String methodCall) {
			int x = methodCall.indexOf(CharPool.PERIOD);
			int y = methodCall.indexOf(CharPool.OPEN_PARENTHESIS);

			return methodCall.substring(x + 1, y);
		}

	}

}