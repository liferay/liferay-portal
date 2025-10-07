/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

/**
 * @author Alan Huang
 */
public class JavaDefaultAdminScreenNameCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			absolutePath.endsWith(
				"/portal-impl/src/com/liferay/portal/service/impl" +
					"/CompanyLocalServiceImpl.java") ||
			absolutePath.endsWith(
				"/portal-kernel/src/com/liferay/portal/kernel/util" +
					"/PropsValues.java")) {

			return content;
		}

		_checkDefaultAdminScreenName(
			fileName, content, "PropsKeys.DEFAULT_ADMIN_SCREEN_NAME");
		_checkDefaultAdminScreenName(
			fileName, content, "PropsValues.DEFAULT_ADMIN_SCREEN_NAME");

		return content;
	}

	private void _checkDefaultAdminScreenName(
		String fileName, String content, String name) {

		int x = -1;

		while (true) {
			x = content.indexOf(name, x + 1);

			if (x == -1) {
				return;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(
				fileName, "Do not use \"" + name + "\", see LPD-59150",
				getLineNumber(content, x));
		}
	}

}