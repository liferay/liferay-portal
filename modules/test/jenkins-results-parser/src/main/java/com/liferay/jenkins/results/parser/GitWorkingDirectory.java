/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class GitWorkingDirectory {

	public static String getGitHubUserName(GitRemote gitRemote) {
		String remoteURL = gitRemote.getRemoteURL();

		if (!remoteURL.contains("github.com")) {
			throw new IllegalArgumentException(
				JenkinsResultsParserUtil.combine(
					gitRemote.getName(),
					" does not point to a GitHub repository"));
		}

		String userName = null;

		if (remoteURL.startsWith("https://github.com/")) {
			userName = remoteURL.substring("https://github.com/".length());
		}
		else {
			userName = remoteURL.substring("git@github.com:".length());
		}

		return userName.substring(0, userName.indexOf("/"));
	}

	public GitRemote addGitRemote(
		boolean force, String gitRemoteName, String remoteURL) {

		return addGitRemote(force, gitRemoteName, remoteURL, false);
	}

	public GitRemote addGitRemote(
		boolean force, String gitRemoteName, String remoteURL, boolean write) {

		if (gitRemoteExists(gitRemoteName)) {
			if (force) {
				removeGitRemote(getGitRemote(gitRemoteName));
			}
			else {
				throw new GitWorkingDirectoryIllegalArgumentException(
					this,
					JenkinsResultsParserUtil.combine(
						"Git remote ", gitRemoteName, " already exists"));
			}
		}

		GitRemote newGitRemote = new GitRemote(this, gitRemoteName, remoteURL);

		_gitRemotes.put(gitRemoteName, newGitRemote);

		if (write) {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT,
				new String[] {
					JenkinsResultsParserUtil.combine(
						"if [ \"$(git remote | grep ", gitRemoteName,
						")\" != \"\" ] ; then git remote remove ",
						gitRemoteName, " ; fi"),
					JenkinsResultsParserUtil.combine(
						"git remote add ", gitRemoteName, " ", remoteURL)
				});

			if (executionResult.getExitValue() != 0) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					JenkinsResultsParserUtil.combine(
						"Unable to write Git remote ", gitRemoteName, "\n",
						executionResult.getStandardError()));
			}
		}

		return newGitRemote;
	}

	public File archive(String fileName) {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10,
			"git archive HEAD -o " + fileName);

		if (executionResult.getExitValue() != 0) {
			File gitDirectory = getGitDirectory();

			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to create Git archive from ",
					gitDirectory.toString(), "\n",
					executionResult.getStandardError()));
		}

		return new File(getWorkingDirectory(), fileName);
	}

	public LocalGitBranch checkoutLocalGitBranch(
		Build.BranchInformation branchInformation) {

		checkoutUpstreamLocalGitBranch();

		LocalGitBranch localGitBranch = createLocalGitBranch(
			JenkinsResultsParserUtil.combine(
				branchInformation.getUpstreamBranchName(), "-temp-",
				String.valueOf(
					JenkinsResultsParserUtil.getCurrentTimeMillis())),
			true);

		try {
			RemoteGitRef cacheBranchFromGitHubDev =
				GitHubDevSyncUtil.fetchCacheBranchFromGitHubDev(
					this, branchInformation.getCachedRemoteGitRefName());

			localGitBranch = createLocalGitBranch(
				localGitBranch.getName(), true,
				cacheBranchFromGitHubDev.getSHA());
		}
		catch (Exception exception) {
			RemoteGitRef senderRemoteGitRef =
				branchInformation.getSenderRemoteGitRef();

			localGitBranch = getRebasedLocalGitBranch(
				localGitBranch.getName(),
				branchInformation.getSenderBranchName(),
				senderRemoteGitRef.getRemoteURL(),
				branchInformation.getSenderBranchSHA(),
				branchInformation.getUpstreamBranchName(),
				branchInformation.getUpstreamBranchSHA());
		}

		checkoutLocalGitBranch(localGitBranch);

		createLocalGitBranch(
			branchInformation.getUpstreamBranchName(), true,
			branchInformation.getUpstreamBranchSHA());

		return localGitBranch;
	}

	public void checkoutLocalGitBranch(LocalGitBranch localGitBranch) {
		checkoutLocalGitBranch(localGitBranch, "-f");
	}

	public void checkoutLocalGitBranch(
		LocalGitBranch localGitBranch, String options) {

		waitForIndexLock();

		StringBuilder sb = new StringBuilder();

		sb.append("git checkout ");

		if (options != null) {
			sb.append(options);
			sb.append(" ");
		}

		String branchName = localGitBranch.getName();

		sb.append(branchName);

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			1000 * 60 * 10, sb.toString());

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to checkout ", branchName, "\n",
					executionResult.getStandardError()));
		}

		int timeout = 0;

		File headFile = new File(_gitDirectory, "HEAD");

		String expectedContent = JenkinsResultsParserUtil.combine(
			"ref: refs/heads/", branchName);

		while (true) {
			String headContent = null;

			try {
				headContent = JenkinsResultsParserUtil.read(headFile);
			}
			catch (IOException ioException) {
				throw new GitWorkingDirectoryRuntimeException(
					this, "Unable to read file " + headFile.getPath(),
					ioException);
			}

			headContent = headContent.trim();

			if (headContent.equals(expectedContent)) {
				return;
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"HEAD file content is: ", headContent,
					". Waiting for branch to be updated."));

			JenkinsResultsParserUtil.sleep(5000);

			timeout++;

			if (timeout >= 59) {
				if (Objects.equals(branchName, getCurrentBranchName())) {
					return;
				}

				throw new GitWorkingDirectoryRuntimeException(
					this, "Unable to checkout branch " + branchName);
			}
		}
	}

	public void checkoutUpstreamLocalGitBranch() {
		if (!Objects.equals(getCurrentBranchName(), getUpstreamBranchName())) {
			checkoutLocalGitBranch(getUpstreamLocalGitBranch());
		}
	}

	public void cherryPick(LocalGitCommit localGitCommit) {
		cherryPick(localGitCommit.getSHA());
	}

	public void cherryPick(String localGitCommitSHA) {
		String cherryPickCommand = JenkinsResultsParserUtil.combine(
			"git cherry-pick " + localGitCommitSHA);

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, cherryPickCommand);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to cherry pick commit ", localGitCommitSHA, "\n",
					executionResult.getStandardError()));
		}
	}

	public void clean() {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10, "git clean -dfx");

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to clean Git repository\n",
					executionResult.getStandardError()));
		}
	}

	public void cleanTempBranches() {
		checkoutUpstreamLocalGitBranch();

		List<String> localGitBranchNames = getLocalGitBranchNames();

		List<String> tempBranchNames = new ArrayList<>(
			localGitBranchNames.size());

		String pattern = JenkinsResultsParserUtil.combine(
			".*", Pattern.quote(getUpstreamBranchName()), "-temp", ".*");

		for (String localGitBranchName : localGitBranchNames) {
			if (localGitBranchName.matches(pattern)) {
				tempBranchNames.add(localGitBranchName);
			}
		}

		if (!tempBranchNames.isEmpty()) {
			for (List<String> branchNames :
					Lists.partition(
						new ArrayList<>(tempBranchNames),
						_BRANCHES_DELETE_BATCH_SIZE)) {

				_deleteLocalGitBranches(branchNames.toArray(new String[0]));
			}
		}
	}

	public void commitFileToCurrentBranch(String fileName, String message) {
		String commitCommand = JenkinsResultsParserUtil.combine(
			"git add ", fileName, " ; git commit -m \"", message, "\"");

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, commitCommand);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to commit file ", fileName, "\n",
					executionResult.getStandardError()));
		}
	}

	public void commitStagedFilesToCurrentBranch(String message) {
		String commitCommand = JenkinsResultsParserUtil.combine(
			"git commit -m \"", message, "\" ");

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, commitCommand);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to commit staged files", "\n",
					executionResult.getStandardError()));
		}
	}

	public void configure(Map<String, String> configMap, String options) {
		String[] commands = new String[configMap.size()];

		int i = 0;

		for (Map.Entry<String, String> entry : configMap.entrySet()) {
			StringBuilder sb = new StringBuilder();

			sb.append("git config ");

			if ((options != null) && !options.isEmpty()) {
				sb.append(options);
				sb.append(" ");
			}

			sb.append(entry.getKey());
			sb.append(" ");
			sb.append(entry.getValue());

			commands[i] = sb.toString();

			i++;
		}

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, commands);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				"Unable to configure Git repository.\n" +
					executionResult.getStandardError());
		}
	}

	public void configure(
		String configName, String configValue, String options) {

		Map<String, String> configMap = new HashMap<>();

		configMap.put(configName, configValue);

		configure(configMap, options);
	}

	public LocalGitBranch createLocalGitBranch(LocalGitBranch localGitBranch) {
		return createLocalGitBranch(
			localGitBranch.getName(), false, localGitBranch.getSHA());
	}

	public LocalGitBranch createLocalGitBranch(
		LocalGitBranch localGitBranch, boolean force) {

		return createLocalGitBranch(
			localGitBranch.getName(), force, localGitBranch.getSHA());
	}

	public LocalGitBranch createLocalGitBranch(String localGitBranchName) {
		return createLocalGitBranch(localGitBranchName, false, null);
	}

	public LocalGitBranch createLocalGitBranch(
		String localGitBranchName, boolean force) {

		return createLocalGitBranch(localGitBranchName, force, null);
	}

	public LocalGitBranch createLocalGitBranch(
		String localGitBranchName, boolean force, String startPoint) {

		return createLocalGitBranch(
			localGitBranchName, force, startPoint, null);
	}

	public LocalGitBranch createLocalGitBranch(
		final String localGitBranchName, final boolean force,
		final String startPoint, final RemoteGitBranch sourceRemoteGitBranch) {

		Retryable<LocalGitBranch> createLocalGitBranchRetryable =
			new Retryable<LocalGitBranch>(true, 3, 0, true) {

				@Override
				public LocalGitBranch execute() {
					return _createLocalGitBranch(
						localGitBranchName, force, startPoint,
						sourceRemoteGitBranch);
				}

			};

		return createLocalGitBranchRetryable.executeWithRetries();
	}

	public String createPullRequest(
		final String body, final String pullRequestBranchName,
		final String receiverUserName, final String senderUserName,
		final String title) {

		Retryable<String> retryable = new Retryable<String>(true, 5, 0, true) {

			@Override
			public String execute() {
				JSONObject requestJSONObject = new JSONObject();

				requestJSONObject.put(
					"base", _upstreamBranchName
				).put(
					"body", body
				).put(
					"head", senderUserName + ":" + pullRequestBranchName
				).put(
					"title", title
				);

				String url = JenkinsResultsParserUtil.getGitHubAPIURL(
					_gitRepositoryName, receiverUserName, "pulls");

				JSONObject responseJSONObject;

				try {
					responseJSONObject = JenkinsResultsParserUtil.toJSONObject(
						url, requestJSONObject.toString());
				}
				catch (IOException ioException) {
					throw new RuntimeException(
						"Unable to create pull request", ioException);
				}

				String pullRequestURL = responseJSONObject.getString(
					"html_url");

				System.out.println(
					"Created a pull request at " + pullRequestURL);

				return pullRequestURL;
			}

		};

		return retryable.executeWithRetries();
	}

	public boolean deepenLocalGitBranch(
		LocalGitBranch localGitBranch, Date shallowSinceDate) {

		RemoteGitBranch sourceRemoteGitBranch =
			localGitBranch.getSourceRemoteGitBranch();

		if (sourceRemoteGitBranch == null) {
			return false;
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Deepening ", localGitBranch.getName(), " to ",
				JenkinsResultsParserUtil.toDateString(
					shallowSinceDate, "yyyy-MM-dd'T'HH:mm:ss'Z'", "UTC")));

		fetch(sourceRemoteGitBranch, shallowSinceDate);

		return true;
	}

	public void deleteLocalGitBranch(LocalGitBranch localGitBranch) {
		if (localGitBranch == null) {
			return;
		}

		deleteLocalGitBranches(Arrays.asList(localGitBranch));
	}

	public void deleteLocalGitBranch(String branchName) {
		deleteLocalGitBranch(getLocalGitBranch(branchName));
	}

	public void deleteLocalGitBranches(List<LocalGitBranch> localGitBranches) {
		if (localGitBranches.isEmpty()) {
			return;
		}

		Set<String> localGitBranchNames = new HashSet<>();

		for (LocalGitBranch localGitBranch : localGitBranches) {
			localGitBranchNames.add(localGitBranch.getName());
		}

		for (List<String> branchNames :
				Lists.partition(
					new ArrayList<>(localGitBranchNames),
					_BRANCHES_DELETE_BATCH_SIZE)) {

			_deleteLocalGitBranches(branchNames.toArray(new String[0]));
		}
	}

	public void deleteLockFiles() {
		File gitDirectory = getGitDirectory();

		String[] lockFileNames = gitDirectory.list(
			JenkinsResultsParserUtil.newFilenameFilter(".*\\.lock"));

		for (String lockFileName : lockFileNames) {
			boolean deleted = false;

			File lockFile = new File(gitDirectory, lockFileName);

			if (lockFile.exists() && lockFile.canWrite()) {
				System.out.println("Deleting lock file " + lockFile.getPath());

				deleted = lockFile.delete();
			}

			if (!deleted) {
				System.out.println("Unable to delete " + lockFile.getPath());
			}
		}
	}

	public void deleteRemoteGitBranch(RemoteGitBranch remoteGitBranch) {
		deleteRemoteGitBranches(Arrays.asList(remoteGitBranch));
	}

	public void deleteRemoteGitBranch(String branchName, GitRemote gitRemote) {
		deleteRemoteGitBranch(branchName, gitRemote.getRemoteURL());
	}

	public void deleteRemoteGitBranch(
		String branchName, RemoteGitRepository remoteGitRepository) {

		deleteRemoteGitBranch(branchName, remoteGitRepository.getRemoteURL());
	}

	public void deleteRemoteGitBranch(String branchName, String remoteURL) {
		deleteRemoteGitBranch(getRemoteGitBranch(branchName, remoteURL));
	}

	public void deleteRemoteGitBranches(
		List<RemoteGitBranch> remoteGitBranches) {

		if (remoteGitBranches.isEmpty()) {
			return;
		}

		Map<String, Set<String>> remoteURLGitBranchNameMap = new HashMap<>();

		for (RemoteGitBranch remoteGitBranch : remoteGitBranches) {
			if (remoteGitBranch == null) {
				continue;
			}

			RemoteGitRepository remoteGitRepository =
				remoteGitBranch.getRemoteGitRepository();

			String remoteURL = remoteGitRepository.getRemoteURL();

			if (!remoteURLGitBranchNameMap.containsKey(remoteURL)) {
				remoteURLGitBranchNameMap.put(remoteURL, new HashSet<String>());
			}

			Set<String> remoteGitBranchNames = remoteURLGitBranchNameMap.get(
				remoteURL);

			remoteGitBranchNames.add(remoteGitBranch.getName());

			remoteURLGitBranchNameMap.put(remoteURL, remoteGitBranchNames);
		}

		List<Callable<Boolean>> callables = new ArrayList<>(
			remoteURLGitBranchNameMap.size());

		for (final Map.Entry<String, Set<String>> remoteURLBranchNamesEntry :
				remoteURLGitBranchNameMap.entrySet()) {

			ParallelExecutor.SequentialCallable<Boolean> callable =
				new ParallelExecutor.SequentialCallable<Boolean>(
					remoteURLBranchNamesEntry.getKey()) {

					@Override
					public Boolean call() throws Exception {
						Set<String> allBranchNames =
							remoteURLBranchNamesEntry.getValue();

						if (allBranchNames.isEmpty()) {
							return true;
						}

						String remoteURL = remoteURLBranchNamesEntry.getKey();

						for (List<String> branchNames :
								Lists.partition(
									new ArrayList<String>(allBranchNames),
									_BRANCHES_DELETE_BATCH_SIZE)) {

							_deleteRemoteGitBranches(
								remoteURL, branchNames.toArray(new String[0]));
						}

						return true;
					}

				};

			callables.add(callable);
		}

		int maximumPoolSize = callables.size();

		if (JenkinsResultsParserUtil.isCloudCINode()) {
			maximumPoolSize = 1;
		}

		ParallelExecutor<Boolean> parallelExecutor = new ParallelExecutor<>(
			callables, true,
			JenkinsResultsParserUtil.getNewThreadPoolExecutor(
				maximumPoolSize, true),
			"deleteRemoteGitBranches");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	public void displayLog() {
		displayLog(1);
	}

	public void displayLog(int logNumber) {
		String command = "git log -n " + logNumber;

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 3,
			command);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to display log");
		}

		System.out.println();
		System.out.println(executionResult.getStandardOut());
		System.out.println();
	}

	public void fetch(GitRemote gitRemote) {
		fetch(gitRemote.getRemoteURL());
	}

	public void fetch(GitRemote gitRemote, boolean noTags) {
		fetch(gitRemote.getRemoteURL(), noTags);
	}

	public LocalGitBranch fetch(LocalGitBranch localGitBranch) {
		return fetch(null, localGitBranch);
	}

	public LocalGitBranch fetch(
		LocalGitBranch localGitBranch, boolean noTags,
		RemoteGitRef remoteGitRef) {

		return fetch(localGitBranch, noTags, remoteGitRef, 3);
	}

	public LocalGitBranch fetch(
		LocalGitBranch localGitBranch, boolean noTags,
		RemoteGitRef remoteGitRef, int retries) {

		return fetch(localGitBranch, noTags, remoteGitRef, retries, null);
	}

	public LocalGitBranch fetch(
		LocalGitBranch localGitBranch, boolean noTags,
		RemoteGitRef remoteGitRef, int retries, Date shallowSinceDate) {

		if (remoteGitRef == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Remote Git reference is null");
		}

		String remoteGitRefSHA = remoteGitRef.getSHA();

		RemoteGitBranch sourceRemoteGitBranch = null;

		if (remoteGitRef instanceof RemoteGitBranch) {
			sourceRemoteGitBranch = (RemoteGitBranch)remoteGitRef;
		}

		if ((shallowSinceDate == null) && localSHAExists(remoteGitRefSHA)) {
			System.out.println(
				remoteGitRefSHA + " already exists in Git repository");

			if ((localGitBranch != null) &&
				Objects.equals(localGitBranch.getSHA(), remoteGitRefSHA)) {

				return localGitBranch;
			}

			if (localGitBranch == null) {
				return createLocalGitBranch(
					remoteGitRef.getName(), true, remoteGitRefSHA,
					sourceRemoteGitBranch);
			}

			return createLocalGitBranch(
				localGitBranch.getName(), true, remoteGitRefSHA,
				sourceRemoteGitBranch);
		}

		StringBuilder gitBranchesSHAReportStringBuilder = new StringBuilder();

		gitBranchesSHAReportStringBuilder.append(
			_getLocalGitBranchesSHAReport());
		gitBranchesSHAReportStringBuilder.append("\nRemote Git branch\n    ");
		gitBranchesSHAReportStringBuilder.append(remoteGitRef.getName());
		gitBranchesSHAReportStringBuilder.append(": ");
		gitBranchesSHAReportStringBuilder.append(remoteGitRef.getSHA());

		RemoteGitRepository remoteGitRepository =
			remoteGitRef.getRemoteGitRepository();

		String remoteURL = remoteGitRepository.getRemoteURL();

		if (!JenkinsResultsParserUtil.isCloudCINode() &&
			JenkinsResultsParserUtil.isCINode() &&
			remoteURL.contains("github.com:liferay/")) {

			String gitHubDevRemoteURL = remoteURL.replace(
				"github.com:liferay/", "github-dev.liferay.com:liferay/");

			RemoteGitBranch gitHubDevRemoteGitBranch = getRemoteGitBranch(
				remoteGitRef.getName(), gitHubDevRemoteURL);

			if (gitHubDevRemoteGitBranch != null) {
				fetch(null, noTags, gitHubDevRemoteGitBranch);

				if (localSHAExists(remoteGitRefSHA)) {
					if (localGitBranch == null) {
						return createLocalGitBranch(
							remoteGitRef.getName(), true, remoteGitRefSHA,
							sourceRemoteGitBranch);
					}

					return createLocalGitBranch(
						localGitBranch.getName(), true, remoteGitRefSHA,
						sourceRemoteGitBranch);
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git fetch -f ");

		if (noTags) {
			sb.append("--no-tags ");
		}
		else {
			sb.append("--tags ");
		}

		if (shallowSinceDate != null) {
			String shallowSinceDateString =
				JenkinsResultsParserUtil.toDateString(
					shallowSinceDate, "yyyy-MM-dd'T'HH:mm:ss'Z'", "UTC");

			sb.append("--shallow-since=\"");
			sb.append(shallowSinceDateString);
			sb.append("\" ");
		}

		sb.append(remoteURL);

		String remoteGitRefName = remoteGitRef.getName();

		if ((remoteGitRefName != null) && !remoteGitRefName.isEmpty()) {
			sb.append(" ");
			sb.append(remoteGitRefName);

			if (localGitBranch != null) {
				sb.append(":");
				sb.append(localGitBranch.getName());
			}
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		GitUtil.ExecutionResult executionResult = null;

		try {
			executionResult = executeBashCommands(
				retries, GitUtil.MILLIS_RETRY_DELAY, true, 1000 * 60 * 15,
				sb.toString());
		}
		catch (TimeoutException timeoutException) {
			Matcher matcher = GitRemote.getRemoteURLMatcher(remoteURL);

			if (matcher.find()) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					JenkinsResultsParserUtil.combine(
						"Unable to fetch branch history.\n",
						"Please rebase with liferay/",
						matcher.group("gitRepositoryName"), " ",
						_upstreamBranchName, " and try again."));
			}
		}
		catch (Exception exception) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to fetch remote url ", remoteURL, "\n",
					exception.getMessage()));
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		if ((executionResult == null) ||
			(executionResult.getExitValue() != 0)) {

			System.out.println(gitBranchesSHAReportStringBuilder.toString());

			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to fetch remote Git ref ", remoteGitRefName,
					" after ",
					JenkinsResultsParserUtil.toDurationString(duration), "\n",
					executionResult.getStandardError()));
		}

		System.out.println(
			"Fetch completed in " +
				JenkinsResultsParserUtil.toDurationString(duration));

		if (duration > (1000 * 60)) {
			System.out.println(gitBranchesSHAReportStringBuilder.toString());
		}

		if (JenkinsResultsParserUtil.isCINode() &&
			(remoteGitRef instanceof RemoteGitBranch) &&
			remoteURL.contains("github.com:liferay/")) {

			String upstreamBranchSHA = getRemoteGitBranchSHA(
				remoteGitRef.getName(), getUpstreamGitRemote());

			if ((upstreamBranchSHA != null) &&
				upstreamBranchSHA.equals(remoteGitRef.getSHA())) {

				LocalGitBranch liferayGitHubLocalGitBranch =
					createLocalGitBranch(
						JenkinsResultsParserUtil.combine(
							"temp-", remoteGitRef.getName()),
						true, remoteGitRef.getSHA());

				List<GitRemote> gitHubDevGitRemotes =
					GitHubDevSyncUtil.getGitHubDevGitRemotes(this);

				try {
					GitHubDevSyncUtil.pushToAllRemotes(
						true, liferayGitHubLocalGitBranch,
						remoteGitRef.getName(), gitHubDevGitRemotes);
				}
				finally {
					if (localGitBranch == null) {
						deleteLocalGitBranch(liferayGitHubLocalGitBranch);
					}

					removeGitRemotes(gitHubDevGitRemotes);
				}
			}
		}

		if (localSHAExists(remoteGitRefSHA)) {
			if (localGitBranch == null) {
				return createLocalGitBranch(
					remoteGitRef.getName(), true, remoteGitRefSHA,
					sourceRemoteGitBranch);
			}

			return createLocalGitBranch(
				localGitBranch.getName(), true, remoteGitRefSHA,
				sourceRemoteGitBranch);
		}

		return null;
	}

	public LocalGitBranch fetch(
		LocalGitBranch localGitBranch, RemoteGitBranch remoteGitBranch) {

		return fetch(localGitBranch, true, remoteGitBranch);
	}

	public LocalGitBranch fetch(RemoteGitRef remoteGitRef) {
		return fetch(null, true, remoteGitRef);
	}

	public LocalGitBranch fetch(
		RemoteGitRef remoteGitRef, Date shallowSinceDate) {

		return fetch(null, true, remoteGitRef, 3, shallowSinceDate);
	}

	public LocalGitBranch fetch(RemoteGitRef remoteGitRef, int retries) {
		return fetch(null, true, remoteGitRef, retries);
	}

	public void fetch(RemoteGitRepository remoteGitRepository) {
		fetch(remoteGitRepository.getRemoteURL());
	}

	public void fetch(RemoteGitRepository remoteGitRepository, boolean noTags) {
		fetch(remoteGitRepository.getRemoteURL(), noTags);
	}

	public void fetch(String remoteURL) {
		fetch(remoteURL, true);
	}

	public void fetch(String remoteURL, boolean noTags) {
		if (remoteURL == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Remote URL is null");
		}

		if (!GitUtil.isValidRemoteURL(remoteURL)) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Invalid remote url " + remoteURL);
		}

		StringBuilder gitBranchesSHAReportStringBuilder = new StringBuilder();

		gitBranchesSHAReportStringBuilder.append(
			_getLocalGitBranchesSHAReport());
		gitBranchesSHAReportStringBuilder.append("\n");
		gitBranchesSHAReportStringBuilder.append(
			_getRemoteGitBranchesSHAReport(null, remoteURL));

		StringBuilder sb = new StringBuilder();

		sb.append("git fetch -f");

		if (noTags) {
			sb.append(" --no-tags");
		}
		else {
			sb.append(" --tags");
		}

		sb.append(" ");
		sb.append(remoteURL);
		sb.append(" refs/heads/*:refs/remotes/origin/*");

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		GitUtil.ExecutionResult executionResult = null;

		try {
			executionResult = executeBashCommands(
				3, GitUtil.MILLIS_RETRY_DELAY, true, 1000 * 60 * 30,
				sb.toString());
		}
		catch (TimeoutException timeoutException) {
			Matcher matcher = GitRemote.getRemoteURLMatcher(remoteURL);

			if (matcher.find()) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					JenkinsResultsParserUtil.combine(
						"Unable to fetch branch history.\n",
						"Please rebase with liferay/",
						matcher.group("gitRepositoryName"), " ",
						_upstreamBranchName, " and try again."));
			}
		}
		catch (Exception exception) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to fetch remote url ", remoteURL, "\n",
					exception.getMessage()));
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		if ((executionResult == null) ||
			(executionResult.getExitValue() != 0)) {

			System.out.println(gitBranchesSHAReportStringBuilder.toString());

			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to fetch remote url ", remoteURL, " after ",
					JenkinsResultsParserUtil.toDurationString(duration), "\n",
					executionResult.getStandardError()));
		}

		System.out.println(
			"Fetch completed in " +
				JenkinsResultsParserUtil.toDurationString(duration));

		if (duration > (1000 * 60)) {
			System.out.println(gitBranchesSHAReportStringBuilder.toString());
		}
	}

	public LocalGitBranch fetch(
		String branchName, LocalGitBranch localGitBranch) {

		if (localGitBranch == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Local Git branch is null");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git fetch -f --no-tags ");
		sb.append(String.valueOf(localGitBranch.getDirectory()));
		sb.append(" ");
		sb.append(localGitBranch.getName());

		if ((branchName != null) && !branchName.isEmpty()) {
			sb.append(":");
			sb.append(branchName);
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 30, sb.toString());

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to fetch from local Git repository ",
					String.valueOf(localGitBranch.getDirectory()), "\n",
					executionResult.getStandardError()));
		}

		String durationString = JenkinsResultsParserUtil.toDurationString(
			JenkinsResultsParserUtil.getCurrentTimeMillis() - start);

		System.out.println("Fetch completed in " + durationString);

		return createLocalGitBranch(
			localGitBranch.getName(), true, localGitBranch.getSHA());
	}

	public void fetchGitCommitParentFromUpstream(String sha) {
		GitRemote upstreamGitRemote = getUpstreamGitRemote();

		if (upstreamGitRemote == null) {
			return;
		}

		executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 15,
			JenkinsResultsParserUtil.combine(
				"git fetch -f --depth=2 ", upstreamGitRemote.getRemoteURL(),
				" ", sha));
	}

	public Set<File> findFiles(String fileName, String fileContentSnippet) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(fileContentSnippet)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git grep ");
		sb.append(fileContentSnippet);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(fileName)) {
			sb.append(" | grep ");
			sb.append(fileName);
		}

		GitUtil.ExecutionResult result = executeBashCommands(
			5, 1000, 60 * 1000, sb.toString());

		if (result.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to run: git grep");
		}

		String regex = JenkinsResultsParserUtil.combine(
			"(?<filePath>[^\\:]+)\\:.+");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(fileName)) {
			regex = JenkinsResultsParserUtil.combine(
				"(?<filePath>[^\\:]+/", fileName, ")\\:.+");
		}

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(result.getStandardOut());

		Set<File> files = new HashSet<>();

		while (matcher.find()) {
			String filePath = matcher.group("filePath");

			files.add(new File(getWorkingDirectory(), filePath.trim()));
		}

		return files;
	}

	public void gc() {
		int retries = 0;

		while (true) {
			GitUtil.ExecutionResult executionResult = null;

			boolean exceptionThrown = false;

			try {
				executionResult = executeBashCommands(
					GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
					60 * 60 * 1000, "git gc");
			}
			catch (RuntimeException runtimeException) {
				exceptionThrown = true;
			}

			System.out.println(executionResult.getStandardOut());

			if (exceptionThrown || (executionResult.getExitValue() != 0)) {
				String standardError = executionResult.getStandardError();

				Matcher matcher = _badRefPattern.matcher(standardError);

				if (matcher.find()) {
					File badRefFile = new File(
						getWorkingDirectory(),
						".git/" + matcher.group("badRef"));

					badRefFile.delete();
				}

				if (retries > 1) {
					throw new GitWorkingDirectoryRuntimeException(
						this,
						JenkinsResultsParserUtil.combine(
							"Unable to garbage collect Git\n", standardError));
				}
			}
			else {
				return;
			}

			retries++;

			JenkinsResultsParserUtil.sleep(GitUtil.MILLIS_RETRY_DELAY);

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Retry garbage collect Git in ",
					String.valueOf(GitUtil.MILLIS_RETRY_DELAY), "ms"));
		}
	}

	public List<String> getBranchNamesContainingSHA(String sha) {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 2,
			"git branch --contains " + sha);

		if (executionResult.getExitValue() != 0) {
			String standardError = executionResult.getStandardError();

			if (standardError.contains("no such commit")) {
				return Collections.emptyList();
			}

			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get branches with SHA ", sha, "\n",
					standardError));
		}

		String standardOut = executionResult.getStandardOut();

		if (standardOut.contains("no such commit")) {
			return Collections.emptyList();
		}

		String[] lines = standardOut.split("\n");

		List<String> branchNamesList = new ArrayList<>(lines.length - 1);

		for (String line : lines) {
			String branchName = line.trim();

			if (branchName.startsWith("* ")) {
				branchName = branchName.substring(2);
			}

			if (branchName.isEmpty()) {
				continue;
			}

			branchNamesList.add(branchName);
		}

		return branchNamesList;
	}

	public int getCommitCountBetweenBranches(
		String baseBranchName, String branchName) {

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10,
			JenkinsResultsParserUtil.combine(
				"git rev-list --count ", baseBranchName, "..", branchName));

		int exitValue = executionResult.getExitValue();

		if (exitValue == 0) {
			return Integer.parseInt(executionResult.getStandardOut());
		}

		System.out.println(executionResult.getStandardError());

		return 0;
	}

	public String getCurrentBranchName() {
		return getCurrentBranchName(false);
	}

	public String getCurrentBranchName(boolean required) {
		waitForIndexLock();

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, "git branch | grep \\*");

		if (executionResult.getExitValue() != 0) {
			System.out.println(executionResult.getStandardError());

			if (required) {
				throw new GitWorkingDirectoryRuntimeException(
					this, "Unable to find required local branch HEAD");
			}

			return null;
		}

		String currentBranchName = executionResult.getStandardOut();

		currentBranchName = currentBranchName.replaceFirst("\\*\\s*", "");

		currentBranchName = currentBranchName.trim();

		if (currentBranchName.isEmpty()) {
			return null;
		}

		return currentBranchName;
	}

	public LocalGitBranch getCurrentLocalGitBranch() {
		String currentBranchName = getCurrentBranchName();

		if (currentBranchName == null) {
			checkoutUpstreamLocalGitBranch();

			return getUpstreamLocalGitBranch();
		}

		return getLocalGitBranch(currentBranchName);
	}

	public List<File> getDeletedFilesList() {
		return getDeletedFilesList(false, null, null);
	}

	public List<File> getDeletedFilesList(
		boolean checkUnstagedFiles, List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers) {

		LocalGitBranch currentLocalGitBranch = getCurrentLocalGitBranch();

		if (currentLocalGitBranch == null) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to determine the current branch");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git diff --diff-filter=ADMR --name-only ");

		sb.append(
			getMergeBaseCommitSHA(
				currentLocalGitBranch,
				getLocalGitBranch(getUpstreamBranchName(), true)));

		if (!checkUnstagedFiles) {
			sb.append(" ");
			sb.append(currentLocalGitBranch.getSHA());
		}

		String gitDiffCommandString = sb.toString();

		List<File> deletedFiles = _deletedFilesMap.get(gitDiffCommandString);

		if (deletedFiles == null) {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT, gitDiffCommandString);

			if (executionResult.getExitValue() == 1) {
				return Collections.emptyList();
			}

			if (executionResult.getExitValue() != 0) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					"Unable to get current branch modified files\n" +
						executionResult.getStandardError());
			}

			deletedFiles = new ArrayList<>();

			String gitDiffOutput = executionResult.getStandardOut();

			for (String line : gitDiffOutput.split("\n")) {
				File deletedFile = new File(_workingDirectory, line);

				if (deletedFile.exists()) {
					continue;
				}

				deletedFiles.add(deletedFile);
			}

			_deletedFilesMap.put(gitDiffCommandString, deletedFiles);
		}

		return JenkinsResultsParserUtil.getIncludedFiles(
			excludesPathMatchers, includesPathMatchers, deletedFiles);
	}

	public String getGitConfigProperty(String gitConfigPropertyName) {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, "git config " + gitConfigPropertyName);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to read Git config property ",
					gitConfigPropertyName, "\n",
					executionResult.getStandardError()));
		}

		String configProperty = executionResult.getStandardOut();

		if (configProperty != null) {
			configProperty = configProperty.trim();
		}

		if ((configProperty == null) || configProperty.isEmpty()) {
			return null;
		}

		return configProperty;
	}

	public File getGitDirectory() {
		return _gitDirectory;
	}

	public GitRemote getGitRemote(String name) {
		if (name.equals("upstream") && JenkinsResultsParserUtil.isCINode()) {
			name = "upstream-temp";
		}

		if (_gitRemotes.isEmpty()) {
			getGitRemotes();
		}

		name = name.trim();

		return _gitRemotes.get(name);
	}

	public Set<String> getGitRemoteNames() {
		Map<String, GitRemote> gitRemotes = getGitRemotes();

		return gitRemotes.keySet();
	}

	public Map<String, GitRemote> getGitRemotes() {
		if (!_gitRemotes.isEmpty()) {
			return _gitRemotes;
		}

		int retries = 0;

		String standardOut = null;

		while (true) {
			if (retries > 1) {
				return _gitRemotes;
			}

			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT, "git remote -v");

			if (executionResult.getExitValue() != 0) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					JenkinsResultsParserUtil.combine(
						"Unable to get list of Git remotes\n",
						executionResult.getStandardError()));
			}

			standardOut = executionResult.getStandardOut();

			standardOut = standardOut.trim();

			if (!standardOut.isEmpty()) {
				break;
			}

			retries++;

			JenkinsResultsParserUtil.sleep(1000);
		}

		String[] lines = standardOut.split("\n");

		Arrays.sort(lines);

		int x = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (line == null) {
				continue;
			}

			line = line.trim();

			if (line.isEmpty()) {
				continue;
			}

			x = i;

			break;
		}

		lines = Arrays.copyOfRange(lines, x, lines.length);

		try {
			StringBuilder sb = new StringBuilder();

			sb.append("Found Git remotes: ");

			for (int i = 0; i < lines.length; i = i + 2) {
				GitRemote gitRemote = new GitRemote(
					this, Arrays.copyOfRange(lines, i, i + 2));

				if (i > 0) {
					sb.append(", ");
				}

				sb.append(gitRemote.getName());

				_gitRemotes.put(gitRemote.getName(), gitRemote);
			}

			System.out.println(sb);
		}
		catch (Throwable throwable) {
			System.out.println("Unable to parse Git remotes\n" + standardOut);

			throw throwable;
		}

		return _gitRemotes;
	}

	public String getGitRepositoryName() {
		return _gitRepositoryName;
	}

	public String getGitRepositoryUsername() {
		return _gitRepositoryUsername;
	}

	public String getLatestCommitSHA() {
		List<LocalGitCommit> localGitCommits = log(1);

		LocalGitCommit latestLocalGitCommit = localGitCommits.get(0);

		return latestLocalGitCommit.getSHA();
	}

	public LocalGitBranch getLocalGitBranch(String branchName) {
		return getLocalGitBranch(branchName, false);
	}

	public LocalGitBranch getLocalGitBranch(
		String branchName, boolean required) {

		if (branchName.equals(getUpstreamBranchName())) {
			return getUpstreamLocalGitBranch();
		}

		return _getLocalGitBranch(branchName, required);
	}

	public List<LocalGitBranch> getLocalGitBranches(String branchName) {
		String upstreamBranchName = getUpstreamBranchName();

		LocalGitRepository localGitRepository =
			GitRepositoryFactory.getLocalGitRepository(
				getGitRepositoryName(), upstreamBranchName,
				getWorkingDirectory());

		if (branchName != null) {
			try {
				RemoteGitBranch upstreamRemoteGitBranch = null;

				if (branchName.equals(upstreamBranchName)) {
					upstreamRemoteGitBranch = getUpstreamRemoteGitBranch();
				}

				return Arrays.asList(
					GitBranchFactory.newLocalGitBranch(
						localGitRepository, branchName,
						getLocalGitBranchSHA(branchName),
						upstreamRemoteGitBranch));
			}
			catch (Exception exception) {
				if (!branchName.equals(upstreamBranchName)) {
					return null;
				}
			}
		}

		List<String> localGitBranchNames = getLocalGitBranchNames();

		List<LocalGitBranch> localGitBranches = new ArrayList<>(
			localGitBranchNames.size());

		Map<String, String> localGitBranchesShaMap =
			getLocalGitBranchesShaMap();

		for (String localGitBranchName : localGitBranchNames) {
			if (!localGitBranchesShaMap.containsKey(localGitBranchName)) {
				System.out.println(
					"Unable to find SHA for local Git branch " +
						localGitBranchName);

				continue;
			}

			RemoteGitBranch upstreamRemoteGitBranch = null;

			if (localGitBranchName.equals(upstreamBranchName)) {
				upstreamRemoteGitBranch = getUpstreamRemoteGitBranch();
			}

			localGitBranches.add(
				GitBranchFactory.newLocalGitBranch(
					localGitRepository, localGitBranchName,
					localGitBranchesShaMap.get(localGitBranchName),
					upstreamRemoteGitBranch));
		}

		return localGitBranches;
	}

	public String getLocalGitBranchSHA(String localGitBranchName) {
		if (localGitBranchName == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Local branch name is null");
		}

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 2,
			"git rev-parse " + localGitBranchName);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to determine SHA of branch ", localGitBranchName,
					"\n", executionResult.getStandardError()));
		}

		return executionResult.getStandardOut();
	}

	public String getMergeBaseCommitSHA(LocalGitBranch... localGitBranches) {
		if (localGitBranches.length < 2) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this,
				"Unable to perform merge-base with less than two branches");
		}

		StringBuilder sb = new StringBuilder("git merge-base");

		for (LocalGitBranch localGitBranch : localGitBranches) {
			sb.append(" ");
			sb.append(localGitBranch.getName());
		}

		String command = sb.toString();

		for (int deepenAttempt = 0;; deepenAttempt++) {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT, command);

			if (executionResult.getExitValue() == 0) {
				return executionResult.getStandardOut();
			}

			boolean deepened = false;

			if (deepenAttempt < _DEEPEN_MAX_ATTEMPTS) {
				Date shallowSinceDate = new Date(
					JenkinsResultsParserUtil.getCurrentTimeMillis() -
						(_DEEPEN_STEP_SIZE_MILLIS << deepenAttempt));

				for (LocalGitBranch localGitBranch : localGitBranches) {
					if (deepenLocalGitBranch(
							localGitBranch, shallowSinceDate)) {

						deepened = true;
					}
				}
			}

			if (!deepened) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					JenkinsResultsParserUtil.combine(
						"Unable to get merge base commit SHA\n",
						executionResult.getStandardError()));
			}
		}
	}

	public String getMergeBaseCommitSHA(String... refNames) {
		if (refNames.length < 2) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this,
				"Unable to perform merge-base with less than two commits");
		}

		if (refNames.length > 2) {
			List<String> refNamesList = new ArrayList<>();

			refNamesList.add(getMergeBaseCommitSHA(refNames[0], refNames[1]));

			Collections.addAll(
				refNamesList, Arrays.copyOfRange(refNames, 2, refNames.length));

			return getMergeBaseCommitSHA(refNamesList.toArray(new String[0]));
		}

		StringBuilder sb = new StringBuilder("git merge-base");

		for (String refName : refNames) {
			sb.append(" ");
			sb.append(refName);
		}

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, sb.toString());

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get merge base commit SHA\n",
					executionResult.getStandardError()));
		}

		return executionResult.getStandardOut();
	}

	public String getMergeBaseWithUpstreamMasterSHA() {
		GitRemote upstreamGitRemote = getUpstreamGitRemote();

		if (upstreamGitRemote == null) {
			return "";
		}

		RemoteGitBranch upstreamMasterRemoteGitBranch = getRemoteGitBranch(
			"master", upstreamGitRemote, true);

		return getMergeBaseCommitSHA(
			getCurrentLocalGitBranch(), fetch(upstreamMasterRemoteGitBranch));
	}

	public List<File> getModifiedDirsList(
		boolean checkUnstagedFiles, List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers) {

		return getModifiedDirsList(
			checkUnstagedFiles, excludesPathMatchers, includesPathMatchers,
			getWorkingDirectory());
	}

	public List<File> getModifiedDirsList(
		boolean checkUnstagedFiles, List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers, File rootDirectory) {

		List<File> subdirectories = getSubdirectoriesContainingFiles(
			1, getModifiedFilesList(checkUnstagedFiles, null, null),
			rootDirectory);

		return JenkinsResultsParserUtil.getIncludedFiles(
			excludesPathMatchers, includesPathMatchers, subdirectories);
	}

	public Set<File> getModifiedFilesInCommitRange(
		String startingCommitSHA, String endingCommitSHA) {

		StringBuilder sb = new StringBuilder();

		sb.append("git log --name-status --no-renames --pretty=\"format:\" \"");
		sb.append(startingCommitSHA);
		sb.append("..");
		sb.append(endingCommitSHA);
		sb.append("\" | tr '\\t' ' ' | sed 's/^[^ ]* *//'");

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, sb.toString());

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get modified files\n",
					executionResult.getStandardError()));
		}

		String standardOut = executionResult.getStandardOut();

		Set<File> modifiedFiles = new HashSet<>();

		for (String modifiedFilePath : standardOut.split("\\s+")) {
			modifiedFiles.add(
				new File(getWorkingDirectory(), modifiedFilePath));
		}

		return modifiedFiles;
	}

	public List<File> getModifiedFilesList() {
		if (_modifiedFilesList != null) {
			return _modifiedFilesList;
		}

		_modifiedFilesList = getModifiedFilesList(false, null, null);

		return _modifiedFilesList;
	}

	public List<File> getModifiedFilesList(boolean checkUnstagedFiles) {
		if (!checkUnstagedFiles && (_modifiedFilesList != null)) {
			return _modifiedFilesList;
		}

		return getModifiedFilesList(checkUnstagedFiles, null, null);
	}

	public List<File> getModifiedFilesList(
		boolean checkUnstagedFiles, List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers) {

		if (!checkUnstagedFiles && (excludesPathMatchers == null) &&
			(includesPathMatchers == null) && !_useUpstreamMasterDiffBase &&
			(_modifiedFilesList != null)) {

			return _modifiedFilesList;
		}

		LocalGitBranch currentLocalGitBranch = getCurrentLocalGitBranch();

		if (currentLocalGitBranch == null) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to determine the current branch");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git diff --diff-filter=ADMR --name-only ");

		if (_useUpstreamMasterDiffBase) {
			sb.append(getMergeBaseWithUpstreamMasterSHA());
			sb.append(" ");
		}
		else {
			sb.append(
				getMergeBaseCommitSHA(
					currentLocalGitBranch,
					getLocalGitBranch(getUpstreamBranchName(), true)));
		}

		if (!checkUnstagedFiles) {
			sb.append(" ");
			sb.append(currentLocalGitBranch.getSHA());
		}

		String gitDiffCommandString = sb.toString();

		List<File> modifiedFiles = _modifiedFilesMap.get(gitDiffCommandString);

		if (modifiedFiles == null) {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT, gitDiffCommandString);

			if (executionResult.getExitValue() == 1) {
				return Collections.emptyList();
			}

			if (executionResult.getExitValue() != 0) {
				throw new GitWorkingDirectoryRuntimeException(
					this,
					"Unable to get current branch modified files\n" +
						executionResult.getStandardError());
			}

			modifiedFiles = new ArrayList<>();

			String gitDiffOutput = executionResult.getStandardOut();

			for (String line : gitDiffOutput.split("\n")) {
				modifiedFiles.add(new File(_workingDirectory, line));
			}

			_modifiedFilesMap.put(gitDiffCommandString, modifiedFiles);
		}

		return JenkinsResultsParserUtil.getIncludedFiles(
			excludesPathMatchers, includesPathMatchers, modifiedFiles);
	}

	public List<File> getModifiedFilesList(
		List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers) {

		return getModifiedFilesList(
			false, excludesPathMatchers, includesPathMatchers);
	}

	public LocalGitBranch getRebasedLocalGitBranch(PullRequest pullRequest) {
		return getRebasedLocalGitBranch(
			pullRequest.getLocalSenderBranchName(),
			pullRequest.getSenderBranchName(), pullRequest.getSenderRemoteURL(),
			pullRequest.getSenderSHA(),
			pullRequest.getUpstreamRemoteGitBranchName(),
			pullRequest.getUpstreamBranchSHA());
	}

	public LocalGitBranch getRebasedLocalGitBranch(
		String rebasedLocalGitBranchName, String senderBranchName,
		String senderRemoteURL, String senderSHA, String upstreamBranchName,
		String upstreamBranchSHA) {

		String currentBranchName = getCurrentBranchName();

		LocalGitBranch tempLocalGitBranch = null;

		try {
			if ((currentBranchName == null) ||
				currentBranchName.equals(rebasedLocalGitBranchName)) {

				tempLocalGitBranch = createLocalGitBranch(
					"temp-" + JenkinsResultsParserUtil.getCurrentTimeMillis());

				checkoutLocalGitBranch(tempLocalGitBranch);
			}

			RemoteGitBranch senderRemoteGitBranch = getRemoteGitBranch(
				senderBranchName, senderRemoteURL, true);

			if (localSHAExists(senderSHA)) {
				createLocalGitBranch(senderBranchName, true, senderSHA);
			}
			else {
				fetch(senderRemoteGitBranch);
			}

			RemoteGitBranch upstreamRemoteGitBranch = getRemoteGitBranch(
				upstreamBranchName, getUpstreamGitRemote(), true);

			if (upstreamBranchSHA == null) {
				upstreamBranchSHA = upstreamRemoteGitBranch.getSHA();
			}

			if (!localSHAExists(upstreamBranchSHA)) {
				fetch(upstreamRemoteGitBranch);
			}

			LocalGitBranch upstreamLocalGitBranch = createLocalGitBranch(
				upstreamBranchName, true, upstreamBranchSHA,
				upstreamRemoteGitBranch);

			LocalGitBranch rebasedLocalGitBranch = createLocalGitBranch(
				rebasedLocalGitBranchName, true, senderSHA,
				senderRemoteGitBranch);

			rebasedLocalGitBranch = rebase(
				true, upstreamLocalGitBranch, rebasedLocalGitBranch);

			clean();

			reset("--hard");

			return rebasedLocalGitBranch;
		}
		finally {
			if (tempLocalGitBranch != null) {
				deleteLocalGitBranch(tempLocalGitBranch);
			}
		}
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, GitRemote gitRemote) {

		return getRemoteGitBranch(
			remoteGitBranchName, gitRemote.getRemoteURL(), false);
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, GitRemote gitRemote, boolean required) {

		return getRemoteGitBranch(
			remoteGitBranchName, gitRemote.getRemoteURL(), required);
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, RemoteGitRepository remoteGitRepository) {

		return getRemoteGitBranch(
			remoteGitBranchName, remoteGitRepository.getRemoteURL(), false);
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, RemoteGitRepository remoteGitRepository,
		boolean required) {

		return getRemoteGitBranch(
			remoteGitBranchName, remoteGitRepository.getRemoteURL(), required);
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, String remoteURL) {

		return getRemoteGitBranch(remoteGitBranchName, remoteURL, false);
	}

	public RemoteGitBranch getRemoteGitBranch(
		String remoteGitBranchName, String remoteURL, boolean required) {

		RemoteGitBranch remoteGitBranch = GitUtil.getRemoteGitBranch(
			remoteGitBranchName, getWorkingDirectory(), remoteURL, required);

		if (remoteGitBranch != null) {
			return remoteGitBranch;
		}

		if (required) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to find required branch ", remoteGitBranchName,
					" from remote URL ", remoteURL));
		}

		return null;
	}

	public List<RemoteGitBranch> getRemoteGitBranches(GitRemote gitRemote) {
		return getRemoteGitBranches(null, gitRemote.getRemoteURL());
	}

	public List<RemoteGitBranch> getRemoteGitBranches(
		RemoteGitRepository remoteGitRepository) {

		return getRemoteGitBranches(null, remoteGitRepository.getRemoteURL());
	}

	public List<RemoteGitBranch> getRemoteGitBranches(String remoteURL) {
		return getRemoteGitBranches(null, remoteURL);
	}

	public List<RemoteGitBranch> getRemoteGitBranches(
		String remoteGitBranchName, GitRemote gitRemote) {

		return getRemoteGitBranches(
			remoteGitBranchName, gitRemote.getRemoteURL());
	}

	public List<RemoteGitBranch> getRemoteGitBranches(
		String remoteGitBranchName, RemoteGitRepository remoteGitRepository) {

		return getRemoteGitBranches(
			remoteGitBranchName, remoteGitRepository.getRemoteURL());
	}

	public List<RemoteGitBranch> getRemoteGitBranches(
		String remoteGitBranchName, String remoteURL) {

		return GitUtil.getRemoteGitBranches(
			remoteGitBranchName, _workingDirectory, remoteURL);
	}

	public List<String> getRemoteGitBranchNames(GitRemote gitRemote) {
		return getRemoteGitBranchNames(gitRemote.getRemoteURL());
	}

	public List<String> getRemoteGitBranchNames(
		RemoteGitRepository remoteGitRepository) {

		return getRemoteGitBranchNames(remoteGitRepository.getRemoteURL());
	}

	public List<String> getRemoteGitBranchNames(String remoteURL) {
		List<String> remoteGitBranchNames = new ArrayList<>();

		List<RemoteGitBranch> remoteGitBranches = getRemoteGitBranches(
			remoteURL);

		for (RemoteGitBranch remoteGitBranch : remoteGitBranches) {
			remoteGitBranchNames.add(remoteGitBranch.getName());
		}

		return remoteGitBranchNames;
	}

	public String getRemoteGitBranchSHA(
		String remoteGitBranchName, GitRemote gitRemote) {

		return getRemoteGitBranchSHA(
			remoteGitBranchName, gitRemote.getRemoteURL());
	}

	public String getRemoteGitBranchSHA(
		String remoteGitBranchName, RemoteGitRepository remoteGitRepository) {

		return getRemoteGitBranchSHA(
			remoteGitBranchName, remoteGitRepository.getRemoteURL());
	}

	public String getRemoteGitBranchSHA(
		String remoteGitBranchName, String remoteURL) {

		if (remoteGitBranchName == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Remote branch name is null");
		}

		if (remoteURL == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Remote URL is null");
		}

		if (!GitUtil.isValidRemoteURL(remoteURL)) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Invalid remote url " + remoteURL);
		}

		if (JenkinsResultsParserUtil.isCINode() &&
			!JenkinsResultsParserUtil.isCloudCINode() &&
			remoteURL.startsWith("git@github.com:liferay/")) {

			try {
				String sha = JenkinsResultsParserUtil.toString(
					JenkinsResultsParserUtil.combine(
						"http://",
						JenkinsResultsParserUtil.getBuildProperty(
							"mirrors.hostname"),
						"/github.com/liferay/sha/", getGitRepositoryName(), "(",
						URLEncoder.encode(
							remoteGitBranchName, StandardCharsets.UTF_8.name()),
						").txt"),
					false);

				sha = sha.trim();

				if (JenkinsResultsParserUtil.isValidGitSHA(sha)) {
					return sha;
				}
			}
			catch (Exception exception) {
				System.out.println(
					"Unable to retrieve cached SHA information: " +
						exception.getMessage());
			}
		}

		String command = JenkinsResultsParserUtil.combine(
			"git ls-remote -h ", remoteURL, " ", remoteGitBranchName);

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10, command);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get remote branch SHA ", remoteURL, " ",
					remoteGitBranchName, "\n",
					executionResult.getStandardError()));
		}

		String input = executionResult.getStandardOut();

		for (String line : input.split("\n")) {
			Matcher matcher = GitRemote.gitLsRemotePattern.matcher(line);

			if (matcher.find()) {
				return matcher.group("sha");
			}
		}

		return null;
	}

	public RemoteGitRef getRemoteGitRef(
		String remoteGitRefName, GitRemote gitRemote, boolean required) {

		return getRemoteGitRef(
			remoteGitRefName, gitRemote.getRemoteURL(), required);
	}

	public RemoteGitRef getRemoteGitRef(
		String remoteGitRefName, String remoteURL, boolean required) {

		RemoteGitRef remoteGitRef = GitUtil.getRemoteGitRef(
			remoteGitRefName, getWorkingDirectory(), remoteURL, required);

		if (remoteGitRef != null) {
			return remoteGitRef;
		}

		if (required) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to find required ref ", remoteGitRefName,
					" from remote URL ", remoteURL));
		}

		return null;
	}

	public String getUpstreamBranchName() {
		return _upstreamBranchName;
	}

	public GitRemote getUpstreamGitRemote() {
		Map<String, GitRemote> gitRemotes = getGitRemotes();

		GitRemote upstreamGitRemote = gitRemotes.get("upstream-temp");

		if (upstreamGitRemote == null) {
			upstreamGitRemote = gitRemotes.get("upstream");
		}

		if (upstreamGitRemote != null) {
			return upstreamGitRemote;
		}

		for (GitRemote gitRemote : gitRemotes.values()) {
			String remoteURL = gitRemote.getRemoteURL();

			if (remoteURL.contains("github.com:liferay/liferay-portal.git") ||
				remoteURL.contains("github.com/liferay/liferay-portal.git")) {

				return gitRemote;
			}
		}

		String gitRepositoryName = getGitRepositoryName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitRepositoryName)) {
			return null;
		}

		return addGitRemote(
			true, "upstream",
			JenkinsResultsParserUtil.combine(
				"git@github.com:liferay/", gitRepositoryName, ".git"));
	}

	public LocalGitBranch getUpstreamLocalGitBranch() {
		String upstreamBranchName = getUpstreamBranchName();

		if (localGitBranchExists(upstreamBranchName)) {
			return _getLocalGitBranch(upstreamBranchName, true);
		}

		RemoteGitBranch upstreamRemoteGitBranch = getRemoteGitBranch(
			upstreamBranchName, getGitRemote("upstream"));

		if (upstreamRemoteGitBranch == null) {
			upstreamRemoteGitBranch = getRemoteGitBranch(
				upstreamBranchName, getGitRemote("origin"));
		}

		String upstreamBranchSHA = upstreamRemoteGitBranch.getSHA();

		fetch(upstreamRemoteGitBranch);

		String currentBranchName = getCurrentBranchName();

		if (currentBranchName == null) {
			checkoutLocalGitBranch(
				createLocalGitBranch(
					upstreamBranchName + "-temp-" +
						JenkinsResultsParserUtil.getCurrentTimeMillis(),
					true, upstreamBranchSHA));
		}

		return createLocalGitBranch(
			upstreamBranchName, true, upstreamBranchSHA);
	}

	public String getUpstreamMasterAheadBehindDescription() {
		GitRemote upstreamGitRemote = getUpstreamGitRemote();

		if (upstreamGitRemote == null) {
			return "";
		}

		executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT,
			JenkinsResultsParserUtil.combine(
				"git fetch ", upstreamGitRemote.getName(), " master"));

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT,
			JenkinsResultsParserUtil.combine(
				"git rev-list --left-right --count ",
				upstreamGitRemote.getName(), "/master...",
				getCurrentBranchName()));

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get ahead/behind count for current branch\n",
					executionResult.getStandardError()));
		}

		String output = executionResult.getStandardOut();

		output = output.trim();

		String[] values = output.split("\\s+");

		String ahead = values[1];
		String behind = values[0];

		StringBuilder sb = new StringBuilder();

		if (!ahead.equals("0")) {
			sb.append(ahead);
			sb.append(" commits ahead");
		}

		if (!behind.equals("0")) {
			if (!ahead.equals("0")) {
				sb.append(", ");
			}

			sb.append(behind);
			sb.append(" commits behind");
		}

		if (sb.length() == 0) {
			return "up to date with";
		}

		sb.append(" of");

		return sb.toString();
	}

	public RemoteGitBranch getUpstreamRemoteGitBranch() {
		return getRemoteGitBranch(
			getUpstreamBranchName(), getUpstreamGitRemote());
	}

	public File getWorkingDirectory() {
		return _workingDirectory;
	}

	public boolean gitRemoteExists(String gitRemoteName) {
		if (getGitRemote(gitRemoteName) != null) {
			return true;
		}

		return false;
	}

	public boolean isOnlyPoshiFilesModified() {
		return isOnlyMatchingFilesModified(_poshiFileNamesMultiPattern);
	}

	public boolean isRemoteGitRepositoryAlive(String remoteURL) {
		String command = JenkinsResultsParserUtil.combine(
			"git ls-remote -h ", remoteURL, " HEAD");

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			1000 * 60 * 10, command);

		if (executionResult.getExitValue() != 0) {
			System.out.println("Unable to connect to " + remoteURL);

			return false;
		}

		System.out.println(remoteURL + " is alive");

		return true;
	}

	public boolean localGitBranchExists(String branchName) {
		waitForIndexLock();

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT,
			"git branch | grep [\\s\\*]*" + branchName + "$");

		if (executionResult.getExitValue() == 0) {
			String standardOut = executionResult.getStandardOut();

			return !standardOut.isEmpty();
		}

		return false;
	}

	public boolean localSHAExists(String sha) {
		String command = "git cat-file -t " + sha;

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 3,
			command);

		if (executionResult.getExitValue() == 0) {
			return true;
		}

		return false;
	}

	public List<LocalGitCommit> log(int num) {
		return _log(0, num, null, null);
	}

	public List<LocalGitCommit> log(int num, File file) {
		return _log(0, num, file, null);
	}

	public List<LocalGitCommit> log(int start, int num) {
		return _log(start, num, null, null);
	}

	public List<LocalGitCommit> log(int start, int num, String sha) {
		return _log(start, num, null, sha);
	}

	public List<LocalGitCommit> log(String startingRef, String endingRef)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append("git log ");
		sb.append(startingRef);
		sb.append("..");
		sb.append(endingRef);
		sb.append(" --patch --stat");
		sb.append(" --pretty=format:'%x00%H %ct %ae %s%n'");

		GitUtil.ExecutionResult result = executeBashCommands(
			5, 1000, 30 * 1000, sb.toString());

		if (result.getExitValue() != 0) {
			throw new IOException(
				JenkinsResultsParserUtil.combine(
					"Unable to find log between ", startingRef, " and ",
					endingRef));
		}

		List<LocalGitCommit> localGitCommits = new ArrayList<>();

		String gitLog = result.getStandardOut();

		gitLog = gitLog.replaceAll("Finished executing Bash commands.", "");

		for (String commitSection : gitLog.split(_GIT_COMMIT_LOG_SEPARATOR)) {
			if (commitSection.isEmpty()) {
				continue;
			}

			int newlineIndex = commitSection.indexOf('\n');

			String gitLogEntity;
			String patch;

			if (newlineIndex == -1) {
				gitLogEntity = commitSection.trim();
				patch = "";
			}
			else {
				gitLogEntity = commitSection.substring(0, newlineIndex);

				gitLogEntity = gitLogEntity.trim();

				patch = commitSection.substring(newlineIndex + 1);
			}

			localGitCommits.add(getLocalGitCommit(gitLogEntity, patch));
		}

		Collections.reverse(localGitCommits);

		return localGitCommits;
	}

	public Map<String, List<LocalGitCommit>> logByTicketId(int number) {
		return _groupLocalGitCommits(log(number));
	}

	public Map<String, List<LocalGitCommit>> logByTicketId(
			String branch1, String branch2)
		throws IOException {

		return _groupLocalGitCommits(log(branch1, branch2));
	}

	public List<RemoteGitBranch> pushBranchesToRemoteGitRepository(
		boolean force, LocalGitBranch localGitBranch,
		List<String> remoteGitBranchNames, GitRemote gitRemote) {

		if (localGitBranch == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Local Git branch is null");
		}

		if (gitRemote == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Git remote is null");
		}

		String remoteURL = gitRemote.getRemoteURL();

		if (!GitUtil.isValidRemoteURL(remoteURL)) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Invalid remote URL " + remoteURL);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git push ");

		if (force) {
			sb.append("-f ");
		}

		sb.append(remoteURL);
		sb.append(" ");

		if (remoteGitBranchNames.isEmpty()) {
			sb.append(localGitBranch.getName());
		}
		else {
			for (String remoteGitBranchName : remoteGitBranchNames) {
				sb.append(localGitBranch.getName());

				if (remoteGitBranchName != null) {
					sb.append(":");
					sb.append(remoteGitBranchName);
				}

				sb.append(" ");
			}
		}

		try {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				1000 * 60 * 10, sb.toString());

			if (executionResult.getExitValue() != 0) {
				return null;
			}
		}
		catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();

			return null;
		}

		List<RemoteGitBranch> remoteGitBranches = new ArrayList<>();

		if (remoteGitBranchNames.isEmpty()) {
			remoteGitBranches.add(
				(RemoteGitBranch)GitBranchFactory.newRemoteGitRef(
					GitRepositoryFactory.getRemoteGitRepository(remoteURL),
					null, localGitBranch.getSHA(), "heads"));
		}
		else {
			for (String remoteGitBranchName : remoteGitBranchNames) {
				remoteGitBranches.add(
					(RemoteGitBranch)GitBranchFactory.newRemoteGitRef(
						GitRepositoryFactory.getRemoteGitRepository(remoteURL),
						remoteGitBranchName, localGitBranch.getSHA(), "heads"));
			}
		}

		return remoteGitBranches;
	}

	public RemoteGitBranch pushToRemoteGitRepository(
		boolean force, LocalGitBranch localGitBranch,
		String remoteGitBranchName, GitRemote gitRemote) {

		return pushToRemoteGitRepository(
			force, localGitBranch, remoteGitBranchName,
			gitRemote.getRemoteURL());
	}

	public RemoteGitBranch pushToRemoteGitRepository(
		boolean force, LocalGitBranch localGitBranch,
		String remoteGitBranchName, RemoteGitRepository remoteGitRepository) {

		return pushToRemoteGitRepository(
			force, localGitBranch, remoteGitBranchName,
			remoteGitRepository.getRemoteURL());
	}

	public RemoteGitBranch pushToRemoteGitRepository(
		boolean force, LocalGitBranch localGitBranch,
		String remoteGitBranchName, String remoteURL) {

		if (localGitBranch == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Local Git branch is null");
		}

		if (remoteURL == null) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Remote URL is null");
		}

		if (!GitUtil.isValidRemoteURL(remoteURL)) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Invalid remote url " + remoteURL);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git push ");

		if (force) {
			sb.append("-f ");
		}

		sb.append(remoteURL);
		sb.append(" ");
		sb.append(localGitBranch.getName());

		if (remoteGitBranchName != null) {
			sb.append(":");
			sb.append(remoteGitBranchName);
		}

		try {
			GitUtil.ExecutionResult executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				1000 * 60 * 10, sb.toString());

			if (executionResult.getExitValue() != 0) {
				return null;
			}
		}
		catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();

			return null;
		}

		return (RemoteGitBranch)GitBranchFactory.newRemoteGitRef(
			GitRepositoryFactory.getRemoteGitRepository(remoteURL),
			remoteGitBranchName, localGitBranch.getSHA(), "heads");
	}

	public LocalGitBranch rebase(
		boolean abortOnFail, LocalGitBranch baseLocalGitBranch,
		LocalGitBranch localGitBranch) {

		List<String> branchNamesContainingSHA = getBranchNamesContainingSHA(
			baseLocalGitBranch.getSHA());

		if (branchNamesContainingSHA.contains(localGitBranch.getName())) {
			checkoutLocalGitBranch(localGitBranch);

			return localGitBranch;
		}

		getMergeBaseCommitSHA(baseLocalGitBranch, localGitBranch);

		checkoutLocalGitBranch(baseLocalGitBranch);

		reset("--hard " + baseLocalGitBranch.getSHA());

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			1000 * 60 * 10,
			JenkinsResultsParserUtil.combine(
				"git rebase ", baseLocalGitBranch.getName(), " ",
				localGitBranch.getName()));

		if (executionResult.getExitValue() == 0) {
			return getCurrentLocalGitBranch();
		}

		if (abortOnFail) {
			rebaseAbort();
		}

		throw new GitWorkingDirectoryRuntimeException(
			this,
			JenkinsResultsParserUtil.combine(
				"Unable to rebase ", localGitBranch.getName(), " to ",
				baseLocalGitBranch.getName(), "\n",
				executionResult.getStandardError()));
	}

	public void rebaseAbort() {
		rebaseAbort(true);
	}

	public void rebaseAbort(boolean ignoreFailure) {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, "git rebase --abort");

		if (!ignoreFailure && (executionResult.getExitValue() != 0)) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to abort rebase\n",
					executionResult.getStandardError()));
		}
	}

	public boolean refContainsSHA(LocalGitBranch localGitBranch, String sha) {
		String name = localGitBranch.getName();

		for (int deepenAttempt = 0;; deepenAttempt++) {
			if (refContainsSHA(name, sha)) {
				return true;
			}

			boolean deepened = false;

			if (deepenAttempt < _DEEPEN_MAX_ATTEMPTS) {
				Date shallowSinceDate = new Date(
					JenkinsResultsParserUtil.getCurrentTimeMillis() -
						(_DEEPEN_STEP_SIZE_MILLIS << deepenAttempt));

				deepened = deepenLocalGitBranch(
					localGitBranch, shallowSinceDate);
			}

			if (!deepened) {
				return false;
			}
		}
	}

	public boolean refContainsSHA(String ref, String sha) {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 2,
			"git merge-base --is-ancestor " + sha + " " + ref);

		if (executionResult.getExitValue() == 0) {
			return true;
		}

		return false;
	}

	public boolean remoteGitBranchExists(
		String branchName, GitRemote gitRemote) {

		return remoteGitBranchExists(branchName, gitRemote.getRemoteURL());
	}

	public boolean remoteGitBranchExists(
		String branchName, RemoteGitRepository remoteGitRepository) {

		return remoteGitBranchExists(
			branchName, remoteGitRepository.getRemoteURL());
	}

	public boolean remoteGitBranchExists(String branchName, String remoteURL) {
		if (getRemoteGitBranch(branchName, remoteURL) != null) {
			return true;
		}

		return false;
	}

	public void removeGitRemote(GitRemote gitRemote) {
		if ((gitRemote == null) || !gitRemoteExists(gitRemote.getName())) {
			return;
		}

		_gitRemotes.remove(gitRemote.getName());
	}

	public void removeGitRemotes(List<GitRemote> gitRemotes) {
		for (GitRemote gitRemote : gitRemotes) {
			removeGitRemote(gitRemote);
		}
	}

	public void reset(String options) {
		String command = "git reset " + options;

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			2, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 5, command);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to reset\n", executionResult.getStandardError()));
		}
	}

	public void setCacheBashCommands(boolean cacheBashCommands) {
		_cacheBashCommands = cacheBashCommands;
	}

	public void setUseUpstreamMasterDiffBase(
		boolean useUpstreamMasterDiffBase) {

		_useUpstreamMasterDiffBase = useUpstreamMasterDiffBase;
	}

	public void stageFileInCurrentLocalGitBranch(String fileName) {
		String command = "git stage " + fileName;

		GitUtil.ExecutionResult result = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, command);

		if (result.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to stage file " + fileName);
		}
	}

	public String status() {
		for (int i = 0; i < 5; i++) {
			try {
				String gitStatus = _status();

				gitStatus = gitStatus.replaceAll(
					"Finished executing Bash commands.", "");

				if (!gitStatus.startsWith("On branch")) {
					throw new GitWorkingDirectoryRuntimeException(
						this, "Unable to run: git status");
				}

				return gitStatus;
			}
			catch (RuntimeException runtimeException) {
				runtimeException.printStackTrace();

				JenkinsResultsParserUtil.sleep(1000);
			}
		}

		throw new GitWorkingDirectoryRuntimeException(
			this, "Unable to run: git status");
	}

	protected GitWorkingDirectory(
			String upstreamBranchName, String workingDirectoryPath)
		throws IOException {

		this(upstreamBranchName, workingDirectoryPath, null);
	}

	protected GitWorkingDirectory(
			String upstreamBranchName, String workingDirectoryPath,
			String gitRepositoryName)
		throws IOException {

		setWorkingDirectory(workingDirectoryPath);

		_upstreamBranchName = upstreamBranchName;

		GitRemote upstreamTempGitRemote = getGitRemote("upstream-temp");

		if (upstreamTempGitRemote != null) {
			removeGitRemote(upstreamTempGitRemote);
		}

		waitForIndexLock();

		if ((gitRepositoryName == null) || gitRepositoryName.equals("")) {
			gitRepositoryName = _loadGitRepositoryName();
		}

		_gitRepositoryName = gitRepositoryName;

		if (JenkinsResultsParserUtil.isCINode()) {
			String remoteGitRepositoryName = _getRemoteGitRepositoryName();

			RemoteGitRepository remoteGitRepository =
				GitRepositoryFactory.getRemoteGitRepository(
					"github.com", remoteGitRepositoryName,
					JenkinsResultsParserUtil.getUpstreamUserName(
						remoteGitRepositoryName, getUpstreamBranchName()));

			addGitRemote(
				true, "upstream-temp", remoteGitRepository.getRemoteURL());
		}

		_gitRepositoryUsername = loadGitRepositoryUsername();
	}

	protected synchronized GitUtil.ExecutionResult executeBashCommands(
			int maxRetries, long retryDelay, boolean throwExceptions,
			long timeout, String... commands)
		throws IOException, TimeoutException {

		String command = String.join(" ", commands);

		if (_cacheBashCommands && _executionResults.containsKey(command)) {
			System.out.println("Using cached execution for: " + command);

			return _executionResults.get(command);
		}

		GitUtil.ExecutionResult executionResult = GitUtil.executeBashCommands(
			maxRetries, retryDelay, throwExceptions, timeout, _workingDirectory,
			commands);

		_executionResults.put(command, executionResult);

		return executionResult;
	}

	protected synchronized GitUtil.ExecutionResult executeBashCommands(
		int maxRetries, long retryDelay, long timeout, String... commands) {

		try {
			return executeBashCommands(
				maxRetries, retryDelay, false, timeout, commands);
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(
				"Unable to execute bash commands: " + Arrays.toString(commands),
				exception);
		}
	}

	protected Boolean getGitConfigPropertyBoolean(
		String gitConfigPropertyName, Boolean defaultValue) {

		String gitConfigProperty = getGitConfigProperty(gitConfigPropertyName);

		if (gitConfigProperty == null) {
			if (defaultValue != null) {
				return defaultValue;
			}

			return null;
		}

		return Boolean.parseBoolean(gitConfigProperty);
	}

	protected Map<String, String> getLocalGitBranchesShaMap() {
		String command = JenkinsResultsParserUtil.combine(
			"git ls-remote -h ",
			JenkinsResultsParserUtil.getCanonicalPath(getWorkingDirectory()));

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			1000 * 60 * 10, command);

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get local Git branch SHAs\n",
					executionResult.getStandardError()));
		}

		String input = executionResult.getStandardOut();

		String[] inputLines = input.split("\n");

		Map<String, String> localGitBranchesShaMap = new HashMap<>();

		for (String line : inputLines) {
			Matcher matcher = GitRemote.gitLsRemotePattern.matcher(line);

			if (matcher.find()) {
				localGitBranchesShaMap.put(
					matcher.group("name"), matcher.group("sha"));
			}
		}

		return localGitBranchesShaMap;
	}

	protected List<String> getLocalGitBranchNames() {
		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT,
			"git for-each-ref refs/heads --format=\"%(refname)\"");

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to get local branch names\n",
					executionResult.getStandardError()));
		}

		String standardOut = executionResult.getStandardOut();

		return toShortNameList(Arrays.asList(standardOut.split("\n")));
	}

	protected LocalGitCommit getLocalGitCommit(String gitLogEntity) {
		return getLocalGitCommit(gitLogEntity, null);
	}

	protected LocalGitCommit getLocalGitCommit(
		String gitLogEntity, String patch) {

		Matcher matcher = _gitLogEntityPattern.matcher(gitLogEntity);

		if (!matcher.matches()) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this, "Unable to find Git SHA");
		}

		int unixTimestamp = Integer.valueOf(matcher.group("commitTime"));

		long epochTimestamp = (long)unixTimestamp * 1000;

		if (patch != null) {
			return GitCommitFactory.newLocalGitCommit(
				matcher.group("email"), this, matcher.group("message"), patch,
				matcher.group("sha"), epochTimestamp);
		}

		return GitCommitFactory.newLocalGitCommit(
			matcher.group("email"), this, matcher.group("message"),
			matcher.group("sha"), epochTimestamp);
	}

	protected File getRealGitDirectory(File gitFile) {
		String gitFileContent = null;

		try {
			gitFileContent = JenkinsResultsParserUtil.read(gitFile);
		}
		catch (IOException ioException) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to find real .git directory", ioException);
		}

		for (String line : gitFileContent.split("\n")) {
			Matcher matcher = _gitDirectoryPathPattern.matcher(line);

			if (!matcher.find()) {
				continue;
			}

			return new File(matcher.group(1));
		}

		throw new GitWorkingDirectoryIllegalArgumentException(
			this, "Unable to find real Git directory in " + gitFile.getPath());
	}

	protected List<File> getSubdirectoriesContainingFiles(
		int depth, List<File> files, File rootDirectory) {

		return JenkinsResultsParserUtil.getDirectoriesContainingFiles(
			JenkinsResultsParserUtil.getSubdirectories(depth, rootDirectory),
			files);
	}

	protected boolean isOnlyMatchingFilesModified(MultiPattern multiPattern) {
		for (File modifiedFile : getModifiedFilesList()) {
			if (multiPattern.matches(modifiedFile.getName()) == null) {
				return false;
			}
		}

		return true;
	}

	protected String loadGitRepositoryUsername() {
		GitRemote upstreamGitRemote = getUpstreamGitRemote();

		String remoteURL = upstreamGitRemote.getRemoteURL();

		int x = remoteURL.indexOf(":") + 1;
		int y = remoteURL.indexOf("/");

		return remoteURL.substring(x, y);
	}

	protected void setWorkingDirectory(String workingDirectoryPath)
		throws IOException {

		_workingDirectory = new File(workingDirectoryPath);

		if (!_workingDirectory.exists()) {
			throw new GitWorkingDirectoryFileNotFoundException(
				this, _workingDirectory.getPath() + " is unavailable");
		}

		_gitDirectory = new File(workingDirectoryPath, ".git");

		if (_gitDirectory.isFile()) {
			_gitDirectory = getRealGitDirectory(_gitDirectory);
		}

		if (!_gitDirectory.exists()) {
			throw new GitWorkingDirectoryFileNotFoundException(
				this, _gitDirectory.getPath() + " is unavailable");
		}
	}

	protected List<String> toShortNameList(List<String> fullNameList) {
		List<String> shortNames = new ArrayList<>(fullNameList.size());

		for (String fullName : fullNameList) {
			shortNames.add(fullName.substring("refs/heads/".length()));
		}

		return shortNames;
	}

	protected void waitForIndexLock() {
		int retries = 0;

		File file = new File(_gitDirectory, "index.lock");

		while (file.exists()) {
			System.out.println("Waiting for index.lock to be cleared.");

			JenkinsResultsParserUtil.sleep(5000);

			retries++;

			if (retries >= 24) {
				file.delete();
			}
		}
	}

	protected static class GitWorkingDirectoryFileNotFoundException
		extends FileNotFoundException {

		public GitWorkingDirectoryFileNotFoundException(
			GitWorkingDirectory gitWorkingDirectory, String message) {

			this("File not found exception", gitWorkingDirectory, message);
		}

		protected GitWorkingDirectoryFileNotFoundException(
			String exceptionName, GitWorkingDirectory gitWorkingDirectory,
			String message) {

			super(
				JenkinsResultsParserUtil.combine(
					exceptionName, "  occurred in ",
					gitWorkingDirectory.getGitRepositoryName(), "\n", message));
		}

	}

	protected static class GitWorkingDirectoryIllegalArgumentException
		extends GitWorkingDirectoryRuntimeException {

		public GitWorkingDirectoryIllegalArgumentException(
			GitWorkingDirectory gitWorkingDirectory, String message) {

			this(gitWorkingDirectory, message, null);
		}

		public GitWorkingDirectoryIllegalArgumentException(
			GitWorkingDirectory gitWorkingDirectory, String message,
			Throwable throwable) {

			super(
				"Illegal argument exception", gitWorkingDirectory, message,
				throwable);
		}

		public GitWorkingDirectoryIllegalArgumentException(
			GitWorkingDirectory gitWorkingDirectory, Throwable throwable) {

			this(gitWorkingDirectory, null, throwable);
		}

	}

	protected static class GitWorkingDirectoryRuntimeException
		extends RuntimeException {

		public GitWorkingDirectoryRuntimeException(
			GitWorkingDirectory gitWorkingDirectory, String message) {

			this(gitWorkingDirectory, message, null);
		}

		public GitWorkingDirectoryRuntimeException(
			GitWorkingDirectory gitWorkingDirectory, String message,
			Throwable throwable) {

			this("Runtime exception", gitWorkingDirectory, message, throwable);
		}

		protected GitWorkingDirectoryRuntimeException(
			String exceptionName, GitWorkingDirectory gitWorkingDirectory,
			String message, Throwable throwable) {

			super(
				JenkinsResultsParserUtil.combine(
					exceptionName, "  occurred in ",
					gitWorkingDirectory.getGitRepositoryName(), "\n", message),
				throwable);
		}

	}

	private static List<String> _getBuildPropertyAsList(String key) {
		try {
			return JenkinsResultsParserUtil.getBuildPropertyAsList(true, key);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build property " + key, ioException);
		}
	}

	private LocalGitBranch _createLocalGitBranch(
		String localGitBranchName, boolean force, String startPoint,
		RemoteGitBranch sourceRemoteGitBranch) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(localGitBranchName)) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "No branch is specified");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(startPoint)) {
			startPoint = "HEAD";
		}

		if (!localSHAExists(startPoint)) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "SHA does not exist " + startPoint);
		}

		List<String> commands = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		sb.append("git branch ");

		if (force) {
			sb.append("-f ");
		}

		sb.append(localGitBranchName);
		sb.append(" ");
		sb.append(startPoint);

		String createLocalGitBranchCommand = sb.toString();

		String currentBranchName = getCurrentBranchName();

		if (currentBranchName == null) {
			commands.add(createLocalGitBranchCommand);

			commands.add("git checkout -f " + localGitBranchName);
		}
		else if (Objects.equals(currentBranchName, localGitBranchName)) {
			commands.add("git checkout --detach");

			commands.add(createLocalGitBranchCommand);

			commands.add("git checkout -f " + currentBranchName);
		}
		else {
			commands.add(createLocalGitBranchCommand);
		}

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY, 240 * 1000,
			commands.toArray(new String[0]));

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to create local branch ", localGitBranchName,
					" at ", startPoint, "\n",
					executionResult.getStandardError()));
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Created local branch ", localGitBranchName, " at ",
				startPoint));

		LocalGitBranch localGitBranch = getLocalGitBranch(
			localGitBranchName, true);

		if (sourceRemoteGitBranch == null) {
			return localGitBranch;
		}

		return GitBranchFactory.newLocalGitBranch(
			localGitBranch.getLocalGitRepository(), localGitBranch.getName(),
			localGitBranch.getSHA(), sourceRemoteGitBranch);
	}

	private boolean _deleteLocalGitBranches(String... branchNames) {
		if (branchNames.length > _BRANCHES_DELETE_BATCH_SIZE) {
			throw new GitWorkingDirectoryIllegalArgumentException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to delete more than ",
					String.valueOf(_BRANCHES_DELETE_BATCH_SIZE),
					" local Git branches at once"));
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git branch -D -f ");

		String joinedBranchNames = JenkinsResultsParserUtil.join(
			" ", branchNames);

		sb.append(joinedBranchNames);

		GitUtil.ExecutionResult executionResult = null;

		boolean exceptionThrown = false;

		try {
			executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				1000 * 60 * 10, sb.toString());
		}
		catch (RuntimeException runtimeException) {
			exceptionThrown = true;
		}

		if (exceptionThrown || (executionResult.getExitValue() != 0)) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Unable to delete local branches:", "\n    ",
					joinedBranchNames.replaceAll("\\s", "\n    ")));

			if (executionResult != null) {
				System.out.println(executionResult.getStandardError());
			}

			return false;
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Deleted local branches:", "\n    ",
				joinedBranchNames.replaceAll("\\s", "\n    ")));

		return true;
	}

	private boolean _deleteRemoteGitBranches(
		String remoteURL, String... branchNames) {

		StringBuilder sb = new StringBuilder();

		sb.append("git push --delete ");
		sb.append(remoteURL);
		sb.append(" ");

		String joinedBranchNames = JenkinsResultsParserUtil.join(
			" ", branchNames);

		sb.append(joinedBranchNames);

		GitUtil.ExecutionResult executionResult = null;

		boolean exceptionThrown = false;

		try {
			executionResult = executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				1000 * 60 * 10, sb.toString());
		}
		catch (RuntimeException runtimeException) {
			exceptionThrown = true;
		}

		if (exceptionThrown || (executionResult.getExitValue() != 0)) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Unable to delete ", remoteURL, " branches:\n    ",
					joinedBranchNames.replaceAll("\\s", "\n    "), "\n",
					executionResult.getStandardError()));

			return false;
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Deleted ", remoteURL, " branches:", "\n    ",
				joinedBranchNames.replaceAll("\\s", "\n    ")));

		return true;
	}

	private String _getGitRepositoryName(GitRemote gitRemote) {
		if (gitRemote == null) {
			return null;
		}

		String remoteURL = gitRemote.getRemoteURL();

		int x = remoteURL.lastIndexOf("/") + 1;

		int y = remoteURL.indexOf(".git");

		if (y == -1) {
			y = remoteURL.length();
		}

		String gitRepositoryName = remoteURL.substring(x, y);

		if (gitRepositoryName.equals("liferay-jenkins-tools-private")) {
			return gitRepositoryName;
		}

		if ((gitRepositoryName.equals("liferay-plugins-ee") ||
			 gitRepositoryName.equals("liferay-portal-ee")) &&
			_upstreamBranchName.equals("master")) {

			gitRepositoryName = gitRepositoryName.replace("-ee", "");
		}

		if (gitRepositoryName.contains("-private") &&
			!_upstreamBranchName.contains("-private")) {

			gitRepositoryName = gitRepositoryName.replace("-private", "");
		}

		return gitRepositoryName;
	}

	private LocalGitBranch _getLocalGitBranch(
		String branchName, boolean required) {

		List<LocalGitBranch> localGitBranches = getLocalGitBranches(branchName);

		if ((localGitBranches != null) && !localGitBranches.isEmpty()) {
			return localGitBranches.get(0);
		}

		if (required) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to find required branch ", branchName, " from ",
					String.valueOf(getWorkingDirectory())));
		}

		return null;
	}

	private String _getLocalGitBranchesSHAReport() {
		StringBuilder sb = new StringBuilder("Local Git branches");

		for (LocalGitBranch localGitBranch : getLocalGitBranches(null)) {
			sb.append("\n    ");

			sb.append(localGitBranch.getName());
			sb.append(": ");
			sb.append(localGitBranch.getSHA());
		}

		return sb.toString();
	}

	private GitRemote _getOriginGitRemote() {
		Map<String, GitRemote> gitRemotes = getGitRemotes();

		return gitRemotes.get("origin");
	}

	private String _getRemoteGitBranchesSHAReport(
		String remoteGitBranchName, String remoteURL) {

		StringBuilder sb = new StringBuilder("Remote Git branches");

		for (RemoteGitBranch remoteGitBranch :
				getRemoteGitBranches(remoteGitBranchName, remoteURL)) {

			sb.append("\n    ");

			sb.append(remoteGitBranch.getName());
			sb.append(": ");
			sb.append(remoteGitBranch.getSHA());
		}

		return sb.toString();
	}

	private String _getRemoteGitRepositoryName() {
		String gitRepositoryName = getGitRepositoryName();

		if (_publicOnlyGitRepositoryNames.contains(gitRepositoryName)) {
			return GitUtil.getPublicRepositoryName(gitRepositoryName);
		}

		if (_privateOnlyGitRepositoryNames.contains(gitRepositoryName)) {
			return GitUtil.getPrivateRepositoryName(gitRepositoryName);
		}

		String upstreamBranchName = getUpstreamBranchName();

		if (upstreamBranchName.startsWith("faro-v") ||
			upstreamBranchName.equals("master")) {

			return GitUtil.getPublicRepositoryName(gitRepositoryName);
		}

		return GitUtil.getPrivateRepositoryName(gitRepositoryName);
	}

	private Map<String, List<LocalGitCommit>> _groupLocalGitCommits(
		List<LocalGitCommit> localGitCommits) {

		Map<String, List<LocalGitCommit>> localGitCommitsMap =
			new LinkedHashMap<>();

		for (LocalGitCommit localGitCommit : localGitCommits) {
			List<LocalGitCommit> localGitCommitsEntry =
				localGitCommitsMap.computeIfAbsent(
					localGitCommit.getTicketId(), k -> new ArrayList<>());

			localGitCommitsEntry.add(localGitCommit);
		}

		return localGitCommitsMap;
	}

	private String _loadGitRepositoryName() {
		String gitRepositoryName = _getGitRepositoryName(_getOriginGitRemote());

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitRepositoryName)) {
			gitRepositoryName = _getGitRepositoryName(getUpstreamGitRemote());
		}

		return gitRepositoryName;
	}

	private List<LocalGitCommit> _log(
		int start, int num, File file, String sha) {

		List<LocalGitCommit> localGitCommits = new ArrayList<>(num);

		String gitLog = _log(
			start, num, file,
			_GIT_COMMIT_LOG_SEPARATOR_FORMAT + "%H %ct %ae %s%n", sha);

		gitLog = gitLog.replaceAll("Finished executing Bash commands.", "");

		for (String commitSection : gitLog.split(_GIT_COMMIT_LOG_SEPARATOR)) {
			if (commitSection.isEmpty()) {
				continue;
			}

			int newlineIndex = commitSection.indexOf('\n');

			String gitLogEntity = null;
			String patch = null;

			if (newlineIndex == -1) {
				gitLogEntity = commitSection.trim();
				patch = "";
			}
			else {
				gitLogEntity = commitSection.substring(0, newlineIndex);

				gitLogEntity = gitLogEntity.trim();

				patch = commitSection.substring(newlineIndex + 1);
			}

			localGitCommits.add(getLocalGitCommit(gitLogEntity, patch));
		}

		return localGitCommits;
	}

	private String _log(
		int start, int num, File file, String format, String sha) {

		if ((sha == null) || sha.isEmpty()) {
			sha = "HEAD";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("git log ");

		if (file != null) {
			sb.append("-n ");
			sb.append(num);
			sb.append(" ");
		}
		else {
			sb.append(sha);
			sb.append("~");
			sb.append(start + num);
			sb.append("..");
			sb.append(sha);
			sb.append("~");
			sb.append(start);
		}

		sb.append(" --patch --pretty=format:'");
		sb.append(format);
		sb.append("'");
		sb.append(" --stat");

		if (file != null) {
			sb.append(" ");
			sb.append(JenkinsResultsParserUtil.getCanonicalPath(file));
		}

		GitUtil.ExecutionResult result = executeBashCommands(
			5, 1000, 30 * 1000, sb.toString());

		if (result.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to run: git log");
		}

		return result.getStandardOut();
	}

	private String _status() {
		String command = "git status";

		GitUtil.ExecutionResult result = executeBashCommands(
			GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
			GitUtil.MILLIS_TIMEOUT, command);

		if (result.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Unable to run: git status");
		}

		return result.getStandardOut();
	}

	private static final int _BRANCHES_DELETE_BATCH_SIZE = 5;

	private static final int _DEEPEN_MAX_ATTEMPTS = 5;

	private static final long _DEEPEN_STEP_SIZE_MILLIS =
		1000L * 60L * 60L * 24L * 30L;

	private static final String _GIT_COMMIT_LOG_SEPARATOR = "\u0000";

	private static final String _GIT_COMMIT_LOG_SEPARATOR_FORMAT = "%x00";

	private static final Pattern _badRefPattern = Pattern.compile(
		"fatal: bad object (?<badRef>.+/HEAD)");
	private static final Map<String, List<File>> _deletedFilesMap =
		new ConcurrentHashMap<>();
	private static final Pattern _gitDirectoryPathPattern = Pattern.compile(
		"gitdir\\: (.*)\\s*");
	private static final Pattern _gitLogEntityPattern = Pattern.compile(
		"(?<sha>[0-9a-f]{40}) (?<commitTime>\\d+) (?<email>[^\\s]*) " +
			"(?<message>.*)");
	private static final Map<String, List<File>> _modifiedFilesMap =
		new ConcurrentHashMap<>();
	private static final MultiPattern _poshiFileNamesMultiPattern =
		new MultiPattern(
			".*\\.function", ".*\\.macro", ".*\\.path", ".*\\.prose",
			".*\\.testcase");
	private static final List<String> _privateOnlyGitRepositoryNames =
		Collections.synchronizedList(
			_getBuildPropertyAsList(
				"git.working.directory.private.only.repository.names"));
	private static final List<String> _publicOnlyGitRepositoryNames =
		Collections.synchronizedList(
			_getBuildPropertyAsList(
				"git.working.directory.public.only.repository.names"));

	private boolean _cacheBashCommands;
	private final Map<String, GitUtil.ExecutionResult> _executionResults =
		new HashMap<>();
	private File _gitDirectory;
	private final Map<String, GitRemote> _gitRemotes =
		new ConcurrentHashMap<>();
	private final String _gitRepositoryName;
	private final String _gitRepositoryUsername;
	private List<File> _modifiedFilesList;
	private final String _upstreamBranchName;
	private boolean _useUpstreamMasterDiffBase;
	private File _workingDirectory;

}