/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class CSSCommentsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _formatComments(content);
	}

	private String _formatComments(String content) {
		Matcher commentMatcher = _commentPattern.matcher(content);

		while (commentMatcher.find()) {
			Matcher commentFormatMatcher = _commentFormatPattern.matcher(
				commentMatcher.group(1));

			if (!commentFormatMatcher.find()) {
				continue;
			}

			String comment = commentFormatMatcher.group(1);

			String[] words = StringUtil.split(comment, CharPool.SPACE);

			for (int i = 1; i < words.length; i++) {
				String previousWord = words[i - 1];

				if (previousWord.endsWith(StringPool.PERIOD) ||
					previousWord.equals(StringPool.SLASH)) {

					continue;
				}

				String word = words[i];

				if ((word.length() <= 1) ||
					!Character.isUpperCase(word.charAt(0)) ||
					!StringUtil.isLowerCase(word.substring(1))) {

					continue;
				}

				comment = StringUtil.replaceFirst(
					comment, word, StringUtil.toLowerCase(word));
			}

			content = StringUtil.replaceFirst(
				content, commentMatcher.group(),
				"/* ---------- " + comment + " ---------- */");
		}

		return content;
	}

	private static final Pattern _commentFormatPattern = Pattern.compile(
		"^-* ?(\\S[ \\w]*?\\S) ?-*$");
	private static final Pattern _commentPattern = Pattern.compile(
		"/\\*[\n ](.*)[\n ]\\*/");

}