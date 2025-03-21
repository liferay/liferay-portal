/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.notification.web.internal.display.context.helper.NotificationRequestHelper;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Paulo Albuquerque
 */
public class ViewNotificationQueueEntriesDisplayContext {

	public ViewNotificationQueueEntriesDisplayContext(
		HttpServletRequest httpServletRequest) {

		_notificationRequestHelper = new NotificationRequestHelper(
			httpServletRequest);
	}

	public String getAPIURL() {
		return "/o/notification/v1.0/notification-queue-entries";
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return Arrays.asList(
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}/resend", null, "put",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "resend"),
				"put", "update", "async"),
			new FDSActionDropdownItem(
				getAPIURL() + "/{id}", "trash", "delete",
				LanguageUtil.get(
					_notificationRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	private final NotificationRequestHelper _notificationRequestHelper;

}