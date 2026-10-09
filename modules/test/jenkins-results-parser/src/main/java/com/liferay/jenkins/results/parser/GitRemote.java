/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class GitRemote {

	public static final Pattern gitLsRemotePattern = Pattern.compile(
		"(?<sha>[^\\s]{40}+)[\\s]+refs/(?<type>[^/]+)+/(?<name>[^\\s]+)");

	public static Matcher getRemoteURLMatcher(String remoteURL) {
		Matcher matcher = _remoteURLMultiPattern.matches(remoteURL);

		if (matcher == null) {
			matcher = _remoteURLMultiPattern.find(remoteURL);
		}

		if (matcher != null) {
			matcher.reset();
		}

		return matcher;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GitRemote)) {
			return false;
		}

		GitRemote otherGitRemote = (GitRemote)object;

		if (Objects.equals(
				getGitRepositoryName(),
				otherGitRemote.getGitRepositoryName()) &&
			Objects.equals(getRemoteURL(), otherGitRemote.getRemoteURL())) {

			return true;
		}

		return false;
	}

	public String getGitRepositoryName() {
		return _gitRepositoryName;
	}

	public GitWorkingDirectory getGitWorkingDirectory() {
		return _gitWorkingDirectory;
	}

	public String getHostname() {
		return _hostname;
	}

	public String getName() {
		return _name;
	}

	public String getPushRemoteURL() {
		if (_pushRemoteURL != null) {
			return _pushRemoteURL;
		}

		return _fetchRemoteURL;
	}

	public String getRemoteURL() {
		return _fetchRemoteURL;
	}

	public String getUsername() {
		return _username;
	}

	@Override
	public int hashCode() {
		String key = getRemoteURL() + getGitRepositoryName();

		return key.hashCode();
	}

	public boolean isAvailable() {
		if (_available != null) {
			return _available;
		}

		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		String command = JenkinsResultsParserUtil.combine(
			"git ls-remote -h -t ", getRemoteURL(), " ",
			gitWorkingDirectory.getUpstreamBranchName());

		try {
			GitUtil.ExecutionResult executionResult =
				GitUtil.executeBashCommands(
					1, GitUtil.MILLIS_RETRY_DELAY, 1000 * 5,
					gitWorkingDirectory.getWorkingDirectory(), command);

			if (executionResult.getExitValue() != 0) {
				_available = false;

				return _available;
			}
		}
		catch (Exception exception) {
			_available = false;

			return _available;
		}

		_available = true;

		return _available;
	}

	@Override
	public String toString() {
		return JenkinsResultsParserUtil.combine(
			getName(), " (", getRemoteURL(), ")");
	}

	protected GitRemote(
		GitWorkingDirectory gitWorkingDirectory, String name,
		String remoteURL) {

		_gitWorkingDirectory = gitWorkingDirectory;
		_name = name;

		_fetchRemoteURL = remoteURL;
		_pushRemoteURL = remoteURL;

		parseRemoteURL();
	}

	protected GitRemote(
		GitWorkingDirectory gitWorkingDirectory, String[] remoteInputLines) {

		_gitWorkingDirectory = gitWorkingDirectory;

		if (remoteInputLines.length != 2) {
			throw new IllegalArgumentException(
				"\"remoteInputLines\" must be an array of 2 strings");
		}

		if (remoteInputLines[0].equals(remoteInputLines[1])) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"\"remoteInputLines[0]\" and ",
					"\"remoteInputLines[1]\" are identical: ",
					remoteInputLines[0]));
		}

		if ((remoteInputLines[0] == null) || (remoteInputLines[1] == null)) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					"Neither \"remoteInputLines[0]\" nor ",
					"\"remoteInputLines[1]\" may be NULL: ",
					Arrays.toString(remoteInputLines)));
		}

		String name = null;
		String fetchRemoteURL = null;
		String pushRemoteURL = null;

		for (String remoteInputLine : remoteInputLines) {
			Matcher matcher = _remotePattern.matcher(remoteInputLine);

			if (!matcher.matches()) {
				throw new IllegalArgumentException(
					"Invalid Git remote input line " + remoteInputLine);
			}

			if (name == null) {
				name = matcher.group("name");
			}

			String remoteURL = matcher.group("remoteURL");
			String type = matcher.group("type");

			if ((fetchRemoteURL == null) && type.equals("fetch")) {
				fetchRemoteURL = remoteURL;
			}

			if ((pushRemoteURL != null) || !type.equals("push")) {
				continue;
			}

			pushRemoteURL = remoteURL;
		}

		_fetchRemoteURL = fetchRemoteURL;
		_name = name;
		_pushRemoteURL = pushRemoteURL;

		parseRemoteURL();
	}

	protected void parseRemoteURL() {
		Matcher remoteURLMatcher = getRemoteURLMatcher(_fetchRemoteURL);

		if ((remoteURLMatcher == null) || !remoteURLMatcher.find()) {
			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"fetch remote URL ", _fetchRemoteURL,
					" is not a valid remote URL"));
		}

		_hostname = remoteURLMatcher.group("hostname");

		try {
			_username = remoteURLMatcher.group("username");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_username = "liferay";
		}

		_gitRepositoryName = remoteURLMatcher.group("gitRepositoryName");
	}

	private static final Pattern _remotePattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"(?<name>[^\\s]+)[\\s]+(?<remoteURL>[^\\s]+)[\\s]+\\(",
			"(?<type>[^\\s]+)\\)"));
	private static final MultiPattern _remoteURLMultiPattern = new MultiPattern(
		"git@(?<hostname>[^:]+):(?<username>[^/]+)" +
			"/(?<gitRepositoryName>[^\\.^\\s]+)(\\.git)?+\\s*",
		"https://(?<hostname>[^/]+)/(?<username>[^/]+)" +
			"/(?<gitRepositoryName>[^\\.^\\s]+)(\\.git)?+\\s*",
		"root@(?<hostname>[^:]+):/opt/dev/projects/github" +
			"/(?<gitRepositoryName>[^\\\\.]+)");

	private Boolean _available;
	private final String _fetchRemoteURL;
	private String _gitRepositoryName;
	private final GitWorkingDirectory _gitWorkingDirectory;
	private String _hostname;
	private final String _name;
	private final String _pushRemoteURL;
	private String _username;

}