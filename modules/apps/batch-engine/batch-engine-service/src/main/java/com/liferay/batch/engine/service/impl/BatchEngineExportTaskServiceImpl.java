/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.impl;

import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.base.BatchEngineExportTaskServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.sql.Blob;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"json.web.service.context.name=batchengine",
		"json.web.service.context.path=BatchEngineExportTask"
	},
	service = AopService.class
)
public class BatchEngineExportTaskServiceImpl
	extends BatchEngineExportTaskServiceBaseImpl {

	@Override
	public BatchEngineExportTask addBatchEngineExportTask(
			String externalReferenceCode, long companyId, long userId,
			String callbackURL, String className, String contentType,
			String executeStatus, List<String> fieldNames,
			Map<String, Serializable> parameters, String taskItemDelegateName)
		throws PortalException {

		_checkPermission(companyId);

		return batchEngineExportTaskLocalService.addBatchEngineExportTask(
			externalReferenceCode, companyId, userId, callbackURL, className,
			contentType, executeStatus, fieldNames, parameters,
			taskItemDelegateName);
	}

	@Override
	public BatchEngineExportTask getBatchEngineExportTask(
			long batchEngineExportTaskId)
		throws PortalException {

		BatchEngineExportTask batchEngineExportTask =
			batchEngineExportTaskLocalService.getBatchEngineExportTask(
				batchEngineExportTaskId);

		_checkPermission(batchEngineExportTask);

		return batchEngineExportTask;
	}

	@Override
	public BatchEngineExportTask
			getBatchEngineExportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		_checkPermission(companyId);

		BatchEngineExportTask batchEngineExportTask =
			batchEngineExportTaskLocalService.
				getBatchEngineExportTaskByExternalReferenceCode(
					externalReferenceCode, companyId);

		_checkPermission(batchEngineExportTask);

		return batchEngineExportTask;
	}

	@Override
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
			long companyId, int start, int end)
		throws PortalException {

		_checkPermission(companyId);

		return _filterBatchEngineExportTasks(
			batchEngineExportTaskLocalService.getBatchEngineExportTasks(
				companyId, start, end));
	}

	@Override
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
			long companyId, int start, int end,
			OrderByComparator<BatchEngineExportTask> orderByComparator)
		throws PortalException {

		_checkPermission(companyId);

		return _filterBatchEngineExportTasks(
			batchEngineExportTaskLocalService.getBatchEngineExportTasks(
				companyId, start, end, orderByComparator));
	}

	@Override
	public int getBatchEngineExportTasksCount(long companyId)
		throws PortalException {

		_checkPermission(companyId);

		List<BatchEngineExportTask> filteredBatchEngineExportTasks =
			_filterBatchEngineExportTasks(
				batchEngineExportTaskLocalService.getBatchEngineExportTasks(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		return filteredBatchEngineExportTasks.size();
	}

	@Override
	public InputStream openContentInputStream(long batchEngineExportTaskId)
		throws PortalException {

		BatchEngineExportTask batchEngineExportTask =
			batchEngineExportTaskLocalService.getBatchEngineExportTask(
				batchEngineExportTaskId);

		_checkPermission(batchEngineExportTask);

		Blob blob = batchEngineExportTask.getContent();

		if (blob == null) {
			return new UnsyncByteArrayInputStream(new byte[0]);
		}

		try {
			return blob.getBinaryStream();
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private void _checkPermission(BatchEngineExportTask batchEngineExportTask)
		throws PrincipalException {

		if (!_hasPermission(batchEngineExportTask, getPermissionChecker())) {
			throw new PrincipalException();
		}
	}

	private void _checkPermission(long companyId) throws PrincipalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		if ((companyId != permissionChecker.getCompanyId()) &&
			!permissionChecker.isOmniadmin()) {

			throw new PrincipalException();
		}
	}

	private List<BatchEngineExportTask> _filterBatchEngineExportTasks(
			List<BatchEngineExportTask> batchEngineExportTasks)
		throws PrincipalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		return TransformUtil.transform(
			batchEngineExportTasks,
			batchEngineExportTask -> {
				if (_hasPermission(batchEngineExportTask, permissionChecker)) {
					return batchEngineExportTask;
				}

				return null;
			});
	}

	private boolean _hasPermission(
		BatchEngineExportTask batchEngineExportTask,
		PermissionChecker permissionChecker) {

		if (permissionChecker.isCompanyAdmin(
				batchEngineExportTask.getCompanyId()) ||
			(batchEngineExportTask.getUserId() ==
				permissionChecker.getUserId())) {

			return true;
		}

		return false;
	}

}