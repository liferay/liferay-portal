/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class TestrayImporterTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testRecordAxisTestClassGroup() {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

		Mockito.doReturn(
			testrayRun
		).when(
			axisTestClassGroup
		).getTestrayRun();

		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		TestrayCaseResult buildTestrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);
		TestrayCaseResult topLevelTestrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		_recordAxisTestClassGroup(
			axisTestClassGroup, buildTestrayCaseResult, testrayContext,
			topLevelTestrayCaseResult);

		Mockito.verify(
			testrayContext
		).recordTestrayCaseResultFile(
			axisTestClassGroup, buildTestrayCaseResult
		);

		Mockito.verify(
			buildTestrayCaseResult
		).setParentTestrayCaseResult(
			topLevelTestrayCaseResult
		);

		Mockito.verify(
			buildTestrayCaseResult
		).setTestrayRun(
			testrayRun
		);

		Mockito.verify(
			testrayContext, Mockito.never()
		).updateTestrayCaseResultFileDurations(
			Mockito.any()
		);
	}

	@Test
	public void testRecordAxisTestClassGroupCached() {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			Mockito.mock(TestrayRun.class)
		).when(
			axisTestClassGroup
		).getTestrayRun();

		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Mockito.doReturn(
			true
		).when(
			testrayContext
		).hasTestrayCaseResultFile(
			axisTestClassGroup
		);

		TestrayCaseResult buildTestrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		_recordAxisTestClassGroup(
			axisTestClassGroup, buildTestrayCaseResult, testrayContext,
			Mockito.mock(TestrayCaseResult.class));

		Mockito.verify(
			buildTestrayCaseResult, Mockito.never()
		).cacheTestrayCaseResultURL();

		Mockito.verify(
			testrayContext, Mockito.never()
		).recordTestrayCaseResultFile(
			Mockito.any(), Mockito.any()
		);

		Mockito.verify(
			testrayContext
		).updateTestrayCaseResultFileDurations(
			axisTestClassGroup
		);
	}

	@Test
	public void testRecordAxisTestClassGroupNoTestrayRun() throws Exception {
		String axisName = RandomTestUtil.randomString();
		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Job job = Mockito.mock(Job.class);

		String message = _recordAxisTestClassGroupNoTestrayRun(
			axisName, job, testrayContext);

		Assert.assertTrue(message.contains(axisName));

		Mockito.verify(
			job
		).setTestrayContext(
			testrayContext
		);

		Job testrayContextJob = Mockito.mock(Job.class);

		Mockito.doReturn(
			Mockito.mock(TestrayContext.class)
		).when(
			testrayContextJob
		).getTestrayContext();

		message = _recordAxisTestClassGroupNoTestrayRun(
			axisName, testrayContextJob, testrayContext);

		Assert.assertTrue(message.contains(axisName));

		Mockito.verify(
			testrayContextJob, Mockito.never()
		).setTestrayContext(
			Mockito.any()
		);

		message = _recordAxisTestClassGroupNoTestrayRun(
			axisName, null, testrayContext);

		Assert.assertTrue(message.contains(axisName));
	}

	private void _recordAxisTestClassGroup(
		AxisTestClassGroup axisTestClassGroup,
		TestrayCaseResult buildTestrayCaseResult, TestrayContext testrayContext,
		TestrayCaseResult topLevelTestrayCaseResult) {

		TestrayImporter testrayImporter = Mockito.mock(TestrayImporter.class);

		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_testrayContext", testrayContext);
		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_topLevelBuildReport",
			Mockito.mock(TopLevelBuildReport.class));
		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_uncreatedTestrayCaseResultsCount",
			new AtomicInteger());

		try (MockedStatic<TestrayFactory> testrayFactoryMockedStatic =
				Mockito.mockStatic(TestrayFactory.class)) {

			testrayFactoryMockedStatic.when(
				() -> TestrayFactory.newBuildTestrayCaseResult(
					Mockito.any(AxisTestClassGroup.class), Mockito.any(),
					Mockito.any(TopLevelBuildReport.class))
			).thenReturn(
				buildTestrayCaseResult
			);

			ReflectionTestUtil.invoke(
				testrayImporter, "_recordAxisTestClassGroup",
				new Class<?>[] {
					AxisTestClassGroup.class, TestrayCaseResult.class
				},
				axisTestClassGroup, topLevelTestrayCaseResult);
		}
	}

	private String _recordAxisTestClassGroupNoTestrayRun(
		String axisName, Job job, TestrayContext testrayContext) {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			axisName
		).when(
			axisTestClassGroup
		).getAxisName();

		Mockito.doReturn(
			job
		).when(
			axisTestClassGroup
		).getJob();

		TestrayImporter testrayImporter = Mockito.mock(TestrayImporter.class);

		ReflectionTestUtil.setFieldValue(
			testrayImporter, "_testrayContext", testrayContext);

		try {
			ReflectionTestUtil.invoke(
				testrayImporter, "_recordAxisTestClassGroup",
				new Class<?>[] {
					AxisTestClassGroup.class, TestrayCaseResult.class
				},
				axisTestClassGroup, Mockito.mock(TestrayCaseResult.class));
		}
		catch (RuntimeException runtimeException) {
			Throwable invocationThrowable = runtimeException.getCause();

			Throwable throwable = invocationThrowable.getCause();

			return throwable.getMessage();
		}

		throw new AssertionError();
	}

}