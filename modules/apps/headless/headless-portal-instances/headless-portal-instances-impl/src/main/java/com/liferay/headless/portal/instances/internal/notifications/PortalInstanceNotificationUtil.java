/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.notifications;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.portal.instances.constants.PortalInstancesNotificationConstants;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luis Ortiz
 */
public class PortalInstanceNotificationUtil {

	public static void addPortalInstance(
		long batchEngineImportTaskId, Object item,
		PortalInstance portalInstance) {

		Map<Object, PortalInstance> portalInstances =
			_portalInstancesMap.computeIfAbsent(
				batchEngineImportTaskId, key -> new IdentityHashMap<>());

		portalInstances.put(item, portalInstance);
	}

	public static void removePortalInstance(
		long batchEngineImportTaskId, Object item) {

		Map<Object, PortalInstance> portalInstances = _portalInstancesMap.get(
			batchEngineImportTaskId);

		if (portalInstances != null) {
			portalInstances.remove(item);
		}
	}

	public static Collection<PortalInstance> removePortalInstances(
		long batchEngineImportTaskId) {

		Map<Object, PortalInstance> portalInstances =
			_portalInstancesMap.remove(batchEngineImportTaskId);

		if (portalInstances == null) {
			return Collections.emptyList();
		}

		return portalInstances.values();
	}

	public static void sendAddUserNotificationEvent(
		boolean initialized, String portalInstanceId, long userId) {

		JSONObject payloadJSONObject = JSONUtil.put(
			"operationType",
			PortalInstancesNotificationConstants.OPERATION_TYPE_ADD
		).put(
			"portalInstanceId", portalInstanceId
		);

		if (initialized) {
			payloadJSONObject.put(
				"status", PortalInstancesNotificationConstants.STATUS_SUCCESS);
		}
		else {
			payloadJSONObject.put(
				"errorMessageKey",
				"the-instance-was-created-but-its-initialization-failed-" +
					"check-the-server-logs"
			).put(
				"status", PortalInstancesNotificationConstants.STATUS_FAILED
			);
		}

		_sendUserNotificationEvent(payloadJSONObject, userId);
	}

	public static void sendUserNotificationEvent(
		JSONObject payloadJSONObject, long userId) {

		if (!BatchEngineThreadLocal.isBatchImportInProcess()) {
			return;
		}

		_sendUserNotificationEvent(payloadJSONObject, userId);
	}

	private static void _sendUserNotificationEvent(
		JSONObject payloadJSONObject, long userId) {

		try {
			UserNotificationEventLocalServiceUtil.sendUserNotificationEvents(
				userId, PortalInstancesPortletKeys.PORTAL_INSTANCES,
				UserNotificationDeliveryConstants.TYPE_WEBSITE,
				payloadJSONObject);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to send the user notification event for portal " +
					"instance " +
						payloadJSONObject.getString("portalInstanceId"),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceNotificationUtil.class);

	private static final Map<Long, Map<Object, PortalInstance>>
		_portalInstancesMap = new ConcurrentHashMap<>();

}