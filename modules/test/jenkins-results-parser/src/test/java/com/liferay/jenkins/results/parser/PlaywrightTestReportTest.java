/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class PlaywrightTestReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassName() {
		testEquals(
			"com.liferay.portal.log.assertor.PortalLogAssertorTest",
			_getTestClassName(
				"com.liferay.portal.log.assertor.PortalLogAssertorTest." +
					"testScanXMLLog"));
		testEquals(
			"setup/site-cms-site/main/setup.spec.ts",
			_getTestClassName(
				"setup/site-cms-site/main/setup.spec.ts.Setup: Create a " +
					"space for Site CMS tests"));
		testEquals(
			"smoke/main/portalSmoke.spec.ts",
			_getTestClassName("smoke/main/portalSmoke.spec.ts.Smoke"));
		testEquals("unknown", _getTestClassName("unknown"));
	}

	@Test
	public void testGetTestClassReports() {
		BaseDownstreamBuildReport baseDownstreamBuildReport = Mockito.mock(
			BaseDownstreamBuildReport.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuildReport
		).getTestClassReports();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuildReport
		).getTestReports();

		Mockito.when(
			baseDownstreamBuildReport.getBatchName()
		).thenReturn(
			"playwright-js-smoke-tomcat101-hypersonic20"
		);

		Mockito.when(
			baseDownstreamBuildReport.getBuildReportJSONObject()
		).thenReturn(
			new JSONObject(
			).put(
				"testResults",
				new JSONArray(
				).put(
					_getTestResultJSONObject(
						"com.liferay.portal.log.assertor." +
							"PortalLogAssertorTest.testScanOSGiLog",
						"PASSED")
				).put(
					_getTestResultJSONObject(
						"com.liferay.portal.log.assertor." +
							"PortalLogAssertorTest.testScanXMLLog",
						"FAILED")
				).put(
					_getTestResultJSONObject(
						"smoke/main/portalSmoke.spec.ts.Smoke", "PASSED")
				)
			)
		);

		List<String> testClassNames = new ArrayList<>();

		for (TestClassReport testClassReport :
				baseDownstreamBuildReport.getTestClassReports()) {

			testClassNames.add(testClassReport.getTestClassName());

			if (!Objects.equals(
					testClassReport.getTestClassName(),
					"com.liferay.portal.log.assertor.PortalLogAssertorTest")) {

				continue;
			}

			List<TestReport> testReports = testClassReport.getTestReports();

			Assert.assertEquals(testReports.toString(), 2, testReports.size());

			Assert.assertTrue(testClassReport.isFailing());
		}

		Assert.assertEquals(
			Arrays.asList(
				"com.liferay.portal.log.assertor.PortalLogAssertorTest",
				"smoke/main/portalSmoke.spec.ts"),
			testClassNames);
	}

	@Test
	public void testGetTestName() {
		testEquals(
			"Setup: Create a space for Site CMS tests",
			_getTestName(
				"setup/site-cms-site/main/setup.spec.ts.Setup: Create a " +
					"space for Site CMS tests"));
		testEquals(
			"Smoke", _getTestName("smoke/main/portalSmoke.spec.ts.Smoke"));
		testEquals(
			"testScanXMLLog",
			_getTestName(
				"com.liferay.portal.log.assertor.PortalLogAssertorTest." +
					"testScanXMLLog"));
		testEquals("unknown", _getTestName("unknown"));
	}

	private PlaywrightTestReport _getPlaywrightTestReport(String testName) {
		return new PlaywrightTestReport(
			null,
			new JSONObject(
			).put(
				"name", testName
			));
	}

	private String _getTestClassName(String testName) {
		PlaywrightTestReport playwrightTestReport = _getPlaywrightTestReport(
			testName);

		return playwrightTestReport.getTestClassName();
	}

	private String _getTestName(String testName) {
		PlaywrightTestReport playwrightTestReport = _getPlaywrightTestReport(
			testName);

		return playwrightTestReport.getTestName();
	}

	private JSONObject _getTestResultJSONObject(String name, String status) {
		return new JSONObject(
		).put(
			"name", name
		).put(
			"status", status
		);
	}

}