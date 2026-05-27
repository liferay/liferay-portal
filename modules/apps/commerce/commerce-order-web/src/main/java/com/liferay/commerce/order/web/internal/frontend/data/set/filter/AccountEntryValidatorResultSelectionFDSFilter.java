/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.filter;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Tancredi Covioli
 */
public class AccountEntryValidatorResultSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "resultStatus";
	}

	@Override
	public String getLabel() {
		return "result";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "failed"),
				AccountEntryValidatorConstants.RESULT_FAILURE),
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "manual"),
				AccountEntryValidatorConstants.RESULT_MANUAL),
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "success"),
				AccountEntryValidatorConstants.RESULT_SUCCESS),
			new SelectionFDSFilterItem(
				LanguageUtil.get(locale, "warning"),
				AccountEntryValidatorConstants.RESULT_WARNING));
	}

}