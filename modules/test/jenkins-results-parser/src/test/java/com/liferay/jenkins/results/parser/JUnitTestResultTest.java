/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class JUnitTestResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestReportURL() {
		Build build = Mockito.mock(Build.class);

		Mockito.when(
			build.getBuildURL()
		).thenReturn(
			"https://test-1-1.liferay.com/job/test%20portal(master)/12"
		);

		JSONObject caseJSONObject = new JSONObject();

		caseJSONObject.put(
			"className", "com.liferay.portal.FooTest"
		).put(
			"duration", 1.0
		).put(
			"name", "testBar[0]"
		).put(
			"status", "PASSED"
		);

		JUnitTestResult jUnitTestResult = new JUnitTestResult(
			build, caseJSONObject);

		testEquals(
			"https://test-1-1.liferay.com/job/test%20portal(master)/12" +
				"/testReport/com.liferay.portal/FooTest/testBar_0_",
			jUnitTestResult.getTestReportURL());
	}

}