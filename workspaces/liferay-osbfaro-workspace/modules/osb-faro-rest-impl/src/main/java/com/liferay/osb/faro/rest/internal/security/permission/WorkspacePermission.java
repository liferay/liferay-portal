/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.security.permission;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroChannelTable;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = WorkspacePermission.class)
public class WorkspacePermission {

	public List<String> getInaccessibleChannelIds(
		long groupId, PermissionChecker permissionChecker) {

		if (permissionChecker.isOmniadmin() ||
			_hasWorkspaceRole(
				groupId, permissionChecker,
				_WORKSPACE_ADMINISTRATOR_ROLE_NAMES)) {

			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_faroChannelLocalService.<List<FaroChannel>>dslQuery(
				DSLQueryFactoryUtil.select(
					FaroChannelTable.INSTANCE
				).from(
					FaroChannelTable.INSTANCE
				).where(
					FaroChannelTable.INSTANCE.workspaceGroupId.eq(
						groupId
					).and(
						FaroChannelTable.INSTANCE.permissionType.eq(
							FaroChannelConstants.PERMISSION_SELECT_USERS)
					)
				)),
			faroChannel -> {
				if (hasChannelPermission(faroChannel, permissionChecker)) {
					return null;
				}

				return faroChannel.getChannelId();
			});
	}

	public boolean hasChannelPermission(
		FaroChannel faroChannel, PermissionChecker permissionChecker) {

		if ((faroChannel.getPermissionType() ==
				FaroChannelConstants.PERMISSION_ALL_USERS) ||
			permissionChecker.isOmniadmin() ||
			_hasWorkspaceRole(
				faroChannel.getWorkspaceGroupId(), permissionChecker,
				_WORKSPACE_ADMINISTRATOR_ROLE_NAMES)) {

			return true;
		}

		return ListUtil.isNotEmpty(
			_userGroupRoleLocalService.getUserGroupRoles(
				permissionChecker.getUserId(), faroChannel.getGroupId()));
	}

	public boolean isWorkspaceMember(
		long groupId, PermissionChecker permissionChecker) {

		return _hasWorkspaceRole(
			groupId, permissionChecker, _WORKSPACE_MEMBER_ROLE_NAMES);
	}

	private boolean _hasWorkspaceRole(
		long groupId, PermissionChecker permissionChecker, String[] roleNames) {

		FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
			groupId, permissionChecker.getUserId());

		if ((faroUser == null) ||
			(faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED)) {

			return false;
		}

		Role role = _roleLocalService.fetchRole(faroUser.getRoleId());

		if (role == null) {
			return false;
		}

		return ArrayUtil.contains(roleNames, role.getName());
	}

	private static final String[] _WORKSPACE_ADMINISTRATOR_ROLE_NAMES = {
		RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_OWNER
	};

	private static final String[] _WORKSPACE_MEMBER_ROLE_NAMES = {
		RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_MEMBER,
		RoleConstants.SITE_OWNER
	};

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}