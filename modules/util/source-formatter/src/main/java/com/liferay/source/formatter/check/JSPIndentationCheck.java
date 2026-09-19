/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith(".jsp") && !fileName.endsWith(".jspf") &&
			!fileName.endsWith(".tag")) {

			return content;
		}

		String originalContent = content;

		while (true) {
			String newContent = _formatTabs(content, originalContent);

			if (newContent.equals(content) ||
				newContent.equals(originalContent)) {

				return _fixTabsInJavaSource(newContent);
			}

			content = newContent;
		}
	}

	private int _calculateTabLevel(String text, boolean javaSource) {
		if (javaSource) {
			return getLevel(
				text,
				new String[] {
					StringPool.OPEN_CURLY_BRACE, StringPool.OPEN_PARENTHESIS
				},
				new String[] {
					StringPool.CLOSE_CURLY_BRACE, StringPool.CLOSE_PARENTHESIS
				});
		}

		if (text.matches("(--)?%>")) {
			return -1;
		}

		text = _stripJavaSource(text);

		text = SourceUtil.stripQuotes(text);

		int level = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (c == '<') {
				if (((i + 1) < text.length()) && (text.charAt(i + 1) == '!')) {
					continue;
				}

				if (((i + 1) < text.length()) && (text.charAt(i + 1) == '/')) {
					level -= 1;
				}
				else {
					level += 1;
				}
			}
			else if ((c == '>') && (i > 0) && (text.charAt(i - 1) == '/')) {
				level -= 1;
			}
			else if (c == '{') {
				level += 1;
			}
			else if (c == '}') {
				level -= 1;
			}
		}

		return level;
	}

	private String _fixTabs(String content, int lineNumber, int diff) {
		return _fixTabs(content, lineNumber, lineNumber, diff);
	}

	private String _fixTabs(
		String content, int startLine, int endLine, int diff) {

		boolean insideUnformattedTextTag = false;

		for (int i = startLine; i <= endLine; i++) {
			String line = getLine(content, i);

			if (Validator.isNull(line) || (getLeadingTabCount(line) < diff)) {
				continue;
			}

			String trimmedLine = StringUtil.trimLeading(line);

			if (insideUnformattedTextTag) {
				if (trimmedLine.matches(".*</(pre|textarea)>")) {
					insideUnformattedTextTag = false;

					if (trimmedLine.matches(".+</(pre|textarea)>")) {
						continue;
					}
				}
				else {
					continue;
				}
			}

			int lineStartPos = getLineStartPos(content, i);

			if (diff < 0) {
				content =
					content.substring(0, lineStartPos) + "\t" +
						content.substring(lineStartPos);
			}
			else {
				content =
					content.substring(0, lineStartPos) +
						content.substring(lineStartPos + 1);
			}

			if (!insideUnformattedTextTag &&
				trimmedLine.matches("<(pre|textarea).*")) {

				insideUnformattedTextTag = true;
			}
		}

		return content;
	}

	private String _fixTabsInJavaSource(String content) {
		Matcher matcher = _javaSourcePattern1.matcher(content);

		while (matcher.find()) {
			String tabs = matcher.group(1);

			int minimumTabCount = _getMinimumTabCount(matcher.group(2));

			if (tabs.length() != minimumTabCount) {
				int diff = minimumTabCount - tabs.length();
				int end = getLineNumber(content, matcher.end(2));
				int start = getLineNumber(content, matcher.start(3));

				return _fixTabs(content, start, end, diff);
			}

			String javaCloseTagTabs = matcher.group(4);

			if (!tabs.equals(javaCloseTagTabs)) {
				int diff = javaCloseTagTabs.length() - tabs.length();

				return _fixTabs(
					content, getLineNumber(content, matcher.end(4)), diff);
			}
		}

		matcher = _javaSourcePattern2.matcher(content);

		while (matcher.find()) {
			String tabs = matcher.group(1);

			int minimumTabCount = _getMinimumTabCount(matcher.group(2));

			if ((tabs.length() + 1) != minimumTabCount) {
				int diff = minimumTabCount - (tabs.length() + 1);
				int end = getLineNumber(content, matcher.end(2));
				int start = getLineNumber(content, matcher.start(3));

				return _fixTabs(content, start, end, diff);
			}
		}

		return content;
	}

	private String _formatTabs(String content, String originalContent)
		throws IOException {

		List<JSPLine> jspLines = _getJSPLines(content);

		if (jspLines == null) {
			return originalContent;
		}

		for (int i = 0; i < jspLines.size(); i++) {
			JSPLine jspLine = jspLines.get(i);

			if (jspLine.isCloseTag()) {
				if (!jspLine.isClosed()) {
					return originalContent;
				}

				continue;
			}

			String line = jspLine.getLine();

			if (!jspLine.isOpenTag()) {
				if (!jspLine.isJavaSource()) {
					int actualTabCount = jspLine.getLeadingTabCount();
					int expectedTabCount = jspLine.getTabLevel();

					String trimmedLine = StringUtil.trim(line);

					if (trimmedLine.equals(StringPool.GREATER_THAN)) {
						expectedTabCount -= 1;
					}

					if (expectedTabCount != actualTabCount) {
						return _fixTabs(
							content, jspLine.getLineNumber(),
							actualTabCount - expectedTabCount);
					}
				}

				continue;
			}

			JSPLine closeTagJSPLine = _getCloseTagJSPLine(jspLine, jspLines, i);

			if (closeTagJSPLine == null) {
				return originalContent;
			}

			String closeTagName = closeTagJSPLine.getTagName();
			String openTagName = jspLine.getTagName();

			if (Validator.isNotNull(closeTagName) &&
				!closeTagName.equals(openTagName)) {

				return originalContent;
			}

			int expectedTabCount = _getExpectedTabCount(
				jspLine, closeTagJSPLine, jspLines, i);

			int actualCloseTagTabCount = closeTagJSPLine.getLeadingTabCount();
			int actualOpenTagTabCount = jspLine.getLeadingTabCount();

			if (expectedTabCount != actualOpenTagTabCount) {
				int diff = actualOpenTagTabCount - expectedTabCount;

				if (actualOpenTagTabCount == actualCloseTagTabCount) {
					return _fixTabs(
						content, jspLine.getLineNumber(),
						closeTagJSPLine.getLineNumber(), diff);
				}

				return _fixTabs(content, jspLine.getLineNumber(), diff);
			}

			if (expectedTabCount != actualCloseTagTabCount) {
				return _fixTabs(
					content, closeTagJSPLine.getLineNumber(),
					actualCloseTagTabCount - expectedTabCount);
			}

			closeTagJSPLine.setClosed(true);
		}

		return content;
	}

	private JSPLine _getCloseTagJSPLine(
		JSPLine openTagJSPLine, List<JSPLine> jspLines, int index) {

		while (true) {
			index++;

			if (index >= jspLines.size()) {
				return null;
			}

			JSPLine jspLine = jspLines.get(index);

			if (!jspLine.isCloseTag()) {
				continue;
			}

			String openTagLine = openTagJSPLine.getLine();

			if (openTagLine.matches("\t*<%!?")) {
				String line = jspLine.getLine();

				if (line.matches("\t*%>")) {
					return jspLine;
				}

				continue;
			}

			if (jspLine.isCloseTag() &&
				(openTagJSPLine.getTabLevel() ==
					(jspLine.getLineTabLevel() + jspLine.getTabLevel()))) {

				return jspLine;
			}
		}
	}

	private int _getExpectedTabCount(
		JSPLine startTagJSPLine, JSPLine closeTagJSPLine,
		List<JSPLine> jspLines, int index) {

		String startTagLine = startTagJSPLine.getLine();

		if (!startTagLine.matches("\t*<%!?")) {
			return startTagJSPLine.getTabLevel();
		}

		int minJavaSourceLevel = startTagJSPLine.getTabLevel();

		while (true) {
			index++;

			JSPLine jspLine = jspLines.get(index);

			minJavaSourceLevel = Math.min(
				minJavaSourceLevel, jspLine.getTabLevel() - 1);

			if (jspLine.equals(closeTagJSPLine)) {
				return minJavaSourceLevel;
			}
		}
	}

	private List<JSPLine> _getJSPLines(String content) throws IOException {
		List<JSPLine> jspLines = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			int lineNumber = 0;

			int tabLevel = 0;

			boolean javaSource = false;
			boolean scriptSource = false;
			boolean multiLineComment = false;
			boolean insideUnformattedTextTag = false;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (Validator.isNull(line)) {
					continue;
				}

				String trimmedLine = StringUtil.trimLeading(line);

				if (trimmedLine.matches(
						"(<%@ )?(page import|tag import|taglib uri)=.*")) {

					continue;
				}

				if (javaSource) {
					if (trimmedLine.matches("%>[\"']?")) {
						javaSource = false;
					}
					else if (trimmedLine.startsWith("/*") &&
							 !trimmedLine.startsWith("/**")) {

						multiLineComment = true;

						continue;
					}

					if (line.endsWith("*/")) {
						multiLineComment = false;

						continue;
					}

					if (multiLineComment || trimmedLine.startsWith("*") ||
						trimmedLine.startsWith("/*") ||
						trimmedLine.startsWith("//")) {

						continue;
					}
				}

				int lineTabLevel = _calculateTabLevel(trimmedLine, javaSource);

				if (!javaSource && !scriptSource && line.endsWith("--%>")) {
					multiLineComment = false;
				}

				if (insideUnformattedTextTag) {
					if (trimmedLine.matches(".*</(pre|textarea)>")) {
						insideUnformattedTextTag = false;

						if (trimmedLine.matches(".+</(pre|textarea)>")) {
							continue;
						}
					}
					else {
						continue;
					}
				}

				if (scriptSource) {
					if (trimmedLine.matches("</(aui:)?script>")) {
						scriptSource = false;
					}
					else {
						continue;
					}
				}

				if (trimmedLine.equals("AUI.add(")) {
					return jspLines;
				}

				if (!multiLineComment) {
					if (!javaSource && (Math.abs(lineTabLevel) > 1)) {
						return null;
					}

					JSPLine jspLine = new JSPLine(
						line, lineNumber, tabLevel, lineTabLevel, javaSource);

					jspLines.add(jspLine);
				}

				if (!javaSource &&
					(trimmedLine.matches("<%!?") ||
					 trimmedLine.matches(".*[\"']<%="))) {

					javaSource = true;
				}
				else if (!multiLineComment) {
					if (trimmedLine.matches("<(aui:)?script.*") &&
						(lineTabLevel > 0)) {

						scriptSource = true;
					}
					else if (trimmedLine.startsWith("<%--") &&
							 !line.endsWith("--%>")) {

						multiLineComment = true;
					}
					else if (trimmedLine.matches("<(pre|textarea).*") &&
							 !trimmedLine.matches(".*</(pre|textarea)>")) {

						insideUnformattedTextTag = true;

						continue;
					}
				}

				tabLevel += lineTabLevel;
			}
		}

		return jspLines;
	}

	private int _getLeadingTabCount(String line) {
		return getLeadingTabCount(line);
	}

	private int _getMinimumTabCount(String s) {
		int minimumTabCount = -1;

		String[] lines = StringUtil.splitLines(s);

		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];

			if (Validator.isNull(line)) {
				continue;
			}

			int tabCount = getLeadingTabCount(line);

			if ((minimumTabCount == -1) || (tabCount < minimumTabCount)) {
				minimumTabCount = tabCount;
			}
		}

		return minimumTabCount;
	}

	private String _stripJavaSource(String text) {
		while (true) {
			int x = text.indexOf("<%");

			if (x == -1) {
				return text;
			}

			int y = text.indexOf("%>", x);

			if (y == -1) {
				return text;
			}

			text = text.substring(0, x) + text.substring(y + 2);
		}
	}

	private static final Pattern _javaSourcePattern1 = Pattern.compile(
		"\n(\t*)(<%\\!?\n(\t*[^\t%].*?))\n(\t*)%>(\n|\\Z)", Pattern.DOTALL);
	private static final Pattern _javaSourcePattern2 = Pattern.compile(
		"\n(\t*)([^\t\n]+[\"']<%=\n(\t*[^\t%].*?))\n\t*%>[\"']\n",
		Pattern.DOTALL);

	private class JSPLine {

		public JSPLine(
			String line, int lineNumber, int tabLevel, int lineTabLevel,
			boolean javaSource) {

			_line = line;
			_lineNumber = lineNumber;
			_tabLevel = tabLevel;
			_lineTabLevel = lineTabLevel;
			_javaSource = javaSource;
		}

		public int getLeadingTabCount() {
			return _getLeadingTabCount(_line);
		}

		public String getLine() {
			return _line;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public int getLineTabLevel() {
			return _lineTabLevel;
		}

		public int getTabLevel() {
			return _tabLevel;
		}

		public String getTagName() {
			Matcher matcher = null;

			if (isCloseTag()) {
				matcher = _closeTagNamePattern.matcher(_line);
			}
			else if (isOpenTag()) {
				matcher = _openTagNamePattern.matcher(_line);
			}
			else {
				return null;
			}

			if (matcher.find()) {
				return matcher.group(1);
			}

			return null;
		}

		public boolean isClosed() {
			return _closed;
		}

		public boolean isCloseTag() {
			if (!_javaSource && (_lineTabLevel == -1)) {
				return true;
			}

			return false;
		}

		public boolean isJavaSource() {
			return _javaSource;
		}

		public boolean isOpenTag() {
			if (!_javaSource && (_lineTabLevel == 1) &&
				_line.matches("^\\s*<.*")) {

				return true;
			}

			return false;
		}

		public void setClosed(boolean closed) {
			_closed = closed;
		}

		private boolean _closed;
		private final Pattern _closeTagNamePattern = Pattern.compile(
			"</([\\-:\\w]+?)>");
		private final boolean _javaSource;
		private final String _line;
		private final int _lineNumber;
		private int _lineTabLevel;
		private final Pattern _openTagNamePattern = Pattern.compile(
			"<([\\-:\\w]+?)([ >\n].*|$)");
		private final int _tabLevel;

	}

}