/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.test.util;

import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.service.persistence.NotificationRecipientSettingUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;

/**
 * @author Shuyang Zhou
 */
public class NotificationRecipientSettingTestUtil {

	public static NotificationRecipientSetting
			fetchNotificationRecipientSetting(
				long notificationRecipientId, String name)
		throws Exception {

		return TransactionTestUtil.invoke(
			NotificationRecipientSettingUtil.getPersistence(),
			notificationRecipientSettingPersistence ->
				notificationRecipientSettingPersistence.fetchByNRI_N(
					notificationRecipientId, name));
	}

}