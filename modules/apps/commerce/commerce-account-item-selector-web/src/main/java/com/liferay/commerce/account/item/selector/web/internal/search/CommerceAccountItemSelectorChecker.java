/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.item.selector.web.internal.search;

import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 * @author Ethan Bustad
 */
public class CommerceAccountItemSelectorChecker extends EmptyOnClickRowChecker {

	public CommerceAccountItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCommerceAccountIds) {

		super(renderResponse);

		_checkedCommerceAccountIds = SetUtil.fromArray(
			checkedCommerceAccountIds);
	}

	@Override
	public boolean isChecked(Object object) {
		AccountEntry accountEntry = (AccountEntry)object;

		return _checkedCommerceAccountIds.contains(
			accountEntry.getAccountEntryId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final Set<Long> _checkedCommerceAccountIds;

}