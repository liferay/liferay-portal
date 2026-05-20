/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.internal.test.BlogPosting;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies that concurrent updates to batch engine tasks do not deadlock in
 * HSQLDB. Without REQUIRES_NEW propagation on the update methods, two threads
 * sharing a transaction context would deadlock on HSQLDB's table-level locks.
 *
 * @author Magdalena Jedraszak
 */
@RunWith(Arquillian.class)
public class BatchEngineTaskDeadlockTest
	extends BaseBatchEngineTaskServiceTest {

	@Test
	public void testConcurrentExportTaskUpdatesDoNotDeadlock()
		throws Exception {

		BatchEngineExportTask exportTask1 =
			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, omniadminUser.getCompanyId(), omniadminUser.getUserId(),
				null, BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Arrays.asList("headline", "id"), null, null);

		BatchEngineExportTask exportTask2 =
			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, omniadminUser.getCompanyId(), omniadminUser.getUserId(),
				null, BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Arrays.asList("headline", "id"), null, null);

		exportTask1.setExecuteStatus(
			BatchEngineTaskExecuteStatus.COMPLETED.name());
		exportTask2.setExecuteStatus(
			BatchEngineTaskExecuteStatus.COMPLETED.name());

		_assertConcurrentUpdatesSucceed(
			() ->
				_batchEngineExportTaskLocalService.updateBatchEngineExportTask(
					exportTask1),
			() ->
				_batchEngineExportTaskLocalService.updateBatchEngineExportTask(
					exportTask2));
	}

	@Test
	public void testConcurrentImportTaskUpdatesDoNotDeadlock()
		throws Exception {

		BatchEngineImportTask importTask1 =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, omniadminUser.getCompanyId(), omniadminUser.getUserId(),
				10, null, BlogPosting.class.getName(), new byte[0], "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);

		BatchEngineImportTask importTask2 =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, omniadminUser.getCompanyId(), omniadminUser.getUserId(),
				10, null, BlogPosting.class.getName(), new byte[0], "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);

		importTask1.setExecuteStatus(
			BatchEngineTaskExecuteStatus.COMPLETED.name());
		importTask2.setExecuteStatus(
			BatchEngineTaskExecuteStatus.COMPLETED.name());

		_assertConcurrentUpdatesSucceed(
			() ->
				_batchEngineImportTaskLocalService.updateBatchEngineImportTask(
					importTask1),
			() ->
				_batchEngineImportTaskLocalService.updateBatchEngineImportTask(
					importTask2));
	}

	private void _assertConcurrentUpdatesSucceed(
			CheckedRunnable runnable1, CheckedRunnable runnable2)
		throws Exception {

		CountDownLatch startLatch = new CountDownLatch(1);
		AtomicReference<Exception> exceptionReference = new AtomicReference<>();

		ExecutorService executorService = Executors.newFixedThreadPool(2);

		Future<?> future1 = executorService.submit(
			() -> {
				try {
					startLatch.await();

					runnable1.run();
				}
				catch (Exception exception) {
					exceptionReference.compareAndSet(null, exception);
				}
			});

		Future<?> future2 = executorService.submit(
			() -> {
				try {
					startLatch.await();

					runnable2.run();
				}
				catch (Exception exception) {
					exceptionReference.compareAndSet(null, exception);
				}
			});

		startLatch.countDown();

		future1.get(10, TimeUnit.SECONDS);
		future2.get(10, TimeUnit.SECONDS);

		executorService.shutdown();

		Assert.assertNull(exceptionReference.get());
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@FunctionalInterface
	private interface CheckedRunnable {

		public void run() throws Exception;

	}

}