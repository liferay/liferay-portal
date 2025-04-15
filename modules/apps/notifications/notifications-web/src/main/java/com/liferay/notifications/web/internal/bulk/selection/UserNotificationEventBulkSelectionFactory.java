/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.model.UserNotificationEvent",
	service = BulkSelectionFactory.class
)
public class UserNotificationEventBulkSelectionFactory
	implements BulkSelectionFactory<UserNotificationEvent> {

	@Override
	public BulkSelection<UserNotificationEvent> create(
		Map<String, String[]> parameterMap) {

		String actionName = MapUtil.getString(
			parameterMap, ActionRequest.ACTION_NAME);

		boolean selectAll = MapUtil.getBoolean(parameterMap, "selectAll");

		if (actionName.equals("markAllNotificationsAsRead") || selectAll) {
			long userId = MapUtil.getLong(parameterMap, "userId");

			return new UserUserNotificationEventBulkSelection(
				userId, parameterMap, _userNotificationEventLocalService);
		}

		long userNotificationEventId = MapUtil.getLong(
			parameterMap, "userNotificationEventId");

		if (userNotificationEventId > 0) {
			return new SingleUserNotificationEventBulkSelection(
				userNotificationEventId, parameterMap,
				_userNotificationEventLocalService);
		}

		long[] selectedEntryIds = GetterUtil.getLongValues(
			StringUtil.split(
				MapUtil.getString(parameterMap, "selectedEntryIds")));

		if (ArrayUtil.isNotEmpty(selectedEntryIds)) {
			return new MultipleUserNotificationEventBulkSelection(
				selectedEntryIds, parameterMap,
				_userNotificationEventLocalService);
		}

		return new EmptyBulkSelection<>();
	}

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}