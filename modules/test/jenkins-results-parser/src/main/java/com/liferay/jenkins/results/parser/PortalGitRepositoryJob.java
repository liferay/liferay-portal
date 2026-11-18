/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Objects;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class PortalGitRepositoryJob
	extends GitRepositoryJob implements PortalTestClassJob {

	@Override
	public PortalGitWorkingDirectory getPortalGitWorkingDirectory() {
		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		if (!(gitWorkingDirectory instanceof PortalGitWorkingDirectory)) {
			throw new RuntimeException("Invalid portal Git working directory");
		}

		return (PortalGitWorkingDirectory)gitWorkingDirectory;
	}

	protected PortalGitRepositoryJob(
		BuildProfile buildProfile, String jobName) {

		this(buildProfile, jobName, null, null);
	}

	protected PortalGitRepositoryJob(
		BuildProfile buildProfile, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String upstreamBranchName) {

		super(buildProfile, jobName, upstreamBranchName);

		_initialize(portalGitWorkingDirectory);
	}

	protected PortalGitRepositoryJob(JSONObject jsonObject) {
		super(jsonObject);

		_initialize(
			GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
				_getPortalUpstreamBranchName(jsonObject)));
	}

	private String _getPortalUpstreamBranchName(JSONObject jsonObject) {
		JSONObject branchJSONObject = jsonObject.optJSONObject("branch");

		if (branchJSONObject != null) {
			String portalUpstreamBranchName = branchJSONObject.optString(
				"upstream_branch_name");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalUpstreamBranchName)) {

				return portalUpstreamBranchName;
			}
		}

		return getUpstreamBranchName();
	}

	private PortalGitWorkingDirectory _getPrivatePortalGitWorkingDirectory(
		PortalGitWorkingDirectory portalGitWorkingDirectory) {

		if (!JenkinsResultsParserUtil.isCINode() ||
			!Objects.equals(
				portalGitWorkingDirectory.getUpstreamBranchName(), "master")) {

			return null;
		}

		File privatePortalDir = new File(
			JenkinsResultsParserUtil.getBaseGitRepositoryDir(),
			"liferay-portal-master-private");

		if (!privatePortalDir.exists()) {
			return null;
		}

		return GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
			"master-private");
	}

	private void _initialize(
		PortalGitWorkingDirectory portalGitWorkingDirectory) {

		String upstreamBranchName = getUpstreamBranchName();

		if ((portalGitWorkingDirectory == null) ||
			(!upstreamBranchName.endsWith("-private") &&
			 portalGitWorkingDirectory.isPrivate())) {

			portalGitWorkingDirectory =
				GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					upstreamBranchName);
		}

		setGitRepositoryDir(portalGitWorkingDirectory.getWorkingDirectory());

		gitWorkingDirectory = portalGitWorkingDirectory;

		PortalGitWorkingDirectory privatePortalGitWorkingDirectory =
			_getPrivatePortalGitWorkingDirectory(portalGitWorkingDirectory);

		if (privatePortalGitWorkingDirectory != null) {
			privatePortalGitWorkingDirectory.setPublicPortalGitWorkingDirectory(
				portalGitWorkingDirectory);

			gitWorkingDirectory = privatePortalGitWorkingDirectory;
		}

		checkGitRepositoryDir();

		jobPropertiesFiles.add(
			new File(gitRepositoryDir, "tools/sdk/build.properties"));

		jobPropertiesFiles.add(new File(gitRepositoryDir, "build.properties"));

		jobPropertiesFiles.add(
			new File(
				gitRepositoryDir,
				JenkinsResultsParserUtil.combine(
					"build.", Environment.get("HOSTNAME"), ".properties")));

		if (upstreamBranchName.endsWith("-private")) {
			File privatePortalDir = new File(
				JenkinsResultsParserUtil.getBaseGitRepositoryDir(),
				"liferay-portal-" + upstreamBranchName);

			jobPropertiesFiles.add(
				new File(
					privatePortalDir,
					JenkinsResultsParserUtil.combine(
						"test.", Environment.get("HOSTNAME"), ".properties")));

			jobPropertiesFiles.add(
				new File(privatePortalDir, "test-private.properties"));
		}

		jobPropertiesFiles.add(
			new File(
				gitRepositoryDir,
				JenkinsResultsParserUtil.combine(
					"test.", Environment.get("HOSTNAME"), ".properties")));

		jobPropertiesFiles.add(new File(gitRepositoryDir, "test.properties"));
	}

}