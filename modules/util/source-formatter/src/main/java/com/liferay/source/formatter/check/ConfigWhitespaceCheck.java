/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class ConfigWhitespaceCheck extends WhitespaceCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		Matcher matcher = _incorrectWhitesapcePattern.matcher(content);

		if (matcher.find()) {
			return StringUtil.insert(content, StringPool.SPACE, matcher.end());
		}

		return super.doProcess(fileName, absolutePath, content);
	}

	private static final Pattern _incorrectWhitesapcePattern = Pattern.compile(
		"\\\\\":(?=\\\\)");

}