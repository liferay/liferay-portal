/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPVarNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		Matcher matcher = _varNamePattern.matcher(content);

		while (matcher.find()) {
			String varName = matcher.group(1);

			String expectedVarName = null;

			if (varName.matches(".*[a-z]Url")) {
				expectedVarName = StringUtil.replaceLast(varName, "Url", "URL");
			}
			else if (varName.matches(".*[a-z]Html")) {
				expectedVarName = StringUtil.replaceLast(
					varName, "Html", "HTML");
			}

			if (expectedVarName == null) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Rename var \"", varName, "\" to \"", expectedVarName,
					"\""),
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

	private static final Pattern _varNamePattern = Pattern.compile(
		"\\svar=\"(\\w+)\"");

}