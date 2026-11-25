/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	public void testGetTestClassReports() {
		String testClassName1 = RandomTestUtil.randomString();

		TestClassReport testClassReport1 = _mockTestClassReport(testClassName1);

		String testClassName2 =
			testClassName1 + "$" + RandomTestUtil.randomString();

		TestClassReport testClassReport2 = _mockTestClassReport(testClassName2);

		String testClassName3 = RandomTestUtil.randomString();

		TestClassReport testClassReport3 = _mockTestClassReport(
			"junit.framework.TestSuite", testClassName2, testClassName3,
			testClassName3);

		BaseDownstreamBuildReport baseDownstreamBuildReport = Mockito.mock(
			BaseDownstreamBuildReport.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuildReport
		).getTestClassReports(
			Mockito.anyString()
		);

		Mockito.doReturn(
			Arrays.asList(
				testClassReport1, testClassReport2, testClassReport3,
				_mockTestClassReport(RandomTestUtil.randomString()))
		).when(
			baseDownstreamBuildReport
		).getTestClassReports();

		testEquals(
			Arrays.asList(testClassReport1, testClassReport2),
			baseDownstreamBuildReport.getTestClassReports(testClassName1));
		testEquals(
			Arrays.asList(testClassReport1, testClassReport2),
			baseDownstreamBuildReport.getTestClassReports(testClassName2));
		testEquals(
			Collections.emptyList(),
			baseDownstreamBuildReport.getTestClassReports(
				RandomTestUtil.randomString()));
		testEquals(
			Collections.singletonList(testClassReport3),
			baseDownstreamBuildReport.getTestClassReports(testClassName3));
	}

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

	private TestClassReport _mockTestClassReport(
		String testClassName, String... testNames) {

		TestClassReport testClassReport = Mockito.mock(TestClassReport.class);

		Mockito.doReturn(
			testClassName
		).when(
			testClassReport
		).getTestClassName();

		List<TestReport> testReports = new ArrayList<>();

		for (String testName : testNames) {
			TestReport testReport = Mockito.mock(TestReport.class);

			Mockito.doReturn(
				testName
			).when(
				testReport
			).getTestName();

			testReports.add(testReport);
		}

		Mockito.doReturn(
			testReports
		).when(
			testClassReport
		).getTestReports();

		return testClassReport;
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