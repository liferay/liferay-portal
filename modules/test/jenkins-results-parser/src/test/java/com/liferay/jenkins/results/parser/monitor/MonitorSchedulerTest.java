/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class MonitorSchedulerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetDueMonitors() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		MonitorScheduler monitorScheduler = new MonitorScheduler(
			monitorResultStore);

		List<Monitor> monitors = Arrays.<Monitor>asList(
			new TestMonitor(_newMonitorConfig(900, "a")));

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				1000000L
			);

			testEquals(monitors, monitorScheduler.getDueMonitors(monitors));

			monitorResultStore.store("a", _newMonitorResult(1000000L));

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				1899000L
			);

			testEquals(
				Collections.emptyList(),
				monitorScheduler.getDueMonitors(monitors));

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				1900000L
			);

			testEquals(monitors, monitorScheduler.getDueMonitors(monitors));
		}
	}

	@Test
	public void testGetDueMonitorsZeroCadence() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		MonitorScheduler monitorScheduler = new MonitorScheduler(
			monitorResultStore);

		List<Monitor> monitors = Arrays.<Monitor>asList(
			new TestMonitor(_newMonitorConfig(0, "a")));

		monitorResultStore.store("a", _newMonitorResult(1000000L));

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				1000000L
			);

			testEquals(monitors, monitorScheduler.getDueMonitors(monitors));
		}
	}

	private MonitorConfig _newMonitorConfig(long cadence, String id) {
		return new MonitorConfig(
			cadence, id, null, MonitorConfig.Severity.MEDIUM, null,
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());
	}

	private MonitorResult _newMonitorResult(long timestamp) {
		return new MonitorResult(
			RandomTestUtil.randomString(), null, MonitorResult.Status.OK,
			timestamp);
	}

}