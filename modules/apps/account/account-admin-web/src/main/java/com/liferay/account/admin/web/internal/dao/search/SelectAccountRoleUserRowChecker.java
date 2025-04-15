/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AccountUserDisplay;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.account.service.AccountRoleLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;

import jakarta.portlet.PortletResponse;

/**
 * @author Pei-Jung Lan
 */
public class SelectAccountRoleUserRowChecker extends EmptyOnClickRowChecker {

	public SelectAccountRoleUserRowChecker(
		PortletResponse portletResponse, long accountEntryId,
		long accountRoleId) {

		super(portletResponse);

		_accountEntryId = accountEntryId;
		_accountRoleId = accountRoleId;
	}

	@Override
	public boolean isChecked(Object object) {
		AccountUserDisplay accountUserDisplay = (AccountUserDisplay)object;

		AccountEntry accountEntry =
			AccountEntryLocalServiceUtil.fetchAccountEntry(_accountEntryId);

		AccountRole accountRole = AccountRoleLocalServiceUtil.fetchAccountRole(
			_accountRoleId);

		return UserGroupRoleLocalServiceUtil.hasUserGroupRole(
			accountUserDisplay.getUserId(),
			accountEntry.getAccountEntryGroupId(), accountRole.getRoleId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final long _accountEntryId;
	private final long _accountRoleId;

}