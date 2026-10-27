/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.TFSourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Huang
 */
public class TFIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String[] lines = content.split("\n", -1);

		boolean blockComment = false;
		String commentIndent = null;
		String newCommentIndent = null;
		List<Integer> openIndentLevels = new ArrayList<>();

		for (int i = 0; i < lines.length; i++) {
			String trimmedLine = StringUtil.trim(lines[i]);

			if (Validator.isBlank(trimmedLine)) {
				continue;
			}

			int expectedTabsCount = 0;

			if (!openIndentLevels.isEmpty()) {
				expectedTabsCount = openIndentLevels.get(
					openIndentLevels.size() - 1);

				char firstChar = trimmedLine.charAt(0);

				if ((firstChar != CharPool.CLOSE_BRACKET) &&
					(firstChar != CharPool.CLOSE_CURLY_BRACE) &&
					(firstChar != CharPool.CLOSE_PARENTHESIS)) {

					expectedTabsCount++;
				}
			}

			boolean previousBlockComment = blockComment;

			StringBundler sb = new StringBundler();

			blockComment = TFSourceUtil.appendBrackets(
				trimmedLine, blockComment, sb);

			if (!previousBlockComment && blockComment) {
				commentIndent = lines[i].substring(
					0, lines[i].indexOf(trimmedLine.charAt(0)));
				newCommentIndent = _getIndentedLine(
					StringPool.BLANK, expectedTabsCount);
			}

			if (!previousBlockComment) {
				lines[i] = _getIndentedLine(trimmedLine, expectedTabsCount);
			}
			else if (lines[i].startsWith(commentIndent)) {
				lines[i] =
					newCommentIndent +
						lines[i].substring(commentIndent.length());
			}
			else {
				lines[i] = newCommentIndent + StringUtil.trimLeading(lines[i]);
			}

			String brackets = sb.toString();

			for (char c : brackets.toCharArray()) {
				if ((c == CharPool.OPEN_BRACKET) ||
					(c == CharPool.OPEN_CURLY_BRACE) ||
					(c == CharPool.OPEN_PARENTHESIS)) {

					openIndentLevels.add(expectedTabsCount);
				}
				else if (openIndentLevels.isEmpty()) {
					return content;
				}
				else {
					openIndentLevels.remove(openIndentLevels.size() - 1);
				}
			}

			if (blockComment) {
				continue;
			}

			String heredocMarker = TFSourceUtil.getHeredocMarker(trimmedLine);

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

	private String _getIndentedLine(String trimmedLine, int expectedTabsCount) {
		StringBundler sb = new StringBundler(expectedTabsCount + 1);

		for (int i = 0; i < expectedTabsCount; i++) {
			sb.append(CharPool.TAB);
		}

		sb.append(trimmedLine);

		return sb.toString();
	}

}