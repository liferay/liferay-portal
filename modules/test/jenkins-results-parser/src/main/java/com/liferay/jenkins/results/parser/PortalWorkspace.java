/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalWorkspace extends BaseWorkspace {

	public Job.BuildProfile getBuildProfile() {
		String buildProfileString = jsonObject.optString(
			"build_profile", "dxp");

		return Job.BuildProfile.getByString(buildProfileString);
	}

	public WorkspaceGitRepository getLiferayOSBAsahWorkspaceGitRepository() {
		return getWorkspaceGitRepository("com-liferay-osb-asah-private");
	}

	public WorkspaceGitRepository getLiferayOSBFaroWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		String portalUpstreamBranchName =
			portalWorkspaceGitRepository.getUpstreamBranchName();

		String repositoryName = "liferay-portal";

		if (!portalUpstreamBranchName.startsWith("release-")) {
			repositoryName += "-ee";
		}

		return getWorkspaceGitRepository(repositoryName);
	}

	public WorkspaceGitRepository
		getLiferayQAPortalLegacyWorkspaceGitRepository() {

		return getWorkspaceGitRepository("liferay-qa-portal-legacy-ee");
	}

	public PluginsWorkspaceGitRepository getPluginsWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository(
				portalWorkspaceGitRepository.getPluginsRepositoryDirName());

		if (!(workspaceGitRepository instanceof
				PluginsWorkspaceGitRepository)) {

			return null;
		}

		return (PluginsWorkspaceGitRepository)workspaceGitRepository;
	}

	public PortalWorkspaceGitRepository getPortalWorkspaceGitRepository() {
		WorkspaceGitRepository workspaceGitRepository =
			getPrimaryWorkspaceGitRepository();

		if (!(workspaceGitRepository instanceof PortalWorkspaceGitRepository)) {
			throw new RuntimeException(
				"The portal workspace Git repository is not set");
		}

		return (PortalWorkspaceGitRepository)workspaceGitRepository;
	}

	public void setBuildProfile(Job.BuildProfile buildProfile) {
		if (buildProfile == null) {
			throw new RuntimeException("Invalid build profile " + buildProfile);
		}

		jsonObject.put("build_profile", buildProfile.toString());
	}

	public void setCommitOSBAsahModule(boolean commitOSBAsahModule) {
		_commitOSBAsahModule = commitOSBAsahModule;
	}

	public void setOSBAsahGitHubURL(String osbAsahGitHubURL) {
		_osbAsahGitHubURL = osbAsahGitHubURL;
	}

	public void setOSBFaroGitHubURL(String osbFaroGitHubURL) {
		_osbFaroGitHubURL = osbFaroGitHubURL;
	}

	@Override
	public void setUp() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		portalWorkspaceGitRepository.setUp();

		_configureLiferayBladeSamplesWorkspaceGitRepository();
		_configureLiferayFacesAlloyWorkspaceGitRepository();
		_configureLiferayFacesBridgeImplWorkspaceGitRepository();
		_configureLiferayFacesPortalWorkspaceGitRepository();
		_configureLiferayFacesShowcaseWorkspaceGitRepository();
		_configureLiferayOSBAsahWorkspaceGitRepository();
		_configureLiferayOSBFaroWorkspaceGitRepository();
		_configureLiferayReleaseToolWorkspaceGitRepository();
		_configurePluginsWorkspaceGitRepository();
		_configurePortalsPlutoWorkspaceGitRepository();
		_configurePortletAPIGitRepository();

		super.setUp();

		Job.BuildProfile buildProfile = getBuildProfile();

		if (buildProfile == Job.BuildProfile.DXP) {
			portalWorkspaceGitRepository.setUpPortalProfile();
		}

		portalWorkspaceGitRepository.setUpTCKHome();

		updateOSBAsahModule();
	}

	protected PortalWorkspace(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected PortalWorkspace(
		String primaryRepositoryName, String upstreamBranchName) {

		super(primaryRepositoryName, upstreamBranchName);
	}

	protected PortalWorkspace(
		String primaryRepositoryName, String upstreamBranchName,
		String jobName) {

		super(primaryRepositoryName, upstreamBranchName, jobName);
	}

	protected void copyLiferayOSBAsahRepositoryToModule() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		File modulesDir = new File(
			portalWorkspaceGitRepository.getDirectory(),
			"modules/dxp/apps/osb/osb-asah");

		if (!modulesDir.exists()) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("com-liferay-osb-asah-private");

		if (workspaceGitRepository == null) {
			return;
		}

		List<PathMatcher> deleteExcludePathMatchers =
			JenkinsResultsParserUtil.toPathMatchers(
				JenkinsResultsParserUtil.combine(
					JenkinsResultsParserUtil.getCanonicalPath(modulesDir),
					File.separator),
				".gradle/", ".gitrepo", "ci-merge", "gradle/");

		try {
			JenkinsResultsParserUtil.delete(
				modulesDir, null, deleteExcludePathMatchers);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		List<PathMatcher> copyExcludePathMatchers =
			JenkinsResultsParserUtil.toPathMatchers(
				JenkinsResultsParserUtil.combine(
					JenkinsResultsParserUtil.getCanonicalPath(
						workspaceGitRepository.getDirectory()),
					File.separator),
				".git/", ".gradle/", "gradle/", "settings.gradle");

		try {
			JenkinsResultsParserUtil.copy(
				workspaceGitRepository.getDirectory(), modulesDir, null,
				copyExcludePathMatchers);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		try {
			Map<String, String> parameters = new HashMap<>();

			parameters.put("module.dir", "dxp/apps/osb/osb-asah");
			parameters.put(
				"portal.dir",
				JenkinsResultsParserUtil.getCanonicalPath(
					portalWorkspaceGitRepository.getDirectory()));

			AntUtil.callTarget(
				portalWorkspaceGitRepository.getDirectory(), "build-test.xml",
				"clean-version-override", parameters);
		}
		catch (AntException antException) {
			throw new RuntimeException(antException);
		}

		List<File> versionOverrideFiles = JenkinsResultsParserUtil.findFiles(
			modulesDir,
			JenkinsResultsParserUtil.combine(
				".version-override-", modulesDir.getName(), ".properties"));

		for (File versionOverrideFile : versionOverrideFiles) {
			JenkinsResultsParserUtil.delete(versionOverrideFile);
		}

		GitWorkingDirectory gitWorkingDirectory =
			portalWorkspaceGitRepository.getGitWorkingDirectory();

		String gitStatus = gitWorkingDirectory.status();

		System.out.println(gitStatus);

		if (!_commitOSBAsahModule || gitStatus.contains("nothing to commit")) {
			return;
		}

		gitWorkingDirectory.commitFileToCurrentBranch(
			"modules/dxp/apps/osb/osb-asah",
			JenkinsResultsParserUtil.combine(
				"Committing changes from ", workspaceGitRepository.getName(),
				" at ", workspaceGitRepository.getSenderBranchSHA(),
				" for testing on CI"));
	}

	protected void updateOSBAsahModule() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		File ciMergeFile = new File(
			portalWorkspaceGitRepository.getDirectory(),
			"modules/dxp/apps/osb/osb-asah/ci-merge");

		if (!ciMergeFile.exists()) {
			return;
		}

		copyLiferayOSBAsahRepositoryToModule();
	}

	private void _configureLiferayBladeSamplesWorkspaceGitRepository() {
		boolean updated = _updateWorkspaceGitRepository(
			"git-commit/liferay-blade-samples", "liferay-blade-samples");

		if (!updated) {
			_updateWorkspaceGitRepository(
				"git-commit-blade-samples", "liferay-blade-samples");
		}
	}

	private void _configureLiferayFacesAlloyWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		String gitHubURL =
			portalWorkspaceGitRepository.getLiferayFacesAlloyURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("liferay-faces-alloy");

		if (workspaceGitRepository == null) {
			return;
		}

		workspaceGitRepository.setGitHubURL(gitHubURL);
	}

	private void _configureLiferayFacesBridgeImplWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		String gitHubURL =
			portalWorkspaceGitRepository.getLiferayFacesBridgeImplURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("liferay-faces-bridge-impl");

		if (workspaceGitRepository == null) {
			return;
		}

		workspaceGitRepository.setGitHubURL(gitHubURL);
	}

	private void _configureLiferayFacesPortalWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		String gitHubURL =
			portalWorkspaceGitRepository.getLiferayFacesPortalURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("liferay-faces-portal");

		if (workspaceGitRepository == null) {
			return;
		}

		workspaceGitRepository.setGitHubURL(gitHubURL);
	}

	private void _configureLiferayFacesShowcaseWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		String gitHubURL =
			portalWorkspaceGitRepository.getLiferayFacesShowcaseURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("liferay-faces-showcase");

		if (workspaceGitRepository == null) {
			return;
		}

		workspaceGitRepository.setGitHubURL(gitHubURL);
	}

	private void _configureLiferayOSBAsahWorkspaceGitRepository() {
		boolean updated = _updateWorkspaceGitRepository(
			"modules/dxp/apps/osb/osb-asah/ci-merge",
			"com-liferay-osb-asah-private");

		if (updated || (_osbAsahGitHubURL == null)) {
			return;
		}

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("com-liferay-osb-asah-private");

		if (workspaceGitRepository == null) {
			return;
		}

		workspaceGitRepository.setGitHubURL(_osbAsahGitHubURL);
	}

	private void _configureLiferayOSBFaroWorkspaceGitRepository() {
		if (_osbFaroGitHubURL == null) {
			return;
		}

		WorkspaceGitRepository liferayOSBFaroWorkspaceGitRepository =
			getLiferayOSBFaroWorkspaceGitRepository();

		if (liferayOSBFaroWorkspaceGitRepository == null) {
			return;
		}

		liferayOSBFaroWorkspaceGitRepository.setGitHubURL(_osbFaroGitHubURL);
	}

	private void _configureLiferayReleaseToolWorkspaceGitRepository() {
		_updateWorkspaceGitRepository(
			"git-commit/liferay-release-tool-ee", "liferay-release-tool-ee");

		LiferayReleaseToolWorkspaceGitRepository
			liferayReleaseToolEEWorkspaceGitRepository =
				_getLiferayReleaseToolEEWorkspaceGitRepository();

		if (liferayReleaseToolEEWorkspaceGitRepository == null) {
			return;
		}

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		liferayReleaseToolEEWorkspaceGitRepository.setPortalUpstreamBranchName(
			portalWorkspaceGitRepository.getUpstreamBranchName());
	}

	private void _configurePluginsWorkspaceGitRepository() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			getPortalWorkspaceGitRepository();

		_updateWorkspaceGitRepository(
			"git-commit-plugins",
			portalWorkspaceGitRepository.getPluginsRepositoryDirName());

		PluginsWorkspaceGitRepository pluginsWorkspaceGitRepository =
			getPluginsWorkspaceGitRepository();

		if (pluginsWorkspaceGitRepository == null) {
			return;
		}

		pluginsWorkspaceGitRepository.setPortalUpstreamBranchName(
			portalWorkspaceGitRepository.getUpstreamBranchName());
	}

	private void _configurePortalsPlutoWorkspaceGitRepository() {
		boolean updated = _updateWorkspaceGitRepository(
			"git-commit/portals-pluto", "portals-pluto");

		if (!updated) {
			_updateWorkspaceGitRepository(
				"git-commit-portals-pluto", "portals-pluto");
		}
	}

	private void _configurePortletAPIGitRepository() {
		boolean updated = _updateWorkspaceGitRepository(
			"git-commit/portlet-api", "portlet-api");

		if (!updated) {
			_updateWorkspaceGitRepository(
				"git-commit-portlet-api", "portlet-api");
		}
	}

	private LiferayReleaseToolWorkspaceGitRepository
		_getLiferayReleaseToolEEWorkspaceGitRepository() {

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository("liferay-release-tool-ee");

		if (!(workspaceGitRepository instanceof
				LiferayReleaseToolWorkspaceGitRepository)) {

			return null;
		}

		return (LiferayReleaseToolWorkspaceGitRepository)workspaceGitRepository;
	}

	private boolean _updateWorkspaceGitRepository(
		String gitCommitFilePath, String gitRepositoryName) {

		WorkspaceGitRepository workspaceGitRepository =
			getWorkspaceGitRepository(gitRepositoryName);

		if (workspaceGitRepository == null) {
			return false;
		}

		WorkspaceGitRepository primaryWorkspaceGitRepository =
			getPrimaryWorkspaceGitRepository();

		String gitCommit = primaryWorkspaceGitRepository.getFileContent(
			gitCommitFilePath);

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitCommit)) {
			return false;
		}

		if (JenkinsResultsParserUtil.isSHA(gitCommit)) {
			workspaceGitRepository.setCommitFileIsSHA(true);

			workspaceGitRepository.setSenderBranchSHA(gitCommit);

			return true;
		}

		if (!GitUtil.isValidGitHubRefURL(gitCommit) &&
			!PullRequest.isValidGitHubPullRequestURL(gitCommit)) {

			return false;
		}

		workspaceGitRepository.setGitHubURL(gitCommit);

		return true;
	}

	private boolean _commitOSBAsahModule;
	private String _osbAsahGitHubURL;
	private String _osbFaroGitHubURL;

}