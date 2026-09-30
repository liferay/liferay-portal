/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.notifications;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.portal.instances.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;

/**
 * @author Luis Ortiz
 */
public class PortalInstanceNotificationUtil {

	public static void sendUserNotificationEvent(
		long userId, JSONObject payloadJSONObject) {

		if (!BatchEngineThreadLocal.isBatchImportInProcess()) {
			return;
		}

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

}