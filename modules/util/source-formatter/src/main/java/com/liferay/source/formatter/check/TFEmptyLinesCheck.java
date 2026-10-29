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

/**
 * @author Alan Huang
 */
public class TFEmptyLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String[] lines = content.split("\n", -1);

		StringBundler sb = new StringBundler(lines.length * 3);

		boolean emptyLine = false;
		boolean previousComment = false;
		String previousLine = null;

		for (int i = 0; i < lines.length; i++) {
			if (Validator.isBlank(StringUtil.trim(lines[i]))) {
				emptyLine = true;

				continue;
			}

			boolean comment = TFSourceUtil.isCommentLine(lines[i]);

			int endLineIndex = i;

			if (comment) {
				endLineIndex = TFSourceUtil.getCommentEndLineIndex(lines, i);
			}
			else {
				String heredocMarker = TFSourceUtil.getHeredocMarker(lines[i]);

				if (heredocMarker != null) {
					endLineIndex = TFSourceUtil.getHeredocEndLineIndex(
						lines, i, heredocMarker);
				}
			}

			if (endLineIndex == -1) {
				return content;
			}

			if ((previousLine != null) &&
				((comment != previousComment) ||
				 (emptyLine && !_isBlockStart(previousLine) &&
				  !_isBlockEnd(lines[i])))) {

				sb.append(CharPool.NEW_LINE);
			}

			for (int j = i; j <= endLineIndex; j++) {
				sb.append(lines[j]);
				sb.append(CharPool.NEW_LINE);
			}

			emptyLine = false;
			i = endLineIndex;
			previousComment = comment;
			previousLine = lines[endLineIndex];
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private boolean _isBlockEnd(String line) {
		String trimmedLine = StringUtil.trim(line);

		if (trimmedLine.startsWith(StringPool.CLOSE_BRACKET) ||
			trimmedLine.startsWith(StringPool.CLOSE_CURLY_BRACE) ||
			trimmedLine.startsWith(StringPool.CLOSE_PARENTHESIS)) {

			return true;
		}

		return false;
	}

	private boolean _isBlockStart(String line) {
		String trimmedLine = StringUtil.trim(TFSourceUtil.removeComment(line));

		if (trimmedLine.endsWith(StringPool.OPEN_BRACKET) ||
			trimmedLine.endsWith(StringPool.OPEN_CURLY_BRACE) ||
			trimmedLine.endsWith(StringPool.OPEN_PARENTHESIS)) {

			return true;
		}

		return false;
	}

}