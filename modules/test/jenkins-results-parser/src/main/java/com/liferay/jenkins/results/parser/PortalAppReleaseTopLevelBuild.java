/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Michael Hashimoto
 */
public class PortalAppReleaseTopLevelBuild
	extends PortalTopLevelBuild implements PortalWorkspaceBuild {

	public PortalAppReleaseTopLevelBuild(
		String url, TopLevelBuild topLevelBuild) {

		super(url, topLevelBuild);
	}

	@Override
	public String getBranchName() {
		return getParameterValue("TEST_PORTAL_BRANCH_NAME");
	}

	public String getPortalAppName() {
		return getParameterValue("TEST_PORTAL_APP_NAME");
	}

	@Override
	public PortalFixpackRelease getPortalFixpackRelease() {
		if (portalFixpackRelease != null) {
			return portalFixpackRelease;
		}

		String portalFixPackZipURL = getParameterValue(
			"TEST_PORTAL_FIX_PACK_ZIP_URL");

		if (portalFixPackZipURL != null) {
			try {
				portalFixpackRelease =
					PortalReleaseFactory.newPortalFixpackRelease(
						new URL(portalFixPackZipURL));

				return portalFixpackRelease;
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		return null;
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (portalRelease != null) {
			return portalRelease;
		}

		String portalBundleVersion = getParameterValue(
			"TEST_PORTAL_BUNDLE_VERSION");

		if (portalBundleVersion != null) {
			portalRelease = PortalReleaseFactory.newPortalRelease(
				portalBundleVersion);

			return portalRelease;
		}

		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		if (portalFixpackRelease != null) {
			portalRelease = portalFixpackRelease.getPortalRelease();

			return portalRelease;
		}

		return null;
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
		}

		String portalGitHubURL = _getPortalGitHubURL();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalGitHubURL)) {
			WorkspaceGitRepository workspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			workspaceGitRepository.setGitHubURL(portalGitHubURL);
		}

		return workspace;
	}

	@Override
	protected boolean isReleaseBuild() {
		return true;
	}

	private String _getPortalGitHubURL() {
		String portalBranchName = getParameterValue("TEST_PORTAL_RELEASE_TAG");
		String portalBranchUsername = getParameterValue(
			"TEST_PORTAL_RELEASE_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBranchName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(portalBranchUsername)) {

			return null;
		}

		String branchName = getBranchName();

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(portalBranchUsername);
		sb.append("/liferay-portal");

		if (!branchName.equals("master")) {
			sb.append("-ee");
		}

		sb.append("/tree/");
		sb.append(portalBranchName);

		return sb.toString();
	}

}