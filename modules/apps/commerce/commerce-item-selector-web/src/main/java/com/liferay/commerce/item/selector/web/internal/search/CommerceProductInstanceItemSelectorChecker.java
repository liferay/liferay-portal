/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.search;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceProductInstanceItemSelectorChecker
	extends EmptyOnClickRowChecker {

	public CommerceProductInstanceItemSelectorChecker(
		RenderResponse renderResponse, CommercePriceList commercePriceList,
		CommercePriceEntryLocalService commercePriceEntryLocalService) {

		super(renderResponse);

		_commercePriceList = commercePriceList;
		_commercePriceEntryLocalService = commercePriceEntryLocalService;
	}

	@Override
	public boolean isChecked(Object object) {
		if (_commercePriceList == null) {
			return false;
		}

		CPInstance cpInstance = (CPInstance)object;

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				_commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid(), StringPool.BLANK);

		if (commercePriceEntry == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final CommercePriceEntryLocalService
		_commercePriceEntryLocalService;
	private final CommercePriceList _commercePriceList;

}