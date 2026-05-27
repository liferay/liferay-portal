/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.filter;

import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author Tancredi Covioli
 */
public class AccountEntryValidatorClassNameSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	public AccountEntryValidatorClassNameSelectionFDSFilter(
		Collection<AccountEntryValidator> accountEntryValidators) {

		_accountEntryValidators = accountEntryValidators;
	}

	@Override
	public String getId() {
		return "className";
	}

	@Override
	public String getLabel() {
		return "class-name";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return TransformUtil.transform(
			_accountEntryValidators,
			accountEntryValidator -> {
				Class<?> clazz = accountEntryValidator.getClass();

				return new SelectionFDSFilterItem(
					clazz.getSimpleName(), clazz.getName());
			});
	}

	private final Collection<AccountEntryValidator> _accountEntryValidators;

}