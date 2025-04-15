/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.notifications;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
	service = UserNotificationDefinition.class
)
public class AssetPublisherUserNotificationDefinition
	extends UserNotificationDefinition {

	public AssetPublisherUserNotificationDefinition() {
		super(
			AssetPublisherPortletKeys.ASSET_PUBLISHER, 0,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			"receive-a-notification-when-someone-adds-a-new-asset-entry-in-" +
				"an-asset-publisher-you-are-subscribed-to");

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