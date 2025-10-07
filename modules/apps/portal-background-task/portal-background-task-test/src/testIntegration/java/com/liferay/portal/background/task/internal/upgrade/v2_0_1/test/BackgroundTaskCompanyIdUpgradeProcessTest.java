/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.upgrade.v2_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class BackgroundTaskCompanyIdUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.portal.background.task.internal.upgrade.v2_0_1." +
				"BackgroundTaskCompanyIdUpgradeProcess");
	}

	@Test
	public void testUpgrade() throws Exception {
		_backgroundTask = _backgroundTaskLocalService.createBackgroundTask(
			_counterLocalService.increment());

		_backgroundTask.setTaskContextMap(_getTaskContextMap(true));

		_backgroundTask = _backgroundTaskLocalService.updateBackgroundTask(
			_backgroundTask);

		Map<String, Serializable> taskContextMap =
			_backgroundTask.getTaskContextMap();

		Assert.assertTrue(
			Objects.equals(_getTaskContextMap(true), taskContextMap));

		_upgradeProcess.upgrade();

		_entityCache.clearCache();

		_backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
			_backgroundTask.getBackgroundTaskId());

		taskContextMap = _backgroundTask.getTaskContextMap();

		Assert.assertTrue(
			Objects.equals(_getTaskContextMap(false), taskContextMap));
	}

	@Test
	public void testUpgradeWithEmptyTaskContextMap() throws Exception {
		_backgroundTask = _backgroundTaskLocalService.createBackgroundTask(
			_counterLocalService.increment());

		_backgroundTask.setTaskContextMap(new HashMap<>());

		_backgroundTask = _backgroundTaskLocalService.updateBackgroundTask(
			_backgroundTask);

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			"update BackgroundTask set taskContextMap = '{}' where " +
				"backgroundTaskId = " + _backgroundTask.getBackgroundTaskId());

		_upgradeProcess.upgrade();

		_entityCache.clearCache();

		_backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
			_backgroundTask.getBackgroundTaskId());

		Assert.assertNotNull(_backgroundTask.getTaskContextMap());
	}

	private Map<String, Serializable> _getTaskContextMap(boolean addCompanyId) {
		return HashMapBuilder.<String, Serializable>put(
			"companyId",
			() -> {
				if (addCompanyId) {
					return TestPropsValues.getCompanyId();
				}

				return null;
			}
		).put(
			"threadLocalValues",
			HashMapBuilder.<String, Serializable>put(
				"clusterInvoke", true
			).put(
				"companyId",
				() -> {
					if (addCompanyId) {
						return TestPropsValues.getCompanyId();
					}

					return null;
				}
			).put(
				"defaultLocale", LocaleUtil.US
			).put(
				"groupId", 0
			).put(
				"principalName", "test"
			).put(
				"siteDefaultLocale", LocaleUtil.CANADA
			).put(
				"themeDisplayLocale", LocaleUtil.FRANCE
			).build()
		).build();
	}

	private static UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "component.name=com.liferay.portal.background.task.internal.upgrade.registry.BackgroundTaskServiceUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@DeleteAfterTestRun
	private BackgroundTask _backgroundTask;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private EntityCache _entityCache;

}