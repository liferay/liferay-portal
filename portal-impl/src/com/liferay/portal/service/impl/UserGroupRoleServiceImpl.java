/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.UserGroupRolePermissionUtil;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.portal.service.base.UserGroupRoleServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class UserGroupRoleServiceImpl extends UserGroupRoleServiceBaseImpl {

	@Override
	public void addUserGroupRoles(long userId, long groupId, long[] roleIds)
		throws PortalException {

		List<UserGroupRole> organizationUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> siteUserGroupRoles = new ArrayList<>();

		Group group = _groupLocalService.getGroup(groupId);

		for (long roleId : roleIds) {
			Role role = _rolePersistence.findByPrimaryKey(roleId);

			UserGroupRolePermissionUtil.check(
				getPermissionChecker(), group, role);

			UserGroupRole userGroupRole = userGroupRolePersistence.create(0);

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupId);
			userGroupRole.setRoleId(roleId);

			if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				organizationUserGroupRoles.add(userGroupRole);
			}
			else if (role.getType() == RoleConstants.TYPE_SITE) {
				siteUserGroupRoles.add(userGroupRole);
			}
		}

		if (!siteUserGroupRoles.isEmpty()) {
			SiteMembershipPolicyUtil.checkRoles(siteUserGroupRoles, null);
		}

		if (!organizationUserGroupRoles.isEmpty()) {
			OrganizationMembershipPolicyUtil.checkRoles(
				organizationUserGroupRoles, null);
		}

		userGroupRoleLocalService.addUserGroupRoles(userId, groupId, roleIds);

		if (!siteUserGroupRoles.isEmpty()) {
			SiteMembershipPolicyUtil.propagateRoles(siteUserGroupRoles, null);
		}

		if (!organizationUserGroupRoles.isEmpty()) {
			OrganizationMembershipPolicyUtil.propagateRoles(
				organizationUserGroupRoles, null);
		}
	}

	@Override
	public void addUserGroupRoles(long[] userIds, long groupId, long roleId)
		throws PortalException {

		UserGroupRolePermissionUtil.check(
			getPermissionChecker(), groupId, roleId);

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		for (long userId : userIds) {
			UserGroupRole userGroupRole = userGroupRolePersistence.create(0);

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupId);
			userGroupRole.setRoleId(roleId);

			userGroupRoles.add(userGroupRole);
		}

		if (userGroupRoles.isEmpty()) {
			return;
		}

		Role role = _rolePersistence.findByPrimaryKey(roleId);

		if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
			OrganizationMembershipPolicyUtil.checkRoles(userGroupRoles, null);
		}
		else if (role.getType() == RoleConstants.TYPE_SITE) {
			SiteMembershipPolicyUtil.checkRoles(userGroupRoles, null);
		}

		userGroupRoleLocalService.addUserGroupRoles(userIds, groupId, roleId);

		if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
			OrganizationMembershipPolicyUtil.propagateRoles(
				userGroupRoles, null);
		}
		else if (role.getType() == RoleConstants.TYPE_SITE) {
			SiteMembershipPolicyUtil.propagateRoles(userGroupRoles, null);
		}
	}

	@Override
	public void deleteUserGroupRoles(long userId, long groupId, long[] roleIds)
		throws PortalException {

		List<UserGroupRole> filteredDepotUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> filteredOrganizationUserGroupRoles =
			new ArrayList<>();
		List<UserGroupRole> filteredSiteUserGroupRoles = new ArrayList<>();

		Group group = _groupLocalService.getGroup(groupId);

		for (long roleId : roleIds) {
			Role role = _rolePersistence.findByPrimaryKey(roleId);

			UserGroupRolePermissionUtil.check(
				getPermissionChecker(), group, role);

			UserGroupRole userGroupRole = userGroupRolePersistence.create(0);

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupId);
			userGroupRole.setRoleId(roleId);

			if (role.getType() == RoleConstants.TYPE_DEPOT) {
				filteredDepotUserGroupRoles.add(userGroupRole);
			}
			else if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				if (!OrganizationMembershipPolicyUtil.isRoleProtected(
						getPermissionChecker(), userId,
						group.getOrganizationId(), roleId)) {

					filteredOrganizationUserGroupRoles.add(userGroupRole);
				}
			}
			else if ((role.getType() == RoleConstants.TYPE_SITE) &&
					 !SiteMembershipPolicyUtil.isRoleProtected(
						 getPermissionChecker(), userId, groupId, roleId)) {

				filteredSiteUserGroupRoles.add(userGroupRole);
			}
		}

		if (filteredDepotUserGroupRoles.isEmpty() &&
			filteredOrganizationUserGroupRoles.isEmpty() &&
			filteredSiteUserGroupRoles.isEmpty()) {

			return;
		}

		if (!filteredOrganizationUserGroupRoles.isEmpty()) {
			OrganizationMembershipPolicyUtil.checkRoles(
				null, filteredOrganizationUserGroupRoles);
		}

		if (!filteredSiteUserGroupRoles.isEmpty()) {
			SiteMembershipPolicyUtil.checkRoles(
				null, filteredSiteUserGroupRoles);
		}

		userGroupRoleLocalService.deleteUserGroupRoles(
			userId, groupId, roleIds);

		if (!filteredOrganizationUserGroupRoles.isEmpty()) {
			OrganizationMembershipPolicyUtil.propagateRoles(
				null, filteredOrganizationUserGroupRoles);
		}

		if (!filteredSiteUserGroupRoles.isEmpty()) {
			SiteMembershipPolicyUtil.propagateRoles(
				null, filteredSiteUserGroupRoles);
		}
	}

	@Override
	public void deleteUserGroupRoles(long[] userIds, long groupId, long roleId)
		throws PortalException {

		UserGroupRolePermissionUtil.check(
			getPermissionChecker(), groupId, roleId);

		List<UserGroupRole> filteredUserGroupRoles = new ArrayList<>();

		Role role = _rolePersistence.findByPrimaryKey(roleId);

		for (long userId : userIds) {
			UserGroupRole userGroupRole = userGroupRolePersistence.create(0);

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupId);
			userGroupRole.setRoleId(roleId);

			if (role.getType() == RoleConstants.TYPE_DEPOT) {
				filteredUserGroupRoles.add(userGroupRole);
			}
			else if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				Group group = _groupPersistence.findByPrimaryKey(groupId);

				if (!OrganizationMembershipPolicyUtil.isRoleProtected(
						getPermissionChecker(), userId,
						group.getOrganizationId(), roleId)) {

					filteredUserGroupRoles.add(userGroupRole);
				}
			}
			else if ((role.getType() == RoleConstants.TYPE_SITE) &&
					 !SiteMembershipPolicyUtil.isRoleProtected(
						 getPermissionChecker(), userId, groupId, roleId)) {

				filteredUserGroupRoles.add(userGroupRole);
			}
		}

		if (filteredUserGroupRoles.isEmpty()) {
			return;
		}

		if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
			OrganizationMembershipPolicyUtil.checkRoles(
				null, filteredUserGroupRoles);
		}
		else if (role.getType() == RoleConstants.TYPE_SITE) {
			SiteMembershipPolicyUtil.checkRoles(null, filteredUserGroupRoles);
		}

		userGroupRoleLocalService.deleteUserGroupRoles(
			userIds, groupId, roleId);

		if (role.getType() == RoleConstants.TYPE_SITE) {
			SiteMembershipPolicyUtil.propagateRoles(
				null, filteredUserGroupRoles);
		}
		else if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
			OrganizationMembershipPolicyUtil.propagateRoles(
				null, filteredUserGroupRoles);
		}
	}

	@Override
	public void updateUserGroupRoles(
			long userId, long groupId, long[] addedRoleIds,
			long[] deletedRoleIds)
		throws PortalException {

		addUserGroupRoles(userId, groupId, addedRoleIds);
		deleteUserGroupRoles(userId, groupId, deletedRoleIds);
	}

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

}