/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.notifications.web.internal.constants.NotificationsPortletKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + NotificationsPortletKeys.NOTIFICATIONS,
		"panel.app.order:Integer=400",
		"panel.category.key=" + PanelCategoryKeys.USER_MY_ACCOUNT
	},
	service = PanelApp.class
)
public class NotificationsPanelApp extends BasePanelApp {

	@Override
	public int getNotificationsCount(User user) {
		if (_userNotificationEventLocalService == null) {
			return 0;
		}

		return _userNotificationEventLocalService.
			getDeliveredArchivedUserNotificationEventsCount(
				user.getUserId(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE, true, false);
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return NotificationsPortletKeys.NOTIFICATIONS;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + NotificationsPortletKeys.NOTIFICATIONS + ")"
	)
	private Portlet _portlet;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}