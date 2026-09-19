/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class CentralGitSubrepository {

	public CentralGitSubrepository(
			File gitrepoFile, String centralUpstreamBranchName)
		throws IOException {

		_centralUpstreamBranchName = centralUpstreamBranchName;

		_ciProperties = new Properties();

		_gitrepoProperties = new Properties();

		_gitrepoProperties.load(new FileInputStream(gitrepoFile));

		_gitSubrepositoryName = _getGitSubrepositoryName();

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(buildProperties.getProperty("base.repository.dir"));
		sb.append("/");
		sb.append(_gitSubrepositoryName);

		if (!_gitSubrepositoryName.endsWith("-private")) {
			sb.append("-private");
		}

		_gitSubrepositoryDirectory = sb.toString();

		_gitSubrepositoryUpstreamBranchName = _centralUpstreamBranchName;
		_gitSubrepositoryUsername = _getGitSubrepositoryUsername();

		String tempBranchName =
			"temp-" + JenkinsResultsParserUtil.getCurrentTimeMillis();

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				_gitSubrepositoryUpstreamBranchName, _gitSubrepositoryDirectory,
				_gitSubrepositoryName);

		LocalGitBranch upstreamLocalGitBranch = null;
		LocalGitBranch tempLocalGitBranch = null;

		try {
			tempLocalGitBranch = gitWorkingDirectory.createLocalGitBranch(
				tempBranchName);

			gitWorkingDirectory.checkoutLocalGitBranch(tempLocalGitBranch);

			GitRemote upstreamGitRemote = gitWorkingDirectory.getGitRemote(
				"upstream");

			upstreamLocalGitBranch = gitWorkingDirectory.getLocalGitBranch(
				_gitSubrepositoryUpstreamBranchName, true);

			gitWorkingDirectory.fetch(
				upstreamLocalGitBranch,
				gitWorkingDirectory.getRemoteGitBranch(
					_gitSubrepositoryUpstreamBranchName, upstreamGitRemote,
					true));
		}
		finally {
			if ((upstreamLocalGitBranch != null) &&
				(tempLocalGitBranch != null) &&
				gitWorkingDirectory.localGitBranchExists(
					tempLocalGitBranch.getName())) {

				gitWorkingDirectory.checkoutLocalGitBranch(
					upstreamLocalGitBranch);

				gitWorkingDirectory.deleteLocalGitBranch(tempLocalGitBranch);
			}
		}

		try {
			File ciPropertiesFile = new File(
				_gitSubrepositoryDirectory, "ci.properties");

			_ciProperties.load(new FileInputStream(ciPropertiesFile));
		}
		catch (FileNotFoundException fileNotFoundException) {
			System.out.println(
				"Unable to find ci.properties in " +
					_gitSubrepositoryDirectory);
		}
	}

	public String getCIProperty(String key) {
		return _ciProperties.getProperty(key);
	}

	public String getGitSubrepositoryName() {
		return _gitSubrepositoryName;
	}

	public String getGitSubrepositoryUpstreamCommit() throws IOException {
		if (_gitSubrepositoryUpstreamCommit == null) {
			_gitSubrepositoryUpstreamCommit =
				_getGitSubrepositoryUpstreamCommit();
		}

		return _gitSubrepositoryUpstreamCommit;
	}

	public boolean isAutoPullEnabled() {
		String mode = _gitrepoProperties.getProperty("mode", "push");

		if (!mode.equals("pull")) {
			return false;
		}

		return Boolean.parseBoolean(
			_gitrepoProperties.getProperty("autopull", "false"));
	}

	public boolean isCentralPullRequestCandidate() throws IOException {
		if (_centralPullRequestCandidate == null) {
			_centralPullRequestCandidate = _isCentralPullRequestCandidate();
		}

		return _centralPullRequestCandidate;
	}

	public boolean isGitSubrepositoryUpstreamCommitMerged() throws IOException {
		String gitSubrepositoryMergedCommit = _gitrepoProperties.getProperty(
			"commit", "");

		return gitSubrepositoryMergedCommit.equals(
			getGitSubrepositoryUpstreamCommit());
	}

	private String _getGitSubrepositoryName() {
		String remote = _gitrepoProperties.getProperty("remote");

		int x = remote.indexOf("/") + 1;
		int y = remote.indexOf(".git");

		return remote.substring(x, y);
	}

	private String _getGitSubrepositoryUpstreamCommit() throws IOException {
		String path = JenkinsResultsParserUtil.combine(
			"git/refs/heads/", _gitSubrepositoryUpstreamBranchName);

		String url = JenkinsResultsParserUtil.getGitHubAPIURL(
			_gitSubrepositoryName, _gitSubrepositoryUsername, path);

		JSONObject branchJSONObject = JenkinsResultsParserUtil.toJSONObject(
			url, false);

		JSONObject objectJSONObject = branchJSONObject.getJSONObject("object");

		return objectJSONObject.getString("sha");
	}

	private String _getGitSubrepositoryUsername() {
		String remote = _gitrepoProperties.getProperty("remote");

		int x = remote.indexOf(":") + 1;
		int y = remote.indexOf("/");

		return remote.substring(x, y);
	}

	private String _getMergePullRequestURL() throws IOException {
		String path = JenkinsResultsParserUtil.combine(
			"commits/", getGitSubrepositoryUpstreamCommit(), "/statuses");

		String url = JenkinsResultsParserUtil.getGitHubAPIURL(
			_gitSubrepositoryName, _gitSubrepositoryUsername, path);

		for (int i = 0; i < 15; i++) {
			JSONArray statusesJSONArray = JenkinsResultsParserUtil.toJSONArray(
				JenkinsResultsParserUtil.combine(
					url, "?page=", String.valueOf(i + 1)),
				true);

			if ((statusesJSONArray == null) ||
				(statusesJSONArray.length() == 0)) {

				break;
			}

			for (int j = 0; j < statusesJSONArray.length(); j++) {
				JSONObject statusesJSONObject = statusesJSONArray.getJSONObject(
					j);

				String context = statusesJSONObject.getString("context");

				if (context.equals("liferay/central-pull-request")) {
					return statusesJSONObject.getString("target_url");
				}
			}
		}

		return null;
	}

	private Boolean _isCentralPullRequestCandidate() throws IOException {
		if (!isAutoPullEnabled()) {
			return false;
		}

		if (isGitSubrepositoryUpstreamCommitMerged()) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"SKIPPED: ", _gitSubrepositoryName,
					" contains merged commit https://github.com/",
					_gitSubrepositoryUsername, "/", _gitSubrepositoryName,
					"/commit/", getGitSubrepositoryUpstreamCommit()));

			return false;
		}

		String mergePullRequestURL = _getMergePullRequestURL();

		if (mergePullRequestURL != null) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"SKIPPED: ", _gitSubrepositoryName,
					" contains an open merge pull request ",
					mergePullRequestURL));

			return false;
		}

		return true;
	}

	private Boolean _centralPullRequestCandidate;
	private final String _centralUpstreamBranchName;
	private final Properties _ciProperties;
	private final String _gitSubrepositoryDirectory;
	private final String _gitSubrepositoryName;
	private final String _gitSubrepositoryUpstreamBranchName;
	private String _gitSubrepositoryUpstreamCommit;
	private final String _gitSubrepositoryUsername;
	private final Properties _gitrepoProperties;

}