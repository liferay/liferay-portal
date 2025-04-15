/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.item.selector.web.internal.search;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountGroupAccountItemSelectorChecker
	extends EmptyOnClickRowChecker {

	public CommerceAccountGroupAccountItemSelectorChecker(
		RenderResponse renderResponse, AccountGroup accountGroup,
		AccountGroupRelLocalService accountGroupRelLocalService) {

		super(renderResponse);

		_accountGroup = accountGroup;
		_accountGroupRelLocalService = accountGroupRelLocalService;
	}

	@Override
	public boolean isChecked(Object object) {
		if (_accountGroup == null) {
			return false;
		}

		AccountEntry accountEntry = (AccountEntry)object;

		AccountGroupRel accountGroupRel =
			_accountGroupRelLocalService.fetchAccountGroupRel(
				_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId());

		if (accountGroupRel == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final AccountGroup _accountGroup;
	private final AccountGroupRelLocalService _accountGroupRelLocalService;

}