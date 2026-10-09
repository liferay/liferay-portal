/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLCustomSQLStylingCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.contains("/custom-sql/")) {
			return content;
		}

		_checkIncorrectLineBreakAfterComma(fileName, content);
		_checkMissingLineBreakAfterKeyword(fileName, content);
		_checkMissingParentheses(fileName, content);
		_checkMultiLineClause(fileName, content);
		_checkScalability(fileName, absolutePath, content);
		_checkUnionStatement(fileName, content);

		content = _fixBooleanValues(content);
		content = _fixIncorrectAndOr(content);
		content = _fixLowerCaseKeywords(content);
		content = _fixMissingCountValue(content);
		content = _fixMissingLineBreakAfterOpenParenthesis(content);
		content = _fixMissingLineBreakBeforeOpenParenthesis(content);
		content = _fixRedundantParenthesesForSingleLineClause(content);
		content = _fixSinglePredicateClause(content);
		content = _formatSingleLineClauseWithMultiplePredicates(
			fileName, content);

		return content;
	}

	private String _addTabs(String content, int start, int end) {
		for (int i = start; i <= end; i++) {
			int lineStartPos = getLineStartPos(content, i);

			content =
				content.substring(0, lineStartPos) + "\t" +
					content.substring(lineStartPos);
		}

		return content;
	}

	private void _checkIncorrectLineBreakAfterComma(
		String fileName, String content) {

		if (true) {
			return;
		}

		Matcher matcher = _incorrectLineBreakAfterCommaPattern.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName, "Incorrect line break after \",\"",
				getLineNumber(content, matcher.start()));
		}
	}

	private void _checkMissingLineBreakAfterKeyword(
		String fileName, String content) {

		Matcher matcher = _missingLineBreakAfterKeywordPattern.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName,
				"There should be a line break after \"" +
					StringUtil.trim(matcher.group(1)) + "\"",
				getLineNumber(content, matcher.end()));
		}
	}

	private void _checkMissingParentheses(String fileName, String content) {
		Matcher matcher = _missingParenthesesPattern1.matcher(content);

		while (matcher.find()) {
			String charBeforeOperator = matcher.group(2);

			if (charBeforeOperator.equals(StringPool.CLOSE_PARENTHESIS)) {
				String s = matcher.group(1);

				if (s.equals(StringPool.CLOSE_PARENTHESIS) ||
					(s.startsWith(StringPool.OPEN_PARENTHESIS) &&
					 (getLevel(s) == 0))) {

					continue;
				}
			}
			else if (charBeforeOperator.equals(StringPool.CLOSE_BRACKET)) {
				String s = matcher.group(1);

				if (s.startsWith(StringPool.OPEN_BRACKET) &&
					(getLevel(s, "[", "]") == 0)) {

					continue;
				}
			}

			addMessage(
				fileName, "Missing parentheses",
				getLineNumber(content, matcher.start()));
		}

		matcher = _missingParenthesesPattern2.matcher(content);

		while (matcher.find()) {
			String nextLine = getLine(
				content, getLineNumber(content, matcher.end()));

			if (nextLine.endsWith(" IN") || nextLine.endsWith("EXISTS") ||
				nextLine.matches(".*\\s+BETWEEN\\s+\\?\\s+AND\\s+\\?.*")) {

				continue;
			}

			addMessage(
				fileName, "Missing parentheses",
				getLineNumber(content, matcher.end()));
		}
	}

	private void _checkMultiLineClause(String fileName, String content) {
		int startPos = -1;

		while (true) {
			startPos = content.indexOf("\t(\n", startPos + 1);

			if (startPos == -1) {
				return;
			}

			int endPos = _getCloseParenthesisPos(content, startPos);

			int endLineNumber = getLineNumber(content, endPos);
			int endLineStartPos = content.lastIndexOf("\n", endPos);

			char c = content.charAt(endPos - 1);

			if (c != CharPool.TAB) {
				String s = StringUtil.trim(
					content.substring(endLineStartPos, endPos));

				addMessage(
					fileName,
					"There should be a line break after \"" + s + "\"",
					endLineNumber);

				continue;
			}

			String afterCloseParenthesis = StringUtil.trim(
				content.substring(endPos + 1));
			String beforeOpenParenthesis = StringUtil.trim(
				content.substring(0, startPos));

			if ((beforeOpenParenthesis.endsWith(" ON") ||
				 beforeOpenParenthesis.endsWith("\tWHERE")) &&
				!afterCloseParenthesis.startsWith("AND") &&
				!afterCloseParenthesis.startsWith("OR") &&
				!afterCloseParenthesis.startsWith("[")) {

				addMessage(
					fileName, "redundant parentheses",
					getLineNumber(content, startPos));
			}

			int endLineTabCount = endPos - endLineStartPos - 1;

			int startLineStartPos = content.lastIndexOf("\n", startPos);

			int startLineTabCount = startPos - startLineStartPos;

			if (endLineTabCount == startLineTabCount) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Line starts with \"", endLineTabCount, "\" tabs, but \"",
					startLineTabCount, "\" tabs are expected"),
				endLineNumber);
		}
	}

	private void _checkScalability(
		String fileName, String absolutePath, String content) {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		for (Element sqlElement : (List<Element>)rootElement.elements("sql")) {
			String sql = sqlElement.getText();

			Matcher matcher = _whereNotInSQLPattern.matcher(sql);

			while (matcher.find()) {
				String id = sqlElement.attributeValue("id");

				int x = id.lastIndexOf(CharPool.PERIOD);

				int y = id.lastIndexOf(CharPool.PERIOD, x - 1);

				String entityName = id.substring(y + 1, x);

				if (isExcludedPath(
						_CUSTOM_FINDER_SCALABILITY_EXCLUDES, absolutePath,
						entityName)) {

					continue;
				}

				addMessage(
					fileName,
					"Avoid using WHERE ... NOT IN: " + id + ", see LPS-51315");
			}
		}
	}

	private void _checkUnionStatement(String fileName, String content) {
		Matcher matcher = _unionPattern.matcher(content);

		while (matcher.find()) {
			String beforeUnionChar = matcher.group(1);

			if (beforeUnionChar.equals(StringPool.CLOSE_PARENTHESIS)) {
				int openParenthesisPos = _getOpenParenthesisPos(
					content, matcher.start(1));

				String s = StringUtil.trim(
					content.substring(openParenthesisPos + 1, matcher.start()));

				if (s.startsWith("SELECT")) {
					addMessage(
						fileName, "Do not use parentheses before UNION",
						getLineNumber(content, matcher.start()));

					continue;
				}
			}

			String afterUnionChar = matcher.group(4);

			if (!afterUnionChar.equals(StringPool.OPEN_PARENTHESIS)) {
				continue;
			}

			addMessage(
				fileName, "Do not use parentheses after UNION",
				getLineNumber(content, matcher.start(3)));
		}
	}

	private String _fixBooleanValues(String content) {
		content = content.replaceAll(
			"(?i)(=\\s*)(false)\\b", "$1[\\$FALSE\\$]");
		content = content.replaceAll("(?i)(=\\s*)(true)\\b", "$1[\\$TRUE\\$]");

		return content;
	}

	private String _fixIncorrectAndOr(String content) {
		Matcher matcher = _incorrectAndOrpattern.matcher(content);

		if (matcher.find()) {
			String whitespace = matcher.group(3);

			if (whitespace.equals(StringPool.SPACE)) {
				return StringUtil.replaceFirst(
					content, matcher.group(),
					StringPool.SPACE + matcher.group(2) + matcher.group(1),
					matcher.start() - 1);
			}

			return StringUtil.replaceFirst(
				content, matcher.group(1), StringPool.SPACE,
				matcher.start() - 1);
		}

		return content;
	}

	private String _fixLowerCaseKeywords(String content) {
		for (String keyword : _SQL_KEYWORDS) {
			Pattern pattern = Pattern.compile(
				"[^\\w.$'\"](" + keyword + ")[^\\w.$'\"]",
				Pattern.CASE_INSENSITIVE);

			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				int level = getLevel(
					content.substring(0, matcher.start()), "<![CDATA[", "]]>");

				if (level == 0) {
					continue;
				}

				content = StringUtil.replaceFirst(
					content, matcher.group(1), keyword, matcher.start());
			}
		}

		return content;
	}

	private String _fixMissingCountValue(String content) {
		Matcher matcher = _missingCountValuePattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.insert(
				content, " AS COUNT_VALUE", matcher.end(1));
		}

		return content;
	}

	private String _fixMissingLineBreakAfterOpenParenthesis(String content) {
		Matcher matcher = _missingLineBreakAfterOpenParenthesisPattern.matcher(
			content);

		while (matcher.find()) {
			if (getLevel(matcher.group()) == 0) {
				continue;
			}

			int startPos = matcher.end(1);

			int startLineNumber = getLineNumber(content, startPos);

			int endPos = _getCloseParenthesisPos(content, startPos);

			int endLineNumber = getLineNumber(content, endPos);

			content = _addTabs(content, startLineNumber + 1, endLineNumber - 1);

			return StringUtil.replaceFirst(
				content, "\t(", "\t(\n\t" + matcher.group(1), matcher.start());
		}

		return content;
	}

	private String _fixMissingLineBreakBeforeOpenParenthesis(String content) {
		Matcher matcher = _missingLineBreakBeforeOpenParenthesisPattern.matcher(
			content);

		if (!matcher.find()) {
			return content;
		}

		int startPos = matcher.end() - 2;

		int startLineNumber = getLineNumber(content, startPos);

		int endPos = _getCloseParenthesisPos(content, startPos);

		int endLineNumber = getLineNumber(content, endPos);

		content = _addTabs(content, startLineNumber + 1, endLineNumber);

		return StringUtil.replaceFirst(
			content, "(\n", "\n\t" + matcher.group(1) + "(\n", matcher.start());
	}

	private String _fixRedundantParenthesesForSingleLineClause(String content) {
		Matcher matcher =
			_redundantParenthesesForSingleLineClausePattern.matcher(content);

		while (matcher.find()) {
			String trimmedNextLine = StringUtil.trim(matcher.group(3));

			if (!trimmedNextLine.startsWith("AND") &&
				!trimmedNextLine.startsWith("OR") &&
				!trimmedNextLine.startsWith("[$AND_OR_CONNECTOR$]")) {

				return StringUtil.replaceFirst(
					content, "(" + matcher.group(2) + ")", matcher.group(2),
					matcher.start());
			}
		}

		return content;
	}

	private String _fixSinglePredicateClause(String content) {
		Matcher matcher = _multiLineSinglePredicatePattern.matcher(content);

		while (matcher.find()) {
			String line = StringUtil.trim(matcher.group(1));

			if (!line.startsWith("[") || !line.endsWith("]")) {
				return StringUtil.replace(
					content, matcher.group(), "\t(" + line + ")");
			}
		}

		return content;
	}

	private String _formatSingleLineClauseWithMultiplePredicates(
		String fileName, String content) {

		Matcher matcher = _singleLineClauseWitMultiplePredicatesPattern.matcher(
			content);

		while (matcher.find()) {
			String afterOperator = matcher.group(5);
			String beforeOperator = matcher.group(3);
			String indent = matcher.group(1);
			String match = matcher.group(2);
			String operator = matcher.group(4);

			StringBundler sb = new StringBundler(11);

			if (beforeOperator.equals(")")) {
				sb.append(") ");
				sb.append(operator);
				sb.append("\n");
				sb.append(indent);
				sb.append(afterOperator);

				return StringUtil.replaceFirst(
					content, match, sb.toString(), matcher.start());
			}

			int lineNumber = getLineNumber(content, matcher.start(3));

			if ((getLevel(match) != 0) || !match.startsWith("(")) {
				addMessage(fileName, "One SQL predicate per line", lineNumber);

				continue;
			}

			int beforeOperatorlevel = getLevel(beforeOperator);

			if ((beforeOperatorlevel < 0) || (beforeOperatorlevel > 1)) {
				addMessage(fileName, "One SQL predicate per line", lineNumber);

				continue;
			}

			if (beforeOperatorlevel == 0) {
				sb.append(beforeOperator);
				sb.append(StringPool.SPACE);
				sb.append(operator);
				sb.append("\n");
				sb.append(indent);
				sb.append(afterOperator);

				return StringUtil.replaceFirst(
					content, match, sb.toString(), matcher.start());
			}

			sb.append("(\n\t");
			sb.append(indent);
			sb.append(beforeOperator.substring(1));
			sb.append(StringPool.SPACE);
			sb.append(operator);
			sb.append("\n\t");
			sb.append(indent);

			int pos = afterOperator.lastIndexOf(StringPool.CLOSE_PARENTHESIS);

			sb.append(afterOperator.substring(0, pos));

			sb.append("\n");
			sb.append(indent);
			sb.append(afterOperator.substring(pos));

			return StringUtil.replaceFirst(
				content, match, sb.toString(), matcher.start());
		}

		return content;
	}

	private int _getCloseParenthesisPos(String content, int startPos) {
		int endPos = startPos;

		while (true) {
			endPos = content.indexOf(")", endPos + 1);

			if (getLevel(content.substring(startPos, endPos + 1)) == 0) {
				return endPos;
			}
		}
	}

	private int _getOpenParenthesisPos(String content, int endPos) {
		int startPos = endPos;

		while (true) {
			startPos = content.lastIndexOf("(", startPos - 1);

			if (getLevel(content.substring(startPos, endPos + 1)) == 0) {
				return startPos;
			}
		}
	}

	private static final String _CUSTOM_FINDER_SCALABILITY_EXCLUDES =
		"custom.finder.scalability.excludes";

	private static final String[] _SQL_KEYWORDS = {
		"ALL", "AND", "AS", "ASC", "BITAND", "BY", "COUNT", "CROSS", "DELETE",
		"DESC", "DISTINCT", "EXISTS", "FROM", "GROUP", "HAVING", "IN", "INNER",
		"IS", "JOIN", "LEFT", "LIKE", "LOWER", "MAX", "MOD", "NOT", "NULL",
		"ON", "OR", "ORDER", "OUTER", "SELECT", "SET", "SUM", "UNION", "UPDATE",
		"WHERE"
	};

	private static final Pattern _incorrectAndOrpattern = Pattern.compile(
		"(\n\t*)(AND|OR|\\[\\$AND_OR_CONNECTOR\\$\\])( |\n)");
	private static final Pattern _incorrectLineBreakAfterCommaPattern =
		Pattern.compile(".(?<! (ASC|DESC)),\n");
	private static final Pattern _missingCountValuePattern = Pattern.compile(
		"SELECT\\s+COUNT\\([^()\n]+(\\))\\s+FROM");
	private static final Pattern _missingLineBreakAfterKeywordPattern =
		Pattern.compile("\n\\s*(.*\\s(BY|FROM|HAVING|JOIN|ON|SELECT|WHERE)) ");
	private static final Pattern _missingLineBreakAfterOpenParenthesisPattern =
		Pattern.compile("(\t+)\\(.+\n");
	private static final Pattern _missingLineBreakBeforeOpenParenthesisPattern =
		Pattern.compile("\n(\t+).*[^\t\n]\\(\n");
	private static final Pattern _missingParenthesesPattern1 = Pattern.compile(
		"\t([^\t]*(\\S))\\s+(AND|OR|\\[\\$AND_OR_CONNECTOR\\$\\])\\s*\n");
	private static final Pattern _missingParenthesesPattern2 = Pattern.compile(
		"\\s(AND|OR|\\[\\$AND_OR_CONNECTOR\\$\\])\\s+[^\\(\\[<\\s]");
	private static final Pattern _multiLineSinglePredicatePattern =
		Pattern.compile("\t\\(\n(.*)\n\t*\\)");
	private static final Pattern
		_redundantParenthesesForSingleLineClausePattern = Pattern.compile(
			"\\s(ON|WHERE)\\s+\\((.*)\\)\n(.*)\n");
	private static final Pattern _singleLineClauseWitMultiplePredicatesPattern =
		Pattern.compile(
			"\n(\t*)((.*\\)) (AND|OR|\\[\\$AND_OR_CONNECTOR\\$\\]) (\\(.*))");
	private static final Pattern _unionPattern = Pattern.compile(
		"(\\S)(\\s+)UNION( ALL)?\\s+(\\S)");
	private static final Pattern _whereNotInSQLPattern = Pattern.compile(
		"WHERE\\s.*\\sNOT IN", Pattern.DOTALL);

}