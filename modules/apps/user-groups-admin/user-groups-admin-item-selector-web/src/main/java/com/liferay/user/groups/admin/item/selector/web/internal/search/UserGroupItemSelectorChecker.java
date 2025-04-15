/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class UserGroupItemSelectorChecker extends EmptyOnClickRowChecker {

	public UserGroupItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedUserGroupIds) {

		super(renderResponse);

		_checkedUserGroupIds = checkedUserGroupIds;
	}

	@Override
	public boolean isChecked(Object object) {
		UserGroup userGroup = (UserGroup)object;

		return ArrayUtil.contains(
			_checkedUserGroupIds, userGroup.getUserGroupId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final long[] _checkedUserGroupIds;

}