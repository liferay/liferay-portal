/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.product.navigation.personal.menu;

import com.liferay.notifications.web.internal.constants.NotificationsPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.BasePersonalMenuEntry;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=200",
		"product.navigation.personal.menu.group:Integer=200"
	},
	service = PersonalMenuEntry.class
)
public class NotificationsPersonalMenuEntry extends BasePersonalMenuEntry {

	@Override
	public String getIcon(PortletRequest portletRequest) {
		if (_userNotificationEventLocalService == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		int count =
			_userNotificationEventLocalService.
				getDeliveredArchivedUserNotificationEventsCount(
					themeDisplay.getUserId(),
					UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
					false);

		if (count > 0) {
			return "simple-circle";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getPortletId() {
		return NotificationsPortletKeys.NOTIFICATIONS;
	}

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}