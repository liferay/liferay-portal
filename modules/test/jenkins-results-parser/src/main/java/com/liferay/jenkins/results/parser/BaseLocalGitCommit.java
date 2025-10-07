/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseLocalGitCommit
	extends BaseGitCommit implements LocalGitCommit {

	@Override
	public GitWorkingDirectory getGitWorkingDirectory() {
		return _gitWorkingDirectory;
	}

	@Override
	public boolean isFileChanged(File file) {
		GitUtil.ExecutionResult executionResult =
			_gitWorkingDirectory.executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT, "git show " + getSHA());

		if (executionResult.getExitValue() != 0) {
			return false;
		}

		String standardOut = executionResult.getStandardOut();

		if (JenkinsResultsParserUtil.isNullOrEmpty(standardOut)) {
			return false;
		}

		String filePath = JenkinsResultsParserUtil.getPathRelativeTo(
			file, _gitWorkingDirectory.getWorkingDirectory());

		for (String line : standardOut.split("\n")) {
			if (line.contains(filePath)) {
				return true;
			}
		}

		return false;
	}

	protected BaseLocalGitCommit(
		String emailAddress, GitWorkingDirectory gitWorkingDirectory,
		String message, String sha, GitCommit.Type type, long commitTime) {

		super(
			emailAddress, gitWorkingDirectory.getGitRepositoryName(), message,
			sha, type, commitTime);

		_gitWorkingDirectory = gitWorkingDirectory;
	}

	@Override
	protected void initCommitTime() {
	}

	@Override
	protected void initEmailAddress() {
	}

	@Override
	protected void initMessage() {
	}

	private final GitWorkingDirectory _gitWorkingDirectory;

}