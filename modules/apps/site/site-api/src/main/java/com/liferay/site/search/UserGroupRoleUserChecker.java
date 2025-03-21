/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.search;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Jorge Ferrer
 */
public class UserGroupRoleUserChecker extends RowChecker {

	public UserGroupRoleUserChecker(
		RenderResponse renderResponse, Group group, Role role) {

		super(renderResponse);

		_group = group;
		_role = role;
	}

	@Override
	public boolean isChecked(Object object) {
		User user = (User)object;

		try {
			return UserGroupRoleLocalServiceUtil.hasUserGroupRole(
				user.getUserId(), _group.getGroupId(), _role.getRoleId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		User user = (User)object;

		try {
			if (isChecked(user)) {
				if (SiteMembershipPolicyUtil.isRoleRequired(
						user.getUserId(), _group.getGroupId(),
						_role.getRoleId())) {

					return true;
				}
			}
			else {
				if (!SiteMembershipPolicyUtil.isRoleAllowed(
						user.getUserId(), _group.getGroupId(),
						_role.getRoleId())) {

					return true;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupRoleUserChecker.class);

	private final Group _group;
	private final Role _role;

}