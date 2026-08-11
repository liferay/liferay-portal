/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.web.internal.frontend.data.set.filter;

import com.liferay.commerce.currency.web.internal.constants.CommerceCurrencyFDSNames;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "frontend.data.set.name=" + CommerceCurrencyFDSNames.COMMERCE_CURRENCIES,
	service = FDSFilter.class
)
public class CommerceCurrencyActiveSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.BOOLEAN;
	}

	@Override
	public String getId() {
		return "active";
	}

	@Override
	public String getLabel() {
		return "active";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem(_language.get(locale, "yes"), true),
			new SelectionFDSFilterItem(_language.get(locale, "no"), false));
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Reference
	private Language _language;

}