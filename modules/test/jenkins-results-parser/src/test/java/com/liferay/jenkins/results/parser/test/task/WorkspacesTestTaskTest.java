/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.task;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.history.BatchHistory;
import com.liferay.jenkins.results.parser.history.TestTaskHistory;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesModulesJUnitBatchTestClassGroup;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesTestTaskTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetLongestDuration() {
		long defaultTestTaskDuration = _randomDuration();

		_testGetLongestDuration(
			_getBatchHistory(_getTestTaskHistory(false, 0)),
			defaultTestTaskDuration, defaultTestTaskDuration);

		long longestDuration = _randomDuration();

		_testGetLongestDuration(
			_getBatchHistory(_getTestTaskHistory(false, longestDuration)),
			defaultTestTaskDuration, longestDuration);

		_testGetLongestDuration(
			_getBatchHistory(null), defaultTestTaskDuration,
			defaultTestTaskDuration);
		_testGetLongestDuration(null, 0, 0);
		_testGetLongestDuration(
			null, defaultTestTaskDuration, defaultTestTaskDuration);
	}

	@Test
	public void testIsIsolated() {
		_testIsIsolated(
			_getBatchHistory(_getTestTaskHistory(false, _randomDuration())),
			false);
		_testIsIsolated(
			_getBatchHistory(_getTestTaskHistory(true, _randomDuration())),
			true);
		_testIsIsolated(_getBatchHistory(null), false);
		_testIsIsolated(null, false);
	}

	private BatchHistory _getBatchHistory(TestTaskHistory testTaskHistory) {
		BatchHistory batchHistory = Mockito.mock(BatchHistory.class);

		Mockito.doReturn(
			testTaskHistory
		).when(
			batchHistory
		).getTestTaskHistory(
			_NAME
		);

		return batchHistory;
	}

	private TestTaskHistory _getTestTaskHistory(
		boolean latestReportMissing, long longestDuration) {

		TestTaskHistory testTaskHistory = Mockito.mock(TestTaskHistory.class);

		Mockito.doReturn(
			longestDuration
		).when(
			testTaskHistory
		).getLongestDuration();

		Mockito.doReturn(
			latestReportMissing
		).when(
			testTaskHistory
		).isLatestReportMissing();

		return testTaskHistory;
	}

	private WorkspacesTestTask _getWorkspacesTestTask(
		BatchHistory batchHistory, long defaultTestTaskDuration) {

		WorkspacesModulesJUnitBatchTestClassGroup
			workspacesModulesJUnitBatchTestClassGroup = Mockito.mock(
				WorkspacesModulesJUnitBatchTestClassGroup.class);

		Mockito.doReturn(
			batchHistory
		).when(
			workspacesModulesJUnitBatchTestClassGroup
		).getBatchHistory();

		Mockito.doReturn(
			defaultTestTaskDuration
		).when(
			workspacesModulesJUnitBatchTestClassGroup
		).getDefaultTestTaskDuration();

		return new WorkspacesTestTask(
			workspacesModulesJUnitBatchTestClassGroup,
			TestClassGroup.GroupingStrategy.TEST_TASK_LONGEST_DURATION, _NAME);
	}

	private long _randomDuration() {
		return (RandomTestUtil.randomInt() & 0xFFFFFF) + 1L;
	}

	private void _testGetLongestDuration(
		BatchHistory batchHistory, long defaultTestTaskDuration,
		long expectedLongestDuration) {

		WorkspacesTestTask workspacesTestTask = _getWorkspacesTestTask(
			batchHistory, defaultTestTaskDuration);

		Assert.assertEquals(
			expectedLongestDuration, workspacesTestTask.getLongestDuration());
		Assert.assertEquals(
			expectedLongestDuration, workspacesTestTask.getWeight());

		JSONObject workspacesTestTaskJSONObject =
			workspacesTestTask.getJSONObject();

		Assert.assertEquals(
			expectedLongestDuration,
			workspacesTestTaskJSONObject.getLong("longest_duration"));
	}

	private void _testIsIsolated(
		BatchHistory batchHistory, boolean expectedIsolated) {

		WorkspacesTestTask workspacesTestTask = _getWorkspacesTestTask(
			batchHistory, _randomDuration());

		Assert.assertEquals(expectedIsolated, workspacesTestTask.isIsolated());
	}

	private static final String _NAME = RandomTestUtil.randomString();

}