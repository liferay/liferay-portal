/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.upgrade.v4_6_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskPersistence;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class DeleteStaleBatchEngineDataUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, companyId, TestPropsValues.getUserId(), null,
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount", "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyList(),
				HashMapBuilder.<String, Serializable>put(
					"siteId", TestPropsValues.getGroupId()
				).build(),
				null);

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, companyId, TestPropsValues.getUserId(), 10, null,
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount",
				new byte[0], "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);

		BatchEngineImportTaskError batchEngineImportTaskError =
			_batchEngineImportTaskErrorLocalService.
				addBatchEngineImportTaskError(
					batchEngineImportTask.getCompanyId(),
					batchEngineImportTask.getUserId(),
					batchEngineImportTask.getBatchEngineImportTaskId(), null,
					RandomTestUtil.randomInt(), "This is just an error");

		_runUpgrade();

		Assert.assertNull(
			_batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
				batchEngineExportTask.getBatchEngineExportTaskId()));
		Assert.assertEquals(
			0,
			_batchEngineExportTaskLocalService.getBatchEngineExportTasksCount(
				companyId));

		Assert.assertNull(
			_batchEngineImportTaskErrorLocalService.
				fetchBatchEngineImportTaskError(
					batchEngineImportTaskError.
						getBatchEngineImportTaskErrorId()));
		Assert.assertEquals(
			0,
			_batchEngineImportTaskErrorLocalService.
				getBatchEngineImportTaskErrorsCount(
					batchEngineImportTask.getBatchEngineImportTaskId()));

		Assert.assertNull(
			_batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
				batchEngineImportTask.getBatchEngineImportTaskId()));
		Assert.assertEquals(
			0,
			_batchEngineImportTaskLocalService.getBatchEngineImportTasksCount(
				companyId));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(4, 6, 4));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Inject
	private BatchEngineImportTaskErrorLocalService
		_batchEngineImportTaskErrorLocalService;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private BatchEngineImportTaskPersistence _batchEngineImportTaskPersistence;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.batch.engine.internal.upgrade.registry.BatchEngineServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}