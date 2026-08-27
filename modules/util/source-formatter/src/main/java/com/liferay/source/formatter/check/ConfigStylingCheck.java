/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class ConfigStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		int index = fileName.lastIndexOf(StringPool.SLASH);

		String shortFileName = fileName.substring(index + 1);

		if (!shortFileName.endsWith(".config") ||
			!shortFileName.startsWith("com.liferay.")) {

			return content;
		}

		Matcher matcher = _arrayPattern.matcher(content);

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String s = matcher.group(2);

			if (!s.contains(StringPool.NEW_LINE)) {
				continue;
			}

			s = s.replaceAll("\\s+\\\\\\s+", StringPool.BLANK);

			if (s.endsWith(StringPool.COMMA)) {
				s = s.substring(0, s.length() - 1);
			}

			String replacement = matcher.group(1) + s + matcher.group(3);

			matcher.appendReplacement(
				sb, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final Pattern _arrayPattern = Pattern.compile(
		"^([\\w.-]+=\\[)(.+?)(\\])$", Pattern.DOTALL | Pattern.MULTILINE);

}