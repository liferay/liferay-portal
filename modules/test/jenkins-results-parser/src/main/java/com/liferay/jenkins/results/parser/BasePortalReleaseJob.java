/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.job.property.JobProperty;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BasePortalReleaseJob
	extends BaseJob implements PortalTestClassJob, TestSuiteJob {

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

		jsonObject.put("test_suite_name", _testSuiteName);
		jsonObject.put("upstream_branch_name", _upstreamBranchName);

		return jsonObject;
	}

	@Override
	public List<String> getJobPropertyOptions() {
		List<String> jobPropertyOptions = super.getJobPropertyOptions();

		jobPropertyOptions.add(_upstreamBranchName);

		jobPropertyOptions.removeAll(Collections.singleton(null));

		return jobPropertyOptions;
	}

	@Override
	public PortalGitWorkingDirectory getPortalGitWorkingDirectory() {
		return _portalGitWorkingDirectory;
	}

	@Override
	public String getTestSuiteName() {
		return _testSuiteName;
	}

	protected BasePortalReleaseJob(
		BuildProfile buildProfile, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName, String upstreamBranchName) {

		super(buildProfile, jobName);

		_testSuiteName = testSuiteName;
		_upstreamBranchName = upstreamBranchName;

		_initialize(portalGitWorkingDirectory);
	}

	protected BasePortalReleaseJob(JSONObject jsonObject) {
		super(jsonObject);

		_testSuiteName = jsonObject.getString("test_suite_name");
		_upstreamBranchName = jsonObject.getString("upstream_branch_name");

		_initialize(null);
	}

	@Override
	protected Set<String> getRawBatchNames() {
		JobProperty jobProperty = getJobProperty("test.batch.names", false);

		recordJobProperty(jobProperty);

		return getSetFromString(jobProperty.getValue());
	}

	@Override
	protected Set<String> getRawDependentBatchNames() {
		JobProperty jobProperty = getJobProperty(
			"test.batch.names.smoke", false);

		recordJobProperty(jobProperty);

		return getSetFromString(jobProperty.getValue());
	}

	private void _initialize(
		PortalGitWorkingDirectory portalGitWorkingDirectory) {

		if (portalGitWorkingDirectory != null) {
			_portalGitWorkingDirectory = portalGitWorkingDirectory;
		}
		else {
			_portalGitWorkingDirectory =
				GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					_upstreamBranchName);
		}

		jobPropertiesFiles.add(
			new File(
				_portalGitWorkingDirectory.getWorkingDirectory(),
				"test.properties"));
	}

	private PortalGitWorkingDirectory _portalGitWorkingDirectory;
	private final String _testSuiteName;
	private final String _upstreamBranchName;

}