/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitModulesTestClass extends ModulesTestClass {

	@Override
	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (!_cachedTestClassReportSearched) {
			getCachedTestClassReport();
		}

		return _cachedDownstreamBuildReport;
	}

	@Override
	public TestClassReport getCachedTestClassReport() {
		if (!isBuildCachingEnabled() || _cachedTestClassReportSearched) {
			return _cachedTestClassReport;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_cachedTestClassReport = batchTestClassGroup.getCachedTestClassReport(
			getName());

		if (_cachedTestClassReport != null) {
			_cachedDownstreamBuildReport =
				_cachedTestClassReport.getDownstreamBuildReport();
		}

		_cachedTestClassReportSearched = true;

		return _cachedTestClassReport;
	}

	@Override
	public String getName() {
		return getTestTaskName();
	}

	@Override
	public String getTestrayMainComponentName() {
		return _testrayMainComponentName;
	}

	@Override
	public String getTestTaskName() {
		String testClassFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			getTestClassFile());

		String testTaskName = testClassFilePath.replaceAll(
			".*/modules(/.+)", "$1");

		return testTaskName.replaceAll("/", ":") + ":" + getTaskName();
	}

	protected JSUnitModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile, "packageRunTest");

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		if ((testPropertiesBaseDir != null) && testPropertiesBaseDir.exists()) {
			_testPropertiesFile = new File(
				testPropertiesBaseDir, "test.properties");

			String testrayMainComponentName =
				JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(_testPropertiesFile),
					"testray.main.component.name");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					testrayMainComponentName)) {

				_testrayMainComponentName = testrayMainComponentName;

				return;
			}

			File appBaseDir = _getAppBaseDir();

			File appTestPropertiesFile = new File(
				appBaseDir, "test.properties");

			_testrayMainComponentName = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getProperties(appTestPropertiesFile),
				"testray.main.component.name");
		}
		else {
			_testPropertiesFile = null;
			_testrayMainComponentName = null;
		}
	}

	protected JSUnitModulesTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		if (jsonObject.has("test_properties_file")) {
			_testPropertiesFile = new File(
				jsonObject.getString("test_properties_file"));
		}
		else {
			_testPropertiesFile = null;
		}

		if (jsonObject.has("testray_main_component_name")) {
			_testrayMainComponentName = jsonObject.getString(
				"testray_main_component_name");
		}
		else {
			_testrayMainComponentName = null;
		}
	}

	@Override
	protected List<File> getModulesProjectDirs() {
		return Collections.singletonList(getModuleBaseDir());
	}

	protected File getTestPropertiesFile() {
		return _testPropertiesFile;
	}

	private File _getAppBaseDir() {
		String filePath = _testPropertiesFile.toString();

		return new File(filePath.replaceAll("(.*/apps/[^/]+)/.+", "$1"));
	}

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private TestClassReport _cachedTestClassReport;
	private boolean _cachedTestClassReportSearched;
	private final File _testPropertiesFile;
	private final String _testrayMainComponentName;

}