/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class GitRepositoryJob extends BaseJob {

	public String getBranchName() {
		return getUpstreamBranchName();
	}

	public GitWorkingDirectory getGitWorkingDirectory() {
		if (gitWorkingDirectory != null) {
			return gitWorkingDirectory;
		}

		checkGitRepositoryDir();

		gitWorkingDirectory = GitWorkingDirectoryFactory.newGitWorkingDirectory(
			getBranchName(),
			JenkinsResultsParserUtil.getCanonicalPath(gitRepositoryDir));

		return gitWorkingDirectory;
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("branch", _getBranchJSONObject());
		jsonObject.put(
			"git_repository_dir",
			JenkinsResultsParserUtil.getCanonicalPath(gitRepositoryDir));
		jsonObject.put("upstream_branch_name", _upstreamBranchName);

		return jsonObject;
	}

	@Override
	public List<String> getJobPropertyOptions() {
		List<String> jobPropertyOptions = super.getJobPropertyOptions();

		jobPropertyOptions.add(getBranchName());

		jobPropertyOptions.removeAll(Collections.singleton(null));

		return jobPropertyOptions;
	}

	public String getRepositoryName() {
		String gitRepositoryDirPath = JenkinsResultsParserUtil.getCanonicalPath(
			gitRepositoryDir);

		return JenkinsResultsParserUtil.getGitRepositoryName(
			gitRepositoryDirPath.replaceAll(".*/([^/]+)", "$1"));
	}

	public String getUpstreamBranchName() {
		return _upstreamBranchName;
	}

	public void setGitRepositoryDir(File gitRepositoryDir) {
		this.gitRepositoryDir = gitRepositoryDir;
	}

	protected GitRepositoryJob(BuildProfile buildProfile, String jobName) {
		this(buildProfile, jobName, null);
	}

	protected GitRepositoryJob(
		BuildProfile buildProfile, String jobName, String upstreamBranchName) {

		super(buildProfile, jobName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			upstreamBranchName = "master";

			Matcher matcher = _jobNamePattern.matcher(getJobName());

			if (matcher.find()) {
				upstreamBranchName = matcher.group("upstreamBranchName");
			}
		}

		if (upstreamBranchName.equals("release")) {
			String githubUpstreamBranchName = Environment.get(
				"GITHUB_UPSTREAM_BRANCH_NAME");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					githubUpstreamBranchName)) {

				upstreamBranchName = githubUpstreamBranchName;
			}
		}

		_upstreamBranchName = upstreamBranchName;
	}

	protected GitRepositoryJob(JSONObject jsonObject) {
		super(jsonObject);

		gitRepositoryDir = new File(jsonObject.getString("git_repository_dir"));
		_upstreamBranchName = jsonObject.getString("upstream_branch_name");
	}

	protected void checkGitRepositoryDir() {
		if (gitRepositoryDir == null) {
			throw new IllegalStateException("Repository directory is not set");
		}

		if (!gitRepositoryDir.exists()) {
			throw new IllegalStateException(
				gitRepositoryDir.getPath() + " does not exist");
		}
	}

	protected File gitRepositoryDir;
	protected GitWorkingDirectory gitWorkingDirectory;

	private JSONObject _getBranchJSONObject() {
		if ((jsonObject != null) && jsonObject.has("branch")) {
			return jsonObject.getJSONObject("branch");
		}

		JSONObject branchJSONObject = new JSONObject();

		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		LocalGitBranch currentLocalGitBranch =
			gitWorkingDirectory.getCurrentLocalGitBranch();

		branchJSONObject.put(
			"current_branch_name", currentLocalGitBranch.getName()
		).put(
			"current_branch_sha", currentLocalGitBranch.getSHA()
		);

		LocalGitBranch upstreamLocalGitBranch =
			gitWorkingDirectory.getUpstreamLocalGitBranch();

		branchJSONObject.put(
			"merge_branch_sha",
			gitWorkingDirectory.getMergeBaseCommitSHA(
				currentLocalGitBranch, upstreamLocalGitBranch)
		).put(
			"upstream_branch_name", upstreamLocalGitBranch.getName()
		).put(
			"upstream_branch_sha", upstreamLocalGitBranch.getSHA()
		);

		File workingDirectory = gitWorkingDirectory.getWorkingDirectory();

		List<String> modifiedFiles = new ArrayList<>();

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			modifiedFiles.add(
				JenkinsResultsParserUtil.getPathRelativeTo(
					modifiedFile, workingDirectory));
		}

		branchJSONObject.put("modified_files", modifiedFiles);

		if (gitWorkingDirectory instanceof PortalGitWorkingDirectory) {
			PortalGitWorkingDirectory portalGitWorkingDirectory =
				(PortalGitWorkingDirectory)gitWorkingDirectory;

			List<String> modifiedModuleDirs = new ArrayList<>();

			try {
				for (File modifiedModuleDir :
						portalGitWorkingDirectory.getModifiedModuleDirsList()) {

					modifiedModuleDirs.add(
						JenkinsResultsParserUtil.getPathRelativeTo(
							modifiedModuleDir, workingDirectory));
				}

				branchJSONObject.put("modified_modules", modifiedModuleDirs);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		return branchJSONObject;
	}

	private static final Pattern _jobNamePattern = Pattern.compile(
		"[^\\(]+\\((?<upstreamBranchName>[^\\)]+)\\)");

	private final String _upstreamBranchName;

}