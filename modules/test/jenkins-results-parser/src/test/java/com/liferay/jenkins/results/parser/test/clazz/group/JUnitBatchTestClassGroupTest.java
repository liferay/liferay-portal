/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Calum Ragan
 */
public class JUnitBatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");
	}

	@Test
	public void testGetAxisCount() throws Exception {
		String className = "SampleAutoBalanceTest";
		String packagePath = "com/liferay/";

		File workingDirectory = temporaryFolder.newFolder();

		File packageDir = new File(workingDirectory, packagePath);

		packageDir.mkdirs();

		BatchTestClassGroupTestUtil.newTestClassFile(className, packageDir);

		Properties jobProperties = new Properties();

		jobProperties.setProperty(
			"test.class.names.auto.balance", packagePath + className + ".java");

		JUnitBatchTestClassGroup jUnitBatchTestClassGroup =
			new JUnitBatchTestClassGroup(
				"unit",
				BatchTestClassGroupTestUtil.getPortalTestClassJob(
					jobProperties,
					Collections.singletonList(
						new File(workingDirectory, "Modified.java")),
					workingDirectory)) {

				@Override
				protected void setTestClasses() {
				}

			};

		testEquals(1, jUnitBatchTestClassGroup.getAxisCount());

		List<AxisTestClassGroup> axisTestClassGroups =
			jUnitBatchTestClassGroup.getAxisTestClassGroups();

		Assert.assertEquals(
			axisTestClassGroups.toString(), 1, axisTestClassGroups.size());

		AxisTestClassGroup axisTestClassGroup = axisTestClassGroups.get(0);

		List<TestClass> testClasses = axisTestClassGroup.getTestClasses();

		testEquals(1, testClasses.size());
	}

	@Test
	public void testSetAxisTestClassGroups() throws Exception {
		_testSetAxisTestClassGroups(Arrays.asList(1, 3, 3), "3000");
		_testSetAxisTestClassGroups(Arrays.asList(4, 3), "");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testSetAxisTestClassGroups(
			List<Integer> expectedAxisSizes, String targetAxisDuration)
		throws Exception {

		JobPropertyFactory.clear();

		Properties jobProperties = new Properties();

		jobProperties.setProperty("test.batch.default.test.duration", "1000");
		jobProperties.setProperty(
			"test.batch.default.test.overhead.duration", "0");
		jobProperties.setProperty(
			"test.batch.target.axis.duration", targetAxisDuration);

		List<File> testClassFiles = new ArrayList<>();

		File workingDirectory = temporaryFolder.newFolder();

		for (int i = 0; i < 7; i++) {
			testClassFiles.add(
				BatchTestClassGroupTestUtil.newTestClassFile(
					"Sample" + i + "Test", workingDirectory));
		}

		JUnitBatchTestClassGroup jUnitBatchTestClassGroup =
			new JUnitBatchTestClassGroup(
				"unit",
				BatchTestClassGroupTestUtil.getPortalTestClassJob(
					jobProperties, Collections.emptyList(), workingDirectory)) {

				@Override
				protected void setTestClasses() {
					for (File testClassFile : testClassFiles) {
						addTestClass(
							TestClassFactory.newTestClass(this, testClassFile));
					}
				}

			};

		testEquals(
			expectedAxisSizes,
			BatchTestClassGroupTestUtil.getAxisSizes(
				jUnitBatchTestClassGroup.getAxisTestClassGroups()));
	}

}