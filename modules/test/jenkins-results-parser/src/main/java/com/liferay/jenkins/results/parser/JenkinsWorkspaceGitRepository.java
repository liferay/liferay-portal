/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Properties;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JenkinsWorkspaceGitRepository extends BaseWorkspaceGitRepository {

	public Properties getCommandsBuildProperties() {
		if (_commandsBuildProperties != null) {
			return _commandsBuildProperties;
		}

		File buildPropertiesFile = new File(
			getDirectory(), "commands/build.properties");

		String buildPropertiesFilePath =
			JenkinsResultsParserUtil.getCanonicalPath(buildPropertiesFile);

		Properties commandsBuildProperties = null;

		try {
			commandsBuildProperties = new EnvironmentBuildProperties(
				buildPropertiesFile);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load " + buildPropertiesFilePath, ioException);
		}

		if (commandsBuildProperties.isEmpty()) {
			throw new RuntimeException(
				"Unable to find build properties for " +
					buildPropertiesFilePath);
		}

		_commandsBuildProperties = commandsBuildProperties;

		return _commandsBuildProperties;
	}

	protected JenkinsWorkspaceGitRepository(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected JenkinsWorkspaceGitRepository(
		PullRequest pullRequest, String upstreamBranchName) {

		super(pullRequest, upstreamBranchName);
	}

	protected JenkinsWorkspaceGitRepository(
		RemoteGitRef remoteGitRef, String upstreamBranchName) {

		super(remoteGitRef, upstreamBranchName);
	}

	private Properties _commandsBuildProperties;

}