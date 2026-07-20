/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eric Yan
 */
public class BaseAsyncDestinationTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSetWorkersSize() {
		ParallelDestination parallelDestination = new ParallelDestination();

		parallelDestination.setName(RandomTestUtil.randomString());
		parallelDestination.setPortalExecutorManager(
			Mockito.mock(PortalExecutorManager.class));

		parallelDestination.open();

		_testSetWorkersSize(parallelDestination, 1, 1);
		_testSetWorkersSize(parallelDestination, 2, 2);
		_testSetWorkersSize(parallelDestination, 1, 1);
	}

	private void _testSetWorkersSize(
		ParallelDestination parallelDestination, int workersCoreSize,
		int workersMaxSize) {

		parallelDestination.setWorkersSize(workersCoreSize, workersMaxSize);

		DestinationStatistics destinationStatistics =
			parallelDestination.getDestinationStatistics();

		Assert.assertEquals(
			workersCoreSize, destinationStatistics.getMinThreadPoolSize());
		Assert.assertEquals(
			workersMaxSize, destinationStatistics.getMaxThreadPoolSize());
	}

}