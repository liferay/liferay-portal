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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class GitHubDevSyncUtil {

	public static void clone(String repositoryName, File workingDirectory) {
		List<String> usedGitHubDevRemoteHostnames = new ArrayList<>();

		while (true) {
			String gitHubDevRemoteHostname =
				JenkinsResultsParserUtil.getRandomGitHubDevNodeHostname(
					usedGitHubDevRemoteHostnames);

			usedGitHubDevRemoteHostnames.add(gitHubDevRemoteHostname);

			String gitHubDevRemoteURL = JenkinsResultsParserUtil.combine(
				"git@", gitHubDevRemoteHostname, ":liferay/", repositoryName);

			try {
				GitUtil.clone(gitHubDevRemoteURL, workingDirectory);
			}
			catch (Exception exception) {
				String message = JenkinsResultsParserUtil.combine(
					"Unable to clone ", repositoryName, " from ",
					gitHubDevRemoteURL, ".");

				if (usedGitHubDevRemoteHostnames.size() == 3) {
					throw new RuntimeException(message, exception);
				}

				System.out.println("Retrying: " + message);
			}

			break;
		}
	}

	public static RemoteGitBranch fetchCacheBranchFromGitHubDev(
		GitWorkingDirectory gitWorkingDirectory, String cacheBranchName) {

		List<GitRemote> gitHubDevGitRemotes = getGitHubDevGitRemotes(
			gitWorkingDirectory);

		List<GitRemote> gitRemotes = new ArrayList<>(gitHubDevGitRemotes);

		try {
			while (!gitHubDevGitRemotes.isEmpty()) {
				GitRemote gitHubDevGitRemote = getRandomGitRemote(
					gitHubDevGitRemotes);

				gitHubDevGitRemotes.remove(gitHubDevGitRemote);

				if (!gitWorkingDirectory.remoteGitBranchExists(
						cacheBranchName, gitHubDevGitRemote.getRemoteURL())) {

					continue;
				}

				try {
					RemoteGitBranch cachedRemoteGitBranch =
						gitWorkingDirectory.getRemoteGitBranch(
							cacheBranchName, gitHubDevGitRemote, true);

					gitWorkingDirectory.fetch(cachedRemoteGitBranch, 1);

					return cachedRemoteGitBranch;
				}
				catch (RuntimeException runtimeException) {
					String message = JenkinsResultsParserUtil.combine(
						"Unable to fetch cached remote Git branch ",
						cacheBranchName, "\n", runtimeException.getMessage());

					if (gitHubDevGitRemotes.isEmpty()) {
						System.out.println(message);

						throw new RuntimeException(
							JenkinsResultsParserUtil.combine(
								"Unable to fetch ", cacheBranchName,
								" from git@github-dev.com"),
							runtimeException);
					}

					System.out.println("Retrying: " + message);
				}
			}

			return null;
		}
		finally {
			gitWorkingDirectory.removeGitRemotes(gitRemotes);
		}
	}

	public static String getCacheBranchName(PullRequest pullRequest) {
		return getCacheBranchName(
			pullRequest.getReceiverUsername(), pullRequest.getSenderUsername(),
			pullRequest.getSenderSHA(), pullRequest.getUpstreamBranchSHA());
	}

	public static String getCacheBranchName(RemoteGitRef remoteGitRef) {
		return getCacheBranchName(
			remoteGitRef.getUsername(), remoteGitRef.getUsername(),
			remoteGitRef.getSHA(), remoteGitRef.getSHA());
	}

	public static String getCacheBranchName(
		String receiverUsername, String senderUsername, String senderSHA,
		String upstreamSHA) {

		return JenkinsResultsParserUtil.combine(
			"cache-", receiverUsername, "-", upstreamSHA, "-", senderUsername,
			"-", senderSHA);
	}

	public static List<GitRemote> getGitHubDevGitRemotes(
		GitWorkingDirectory gitWorkingDirectory) {

		List<String> gitHubDevRemoteURLs = getGitHubDevRemoteURLs(
			gitWorkingDirectory);

		List<GitRemote> gitRemotes = new ArrayList<>();

		for (int i = 0; i < gitHubDevRemoteURLs.size(); i++) {
			String gitHubDevRemoteURL = gitHubDevRemoteURLs.get(i);

			String gitHubDevRemoteName = "git-hub-dev-remote-" + i;

			GitRemote gitRemote = gitWorkingDirectory.getGitRemote(
				gitHubDevRemoteName);

			if ((gitRemote == null) ||
				!gitHubDevRemoteURL.equals(gitRemote.getRemoteURL())) {

				gitRemote = gitWorkingDirectory.addGitRemote(
					true, gitHubDevRemoteName, gitHubDevRemoteURL);
			}

			gitRemotes.add(gitRemote);
		}

		return gitRemotes;
	}

	public static String synchronizeToGitHubDev(
		GitWorkingDirectory gitWorkingDirectory, String receiverUsername,
		String senderBranchName, String senderUsername, String senderBranchSHA,
		String upstreamBranchSHA) {

		return synchronizeToGitHubDev(
			gitWorkingDirectory, receiverUsername, 0, senderBranchName,
			senderUsername, senderBranchSHA, upstreamBranchSHA);
	}

	public static String synchronizeToGitHubDev(
		LocalGitBranch localGitBranch,
		WorkspaceGitRepository workspaceGitRepository) {

		return synchronizeToGitHubDev(
			localGitBranch, workspaceGitRepository, 0);
	}

	public static boolean synchronizeUpstreamBranchToGitHubDev(
		GitWorkingDirectory gitWorkingDirectory,
		LocalGitBranch localGitBranch) {

		return synchronizeUpstreamBranchToGitHubDev(
			gitWorkingDirectory, localGitBranch, 0);
	}

	protected static void cacheBranch(
		GitWorkingDirectory gitWorkingDirectory, LocalGitBranch localGitBranch,
		String cacheBranchName, GitRemote gitRemote, long timestamp) {

		gitWorkingDirectory.pushBranchesToRemoteGitRepository(
			true, localGitBranch,
			Arrays.asList(cacheBranchName, cacheBranchName + "-" + timestamp),
			gitRemote);
	}

	protected static void cacheBranches(
		final GitWorkingDirectory gitWorkingDirectory,
		final LocalGitBranch localGitBranch, final String cacheBranchName,
		List<GitRemote> gitHubDevGitRemotes, final String upstreamUsername) {

		final long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		final RemoteGitBranch upstreamRemoteGitBranch =
			gitWorkingDirectory.getRemoteGitBranch(
				gitWorkingDirectory.getUpstreamBranchName(),
				gitWorkingDirectory.getGitRemote("upstream"), true);

		List<Callable<Object>> callables = new ArrayList<>();

		for (final GitRemote gitHubDevGitRemote : gitHubDevGitRemotes) {
			Callable<Object> callable = new SafeCallable<Object>(
				gitHubDevGitRemote.getHostname()) {

				@Override
				public Object safeCall() {
					cacheBranch(
						gitWorkingDirectory, localGitBranch, cacheBranchName,
						gitHubDevGitRemote, start);

					if (upstreamUsername.equals("liferay")) {
						LocalGitBranch upstreamLocalGitBranch =
							gitWorkingDirectory.getLocalGitBranch(
								upstreamRemoteGitBranch.getName(), true);

						gitWorkingDirectory.pushToRemoteGitRepository(
							true, upstreamLocalGitBranch,
							upstreamRemoteGitBranch.getName(),
							gitHubDevGitRemote);
					}

					return null;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "cacheBranches");

		try {
			parallelExecutor.execute(60L * 60L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		System.out.println(
			"Cache branches pushed up in " +
				JenkinsResultsParserUtil.toDurationString(duration));
	}

	protected static void checkoutUpstreamLocalGitBranch(
		GitWorkingDirectory gitWorkingDirectory, String upstreamBranchSHA) {

		LocalGitBranch upstreamLocalGitBranch = updateUpstreamLocalGitBranch(
			gitWorkingDirectory, upstreamBranchSHA);

		if (upstreamLocalGitBranch != null) {
			gitWorkingDirectory.checkoutLocalGitBranch(upstreamLocalGitBranch);
		}
	}

	protected static void copyUpstreamRefsToHeads(
			GitWorkingDirectory gitWorkingDirectory)
		throws IOException {

		File gitDir = gitWorkingDirectory.getGitDirectory();

		File headsDir = new File(gitDir, "refs/heads");
		File upstreamDir = new File(gitDir, "refs/remotes/upstream-temp");

		for (File file : upstreamDir.listFiles()) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Copying ", headsDir.getPath(), " to ",
					upstreamDir.getPath()));
			JenkinsResultsParserUtil.copy(
				file, new File(headsDir, file.getName()));
		}
	}

	protected static void deleteCacheLocalGitBranches(
		String excludeBranchName, GitWorkingDirectory gitWorkingDirectory) {

		for (String localGitBranchName :
				gitWorkingDirectory.getLocalGitBranchNames()) {

			if (localGitBranchName.matches(_cacheBranchPattern.pattern()) &&
				!localGitBranchName.equals(excludeBranchName)) {

				gitWorkingDirectory.deleteLocalGitBranch(localGitBranchName);
			}
		}
	}

	protected static void deleteCacheRemoteGitBranch(
		String cacheBranchName, GitWorkingDirectory gitWorkingDirectory,
		Map<String, RemoteGitBranch> remoteGitBranches) {

		List<RemoteGitBranch> cacheRemoteGitBranches = new ArrayList<>(2);

		for (Map.Entry<String, RemoteGitBranch> entry :
				remoteGitBranches.entrySet()) {

			String remoteGitBranchName = entry.getKey();

			if (!remoteGitBranchName.startsWith(cacheBranchName)) {
				continue;
			}

			cacheRemoteGitBranches.add(entry.getValue());
		}

		if (!cacheRemoteGitBranches.isEmpty()) {
			deleteRemoteGitBranches(
				gitWorkingDirectory, cacheRemoteGitBranches);
		}
	}

	protected static void deleteExpiredCacheBranches(
		GitRemote gitRemote, long timestamp) {

		int branchCount = 0;
		int deleteCount = 0;
		long oldestBranchAge = Long.MIN_VALUE;

		Map<String, RemoteGitBranch> remoteGitBranches = new HashMap<>();

		GitWorkingDirectory gitWorkingDirectory =
			gitRemote.getGitWorkingDirectory();

		for (RemoteGitBranch remoteGitBranch :
				gitWorkingDirectory.getRemoteGitBranches(gitRemote)) {

			remoteGitBranches.put(remoteGitBranch.getName(), remoteGitBranch);
		}

		List<RemoteGitBranch> expiredRemoteGitBranches = new ArrayList<>();

		for (Map.Entry<String, RemoteGitBranch> entry :
				remoteGitBranches.entrySet()) {

			RemoteGitBranch remoteGitBranch = entry.getValue();

			String remoteGitBranchName = remoteGitBranch.getName();

			Matcher matcher = _cacheBranchPattern.matcher(remoteGitBranchName);

			if (!matcher.matches()) {
				continue;
			}

			String lastBlock = matcher.group(2);

			if (!lastBlock.matches("\\d+")) {
				continue;
			}

			branchCount++;

			long remoteGitBranchTimestamp = Long.parseLong(lastBlock);

			long branchAge = timestamp - remoteGitBranchTimestamp;

			if (branchAge > _MILLIS_BRANCH_EXPIRATION) {
				String gitRepositoryBaseRemoteGitBranchName =
					remoteGitBranchName.replaceAll("(.*)-\\d+", "$1");

				RemoteGitBranch gitRepositoryBaseRemoteGitBranch =
					remoteGitBranches.get(gitRepositoryBaseRemoteGitBranchName);

				if (gitRepositoryBaseRemoteGitBranch != null) {
					expiredRemoteGitBranches.add(
						gitRepositoryBaseRemoteGitBranch);
				}

				expiredRemoteGitBranches.add(remoteGitBranch);

				deleteCount++;
			}
			else {
				oldestBranchAge = Math.max(oldestBranchAge, branchAge);
			}
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Deleting ", String.valueOf(expiredRemoteGitBranches.size()),
				" branches from ", gitRemote.getRemoteURL()));

		deleteRemoteGitBranches(gitWorkingDirectory, expiredRemoteGitBranches);

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Found ", String.valueOf(branchCount), " cache branches on ",
				gitRemote.getRemoteURL(), " ", String.valueOf(deleteCount),
				" were deleted. ", String.valueOf(branchCount - deleteCount),
				" remain. The oldest branch is ",
				JenkinsResultsParserUtil.toDurationString(oldestBranchAge),
				" old."));
	}

	protected static void deleteExpiredRemoteGitBranches(
		List<GitRemote> gitHubDevGitRemotes) {

		final long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		List<Callable<Object>> callables = new ArrayList<>();

		for (final GitRemote gitHubDevGitRemote : gitHubDevGitRemotes) {
			Callable<Object> callable = new SafeCallable<Object>(
				gitHubDevGitRemote.getHostname()) {

				@Override
				public Object safeCall() {
					deleteExpiredCacheBranches(gitHubDevGitRemote, start);

					return null;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "deleteExpiredRemoteGitBranches");

		try {
			parallelExecutor.execute(60L * 5L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		System.out.println(
			"Expired cache branches deleted in " +
				JenkinsResultsParserUtil.toDurationString(duration));
	}

	protected static void deleteExtraTimestampBranches(
		GitRemote gitHubDevGitRemote) {

		GitWorkingDirectory gitWorkingDirectory =
			gitHubDevGitRemote.getGitWorkingDirectory();

		List<RemoteGitBranch> remoteGitBranches =
			gitWorkingDirectory.getRemoteGitBranches(gitHubDevGitRemote);

		Collections.sort(remoteGitBranches);

		Map<String, List<RemoteGitBranch>> remoteGitBranchesMap =
			new HashMap<>();

		for (RemoteGitBranch remoteGitBranch : remoteGitBranches) {
			String remoteGitBranchName = remoteGitBranch.getName();

			if (remoteGitBranchName.matches(
					_cacheBranchPattern.pattern() + "-\\d+")) {

				String baseCacheBranchName = remoteGitBranchName.replaceAll(
					"(.*)-\\d+", "$1");

				List<RemoteGitBranch> timestampedRemoteGitBranches =
					remoteGitBranchesMap.computeIfAbsent(
						baseCacheBranchName, key -> new ArrayList<>());

				timestampedRemoteGitBranches.add(remoteGitBranch);
			}
		}

		for (Map.Entry<String, List<RemoteGitBranch>> entry :
				remoteGitBranchesMap.entrySet()) {

			List<RemoteGitBranch> timestampedRemoteGitBranches =
				entry.getValue();

			if (timestampedRemoteGitBranches.size() > 1) {
				timestampedRemoteGitBranches.remove(
					timestampedRemoteGitBranches.size() - 1);

				deleteRemoteGitBranches(
					gitWorkingDirectory, timestampedRemoteGitBranches);
			}
		}
	}

	protected static void deleteExtraTimestampBranches(
		List<GitRemote> gitHubDevGitRemotes) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		List<Callable<Object>> callables = new ArrayList<>();

		for (final GitRemote gitHubDevGitRemote : gitHubDevGitRemotes) {
			Callable<Object> callable = new SafeCallable<Object>(
				gitHubDevGitRemote.getHostname()) {

				@Override
				public Object safeCall() {
					deleteExtraTimestampBranches(gitHubDevGitRemote);

					return null;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "deleteExtraTimestampBranches");

		try {
			parallelExecutor.execute(60L * 5L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		System.out.println(
			"Local Git nodes cleaned in " +
				JenkinsResultsParserUtil.toDurationString(duration));
	}

	protected static void deleteFromAllRemotes(
		final String remoteGitBranchName, List<GitRemote> gitRemotes) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		List<Callable<Boolean>> callables = new ArrayList<>();

		for (final GitRemote gitRemote : gitRemotes) {
			Callable<Boolean> callable = new SafeCallable<Boolean>(
				gitRemote.getHostname()) {

				@Override
				public Boolean safeCall() {
					GitWorkingDirectory gitWorkingDirectory =
						gitRemote.getGitWorkingDirectory();

					gitWorkingDirectory.deleteRemoteGitBranch(
						remoteGitBranchName, gitRemote);

					return true;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Boolean> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "deleteFromAllRemotes");

		try {
			parallelExecutor.execute(60L * 5L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Deleted ", remoteGitBranchName, " on ",
				String.valueOf(gitRemotes.size()), " Git nodes in ",
				JenkinsResultsParserUtil.toDurationString(duration)));
	}

	protected static void deleteOrphanedCacheBranches(GitRemote gitRemote) {
		List<RemoteGitBranch> cacheRemoteGitBranches =
			getCacheRemoteGitBranches(gitRemote);

		Map<String, RemoteGitBranch> baseCacheRemoteGitBranchesMap =
			new HashMap<>();

		Map<String, RemoteGitBranch> timestampedCacheRemoteGitBranchMap =
			new HashMap<>();

		for (RemoteGitBranch cacheRemoteGitBranch : cacheRemoteGitBranches) {
			String cacheRemoteGitBranchName = cacheRemoteGitBranch.getName();

			if (cacheRemoteGitBranchName.matches(
					_cacheBranchPattern.pattern())) {

				if (cacheRemoteGitBranchName.matches(
						_cacheBranchPattern.pattern() + "-\\d+")) {

					timestampedCacheRemoteGitBranchMap.put(
						cacheRemoteGitBranchName, cacheRemoteGitBranch);
				}
				else {
					baseCacheRemoteGitBranchesMap.put(
						cacheRemoteGitBranchName, cacheRemoteGitBranch);
				}
			}
		}

		Map<String, RemoteGitBranch> orphanedBaseCacheRemoteGitBranchesMap =
			new HashMap<>(baseCacheRemoteGitBranchesMap);
		Map<String, RemoteGitBranch>
			orphanedTimestampedCacheRemoteGitBranchesMap = new HashMap<>(
				timestampedCacheRemoteGitBranchMap);

		for (String baseCacheRemoteGitBranchName :
				baseCacheRemoteGitBranchesMap.keySet()) {

			String timestampedCacheRemoteGitBranchNamePattern =
				Pattern.quote(baseCacheRemoteGitBranchName) + "-\\d+";

			for (String timestampedCacheRemoteGitBranchName :
					timestampedCacheRemoteGitBranchMap.keySet()) {

				if (timestampedCacheRemoteGitBranchName.matches(
						timestampedCacheRemoteGitBranchNamePattern)) {

					orphanedBaseCacheRemoteGitBranchesMap.remove(
						baseCacheRemoteGitBranchName);
				}
			}
		}

		for (String timestampedCacheRemoteGitBranchName :
				timestampedCacheRemoteGitBranchMap.keySet()) {

			String baseCacheRemoteGitBranchName =
				timestampedCacheRemoteGitBranchName.replaceAll(
					"(.*)-\\d+", "$1");

			if (baseCacheRemoteGitBranchesMap.containsKey(
					baseCacheRemoteGitBranchName)) {

				orphanedTimestampedCacheRemoteGitBranchesMap.remove(
					timestampedCacheRemoteGitBranchName);
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String orphanedBaseCacheRemoteGitBranchName :
				orphanedBaseCacheRemoteGitBranchesMap.keySet()) {

			sb.append(orphanedBaseCacheRemoteGitBranchName);
			sb.append("\n");
		}

		for (String orphanedTimestampedCacheRemoteGitBranchName :
				orphanedTimestampedCacheRemoteGitBranchesMap.keySet()) {

			sb.append(orphanedTimestampedCacheRemoteGitBranchName);
			sb.append("\n");
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Found ",
				String.valueOf(orphanedBaseCacheRemoteGitBranchesMap.size()),
				" orphaned base cache branches ", "and ",
				String.valueOf(
					orphanedTimestampedCacheRemoteGitBranchesMap.size()),
				" orphaned timestamp branches on ", gitRemote.getRemoteURL(),
				".\n", sb.toString()));

		List<RemoteGitBranch> orphanedCacheRemoteGitBranches = new ArrayList<>(
			orphanedBaseCacheRemoteGitBranchesMap.size() +
				orphanedTimestampedCacheRemoteGitBranchesMap.size());

		orphanedCacheRemoteGitBranches.addAll(
			orphanedBaseCacheRemoteGitBranchesMap.values());
		orphanedCacheRemoteGitBranches.addAll(
			orphanedTimestampedCacheRemoteGitBranchesMap.values());

		deleteRemoteGitBranches(
			gitRemote.getGitWorkingDirectory(), orphanedCacheRemoteGitBranches);
	}

	protected static void deleteOrphanedCacheBranches(
		List<GitRemote> gitRemotes) {

		List<Callable<Object>> callables = new ArrayList<>(gitRemotes.size());

		for (final GitRemote gitRemote : gitRemotes) {
			Callable<Object> callable = new SafeCallable<Object>(
				gitRemote.getHostname()) {

				@Override
				public Object safeCall() {
					deleteOrphanedCacheBranches(gitRemote);

					return null;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "deleteOrphanedCacheBranches");

		try {
			parallelExecutor.execute(60L * 15L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	protected static void deleteRemoteGitBranches(
		GitWorkingDirectory gitWorkingDirectory,
		List<RemoteGitBranch> remoteGitBranches) {

		if (remoteGitBranches.isEmpty()) {
			return;
		}

		gitWorkingDirectory.deleteRemoteGitBranches(remoteGitBranches);
	}

	protected static List<RemoteGitBranch> getCacheRemoteGitBranches(
		GitRemote gitRemote) {

		List<RemoteGitBranch> cacheRemoteGitBranches = new ArrayList<>();

		Map<String, RemoteGitBranch> remoteGitBranches = new HashMap<>();

		GitWorkingDirectory gitWorkingDirectory =
			gitRemote.getGitWorkingDirectory();

		for (RemoteGitBranch remoteGitBranch :
				gitWorkingDirectory.getRemoteGitBranches(gitRemote)) {

			remoteGitBranches.put(remoteGitBranch.getName(), remoteGitBranch);
		}

		for (Map.Entry<String, RemoteGitBranch> entry :
				remoteGitBranches.entrySet()) {

			String remoteGitBranchName = entry.getKey();

			if (remoteGitBranchName.matches(_cacheBranchPattern.pattern())) {
				if (hasTimestampBranch(remoteGitBranches)) {
					cacheRemoteGitBranches.add(entry.getValue());
				}
				else {
					deleteCacheRemoteGitBranch(
						remoteGitBranchName, gitWorkingDirectory,
						remoteGitBranches);
				}
			}
		}

		return cacheRemoteGitBranches;
	}

	protected static List<String> getGitHubDevNodeHostnames() {
		if (gitHubDevNodeHostnames != null) {
			return new ArrayList<>(gitHubDevNodeHostnames);
		}

		gitHubDevNodeHostnames =
			JenkinsResultsParserUtil.getGitHubCacheHostnames();

		return gitHubDevNodeHostnames;
	}

	protected static List<String> getGitHubDevRemoteURLs(
		GitWorkingDirectory gitWorkingDirectory) {

		List<String> gitHubDevRemoteURLs = new ArrayList<>();

		for (String gitHubDevNodeHostname : getGitHubDevNodeHostnames()) {
			if (gitHubDevNodeHostname.startsWith("slave-")) {
				gitHubDevRemoteURLs.add(
					JenkinsResultsParserUtil.combine(
						"root@", gitHubDevNodeHostname.substring(6),
						":/opt/dev/projects/github/",
						gitWorkingDirectory.getGitRepositoryName()));

				continue;
			}

			String gitRepositoryName =
				gitWorkingDirectory.getGitRepositoryName();

			String upstreamBranchName =
				gitWorkingDirectory.getUpstreamBranchName();

			if (gitRepositoryName.equals("liferay-portal-ee") &&
				upstreamBranchName.equals("master")) {

				gitRepositoryName = "liferay-portal";
			}

			gitHubDevRemoteURLs.add(
				JenkinsResultsParserUtil.combine(
					"git@", gitHubDevNodeHostname, ":liferay/",
					gitRepositoryName, ".git"));
		}

		return gitHubDevRemoteURLs;
	}

	protected static String getGitHubRemoteURL(
		String repositoryName, String userName) {

		return JenkinsResultsParserUtil.combine(
			"git@github.com:", userName, "/", repositoryName, ".git");
	}

	protected static GitRemote getRandomGitRemote(List<GitRemote> gitRemotes) {
		return gitRemotes.get(
			JenkinsResultsParserUtil.getRandomValue(0, gitRemotes.size() - 1));
	}

	protected static boolean hasTimestampBranch(
		Map<String, RemoteGitBranch> remoteGitBranches) {

		for (String remoteGitBranchName : remoteGitBranches.keySet()) {
			Matcher matcher = _cacheBranchPattern.matcher(remoteGitBranchName);

			if (matcher.matches()) {
				String lastBlock = matcher.group(2);

				if (lastBlock.matches("\\d+")) {
					return true;
				}
			}
		}

		return false;
	}

	protected static void pushToAllRemotes(
		final boolean force, final LocalGitBranch localGitBranch,
		final String remoteGitBranchName, List<GitRemote> gitRemotes) {

		if (localGitBranch == null) {
			throw new RuntimeException("Local Git branch is null");
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		List<Callable<Boolean>> callables = new ArrayList<>();

		for (final GitRemote gitRemote : gitRemotes) {
			Callable<Boolean> callable = new SafeCallable<Boolean>(
				gitRemote.getHostname()) {

				@Override
				public Boolean safeCall() {
					GitWorkingDirectory gitWorkingDirectory =
						gitRemote.getGitWorkingDirectory();

					RemoteGitBranch remoteGitBranch =
						gitWorkingDirectory.getRemoteGitBranch(
							remoteGitBranchName, gitRemote);

					if ((remoteGitBranch == null) ||
						!Objects.equals(
							remoteGitBranch.getSHA(),
							localGitBranch.getSHA())) {

						remoteGitBranch =
							gitWorkingDirectory.pushToRemoteGitRepository(
								force, localGitBranch, remoteGitBranchName,
								gitRemote);

						return Boolean.valueOf(remoteGitBranch != null);
					}

					return true;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Boolean> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "pushToAllRemotes");

		try {
			parallelExecutor.execute(60L * 60L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		long duration = JenkinsResultsParserUtil.getCurrentTimeMillis() - start;

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Pushed ", localGitBranch.getName(), " to ",
				remoteGitBranchName, " on ", String.valueOf(gitRemotes.size()),
				" Git nodes in ",
				JenkinsResultsParserUtil.toDurationString(duration)));
	}

	protected static boolean remoteGitBranchExists(
		final String remoteGitBranchName,
		final GitWorkingDirectory gitWorkingDirectory,
		List<GitRemote> gitRemotes) {

		List<Callable<Boolean>> callables = new ArrayList<>(gitRemotes.size());

		for (final GitRemote gitRemote : gitRemotes) {
			Callable<Boolean> callable = new SafeCallable<Boolean>(
				gitRemote.getHostname()) {

				@Override
				public Boolean safeCall() {
					try {
						return gitWorkingDirectory.remoteGitBranchExists(
							remoteGitBranchName, gitRemote);
					}
					catch (Exception exception) {
						exception.printStackTrace();

						return true;
					}
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Boolean> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "remoteGitBranchExists");

		try {
			for (Boolean bool : parallelExecutor.execute(60L * 5L)) {
				if ((bool == null) || !bool) {
					return false;
				}
			}
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		return true;
	}

	protected static String synchronizeToGitHubDev(
		GitWorkingDirectory gitWorkingDirectory, String receiverUsername,
		int retryCount, String senderBranchName, String senderUsername,
		String senderBranchSHA, String upstreamBranchSHA) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		File gitRepositoryDirectory = gitWorkingDirectory.getWorkingDirectory();

		LocalGitBranch currentLocalGitBranch =
			gitWorkingDirectory.getCurrentLocalGitBranch();

		if (currentLocalGitBranch == null) {
			LocalGitBranch localUpstreamGitBranch =
				gitWorkingDirectory.getUpstreamLocalGitBranch();

			gitWorkingDirectory.checkoutLocalGitBranch(localUpstreamGitBranch);

			currentLocalGitBranch = localUpstreamGitBranch;
		}

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Starting synchronization with local-git. Current repository ",
				"directory is ", gitRepositoryDirectory.getPath(), ". Current ",
				"branch is ", currentLocalGitBranch.getName(), "."));

		GitRemote senderGitRemote = null;

		try {
			senderGitRemote = gitWorkingDirectory.addGitRemote(
				true, "sender-temp",
				getGitHubRemoteURL(
					gitWorkingDirectory.getGitRepositoryName(),
					senderUsername));

			String cacheBranchName = getCacheBranchName(
				receiverUsername, senderUsername, senderBranchSHA,
				upstreamBranchSHA);

			String upstreamBranchName =
				gitWorkingDirectory.getUpstreamBranchName();

			List<GitRemote> gitHubDevGitRemotes = null;

			try {
				gitHubDevGitRemotes = getGitHubDevGitRemotes(
					gitWorkingDirectory);

				deleteCacheLocalGitBranches(
					cacheBranchName, gitWorkingDirectory);

				if (JenkinsResultsParserUtil.getRandomValue(1, 10) == 5) {
					deleteExtraTimestampBranches(gitHubDevGitRemotes);

					deleteOrphanedCacheBranches(gitHubDevGitRemotes);

					deleteExpiredRemoteGitBranches(gitHubDevGitRemotes);
				}

				RemoteGitBranch cacheRemoteGitBranch = null;

				try {
					cacheRemoteGitBranch = fetchCacheBranchFromGitHubDev(
						gitWorkingDirectory, cacheBranchName);
				}
				catch (Exception exception) {
					cacheRemoteGitBranch = null;

					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Cache branch ", cacheBranchName,
							" does not exist"));
				}

				if (cacheRemoteGitBranch != null) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Cache branch ", cacheBranchName,
							" already exists"));

					gitWorkingDirectory.deleteLocalGitBranch(cacheBranchName);

					gitWorkingDirectory.createLocalGitBranch(
						cacheBranchName, true, cacheRemoteGitBranch.getSHA());

					if (!gitWorkingDirectory.localGitBranchExists(
							upstreamBranchName)) {

						updateUpstreamLocalGitBranch(
							gitWorkingDirectory, upstreamBranchSHA);
					}

					updateCacheRemoteGitBranchTimestamp(
						cacheBranchName, gitWorkingDirectory,
						gitHubDevGitRemotes);

					return cacheBranchName;
				}

				senderBranchName = senderBranchName.trim();

				LocalGitBranch cacheLocalGitBranch =
					gitWorkingDirectory.getRebasedLocalGitBranch(
						cacheBranchName, senderBranchName,
						senderGitRemote.getRemoteURL(), senderBranchSHA,
						upstreamBranchName, upstreamBranchSHA);

				cacheBranches(
					gitWorkingDirectory, cacheLocalGitBranch,
					cacheLocalGitBranch.getName(), gitHubDevGitRemotes,
					"liferay");

				return cacheBranchName;
			}
			catch (Exception exception) {
				if (retryCount == 1) {
					throw exception;
				}

				gitHubDevGitRemotes = null;
				senderGitRemote = null;

				System.out.println(
					"Synchronization with local-git failed. Retrying.");

				exception.printStackTrace();

				gitWorkingDirectory.checkoutLocalGitBranch(
					currentLocalGitBranch);

				return synchronizeToGitHubDev(
					gitWorkingDirectory, receiverUsername, retryCount + 1,
					senderBranchName, senderUsername, senderBranchSHA,
					upstreamBranchSHA);
			}
			finally {
				if (gitHubDevGitRemotes != null) {
					try {
						gitWorkingDirectory.removeGitRemotes(
							gitHubDevGitRemotes);
					}
					catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				if (gitWorkingDirectory.localGitBranchExists(
						currentLocalGitBranch.getName())) {

					gitWorkingDirectory.checkoutLocalGitBranch(
						currentLocalGitBranch);
				}
				else {
					checkoutUpstreamLocalGitBranch(
						gitWorkingDirectory, upstreamBranchSHA);
				}

				gitWorkingDirectory.deleteLocalGitBranch(cacheBranchName);
			}
		}
		finally {
			if (senderGitRemote != null) {
				try {
					gitWorkingDirectory.removeGitRemote(senderGitRemote);
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			String durationString = JenkinsResultsParserUtil.toDurationString(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start);

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Synchronization with local Git completed in ",
					durationString, ". Current repository directory is",
					gitRepositoryDirectory.getPath()));
		}
	}

	protected static String synchronizeToGitHubDev(
		LocalGitBranch localGitBranch,
		WorkspaceGitRepository workspaceGitRepository, int retryCount) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		GitWorkingDirectory gitWorkingDirectory =
			workspaceGitRepository.getGitWorkingDirectory();

		String cacheBranchName =
			workspaceGitRepository.getGitHubDevBranchName();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Starting synchronization with local-git. Current repository ",
				"directory is ",
				JenkinsResultsParserUtil.getCanonicalPath(
					workspaceGitRepository.getDirectory()),
				". Branch to cache is ", cacheBranchName, "."));

		try {
			List<GitRemote> gitHubDevGitRemotes = null;

			try {
				gitHubDevGitRemotes = getGitHubDevGitRemotes(
					gitWorkingDirectory);

				if (JenkinsResultsParserUtil.getRandomValue(1, 10) == 5) {
					deleteExtraTimestampBranches(gitHubDevGitRemotes);

					deleteOrphanedCacheBranches(gitHubDevGitRemotes);

					deleteExpiredRemoteGitBranches(gitHubDevGitRemotes);
				}

				if (remoteGitBranchExists(
						cacheBranchName, gitWorkingDirectory,
						gitHubDevGitRemotes)) {

					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Cache branch ", cacheBranchName,
							" already exists"));

					updateCacheRemoteGitBranchTimestamp(
						cacheBranchName, gitWorkingDirectory,
						gitHubDevGitRemotes);

					return cacheBranchName;
				}

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Cache branch ", cacheBranchName, " does not exist"));

				cacheBranches(
					gitWorkingDirectory, localGitBranch, cacheBranchName,
					gitHubDevGitRemotes, "liferay");

				return cacheBranchName;
			}
			catch (Exception exception) {
				if (retryCount == 1) {
					throw exception;
				}

				gitHubDevGitRemotes = null;

				System.out.println(
					"Synchronization with local-git failed. Retrying.");

				exception.printStackTrace();

				return synchronizeToGitHubDev(
					localGitBranch, workspaceGitRepository, retryCount + 1);
			}
			finally {
				if (gitHubDevGitRemotes != null) {
					try {
						gitWorkingDirectory.removeGitRemotes(
							gitHubDevGitRemotes);
					}
					catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
		finally {
			String durationString = JenkinsResultsParserUtil.toDurationString(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start);

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Synchronization with local-git completed in ",
					durationString, ". Current repository directory is",
					JenkinsResultsParserUtil.getCanonicalPath(
						workspaceGitRepository.getDirectory())));
		}
	}

	protected static boolean synchronizeUpstreamBranchToGitHubDev(
		GitWorkingDirectory gitWorkingDirectory, LocalGitBranch localGitBranch,
		int retryCount) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		File gitRepositoryDirectory = gitWorkingDirectory.getWorkingDirectory();

		gitWorkingDirectory.checkoutLocalGitBranch(localGitBranch);

		String upstreamBranchName = gitWorkingDirectory.getUpstreamBranchName();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Starting synchronization with local-git. Current repository ",
				"directory is ", gitRepositoryDirectory.getPath(), ". Current ",
				"branch is ", localGitBranch.getName(), " at hash ",
				localGitBranch.getSHA(), ". Synchronization target upstream ",
				"branch is ", upstreamBranchName, "."));

		try {
			List<GitRemote> gitHubDevGitRemotes = getGitHubDevGitRemotes(
				gitWorkingDirectory);

			try {
				pushToAllRemotes(
					true, localGitBranch, upstreamBranchName,
					gitHubDevGitRemotes);
			}
			finally {
				if (gitHubDevGitRemotes != null) {
					try {
						gitWorkingDirectory.removeGitRemotes(
							gitHubDevGitRemotes);
					}
					catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
		finally {
			String durationString = JenkinsResultsParserUtil.toDurationString(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start);

			System.out.println(
				"Synchronization with local Git completed in " +
					durationString);
		}

		return true;
	}

	protected static void updateCacheRemoteGitBranchTimestamp(
		String cacheBranchName, GitWorkingDirectory gitWorkingDirectory,
		List<GitRemote> gitHubDevGitRemotes) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			List<RemoteGitBranch> cacheRemoteGitBranches = null;
			GitRemote gitHubDevGitRemote = null;

			while (cacheRemoteGitBranches == null) {
				try {
					gitHubDevGitRemote = getRandomGitRemote(
						gitHubDevGitRemotes);

					cacheRemoteGitBranches = getCacheRemoteGitBranches(
						gitHubDevGitRemote);
				}
				catch (Exception exception) {
					exception.printStackTrace();

					gitHubDevGitRemotes.remove(gitHubDevGitRemote);

					if (gitHubDevGitRemotes.isEmpty()) {
						throw new RuntimeException(
							"No remote repositories could be reached",
							exception);
					}
				}
			}

			RemoteGitBranch oldTimestampCacheRemoteGitBranch = null;

			Pattern pattern = Pattern.compile(
				Pattern.quote(cacheBranchName) + "-(\\d+)");

			for (RemoteGitBranch cacheRemoteGitBranch :
					cacheRemoteGitBranches) {

				Matcher matcher = pattern.matcher(
					cacheRemoteGitBranch.getName());

				if (!matcher.matches()) {
					continue;
				}

				long existingTimestamp = Long.parseLong(matcher.group(1));

				long branchAge =
					JenkinsResultsParserUtil.getCurrentTimeMillis() -
						existingTimestamp;

				if (branchAge > _MILLIS_BRANCH_UPDATE_AGE) {
					oldTimestampCacheRemoteGitBranch = cacheRemoteGitBranch;
				}

				break;
			}

			if (oldTimestampCacheRemoteGitBranch == null) {
				return;
			}

			String newTimestampCacheRemoteBranchName =
				JenkinsResultsParserUtil.combine(
					cacheBranchName, "-",
					String.valueOf(
						JenkinsResultsParserUtil.getCurrentTimeMillis()));

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Updating existing timestamp for branch ",
					oldTimestampCacheRemoteGitBranch.getName(), " to ",
					newTimestampCacheRemoteBranchName));

			LocalGitBranch originalCheckedOutLocalGitBranch =
				gitWorkingDirectory.getCurrentLocalGitBranch();

			if (originalCheckedOutLocalGitBranch == null) {
				originalCheckedOutLocalGitBranch =
					gitWorkingDirectory.getUpstreamLocalGitBranch();
			}

			LocalGitBranch newTimestampLocalGitBranch =
				gitWorkingDirectory.createLocalGitBranch(
					newTimestampCacheRemoteBranchName);

			newTimestampLocalGitBranch = gitWorkingDirectory.fetch(
				newTimestampLocalGitBranch, oldTimestampCacheRemoteGitBranch);

			try {
				pushToAllRemotes(
					true, newTimestampLocalGitBranch,
					newTimestampCacheRemoteBranchName, gitHubDevGitRemotes);

				deleteFromAllRemotes(
					oldTimestampCacheRemoteGitBranch.getName(),
					gitHubDevGitRemotes);
			}
			finally {
				gitWorkingDirectory.checkoutLocalGitBranch(
					originalCheckedOutLocalGitBranch);

				gitWorkingDirectory.deleteLocalGitBranch(
					newTimestampLocalGitBranch);
			}
		}
		finally {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Cache branch timestamp updated in ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getCurrentTimeMillis() -
							start)));
		}
	}

	protected static LocalGitBranch updateUpstreamLocalGitBranch(
		GitWorkingDirectory gitWorkingDirectory, String upstreamBranchSHA) {

		String upstreamBranchName = gitWorkingDirectory.getUpstreamBranchName();

		RemoteGitBranch upstreamRemoteGitBranch =
			gitWorkingDirectory.getRemoteGitBranch(
				upstreamBranchName, gitWorkingDirectory.getUpstreamGitRemote(),
				true);

		LocalGitBranch upstreamLocalGitBranch =
			gitWorkingDirectory.getUpstreamLocalGitBranch();

		if (upstreamLocalGitBranch == null) {
			upstreamLocalGitBranch = gitWorkingDirectory.createLocalGitBranch(
				upstreamBranchName);

			gitWorkingDirectory.fetch(
				upstreamLocalGitBranch, upstreamRemoteGitBranch);
		}

		String upstreamLocalGitBranchSHA = upstreamLocalGitBranch.getSHA();

		String upstreamRemoteGitBranchSHA = upstreamRemoteGitBranch.getSHA();

		if ((upstreamBranchSHA != null) &&
			!upstreamRemoteGitBranchSHA.equals(upstreamBranchSHA)) {

			upstreamRemoteGitBranchSHA = upstreamBranchSHA;
		}

		if (upstreamLocalGitBranchSHA.equals(upstreamRemoteGitBranchSHA)) {
			return upstreamLocalGitBranch;
		}

		gitWorkingDirectory.rebaseAbort();

		gitWorkingDirectory.clean();

		gitWorkingDirectory.reset("--hard");

		gitWorkingDirectory.fetch(upstreamRemoteGitBranch);

		String tempBranchName =
			"temp-" + JenkinsResultsParserUtil.getCurrentTimeMillis();

		LocalGitBranch tempLocalGitBranch = null;

		try {
			tempLocalGitBranch = gitWorkingDirectory.createLocalGitBranch(
				tempBranchName, true, upstreamRemoteGitBranchSHA);

			gitWorkingDirectory.checkoutLocalGitBranch(
				tempLocalGitBranch, "-f");

			gitWorkingDirectory.deleteLocalGitBranch(upstreamBranchName);

			upstreamLocalGitBranch = gitWorkingDirectory.createLocalGitBranch(
				upstreamRemoteGitBranch.getName(), true,
				upstreamRemoteGitBranchSHA);

			gitWorkingDirectory.checkoutLocalGitBranch(upstreamLocalGitBranch);
		}
		finally {
			if (tempLocalGitBranch != null) {
				gitWorkingDirectory.deleteLocalGitBranch(tempLocalGitBranch);
			}
		}

		return upstreamLocalGitBranch;
	}

	protected static List<String> gitHubDevNodeHostnames;

	private static final long _MILLIS_BRANCH_EXPIRATION =
		1000 * 60 * 60 * 24 * 2;

	private static final long _MILLIS_BRANCH_UPDATE_AGE = 1000 * 60 * 60 * 24;

	private static final Pattern _cacheBranchPattern = Pattern.compile(
		"cache(-([^-]+))+");
	private static final ThreadPoolExecutor _threadPoolExecutor =
		JenkinsResultsParserUtil.isCloudCINode() ?
			JenkinsResultsParserUtil.getNewThreadPoolExecutor(1, true) :
				JenkinsResultsParserUtil.getNewThreadPoolExecutor(16, true);

	private abstract static class SafeCallable<T>
		extends ParallelExecutor.SequentialCallable<T> {

		public SafeCallable() {
			this(null);
		}

		public SafeCallable(String groupName) {
			super(groupName);
		}

		@Override
		public final T call() {
			try {
				return safeCall();
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}

			return null;
		}

		public abstract T safeCall();

	}

}