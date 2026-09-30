/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.scheduler;

import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	configurationPid = "com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration",
	service = SchedulerJobConfiguration.class
)
public class BatchEngineTaskCleanerSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			_deleteCompletedBatchEngineExportTasks();
			_deleteCompletedBatchEngineImportTasks();
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
			batchEngineTaskConfiguration.completedTasksCleanerScanInterval(),
			TimeUnit.DAY);
	}

	private void _deleteCompletedBatchEngineExportTasks()
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_batchEngineExportTaskLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"executeStatus",
					BatchEngineTaskExecuteStatus.COMPLETED.toString())));
		actionableDynamicQuery.setPerformActionMethod(
			(BatchEngineExportTask batchEngineExportTask) ->
				_batchEngineExportTaskLocalService.deleteBatchEngineExportTask(
					batchEngineExportTask.getBatchEngineExportTaskId()));

		actionableDynamicQuery.performActions();
	}

	private void _deleteCompletedBatchEngineImportTasks()
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_batchEngineImportTaskLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"executeStatus",
					BatchEngineTaskExecuteStatus.COMPLETED.toString())));
		actionableDynamicQuery.setPerformActionMethod(
			(BatchEngineImportTask batchEngineImportTask) ->
				_batchEngineImportTaskLocalService.deleteBatchEngineImportTask(
					batchEngineImportTask.getBatchEngineImportTaskId()));

		actionableDynamicQuery.performActions();
	}

	@Reference
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Reference
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	private TriggerConfiguration _triggerConfiguration;

}