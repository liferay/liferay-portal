/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.internal.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.site.dsr.site.initializer.constants.DSRRoleConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Matyas Wollner
 */
public class DSRRoleUtil {

	public static String getHighestRoleName(
			long groupId, UserGroupRoleLocalService userGroupRoleLocalService,
			long userId)
		throws PortalException {

		List<String> roleNames = TransformUtil.transform(
			userGroupRoleLocalService.getUserGroupRoles(userId, groupId),
			userGroupRole -> {
				Role role = userGroupRole.getRole();

				return role.getName();
			});

		for (String roleName : _ROLE_NAMES_BY_RANK) {
			if (roleNames.contains(roleName)) {
				return roleName;
			}
		}

		return RoleConstants.SITE_MEMBER;
	}

	public static boolean isManageableRoleName(
		String managingRoleName, String roleName) {

		Set<String> manageableRoleNames = _manageableRoleNamesMap.get(
			managingRoleName);

		if (manageableRoleNames == null) {
			return false;
		}

		return manageableRoleNames.contains(roleName);
	}

	private static final String[] _ROLE_NAMES_BY_RANK = {
		RoleConstants.SITE_OWNER, RoleConstants.SITE_ADMINISTRATOR,
		DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
		DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR, RoleConstants.SITE_MEMBER
	};

	private static final Map<String, Set<String>> _manageableRoleNamesMap =
		HashMapBuilder.<String, Set<String>>put(
			DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
			SetUtil.fromArray(RoleConstants.SITE_MEMBER)
		).put(
			DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
			SetUtil.fromArray(
				DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
				RoleConstants.SITE_MEMBER)
		).put(
			RoleConstants.SITE_ADMINISTRATOR,
			SetUtil.fromArray(
				DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
				DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
				RoleConstants.SITE_MEMBER)
		).put(
			RoleConstants.SITE_OWNER,
			SetUtil.fromArray(
				DSRRoleConstants.NAME_DSR_CONTENT_CONTRIBUTOR,
				DSRRoleConstants.NAME_DSR_ROOM_COLLABORATOR,
				RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_MEMBER)
		).build();

}