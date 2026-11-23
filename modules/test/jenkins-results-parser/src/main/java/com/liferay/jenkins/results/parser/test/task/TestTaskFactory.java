/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.task;

import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesModulesJUnitBatchTestClassGroup;

/**
 * @author Michael Hashimoto
 */
public class TestTaskFactory {

	public static TestTask newTestTask(
		BatchTestClassGroup batchTestClassGroup,
		TestClassGroup.GroupingStrategy groupingStrategy, String name) {

		if (batchTestClassGroup instanceof
				WorkspacesModulesJUnitBatchTestClassGroup) {

			return new WorkspacesTestTask(
				batchTestClassGroup, groupingStrategy, name);
		}

		return new DefaultTestTask(batchTestClassGroup, groupingStrategy, name);
	}

}