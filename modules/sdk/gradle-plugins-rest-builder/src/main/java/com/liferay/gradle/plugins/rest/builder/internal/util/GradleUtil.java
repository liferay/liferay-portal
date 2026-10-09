/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.rest.builder.internal.util;

import java.util.Set;

import org.gradle.api.Project;

/**
 * @author Peter Shin
 */
public class GradleUtil extends com.liferay.gradle.util.GradleUtil {

	public static Project findProject(Project rootProject, String name) {
		for (Project project : rootProject.getAllprojects()) {
			if (!name.equals(project.getName())) {
				continue;
			}

			Set<Project> subprojects = project.getSubprojects();

			if (subprojects.isEmpty()) {
				return project;
			}
		}

		return null;
	}

}