/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.configuration.web.internal.notification;

import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;
import com.liferay.scim.configuration.web.internal.constants.ScimWebKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = "jakarta.portlet.name=" + ScimWebKeys.SCIM_CONFIGURATION,
	service = UserNotificationDefinition.class
)
public class ScimUserNotificationDefinition extends UserNotificationDefinition {

	public ScimUserNotificationDefinition() {
		super(
			ScimWebKeys.SCIM_CONFIGURATION, 0,
			MembershipRequestConstants.STATUS_PENDING,
			"receive-a-notification-when-scim-access-token-is-about-to-expire");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, true,
				false));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				false));
	}

}