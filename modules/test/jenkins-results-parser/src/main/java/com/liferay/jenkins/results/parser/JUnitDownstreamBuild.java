/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Hashimoto
 */
public class JUnitDownstreamBuild extends BaseDownstreamBuild {

	@Override
	public boolean isUniqueFailure() {
		if (!isFailing()) {
			return false;
		}

		List<TestResult> testResults = new ArrayList<>();

		testResults.addAll(getTestResults("FAILED"));
		testResults.addAll(getTestResults("REGRESSION"));

		if (testResults.isEmpty()) {
			return true;
		}

		for (TestResult testResult : testResults) {
			if (testResult.isUniqueFailure()) {
				return true;
			}
		}

		return false;
	}

	protected JUnitDownstreamBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

}