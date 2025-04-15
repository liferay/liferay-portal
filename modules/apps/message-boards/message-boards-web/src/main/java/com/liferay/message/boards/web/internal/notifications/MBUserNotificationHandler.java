/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.notifications;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = UserNotificationHandler.class
)
public class MBUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public MBUserNotificationHandler() {
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
	}

}