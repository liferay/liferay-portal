/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class RoleItemSelectorChecker extends EmptyOnClickRowChecker {

	public RoleItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedRoleIds,
		String[] excludedRoleNames) {

		super(renderResponse);

		_checkedRoleIds = checkedRoleIds;
		_excludedRoleNames = excludedRoleNames;
	}

	@Override
	public boolean isChecked(Object object) {
		Role role = (Role)object;

		return ArrayUtil.contains(_checkedRoleIds, role.getRoleId());
	}

	@Override
	public boolean isDisabled(Object object) {
		Role role = (Role)object;

		return ArrayUtil.contains(_excludedRoleNames, role.getName());
	}

	private final long[] _checkedRoleIds;
	private final String[] _excludedRoleNames;

}