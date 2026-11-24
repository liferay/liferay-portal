/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.testray.TestrayContext;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class DefaultTopLevelBuildReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestrayAttachmentURLsDuringReset() throws Exception {
		TopLevelBuild topLevelBuild = _getTopLevelBuild();

		DefaultTopLevelBuildReport defaultTopLevelBuildReport =
			new DefaultTopLevelBuildReport(topLevelBuild);

		BaseDownstreamBuild baseDownstreamBuild = _getBaseDownstreamBuild(
			defaultTopLevelBuildReport);

		Mockito.doReturn(
			Collections.<Build>singletonList(baseDownstreamBuild)
		).when(
			topLevelBuild
		).getDownstreamBuilds();

		BuildUpdater buildUpdater = Mockito.mock(BuildUpdater.class);

		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_buildUpdater", buildUpdater);

		CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
		long timeoutSeconds = 10;

		Mockito.doAnswer(
			invocation -> {
				cyclicBarrier.await(timeoutSeconds, TimeUnit.SECONDS);

				baseDownstreamBuild.reset();

				return null;
			}
		).when(
			buildUpdater
		).update();

		Mockito.doAnswer(
			invocation -> {
				cyclicBarrier.await(timeoutSeconds, TimeUnit.SECONDS);

				return new JSONObject();
			}
		).when(
			topLevelBuild
		).getBuildReportJSONObject();

		AtomicReference<Throwable> throwableAtomicReference =
			new AtomicReference<>();

		Thread thread0 = _newThread(
			defaultTopLevelBuildReport::getTestrayAttachmentURLs,
			throwableAtomicReference);
		Thread thread1 = _newThread(
			baseDownstreamBuild::update, throwableAtomicReference);

		thread0.start();
		thread1.start();

		thread0.join(TimeUnit.SECONDS.toMillis(timeoutSeconds * 2));
		thread1.join(TimeUnit.SECONDS.toMillis(timeoutSeconds * 2));

		String deadLockedThreadInfo = _getDeadlockedThreadInfo();

		Assert.assertFalse(deadLockedThreadInfo, thread0.isAlive());
		Assert.assertFalse(deadLockedThreadInfo, thread1.isAlive());

		Assert.assertNull(throwableAtomicReference.get());
	}

	private BaseDownstreamBuild _getBaseDownstreamBuild(
		DefaultTopLevelBuildReport defaultTopLevelBuildReport) {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		Job job = Mockito.mock(Job.class);

		Mockito.doReturn(
			job
		).when(
			axisTestClassGroup
		).getJob();

		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Mockito.doAnswer(
			invocation -> {
				defaultTopLevelBuildReport.getTestrayCaseResultFileName(
					axisTestClassGroup.getAxisName());

				return null;
			}
		).when(
			testrayContext
		).deleteTestrayCaseResultFile(
			axisTestClassGroup
		);

		Mockito.doReturn(
			testrayContext
		).when(
			job
		).getTestrayContext();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_testrayCaseResultURLLock", new Object());

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getBuildReportJSONObject();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getTestClassResults();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getTestResults();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getTestResults(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).reset();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).update();

		Mockito.doReturn(
			axisTestClassGroup
		).when(
			baseDownstreamBuild
		).getAxisTestClassGroup();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			baseDownstreamBuild
		).getBatchName();

		Mockito.doReturn(
			"https://test-1-1/job/test-job/AXIS_VARIABLE=0/456"
		).when(
			baseDownstreamBuild
		).getBuildURL();

		Mockito.doReturn(
			true
		).when(
			baseDownstreamBuild
		).isCompleted();

		return baseDownstreamBuild;
	}

	private String _getDeadlockedThreadInfo() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

		long[] threadIds = threadMXBean.findMonitorDeadlockedThreads();

		if (threadIds == null) {
			return "No deadlocked threads";
		}

		ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(
			threadIds, Integer.MAX_VALUE);

		return Arrays.toString(threadInfos);
	}

	private TopLevelBuild _getTopLevelBuild() {
		TopLevelBuild topLevelBuild = Mockito.mock(TopLevelBuild.class);

		Mockito.doReturn(
			"https://test-1-1/job/test-job/123"
		).when(
			topLevelBuild
		).getBuildURL();

		return topLevelBuild;
	}

	private Thread _newThread(
		Runnable runnable,
		AtomicReference<Throwable> throwableAtomicReference) {

		Thread thread = new Thread(
			() -> {
				try {
					runnable.run();
				}
				catch (Throwable throwable) {
					throwableAtomicReference.compareAndSet(null, throwable);
				}
			});

		thread.setDaemon(true);

		return thread;
	}

}