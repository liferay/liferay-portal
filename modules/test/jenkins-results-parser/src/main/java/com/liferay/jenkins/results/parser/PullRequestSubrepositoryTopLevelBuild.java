/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Michael Hashimoto
 */
public class PullRequestSubrepositoryTopLevelBuild
	extends DefaultTopLevelBuild
	implements PluginsBranchInformationBuild, PortalBranchInformationBuild,
			   PullRequestBuild, WorkspaceBuild {

	public PullRequestSubrepositoryTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);
	}

	@Override
	public String getBranchName() {
		String branchName = getParameterValue("GITHUB_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(branchName)) {
			return branchName;
		}

		return getBranchName();
	}

	@Override
	public BranchInformation getPluginsBranchInformation() {
		Workspace workspace = getWorkspace();

		if (!(workspace instanceof SubrepositoryWorkspace)) {
			return null;
		}

		SubrepositoryWorkspace subrepositoryWorkspace =
			(SubrepositoryWorkspace)workspace;

		return new WorkspaceBranchInformation(
			subrepositoryWorkspace.getPluginsWorkspaceGitRepository());
	}

	@Override
	public BranchInformation getPortalBaseBranchInformation() {
		return null;
	}

	@Override
	public BranchInformation getPortalBranchInformation() {
		Workspace workspace = getWorkspace();

		if (!(workspace instanceof SubrepositoryWorkspace)) {
			return null;
		}

		SubrepositoryWorkspace subrepositoryWorkspace =
			(SubrepositoryWorkspace)workspace;

		return new WorkspaceBranchInformation(
			subrepositoryWorkspace.getPortalWorkspaceGitRepository());
	}

	public String getPortalUpstreamBranchName() {
		String portalUpstreamBranchName = getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			return portalUpstreamBranchName;
		}

		return getBranchName();
	}

	@Override
	public PullRequest getPullRequest() {
		if (_pullRequest != null) {
			return _pullRequest;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(getParameterValue("GITHUB_RECEIVER_USERNAME"));
		sb.append("/");
		sb.append(getParameterValue("REPOSITORY_NAME"));
		sb.append("/pull/");
		sb.append(getParameterValue("GITHUB_PULL_REQUEST_NUMBER"));

		_pullRequest = PullRequestFactory.newPullRequest(sb.toString());

		return _pullRequest;
	}

	@Override
	public Workspace getWorkspace() {
		PullRequest pullRequest = getPullRequest();

		Workspace workspace = WorkspaceFactory.newWorkspace(
			pullRequest.getGitRepositoryName(),
			pullRequest.getUpstreamRemoteGitBranchName(), getJobName());

		if (workspace instanceof SubrepositoryWorkspace) {
			SubrepositoryWorkspace subrepositoryWorkspace =
				(SubrepositoryWorkspace)workspace;

			subrepositoryWorkspace.setBuildProfile(getBuildProfile());
			subrepositoryWorkspace.setPortalUpstreamBranchName(
				getPortalUpstreamBranchName());
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

	private PullRequest _pullRequest;

}