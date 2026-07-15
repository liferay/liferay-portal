/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class MonitorEngineTest extends com.liferay.jenkins.results.parser.Test {

	@Test(timeout = 10000)
	public void testRunCycle() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		long intervalMillis = 1000L * 10L;

		TestMonitor hangingTestMonitor = new TestMonitor(
			_newMonitorConfig("a", intervalMillis / 1000, 1)) {

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
			_newMonitorConfig("b", intervalMillis / 1000, 0));

		TestMonitor throwingTestMonitor = new TestMonitor(
			_newMonitorConfig("c", intervalMillis / 1000, 0)) {

			@Override
			public MonitorResult execute() {
				throw new RuntimeException("Unable to execute the monitor");
			}

		};

		MonitorEngine monitorEngine = new MonitorEngine(
			monitorResultStore,
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

			long virtualCurrentTime = intervalMillis + 1L;

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				virtualCurrentTime
			);

			Map<Monitor, MonitorResult> monitorResultsMap =
				monitorEngine.runCycle();

			testEquals(3, monitorResultsMap.size());

			MonitorResult latestMonitorResult =
				monitorResultStore.getLatestMonitorResult(
					hangingTestMonitor.getId());

			testEquals(
				MonitorResult.Status.UNKNOWN, latestMonitorResult.getStatus());
			testEquals(
				"Monitor a timed out after 1000 ms",
				latestMonitorResult.getMessage());
			testEquals(virtualCurrentTime, latestMonitorResult.getTimestamp());

			latestMonitorResult = monitorResultStore.getLatestMonitorResult(
				passingTestMonitor.getId());

			testEquals(
				MonitorResult.Status.OK, latestMonitorResult.getStatus());
			testEquals(virtualCurrentTime, latestMonitorResult.getTimestamp());

			latestMonitorResult = monitorResultStore.getLatestMonitorResult(
				throwingTestMonitor.getId());

			testEquals(
				MonitorResult.Status.UNKNOWN, latestMonitorResult.getStatus());
			testEquals(
				"Monitor c failed: Unable to execute the monitor",
				latestMonitorResult.getMessage());

			monitorResultsMap = monitorEngine.runCycle();

			testEquals(0, monitorResultsMap.size());

			List<MonitorResult> monitorResults =
				monitorResultStore.getMonitorResults(
					passingTestMonitor.getId());

			testEquals(1, monitorResults.size());

			virtualCurrentTime += intervalMillis;

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				virtualCurrentTime
			);

			monitorResultsMap = monitorEngine.runCycle();

			testEquals(3, monitorResultsMap.size());

			monitorResults = monitorResultStore.getMonitorResults(
				passingTestMonitor.getId());

			testEquals(2, monitorResults.size());
		}
	}

	private MonitorConfig _newMonitorConfig(
		String id, long intervalSeconds, long timeout) {

		return new MonitorConfig(
			id, intervalSeconds, null, MonitorConfig.Severity.MEDIUM, null,
			timeout, RandomTestUtil.randomString());
	}

}