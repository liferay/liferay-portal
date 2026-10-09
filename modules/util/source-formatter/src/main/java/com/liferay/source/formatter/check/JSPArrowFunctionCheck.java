/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

/**
 * @author Hugo Huijser
 */
public class JSPArrowFunctionCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		int x = -1;

		while (true) {
			x = content.indexOf("=>", x + 1);

			if (x == -1) {
				return content;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(
				fileName, "Do not use arrow function",
				getLineNumber(content, x));
		}
	}

}