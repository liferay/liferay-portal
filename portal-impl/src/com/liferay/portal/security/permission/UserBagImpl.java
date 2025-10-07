/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author László Csontos
 * @author Preston Crary
 */
public class UserBagImpl implements UserBag {

	public UserBagImpl(
		long userId, long userMvccVersion, long[] userGroupIds,
		Collection<Organization> userOrgIds, Collection<Long> userOrgGroupIds,
		Collection<UserGroup> userUserGroupIds, long[] userUserGroupGroupIds,
		Collection<Role> userRoleIds) {

		_userId = userId;
		_userMvccVersion = userMvccVersion;
		_userGroupIds = _toSortedLongArray(userGroupIds);
		_userOrgIds = _toSortedLongArray(userOrgIds);
		_userOrgGroupIds = _toSortedLongArray(
			ArrayUtil.toLongArray(userOrgGroupIds));
		_userUserGroupIds = _toSortedLongArray(userUserGroupIds);
		_userUserGroupGroupIds = _toSortedLongArray(userUserGroupGroupIds);
		_userRoleIds = _toSortedLongArray(userRoleIds);
	}

	public UserBagImpl(
		long userId, long userMvccVersion, long[] userGroupIds,
		Collection<Organization> userOrgIds, Collection<Long> userOrgGroupIds,
		Collection<UserGroup> userUserGroupIds, long[] userUserGroupGroupIds,
		long[] userRoleIds) {

		_userId = userId;
		_userMvccVersion = userMvccVersion;
		_userGroupIds = _toSortedLongArray(userGroupIds);
		_userOrgIds = _toSortedLongArray(userOrgIds);
		_userOrgGroupIds = _toSortedLongArray(
			ArrayUtil.toLongArray(userOrgGroupIds));
		_userUserGroupIds = _toSortedLongArray(userUserGroupIds);
		_userUserGroupGroupIds = _toSortedLongArray(userUserGroupGroupIds);
		_userRoleIds = _toSortedLongArray(userRoleIds);
	}

	@Override
	public Set<Group> getGroups() throws PortalException {
		Set<Group> groups = new HashSet<>(getUserGroups());

		groups.addAll(getUserOrgGroups());
		groups.addAll(getUserUserGroupGroups());

		return groups;
	}

	@Override
	public long[] getRoleIds() {
		return _userRoleIds.clone();
	}

	@Override
	public List<Role> getRoles() throws PortalException {
		return RoleLocalServiceUtil.getRoles(_userRoleIds);
	}

	@Override
	public long[] getUserGroupIds() {
		return _userGroupIds.clone();
	}

	@Override
	public List<Group> getUserGroups() throws PortalException {
		return GroupLocalServiceUtil.getGroups(_userGroupIds);
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public long getUserMVCCVersion() {
		return _userMvccVersion;
	}

	@Override
	public long[] getUserOrgGroupIds() {
		return _userOrgGroupIds.clone();
	}

	@Override
	public List<Group> getUserOrgGroups() throws PortalException {
		return GroupLocalServiceUtil.getGroups(_userOrgGroupIds);
	}

	@Override
	public long[] getUserOrgIds() {
		return _userOrgIds.clone();
	}

	@Override
	public List<Organization> getUserOrgs() throws PortalException {
		return OrganizationLocalServiceUtil.getOrganizations(_userOrgIds);
	}

	@Override
	public List<Group> getUserUserGroupGroups() throws PortalException {
		return GroupLocalServiceUtil.getGroups(_userUserGroupGroupIds);
	}

	@Override
	public long[] getUserUserGroupsIds() {
		return _userUserGroupIds;
	}

	@Override
	public boolean hasRole(Role role) {
		return _search(_userRoleIds, role.getRoleId());
	}

	@Override
	public boolean hasUserGroup(Group group) {
		return _search(_userGroupIds, group.getGroupId());
	}

	@Override
	public boolean hasUserOrg(Organization organization) {
		return _search(_userOrgIds, organization.getOrganizationId());
	}

	@Override
	public boolean hasUserOrgGroup(Group group) {
		return _search(_userOrgGroupIds, group.getGroupId());
	}

	private boolean _search(long[] ids, long id) {
		if (Arrays.binarySearch(ids, id) >= 0) {
			return true;
		}

		return false;
	}

	private long[] _toSortedLongArray(
		Collection<? extends BaseModel<?>> baseModels) {

		if ((baseModels == null) || baseModels.isEmpty()) {
			return _EMPTY;
		}

		long[] array = new long[baseModels.size()];

		int index = 0;

		for (BaseModel<?> baseModel : baseModels) {
			array[index++] = (long)baseModel.getPrimaryKeyObj();
		}

		Arrays.sort(array);

		return array;
	}

	private long[] _toSortedLongArray(long[] ids) {
		if (ids.length == 0) {
			return _EMPTY;
		}

		Arrays.sort(ids);

		return ids;
	}

	private static final long[] _EMPTY = {};

	private final long[] _userGroupIds;
	private final long _userId;
	private final long _userMvccVersion;
	private final long[] _userOrgGroupIds;
	private final long[] _userOrgIds;
	private final long[] _userRoleIds;
	private final long[] _userUserGroupGroupIds;
	private final long[] _userUserGroupIds;

}