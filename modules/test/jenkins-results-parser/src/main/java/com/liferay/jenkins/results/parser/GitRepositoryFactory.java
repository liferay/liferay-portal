/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public class GitRepositoryFactory {

	public static LocalGitRepository getLocalGitRepository(
		String repositoryName, String upstreamBranchName) {

		return new DefaultLocalGitRepository(
			repositoryName, upstreamBranchName);
	}

	public static LocalGitRepository getLocalGitRepository(
		String repositoryName, String upstreamBranchName, File repositoryDir) {

		return new DefaultLocalGitRepository(
			repositoryName, upstreamBranchName, repositoryDir);
	}

	public static RemoteGitRepository getRemoteGitRepository(
		GitRemote gitRemote) {

		String hostname = gitRemote.getHostname();

		if (hostname.equalsIgnoreCase("github.com")) {
			return new GitHubRemoteGitRepository(gitRemote);
		}

		return new DefaultRemoteGitRepository(gitRemote);
	}

	public static RemoteGitRepository getRemoteGitRepository(String remoteURL) {
		Matcher matcher = GitRemote.getRemoteURLMatcher(remoteURL);

		if ((matcher == null) || !matcher.find()) {
			throw new RuntimeException("Invalid remote URL " + remoteURL);
		}

		String patternString = String.valueOf(matcher.pattern());

		String username = "liferay";

		if (patternString.contains("(?<username>")) {
			username = matcher.group("username");
		}

		return getRemoteGitRepository(
			matcher.group("hostname"), matcher.group("gitRepositoryName"),
			username);
	}

	public static RemoteGitRepository getRemoteGitRepository(
		String hostname, String gitRepositoryName, String username) {

		if (hostname.equalsIgnoreCase("github.com")) {
			return new GitHubRemoteGitRepository(gitRepositoryName, username);
		}

		return new DefaultRemoteGitRepository(
			hostname, gitRepositoryName, username);
	}

	public static WorkspaceGitRepository getWorkspaceGitRepository(
		PullRequest pullRequest) {

		String gitDirectoryName = JenkinsResultsParserUtil.getGitDirectoryName(
			pullRequest.getGitRepositoryName(),
			pullRequest.getUpstreamRemoteGitBranchName());

		WorkspaceGitRepository workspaceGitRepository =
			_workspaceGitRepositories.get(gitDirectoryName);

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (workspaceGitRepository != null) {
			workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

			buildDatabase.putWorkspaceGitRepository(
				gitDirectoryName, workspaceGitRepository);

			return workspaceGitRepository;
		}

		if (buildDatabase.hasWorkspaceGitRepository(gitDirectoryName)) {
			workspaceGitRepository = buildDatabase.getWorkspaceGitRepository(
				gitDirectoryName);

			workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

			_workspaceGitRepositories.put(
				gitDirectoryName, workspaceGitRepository);

			return workspaceGitRepository;
		}

		String gitRepositoryName =
			JenkinsResultsParserUtil.getGitRepositoryName(gitDirectoryName);
		String gitUpstreamBranchName =
			pullRequest.getUpstreamRemoteGitBranchName();

		if ((gitRepositoryName == null) || (gitUpstreamBranchName == null)) {
			throw new RuntimeException(
				"Unable to find git directory name " + gitDirectoryName);
		}

		if (gitRepositoryName.matches("liferay-plugins(-ee)?")) {
			workspaceGitRepository = new PluginsWorkspaceGitRepository(
				pullRequest, gitUpstreamBranchName);
		}
		else if (gitRepositoryName.matches("liferay-portal(-ee)?")) {
			workspaceGitRepository = new PortalWorkspaceGitRepository(
				pullRequest, gitUpstreamBranchName);
		}
		else if (gitRepositoryName.equals("liferay-qa-websites-ee")) {
			workspaceGitRepository = new QAWebsitesWorkspaceGitRepository(
				pullRequest, gitUpstreamBranchName);
		}
		else if (gitRepositoryName.equals("liferay-release-tool-ee")) {
			workspaceGitRepository =
				new LiferayReleaseToolWorkspaceGitRepository(
					pullRequest, gitUpstreamBranchName);
		}
		else {
			workspaceGitRepository = new DefaultWorkspaceGitRepository(
				pullRequest, gitUpstreamBranchName);
		}

		buildDatabase.putWorkspaceGitRepository(
			gitDirectoryName, workspaceGitRepository);

		_workspaceGitRepositories.put(gitDirectoryName, workspaceGitRepository);

		return workspaceGitRepository;
	}

	public static WorkspaceGitRepository getWorkspaceGitRepository(
		String gitDirectoryName) {

		return getWorkspaceGitRepository(
			JenkinsResultsParserUtil.getGitRepositoryName(gitDirectoryName),
			JenkinsResultsParserUtil.getGitUpstreamBranchName(
				gitDirectoryName));
	}

	public static WorkspaceGitRepository getWorkspaceGitRepository(
		String repositoryName, String upstreamBranchName) {

		String gitDirectoryName = JenkinsResultsParserUtil.getGitDirectoryName(
			repositoryName, upstreamBranchName);

		WorkspaceGitRepository workspaceGitRepository =
			_workspaceGitRepositories.get(gitDirectoryName);

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (workspaceGitRepository != null) {
			buildDatabase.putWorkspaceGitRepository(
				gitDirectoryName, workspaceGitRepository);

			return workspaceGitRepository;
		}

		if (buildDatabase.hasWorkspaceGitRepository(gitDirectoryName)) {
			workspaceGitRepository = buildDatabase.getWorkspaceGitRepository(
				gitDirectoryName);

			_workspaceGitRepositories.put(
				gitDirectoryName, workspaceGitRepository);

			return workspaceGitRepository;
		}

		String gitRepositoryName =
			JenkinsResultsParserUtil.getGitRepositoryName(gitDirectoryName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			upstreamBranchName =
				JenkinsResultsParserUtil.getGitUpstreamBranchName(
					gitDirectoryName);
		}

		if ((gitRepositoryName == null) || (upstreamBranchName == null)) {
			throw new RuntimeException(
				"Unable to find git directory name " + gitDirectoryName);
		}

		RemoteGitRef remoteGitRef = GitUtil.getRemoteGitRef(
			JenkinsResultsParserUtil.combine(
				"https://github.com/",
				JenkinsResultsParserUtil.getUpstreamUserName(
					repositoryName, upstreamBranchName),
				"/", gitRepositoryName, "/tree/", upstreamBranchName));

		if (gitRepositoryName.matches("liferay-plugins(-ee)?")) {
			workspaceGitRepository = new PluginsWorkspaceGitRepository(
				remoteGitRef, upstreamBranchName);
		}
		else if (gitRepositoryName.matches("liferay-portal(-ee)?")) {
			workspaceGitRepository = new PortalWorkspaceGitRepository(
				remoteGitRef, upstreamBranchName);
		}
		else if (gitRepositoryName.equals("liferay-qa-websites-ee")) {
			workspaceGitRepository = new QAWebsitesWorkspaceGitRepository(
				remoteGitRef, upstreamBranchName);
		}
		else if (gitRepositoryName.equals("liferay-release-tool-ee")) {
			workspaceGitRepository =
				new LiferayReleaseToolWorkspaceGitRepository(
					remoteGitRef, upstreamBranchName);
		}
		else {
			workspaceGitRepository = new DefaultWorkspaceGitRepository(
				remoteGitRef, upstreamBranchName);
		}

		buildDatabase.putWorkspaceGitRepository(
			gitDirectoryName, workspaceGitRepository);

		_workspaceGitRepositories.put(gitDirectoryName, workspaceGitRepository);

		return workspaceGitRepository;
	}

	protected static WorkspaceGitRepository getWorkspaceGitRepository(
		JSONObject jsonObject) {

		String gitDirectoryName = jsonObject.optString("directory_name", null);

		if (gitDirectoryName == null) {
			throw new RuntimeException("Invalid JSONObject " + jsonObject);
		}

		WorkspaceGitRepository workspaceGitRepository =
			_workspaceGitRepositories.get(gitDirectoryName);

		if (workspaceGitRepository != null) {
			return workspaceGitRepository;
		}

		String repositoryName = jsonObject.optString("name", null);

		if (repositoryName == null) {
			throw new RuntimeException("Invalid JSONObject " + jsonObject);
		}
		else if (repositoryName.matches("liferay-plugins(-ee)?")) {
			workspaceGitRepository = new PluginsWorkspaceGitRepository(
				jsonObject);
		}
		else if (repositoryName.matches("liferay-portal(-ee)?")) {
			workspaceGitRepository = new PortalWorkspaceGitRepository(
				jsonObject);
		}
		else if (repositoryName.equals("liferay-qa-websites-ee")) {
			workspaceGitRepository = new QAWebsitesWorkspaceGitRepository(
				jsonObject);
		}
		else if (repositoryName.equals("liferay-release-tool-ee")) {
			workspaceGitRepository =
				new LiferayReleaseToolWorkspaceGitRepository(jsonObject);
		}
		else {
			workspaceGitRepository = new DefaultWorkspaceGitRepository(
				jsonObject);
		}

		_workspaceGitRepositories.put(gitDirectoryName, workspaceGitRepository);

		return workspaceGitRepository;
	}

	private static final Map<String, WorkspaceGitRepository>
		_workspaceGitRepositories = new HashMap<>();

}