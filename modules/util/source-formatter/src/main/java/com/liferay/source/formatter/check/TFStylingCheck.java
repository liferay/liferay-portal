/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.TFSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class TFStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String[] lines = content.split("\n", -1);

		boolean blockComment = false;

		for (int i = 0; i < lines.length; i++) {
			if (!blockComment) {
				lines[i] = _quoteBlockLabels(lines[i]);
				lines[i] = _removeSpacesAroundEqualSigns(lines[i]);
			}

			blockComment = TFSourceUtil.appendBrackets(
				lines[i], blockComment, new StringBundler());

			String heredocMarker = TFSourceUtil.getHeredocMarker(lines[i]);

			if (heredocMarker == null) {
				continue;
			}

			i = TFSourceUtil.getHeredocEndLineIndex(lines, i, heredocMarker);

			if (i == -1) {
				return content;
			}
		}

		StringBundler sb = new StringBundler(lines.length * 2);

		for (String line : lines) {
			sb.append(line);
			sb.append(CharPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private boolean _isAssignmentEqualSign(String line, int index) {
		if (index > 0) {
			char previousChar = line.charAt(index - 1);

			if ((previousChar == CharPool.EQUAL) ||
				(previousChar == CharPool.EXCLAMATION) ||
				(previousChar == CharPool.GREATER_THAN) ||
				(previousChar == CharPool.LESS_THAN)) {

				return false;
			}
		}

		if ((index + 1) < line.length()) {
			char nextChar = line.charAt(index + 1);

			if ((nextChar == CharPool.EQUAL) ||
				(nextChar == CharPool.GREATER_THAN)) {

				return false;
			}
		}

		return true;
	}

	private String _quoteBlockLabels(String line) {
		String trimmedLine = StringUtil.trim(line);

		if (!trimmedLine.endsWith(StringPool.OPEN_CURLY_BRACE)) {
			return line;
		}

		trimmedLine = StringUtil.trim(
			trimmedLine.substring(0, trimmedLine.length() - 1));

		String[] parts = trimmedLine.split("\\s+");

		if ((parts.length == 1) || (parts.length > 3)) {
			return line;
		}

		Matcher matcher = _blockTypePattern.matcher(parts[0]);

		if (!matcher.matches()) {
			return line;
		}

		StringBundler sb = new StringBundler();

		sb.append(line.substring(0, line.indexOf(trimmedLine)));
		sb.append(parts[0]);

		for (int i = 1; i < parts.length; i++) {
			String part = parts[i];

			matcher = _labelPattern.matcher(part);

			if (!matcher.matches()) {
				return line;
			}

			sb.append(CharPool.SPACE);

			if (part.startsWith(StringPool.QUOTE)) {
				sb.append(part);

				continue;
			}

			sb.append(CharPool.QUOTE);
			sb.append(part);
			sb.append(CharPool.QUOTE);
		}

		sb.append(" {");

		return sb.toString();
	}

	private String _removeSpacesAroundEqualSigns(String line) {
		StringBundler sb = new StringBundler();

		int index = 0;
		int startIndex = 0;

		while (index < line.length()) {
			char c = line.charAt(index);

			if (c == CharPool.QUOTE) {
				index = TFSourceUtil.getStringEndIndex(line, index + 1);

				continue;
			}

			if ((c == CharPool.POUND) || line.startsWith("//", index) ||
				line.startsWith("/*", index)) {

				break;
			}

			if ((c != CharPool.EQUAL) || !_isAssignmentEqualSign(line, index)) {
				index++;

				continue;
			}

			sb.append(
				StringUtil.trimTrailing(line.substring(startIndex, index)));
			sb.append(CharPool.EQUAL);

			index++;

			while ((index < line.length()) &&
				   Character.isWhitespace(line.charAt(index))) {

				index++;
			}

			startIndex = index;
		}

		if (startIndex == 0) {
			return line;
		}

		sb.append(line.substring(startIndex));

		return sb.toString();
	}

	private static final Pattern _blockTypePattern = Pattern.compile("[\\w-]+");
	private static final Pattern _labelPattern = Pattern.compile(
		"\"[^\"]*\"|[\\w-]+");

}