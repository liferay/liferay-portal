/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class LogParametersCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _formatLogParameters(content, fileName);
	}

	private String _formatLogParameters(String content, String fileName) {
		Matcher matcher = _logPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start()) ||
				!isJavaSource(content, matcher.start())) {

				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				matcher.group());

			if (parameterList.isEmpty()) {
				continue;
			}

			String firstParameter = StringUtil.trim(parameterList.get(0));

			if (firstParameter.matches("\\w*[eE]xception\\.getMessage\\(\\)")) {
				String replacement = firstParameter.substring(
					0, firstParameter.indexOf("."));

				return StringUtil.replaceFirst(
					content, firstParameter, replacement, matcher.start(2));
			}

			if (!Validator.isVariableName(firstParameter)) {
				continue;
			}

			String variableTypeName = getVariableTypeName(
				content, null, content, fileName, firstParameter);

			if (variableTypeName == null) {
				continue;
			}

			if (variableTypeName.equals("StringBundler")) {
				return StringUtil.replaceFirst(
					content, firstParameter, firstParameter + ".toString()",
					matcher.start(2));
			}

			if ((parameterList.size() != 2) ||
				!variableTypeName.endsWith("Exception")) {

				continue;
			}

			String secondParameter = StringUtil.trim(parameterList.get(1));

			if (firstParameter.equals(secondParameter)) {
				return StringUtil.replaceFirst(
					content, firstParameter + StringPool.COMMA,
					StringPool.BLANK, matcher.start(2));
			}
		}

		return content;
	}

	private static final Pattern _logPattern = Pattern.compile(
		"_log\\.(debug|error|fatal|info|trace|warn)\\((.+?)\\);\n",
		Pattern.DOTALL);

}