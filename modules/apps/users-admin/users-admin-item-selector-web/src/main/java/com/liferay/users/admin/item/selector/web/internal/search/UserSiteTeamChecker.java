/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class UserSiteTeamChecker extends EmptyOnClickRowChecker {

	public UserSiteTeamChecker(RenderResponse renderResponse, Team team) {
		super(renderResponse);

		_team = team;
	}

	@Override
	public boolean isChecked(Object object) {
		return hasTeamUser(object);
	}

	@Override
	public boolean isDisabled(Object object) {
		return hasTeamUser(object);
	}

	protected boolean hasTeamUser(Object object) {
		User user = (User)object;

		try {
			return UserLocalServiceUtil.hasTeamUser(
				_team.getTeamId(), user.getUserId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserSiteTeamChecker.class);

	private final Team _team;

}