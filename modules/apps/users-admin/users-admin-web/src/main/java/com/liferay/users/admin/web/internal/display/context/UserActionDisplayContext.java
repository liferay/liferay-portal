/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.users.admin.user.action.contributor.UserActionContributor;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Drew Brokke
 */
public class UserActionDisplayContext {

	public UserActionDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest, User user, User selUser) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;
		_user = user;
		_selUser = selUser;
	}

	public UserActionContributor[] getFilteredUserActionContributors() {
		UserActionContributor[] userActionContributors =
			(UserActionContributor[])_httpServletRequest.getAttribute(
				UsersAdminWebKeys.USER_ACTION_CONTRIBUTORS);

		if (userActionContributors == null) {
			return new UserActionContributor[0];
		}

		return ArrayUtil.filter(
			userActionContributors,
			userActionContributor -> userActionContributor.isShow(
				_liferayPortletRequest, _user, _selUser));
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final User _selUser;
	private final User _user;

}