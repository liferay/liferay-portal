/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaLogLevelCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.contains("Log")) {
			_checkLogLevel(content, fileName);
		}

		return content;
	}

	private void _checkLogLevel(String content, String fileName) {
		Matcher matcher = _logLevelPattern.matcher(content);

		while (matcher.find()) {
			int pos = matcher.start();

			while (true) {
				pos = content.lastIndexOf(
					StringPool.NEW_LINE + StringPool.TAB, pos - 1);

				char c = content.charAt(pos + 2);

				if (c != CharPool.TAB) {
					break;
				}
			}

			String codeBlock = content.substring(pos, matcher.start());
			String s =
				"_log.is" + StringUtil.upperCaseFirstLetter(matcher.group(2)) +
					"Enabled()";

			if (codeBlock.contains(s) ^ s.equals("_log.isErrorEnabled()")) {
				continue;
			}

			int lineNumber = getLineNumber(content, matcher.start(1));

			if (codeBlock.contains(s)) {
				addMessage(
					fileName, "Do not use _log.isErrorEnabled()", lineNumber);
			}
			else {
				addMessage(fileName, "Use " + s, lineNumber);
			}
		}
	}

	private static final Pattern _logLevelPattern = Pattern.compile(
		"\n(\t+)_log.(debug|error|info|trace|warn)\\(");

}