/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesModulesJUnitTestClass extends ModulesJUnitTestClass {

	@Override
	public String getName() {
		return _getPortalRelativePath(getTestClassFile());
	}

	@Override
	public String getTestTaskName() {
		String taskName = getTaskName();

		if (JenkinsResultsParserUtil.isNullOrEmpty(taskName)) {
			return super.getTestTaskName();
		}

		Pattern pattern = Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"(?<workspaceDir>.*workspaces/[^/]+)(?<projectPath>/.+)/src/",
				taskName, "/.+"));

		Matcher matcher = pattern.matcher(
			_getPortalRelativePath(getTestClassFile()));

		if (!matcher.matches()) {
			return super.getTestTaskName();
		}

		String projectPath = matcher.group("projectPath");

		return JenkinsResultsParserUtil.combine(
			matcher.group("workspaceDir"), projectPath.replaceAll("/", ":"),
			":", taskName);
	}

	protected WorkspacesModulesJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile);
	}

	protected WorkspacesModulesJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile,
		List<String> testClassMethodNames) {

		super(batchTestClassGroup, testClassFile, testClassMethodNames);
	}

	protected WorkspacesModulesJUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);
	}

	@Override
	protected String getTaskName() {
		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		String batchName = batchTestClassGroup.getBatchName();

		if (batchName.startsWith("workspaces-unit")) {
			return "test";
		}

		return super.getTaskName();
	}

	private String _getPortalRelativePath(File file) {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		Path path = Paths.get(
			JenkinsResultsParserUtil.getCanonicalPath(
				portalGitWorkingDirectory.getWorkingDirectory()));

		return String.valueOf(
			path.relativize(
				Paths.get(JenkinsResultsParserUtil.getCanonicalPath(file))));
	}

}