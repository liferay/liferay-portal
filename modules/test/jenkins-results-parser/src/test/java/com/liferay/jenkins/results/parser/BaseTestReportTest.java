/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class BaseTestReportTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestClassName() {
		testEquals(
			"LocalFile.PortalSmoke",
			_getTestClassName(
				"LocalFile.PortalSmoke", "LocalFile.PortalSmoke#Smoke"));
		testEquals(
			"LocalFile.PortalSmoke#Smoke",
			_getTestClassName(null, "LocalFile.PortalSmoke#Smoke"));
		testEquals(
			"com.liferay.jenkins.Jenkins.build-iw",
			_getTestClassName(null, "com.liferay.jenkins.Jenkins.build-iw"));
		testEquals(
			"com.liferay.portal.log.assertor.PortalLogAssertorTest",
			_getTestClassName(
				"com.liferay.portal.log.assertor.PortalLogAssertorTest",
				"com.liferay.portal.log.assertor.PortalLogAssertorTest." +
					"testScanXMLLog"));
		testEquals(
			"com.liferay.portal.log.assertor.PortalLogAssertorTest",
			_getTestClassName(
				null,
				"com.liferay.portal.log.assertor.PortalLogAssertorTest." +
					"testScanXMLLog"));
	}

	private String _getTestClassName(String testClassName, String testName) {
		JSONObject jsonObject = new JSONObject(
		).put(
			"name", testName
		);

		if (testClassName != null) {
			jsonObject.put("testClassName", testClassName);
		}

		DefaultTestReport defaultTestReport = new DefaultTestReport(
			null, jsonObject);

		return defaultTestReport.getTestClassName();
	}

}