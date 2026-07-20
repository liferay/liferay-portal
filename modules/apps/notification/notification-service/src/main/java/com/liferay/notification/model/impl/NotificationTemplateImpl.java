/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.model.impl;

import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.service.NotificationRecipientLocalServiceUtil;

/**
 * @author Gabriel Albuquerque
 */
public class NotificationTemplateImpl extends NotificationTemplateBaseImpl {

	@Override
	public NotificationRecipient getNotificationRecipient() {
		return NotificationRecipientLocalServiceUtil.
			fetchNotificationRecipientByClassPK(getNotificationTemplateId());
	}

}