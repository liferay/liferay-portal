/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.internal.test.BlogPosting;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class BatchEngineTaskCleanerSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetJobExecutorUnsafeRunnable() throws Exception {
		List<BatchEngineExportTask> completedBatchEngineExportTasks =
			new ArrayList<>();
		List<BatchEngineImportTask> completedBatchEngineImportTasks =
			new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			completedBatchEngineExportTasks.add(
				_addBatchEngineExportTask(
					BatchEngineTaskExecuteStatus.COMPLETED));

			BatchEngineImportTask batchEngineImportTask =
				_addBatchEngineImportTask(
					BatchEngineTaskExecuteStatus.COMPLETED);

			_batchEngineImportTaskErrorLocalService.
				addBatchEngineImportTaskError(
					batchEngineImportTask.getCompanyId(),
					batchEngineImportTask.getUserId(),
					batchEngineImportTask.getBatchEngineImportTaskId(), null,
					RandomTestUtil.randomInt(), RandomTestUtil.randomString());

			completedBatchEngineImportTasks.add(batchEngineImportTask);
		}

		for (BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus :
				BatchEngineTaskExecuteStatus.values()) {

			if (batchEngineTaskExecuteStatus ==
					BatchEngineTaskExecuteStatus.COMPLETED) {

				continue;
			}

			_batchEngineExportTasks.add(
				_addBatchEngineExportTask(batchEngineTaskExecuteStatus));
			_batchEngineImportTasks.add(
				_addBatchEngineImportTask(batchEngineTaskExecuteStatus));
		}

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		for (BatchEngineExportTask batchEngineExportTask :
				completedBatchEngineExportTasks) {

			Assert.assertNull(
				_batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
					batchEngineExportTask.getBatchEngineExportTaskId()));
		}

		for (BatchEngineImportTask batchEngineImportTask :
				completedBatchEngineImportTasks) {

			Assert.assertNull(
				_batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
					batchEngineImportTask.getBatchEngineImportTaskId()));
			Assert.assertEquals(
				0,
				_batchEngineImportTaskErrorLocalService.
					getBatchEngineImportTaskErrorsCount(
						batchEngineImportTask.getBatchEngineImportTaskId()));
		}

		for (BatchEngineExportTask batchEngineExportTask :
				_batchEngineExportTasks) {

			Assert.assertNotNull(
				_batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
					batchEngineExportTask.getBatchEngineExportTaskId()));
		}

		for (BatchEngineImportTask batchEngineImportTask :
				_batchEngineImportTasks) {

			Assert.assertNotNull(
				_batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
					batchEngineImportTask.getBatchEngineImportTaskId()));
		}
	}

	private BatchEngineExportTask _addBatchEngineExportTask(
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus)
		throws Exception {

		return _batchEngineExportTaskLocalService.addBatchEngineExportTask(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			null, BlogPosting.class.getName(), "JSON",
			batchEngineTaskExecuteStatus.name(), null, new HashMap<>(), null);
	}

	private BatchEngineImportTask _addBatchEngineImportTask(
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus)
		throws Exception {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			10, null, BlogPosting.class.getName(), new byte[0], "JSON",
			batchEngineTaskExecuteStatus.name(), null,
			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
			BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@DeleteAfterTestRun
	private final List<BatchEngineExportTask> _batchEngineExportTasks =
		new ArrayList<>();

	@Inject
	private BatchEngineImportTaskErrorLocalService
		_batchEngineImportTaskErrorLocalService;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@DeleteAfterTestRun
	private final List<BatchEngineImportTask> _batchEngineImportTasks =
		new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.batch.engine.internal.scheduler.BatchEngineTaskCleanerSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}