/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitJUnitTestResult extends JUnitTestResult {

	@Override
	public String getClassName() {
		return getTestTaskName();
	}

	@Override
	public String getDisplayName() {
		return getTestName();
	}

	@Override
	public String getTestName() {
		String testName = JenkinsResultsParserUtil.combine(
			super.getClassName(), ".", super.getTestName());

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
		String testTaskName = JenkinsResultsParserUtil.combine(
			super.getClassName(), ".", super.getTestName());

		int x = testTaskName.indexOf(".modules.");

		if (x > 0) {
			testTaskName = testTaskName.substring(x + 9);
		}

		if (testTaskName.contains(".clay.")) {
			testTaskName = testTaskName.substring(
				0, testTaskName.indexOf(".clay."));
		}
		else if (testTaskName.contains(".src.")) {
			testTaskName = testTaskName.substring(
				0, testTaskName.indexOf(".src."));
		}
		else if (testTaskName.contains(".test.")) {
			testTaskName = testTaskName.substring(
				0, testTaskName.indexOf(".test."));
		}

		if (!testTaskName.contains("apps.")) {
			testTaskName = "apps." + testTaskName;
		}

		return ":" + testTaskName.replaceAll("\\.", ":") + ":packageRunTest";
	}

	protected JSUnitJUnitTestResult(Build build, JSONObject caseJSONObject) {
		super(build, caseJSONObject);
	}

}