/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Brett Swaim
 */
public class UserGroupGroupRoleRoleChecker extends EmptyOnClickRowChecker {

	public UserGroupGroupRoleRoleChecker(
		RenderResponse renderResponse, UserGroup userGroup, Group group) {

		super(renderResponse);

		_userGroup = userGroup;
		_group = group;
	}

	@Override
	public boolean isChecked(Object object) {
		Role role = (Role)object;

		try {
			return UserGroupGroupRoleLocalServiceUtil.hasUserGroupGroupRole(
				_userGroup.getUserGroupId(), _group.getGroupId(),
				role.getRoleId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupGroupRoleRoleChecker.class);

	private final Group _group;
	private final UserGroup _userGroup;

}