/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JSPSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPStylingCheck extends BaseStylingCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _combineJavaSourceBlocks(fileName, content);

		content = _formatJSPExpressionTag(content);

		content = _formatLineBreak(fileName, content);

		content = _fixEmptyJavaSourceTag(content);

		content = _fixIncorrectBacktick(content);

		content = _fixIncorrectClosingTag(content);

		content = _fixIncorrectSingleLineJavaSource(content);

		content = StringUtil.replace(
			content,
			new String[] {
				"alert('<%= LanguageUtil.", "alert(\"<%= LanguageUtil.",
				"confirm('<%= LanguageUtil.", "confirm(\"<%= LanguageUtil.",
				";;\n"
			},
			new String[] {
				"alert('<%= UnicodeLanguageUtil.",
				"alert(\"<%= UnicodeLanguageUtil.",
				"confirm('<%= UnicodeLanguageUtil.",
				"confirm(\"<%= UnicodeLanguageUtil.", ";\n"
			});

		content = content.replaceAll("'<%= (\"((?!\").)*?\") %>'", "$1");

		content = content.replaceAll(
			"((['\"])<%= ((?<!%>).)*?)\\\\(\".+?)\\\\(\".*?%>\\2)", "$1$4$5");

		Matcher matcher = _portletNamespacePattern.matcher(content);

		while (matcher.find()) {
			String s = matcher.group(2);

			if (s.endsWith(StringPool.CLOSE_PARENTHESIS) &&
				(getLevel(s) == 0)) {

				return StringUtil.insert(
					content, StringPool.SEMICOLON, matcher.end() - 1);
			}
		}

		return formatStyling(content);
	}

	private String _combineJavaSourceBlocks(String fileName, String content) {
		int x = -1;

		while (true) {
			x = content.indexOf("<%!", x + 1);

			if ((x == -1) || !ToolsUtil.isInsideQuotes(content, x)) {
				break;
			}
		}

		int y = x;

		while (true) {
			y = content.indexOf("<%\n", y + 1);

			if ((y == -1) || !ToolsUtil.isInsideQuotes(content, y)) {
				break;
			}
		}

		if ((x != -1) && (y != -1) && (x < y)) {
			addMessage(
				fileName, "\"<%!...%>\" block should come after <%...%> block",
				getLineNumber(content, x));

			return content;
		}

		y = x;

		while (true) {
			y = content.indexOf("<%!", y + 1);

			if (y == -1) {
				break;
			}

			if (!ToolsUtil.isInsideQuotes(content, y)) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Combine <%!...%> blocks at line \"",
						getLineNumber(content, x), "\" and \"",
						getLineNumber(content, y), "\""));

				return content;
			}
		}

		Matcher matcher = _adjacentJavaBlocksPattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				content, matcher.group(), "\n\n", matcher.start() - 1);
		}

		return content;
	}

	private String _fixEmptyJavaSourceTag(String content) {
		Matcher matcher = _emptyJavaSourceTagPattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.removeSubstring(content, matcher.group());
		}

		return content;
	}

	private String _fixIncorrectBacktick(String content) {
		Matcher matcher = _incorrectBacktickPattern.matcher(content);

		if (matcher.find() &&
			JSPSourceUtil.isJSSource(content, matcher.start())) {

			return StringUtil.replaceFirst(
				content, matcher.group(), "'" + matcher.group(1) + "'",
				matcher.start());
		}

		return content;
	}

	private String _fixIncorrectClosingTag(String content) {
		Matcher matcher = _incorrectClosingTagPattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				content, " />\n", "\n" + matcher.group(1) + "/>\n",
				matcher.end(1));
		}

		return content;
	}

	private String _fixIncorrectSingleLineJavaSource(String content) {
		Matcher matcher = _incorrectSingleLineJavaSourcePattern.matcher(
			content);

		while (matcher.find()) {
			String javaSource = matcher.group(3);

			if (javaSource.contains("<%")) {
				continue;
			}

			String indent = matcher.group(1);

			StringBundler sb = new StringBundler(6);

			sb.append("<%\n");
			sb.append(indent);
			sb.append(StringUtil.trim(javaSource));
			sb.append("\n");
			sb.append(indent);
			sb.append("%>");

			return StringUtil.replaceFirst(
				content, matcher.group(2), sb.toString(), matcher.start());
		}

		return content;
	}

	private String _formatJSPExpressionTag(String content) {
		Matcher matcher = _jspExpressionTagPattern.matcher(content);

		while (matcher.find()) {
			if (!ToolsUtil.isInsideQuotes(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, matcher.group(), "<portlet:namespace />",
					matcher.start());
			}
		}

		return content;
	}

	private String _formatLineBreak(String fileName, String content) {
		Matcher matcher = _incorrectLineBreakPattern1.matcher(content);

		while (matcher.find()) {
			if (JSPSourceUtil.isJSSource(content, matcher.start(1))) {
				continue;
			}

			addMessage(
				fileName, "There should be a line break after \"}\"",
				getLineNumber(content, matcher.start(1)));
		}

		matcher = _incorrectLineBreakPattern2.matcher(content);

		while (matcher.find()) {
			if (isJavaSource(content, matcher.start())) {
				return StringUtil.replaceFirst(
					content, matcher.group(1), StringPool.SPACE,
					matcher.start());
			}
		}

		matcher = _incorrectLineBreakPattern3.matcher(content);

		while (matcher.find()) {
			addMessage(
				fileName, "There should be a line break after \"<%=\"",
				getLineNumber(content, matcher.start()));
		}

		matcher = _incorrectLineBreakPattern4.matcher(content);

		content = matcher.replaceAll("$1\n\t$2$4\n$2$5");

		matcher = _incorrectLineBreakPattern5.matcher(content);

		while (matcher.find()) {
			String s = matcher.group(2);

			if (!s.contains("%>") && !s.startsWith("HashMapBuilder.")) {
				return StringUtil.replaceFirst(
					content, matcher.group(),
					StringBundler.concat(
						matcher.group(1), " ",
						StringUtil.trim(matcher.group(2)), " ",
						matcher.group(3)));
			}
		}

		matcher = _incorrectLineBreakPattern6.matcher(content);

		content = matcher.replaceAll("$1\n$2\t$3\n$2$4");

		matcher = _incorrectLineBreakPattern7.matcher(content);

		while (matcher.find()) {
			int lineNumber = getLineNumber(content, matcher.start(1));

			String line = SourceUtil.getLine(content, lineNumber);

			if (line.endsWith("%>")) {
				continue;
			}

			addMessage(
				fileName, "There should be a line break after \"<%\"",
				lineNumber);
		}

		return content;
	}

	private static final Pattern _adjacentJavaBlocksPattern = Pattern.compile(
		"\n\t*%>\n+\t*<%\n");
	private static final Pattern _emptyJavaSourceTagPattern = Pattern.compile(
		"\n\t*<%\\!?\n+\t*%>(\n|\\Z)");
	private static final Pattern _incorrectBacktickPattern = Pattern.compile(
		"`((.(?!\\{.+\\}))*)`");
	private static final Pattern _incorrectClosingTagPattern = Pattern.compile(
		"\n(\t*)\t((?!<\\w).)* />\n");
	private static final Pattern _incorrectLineBreakPattern1 = Pattern.compile(
		"[\n\t]\\} ?(catch|else|finally) ");
	private static final Pattern _incorrectLineBreakPattern2 = Pattern.compile(
		"=(\n\\s*).*;\n");
	private static final Pattern _incorrectLineBreakPattern3 = Pattern.compile(
		"<%= *\\S((?!%>).)*\n");
	private static final Pattern _incorrectLineBreakPattern4 = Pattern.compile(
		"(\n(\t*)<(\\w+)>)(<\\w+>.*)(</\\3>\n)");
	private static final Pattern _incorrectLineBreakPattern5 = Pattern.compile(
		"(<%=)\n\t*(.*)\n\t*(%>)");
	private static final Pattern _incorrectLineBreakPattern6 = Pattern.compile(
		"(\n(\t+)\\w+='<%=) (HashMapBuilder\\..*) *(%>')");
	private static final Pattern _incorrectLineBreakPattern7 = Pattern.compile(
		"\n(\t*<% .)");
	private static final Pattern _incorrectSingleLineJavaSourcePattern =
		Pattern.compile("(\t*)(<% (.*) %>)\n");
	private static final Pattern _jspExpressionTagPattern = Pattern.compile(
		"<%= liferayPortletResponse\\.getNamespace\\(\\) %>");
	private static final Pattern _portletNamespacePattern = Pattern.compile(
		"=([\"'])<portlet:namespace />(\\w+\\(.*?)\\1");

}