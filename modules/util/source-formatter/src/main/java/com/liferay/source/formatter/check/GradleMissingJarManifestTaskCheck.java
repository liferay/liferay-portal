/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.parser.GradleFile;

import java.util.Set;

/**
 * @author Alan Huang
 */
public class GradleMissingJarManifestTaskCheck extends BaseGradleFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, GradleFile gradleFile,
		String content) {

		Set<String> tasks = gradleFile.getTasks();

		if (tasks.contains("task jarManifest")) {
			return content;
		}

		for (String task : tasks) {
			if (!task.startsWith("task jarPatched(")) {
				continue;
			}

			addMessage(fileName, "Missing \"jarManifest\" task");
		}

		return content;
	}

}