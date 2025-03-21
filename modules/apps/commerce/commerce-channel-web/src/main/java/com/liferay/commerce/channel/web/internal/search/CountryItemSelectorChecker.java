/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Stefano Motta
 */
public class CountryItemSelectorChecker extends EmptyOnClickRowChecker {

	public CountryItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCountryIds) {

		super(renderResponse);

		_checkedCountryIds = checkedCountryIds;
	}

	@Override
	public boolean isChecked(Object object) {
		Country country = (Country)object;

		return ArrayUtil.contains(_checkedCountryIds, country.getCountryId());
	}

	@Override
	public boolean isDisabled(Object object) {
		Country country = (Country)object;

		return ArrayUtil.contains(_checkedCountryIds, country.getCountryId());
	}

	private final long[] _checkedCountryIds;

}