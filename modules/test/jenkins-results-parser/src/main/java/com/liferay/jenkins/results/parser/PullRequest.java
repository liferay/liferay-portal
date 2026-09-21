/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.atlassian.jira.rest.client.api.domain.Issue;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PullRequest {

	public static String getURL(
		String username, String repositoryName, String pullRequestNumber) {

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(username);
		sb.append("/");
		sb.append(repositoryName);
		sb.append("/pull/");
		sb.append(pullRequestNumber);

		return sb.toString();
	}

	public static boolean isValidGitHubPullRequestURL(String gitHubURL) {
		Matcher matcher = _gitHubPullRequestURLPattern.matcher(gitHubURL);

		return matcher.find();
	}

	public Comment addComment(String body) {
		if (!isUpdateEnabled()) {
			return new Comment(new JSONObject());
		}

		body = body.replaceAll("(\\>)\\s+(\\<)", "$1$2");
		body = body.replace("&quot;", "\\&quot;");

		JSONObject dataJSONObject = new JSONObject();

		dataJSONObject.put("body", body);

		try {
			JSONObject responseJSONObject =
				JenkinsResultsParserUtil.toJSONObject(
					JenkinsResultsParserUtil.combine(
						_jsonObject.getString("issue_url"), "/comments"),
					dataJSONObject.toString());

			return new Comment(responseJSONObject);
		}
		catch (GitHubSecondaryRateLimitRuntimeException
					gitHubSecondaryRateLimitRuntimeException) {

			StringBuilder sb = new StringBuilder();

			sb.append("Unable to post comment in GitHub pull request\n");
			sb.append("URL: ");
			sb.append(getURL());
			sb.append("\nMessage:\n");
			sb.append(body);
			sb.append("\n");

			NotificationUtil.sendSlackNotification(
				sb.toString(), "#ci-notifications", ":liferay-ci:",
				"Secondary rate limit exceeded", "Liferay CI");

			throw new GitHubSecondaryRateLimitRuntimeException(
				gitHubSecondaryRateLimitRuntimeException.getGitHubAPIURL(),
				gitHubSecondaryRateLimitRuntimeException.getRetryAfterSeconds(),
				sb.toString(), gitHubSecondaryRateLimitRuntimeException);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to post comment in GitHub pull request " + getURL(),
				ioException);
		}
	}

	public void addLabel(GitHubRemoteGitRepository.Label label) {
		addLabels(Arrays.asList(label));
	}

	public void addLabels(List<GitHubRemoteGitRepository.Label> labels) {
		if (!isUpdateEnabled() || (labels == null) || labels.isEmpty()) {
			return;
		}

		List<GitHubRemoteGitRepository.Label> newLabels = new ArrayList<>();

		for (GitHubRemoteGitRepository.Label label : labels) {
			if ((label == null) || hasLabel(label.getName())) {
				continue;
			}

			newLabels.add(label);
		}

		if (newLabels.isEmpty()) {
			return;
		}

		JSONArray jsonArray = new JSONArray();

		for (GitHubRemoteGitRepository.Label newLabel : newLabels) {
			jsonArray.put(newLabel.getName());
		}

		String gitHubAPIURL = JenkinsResultsParserUtil.getGitHubAPIURL(
			getGitHubRemoteGitRepositoryName(), getOwnerUsername(),
			"issues/" + getNumber() + "/labels");

		try {
			JenkinsResultsParserUtil.toString(
				gitHubAPIURL, false, HttpRequestMethod.POST,
				jsonArray.toString());
		}
		catch (IOException ioException) {
			System.out.println("Unable to add labels " + jsonArray);

			ioException.printStackTrace();
		}
	}

	public void close() {
		if (!isUpdateEnabled()) {
			return;
		}

		if (Objects.equals(getState(), "open")) {
			JSONObject postContentJSONObject = new JSONObject();

			postContentJSONObject.put("state", "closed");

			try {
				JenkinsResultsParserUtil.toString(
					_jsonObject.getString("url"), false, HttpRequestMethod.POST,
					postContentJSONObject.toString());
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to close pull request " + getHtmlURL(),
					ioException);
			}
		}

		_jsonObject.put("state", "closed");
	}

	public void copyLabelsToPullRequest(PullRequest pullRequest) {
		if (pullRequest == null) {
			return;
		}

		refresh();

		pullRequest.addLabels(getLabels());
	}

	public void copyStatusesToPullRequest(PullRequest pullRequest) {
		if (pullRequest == null) {
			return;
		}

		JSONObject senderSHAStatusJSONObject = getSenderSHAStatusJSONObject();

		if (senderSHAStatusJSONObject == null) {
			return;
		}

		JSONArray statusesJSONArray = senderSHAStatusJSONObject.optJSONArray(
			"statuses");

		if ((statusesJSONArray == null) || statusesJSONArray.isEmpty()) {
			return;
		}

		GitHubRemoteGitCommit gitHubRemoteGitCommit =
			pullRequest.getGitHubRemoteGitCommit();

		for (int i = 0; i < statusesJSONArray.length(); i++) {
			JSONObject statusJSONObject = statusesJSONArray.getJSONObject(i);

			String state = statusJSONObject.getString("state");
			String context = statusJSONObject.getString("context");
			String description = statusJSONObject.optString(
				"description", null);
			String targetURL = statusJSONObject.optString("target_url", null);

			GitHubRemoteGitCommit.Status status =
				GitHubRemoteGitCommit.Status.valueOf(state.toUpperCase());

			gitHubRemoteGitCommit.setStatus(
				status, context, description, targetURL);
		}
	}

	public String forward(
			String commentBody, String consoleURL,
			String forwardReceiverUsername, String forwardBranchName,
			String forwardSenderUsername, File gitRepositoryDir)
		throws ForwardPullRequestException {

		if (!isUpdateEnabled()) {
			return null;
		}

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				getUpstreamRemoteGitBranchName(),
				gitRepositoryDir.getAbsolutePath(), getGitRepositoryName());

		String senderSHA = getSenderSHA();

		if (!gitWorkingDirectory.localSHAExists(senderSHA)) {
			RemoteGitRef senderRemoteGitRef =
				gitWorkingDirectory.getRemoteGitRef(
					getSenderBranchName(), getSenderRemoteURL(), true);

			gitWorkingDirectory.fetch(senderRemoteGitRef);
		}

		LocalGitBranch forwardLocalGitBranch =
			gitWorkingDirectory.createLocalGitBranch(
				forwardBranchName, true, senderSHA);

		RemoteGitBranch forwardRemoteGitBranch =
			gitWorkingDirectory.pushToRemoteGitRepository(
				true, forwardLocalGitBranch, forwardLocalGitBranch.getName(),
				GitUtil.getUserRemoteURL(
					getGitRepositoryName(), forwardSenderUsername));

		if (forwardRemoteGitBranch == null) {
			throw new RuntimeException("Unable to push branch to GitHub");
		}

		String compareAPIURL = JenkinsResultsParserUtil.getGitHubAPIURL(
			getGitRepositoryName(), forwardReceiverUsername,
			JenkinsResultsParserUtil.combine(
				"compare/", getUpstreamRemoteGitBranchName(), "...",
				forwardSenderUsername, ":", forwardBranchName));

		int aheadBy = -1;

		try {
			JSONObject compareJSONObject =
				JenkinsResultsParserUtil.toJSONObject(compareAPIURL);

			aheadBy = compareJSONObject.getInt("ahead_by");
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to compare branches: " + ioException.getMessage());
		}

		if (aheadBy == 0) {
			StringBuilder sb = new StringBuilder();

			sb.append("`ci:forward` could not forward this pull ");
			sb.append("request because every commit on this pull request is ");
			sb.append("already present on `");
			sb.append(forwardReceiverUsername);
			sb.append("/");
			sb.append(getGitRepositoryName());
			sb.append(":");
			sb.append(getUpstreamRemoteGitBranchName());
			sb.append("`.");

			throw new ForwardPullRequestException(sb.toString(), false, null);
		}

		return gitWorkingDirectory.createPullRequest(
			commentBody, forwardBranchName, forwardReceiverUsername,
			forwardSenderUsername, getTitle());
	}

	public URL getBaseURL() {
		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://github.com/", getReceiverUsername(), "/",
					getGitRepositoryName(), "/tree/",
					getUpstreamRemoteGitBranchName()));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public String getBody() {
		return _jsonObject.optString("body");
	}

	public String getCIMergeSHA() {
		getFileNames();

		return _ciMergeSHA;
	}

	public String getCIMergeSubrepo() {
		for (String fileName : getFileNames()) {
			if (fileName.endsWith("/ci-merge")) {
				return fileName.replace("/ci-merge", "");
			}
		}

		throw new IllegalStateException("Unable to find ci-merge file");
	}

	public List<Comment> getComments() {
		if (_comments != null) {
			return _comments;
		}

		_comments = new ArrayList<>();

		String gitHubAPIURL = JenkinsResultsParserUtil.getGitHubAPIURL(
			getGitHubRemoteGitRepositoryName(), getOwnerUsername(),
			"issues/" + getNumber() + "/comments?per_page=100&page=");

		for (int pageNumber = 1;
			 pageNumber <=
				 JenkinsResultsParserUtil.PAGES_GITHUB_API_PAGES_SIZE_MAX;
			 pageNumber++) {

			try {
				JSONArray commentJSONArray =
					JenkinsResultsParserUtil.toJSONArray(
						gitHubAPIURL + pageNumber, false);

				if (commentJSONArray.length() == 0) {
					break;
				}

				for (int i = 0; i < commentJSONArray.length(); i++) {
					_comments.add(
						new Comment(commentJSONArray.getJSONObject(i)));
				}

				if (commentJSONArray.length() < JenkinsResultsParserUtil.
						PER_PAGE_GITHUB_API_PAGES_SIZE_MAX) {

					break;
				}

				if (pageNumber ==
						JenkinsResultsParserUtil.
							PAGES_GITHUB_API_PAGES_SIZE_MAX) {

					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Too many comments (>",
							String.valueOf(_gitHubRemoteGitCommits.size()),
							") found for ", "pull request ", getHtmlURL()));
				}
			}
			catch (IOException ioException) {
				_comments = null;

				throw new RuntimeException(
					"Unable to get pull request comments", ioException);
			}
		}

		Collections.sort(_comments);

		return _comments;
	}

	public String getCommonParentSHA() {
		if (_commonParentSHA == null) {
			_initCommits();
		}

		return _commonParentSHA;
	}

	public List<String> getCompletedTestSuiteNames() {
		List<String> testSuiteNames = new ArrayList<>();

		JSONArray statusesJSONArray = getSenderSHAStatusesJSONArray();

		for (int i = 0; i < statusesJSONArray.length(); i++) {
			JSONObject jsonObject = statusesJSONArray.getJSONObject(i);

			String testSuiteName = jsonObject.getString("context");

			if (!Objects.equals(testSuiteName, "default")) {
				Matcher matcher = _liferayContextPattern.matcher(testSuiteName);

				if (matcher.find()) {
					testSuiteName = matcher.group("testSuiteName");
				}
			}

			if (testSuiteNames.contains(testSuiteName)) {
				continue;
			}

			String state = jsonObject.getString("state");

			if (!Objects.equals(state, "failure") &&
				!Objects.equals(state, "success")) {

				continue;
			}

			testSuiteNames.add(testSuiteName);
		}

		return testSuiteNames;
	}

	public List<String> getFileNames() {
		if (!_fileNames.isEmpty()) {
			return _fileNames;
		}

		_ciMergeSHA = "";

		String filesURL = JenkinsResultsParserUtil.combine(
			"https://api.github.com/repos/", getReceiverUsername(), "/",
			getGitHubRemoteGitRepositoryName(), "/pulls/", getNumber(),
			"/files");

		try {
			JSONArray filesJSONArray = JenkinsResultsParserUtil.toJSONArray(
				filesURL, false);

			for (int j = 0; j < filesJSONArray.length(); j++) {
				JSONObject fileJSONObject = filesJSONArray.getJSONObject(j);

				String fileName = fileJSONObject.getString("filename");

				_fileNames.add(fileName);

				if (fileName.endsWith("/ci-merge")) {
					String patch = fileJSONObject.getString("patch");

					Matcher matcher = _ciMergeSHAPattern.matcher(patch);

					if (matcher.find()) {
						String sha = matcher.group(1);

						if (!matcher.find()) {
							_ciMergeSHA = sha;
						}
					}
				}
			}

			return _fileNames;
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get pull request file names", ioException);
		}
	}

	public List<GitHubRemoteGitCommit> getGitHubRemoteCommits() {
		if (_gitHubRemoteGitCommits == null) {
			_initCommits();
		}

		return _gitHubRemoteGitCommits;
	}

	public GitHubRemoteGitCommit getGitHubRemoteGitCommit() {
		if (_gitHubRemoteGitCommits == null) {
			_initCommits();
		}

		return _gitHubRemoteGitCommits.get(_gitHubRemoteGitCommits.size() - 1);
	}

	public GitHubRemoteGitRepository getGitHubRemoteGitRepository() {
		if (_gitHubRemoteGitRepository == null) {
			_gitHubRemoteGitRepository =
				(GitHubRemoteGitRepository)
					GitRepositoryFactory.getRemoteGitRepository(
						"github.com", _gitHubRemoteGitRepositoryName,
						getOwnerUsername());
		}

		return _gitHubRemoteGitRepository;
	}

	public String getGitHubRemoteGitRepositoryName() {
		return _gitHubRemoteGitRepositoryName;
	}

	public String getGitRepositoryName() {
		return getGitHubRemoteGitRepositoryName();
	}

	public URL getHeadURL() {
		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://github.com/", getSenderUsername(), "/",
					getGitRepositoryName(), "/tree/", getSenderBranchName()));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public String getHtmlURL() {
		return _jsonObject.getString("html_url");
	}

	public Set<Issue> getJIRAIssues() {
		if (_jiraIssues == null) {
			_initJIRAIssues();
		}

		return _jiraIssues;
	}

	public String getJSON() {
		return _jsonObject.toString(4);
	}

	public JSONObject getJSONObject() {
		return _jsonObject;
	}

	public List<GitHubRemoteGitRepository.Label> getLabels() {
		if (_labels == null) {
			_refreshJSONObject();

			JSONArray labelJSONArray = _jsonObject.getJSONArray("labels");

			_labels = new ArrayList<>(labelJSONArray.length());

			for (int i = 0; i < labelJSONArray.length(); i++) {
				JSONObject labelJSONObject = labelJSONArray.getJSONObject(i);

				_labels.add(
					new GitHubRemoteGitRepository.Label(
						labelJSONObject, getGitHubRemoteGitRepository()));
			}
		}

		return _labels;
	}

	public String getLocalSenderBranchName() {
		return JenkinsResultsParserUtil.combine(
			getSenderUsername(), "-", getNumber(), "-", getSenderBranchName());
	}

	public String getMergeableState() {
		Retryable<String> retryable = new Retryable<String>(false, 5, 5, true) {

			@Override
			public String execute() {
				if (_firstAttempt) {
					_firstAttempt = false;
				}
				else {
					_refreshJSONObject();
				}

				String mergeableState = _jsonObject.getString(
					"mergeable_state");

				if (mergeableState.equals("unknown")) {
					throw new RuntimeException("Mergeable state is unknown");
				}

				return mergeableState;
			}

			private boolean _firstAttempt = true;

		};

		return retryable.executeWithRetries();
	}

	public String getNumber() {
		return String.valueOf(_number);
	}

	public String getOwnerUsername() {
		return _ownerUsername;
	}

	public List<String> getPassingTestSuiteNames() {
		List<String> testSuiteNames = new ArrayList<>();

		JSONArray statusesJSONArray = getSenderSHAStatusesJSONArray();

		for (int i = 0; i < statusesJSONArray.length(); i++) {
			JSONObject jsonObject = statusesJSONArray.getJSONObject(i);

			String testSuiteName = jsonObject.getString("context");

			if (!Objects.equals(testSuiteName, "default")) {
				Matcher matcher = _liferayContextPattern.matcher(testSuiteName);

				if (matcher.find()) {
					testSuiteName = matcher.group("testSuiteName");
				}
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName)) {
				testSuiteName = "default";
			}

			if (testSuiteNames.contains(testSuiteName) ||
				!Objects.equals(jsonObject.getString("state"), "success")) {

				continue;
			}

			testSuiteNames.add(testSuiteName);
		}

		return testSuiteNames;
	}

	public String getReceiverUsername() {
		JSONObject baseJSONObject = _jsonObject.getJSONObject("base");

		JSONObject userJSONObject = baseJSONObject.getJSONObject("user");

		return userJSONObject.getString("login");
	}

	public String getRefName() {
		JSONObject baseJSONObject = _jsonObject.getJSONObject("base");

		return baseJSONObject.getString("ref");
	}

	public String getSenderBranchName() {
		JSONObject headJSONObject = _jsonObject.getJSONObject("head");

		return headJSONObject.getString("ref");
	}

	public RemoteGitBranch getSenderRemoteGitBranch() {
		if (_senderRemoteGitBranch == null) {
			_senderRemoteGitBranch =
				(RemoteGitBranch)GitBranchFactory.newRemoteGitRef(
					GitRepositoryFactory.getRemoteGitRepository(
						"github.com", getGitHubRemoteGitRepositoryName(),
						getSenderUsername()),
					getSenderBranchName(), getSenderSHA(), "heads");
		}

		return _senderRemoteGitBranch;
	}

	public String getSenderRemoteURL() {
		return JenkinsResultsParserUtil.combine(
			"git@github.com:", getSenderUsername(), "/",
			getGitHubRemoteGitRepositoryName());
	}

	public String getSenderSHA() {
		JSONObject headJSONObject = _jsonObject.getJSONObject("head");

		return headJSONObject.getString("sha");
	}

	public JSONObject getSenderSHAStatusJSONObject() {
		JSONObject statusJSONObject = null;

		try {
			String statusURL = _jsonObject.getString("statuses_url");

			statusURL = statusURL.replace("statuses", "status");

			statusJSONObject = JenkinsResultsParserUtil.toJSONObject(statusURL);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return statusJSONObject;
	}

	public JSONArray getSenderSHAStatusesJSONArray() {
		JSONArray statusesJSONArray = null;

		try {
			statusesJSONArray = JenkinsResultsParserUtil.toJSONArray(
				_jsonObject.getString("statuses_url"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return statusesJSONArray;
	}

	public String getSenderUsername() {
		JSONObject headJSONObject = _jsonObject.getJSONObject("head");

		JSONObject userJSONObject = headJSONObject.getJSONObject("user");

		return userJSONObject.getString("login");
	}

	public String getState() {
		return _jsonObject.getString("state");
	}

	public List<String> getStatusDescriptions() {
		GitHubRemoteGitCommit gitHubRemoteGitCommit =
			getGitHubRemoteGitCommit();

		return gitHubRemoteGitCommit.getStatusDescriptions();
	}

	public String getTitle() {
		return _jsonObject.getString("title");
	}

	public String getURL() {
		return getURL(
			getReceiverUsername(), getGitRepositoryName(), getNumber());
	}

	public String getUpstreamBranchSHA() {
		RemoteGitBranch upstreamRemoteGitBranch = getUpstreamRemoteGitBranch();

		return upstreamRemoteGitBranch.getSHA();
	}

	public RemoteGitBranch getUpstreamRemoteGitBranch() {
		if (_liferayRemoteGitBranch == null) {
			String gitRepositoryName = getGitRepositoryName();

			_liferayRemoteGitBranch = GitUtil.getRemoteGitBranch(
				getUpstreamRemoteGitBranchName(), new File("."),
				JenkinsResultsParserUtil.combine(
					"git@github.com:",
					JenkinsResultsParserUtil.getUpstreamUserName(
						gitRepositoryName, getUpstreamRemoteGitBranchName()),
					"/", gitRepositoryName, ".git"));
		}

		return _liferayRemoteGitBranch;
	}

	public String getUpstreamRemoteGitBranchName() {
		JSONObject baseJSONObject = _jsonObject.getJSONObject("base");

		return baseJSONObject.getString("ref");
	}

	public boolean hasLabel(String labelName) {
		for (GitHubRemoteGitRepository.Label label : getLabels()) {
			if (labelName.equals(label.getName())) {
				return true;
			}
		}

		return false;
	}

	public boolean hasRequiredCompletedTestSuites() {
		return hasRequiredCompletedTestSuites(false);
	}

	public boolean hasRequiredCompletedTestSuites(boolean force) {
		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String propertyName = JenkinsResultsParserUtil.combine(
			"ci.forward", force ? ".force" : "", ".required.completed.suites");

		String requiredCompletedTestSuiteNames =
			JenkinsResultsParserUtil.getProperty(
				buildProperties, propertyName, getGitRepositoryName(),
				getRefName());

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				requiredCompletedTestSuiteNames)) {

			return true;
		}

		List<String> completedTestSuiteNames = getCompletedTestSuiteNames();

		for (String requiredCompletedSuiteName :
				requiredCompletedTestSuiteNames.split("\\s*,\\s*")) {

			if (!completedTestSuiteNames.contains(requiredCompletedSuiteName)) {
				return false;
			}
		}

		return true;
	}

	public boolean hasRequiredPassingTestSuites() {
		return hasRequiredPassingTestSuites(false);
	}

	public boolean hasRequiredPassingTestSuites(boolean force) {
		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String propertyName = JenkinsResultsParserUtil.combine(
			"ci.forward", force ? ".force" : "", ".required.passing.suites");

		String requiredPassingTestSuiteNames =
			JenkinsResultsParserUtil.getProperty(
				buildProperties, propertyName, getGitRepositoryName(),
				getRefName());

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				requiredPassingTestSuiteNames)) {

			return true;
		}

		List<String> passingTestSuiteNames = getPassingTestSuiteNames();

		for (String requiredPassingTestSuiteName :
				requiredPassingTestSuiteNames.split("\\s*,\\s*")) {

			if (!passingTestSuiteNames.contains(requiredPassingTestSuiteName)) {
				return false;
			}
		}

		return true;
	}

	public boolean isAutoCloseCommentAvailable() {
		if (_autoCloseCommentAvailable != null) {
			return _autoCloseCommentAvailable;
		}

		List<Comment> comments = getComments();

		for (Comment comment : comments) {
			String commentBody = comment.getBody();

			if (commentBody.contains("auto-close=\"false\"")) {
				_autoCloseCommentAvailable = true;

				return _autoCloseCommentAvailable;
			}
		}

		_autoCloseCommentAvailable = false;

		return _autoCloseCommentAvailable;
	}

	public boolean isMergeSubrepoRequest() {
		for (String filename : getFileNames()) {
			if (filename.endsWith("/ci-merge")) {
				return true;
			}
		}

		return false;
	}

	public boolean isUpdateEnabled() {
		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String githubPullRequestUpdateEnabled =
			JenkinsResultsParserUtil.getProperty(
				buildProperties, "github.pull.request.update.enabled");

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				githubPullRequestUpdateEnabled)) {

			return true;
		}

		return Boolean.parseBoolean(githubPullRequestUpdateEnabled);
	}

	public boolean isValidCIMergeFile() {
		List<String> fileNames = getFileNames();

		if ((fileNames.size() == 1) && isMergeSubrepoRequest()) {
			return true;
		}

		return false;
	}

	public void lock() {
		if (!isUpdateEnabled()) {
			return;
		}

		try {
			JenkinsResultsParserUtil.toString(
				getIssueURL() + "/lock", false, HttpRequestMethod.PUT);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to lock pull request " + getHtmlURL(), ioException);
		}
	}

	public void refresh() {
		_comments = null;
		_commonParentSHA = null;
		_gitHubRemoteGitCommits = null;
		_labels = null;

		getLabels();
	}

	public void removeComment(Comment comment) {
		removeComment(comment.getId());
	}

	public void removeComment(String id) {
		if (!isUpdateEnabled()) {
			return;
		}

		String editCommentURL = _jsonObject.getString("issue_url");

		editCommentURL = editCommentURL.replaceFirst("issues/\\d+", "issues");

		try {
			JenkinsResultsParserUtil.toString(
				JenkinsResultsParserUtil.combine(
					editCommentURL, "/comments/", id),
				false, HttpRequestMethod.DELETE);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to delete comment in GitHub pull request " + getURL(),
				ioException);
		}
	}

	public void removeLabel(String labelName) {
		if (!isUpdateEnabled() || !hasLabel(labelName)) {
			return;
		}

		String path = JenkinsResultsParserUtil.combine(
			"issues/", getNumber(), "/labels/",
			JenkinsResultsParserUtil.fixURL(labelName));

		String gitHubAPIURL = JenkinsResultsParserUtil.getGitHubAPIURL(
			getGitHubRemoteGitRepositoryName(), getOwnerUsername(), path);

		try {
			JenkinsResultsParserUtil.toString(
				gitHubAPIURL, false, HttpRequestMethod.DELETE);

			_labels = null;
		}
		catch (IOException ioException) {
			System.out.println("Unable to remove label " + labelName);

			ioException.printStackTrace();
		}
	}

	public void resetAutoCloseCommentAvailable() {
		_autoCloseCommentAvailable = null;
	}

	public void setTestSuiteStatus(
		String testSuiteName, TestSuiteStatus testSuiteStatus) {

		setTestSuiteStatus(testSuiteName, testSuiteStatus, null);
	}

	public void setTestSuiteStatus(
		String testSuiteName, TestSuiteStatus testSuiteStatus,
		String targetURL) {

		setTestSuiteStatus(testSuiteName, testSuiteStatus, targetURL, null);
	}

	public void setTestSuiteStatus(
		String testSuiteName, TestSuiteStatus testSuiteStatus, String targetURL,
		String senderSHA) {

		if (!isUpdateEnabled()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("ci:test");

		if (!testSuiteName.equals(_NAME_TEST_SUITE_DEFAULT)) {
			sb.append(":");
			sb.append(testSuiteName);
		}

		sb.append(" ");

		String testSuiteLabelPrefix = sb.toString();

		List<String> oldLabelNames = new ArrayList<>();

		for (GitHubRemoteGitRepository.Label label : getLabels()) {
			String name = label.getName();

			if (name.startsWith(testSuiteLabelPrefix)) {
				oldLabelNames.add(label.getName());
			}
		}

		for (String oldLabelName : oldLabelNames) {
			removeLabel(oldLabelName);
		}

		sb.append(" - ");
		sb.append(StringUtils.lowerCase(testSuiteStatus.toString()));

		GitHubRemoteGitRepository gitHubRemoteGitRepository =
			getGitHubRemoteGitRepository();

		GitHubRemoteGitRepository.Label testSuiteLabel =
			gitHubRemoteGitRepository.getLabel(sb.toString());

		if ((testSuiteLabel == null) &&
			gitHubRemoteGitRepository.addLabel(
				testSuiteStatus.getColor(), "", sb.toString())) {

			testSuiteLabel = gitHubRemoteGitRepository.getLabel(sb.toString());
		}

		addLabel(testSuiteLabel);

		if ((targetURL == null) ||
			(testSuiteStatus == TestSuiteStatus.MISSING)) {

			return;
		}

		GitHubRemoteGitCommit gitHubRemoteGitCommit =
			getGitHubRemoteGitCommit();

		if ((senderSHA != null) && senderSHA.matches("[0-9a-f]{7,40}")) {
			gitHubRemoteGitCommit = GitCommitFactory.newGitHubRemoteGitCommit(
				getOwnerUsername(), getGitHubRemoteGitRepositoryName(),
				senderSHA);
		}

		GitHubRemoteGitCommit.Status status =
			GitHubRemoteGitCommit.Status.valueOf(testSuiteStatus.toString());

		String context = _NAME_TEST_SUITE_DEFAULT;

		if (!testSuiteName.equals(_NAME_TEST_SUITE_DEFAULT)) {
			context = "liferay/ci:test:" + testSuiteName;
		}

		sb = new StringBuilder();

		sb.append("\"ci:test");

		if (!testSuiteName.equals(_NAME_TEST_SUITE_DEFAULT)) {
			sb.append(":");
			sb.append(testSuiteName);
		}

		sb.append("\"");

		if (testSuiteStatus == TestSuiteStatus.BYPASSED) {
			sb.append(" was BYPASSED.");
		}
		else if ((testSuiteStatus == TestSuiteStatus.ERROR) ||
				 (testSuiteStatus == TestSuiteStatus.FAILURE)) {

			sb.append(" has FAILED.");
		}
		else if (testSuiteStatus == TestSuiteStatus.PENDING) {
			sb.append(" is running.");
		}
		else if (testSuiteStatus == TestSuiteStatus.SUCCESS) {
			sb.append(" has PASSED.");
		}

		gitHubRemoteGitCommit.setStatus(
			status, context, sb.toString(), targetURL);
	}

	public Comment updateComment(Comment comment) {
		return updateComment(comment.getBody(), comment.getId());
	}

	public Comment updateComment(String body, String id) {
		if (!isUpdateEnabled()) {
			return null;
		}

		JSONObject jsonObject = new JSONObject();

		body = body.replaceAll("(\\>)\\s+(\\<)", "$1$2");
		body = body.replace("&quot;", "\\&quot;");

		jsonObject.put("body", body);

		try {
			String editCommentURL = _jsonObject.getString("issue_url");

			editCommentURL = editCommentURL.replaceFirst(
				"issues/\\d+", "issues");

			return new Comment(
				JenkinsResultsParserUtil.toJSONObject(
					JenkinsResultsParserUtil.combine(
						editCommentURL, "/comments/", id),
					false, HttpRequestMethod.PATCH, jsonObject.toString()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to update comment in GitHub pull request " + getURL(),
				ioException);
		}
	}

	public static class Comment implements Comparable<Comment> {

		public Comment(JSONObject commentJSONObject) {
			_commentJSONObject = commentJSONObject;
		}

		@Override
		public int compareTo(Comment comment) {
			Date createdDate = getCreatedDate();

			return createdDate.compareTo(comment.getCreatedDate());
		}

		public String getBody() {
			return _commentJSONObject.optString("body");
		}

		public Date getCreatedDate() {
			try {
				return _UtcIso8601SimpleDateFormat.parse(
					_commentJSONObject.getString("created_at"));
			}
			catch (ParseException parseException) {
				throw new RuntimeException(
					"Unable to parse created date " +
						_commentJSONObject.getString("created_at"),
					parseException);
			}
		}

		public String getId() {
			return String.valueOf(_commentJSONObject.optLong("id"));
		}

		public Date getModifiedDate() {
			try {
				return _UtcIso8601SimpleDateFormat.parse(
					_commentJSONObject.optString("modified_at"));
			}
			catch (ParseException parseException) {
				throw new RuntimeException(
					"Unable to parse modified date " +
						_commentJSONObject.getString("modified_at"),
					parseException);
			}
		}

		public URL getURL() {
			try {
				return new URL(_commentJSONObject.optString("html_url"));
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		public String getUserLogin() {
			JSONObject userJSONObject = _commentJSONObject.getJSONObject(
				"user");

			return userJSONObject.getString("login");
		}

		private static final SimpleDateFormat _UtcIso8601SimpleDateFormat;

		static {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");

			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			_UtcIso8601SimpleDateFormat = simpleDateFormat;
		}

		private final JSONObject _commentJSONObject;

	}

	public static class ForwardPullRequestException extends Exception {

		public ForwardPullRequestException(
			String message, boolean retryable, Throwable throwable) {

			super(message, throwable);

			_retryable = retryable;
		}

		public boolean isRetryable() {
			return _retryable;
		}

		private final boolean _retryable;

	}

	public static enum TestSuiteStatus {

		BYPASSED("bcf5db"), ERROR("fccdcc"), FAILURE("fccdcc"),
		MISSING("eeeeee"), PENDING("fff4c9"), SUCCESS("c7e8cb");

		public String getColor() {
			return _color;
		}

		private TestSuiteStatus(String color) {
			_color = color;
		}

		private final String _color;

	}

	protected PullRequest(JSONObject jsonObject) {
		JSONObject baseJSONObject = jsonObject.getJSONObject("base");

		JSONObject repoJSONObject = baseJSONObject.getJSONObject("repo");

		_gitHubRemoteGitRepositoryName = repoJSONObject.getString("name");

		_number = jsonObject.getInt("number");

		JSONObject ownerJSONObject = repoJSONObject.getJSONObject("owner");

		_ownerUsername = ownerJSONObject.getString("login");

		_jsonObject = jsonObject;
	}

	protected PullRequest(String gitHubURL) {
		Matcher matcher = _gitHubPullRequestURLPattern.matcher(gitHubURL);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid GitHub URL " + gitHubURL);
		}

		_gitHubRemoteGitRepositoryName = matcher.group(
			"gitHubRemoteGitRepositoryName");
		_number = Integer.parseInt(matcher.group("number"));
		_ownerUsername = matcher.group("owner");

		refresh();
	}

	protected String getGitHubAPIURL() {
		return JenkinsResultsParserUtil.getGitHubAPIURL(
			_gitHubRemoteGitRepositoryName, _ownerUsername, "pulls/" + _number);
	}

	protected String getIssueURL() {
		return _jsonObject.getString("issue_url");
	}

	protected void updateGithub() {
		JSONObject jsonObject = new JSONObject();

		List<String> labelNames = new ArrayList<>();

		for (GitHubRemoteGitRepository.Label label : getLabels()) {
			labelNames.add(label.getName());
		}

		jsonObject.put("labels", labelNames);

		try {
			JenkinsResultsParserUtil.toJSONObject(
				getIssueURL(), jsonObject.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _initCommits() {
		String commitsURL = _jsonObject.getString("commits_url");

		_gitHubRemoteGitCommits = new ArrayList<>();

		try {
			for (int pageNumber = 1;
				 pageNumber <=
					 JenkinsResultsParserUtil.PAGES_GITHUB_API_PAGES_SIZE_MAX;
				 pageNumber++) {

				JSONArray commitsJSONArray =
					JenkinsResultsParserUtil.toJSONArray(
						JenkinsResultsParserUtil.combine(
							commitsURL, "?per_page=100&page=",
							String.valueOf(pageNumber)));

				if (commitsJSONArray.length() == 0) {
					break;
				}

				for (int i = 0; i < commitsJSONArray.length(); i++) {
					JSONObject commitJSONObject =
						commitsJSONArray.getJSONObject(i);

					_gitHubRemoteGitCommits.add(
						GitCommitFactory.newGitHubRemoteGitCommit(
							getOwnerUsername(), getGitRepositoryName(),
							commitJSONObject.getString("sha"),
							commitJSONObject));
				}

				if (pageNumber == 1) {
					JSONObject firstCommitJSONObject =
						commitsJSONArray.getJSONObject(0);

					JSONArray parentsJSONArray =
						firstCommitJSONObject.getJSONArray("parents");

					JSONObject firstParentJSONObject =
						parentsJSONArray.getJSONObject(0);

					_commonParentSHA = firstParentJSONObject.getString("sha");
				}

				if (commitsJSONArray.length() < JenkinsResultsParserUtil.
						PER_PAGE_GITHUB_API_PAGES_SIZE_MAX) {

					break;
				}

				if (pageNumber ==
						JenkinsResultsParserUtil.
							PAGES_GITHUB_API_PAGES_SIZE_MAX) {

					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Too many GitHub remote commits (>",
							String.valueOf(_gitHubRemoteGitCommits.size()),
							") found for ", "pull request ", getHtmlURL()));
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get GitHub remote commits for pull request " +
					getHtmlURL(),
				ioException);
		}
	}

	private void _initJIRAIssues() {
		getGitHubRemoteCommits();

		_jiraIssues = new HashSet<>();

		for (GitHubRemoteGitCommit gitHubRemoteGitCommit :
				_gitHubRemoteGitCommits) {

			Issue issue = gitHubRemoteGitCommit.getJIRAIssue();

			if (issue != null) {
				_jiraIssues.add(issue);
			}
		}
	}

	private void _refreshJSONObject() {
		try {
			_jsonObject = JenkinsResultsParserUtil.toJSONObject(
				getGitHubAPIURL(), false);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String _NAME_TEST_SUITE_DEFAULT = "default";

	private static final Pattern _ciMergeSHAPattern = Pattern.compile(
		"\\+([0-9a-f]{40})");
	private static final Pattern _gitHubPullRequestURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"https://github.com/(?<owner>[^/]+)/",
			"(?<gitHubRemoteGitRepositoryName>[^/]+)/pull/(?<number>\\d+)"));
	private static final Pattern _liferayContextPattern = Pattern.compile(
		"liferay/ci:test(:(?<testSuiteName>[^:]+))?");

	private Boolean _autoCloseCommentAvailable;
	private String _ciMergeSHA = "";
	private List<Comment> _comments;
	private String _commonParentSHA;
	private final List<String> _fileNames = new ArrayList<>();
	private List<GitHubRemoteGitCommit> _gitHubRemoteGitCommits;
	private GitHubRemoteGitRepository _gitHubRemoteGitRepository;
	private final String _gitHubRemoteGitRepositoryName;
	private Set<Issue> _jiraIssues;
	private JSONObject _jsonObject;
	private List<GitHubRemoteGitRepository.Label> _labels;
	private RemoteGitBranch _liferayRemoteGitBranch;
	private final Integer _number;
	private final String _ownerUsername;
	private RemoteGitBranch _senderRemoteGitBranch;

}