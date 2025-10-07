/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class UserBagFactoryUtil {

	public static UserBag create(User user) throws PortalException {
		long userId = user.getUserId();

		UserBag userBag = PermissionCacheUtil.getUserBag(userId);

		if ((userBag != null) &&
			(user.getMvccVersion() == userBag.getUserMVCCVersion())) {

			return userBag;
		}

		Set<Long> allGroupIds = new HashSet<>();

		Collection<Organization> userOrgs = _getUserOrgs(userId);

		Set<Long> userOrgGroupIds = new HashSet<>();

		for (Organization organization : userOrgs) {
			userOrgGroupIds.add(organization.getGroupId());
		}

		allGroupIds.addAll(userOrgGroupIds);

		List<UserGroup> userUserGroups = user.getUserGroups();

		long[] userUserGroupGroupIds = new long[userUserGroups.size()];

		for (int i = 0; i < userUserGroups.size(); i++) {
			UserGroup userUserGroup = userUserGroups.get(i);

			long groupId = userUserGroup.getGroupId();

			userUserGroupGroupIds[i] = groupId;

			allGroupIds.add(groupId);
		}

		long[] userGroupIds = UserLocalServiceUtil.getGroupPrimaryKeys(userId);

		Set<Long> userGroupIdsSet = new HashSet<>();

		for (long userGroupId : userGroupIds) {
			userGroupIdsSet.add(userGroupId);

			allGroupIds.add(userGroupId);
		}

		if (!userOrgs.isEmpty() || !userUserGroups.isEmpty()) {
			List<Group> userGroups = GroupLocalServiceUtil.getUserGroups(
				userId, true);

			for (Group userGroup : userGroups) {
				long groupId = userGroup.getGroupId();

				userGroupIdsSet.add(groupId);

				allGroupIds.add(groupId);
			}

			userGroupIds = ArrayUtil.toLongArray(userGroupIdsSet);
		}

		if (allGroupIds.isEmpty()) {
			long[] userRoleIds = UserLocalServiceUtil.getRolePrimaryKeys(
				userId);

			userBag = new UserBagImpl(
				userId, user.getMvccVersion(), userGroupIds, userOrgs,
				userOrgGroupIds, userUserGroups, userUserGroupGroupIds,
				userRoleIds);
		}
		else {
			List<Role> userRoles = RoleLocalServiceUtil.getUserRelatedRoles(
				userId, ArrayUtil.toLongArray(allGroupIds));

			userBag = new UserBagImpl(
				userId, user.getMvccVersion(), userGroupIds, userOrgs,
				userOrgGroupIds, userUserGroups, userUserGroupGroupIds,
				userRoles);
		}

		PermissionCacheUtil.putUserBag(userId, userBag);

		return userBag;
	}

	private static Collection<Organization> _getUserOrgs(long userId)
		throws PortalException {

		List<Organization> userOrgs =
			OrganizationLocalServiceUtil.getUserOrganizations(userId);

		if (userOrgs.isEmpty()) {
			return Collections.emptyList();
		}

		if (PropsValues.ORGANIZATIONS_MEMBERSHIP_STRICT) {
			return userOrgs;
		}

		Set<Organization> organizations = new LinkedHashSet<>();

		for (Organization organization : userOrgs) {
			if (organizations.add(organization)) {
				List<Organization> ancestorOrganizations =
					OrganizationLocalServiceUtil.getParentOrganizations(
						organization.getOrganizationId());

				organizations.addAll(ancestorOrganizations);
			}
		}

		return organizations;
	}

}