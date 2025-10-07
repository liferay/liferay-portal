/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.notifications;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(
	property = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
	service = UserNotificationDefinition.class
)
public class DepotEntryAddUserUserNotificationDefinition
	extends UserNotificationDefinition {

	public DepotEntryAddUserUserNotificationDefinition() {
		super(
			DepotPortletKeys.DEPOT_ADMIN, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			"receive-a-notification-when-someone-adds-you-to-an-asset-library");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}