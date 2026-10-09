/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaModuleClassGetResourceCallCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/modules/sdk/")) {
			return content;
		}

		String className = JavaSourceUtil.getClassName(fileName);

		Pattern pattern = Pattern.compile(
			className + "\\.class\\.getResource(AsStream)?\\(");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String getResourceMethodCall = JavaSourceUtil.getMethodCall(
				content, matcher.start());

			List<String> parameterList = JavaSourceUtil.getParameterList(
				getResourceMethodCall);

			if (parameterList.isEmpty()) {
				return content;
			}

			String parameter = parameterList.get(0);

			if (!parameter.startsWith(StringPool.QUOTE)) {
				continue;
			}

			String literalString = null;
			int x = 0;

			while (true) {
				x = parameter.indexOf(CharPool.QUOTE, x + 1);

				if (ToolsUtil.isInsideQuotes(parameter, x)) {
					continue;
				}

				literalString = parameter.substring(1, x);

				break;
			}

			if ((literalString.length() <= 1) ||
				literalString.equals("dependencies") ||
				literalString.startsWith("dependencies/")) {

				continue;
			}

			addMessage(
				fileName,
				"Resource files should be in \"dependencies\" directory",
				getLineNumber(content, matcher.start()));
		}

		return content;
	}

}