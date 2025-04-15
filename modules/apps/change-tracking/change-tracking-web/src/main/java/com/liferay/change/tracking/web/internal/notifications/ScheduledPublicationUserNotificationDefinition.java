/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.notifications;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brooke Dalton
 */
@Component(
	property = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
	service = UserNotificationDefinition.class
)
public class ScheduledPublicationUserNotificationDefinition
	extends UserNotificationDefinition {

	public ScheduledPublicationUserNotificationDefinition() {
		super(
			CTPortletKeys.PUBLICATIONS, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY,
			"receive-a-notification-when-a-scheduled-publication-fails");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, false,
				false));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}