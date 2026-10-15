/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.RandomTestUtil;
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
import org.junit.Test;

import org.mockito.Mockito;

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
		_testGetAxisCount("-1", 3, 12);
		_testGetAxisCount("0", 0, 12);
		_testGetAxisCount("10", 10, 3);
		_testGetAxisCount("abc", 3, 12);
		_testGetAxisCount(null, 0, 0);
		_testGetAxisCount(null, 3, 12);

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
	public void testGetSegmentMaxChildren() {
		_testGetSegmentMaxChildren(0, "0");
		_testGetSegmentMaxChildren(25, "");
		_testGetSegmentMaxChildren(25, "-2");
		_testGetSegmentMaxChildren(3, "3");
	}

	@Test
	public void testSetAxisTestClassGroups() {
		_testSetAxisTestClassGroups("10", Arrays.asList(1, 1, 1), 3);
		_testSetAxisTestClassGroups(null, Arrays.asList(4, 4, 4), 12);
		_testSetAxisTestClassGroups(null, Arrays.asList(5, 5, 3), 13);
		_testSetAxisTestClassGroups(null, Collections.emptyList(), 0);

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			"0", null, null, 12);

		batchTestClassGroup.setAxisTestClassGroups();

		testEquals(
			Collections.emptyList(),
			batchTestClassGroup.getAxisTestClassGroups());
	}

	@Test
	public void testSetSegmentTestClassGroups() {
		String baseSlaveLabel = RandomTestUtil.randomString();
		Integer minimumSlaveRAM = RandomTestUtil.randomInt();
		File testBaseDir = new File(RandomTestUtil.randomString());

		AxisTestClassGroup axisTestClassGroup = _mockAxisTestClassGroup(
			baseSlaveLabel, minimumSlaveRAM, testBaseDir);

		_testSetSegmentTestClassGroups(
			Arrays.asList(1, 1), axisTestClassGroup,
			_mockAxisTestClassGroup(
				RandomTestUtil.randomString(), minimumSlaveRAM, testBaseDir));
		_testSetSegmentTestClassGroups(
			Arrays.asList(1, 1), axisTestClassGroup,
			_mockAxisTestClassGroup(
				baseSlaveLabel, RandomTestUtil.randomInt(), testBaseDir));
		_testSetSegmentTestClassGroups(
			Arrays.asList(1, 1), axisTestClassGroup,
			_mockAxisTestClassGroup(
				baseSlaveLabel, minimumSlaveRAM,
				new File(RandomTestUtil.randomString())));
		_testSetSegmentTestClassGroups(
			Arrays.asList(1, 1), axisTestClassGroup,
			_mockAxisTestClassGroup(baseSlaveLabel, minimumSlaveRAM, null));
		_testSetSegmentTestClassGroups(
			Arrays.asList(2), axisTestClassGroup,
			_mockAxisTestClassGroup(
				baseSlaveLabel, minimumSlaveRAM, testBaseDir));
	}

	@Test
	public void testSetSegmentTestClassGroupsWithMaxChildren() {
		String baseSlaveLabel = RandomTestUtil.randomString();
		Integer minimumSlaveRAM = RandomTestUtil.randomInt();

		_testSetSegmentTestClassGroups(
			Arrays.asList(2, 2), "3",
			_mockAxisTestClassGroup(baseSlaveLabel, minimumSlaveRAM, null),
			_mockAxisTestClassGroup(baseSlaveLabel, minimumSlaveRAM + 1, null),
			_mockAxisTestClassGroup(baseSlaveLabel, minimumSlaveRAM, null),
			_mockAxisTestClassGroup(baseSlaveLabel, minimumSlaveRAM + 1, null));
		_testSetSegmentTestClassGroups(
			Arrays.asList(3, 3, 1), "3",
			_mockAxisTestClassGroups(baseSlaveLabel, 7, minimumSlaveRAM, null));
	}

	@Test
	public void testSetSegmentTestClassGroupsWithZeroMaxChildren() {
		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, null, "0", 0);

		batchTestClassGroup.addAxisTestClassGroup(
			_mockAxisTestClassGroup(
				RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				null));

		try {
			batchTestClassGroup.setSegmentTestClassGroups();

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			testEquals(null, illegalArgumentException.getMessage());
		}
	}

	private List<TestClass> _getTestClasses(
		List<AxisTestClassGroup> axisTestClassGroups) {

		List<TestClass> testClasses = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			testClasses.addAll(axisTestClassGroup.getTestClasses());
		}

		return testClasses;
	}

	private AxisTestClassGroup _mockAxisTestClassGroup(
		String baseSlaveLabel, Integer minimumSlaveRAM, File testBaseDir) {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			baseSlaveLabel
		).when(
			axisTestClassGroup
		).getBaseSlaveLabel();

		Mockito.doReturn(
			minimumSlaveRAM
		).when(
			axisTestClassGroup
		).getMinimumSlaveRAM();

		Mockito.doReturn(
			testBaseDir
		).when(
			axisTestClassGroup
		).getTestBaseDir();

		return axisTestClassGroup;
	}

	private AxisTestClassGroup[] _mockAxisTestClassGroups(
		String baseSlaveLabel, int count, Integer minimumSlaveRAM,
		File testBaseDir) {

		AxisTestClassGroup[] axisTestClassGroups =
			new AxisTestClassGroup[count];

		for (int i = 0; i < count; i++) {
			axisTestClassGroups[i] = _mockAxisTestClassGroup(
				baseSlaveLabel, minimumSlaveRAM, testBaseDir);
		}

		return axisTestClassGroups;
	}

	private BatchTestClassGroup _newBatchTestClassGroup(
		String axisCount, String axisMaxSize, String segmentMaxChildren,
		int testClassCount) {

		JobPropertyFactory.clear();

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
		String axisCount, int expectedAxisCount, int testClassCount) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			axisCount, null, null, testClassCount);

		testEquals(expectedAxisCount, batchTestClassGroup.getAxisCount());
	}

	private void _testGetAxisMaxSize(
		String axisMaxSize, int expectedAxisMaxSize) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, axisMaxSize, null, 0);

		testEquals(expectedAxisMaxSize, batchTestClassGroup.getAxisMaxSize());
	}

	private void _testGetSegmentMaxChildren(
		int expectedSegmentMaxChildren, String segmentMaxChildren) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, null, segmentMaxChildren, 0);

		testEquals(
			expectedSegmentMaxChildren,
			batchTestClassGroup.getSegmentMaxChildren());
	}

	private void _testSetAxisTestClassGroups(
		String axisCount, List<Integer> expectedAxisSizes, int testClassCount) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			axisCount, null, null, testClassCount);

		batchTestClassGroup.setAxisTestClassGroups();

		List<AxisTestClassGroup> axisTestClassGroups =
			batchTestClassGroup.getAxisTestClassGroups();

		testEquals(
			expectedAxisSizes,
			BatchTestClassGroupTestUtil.getAxisSizes(axisTestClassGroups));

		List<TestClass> axisTestClasses = _getTestClasses(axisTestClassGroups);

		Collections.sort(axisTestClasses);

		testEquals(batchTestClassGroup.getTestClasses(), axisTestClasses);
	}

	private void _testSetSegmentTestClassGroups(
		List<Integer> expectedAxisCounts,
		AxisTestClassGroup... axisTestClassGroups) {

		_testSetSegmentTestClassGroups(
			expectedAxisCounts, null, axisTestClassGroups);
	}

	private void _testSetSegmentTestClassGroups(
		List<Integer> expectedAxisCounts, String segmentMaxChildren,
		AxisTestClassGroup... axisTestClassGroups) {

		BatchTestClassGroup batchTestClassGroup = _newBatchTestClassGroup(
			null, null, segmentMaxChildren, 0);

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			batchTestClassGroup.addAxisTestClassGroup(axisTestClassGroup);
		}

		batchTestClassGroup.setSegmentTestClassGroups();

		int segmentCount = batchTestClassGroup.getSegmentCount();

		batchTestClassGroup.setSegmentTestClassGroups();

		testEquals(segmentCount, batchTestClassGroup.getSegmentCount());

		List<Integer> axisCounts = new ArrayList<>();

		List<AxisTestClassGroup> segmentAxisTestClassGroups = new ArrayList<>();

		List<SegmentTestClassGroup> segmentTestClassGroups =
			batchTestClassGroup.getSegmentTestClassGroups();

		for (SegmentTestClassGroup segmentTestClassGroup :
				segmentTestClassGroups) {

			List<AxisTestClassGroup> childAxisTestClassGroups =
				segmentTestClassGroup.getAxisTestClassGroups();

			axisCounts.add(childAxisTestClassGroups.size());

			segmentAxisTestClassGroups.addAll(childAxisTestClassGroups);

			AxisTestClassGroup firstAxisTestClassGroup =
				childAxisTestClassGroups.get(0);

			for (AxisTestClassGroup childAxisTestClassGroup :
					childAxisTestClassGroups) {

				testEquals(
					firstAxisTestClassGroup.getBaseSlaveLabel(),
					childAxisTestClassGroup.getBaseSlaveLabel());
				testEquals(
					firstAxisTestClassGroup.getMinimumSlaveRAM(),
					childAxisTestClassGroup.getMinimumSlaveRAM());
				testEquals(
					firstAxisTestClassGroup.getTestBaseDir(),
					childAxisTestClassGroup.getTestBaseDir());
			}
		}

		testEquals(expectedAxisCounts, axisCounts);

		List<AxisTestClassGroup> batchAxisTestClassGroups =
			batchTestClassGroup.getAxisTestClassGroups();

		testEquals(
			batchAxisTestClassGroups.size(), segmentAxisTestClassGroups.size());

		Assert.assertTrue(
			segmentAxisTestClassGroups.containsAll(batchAxisTestClassGroups));
	}

}