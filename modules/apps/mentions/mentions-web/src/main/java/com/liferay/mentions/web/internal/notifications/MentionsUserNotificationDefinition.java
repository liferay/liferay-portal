/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.notifications;

import com.liferay.mentions.constants.MentionsConstants;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + MentionsPortletKeys.MENTIONS,
	service = UserNotificationDefinition.class
)
public class MentionsUserNotificationDefinition
	extends UserNotificationDefinition {

	public MentionsUserNotificationDefinition() {
		super(
			MentionsPortletKeys.MENTIONS, 0,
			MentionsConstants.NOTIFICATION_TYPE_MENTION,
			"receive-a-notification-when-someone-mentions-you-in-a-blogs-" +
				"entry,-comment,-or-message-boards-message");

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