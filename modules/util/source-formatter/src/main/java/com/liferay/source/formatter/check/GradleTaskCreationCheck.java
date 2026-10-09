/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.GradleFile;

/**
 * @author Peter Shin
 */
public class GradleTaskCreationCheck extends BaseGradleFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, GradleFile gradleFile,
		String content) {

		if (absolutePath.contains("/project-templates-")) {
			return content;
		}

		String[] lines = StringUtil.splitLines(content);

		for (String line : lines) {
			if (!line.matches("^task\\s+.*$") || !line.contains("{")) {
				continue;
			}

			addMessage(
				fileName,
				"The task should be declared in a separate line before the " +
					"closure",
				getLineNumber(content, content.indexOf(line)));
		}

		return content;
	}

}