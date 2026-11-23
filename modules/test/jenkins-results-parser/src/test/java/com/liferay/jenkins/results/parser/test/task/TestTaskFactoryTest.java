/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.task;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.history.BatchHistory;
import com.liferay.jenkins.results.parser.history.TestTaskHistory;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesModulesJUnitBatchTestClassGroup;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestTaskFactoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testNewTestTask() throws Exception {
		List<Class<? extends BatchTestClassGroup>> batchTestClassGroupClasses =
			_getBatchTestClassGroupClasses();

		Assert.assertTrue(
			batchTestClassGroupClasses.contains(
				WorkspacesModulesJUnitBatchTestClassGroup.class));

		for (Class<? extends BatchTestClassGroup> batchTestClassGroupClass :
				batchTestClassGroupClasses) {

			Class<? extends TestTask> testTaskClass = DefaultTestTask.class;

			if (WorkspacesModulesJUnitBatchTestClassGroup.class.
					isAssignableFrom(batchTestClassGroupClass)) {

				testTaskClass = WorkspacesTestTask.class;
			}

			_testNewTestTask(batchTestClassGroupClass, testTaskClass);
		}
	}

	private List<Class<? extends BatchTestClassGroup>>
			_getBatchTestClassGroupClasses()
		throws Exception {

		List<Class<? extends BatchTestClassGroup>> batchTestClassGroupClasses =
			new ArrayList<>();

		Package packageObject = BatchTestClassGroup.class.getPackage();

		String packageName = packageObject.getName();

		File packageDir = new File(
			"src/main/java/" + packageName.replace('.', '/'));

		for (File file : packageDir.listFiles()) {
			String fileName = file.getName();

			if (!fileName.endsWith(".java")) {
				continue;
			}

			Class<?> clazz = Class.forName(
				packageName + "." +
					fileName.substring(0, fileName.length() - 5));

			if (!BatchTestClassGroup.class.isAssignableFrom(clazz)) {
				continue;
			}

			batchTestClassGroupClasses.add(
				clazz.asSubclass(BatchTestClassGroup.class));
		}

		return batchTestClassGroupClasses;
	}

	private void _testNewTestTask(
		Class<? extends BatchTestClassGroup> batchTestClassGroupClass,
		Class<? extends TestTask> expectedTestTaskClass) {

		long averageDuration = (RandomTestUtil.randomInt() & 0xFFFFFF) + 1L;

		long longestDuration = averageDuration + 1;

		TestTaskHistory testTaskHistory = Mockito.mock(TestTaskHistory.class);

		Mockito.doReturn(
			averageDuration
		).when(
			testTaskHistory
		).getAverageDuration();

		Mockito.doReturn(
			longestDuration
		).when(
			testTaskHistory
		).getLongestDuration();

		String testTaskName = JenkinsResultsParserUtil.combine(
			"workspaces/", RandomTestUtil.randomString(), ":modules:",
			RandomTestUtil.randomString(), ":test");

		BatchHistory batchHistory = Mockito.mock(BatchHistory.class);

		Mockito.doReturn(
			testTaskHistory
		).when(
			batchHistory
		).getTestTaskHistory(
			testTaskName
		);

		BatchTestClassGroup batchTestClassGroup = Mockito.mock(
			batchTestClassGroupClass);

		Mockito.doReturn(
			batchHistory
		).when(
			batchTestClassGroup
		).getBatchHistory();

		TestTask testTask = TestTaskFactory.newTestTask(
			batchTestClassGroup,
			TestClassGroup.GroupingStrategy.TEST_TASK_AVERAGE_DURATION,
			testTaskName);

		Assert.assertSame(batchHistory, testTask.getBatchHistory());
		Assert.assertSame(expectedTestTaskClass, testTask.getClass());
		Assert.assertEquals(testTaskName, testTask.getName());
		Assert.assertEquals(averageDuration, testTask.getWeight());

		TestTask longestDurationTestTask = TestTaskFactory.newTestTask(
			batchTestClassGroup,
			TestClassGroup.GroupingStrategy.TEST_TASK_LONGEST_DURATION,
			testTaskName);

		Assert.assertEquals(
			longestDuration, longestDurationTestTask.getWeight());
		Assert.assertEquals(testTask, longestDurationTestTask);
		Assert.assertEquals(
			testTask.hashCode(), longestDurationTestTask.hashCode());
	}

}