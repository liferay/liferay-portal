/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.web.internal.notifications;

import com.liferay.flags.web.internal.constants.FlagsPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + FlagsPortletKeys.FLAGS,
	service = UserNotificationDefinition.class
)
public class FlagsAddEntryUserNotificationDefinition
	extends UserNotificationDefinition {

	public FlagsAddEntryUserNotificationDefinition() {
		super(
			FlagsPortletKeys.FLAGS, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			"receive-a-notification-when-someone-adds-a-new-flag-in-a-site-" +
				"where-you-are-an-administrator");

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