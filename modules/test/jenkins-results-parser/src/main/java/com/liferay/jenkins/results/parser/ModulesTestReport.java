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
public class ModulesTestReport extends BaseTestReport {

	@Override
	public String getTestClassName() {
		Matcher matcher = _testNamePattern.matcher(super.getTestName());

		if (matcher.matches()) {
			return matcher.group("testClassName");
		}

		return getTestName();
	}

	@Override
	public String getTestName() {
		Matcher matcher = _testNamePattern.matcher(super.getTestName());

		if (matcher.matches()) {
			return matcher.group("testName");
		}

		return getTestName();
	}

	protected ModulesTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		super(downstreamBuildReport, jsonObject);
	}

	private static final Pattern _testNamePattern = Pattern.compile(
		"(?<testClassName>.*)\\.(?<testName>[^\\.]+)");

}