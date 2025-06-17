/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.comparator.PropertyValueComparator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class GradleStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = _fixMissingLineBreakAroundCurlyBraces(content);
		content = _sortFileNames(content);
		content = _sortMapKeys("transformKeys", content);
		content = _stylingCheck(content, _stylingPattern1, "$1$2 {\n\t$3\n}$4");
		content = _stylingCheck(content, _stylingPattern2, "$1$2 = $3$4");

		return content;
	}

	private String _fixMissingLineBreakAroundCurlyBraces(String content) {
		int openCurlyBracePosition = -1;

		outerLoop:
		while (true) {
			openCurlyBracePosition = content.indexOf(
				StringPool.OPEN_CURLY_BRACE, openCurlyBracePosition + 1);

			if (openCurlyBracePosition == -1) {
				break;
			}

			char previousChar = content.charAt(openCurlyBracePosition - 1);

			if (previousChar == CharPool.DOLLAR) {
				continue;
			}

			int[] multiLineStringsPositions = SourceUtil.getMultiLinePositions(
				content, _multiLineStringsPattern);

			if (ToolsUtil.isInsideQuotes(content, openCurlyBracePosition) ||
				SourceUtil.isInsideMultiLines(
					SourceUtil.getLineNumber(content, openCurlyBracePosition),
					multiLineStringsPositions) ||
				_isInRegexPattern(content, openCurlyBracePosition) ||
				_isInSingleLineComment(content, openCurlyBracePosition)) {

				continue;
			}

			int closeCurlyBracePosition = openCurlyBracePosition;

			while (true) {
				closeCurlyBracePosition = content.indexOf(
					StringPool.CLOSE_CURLY_BRACE, closeCurlyBracePosition + 1);

				if (closeCurlyBracePosition == -1) {
					break;
				}

				if (ToolsUtil.isInsideQuotes(
						content, closeCurlyBracePosition) ||
					SourceUtil.isInsideMultiLines(
						SourceUtil.getLineNumber(
							content, closeCurlyBracePosition),
						multiLineStringsPositions) ||
					_isInRegexPattern(content, closeCurlyBracePosition) ||
					_isInSingleLineComment(content, closeCurlyBracePosition)) {

					continue;
				}

				String curlyBraceContent = content.substring(
					openCurlyBracePosition, closeCurlyBracePosition + 1);

				int level = getLevel(
					curlyBraceContent, StringPool.OPEN_CURLY_BRACE,
					StringPool.CLOSE_CURLY_BRACE);

				if (level != 0) {
					continue;
				}

				char nextChar;

				if (closeCurlyBracePosition < (content.length() - 1)) {
					nextChar = content.charAt(closeCurlyBracePosition + 1);

					if ((nextChar == CharPool.CLOSE_PARENTHESIS) ||
						(nextChar == CharPool.PERIOD)) {

						continue outerLoop;
					}
				}

				previousChar = content.charAt(closeCurlyBracePosition - 1);

				if ((previousChar != CharPool.NEW_LINE) &&
					(previousChar != CharPool.TAB)) {

					return StringUtil.insert(
						content, "\n", closeCurlyBracePosition);
				}

				nextChar = content.charAt(openCurlyBracePosition + 1);

				if (nextChar != CharPool.NEW_LINE) {
					return StringUtil.insert(
						content, "\n", openCurlyBracePosition + 1);
				}
			}
		}

		return content;
	}

	private boolean _isInRegexPattern(String content, int position) {
		int lineNumber = getLineNumber(content, position);

		String line = getLine(content, lineNumber);

		int lineStartPos = getLineStartPos(content, lineNumber);

		int regexPatternStartPos = content.indexOf("~ /", lineStartPos);

		if (regexPatternStartPos == -1) {
			regexPatternStartPos = content.indexOf("~/", lineStartPos);

			if (regexPatternStartPos == -1) {
				regexPatternStartPos = content.indexOf(
					".matches(/", lineStartPos);
			}
		}

		if (regexPatternStartPos == -1) {
			return false;
		}

		int regexPatternEndPos = line.lastIndexOf("/");

		if (regexPatternEndPos == -1) {
			return false;
		}

		regexPatternEndPos = lineStartPos + regexPatternEndPos;

		if ((position > regexPatternStartPos) &&
			(position < regexPatternEndPos)) {

			return true;
		}

		return false;
	}

	private boolean _isInSingleLineComment(String content, int position) {
		int lineNumber = getLineNumber(content, position);

		String line = getLine(content, lineNumber);

		line = line.trim();

		return line.startsWith("//");
	}

	private String _sortFileNames(String content) {
		Matcher matcher = _fileNamespattern.matcher(content);

		List<String> fileNames = new ArrayList<>();

		while (matcher.find()) {
			String match = matcher.group();

			for (String fileName : match.split("\n")) {
				if (Validator.isNull(fileName)) {
					continue;
				}

				fileNames.add(fileName);
			}

			Collections.sort(fileNames, new FileNameComparator());

			StringBundler sb = new StringBundler(fileNames.size() * 2);

			for (String fileName : fileNames) {
				sb.append(StringPool.NEW_LINE);
				sb.append(fileName);
			}

			String replacement = sb.toString();

			if (!StringUtil.equals(replacement, match)) {
				return StringUtil.replaceFirst(
					content, match, replacement, matcher.start());
			}
		}

		return content;
	}

	private String _sortMapKeys(String mapName, String content) {
		Pattern pattern = Pattern.compile(
			"\n(\t*)(" + mapName + ") = \\[([\\s\\S]*?)\\]\n");

		Matcher matcher1 = pattern.matcher(content);

		if (!matcher1.find()) {
			return content;
		}

		String match = matcher1.group(3);

		if (Validator.isNull(match)) {
			return content;
		}

		Map<String, String> map = new TreeMap<>(
			new NaturalOrderStringComparator());

		Matcher matcher2 = _mapKeyPattern.matcher(match);

		while (matcher2.find()) {
			map.put(matcher2.group(1), matcher2.group(2));
		}

		StringBundler sb = new StringBundler(map.size() * 9);

		String indent = matcher1.group(1);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (map.size() == 1) {
				sb.append(entry.getKey());
				sb.append(": ");
				sb.append(entry.getValue());

				break;
			}

			sb.append(CharPool.NEW_LINE);
			sb.append(indent);
			sb.append(CharPool.TAB);
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(CharPool.COMMA);
		}

		if (map.size() > 1) {
			sb.setIndex(sb.index() - 1);
			sb.append(CharPool.NEW_LINE);
			sb.append(indent);
		}

		return StringUtil.replaceFirst(content, match, sb.toString());
	}

	private String _stylingCheck(
		String content, Pattern pattern, String replacement) {

		int[] multiLineStringsPositions = SourceUtil.getMultiLinePositions(
			content, _multiLineStringsPattern);

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (!SourceUtil.isInsideMultiLines(
					SourceUtil.getLineNumber(content, matcher.start()),
					multiLineStringsPositions)) {

				return matcher.replaceFirst(replacement);
			}
		}

		return content;
	}

	private static final Pattern _fileNamespattern = Pattern.compile(
		"(\n\t*fileNames .+){2,}");
	private static final Pattern _mapKeyPattern = Pattern.compile(
		"(\".+?\") *: *(\".+?\")");
	private static final Pattern _multiLineStringsPattern = Pattern.compile(
		"(\"\"\"|''')\\\\\n.*?\\1", Pattern.DOTALL);
	private static final Pattern _stylingPattern1 = Pattern.compile(
		"(\\A|\n)(\\w+)\\.(\\w+ = \\w+)(\n|\\Z)");
	private static final Pattern _stylingPattern2 = Pattern.compile(
		"(\\A|\n)(\t*\\w+)(?! = .) *=(?!~) *(.*?)(\n|\\Z)");

	private class FileNameComparator extends PropertyValueComparator {

		@Override
		public int compare(String fileName1, String fileName2) {
			return super.compare(
				fileName1.substring(fileName1.indexOf("fileNames ") + 10),
				fileName2.substring(fileName2.indexOf("fileNames ") + 10));
		}

	}

}