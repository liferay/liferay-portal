/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

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

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _arrayPattern.matcher(content);

		while (matcher.find()) {
			String s = matcher.group(2);

			s = s.replaceAll("\\s+\\\\\\s+", StringPool.BLANK);

			if (s.endsWith(StringPool.COMMA)) {
				s = s.substring(0, s.length() - 1);
			}

			int x = -1;

			while (x < (s.length() - 1)) {
				x = s.indexOf(StringPool.COMMA, x + 1);

				if (x == -1) {
					break;
				}

				if (ToolsUtil.isInsideQuotes(s, x)) {
					continue;
				}

				char c = s.charAt(x + 1);

				if (c == CharPool.SPACE) {
					continue;
				}

				s = StringUtil.insert(s, StringPool.SPACE, x + 1);
			}

			matcher.appendReplacement(
				sb,
				matcher.group(1) + Matcher.quoteReplacement(s.trim()) +
					matcher.group(3));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final Pattern _arrayPattern = Pattern.compile(
		"^([\\w.-]+=\\[)(.+?)(\\])$", Pattern.DOTALL | Pattern.MULTILINE);

}