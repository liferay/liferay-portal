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
public class BaseTestClassReportTest
	extends com.liferay.jenkins.results.parser.Test {

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

}