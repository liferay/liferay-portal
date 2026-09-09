/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.security.permission.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;

import java.util.Map;

/**
 * @author Stefano Motta
 */
public class DSRRoleAssignmentPermissionUtil {

	public static void checkPermission(Group group, String roleKey, long userId)
		throws PortalException {

		if (Validator.isNull(roleKey)) {
			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(group.getGroupId()) ||
			permissionChecker.isGroupOwner(group.getGroupId())) {

			return;
		}

		Integer rolePriority = _rolePrioritiesMap.get(roleKey);

		if ((rolePriority == null) ||
			(rolePriority > getRolePriority(group.getGroupId(), userId))) {

			throw new RoleAssignmentException(
				"you-do-not-have-permission-to-assign-this-role");
		}
	}

	public static int getRolePriority(long groupId, long userId)
		throws PortalException {

		int rolePriority = 0;

		for (UserGroupRole userGroupRole :
				UserGroupRoleLocalServiceUtil.getUserGroupRoles(
					userId, groupId)) {

			Role role = userGroupRole.getRole();

			rolePriority = Math.max(
				rolePriority,
				_rolePrioritiesMap.getOrDefault(role.getName(), 0));
		}

		return rolePriority;
	}

	private static final Map<String, Integer> _rolePrioritiesMap =
		HashMapBuilder.put(
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR, 1
		).put(
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR, 2
		).put(
			RoleConstants.SITE_ADMINISTRATOR, 3
		).put(
			RoleConstants.SITE_MEMBER, 0
		).put(
			RoleConstants.SITE_OWNER, 4
		).build();

}