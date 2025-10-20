/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.XMLSourceUtil;

import java.io.IOException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class WhitespaceCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = _trimContent(fileName, absolutePath, content);

		content = StringUtil.replace(content, "\n\n\n", "\n\n");

		if (content.startsWith(StringPool.NEW_LINE)) {
			content = content.substring(1);
		}

		if (content.endsWith(StringPool.RETURN)) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	protected String formatClosingTag(String content) {
		Matcher matcher = _closingTagPattern.matcher(content);

		while (matcher.find()) {
			if (XMLSourceUtil.isInsideCDATAMarkup(content, matcher.start())) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, matcher.group(),
				matcher.group(1) + " " + matcher.group(2), matcher.start());
		}

		return content;
	}

	protected String formatDoubleSpace(String line) {
		String trimmedLine = StringUtil.trim(line);

		int x = -1;

		while (true) {
			x = trimmedLine.indexOf(StringPool.DOUBLE_SPACE, x + 1);

			if (x == -1) {
				return line;
			}

			if (!ToolsUtil.isInsideQuotes(trimmedLine, x)) {
				return StringUtil.replace(
					line, trimmedLine,
					StringUtil.replaceFirst(
						trimmedLine, StringPool.DOUBLE_SPACE, StringPool.SPACE,
						x));
			}
		}
	}

	protected String formatIncorrectSyntax(String line, String regex) {
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(line);

		while (matcher.find()) {
			int x = matcher.start(1);

			if (!ToolsUtil.isInsideQuotes(line, x)) {
				return StringUtil.insert(line, StringPool.SPACE, x);
			}
		}

		return line;
	}

	protected String formatIncorrectSyntax(
		String line, String incorrectSyntax, String correctSyntax,
		boolean lineStart) {

		if (lineStart) {
			if (line.startsWith(incorrectSyntax)) {
				line = StringUtil.replaceFirst(
					line, incorrectSyntax, correctSyntax);
			}

			return line;
		}

		for (int x = -1;;) {
			x = line.indexOf(incorrectSyntax, x + 1);

			if (x == -1) {
				return line;
			}

			if (!ToolsUtil.isInsideQuotes(line, x)) {
				line = StringUtil.replaceFirst(
					line, incorrectSyntax, correctSyntax, x);
			}
		}
	}

	protected String formatSelfClosingTags(String line) {
		Matcher matcher = _selfClosingTagsPattern.matcher(line);

		while (matcher.find()) {
			int level = 1;

			for (int x = matcher.end(); x < line.length(); x++) {
				if (ToolsUtil.isInsideQuotes(line, x)) {
					continue;
				}

				char c = line.charAt(x);

				if (c == CharPool.LESS_THAN) {
					level++;
				}
				else if (c == CharPool.GREATER_THAN) {
					level--;
				}

				if (level != 0) {
					continue;
				}

				if (Objects.equals(line.substring(x - 2, x), " /")) {
					break;
				}

				char previousChar = line.charAt(x - 1);

				if (previousChar == CharPool.SPACE) {
					return StringUtil.insert(line, StringPool.SLASH, x);
				}

				if (previousChar == CharPool.SLASH) {
					return StringUtil.insert(line, StringPool.SPACE, x - 1);
				}

				return StringUtil.insert(line, " /", x);
			}
		}

		return line;
	}

	protected String formatWhitespace(
		String line, String linePart, boolean javaSource) {

		String originalLinePart = linePart;

		linePart = formatIncorrectSyntax(linePart, "catch(", "catch (", true);
		linePart = formatIncorrectSyntax(linePart, "else{", "else {", true);
		linePart = formatIncorrectSyntax(
			linePart, "else if(", "else if (", true);
		linePart = formatIncorrectSyntax(linePart, "for(", "for (", true);
		linePart = formatIncorrectSyntax(linePart, "if(", "if (", true);
		linePart = formatIncorrectSyntax(linePart, "task(", "task (", true);
		linePart = formatIncorrectSyntax(linePart, "while(", "while (", true);
		linePart = formatIncorrectSyntax(linePart, "List <", "List<", false);
		linePart = formatIncorrectSyntax(linePart, "}else", "}\nelse", true);
		linePart = formatIncorrectSyntax(linePart, "} else", "}\nelse", true);

		if (javaSource) {
			linePart = formatIncorrectSyntax(linePart, " ...", "...", false);
			linePart = formatIncorrectSyntax(linePart, " .", ".", false);
			linePart = formatIncorrectSyntax(linePart, ". ", ".", false);
			linePart = formatIncorrectSyntax(linePart, " [", "[", false);
			linePart = formatIncorrectSyntax(linePart, "{ ", "{", false);
			linePart = formatIncorrectSyntax(linePart, " }", "}", false);
			linePart = formatIncorrectSyntax(linePart, " )", ")", false);
			linePart = formatIncorrectSyntax(linePart, "( ", "(", false);
			linePart = formatIncorrectSyntax(linePart, "){", ") {", false);
			linePart = formatIncorrectSyntax(linePart, "]{", "] {", false);
			linePart = formatIncorrectSyntax(linePart, "[^\"'\\s](\\|\\|)");
			linePart = formatIncorrectSyntax(linePart, "\\|\\|([^\"'\\s])");
			linePart = formatIncorrectSyntax(linePart, "[^\"'\\s](\\&\\&)");
			linePart = formatIncorrectSyntax(linePart, "\\&\\&([^\"'\\s])");
			linePart = formatIncorrectSyntax(linePart, "\\.\\.\\.(\\w)");
			linePart = formatIncorrectSyntax(linePart, "\\w(=)");
			linePart = formatIncorrectSyntax(linePart, "=(\\w)");
			linePart = formatIncorrectSyntax(
				linePart, "for \\([^:]*[^:\"'\\s](:)");
			linePart = formatIncorrectSyntax(
				linePart, "for \\([^:]*:([^:\"'\\s])");
		}

		if (!javaSource) {
			line = StringUtil.replace(line, originalLinePart, linePart);

			return formatIncorrectSyntax(
				line, StringPool.SPACE + StringPool.TAB, StringPool.TAB, false);
		}

		if (!line.contains(StringPool.DOUBLE_SLASH)) {
			while (linePart.contains(StringPool.TAB)) {
				linePart = StringUtil.replaceLast(
					linePart, CharPool.TAB, CharPool.SPACE);
			}
		}

		if (line.contains(StringPool.DOUBLE_SLASH)) {
			line = StringUtil.replace(line, originalLinePart, linePart);

			return formatIncorrectSyntax(
				line, StringPool.SPACE + StringPool.TAB, StringPool.TAB, false);
		}

		int pos = linePart.indexOf(") ");

		if ((pos != -1) && ((pos + 2) < linePart.length()) &&
			!linePart.contains(StringPool.AT) &&
			!ToolsUtil.isInsideQuotes(linePart, pos)) {

			String linePart2 = linePart.substring(pos + 2);

			if (Character.isLetter(linePart2.charAt(0)) &&
				!linePart2.startsWith("default") &&
				!linePart2.startsWith("instanceof") &&
				!linePart2.startsWith("return") &&
				!linePart2.startsWith("throws")) {

				linePart = StringUtil.replaceFirst(
					linePart, StringPool.SPACE, StringPool.BLANK, pos);
			}
		}

		pos = linePart.indexOf(" (");

		if ((pos != -1) && !linePart.contains(StringPool.EQUAL) &&
			!ToolsUtil.isInsideQuotes(linePart, pos) &&
			(linePart.startsWith("private ") ||
			 linePart.startsWith("protected ") ||
			 linePart.startsWith("public "))) {

			linePart = StringUtil.replaceFirst(linePart, " (", "(", pos);
		}

		for (int x = -1;;) {
			int posComma = linePart.indexOf(CharPool.COMMA, x + 1);
			int posSemicolon = linePart.indexOf(CharPool.SEMICOLON, x + 1);

			if ((posComma == -1) && (posSemicolon == -1)) {
				break;
			}

			x = Math.min(posComma, posSemicolon);

			if (x == -1) {
				x = Math.max(posComma, posSemicolon);
			}

			if (ToolsUtil.isInsideQuotes(linePart, x)) {
				continue;
			}

			if (linePart.length() > (x + 1)) {
				char nextChar = linePart.charAt(x + 1);

				if ((nextChar != CharPool.APOSTROPHE) &&
					(nextChar != CharPool.CLOSE_PARENTHESIS) &&
					(nextChar != CharPool.SPACE) &&
					(nextChar != CharPool.STAR)) {

					linePart = StringUtil.insert(
						linePart, StringPool.SPACE, x + 1);
				}
			}

			if (x > 0) {
				char previousChar = linePart.charAt(x - 1);

				if (previousChar == CharPool.SPACE) {
					linePart =
						linePart.substring(0, x - 1) + linePart.substring(x);
				}
			}
		}

		line = StringUtil.replace(line, originalLinePart, linePart);

		return formatIncorrectSyntax(
			line, StringPool.SPACE + StringPool.TAB, StringPool.TAB, false);
	}

	protected boolean isAllowLeadingSpaces(
		String fileName, String absolutePath) {

		return isAttributeValue(_ALLOW_LEADING_SPACES_KEY, absolutePath);
	}

	protected boolean isAllowTrailingEmptyLines(
		String fileName, String absolutePath) {

		return isAttributeValue(_ALLOW_TRAILING_EMPTY_LINES, absolutePath);
	}

	protected boolean isAllowTrailingSpaces(String line) {
		return false;
	}

	protected String trimLine(
		String fileName, String absolutePath, String line) {

		String trimmedTrailingLine = StringUtil.trimTrailing(line);

		if (trimmedTrailingLine.length() == 0) {
			return StringPool.BLANK;
		}

		if (!isAllowTrailingSpaces(line) &&
			(!isAttributeValue(
				_ALLOW_TRAILING_DOUBLE_SPACE_KEY, absolutePath) ||
			 !line.endsWith(StringPool.DOUBLE_SPACE))) {

			line = trimmedTrailingLine;
		}

		if (!isAttributeValue(_ALLOW_DOUBLE_SPACE_KEY, absolutePath)) {
			line = formatDoubleSpace(line);
		}

		if (isAllowLeadingSpaces(fileName, absolutePath) ||
			line.startsWith(" *")) {

			return line;
		}

		while (line.matches("^\t*    .*")) {
			line = StringUtil.replaceFirst(
				line, StringPool.FOUR_SPACES, StringPool.TAB);
		}

		while (line.startsWith(StringPool.SPACE)) {
			line = StringUtil.replaceFirst(
				line, CharPool.SPACE, StringPool.BLANK);
		}

		return line;
	}

	protected String trimLine(
		String fileName, String absolutePath, String content, String line,
		int lineNumber) {

		return trimLine(fileName, absolutePath, line);
	}

	private String _trimContent(
			String fileName, String absolutePath, String content)
		throws IOException {

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				sb.append(
					trimLine(
						fileName, absolutePath, content, line, lineNumber));
				sb.append("\n");
			}
		}

		if (isAllowTrailingEmptyLines(fileName, absolutePath) &&
			content.endsWith("\n")) {

			return sb.toString();
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private static final String _ALLOW_DOUBLE_SPACE_KEY = "allowDoubleSpace";

	private static final String _ALLOW_LEADING_SPACES_KEY =
		"allowLeadingSpaces";

	private static final String _ALLOW_TRAILING_DOUBLE_SPACE_KEY =
		"allowTrailingDoubleSpace";

	private static final String _ALLOW_TRAILING_EMPTY_LINES =
		"allowTrailingEmptyLines";

	private static final Pattern _closingTagPattern = Pattern.compile(
		"(<[\\w-.:]+)(/>)");
	private static final Pattern _selfClosingTagsPattern = Pattern.compile(
		"<(area|base|br|col|command|embed|hr|img|input|keygen|link|meta|" +
			"param|source|track|wbr)(?!( />|\\w))");

}