/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class PortalFixpackReleasePortalTopLevelBuild
	extends PortalTopLevelBuild implements PortalWorkspaceBuild {

	public PortalFixpackReleasePortalTopLevelBuild(
		String url, TopLevelBuild topLevelBuild) {

		super(url, topLevelBuild);
	}

	@Override
	public String getBaseGitRepositoryName() {
		return "liferay-portal-ee";
	}

	@Override
	public String getBranchName() {
		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		PortalRelease portalRelease = portalFixpackRelease.getPortalRelease();

		String portalVersion = portalRelease.getPortalVersion();

		Matcher matcher = _pattern.matcher(portalVersion);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid portal version: " + portalVersion);
		}

		String branchName = JenkinsResultsParserUtil.combine(
			matcher.group("major"), ".", matcher.group("minor"), ".x");

		if (branchName.equals("6.1.x")) {
			return "ee-6.1.30";
		}
		else if (branchName.equals("6.2.x")) {
			return "ee-6.2.10";
		}

		return branchName;
	}

	@Override
	public PortalFixpackRelease getPortalFixpackRelease() {
		if (_portalFixpackRelease != null) {
			return _portalFixpackRelease;
		}

		try {
			URL portalFixpackURL = new URL(
				getParameterValue("TEST_BUILD_FIX_PACK_ZIP_URL"));

			_portalFixpackRelease =
				PortalReleaseFactory.newPortalFixpackRelease(portalFixpackURL);
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

		return _portalFixpackRelease;
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (_portalRelease != null) {
			return _portalRelease;
		}

		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		_portalRelease = portalFixpackRelease.getPortalRelease();

		return _portalRelease;
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
	protected String getReleaseRepositoryName() {
		return "liferay-portal-ee";
	}

	@Override
	protected boolean isReleaseBuild() {
		return true;
	}

	private String _getPortalGitHubURL() {
		String portalBranchName = getParameterValue(
			"TEST_PORTAL_USER_BRANCH_NAME");
		String portalBranchUsername = getParameterValue(
			"TEST_PORTAL_USER_NAME");

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

	private static final Pattern _pattern = Pattern.compile(
		"(?<major>\\d)\\.(?<minor>\\d)\\.(?<fix>\\d+)");

	private PortalFixpackRelease _portalFixpackRelease;
	private PortalRelease _portalRelease;

}