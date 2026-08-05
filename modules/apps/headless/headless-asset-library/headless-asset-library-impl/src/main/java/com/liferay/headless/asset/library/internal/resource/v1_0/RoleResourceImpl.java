/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.headless.asset.library.dto.v1_0.Role;
import com.liferay.headless.asset.library.resource.v1_0.RoleResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Roberto Díaz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/role.properties",
	scope = ServiceScope.PROTOTYPE, service = RoleResource.class
)
public class RoleResourceImpl extends BaseRoleResourceImpl {

	@Override
	public Page<Role> getAssetLibraryRolesPage(
			String assetLibraryExternalReferenceCode, Pagination pagination)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);

		_checkAssetLibraryAdminOrAssetLibraryMember(group.getGroupId());

		List<com.liferay.portal.kernel.model.Role> roles =
			_roleLocalService.getTypeRoles(RoleConstants.TYPE_DEPOT);

		roles = DepotRoleUtil.filter(group.getGroupId(), roles);

		if (pagination == null) {
			return Page.of(transform(roles, this::_toRole));
		}

		return Page.of(
			transform(
				ListUtil.subList(
					roles, pagination.getStartPosition(),
					pagination.getEndPosition()),
				this::_toRole),
			pagination, roles.size());
	}

	@Override
	public Page<Role> getAssetLibraryUserAccountRolesPage(
			String assetLibraryExternalReferenceCode,
			String userAccountExternalReferenceCode)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		if (!_groupService.hasUserGroup(user.getUserId(), group.getGroupId())) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"User ", user.getUserId(), " is not associated to group ",
					group.getGroupId()));
		}

		return Page.of(
			transform(
				_roleService.getUserGroupRoles(
					user.getUserId(), group.getGroupId()),
				this::_toRole));
	}

	@Override
	public Page<Role> getAssetLibraryUserGroupRolesPage(
			String assetLibraryExternalReferenceCode,
			String userGroupExternalReferenceCode)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				userGroupExternalReferenceCode, contextCompany.getCompanyId());

		if (!_userGroupLocalService.hasGroupUserGroup(
				group.getGroupId(), userGroup.getUserGroupId())) {

			throw new NoSuchUserGroupException(
				"No user group exists with user group ID " +
					userGroup.getUserGroupId());
		}

		return Page.of(
			transform(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroup.getUserGroupId(), group.getGroupId()),
				userGroupGroupRole -> _toRole(userGroupGroupRole.getRole())));
	}

	@Override
	public Page<Role> putAssetLibraryUserAccountRolesPage(
			String assetLibraryExternalReferenceCode,
			String userAccountExternalReferenceCode, Role[] roles)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		if (!_groupService.hasUserGroup(user.getUserId(), group.getGroupId())) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"User ", user.getUserId(), " is not associated to group ",
					group.getGroupId()));
		}

		List<com.liferay.portal.kernel.model.Role> currentRoles =
			_roleLocalService.getUserGroupRoles(
				user.getUserId(), group.getGroupId());

		List<com.liferay.portal.kernel.model.Role> updatedRoles;

		if (_isDefaultAssetLibraryMemberRoleAssignment(currentRoles, roles)) {

			// The CMS "Manage Members" UI (ManageMembersList) adds a brand
			// new member and then, in a separate follow-up call, assigns
			// them the space's fixed default role
			// (SPACE_MEMBERS_CONFIG.defaultRoleExternalReferenceCode,
			// hardcoded to Asset Library Member - never configurable, never
			// any other role). That follow-up call goes through
			// UserGroupRoleService, which requires ASSIGN_USER_ROLES; if a
			// caller holding only ASSIGN_MEMBERS lacks it, the UI does not
			// roll back the membership it just added - the user stays a
			// member with no role, and the only feedback is an error toast.
			// To avoid that half-succeeded state, a caller with just
			// ASSIGN_MEMBERS is allowed to grant this one specific,
			// hardcoded role, but only to a user who currently holds no
			// roles here (i.e. was just added, mirroring the UI's own
			// !isAlreadyMember condition for making this call at all).
			// Reassigning an existing member's role, removing a role, or
			// granting any other role - including Administrator or Content
			// Reviewer - still requires ASSIGN_USER_ROLES or Role-scoped
			// ASSIGN_MEMBERS, unchanged. The role ID is resolved through
			// the local service (not _getRoleIds/_roleService.getRole,
			// which requires Role-scoped VIEW) for the same reason the
			// read of the updated roles below does: the caller may hold
			// ASSIGN_MEMBERS on the group without holding VIEW on the
			// individual Role, which would otherwise reject the lookup
			// entirely before the assignment is ever attempted.

			_checkAssetLibraryAdminOrAssignMembers(group.getGroupId());

			com.liferay.portal.kernel.model.Role assetLibraryMemberRole =
				_roleLocalService.getRole(
					contextCompany.getCompanyId(),
					DepotRolesConstants.ASSET_LIBRARY_MEMBER);

			// The read below goes through the local service, not
			// _roleService, for the same reason the role lookup above does:
			// this caller may hold ASSIGN_MEMBERS without Role-scoped VIEW
			// on Asset Library Member, and the permissioned read would
			// filter the assignment just made down to nothing. The else
			// branch below keeps _roleService, since every role reaching it
			// was already resolved through _getRoleIds, which itself
			// requires Role-scoped VIEW.

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {assetLibraryMemberRole.getRoleId()});

			updatedRoles = _roleLocalService.getUserGroupRoles(
				user.getUserId(), group.getGroupId());
		}
		else {
			_userGroupRoleService.deleteUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				ListUtil.toLongArray(
					currentRoles,
					com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));

			_userGroupRoleService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(), _getRoleIds(roles));

			updatedRoles = _roleService.getUserGroupRoles(
				user.getUserId(), group.getGroupId());
		}

		return Page.of(transform(updatedRoles, this::_toRole));
	}

	@Override
	public Page<Role> putAssetLibraryUserGroupRolesPage(
			String assetLibraryExternalReferenceCode,
			String userGroupExternalReferenceCode, Role[] roles)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				userGroupExternalReferenceCode, contextCompany.getCompanyId());

		if (!_userGroupLocalService.hasGroupUserGroup(
				group.getGroupId(), userGroup.getUserGroupId())) {

			throw new NoSuchUserGroupException(
				"No user group exists with user group ID " +
					userGroup.getUserGroupId());
		}

		_userGroupGroupRoleService.deleteUserGroupGroupRoles(
			userGroup.getUserGroupId(), group.getGroupId(),
			ListUtil.toLongArray(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroup.getUserGroupId(), group.getGroupId()),
				UserGroupGroupRole::getRoleId));

		_userGroupGroupRoleService.addUserGroupGroupRoles(
			userGroup.getUserGroupId(), group.getGroupId(), _getRoleIds(roles));

		return Page.of(
			transform(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroup.getUserGroupId(), group.getGroupId()),
				userGroupGroupRole -> _toRole(userGroupGroupRole.getRole())));
	}

	private void _checkAssetLibraryAdminOrAssetLibraryMember(long groupId)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId)) {
			return;
		}

		if (!_groupService.hasUserGroup(contextUser.getUserId(), groupId)) {
			throw new PrincipalException.MustHavePermission(
				contextUser.getUserId(), ActionKeys.VIEW);
		}
	}

	private void _checkAssetLibraryAdminOrAssignMembers(long groupId)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId) ||
			GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.ASSIGN_MEMBERS) ||
			GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.ASSIGN_USER_ROLES)) {

			return;
		}

		throw new PrincipalException.MustHavePermission(
			contextUser.getUserId(), ActionKeys.ASSIGN_MEMBERS);
	}

	private Group _getGroup(String externalReferenceCode) throws Exception {
		Group group = _groupService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			throw new NoSuchGroupException(
				"No group exists with external reference code " +
					externalReferenceCode);
		}

		return group;
	}

	private long[] _getRoleIds(Role[] roles) throws Exception {
		return transformToLongArray(
			roles,
			role -> {
				com.liferay.portal.kernel.model.Role serviceBuilderRole =
					_roleService.getRole(
						contextCompany.getCompanyId(), role.getName());

				return serviceBuilderRole.getRoleId();
			});
	}

	private boolean _isDefaultAssetLibraryMemberRoleAssignment(
		List<com.liferay.portal.kernel.model.Role> currentRoles, Role[] roles) {

		if (!currentRoles.isEmpty() || (roles.length != 1)) {
			return false;
		}

		return Objects.equals(
			roles[0].getName(), DepotRolesConstants.ASSET_LIBRARY_MEMBER);
	}

	private Role _toRole(com.liferay.portal.kernel.model.Role role)
		throws PortalException {

		return new Role() {
			{
				setExternalReferenceCode(role::getExternalReferenceCode);
				setId(role::getRoleId);
				setName(role::getName);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(role.getTitleMap()));
				setRoleType(role::getType);
			}
		};
	}

	@Reference
	private GroupService _groupService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Reference
	private UserGroupGroupRoleService _userGroupGroupRoleService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserGroupRoleService _userGroupRoleService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserService _userService;

}