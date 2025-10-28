/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterArgs;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nícolas Moura
 */
public class UpgradeCatchAllJavaLongLinesCheck extends JavaLongLinesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.contains("/upgrade/upgrade-catch-all-check") ||
			!fileName.contains("-before")) {

			return content;
		}

		Matcher matcher = _pattern.matcher(fileName);

		if (!matcher.find()) {
			return content;
		}

		setMaxLineLength(SourceFormatterArgs.MAX_LINE_LENGTH);

		return super.doProcess(
			StringUtil.removeLast(fileName, "-before"), absolutePath, content);
	}

	private static final Pattern _pattern = Pattern.compile(
		"(LPD|LPS)_[0-9]+\\.java");

}