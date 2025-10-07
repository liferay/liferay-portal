/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

/**
 * @author Alan Huang
 */
public class JavaLocalServiceImplUserIdUsageCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("LocalServiceImpl.java")) {
			return content;
		}

		_checkGetUserIdCall(fileName, content, "GuestOrUserUtil.getUserId()");
		_checkGetUserIdCall(
			fileName, content, "PrincipalThreadLocal.getUserId()");

		return content;
	}

	private void _checkGetUserIdCall(
		String fileName, String content, String methodName) {

		int x = -1;

		while (true) {
			x = content.indexOf(methodName, x + 1);

			if (x == -1) {
				return;
			}

			addMessage(
				fileName,
				"Do not use \"" + methodName +
					"\", use \"userId\" as parameter instead",
				getLineNumber(content, x));
		}
	}

}