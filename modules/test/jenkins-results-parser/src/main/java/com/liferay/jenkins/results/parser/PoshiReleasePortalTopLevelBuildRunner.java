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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * @author Kenji Heigel
 */
public class PoshiReleasePortalTopLevelBuildRunner
	extends PortalTopLevelBuildRunner<PortalTopLevelBuildData> {

	@Override
	public Workspace getWorkspace() {
		Workspace workspace = super.getWorkspace();

		WorkspaceGitRepository primaryWorkspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		primaryWorkspaceGitRepository.setRebase(true);

		PortalTopLevelBuildData portalTopLevelBuildData = getBuildData();

		primaryWorkspaceGitRepository.setGitHubURL(
			portalTopLevelBuildData.getPortalGitHubURL());

		return workspace;
	}

	@Override
	public void run() {
		validateBuildParameters();

		publishJenkinsReport();

		updateBuildDescription();

		setUpWorkspace();

		_preparePoshiPortalPullRequests();

		invokeDownstreamBuilds();

		waitForDownstreamBuildsToComplete();

		publishJenkinsReport();

		_deleteRemoteGitBranches();
	}

	protected PoshiReleasePortalTopLevelBuildRunner(
		PortalTopLevelBuildData portalTopLevelBuildData) {

		super(portalTopLevelBuildData);
	}

	@Override
	protected void invokeDownstreamBuilds() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		for (Map.Entry<GitWorkingDirectory, PullRequest> entry :
				_pullRequests.entrySet()) {

			GitWorkingDirectory gitWorkingDirectory = entry.getKey();

			for (String jobName : _getJobNames(gitWorkingDirectory)) {
				topLevelBuild.addDownstreamBuilds(
					_getBuildInvocationURL(jobName, entry));
			}
		}
	}

	@Override
	protected void prepareInvocationBuildDataList() {
	}

	@Override
	protected void validateBuildParameters() {
		_validateBuildParameterPortalGitHubURL();
	}

	private void _commitGradlePluginsPoshiRunnerCache(
			GitWorkingDirectory gitWorkingDirectory)
		throws IOException {

		File buildGradleFile = null;

		if (gitWorkingDirectory instanceof PortalGitWorkingDirectory) {
			buildGradleFile = _getBuildTestGradleFile(gitWorkingDirectory);
		}

		if (gitWorkingDirectory instanceof QAWebsitesGitWorkingDirectory) {
			buildGradleFile = new File(
				gitWorkingDirectory.getWorkingDirectory(), "lxc");
		}

		gitWorkingDirectory.commitFileToCurrentBranch(
			buildGradleFile.getCanonicalPath(),
			"POSHI-0 CI TESTING ONLY: Use latest gradle-plugins-poshi-runner");

		gitWorkingDirectory.commitFileToCurrentBranch(
			".m2-tmp", "POSHI-0 CI TESTING ONLY: FAKE GRADLE CACHE");
	}

	private LocalGitBranch _createLocalGitBranch(
			GitWorkingDirectory gitWorkingDirectory)
		throws IOException {

		LocalGitBranch pullRequestLocalGitBranch = null;

		String branchName =
			gitWorkingDirectory.getUpstreamBranchName() + "-temp-pr-" +
				System.currentTimeMillis();

		if (gitWorkingDirectory instanceof QAWebsitesGitWorkingDirectory) {
			pullRequestLocalGitBranch =
				gitWorkingDirectory.createLocalGitBranch(branchName, true);
		}
		else {
			pullRequestLocalGitBranch =
				gitWorkingDirectory.createLocalGitBranch(
					branchName, true,
					_getDistPortalBundlesBuildSHA(
						gitWorkingDirectory.getUpstreamBranchName()));
		}

		gitWorkingDirectory.checkoutLocalGitBranch(pullRequestLocalGitBranch);

		gitWorkingDirectory.reset("--hard");

		_updateGradlePluginsPoshiRunnerDependency(gitWorkingDirectory);

		_commitGradlePluginsPoshiRunnerCache(gitWorkingDirectory);

		return pullRequestLocalGitBranch;
	}

	private PullRequest _createPortalPullRequest(
		GitWorkingDirectory gitWorkingDirectory) {

		try {
			LocalGitBranch localGitBranch =
				gitWorkingDirectory.getCurrentLocalGitBranch();

			String upstreamBranchName =
				gitWorkingDirectory.getUpstreamBranchName();

			if ((gitWorkingDirectory instanceof
					QAWebsitesGitWorkingDirectory) ||
				!upstreamBranchName.equals("master")) {

				localGitBranch = _createLocalGitBranch(gitWorkingDirectory);
			}

			RemoteGitBranch remoteGitBranch =
				gitWorkingDirectory.pushToRemoteGitRepository(
					true, localGitBranch, localGitBranch.getName(),
					_getPullRequestRemoteGitRepository(gitWorkingDirectory));

			_remoteGitBranches.put(gitWorkingDirectory, remoteGitBranch);

			PortalTopLevelBuildData portalTopLevelBuildData = getBuildData();

			return PullRequestFactory.newPullRequest(
				gitWorkingDirectory.createPullRequest(
					"Testing Poshi Release: " +
						portalTopLevelBuildData.getBuildURL(),
					remoteGitBranch.getName(), remoteGitBranch.getUsername(),
					remoteGitBranch.getUsername(),
					"Poshi Release | " + upstreamBranchName));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to create pull request", ioException);
		}
	}

	private void _deleteRemoteGitBranches() {
		for (Map.Entry<GitWorkingDirectory, RemoteGitBranch> entry :
				_remoteGitBranches.entrySet()) {

			RemoteGitBranch remoteGitBranch = entry.getValue();

			if (remoteGitBranch != null) {
				GitWorkingDirectory gitWorkingDirectory = entry.getKey();

				String upstreamBranchName =
					gitWorkingDirectory.getUpstreamBranchName();

				if (upstreamBranchName.equals("master")) {
					continue;
				}

				System.out.println(
					"Deleting remote git branch: " +
						gitWorkingDirectory.getGitDirectory());

				gitWorkingDirectory.deleteRemoteGitBranch(remoteGitBranch);
			}
		}
	}

	private List<File> _getBuildGradleFiles(
		GitWorkingDirectory gitWorkingDirectory) {

		if (gitWorkingDirectory instanceof PortalGitWorkingDirectory) {
			return new ArrayList<>(
				Arrays.asList(
					new File(
						gitWorkingDirectory.getWorkingDirectory(),
						"portal-web/build-test.gradle")));
		}

		if (gitWorkingDirectory instanceof QAWebsitesGitWorkingDirectory) {
			return JenkinsResultsParserUtil.findFiles(
				new File(gitWorkingDirectory.getWorkingDirectory(), "lxc"),
				"build\\.gradle");
		}

		return Collections.emptyList();
	}

	private String _getBuildInvocationURL(
		String jobName, Map.Entry<GitWorkingDirectory, PullRequest> entry) {

		StringBuilder sb = new StringBuilder();

		BuildData buildData = getBuildData();

		sb.append(getBaseInvocationURL(buildData.getCohortName(), jobName));

		sb.append("/job/");
		sb.append(jobName);
		sb.append("/buildWithParameters?");

		try {
			Map<String, String> invocationParameters = new HashMap<>();

			invocationParameters.put(
				"JENKINS_GITHUB_BRANCH_NAME",
				_getGitHubBranchName("JENKINS_GITHUB_URL"));
			invocationParameters.put(
				"JENKINS_GITHUB_BRANCH_USERNAME",
				_getGitHubBranchUsername("JENKINS_GITHUB_URL"));
			invocationParameters.put(
				"JENKINS_TOP_LEVEL_BUILD_URL", buildData.getBuildURL());
			invocationParameters.put(
				"PARENT_BUILD_URL", buildData.getBuildURL());

			PullRequest pullRequest = entry.getValue();

			if (jobName.equals("test-qa-websites-source-format") ||
				jobName.equals("test-portal-source-format")) {

				invocationParameters.put("JENKINS_JOB_VARIANT", "sf");
				invocationParameters.put(
					"PULL_REQUEST_URL", pullRequest.getHtmlURL());
			}
			else if (jobName.startsWith("test-portal-acceptance-pullrequest")) {
				GitWorkingDirectory gitWorkingDirectory = entry.getKey();

				String upstreamBranchName =
					gitWorkingDirectory.getUpstreamBranchName();

				invocationParameters.put(
					"CI_TEST_SUITE", _getCITestSuite(upstreamBranchName));

				invocationParameters.put(
					"GITHUB_PULL_REQUEST_NUMBER", pullRequest.getNumber());
				invocationParameters.put(
					"GITHUB_RECEIVER_USERNAME",
					pullRequest.getReceiverUsername());
				invocationParameters.put(
					"GITHUB_SENDER_BRANCH_NAME",
					pullRequest.getSenderBranchName());
				invocationParameters.put(
					"GITHUB_SENDER_BRANCH_SHA", pullRequest.getSenderSHA());
				invocationParameters.put(
					"GITHUB_SENDER_USERNAME", pullRequest.getSenderUsername());
				invocationParameters.put(
					"GITHUB_UPSTREAM_BRANCH_NAME", upstreamBranchName);
				invocationParameters.put(
					"GITHUB_UPSTREAM_BRANCH_SHA",
					_getDistPortalBundlesBuildSHA(upstreamBranchName));
				invocationParameters.put(
					"JENKINS_JOB_VARIANT", _getCITestSuite(upstreamBranchName));
				invocationParameters.put(
					"PORTAL_BUNDLES_DIST_URL",
					JenkinsResultsParserUtil.getDistPortalBundlesBuildURL(
						upstreamBranchName));
			}

			for (Map.Entry<String, String> invocationParameter :
					invocationParameters.entrySet()) {

				String invocationParameterValue =
					invocationParameter.getValue();

				if (JenkinsResultsParserUtil.isNullOrEmpty(
						invocationParameterValue)) {

					continue;
				}

				sb.append(invocationParameter.getKey());
				sb.append("=");
				sb.append(invocationParameterValue);
				sb.append("&");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (sb.charAt(sb.length() - 1) == '&') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	private File _getBuildTestGradleFile(
		GitWorkingDirectory gitWorkingDirectory) {

		return new File(
			gitWorkingDirectory.getWorkingDirectory(),
			"portal-web/build-test.gradle");
	}

	private String _getCITestSuite(String upstreamBranchName) {
		if (upstreamBranchName.equals("master")) {
			return getBuildParameter(
				_NAME_BUILD_PARAMETER_PORTAL_MASTER_CI_TEST_SUITE);
		}

		return _DEFAULT_CI_TEST_SUITE;
	}

	private String _getDistPortalBundlesBuildSHA(String upstreamBranchName)
		throws IOException {

		String distPortalBundlesBuildURL = null;

		try {
			distPortalBundlesBuildURL =
				JenkinsResultsParserUtil.getDistPortalBundlesBuildURL(
					upstreamBranchName);
		}
		catch (Exception exception) {
			System.out.println("WARNING: Unable to get a dist portal bundle");
		}

		if (!JenkinsResultsParserUtil.isURL(distPortalBundlesBuildURL)) {
			StringBuilder sb = new StringBuilder();

			sb.append("https://github.com/liferay/liferay-portal");

			if (!upstreamBranchName.equals("master")) {
				sb.append("-ee");
			}

			sb.append("/tree/");
			sb.append(upstreamBranchName);

			RemoteGitRef remoteGitRef = GitUtil.getRemoteGitRef(sb.toString());

			return remoteGitRef.getSHA();
		}

		String distPortalBundlesBuildSHA = JenkinsResultsParserUtil.toString(
			distPortalBundlesBuildURL + "/git-hash");

		return distPortalBundlesBuildSHA.trim();
	}

	private String _getGitHubBranchName(String parameterName) {
		BuildData buildData = getBuildData();

		String gitHubURL = buildData.getBuildParameter(parameterName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return null;
		}

		Matcher matcher = _gitHubURLPattern.matcher(gitHubURL);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("branch");
	}

	private String _getGitHubBranchUsername(String parameterName) {
		BuildData buildData = getBuildData();

		String gitHubURL = buildData.getBuildParameter(parameterName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return null;
		}

		Matcher matcher = _gitHubURLPattern.matcher(gitHubURL);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("username");
	}

	private String _getGradlePluginsPoshiRunnerVersion() {
		if (_gradlePluginsPoshiRunnerVersion != null) {
			return _gradlePluginsPoshiRunnerVersion;
		}

		Workspace workspace = getWorkspace();

		WorkspaceGitRepository primaryWorkspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		File bndFile = new File(
			primaryWorkspaceGitRepository.getDirectory(),
			"modules/sdk/gradle-plugins-poshi-runner/bnd.bnd");

		try {
			String fileContent = JenkinsResultsParserUtil.read(bndFile);

			Matcher matcher = _bundleVersionPattern.matcher(fileContent);

			matcher.find();

			_gradlePluginsPoshiRunnerVersion = matcher.group(1);

			return _gradlePluginsPoshiRunnerVersion;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private List<String> _getJobNames(GitWorkingDirectory gitWorkingDirectory) {
		if (gitWorkingDirectory instanceof QAWebsitesGitWorkingDirectory) {
			return Collections.singletonList("test-qa-websites-source-format");
		}

		if (gitWorkingDirectory instanceof PortalGitWorkingDirectory) {
			List<String> jobNames = new ArrayList<>();

			String upstreamBranchName =
				gitWorkingDirectory.getUpstreamBranchName();

			jobNames.add(
				"test-portal-acceptance-pullrequest(" + upstreamBranchName +
					")");

			if (upstreamBranchName.equals("master")) {
				jobNames.add("test-portal-source-format");
			}

			return jobNames;
		}

		return Collections.emptyList();
	}

	private String _getPortalGitHubURL() {
		return getBuildParameter(_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL);
	}

	private RemoteGitRepository _getPullRequestRemoteGitRepository(
		GitWorkingDirectory gitWorkingDirectory) {

		String portalGitHubURL = _getPortalGitHubURL();

		Matcher matcher = GitRemote.getRemoteURLMatcher(portalGitHubURL);

		if (matcher.find()) {
			return GitRepositoryFactory.getRemoteGitRepository(
				matcher.group("hostname"),
				gitWorkingDirectory.getGitRepositoryName(),
				matcher.group("username"));
		}

		throw new RuntimeException(
			"Invalid portal GitHub URL: " + portalGitHubURL);
	}

	private void _preparePoshiPortalPullRequests() {
		Workspace workspace = getWorkspace();

		WorkspaceGitRepository primaryWorkspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		File jarFile = new File(
			primaryWorkspaceGitRepository.getDirectory(),
			".m2-tmp/com/liferay/com.liferay.gradle.plugins.poshi.runner/" +
				_getGradlePluginsPoshiRunnerVersion() +
					"/com.liferay.gradle.plugins.poshi.runner-" +
						_getGradlePluginsPoshiRunnerVersion() + ".jar");

		if (!jarFile.exists()) {
			throw new RuntimeException(
				"Poshi Runner Gradle Plugin cached jar does not exist: " +
					jarFile);
		}

		for (WorkspaceGitRepository workspaceGitRepository :
				workspace.getWorkspaceGitRepositories()) {

			if (workspaceGitRepository instanceof
					PortalWorkspaceGitRepository ||
				workspaceGitRepository instanceof
					QAWebsitesWorkspaceGitRepository) {

				GitWorkingDirectory gitWorkingDirectory =
					workspaceGitRepository.getGitWorkingDirectory();

				_pullRequests.put(
					gitWorkingDirectory,
					_createPortalPullRequest(gitWorkingDirectory));
			}
		}
	}

	private void _updateGradlePluginsPoshiRunnerDependency(
			GitWorkingDirectory gitWorkingDirectory)
		throws IOException {

		Workspace workspace = getWorkspace();

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		File sourceCacheDir = new File(
			workspaceGitRepository.getDirectory(),
			".m2-tmp/com/liferay/com.liferay.gradle.plugins.poshi.runner");

		File targetCacheDir = new File(
			gitWorkingDirectory.getWorkingDirectory(),
			".m2-tmp/com/liferay/com.liferay.gradle.plugins.poshi.runner");

		FileUtils.copyDirectory(sourceCacheDir, targetCacheDir);

		List<File> buildGradleFiles = _getBuildGradleFiles(gitWorkingDirectory);

		_updateGradlePluginsPoshiRunnerDependency(buildGradleFiles);

		if (gitWorkingDirectory instanceof QAWebsitesGitWorkingDirectory) {
			for (File buildGradleFile : buildGradleFiles) {
				String buildGradleFileContent = JenkinsResultsParserUtil.read(
					buildGradleFile);

				JenkinsResultsParserUtil.write(
					buildGradleFile,
					buildGradleFileContent.replaceAll(
						"([\\s]*)(mavenLocal\\(\\))",
						"$1$2\n$1maven {$1\turl file(\"" +
							gitWorkingDirectory.getWorkingDirectory() +
								"/.m2-tmp\")$1}"));
			}
		}
	}

	private void _updateGradlePluginsPoshiRunnerDependency(List<File> files)
		throws IOException {

		for (File file : files) {
			String fileContent = JenkinsResultsParserUtil.read(file);

			JenkinsResultsParserUtil.write(
				file,
				fileContent.replaceAll(
					_BUILD_GRADLE_PLUGINS_REGEX,
					"$1$2$1classpath group: \"com.liferay\", name: " +
						"\"com.liferay.gradle.plugins.poshi.runner\", " +
							"version: \"" +
								_getGradlePluginsPoshiRunnerVersion() + "\""));
		}
	}

	private void _validateBuildParameterPortalGitHubURL() {
		String portalGitHubURL = _getPortalGitHubURL();

		if ((portalGitHubURL == null) || portalGitHubURL.isEmpty()) {
			failBuildRunner(
				_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL + " is null");
		}

		String failureMessage = JenkinsResultsParserUtil.combine(
			_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL,
			" has an invalid Portal GitHub URL <a href=\"", portalGitHubURL,
			"\">", portalGitHubURL, "</a>");

		Matcher matcher = _gitHubURLPattern.matcher(portalGitHubURL);

		if (!matcher.find()) {
			failBuildRunner(failureMessage);
		}

		String repositoryName = matcher.group("repository");

		if (!repositoryName.equals("liferay-portal")) {
			failBuildRunner(failureMessage);
		}
	}

	private static final String _BUILD_GRADLE_PLUGINS_REGEX =
		"([\\s]*)(.*\"com\\.liferay\\.gradle\\.plugins\\.defaults\".*)";

	private static final String _DEFAULT_CI_TEST_SUITE = "poshi-release";

	private static final String _NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL =
		"PORTAL_GITHUB_URL";

	private static final String
		_NAME_BUILD_PARAMETER_PORTAL_MASTER_CI_TEST_SUITE =
			"PORTAL_MASTER_CI_TEST_SUITE";

	private static final Pattern _bundleVersionPattern = Pattern.compile(
		"Bundle-Version:[\\s]*(.*)");
	private static final Pattern _gitHubURLPattern = Pattern.compile(
		"https://github.com/(?<username>[^/]+)/(?<repository>[^/]+)/" +
			"(commits|tree)/(?<branch>[^/]+)");

	private String _gradlePluginsPoshiRunnerVersion;
	private final Map<GitWorkingDirectory, PullRequest> _pullRequests =
		new HashMap<>();
	private final Map<GitWorkingDirectory, RemoteGitBranch> _remoteGitBranches =
		new HashMap<>();

}