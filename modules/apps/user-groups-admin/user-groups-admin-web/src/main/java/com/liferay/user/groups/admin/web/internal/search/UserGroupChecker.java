/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Drew Brokke
 */
public class UserGroupChecker extends EmptyOnClickRowChecker {

	public UserGroupChecker(RenderResponse renderResponse) {
		super(renderResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		UserGroup userGroup = (UserGroup)object;

		if (!UserGroupPermissionUtil.contains(
				PermissionThreadLocal.getPermissionChecker(),
				userGroup.getUserGroupId(), ActionKeys.DELETE)) {

			return true;
		}

		return super.isDisabled(object);
	}

}