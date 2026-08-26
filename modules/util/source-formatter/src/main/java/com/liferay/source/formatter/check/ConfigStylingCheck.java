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

		int pos = fileName.lastIndexOf(StringPool.SLASH);

		String shortFileName = fileName.substring(pos + 1);

		if (!shortFileName.endsWith(".config") ||
			!shortFileName.startsWith("com.liferay.")) {

			return content;
		}

		Matcher matcher = _arrayPattern.matcher(content);

		while (matcher.find()) {
			String s = matcher.group();
			int a = 0;
		}

		return content;
	}

	private static final Pattern _arrayPattern = Pattern.compile(
		"^[\\w.-]+=\\[.+\\]$", Pattern.DOTALL | Pattern.MULTILINE);

}