/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PlaywrightTestReport extends BaseTestReport {

	@Override
	public String getTestClassName() {
		String testName = super.getTestName();

		Matcher matcher = _getTestNameMatcher(testName);

		if (matcher == null) {
			return testName;
		}

		return matcher.group("testClassName");
	}

	@Override
	public String getTestName() {
		String testName = super.getTestName();

		Matcher matcher = _getTestNameMatcher(testName);

		if (matcher == null) {
			return testName;
		}

		return matcher.group("testName");
	}

	protected PlaywrightTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		super(downstreamBuildReport, jsonObject);
	}

	private Matcher _getTestNameMatcher(String testName) {
		Matcher matcher = _playwrightTestNamePattern.matcher(testName);

		if (matcher.find()) {
			return matcher;
		}

		matcher = _jUnitTestNamePattern.matcher(testName);

		if (matcher.find()) {
			return matcher;
		}

		return null;
	}

	private static final Pattern _jUnitTestNamePattern = Pattern.compile(
		"(?<testClassName>.*Test)\\.(?<testName>[^\\.]+)");
	private static final Pattern _playwrightTestNamePattern = Pattern.compile(
		"(?<testClassName>[^\\s]+\\.ts)\\.(?<testName>.+)");

}