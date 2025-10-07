/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class GitUtil {

	public static final long MILLIS_RETRY_DELAY = 30 * 1000;

	public static final long MILLIS_TIMEOUT = 120 * 1000;

	public static final int RETRIES_SIZE_MAX = 1;

	public static void clone(String remoteURL, File workingDirectory) {
		String command = JenkinsResultsParserUtil.combine(
			"git clone ", remoteURL, " ",
			JenkinsResultsParserUtil.getCanonicalPath(workingDirectory));

		Process process = null;

		try {
			process = JenkinsResultsParserUtil.executeBashCommands(command);
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(
				"Unable to clone " + remoteURL, exception);
		}

		if ((process != null) && (process.exitValue() != 0)) {
			String errorString = null;

			try {
				errorString = JenkinsResultsParserUtil.readInputStream(
					process.getErrorStream());
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to clone ", remoteURL, "\n", errorString));
		}
	}

	public static String getDefaultBranchName(File workingDirectory) {
		String defaultBranchName = _getDefaultBranchName(
			workingDirectory, "origin");

		if (defaultBranchName == null) {
			defaultBranchName = _getDefaultBranchName(
				workingDirectory, "upstream");
		}

		return defaultBranchName;
	}

	public static String getPrivateRepositoryName(String repositoryName) {
		if (repositoryName.endsWith("-ee") ||
			repositoryName.endsWith("-private")) {

			return repositoryName;
		}

		if (repositoryName.startsWith("com-liferay")) {
			return repositoryName + "-private";
		}

		return repositoryName + "-ee";
	}

	public static String getPublicRepositoryName(String repositoryName) {
		if (!repositoryName.endsWith("-ee") &&
			!repositoryName.endsWith("-private")) {

			return repositoryName;
		}

		if (repositoryName.startsWith("com-liferay")) {
			return repositoryName.replace("-private", "");
		}

		return repositoryName.replace("-ee", "");
	}

	public static RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, File workingDirectory, String remoteURL) {

		return getRemoteGitBranch(
			remoteGitBranchName, workingDirectory, remoteURL, true);
	}

	public static RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, File workingDirectory, String remoteURL,
		boolean required) {

		RemoteGitRef remoteGitRef = getRemoteGitRef(
			remoteGitBranchName, workingDirectory, remoteURL, required);

		if (!(remoteGitRef instanceof RemoteGitBranch)) {
			if (!required) {
				return null;
			}

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to find remote Git branch ", remoteGitBranchName,
					" on remote URL ", remoteURL));
		}

		return (RemoteGitBranch)remoteGitRef;
	}

	public static List<RemoteGitBranch> getRemoteGitBranches(
		String remoteGitBranchName, File workingDirectory, String remoteURL) {

		List<RemoteGitBranch> remoteGitBranches = new ArrayList<>();

		for (RemoteGitRef remoteGitRef :
				getRemoteGitRefs(
					remoteGitBranchName, workingDirectory, remoteURL)) {

			if (remoteGitRef instanceof RemoteGitBranch) {
				remoteGitBranches.add((RemoteGitBranch)remoteGitRef);
			}
		}

		return remoteGitBranches;
	}

	public static RemoteGitRef getRemoteGitRef(String gitHubURL) {
		return getRemoteGitRef(gitHubURL, true);
	}

	public static RemoteGitRef getRemoteGitRef(
		String gitHubURL, boolean required) {

		Matcher matcher = _gitHubRefURLPattern.matcher(gitHubURL);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid GitHub URL " + gitHubURL);
		}

		String remoteGitRepositoryURL = JenkinsResultsParserUtil.combine(
			"git@github.com:", matcher.group("username"), "/",
			matcher.group("gitRepositoryName"), ".git");

		return getRemoteGitRef(
			matcher.group("refName"), new File("."), remoteGitRepositoryURL,
			required);
	}

	public static RemoteGitRef getRemoteGitRef(
		String remoteGitBranchName, File workingDirectory, String remoteURL) {

		return getRemoteGitRef(
			remoteGitBranchName, workingDirectory, remoteURL, true);
	}

	public static RemoteGitRef getRemoteGitRef(
		String remoteGitBranchName, File workingDirectory, String remoteURL,
		boolean required) {

		String key = JenkinsResultsParserUtil.combine(
			remoteURL, "#", remoteGitBranchName);

		synchronized (_gitHubRemoteGitRefs) {
			if (remoteURL.contains(_HOSTNAME_GITHUB)) {
				RemoteGitRef gitHubRemoteGitRef = _gitHubRemoteGitRefs.get(key);

				if (gitHubRemoteGitRef != null) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Using cached Git ref from ", remoteURL, " for ",
							remoteGitBranchName));

					return gitHubRemoteGitRef;
				}
			}

			List<RemoteGitRef> remoteGitRefs = null;

			if (remoteURL.contains(_HOSTNAME_GITHUB_CACHE_PROXY)) {
				List<String> usedGitHubDevNodeHostnames = new ArrayList<>(3);

				while ((usedGitHubDevNodeHostnames.size() < 3) &&
					   ((remoteGitRefs == null) || remoteGitRefs.isEmpty())) {

					String gitHubDevNodeHostname =
						JenkinsResultsParserUtil.getRandomGitHubDevNodeHostname(
							usedGitHubDevNodeHostnames);

					String gitHubDevNodeRemoteURL = remoteURL.replace(
						_HOSTNAME_GITHUB_CACHE_PROXY, gitHubDevNodeHostname);

					if (gitHubDevNodeHostname.startsWith("slave-")) {
						gitHubDevNodeRemoteURL = toSlaveGitHubDevNodeRemoteURL(
							remoteURL, gitHubDevNodeHostname.substring(6));
					}

					try {
						remoteGitRefs = getRemoteGitRefs(
							remoteGitBranchName, workingDirectory,
							gitHubDevNodeRemoteURL);
					}
					catch (Exception exception) {
						exception.printStackTrace();
					}

					usedGitHubDevNodeHostnames.add(gitHubDevNodeHostname);
				}
			}
			else {
				remoteGitRefs = getRemoteGitRefs(
					remoteGitBranchName, workingDirectory, remoteURL);
			}

			if ((remoteGitRefs == null) || remoteGitRefs.isEmpty()) {
				if (!required) {
					return null;
				}

				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to find remote Git ref ", remoteGitBranchName,
						" on remote URL ", remoteURL));
			}

			RemoteGitRef remoteGitRef = remoteGitRefs.get(0);

			if (remoteURL.contains(_HOSTNAME_GITHUB)) {
				_gitHubRemoteGitRefs.put(key, remoteGitRef);
			}

			return remoteGitRef;
		}
	}

	public static List<RemoteGitRef> getRemoteGitRefs(
		String remoteGitBranchName, File workingDirectory, String remoteURL) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		if (!isValidRemoteURL(remoteURL)) {
			throw new IllegalArgumentException(
				"Invalid remote url " + remoteURL);
		}

		String command = null;

		if (remoteGitBranchName != null) {
			command = JenkinsResultsParserUtil.combine(
				"git ls-remote -h -t ", remoteURL, " ", remoteGitBranchName);
		}
		else {
			command = JenkinsResultsParserUtil.combine(
				"git ls-remote -h ", remoteURL);
		}

		ExecutionResult executionResult = null;

		try {
			String timeoutSeconds = null;

			try {
				timeoutSeconds = JenkinsResultsParserUtil.getBuildProperty(
					"git.lsremote.timeout.seconds");
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to get build property, " +
						"\"git.lsremote.timeout.seconds\"");

				ioException.printStackTrace();
			}
			finally {
				if (JenkinsResultsParserUtil.isNullOrEmpty(timeoutSeconds)) {
					timeoutSeconds = "120";
				}
			}

			executionResult = executeBashCommands(
				3, GitUtil.MILLIS_RETRY_DELAY,
				1000 * Long.parseLong(timeoutSeconds), workingDirectory,
				command);

			if (executionResult.getExitValue() != 0) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to get remote refs from ", remoteURL, "\n",
						executionResult.getStandardError()));
			}
		}
		catch (Exception exception) {
			return new ArrayList<>();
		}

		String input = executionResult.getStandardOut();

		if (JenkinsResultsParserUtil.isNullOrEmpty(input)) {
			return new ArrayList<>();
		}

		List<RemoteGitRef> remoteGitRefs = new ArrayList<>();

		Matcher remoteURLMatcher = GitRemote.getRemoteURLMatcher(remoteURL);

		remoteURLMatcher.find();

		String username = "liferay";

		try {
			username = remoteURLMatcher.group("username");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		RemoteGitRepository remoteGitRepository =
			GitRepositoryFactory.getRemoteGitRepository(
				remoteURLMatcher.group("hostname"),
				remoteURLMatcher.group("gitRepositoryName"), username);

		for (String line : input.split("\n")) {
			Pattern gitLsRemotePattern = GitRemote.gitLsRemotePattern;

			Matcher gitLsRemoteMatcher = gitLsRemotePattern.matcher(line);

			if (!gitLsRemoteMatcher.find()) {
				continue;
			}

			remoteGitRefs.add(
				GitBranchFactory.newRemoteGitRef(
					remoteGitRepository, gitLsRemoteMatcher.group("name"),
					gitLsRemoteMatcher.group("sha"),
					gitLsRemoteMatcher.group("type")));
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"getRemoteGitRefs found ", String.valueOf(remoteGitRefs.size()),
				" refs at ", remoteURL, " in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start),
				"."));

		return remoteGitRefs;
	}

	public static String getUserRemoteURL(
		String repositoryName, String username) {

		return JenkinsResultsParserUtil.combine(
			"git@github.com:", username, "/", repositoryName, ".git");
	}

	public static boolean isValidGitHubRefURL(String gitHubURL) {
		Matcher matcher = _gitHubRefURLPattern.matcher(gitHubURL);

		return matcher.find();
	}

	public static boolean isValidRemoteURL(String remoteURL) {
		Matcher matcher = GitRemote.getRemoteURLMatcher(remoteURL);

		if (matcher != null) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		ExecutionResult executionResult = executeBashCommands(
			3, 1000 * 10, 1000 * 60, new File("."), args[0]);

		System.out.println(executionResult.getStandardOut());

		if (executionResult.getExitValue() == 0) {
			return;
		}

		System.err.println(executionResult.getStandardError());

		throw new RuntimeException("Unable to run command:\n     " + args[0]);
	}

	public static String toSlaveGitHubDevNodeRemoteURL(
		String gitHubDevRemoteURL, String slaveGitHubDevNodeHostname) {

		Matcher matcher = GitRemote.getRemoteURLMatcher(gitHubDevRemoteURL);

		if ((matcher != null) && matcher.find()) {
			String hostname = matcher.group("hostname");

			if ((hostname != null) && hostname.endsWith("github-dev")) {
				return JenkinsResultsParserUtil.combine(
					"root@", slaveGitHubDevNodeHostname,
					":/opt/dev/projects/github/",
					matcher.group("gitRepositoryName"));
			}
		}

		throw new IllegalArgumentException(
			"Invalid github-dev remote url " + gitHubDevRemoteURL);
	}

	public static class ExecutionResult {

		public int getExitValue() {
			return _exitValue;
		}

		public String getStandardError() {
			return _standardError;
		}

		public String getStandardOut() {
			return _standardOut;
		}

		protected ExecutionResult(
			int exitValue, String standardError, String standardOut) {

			_exitValue = exitValue;
			_standardError = standardError;

			if (standardOut.endsWith("\nFinished executing Bash commands.")) {
				_standardOut = standardOut.substring(
					0,
					standardOut.indexOf("\nFinished executing Bash commands."));
			}
			else {
				_standardOut = standardOut;
			}
		}

		private final int _exitValue;
		private final String _standardError;
		private final String _standardOut;

	}

	protected static ExecutionResult executeBashCommands(
		int maxRetries, long retryDelay, long timeout, File workingDirectory,
		String... commands) {

		Process process = null;

		int retries = 0;
		List<String> usedGitHubDevNodeHostnames = new ArrayList<>(maxRetries);

		while (retries < maxRetries) {
			String[] modifiedCommands = Arrays.copyOf(
				commands, commands.length);

			String gitHubDevNodeHostname =
				JenkinsResultsParserUtil.getRandomGitHubDevNodeHostname(
					usedGitHubDevNodeHostnames);

			usedGitHubDevNodeHostnames.add(gitHubDevNodeHostname);

			if (gitHubDevNodeHostname.startsWith("slave-")) {
				gitHubDevNodeHostname = gitHubDevNodeHostname.substring(6);

				for (int i = 0; i < modifiedCommands.length; i++) {
					String modifiedCommand = modifiedCommands[i];

					if (!modifiedCommand.contains(
							_HOSTNAME_GITHUB_CACHE_PROXY)) {

						continue;
					}

					Matcher matcher = GitRemote.getRemoteURLMatcher(
						modifiedCommands[i]);

					if (matcher != null) {
						while (matcher.find()) {
							retryDelay = 0;

							modifiedCommand = modifiedCommand.replaceFirst(
								matcher.group(0),
								toSlaveGitHubDevNodeRemoteURL(
									matcher.group(0), gitHubDevNodeHostname));
						}
					}

					modifiedCommands[i] = modifiedCommand;
				}
			}
			else if (!gitHubDevNodeHostname.isEmpty()) {
				for (int i = 0; i < modifiedCommands.length; i++) {
					modifiedCommands[i] = modifiedCommands[i].replace(
						_HOSTNAME_GITHUB_CACHE_PROXY, gitHubDevNodeHostname);

					if ((retryDelay != 0) &&
						modifiedCommands[i].contains(
							_HOSTNAME_GITHUB_CACHE_PROXY)) {

						retryDelay = 0;
					}
				}
			}

			try {
				retries++;

				process = JenkinsResultsParserUtil.executeBashCommands(
					true, workingDirectory, timeout, modifiedCommands);
			}
			catch (IOException | TimeoutException exception) {
				if (retries == maxRetries) {
					throw new RuntimeException(
						"Unable to execute bash commands: " +
							Arrays.toString(commands),
						exception);
				}

				exception.printStackTrace();
			}
			finally {
				try {
					if ((process != null) && (process.exitValue() == 0)) {
						break;
					}

					if (retries < maxRetries) {
						usedGitHubDevNodeHostnames.add(gitHubDevNodeHostname);

						System.out.println(
							"Unable to execute bash commands retrying... ");

						JenkinsResultsParserUtil.sleep(retryDelay);
					}
				}
				finally {
					if (process != null) {
						_debugDNS(process);
					}
				}
			}
		}

		String standardErr = "";

		try {
			standardErr = JenkinsResultsParserUtil.readInputStream(
				process.getErrorStream());
		}
		catch (IOException ioException) {
			standardErr = "";
		}

		String standardOut = "";

		try {
			standardOut = JenkinsResultsParserUtil.readInputStream(
				process.getInputStream());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read process input stream", ioException);
		}

		return new ExecutionResult(
			process.exitValue(), standardErr.trim(), standardOut.trim());
	}

	private static void _debugDNS(Process process) {
		String standardErr = "";

		try {
			standardErr = JenkinsResultsParserUtil.readInputStream(
				process.getErrorStream());
		}
		catch (IOException ioException) {
			standardErr = "";
		}

		Matcher matcher = _dnsDebugPattern.matcher(standardErr);

		if (matcher.find()) {
			String hostname = matcher.group("hostname");

			try {
				Process digProcess =
					JenkinsResultsParserUtil.executeBashCommands(
						new String[] {
							"dig " + hostname, "dig @10.0.1.11 " + hostname
						});

				System.out.println(
					JenkinsResultsParserUtil.readInputStream(
						digProcess.getInputStream()));
			}
			catch (Exception exception) {
				System.out.println(
					"Unable to execute debug DNS: " + exception.getMessage());
			}
		}
	}

	private static String _getDefaultBranchName(
		File workingDirectory, String gitRemoteName) {

		ExecutionResult executionResult = executeBashCommands(
			RETRIES_SIZE_MAX, MILLIS_RETRY_DELAY, MILLIS_TIMEOUT,
			workingDirectory,
			JenkinsResultsParserUtil.combine(
				"git remote show ", gitRemoteName, " | grep \"HEAD branch\" | ",
				"cut -d \":\" -f 2"));

		if (executionResult.getExitValue() != 0) {
			return null;
		}

		String defaultBranchName = executionResult.getStandardOut();

		defaultBranchName = defaultBranchName.replace(
			"Finished executing Bash commands.", "");

		defaultBranchName = defaultBranchName.trim();

		if (defaultBranchName.isEmpty()) {
			return null;
		}

		return defaultBranchName;
	}

	private static final String _HOSTNAME_GITHUB = "github.com";

	private static final String _HOSTNAME_GITHUB_CACHE_PROXY =
		"github-dev.liferay.com";

	private static final Pattern _dnsDebugPattern = Pattern.compile(
		"Unable to resolve hostname (?<hostname>[^:]+): ");
	private static final Pattern _gitHubRefURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"https://github.com/(?<username>[^/]+)/",
			"(?<gitRepositoryName>[^/]+)/tree/(?<refName>.+)"));
	private static final Map<String, RemoteGitRef> _gitHubRemoteGitRefs =
		new HashMap<>();

}