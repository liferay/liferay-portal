/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.security.permission.wrapper;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.permission.PermissionCacheUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Cristina González
 */
public class DepotPermissionCheckerWrapper extends PermissionCheckerWrapper {

	public DepotPermissionCheckerWrapper(
		PermissionChecker permissionChecker,
		ModelResourcePermission<DepotEntry> depotEntryModelResourcePermission,
		GroupLocalService groupLocalService, RoleLocalService roleLocalService,
		UserGroupRoleLocalService userGroupRoleLocalService) {

		super(permissionChecker);

		_depotEntryModelResourcePermission = depotEntryModelResourcePermission;
		_groupLocalService = groupLocalService;
		_roleLocalService = roleLocalService;
		_userGroupRoleLocalService = userGroupRoleLocalService;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		if (_isDepotGroupOwner(group)) {
			return true;
		}

		Boolean hasPermission = _hasPermission(name, primKey, actionId);

		if (hasPermission != null) {
			return hasPermission;
		}

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		if (_isDepotGroupOwner(group)) {
			return true;
		}

		Boolean hasPermission = _hasPermission(
			name, GetterUtil.getLong(primKey), actionId);

		if (hasPermission != null) {
			return hasPermission;
		}

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		if (_isDepotGroupOwner(_groupLocalService.fetchGroup(groupId))) {
			return true;
		}

		Boolean hasPermission = _hasPermission(name, primKey, actionId);

		if (hasPermission != null) {
			return hasPermission;
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		if (_isDepotGroupOwner(_groupLocalService.fetchGroup(groupId))) {
			return true;
		}

		Boolean hasPermission = _hasPermission(
			name, GetterUtil.getLong(primKey), actionId);

		if (hasPermission != null) {
			return hasPermission;
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		try {
			if (!isSignedIn()) {
				return false;
			}

			if (permissionChecker.isContentReviewer(companyId, groupId) ||
				isGroupAdmin(groupId)) {

				return true;
			}

			return _isOrAddToPermissionCache(
				_groupLocalService.fetchGroup(groupId),
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				this::_isContentReviewer);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		try {
			if (!isSignedIn()) {
				return false;
			}

			if (permissionChecker.isGroupAdmin(groupId)) {
				return true;
			}

			return _isOrAddToPermissionCache(
				_groupLocalService.fetchGroup(groupId),
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				this::_isGroupAdmin);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isGroupMember(long groupId) {
		try {
			if (!isSignedIn()) {
				return false;
			}

			if (permissionChecker.isGroupMember(groupId)) {
				return true;
			}

			return _isGroupMember(_groupLocalService.fetchGroup(groupId));
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		try {
			if (!isSignedIn()) {
				return false;
			}

			if (permissionChecker.isGroupOwner(groupId)) {
				return true;
			}

			return _isOrAddToPermissionCache(
				_groupLocalService.fetchGroup(groupId),
				DepotRolesConstants.ASSET_LIBRARY_OWNER, this::_isGroupOwner);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private Boolean _hasPermission(String name, long primKey, String actionId) {
		if (StringUtil.equals(name, Group.class.getName())) {
			Group group = _groupLocalService.fetchGroup(primKey);

			if ((group != null) && group.isDepot()) {
				try {
					if (!_supportedActionIds.contains(actionId)) {
						return false;
					}

					if (_isGroupAdmin(group)) {
						return true;
					}

					return _depotEntryModelResourcePermission.contains(
						this, group.getClassPK(), actionId);
				}
				catch (PortalException portalException) {
					_log.error(portalException);

					return false;
				}
			}
		}

		return null;
	}

	private boolean _hasRole(long companyId, long[] roleIds, String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(companyId, roleName);

		if (Arrays.binarySearch(roleIds, role.getRoleId()) >= 0) {
			return true;
		}

		return false;
	}

	private boolean _isContentReviewer(Group group) throws PortalException {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		return _userGroupRoleLocalService.hasUserGroupRole(
			getUserId(), liveGroup.getGroupId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER, true);
	}

	private boolean _isDepotGroupOwner(Group group) {
		if ((group != null) && group.isDepot() &&
			isGroupOwner(group.getGroupId())) {

			return true;
		}

		return false;
	}

	private boolean _isGroupAdmin(Group group) throws PortalException {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		if (_userGroupRoleLocalService.hasUserGroupRole(
				getUserId(), liveGroup.getGroupId(),
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, true) ||
			_userGroupRoleLocalService.hasUserGroupRole(
				getUserId(), liveGroup.getGroupId(),
				DepotRolesConstants.ASSET_LIBRARY_OWNER, true)) {

			return true;
		}

		Group parentGroup = liveGroup;

		while (!parentGroup.isRoot()) {
			parentGroup = parentGroup.getParentGroup();

			if (permissionChecker.hasPermission(
					parentGroup, Group.class.getName(),
					String.valueOf(parentGroup.getGroupId()),
					ActionKeys.MANAGE_SUBGROUPS)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isGroupMember(Group group) throws Exception {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		long[] roleIds = getRoleIds(getUserId(), liveGroup.getGroupId());

		if (_hasRole(
				liveGroup.getCompanyId(), roleIds,
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER) ||
			_hasRole(
				liveGroup.getCompanyId(), roleIds,
				DepotRolesConstants.ASSET_LIBRARY_MEMBER)) {

			return true;
		}

		return false;
	}

	private boolean _isGroupOwner(Group group) throws PortalException {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		return _userGroupRoleLocalService.hasUserGroupRole(
			getUserId(), liveGroup.getGroupId(),
			DepotRolesConstants.ASSET_LIBRARY_OWNER, true);
	}

	private boolean _isOrAddToPermissionCache(
			Group group, String roleName,
			UnsafeFunction<Group, Boolean, Exception> unsafeFunction)
		throws Exception {

		if (group == null) {
			return false;
		}

		Boolean value = PermissionCacheUtil.getUserPrimaryKeyRole(
			getUserId(), group.getGroupId(), roleName);

		if (value != null) {
			return value;
		}

		value = unsafeFunction.apply(group);

		PermissionCacheUtil.putUserPrimaryKeyRole(
			getUserId(), group.getGroupId(), roleName, value);

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotPermissionCheckerWrapper.class);

	private static final Set<String> _supportedActionIds = new HashSet<>(
		Arrays.asList(
			ActionKeys.ASSIGN_MEMBERS, ActionKeys.ASSIGN_USER_ROLES,
			ActionKeys.DELETE, ActionKeys.PUBLISH_STAGING, ActionKeys.UPDATE,
			ActionKeys.VIEW, ActionKeys.VIEW_MEMBERS,
			ActionKeys.VIEW_SITE_ADMINISTRATION, ActionKeys.VIEW_STAGING));

	private final ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;
	private final GroupLocalService _groupLocalService;
	private final RoleLocalService _roleLocalService;
	private final UserGroupRoleLocalService _userGroupRoleLocalService;

}