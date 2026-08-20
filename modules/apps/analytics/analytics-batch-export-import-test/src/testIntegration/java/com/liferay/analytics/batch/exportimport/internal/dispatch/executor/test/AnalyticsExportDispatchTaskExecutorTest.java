/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.batch.exportimport.internal.dispatch.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class AnalyticsExportDispatchTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIsClusterModeSingle() {
		for (String dispatchTaskExecutorType : _DISPATCH_TASK_EXECUTOR_TYPES) {
			Assert.assertTrue(
				_dispatchTaskExecutorRegistry.isClusterModeSingle(
					dispatchTaskExecutorType));
		}
	}

	@Test
	public void testUpdateDispatchTriggerRejectsAllNodesClusterMode()
		throws Exception {

		for (String dispatchTaskExecutorType : _DISPATCH_TASK_EXECUTOR_TYPES) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.addDispatchTrigger(
					null, TestPropsValues.getUserId(), dispatchTaskExecutorType,
					null, RandomTestUtil.randomString(), false);

			try {
				dispatchTrigger =
					_dispatchTriggerLocalService.updateDispatchTrigger(
						dispatchTrigger.getDispatchTriggerId(), false,
						"0 0 * * * ?", DispatchTaskClusterMode.ALL_NODES, 0, 0,
						0, 0, 0, true, false, 0, 1, 2026, 0, 0, "UTC");

				DispatchTaskClusterMode dispatchTaskClusterMode =
					DispatchTaskClusterMode.valueOf(
						dispatchTrigger.getDispatchTaskClusterMode());

				Assert.assertNotEquals(
					StorageType.MEMORY,
					dispatchTaskClusterMode.getStorageType());
			}
			finally {
				_dispatchTriggerLocalService.deleteDispatchTrigger(
					dispatchTrigger);
			}
		}
	}

	private static final String[] _DISPATCH_TASK_EXECUTOR_TYPES = {
		"export-analytics-asset-entities", "export-analytics-dxp-entities"
	};

	@Inject
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}