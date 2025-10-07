/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.nio.file.Path;

import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class ModulesTestClass extends BaseTestClass {

	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (!isBuildCachingEnabled()) {
			return null;
		}

		if (_cachedTestClassReportSearched) {
			getCachedTestClassReport();
		}

		return _cachedDownstreamBuildReport;
	}

	public TestClassReport getCachedTestClassReport() {
		if (!isBuildCachingEnabled() || _cachedTestClassReportSearched) {
			return _cachedTestClassReport;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_cachedTestClassReport = batchTestClassGroup.getCachedTestClassReport(
			getTestClassName());

		if (_cachedTestClassReport != null) {
			_cachedDownstreamBuildReport =
				_cachedTestClassReport.getDownstreamBuildReport();
		}

		_cachedTestClassReportSearched = true;

		return _cachedTestClassReport;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		jsonObject.put("task_name", getTaskName());

		File testPropertiesFile = getTestPropertiesFile();

		if ((testPropertiesFile != null) && testPropertiesFile.exists()) {
			jsonObject.put(
				"test_properties_file", String.valueOf(testPropertiesFile));
		}

		String testrayMainComponentName = getTestrayMainComponentName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayMainComponentName)) {
			jsonObject.put(
				"testray_main_component_name", testrayMainComponentName);
		}

		return jsonObject;
	}

	public String getModulePath() {
		String modulePath = getName();

		if (modulePath.startsWith("modules")) {
			modulePath = modulePath.substring(7);
		}

		return modulePath;
	}

	public String getTaskName() {
		return _taskName;
	}

	@Override
	public String getTestClassName() {
		String modulePath = getModulePath();

		return "modules" + modulePath.replaceAll("/", ".");
	}

	public String getTestrayMainComponentName() {
		return _testrayMainComponentName;
	}

	@Override
	public String getTestTaskName() {
		String modulePath = getModulePath();

		return modulePath.replaceAll("/", ":") + ":" + getTaskName();
	}

	protected ModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, File moduleBaseDir,
		String taskName) {

		super(batchTestClassGroup, moduleBaseDir);

		_taskName = taskName;

		if (this instanceof JSUnitModulesTestClass) {
			_testPropertiesFile = null;
			_testrayMainComponentName = null;

			return;
		}

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		if ((testPropertiesBaseDir != null) && testPropertiesBaseDir.exists()) {
			_testPropertiesFile = new File(
				testPropertiesBaseDir, "test.properties");

			_testrayMainComponentName = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getProperties(_testPropertiesFile),
				"testray.main.component.name");
		}
		else {
			_testPropertiesFile = null;
			_testrayMainComponentName = null;
		}

		for (File modulesProjectDir : getModulesProjectDirs()) {
			String path = JenkinsResultsParserUtil.getPathRelativeTo(
				modulesProjectDir, getPortalModulesBaseDir());

			String moduleTaskCall = JenkinsResultsParserUtil.combine(
				":", path.replaceAll("/", ":"), ":", getTaskName());

			addTestClassMethod(moduleTaskCall);
		}
	}

	protected ModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_taskName = jsonObject.getString("task_name");

		if (jsonObject.has("test_properties_file")) {
			_testPropertiesFile = new File(
				jsonObject.getString("test_properties_file"));
		}
		else {
			_testPropertiesFile = null;
		}

		_testrayMainComponentName = jsonObject.optString(
			"testray_main_component_name");
	}

	protected File getModuleBaseDir() {
		return getTestClassFile();
	}

	protected Path getModuleBaseDirPath() {
		File testClassFile = getTestClassFile();

		return testClassFile.toPath();
	}

	protected abstract List<File> getModulesProjectDirs();

	protected File getPortalModulesBaseDir() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		return new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");
	}

	protected File getTestPropertiesFile() {
		return _testPropertiesFile;
	}

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private TestClassReport _cachedTestClassReport;
	private boolean _cachedTestClassReportSearched;
	private final String _taskName;
	private final File _testPropertiesFile;
	private final String _testrayMainComponentName;

}