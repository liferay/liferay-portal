/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class GitCommitFactory {

	public static GitHubRemoteGitCommit newGitHubRemoteGitCommit(
		String gitHubUsername, String gitRepositoryName, String sha) {

		String gitHubCommitURL = _getGitHubCommitURL(
			gitHubUsername, gitRepositoryName, sha);

		GitHubRemoteGitCommit gitHubRemoteGitCommit =
			_gitHubRemoteGitCommits.get(gitHubCommitURL);

		if (gitHubRemoteGitCommit != null) {
			return gitHubRemoteGitCommit;
		}

		try {
			return newGitHubRemoteGitCommit(
				gitHubUsername, gitRepositoryName, sha,
				JenkinsResultsParserUtil.toJSONObject(gitHubCommitURL));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get commit details", ioException);
		}
	}

	public static GitHubRemoteGitCommit newGitHubRemoteGitCommit(
		String gitHubUsername, String gitRepositoryName, String sha,
		JSONObject jsonObject) {

		JSONObject commitJSONObject = jsonObject.getJSONObject("commit");

		String message = commitJSONObject.getString("message");

		JSONObject committerJSONObject = commitJSONObject.getJSONObject(
			"committer");

		JSONArray filesJSONArray = jsonObject.optJSONArray("files");

		List<String> fileNames = null;

		if (filesJSONArray != null) {
			fileNames = new ArrayList<>(filesJSONArray.length());

			for (int i = 0; i < filesJSONArray.length(); i++) {
				JSONObject fileJSONObject = filesJSONArray.getJSONObject(i);

				fileNames.add(fileJSONObject.getString("filename"));
			}
		}

		try {
			DateFormat gitHubDateFormat =
				JenkinsResultsParserUtil.getGitHubDateFormat();

			Date date = gitHubDateFormat.parse(
				committerJSONObject.getString("date"));

			GitHubRemoteGitCommit remoteGitCommit = new GitHubRemoteGitCommit(
				committerJSONObject.getString("email"), gitHubUsername,
				gitRepositoryName, message, fileNames,
				jsonObject.getString("sha"), _getGitCommitType(message),
				date.getTime());

			_gitHubRemoteGitCommits.put(
				jsonObject.getString("url"), remoteGitCommit);

			return remoteGitCommit;
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}
	}

	public static LocalGitCommit newLocalGitCommit(
		String emailAddress, GitWorkingDirectory gitWorkingDirectory,
		String message, String sha, long commitTime) {

		return new DefaultLocalGitCommit(
			emailAddress, gitWorkingDirectory, message, sha,
			_getGitCommitType(message), commitTime);
	}

	public static LocalGitCommit newLocalGitCommit(
		String emailAddress, GitWorkingDirectory gitWorkingDirectory,
		String message, String patch, String sha, long commitTime) {

		return new DefaultLocalGitCommit(
			emailAddress, gitWorkingDirectory, message, patch, sha,
			_getGitCommitType(message), commitTime);
	}

	private static GitCommit.Type _getGitCommitType(String message) {
		if (message.startsWith("archive:ignore")) {
			return GitCommit.Type.LEGACY_ARCHIVE;
		}

		return GitCommit.Type.MANUAL;
	}

	private static String _getGitHubCommitURL(
		String gitHubUsername, String gitRepositoryName, String sha) {

		return JenkinsResultsParserUtil.getGitHubAPIURL(
			gitRepositoryName, gitHubUsername, "commits/" + sha);
	}

	private static final Map<String, GitHubRemoteGitCommit>
		_gitHubRemoteGitCommits = new ConcurrentHashMap<>();

}