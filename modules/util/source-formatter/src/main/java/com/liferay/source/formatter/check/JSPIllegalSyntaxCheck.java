/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

/**
 * @author Hugo Huijser
 */
public class JSPIllegalSyntaxCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkIllegalSyntax(
			fileName, content, "console.log(", "Do not use console.log");
		_checkIllegalSyntax(
			fileName, content, "debugger.", "Do not use debugger");

		if (!fileName.endsWith("test.jsp")) {
			_checkIllegalSyntax(
				fileName, content, "System.out.print",
				"Do not call \"System.out.print\"");
		}

		return content;
	}

	private void _checkIllegalSyntax(
		String fileName, String content, String syntax, String message) {

		int x = -1;

		while (true) {
			x = content.indexOf(syntax, x + 1);

			if (x == -1) {
				return;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(fileName, message, getLineNumber(content, x));
		}
	}

}