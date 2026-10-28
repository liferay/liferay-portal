/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.batch.engine.exception.handler;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceCopy;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstanceImport;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(service = BatchEngineImportTaskExceptionHandler.class)
public class PortalInstanceBatchEngineImportTaskExceptionHandler
	implements BatchEngineImportTaskExceptionHandler {

	@Override
	public void handle(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
		Exception exception, Object item, String message) {

		String operationType = _getOperationType(batchEngineImportTask, item);

		if (operationType == null) {
			return;
		}

		PortalInstanceNotificationUtil.removePortalInstance(
			batchEngineImportTask.getBatchEngineImportTaskId(), item);

		String portalInstanceId = _getPortalInstanceId(item);

		PortalInstanceNotificationUtil.sendUserNotificationEvent(
			JSONUtil.put(
				"errorMessage", message
			).put(
				"errorMessageKey", _getErrorMessageKey(exception, operationType)
			).put(
				"operationType", operationType
			).put(
				"portalInstanceId", portalInstanceId
			).put(
				"schemaName",
				_getSchemaName(item, operationType, portalInstanceId)
			).put(
				"sourcePortalInstanceId", _getSourcePortalInstanceId(item)
			).put(
				"status", PortalInstancesNotificationConstants.STATUS_FAILED
			),
			batchEngineImportTask.getUserId());
	}

	private String _getCopyErrorMessageKey(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.endsWith(" is the default company ID")) {
				return "the-default-instance-cannot-be-copied";
			}

			return "please-enter-a-valid-destination-company-id";
		}

		if (exception instanceof UnsupportedOperationException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.equals("Copying an instance is already in progress")) {
				return "copying-an-instance-is-already-in-progress";
			}

			if (message.equals("Database partitioning must be enabled")) {
				return "database-partitioning-must-be-enabled";
			}

			return null;
		}

		Throwable throwable = exception.getCause();

		if ((exception instanceof CompanyNameException) ||
			(throwable instanceof CompanyNameException)) {

			return "please-enter-a-valid-name";
		}

		if ((exception instanceof CompanyVirtualHostException) ||
			(throwable instanceof CompanyVirtualHostException)) {

			return "please-enter-a-valid-virtual-host";
		}

		if ((exception instanceof CompanyWebIdException) ||
			(throwable instanceof CompanyWebIdException)) {

			return "please-enter-a-valid-web-id";
		}

		return null;
	}

	private String _getErrorMessageKey(
		Exception exception, String operationType) {

		if (Objects.equals(
				operationType,
				PortalInstancesNotificationConstants.OPERATION_TYPE_COPY)) {

			return _getCopyErrorMessageKey(exception);
		}

		if (Objects.equals(
				operationType,
				PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT)) {

			return _getExportErrorMessageKey(exception);
		}

		if (Objects.equals(
				operationType,
				PortalInstancesNotificationConstants.OPERATION_TYPE_IMPORT)) {

			return _getImportErrorMessageKey(exception);
		}

		Throwable throwable = exception;

		if (Objects.equals(exception.getClass(), PortalException.class) &&
			(exception.getCause() != null)) {

			throwable = exception.getCause();
		}

		if (throwable instanceof CompanyMaxUsersException) {
			return "please-enter-a-valid-max-users";
		}

		if (throwable instanceof CompanyMxException) {
			return "please-enter-a-valid-mail-domain";
		}

		if (throwable instanceof CompanyVirtualHostException) {
			return "please-enter-a-valid-virtual-host";
		}

		if (throwable instanceof CompanyWebIdException) {
			return "please-enter-a-valid-web-id";
		}

		if (throwable instanceof ContactNameException.MustHaveFirstName) {
			return "please-enter-a-valid-first-name";
		}

		if (throwable instanceof ContactNameException.MustHaveLastName) {
			return "please-enter-a-valid-last-name";
		}

		if (throwable instanceof ContactNameException.MustHaveMiddleName) {
			return "please-enter-a-valid-middle-name";
		}

		if (throwable instanceof ContactNameException.MustHaveValidFullName) {
			return "please-enter-a-valid-first-middle-and-last-name";
		}

		if (throwable instanceof IllegalArgumentException) {
			String message = GetterUtil.getString(throwable.getMessage());

			if (message.startsWith("Site initializer ")) {
				return "please-select-a-valid-virtual-instance-initializer";
			}

			return null;
		}

		if (throwable instanceof RequiredCompanyException) {
			return "the-default-instance-cannot-be-deleted";
		}

		if (throwable instanceof UserEmailAddressException) {
			return "please-enter-a-valid-email-address";
		}

		if (throwable instanceof UserPasswordException) {
			return "please-enter-a-valid-password";
		}

		if (throwable instanceof UserScreenNameException) {
			return "please-enter-a-valid-screen-name";
		}

		return null;
	}

	private String _getExportErrorMessageKey(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			return "the-exported-schema-x-already-exists";
		}

		if (exception instanceof RequiredCompanyException) {
			return "the-default-instance-cannot-be-exported";
		}

		return null;
	}

	private String _getImportErrorMessageKey(Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.startsWith("Database partition ")) {
				return "an-instance-for-this-schema-already-exists";
			}

			if (message.startsWith("Invalid schema name ") ||
				message.endsWith(" is the default company ID")) {

				return "please-enter-a-valid-schema-name";
			}

			if (message.startsWith(
					"Unable to insert the database partition ")) {

				return "the-exported-schema-does-not-exist";
			}

			return null;
		}

		if (exception instanceof UnsupportedOperationException) {
			String message = GetterUtil.getString(exception.getMessage());

			if (message.equals(
					"Importing an instance is already in progress")) {

				return "importing-an-instance-is-already-in-progress";
			}

			if (message.equals("Database partitioning must be enabled")) {
				return "database-partitioning-must-be-enabled";
			}

			return null;
		}

		Throwable throwable = exception.getCause();

		if ((exception instanceof CompanyNameException) ||
			(throwable instanceof CompanyNameException)) {

			return "please-enter-a-valid-name";
		}

		if ((exception instanceof CompanyVirtualHostException) ||
			(throwable instanceof CompanyVirtualHostException)) {

			return "please-enter-a-valid-virtual-host";
		}

		if ((exception instanceof CompanyWebIdException) ||
			(throwable instanceof CompanyWebIdException)) {

			return "please-enter-a-valid-web-id";
		}

		return null;
	}

	private String _getOperationType(
		BatchEngineImportTask batchEngineImportTask, Object item) {

		String operation = batchEngineImportTask.getOperation();

		if (Objects.equals(operation, BatchEngineTaskOperation.CREATE.name())) {
			if (item instanceof PortalInstance) {
				return PortalInstancesNotificationConstants.OPERATION_TYPE_ADD;
			}

			if (item instanceof PortalInstanceCopy) {
				return PortalInstancesNotificationConstants.OPERATION_TYPE_COPY;
			}

			if (item instanceof PortalInstanceExport) {
				return PortalInstancesNotificationConstants.
					OPERATION_TYPE_EXPORT;
			}

			if (item instanceof PortalInstanceImport) {
				return PortalInstancesNotificationConstants.
					OPERATION_TYPE_IMPORT;
			}

			return null;
		}

		if (Objects.equals(operation, BatchEngineTaskOperation.DELETE.name()) &&
			(item instanceof PortalInstance)) {

			return PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE;
		}

		return null;
	}

	private String _getPortalInstanceId(Object item) {
		if (item instanceof PortalInstanceCopy) {
			PortalInstanceCopy portalInstanceCopy = (PortalInstanceCopy)item;

			return portalInstanceCopy.getWebId();
		}

		if (item instanceof PortalInstanceExport) {
			PortalInstanceExport portalInstanceExport =
				(PortalInstanceExport)item;

			return portalInstanceExport.getPortalInstanceId();
		}

		if (item instanceof PortalInstanceImport) {
			PortalInstanceImport portalInstanceImport =
				(PortalInstanceImport)item;

			return portalInstanceImport.getWebId();
		}

		PortalInstance portalInstance = (PortalInstance)item;

		return portalInstance.getPortalInstanceId();
	}

	private String _getSchemaName(
		Object item, String operationType, String portalInstanceId) {

		if (item instanceof PortalInstanceImport) {
			PortalInstanceImport portalInstanceImport =
				(PortalInstanceImport)item;

			return portalInstanceImport.getSchemaName();
		}

		if (!Objects.equals(
				operationType,
				PortalInstancesNotificationConstants.OPERATION_TYPE_EXPORT)) {

			return null;
		}

		try {
			Company company = _companyLocalService.getCompanyByWebId(
				portalInstanceId);

			return DBPartitionUtil.getExportedPartitionName(
				company.getCompanyId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private String _getSourcePortalInstanceId(Object item) {
		if (item instanceof PortalInstanceCopy) {
			PortalInstanceCopy portalInstanceCopy = (PortalInstanceCopy)item;

			return portalInstanceCopy.getSourcePortalInstanceId();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceBatchEngineImportTaskExceptionHandler.class);

	@Reference
	private CompanyLocalService _companyLocalService;

}