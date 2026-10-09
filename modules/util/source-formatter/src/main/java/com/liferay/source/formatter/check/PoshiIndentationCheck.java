/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JSONSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _fixTripleQuotesIndentation(content);
	}

	private String _fixCurlIndentation(
		String tripleQuotesContent, String indent) {

		String trimmedContent = tripleQuotesContent.trim();

		String[] lines = trimmedContent.split("\n");

		if (lines.length < 2) {
			return tripleQuotesContent;
		}

		int leadingTabCount = indent.length();

		lines[0] = _fixTabs(leadingTabCount + 1, lines[0]);

		int level = leadingTabCount + 2;

		for (int i = 1; i < lines.length; i++) {
			String trimmedLine = StringUtil.trim(lines[i]);

			lines[i] = _fixTabs(level, trimmedLine);

			trimmedLine = trimmedLine.replaceAll(
				"(.*?)\\$\\{.*?\\}(.*)", "$1$2");

			level += getLevel(trimmedLine, "[{", "}]");

			trimmedLine = StringUtil.removeSubstrings(trimmedLine, "[{", "}]");

			level += getLevel(
				trimmedLine,
				new String[] {
					StringPool.OPEN_BRACKET, StringPool.OPEN_CURLY_BRACE
				},
				new String[] {
					StringPool.CLOSE_BRACKET, StringPool.CLOSE_CURLY_BRACE
				});

			if (!trimmedLine.endsWith("'{")) {
				continue;
			}

			level++;
		}

		StringBundler sb = new StringBundler((lines.length * 2) + 2);

		for (String line : lines) {
			sb.append(CharPool.NEW_LINE);
			sb.append(line);
		}

		sb.append(CharPool.NEW_LINE);
		sb.append(indent);

		return _formatJSONInCurl(sb.toString());
	}

	private String _fixTableIndentation(
		String tripleQuotesContent, String indent) {

		String trimmedContent = tripleQuotesContent.trim();

		if (!trimmedContent.startsWith("|") && !trimmedContent.endsWith("|")) {
			return tripleQuotesContent;
		}

		String[] lines = trimmedContent.split("\n");

		for (int i = 0; i < lines.length; i++) {
			if (Validator.isNull(lines[i])) {
				continue;
			}

			String trimmedLine = StringUtil.trim(lines[i]);

			if (!trimmedLine.startsWith("|") || !trimmedLine.endsWith("|")) {
				return tripleQuotesContent;
			}

			lines[i] = indent + StringPool.TAB + trimmedLine;
		}

		StringBundler sb = new StringBundler((lines.length * 2) + 2);

		for (String line : lines) {
			sb.append(CharPool.NEW_LINE);
			sb.append(line);
		}

		sb.append(CharPool.NEW_LINE);
		sb.append(indent);

		return sb.toString();
	}

	private String _fixTabs(int expectedTabCount, String line) {
		if (line.equals("]") || line.equals("]'") || line.equals("],") ||
			line.equals("}") || line.equals("}'") || line.equals("},") ||
			line.equals("}]") || line.equals("}]'") || line.equals("}],")) {

			expectedTabCount--;
		}

		StringBundler sb = new StringBundler(expectedTabCount + 1);

		for (int i = 0; i < expectedTabCount; i++) {
			sb.append(CharPool.TAB);
		}

		sb.append(StringUtil.trim(line));

		return sb.toString();
	}

	private String _fixTripleQuotesIndentation(String content) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _tripleQuotesPattern.matcher(content);

		while (matcher.find()) {
			String replacement = null;
			String variableName = matcher.group(3);

			if (variableName.matches("(?i)\\w*curl")) {
				replacement = _fixCurlIndentation(
					matcher.group(4), matcher.group(1));
			}
			else if (variableName.equals("table")) {
				replacement = _fixTableIndentation(
					matcher.group(4), matcher.group(1));
			}

			if ((replacement == null) ||
				StringUtil.equals(matcher.group(4), replacement)) {

				continue;
			}

			matcher.appendReplacement(
				sb,
				Matcher.quoteReplacement(
					StringUtil.replaceFirst(
						matcher.group(), matcher.group(4), replacement)));
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private String _formatJSONInCurl(String content) {
		int x = content.indexOf("-d '{");

		if (x == -1) {
			return content;
		}

		int y = x;

		while (true) {
			y = content.indexOf("}", y + 1);

			if (y == -1) {
				return content;
			}

			if (ToolsUtil.isInsideQuotes(content, y)) {
				continue;
			}

			String s = content.substring(x + 4, y + 1);

			int level = getLevel(
				s, StringPool.OPEN_CURLY_BRACE, StringPool.CLOSE_CURLY_BRACE);

			if (level != 0) {
				continue;
			}

			JSONObject jsonObject = JSONSourceUtil.getJSONObject(s);

			if (jsonObject == null) {
				break;
			}

			int lineNumber = SourceUtil.getLineNumber(content, x);

			String replacement = JSONSourceUtil.fixIndentation(
				jsonObject,
				SourceUtil.getIndent(SourceUtil.getLine(content, lineNumber)));

			replacement = StringUtil.trim(replacement);

			if (s.equals(replacement)) {
				break;
			}

			return StringUtil.replaceFirst(content, s, replacement, x);
		}

		return content;
	}

	private static final Pattern _tripleQuotesPattern = Pattern.compile(
		"(?:\n)(\t+)(var )?(\\w+) = '''(.+?)'''[,;)]", Pattern.DOTALL);

}