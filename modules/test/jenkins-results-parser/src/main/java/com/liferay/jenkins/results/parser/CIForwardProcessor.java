/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public class CIForwardProcessor {

	public CIForwardProcessor(
		String consoleLogURL, boolean force, File gitRepositoryDir,
		PullRequest pullRequest, String recipientUsername) {

		_consoleLogURL = consoleLogURL;
		_force = force;

		if (gitRepositoryDir == null) {
			throw new IllegalArgumentException(
				"Git repository directory is null");
		}

		if (!gitRepositoryDir.exists()) {
			throw new IllegalArgumentException(
				"Git repository directory does not exist");
		}

		_gitRepositoryDir = gitRepositoryDir;

		if (pullRequest == null) {
			throw new IllegalArgumentException("Pull request is null");
		}

		String pullRequestState = pullRequest.getState();

		if (pullRequestState.equals("closed")) {
			throw new IllegalArgumentException("Pull request is closed");
		}

		_pullRequest = pullRequest;

		if (JenkinsResultsParserUtil.isNullOrEmpty(recipientUsername)) {
			throw new IllegalArgumentException("Recipient username is null");
		}

		_recipientUsername = recipientUsername;
	}

	public void execute() {
		String forwardedPullRequestURL = null;

		try {
			if (_isForwardCanceled()) {
				return;
			}

			List<String> openForwardedPullRequestUrls =
				_getOpenForwardedPullRequestUrls();

			if (!openForwardedPullRequestUrls.isEmpty()) {
				_pullRequest.addComment(
					_getHasOpenForwardedPullRequestCommentBody(
						openForwardedPullRequestUrls));

				return;
			}

			if (!_isForwardEligible()) {
				_pullRequest.addComment(_getUnsuccessfulCommentBody());

				return;
			}

			if (_hasMergeConflict()) {
				_pullRequest.addComment(_getMergeConflictCommentBody());

				return;
			}

			_pullRequest.addComment(_getPassedCommentBody());

			final String senderUsername;

			try {
				senderUsername = JenkinsResultsParserUtil.getBuildProperty(
					"github.ci.username");
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to get build property", ioException);
			}

			final String initialComment =
				_getCIForwardPullRequestInitialComment();

			Retryable<String> retryable = new Retryable<String>(
				true, 3, (int)(_RETRY_PERIOD / 1000), true) {

				@Override
				public String execute() {
					try {
						String pullRequestURL = _pullRequest.forward(
							_getCIForwardCommentBody(initialComment),
							_consoleLogURL, _recipientUsername,
							_getCIForwardBranchName(), senderUsername,
							_gitRepositoryDir);

						String forwardLabel = "ci:forward";

						if (_force) {
							forwardLabel = "ci:forward:force";
						}

						GitHubRemoteGitRepository gitHubRemoteGitRepository =
							_pullRequest.getGitHubRemoteGitRepository();

						gitHubRemoteGitRepository.addLabel(
							"bcf5db", "", forwardLabel);

						GitHubRemoteGitRepository.Label ciForwardForceLabel =
							gitHubRemoteGitRepository.getLabel(forwardLabel);

						_pullRequest.addLabel(ciForwardForceLabel);

						_pullRequest.close();

						return pullRequestURL;
					}
					catch (PullRequest.ForwardPullRequestException
								forwardPullRequestException) {

						if (!forwardPullRequestException.isRetryable()) {
							breakLoop();
						}

						throw new RuntimeException(forwardPullRequestException);
					}
					catch (Exception exception) {
						if (exception instanceof RuntimeException) {
							throw (RuntimeException)exception;
						}

						throw new RuntimeException(exception);
					}
				}

				@Override
				protected String getRetryMessage(int retryCount) {
					if (retryCount < maxRetries) {
						_pullRequest.addComment(_getRetryCommentBody());
					}

					return null;
				}

			};

			try {
				forwardedPullRequestURL = retryable.executeWithRetries();
			}
			catch (GitHubSecondaryRateLimitRuntimeException
						gitHubSecondaryRateLimitRuntimeException) {

				StringBuilder sb = new StringBuilder();

				sb.append("Secondary rate limit exceeded\n");
				sb.append("Pull Request URL: ");
				sb.append(_pullRequest.getURL());
				sb.append("\nConsole log URL: ");
				sb.append(_consoleLogURL);

				NotificationUtil.sendSlackNotification(
					sb.toString(), "#ci-notifications", ":liferay-ci:",
					"Unable to forward pull request", "Liferay CI");

				throw new GitHubSecondaryRateLimitRuntimeException(
					gitHubSecondaryRateLimitRuntimeException.getGitHubAPIURL(),
					gitHubSecondaryRateLimitRuntimeException.
						getRetryAfterSeconds(),
					sb.toString(), gitHubSecondaryRateLimitRuntimeException);
			}
			catch (Exception exception) {
				Throwable throwable = exception.getCause();

				if (throwable instanceof
						PullRequest.ForwardPullRequestException) {

					_pullRequest.addComment(throwable.getMessage());

					return;
				}

				exception.printStackTrace();

				StringBuilder sb = new StringBuilder();

				sb.append("Unknown exception\n");
				sb.append("Pull Request URL: ");
				sb.append(_pullRequest.getURL());
				sb.append("\nConsole log URL: ");
				sb.append(_consoleLogURL);

				NotificationUtil.sendSlackNotification(
					sb.toString(), "#ci-notifications", ":liferay-ci:",
					"Unable to forward pull request", "Liferay CI");

				_pullRequest.addComment(_getFailureCommentBody());
			}
		}
		catch (Exception exception) {
			try {
				_pullRequest.addComment(_getUnsuccessfulCommentBody());
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to post failure comment", ioException);
			}

			throw new RuntimeException(
				"Unable to forward pull request", exception);
		}

		PullRequest forwardedPullRequest = new PullRequest(
			forwardedPullRequestURL);

		try {
			for (PullRequest.Comment comment :
					_getSuiteTestResultGitHubComments()) {

				forwardedPullRequest.addComment(comment.getBody());
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		_pullRequest.addComment(
			_getSuccessCommentBody(forwardedPullRequestURL));
		_pullRequest.copyLabelsToPullRequest(forwardedPullRequest);
		_pullRequest.copyStatusesToPullRequest(forwardedPullRequest);
	}

	private PullRequest.Comment _findMostRecentTestResultComment(
		String testSuiteName, boolean requiredPassing,
		List<PullRequest.Comment> comments) {

		StringBuilder sb = new StringBuilder();

		if (requiredPassing) {
			sb.append("heavy_check_mark: ci:test:");
		}
		else {
			sb.append(": ci:test:");
		}

		sb.append(testSuiteName);

		String testSuiteString = sb.toString();

		for (PullRequest.Comment comment : comments) {
			String commentBody = comment.getBody();

			if (commentBody.contains(testSuiteString)) {
				return comment;
			}
		}

		return null;
	}

	private String[] _getBuildPropertyAsArray(String propertyName)
		throws IOException {

		String propertyValue = JenkinsResultsParserUtil.getProperty(
			JenkinsResultsParserUtil.getBuildProperties(), propertyName,
			_pullRequest.getGitRepositoryName(), _pullRequest.getRefName());

		if (JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {
			return new String[0];
		}

		return propertyValue.split("\\s*,\\s*");
	}

	private String _getCanceledCommentBody(String reason) {
		StringBuilder sb = new StringBuilder();

		sb.append("Pull request will not be forwarded to `");
		sb.append(_recipientUsername);
		sb.append("` because ");
		sb.append(reason);
		sb.append(".\n[Console](");
		sb.append(_consoleLogURL);
		sb.append(")\n");

		return sb.toString();
	}

	private String _getCIForwardBranchName() throws IOException {
		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getBuildProperty(
				"ci.forward.branch.name.prefix"),
			_pullRequest.getSenderBranchName(), "-pr-",
			_pullRequest.getNumber(), "-sender-",
			_pullRequest.getSenderUsername());
	}

	private String _getCIForwardCommentBody(String initialComment) {
		StringBuilder sb = new StringBuilder();

		sb.append("Forwarded from: ");
		sb.append(_pullRequest.getURL());

		int forwardAttempts = 0;
		Date firstForwardAttemptDate = null;

		for (PullRequest.Comment comment : _pullRequest.getComments()) {
			String commentBody = comment.getBody();

			if ((commentBody == null) ||
				!commentBody.startsWith("ci:forward")) {

				continue;
			}

			forwardAttempts++;

			if (firstForwardAttemptDate == null) {
				firstForwardAttemptDate = comment.getCreatedDate();
			}
		}

		String duration = null;

		if (firstForwardAttemptDate != null) {
			duration = JenkinsResultsParserUtil.toDurationString(
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					firstForwardAttemptDate.getTime());
		}

		if (forwardAttempts > 0) {
			sb.append(" (Took ");
			sb.append(forwardAttempts);
			sb.append(" `ci:forward` ");
			sb.append(
				JenkinsResultsParserUtil.getNounForm(
					forwardAttempts, "attempts", "attempt"));
			sb.append(" in ");
			sb.append(duration);
			sb.append(")");
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("\n[Console](");
			sb.append(_consoleLogURL);
			sb.append(")\n\n");
		}

		String senderUsername = _pullRequest.getSenderUsername();

		sb.append("@");
		sb.append(senderUsername);

		String receiverUsername = _pullRequest.getReceiverUsername();

		if (!senderUsername.equals(receiverUsername)) {
			sb.append("\n");
			sb.append("@");
			sb.append(receiverUsername);
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(initialComment)) {
			sb.append("\n");
			sb.append(initialComment);
		}

		return sb.toString();
	}

	private String _getCIForwardPullRequestInitialComment() throws IOException {
		StringBuilder sb = new StringBuilder();

		String pullRequestBody = _pullRequest.getBody();

		if (!pullRequestBody.isEmpty()) {
			sb.append("\n");
			sb.append("Original pull request comment:\n");
			sb.append(pullRequestBody);
		}

		return sb.toString();
	}

	private List<String> _getFailedRequiredPassingTestSuiteNames()
		throws IOException {

		List<String> passingTestSuiteNames =
			_pullRequest.getPassingTestSuiteNames();

		String joinedPassingTestSuiteNames = JenkinsResultsParserUtil.join(
			",", passingTestSuiteNames);

		System.out.println(
			"passing test suites: " + joinedPassingTestSuiteNames);

		List<String> failingRequiredPassingTestSuiteNames = new ArrayList<>(
			passingTestSuiteNames.size());

		String[] requiredPassingTestSuiteNames =
			_getRequiredPassingTestSuiteNames();

		String joinedRequiredPassingTestSuiteNames =
			JenkinsResultsParserUtil.join(",", requiredPassingTestSuiteNames);

		System.out.println(
			"required passing test suites: " +
				joinedRequiredPassingTestSuiteNames);

		for (String requiredPassingTestSuiteName :
				requiredPassingTestSuiteNames) {

			if (passingTestSuiteNames.contains(requiredPassingTestSuiteName) ||
				_hasPassingStatus(requiredPassingTestSuiteName)) {

				continue;
			}

			failingRequiredPassingTestSuiteNames.add(
				requiredPassingTestSuiteName);
		}

		return failingRequiredPassingTestSuiteNames;
	}

	private String _getFailureCommentBody() {
		StringBuilder sb = new StringBuilder();

		sb.append("Error has occurred while forwarding pull request to `");
		sb.append(_recipientUsername);
		sb.append("`.\n");
		sb.append("Please try again later or contact ");
		sb.append("the CI team for assistance.\n");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("See console log for details: [Full Console](");
			sb.append(_consoleLogURL);
			sb.append("consoleText");
			sb.append(")");
		}

		return sb.toString();
	}

	private String _getGitHubApiSearchUrl() throws IOException {
		List<String> filters = Arrays.asList(
			"author:" +
				JenkinsResultsParserUtil.getBuildProperty("github.ci.username"),
			"head:" + _getCIForwardBranchName(), "is:pr", "is:open",
			JenkinsResultsParserUtil.combine(
				"repo:", _recipientUsername, "/",
				_pullRequest.getGitRepositoryName()));

		return JenkinsResultsParserUtil.getGitHubApiSearchUrl(filters);
	}

	private List<PullRequest.Comment> _getGitHubCIComments() {
		try {
			List<PullRequest.Comment> comments = _pullRequest.getComments();

			Collections.reverse(comments);

			String gitHubCIUsername = JenkinsResultsParserUtil.getBuildProperty(
				"github.ci.username");

			List<PullRequest.Comment> gitHubCIComments = new ArrayList<>(
				comments.size());

			for (PullRequest.Comment comment : comments) {
				if (gitHubCIUsername.equals(comment.getUserLogin())) {
					gitHubCIComments.add(comment);
				}
			}

			return gitHubCIComments;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getHasOpenForwardedPullRequestCommentBody(
		List<String> openForwardedPullRequestURLs) {

		StringBuilder sb = new StringBuilder();

		sb.append(
			"This pull request already has open forwarded pull request(s):\n");

		for (String openForwardedPullRequestURL :
				openForwardedPullRequestURLs) {

			sb.append(openForwardedPullRequestURL);
			sb.append("\n");
		}

		sb.append("\nPull request will not be forwarded to ");
		sb.append("`");
		sb.append(_recipientUsername);
		sb.append("`.\n");
		sb.append("[Console](");
		sb.append(_consoleLogURL);
		sb.append(")\n");

		return sb.toString();
	}

	private List<String> _getIncompleteRequiredCompletedTestSuiteNames()
		throws IOException {

		List<String> completedTestSuiteNames =
			_pullRequest.getCompletedTestSuiteNames();

		String joinedCompletedTestSuiteNames = JenkinsResultsParserUtil.join(
			",", completedTestSuiteNames);

		System.out.println(
			"completed test suites: " + joinedCompletedTestSuiteNames);

		List<String> incompleteRequiredCompletedTestSuiteNames =
			new ArrayList<>(completedTestSuiteNames.size());

		String[] requiredCompletedTestSuiteNames =
			_getRequiredCompletedTestSuiteNames();

		String joinedRequiredCompletedTestSuiteNames =
			JenkinsResultsParserUtil.join(",", requiredCompletedTestSuiteNames);

		System.out.println(
			"required completed test suites: " +
				joinedRequiredCompletedTestSuiteNames);

		for (String requiredCompletedTestSuiteName :
				requiredCompletedTestSuiteNames) {

			if (!completedTestSuiteNames.contains(
					requiredCompletedTestSuiteName)) {

				incompleteRequiredCompletedTestSuiteNames.add(
					requiredCompletedTestSuiteName);
			}
		}

		return incompleteRequiredCompletedTestSuiteNames;
	}

	private String _getMergeConflictCommentBody() {
		StringBuilder sb = new StringBuilder();

		sb.append("Unable to forward to ");
		sb.append(_recipientUsername);
		sb.append(":");
		sb.append(_pullRequest.getUpstreamRemoteGitBranchName());
		sb.append(" because the new pull request would contain a merge ");
		sb.append("conflict.");

		return sb.toString();
	}

	private List<String> _getOpenForwardedPullRequestUrls() throws IOException {
		List<String> openForwardedPullRequestUrls = new ArrayList<>();

		JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
			_getGitHubApiSearchUrl());

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
			return openForwardedPullRequestUrls;
		}

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemsJSONObject = itemsJSONArray.getJSONObject(i);

			openForwardedPullRequestUrls.add(
				itemsJSONObject.optString("html_url"));
		}

		return openForwardedPullRequestUrls;
	}

	private String _getPassedCommentBody() {
		StringBuilder sb = new StringBuilder();

		sb.append("All required test suite(s) ");

		if (_force) {
			sb.append("completed");
		}
		else {
			sb.append("passed");
		}

		sb.append(".\n");
		sb.append("Forwarding pull request to `");
		sb.append(_recipientUsername);
		sb.append("`.\n");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("[Console](");
			sb.append(_consoleLogURL);
			sb.append(")\n");
		}

		return sb.toString();
	}

	private String _getPRCheckStatusLine() {
		String state = _getSenderSHAStatusState("pr-check");

		if (Objects.equals(state, "error") ||
			Objects.equals(state, "failure")) {

			return _getTestSuiteStatusLine(
				":x:",
				JenkinsResultsParserUtil.combine(
					"`pr-check` failed. Fix the failures and push, then rerun ",
					"the `/pr-check` Claude Code skill and publish the result ",
					"with `/pr-check-publish`."),
				"pr-check");
		}

		if (_pullRequest.hasLabel("pr-check - skipped")) {
			return _getTestSuiteStatusLine(
				":warning:",
				JenkinsResultsParserUtil.combine(
					"Pull requests with a skipped `pr-check` may not be ",
					"forwarded. Please send this pull request manually using ",
					"the `/pr` Claude Code skill."),
				"pr-check");
		}

		if (_pullRequest.hasLabel("pr-check - failure") ||
			_pullRequest.hasLabel("pr-check - success")) {

			return _getTestSuiteStatusLine(
				":warning:",
				JenkinsResultsParserUtil.combine(
					"The `pr-check` result does not match the current head ",
					"commit. Please rerun the `/pr-check` Claude Code skill ",
					"and publish the result with `/pr-check-publish`."),
				"pr-check");
		}

		return _getTestSuiteStatusLine(
			":x:",
			JenkinsResultsParserUtil.combine(
				"This pull request has no `pr-check` result. Please run the ",
				"`/pr-check` Claude Code skill and publish the result with ",
				"`/pr-check-publish`."),
			"pr-check");
	}

	private String[] _getRequiredCompletedTestSuiteNames() throws IOException {
		return _getBuildPropertyAsArray(
			JenkinsResultsParserUtil.combine(
				"ci.forward", _force ? ".force" : "",
				".required.completed.suites"));
	}

	private String[] _getRequiredPassingTestSuiteNames() throws IOException {
		return _getBuildPropertyAsArray(
			JenkinsResultsParserUtil.combine(
				"ci.forward", _force ? ".force" : "",
				".required.passing.suites"));
	}

	private String _getRetryCommentBody() {
		StringBuilder sb = new StringBuilder();

		sb.append(
			"Error has occurred while attempting to forward pull request to `");
		sb.append(_recipientUsername);
		sb.append("`. Retrying in ");
		sb.append(JenkinsResultsParserUtil.toDurationString(_RETRY_PERIOD));
		sb.append("...");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("\nSee console log for detail:[Full Console](");
			sb.append(_consoleLogURL);
			sb.append(")\n");
		}

		return sb.toString();
	}

	private String _getSenderSHAStatusState(String statusContext) {
		JSONObject statusJSONObject =
			_pullRequest.getSenderSHAStatusJSONObject();

		if (statusJSONObject == null) {
			return null;
		}

		JSONArray statusesJSONArray = statusJSONObject.optJSONArray("statuses");

		if (statusesJSONArray == null) {
			return null;
		}

		for (int i = 0; i < statusesJSONArray.length(); i++) {
			JSONObject statusesJSONObject = statusesJSONArray.getJSONObject(i);

			if (statusContext.equals(statusesJSONObject.getString("context"))) {
				return statusesJSONObject.getString("state");
			}
		}

		return null;
	}

	private String _getSuccessCommentBody(String forwardedPullRequestURL) {
		StringBuilder sb = new StringBuilder();

		sb.append("Pull request has been successfully forwarded to  ");
		sb.append(forwardedPullRequestURL);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("\n[Console](");
			sb.append(_consoleLogURL);
			sb.append(")");
		}

		return sb.toString();
	}

	private List<PullRequest.Comment> _getSuiteTestResultGitHubComments()
		throws IOException {

		Set<String> testSuiteNames = new HashSet<>();

		List<String> requiredCompletedTestSuiteNames = Arrays.asList(
			_getRequiredCompletedTestSuiteNames());

		testSuiteNames.addAll(requiredCompletedTestSuiteNames);

		List<String> requiredPassingTestSuiteNames = Arrays.asList(
			_getRequiredPassingTestSuiteNames());

		testSuiteNames.addAll(requiredPassingTestSuiteNames);

		List<PullRequest.Comment> filteredComments = new ArrayList<>(
			testSuiteNames.size());

		List<PullRequest.Comment> comments = _getGitHubCIComments();

		for (String testSuiteName : testSuiteNames) {
			boolean requiredPassing = requiredPassingTestSuiteNames.contains(
				testSuiteName);

			PullRequest.Comment comment = _findMostRecentTestResultComment(
				testSuiteName, requiredPassing, comments);

			if ((comment != null) && !filteredComments.contains(comment)) {
				filteredComments.add(comment);
			}
		}

		Collections.sort(filteredComments);

		return filteredComments;
	}

	private String _getTestSuiteStatusLine(
		String marker, String message, String testSuiteName) {

		StringBuilder sb = new StringBuilder();

		sb.append("- ");
		sb.append(marker);
		sb.append(" ");

		if (testSuiteName.equals("pr-check")) {
			sb.append("**pr-check**");
		}
		else {
			sb.append("ci:test:**");
			sb.append(testSuiteName);
			sb.append("**");
		}

		sb.append(" - ");
		sb.append(message);
		sb.append("\n");

		return sb.toString();
	}

	private String _getUnsuccessfulCommentBody() throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("This pull request will not be forwarded to `");
		sb.append(_recipientUsername);
		sb.append("` because not all required checks passed:\n");

		List<String> incompleteRequiredCompletedTestSuiteNames =
			_getIncompleteRequiredCompletedTestSuiteNames();

		for (String requiredCompletedTestSuiteName :
				_getRequiredCompletedTestSuiteNames()) {

			if (incompleteRequiredCompletedTestSuiteNames.contains(
					requiredCompletedTestSuiteName)) {

				sb.append(
					_getTestSuiteStatusLine(
						":warning:", "Not Completed",
						requiredCompletedTestSuiteName));

				continue;
			}

			sb.append(
				_getTestSuiteStatusLine(
					":white_check_mark:", "Completed",
					requiredCompletedTestSuiteName));
		}

		List<String> failedRequiredPassingTestSuiteNames =
			_getFailedRequiredPassingTestSuiteNames();

		for (String requiredPassingTestSuiteName :
				_getRequiredPassingTestSuiteNames()) {

			if (!failedRequiredPassingTestSuiteNames.contains(
					requiredPassingTestSuiteName)) {

				sb.append(
					_getTestSuiteStatusLine(
						":white_check_mark:", "Passed",
						requiredPassingTestSuiteName));

				continue;
			}

			if (requiredPassingTestSuiteName.equals("pr-check")) {
				sb.append(_getPRCheckStatusLine());

				continue;
			}

			if (requiredPassingTestSuiteName.equals("stable")) {
				sb.append(
					_getTestSuiteStatusLine(
						":x:",
						JenkinsResultsParserUtil.combine(
							"This test suite failed. If you believe the ",
							"failures were caused by flaky tests, please ",
							"contact QA for confirmation and rerun the test."),
						requiredPassingTestSuiteName));

				continue;
			}

			sb.append(
				_getTestSuiteStatusLine(
					":x:",
					JenkinsResultsParserUtil.combine(
						"This test suite failed. Fix the failures and push, ",
						"then rerun the `/pr-check` Claude Code skill and ",
						"publish the result with `/pr-check-publish`."),
					requiredPassingTestSuiteName));
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_consoleLogURL)) {
			sb.append("\n[Console](");
			sb.append(_consoleLogURL);
			sb.append(")\n");
		}

		return sb.toString();
	}

	private boolean _hasMergeConflict() {
		String upstreamBranchName =
			_pullRequest.getUpstreamRemoteGitBranchName();

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				upstreamBranchName, _gitRepositoryDir.getAbsolutePath(),
				_pullRequest.getGitRepositoryName());

		String receiverRemoteURL = GitUtil.getUserRemoteURL(
			_pullRequest.getGitRepositoryName(), _recipientUsername);

		RemoteGitBranch senderRemoteGitBranch =
			_pullRequest.getSenderRemoteGitBranch();

		RemoteGitBranch receiverRemoteGitBranch =
			gitWorkingDirectory.getRemoteGitBranch(
				upstreamBranchName, receiverRemoteURL, true);

		if (receiverRemoteGitBranch.getMergeBaseCommit(senderRemoteGitBranch) ==
				null) {

			return false;
		}

		gitWorkingDirectory.fetch(receiverRemoteGitBranch);
		gitWorkingDirectory.fetch(senderRemoteGitBranch);

		LocalGitBranch receiverLocalGitBranch =
			gitWorkingDirectory.createLocalGitBranch(
				JenkinsResultsParserUtil.combine(
					_recipientUsername, "-", upstreamBranchName, "-precheck"),
				true, receiverRemoteGitBranch.getSHA(),
				receiverRemoteGitBranch);

		LocalGitBranch senderLocalGitBranch =
			gitWorkingDirectory.createLocalGitBranch(
				_pullRequest.getLocalSenderBranchName() + "-precheck", true,
				_pullRequest.getSenderSHA(), senderRemoteGitBranch);

		try {
			gitWorkingDirectory.rebase(
				true, receiverLocalGitBranch, senderLocalGitBranch);

			return false;
		}
		catch (GitWorkingDirectory.GitWorkingDirectoryRuntimeException
					gitWorkingDirectoryRuntimeException) {

			String message = gitWorkingDirectoryRuntimeException.getMessage();

			if ((message != null) && message.contains("Unable to rebase ")) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Detected merge conflict between ",
						senderRemoteGitBranch.getUsername(), ":",
						senderRemoteGitBranch.getName(), " and ",
						_recipientUsername, ":", upstreamBranchName, "\n",
						message));

				return true;
			}

			System.out.println(
				"WARNING: Unable to determine merge conflict status\n" +
					String.valueOf(message));

			return false;
		}
	}

	private boolean _hasPassingStatus(String statusContext) {
		return Objects.equals(
			_getSenderSHAStatusState(statusContext), "success");
	}

	private boolean _isForwardCanceled() {
		PullRequest pullRequest = PullRequestFactory.newPullRequest(
			_pullRequest.getURL());

		String state = pullRequest.getState();

		if (state.equals("closed")) {
			_pullRequest.addComment(
				_getCanceledCommentBody("the pull request was closed"));

			return true;
		}

		String gitHubCIUsername;

		try {
			gitHubCIUsername = JenkinsResultsParserUtil.getBuildProperty(
				"github.ci.username");
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build property", ioException);
		}

		Date latestForwardCommentDate = null;
		Date latestStopCommentDate = null;

		for (PullRequest.Comment comment : pullRequest.getComments()) {
			if (gitHubCIUsername.equals(comment.getUserLogin())) {
				continue;
			}

			String body = comment.getBody();

			if (body == null) {
				continue;
			}

			body = body.trim();

			body = body.toLowerCase();

			Date commentDate = comment.getCreatedDate();

			if (body.startsWith("ci:forward")) {
				if ((latestForwardCommentDate == null) ||
					commentDate.after(latestForwardCommentDate)) {

					latestForwardCommentDate = commentDate;
				}
			}
			else if (body.startsWith("ci:stop") &&
					 !body.startsWith("ci:stop:")) {

				if ((latestStopCommentDate == null) ||
					commentDate.after(latestStopCommentDate)) {

					latestStopCommentDate = commentDate;
				}
			}
		}

		if (latestForwardCommentDate == null) {
			_pullRequest.addComment(
				_getCanceledCommentBody("the ci:forward comment was removed"));

			return true;
		}

		if ((latestStopCommentDate != null) &&
			latestStopCommentDate.after(latestForwardCommentDate)) {

			_pullRequest.addComment(
				_getCanceledCommentBody("ci:stop was requested"));

			return true;
		}

		return false;
	}

	private boolean _isForwardEligible() throws IOException {
		List<String> incompleteRequiredCompletedTestSuiteNames =
			_getIncompleteRequiredCompletedTestSuiteNames();

		if (!incompleteRequiredCompletedTestSuiteNames.isEmpty()) {
			return false;
		}

		List<String> failedRequiredPassingTestSuiteNames =
			_getFailedRequiredPassingTestSuiteNames();

		return failedRequiredPassingTestSuiteNames.isEmpty();
	}

	private static final long _RETRY_PERIOD = 1000L * 60L;

	private final String _consoleLogURL;
	private final boolean _force;
	private final File _gitRepositoryDir;
	private final PullRequest _pullRequest;
	private final String _recipientUsername;

}