/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class ControllerPortalTopLevelBuildData extends PortalTopLevelBuildData {

	public String getTestrayBuildName() {
		String testrayProjectName = getTestrayProjectName();

		if (testrayProjectName == null) {
			return null;
		}

		return JenkinsResultsParserUtil.combine(
			getTestrayRoutineName(), " - ", String.valueOf(getBuildNumber()),
			" - ",
			JenkinsResultsParserUtil.toDateString(
				new Date(getStartTime()), "yyyy-MM-dd[HH:mm:ss]",
				"America/Los_Angeles"));
	}

	public String getTestrayProjectName() {
		String testrayProjectName = Environment.get("TESTRAY_PROJECT_NAME");

		if ((testrayProjectName != null) && !testrayProjectName.isEmpty()) {
			return testrayProjectName;
		}

		return null;
	}

	public String getTestrayRoutineName() {
		String testrayProjectName = getTestrayProjectName();

		if (testrayProjectName == null) {
			return null;
		}

		return JenkinsResultsParserUtil.combine(
			"[", getPortalUpstreamBranchName(), "] ci:test:",
			getTestSuiteName());
	}

	public String getTestSuiteName() {
		String jobName = getJobName();

		Matcher matcher = _jobNamePattern.matcher(jobName);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid job name " + jobName);
		}

		String testSuiteName = matcher.group("testSuiteName");

		if (testSuiteName == null) {
			testSuiteName = "default";
		}

		return testSuiteName;
	}

	protected ControllerPortalTopLevelBuildData(
		String runId, String jobName, String buildURL) {

		super(runId, jobName, buildURL);

		setPortalBranchSHA(_getPortalBranchSHA());
		setPortalGitHubURL(_getPortalGitHubURL());
		setPortalUpstreamBranchName(_getPortalUpstreamBranchName());

		String jenkinsGitHubURL = getBuildParameter("JENKINS_GITHUB_URL");

		if ((jenkinsGitHubURL != null) && !jenkinsGitHubURL.isEmpty()) {
			setJenkinsGitHubURL(jenkinsGitHubURL);
		}
	}

	private String _getPortalBranchSHA() {
		RemoteGitRef remoteGitRef = GitUtil.getRemoteGitRef(
			_getPortalGitHubURL());

		return remoteGitRef.getSHA();
	}

	private String _getPortalGitHubURL() {
		String portalGitHubURL = Environment.get("PORTAL_GITHUB_URL");

		if ((portalGitHubURL != null) && !portalGitHubURL.isEmpty()) {
			return portalGitHubURL;
		}

		return JenkinsResultsParserUtil.combine(
			"https://github.com/liferay/", _getPortalRepositoryName(), "/tree/",
			_getPortalUpstreamBranchName());
	}

	private String _getPortalRepositoryName() {
		String upstreamBranchName = _getPortalUpstreamBranchName();

		if (upstreamBranchName.equals("master")) {
			return "liferay-portal";
		}

		return "liferay-portal-ee";
	}

	private String _getPortalUpstreamBranchName() {
		String portalUpstreamBranch = getBuildParameter(
			"PORTAL_UPSTREAM_BRANCH");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranch)) {
			return portalUpstreamBranch;
		}

		String jobName = getJobName();

		Matcher matcher = _jobNamePattern.matcher(jobName);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid job name " + jobName);
		}

		return matcher.group("upstreamBranchName");
	}

	private static final Pattern _jobNamePattern = Pattern.compile(
		"[^\\(]+\\((?<upstreamBranchName>[^_\\)]+)" +
			"(_(?<testSuiteName>[^\\)]+))?\\)");

}