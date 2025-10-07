/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.email.provider;

import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Christopher Kian
 */
public class UserGroupEmailProvider implements EmailProvider {

	public UserGroupEmailProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String provide(NotificationContext notificationContext, Object value)
		throws PortalException {

		if (value == null) {
			return StringPool.BLANK;
		}

		Set<String> emailAddresses = new HashSet<>();

		for (Map<String, String> userGroupMap :
				(List<Map<String, String>>)value) {

			UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
				notificationContext.getCompanyId(),
				userGroupMap.get(
					NotificationRecipientSettingConstants.
						NAME_USER_GROUP_NAME));

			if (userGroup == null) {
				continue;
			}

			ListUtil.isNotEmptyForEach(
				_userLocalService.getUserGroupUsers(userGroup.getUserGroupId()),
				user -> {
					if (!ModelResourcePermissionUtil.contains(
							_permissionCheckerFactory.create(user),
							notificationContext.getGroupId(),
							notificationContext.getClassName(),
							notificationContext.getClassPK(),
							ActionKeys.VIEW)) {

						return;
					}

					emailAddresses.add(user.getEmailAddress());
				});
		}

		return StringUtil.merge(emailAddresses);
	}

	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}