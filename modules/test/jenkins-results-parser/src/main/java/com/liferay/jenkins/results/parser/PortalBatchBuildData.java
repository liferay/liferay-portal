/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalBatchBuildData
	extends BaseBatchBuildData implements PortalBuildData {

	public static boolean isValidJSONObject(JSONObject jsonObject) {
		return isValidJSONObject(jsonObject, _TYPE);
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		if (!(topLevelBuildData instanceof PortalTopLevelBuildData)) {
			throw new RuntimeException("Invalid top level build data");
		}

		PortalTopLevelBuildData portalTopLevelBuildData =
			(PortalTopLevelBuildData)topLevelBuildData;

		return portalTopLevelBuildData.getBuildProfile();
	}

	@Override
	public String getPortalBranchSHA() {
		return optString("portal_branch_sha");
	}

	@Override
	public String getPortalGitHubBranchName() {
		return getGitHubBranchName(getPortalGitHubURL());
	}

	@Override
	public String getPortalGitHubRepositoryName() {
		return getGitHubRepositoryName(getPortalGitHubURL());
	}

	@Override
	public String getPortalGitHubURL() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		if (!(topLevelBuildData instanceof PortalTopLevelBuildData)) {
			throw new RuntimeException("Invalid top level build data");
		}

		PortalTopLevelBuildData portalTopLevelBuildData =
			(PortalTopLevelBuildData)topLevelBuildData;

		return portalTopLevelBuildData.getPortalGitHubURL();
	}

	@Override
	public String getPortalGitHubUsername() {
		return getGitHubUsername(getPortalGitHubURL());
	}

	@Override
	public String getPortalUpstreamBranchName() {
		TopLevelBuildData topLevelBuildData = getTopLevelBuildData();

		if (!(topLevelBuildData instanceof PortalTopLevelBuildData)) {
			throw new RuntimeException("Invalid top level build data");
		}

		PortalTopLevelBuildData portalTopLevelBuildData =
			(PortalTopLevelBuildData)topLevelBuildData;

		return portalTopLevelBuildData.getPortalUpstreamBranchName();
	}

	@Override
	public void put(String key, Object value) {
		super.put(key, value);
	}

	@Override
	public void setPortalBranchSHA(String portalBranchSHA) {
		put("portal_branch_sha", portalBranchSHA);
	}

	@Override
	public void setPortalGitHubURL(String portalGitHubURL) {
		put("portal_github_url", portalGitHubURL);
	}

	@Override
	public void setPortalUpstreamBranchName(String portalUpstreamBranchName) {
		put("portal_upstream_branch_name", portalUpstreamBranchName);
	}

	protected PortalBatchBuildData(
		String runId, String jobName, String buildURL) {

		super(runId, jobName, buildURL);
	}

	@Override
	protected String getType() {
		return _TYPE;
	}

	private static final String _TYPE = "portal_batch";

}