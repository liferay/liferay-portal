/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.notifications;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.internal.constants.RemoteInstanceConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class RemoteImportUserNotificationUtil {

	public static void sendUserNotificationEvent(ObjectEntry objectEntry)
		throws Exception {

		if (objectEntry == null) {
			return;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		UserNotificationEventLocalServiceUtil.sendUserNotificationEvents(
			objectEntry.getUserId(), ExportImportPortletKeys.EXPORT_IMPORT,
			UserNotificationDeliveryConstants.TYPE_WEBSITE,
			JSONUtil.put(
				"remoteImport", true
			).put(
				"remoteImportProcessId",
				MapUtil.getLong(values, "remoteImportProcessId")
			).put(
				"remoteImportURL", MapUtil.getString(values, "remoteImportURL")
			).put(
				"remoteInstanceName",
				_getRemoteInstanceName(
					MapUtil.getLong(
						values,
						RemoteInstanceConstants.
							OBJECT_FIELD_NAME_REMOTE_INSTANCE_ID))
			).put(
				"status", MapUtil.getInteger(values, "importStatus")
			));
	}

	private static String _getRemoteInstanceName(long remoteInstanceId) {
		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			remoteInstanceId);

		if (objectEntry == null) {
			return StringPool.BLANK;
		}

		return MapUtil.getString(objectEntry.getValues(), "name");
	}

}