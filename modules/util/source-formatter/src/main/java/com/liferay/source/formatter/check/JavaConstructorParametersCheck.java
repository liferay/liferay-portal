/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaConstructorParametersCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		JavaSignature signature = javaTerm.getSignature();

		List<JavaParameter> parameters = signature.getParameters();

		if (!parameters.isEmpty()) {
			_checkConstructorParameterOrder(fileName, javaTerm, parameters);

			String content = _sortAssignCalls(
				javaTerm.getContent(), parameters);

			content = _fixIncorrectEmptyLines(
				content, _missingLineBreakPattern1, parameters);
			content = _fixIncorrectEmptyLines(
				content, _missingLineBreakPattern2, parameters);

			return _fixPassedInVariables(
				absolutePath, content, fileContent, fileName, parameters);
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR};
	}

	private void _checkConstructorParameterOrder(
		String fileName, JavaTerm javaTerm, List<JavaParameter> parameters) {

		String previousGlobalVariableName = null;
		String previousParameterName = null;
		int previousPos = -1;

		for (JavaParameter parameter : parameters) {
			String parameterName = parameter.getParameterName();

			Pattern pattern = Pattern.compile(
				StringBundler.concat(
					"\\{\n([\\s\\S]*?)((_|this\\.)", parameterName,
					") =[ \t\n]+", parameterName, ";"));

			Matcher matcher = pattern.matcher(javaTerm.getContent());

			if (!matcher.find()) {
				continue;
			}

			String beforeParameter = matcher.group(1);

			if (beforeParameter.contains(parameterName + " =")) {
				continue;
			}

			int pos = matcher.start(2);

			if ((previousPos > pos) &&
				previousGlobalVariableName.startsWith(matcher.group(3))) {

				addMessage(
					fileName,
					StringBundler.concat(
						"\"", previousGlobalVariableName, " = ",
						previousParameterName, ";\" should come before \"",
						matcher.group(2), " = ", parameterName,
						";\" to match order of constructor parameters"),
					javaTerm.getLineNumber(previousPos));

				return;
			}

			previousGlobalVariableName = matcher.group(2);
			previousParameterName = parameterName;
			previousPos = pos;
		}
	}

	private boolean _containsParameterName(
		List<JavaParameter> parameters, String name) {

		for (JavaParameter parameter : parameters) {
			if (name.equals(parameter.getParameterName())) {
				return true;
			}
		}

		return false;
	}

	private String _fixIncorrectEmptyLines(
		String content, Pattern pattern, List<JavaParameter> parameters) {

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String name1 = matcher.group(3);
			String name2 = matcher.group(5);

			if (!_containsParameterName(parameters, name1) ||
				!_containsParameterName(parameters, name2)) {

				continue;
			}

			String previousStatementsBlock = _getPreviousStatementsBlock(
				content, matcher.group(1), matcher.start() + 1);

			if (previousStatementsBlock.matches(
					StringBundler.concat(
						"(?s).*\\W(", matcher.group(2), ")?", name1,
						"\\W.*"))) {

				continue;
			}

			String nextStatementsBlock = _getNextStatementsBlock(
				content, matcher.group(1), matcher.start(6));

			if (Validator.isNull(nextStatementsBlock) ||
				!nextStatementsBlock.matches(
					"(?s).*\\W(_" + name2 + "\\W).*")) {

				return StringUtil.replaceFirst(
					content, StringPool.NEW_LINE, StringPool.BLANK,
					matcher.start(4));
			}
		}

		return content;
	}

	private String _fixPassedInVariables(
		String content, int pos, String globalVariableName,
		String parameterName) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"\\b(", globalVariableName, "|", parameterName, ")\\b"));

		Matcher matcher1 = pattern.matcher(content);

		outerLoop:
		while (matcher1.find()) {
			int start = matcher1.start();

			if ((start < pos) || ToolsUtil.isInsideQuotes(content, start)) {
				continue;
			}

			String followingCode = StringUtil.trimLeading(
				content.substring(matcher1.end()));

			if (followingCode.startsWith("=") &&
				!followingCode.startsWith("==")) {

				break;
			}

			String matchedGlobalVariableName = matcher1.group();

			if (followingCode.startsWith("!=") ||
				followingCode.startsWith("==") ||
				followingCode.startsWith(">") ||
				followingCode.startsWith("<")) {

				if (!StringUtil.equals(
						matchedGlobalVariableName, globalVariableName)) {

					continue;
				}

				return StringUtil.replaceFirst(
					content, globalVariableName, parameterName, start);
			}

			if (followingCode.startsWith(".")) {
				if (!followingCode.startsWith(".get") &&
					!followingCode.startsWith(".is")) {

					break;
				}

				if (!StringUtil.equals(
						matchedGlobalVariableName, globalVariableName)) {

					continue;
				}

				return StringUtil.replaceFirst(
					content, globalVariableName, parameterName, start);
			}

			char previousChar = content.charAt(start - 1);

			if ((previousChar != CharPool.OPEN_PARENTHESIS) &&
				(previousChar != CharPool.SPACE) &&
				(previousChar != CharPool.TAB)) {

				continue;
			}

			int previousOpenParenthesisPosition = start;

			while (true) {
				previousOpenParenthesisPosition = content.lastIndexOf(
					StringPool.OPEN_PARENTHESIS,
					previousOpenParenthesisPosition - 1);

				if (previousOpenParenthesisPosition == -1) {
					continue outerLoop;
				}

				if (ToolsUtil.isInsideQuotes(
						content, previousOpenParenthesisPosition)) {

					continue;
				}

				String methodCall = content.substring(
					previousOpenParenthesisPosition, start);

				if (ToolsUtil.getLevel(methodCall) == 1) {
					break;
				}
			}

			Matcher matcher2 = _methodCallPattern.matcher(content);

			while (matcher2.find()) {
				int parenthesisIndex = matcher2.end() - 1;

				if (parenthesisIndex > previousOpenParenthesisPosition) {
					break;
				}

				if (parenthesisIndex != previousOpenParenthesisPosition) {
					continue;
				}

				if (Validator.isNull(matcher2.group(2))) {
					String previousCode = content.substring(
						0, matcher2.start());

					if (previousCode.endsWith("new ")) {
						continue;
					}
				}

				String methodFullName = matcher2.group();
				String methodName = matcher2.group(3);

				if (!methodName.startsWith("_get") &&
					!methodName.startsWith("_is") &&
					!methodName.startsWith("get") &&
					!methodName.startsWith("is") &&
					!methodFullName.startsWith("StringBundler.concat(")) {

					return content;
				}

				if (StringUtil.equals(
						matchedGlobalVariableName, globalVariableName)) {

					return StringUtil.replaceFirst(
						content, globalVariableName, parameterName, start);
				}
			}
		}

		return content;
	}

	private String _fixPassedInVariables(
		String absolutePath, String content, String fileContent,
		String fileName, List<JavaParameter> parameters) {

		if (!isAttributeValue(_CHECK_PASSED_IN_VARIABLES_KEY, absolutePath)) {
			return content;
		}

		for (JavaParameter parameter : parameters) {
			String parameterName = parameter.getParameterName();

			Pattern pattern = Pattern.compile(
				StringBundler.concat(
					"\n\t*?(_", parameterName, ") =[ \t\n]+", parameterName,
					";"));

			Matcher matcher = pattern.matcher(content);

			if (!matcher.find()) {
				continue;
			}

			String globalVariableName = matcher.group(1);

			String globalVariableTypeName = getVariableTypeName(
				content, null, fileContent, fileName, globalVariableName, true,
				false);

			String parameterTypeName = parameter.getParameterType();

			if ((globalVariableTypeName == null) ||
				!StringUtil.equals(parameterTypeName, globalVariableTypeName)) {

				continue;
			}

			String newContent = _fixPassedInVariables(
				content, matcher.end(), globalVariableName, parameterName);

			if (!StringUtil.equals(content, newContent)) {
				return newContent;
			}
		}

		return content;
	}

	private int _getIndex(
		String name, String value, List<JavaParameter> parameters) {

		for (int i = 0; i < parameters.size(); i++) {
			JavaParameter parameter = parameters.get(i);

			String parameterName = parameter.getParameterName();

			if (name.equals(parameterName)) {
				if (value.matches("(?s).*\\W" + parameterName + "\\W.*")) {
					return i;
				}

				return parameters.size();
			}
		}

		return parameters.size();
	}

	private String _getNextStatementsBlock(
		String content, String indent, int pos) {

		int x = pos;

		while (true) {
			x = content.indexOf("\n\n", x + 1);

			if (x == -1) {
				return StringPool.BLANK;
			}

			String nextLine = getLine(content, getLineNumber(content, x + 2));

			if (indent.equals(SourceUtil.getIndent(nextLine))) {
				break;
			}
		}

		int y = x;

		while (true) {
			y = content.indexOf("\n\n", y + 1);

			if (y == -1) {
				return content.substring(x);
			}

			String nextLine = getLine(content, getLineNumber(content, y + 2));

			if (indent.equals(SourceUtil.getIndent(nextLine))) {
				break;
			}
		}

		return content.substring(x, y);
	}

	private String _getPreviousStatementsBlock(
		String content, String indent, int pos) {

		int x = pos;

		while (true) {
			x = content.lastIndexOf("\n\n", x - 1);

			if (x == -1) {
				return StringPool.BLANK;
			}

			String nextLine = getLine(content, getLineNumber(content, x + 2));

			if (indent.equals(SourceUtil.getIndent(nextLine))) {
				break;
			}
		}

		int y = x;

		while (true) {
			y = content.lastIndexOf("\n\n", y - 1);

			if (y == -1) {
				int z = content.indexOf("{\n");

				if (z == -1) {
					return StringPool.BLANK;
				}

				return content.substring(z + 1, x);
			}

			String nextLine = getLine(content, getLineNumber(content, y + 2));

			if (indent.equals(SourceUtil.getIndent(nextLine))) {
				break;
			}
		}

		return content.substring(y, x);
	}

	private String _sortAssignCalls(
		String content, List<JavaParameter> parameters) {

		String firstFollowingStatement = null;

		Matcher assignCallMatcher = _assignCallPattern.matcher(content);

		while (assignCallMatcher.find()) {
			Pattern nextCallPattern = Pattern.compile(
				"^\t+" + assignCallMatcher.group(1) + "(\\w+) (=[^;]+;)\\n");

			String followingCode = content.substring(assignCallMatcher.end());

			Matcher nextCallMatcher = nextCallPattern.matcher(followingCode);

			if (!nextCallMatcher.find()) {
				continue;
			}

			int index1 = _getIndex(
				assignCallMatcher.group(2), assignCallMatcher.group(3),
				parameters);
			int index2 = _getIndex(
				nextCallMatcher.group(1), nextCallMatcher.group(2), parameters);

			if (index1 > index2) {
				String assignment1 = StringUtil.trim(assignCallMatcher.group());
				String assignment2 = StringUtil.trim(nextCallMatcher.group());

				content = StringUtil.replaceFirst(
					content, assignment2, assignment1,
					assignCallMatcher.start());

				content = StringUtil.replaceFirst(
					content, assignment1, assignment2,
					assignCallMatcher.start());

				return _sortAssignCalls(content, parameters);
			}

			if ((index1 == index2) || (index2 != parameters.size())) {
				continue;
			}

			firstFollowingStatement = nextCallMatcher.group();
		}

		if (firstFollowingStatement != null) {
			return StringUtil.replaceFirst(
				content, firstFollowingStatement,
				"\n" + firstFollowingStatement);
		}

		return content;
	}

	private static final String _CHECK_PASSED_IN_VARIABLES_KEY =
		"checkPassedInVariables";

	private static final Pattern _assignCallPattern = Pattern.compile(
		"\t(_|this\\.)(\\w+) (=[^;]+;)\n");
	private static final Pattern _methodCallPattern = Pattern.compile(
		"((\\w+)\\.\\s*)?(\\w+)\\(");
	private static final Pattern _missingLineBreakPattern1 = Pattern.compile(
		"\n(\t+)(_)(\\w+) =[ \t\n]+\\3;(?=(\n\n)\\1_(\\w+) =[ \t\n]+\\5(;)\n)");
	private static final Pattern _missingLineBreakPattern2 = Pattern.compile(
		"\n(\t+)(this\\.)(\\w+) =[ \t\n]+\\3;(?=(\n\n)\\1this\\.(\\w+) " +
			"=[ \t\n]+\\5(;)\n)");

}