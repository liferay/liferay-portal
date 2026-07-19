/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaConnectionTransactionCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		String content = javaTerm.getContent();

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!fileContent.contains("import java.sql.Connection;")) {

			return content;
		}

		for (String allowedFileName :
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY, absolutePath)) {

			if (absolutePath.endsWith(allowedFileName)) {
				return content;
			}
		}

		_checkConnectionTransactionMethodCall(
			content, fileContent, fileName, javaTerm, "commit");
		_checkConnectionTransactionMethodCall(
			content, fileContent, fileName, javaTerm, "rollback");
		_checkConnectionTransactionMethodCall(
			content, fileContent, fileName, javaTerm, "setAutoCommit");

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkConnectionTransactionMethodCall(
		String content, String fileContent, String fileName, JavaTerm javaTerm,
		String methodName) {

		Matcher matcher = Pattern.compile(
			"(\\w+)\\." + methodName + "\\("
		).matcher(
			content
		);

		while (matcher.find()) {
			int start = matcher.start();

			if (ToolsUtil.isInsideQuotes(content, start)) {
				continue;
			}

			String variableName = matcher.group(1);

			if (!Objects.equals(
					getVariableTypeName(
						content, javaTerm, fileContent, fileName, variableName),
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
				getLineNumber(content, start));
		}
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

}