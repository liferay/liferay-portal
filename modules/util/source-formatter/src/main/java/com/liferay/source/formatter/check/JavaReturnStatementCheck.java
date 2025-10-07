/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaReturnStatementCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		return _formatReturnStatements(javaTerm);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD};
	}

	private String _formatReturnAssignStatement(
		Matcher matcher, String returnStatement) {

		String returnFollowingCode = matcher.group(2);

		int index = returnFollowingCode.indexOf(" =");

		if ((index == -1) || ((index + 2) >= returnFollowingCode.length())) {
			return returnStatement;
		}

		char c = returnFollowingCode.charAt(index + 2);

		if (!Character.isWhitespace(c)) {
			return returnStatement;
		}

		String s = returnFollowingCode.substring(0, index);

		if (!s.matches("\\w+")) {
			return returnStatement;
		}

		StringBundler sb = new StringBundler(8);

		sb.append("\n");
		sb.append(matcher.group(1));
		sb.append(returnFollowingCode);
		sb.append(";\n\n");
		sb.append(matcher.group(1));
		sb.append("return ");
		sb.append(s);
		sb.append(";\n");

		return sb.toString();
	}

	private String _formatReturnStatement(
		String javaTermContent, String returnStatement, String tabs,
		String ifCondition, String trueValue, String falseValue) {

		StringBundler sb = new StringBundler(15);

		sb.append("\n");
		sb.append(tabs);
		sb.append("if (");
		sb.append(ifCondition);
		sb.append(") {\n\n");
		sb.append(tabs);
		sb.append("\treturn ");
		sb.append(trueValue);
		sb.append(";\n");
		sb.append(tabs);
		sb.append("}\n\n");
		sb.append(tabs);
		sb.append("return ");
		sb.append(falseValue);
		sb.append(";\n");

		return StringUtil.replace(
			javaTermContent, returnStatement, sb.toString());
	}

	private String _formatReturnStatements(JavaTerm javaTerm) {
		JavaSignature signature = javaTerm.getSignature();

		String returnType = signature.getReturnType();

		if (Validator.isBlank(returnType)) {
			return javaTerm.getContent();
		}

		String javaTermContent = javaTerm.getContent();

		Matcher matcher1 = _returnPattern.matcher(javaTermContent);

		while (matcher1.find()) {
			String returnStatement = matcher1.group();

			if (returnStatement.contains("\t//") ||
				returnStatement.contains(" {\n")) {

				continue;
			}

			String[] ternaryOperatorParts = getTernaryOperatorParts(
				matcher1.group(2));

			if (ternaryOperatorParts != null) {
				String falseValue = ternaryOperatorParts[2];
				String ifCondition = ternaryOperatorParts[0];
				String trueValue = ternaryOperatorParts[1];

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					ifCondition, trueValue, falseValue);
			}

			String newReturnStatement = _formatReturnAssignStatement(
				matcher1, returnStatement);

			if (!newReturnStatement.equals(returnStatement)) {
				return StringUtil.replaceFirst(
					javaTermContent, returnStatement, newReturnStatement,
					matcher1.start());
			}

			if (!returnType.equals("boolean")) {
				continue;
			}

			String strippedReturnStatement = SourceUtil.stripQuotes(
				returnStatement);

			if (strippedReturnStatement.contains("|") ||
				strippedReturnStatement.contains("&") ||
				strippedReturnStatement.contains("^")) {

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					matcher1.group(2), "true", "false");
			}

			Matcher matcher2 = _relationalOperatorPattern.matcher(
				returnStatement);

			if (matcher2.find() &&
				!ToolsUtil.isInsideQuotes(returnStatement, matcher2.start(1))) {

				return _formatReturnStatement(
					javaTermContent, returnStatement, matcher1.group(1),
					matcher1.group(2), "true", "false");
			}
		}

		return javaTermContent;
	}

	private static final Pattern _relationalOperatorPattern = Pattern.compile(
		".* (==|!=|<|>|>=|<=)[ \n].*");
	private static final Pattern _returnPattern = Pattern.compile(
		"\n(\t+)return (.*?);\n", Pattern.DOTALL);

}