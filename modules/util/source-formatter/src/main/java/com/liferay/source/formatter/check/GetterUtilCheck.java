/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class GetterUtilCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws ReflectiveOperationException {

		_checkDefaultValues(fileName, content, _getterUtilGetPattern, 2);
		_checkDefaultValues(fileName, content, _paramUtilGetPattern, 3);

		return content;
	}

	private void _checkDefaultValues(
			String fileName, String content, Pattern pattern,
			int parameterCount)
		throws ReflectiveOperationException {

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start() + 1)) {
				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start() + 1));

			if (parameterList.size() != parameterCount) {
				continue;
			}

			String defaultVariableName =
				"DEFAULT_" + StringUtil.toUpperCase(matcher.group(1));

			Field defaultValuefield = GetterUtil.class.getDeclaredField(
				defaultVariableName);

			String defaultValue = String.valueOf(defaultValuefield.get(null));

			defaultValue = defaultValue.replaceFirst("\\.0", StringPool.BLANK);

			String value = parameterList.get(parameterCount - 1);

			String formattedValue = value.replaceFirst(
				"0(\\.0)?[dDfFlL]?", "0");

			if (formattedValue.equals("StringPool.BLANK")) {
				formattedValue = StringPool.BLANK;
			}

			if (!Objects.equals(formattedValue, defaultValue)) {
				continue;
			}

			addMessage(
				fileName, "No need to pass default value \"" + value + "\"",
				getLineNumber(content, matcher.start()));
		}
	}

	private static final Pattern _getterUtilGetPattern = Pattern.compile(
		"\\WGetterUtil\\.get(Boolean|Double|Float|Integer|Long|Number|Object|" +
			"Short|String)\\(",
		Pattern.DOTALL);
	private static final Pattern _paramUtilGetPattern = Pattern.compile(
		"\\WParamUtil\\.get(Boolean|Double|Float|Integer|Long|Number|Short|" +
			"String)\\(",
		Pattern.DOTALL);

}