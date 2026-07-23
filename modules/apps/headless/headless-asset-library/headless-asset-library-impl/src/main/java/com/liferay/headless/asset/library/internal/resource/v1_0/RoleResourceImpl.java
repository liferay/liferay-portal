/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

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
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.List;

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

		_userGroupRoleService.deleteUserGroupRoles(
			user.getUserId(), group.getGroupId(),
			ListUtil.toLongArray(
				_roleService.getUserGroupRoles(
					user.getUserId(), group.getGroupId()),
				com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));

		_userGroupRoleService.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), _getRoleIds(roles));

		return Page.of(
			transform(
				_roleService.getUserGroupRoles(
					user.getUserId(), group.getGroupId()),
				this::_toRole));
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
	private UserGroupRoleService _userGroupRoleService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserService _userService;

}