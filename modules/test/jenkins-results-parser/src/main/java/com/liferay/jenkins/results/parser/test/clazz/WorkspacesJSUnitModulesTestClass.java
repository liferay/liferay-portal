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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesJSUnitModulesTestClass extends JSUnitModulesTestClass {

	@Override
	public String getTestClassName() {
		return getName();
	}

	@Override
	public String getTestTaskName() {
		Matcher matcher = _pattern.matcher(
			_getPortalRelativePath(getTestClassFile()));

		if (!matcher.matches()) {
			return super.getTestTaskName();
		}

		String projectPath = matcher.group("projectPath");

		return JenkinsResultsParserUtil.combine(
			matcher.group("workspaceDir"), projectPath.replaceAll("/", ":"),
			":", getTaskName());
	}

	protected WorkspacesJSUnitModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile);
	}

	protected WorkspacesJSUnitModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);
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

	private static final Pattern _pattern = Pattern.compile(
		"(?<workspaceDir>.*workspaces/[^/]+)(?<projectPath>/.+)");

}