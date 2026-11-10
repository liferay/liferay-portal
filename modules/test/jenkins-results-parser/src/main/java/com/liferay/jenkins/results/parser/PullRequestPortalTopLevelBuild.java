/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.URL;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Peter Yoo
 */
public class PullRequestPortalTopLevelBuild
	extends PortalTopLevelBuild
	implements PortalWorkspaceBuild, PullRequestBuild {

	public PullRequestPortalTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);

		setCompareToUpstream(true);

		String testSuiteName = getTestSuiteName();

		if (testSuiteName.equals("stable")) {
			setCompareToUpstream(false);
		}
	}

	public boolean bypassCITestRelevant() {
		String testSuiteName = getTestSuiteName();

		if ((testSuiteName == null) || !testSuiteName.equals("relevant")) {
			return false;
		}

		Workspace workspace = getWorkspace();

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		if (!(workspaceGitRepository instanceof PortalWorkspaceGitRepository)) {
			return false;
		}

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			(PortalWorkspaceGitRepository)workspaceGitRepository;

		return portalWorkspaceGitRepository.bypassCITestRelevant();
	}

	@Override
	public String getBranchName() {
		String branchName = getParameterValue("GITHUB_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(branchName)) {
			return branchName;
		}

		return super.getBranchName();
	}

	public String getPortalUpstreamBranchName() {
		String portalUpstreamBranchName = getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			return portalUpstreamBranchName;
		}

		return null;
	}

	@Override
	public PortalWorkspace getPortalWorkspace() {
		Workspace workspace = getWorkspace();

		if (!(workspace instanceof PortalWorkspace)) {
			return null;
		}

		return (PortalWorkspace)workspace;
	}

	@Override
	public PullRequest getPullRequest() {
		if (_pullRequest != null) {
			return _pullRequest;
		}

		String pullRequestURL = getParameterValue("PULL_REQUEST_URL");

		if (pullRequestURL == null) {
			StringBuilder sb = new StringBuilder();

			sb.append("https://github.com/");
			sb.append(getParameterValue("GITHUB_RECEIVER_USERNAME"));
			sb.append("/");
			sb.append(getBaseGitRepositoryName());
			sb.append("/pull/");
			sb.append(getParameterValue("GITHUB_PULL_REQUEST_NUMBER"));

			pullRequestURL = sb.toString();
		}

		_pullRequest = PullRequestFactory.newPullRequest(pullRequestURL, this);

		return _pullRequest;
	}

	@Override
	public String getResult() {
		List<Build> downstreamBuildFailures = getFailedDownstreamBuilds();

		if (downstreamBuildFailures.isEmpty()) {
			return super.getResult();
		}

		Properties buildProperties;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		boolean pullRequestForwardUpstreamFailureComparisonEnabled =
			Boolean.parseBoolean(
				buildProperties.getProperty(
					"pull.request.forward.upstream.failure.comparison." +
						"enabled"));

		String result = "FAILURE";

		if (!pullRequestForwardUpstreamFailureComparisonEnabled ||
			!isCompareToUpstream()) {

			return result;
		}

		String testSuiteName = getTestSuiteName();

		if (!testSuiteName.matches("relevant|stable")) {
			return result;
		}

		String batchWhitelist = buildProperties.getProperty(
			"pull.request.forward.upstream.failure.comparison.batch.whitelist");

		List<String> whitelistedBatchRegexes = Arrays.asList(
			batchWhitelist.split("\\s*,\\s*"));

		for (Build downstreamBuild : downstreamBuildFailures) {
			if (downstreamBuild.isUniqueFailure()) {
				return result;
			}

			boolean approved = false;

			String jobVariant = downstreamBuild.getJobVariant();

			jobVariant = jobVariant.replaceAll("(.*)/.*", "$1");

			for (String whiteListedBatchRegex : whitelistedBatchRegexes) {
				if (jobVariant.matches(".*" + whiteListedBatchRegex + ".*")) {
					approved = true;

					break;
				}
			}

			if (!approved) {
				return result;
			}
		}

		return "APPROVED";
	}

	@Override
	public Workspace getWorkspace() {
		PullRequest pullRequest = getPullRequest();

		Workspace workspace = WorkspaceFactory.newWorkspace(
			pullRequest.getGitRepositoryName(),
			pullRequest.getUpstreamRemoteGitBranchName(), getJobName());

		if (workspace instanceof PortalWorkspace) {
			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			portalWorkspace.setBuildProfile(getBuildProfile());
			portalWorkspace.setOSBAsahGitHubURL(_getOSBAsahGitHubURL());
			portalWorkspace.setOSBFaroGitHubURL(_getOSBFaroGitHubURL());
			portalWorkspace.setPortalUpstreamBranchName(
				getPortalUpstreamBranchName());
		}

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		String upstreamBranchSHA = _getUpstreamBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			workspaceGitRepository.setBaseBranchSHA(upstreamBranchSHA);
		}

		String upstreamBranchUsername = getParameterValue(
			"GITHUB_UPSTREAM_BRANCH_USERNAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchUsername)) {
			workspaceGitRepository.setBaseBranchUsername(
				upstreamBranchUsername);
		}

		workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

		String senderBranchSHA = _getSenderBranchSHA();

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			workspaceGitRepository.setSenderBranchSHA(senderBranchSHA);
		}

		return workspace;
	}

	@Override
	public boolean isUniqueFailure() {
		List<Build> failedDownstreamBuilds = getFailedDownstreamBuilds();

		for (Build downstreamBuild : failedDownstreamBuilds) {
			if (downstreamBuild.isUniqueFailure()) {
				return true;
			}
		}

		return failedDownstreamBuilds.isEmpty();
	}

	private String _getOSBAsahGitHubURL() {
		String osbAsahGitHubURL = getParameterValue("OSB_ASAH_GITHUB_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(osbAsahGitHubURL)) {
			return osbAsahGitHubURL;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			return controllerBuild.getParameterValue("OSB_ASAH_GITHUB_URL");
		}

		return null;
	}

	private String _getOSBFaroGitHubURL() {
		String osbFaroGitHubURL = getParameterValue("OSB_FARO_GITHUB_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(osbFaroGitHubURL)) {
			return osbFaroGitHubURL;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			osbFaroGitHubURL = controllerBuild.getParameterValue(
				"OSB_FARO_GITHUB_URL");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(osbFaroGitHubURL)) {
				return osbFaroGitHubURL;
			}
		}

		return "https://github.com/liferay/liferay-portal/tree/master";
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

		String portalBundlesDistURL = getParameterValue(
			"PORTAL_BUNDLES_DIST_URL");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBundlesDistURL)) {
			return null;
		}

		if (portalBundlesDistURL.startsWith("s3://")) {
			return JenkinsResultsParserUtil.getPortalBundleGitCommit(
				portalBundlesDistURL);
		}

		try {
			URL portalBundlesGitHashURL = new URL(
				JenkinsResultsParserUtil.getLocalURL(portalBundlesDistURL) +
					"/git-hash");

			if (!JenkinsResultsParserUtil.exists(portalBundlesGitHashURL)) {
				return null;
			}

			String portalBundlesGitHash = JenkinsResultsParserUtil.toString(
				portalBundlesGitHashURL.toString());

			portalBundlesGitHash = portalBundlesGitHash.trim();

			if (JenkinsResultsParserUtil.isSHA(portalBundlesGitHash)) {
				return portalBundlesGitHash;
			}

			return null;
		}
		catch (IOException ioException) {
			return null;
		}
	}

	private PullRequest _pullRequest;

}