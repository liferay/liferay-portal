/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.tools.ToolsUtil;

/**
 * @author Alan Huang
 */
public class JavaVirtualThreadsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkMethodCall(
			fileName, content, "Executors.newVirtualThreadPerTaskExecutor");
		_checkMethodCall(fileName, content, "Thread.ofVirtual");
		_checkMethodCall(fileName, content, "Thread.startVirtualThread");

		return content;
	}

	private void _checkMethodCall(
		String fileName, String content, String methodCall) {

		int x = -1;

		while (true) {
			x = content.indexOf(methodCall + "(", x + 1);

			if (x == -1) {
				return;
			}

			if (ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			addMessage(
				fileName,
				"Do not use virtual threads (e.g., Executors.newVirtualThread" +
					"PerTaskExecutor(), Thread.ofVirtual(), Thread.start" +
						"VirtualThread())",
				getLineNumber(content, x));
		}
	}

}