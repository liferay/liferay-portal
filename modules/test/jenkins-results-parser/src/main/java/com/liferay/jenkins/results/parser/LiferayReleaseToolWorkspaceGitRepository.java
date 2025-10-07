/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class LiferayReleaseToolWorkspaceGitRepository
	extends BaseWorkspaceGitRepository {

	public String getPortalUpstreamBranchName() {
		return optString("portal_upstream_branch_name", "master");
	}

	public void setPortalUpstreamBranchName(String portalUpstreamBranchName) {
		put("portal_upstream_branch_name", portalUpstreamBranchName);
	}

	@Override
	public void writePropertiesFiles() {
		_writeBuildPropertiesFile();
	}

	protected LiferayReleaseToolWorkspaceGitRepository(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected LiferayReleaseToolWorkspaceGitRepository(
		PullRequest pullRequest, String upstreamBranchName) {

		super(pullRequest, upstreamBranchName);
	}

	protected LiferayReleaseToolWorkspaceGitRepository(
		RemoteGitRef remoteGitRef, String upstreamBranchName) {

		super(remoteGitRef, upstreamBranchName);
	}

	@Override
	protected Set<String> getPropertyOptions() {
		Set<String> propertyOptions = new HashSet<>(super.getPropertyOptions());

		propertyOptions.add(getPortalUpstreamBranchName());

		return propertyOptions;
	}

	private void _writeBuildPropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"build.", System.getenv("HOSTNAME"), ".properties")),
			getProperties("release.tool.build.properties"), true);
	}

}