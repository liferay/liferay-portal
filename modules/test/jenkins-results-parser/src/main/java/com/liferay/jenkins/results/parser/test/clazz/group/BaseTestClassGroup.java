/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildDatabaseUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Peter Yoo
 */
public abstract class BaseTestClassGroup implements TestClassGroup {

	public abstract String getOSArchitecture();

	public String getSlaveLabel() {
		String baseSlaveLabel = getBaseSlaveLabel();

		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			return baseSlaveLabel;
		}

		try {
			String osArchitecture = getOSArchitecture();

			if (Objects.equals(osArchitecture, "arm") ||
				Objects.equals(osArchitecture, "x86")) {

				StringBuilder sb = new StringBuilder();

				sb.append("slave.label.");
				sb.append(osArchitecture);
				sb.append("[");
				sb.append(baseSlaveLabel);
				sb.append("]");

				String slaveLabel = JenkinsResultsParserUtil.getBuildProperty(
					sb.toString());

				if (!JenkinsResultsParserUtil.isNullOrEmpty(slaveLabel)) {
					return slaveLabel;
				}
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return baseSlaveLabel;
	}

	@Override
	public List<TestClass> getTestClasses() {
		return new ArrayList<>(_testClasses);
	}

	@Override
	public List<File> getTestClassFiles() {
		List<File> testClassFiles = new ArrayList<>();

		for (TestClass testClass : _testClasses) {
			testClassFiles.add(testClass.getTestClassFile());
		}

		return testClassFiles;
	}

	@Override
	public boolean hasTestClasses() {
		List<TestClass> testClasses = getTestClasses();

		if ((testClasses != null) && !testClasses.isEmpty()) {
			return true;
		}

		return false;
	}

	protected void addTestClass(TestClass testClass) {
		_testClasses.add(testClass);
	}

	protected void addTestClasses(List<TestClass> testClasses) {
		for (TestClass testClass : testClasses) {
			addTestClass(testClass);
		}
	}

	protected boolean containsTestClasses() {
		return !_testClasses.isEmpty();
	}

	protected abstract String getBaseSlaveLabel();

	protected String getBuildStartProperty(String propertyName) {
		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		if (buildDatabase.hasProperties("start.properties")) {
			Properties startProperties = buildDatabase.getProperties(
				"start.properties");

			return JenkinsResultsParserUtil.getProperty(
				startProperties, propertyName);
		}

		return null;
	}

	protected int getTestClassCount() {
		return _testClasses.size();
	}

	protected void removeTestClass(TestClass testClass) {
		_testClasses.remove(testClass);
	}

	private final Set<TestClass> _testClasses = new TreeSet<>();

}