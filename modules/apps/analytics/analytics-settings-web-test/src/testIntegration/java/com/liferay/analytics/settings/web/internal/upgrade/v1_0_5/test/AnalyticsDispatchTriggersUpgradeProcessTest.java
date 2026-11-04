/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.upgrade.v1_0_5.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AnalyticsDispatchTriggersUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradeWithInactiveAnalyticsDispatchTrigger()
		throws Exception {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, TestPropsValues.getUserId(),
				"export-analytics-dxp-entities", null,
				"export-analytics-dxp-entities", false);

		try {
			_runUpgrade();

			dispatchTrigger = _dispatchTriggerLocalService.getDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());

			Assert.assertEquals(
				"0 0 * * * ?", dispatchTrigger.getCronExpression());
			Assert.assertTrue(dispatchTrigger.isActive());
		}
		finally {
			_dispatchTriggerLocalService.deleteDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());
		}
	}

	@Test
	public void testUpgradeWithInactiveDispatchTrigger() throws Exception {
		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, TestPropsValues.getUserId(), "batch-planner", null,
				RandomTestUtil.randomString(), false);

		try {
			_runUpgrade();

			dispatchTrigger = _dispatchTriggerLocalService.getDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());

			Assert.assertFalse(dispatchTrigger.isActive());
		}
		finally {
			_dispatchTriggerLocalService.deleteDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());
		}
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.analytics.settings.web.internal.upgrade.v1_0_5." +
				"AnalyticsDispatchTriggersUpgradeProcess");

		upgradeProcess.upgrade();
	}

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.analytics.settings.web.internal.upgrade.registry.AnalyticsSettingsWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}