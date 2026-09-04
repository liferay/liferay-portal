/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;

import java.io.File;

import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class WorkspacesJSUnitModulesBatchTestClassGroup
	extends JSUnitModulesBatchTestClassGroup {

	protected WorkspacesJSUnitModulesBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected WorkspacesJSUnitModulesBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	@Override
	protected List<File> getBaseModuleDirs() {
		List<File> workspacesDirs = new ArrayList<>();

		for (File portalDir : _getPortalDirs()) {
			File workspacesDir = new File(portalDir, "workspaces");

			if (workspacesDir.exists()) {
				workspacesDirs.add(workspacesDir);
			}
		}

		return workspacesDirs;
	}

	@Override
	protected List<PathMatcher> getExcludesPathMatchers() {
		return _getWorkspacesPathMatchers(getExcludesJobProperties());
	}

	@Override
	protected List<PathMatcher> getIncludesPathMatchers() {
		if (isRootCauseAnalysis()) {
			return super.getIncludesPathMatchers();
		}

		return _getWorkspacesPathMatchers(getIncludesJobProperties());
	}

	@Override
	protected String getTestClassMethodName(File jsUnitFile) {
		String jsUnitFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			jsUnitFile);

		for (File portalDir : _getPortalDirs()) {
			String portalDirPath = JenkinsResultsParserUtil.getCanonicalPath(
				portalDir);

			if (jsUnitFilePath.startsWith(portalDirPath + "/")) {
				return jsUnitFilePath.substring(portalDirPath.length() + 1);
			}
		}

		return super.getTestClassMethodName(jsUnitFile);
	}

	@Override
	protected boolean isModulesProjectDir(File projectDir) {
		File packageJSONFile = new File(projectDir, "package.json");

		if (!packageJSONFile.exists()) {
			return false;
		}

		return JenkinsResultsParserUtil.isFileIncluded(
			getExcludesPathMatchers(), getIncludesPathMatchers(),
			projectDir.toPath());
	}

	@Override
	protected boolean isSkippedProjectDir(File projectDir) {
		String projectDirName = projectDir.getName();

		if (projectDirName.equals("build") || projectDirName.equals("dist") ||
			projectDirName.equals("node_modules")) {

			return true;
		}

		return false;
	}

	private List<File> _getPortalDirs() {
		List<File> portalDirs = new ArrayList<>();

		portalDirs.add(portalGitWorkingDirectory.getWorkingDirectory());

		File portalPrivateDir = portalGitWorkingDirectory.getPortalPrivateDir();

		if (portalPrivateDir != null) {
			portalDirs.add(portalPrivateDir);
		}

		return portalDirs;
	}

	private List<PathMatcher> _getWorkspacesPathMatchers(
		List<JobProperty> jobProperties) {

		List<PathMatcher> pathMatchers = new ArrayList<>();

		for (File portalDir : _getPortalDirs()) {
			for (JobProperty jobProperty : jobProperties) {
				if (jobProperty == null) {
					continue;
				}

				pathMatchers.addAll(
					getPathMatchers(jobProperty.getValue(), portalDir));
			}
		}

		return pathMatchers;
	}

}