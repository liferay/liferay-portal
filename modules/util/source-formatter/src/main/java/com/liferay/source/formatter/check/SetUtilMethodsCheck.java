/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class SetUtilMethodsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _checkFromArrayCalls(content);
	}

	private String _checkFromArrayCalls(String content) {
		Matcher matcher = _fromArrayPattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start()));

			if (parameterList.size() != 1) {
				continue;
			}

			String parameter = parameterList.get(0);

			String arrayParameters = parameter.replaceFirst(
				".+\\{([\\s\\S]+)\\}", "$1");

			return _fixFromArrayParameters(
				content, arrayParameters, matcher.start());
		}

		return content;
	}

	private String _fixFromArrayParameters(
		String content, String arrayParameters, int pos) {

		int x = pos;

		while (true) {
			x = content.indexOf(StringPool.CLOSE_PARENTHESIS, x + 1);

			String call = content.substring(pos, x + 1);

			if ((ToolsUtil.getLevel(call, "(", ")") == 0) &&
				(ToolsUtil.getLevel(call, "{", "}") == 0)) {

				String replacement = StringBundler.concat(
					"SetUtil.fromArray(", arrayParameters, ")");

				return StringUtil.replaceFirst(content, call, replacement, pos);
			}
		}
	}

	private static final Pattern _fromArrayPattern = Pattern.compile(
		"SetUtil\\.fromArray\\(\\s*(?=new )");

}