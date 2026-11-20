/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class MonitorEngineTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testMonitorEngineDuplicateIds() {
		new MonitorEngine(
			Arrays.<Monitor>asList(
				new TestMonitor(_newMonitorConfig("a")),
				new TestMonitor(_newMonitorConfig("b"))));

		try {
			new MonitorEngine(
				Arrays.<Monitor>asList(
					new TestMonitor(_newMonitorConfig("a")),
					new TestMonitor(_newMonitorConfig("a"))));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test(timeout = 10000)
	public void testRunCycle() {
		TestMonitor hangingTestMonitor = new TestMonitor(
			_newMonitorConfig("a", 1)) {

			@Override
			public MonitorResult execute() {
				try {
					Thread.sleep(10000);
				}
				catch (InterruptedException interruptedException) {
					Thread thread = Thread.currentThread();

					thread.interrupt();
				}

				return null;
			}

		};

		TestMonitor passingTestMonitor = new TestMonitor(
			_newMonitorConfig("b", 0));

		TestMonitor throwingTestMonitor = new TestMonitor(
			_newMonitorConfig("c", 0)) {

			@Override
			public MonitorResult execute() {
				throw new RuntimeException("Unable to execute the monitor");
			}

		};

		MonitorEngine monitorEngine = new MonitorEngine(
			Arrays.<Monitor>asList(
				hangingTestMonitor, passingTestMonitor, throwingTestMonitor));

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.combine(Mockito.<String[]>any())
			).thenAnswer(
				invocation -> {
					StringBuilder sb = new StringBuilder();

					for (Object argument : invocation.getArguments()) {
						sb.append(argument);
					}

					return sb.toString();
				}
			);

			long virtualCurrentTime = RandomTestUtil.randomLong();

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				virtualCurrentTime
			);

			Map<Monitor, MonitorResult> monitorResultsMap =
				monitorEngine.runCycle();

			testEquals(3, monitorResultsMap.size());

			MonitorResult monitorResult = monitorResultsMap.get(
				hangingTestMonitor);

			testEquals(
				"Monitor a timed out after 1000 ms",
				monitorResult.getMessage());
			testEquals(
				MonitorResult.Status.CRITICAL, monitorResult.getStatus());
			testEquals(virtualCurrentTime, monitorResult.getTimestamp());

			monitorResult = monitorResultsMap.get(passingTestMonitor);

			testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
			testEquals(virtualCurrentTime, monitorResult.getTimestamp());

			monitorResult = monitorResultsMap.get(throwingTestMonitor);

			testEquals(
				"Monitor c failed: Unable to execute the monitor",
				monitorResult.getMessage());
			testEquals(
				MonitorResult.Status.CRITICAL, monitorResult.getStatus());

			monitorResultsMap = monitorEngine.runCycle();

			testEquals(3, monitorResultsMap.size());
		}
	}

	@Test(timeout = 10000)
	public void testRunCycleContinuesWhenPrepareCycleFails() {
		TestMonitor failingTestMonitor = Mockito.spy(
			new TestMonitor(_newMonitorConfig("a", 0)));

		Mockito.doThrow(
			new RuntimeException()
		).when(
			failingTestMonitor
		).prepareCycle();

		TestMonitor testMonitor = Mockito.spy(
			new TestMonitor(_newMonitorConfig("b", 0)));

		MonitorEngine monitorEngine = new MonitorEngine(
			Arrays.<Monitor>asList(failingTestMonitor, testMonitor));

		PrintStream printStream = System.out;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream, true));

		try {
			Map<Monitor, MonitorResult> monitorResultsMap =
				monitorEngine.runCycle();

			testEquals(2, monitorResultsMap.size());
		}
		finally {
			System.setOut(printStream);
		}

		Mockito.verify(
			testMonitor
		).prepareCycle();

		testEquals(
			"WARNING: Unable to prepare monitor a: " +
				"java.lang.RuntimeException\n",
			byteArrayOutputStream.toString());
	}

	@Test(timeout = 10000)
	public void testRunCycleIgnoresMonitorsAddedAfterConstruction() {
		List<Monitor> monitors = new ArrayList<>();

		monitors.add(new TestMonitor(_newMonitorConfig("a", 0)));

		MonitorEngine monitorEngine = new MonitorEngine(monitors);

		monitors.add(new TestMonitor(_newMonitorConfig("b", 0)));

		Map<Monitor, MonitorResult> monitorResultsMap =
			monitorEngine.runCycle();

		testEquals(1, monitorResultsMap.size());
	}

	@Test(timeout = 10000)
	public void testRunCycleKeepsDurationMillis() {
		TestMonitor testMonitor = new TestMonitor(
			_newMonitorConfig(RandomTestUtil.randomString(), 10)) {

			@Override
			public MonitorResult execute() {
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				return super.execute();
			}

		};

		MonitorEngine monitorEngine = new MonitorEngine(
			Collections.<Monitor>singletonList(testMonitor));

		Map<Monitor, MonitorResult> monitorResultsMap =
			monitorEngine.runCycle();

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() >= 200);
	}

	@Test
	public void testRunCycleKeepsLinkURLs() {
		List<String> linkURLs = Collections.singletonList(
			"https://" + RandomTestUtil.randomString());

		TestMonitor testMonitor = new TestMonitor(
			_newMonitorConfig(RandomTestUtil.randomString(), 10)) {

			@Override
			public MonitorResult execute() {
				return new MonitorResult(
					linkURLs, RandomTestUtil.randomString(), null,
					MonitorResult.Status.CRITICAL, RandomTestUtil.randomLong());
			}

		};

		MonitorEngine monitorEngine = new MonitorEngine(
			Collections.<Monitor>singletonList(testMonitor));

		Map<Monitor, MonitorResult> monitorResultsMap =
			monitorEngine.runCycle();

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals(linkURLs, monitorResult.getLinkURLs());
	}

	@Test
	public void testRunCyclePreparesMonitors() {
		TestMonitor testMonitor = Mockito.spy(
			new TestMonitor(_newMonitorConfig("a")));

		MonitorEngine monitorEngine = new MonitorEngine(
			Arrays.<Monitor>asList(testMonitor));

		monitorEngine.runCycle();

		Mockito.verify(
			testMonitor
		).prepareCycle();
	}

	private MonitorConfig _newMonitorConfig(String id) {
		return _newMonitorConfig(id, RandomTestUtil.randomLong());
	}

	private MonitorConfig _newMonitorConfig(String id, long timeoutSeconds) {
		return new MonitorConfig(
			id, null, MonitorConfig.Severity.MEDIUM, null, timeoutSeconds,
			RandomTestUtil.randomString());
	}

}