/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.tools.ToolsUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaVirtualThreadsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkMethodCall(
			fileName, content, "Executors", "newVirtualThreadPerTaskExecutor");
		_checkMethodCall(fileName, content, "Thread", "ofVirtual");
		_checkMethodCall(fileName, content, "Thread", "startVirtualThread");

		return content;
	}

	private void _checkMethodCall(
		String fileName, String content, String className, String methodName) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"\\b", className, "\\.\\s*", methodName, "\\b\\("));

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			if (ToolsUtil.isInsideQuotes(content, matcher.start())) {
				continue;
			}

			addMessage(
				fileName,
				"Do not use virtual threads (e.g., Executors.newVirtualThread" +
					"PerTaskExecutor(), Thread.ofVirtual(), Thread.start" +
						"VirtualThread())",
				getLineNumber(content, matcher.start()));
		}
	}

}