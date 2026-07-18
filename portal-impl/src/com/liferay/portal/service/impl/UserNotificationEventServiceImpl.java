/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.service.base.UserNotificationEventServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class UserNotificationEventServiceImpl
	extends UserNotificationEventServiceBaseImpl {

	@Override
	public UserNotificationEvent getUserNotificationEvent(
			long userNotificationEventId)
		throws PortalException {

		UserNotificationEvent userNotificationEvent =
			userNotificationEventPersistence.findByPrimaryKey(
				userNotificationEventId);

		UserPermissionUtil.check(
			getPermissionChecker(), userNotificationEvent.getUserId(),
			ActionKeys.VIEW);

		return userNotificationEvent;
	}

	@Override
	public UserNotificationEvent updateUserNotificationEvent(
			String uuid, long companyId, boolean archive)
		throws PortalException {

		UserNotificationEvent userNotificationEvent =
			userNotificationEventLocalService.
				getUserNotificationEventByUuidAndCompanyId(uuid, companyId);

		UserPermissionUtil.check(
			getPermissionChecker(), userNotificationEvent.getUserId(),
			ActionKeys.UPDATE);

		return userNotificationEventLocalService.updateUserNotificationEvent(
			uuid, companyId, archive);
	}

}