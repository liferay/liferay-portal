/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public class PortalReleasePortalTopLevelBuild
	extends PortalTopLevelBuild implements PortalWorkspaceBuild {

	public PortalReleasePortalTopLevelBuild(
		String url, TopLevelBuild topLevelBuild) {

		super(url, topLevelBuild);
	}

	@Override
	public String getBaseGitRepositoryName() {
		String branchName = getBranchName();

		if (branchName.equals("master")) {
			return "liferay-portal";
		}

		return "liferay-portal-ee";
	}

	@Override
	public String getBranchName() {
		String portalBranchName = getParameterValue("TEST_PORTAL_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalBranchName)) {
			return portalBranchName;
		}

		PortalRelease portalRelease = getPortalRelease();

		String portalVersion = portalRelease.getPortalVersion();

		Matcher matcher = _pattern.matcher(portalVersion);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid portal version: " + portalVersion);
		}

		return JenkinsResultsParserUtil.combine(
			matcher.group("major"), ".", matcher.group("minor"), ".x");
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (_portalRelease != null) {
			return _portalRelease;
		}

		String tomcatURLString = getParameterValue(
			"TEST_PORTAL_RELEASE_TOMCAT_URL");

		try {
			if (JenkinsResultsParserUtil.isNullOrEmpty(tomcatURLString)) {
				try {
					_portalRelease = new PortalRelease(
						new URL(getUserContentURL() + "/bundles"),
						JenkinsResultsParserUtil.getProperty(
							JenkinsResultsParserUtil.getBuildProperties(),
							"portal.latest.bundle.version",
							getParameterValue("TEST_PORTAL_BRANCH_NAME")));
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
			else {
				URL portalReleaseTomcatURL = new URL(tomcatURLString);

				_portalRelease = PortalReleaseFactory.newPortalRelease(
					portalReleaseTomcatURL);

				_portalRelease.setPortalBundleTomcatURL(portalReleaseTomcatURL);
			}

			String portalDependenciesZipURLString = getParameterValue(
				"TEST_PORTAL_RELEASE_DEPENDENCIES_URL");

			if (JenkinsResultsParserUtil.isURL(
					portalDependenciesZipURLString)) {

				_portalRelease.setPortalDependenciesZipURL(
					new URL(portalDependenciesZipURLString));
			}

			String portalOSGiZipURLString = getParameterValue(
				"TEST_PORTAL_RELEASE_OSGI_URL");

			if (JenkinsResultsParserUtil.isURL(portalOSGiZipURLString)) {
				_portalRelease.setPortalOSGiZipURL(
					new URL(portalOSGiZipURLString));
			}

			String portalSQLZipURLString = getParameterValue(
				"TEST_PORTAL_RELEASE_SQL_URL");

			if (JenkinsResultsParserUtil.isURL(portalSQLZipURLString)) {
				_portalRelease.setPortalSQLZipURL(
					new URL(portalSQLZipURLString));
			}

			String portalToolsZipURLString = getParameterValue(
				"TEST_PORTAL_RELEASE_TOOLS_URL");

			if (JenkinsResultsParserUtil.isURL(portalToolsZipURLString)) {
				_portalRelease.setPortalToolsZipURL(
					new URL(portalToolsZipURLString));
			}

			String portalWarURLString = getParameterValue(
				"TEST_PORTAL_RELEASE_WAR_URL");

			if (JenkinsResultsParserUtil.isURL(portalWarURLString)) {
				_portalRelease.setPortalWarURL(new URL(portalWarURLString));
			}
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

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

	@Override
	protected String getReleaseRepositoryName() {
		String portalRepositoryName = getParameterValue(
			"TEST_PORTAL_REPOSITORY_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalRepositoryName)) {
			return portalRepositoryName;
		}

		String portalReleaseVersion = getParameterValue(
			"TEST_PORTAL_RELEASE_VERSION");

		if (PortalRelease.isQuarterlyRelease(portalReleaseVersion) ||
			!Objects.equals(getBranchName(), "master")) {

			return "liferay-portal-ee";
		}

		return "liferay-portal";
	}

	@Override
	protected boolean isReleaseBuild() {
		return true;
	}

	private String _getPortalGitCommit() {
		return getParameterValue("TEST_PORTAL_RELEASE_GIT_ID");
	}

	private String _getPortalGitHubURL() {
		String portalBranchName = getParameterValue(
			"TEST_PORTAL_USER_BRANCH_NAME");
		String portalBranchUsername = getParameterValue(
			"TEST_PORTAL_USER_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBranchName) ||
			JenkinsResultsParserUtil.isNullOrEmpty(portalBranchUsername)) {

			portalBranchName = getBranchName();
			portalBranchUsername = "liferay";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(portalBranchUsername);
		sb.append("/");
		sb.append(getReleaseRepositoryName());
		sb.append("/tree/");
		sb.append(portalBranchName);

		return sb.toString();
	}

	private static final Pattern _pattern = Pattern.compile(
		"(?<major>\\d)\\.(?<minor>\\d)\\.(?<fix>\\d+)");

	private PortalRelease _portalRelease;

}