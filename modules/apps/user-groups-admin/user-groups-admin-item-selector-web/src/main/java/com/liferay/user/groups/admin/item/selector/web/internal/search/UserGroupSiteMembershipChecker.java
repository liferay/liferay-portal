/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Charles May
 */
public class UserGroupSiteMembershipChecker extends EmptyOnClickRowChecker {

	public UserGroupSiteMembershipChecker(
		RenderResponse renderResponse, long groupId) {

		super(renderResponse);

		_groupId = groupId;
	}

	@Override
	public boolean isChecked(Object object) {
		UserGroup userGroup = (UserGroup)object;

		try {
			return UserGroupLocalServiceUtil.hasGroupUserGroup(
				_groupId, userGroup.getUserGroupId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupSiteMembershipChecker.class);

	private final long _groupId;

}