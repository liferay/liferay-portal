/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Michael Hashimoto
 */
public class LegacyTopLevelBuild
	extends DefaultTopLevelBuild implements PortalWorkspaceBuild {

	public LegacyTopLevelBuild(String url) {
		super(url);
	}

	public LegacyTopLevelBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	@Override
	public String getBaseGitRepositoryName() {
		String branchName = getBranchName();

		if (!branchName.equals("master")) {
			return "liferay-portal-ee";
		}

		return "liferay-portal";
	}

	@Override
	public String getBranchName() {
		String portalUpstreamBranchName = getParameterValue(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			return portalUpstreamBranchName;
		}

		return "master";
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

		if (!(workspace instanceof PortalWorkspace)) {
			return workspace;
		}

		PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			portalWorkspace.getPortalWorkspaceGitRepository();

		String portalGitHubURL = _getPortalGitHubURL();

		if ((portalWorkspaceGitRepository != null) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(portalGitHubURL)) {

			portalWorkspaceGitRepository.setGitHubURL(portalGitHubURL);
		}

		WorkspaceGitRepository legacyWorkspaceGitRepository =
			portalWorkspace.getLiferayQAPortalLegacyWorkspaceGitRepository();

		String legacyGitHubURL = _getLegacyGitHubURL();

		if ((legacyWorkspaceGitRepository != null) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(legacyGitHubURL)) {

			legacyWorkspaceGitRepository.setGitHubURL(legacyGitHubURL);
		}

		return workspace;
	}

	private String _getLegacyGitHubURL() {
		String legacyGitHubBranchName = getParameterValue(
			"LEGACY_GITHUB_BRANCH_NAME");
		String legacyGitHubBranchUsername = getParameterValue(
			"LEGACY_GITHUB_BRANCH_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(legacyGitHubBranchName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(
				legacyGitHubBranchUsername)) {

			return null;
		}

		return JenkinsResultsParserUtil.combine(
			"https://github.com/", legacyGitHubBranchUsername,
			"/liferay-qa-portal-legacy-ee/tree/", legacyGitHubBranchName);
	}

	private String _getPortalGitHubURL() {
		String portalGitHubBranchName = getParameterValue(
			"PORTAL_GITHUB_BRANCH_NAME");
		String portalGitHubBranchUsername = getParameterValue(
			"PORTAL_GITHUB_BRANCH_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalGitHubBranchName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(
				portalGitHubBranchUsername)) {

			return null;
		}

		return JenkinsResultsParserUtil.combine(
			"https://github.com/", portalGitHubBranchUsername,
			"/liferay-portal/tree/", portalGitHubBranchName);
	}

}