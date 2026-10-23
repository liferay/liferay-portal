/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class MergePortalSubrepositoryUtil {

	public static void mergePortalSubrepository(
			URL jenkinsBuildURL, PullRequest portalPullRequest,
			URL subrepositoryGitHubURL, String subrepositoryUpstreamBranchName,
			String targetGitRepoCommitSHA)
		throws IOException {

		_onMergeStarted(jenkinsBuildURL, portalPullRequest);

		_checkPassingTestSuites(jenkinsBuildURL, portalPullRequest);

		GitWorkingDirectory portalGitWorkingDirectory = _getGitWorkingDirectory(
			jenkinsBuildURL, portalPullRequest, portalPullRequest.getBaseURL(),
			portalPullRequest.getUpstreamRemoteGitBranchName());

		String startingPortalCommitSHA =
			portalGitWorkingDirectory.getLatestCommitSHA();

		GitWorkingDirectory subrepositoryGitWorkingDirectory =
			_getGitWorkingDirectory(
				jenkinsBuildURL, portalPullRequest, subrepositoryGitHubURL,
				subrepositoryUpstreamBranchName);

		String currentGitRepoCommitSHA = _getCurrentGitRepoCommitSHA(
			jenkinsBuildURL, portalPullRequest, portalGitWorkingDirectory,
			subrepositoryGitWorkingDirectory, targetGitRepoCommitSHA);

		_fetchSubrepositoryBranchToPortalRepository(
			jenkinsBuildURL, portalPullRequest, portalGitWorkingDirectory,
			subrepositoryGitWorkingDirectory);

		_checkMergeCommitSHA(
			jenkinsBuildURL, portalPullRequest, currentGitRepoCommitSHA,
			targetGitRepoCommitSHA, portalGitWorkingDirectory,
			subrepositoryGitWorkingDirectory);

		_createAndApplyPatch(
			jenkinsBuildURL, portalPullRequest, portalGitWorkingDirectory,
			subrepositoryGitWorkingDirectory, currentGitRepoCommitSHA,
			targetGitRepoCommitSHA);

		_commitGitRepoUpdates(
			jenkinsBuildURL, portalPullRequest, portalGitWorkingDirectory,
			subrepositoryGitWorkingDirectory, startingPortalCommitSHA,
			targetGitRepoCommitSHA);

		_pushUpdatesToRemoteBranch(
			jenkinsBuildURL, portalPullRequest, portalGitWorkingDirectory);

		String endingPortalCommitSHA =
			portalGitWorkingDirectory.getLatestCommitSHA();

		_onMergeCompleted(
			jenkinsBuildURL, portalPullRequest, subrepositoryGitHubURL,
			targetGitRepoCommitSHA, startingPortalCommitSHA,
			endingPortalCommitSHA);
	}

	private static void _checkMergeCommitSHA(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		String currentGitRepoCommitSHA, String targetGitRepoCommitSHA,
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory) {

		if (Objects.equals(
				subrepositoryGitWorkingDirectory.getMergeBaseCommitSHA(
					currentGitRepoCommitSHA, targetGitRepoCommitSHA),
				currentGitRepoCommitSHA)) {

			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(
			_getGitHubURLString(
				portalPullRequest.getBaseURL(), targetGitRepoCommitSHA));
		sb.append(" is incompatible with sha found in '");
		sb.append(
			JenkinsResultsParserUtil.getPathRelativeTo(
				_getGitRepoFile(
					portalGitWorkingDirectory,
					subrepositoryGitWorkingDirectory),
				portalGitWorkingDirectory.getWorkingDirectory()));
		sb.append("'");

		_reportError(sb.toString(), jenkinsBuildURL, portalPullRequest);
	}

	private static void _checkPassingTestSuites(
		URL jenkinsBuildURL, PullRequest portalPullRequest) {

		List<String> requiredPassingTestSuiteNames = new ArrayList<>();

		Collections.addAll(requiredPassingTestSuiteNames, "relevant", "sf");

		requiredPassingTestSuiteNames.removeAll(
			portalPullRequest.getPassingTestSuiteNames());

		if (requiredPassingTestSuiteNames.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Skip merge subrepo because tests have not passed.\n");
		sb.append("<ul>\n");

		for (String requiredPassingTestSuiteName :
				requiredPassingTestSuiteNames) {

			sb.append("<li>ci:test:");
			sb.append(requiredPassingTestSuiteName);
			sb.append("</li>\n");
		}

		sb.append("</ul>\n");

		_reportError(sb.toString(), jenkinsBuildURL, portalPullRequest);
	}

	private static void _commitGitRepoUpdates(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory,
		String startingPortalCommitSHA, String targetGitRepoCommitSHA) {

		File gitRepoFile = _getGitRepoFile(
			portalGitWorkingDirectory, subrepositoryGitWorkingDirectory);

		String gitRepoFilePath = JenkinsResultsParserUtil.getPathRelativeTo(
			gitRepoFile, portalGitWorkingDirectory.getWorkingDirectory());

		try {
			String gitRepoFileContent = JenkinsResultsParserUtil.read(
				gitRepoFile);

			gitRepoFileContent = gitRepoFileContent.replaceAll(
				"commit = [0-9a-f]{40}", "commit = " + targetGitRepoCommitSHA);
			gitRepoFileContent = gitRepoFileContent.replaceAll(
				"parent = [0-9a-f]{40}", "parent = " + startingPortalCommitSHA);

			JenkinsResultsParserUtil.write(gitRepoFile, gitRepoFileContent);

			portalGitWorkingDirectory.stageFileInCurrentLocalGitBranch(
				JenkinsResultsParserUtil.getPathRelativeTo(
					gitRepoFile,
					portalGitWorkingDirectory.getWorkingDirectory()));

			portalGitWorkingDirectory.commitFileToCurrentBranch(
				gitRepoFilePath,
				"subrepo:ignore Update '" + gitRepoFilePath + "'.");
		}
		catch (IOException ioException) {
			_reportError(
				"Unable to update " + gitRepoFilePath, jenkinsBuildURL,
				portalPullRequest, ioException);
		}
	}

	private static void _createAndApplyPatch(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory, String gitRepoSHA,
		String targetGitRepoCommitSHA) {

		Set<File> modifiedFilesInCommitRange =
			subrepositoryGitWorkingDirectory.getModifiedFilesInCommitRange(
				gitRepoSHA, targetGitRepoCommitSHA);

		StringBuilder sb = new StringBuilder();

		sb.append("git format-patch --root \"");
		sb.append(gitRepoSHA);
		sb.append("..");
		sb.append(targetGitRepoCommitSHA);
		sb.append("\" -- ");

		boolean foundModifiedFiles = false;

		for (File modifiedFile : modifiedFilesInCommitRange) {
			String modifiedFilePath = JenkinsResultsParserUtil.getCanonicalPath(
				modifiedFile);

			if (modifiedFilePath.endsWith("gradle.properties") ||
				modifiedFilePath.endsWith("gradlew") ||
				modifiedFilePath.endsWith("gradlew.bat") ||
				modifiedFilePath.contains("gradle/")) {

				continue;
			}

			sb.append(" ");
			sb.append(
				JenkinsResultsParserUtil.getPathRelativeTo(
					modifiedFile,
					subrepositoryGitWorkingDirectory.getWorkingDirectory()));

			foundModifiedFiles = true;
		}

		if (!foundModifiedFiles) {
			_reportError(
				"No found modified files", jenkinsBuildURL, portalPullRequest);

			return;
		}

		GitUtil.ExecutionResult executionResult =
			portalGitWorkingDirectory.executeBashCommands(
				3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10, "rm -f *.patch",
				sb.toString(), "(git am --abort || true)",
				JenkinsResultsParserUtil.combine(
					"git am --directory=\"",
					_getSubrepositoryModuleDirPath(
						portalGitWorkingDirectory,
						subrepositoryGitWorkingDirectory),
					"\" --keep-cr --whitespace=nowarn *.patch"));

		if (executionResult.getExitValue() != 0) {
			throw new RuntimeException(
				"Unable to create & apply the patch \n" +
					executionResult.getStandardError());
		}

		System.out.println(executionResult.getStandardOut());
	}

	private static void _fetchSubrepositoryBranchToPortalRepository(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory) {

		try {
			portalGitWorkingDirectory.fetchLocalGitBranch(
				null,
				subrepositoryGitWorkingDirectory.getCurrentLocalGitBranch());
		}
		catch (Exception exception) {
			File subrepositoryWorkingDirectory =
				subrepositoryGitWorkingDirectory.getWorkingDirectory();

			_reportError(
				"Unable to fetch from " + subrepositoryWorkingDirectory,
				jenkinsBuildURL, portalPullRequest, exception);
		}
	}

	private static String _getCurrentGitRepoCommitSHA(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory,
		String targetGitRepoCommitSHA) {

		File gitRepoFile = _getGitRepoFile(
			portalGitWorkingDirectory, subrepositoryGitWorkingDirectory);

		Properties gitRepoProperties = JenkinsResultsParserUtil.getProperties(
			gitRepoFile);

		String currentGitRepoCommitSHA = gitRepoProperties.getProperty(
			"commit");

		if (Objects.equals(currentGitRepoCommitSHA, targetGitRepoCommitSHA)) {
			StringBuilder sb = new StringBuilder();

			sb.append(
				_getGitHubURLString(
					portalPullRequest.getBaseURL(), targetGitRepoCommitSHA));
			sb.append(" already found in '");
			sb.append(
				JenkinsResultsParserUtil.getPathRelativeTo(
					gitRepoFile,
					portalGitWorkingDirectory.getWorkingDirectory()));
			sb.append("'");

			_reportError(sb.toString(), jenkinsBuildURL, portalPullRequest);

			return null;
		}

		return currentGitRepoCommitSHA;
	}

	private static String _getGitHubURLString(
		URL gitHubURL, String gitBranchSHA) {

		StringBuilder sb = new StringBuilder();

		Matcher matcher = _gitHubURLPattern.matcher(String.valueOf(gitHubURL));

		if (matcher.find()) {
			sb.append(matcher.group("userName"));
			sb.append("/");
			sb.append(matcher.group("repositoryName"));
			sb.append("/");
			sb.append(matcher.group("branchName"));
		}
		else {
			sb.append(gitHubURL);
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitBranchSHA)) {
			sb.append(" (");
			sb.append(gitBranchSHA.substring(0, 7));
			sb.append(")");
		}

		return sb.toString();
	}

	private static File _getGitRepoFile(
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory) {

		GitRemote subrepositoryUpstreamGitRemote =
			subrepositoryGitWorkingDirectory.getUpstreamGitRemote();

		Set<File> gitRepoFiles = portalGitWorkingDirectory.findFiles(
			".gitrepo", subrepositoryUpstreamGitRemote.getRemoteURL());

		for (File gitRepoFile : gitRepoFiles) {
			return gitRepoFile;
		}

		return null;
	}

	private static GitWorkingDirectory _getGitWorkingDirectory(
			URL jenkinsBuildURL, PullRequest portalPullRequest, URL gitHubURL,
			String upstreamBranchName)
		throws IOException {

		Matcher gitHubURLMatcher = _gitHubURLPattern.matcher(
			String.valueOf(gitHubURL));

		if (!gitHubURLMatcher.find()) {
			_reportError(
				"Invalid GitHub URL " + gitHubURL, jenkinsBuildURL,
				portalPullRequest);

			return null;
		}

		String baseRepositoryDirPath =
			JenkinsResultsParserUtil.getBuildProperty("base.repository.dir");

		String repositoryName = gitHubURLMatcher.group("repositoryName");

		String repositoryDirPath = JenkinsResultsParserUtil.combine(
			baseRepositoryDirPath, "/", repositoryName);

		if (repositoryName.equals("liferay-portal-ee") &&
			upstreamBranchName.matches("\\d+\\.\\d+\\.x")) {

			repositoryDirPath = JenkinsResultsParserUtil.combine(
				baseRepositoryDirPath, "/liferay-portal-", upstreamBranchName);
		}

		return GitWorkingDirectoryFactory.newGitWorkingDirectory(
			upstreamBranchName, repositoryDirPath, repositoryName);
	}

	private static String _getPullRequestLink(PullRequest portalPullRequest) {
		StringBuilder sb = new StringBuilder();

		sb.append("<a href=\"");
		sb.append(portalPullRequest.getHtmlURL());
		sb.append("\">");
		sb.append(portalPullRequest.getReceiverUsername());
		sb.append("#");
		sb.append(portalPullRequest.getNumber());
		sb.append("</a>");

		return sb.toString();
	}

	private static String _getSubrepositoryModuleDirPath(
		GitWorkingDirectory portalGitWorkingDirectory,
		GitWorkingDirectory subrepositoryGitWorkingDirectory) {

		String relativeFilePath = JenkinsResultsParserUtil.getPathRelativeTo(
			_getGitRepoFile(
				portalGitWorkingDirectory, subrepositoryGitWorkingDirectory),
			portalGitWorkingDirectory.getWorkingDirectory());

		return relativeFilePath.replaceAll("/\\.gitrepo", "");
	}

	private static void _onMergeCompleted(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		URL subrepositoryGitHubURL, String targetGitRepoCommitSHA,
		String startingPortalCommitSHA, String endingPortalCommitSHA) {

		Matcher matcher = _gitHubURLPattern.matcher(
			String.valueOf(subrepositoryGitHubURL));

		if (!matcher.find()) {
			_reportError(
				"Invalid subrepository GitHub URL " + subrepositoryGitHubURL,
				jenkinsBuildURL, portalPullRequest);

			return;
		}

		String message = JenkinsResultsParserUtil.combine(
			"Completed subrepo merge process at ",
			JenkinsResultsParserUtil.toDateString(new Date()), ".\n",
			"All commits have been successfully pulled.\n",
			"Diff URL: <a href=\"https://github.com/",
			portalPullRequest.getReceiverUsername(), "/",
			portalPullRequest.getGitRepositoryName(), "/compare/",
			startingPortalCommitSHA, "...", endingPortalCommitSHA, "\">",
			startingPortalCommitSHA.substring(0, 7), "...",
			endingPortalCommitSHA.substring(0, 7), "</a>");

		JenkinsResultsParserUtil.updateBuildDescription(
			message, jenkinsBuildURL);

		PullRequest.Comment pullRequestComment = portalPullRequest.addComment(
			JenkinsResultsParserUtil.combine(
				message, "\n\nFor more details click <a href=\"",
				String.valueOf(jenkinsBuildURL), "\">here</a>."));

		GitHubRemoteGitCommit gitHubRemoteGitCommit =
			GitCommitFactory.newGitHubRemoteGitCommit(
				matcher.group("userName"), matcher.group("repositoryName"),
				targetGitRepoCommitSHA);

		gitHubRemoteGitCommit.setStatus(
			GitHubRemoteGitCommit.Status.SUCCESS, "liferay/merged-into-central",
			JenkinsResultsParserUtil.combine(
				"Merged into ", portalPullRequest.getReceiverUsername(), "/",
				portalPullRequest.getGitRepositoryName()),
			String.valueOf(pullRequestComment.getURL()));

		portalPullRequest.close();
	}

	private static void _onMergeStarted(
		URL jenkinsBuildURL, PullRequest portalPullRequest) {

		portalPullRequest.addComment(
			JenkinsResultsParserUtil.combine(
				"Started subrepo merge process <a href=\"",
				String.valueOf(jenkinsBuildURL), "\">here</a> at ",
				JenkinsResultsParserUtil.toDateString(new Date()), "."));

		JenkinsResultsParserUtil.updateBuildDescription(
			_getPullRequestLink(portalPullRequest), jenkinsBuildURL);
	}

	private static void _pushUpdatesToRemoteBranch(
		URL jenkinsBuildURL, PullRequest portalPullRequest,
		GitWorkingDirectory portalGitWorkingDirectory) {

		URL portalBaseURL = portalPullRequest.getBaseURL();

		Matcher gitHubURLMatcher = _gitHubURLPattern.matcher(
			String.valueOf(portalBaseURL));

		if (!gitHubURLMatcher.find()) {
			_reportError(
				"Invalid GitHub URL " + portalBaseURL, jenkinsBuildURL,
				portalPullRequest);

			return;
		}

		LocalGitBranch currentLocalGitBranch =
			portalGitWorkingDirectory.getCurrentLocalGitBranch();
		String remoteURL = JenkinsResultsParserUtil.combine(
			"git@github.com:", gitHubURLMatcher.group("userName"), "/",
			gitHubURLMatcher.group("repositoryName"), ".git");
		String remoteGitBranchName = gitHubURLMatcher.group("branchName");

		Retryable<Void> retryable = new Retryable<Void>() {

			@Override
			public Void execute() {
				RemoteGitBranch remoteGitBranch =
					portalGitWorkingDirectory.pushToRemoteGitRepository(
						false, currentLocalGitBranch, remoteGitBranchName,
						remoteURL);

				if (remoteGitBranch == null) {
					throw new RuntimeException(
						"Unable to push updates to " + remoteURL);
				}

				return null;
			}

		};

		try {
			retryable.executeWithRetries();
		}
		catch (Exception exception) {
			_reportError(
				"Unable to push updates to " + remoteURL, jenkinsBuildURL,
				portalPullRequest, exception);
		}
	}

	private static void _reportError(
		String errorMessage, URL jenkinsBuildURL,
		PullRequest portalPullRequest) {

		_reportError(errorMessage, jenkinsBuildURL, portalPullRequest, null);
	}

	private static void _reportError(
		String errorMessage, URL jenkinsBuildURL, PullRequest portalPullRequest,
		Exception exception) {

		System.out.println(errorMessage);

		JenkinsResultsParserUtil.updateBuildDescription(
			errorMessage, jenkinsBuildURL);

		portalPullRequest.addComment(
			JenkinsResultsParserUtil.combine(
				errorMessage, "\n\nFor more details click <a href=\"",
				String.valueOf(jenkinsBuildURL), "\">here</a>."));

		if (exception != null) {
			throw new RuntimeException(errorMessage, exception);
		}

		throw new RuntimeException(errorMessage);
	}

	private static final Pattern _gitHubURLPattern = Pattern.compile(
		"https://github.com/(?<userName>[^/]+)/(?<repositoryName>[^/]+)/tree/" +
			"(?<branchName>[^/]+)");

}