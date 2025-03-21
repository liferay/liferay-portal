/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicyUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Charles May
 * @author Pei-Jung Lan
 */
public class SetUserUserGroupChecker extends EmptyOnClickRowChecker {

	public SetUserUserGroupChecker(
		RenderResponse renderResponse, long userGroupId) {

		super(renderResponse);

		_userGroupId = userGroupId;
	}

	@Override
	public boolean isChecked(Object object) {
		User user = (User)object;

		try {
			return UserLocalServiceUtil.hasUserGroupUser(
				_userGroupId, user.getUserId());
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
			if (isChecked(user) ||
				!UserGroupMembershipPolicyUtil.isMembershipAllowed(
					user.getUserId(), _userGroupId)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SetUserUserGroupChecker.class);

	private final long _userGroupId;

}