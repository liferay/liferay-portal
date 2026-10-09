/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaLongLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (absolutePath.endsWith("Table.java") &&
			!absolutePath.endsWith("/Table.java")) {

			return content;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				if (isExcludedPath(
						_LINE_LENGTH_EXCLUDES, absolutePath, lineNumber) ||
					line.matches("\\s*\\*.*") ||
					(getLineLength(line) <= getMaxLineLength())) {

					continue;
				}

				String trimmedLine = StringUtil.trimLeading(line);

				if (_isAnnotationParameter(content, trimmedLine) ||
					isInsideTextBlock(
						content, getLineStartPos(content, lineNumber))) {

					continue;
				}

				if (!line.contains(StringPool.QUOTE) &&
					!trimmedLine.startsWith("//")) {

					continue;
				}

				addMessage(fileName, "> " + getMaxLineLength(), lineNumber);
			}
		}

		return content;
	}

	private boolean _isAnnotationParameter(String content, String line) {
		int x = -1;
		int y = -1;
		int z = -1;

		while (true) {
			x = line.indexOf(StringPool.COMMA_AND_SPACE, x + 1);

			if (x == -1) {
				break;
			}

			y = line.indexOf(StringPool.OPEN_PARENTHESIS);
			z = line.lastIndexOf(StringPool.CLOSE_PARENTHESIS);

			if ((y != -1) && (z != -1) && (x > y) && (z > x)) {
				break;
			}

			if (!ToolsUtil.isInsideQuotes(line, x)) {
				return false;
			}
		}

		Matcher matcher = _annotationPattern.matcher(content);

		while (matcher.find()) {
			x = matcher.end();

			while (true) {
				x = content.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

				if (x == -1) {
					break;
				}

				String annotationParameters = content.substring(
					matcher.end() - 2, x + 1);

				if (getLevel(annotationParameters) == 0) {
					if (annotationParameters.contains(line)) {
						return true;
					}

					break;
				}
			}
		}

		return false;
	}

	private static final String _LINE_LENGTH_EXCLUDES = "line.length.excludes";

	private static final Pattern _annotationPattern = Pattern.compile(
		"\n\t*@(.+)\\(\n");

}