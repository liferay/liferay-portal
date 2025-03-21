/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceCountryItemSelectorChecker extends EmptyOnClickRowChecker {

	public CommerceCountryItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCountryIds) {

		super(renderResponse);

		_checkedCountryIds = SetUtil.fromArray(checkedCountryIds);
	}

	@Override
	public boolean isChecked(Object object) {
		Country country = (Country)object;

		return _checkedCountryIds.contains(country.getCountryId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final Set<Long> _checkedCountryIds;

}