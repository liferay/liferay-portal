/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalAppReleaseJob extends BaseJob implements PortalTestClassJob {

	@Override
	public Set<String> getAppServerTypes() {
		return Collections.emptySet();
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put(
			"portal_upstream_branch_name", _portalUpstreamBranchName);

		return jsonObject;
	}

	@Override
	public List<String> getJobPropertyOptions() {
		List<String> jobPropertyOptions = super.getJobPropertyOptions();

		jobPropertyOptions.add(
			_portalGitWorkingDirectory.getUpstreamBranchName());

		return jobPropertyOptions;
	}

	@Override
	public PortalGitWorkingDirectory getPortalGitWorkingDirectory() {
		return _portalGitWorkingDirectory;
	}

	@Override
	public boolean isSegmentEnabled() {
		return true;
	}

	protected PortalAppReleaseJob(
		BuildProfile buildProfile, String jobName,
		String portalUpstreamBranchName) {

		super(buildProfile, jobName);

		_portalUpstreamBranchName = portalUpstreamBranchName;

		_initialize();
	}

	protected PortalAppReleaseJob(JSONObject jsonObject) {
		super(jsonObject);

		_portalUpstreamBranchName = jsonObject.getString(
			"portal_upstream_branch_name");

		_initialize();
	}

	private void _initialize() {
		GitWorkingDirectory jenkinsGitWorkingDirectory =
			GitWorkingDirectoryFactory.newJenkinsGitWorkingDirectory();

		jobPropertiesFiles.add(
			new File(
				jenkinsGitWorkingDirectory.getWorkingDirectory(),
				"commands/build.properties"));

		_portalGitWorkingDirectory =
			GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
				_portalUpstreamBranchName);

		jobPropertiesFiles.add(
			new File(
				_portalGitWorkingDirectory.getWorkingDirectory(),
				"test.properties"));
	}

	private PortalGitWorkingDirectory _portalGitWorkingDirectory;
	private final String _portalUpstreamBranchName;

}