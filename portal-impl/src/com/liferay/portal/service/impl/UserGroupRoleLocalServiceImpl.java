/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupGroupRoleTable;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.service.base.UserGroupRoleLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Ferrer
 */
public class UserGroupRoleLocalServiceImpl
	extends UserGroupRoleLocalServiceBaseImpl {

	@Override
	public UserGroupRole addUserGroupRole(
		long userId, long groupId, long roleId) {

		UserGroupRole userGroupRole = userGroupRolePersistence.fetchByU_G_R(
			userId, groupId, roleId);

		if (userGroupRole == null) {
			userGroupRole = userGroupRolePersistence.create(
				counterLocalService.increment(UserGroupRole.class.getName()));

			userGroupRole.setUserId(userId);
			userGroupRole.setGroupId(groupId);
			userGroupRole.setRoleId(roleId);

			userGroupRole = userGroupRolePersistence.update(userGroupRole);
		}

		return userGroupRole;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public UserGroupRole addUserGroupRole(UserGroupRole userGroupRole) {
		if (userGroupRole.getUserGroupRoleId() == 0) {
			userGroupRole.setUserGroupRoleId(
				counterLocalService.increment(UserGroupRole.class.getName()));
		}

		userGroupRole.setNew(true);

		return userGroupRolePersistence.update(userGroupRole);
	}

	@Override
	public List<UserGroupRole> addUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		for (long roleId : roleIds) {
			UserGroupRole userGroupRole = addUserGroupRole(
				userId, groupId, roleId);

			userGroupRoles.add(userGroupRole);
		}

		Group group = _groupPersistence.fetchByPrimaryKey(groupId);

		if (group.isRegularSite() &&
			_groupPersistence.addUser(groupId, userId)) {

			_reindex(new long[] {userId});
		}

		return userGroupRoles;
	}

	@Override
	public List<UserGroupRole> addUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		for (long userId : userIds) {
			UserGroupRole userGroupRole = addUserGroupRole(
				userId, groupId, roleId);

			userGroupRoles.add(userGroupRole);
		}

		Group group = _groupPersistence.fetchByPrimaryKey(groupId);

		if (group.isRegularSite() &&
			_groupPersistence.addUsers(groupId, userIds)) {

			_reindex(userIds);
		}

		return userGroupRoles;
	}

	@Override
	public void deleteUserGroupRoles(long groupId, int roleType) {
		List<UserGroupRole> userGroupRoles =
			userGroupRoleFinder.findByGroupRoleType(groupId, roleType);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			userGroupRolePersistence.removeByG_R(
				groupId, userGroupRole.getRoleId());
		}
	}

	@Override
	public void deleteUserGroupRoles(
		long userId, long groupId, long[] roleIds) {

		for (long roleId : roleIds) {
			UserGroupRole userGroupRole = userGroupRolePersistence.fetchByU_G_R(
				userId, groupId, roleId);

			if (userGroupRole != null) {
				userGroupRolePersistence.remove(userGroupRole);
			}
		}
	}

	@Override
	public void deleteUserGroupRoles(long userId, long[] groupIds) {
		for (long groupId : groupIds) {
			userGroupRolePersistence.removeByU_G(userId, groupId);
		}
	}

	@Override
	public void deleteUserGroupRoles(long[] userIds, long groupId) {
		for (long userId : userIds) {
			userGroupRolePersistence.removeByU_G(userId, groupId);
		}
	}

	@Override
	public void deleteUserGroupRoles(
		long[] userIds, long groupId, int roleType) {

		List<Role> roles = _rolePersistence.findByT_S(
			roleType, StringPool.BLANK);

		for (long userId : userIds) {
			for (Role role : roles) {
				UserGroupRole userGroupRole =
					userGroupRolePersistence.fetchByU_G_R(
						userId, groupId, role.getRoleId());

				if (userGroupRole != null) {
					userGroupRolePersistence.remove(userGroupRole);
				}
			}
		}
	}

	@Override
	public void deleteUserGroupRoles(
		long[] userIds, long groupId, long roleId) {

		for (long userId : userIds) {
			UserGroupRole userGroupRole = userGroupRolePersistence.fetchByU_G_R(
				userId, groupId, roleId);

			if (userGroupRole != null) {
				userGroupRolePersistence.remove(userGroupRole);
			}
		}
	}

	@Override
	public void deleteUserGroupRolesByGroupId(long groupId) {
		userGroupRolePersistence.removeByGroupId(groupId);
	}

	@Override
	public void deleteUserGroupRolesByRoleId(long roleId) {
		userGroupRolePersistence.removeByRoleId(roleId);
	}

	@Override
	public void deleteUserGroupRolesByUserId(long userId) {
		userGroupRolePersistence.removeByUserId(userId);
	}

	@Override
	public UserGroupRole fetchUserGroupRole(
		long userId, long groupId, long roleId) {

		return userGroupRolePersistence.fetchByU_G_R(userId, groupId, roleId);
	}

	@Override
	public List<UserGroupRole> getUserGroupRoles(long userId) {
		return userGroupRolePersistence.findByUserId(userId);
	}

	@Override
	public List<UserGroupRole> getUserGroupRoles(long userId, long groupId) {
		return userGroupRolePersistence.findByU_G(userId, groupId);
	}

	@Override
	public List<UserGroupRole> getUserGroupRoles(
		long userId, long groupId, int start, int end) {

		return userGroupRolePersistence.findByU_G(userId, groupId, start, end);
	}

	@Override
	public List<UserGroupRole> getUserGroupRolesByGroup(long groupId) {
		return userGroupRolePersistence.findByGroupId(groupId);
	}

	@Override
	public List<UserGroupRole> getUserGroupRolesByGroupAndRole(
		long groupId, long roleId) {

		return userGroupRolePersistence.findByG_R(groupId, roleId);
	}

	@Override
	public List<UserGroupRole> getUserGroupRolesByUserUserGroupAndGroup(
		long userId, long groupId) {

		return userGroupRoleFinder.findByUserUserGroupGroupRole(
			userId, groupId);
	}

	@Override
	public int getUserGroupRolesCount(long userId, long groupId) {
		return userGroupRolePersistence.countByU_G(userId, groupId);
	}

	@Override
	public boolean hasUserGroupRole(long userId, long groupId, long roleId) {
		return hasUserGroupRole(userId, groupId, roleId, false);
	}

	@Override
	public boolean hasUserGroupRole(
		long userId, long groupId, long roleId, boolean inherit) {

		int count = userGroupRolePersistence.countByU_G_R(
			userId, groupId, roleId);

		if (count > 0) {
			return true;
		}

		if (inherit) {
			count = _rolePersistence.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					RoleTable.INSTANCE
				).innerJoinON(
					UserGroupGroupRoleTable.INSTANCE,
					UserGroupGroupRoleTable.INSTANCE.roleId.eq(
						RoleTable.INSTANCE.roleId)
				).innerJoinON(
					Users_UserGroupsTable.INSTANCE,
					Users_UserGroupsTable.INSTANCE.userGroupId.eq(
						UserGroupGroupRoleTable.INSTANCE.userGroupId)
				).where(
					RoleTable.INSTANCE.roleId.eq(
						roleId
					).and(
						UserGroupGroupRoleTable.INSTANCE.groupId.eq(groupId)
					).and(
						Users_UserGroupsTable.INSTANCE.userId.eq(userId)
					)
				));

			if (count > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasUserGroupRole(long userId, long groupId, String roleName)
		throws PortalException {

		return hasUserGroupRole(userId, groupId, roleName, false);
	}

	@Override
	public boolean hasUserGroupRole(
			long userId, long groupId, String roleName, boolean inherit)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		Role role = _rolePersistence.fetchByC_N(user.getCompanyId(), roleName);

		if (role == null) {
			return false;
		}

		return hasUserGroupRole(userId, groupId, role.getRoleId(), inherit);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public UserGroupRole updateUserGroupRole(UserGroupRole userGroupRole) {
		if (userGroupRole.getUserGroupRoleId() == 0) {
			userGroupRole.setUserGroupRoleId(
				counterLocalService.increment(UserGroupRole.class.getName()));
		}

		return userGroupRolePersistence.update(userGroupRole);
	}

	private void _reindex(long[] userIds) {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			User.class);

		try {
			for (long userId : userIds) {
				indexer.reindex(User.class.getName(), userId);
			}
		}
		catch (SearchException searchException) {
			throw new SystemException(searchException);
		}
	}

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}