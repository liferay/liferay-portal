/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.impl;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.batch.engine.exception.BatchEngineImportTaskParametersException;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.base.BatchEngineImportTaskLocalServiceBaseImpl;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskErrorPersistence;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.batch.engine.model.BatchEngineImportTask",
	service = AopService.class
)
@CTAware
public class BatchEngineImportTaskLocalServiceImpl
	extends BatchEngineImportTaskLocalServiceBaseImpl {

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BatchEngineImportTask addBatchEngineImportTask(
			String externalReferenceCode, long companyId, long userId,
			long batchSize, String callbackURL, String className,
			byte[] content, String contentType, String executeStatus,
			Map<String, String> fieldNameMappingMap, int importStrategy,
			String operation, Map<String, Serializable> parameters,
			String taskItemDelegateName)
		throws PortalException {

		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate =
			_batchEngineTaskItemDelegateRegistry.getBatchEngineTaskItemDelegate(
				companyId, className, taskItemDelegateName);

		return addBatchEngineImportTask(
			externalReferenceCode, companyId, userId, batchSize, callbackURL,
			className, content, contentType, executeStatus, fieldNameMappingMap,
			importStrategy, operation, parameters, taskItemDelegateName,
			batchEngineTaskItemDelegate);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BatchEngineImportTask addBatchEngineImportTask(
			String externalReferenceCode, long companyId, long userId,
			long batchSize, String callbackURL, String className,
			byte[] content, String contentType, String executeStatus,
			Map<String, String> fieldNameMappingMap, int importStrategy,
			String operation, Map<String, Serializable> parameters,
			String taskItemDelegateName,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate)
		throws PortalException {

		if ((parameters != null) && !parameters.isEmpty()) {
			_validateDelimiter(
				(String)parameters.getOrDefault("delimiter", null));
			_validateEnclosingCharacter(
				(String)parameters.getOrDefault("enclosingCharacter", null));
			_validateStrategies(
				batchEngineTaskItemDelegate,
				(String)parameters.getOrDefault("createStrategy", null),
				(String)parameters.getOrDefault("updateStrategy", null));
		}

		BatchEngineImportTask batchEngineImportTask =
			batchEngineImportTaskPersistence.create(
				counterLocalService.increment(
					BatchEngineImportTask.class.getName()));

		batchEngineImportTask.setExternalReferenceCode(externalReferenceCode);
		batchEngineImportTask.setCompanyId(companyId);
		batchEngineImportTask.setUserId(userId);
		batchEngineImportTask.setBatchSize(batchSize);
		batchEngineImportTask.setCallbackURL(callbackURL);
		batchEngineImportTask.setClassName(className);
		batchEngineImportTask.setContent(
			new OutputBlob(
				new UnsyncByteArrayInputStream(content), content.length));
		batchEngineImportTask.setContentType(contentType);
		batchEngineImportTask.setExecuteStatus(executeStatus);

		if ((fieldNameMappingMap != null) && !fieldNameMappingMap.isEmpty()) {
			batchEngineImportTask.setFieldNameMapping((Map)fieldNameMappingMap);
		}

		batchEngineImportTask.setImportStrategy(importStrategy);
		batchEngineImportTask.setOperation(operation);
		batchEngineImportTask.setParameters(parameters);
		batchEngineImportTask.setTaskItemDelegateName(taskItemDelegateName);

		return batchEngineImportTaskPersistence.update(batchEngineImportTask);
	}

	@Override
	public BatchEngineImportTask deleteBatchEngineImportTask(
			long batchEngineImportTaskId)
		throws PortalException {

		_batchEngineImportTaskErrorPersistence.removeByBatchEngineImportTaskId(
			batchEngineImportTaskId);

		return batchEngineImportTaskPersistence.remove(batchEngineImportTaskId);
	}

	@Override
	public List<BatchEngineImportTask> getBatchEngineImportTasks(
		long companyId, int start, int end) {

		return batchEngineImportTaskPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public List<BatchEngineImportTask> getBatchEngineImportTasks(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return batchEngineImportTaskPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<BatchEngineImportTask> getBatchEngineImportTasks(
		String executeStatus) {

		return batchEngineImportTaskPersistence.findByExecuteStatus(
			executeStatus);
	}

	@Override
	public int getBatchEngineImportTasksCount(long companyId) {
		return batchEngineImportTaskPersistence.countByCompanyId(companyId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BatchEngineImportTask updateBatchEngineImportTask(
		BatchEngineImportTask batchEngineImportTask) {

		return super.updateBatchEngineImportTask(batchEngineImportTask);
	}

	private void _validateDelimiter(String delimiter)
		throws BatchEngineImportTaskParametersException {

		if (Validator.isNull(delimiter)) {
			return;
		}

		if (_INVALID_ENCLOSING_CHARACTERS.contains(delimiter)) {
			throw new BatchEngineImportTaskParametersException(
				"Illegal delimiter value " + delimiter);
		}
	}

	private void _validateEnclosingCharacter(String enclosingCharacter)
		throws BatchEngineImportTaskParametersException {

		if (Validator.isNull(enclosingCharacter)) {
			return;
		}

		if (!_INVALID_ENCLOSING_CHARACTERS.contains(enclosingCharacter)) {
			throw new BatchEngineImportTaskParametersException(
				"Illegal enclosing character value " + enclosingCharacter);
		}
	}

	private void _validateStrategies(
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			String createStrategy, String updateStrategy)
		throws BatchEngineImportTaskParametersException {

		if (Validator.isNotNull(createStrategy) &&
			!batchEngineTaskItemDelegate.hasCreateStrategy(createStrategy)) {

			throw new BatchEngineImportTaskParametersException(
				"Illegal create strategy " + createStrategy);
		}

		if (Validator.isNotNull(updateStrategy) &&
			!batchEngineTaskItemDelegate.hasUpdateStrategy(updateStrategy)) {

			throw new BatchEngineImportTaskParametersException(
				"Illegal update strategy " + updateStrategy);
		}
	}

	private static final String _INVALID_ENCLOSING_CHARACTERS =
		StringPool.APOSTROPHE + StringPool.QUOTE;

	@Reference
	private BatchEngineImportTaskErrorPersistence
		_batchEngineImportTaskErrorPersistence;

	@Reference
	private BatchEngineTaskItemDelegateRegistry
		_batchEngineTaskItemDelegateRegistry;

}