/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Caio Farias
 */
public class UserGroupUsersProvider implements UsersProvider {

	public UserGroupUsersProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_USER_GROUP;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		Set<User> users = new LinkedHashSet<>();

		for (String value : values) {
			UserGroup userGroup = _userGroupLocalService.getUserGroup(
				notificationContext.getCompanyId(), value);

			users.addAll(
				_userLocalService.getUserGroupUsers(
					userGroup.getUserGroupId()));
		}

		return ListUtil.filter(
			new ArrayList<>(users),
			user -> ModelResourcePermissionUtil.contains(
				_permissionCheckerFactory.create(user),
				notificationContext.getGroupId(),
				notificationContext.getClassName(),
				notificationContext.getClassPK(), ActionKeys.VIEW));
	}

	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}