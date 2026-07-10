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
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class BatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

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

		List<TestClass> axisTestClasses = new ArrayList<>();

		for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
			axisTestClasses.addAll(axisTestClassGroup.getTestClasses());
		}

		Collections.sort(axisTestClasses);

		Assert.assertEquals(testClasses, axisTestClasses);
	}

}