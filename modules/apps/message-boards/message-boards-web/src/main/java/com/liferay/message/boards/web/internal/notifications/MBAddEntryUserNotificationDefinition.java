/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.notifications;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = UserNotificationDefinition.class
)
public class MBAddEntryUserNotificationDefinition
	extends UserNotificationDefinition {

	public MBAddEntryUserNotificationDefinition() {
		super(
			MBPortletKeys.MESSAGE_BOARDS, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			"receive-a-notification-when-someone-adds-a-new-post-in-a-thread-" +
				"or-category-you-are-subscribed-to");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, true,
				true));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}