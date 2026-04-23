/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Leslie Wong
 */
public class CompileModulesBatchTestClassGroup
	extends ModulesBatchTestClassGroup {

	protected CompileModulesBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected CompileModulesBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	@Override
	protected void setAxisTestClassGroups() {
		if (!containsTestClasses()) {
			return;
		}

		int axisCount = getAxisCount();

		if (axisCount == 0) {
			return;
		}

		List<TestClass> testClasses = getTestClasses();

		Collections.sort(
			testClasses,
			(testClass1, testClass2) -> {
				List<TestClassMethod> testClassMethods1 =
					testClass1.getTestClassMethods();
				List<TestClassMethod> testClassMethods2 =
					testClass2.getTestClassMethods();

				int methodCount1 = testClassMethods1.size();
				int methodCount2 = testClassMethods2.size();

				if (methodCount1 != methodCount2) {
					return Integer.compare(methodCount2, methodCount1);
				}

				return testClass1.compareTo(testClass2);
			});

		List<AxisTestClassGroup> newAxisTestClassGroups = new ArrayList<>(
			axisCount);
		int[] axisTestClassMethodCounts = new int[axisCount];

		for (int i = 0; i < axisCount; i++) {
			newAxisTestClassGroups.add(
				TestClassGroupFactory.newAxisTestClassGroup(this));
		}

		for (TestClass testClass : testClasses) {
			int minAxisIndex = 0;

			for (int i = 1; i < axisCount; i++) {
				if (axisTestClassMethodCounts[i] <
						axisTestClassMethodCounts[minAxisIndex]) {

					minAxisIndex = i;
				}
			}

			AxisTestClassGroup axisTestClassGroup = newAxisTestClassGroups.get(
				minAxisIndex);

			axisTestClassGroup.addTestClass(testClass);

			List<TestClassMethod> testClassMethods =
				testClass.getTestClassMethods();

			axisTestClassMethodCounts[minAxisIndex] += testClassMethods.size();
		}

		for (AxisTestClassGroup axisTestClassGroup : newAxisTestClassGroups) {
			List<TestClass> axisTestClasses =
				axisTestClassGroup.getTestClasses();

			if (!axisTestClasses.isEmpty()) {
				axisTestClassGroups.add(axisTestClassGroup);
			}
		}
	}

	@Override
	protected void setTestClasses() throws IOException {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		List<PathMatcher> excludesPathMatchers = getPathMatchers(
			getExcludesJobProperties());
		List<PathMatcher> includesPathMatchers = getIncludesPathMatchers();

		if (testRelevantChanges) {
			List<File> modifiedModuleDirsList =
				portalGitWorkingDirectory.getModifiedModuleDirsList(
					excludesPathMatchers, includesPathMatchers);

			for (File modifiedModuleDir : modifiedModuleDirsList) {
				List<File> lfrBuildPortalFiles =
					JenkinsResultsParserUtil.findFiles(
						modifiedModuleDir, "\\.lfrbuild-portal");

				if (!lfrBuildPortalFiles.isEmpty()) {
					moduleDirsList.add(modifiedModuleDir);
				}
			}
		}
		else {
			moduleDirsList.addAll(
				portalGitWorkingDirectory.getModuleDirsList(
					excludesPathMatchers, includesPathMatchers));
		}

		addTestClasses(moduleDirsList);
	}

}