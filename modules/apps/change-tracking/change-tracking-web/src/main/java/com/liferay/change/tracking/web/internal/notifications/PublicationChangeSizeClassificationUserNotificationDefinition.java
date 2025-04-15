/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.notifications;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gislayne Vitorino
 */
@Component(
	property = "jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
	service = UserNotificationDefinition.class
)
public class PublicationChangeSizeClassificationUserNotificationDefinition
	extends UserNotificationDefinition {

	public PublicationChangeSizeClassificationUserNotificationDefinition() {
		super(
			CTPortletKeys.PUBLICATIONS, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
			"receive-a-notification-when-a-publication-changes-size-" +
				"classification");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}