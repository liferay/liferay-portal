/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Charlotte Wong
 */
public class DockerReleaseTopLevelBuild
	extends DefaultTopLevelBuild implements WorkspaceBuild {

	public DockerReleaseTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(getParameterValue("GITHUB_RECEIVER_USERNAME"));
		sb.append("/liferay-docker/pull/");
		sb.append(getParameterValue("GITHUB_PULL_REQUEST_NUMBER"));

		_pullRequest = PullRequestFactory.newPullRequest(sb.toString());
	}

	@Override
	public String getBaseGitRepositoryName() {
		return "liferay-docker";
	}

	@Override
	public String getBranchName() {
		return getParameterValue("GITHUB_UPSTREAM_BRANCH_NAME");
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		return Job.BuildProfile.DXP;
	}

	public PullRequest getPullRequest() {
		return _pullRequest;
	}

	@Override
	public Workspace getWorkspace() {
		PullRequest pullRequest = getPullRequest();

		Workspace workspace = WorkspaceFactory.newWorkspace(
			"liferay-docker", getParameterValue("GITHUB_UPSTREAM_BRANCH_NAME"),
			"test-docker-release-pullrequest");

		if (workspace instanceof PortalWorkspace) {
			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			portalWorkspace.setBuildProfile(getBuildProfile());
		}

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		String upstreamBranchSHA = _getUpstreamBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			workspaceGitRepository.setBaseBranchSHA(upstreamBranchSHA);
		}

		workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

		String senderBranchSHA = _getSenderBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			workspaceGitRepository.setSenderBranchSHA(senderBranchSHA);
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