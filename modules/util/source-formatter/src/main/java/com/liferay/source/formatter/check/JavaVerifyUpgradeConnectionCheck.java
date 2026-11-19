/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.check.util.JavaSourceUtil;

/**
 * @author Hugo Huijser
 */
public class JavaVerifyUpgradeConnectionCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (absolutePath.contains("/test/") ||
			fileName.endsWith("DBUpgrader.java") ||
			fileName.endsWith("UpgradeRecorder.java") ||
			fileName.endsWith("Test.java") ||
			fileName.endsWith("UpgradeExecutor.java") ||
			content.contains("Callable<Void>")) {

			return content;
		}

		String className = JavaSourceUtil.getClassName(fileName);

		if (className.contains("Upgrade") || className.contains("Verify")) {
			_checkConnectionField(fileName, content, "getConnection");
			_checkConnectionField(
				fileName, content, "getUpgradeOptimizedConnection");
		}

		return content;
	}

	private void _checkConnectionField(
		String fileName, String content, String methodName) {

		int x = -1;

		while (true) {
			x = content.indexOf("DataAccess." + methodName, x + 1);

			if (x == -1) {
				break;
			}

			addMessage(
				fileName,
				"Use existing connection field instead of DataAccess." +
					methodName,
				getLineNumber(content, x));
		}
	}

}