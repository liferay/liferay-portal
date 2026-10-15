/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseDownstreamBuildReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestrayCaseResultURL() throws Exception {
		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		_testGetTestrayCaseResultURL(new JSONObject(), null);
		_testGetTestrayCaseResultURL(
			new JSONObject(
			).put(
				"testrayCaseResultURL", ""
			),
			null);
		_testGetTestrayCaseResultURL(
			new JSONObject(
			).put(
				"testrayCaseResultURL", RandomTestUtil.randomString()
			),
			null);
		_testGetTestrayCaseResultURL(
			new JSONObject(
			).put(
				"testrayCaseResultURL", String.valueOf(testrayCaseResultURL)
			),
			testrayCaseResultURL);
		_testGetTestrayCaseResultURL(null, null);
	}

	private void _testGetTestrayCaseResultURL(
		JSONObject buildReportJSONObject, URL expectedTestrayCaseResultURL) {

		BaseDownstreamBuildReport baseDownstreamBuildReport = Mockito.mock(
			BaseDownstreamBuildReport.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuildReport
		).getTestrayCaseResultURL();

		Mockito.when(
			baseDownstreamBuildReport.getBuildReportJSONObject()
		).thenReturn(
			buildReportJSONObject
		);

		Assert.assertEquals(
			expectedTestrayCaseResultURL,
			baseDownstreamBuildReport.getTestrayCaseResultURL());
	}

}