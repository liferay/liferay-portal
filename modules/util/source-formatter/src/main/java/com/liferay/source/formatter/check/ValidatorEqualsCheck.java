/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class ValidatorEqualsCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkValidatorEquals(fileName, content);

		return content;
	}

	private void _checkValidatorEquals(String fileName, String content) {
		Matcher matcher = _validatorEqualsPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			addMessage(
				fileName,
				"Use Objects.equals(Object, Object) instead of " +
					"Validator.equals(Object, Object)",
				getLineNumber(content, matcher.start()));
		}
	}

	private static final Pattern _validatorEqualsPattern = Pattern.compile(
		"\\WValidator\\.equals\\(");

}