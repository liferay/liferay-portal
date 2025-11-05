/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.ci.test.runner;

import org.gradle.api.Project;

/**
 * @author Calum Ragan
 */
public class CITestRunnerExtension {

	public CITestRunnerExtension(Project project) {
		_project = project;
	}

	private final Project _project;

}