/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.task;

import com.liferay.jenkins.results.parser.history.TestTaskHistory;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroup;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesTestTask extends BaseTestTask {

	@Override
	public long getLongestDuration() {
		long longestDuration = super.getLongestDuration();

		if (longestDuration > 0) {
			return longestDuration;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		return batchTestClassGroup.getDefaultTestTaskDuration();
	}

	@Override
	public boolean isIsolated() {
		TestTaskHistory testTaskHistory = getTestTaskHistory();

		if (testTaskHistory == null) {
			return false;
		}

		return testTaskHistory.isLatestReportMissing();
	}

	protected WorkspacesTestTask(
		BatchTestClassGroup batchTestClassGroup,
		TestClassGroup.GroupingStrategy groupingStrategy, String name) {

		super(batchTestClassGroup, groupingStrategy, name);
	}

}