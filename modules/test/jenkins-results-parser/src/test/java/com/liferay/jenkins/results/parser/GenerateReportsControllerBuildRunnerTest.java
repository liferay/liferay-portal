/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class GenerateReportsControllerBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_currentTimeMillis = JenkinsResultsParserUtil.getCurrentTimeMillis();
	}

	@Test
	public void testGetSelectedReportNames() throws Exception {
		Map<GenerateReportsBuildRunner.Report, Long> reportGeneratedTimes =
			new HashMap<>();
		long reportStaleTime =
			_currentTimeMillis - (3 * _MILLISECONDS_PER_HOUR);

		reportGeneratedTimes.put(
			GenerateReportsBuildRunner.Report.BUILD_HISTORY,
			reportStaleTime - _MILLISECONDS_PER_DAY);

		String firstBuildURL = RandomTestUtil.randomString();
		String secondBuildURL = RandomTestUtil.randomString();

		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					reportStaleTime + (2 * _MILLISECONDS_PER_HOUR),
					firstBuildURL),
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					reportStaleTime - 1, secondBuildURL)),
			true, Collections.<String>emptyList(), reportGeneratedTimes);
		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					reportStaleTime - 1, firstBuildURL),
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					reportStaleTime + (2 * _MILLISECONDS_PER_HOUR),
					secondBuildURL)),
			true, Collections.<String>emptyList(), reportGeneratedTimes);

		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					reportStaleTime - 1, firstBuildURL)),
			true,
			Arrays.asList(
				GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString()),
			reportGeneratedTimes);
	}

	@Test
	public void testGetSelectedReportNamesAWSBuildComparison()
		throws Exception {

		Map<GenerateReportsBuildRunner.Report, Long> reportGeneratedTimes =
			new HashMap<>();

		_testGetSelectedReportNames(
			Collections.<JSONObject>emptyList(), false,
			Arrays.asList(
				GenerateReportsBuildRunner.Report.AWS_BUILD_COMPARISON.
					toString()),
			reportGeneratedTimes);
		_testGetSelectedReportNames(
			Collections.<JSONObject>emptyList(), true,
			Collections.<String>emptyList(), reportGeneratedTimes);

		List<String> reportNames = new ArrayList<>();

		reportGeneratedTimes.clear();

		for (GenerateReportsBuildRunner.Report report :
				GenerateReportsBuildRunner.Report.values()) {

			if (report ==
					GenerateReportsBuildRunner.Report.AWS_BUILD_COMPARISON) {

				continue;
			}

			reportGeneratedTimes.put(report, 0L);

			reportNames.add(report.toString());
		}

		Collections.sort(reportNames);

		_testGetSelectedReportNames(
			Collections.<JSONObject>emptyList(), true, reportNames,
			reportGeneratedTimes);
	}

	@Test
	public void testGetSelectedReportNamesWithoutGeneratedTime()
		throws Exception {

		Map<GenerateReportsBuildRunner.Report, Long> reportGeneratedTimes =
			new HashMap<>();

		reportGeneratedTimes.put(
			GenerateReportsBuildRunner.Report.BUILD_HISTORY, 0L);

		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					(_currentTimeMillis - _MILLISECONDS_PER_DAY) + 1,
					RandomTestUtil.randomString())),
			true, Collections.<String>emptyList(), reportGeneratedTimes);
		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					_currentTimeMillis - _MILLISECONDS_PER_DAY,
					RandomTestUtil.randomString())),
			true,
			Arrays.asList(
				GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString()),
			reportGeneratedTimes);

		reportGeneratedTimes.put(
			GenerateReportsBuildRunner.Report.BUILD_HISTORY, null);

		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					(_currentTimeMillis - _MILLISECONDS_PER_DAY) + 1,
					RandomTestUtil.randomString())),
			true, Collections.<String>emptyList(), reportGeneratedTimes);
		_testGetSelectedReportNames(
			Arrays.asList(
				_newBuildJSONObject(
					GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString(),
					_currentTimeMillis - _MILLISECONDS_PER_DAY,
					RandomTestUtil.randomString())),
			true,
			Arrays.asList(
				GenerateReportsBuildRunner.Report.BUILD_HISTORY.toString()),
			reportGeneratedTimes);
	}

	@Test
	public void testIsReportGenerationDue() {
		long reportGeneratedTime = Math.abs(RandomTestUtil.randomInt());

		long reportStaleTime = reportGeneratedTime + _MILLISECONDS_PER_DAY;

		_testIsReportGenerationDue(
			reportStaleTime + 1, true, null, reportGeneratedTime,
			_MILLISECONDS_PER_DAY);
		_testIsReportGenerationDue(
			reportStaleTime + 1, true, reportStaleTime - 1, reportGeneratedTime,
			_MILLISECONDS_PER_DAY);
		_testIsReportGenerationDue(
			reportStaleTime - 1, false, null, reportGeneratedTime,
			_MILLISECONDS_PER_DAY);
		_testIsReportGenerationDue(
			reportStaleTime - 1, false, reportGeneratedTime + 1,
			reportGeneratedTime, _MILLISECONDS_PER_DAY);
		_testIsReportGenerationDue(
			reportStaleTime, true, null, reportGeneratedTime,
			_MILLISECONDS_PER_DAY);

		_testIsReportGenerationDue(
			_currentTimeMillis, true, null, 0, _MILLISECONDS_PER_DAY);
	}

	@Test
	public void testIsReportGenerationDueRetry() {
		long reportGeneratedTime = Math.abs(RandomTestUtil.randomInt());

		long reportStaleTime = reportGeneratedTime + _MILLISECONDS_PER_DAY;

		_testIsReportGenerationDueRetry(
			reportStaleTime + (32 * _MILLISECONDS_PER_HOUR),
			reportGeneratedTime, _MILLISECONDS_PER_DAY, _MILLISECONDS_PER_DAY);
		_testIsReportGenerationDueRetry(
			reportStaleTime + (4 * _MILLISECONDS_PER_HOUR), reportGeneratedTime,
			4 * _MILLISECONDS_PER_HOUR, _MILLISECONDS_PER_DAY);
		_testIsReportGenerationDueRetry(
			reportStaleTime + _MILLISECONDS_PER_HOUR, reportGeneratedTime,
			2 * _MILLISECONDS_PER_HOUR, _MILLISECONDS_PER_DAY);
		_testIsReportGenerationDueRetry(
			reportStaleTime, reportGeneratedTime, 2 * _MILLISECONDS_PER_HOUR,
			_MILLISECONDS_PER_DAY);

		long millisecondsPerMinute = 60 * 1000;

		_testIsReportGenerationDueRetry(
			reportGeneratedTime + (11 * millisecondsPerMinute),
			reportGeneratedTime, 10 * millisecondsPerMinute,
			10 * millisecondsPerMinute);
	}

	private JSONObject _newBuildJSONObject(
		String description, long timestamp, String url) {

		return new JSONObject(
		).put(
			"description", RandomTestUtil.randomString() + description
		).put(
			"timestamp", timestamp
		).put(
			"url", url
		);
	}

	private void _testGetSelectedReportNames(
			List<JSONObject> buildJSONObjects, boolean cloudCINode,
			List<String> expectedReportNames,
			Map<GenerateReportsBuildRunner.Report, Long> reportGeneratedTimes)
		throws Exception {

		String jobURL = RandomTestUtil.randomString();

		mockURLReaders();

		setURLReaderOutput(
			String.valueOf(
				new JSONObject(
				).put(
					"builds", new JSONArray(buildJSONObjects)
				)),
			jobURL);

		GenerateReportsControllerBuildRunner
			generateReportsControllerBuildRunner = Mockito.mock(
				GenerateReportsControllerBuildRunner.class);

		BuildData buildData = Mockito.mock(BuildData.class);

		Mockito.doReturn(
			jobURL
		).when(
			buildData
		).getJobURL();

		Mockito.doReturn(
			_currentTimeMillis
		).when(
			buildData
		).getStartTime();

		Mockito.doReturn(
			buildData
		).when(
			generateReportsControllerBuildRunner
		).getBuildData();

		Map<File, String> reportDataFileContents = new HashMap<>();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.getBuildProperties(
					Mockito.anyBoolean())
			).thenReturn(
				new Properties()
			);

			for (GenerateReportsBuildRunner.Report report :
					GenerateReportsBuildRunner.Report.values()) {

				File dataFile = report.getDataFile();

				if (dataFile == null) {
					continue;
				}

				Long reportGeneratedTime = reportGeneratedTimes.getOrDefault(
					report, _currentTimeMillis);

				if (reportGeneratedTime == null) {
					continue;
				}

				reportDataFileContents.put(
					dataFile,
					"var dataGeneratedDate = new Date(" + reportGeneratedTime +
						");");
			}

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::isCloudCINode
			).thenReturn(
				cloudCINode
			);

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.read(Mockito.any(File.class))
			).thenAnswer(
				invocation -> {
					File dataFile = invocation.getArgument(0);

					if (!reportDataFileContents.containsKey(dataFile)) {
						throw new IOException(RandomTestUtil.randomString());
					}

					return reportDataFileContents.get(dataFile);
				}
			);

			testEquals(
				expectedReportNames,
				ReflectionTestUtil.invoke(
					generateReportsControllerBuildRunner,
					"_getSelectedReportNames", new Class<?>[0]));
		}
	}

	private void _testIsReportGenerationDue(
		long currentTime, boolean expected, Long latestReportInvocationTime,
		long reportGeneratedTime, long reportStaleDuration) {

		testEquals(
			expected,
			ReflectionTestUtil.invoke(
				Mockito.mock(GenerateReportsControllerBuildRunner.class),
				"_isReportGenerationDue",
				new Class<?>[] {long.class, Long.class, long.class, long.class},
				currentTime, latestReportInvocationTime, reportGeneratedTime,
				reportStaleDuration));
	}

	private void _testIsReportGenerationDueRetry(
		long latestReportInvocationTime, long reportGeneratedTime,
		long reportRetryDuration, long reportStaleDuration) {

		_testIsReportGenerationDue(
			(latestReportInvocationTime + reportRetryDuration) - 1, false,
			latestReportInvocationTime, reportGeneratedTime,
			reportStaleDuration);
		_testIsReportGenerationDue(
			latestReportInvocationTime + reportRetryDuration, true,
			latestReportInvocationTime, reportGeneratedTime,
			reportStaleDuration);
	}

	private static final long _MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

	private static final long _MILLISECONDS_PER_HOUR = 60 * 60 * 1000;

	private long _currentTimeMillis;

}