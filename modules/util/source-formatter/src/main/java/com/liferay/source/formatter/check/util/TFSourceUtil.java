/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class TFSourceUtil {

	public static boolean appendBrackets(
		String line, boolean blockComment, StringBundler sb) {

		int index = 0;

		if (blockComment) {
			index = line.indexOf("*/");

			if (index == -1) {
				return true;
			}

			index += 2;
		}

		while (index < line.length()) {
			char c = line.charAt(index);

			if ((c == CharPool.POUND) || line.startsWith("//", index)) {
				return false;
			}

			if (c == CharPool.QUOTE) {
				index = getStringEndIndex(line, index + 1);

				continue;
			}

			if (line.startsWith("/*", index)) {
				index = line.indexOf("*/", index + 2);

				if (index == -1) {
					return true;
				}

				index += 2;

				continue;
			}

			if ((c == CharPool.CLOSE_BRACKET) ||
				(c == CharPool.CLOSE_CURLY_BRACE) ||
				(c == CharPool.CLOSE_PARENTHESIS) ||
				(c == CharPool.OPEN_BRACKET) ||
				(c == CharPool.OPEN_CURLY_BRACE) ||
				(c == CharPool.OPEN_PARENTHESIS)) {

				sb.append(c);
			}

			index++;
		}

		return false;
	}

	public static int getHeredocEndLineIndex(
		String[] lines, int lineIndex, String heredocMarker) {

		for (int i = lineIndex + 1; i < lines.length; i++) {
			if (heredocMarker.equals(StringUtil.trim(lines[i]))) {
				return i;
			}
		}

		return -1;
	}

	public static String getHeredocMarker(String line) {
		Matcher matcher = _heredocPattern.matcher(
			StringUtil.trim(removeComment(line)));

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public static int getStringEndIndex(String line, int index) {
		while (index < line.length()) {
			char c = line.charAt(index);

			if (c == CharPool.BACK_SLASH) {
				index += 2;

				continue;
			}

			if (c == CharPool.QUOTE) {
				return index + 1;
			}

			if (line.startsWith("$${", index) ||
				line.startsWith("%%{", index)) {

				index += 3;

				continue;
			}

			if (line.startsWith("${", index) || line.startsWith("%{", index)) {
				index = _getTemplateEndIndex(line, index + 2);

				continue;
			}

			index++;
		}

		return Math.min(index, line.length());
	}

	public static String removeComment(String line) {
		int index = 0;

		while (index < line.length()) {
			char c = line.charAt(index);

			if ((c == CharPool.POUND) || line.startsWith("//", index) ||
				line.startsWith("/*", index)) {

				return line.substring(0, index);
			}

			if (c == CharPool.QUOTE) {
				index = getStringEndIndex(line, index + 1);

				continue;
			}

			index++;
		}

		return line;
	}

	private static int _getTemplateEndIndex(String line, int index) {
		int level = 0;

		while (index < line.length()) {
			char c = line.charAt(index);

			if (c == CharPool.QUOTE) {
				index = getStringEndIndex(line, index + 1);

				continue;
			}

			if (c == CharPool.CLOSE_CURLY_BRACE) {
				if (level == 0) {
					return index + 1;
				}

				level--;
			}
			else if (c == CharPool.OPEN_CURLY_BRACE) {
				level++;
			}

			index++;
		}

		return index;
	}

	private static final Pattern _heredocPattern = Pattern.compile(
		"<<-?([A-Za-z_][\\w-]*)$");

}