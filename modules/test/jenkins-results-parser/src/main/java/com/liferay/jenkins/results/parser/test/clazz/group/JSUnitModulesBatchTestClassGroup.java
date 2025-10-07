/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitModulesBatchTestClassGroup
	extends ModulesBatchTestClassGroup {

	protected JSUnitModulesBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected JSUnitModulesBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	@Override
	protected void setAxisTestClassGroups() {
		super.setAxisTestClassGroups();

		TestClass faroTestClass = null;
		AxisTestClassGroup originalAxisTestClassGroup = null;

		axisTestClassGroupLoop:
		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
				String testClassName = testClass.getName();

				if (testClassName.contains("osb-faro")) {
					faroTestClass = testClass;

					originalAxisTestClassGroup = axisTestClassGroup;

					break axisTestClassGroupLoop;
				}
			}
		}

		if (faroTestClass != null) {
			originalAxisTestClassGroup.removeTestClass(faroTestClass);

			AxisTestClassGroup faroAxisTestClassGroup =
				TestClassGroupFactory.newAxisTestClassGroup(this);

			faroAxisTestClassGroup.addTestClass(faroTestClass);

			axisTestClassGroups.add(faroAxisTestClassGroup);
		}
	}

	@Override
	protected void setTestClasses() throws IOException {
		List<File> moduleDirs = new ArrayList<>();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		moduleDirs.addAll(
			portalGitWorkingDirectory.getModuleDirsList(
				getPathMatchers(getExcludesJobProperties()),
				getIncludesPathMatchers()));

		List<String> excludedTestMethodNames = new ArrayList<>();

		for (JobProperty excludesJobProperty : getExcludesJobProperties()) {
			String excludesJobPropertyValue = excludesJobProperty.getValue();

			if (excludesJobPropertyValue != null) {
				for (String excludesJobPropertyValueElement :
						excludesJobPropertyValue.split("\\s*,\\s*")) {

					excludesJobPropertyValueElement =
						excludesJobPropertyValueElement.replace("/", ":");

					excludedTestMethodNames.add(
						excludesJobPropertyValueElement.replaceAll(
							"[^a-zA-Z-:]", ""));
				}
			}
		}

		for (File moduleDir : moduleDirs) {
			List<File> moduleTestDirs = _getModulesProjectDirs(moduleDir);

			for (File moduleTestDir : moduleTestDirs) {
				String moduleTestDirPath =
					JenkinsResultsParserUtil.getCanonicalPath(moduleTestDir);
				TestClass testClass = TestClassFactory.newTestClass(
					this, moduleTestDir);

				for (File jsUnitFile :
						portalGitWorkingDirectory.getJSUnitFiles()) {

					String jsUnitFilePath =
						JenkinsResultsParserUtil.getCanonicalPath(jsUnitFile);

					if (!jsUnitFilePath.startsWith(moduleTestDirPath)) {
						continue;
					}

					String testClassMethodName =
						JenkinsResultsParserUtil.getPathRelativeTo(
							jsUnitFile,
							portalGitWorkingDirectory.getWorkingDirectory());

					testClass.addTestClassMethod(
						TestClassFactory.newTestClassMethod(
							false, testClassMethodName, testClass));
				}

				if (!testClass.hasTestClassMethods()) {
					continue;
				}

				List<TestClassMethod> testClassMethods =
					testClass.getTestClassMethods();

				Iterator<TestClassMethod> iterator =
					testClassMethods.iterator();

				while (iterator.hasNext()) {
					TestClassMethod testClassMethod = iterator.next();

					String testClassMethodName = testClassMethod.getName();

					for (String excludedMethodName : excludedTestMethodNames) {
						if (testClassMethodName.contains(excludedMethodName)) {
							iterator.remove();

							break;
						}
					}
				}

				if (!testClassMethods.isEmpty()) {
					addTestClass(testClass);
				}
			}
		}
	}

	private List<File> _getModulesProjectDirs(File portalModulesBaseDir)
		throws IOException {

		List<File> modulesProjectDirs = new ArrayList<>();

		boolean testGitrepoJSUnit = _isTestGitrepoJSUnit();

		Files.walkFileTree(
			portalModulesBaseDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					File file = filePath.toFile();

					File currentDirectory =
						JenkinsResultsParserUtil.getCanonicalFile(file);

					String currentDirectoryPath =
						currentDirectory.getAbsolutePath();

					if (currentDirectoryPath.contains("modules") &&
						!(currentDirectoryPath.contains("modules/apps") ||
						  currentDirectoryPath.contains("modules/dxp"))) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (!testGitrepoJSUnit) {
						File gitrepoFile = new File(
							currentDirectory, ".gitrepo");

						if (gitrepoFile.exists() &&
							!currentDirectoryPath.contains("osb-faro")) {

							return FileVisitResult.SKIP_SUBTREE;
						}
					}

					File buildGradleFile = new File(
						currentDirectory, "build.gradle");
					File packageJSONFile = new File(
						currentDirectory, "package.json");

					if (!buildGradleFile.exists() ||
						!packageJSONFile.exists()) {

						return FileVisitResult.CONTINUE;
					}

					try {
						JSONObject packageJSONObject = new JSONObject(
							JenkinsResultsParserUtil.read(packageJSONFile));

						if (!packageJSONObject.has("scripts")) {
							return FileVisitResult.CONTINUE;
						}

						JSONObject scriptsJSONObject =
							packageJSONObject.getJSONObject("scripts");

						if (!scriptsJSONObject.has("test")) {
							return FileVisitResult.CONTINUE;
						}

						modulesProjectDirs.add(currentDirectory);

						return FileVisitResult.SKIP_SUBTREE;
					}
					catch (IOException | JSONException exception) {
						return FileVisitResult.CONTINUE;
					}
				}

			});

		return modulesProjectDirs;
	}

	private boolean _isTestGitrepoJSUnit() {
		JobProperty jobProperty = getJobProperty("test.gitrepo.js.unit");

		String jobPropertyValue = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobPropertyValue) &&
			jobPropertyValue.equals("true")) {

			recordJobProperty(jobProperty);

			return true;
		}

		return false;
	}

}