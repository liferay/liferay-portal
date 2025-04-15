/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.internal.resource.v1_0;

import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.ItemClassRegistry;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskService;
import com.liferay.headless.batch.engine.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.internal.resource.v1_0.util.ParametersUtil;
import com.liferay.headless.batch.engine.resource.v1_0.ExportTaskResource;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Ivica Cardic
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/export-task.properties",
	property = "batch.engine=true", scope = ServiceScope.PROTOTYPE,
	service = ExportTaskResource.class
)
public class ExportTaskResourceImpl extends BaseExportTaskResourceImpl {

	@Override
	public ExportTask getExportTask(Long exportTaskId) throws Exception {
		return _toExportTask(
			_batchEngineExportTaskService.getBatchEngineExportTask(
				exportTaskId));
	}

	@Override
	public ExportTask getExportTaskByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		return _toExportTask(
			_batchEngineExportTaskService.
				getBatchEngineExportTaskByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Response getExportTaskByExternalReferenceCodeContent(
			String externalReferenceCode)
		throws Exception {

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskService.
				getBatchEngineExportTaskByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return _getExportTaskContent(batchEngineExportTask);
	}

	@Override
	public Response getExportTaskContent(Long exportTaskId) throws Exception {
		return _getExportTaskContent(
			_batchEngineExportTaskService.getBatchEngineExportTask(
				exportTaskId));
	}

	@Override
	public ExportTask postExportTask(
			String className, String contentType, String batchNestedFields,
			String callbackURL, String externalReferenceCode, String fieldNames,
			String taskItemDelegateName)
		throws Exception {

		Class<?> clazz = _itemClassRegistry.getItemClass(className);

		if (clazz == null) {
			throw new IllegalArgumentException(
				"Unknown class name: " + className);
		}

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				ExportTaskResourceImpl.class.getName());

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskService.addBatchEngineExportTask(
				externalReferenceCode, contextCompany.getCompanyId(),
				contextUser.getUserId(), callbackURL, className,
				StringUtil.upperCase(contentType),
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				_toList(fieldNames),
				ParametersUtil.toParameters(contextUriInfo, _ignoredParameters),
				taskItemDelegateName);

		executorService.submit(
			() -> _batchEngineExportTaskExecutor.execute(
				batchEngineExportTask));

		return _toExportTask(batchEngineExportTask);
	}

	private Response _getExportTaskContent(
			BatchEngineExportTask batchEngineExportTask)
		throws Exception {

		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus =
			BatchEngineTaskExecuteStatus.valueOf(
				batchEngineExportTask.getExecuteStatus());

		if (batchEngineTaskExecuteStatus ==
				BatchEngineTaskExecuteStatus.COMPLETED) {

			InputStream contentInputStream =
				_batchEngineExportTaskService.openContentInputStream(
					batchEngineExportTask.getBatchEngineExportTaskId());

			StreamingOutput streamingOutput =
				outputStream -> StreamUtil.transfer(
					contentInputStream, outputStream);

			return Response.ok(
				streamingOutput
			).header(
				"content-disposition",
				"attachment; filename=" + StringUtil.randomString() + ".zip"
			).build();
		}

		return Response.status(
			Response.Status.NOT_FOUND
		).build();
	}

	private ExportTask _toExportTask(
		BatchEngineExportTask batchEngineExportTask) {

		return new ExportTask() {
			{
				setClassName(batchEngineExportTask::getClassName);
				setContentType(batchEngineExportTask::getContentType);
				setEndTime(batchEngineExportTask::getEndTime);
				setErrorMessage(batchEngineExportTask::getErrorMessage);
				setExecuteStatus(
					() -> ExportTask.ExecuteStatus.create(
						batchEngineExportTask.getExecuteStatus()));
				setExternalReferenceCode(
					batchEngineExportTask::getExternalReferenceCode);
				setId(batchEngineExportTask::getBatchEngineExportTaskId);
				setProcessedItemsCount(
					batchEngineExportTask::getProcessedItemsCount);
				setStartTime(batchEngineExportTask::getStartTime);
				setTotalItemsCount(batchEngineExportTask::getTotalItemsCount);
			}
		};
	}

	private List<String> _toList(String fieldNamesString) {
		if (Validator.isNull(fieldNamesString)) {
			return Collections.emptyList();
		}

		return Arrays.asList(StringUtil.split(fieldNamesString, ','));
	}

	private static final Set<String> _ignoredParameters = new HashSet<>(
		Arrays.asList("callbackURL", "fieldNames", "taskItemDelegateName"));

	@Reference
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Reference
	private BatchEngineExportTaskService _batchEngineExportTaskService;

	@Reference
	private ItemClassRegistry _itemClassRegistry;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

}