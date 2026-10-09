/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPIncludeCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		int x = -1;

		while (true) {
			x = content.indexOf("<%@ include file", x + 1);

			if (x == -1) {
				break;
			}

			int y = content.indexOf(CharPool.NEW_LINE, x);

			if (y == -1) {
				y = content.length();
			}

			String line = content.substring(x, y);

			y = line.indexOf(CharPool.QUOTE);

			int z = line.indexOf(CharPool.QUOTE, y + 1);

			if (z == -1) {
				continue;
			}

			String includeFileName = line.substring(y + 1, z);

			if (includeFileName.startsWith("/")) {
				continue;
			}

			addMessage(
				fileName,
				"Include \"" + includeFileName + "\" should start with \"/\"",
				getLineNumber(content, x));
		}

		Matcher matcher = _includeFilePattern.matcher(content);

		while (matcher.find()) {
			content = StringUtil.replaceFirst(
				content, matcher.group(),
				"@ include file=\"" + matcher.group(1) + "\"", matcher.start());
		}

		return content;
	}

	private static final Pattern _includeFilePattern = Pattern.compile(
		"\\s*@\\s*include\\s*file=['\"](.*)['\"]");

}