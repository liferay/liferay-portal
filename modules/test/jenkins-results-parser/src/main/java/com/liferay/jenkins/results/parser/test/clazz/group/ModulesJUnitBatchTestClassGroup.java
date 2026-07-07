/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.google.common.collect.Lists;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.batch.JUnitTestBatch;
import com.liferay.jenkins.results.parser.test.clazz.ModulesJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.task.TestTask;
import com.liferay.jenkins.results.parser.test.task.TestTaskFactory;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Yi-Chen Tsai
 */
public class ModulesJUnitBatchTestClassGroup extends JUnitBatchTestClassGroup {

	@Override
	public List<TestTask> getTestTasks() {
		if (_testTasks != null) {
			return new ArrayList<>(_testTasks.values());
		}

		_testTasks = new HashMap<>();

		TestClassGroup.GroupingStrategy groupingStrategy =
			getGroupingStrategy();

		for (ModulesJUnitTestClass modulesJUnitTestClass :
				_getModulesJUnitTestClasses()) {

			String testTaskName = modulesJUnitTestClass.getTestTaskName();

			TestTask testTask = _testTasks.get(testTaskName);

			if (testTask == null) {
				testTask = TestTaskFactory.newTestTask(
					this, groupingStrategy, testTaskName);

				_testTasks.put(testTaskName, testTask);
			}

			testTask.addTestClass(modulesJUnitTestClass);

			modulesJUnitTestClass.setTestTask(testTask);
		}

		return new ArrayList<>(_testTasks.values());
	}

	protected ModulesJUnitBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected ModulesJUnitBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	protected ModulesJUnitBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob,
		JUnitTestBatch jUnitTestBatch) {

		super(batchName, portalTestClassJob, jUnitTestBatch);
	}

	@Override
	protected void addTestClass(TestClass testClass) {
		String testClassFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			testClass.getTestClassFile());

		File modulesDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		String modulesDirPath = JenkinsResultsParserUtil.getCanonicalPath(
			modulesDir);

		if (!testClassFilePath.startsWith(modulesDirPath + "/")) {
			return;
		}

		super.addTestClass(testClass);
	}

	@Override
	protected List<JobProperty> getDefaultExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.addAll(super.getDefaultExcludesJobProperties());

		for (File modulePullSubrepoDir :
				portalGitWorkingDirectory.getModulePullSubrepoDirs()) {

			excludesJobProperties.add(
				getJobProperty(
					"test.batch.class.names.excludes.subrepo",
					modulePullSubrepoDir, JobProperty.Type.EXCLUDE_GLOB));
		}

		return excludesJobProperties;
	}

	@Override
	protected List<JobProperty> getReleaseIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		Set<File> releaseModuleAppDirs = _getReleaseModuleAppDirs();

		if (!releaseModuleAppDirs.isEmpty()) {
			for (File releaseModuleAppDir : releaseModuleAppDirs) {
				includesJobProperties.add(
					getJobProperty(
						"test.batch.class.names.includes.modules",
						releaseModuleAppDir, JobProperty.Type.INCLUDE_GLOB));
			}
		}

		return includesJobProperties;
	}

	@Override
	protected List<JobProperty> getRelevantExcludesJobProperties() {
		Set<File> modifiedModuleDirsList = new HashSet<>();

		try {
			modifiedModuleDirsList.addAll(
				portalGitWorkingDirectory.getModifiedModuleDirsList());
		}
		catch (IOException ioException) {
			File workingDirectory =
				portalGitWorkingDirectory.getWorkingDirectory();

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to get relevant module group directories in ",
					workingDirectory.getPath()),
				ioException);
		}

		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.addAll(getDefaultExcludesJobProperties());

		for (File modifiedFile :
				portalGitWorkingDirectory.getModifiedFilesList()) {

			if (JenkinsResultsParserUtil.isPoshiFile(modifiedFile)) {
				continue;
			}

			excludesJobProperties.addAll(
				getJobProperties(
					modifiedFile,
					"modules.includes.required.test.batch.class.names.excludes",
					JobProperty.Type.MODULE_EXCLUDE_GLOB, null));
		}

		return excludesJobProperties;
	}

	@Override
	protected List<JobProperty> getRelevantIncludesJobProperties() {
		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			return super.getRelevantIncludesJobProperties();
		}

		Set<File> modifiedModuleDirsSet = new HashSet<>();
		List<File> modifiedNonposhiModulesList = new ArrayList<>();
		List<File> modifiedPoshiModulesList = new ArrayList<>();

		try {
			modifiedModuleDirsSet.addAll(
				portalGitWorkingDirectory.getModifiedModuleDirsList());
			modifiedNonposhiModulesList =
				portalGitWorkingDirectory.getModifiedNonposhiModules();
			modifiedPoshiModulesList =
				portalGitWorkingDirectory.getModifiedPoshiModules();
		}
		catch (IOException ioException) {
			File workingDirectory =
				portalGitWorkingDirectory.getWorkingDirectory();

			throw new RuntimeException(
				JenkinsResultsParserUtil.combine(
					"Unable to get relevant module group directories in ",
					workingDirectory.getPath()),
				ioException);
		}

		if (testRelevantChanges) {
			modifiedModuleDirsSet.addAll(
				getRequiredModuleDirs(
					Lists.newArrayList(modifiedModuleDirsSet)));
		}

		Set<JobProperty> includesJobProperties = new HashSet<>();

		for (File modifiedModuleDir : modifiedModuleDirsSet) {
			if (modifiedPoshiModulesList.contains(modifiedModuleDir) &&
				!modifiedNonposhiModulesList.contains(modifiedModuleDir)) {

				continue;
			}

			includesJobProperties.add(
				getJobProperty(
					"test.batch.class.names.includes.modules",
					modifiedModuleDir, JobProperty.Type.INCLUDE_GLOB));
		}

		for (File modifiedFile :
				portalGitWorkingDirectory.getModifiedFilesList()) {

			if (JenkinsResultsParserUtil.isPoshiFile(modifiedFile)) {
				continue;
			}

			String modifiedFileCanonicalPath =
				JenkinsResultsParserUtil.getCanonicalPath(modifiedFile);

			if (modifiedFileCanonicalPath.contains("modules")) {
				includesJobProperties.addAll(
					getJobProperties(
						modifiedFile, "test.batch.class.names.includes.modules",
						JobProperty.Type.MODULE_INCLUDE_GLOB, null));
			}

			includesJobProperties.addAll(
				getJobProperties(
					modifiedFile,
					"modules.includes.required.test.batch.class.names.includes",
					JobProperty.Type.MODULE_INCLUDE_GLOB, null));
		}

		return new ArrayList<>(includesJobProperties);
	}

	private String _getAppTitle(File appBndFile) {
		Properties appBndProperties = JenkinsResultsParserUtil.getProperties(
			appBndFile);

		String appTitle = appBndProperties.getProperty(
			"Liferay-Releng-App-Title");

		return appTitle.replace(
			"${liferay.releng.app.title.prefix}", _getAppTitlePrefix());
	}

	private String _getAppTitlePrefix() {
		Job job = getJob();

		if (job.getBuildProfile() == Job.BuildProfile.DXP) {
			return "Liferay";
		}

		return "Liferay CE";
	}

	private Set<String> _getBundledAppNames() {
		Set<String> bundledAppNames = new HashSet<>();

		File liferayHome = _getLiferayHome();

		if ((liferayHome == null) || !liferayHome.exists()) {
			return bundledAppNames;
		}

		List<File> bundledApps = JenkinsResultsParserUtil.findFiles(
			liferayHome, ".*\\.lpkg");

		for (File bundledApp : bundledApps) {
			String bundledAppName = bundledApp.getName();

			bundledAppNames.add(bundledAppName);
		}

		return bundledAppNames;
	}

	private Set<String> _getBundledModuleNames() {
		Set<String> bundledModuleNames = new HashSet<>();

		File liferayHome = _getLiferayHome();

		if ((liferayHome == null) || !liferayHome.exists()) {
			return bundledModuleNames;
		}

		List<File> bundledModules = JenkinsResultsParserUtil.findFiles(
			liferayHome, ".*\\.jar");

		for (File bundledModule : bundledModules) {
			String bundledModuleName = bundledModule.getName();

			bundledModuleNames.add(bundledModuleName);
		}

		return bundledModuleNames;
	}

	private File _getLiferayHome() {
		Properties buildProperties = JenkinsResultsParserUtil.getProperties(
			new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				"build.properties"));

		String liferayHomePath = buildProperties.getProperty("liferay.home");

		if (liferayHomePath == null) {
			return null;
		}

		return new File(liferayHomePath);
	}

	private List<ModulesJUnitTestClass> _getModulesJUnitTestClasses() {
		List<ModulesJUnitTestClass> modulesJUnitTestClasses = new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			modulesJUnitTestClasses.add((ModulesJUnitTestClass)testClass);
		}

		return modulesJUnitTestClasses;
	}

	private File _getReleaseModuleAppDir(File releaseModuleDir) {
		if (releaseModuleDir.equals(
				portalGitWorkingDirectory.getWorkingDirectory())) {

			return null;
		}

		File appBndFile = new File(releaseModuleDir, "app.bnd");

		if (appBndFile.exists()) {
			return releaseModuleDir;
		}

		return _getReleaseModuleAppDir(releaseModuleDir.getParentFile());
	}

	private Set<File> _getReleaseModuleAppDirs() {
		Set<File> releaseModuleAppDirs = new HashSet<>();

		Set<String> bundledAppNames = _getBundledAppNames();

		for (File moduleAppDir : portalGitWorkingDirectory.getModuleAppDirs()) {
			File appBndFile = new File(moduleAppDir, "app.bnd");

			String appTitle = _getAppTitle(appBndFile);

			for (String bundledAppName : bundledAppNames) {
				String regex = JenkinsResultsParserUtil.combine(
					"((.* - )?", Pattern.quote(appTitle), " -.*|",
					Pattern.quote(appTitle), ")\\.lpkg");

				if (!bundledAppName.matches(regex)) {
					continue;
				}

				List<File> skipTestIntegrationCheckFiles =
					JenkinsResultsParserUtil.findFiles(
						moduleAppDir,
						".lfrbuild-ci-skip-test-integration-check");

				if (!skipTestIntegrationCheckFiles.isEmpty()) {
					System.out.println("Ignoring " + moduleAppDir);

					continue;
				}

				releaseModuleAppDirs.add(moduleAppDir);
			}
		}

		if (releaseModuleAppDirs.isEmpty()) {
			for (File releaseModuleDir : _getReleaseModuleDirs()) {
				File releaseModuleAppDir = _getReleaseModuleAppDir(
					releaseModuleDir);

				if (releaseModuleAppDir == null) {
					continue;
				}

				releaseModuleAppDirs.add(releaseModuleAppDir);
			}
		}

		return releaseModuleAppDirs;
	}

	private Set<File> _getReleaseModuleDirs() {
		Set<File> releaseModuleDirs = new HashSet<>();

		Set<String> bundledModuleNames = _getBundledModuleNames();

		for (File moduleDir : portalGitWorkingDirectory.getModuleDirs()) {
			File bndBndFile = new File(moduleDir, "bnd.bnd");

			String symbolicName = _getSymbolicName(bndBndFile);

			for (String bundledModuleName : bundledModuleNames) {
				if (!bundledModuleName.equals(symbolicName + ".jar")) {
					continue;
				}

				List<File> skipTestIntegrationCheckFiles =
					JenkinsResultsParserUtil.findFiles(
						moduleDir, ".lfrbuild-ci-skip-test-integration-check");

				if (!skipTestIntegrationCheckFiles.isEmpty()) {
					System.out.println("Ignoring " + moduleDir);

					continue;
				}

				releaseModuleDirs.add(moduleDir);

				break;
			}
		}

		return releaseModuleDirs;
	}

	private String _getSymbolicName(File bndBndFile) {
		Properties bndBndProperties = JenkinsResultsParserUtil.getProperties(
			bndBndFile);

		return bndBndProperties.getProperty("Bundle-SymbolicName");
	}

	private Map<String, TestTask> _testTasks;

}