/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.XMLSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class XMLIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _fixLineBreak(content);

		while (true) {
			String newContent = _fixTagsIndentation(content);

			if (newContent.equals(content)) {
				return newContent;
			}

			content = newContent;
		}
	}

	private String _fixIndentation(
		String content, String line, int expectedTabCount, int lineNumber) {

		StringBundler sb = new StringBundler(expectedTabCount + 1);

		for (int i = 0; i < expectedTabCount; i++) {
			sb.append(CharPool.TAB);
		}

		sb.append(StringUtil.trim(line));

		String newLine = sb.toString();

		if (line.equals(newLine)) {
			return content;
		}

		return StringUtil.replaceFirst(
			content, line, newLine, getLineStartPos(content, lineNumber));
	}

	private String _fixLineBreak(String content) {
		int x = -1;

		while (true) {
			x = content.indexOf(">", x + 2);

			if (x == -1) {
				break;
			}

			String s = StringUtil.trimLeading(content.substring(x + 1), '\n');

			if (Validator.isNull(s) || StringUtil.startsWith(s, "\n") ||
				!s.startsWith("<") || s.startsWith("</") ||
				s.startsWith("<![CDATA[") ||
				XMLSourceUtil.isInsideCDATAMarkup(content, x) ||
				XMLSourceUtil.isInsideComment(content, x)) {

				continue;
			}

			content = StringUtil.insert(content, "\n", x + 1);
		}

		Matcher matcher = _incorrectLineBreakPattern1.matcher(content);

		while (matcher.find()) {
			String s = matcher.group(1);

			if (s.equals("</code>") ||
				XMLSourceUtil.isInsideCDATAMarkup(content, matcher.start()) ||
				XMLSourceUtil.isInsideComment(content, matcher.start())) {

				continue;
			}

			return StringUtil.replaceFirst(
				content, matcher.group(2), "\n", matcher.start(2));
		}

		matcher = _incorrectLineBreakPattern2.matcher(content);

		while (matcher.find()) {
			if (XMLSourceUtil.isInsideCDATAMarkup(content, matcher.start()) ||
				XMLSourceUtil.isInsideComment(content, matcher.start())) {

				continue;
			}

			return StringUtil.replaceFirst(
				content, matcher.group(2), "\n", matcher.start(2));
		}

		return content;
	}

	private String _fixMultiLineTagAttributesIndentation(
		String content, String[] lines, TokenOccurrence previousTokenOccurrence,
		TokenOccurrence tokenOccurrence, int expectedTabCount) {

		if ((previousTokenOccurrence == null) ||
			(tokenOccurrence.getLineNumber() ==
				previousTokenOccurrence.getLineNumber())) {

			return content;
		}

		String previousToken = previousTokenOccurrence.getToken();

		if (!previousToken.equals(_TAG_OPEN)) {
			return content;
		}

		String token = tokenOccurrence.getToken();

		if (!token.equals(_MULTI_LINE_TAG_CLOSE) && !token.equals(_TAG_CLOSE)) {
			return content;
		}

		String startLine = lines[previousTokenOccurrence.getLineNumber() - 1];

		if (!startLine.matches("\\s*<[-\\w:]+")) {
			return content;
		}

		for (int i = previousTokenOccurrence.getLineNumber() + 1;
			 i < tokenOccurrence.getLineNumber(); i++) {

			String line = lines[i - 1];

			if (!line.matches("\\s*[\\w-]+=.*")) {
				continue;
			}

			content = _fixIndentation(content, line, expectedTabCount, i);
		}

		return content;
	}

	private String _fixTagIndentation(
		String content, String line, TokenOccurrence tokenOccurrence,
		int expectedTabCount) {

		if (tokenOccurrence.getLinePos() != 0) {
			return content;
		}

		String token = tokenOccurrence.getToken();

		if (token.equals(_CDATA_CLOSE) || token.equals(_CDATA_OPEN) ||
			token.equals(_COMMENT_TAG_CLOSE)) {

			return content;
		}

		if (token.equals(_CLOSING_TAG_OPEN) || token.equals(_TAG_CLOSE) ||
			token.equals(_MULTI_LINE_TAG_CLOSE)) {

			expectedTabCount--;
		}

		return _fixIndentation(
			content, line, expectedTabCount, tokenOccurrence.getLineNumber());
	}

	private String _fixTagsIndentation(String content) {
		String[] lines = StringUtil.splitLines(content);

		int level = 0;
		String[] tokens = {
			_COMMENT_TAG_OPEN, _DOCTYPE_TAG_OPEN, _HEADER_TAG_OPEN, _TAG_OPEN
		};

		TokenOccurrence previousTokenOccurrence = null;

		while (true) {
			TokenOccurrence tokenOccurrence = _getNextTokenOccurrence(
				lines, previousTokenOccurrence, tokens);

			if (tokenOccurrence == null) {
				return content;
			}

			String newContent = _fixTagIndentation(
				content, lines[tokenOccurrence.getLineNumber() - 1],
				tokenOccurrence, level);

			newContent = _fixMultiLineTagAttributesIndentation(
				newContent, lines, previousTokenOccurrence, tokenOccurrence,
				level);

			if (!newContent.equals(content)) {
				return newContent;
			}

			String token = tokenOccurrence.getToken();

			if (token.equals(_CDATA_OPEN)) {
				tokens = new String[] {_CDATA_CLOSE};
			}
			else if (token.equals(_CLOSING_TAG_OPEN)) {
				level--;
				tokens = new String[] {_TAG_CLOSE};
			}
			else if (token.equals(_COMMENT_TAG_OPEN)) {
				tokens = new String[] {_COMMENT_TAG_CLOSE};
			}
			else if (token.equals(_DOCTYPE_TAG_OPEN) ||
					 token.equals(_HEADER_TAG_OPEN)) {

				tokens = new String[] {_TAG_CLOSE};
			}
			else if (token.equals(_TAG_OPEN)) {
				level++;
				tokens = new String[] {_MULTI_LINE_TAG_CLOSE, _TAG_CLOSE};
			}
			else {
				if (token.equals(_MULTI_LINE_TAG_CLOSE)) {
					level--;
				}

				tokens = new String[] {
					_CDATA_OPEN, _CLOSING_TAG_OPEN, _COMMENT_TAG_OPEN,
					_DOCTYPE_TAG_OPEN, _HEADER_TAG_OPEN, _TAG_OPEN
				};
			}

			previousTokenOccurrence = tokenOccurrence;
		}
	}

	private int _getFirstMatchPos(String line, int startPos, String token) {
		int pos = startPos;

		while (true) {
			pos = line.indexOf(token, pos + 1);

			if ((pos == -1) ||
				(!token.equals(_MULTI_LINE_TAG_CLOSE) &&
				 !token.equals(_TAG_CLOSE))) {

				return pos;
			}

			int i = Math.max(0, startPos);

			if (!ToolsUtil.isInsideQuotes(line.substring(i), pos - i, false)) {
				return pos;
			}
		}
	}

	private TokenOccurrence _getNextTokenOccurrence(
		String[] lines, TokenOccurrence previousTokenOccurrence,
		String... tokens) {

		int startLine = 1;
		int startPos = -1;

		if (previousTokenOccurrence != null) {
			startLine = previousTokenOccurrence.getLineNumber();
			startPos = previousTokenOccurrence.getLinePos();
		}

		String match = null;
		int min = -1;

		for (int i = startLine; i < lines.length; i++) {
			String line = StringUtil.trim(lines[i - 1]);

			if (Validator.isNull(line)) {
				continue;
			}

			for (String token : tokens) {
				int matchPos = _getFirstMatchPos(line, startPos, token);

				if ((matchPos == -1) || ((min != -1) && (min <= matchPos))) {
					continue;
				}

				match = token;
				min = matchPos;
			}

			if (min != -1) {
				return new TokenOccurrence(i, min, match);
			}

			startPos = -1;
		}

		return null;
	}

	private static final String _CDATA_CLOSE = "]]>";

	private static final String _CDATA_OPEN = "<![CDATA[";

	private static final String _CLOSING_TAG_OPEN = "</";

	private static final String _COMMENT_TAG_CLOSE = "-->";

	private static final String _COMMENT_TAG_OPEN = "<!--";

	private static final String _DOCTYPE_TAG_OPEN = "<!";

	private static final String _HEADER_TAG_OPEN = "<?";

	private static final String _MULTI_LINE_TAG_CLOSE = "/>";

	private static final String _TAG_CLOSE = ">";

	private static final String _TAG_OPEN = "<";

	private static final Pattern _incorrectLineBreakPattern1 = Pattern.compile(
		"(</[-\\w:]+>)( *)(</[-\\w:]+>)");
	private static final Pattern _incorrectLineBreakPattern2 = Pattern.compile(
		"(?!\")(/>)( *)(</[-\\w:]+>)");

	private class TokenOccurrence {

		public TokenOccurrence(int lineNumber, int linePos, String token) {
			_lineNumber = lineNumber;
			_linePos = linePos;
			_token = token;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public int getLinePos() {
			return _linePos;
		}

		public String getToken() {
			return _token;
		}

		private final int _lineNumber;
		private final int _linePos;
		private final String _token;

	}

}