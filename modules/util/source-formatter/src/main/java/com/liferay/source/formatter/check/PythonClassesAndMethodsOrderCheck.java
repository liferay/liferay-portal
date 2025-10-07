/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.PythonSourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PythonClassesAndMethodsOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _formatClassDefinitionHeader(absolutePath, content);

		return _sortItems(fileName, content, StringPool.BLANK);
	}

	private List<String> _combineAnnotationsAndComments(
		List<String> statements, String indent) {

		List<String> statementsList = new ArrayList<>();

		StringBundler sb = new StringBundler();

		String previousStatement = StringPool.BLANK;

		for (String statement : statements) {
			if (statement.startsWith(indent + StringPool.AT) ||
				statement.startsWith(indent + StringPool.POUND)) {

				sb.append(statement);
				sb.append("\n");
			}
			else if (previousStatement.startsWith(indent + StringPool.AT) ||
					 previousStatement.startsWith(indent + StringPool.POUND)) {

				sb.append(statement);

				statementsList.add(sb.toString());

				sb.setIndex(0);
			}
			else {
				statementsList.add(statement);
			}

			previousStatement = statement;
		}

		if (sb.index() > 0) {
			statementsList.add(StringUtil.trimTrailing(sb.toString()));
		}

		return statementsList;
	}

	private String _formatClassDefinitionHeader(
		String absolutePath, String content) {

		int maxLineLength = 0;

		try {
			maxLineLength = Integer.parseInt(
				getAttributeValue(_MAX_LINE_LENGTH, absolutePath));
		}
		catch (NumberFormatException numberFormatException) {
			return content;
		}

		Matcher matcher = _classDefinitionHeaderPattern1.matcher(content);

		while (matcher.find()) {
			String indent = matcher.group(1);
			String className = matcher.group(2);

			List<String> parentClassList = ListUtil.fromString(
				matcher.group(4), StringPool.COMMA);

			for (int i = 0; i < parentClassList.size(); i++) {
				parentClassList.set(i, StringUtil.trim(parentClassList.get(i)));
			}

			StringBundler sb = new StringBundler(7);

			sb.append(indent);
			sb.append("class ");
			sb.append(className);

			if (ListUtil.isNotEmpty(parentClassList)) {
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(
					ListUtil.toString(
						parentClassList, StringPool.BLANK,
						StringPool.COMMA_AND_SPACE));
				sb.append(StringPool.CLOSE_PARENTHESIS);
			}

			sb.append(StringPool.COLON);

			content = StringUtil.replace(
				content, matcher.group(),
				_splitLine(sb.toString(), indent + "\t", maxLineLength) +
					"\n\n");
		}

		return content;
	}

	private int _sortClasses(String class1, String class2) {
		Matcher matcher = _classDefinitionHeaderPattern2.matcher(class1);

		if (!matcher.find()) {
			return 0;
		}

		String className1 = matcher.group(1);

		List<String> parentClassList = ListUtil.fromString(
			matcher.group(3), StringPool.COMMA);

		for (int i = 0; i < parentClassList.size(); i++) {
			parentClassList.set(i, StringUtil.trim(parentClassList.get(i)));
		}

		matcher = _classDefinitionHeaderPattern2.matcher(class2);

		if (!matcher.find()) {
			return 0;
		}

		String className2 = matcher.group(1);

		if (className1.compareTo(className2) < 0) {
			if (!parentClassList.contains(className2)) {
				return -1;
			}

			return 1;
		}

		return className1.compareTo(className2);
	}

	private int _sortItems(String statement1, String statement2) {
		String trimmedStatement1 = StringUtil.trimLeading(
			statement1.replaceAll("(\t*[#@].*(\\Z|\n))*(.*)", "$3"));
		String trimmedStatement2 = StringUtil.trimLeading(
			statement2.replaceAll("(\t*[#@].*(\\Z|\n))*(.*)", "$3"));

		if (Validator.isNull(trimmedStatement1) ||
			Validator.isNull(trimmedStatement2)) {

			return 0;
		}

		if (trimmedStatement1.startsWith("class ") &&
			trimmedStatement2.startsWith("class ")) {

			return _sortClasses(trimmedStatement1, trimmedStatement2);
		}

		if (trimmedStatement1.startsWith("def ") &&
			trimmedStatement2.startsWith("def ")) {

			return _sortMethods(trimmedStatement1, trimmedStatement2);
		}

		return 0;
	}

	private String _sortItems(String fileName, String content, String indent) {
		List<String> statements = PythonSourceUtil.getPythonStatements(
			content, indent);

		statements = _combineAnnotationsAndComments(statements, indent);

		List<String> oldStatements = new ArrayList<>(statements);

		Collections.sort(
			statements,
			new Comparator<String>() {

				@Override
				public int compare(String statement1, String statement2) {
					return _sortItems(statement1, statement2);
				}

			});

		if (!oldStatements.equals(statements)) {
			StringBundler sb = new StringBundler(statements.size() * 2);

			for (String statement : statements) {
				sb.append(statement);
				sb.append("\n");
			}

			sb.setIndex(sb.index() - 1);

			String[] lines = content.split("\n");

			if (!indent.equals("")) {
				content = lines[0] + "\n" + sb.toString();
			}
			else {
				content = sb.toString();
			}
		}

		statements = PythonSourceUtil.getPythonStatements(content, indent);

		for (String statement : statements) {
			String nestedStatementIndent =
				PythonSourceUtil.getNestedStatementIndent(statement);

			if (!nestedStatementIndent.equals(StringPool.BLANK)) {
				content = StringUtil.replaceFirst(
					content, statement,
					_sortItems(fileName, statement, nestedStatementIndent));
			}
		}

		return content;
	}

	private int _sortMethods(String method1, String method2) {
		String[] lines1 = method1.split("\n", 2);

		String methodName1 = lines1[0].replaceFirst("def (\\w+).*", "$1");

		String[] lines2 = method2.split("\n", 2);

		String methodName2 = lines2[0].replaceFirst("def (\\w+).*", "$1");

		return methodName1.compareTo(methodName2);
	}

	private String _splitLine(String line, String indent, int maxLineLength) {
		if (line.length() <= maxLineLength) {
			return line;
		}

		int pos = line.indexOf(StringPool.COMMA_AND_SPACE, indent.length());

		if (pos == -1) {
			return line;
		}

		if (pos > maxLineLength) {
			return StringBundler.concat(
				line.substring(0, pos + 1), StringPool.NEW_LINE,
				_splitLine(
					indent + StringUtil.trimLeading(line.substring(pos + 2)),
					indent, maxLineLength));
		}

		pos = line.lastIndexOf(StringPool.COMMA_AND_SPACE, maxLineLength);

		return StringBundler.concat(
			line.substring(0, pos + 1), StringPool.NEW_LINE,
			_splitLine(
				indent + StringUtil.trimLeading(line.substring(pos + 2)),
				indent, maxLineLength));
	}

	private static final String _MAX_LINE_LENGTH = "maxLineLength";

	private static final Pattern _classDefinitionHeaderPattern1 =
		Pattern.compile(
			"(?<=\n)(\t*)class (\\w+)(\\((.*?)\\))?:\n+", Pattern.DOTALL);
	private static final Pattern _classDefinitionHeaderPattern2 =
		Pattern.compile("class (\\w+)(\\((.*?)\\))?:", Pattern.DOTALL);

}