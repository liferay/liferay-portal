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
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;

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
		Exception exception1, Object item, String message) {

		if (!(item instanceof PortalInstance)) {
			return;
		}

		String operationType = _getOperationType(batchEngineImportTask);

		if (operationType == null) {
			return;
		}

		PortalInstance portalInstance = (PortalInstance)item;

		try {
			_userNotificationEventLocalService.sendUserNotificationEvents(
				batchEngineImportTask.getUserId(),
				PortalInstancesPortletKeys.PORTAL_INSTANCES,
				UserNotificationDeliveryConstants.TYPE_WEBSITE,
				JSONUtil.put(
					"errorMessageKey", _getErrorMessageKey(exception1)
				).put(
					"operationType", operationType
				).put(
					"portalInstanceId", portalInstance.getPortalInstanceId()
				).put(
					"status", PortalInstancesNotificationConstants.STATUS_FAILED
				));
		}
		catch (Exception exception2) {
			_log.error(
				"Unable to send the user notification event for portal " +
					"instance " + portalInstance.getPortalInstanceId(),
				exception2);
		}
	}

	private String _getErrorMessageKey(Exception exception) {
		if (exception instanceof CompanyMaxUsersException) {
			return "please-enter-a-valid-max-users";
		}

		if (exception instanceof CompanyMxException) {
			return "please-enter-a-valid-mail-domain";
		}

		if (exception instanceof CompanyVirtualHostException) {
			return "please-enter-a-valid-virtual-host";
		}

		if (exception instanceof CompanyWebIdException) {
			return "please-enter-a-valid-web-id";
		}

		if (exception instanceof ContactNameException.MustHaveFirstName) {
			return "please-enter-a-valid-first-name";
		}

		if (exception instanceof ContactNameException.MustHaveLastName) {
			return "please-enter-a-valid-last-name";
		}

		if (exception instanceof ContactNameException.MustHaveMiddleName) {
			return "please-enter-a-valid-middle-name";
		}

		if (exception instanceof ContactNameException.MustHaveValidFullName) {
			return "please-enter-a-valid-first-middle-and-last-name";
		}

		if (exception instanceof RequiredCompanyException) {
			return "the-default-instance-cannot-be-deleted";
		}

		if (exception instanceof UserEmailAddressException) {
			return "please-enter-a-valid-email-address";
		}

		if (exception instanceof UserPasswordException) {
			return "please-enter-a-valid-password";
		}

		if (exception instanceof UserScreenNameException) {
			return "please-enter-a-valid-screen-name";
		}

		return "an-unexpected-error-occurred";
	}

	private String _getOperationType(
		BatchEngineImportTask batchEngineImportTask) {

		String operation = batchEngineImportTask.getOperation();

		if (Objects.equals(operation, BatchEngineTaskOperation.CREATE.name())) {
			return PortalInstancesNotificationConstants.OPERATION_TYPE_ADD;
		}

		if (Objects.equals(operation, BatchEngineTaskOperation.DELETE.name())) {
			return PortalInstancesNotificationConstants.OPERATION_TYPE_DELETE;
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceBatchEngineImportTaskExceptionHandler.class);

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}