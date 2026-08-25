/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.RandomTestUtil;
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
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class BatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	public void setUpGitRemotes() throws Exception {
		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");
	}

	@Test
	public void testGetAxisCount() {
		_testGetAxisCount("-1", null, 3, 12);
		_testGetAxisCount("0", null, 0, 12);
		_testGetAxisCount("7", null, 7, 12);
		_testGetAxisCount("abc", null, 3, 12);
		_testGetAxisCount(null, "", 1, 12);
		_testGetAxisCount(null, "-3", 1, 12);
		_testGetAxisCount(null, "abc", 1, 12);
		_testGetAxisCount(null, null, 0, 0);
		_testGetAxisCount(null, null, 1, 1);
		_testGetAxisCount(null, null, 3, 12);
	}

	@Test
	public void testGetAxisCountAxisMaxSizeZero() {
		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, "0", null, 12);

		try {
			batchTestClassGroup.getAxisCount();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			testEquals(
				"'test.batch.axis.max.size' cannot be 0 or less",
				runtimeException.getMessage());
		}
	}

	@Test
	public void testGetAxisMaxSize() {
		_testGetAxisMaxSize("", 5000);
		_testGetAxisMaxSize("-3", 5000);
		_testGetAxisMaxSize("0", 0);
		_testGetAxisMaxSize("5", 5);
		_testGetAxisMaxSize("abc", 5000);
	}

	@Test
	public void testGetAxisTestClassGroups() {
		BatchTestClassGroupTestUtil.resetCaches();

		BatchTestClassGroup batchTestClassGroup = new BatchTestClassGroup(
			"default", BatchTestClassGroupTestUtil.getPortalTestClassJob()) {
		};

		int axisMaxSize = batchTestClassGroup.getAxisMaxSize();

		for (int i = 0; i < (axisMaxSize + 2); i++) {
			batchTestClassGroup.addTestClass(
				TestClassFactory.newTestClass(
					batchTestClassGroup,
					new File(RandomTestUtil.randomString())));
		}

		batchTestClassGroup.setAxisTestClassGroups();

		List<TestClass> testClasses = batchTestClassGroup.getTestClasses();

		List<AxisTestClassGroup> axisTestClassGroups =
			batchTestClassGroup.getAxisTestClassGroups();

		Assert.assertEquals(
			axisTestClassGroups.toString(),
			(int)Math.ceil((double)testClasses.size() / axisMaxSize),
			axisTestClassGroups.size());

		List<TestClass> axisTestClasses = _getTestClasses(axisTestClassGroups);

		Collections.sort(axisTestClasses);

		Assert.assertEquals(testClasses, axisTestClasses);
	}

	@Test
	public void testSetAxisTestClassGroups() {
		_testSetAxisTestClassGroups("10", null, Arrays.asList(1, 1, 1), 3);
		_testSetAxisTestClassGroups("4", null, Arrays.asList(2, 2, 2, 1), 7);
		_testSetAxisTestClassGroups(null, null, Collections.emptyList(), 0);
		_testSetAxisTestClassGroups(null, null, Arrays.asList(4, 4, 4), 12);
		_testSetAxisTestClassGroups(null, null, Arrays.asList(5, 5, 3), 13);
	}

	private List<Integer> _getAxisSizes(
		List<AxisTestClassGroup> axisTestClassGroups) {

		List<Integer> axisSizes = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			List<TestClass> testClasses = axisTestClassGroup.getTestClasses();

			axisSizes.add(testClasses.size());
		}

		return axisSizes;
	}

	private List<TestClass> _getTestClasses(
		List<AxisTestClassGroup> axisTestClassGroups) {

		List<TestClass> testClasses = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			testClasses.addAll(axisTestClassGroup.getTestClasses());
		}

		return testClasses;
	}

	private BatchTestClassGroup _newBatchTestClassGroup(
		String axisCount, String axisMaxSize, String segmentMaxChildren,
		int testClassCount) {

		BatchTestClassGroupTestUtil.resetCaches();

		Properties jobProperties = new Properties();

		if (axisCount != null) {
			jobProperties.setProperty("test.batch.axis.count", axisCount);
		}

		if (axisMaxSize != null) {
			jobProperties.setProperty("test.batch.axis.max.size", axisMaxSize);
		}

		if (segmentMaxChildren != null) {
			jobProperties.setProperty(
				"test.batch.segment.max.children", segmentMaxChildren);
		}

		BatchTestClassGroup batchTestClassGroup = new BatchTestClassGroup(
			"default",
			BatchTestClassGroupTestUtil.getPortalTestClassJob(jobProperties)) {
		};

		for (int i = 0; i < testClassCount; i++) {
			batchTestClassGroup.addTestClass(
				TestClassFactory.newTestClass(
					batchTestClassGroup,
					new File(RandomTestUtil.randomString())));
		}

		return batchTestClassGroup;
	}

	private void _testGetAxisCount(
		String axisCount, String axisMaxSize, int expectedAxisCount,
		int testClassCount) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			axisCount, axisMaxSize, null, testClassCount);

		testEquals(expectedAxisCount, batchTestClassGroup.getAxisCount());
	}

	private void _testGetAxisMaxSize(
		String axisMaxSize, int expectedAxisMaxSize) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, axisMaxSize, null, 0);

		testEquals(expectedAxisMaxSize, batchTestClassGroup.getAxisMaxSize());
	}

	private void _testSetAxisTestClassGroups(
		String axisCount, String axisMaxSize, List<Integer> expectedAxisSizes,
		int testClassCount) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			axisCount, axisMaxSize, null, testClassCount);

		batchTestClassGroup.setAxisTestClassGroups();

		List<AxisTestClassGroup> axisTestClassGroups =
			batchTestClassGroup.getAxisTestClassGroups();

		testEquals(expectedAxisSizes, _getAxisSizes(axisTestClassGroups));

		List<TestClass> axisTestClasses = _getTestClasses(axisTestClassGroups);

		Collections.sort(axisTestClasses);

		testEquals(batchTestClassGroup.getTestClasses(), axisTestClasses);
	}

}