/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.resource.v1_0;

import com.liferay.headless.asset.library.dto.v1_0.Role;
import com.liferay.headless.asset.library.resource.v1_0.RoleResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;

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
	public Page<Role>
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeRolesPage(
				String assetLibraryExternalReferenceCode,
				String userAccountExternalReferenceCode)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		return getAssetLibraryUserAccountRolesPage(
			group.getGroupId(), user.getUserId());
	}

	@Override
	public Page<Role>
			getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCodeRolesPage(
				String assetLibraryExternalReferenceCode,
				String userGroupExternalReferenceCode)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				userGroupExternalReferenceCode, contextCompany.getCompanyId());

		return getAssetLibraryUserGroupRolesPage(
			group.getGroupId(), userGroup.getUserGroupId());
	}

	@Override
	public Page<Role> getAssetLibraryUserAccountRolesPage(
			Long assetLibraryId, Long userAccountId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		if (!_groupService.hasUserGroup(userAccountId, assetLibraryId)) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"User ", userAccountId, " is not associated to group ",
					assetLibraryId));
		}

		return Page.of(
			transform(
				_roleService.getUserGroupRoles(userAccountId, assetLibraryId),
				this::_toRole));
	}

	@Override
	public Page<Role> getAssetLibraryUserGroupRolesPage(
			Long assetLibraryId, Long userGroupId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		if (!_userGroupLocalService.hasGroupUserGroup(
				assetLibraryId, userGroupId)) {

			throw new NoSuchUserGroupException(
				"No user group exists with user group ID " + userGroupId);
		}

		return Page.of(
			transform(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroupId, assetLibraryId),
				userGroupGroupRole -> _toRole(userGroupGroupRole.getRole())));
	}

	@Override
	public Page<Role>
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeRolesPage(
				String assetLibraryExternalReferenceCode,
				String userAccountExternalReferenceCode, Role[] roles)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		User user = _userService.getUserByExternalReferenceCode(
			userAccountExternalReferenceCode, contextCompany.getCompanyId());

		return putAssetLibraryUserAccountRolesPage(
			group.getGroupId(), user.getUserId(), roles);
	}

	@Override
	public Page<Role>
			putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCodeRolesPage(
				String assetLibraryExternalReferenceCode,
				String userGroupExternalReferenceCode, Role[] roles)
		throws Exception {

		Group group = _getGroup(assetLibraryExternalReferenceCode);
		UserGroup userGroup =
			_userGroupService.getUserGroupByExternalReferenceCode(
				userGroupExternalReferenceCode, contextCompany.getCompanyId());

		return putAssetLibraryUserGroupRolesPage(
			group.getGroupId(), userGroup.getUserGroupId(), roles);
	}

	@Override
	public Page<Role> putAssetLibraryUserAccountRolesPage(
			Long assetLibraryId, Long userAccountId, Role[] roles)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		if (!_groupService.hasUserGroup(userAccountId, assetLibraryId)) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"User ", userAccountId, " is not associated to group ",
					assetLibraryId));
		}

		_userGroupRoleService.deleteUserGroupRoles(
			userAccountId, assetLibraryId,
			ListUtil.toLongArray(
				_roleService.getUserGroupRoles(userAccountId, assetLibraryId),
				com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));

		_userGroupRoleService.addUserGroupRoles(
			userAccountId, assetLibraryId, _getRoleIds(roles));

		return Page.of(
			transform(
				_roleService.getUserGroupRoles(userAccountId, assetLibraryId),
				this::_toRole));
	}

	@Override
	public Page<Role> putAssetLibraryUserGroupRolesPage(
			Long assetLibraryId, Long userGroupId, Role[] roles)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		if (!_userGroupLocalService.hasGroupUserGroup(
				assetLibraryId, userGroupId)) {

			throw new NoSuchUserGroupException(
				"No user group exists with user group ID " + userGroupId);
		}

		_userGroupGroupRoleService.deleteUserGroupGroupRoles(
			userGroupId, assetLibraryId,
			ListUtil.toLongArray(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroupId, assetLibraryId),
				UserGroupGroupRole::getRoleId));

		_userGroupGroupRoleService.addUserGroupGroupRoles(
			userGroupId, assetLibraryId, _getRoleIds(roles));

		return Page.of(
			transform(
				_userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroupId, assetLibraryId),
				userGroupGroupRole -> _toRole(userGroupGroupRole.getRole())));
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
				setRoleType(role::getType);
			}
		};
	}

	@Reference
	private GroupService _groupService;

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