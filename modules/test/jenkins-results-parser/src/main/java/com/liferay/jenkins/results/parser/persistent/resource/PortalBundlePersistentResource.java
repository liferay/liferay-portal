/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalWorkspace;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;

import java.io.IOException;

/**
 * @author Michael Hashimoto
 */
public class PortalBundlePersistentResource
	extends BaseBundlePersistentResource {

	@Override
	public String getBaseS3ObjectPath() {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.persistent.resources.archives.path"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		PortalWorkspace portalWorkspace = _getPortalWorkspace();

		sb.append("/");
		sb.append(getS3ObjectPathName());
		sb.append("/");
		sb.append(portalWorkspace.getS3ObjectPathName());

		return sb.toString();
	}

	@Override
	public Type getType() {
		return Type.PORTAL_BUNDLE;
	}

	protected PortalBundlePersistentResource(
		BuildDatabase buildDatabase, TopLevelBuild topLevelBuild) {

		super(buildDatabase, topLevelBuild);
	}

	@Override
	protected WorkspaceGitRepository getBundleWorkspaceGitRepository() {
		PortalWorkspace portalWorkspace = _getPortalWorkspace();

		return portalWorkspace.getPortalWorkspaceGitRepository();
	}

	private PortalWorkspace _getPortalWorkspace() {
		Workspace workspace = getWorkspace();

		if (!(workspace instanceof PortalWorkspace)) {
			throw new RuntimeException("The portal workspace is not set");
		}

		return (PortalWorkspace)workspace;
	}

}