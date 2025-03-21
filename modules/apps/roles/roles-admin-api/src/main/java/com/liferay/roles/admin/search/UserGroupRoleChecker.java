/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Charles May
 */
public class UserGroupRoleChecker extends EmptyOnClickRowChecker {

	public UserGroupRoleChecker(RenderResponse renderResponse, Role role) {
		super(renderResponse);

		_role = role;
	}

	@Override
	public boolean isChecked(Object object) {
		UserGroup userGroup = (UserGroup)object;

		try {
			Group userGroupGroup = userGroup.getGroup();

			return GroupLocalServiceUtil.hasRoleGroup(
				_role.getRoleId(), userGroupGroup.getGroupId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		UserGroup userGroup = (UserGroup)object;

		return isChecked(userGroup);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupRoleChecker.class);

	private final Role _role;

}