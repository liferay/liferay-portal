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
public class PortalTopLevelBuild
	extends DefaultTopLevelBuild
	implements AnalyticsCloudBranchInformationBuild,
			   PluginsBranchInformationBuild, PortalBranchInformationBuild,
			   PortalFixpackReleaseBuild, PortalReleaseBuild {

	public PortalTopLevelBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	@Override
	public String getBaseGitRepositoryName() {
		String branchName = getBranchName();

		if (branchName.startsWith("faro-v") || branchName.equals("master")) {
			return "liferay-portal";
		}

		return "liferay-portal-ee";
	}

	@Override
	public BranchInformation getOSBAsahBranchInformation() {
		if (fromArchive || !(this instanceof PortalWorkspaceBuild)) {
			return getBranchInformation("osb.asah");
		}

		PortalWorkspaceBuild portalWorkspaceBuild = (PortalWorkspaceBuild)this;

		PortalWorkspace portalWorkspace =
			portalWorkspaceBuild.getPortalWorkspace();

		if (portalWorkspace == null) {
			return null;
		}

		WorkspaceGitRepository workspaceGitRepository =
			portalWorkspace.getOSBAsahWorkspaceGitRepository();

		if (workspaceGitRepository == null) {
			return null;
		}

		return new WorkspaceBranchInformation(workspaceGitRepository);
	}

	@Override
	public BranchInformation getPluginsBranchInformation() {
		if (fromArchive || !(this instanceof PortalWorkspaceBuild)) {
			return getBranchInformation("plugins");
		}

		PortalWorkspaceBuild portalWorkspaceBuild = (PortalWorkspaceBuild)this;

		PortalWorkspace portalWorkspace =
			portalWorkspaceBuild.getPortalWorkspace();

		if (portalWorkspace == null) {
			return null;
		}

		WorkspaceGitRepository workspaceGitRepository =
			portalWorkspace.getPluginsWorkspaceGitRepository();

		if (workspaceGitRepository == null) {
			return null;
		}

		return new WorkspaceBranchInformation(workspaceGitRepository);
	}

	@Override
	public BranchInformation getPortalBaseBranchInformation() {
		if (this instanceof PortalWorkspaceBuild) {
			return null;
		}

		BranchInformation portalBranchInformation =
			getPortalBranchInformation();

		if (portalBranchInformation == null) {
			return null;
		}

		String upstreamBranchName =
			portalBranchInformation.getUpstreamBranchName();

		if (upstreamBranchName.contains("-private")) {
			return getBranchInformation("portal.base");
		}

		return null;
	}

	@Override
	public BranchInformation getPortalBranchInformation() {
		if (fromArchive || !(this instanceof PortalWorkspaceBuild)) {
			return getBranchInformation("portal");
		}

		PortalWorkspaceBuild portalWorkspaceBuild = (PortalWorkspaceBuild)this;

		PortalWorkspace portalWorkspace =
			portalWorkspaceBuild.getPortalWorkspace();

		if (portalWorkspace == null) {
			return null;
		}

		WorkspaceGitRepository workspaceGitRepository =
			portalWorkspace.getPortalWorkspaceGitRepository();

		if (workspaceGitRepository == null) {
			return null;
		}

		return new WorkspaceBranchInformation(workspaceGitRepository);
	}

	@Override
	public PortalFixpackRelease getPortalFixpackRelease() {
		if (portalFixpackRelease != null) {
			return portalFixpackRelease;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild == null) {
			return null;
		}

		String portalFixPackVersion = controllerBuild.getParameterValue(
			"PORTAL_FIX_PACK_VERSION");

		if (portalFixPackVersion != null) {
			portalFixpackRelease = PortalReleaseFactory.newPortalFixpackRelease(
				portalFixPackVersion, getPortalRelease());

			return portalFixpackRelease;
		}

		String portalFixPackZipURL = controllerBuild.getParameterValue(
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

		return portalFixpackRelease;
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (portalRelease != null) {
			return portalRelease;
		}

		Build controllerBuild = getControllerBuild();

		if (controllerBuild == null) {
			return null;
		}

		String portalBundleVersion = controllerBuild.getParameterValue(
			"PORTAL_BUNDLE_VERSION");

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

		return portalRelease;
	}

	protected PortalFixpackRelease portalFixpackRelease;
	protected PortalRelease portalRelease;

}