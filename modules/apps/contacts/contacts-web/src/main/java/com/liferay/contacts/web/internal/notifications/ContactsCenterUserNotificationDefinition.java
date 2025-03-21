/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.notifications;

import com.liferay.contacts.constants.SocialRelationConstants;
import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
	service = UserNotificationDefinition.class
)
@Deprecated
public class ContactsCenterUserNotificationDefinition
	extends UserNotificationDefinition {

	public ContactsCenterUserNotificationDefinition() {
		super(
			ContactsPortletKeys.CONTACTS_CENTER, 0,
			SocialRelationConstants.SOCIAL_RELATION_REQUEST,
			"receive-a-notification-when-someone-sends-you-a-social-" +
				"relationship-request");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}