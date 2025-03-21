/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.UserGroup;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Geyson Silva
 */
public class UserGroupChecker extends EmptyOnClickRowChecker {

	public UserGroupChecker(RenderResponse renderResponse, Set<String> ids) {
		super(renderResponse);

		setRowIds("syncedUserGroupIds");

		_ids = ids;
	}

	@Override
	public boolean isChecked(Object object) {
		UserGroup userGroup = (UserGroup)object;

		return _ids.contains(String.valueOf(userGroup.getUserGroupId()));
	}

	private final Set<String> _ids;

}