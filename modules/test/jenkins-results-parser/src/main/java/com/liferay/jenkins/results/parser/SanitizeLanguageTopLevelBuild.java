/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Charlotte Wong
 */
public class SanitizeLanguageTopLevelBuild
	extends DefaultTopLevelBuild implements PortalWorkspaceBuild {

	public SanitizeLanguageTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);

		String receiverUsername = getParameterValue("GITHUB_RECEIVER_USERNAME");
		String pullRequestNumber = getParameterValue(
			"GITHUB_PULL_REQUEST_NUMBER");

		if (JenkinsResultsParserUtil.isNullOrEmpty(receiverUsername) ||
			JenkinsResultsParserUtil.isNullOrEmpty(pullRequestNumber) ||
			pullRequestNumber.equals("0")) {

			_pullRequest = null;
		}
		else {
			StringBuilder sb = new StringBuilder();

			sb.append("https://github.com/");
			sb.append(receiverUsername);
			sb.append("/liferay-portal");
			sb.append("/pull/");
			sb.append(pullRequestNumber);

			_pullRequest = PullRequestFactory.newPullRequest(sb.toString());
		}
	}

	@Override
	public String getBaseGitRepositoryName() {
		return "liferay-portal";
	}

	@Override
	public String getBranchName() {
		String upstreamBranchName = getParameterValue(
			"GITHUB_UPSTREAM_BRANCH_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			return "master";
		}

		return upstreamBranchName;
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		return Job.BuildProfile.DXP;
	}

	@Override
	public PortalWorkspace getPortalWorkspace() {
		Workspace workspace = getWorkspace();

		return (PortalWorkspace)workspace;
	}

	public PullRequest getPullRequest() {
		return _pullRequest;
	}

	@Override
	public String getTestSuiteName() {
		String ciTestSuite = getParameterValue("CI_TEST_SUITE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(ciTestSuite)) {
			ciTestSuite = "default";
		}

		return ciTestSuite;
	}

	@Override
	public Workspace getWorkspace() {
		Workspace workspace = WorkspaceFactory.newWorkspace(
			"liferay-portal", getBranchName(), "sanitize-language");

		if (workspace instanceof PortalWorkspace) {
			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			portalWorkspace.setBuildProfile(getBuildProfile());
		}

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		if (_pullRequest != null) {
			workspaceGitRepository.setGitHubURL(_pullRequest.getHtmlURL());
		}

		String senderBranchSHA = _getSenderBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			workspaceGitRepository.setSenderBranchSHA(senderBranchSHA);
		}

		String upstreamBranchSHA = _getUpstreamBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			workspaceGitRepository.setBaseBranchSHA(upstreamBranchSHA);
		}

		return workspace;
	}

	private String _getSenderBranchSHA() {
		String senderBranchSHA = getParameterValue("GITHUB_SENDER_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			return senderBranchSHA;
		}

		return null;
	}

	private String _getUpstreamBranchSHA() {
		String upstreamBranchSHA = getParameterValue(
			"GITHUB_UPSTREAM_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			return upstreamBranchSHA;
		}

		return null;
	}

	private final PullRequest _pullRequest;

}