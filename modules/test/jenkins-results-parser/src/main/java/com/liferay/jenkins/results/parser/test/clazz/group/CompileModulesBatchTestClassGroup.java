/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassBalancedListSplitter;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.Collections;
import java.util.Comparator;
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

		long totalWeight = 0;

		for (TestClass testClass : testClasses) {
			totalWeight += testClass.getWeight();
		}

		long maxListWeight = (totalWeight + axisCount - 1) / axisCount;

		Collections.sort(
			testClasses,
			new Comparator<TestClass>() {

				@Override
				public int compare(TestClass testClass1, TestClass testClass2) {
					return Long.compare(
						testClass2.getWeight(), testClass1.getWeight());
				}

			});

		TestClassBalancedListSplitter testClassBalancedListSplitter =
			new TestClassBalancedListSplitter(maxListWeight);

		for (List<TestClass> axisTestClasses :
				testClassBalancedListSplitter.split(testClasses)) {

			AxisTestClassGroup axisTestClassGroup =
				TestClassGroupFactory.newAxisTestClassGroup(this);

			for (TestClass testClass : axisTestClasses) {
				axisTestClassGroup.addTestClass(testClass);
			}

			axisTestClassGroups.add(axisTestClassGroup);
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

				if (lfrBuildPortalFiles.isEmpty()) {
					continue;
				}

				moduleDirsList.add(modifiedModuleDir);
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