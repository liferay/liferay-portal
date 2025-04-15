/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class UserItemSelectorChecker extends EmptyOnClickRowChecker {

	public UserItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedUserIds,
		boolean checkedUserIdsEnabled) {

		super(renderResponse);

		_checkedUserIds = checkedUserIds;
		_checkedUserIdsEnabled = checkedUserIdsEnabled;
	}

	@Override
	public boolean isChecked(Object object) {
		User user = (User)object;

		return ArrayUtil.contains(_checkedUserIds, user.getUserId());
	}

	@Override
	public boolean isDisabled(Object object) {
		if (!_checkedUserIdsEnabled) {
			return isChecked(object);
		}

		return false;
	}

	private final long[] _checkedUserIds;
	private final boolean _checkedUserIdsEnabled;

}