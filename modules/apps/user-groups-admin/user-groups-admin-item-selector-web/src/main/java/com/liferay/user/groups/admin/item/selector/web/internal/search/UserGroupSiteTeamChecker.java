/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Edward Han
 */
public class UserGroupSiteTeamChecker extends EmptyOnClickRowChecker {

	public UserGroupSiteTeamChecker(RenderResponse renderResponse, Team team) {
		super(renderResponse);

		_team = team;
	}

	@Override
	public boolean isChecked(Object object) {
		return hasTeamUserGroup(object);
	}

	@Override
	public boolean isDisabled(Object object) {
		return hasTeamUserGroup(object);
	}

	protected boolean hasTeamUserGroup(Object object) {
		UserGroup userGroup = (UserGroup)object;

		try {
			return UserGroupLocalServiceUtil.hasTeamUserGroup(
				_team.getTeamId(), userGroup.getUserGroupId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupSiteTeamChecker.class);

	private final Team _team;

}