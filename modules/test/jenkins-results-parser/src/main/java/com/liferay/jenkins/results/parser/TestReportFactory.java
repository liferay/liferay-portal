/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestReportFactory {

	public static TestClassReport newTestClassReport(
		DownstreamBuildReport downstreamBuildReport, String testClassName) {

		return new DefaultTestClassReport(downstreamBuildReport, testClassName);
	}

	public static TestReport newTestReport(
		DownstreamBuildReport downstreamBuildReport, JSONObject jsonObject) {

		String batchName = downstreamBuildReport.getBatchName();

		if (batchName.startsWith("integration") ||
			batchName.startsWith("modules-integration") ||
			batchName.startsWith("modules-unit") ||
			batchName.startsWith("unit")) {

			return new JUnitTestReport(downstreamBuildReport, jsonObject);
		}
		else if (batchName.startsWith("js-unit")) {
			return new JSUnitTestReport(downstreamBuildReport, jsonObject);
		}
		else if (batchName.startsWith("modules-compile") ||
				 batchName.startsWith("modules-semantic-versioning") ||
				 batchName.startsWith("rest-builder") ||
				 batchName.startsWith("service-builder") ||
				 batchName.startsWith("workspaces-compile")) {

			return new ModulesTestReport(downstreamBuildReport, jsonObject);
		}
		else if (batchName.startsWith("playwright-js")) {
			return new PlaywrightTestReport(downstreamBuildReport, jsonObject);
		}

		return new DefaultTestReport(downstreamBuildReport, jsonObject);
	}

}