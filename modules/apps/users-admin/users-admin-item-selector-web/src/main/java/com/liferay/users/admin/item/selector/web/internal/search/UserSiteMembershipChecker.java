/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class UserSiteMembershipChecker extends EmptyOnClickRowChecker {

	public UserSiteMembershipChecker(
		RenderResponse renderResponse, Group group) {

		super(renderResponse);

		_group = group;
	}

	@Override
	public boolean isChecked(Object object) {
		User user = null;

		if (object instanceof User) {
			user = (User)object;
		}
		else if (object instanceof Object[]) {
			user = (User)((Object[])object)[0];
		}
		else {
			throw new IllegalArgumentException(object + " is not a user");
		}

		try {
			return UserLocalServiceUtil.hasGroupUser(
				_group.getGroupId(), user.getUserId());
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
				!SiteMembershipPolicyUtil.isMembershipAllowed(
					user.getUserId(), _group.getGroupId())) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserSiteMembershipChecker.class);

	private final Group _group;

}