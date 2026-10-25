/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.ToolsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaTypedPropertyKeyCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		Matcher matcher = _bndImportPattern.matcher(content);

		if (matcher.find()) {
			return content;
		}

		matcher = _typedPropertyKeyPattern.matcher(content);

		while (matcher.find()) {
			if (matcher.group(1) != null) {
				if (ToolsUtil.isInsideQuotes(content, matcher.start() - 1)) {
					continue;
				}

				addMessage(
					fileName,
					_getMessage(matcher.group(1), matcher.group(2)) +
						" with a typed value",
					getLineNumber(content, matcher.start()));

				continue;
			}

			if (!ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			addMessage(
				fileName,
				_getMessage(matcher.group(3), matcher.group(4)) +
					" in a filter",
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

	private String _getMessage(String typedKey, String key) {
		return StringBundler.concat(
			"The typed key syntax \"", typedKey,
			"\" is only parsed in component property strings, use the plain ",
			"key \"", key, "\"");
	}

	private static final String _TYPES =
		"(?:Boolean|Byte|Character|Double|Float|Integer|Long|Short|String)";

	private static final Pattern _bndImportPattern = Pattern.compile(
		"^import aQute\\.bnd\\.", Pattern.MULTILINE);
	private static final Pattern _typedPropertyKeyPattern = Pattern.compile(
		StringBundler.concat(
			"\"(([\\w.-]+):", _TYPES, ")\"|\\((([\\w.-]+):", _TYPES,
			")(?=[=~<>])"));

}