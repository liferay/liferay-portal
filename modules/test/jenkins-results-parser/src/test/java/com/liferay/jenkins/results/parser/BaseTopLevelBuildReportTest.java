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
	public void testAddDownstreamBuildReport() {
		BaseTopLevelBuildReport baseTopLevelBuildReport =
			_newBaseTopLevelBuildReport();

		baseTopLevelBuildReport.addDownstreamBuildReport(null);

		_assertDownstreamBuildReports(baseTopLevelBuildReport, 0);

		DownstreamBuildReport cachedDownstreamBuildReport =
			_newDownstreamBuildReport(RandomTestUtil.randomString(), true);
		DownstreamBuildReport downstreamBuildReport = _newDownstreamBuildReport(
			RandomTestUtil.randomString(), false);

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
		baseTopLevelBuildReport.addDownstreamBuildReport(downstreamBuildReport);

		baseTopLevelBuildReport.addDownstreamBuildReport(
			cachedDownstreamBuildReport);
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
		return new BaseTopLevelBuildReport(_BUILD_URL_STRING) {

			@Override
			public JSONObject getBuildReportJSONObject() {
				return new JSONObject();
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

	private static final String _BUILD_URL_STRING =
		"https://test-1-1/job/test-job/123";

}