/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Matthew Kong
 */
public class FaroPermissionChecker {

	public static boolean hasFaroChannelAccessPermission(
		FaroChannel faroChannel) {

		if ((faroChannel.getPermissionType() ==
				FaroChannelConstants.PERMISSION_ALL_USERS) ||
			isGroupAdmin(faroChannel.getWorkspaceGroupId())) {

			return true;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return ListUtil.isNotEmpty(
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				permissionChecker.getUserId(), faroChannel.getGroupId()));
	}

	public static boolean isGroupAdmin(long groupId) {
		return _hasPermission(
			groupId, PermissionThreadLocal.getPermissionChecker(),
			RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_OWNER);
	}

	public static boolean isGroupMember(long groupId) {
		return isGroupMember(
			groupId, PermissionThreadLocal.getPermissionChecker());
	}

	public static boolean isGroupMember(
		long groupId, PermissionChecker permissionChecker) {

		return _hasPermission(
			groupId, permissionChecker, RoleConstants.SITE_ADMINISTRATOR,
			RoleConstants.SITE_MEMBER, RoleConstants.SITE_OWNER);
	}

	public static boolean isGroupOwner(long groupId) {
		return _hasPermission(
			groupId, PermissionThreadLocal.getPermissionChecker(),
			RoleConstants.SITE_OWNER);
	}

	private static boolean _hasPermission(
		long groupId, PermissionChecker permissionChecker,
		String... roleNames) {

		if (permissionChecker.isOmniadmin()) {
			return true;
		}

		FaroUser faroUser = FaroUserLocalServiceUtil.fetchFaroUser(
			groupId, permissionChecker.getUserId());

		if ((faroUser == null) ||
			(faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED)) {

			return false;
		}

		Role role = RoleLocalServiceUtil.fetchRole(faroUser.getRoleId());

		if (role == null) {
			return false;
		}

		for (String roleName : roleNames) {
			if (StringUtil.equals(role.getName(), roleName)) {
				return true;
			}
		}

		return false;
	}

}