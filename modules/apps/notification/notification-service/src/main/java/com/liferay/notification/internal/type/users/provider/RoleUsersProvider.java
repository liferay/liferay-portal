/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.users.provider;

import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRoleModel;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Feliphe Marinho
 */
public class RoleUsersProvider implements UsersProvider {

	public RoleUsersProvider(
		PermissionCheckerFactory permissionCheckerFactory,
		RoleLocalService roleLocalService,
		UserGroupRoleLocalService userGroupRoleLocalService,
		UserLocalService userLocalService) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_roleLocalService = roleLocalService;
		_userGroupRoleLocalService = userGroupRoleLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public String getRecipientType() {
		return NotificationRecipientConstants.TYPE_ROLE;
	}

	@Override
	public List<User> provide(
			NotificationContext notificationContext, List<String> values)
		throws PortalException {

		Set<Long> userIds = new LinkedHashSet<>();

		for (String value : values) {
			Role role = _roleLocalService.getRole(
				notificationContext.getCompanyId(), value);

			if ((role.getType() == RoleConstants.TYPE_ORGANIZATION) ||
				(role.getType() == RoleConstants.TYPE_SITE)) {

				userIds.addAll(
					ListUtil.toList(
						_userGroupRoleLocalService.
							getUserGroupRolesByGroupAndRole(
								notificationContext.getGroupId(),
								role.getRoleId()),
						UserGroupRoleModel::getUserId));

				continue;
			}

			for (User user :
					_userLocalService.getInheritedRoleUsers(
						role.getRoleId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						null)) {

				userIds.add(user.getUserId());
			}
		}

		return TransformUtil.unsafeTransform(
			userIds,
			userId -> {
				User user = _userLocalService.getUser(userId);

				if (!ModelResourcePermissionUtil.contains(
						_permissionCheckerFactory.create(user),
						notificationContext.getGroupId(),
						notificationContext.getClassName(),
						notificationContext.getClassPK(), ActionKeys.VIEW)) {

					return null;
				}

				return user;
			});
	}

	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final RoleLocalService _roleLocalService;
	private final UserGroupRoleLocalService _userGroupRoleLocalService;
	private final UserLocalService _userLocalService;

}