/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.regex.Matcher;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PlaywrightTestReport extends BaseTestReport {

	@Override
	public String getTestClassName() {
		String testName = super.getTestName();

		Matcher matcher = _testNameMultiPattern.find(testName);

		if (matcher == null) {
			return testName;
		}

		return matcher.group("testClassName");
	}

	@Override
	public String getTestName() {
		String testName = super.getTestName();

		Matcher matcher = _testNameMultiPattern.find(testName);

		if (matcher == null) {
			return testName;
		}

		return matcher.group("testName");
	}

	protected PlaywrightTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		super(downstreamBuildReport, jsonObject);
	}

	private static final MultiPattern _testNameMultiPattern = new MultiPattern(
		"(?<testClassName>[^\\s]+\\.ts)\\.(?<testName>.+)",
		"(?<testClassName>.*Test)\\.(?<testName>[^\\.]+)");

}