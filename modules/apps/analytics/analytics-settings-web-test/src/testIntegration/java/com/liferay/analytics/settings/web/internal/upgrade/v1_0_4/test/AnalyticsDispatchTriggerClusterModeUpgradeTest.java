/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.upgrade.v1_0_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.service.persistence.DispatchTriggerPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class AnalyticsDispatchTriggerClusterModeUpgradeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeKeepsClusterModeOfOtherDispatchTriggers()
		throws Exception {

		DispatchTrigger dispatchTrigger = _addAllNodesDispatchTrigger(
			"batch-planner");

		_runUpgrade();

		Assert.assertEquals(
			DispatchTaskClusterMode.ALL_NODES.getMode(),
			_getDispatchTaskClusterMode(
				dispatchTrigger.getDispatchTriggerId()));

		_dispatchTriggerLocalService.deleteDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId());
	}

	@Test
	public void testUpgradeUpdatesAllNodesAnalyticsDispatchTriggers()
		throws Exception {

		for (String dispatchTaskExecutorType : _DISPATCH_TASK_EXECUTOR_TYPES) {
			DispatchTrigger dispatchTrigger = _addAllNodesDispatchTrigger(
				dispatchTaskExecutorType);

			_runUpgrade();

			Assert.assertEquals(
				DispatchTaskClusterMode.SINGLE_NODE_PERSISTED.getMode(),
				_getDispatchTaskClusterMode(
					dispatchTrigger.getDispatchTriggerId()));

			_dispatchTriggerLocalService.deleteDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());
		}
	}

	private DispatchTrigger _addAllNodesDispatchTrigger(
			String dispatchTaskExecutorType)
		throws Exception {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, TestPropsValues.getUserId(), dispatchTaskExecutorType,
				null, RandomTestUtil.randomString(), false);

		dispatchTrigger.setDispatchTaskClusterMode(
			DispatchTaskClusterMode.ALL_NODES.getMode());

		return _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger);
	}

	private int _getDispatchTaskClusterMode(long dispatchTriggerId)
		throws Exception {

		_dispatchTriggerPersistence.clearCache();

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTriggerId);

		return dispatchTrigger.getDispatchTaskClusterMode();
	}

	private void _runUpgrade() throws Exception {
		for (UpgradeProcess upgradeProcess :
				UpgradeTestUtil.getUpgradeSteps(
					_upgradeStepRegistrator, new Version(1, 0, 4))) {

			upgradeProcess.upgrade();
		}
	}

	private static final String[] _DISPATCH_TASK_EXECUTOR_TYPES = {
		"export-analytics-asset-entities", "export-analytics-dxp-entities"
	};

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private DispatchTriggerPersistence _dispatchTriggerPersistence;

	@Inject(
		filter = "(&(component.name=com.liferay.analytics.settings.web.internal.upgrade.registry.AnalyticsSettingsWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}