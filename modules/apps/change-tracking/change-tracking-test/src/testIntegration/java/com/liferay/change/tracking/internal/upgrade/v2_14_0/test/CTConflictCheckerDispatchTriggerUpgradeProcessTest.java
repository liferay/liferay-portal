/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_14_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTConflictCheckerDispatchTriggerUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.change.tracking.internal.upgrade.v2_14_0." +
				"CTConflictCheckerDispatchTriggerUpgradeProcess");
	}

	@Test
	public void testUpgrade() throws Exception {
		_testUpgradeWithDispatchTaskSettingsColumn();
		_testUpgradeWithoutDispatchTaskSettingsColumn();
	}

	private void _renameColumn(String columnName, String newColumnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DB db = DBManagerUtil.getDB();

			db.alterColumnName(
				connection, "DispatchTrigger", columnName, newColumnName);
		}
	}

	private void _testUpgradeWithDispatchTaskSettingsColumn() throws Exception {
		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, TestPropsValues.getUserId(),
				"scheduled-publications-conflict-checks",
				UnicodePropertiesBuilder.create(
					true
				).put(
					"featureFlagKey", "LPD-11018"
				).build(),
				RandomTestUtil.randomString(), false);

		UnicodeProperties unicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		Assert.assertEquals(
			"LPD-11018", unicodeProperties.getProperty("featureFlagKey"));

		_upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		dispatchTrigger = _dispatchTriggerLocalService.getDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId());

		unicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		Assert.assertNull(unicodeProperties.getProperty("featureFlagKey"));
	}

	private void _testUpgradeWithoutDispatchTaskSettingsColumn()
		throws Exception {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				null, TestPropsValues.getUserId(),
				"scheduled-publications-conflict-checks",
				UnicodePropertiesBuilder.create(
					true
				).put(
					"featureFlagKey", "LPD-11018"
				).build(),
				RandomTestUtil.randomString(), false);

		_renameColumn("dispatchTaskSettings", "taskSettings");

		try {
			_upgradeProcess.upgrade();
		}
		finally {
			_renameColumn("taskSettings", "dispatchTaskSettings");
		}

		CacheRegistryUtil.clear();

		dispatchTrigger = _dispatchTriggerLocalService.getDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId());

		UnicodeProperties unicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		Assert.assertEquals(
			"LPD-11018", unicodeProperties.getProperty("featureFlagKey"));
	}

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "(&(component.name=com.liferay.change.tracking.internal.upgrade.registry.ChangeTrackingServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}