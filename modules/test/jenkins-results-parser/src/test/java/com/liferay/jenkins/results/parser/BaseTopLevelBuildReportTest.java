/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class BaseTopLevelBuildReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testAccessorsWithBuildReport() {
		String testSuiteName = RandomTestUtil.randomString();

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(
				new JSONObject(
				).put(
					"testSuiteName", testSuiteName
				).put(
					"totalActualDuration", 1000L
				).put(
					"totalCachedDuration", 2000L
				).put(
					"totalDuration", 3000L
				));

		Assert.assertEquals(
			1000L, baseTopLevelBuildReport.getTotalActualDuration());
		Assert.assertEquals(
			2000L, baseTopLevelBuildReport.getTotalCachedDuration());
		Assert.assertEquals(3000L, baseTopLevelBuildReport.getTotalDuration());
		Assert.assertEquals(
			testSuiteName, baseTopLevelBuildReport.getTestSuiteName());

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		Assert.assertEquals("", baseTopLevelBuildReport.getTestSuiteName());
	}

	@Test
	public void testAccessorsWithoutBuildReport() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport(null);

		Assert.assertNull(baseTopLevelBuildReport.getControllerBuildReport());
		Assert.assertNull(baseTopLevelBuildReport.getTestSuiteName());

		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTotalActualDuration());
		Assert.assertEquals(
			0L, baseTopLevelBuildReport.getTotalCachedDuration());
		Assert.assertEquals(0L, baseTopLevelBuildReport.getTotalDuration());
	}

	@Test
	public void testAddDownstreamBuildReport() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(null);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);

		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);
	}

	@Test
	public void testAddDownstreamBuildReports() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReports(null);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);
		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		baseTopLevelBuildReport.addDownstreamBuildReports(
			Arrays.asList(cachedDownstreamBuildReport, downstreamBuildReport));

		List<DownstreamBuildReport> downstreamBuildReports =
			_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);

		Assert.assertTrue(
			downstreamBuildReports.contains(cachedDownstreamBuildReport));
		Assert.assertTrue(
			downstreamBuildReports.contains(downstreamBuildReport));
	}

	@Test
	public void testGetDownstreamBuildReport() {
		String axisName = RandomTestUtil.randomString();

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(axisName, true);
		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			axisName, false);

		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		Assert.assertSame(
			downstreamBuildReport,
			baseTopLevelBuildReport.getDownstreamBuildReport(axisName));

		Assert.assertNull(
			baseTopLevelBuildReport.getDownstreamBuildReport(
				RandomTestUtil.randomString()));

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 2);

		baseTopLevelBuildReport = _newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);

		Assert.assertSame(
			cachedDownstreamBuildReport,
			baseTopLevelBuildReport.getDownstreamBuildReport(axisName));
	}

	private List<DownstreamBuildReport> _assertDownstreamBuildReports(
		BaseTopLevelBuildReport baseTopLevelBuildReport, int expectedCount) {

		List<DownstreamBuildReport> downstreamBuildReports =
			baseTopLevelBuildReport.getDownstreamBuildReports();

		Assert.assertEquals(
			downstreamBuildReports.toString(), expectedCount,
			downstreamBuildReports.size());

		return downstreamBuildReports;
	}

	private BaseTopLevelBuildReport _newBaseTopLevelBuildReport() {
		return _newBaseTopLevelBuildReport(new JSONObject());
	}

	private BaseTopLevelBuildReport _newBaseTopLevelBuildReport(
		JSONObject buildReportJSONObject) {

		return new BaseTopLevelBuildReport(
			"https://test-1-1/job/test-job/123") {

			@Override
			public JSONObject getBuildReportJSONObject() {
				return buildReportJSONObject;
			}

		};
	}

	private DownstreamBuildReport _newDownstreamBuildReport(
		String axisName, boolean buildCached) {

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.doReturn(
			axisName
		).when(
			downstreamBuildReport
		).getAxisName();

		Mockito.doReturn(
			buildCached
		).when(
			downstreamBuildReport
		).isBuildCached();

		return downstreamBuildReport;
	}

}