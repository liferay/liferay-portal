/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.notifications;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
	service = UserNotificationHandler.class
)
public class BookmarksUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public BookmarksUserNotificationHandler() {
		setPortletId(BookmarksPortletKeys.BOOKMARKS);
	}

}