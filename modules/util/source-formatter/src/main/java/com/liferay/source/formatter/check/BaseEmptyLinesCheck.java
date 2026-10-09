/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseEmptyLinesCheck extends BaseFileCheck {

	protected String fixEmptyLinesBetweenTags(String content) {
		Matcher matcher = _emptyLineBetweenTagsPattern1.matcher(content);

		while (matcher.find()) {
			String tabs1 = matcher.group(1);
			String tabs2 = matcher.group(4);

			if (!tabs1.equals(tabs2)) {
				continue;
			}

			String lineBreaks = matcher.group(3);
			String tagName1 = matcher.group(2);
			String tagName2 = matcher.group(5);

			if (tagName1.endsWith(":when") ||
				(ArrayUtil.contains(_STYLING_TAG_NAMES, tagName1) &&
				 ArrayUtil.contains(_STYLING_TAG_NAMES, tagName2))) {

				if (lineBreaks.equals("\n\n")) {
					return StringUtil.replaceFirst(
						content, "\n\n", "\n", matcher.end(1));
				}
			}
			else if (lineBreaks.equals("\n")) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.end(1));
			}
		}

		matcher = _emptyLineBetweenTagsPattern2.matcher(content);

		while (matcher.find()) {
			int lineNumber = getLineNumber(content, matcher.start());

			String line = getLine(content, lineNumber);

			String tabs = SourceUtil.getIndent(line);

			if (!tabs.equals(matcher.group(3))) {
				continue;
			}

			String trimmedLine = StringUtil.trimLeading(line);

			if (!trimmedLine.startsWith(StringPool.LESS_THAN) ||
				line.contains("\"hidden\"")) {

				continue;
			}

			line = getLine(content, lineNumber + 1);

			if (line.contains("\"hidden\"")) {
				continue;
			}

			String tagName1 = matcher.group(2);

			if (tagName1 == null) {
				int pos = trimmedLine.indexOf(CharPool.SPACE);

				tagName1 = trimmedLine.substring(1, pos);
			}

			String tagName2 = matcher.group(4);

			if ((!tagName1.equals(tagName2) &&
				 (!ArrayUtil.contains(_STYLING_TAG_NAMES, tagName1) ||
				  !ArrayUtil.contains(_STYLING_TAG_NAMES, tagName2))) ||
				(tagName1.matches("h\\d") && tagName1.matches("h\\d"))) {

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start());
			}
		}

		matcher = _emptyLineBetweenSameSelfClosingTagsPattern.matcher(content);

		while (matcher.find()) {
			if (StringUtil.equals(matcher.group(2), "link")) {
				continue;
			}

			String match = matcher.group();

			String replacement = match.replaceAll("\n\n", "\n");

			StringBundler sb = new StringBundler();

			if (ArrayUtil.contains(
					_ENFORCE_EMPTY_LINE_SELF_CLOSING_TAG_NAMES,
					matcher.group(2))) {

				String previousLine = null;

				for (String line : replacement.split("\n")) {
					if ((previousLine != null) &&
						(!previousLine.contains("\"hidden\"") ||
						 !line.contains("\"hidden\""))) {

						sb.append("\n");
					}

					sb.append(line);
					sb.append("\n");

					previousLine = line;
				}
			}

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);

				replacement = sb.toString();
			}

			if (!replacement.equals(match)) {
				return StringUtil.replaceFirst(
					content, matcher.group(), replacement, matcher.start());
			}
		}

		matcher = _missingEmptyLineBetweenTagsPattern1.matcher(content);

		while (matcher.find()) {
			String tabs1 = matcher.group(1);
			String tabs2 = matcher.group(2);

			if (tabs1.equals(tabs2)) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.end(1));
			}
		}

		matcher = _missingEmptyLineBetweenTagsPattern2.matcher(content);

		while (matcher.find()) {
			String tabs1 = matcher.group(1);
			String tabs2 = matcher.group(2);

			if (tabs1.equals(tabs2)) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.end(1));
			}
		}

		return content;
	}

	protected String fixEmptyLinesInMultiLineTags(String content) {
		Matcher matcher = _emptyLineInMultiLineTagsPattern1.matcher(content);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				content, "\n\n", "\n", matcher.start());
		}

		matcher = _emptyLineInMultiLineTagsPattern2.matcher(content);

		while (matcher.find()) {
			String tabs1 = matcher.group(1);
			String tabs2 = matcher.group(2);

			if (tabs1.length() == (tabs2.length() + 1)) {
				return StringUtil.replaceFirst(
					content, "\n\n", "\n", matcher.start());
			}
		}

		return content;
	}

	protected String fixEmptyLinesInNestedTags(String content) {
		content = fixEmptyLinesInNestedTags(
			content, _emptyLineInNestedTagsPattern1, true);

		return fixEmptyLinesInNestedTags(
			content, _emptyLineInNestedTagsPattern2, false);
	}

	protected String fixEmptyLinesInNestedTags(
		String content, Pattern pattern, boolean startTag) {

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String tabs2 = null;

			if (startTag) {
				String secondLine = matcher.group(3);

				if (secondLine.equals("<%") || secondLine.startsWith("<%--") ||
					secondLine.startsWith("<!--")) {

					continue;
				}

				tabs2 = matcher.group(2);
			}
			else {
				String firstLine = matcher.group(2);

				if (firstLine.equals("%>") || firstLine.endsWith("-->")) {
					continue;
				}

				tabs2 = matcher.group(3);
			}

			String tabs1 = matcher.group(1);

			if ((!startTag || ((tabs1.length() + 1) != tabs2.length())) &&
				(startTag || ((tabs1.length() - 1) != tabs2.length()))) {

				continue;
			}

			content = StringUtil.replaceFirst(
				content, StringPool.NEW_LINE, StringPool.BLANK, matcher.end(1));
		}

		return content;
	}

	protected String fixIncorrectEmptyLineAfterOpenCurlyBrace(String content) {
		Matcher matcher = _incorrectOpenCurlyBracePattern.matcher(content);

		while (matcher.find()) {
			if (!isJavaSource(content, matcher.end())) {
				continue;
			}

			boolean requiresEmptyLine = false;

			String s = matcher.group(2);

			if (s != null) {
				if (getLevel(s) != 0) {
					continue;
				}

				int x = _getMatchingClosingCurlyBracePos(
					content, matcher.end() - 2);

				s = StringUtil.trim(content.substring(x + 1));

				if (!s.startsWith("}")) {
					requiresEmptyLine = true;
				}
			}
			else if (matcher.group(1) == null) {
				requiresEmptyLine = true;
			}

			String lineBreaks = matcher.group(4);

			if (requiresEmptyLine && lineBreaks.equals("\n")) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start(4));
			}

			if (!requiresEmptyLine && lineBreaks.equals("\n\n")) {
				return StringUtil.replaceFirst(
					content, "\n\n", "\n", matcher.start(4));
			}
		}

		return content;
	}

	protected String fixIncorrectEmptyLineBeforeCloseCurlyBrace(
		String content) {

		Matcher matcher1 = _incorrectCloseCurlyBracePattern1.matcher(content);

		while (matcher1.find()) {
			if (!isJavaSource(content, matcher1.end())) {
				continue;
			}

			String lastLine = StringUtil.trimLeading(matcher1.group(1));

			if (lastLine.startsWith("// ")) {
				continue;
			}

			String tabs = matcher1.group(2);

			int tabCount = tabs.length();

			int pos = matcher1.start();

			while (true) {
				pos = content.lastIndexOf("\n" + tabs, pos - 1);

				char c = content.charAt(pos + tabCount + 1);

				if ((c == CharPool.NEW_LINE) || (c == CharPool.TAB)) {
					continue;
				}

				String codeBlock = content.substring(pos + 1, matcher1.end());

				String firstLine = codeBlock.substring(
					0, codeBlock.indexOf(CharPool.NEW_LINE) + 1);

				Matcher matcher2 = _incorrectCloseCurlyBracePattern2.matcher(
					firstLine);

				if (matcher2.find()) {
					break;
				}

				return StringUtil.replaceFirst(
					content, "\n\n" + tabs + "}\n", "\n" + tabs + "}\n", pos);
			}
		}

		matcher1 = _incorrectCloseCurlyBracePattern3.matcher(content);

		while (matcher1.find()) {
			if (!isJavaSource(content, matcher1.end())) {
				continue;
			}

			String lineBreaks = matcher1.group(1);

			int x = _getMatchingOpenCurlyBracePos(content, matcher1.end());

			int lineNumber = getLineNumber(content, x);

			String lineAfterOpenCurlyBrace = getLine(content, lineNumber + 1);

			if (Validator.isNull(lineAfterOpenCurlyBrace) &&
				lineBreaks.equals("\n")) {

				String nextLineAfterOpenCurlyBrace = StringUtil.trim(
					getLine(content, lineNumber + 2));

				if (nextLineAfterOpenCurlyBrace.startsWith("//")) {
					continue;
				}

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher1.start(1));
			}

			if (Validator.isNotNull(lineAfterOpenCurlyBrace) &&
				lineBreaks.equals("\n\n")) {

				return StringUtil.replaceFirst(
					content, "\n\n", "\n", matcher1.start(1));
			}
		}

		return content;
	}

	protected String fixMissingEmptyLineAfterDoctype(String content) {
		Matcher matcher = _missingEmptyLineAfterDoctypePattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				content, "\n", "\n\n", matcher.start());
		}

		return content;
	}

	protected String fixMissingEmptyLineAfterSettingVariable(String content) {
		Matcher matcher1 = _setVariablePattern.matcher(content);

		while (matcher1.find()) {
			if (!isJavaSource(content, matcher1.start()) ||
				(content.charAt(matcher1.end()) == CharPool.NEW_LINE)) {

				continue;
			}

			int x = content.indexOf(";\n", matcher1.end());

			if (x == -1) {
				return content;
			}

			String nextCommand = content.substring(matcher1.end(), x + 1);

			if (nextCommand.contains("{\n") ||
				nextCommand.matches("\t*%>[\\S\\s]*")) {

				continue;
			}

			String variableName = matcher1.group(2);

			Pattern pattern2 = Pattern.compile("\\W(" + variableName + ")\\W");

			Matcher matcher2 = pattern2.matcher(nextCommand);

			if (!matcher2.find()) {
				continue;
			}

			x = matcher2.start(1);

			if (ToolsUtil.isInsideQuotes(nextCommand, x)) {
				continue;
			}

			x += matcher1.end();

			int y = content.lastIndexOf("\ttry (", x);

			if (y != -1) {
				int z = content.indexOf(") {\n", y);

				if (z > x) {
					continue;
				}
			}

			return StringUtil.replaceFirst(
				content, "\n", "\n\n", matcher1.end(3));
		}

		return content;
	}

	protected String fixMissingEmptyLines(String absolutePath, String content) {
		Matcher matcher = _missingEmptyLinePattern1.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start()) &&
				(getLevel(matcher.group()) == 0)) {

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start());
			}
		}

		matcher = _missingEmptyLinePattern2.matcher(content);

		while (matcher.find()) {
			if (!isJavaSource(content, matcher.start())) {
				continue;
			}

			String match = matcher.group();

			if (!match.contains(StringPool.OPEN_PARENTHESIS)) {
				continue;
			}

			String whitespace = matcher.group(1);

			int x = content.indexOf(whitespace + "}\n", matcher.end());
			int y = content.indexOf(whitespace + "}\n\n", matcher.end());

			if ((x != -1) && (x != y)) {
				return StringUtil.replaceFirst(content, "\n", "\n\n", x + 1);
			}
		}

		matcher = _missingEmptyLinePattern3.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start()) &&
				(getLevel(matcher.group(1)) != 0)) {

				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.end(1));
			}
		}

		matcher = _missingEmptyLinePattern4.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start());
			}
		}

		matcher = _missingEmptyLinePattern5.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start() + 1);
			}
		}

		matcher = _missingEmptyLinePattern6.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start());
			}
		}

		matcher = _missingEmptyLinePattern7.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start());
			}
		}

		return content;
	}

	protected String fixMissingEmptyLinesAroundComments(String content) {
		Matcher matcher = _missingEmptyLineAfterCommentPattern.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start() + 1);
			}
		}

		matcher = _missingEmptyLineBeforeCommentPattern.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, "\n", "\n\n", matcher.start() + 1);
			}
		}

		return content;
	}

	protected String fixRedundantEmptyLines(String content) {
		outerLoop:
		while (true) {
			Matcher matcher = _redundantEmptyLinePattern1.matcher(content);

			while (matcher.find()) {
				if (!isJavaSource(content, matcher.start())) {
					continue;
				}

				String previousLine = StringUtil.trim(matcher.group(1));

				if (previousLine.startsWith("import ") ||
					previousLine.startsWith("package ")) {

					continue;
				}

				content = StringUtil.replaceFirst(
					content, "\n", StringPool.BLANK, matcher.end(1));

				continue outerLoop;
			}

			matcher = _redundantEmptyLinePattern2.matcher(content);

			while (matcher.find()) {
				if (!isJavaSource(content, matcher.start())) {
					continue;
				}

				String nextLine = matcher.group(1);

				if (nextLine.startsWith("import ") ||
					nextLine.startsWith("package ") ||
					nextLine.startsWith("/*")) {

					continue;
				}

				content = StringUtil.replaceFirst(
					content, "\n", StringPool.BLANK, matcher.start() + 1);

				continue outerLoop;
			}

			matcher = _redundantEmptyLinePattern3.matcher(content);

			while (matcher.find()) {
				if (!isJavaSource(content, matcher.start())) {
					continue;
				}

				content = StringUtil.replaceFirst(
					content, "\n", StringPool.BLANK, matcher.start() + 1);

				continue outerLoop;
			}

			matcher = _redundantEmptyLinePattern4.matcher(content);

			while (matcher.find()) {
				if (!isJavaSource(content, matcher.start())) {
					continue;
				}

				content = StringUtil.replaceFirst(
					content, "\n", StringPool.BLANK, matcher.start());

				continue outerLoop;
			}

			matcher = _redundantEmptyLinePattern5.matcher(content);

			while (matcher.find()) {
				if (!isJavaSource(content, matcher.start())) {
					continue;
				}

				content = StringUtil.replaceFirst(
					content, "\n", StringPool.BLANK, matcher.start());

				continue outerLoop;
			}

			break;
		}

		return content;
	}

	private int _getMatchingClosingCurlyBracePos(String content, int start) {
		int x = start;

		while (true) {
			x = content.indexOf(CharPool.CLOSE_CURLY_BRACE, x + 1);

			if (getLevel(content.substring(start, x + 1), "{", "}") == 0) {
				return x;
			}
		}
	}

	private int _getMatchingOpenCurlyBracePos(String content, int start) {
		int x = start;

		while (true) {
			x = content.lastIndexOf(CharPool.OPEN_CURLY_BRACE, x - 1);

			int y = content.lastIndexOf("\n", x - 1);

			String s = StringUtil.trim(content.substring(y, x));

			if (s.startsWith("//") || s.startsWith("*")) {
				continue;
			}

			if (getLevel(content.substring(x, start), "{", "}") == 0) {
				return x;
			}
		}
	}

	private static final String[] _ENFORCE_EMPTY_LINE_SELF_CLOSING_TAG_NAMES = {
		"img", "input"
	};

	private static final String[] _STYLING_TAG_NAMES = {
		"dd", "dt", "li", "span", "td", "th", "tr"
	};

	private static final Pattern _emptyLineBetweenSameSelfClosingTagsPattern =
		Pattern.compile("(?<=\n)(\t*<(\\w+) ).+?/>(\n+\\1.+?/>(?=\n))+");
	private static final Pattern _emptyLineBetweenTagsPattern1 =
		Pattern.compile("\n(\t*)</([-\\w:]+)>(\n*)(\t*)<([-\\w:]+)[> \n]");
	private static final Pattern _emptyLineBetweenTagsPattern2 =
		Pattern.compile("(\\S</([\\w:]+)>| />)\n(\t+)<([-\\w:]+)[> \n]");
	private static final Pattern _emptyLineInMultiLineTagsPattern1 =
		Pattern.compile("\n\t*<[-\\w:#]+\n\n\t*\\w");
	private static final Pattern _emptyLineInMultiLineTagsPattern2 =
		Pattern.compile("\n(\t*)\\S*[^>]\n\n(\t*)(/?)>\n");
	private static final Pattern _emptyLineInNestedTagsPattern1 =
		Pattern.compile("(?:\\A|\n)(\t*)(?:<\\w.*[^/])?>\n(?=\n(\t*)(<.*)\n)");
	private static final Pattern _emptyLineInNestedTagsPattern2 =
		Pattern.compile("\n(\t*)(.*>)\n(?=\n(\t*)</.*(\n|$))");
	private static final Pattern _incorrectCloseCurlyBracePattern1 =
		Pattern.compile("\n(.+)\n\n(\t+)}\n");
	private static final Pattern _incorrectCloseCurlyBracePattern2 =
		Pattern.compile("(\t| )@?(class|enum|interface|new)\\s");
	private static final Pattern _incorrectCloseCurlyBracePattern3 =
		Pattern.compile("\n\t*\\}(\n+)\t*\\}\\)*;\n");
	private static final Pattern _incorrectOpenCurlyBracePattern =
		Pattern.compile(
			"\n.*?(\\Wnew (.*\\)) |\\[\\] (\\w+ = )?)?\\{(\n+)\t*\\{\n");
	private static final Pattern _missingEmptyLineAfterCommentPattern =
		Pattern.compile("\n\t*// .*\n[\t ]*(?!// )\\S");
	private static final Pattern _missingEmptyLineAfterDoctypePattern =
		Pattern.compile(
			"^(<\\?xml .*\\?>|<\\!DOCTYPE .*>)\n<\\w",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static final Pattern _missingEmptyLineBeforeCommentPattern =
		Pattern.compile("\n[\t ]*(?!// )\\S.*\n\t*// ");
	private static final Pattern _missingEmptyLineBetweenTagsPattern1 =
		Pattern.compile("\n(\t*)/>\n(\t*)<[-\\w:]+[> \n]");
	private static final Pattern _missingEmptyLineBetweenTagsPattern2 =
		Pattern.compile(
			"\n(\t*)<.* />\n(\t*)<([-\\w:]+|\\w((?!</| />).)*[^/]>)\n");
	private static final Pattern _missingEmptyLinePattern1 = Pattern.compile(
		"(\t| = | -> |return )new .*\\(.*\\) \\{\n\t+[^{\t]");
	private static final Pattern _missingEmptyLinePattern2 = Pattern.compile(
		"(\n\t*)(public|private|protected) [^;]+? \\{");
	private static final Pattern _missingEmptyLinePattern3 = Pattern.compile(
		"\n(.*\\) \\{)\n[\t ]*[^ \n\t\\}]");
	private static final Pattern _missingEmptyLinePattern4 = Pattern.compile(
		"[^%{:/\n]\n\t*(for|if|try) [({]");
	private static final Pattern _missingEmptyLinePattern5 = Pattern.compile(
		"[\t\n]\\}\n[\t ]*(?!(%>|/?\\*|\\}|\\)|//|catch |else |finally " +
			"|while ))\\S");
	private static final Pattern _missingEmptyLinePattern6 = Pattern.compile(
		"[^%{:\\s]\n\t*(break|continue|return|throw)[ ;]");
	private static final Pattern _missingEmptyLinePattern7 = Pattern.compile(
		"[^%{\\s]\n\t*break;");
	private static final Pattern _redundantEmptyLinePattern1 = Pattern.compile(
		"\n(.*)\n\npublic ((abstract|static) )*(class|enum|interface) ");
	private static final Pattern _redundantEmptyLinePattern2 = Pattern.compile(
		"\n\t* \\*/\n\n\t*(.+)\n");
	private static final Pattern _redundantEmptyLinePattern3 = Pattern.compile(
		"[\n\t](catch |else |finally |for |if |try |while ).*\\{\n\n\t+\\w");
	private static final Pattern _redundantEmptyLinePattern4 = Pattern.compile(
		"\\{\n\n\t*\\}");
	private static final Pattern _redundantEmptyLinePattern5 = Pattern.compile(
		"\\}\n\n\t*(catch|else( if)?|finally) [\\(\\{]");
	private static final Pattern _setVariablePattern = Pattern.compile(
		"(?<=[\n\t])[A-Z]\\w+(<[^\n]+>)? (\\w+) =\\s+((?!\\{\n).)*?;\n",
		Pattern.DOTALL);

}