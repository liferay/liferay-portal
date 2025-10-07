/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.impl;

import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.base.BatchEngineExportTaskLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ivica Cardic
 */
@Component(
	property = "model.class.name=com.liferay.batch.engine.model.BatchEngineExportTask",
	service = AopService.class
)
public class BatchEngineExportTaskLocalServiceImpl
	extends BatchEngineExportTaskLocalServiceBaseImpl {

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BatchEngineExportTask addBatchEngineExportTask(
		String externalReferenceCode, long companyId, long userId,
		String callbackURL, String className, String contentType,
		String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName) {

		BatchEngineExportTask batchEngineExportTask =
			createBatchEngineExportTask(
				counterLocalService.increment(
					BatchEngineExportTask.class.getName()),
				externalReferenceCode, companyId, userId, callbackURL,
				className, contentType, executeStatus, fieldNames, parameters,
				taskItemDelegateName);

		return batchEngineExportTaskPersistence.update(batchEngineExportTask);
	}

	@Override
	public BatchEngineExportTask createBatchEngineExportTask(
		long batchEngineExportTaskId, String externalReferenceCode,
		long companyId, long userId, String callbackURL, String className,
		String contentType, String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName) {

		BatchEngineExportTask batchEngineExportTask =
			batchEngineExportTaskPersistence.create(batchEngineExportTaskId);

		batchEngineExportTask.setExternalReferenceCode(externalReferenceCode);
		batchEngineExportTask.setCompanyId(companyId);
		batchEngineExportTask.setUserId(userId);
		batchEngineExportTask.setCallbackURL(callbackURL);
		batchEngineExportTask.setClassName(className);
		batchEngineExportTask.setContent(
			new OutputBlob(new UnsyncByteArrayInputStream(new byte[0]), 0));
		batchEngineExportTask.setContentType(contentType);
		batchEngineExportTask.setExecuteStatus(executeStatus);
		batchEngineExportTask.setFieldNamesList(fieldNames);
		batchEngineExportTask.setParameters(parameters);
		batchEngineExportTask.setTaskItemDelegateName(taskItemDelegateName);

		return batchEngineExportTask;
	}

	@Override
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end) {

		return batchEngineExportTaskPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return batchEngineExportTaskPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		String executeStatus) {

		return batchEngineExportTaskPersistence.findByExecuteStatus(
			executeStatus);
	}

	@Override
	public int getBatchEngineExportTasksCount(long companyId) {
		return batchEngineExportTaskPersistence.countByCompanyId(companyId);
	}

}