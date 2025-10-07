/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.List;

import org.json.JSONObject;

/**
 * @author Yi-Chen Tsai
 */
public class ServiceBuilderModulesBatchTestClassGroup
	extends ModulesBatchTestClassGroup {

	@Override
	public int getAxisCount() {
		if (ignore()) {
			return 0;
		}

		if (!containsTestClasses() && (_buildType == BuildType.CORE)) {
			return 1;
		}

		return super.getAxisCount();
	}

	public BuildType getBuildType() {
		return _buildType;
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("build_type", _buildType);

		return jsonObject;
	}

	public static enum BuildType {

		CORE, FULL

	}

	protected ServiceBuilderModulesBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);

		_buildType = BuildType.valueOf(
			jsonObject.optString("build_type", "FULL"));
	}

	protected ServiceBuilderModulesBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	@Override
	protected void setAxisTestClassGroups() {
		int axisCount = getAxisCount();

		if (!containsTestClasses() && (axisCount == 1)) {
			axisTestClassGroups.add(
				0, TestClassGroupFactory.newAxisTestClassGroup(this));

			return;
		}

		super.setAxisTestClassGroups();
	}

	@Override
	protected void setTestClasses() throws IOException {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		File portalModulesBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		List<PathMatcher> excludesPathMatchers = getPathMatchers(
			getExcludesJobProperties());
		List<PathMatcher> includesPathMatchers = getIncludesPathMatchers();

		if (testRelevantChanges &&
			!(includeStableTestSuite && isStableTestSuiteBatch())) {

			List<File> modifiedFiles =
				portalGitWorkingDirectory.getModifiedFilesList();

			List<File> modifiedPortalToolsServiceBuilderFiles =
				JenkinsResultsParserUtil.getIncludedFiles(
					null,
					getPathMatchers(
						"util/portal-tools-service-builder/**",
						portalModulesBaseDir),
					modifiedFiles);

			if (!modifiedPortalToolsServiceBuilderFiles.isEmpty()) {
				_buildType = BuildType.FULL;

				return;
			}

			List<File> modifiedPortalImplFiles =
				JenkinsResultsParserUtil.getIncludedFiles(
					null,
					getPathMatchers(
						"portal-impl/**",
						portalGitWorkingDirectory.getWorkingDirectory()),
					modifiedFiles);

			if (!modifiedPortalImplFiles.isEmpty()) {
				_buildType = BuildType.CORE;
			}
			else {
				List<File> modifiedPortalKernelFiles =
					JenkinsResultsParserUtil.getIncludedFiles(
						null,
						getPathMatchers(
							"portal-kernel/**",
							portalGitWorkingDirectory.getWorkingDirectory()),
						modifiedFiles);

				if (!modifiedPortalKernelFiles.isEmpty()) {
					_buildType = BuildType.CORE;
				}
			}

			moduleDirsList.addAll(
				portalGitWorkingDirectory.getModifiedModuleDirsList(
					excludesPathMatchers, includesPathMatchers));
		}
		else {
			_buildType = BuildType.FULL;

			moduleDirsList.addAll(
				portalGitWorkingDirectory.getModuleDirsList(
					excludesPathMatchers, includesPathMatchers));
		}

		File portalImplBuildFile = new File(
			portalGitWorkingDirectory.getWorkingDirectory(),
			"portal-impl/build.xml");

		addTestClass(TestClassFactory.newTestClass(this, portalImplBuildFile));

		addTestClasses(moduleDirsList);
	}

	private BuildType _buildType;

}