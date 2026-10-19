/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class PropertiesLongLinesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		Matcher matcher = _commentsPattern.matcher(content);

		while (matcher.find()) {
			String match = matcher.group();

			String prefix = matcher.group(1);

			String comment = _splitComment(
				StringUtil.removeSubstring(match, "\n" + prefix), prefix);

			if (!StringUtil.equals(match, comment)) {
				return StringUtil.replaceFirst(
					content, match, comment, matcher.start());
			}
		}

		return content;
	}

	private String _splitComment(String comment, String prefix) {
		if (comment.length() <= getMaxLineLength()) {
			return comment;
		}

		int x = prefix.length() + 1;

		if (comment.startsWith(prefix + " See http")) {
			x += 4;
		}

		x = comment.indexOf(CharPool.SPACE, x);

		if (x == -1) {
			return comment;
		}

		if (x <= getMaxLineLength()) {
			x = comment.lastIndexOf(CharPool.SPACE, getMaxLineLength());
		}

		return comment.substring(0, x) + "\n" +
			_splitComment(prefix + " " + comment.substring(x + 1), prefix);
	}

	private static final Pattern _commentsPattern = Pattern.compile(
		"^([ \t]*(?:#+|\\$h\\d+)) (?!Env: )\\S.*(\n\\1 (?!Env: )\\S.*)*",
		Pattern.MULTILINE);

}