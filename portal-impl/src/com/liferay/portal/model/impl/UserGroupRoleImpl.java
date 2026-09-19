/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class UserGroupRoleImpl extends UserGroupRoleBaseImpl {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserGroupRole)) {
			return false;
		}

		UserGroupRole userGroupRole = (UserGroupRole)object;

		if ((getUserId() == userGroupRole.getUserId()) &&
			(getGroupId() == userGroupRole.getGroupId()) &&
			(getRoleId() == userGroupRole.getRoleId())) {

			return true;
		}

		return false;
	}

	@Override
	public Group getGroup() throws PortalException {
		return GroupLocalServiceUtil.getGroup(getGroupId());
	}

	@Override
	public Role getRole() throws PortalException {
		return RoleLocalServiceUtil.getRole(getRoleId());
	}

	@Override
	public User getUser() throws PortalException {
		return UserLocalServiceUtil.getUser(getUserId());
	}

	@Override
	public boolean hasOrganizationRole() {
		Role role = RoleLocalServiceUtil.fetchRole(getRoleId());

		if ((role != null) &&
			(role.getType() == RoleConstants.TYPE_ORGANIZATION)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean hasSiteRole() {
		try {
			Group group = getGroup();
			Role role = getRole();

			if ((group != null) && group.isSite() && (role != null) &&
				(role.getType() == RoleConstants.TYPE_SITE)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, getUserId());

		hash = HashUtil.hash(hash, getGroupId());

		return HashUtil.hash(hash, getRoleId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupRoleImpl.class);

}