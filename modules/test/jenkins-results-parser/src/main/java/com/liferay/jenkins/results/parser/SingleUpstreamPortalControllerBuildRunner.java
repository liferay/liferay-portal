/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class SingleUpstreamPortalControllerBuildRunner
	<S extends ControllerPortalTopLevelBuildData>
		extends BaseUpstreamPortalControllerBuildRunner<S> {

	protected SingleUpstreamPortalControllerBuildRunner(S buildData) {
		super(buildData);
	}

	@Override
	protected String getInvocationJobName() {
		return "test-portal-upstream";
	}

	@Override
	protected void invokeBuild() {
		S buildData = getBuildData();

		String testSuiteName = buildData.getTestSuiteName();

		Map<String, String> invocationParameters = new HashMap<>();

		invocationParameters.putAll(buildData.getBuildParameters());

		invocationParameters.put("CI_TEST_SUITE", testSuiteName);
		invocationParameters.put(
			"CONTROLLER_BUILD_URL", buildData.getBuildURL());
		invocationParameters.put(
			"JENKINS_GITHUB_BRANCH_NAME",
			buildData.getJenkinsGitHubBranchName());
		invocationParameters.put(
			"JENKINS_GITHUB_BRANCH_USERNAME",
			buildData.getJenkinsGitHubUsername());
		invocationParameters.put("PARENT_BUILD_URL", buildData.getBuildURL());
		invocationParameters.put(
			"PORTAL_GIT_COMMIT", buildData.getPortalBranchSHA());

		String portalGitHubCompareURL = getPortalGitHubCompareURL();

		if (portalGitHubCompareURL != null) {
			invocationParameters.put(
				"PORTAL_GITHUB_COMPARE_URL", portalGitHubCompareURL);
		}

		invocationParameters.put(
			"PORTAL_GITHUB_URL", buildData.getPortalGitHubURL());
		invocationParameters.put(
			"PORTAL_UPSTREAM_BRANCH_NAME",
			buildData.getPortalUpstreamBranchName());
		invocationParameters.put("SLAVE_LABEL", getSlaveLabel(testSuiteName));
		invocationParameters.put(
			"TEST_PORTAL_BUILD_PROFILE",
			getTestPortalBuildProfile(testSuiteName));

		String testrayProjectName = buildData.getTestrayProjectName();

		if (testrayProjectName != null) {
			invocationParameters.put(
				"TESTRAY_BUILD_NAME", buildData.getTestrayBuildName());
			invocationParameters.put(
				"TESTRAY_PROJECT_NAME", testrayProjectName);
			invocationParameters.put(
				"TESTRAY_ROUTINE_NAME", buildData.getTestrayRoutineName());
		}

		invocationParameters.put(
			"TESTRAY_SLACK_CHANNELS", getTestraySlackChannels(testSuiteName));
		invocationParameters.put(
			"TESTRAY_SLACK_ICON_EMOJI",
			getTestraySlackIconEmoji(testSuiteName));
		invocationParameters.put(
			"TESTRAY_SLACK_USERNAME", getTestraySlackUsername(testSuiteName));

		String invocationJobURL = getInvocationJobURL(testSuiteName);

		long queueId = JenkinsResultsParserUtil.invokeJenkinsBuild(
			invocationJobURL, invocationParameters);

		if (queueId == 0) {
			throw new RuntimeException("Unable to invoke " + invocationJobURL);
		}

		keepJenkinsBuild(true);

		StringBuilder sb = new StringBuilder();

		sb.append("<a href=\"");
		sb.append(JenkinsResultsParserUtil.getRemoteURL(invocationJobURL));
		sb.append("\"><strong>IN QUEUE</strong></a>");
		sb.append("<ul><li><strong>Git ID:</strong> ");
		sb.append("<a href=\"https://github.com/");
		sb.append(buildData.getPortalGitHubUsername());
		sb.append("/");
		sb.append(buildData.getPortalGitHubRepositoryName());
		sb.append("/commit/");
		sb.append(buildData.getPortalBranchSHA());
		sb.append("\">");
		sb.append(getPortalBranchAbbreviatedSHA());
		sb.append("</a></li>");

		if (portalGitHubCompareURL != null) {
			sb.append("<li><strong>Git Compare:</strong> <a href=\"");
			sb.append(getPortalGitHubCompareURL());
			sb.append("\">??? commits</a></li>");
		}

		sb.append("</ul>");

		buildData.setBuildDescription(sb.toString());

		updateBuildDescription();
	}

}