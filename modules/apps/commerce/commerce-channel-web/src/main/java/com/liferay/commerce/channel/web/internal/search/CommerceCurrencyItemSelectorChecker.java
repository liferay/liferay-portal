/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.search;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Fabio Monaco
 */
public class CommerceCurrencyItemSelectorChecker
	extends EmptyOnClickRowChecker {

	public CommerceCurrencyItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCommerceCurrencyIds) {

		super(renderResponse);

		_checkedCommerceCurrencyIds = checkedCommerceCurrencyIds;
	}

	@Override
	public boolean isChecked(Object object) {
		CommerceCurrency commerceCurrency = (CommerceCurrency)object;

		return ArrayUtil.contains(
			_checkedCommerceCurrencyIds,
			commerceCurrency.getCommerceCurrencyId());
	}

	@Override
	public boolean isDisabled(Object object) {
		CommerceCurrency commerceCurrency = (CommerceCurrency)object;

		return ArrayUtil.contains(
			_checkedCommerceCurrencyIds,
			commerceCurrency.getCommerceCurrencyId());
	}

	private final long[] _checkedCommerceCurrencyIds;

}