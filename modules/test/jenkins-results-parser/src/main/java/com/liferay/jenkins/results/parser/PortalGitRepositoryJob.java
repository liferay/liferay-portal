/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

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

		_initialize(null);
	}

	private void _initialize(
		PortalGitWorkingDirectory portalGitWorkingDirectory) {

		if (portalGitWorkingDirectory != null) {
			gitWorkingDirectory = portalGitWorkingDirectory;
		}
		else {
			gitWorkingDirectory =
				GitWorkingDirectoryFactory.newPortalGitWorkingDirectory(
					getUpstreamBranchName());
		}

		setGitRepositoryDir(gitWorkingDirectory.getWorkingDirectory());

		checkGitRepositoryDir();

		jobPropertiesFiles.add(
			new File(gitRepositoryDir, "tools/sdk/build.properties"));
		jobPropertiesFiles.add(new File(gitRepositoryDir, "build.properties"));
		jobPropertiesFiles.add(new File(gitRepositoryDir, "test.properties"));
		jobPropertiesFiles.add(
			new File(
				gitRepositoryDir,
				JenkinsResultsParserUtil.combine(
					"test.", Environment.get("HOSTNAME"), ".properties")));
	}

}