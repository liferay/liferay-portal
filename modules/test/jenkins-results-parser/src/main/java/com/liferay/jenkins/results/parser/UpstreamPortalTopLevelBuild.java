/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.URL;

/**
 * @author Michael Hashimoto
 */
public class UpstreamPortalTopLevelBuild
	extends PortalTopLevelBuild implements PortalWorkspaceBuild {

	public UpstreamPortalTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);
	}

	@Override
	public String getBranchName() {
		String portalUpstreamBranchName = getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			return portalUpstreamBranchName;
		}

		return super.getBranchName();
	}

	public String getPortalUpstreamBranchName() {
		String branchName = getBranchName();

		if (branchName.endsWith("-private")) {
			return branchName.substring(0, branchName.lastIndexOf("-private"));
		}

		return branchName;
	}

	@Override
	public PortalWorkspace getPortalWorkspace() {
		Workspace workspace = getWorkspace();

		if (!(workspace instanceof PortalWorkspace)) {
			return null;
		}

		return (PortalWorkspace)workspace;
	}

	@Override
	public Workspace getWorkspace() {
		Workspace workspace = WorkspaceFactory.newWorkspace(
			getBaseGitRepositoryName(), getBranchName(), getJobName());

		if (workspace instanceof PortalWorkspace) {
			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			portalWorkspace.setBuildProfile(getBuildProfile());
			portalWorkspace.setOSBAsahGitHubURL(_getOSBAsahGitHubURL());
			portalWorkspace.setOSBFaroGitHubURL(_getOSBFaroGitHubURL());

			String portalUpstreamBranchName = getPortalUpstreamBranchName();

			if (!portalUpstreamBranchName.equals(getBranchName())) {
				portalWorkspace.setPortalUpstreamBranchName(
					portalUpstreamBranchName);
			}
		}

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		String portalGitHubURL = _getPortalGitHubURL();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalGitHubURL)) {
			workspaceGitRepository.setGitHubURL(portalGitHubURL);
		}

		String portalGitCommit = _getPortalGitCommit();

		if (JenkinsResultsParserUtil.isSHA(portalGitCommit)) {
			workspaceGitRepository.setSenderBranchSHA(portalGitCommit);
		}

		return workspace;
	}

	private String _getOSBAsahGitHubURL() {
		String osbAsahGitHubURL = getParameterValue("OSB_ASAH_GITHUB_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(osbAsahGitHubURL)) {
			return osbAsahGitHubURL;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			return controllerBuild.getParameterValue("OSB_ASAH_GITHUB_URL");
		}

		return null;
	}

	private String _getOSBFaroGitHubURL() {
		String osbFaroGitHubURL = getParameterValue("OSB_FARO_GITHUB_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(osbFaroGitHubURL)) {
			return osbFaroGitHubURL;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			osbFaroGitHubURL = controllerBuild.getParameterValue(
				"OSB_FARO_GITHUB_URL");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(osbFaroGitHubURL)) {
				return osbFaroGitHubURL;
			}
		}

		return "https://github.com/liferay/liferay-portal/tree/master";
	}

	private String _getPortalGitCommit() {
		String portalGitCommit = getParameterValue("PORTAL_GIT_COMMIT");

		if (JenkinsResultsParserUtil.isSHA(portalGitCommit)) {
			return portalGitCommit;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			portalGitCommit = controllerBuild.getParameterValue(
				"PORTAL_GIT_COMMIT");

			if (JenkinsResultsParserUtil.isSHA(portalGitCommit)) {
				return portalGitCommit;
			}
		}

		String portalBundlesDistURL = getParameterValue(
			"PORTAL_BUNDLES_DIST_URL");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBundlesDistURL)) {
			return null;
		}

		try {
			URL portalBundlesGitHashURL = new URL(
				JenkinsResultsParserUtil.getLocalURL(portalBundlesDistURL) +
					"/git-hash");

			if (!JenkinsResultsParserUtil.exists(portalBundlesGitHashURL)) {
				return null;
			}

			String portalBundlesGitHash = JenkinsResultsParserUtil.toString(
				portalBundlesGitHashURL.toString());

			portalBundlesGitHash = portalBundlesGitHash.trim();

			if (JenkinsResultsParserUtil.isSHA(portalBundlesGitHash)) {
				return portalBundlesGitHash;
			}

			return null;
		}
		catch (IOException ioException) {
			return null;
		}
	}

	private String _getPortalGitHubURL() {
		String gitHubURL = getParameterValue("PORTAL_GITHUB_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
			return gitHubURL;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild != null) {
			gitHubURL = controllerBuild.getParameterValue("PORTAL_GITHUB_URL");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(gitHubURL)) {
				return gitHubURL;
			}
		}

		String gitHubBranchName = getParameterValue(
			"PORTAL_GITHUB_BRANCH_NAME");
		String gitHubBranchUsername = getParameterValue(
			"PORTAL_GITHUB_BRANCH_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(gitHubBranchName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(gitHubBranchUsername)) {

			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(gitHubBranchUsername);
		sb.append("/liferay-portal");

		String branchName = getBranchName();

		if (!branchName.equals("master")) {
			sb.append("-ee");
		}

		sb.append("/tree/");
		sb.append(gitHubBranchName);

		return sb.toString();
	}

}