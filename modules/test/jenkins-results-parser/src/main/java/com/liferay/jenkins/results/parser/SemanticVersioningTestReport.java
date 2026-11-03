/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class SemanticVersioningTestReport extends BaseTestReport {

	@Override
	public String getTestClassName() {
		String testName = getTestName();

		if (testName.endsWith("]")) {
			return testName;
		}

		return super.getTestClassName();
	}

	protected SemanticVersioningTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		super(downstreamBuildReport, jsonObject);
	}

}