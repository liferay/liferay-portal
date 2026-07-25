/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.ToolsUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaConnectionTransactionCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!content.contains("import java.sql.Connection;")) {

			return content;
		}

		for (String allowedFileName :
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY, absolutePath)) {

			if (absolutePath.endsWith(allowedFileName)) {
				return content;
			}
		}

		_checkMethodCall(fileName, content, "commit");
		_checkMethodCall(fileName, content, "rollback");
		_checkMethodCall(fileName, content, "setAutoCommit");

		return content;
	}

	private void _checkMethodCall(
		String fileName, String content, String methodName) {

		Pattern pattern = Pattern.compile("\\b(\\w+)\\." + methodName + "\\(");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start()) ||
				!Objects.equals(
					getVariableTypeName(
						content, null, content, fileName, matcher.group(1)),
					"Connection")) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Do not call \"", methodName,
					"\" on a database connection. Transaction boundaries are ",
					"managed by the container, and committing a borrowed ",
					"connection can flush the caller's transaction. See ",
					"LPD-98668."),
				getLineNumber(content, matcher.start()));
		}
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

}