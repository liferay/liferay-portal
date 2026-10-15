/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.net.URL;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseParentBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		JenkinsResultsParserUtil.setBuildProperties(
			(Hashtable<Object, Object>)null);

		super.tearDown();
	}

	@Test
	public void testGenerateTestrayCaseResult() throws Exception {
		BaseParentBuild baseParentBuild = _mockBaseParentBuild();

		DownstreamBuild downstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);
		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Mockito.verify(
			downstreamBuild
		).getTestrayCaseResultURL();

		DownstreamBuild cachedDownstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		Mockito.when(
			cachedDownstreamBuild.isBuildCached()
		).thenReturn(
			true
		);

		_generateTestrayCaseResult(baseParentBuild, cachedDownstreamBuild);

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Mockito.verify(
			cachedDownstreamBuild
		).getTestrayCaseResultURL();

		DownstreamBuild archivedDownstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		Mockito.when(
			archivedDownstreamBuild.isFromArchive()
		).thenReturn(
			true
		);

		_generateTestrayCaseResult(baseParentBuild, Mockito.mock(Build.class));
		_generateTestrayCaseResult(baseParentBuild, _mockDownstreamBuild(""));
		_generateTestrayCaseResult(baseParentBuild, _mockDownstreamBuild(null));
		_generateTestrayCaseResult(baseParentBuild, archivedDownstreamBuild);

		Map<String, Future<URL>> downstreamTestrayCaseResultURLFutures =
			ReflectionTestUtil.getFieldValue(
				baseParentBuild, "_downstreamTestrayCaseResultURLFutures");

		Assert.assertEquals(
			downstreamTestrayCaseResultURLFutures.toString(), 2,
			downstreamTestrayCaseResultURLFutures.size());

		CountDownLatch countDownLatch = new CountDownLatch(1);

		DownstreamBuild blockedDownstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		Mockito.when(
			blockedDownstreamBuild.getTestrayCaseResultURL()
		).thenAnswer(
			invocation -> {
				countDownLatch.await();

				return null;
			}
		);

		_generateTestrayCaseResult(baseParentBuild, blockedDownstreamBuild);

		Assert.assertEquals(1, countDownLatch.getCount());

		countDownLatch.countDown();

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();
	}

	@Test
	public void testRemoveTestrayCaseResultURL() {
		BaseParentBuild baseParentBuild = _mockBaseParentBuild();

		String axisName = RandomTestUtil.randomString();

		DownstreamBuild downstreamBuild = _mockDownstreamBuild(axisName);

		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);

		baseParentBuild.removeTestrayCaseResultURL(null);

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Mockito.verify(
			downstreamBuild
		).getTestrayCaseResultURL();

		baseParentBuild.removeTestrayCaseResultURL(axisName);

		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Mockito.verify(
			downstreamBuild, Mockito.times(2)
		).getTestrayCaseResultURL();
	}

	@Test
	public void testUpdate() throws Exception {
		Properties buildProperties = new Properties();

		buildProperties.setProperty("build.thread.spawn.frequency", "1");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		BaseParentBuild baseParentBuild = _mockBaseParentBuild();

		Mockito.doCallRealMethod(
		).when(
			baseParentBuild
		).update();

		DownstreamBuild downstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		Mockito.doReturn(
			Collections.singletonList(downstreamBuild)
		).when(
			baseParentBuild
		).getDownstreamBuilds(
			null
		);

		Mockito.when(
			downstreamBuild.getStatus()
		).thenReturn(
			"completed"
		);

		ReflectionTestUtil.setFieldValue(
			baseParentBuild, "_buildUpdater", Mockito.mock(BuildUpdater.class));

		baseParentBuild.update();
		baseParentBuild.update();

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Mockito.verify(
			downstreamBuild
		).getTestrayCaseResultURL();

		Mockito.verify(
			downstreamBuild, Mockito.never()
		).update();
	}

	@Test
	public void testWaitForDownstreamTestrayCaseResultURLs() {
		String failedAxisName = RandomTestUtil.randomString();

		DownstreamBuild failedDownstreamBuild = _mockDownstreamBuild(
			failedAxisName);

		String message = RandomTestUtil.randomString();

		Mockito.when(
			failedDownstreamBuild.getTestrayCaseResultURL()
		).thenThrow(
			new RuntimeException(message)
		);

		BaseParentBuild baseParentBuild = _mockBaseParentBuild();
		DownstreamBuild downstreamBuild = _mockDownstreamBuild(
			RandomTestUtil.randomString());

		_generateTestrayCaseResult(baseParentBuild, downstreamBuild);

		_generateTestrayCaseResult(baseParentBuild, failedDownstreamBuild);

		PrintStream printStream = System.out;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream));

		try {
			baseParentBuild.waitForDownstreamTestrayCaseResultURLs();
		}
		finally {
			System.setOut(printStream);
		}

		String output = byteArrayOutputStream.toString();

		Assert.assertTrue(output, output.contains(failedAxisName));
		Assert.assertTrue(output, output.contains(message));

		Mockito.verify(
			downstreamBuild
		).getTestrayCaseResultURL();

		Map<String, Future<URL>> downstreamTestrayCaseResultURLFutures =
			ReflectionTestUtil.getFieldValue(
				baseParentBuild, "_downstreamTestrayCaseResultURLFutures");

		FutureTask<URL> futureTask = new FutureTask<>(() -> null);

		downstreamTestrayCaseResultURLFutures.put(
			RandomTestUtil.randomString(), futureTask);

		Thread futureTaskThread = new Thread(
			() -> {
				JenkinsResultsParserUtil.sleep(200);

				futureTask.run();
			});

		futureTaskThread.start();

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Assert.assertTrue(futureTask.isDone());

		downstreamTestrayCaseResultURLFutures.put(
			RandomTestUtil.randomString(), new FutureTask<>(() -> null));

		Thread thread = Thread.currentThread();

		thread.interrupt();

		baseParentBuild.waitForDownstreamTestrayCaseResultURLs();

		Assert.assertTrue(Thread.interrupted());
	}

	private void _generateTestrayCaseResult(
		BaseParentBuild baseParentBuild, Build build) {

		ReflectionTestUtil.invoke(
			baseParentBuild, "_generateTestrayCaseResult",
			new Class<?>[] {Build.class}, build);
	}

	private BaseParentBuild _mockBaseParentBuild() {
		BaseParentBuild baseParentBuild = Mockito.mock(BaseParentBuild.class);

		Mockito.doCallRealMethod(
		).when(
			baseParentBuild
		).removeTestrayCaseResultURL(
			Mockito.any()
		);

		Mockito.doCallRealMethod(
		).when(
			baseParentBuild
		).waitForDownstreamTestrayCaseResultURLs();

		return baseParentBuild;
	}

	private DownstreamBuild _mockDownstreamBuild(String axisName) {
		DownstreamBuild downstreamBuild = Mockito.mock(DownstreamBuild.class);

		Mockito.when(
			downstreamBuild.getAxisName()
		).thenReturn(
			axisName
		);

		return downstreamBuild;
	}

}