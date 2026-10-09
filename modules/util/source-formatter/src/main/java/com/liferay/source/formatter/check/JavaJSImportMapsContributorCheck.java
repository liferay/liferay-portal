/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;

/**
 * @author Bryce Osterhaus
 */
public class JavaJSImportMapsContributorCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (!fileName.endsWith("JSImportMapsContributor.java")) {
			return content;
		}

		int x = -1;

		while (true) {
			x = content.indexOf(".put(", x + 1);

			if (x == -1) {
				break;
			}

			List<String> parameterNames = JavaSourceUtil.getParameterNames(
				JavaSourceUtil.getMethodCall(content, x));

			if (parameterNames.size() != 2) {
				continue;
			}

			String importName = parameterNames.get(0);
			String resourcePath = parameterNames.get(1);

			if (SourceUtil.isLiteralString(importName) &&
				!importName.endsWith("/api\"") &&
				!resourcePath.contains("__liferay__/exports")) {

				addMessage(
					fileName, "Import map name should end with \"/api\"",
					getLineNumber(content, x));
			}

			if (resourcePath.contains("__liferay__/api.js") ||
				resourcePath.contains("__liferay__/exports")) {

				continue;
			}

			addMessage(
				fileName,
				"Import map resource path should only contain " +
					"\"__liferay__/api.js\" or \"__liferay__/exports\"",
				getLineNumber(content, x));
		}

		return content;
	}

}