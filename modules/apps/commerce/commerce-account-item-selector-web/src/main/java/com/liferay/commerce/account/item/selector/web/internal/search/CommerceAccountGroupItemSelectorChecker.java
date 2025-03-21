/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.item.selector.web.internal.search;

import com.liferay.account.model.AccountGroup;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountGroupItemSelectorChecker
	extends EmptyOnClickRowChecker {

	public CommerceAccountGroupItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCommerceAccountGroupIds) {

		super(renderResponse);

		_checkedCommerceAccountGroupIds = SetUtil.fromArray(
			checkedCommerceAccountGroupIds);
	}

	@Override
	public boolean isChecked(Object object) {
		AccountGroup accountGroup = (AccountGroup)object;

		return _checkedCommerceAccountGroupIds.contains(
			accountGroup.getAccountGroupId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final Set<Long> _checkedCommerceAccountGroupIds;

}