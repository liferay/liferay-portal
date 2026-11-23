/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseTestClassReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetOverheadDuration() {
		_testGetOverheadDuration(
			1000, 0, new StopWatchRecordsGroup(), 1000, 2500);
		_testGetOverheadDuration(
			10000, 6500, new StopWatchRecordsGroup(), 1000, 2500);
		_testGetOverheadDuration(10000, 6500, null, 1000, 2500);

		StopWatchRecordsGroup stopWatchRecordsGroup =
			new StopWatchRecordsGroup();

		stopWatchRecordsGroup.add(
			new StopWatchRecord(
				"test.execution.duration", RandomTestUtil.randomLong()));

		_testGetOverheadDuration(
			10000, 6500, stopWatchRecordsGroup, 1000, 2500);

		stopWatchRecordsGroup = new StopWatchRecordsGroup();

		stopWatchRecordsGroup.add(
			new StopWatchRecord(
				"test.execution.duration", RandomTestUtil.randomLong(), 3000));

		_testGetOverheadDuration(
			10000, 7000, stopWatchRecordsGroup, 1000, 2500);
	}

	@Test
	public void testGetTestTaskName() {
		DefaultTestClassReport defaultTestClassReport =
			new DefaultTestClassReport(null, RandomTestUtil.randomString());

		testEquals(null, defaultTestClassReport.getTestTaskName());

		String testTaskName = RandomTestUtil.randomString();

		defaultTestClassReport.addTestReport(
			_getDefaultTestReport(testTaskName));

		testEquals(testTaskName, defaultTestClassReport.getTestTaskName());

		defaultTestClassReport.addTestReport(
			_getDefaultTestReport(RandomTestUtil.randomString()));

		testEquals(testTaskName, defaultTestClassReport.getTestTaskName());
	}

	private DefaultTestReport _getDefaultTestReport(String testTaskName) {
		return new DefaultTestReport(
			null,
			new JSONObject(
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"testTaskName", testTaskName
			));
	}

	private void _testGetOverheadDuration(
		long duration, long overheadDuration,
		StopWatchRecordsGroup stopWatchRecordsGroup,
		long... testReportDurations) {

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.when(
			downstreamBuildReport.getDuration()
		).thenReturn(
			duration
		);

		Mockito.when(
			downstreamBuildReport.getStopWatchRecordsGroup()
		).thenReturn(
			stopWatchRecordsGroup
		);

		List<TestReport> testReports = new ArrayList<>();

		for (long testReportDuration : testReportDurations) {
			testReports.add(
				new DefaultTestReport(
					null,
					new JSONObject(
					).put(
						"duration", testReportDuration
					)));
		}

		Mockito.when(
			downstreamBuildReport.getTestReports()
		).thenReturn(
			testReports
		);

		DefaultTestClassReport defaultTestClassReport =
			new DefaultTestClassReport(
				downstreamBuildReport, RandomTestUtil.randomString());

		testEquals(
			overheadDuration, defaultTestClassReport.getOverheadDuration());
	}

}