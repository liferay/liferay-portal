/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class MergeCentralGitSubrepositoryUtil {

	public static void createGitSubrepositoryMergePullRequests(
			String centralWorkingDirectory, String centralUpstreamBranchName,
			String receiverUserName, String senderUserName,
			String topLevelBranchName)
		throws IOException {

		GitWorkingDirectory centralGitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				centralUpstreamBranchName, centralWorkingDirectory);

		File modulesDir = new File(
			centralGitWorkingDirectory.getWorkingDirectory(), "modules");

		if (!modulesDir.exists()) {
			return;
		}

		List<String> failedGitrepoPaths = new ArrayList<>();
		List<String> skippedGitrepoPaths = new ArrayList<>();
		List<String> subrepoMergeBlacklist =
			JenkinsResultsParserUtil.getBuildPropertyAsList(
				false, "subrepo.merge.blacklist");

		List<File> gitrepoFiles = JenkinsResultsParserUtil.findFiles(
			modulesDir, ".gitrepo");

		for (File gitrepoFile : gitrepoFiles) {
			try {
				Properties gitrepoProperties = _getPropertiesFromGitrepoFile(
					gitrepoFile);

				String remote = gitrepoProperties.getProperty("remote");

				if (remote == null) {
					skippedGitrepoPaths.add(gitrepoFile.getParent());

					continue;
				}

				if (_isBlacklisted(remote, subrepoMergeBlacklist)) {
					continue;
				}

				CentralGitSubrepository centralGitSubrepository =
					new CentralGitSubrepository(
						gitrepoFile, centralUpstreamBranchName);

				if (!centralGitSubrepository.isAutoPullEnabled()) {
					continue;
				}

				String mergeBranchName = _getMergeBranchName(
					centralUpstreamBranchName,
					centralGitSubrepository.getGitSubrepositoryName(),
					centralGitSubrepository.
						getGitSubrepositoryUpstreamCommit());

				if (centralGitSubrepository.isCentralPullRequestCandidate()) {
					GitRemote upstreamGitRemote =
						centralGitWorkingDirectory.getGitRemote("upstream");

					if (!centralGitWorkingDirectory.remoteGitBranchExists(
							mergeBranchName, upstreamGitRemote)) {

						LocalGitBranch topLevelLocalGitBranch =
							centralGitWorkingDirectory.getLocalGitBranch(
								topLevelBranchName, true);

						LocalGitBranch mergeLocalGitBranch =
							_createMergeLocalGitBranch(
								centralGitWorkingDirectory, mergeBranchName,
								topLevelLocalGitBranch);

						_commitCiMergeFile(
							centralGitWorkingDirectory, centralGitSubrepository,
							gitrepoFile);

						_pushMergeLocalGitBranchToRemote(
							centralGitWorkingDirectory, mergeLocalGitBranch,
							senderUserName);
					}

					_createMergePullRequest(
						centralGitWorkingDirectory, centralGitSubrepository,
						mergeBranchName, receiverUserName, senderUserName);
				}

				_deleteStalePulls(
					centralGitWorkingDirectory, centralGitSubrepository,
					mergeBranchName, receiverUserName);

				_deleteStaleBranches(
					centralGitWorkingDirectory, centralGitSubrepository,
					mergeBranchName);
			}
			catch (Exception exception) {
				failedGitrepoPaths.add(gitrepoFile.getParent());

				exception.printStackTrace();
			}
		}

		if (!skippedGitrepoPaths.isEmpty()) {
			_sendEmail(
				JenkinsResultsParserUtil.combine(
					"Skipped these subrepositories with no \"remote\" key:\n",
					StringUtils.join(skippedGitrepoPaths, "\n")));
		}

		if (!failedGitrepoPaths.isEmpty()) {
			String message = JenkinsResultsParserUtil.combine(
				"Unable to create a pull to merge these subrepositories:\n",
				StringUtils.join(failedGitrepoPaths, "\n"));

			_sendEmail(message);

			throw new RuntimeException(message);
		}
	}

	private static void _commitCiMergeFile(
			GitWorkingDirectory centralGitWorkingDirectory,
			CentralGitSubrepository centralGitSubrepository, File gitrepoFile)
		throws IOException {

		String gitSubrepositoryUpstreamCommit =
			centralGitSubrepository.getGitSubrepositoryUpstreamCommit();

		String ciMergeFilePath = _getCiMergeFilePath(
			centralGitWorkingDirectory, gitrepoFile);

		JenkinsResultsParserUtil.write(
			new File(
				centralGitWorkingDirectory.getWorkingDirectory(),
				ciMergeFilePath),
			gitSubrepositoryUpstreamCommit);

		centralGitWorkingDirectory.stageFileInCurrentLocalGitBranch(
			ciMergeFilePath);

		centralGitWorkingDirectory.commitStagedFilesToCurrentBranch(
			"Create " + ciMergeFilePath + ".");
	}

	private static LocalGitBranch _createMergeLocalGitBranch(
		GitWorkingDirectory centralGitWorkingDirectory, String mergeBranchName,
		LocalGitBranch topLevelLocalGitBranch) {

		centralGitWorkingDirectory.reset("--hard");

		centralGitWorkingDirectory.checkoutLocalGitBranch(
			topLevelLocalGitBranch);

		LocalGitBranch mergeLocalGitBranch =
			centralGitWorkingDirectory.getLocalGitBranch(mergeBranchName);

		if (mergeLocalGitBranch != null) {
			centralGitWorkingDirectory.deleteLocalGitBranch(
				mergeLocalGitBranch);
		}

		mergeLocalGitBranch = centralGitWorkingDirectory.createLocalGitBranch(
			mergeBranchName);

		centralGitWorkingDirectory.checkoutLocalGitBranch(mergeLocalGitBranch);

		return mergeLocalGitBranch;
	}

	private static void _createMergePullRequest(
			GitWorkingDirectory centralGitWorkingDirectory,
			CentralGitSubrepository centralGitSubrepository,
			String mergeBranchName, String receiverUserName,
			String senderUserName)
		throws IOException {

		String gitSubrepositoryName =
			centralGitSubrepository.getGitSubrepositoryName();
		String gitSubrepositoryUpstreamCommit =
			centralGitSubrepository.getGitSubrepositoryUpstreamCommit();

		String url = JenkinsResultsParserUtil.getGitHubAPIURL(
			gitSubrepositoryName, senderUserName,
			"statuses/" + gitSubrepositoryUpstreamCommit);

		JSONObject requestJSONObject = new JSONObject();

		requestJSONObject.put(
			"context", "liferay/central-pull-request"
		).put(
			"description", "Tests are queued on Jenkins."
		).put(
			"state", "pending"
		);

		StringBuilder sb = new StringBuilder();

		sb.append("Merging the following commit: [");
		sb.append(gitSubrepositoryUpstreamCommit);
		sb.append("](https://github.com/");
		sb.append(senderUserName);
		sb.append("/");
		sb.append(gitSubrepositoryName);
		sb.append("/commit/");
		sb.append(gitSubrepositoryUpstreamCommit);
		sb.append(")");

		String title = gitSubrepositoryName + " - Central Merge Pull Request";

		String pullRequestURL = centralGitWorkingDirectory.createPullRequest(
			sb.toString(), mergeBranchName, receiverUserName, senderUserName,
			title);

		requestJSONObject.put("target_url", pullRequestURL);

		JenkinsResultsParserUtil.toJSONObject(
			url, requestJSONObject.toString());
	}

	private static void _deleteStaleBranches(
			GitWorkingDirectory centralGitWorkingDirectory,
			CentralGitSubrepository centralGitSubrepository,
			String mergeBranchName)
		throws IOException {

		GitRemote upstreamGitRemote = centralGitWorkingDirectory.getGitRemote(
			"upstream");

		if (_upstreamRemoteGitBranchNames == null) {
			_upstreamRemoteGitBranchNames =
				centralGitWorkingDirectory.getRemoteGitBranchNames(
					upstreamGitRemote);
		}

		String mergeBranchNamePrefix = _getMergeBranchNamePrefix(
			mergeBranchName);

		for (String upstreamRemoteGitBranchName :
				_upstreamRemoteGitBranchNames) {

			if ((upstreamRemoteGitBranchName.equals(mergeBranchName) &&
				 !centralGitSubrepository.
					 isGitSubrepositoryUpstreamCommitMerged()) ||
				!upstreamRemoteGitBranchName.startsWith(
					mergeBranchNamePrefix)) {

				continue;
			}

			centralGitWorkingDirectory.deleteRemoteGitBranch(
				upstreamRemoteGitBranchName, upstreamGitRemote);
		}
	}

	private static void _deleteStalePulls(
			GitWorkingDirectory centralGitWorkingDirectory,
			CentralGitSubrepository centralGitSubrepository,
			String mergeBranchName, String receiverUserName)
		throws IOException {

		if (_pullsJSONArray == null) {
			_pullsJSONArray = new JSONArray();

			int page = 1;

			while (page < 10) {
				String url = JenkinsResultsParserUtil.getGitHubAPIURL(
					centralGitWorkingDirectory.getGitRepositoryName(),
					receiverUserName, "pulls?page=" + String.valueOf(page));

				JSONArray jsonArray = JenkinsResultsParserUtil.toJSONArray(url);

				if ((jsonArray != null) && (jsonArray.length() > 0)) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						JSONObject userJSONObject = jsonObject.getJSONObject(
							"user");

						String login = userJSONObject.getString("login");

						if (login.equals("liferay-continuous-integration")) {
							_pullsJSONArray.put(jsonObject);
						}
					}
				}
				else {
					break;
				}

				page++;
			}
		}

		String mergeBranchNamePrefix = _getMergeBranchNamePrefix(
			mergeBranchName);

		for (int i = 0; i < _pullsJSONArray.length(); i++) {
			JSONObject jsonObject = _pullsJSONArray.getJSONObject(i);

			JSONObject headJSONObject = jsonObject.getJSONObject("head");

			String refName = headJSONObject.getString("ref");

			if ((refName.equals(mergeBranchName) &&
				 !centralGitSubrepository.
					 isGitSubrepositoryUpstreamCommitMerged()) ||
				!refName.startsWith(mergeBranchNamePrefix)) {

				continue;
			}

			System.out.println(
				"Closing pull request " + jsonObject.getString("html_url"));

			JSONObject requestJSONObject = new JSONObject();

			requestJSONObject.put(
				"body", "This stale merge pull request has been closed.");

			JenkinsResultsParserUtil.toJSONObject(
				jsonObject.getString("comments_url"),
				requestJSONObject.toString());

			requestJSONObject = new JSONObject();

			requestJSONObject.put("state", "closed");

			JenkinsResultsParserUtil.toJSONObject(
				jsonObject.getString("url"), requestJSONObject.toString());
		}
	}

	private static String _getCiMergeFilePath(
			GitWorkingDirectory centralGitWorkingDirectory, File gitrepoFile)
		throws IOException {

		File centralWorkingDirectory =
			centralGitWorkingDirectory.getWorkingDirectory();

		String ciMergeFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			gitrepoFile);

		ciMergeFilePath = ciMergeFilePath.replace(".gitrepo", "ci-merge");

		return ciMergeFilePath.replace(
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getCanonicalPath(
					centralWorkingDirectory),
				File.separator),
			"");
	}

	private static String _getMergeBranchName(
		String centralUpstreamBranchName, String gitSubrepositoryName,
		String gitSubrepositoryUpstreamCommit) {

		return JenkinsResultsParserUtil.combine(
			"ci-merge-", gitSubrepositoryName, "-", centralUpstreamBranchName,
			"-", gitSubrepositoryUpstreamCommit);
	}

	private static String _getMergeBranchNamePrefix(String mergeBranchName) {
		return mergeBranchName.substring(0, mergeBranchName.lastIndexOf("-"));
	}

	private static Properties _getPropertiesFromGitrepoFile(File gitrepoFile)
		throws IOException {

		Properties properties = new Properties();

		properties.load(new FileInputStream(gitrepoFile));

		return properties;
	}

	private static boolean _isBlacklisted(
		String remote, List<String> subrepoMergeBlacklist) {

		Matcher matcher = _githubRemotePattern.matcher(remote);

		if (matcher.find() &&
			subrepoMergeBlacklist.contains(
				matcher.group("gitSubrepositoryName"))) {

			return true;
		}

		return false;
	}

	private static void _pushMergeLocalGitBranchToRemote(
		GitWorkingDirectory centralGitWorkingDirectory,
		LocalGitBranch mergeLocalGitBranch, String senderUserName) {

		String centralGitRepositoryName =
			centralGitWorkingDirectory.getGitRepositoryName();

		String originRemoteURL = JenkinsResultsParserUtil.combine(
			"git@github.com:", senderUserName, "/", centralGitRepositoryName,
			".git");

		GitRemote originGitRemote = centralGitWorkingDirectory.addGitRemote(
			true, "tempRemote", originRemoteURL);

		try {
			centralGitWorkingDirectory.pushToRemoteGitRepository(
				false, mergeLocalGitBranch, mergeLocalGitBranch.getName(),
				originGitRemote);
		}
		finally {
			centralGitWorkingDirectory.removeGitRemote(originGitRemote);
		}
	}

	private static void _sendEmail(String message) throws IOException {
		Properties buildProperties =
			JenkinsResultsParserUtil.getBuildProperties();

		NotificationUtil.sendEmail(
			message, "jenkins", "Merge central Git subrepository",
			buildProperties.getProperty(
				"email.list[merge-central-subrepository]"));
	}

	private static final Pattern _githubRemotePattern = Pattern.compile(
		"git@github.com:[-\\w]+\\/(?<gitSubrepositoryName>[-\\w]+)\\.git");
	private static JSONArray _pullsJSONArray;
	private static List<String> _upstreamRemoteGitBranchNames;

}