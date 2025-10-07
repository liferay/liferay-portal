/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

/**
 * @author Michael Hashimoto
 */
public class TopLevelStandaloneBuildTestrayCaseResult
	extends BaseStandaloneBuildTestrayCaseResult {

	public TopLevelStandaloneBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(testrayBuild, topLevelBuildReport);
	}

	@Override
	public BuildReport getBuildReport() {
		return getTopLevelBuildReport();
	}

	@Override
	public String getName() {
		return "Top Level Build";
	}

	@Override
	protected String getBatchName() {
		return "top-level-build";
	}

	@Override
	protected String getFileName() {
		TopLevelBuildReport topLevelBuildReport = getTopLevelBuildReport();

		JenkinsMaster jenkinsMaster = topLevelBuildReport.getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			"TESTS-", jenkinsMaster.getName(), "_",
			topLevelBuildReport.getJobName(), "_",
			String.valueOf(topLevelBuildReport.getBuildNumber()), "_",
			getBatchName(), ".xml");
	}

}