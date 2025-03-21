/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.security.membershippolicy.RoleMembershipPolicyUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Drew Brokke
 */
public class UnsetUserRoleChecker extends EmptyOnClickRowChecker {

	public UnsetUserRoleChecker(RenderResponse renderResponse, Role role) {
		super(renderResponse);

		_role = role;
	}

	@Override
	public boolean isDisabled(Object object) {
		User user = (User)object;

		try {
			if (RoleMembershipPolicyUtil.isRoleRequired(
					user.getUserId(), _role.getRoleId())) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnsetUserRoleChecker.class);

	private final Role _role;

}