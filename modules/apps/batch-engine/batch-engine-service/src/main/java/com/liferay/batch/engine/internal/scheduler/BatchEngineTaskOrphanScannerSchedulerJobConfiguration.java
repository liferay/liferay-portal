/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.scheduler;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.Time;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	configurationPid = "com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration",
	service = SchedulerJobConfiguration.class
)
public class BatchEngineTaskOrphanScannerSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			NoticeableExecutorService noticeableExecutorService =
				_portalExecutorManager.getPortalExecutor(
					BatchEngineTaskOrphanScannerSchedulerJobConfiguration.class.
						getName());

			long time = System.currentTimeMillis();

			for (BatchEngineExportTask batchEngineExportTask :
					_batchEngineExportTaskLocalService.
						getBatchEngineExportTasks(
							BatchEngineTaskExecuteStatus.STARTED.toString())) {

				Date modifiedDate = batchEngineExportTask.getModifiedDate();

				if ((time - modifiedDate.getTime()) <= _orphanageThreshold) {
					continue;
				}

				noticeableExecutorService.submit(
					() -> _batchEngineExportTaskExecutor.execute(
						batchEngineExportTask));
			}

			for (BatchEngineImportTask batchEngineImportTask :
					_batchEngineImportTaskLocalService.
						getBatchEngineImportTasks(
							BatchEngineTaskExecuteStatus.STARTED.toString())) {

				Date modifiedDate = batchEngineImportTask.getModifiedDate();

				if ((time - modifiedDate.getTime()) <= _orphanageThreshold) {
					continue;
				}

				noticeableExecutorService.submit(
					() -> _batchEngineImportTaskExecutor.execute(
						batchEngineImportTask));
			}
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		BatchEngineTaskConfiguration batchEngineTaskConfiguration =
			ConfigurableUtil.createConfigurable(
				BatchEngineTaskConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			batchEngineTaskConfiguration.orphanScanInterval(), TimeUnit.MINUTE);

		_orphanageThreshold =
			batchEngineTaskConfiguration.orphanageThreshold() * Time.MINUTE;
	}

	@Deactivate
	protected void deactivate() {
		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				BatchEngineTaskOrphanScannerSchedulerJobConfiguration.class.
					getName(),
				false);

		if (executorService != null) {
			executorService.shutdownNow();
		}
	}

	@Reference
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Reference
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Reference
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Reference
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	private long _orphanageThreshold;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private TriggerConfiguration _triggerConfiguration;

}