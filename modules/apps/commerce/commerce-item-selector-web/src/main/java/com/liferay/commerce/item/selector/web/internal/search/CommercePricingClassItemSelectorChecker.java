/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.search;

import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.util.SetUtil;

import jakarta.portlet.RenderResponse;

import java.util.Set;

/**
 * @author Riccardo Alberti
 */
public class CommercePricingClassItemSelectorChecker
	extends EmptyOnClickRowChecker {

	public CommercePricingClassItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedCommercePricingClassIds) {

		super(renderResponse);

		_checkedCommercePricingClassIds = SetUtil.fromArray(
			checkedCommercePricingClassIds);
	}

	@Override
	public boolean isChecked(Object object) {
		CommercePricingClass commercePricingClass =
			(CommercePricingClass)object;

		return _checkedCommercePricingClassIds.contains(
			commercePricingClass.getCommercePricingClassId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final Set<Long> _checkedCommercePricingClassIds;

}