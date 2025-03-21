/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.web.internal.notifications;

import com.liferay.invitation.invite.members.constants.InviteMembersPortletKeys;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + InviteMembersPortletKeys.INVITE_MEMBERS,
	service = UserNotificationDefinition.class
)
public class InviteMembersUserNotificationDefinition
	extends UserNotificationDefinition {

	public InviteMembersUserNotificationDefinition() {
		super(
			InviteMembersPortletKeys.INVITE_MEMBERS, 0,
			MembershipRequestConstants.STATUS_PENDING,
			"receive-a-notification-when-someone-sends-you-a-membership-" +
				"request");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}