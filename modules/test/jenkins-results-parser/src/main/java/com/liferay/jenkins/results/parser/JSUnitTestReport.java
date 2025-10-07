/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitTestReport extends BaseTestReport {

	@Override
	public String getTestClassName() {
		return getTestTaskName();
	}

	@Override
	public String getTestName() {
		String testName = super.getTestName();

		int x = testName.indexOf(".modules.");

		if (x > 0) {
			testName = testName.substring(x + 9);
		}

		if (!testName.startsWith("apps") && !testName.startsWith("dxp.apps")) {
			testName = "apps." + testName;
		}

		return testName;
	}

	@Override
	public String getTestTaskName() {
		String testName = super.getTestName();

		int x = testName.indexOf(".modules.");

		if (x > 0) {
			testName = testName.substring(x + 9);
		}

		if (testName.contains(".src.")) {
			testName = testName.substring(0, testName.indexOf(".src."));
		}
		else if (testName.contains(".test.")) {
			testName = testName.substring(0, testName.indexOf(".test."));
		}

		if (!testName.contains("apps.")) {
			testName = "apps." + testName;
		}

		return ":" + testName.replaceAll("\\.", ":") + ":packageRunTest";
	}

	protected JSUnitTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		super(downstreamBuildReport, jsonObject);
	}

}