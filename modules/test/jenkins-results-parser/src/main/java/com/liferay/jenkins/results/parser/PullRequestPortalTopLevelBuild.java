/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.dom4j.Element;

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

	public String getStableJobResult() {
		if (_stableJobResult != null) {
			return _stableJobResult;
		}

		List<Build> stableJobDownstreamBuilds = getStableJobDownstreamBuilds();

		int stableJobDownstreamBuildsSize = stableJobDownstreamBuilds.size();

		if (stableJobDownstreamBuildsSize == 0) {
			return null;
		}

		String result = getResult();

		if (result == null) {
			return null;
		}

		if (result.equals("SUCCESS")) {
			_stableJobResult = result;
		}

		Job stableJob = _getStableJob();

		if (stableJob == null) {
			return null;
		}

		List<String> stableJobBatchNames = new ArrayList<>(
			stableJob.getBatchNames());

		int stableJobDownstreamBuildsCompletedCount =
			getJobVariantsDownstreamBuildCount(
				stableJobBatchNames, null, "completed");

		if (stableJobDownstreamBuildsCompletedCount !=
				stableJobDownstreamBuildsSize) {

			return null;
		}

		int stableJobDownstreamBuildsSuccessCount =
			getJobVariantsDownstreamBuildCount(
				stableJobBatchNames, "SUCCESS", null);

		if (stableJobDownstreamBuildsSuccessCount ==
				stableJobDownstreamBuildsSize) {

			_stableJobResult = "SUCCESS";
		}
		else {
			_stableJobResult = "FAILURE";
		}

		return _stableJobResult;
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
		}

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

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

	protected Element getFailedStableJobSummaryElement() {
		Job stableJob = _getStableJob();

		if (stableJob == null) {
			return Dom4JUtil.getNewElement("span");
		}

		List<String> stableJobBatchNames = new ArrayList<>(
			stableJob.getBatchNames());

		Element jobSummaryListElement = getJobSummaryListElement(
			false, stableJobBatchNames);

		int stableJobDownstreamBuildsSuccessCount =
			getJobVariantsDownstreamBuildCount(
				stableJobBatchNames, "SUCCESS", null);

		int stableJobDownstreamBuildsCount = getJobVariantsDownstreamBuildCount(
			stableJobBatchNames, null, null);

		int stableJobDownstreamBuildsFailureCount =
			stableJobDownstreamBuildsCount -
				stableJobDownstreamBuildsSuccessCount;

		return Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"h4", null,
				String.valueOf(stableJobDownstreamBuildsFailureCount),
				" Failed Jobs:"),
			jobSummaryListElement);
	}

	protected List<Build> getStableJobDownstreamBuilds() {
		Job stableJob = _getStableJob();

		if (stableJob != null) {
			return getJobVariantsDownstreamBuilds(
				stableJob.getBatchNames(), null, null);
		}

		return Collections.emptyList();
	}

	protected Element getStableJobResultElement() {
		Job stableJob = _getStableJob();

		if (stableJob == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		String stableJobResult = getStableJobResult();

		if ((stableJobResult != null) && stableJobResult.equals("SUCCESS")) {
			sb.append(":heavy_check_mark: ");
		}
		else {
			sb.append(":x: ");
		}

		sb.append("ci:test:stable - ");

		sb.append(
			getJobVariantsDownstreamBuildCount(
				new ArrayList<>(stableJob.getBatchNames()), "SUCCESS", null));

		sb.append(" out of ");

		List<Build> stableJobDownstreamBuilds = getStableJobDownstreamBuilds();

		sb.append(stableJobDownstreamBuilds.size());

		sb.append(" jobs passed");

		return Dom4JUtil.getNewElement("h3", null, sb.toString());
	}

	protected Element getStableJobSuccessSummaryElement() {
		Job stableJob = _getStableJob();

		if (stableJob == null) {
			return Dom4JUtil.getNewElement("span");
		}

		List<String> stableJobBatchNames = new ArrayList<>(
			stableJob.getBatchNames());

		Element stableJobSummaryListElement = getJobSummaryListElement(
			true, stableJobBatchNames);

		int stableJobDownstreamBuildsSuccessCount =
			getJobVariantsDownstreamBuildCount(
				stableJobBatchNames, "SUCCESS", null);

		return Dom4JUtil.getNewElement(
			"details", null,
			Dom4JUtil.getNewElement(
				"summary", null,
				Dom4JUtil.getNewElement(
					"strong", null,
					String.valueOf(stableJobDownstreamBuildsSuccessCount),
					" Successful Jobs:")),
			stableJobSummaryListElement);
	}

	protected Element getStableJobSummaryElement() {
		Job stableJob = _getStableJob();

		if (stableJob == null) {
			return Dom4JUtil.getNewElement("span");
		}

		List<String> stableJobBatchNames = new ArrayList<>(
			stableJob.getBatchNames());

		int stableJobDownstreamBuildSuccessCount =
			getJobVariantsDownstreamBuildCount(
				stableJobBatchNames, "SUCCESS", null);

		List<Build> stableJobDownstreamBuilds = getStableJobDownstreamBuilds();

		Element detailsElement = Dom4JUtil.getNewElement(
			"details", null,
			Dom4JUtil.getNewElement(
				"summary", null,
				Dom4JUtil.getNewElement(
					"strong", null, "ci:test:stable - ",
					String.valueOf(stableJobDownstreamBuildSuccessCount),
					" out of ",
					String.valueOf(stableJobDownstreamBuilds.size()),
					" jobs PASSED")));

		int stableJobDownstreamBuildCount = getJobVariantsDownstreamBuildCount(
			stableJobBatchNames, null, null);

		if (stableJobDownstreamBuildSuccessCount <
				stableJobDownstreamBuildCount) {

			Dom4JUtil.addToElement(
				detailsElement, getFailedStableJobSummaryElement());
		}

		if (stableJobDownstreamBuildSuccessCount > 0) {
			Dom4JUtil.addToElement(
				detailsElement, getStableJobSuccessSummaryElement());
		}

		return detailsElement;
	}

	@Override
	protected Element getTopGitHubMessageElement() {
		Element rootElement = super.getTopGitHubMessageElement();

		List<Build> stableJobDownstreamBuilds = new ArrayList<>();

		Job stableJob = _getStableJob();

		if (stableJob != null) {
			stableJobDownstreamBuilds.addAll(getStableJobDownstreamBuilds());
		}

		if (!stableJobDownstreamBuilds.isEmpty()) {
			Dom4JUtil.insertElementAfter(
				rootElement, null, getStableJobResultElement());
		}

		Element detailsElement = rootElement.element("details");

		if (!stableJobDownstreamBuilds.isEmpty()) {
			Element jobSummaryElement = detailsElement.element("details");

			Dom4JUtil.insertElementBefore(
				detailsElement, jobSummaryElement,
				getStableJobSummaryElement());
		}

		return rootElement;
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

	private synchronized Job _getStableJob() {
		if (_stableJob != null) {
			return _stableJob;
		}

		String testSuiteName = getTestSuiteName();

		if (!testSuiteName.equals("relevant")) {
			return null;
		}

		String branchName = getBranchName();
		Job.BuildProfile buildProfile = getBuildProfile();
		String jobName = getJobName();
		String repositoryName = getBaseGitRepositoryName();
		String stableTestSuiteName = "stable";

		try {
			_stableJob = JobFactory.newJob(
				buildProfile, jobName, null, null, null, branchName, null,
				repositoryName, stableTestSuiteName, branchName);

			BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

			buildDatabase.putJob(
				JobFactory.getKey(
					buildProfile, jobName, null, branchName, null,
					repositoryName, stableTestSuiteName, branchName),
				_stableJob);
		}
		catch (Exception exception) {
			System.out.println("Unable to create stable job for " + jobName);

			exception.printStackTrace();
		}

		return _stableJob;
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
	private Job _stableJob;
	private String _stableJobResult;

}