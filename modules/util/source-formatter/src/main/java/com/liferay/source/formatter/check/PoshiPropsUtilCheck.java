/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiPropsUtilCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if ((!fileName.endsWith(".macro") && !fileName.endsWith(".testcase")) ||
			SourceUtil.isXML(content)) {

			return content;
		}

		File file = new File(getPortalDir(), "test.properties");

		if (!file.exists()) {
			return content;
		}

		Properties properties = new Properties();

		properties.load(new FileInputStream(file));

		return _checkInlinePassword(fileName, content, properties);
	}

	private String _checkInlinePassword(
		String fileName, String content, Properties properties) {

		Matcher matcher = _propsUtilGetPasswordPattern.matcher(content);

		while (matcher.find()) {
			String previousCodeBlock = content.substring(0, matcher.start());

			if (!previousCodeBlock.contains("\tmacro ") &&
				!previousCodeBlock.contains("\ttest ")) {

				continue;
			}

			int x = previousCodeBlock.lastIndexOf("if (");

			if (x != -1) {
				String s = previousCodeBlock.substring(x);

				if (!s.contains("\tmacro ") && !s.contains("\ttest ") &&
					(getLevel(s, "{", "}") == 1)) {

					continue;
				}
			}

			String followingCode = _getFollowingCode(content, matcher.end());
			String variableName = matcher.group(1);

			if (!_isUnnecessaryAssign(followingCode, variableName)) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Pass \"", matcher.group(2),
					"\" directly instead of assigning value to variable \"",
					variableName, "\""),
				getLineNumber(content, matcher.start()) + 1);
		}

		String password = properties.getProperty(
			"test.portal.default.admin.password");

		if (Validator.isNull(password)) {
			return content;
		}

		StringBuffer sb = new StringBuffer();

		Pattern pattern = Pattern.compile(
			"(\\w*[Pp]assword = )\"" + password + "\"");

		matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (_isInsideTripleQuotes(content, matcher.start())) {
				continue;
			}

			matcher.appendReplacement(
				sb,
				matcher.group(1) + "PropsUtil.get(\"default.admin.password\")");
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private String _getFollowingCode(String content, int end) {
		int x = content.indexOf("\n\t}", end);

		return content.substring(end, x);
	}

	private boolean _isInsideTripleQuotes(String content, int pos) {
		String s = content.substring(pos);

		int x = s.indexOf("''';");

		if (x == -1) {
			return false;
		}

		s = s.substring(0, x);

		if (!s.contains("= '''")) {
			return true;
		}

		return false;
	}

	private boolean _isUnnecessaryAssign(String content, String variableName) {
		int x = -1;

		while (true) {
			x = content.indexOf("${" + variableName + "}", x + 1);

			if (x == -1) {
				return true;
			}

			if (_isInsideTripleQuotes(content, x)) {
				return false;
			}

			String line = getLine(content, getLineNumber(content, x));

			if (line.contains("curl") || line.contains("if (")) {
				return false;
			}
		}
	}

	private static final Pattern _propsUtilGetPasswordPattern = Pattern.compile(
		"\n\t+var (\\w+) = (PropsUtil.get\\(\"default.admin.password\"\\));");

}