/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AccountRoleDisplay;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;

import jakarta.portlet.PortletResponse;

/**
 * @author Pei-Jung Lan
 */
public class AccountRoleRowChecker extends EmptyOnClickRowChecker {

	public AccountRoleRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		AccountRoleDisplay accountRoleDisplay = (AccountRoleDisplay)object;

		Role role = accountRoleDisplay.getRole();

		if ((role.getType() == RoleConstants.TYPE_ACCOUNT) &&
			AccountRoleConstants.isSharedRole(role)) {

			return true;
		}

		return false;
	}

}