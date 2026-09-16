/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.internal.security.permission.LiberalPermissionChecker;
import com.liferay.batch.engine.internal.util.ItemIndexThreadLocal;
import com.liferay.batch.engine.jaxrs.uri.BatchEngineUriInfo;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Ivica Cardic
 */
public class BatchEngineTaskExecutorUtil {

	public static <T> T execute(
			boolean checkPermissions,
			UnsafeSupplier<T, Throwable> unsafeSupplier, User user)
		throws Throwable {

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		auditRequestThreadLocal.setRealUserEmailAddress(user.getEmailAddress());
		auditRequestThreadLocal.setRealUserId(user.getUserId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (checkPermissions) {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
		}
		else {
			PermissionThreadLocal.setPermissionChecker(
				new LiberalPermissionChecker(user));
		}

		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(user.getUserId());

		try (SafeCloseable safeCloseable =
				ItemIndexThreadLocal.pushIndexQueueWithSafeCloseable()) {

			return unsafeSupplier.get();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			PrincipalThreadLocal.setName(name);
		}
	}

	public static void sendBatchProgressMessage(
		BackgroundTaskStatusMessageSender backgroundTaskStatusMessageSender,
		int processedItemsCount) {

		if (backgroundTaskStatusMessageSender == null) {
			return;
		}

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		message.put("batchEngineProcessedItemsCount", processedItemsCount);
		message.put("messageType", "batchProgress");

		backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);
	}

	public static void setContextFields(
			long companyId,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			Map<String, Serializable> parameters, User user)
		throws PortalException {

		batchEngineTaskItemDelegate.setContextCompany(
			CompanyLocalServiceUtil.getCompany(companyId));

		BatchEngineUriInfo.Builder builder = new BatchEngineUriInfo.Builder();

		for (Map.Entry<String, Serializable> entry : parameters.entrySet()) {
			builder.queryParameter(
				entry.getKey(), String.valueOf(entry.getValue()));
		}

		batchEngineTaskItemDelegate.setContextUriInfo(builder.build());

		batchEngineTaskItemDelegate.setContextUser(user);
		batchEngineTaskItemDelegate.setLanguageId(user.getLanguageId());
	}

}