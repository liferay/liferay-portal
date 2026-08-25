/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Cikos
 */
@Component(
	property = {
		"frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
		"frontend.data.set.name=" + FDSSampleFDSNames.DELEGATED_FILTERS
	},
	service = FDSFilter.class
)
public class SizeSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "size";
	}

	@Override
	public String getLabel() {
		return "size";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem("Tiny", "Tiny"),
			new SelectionFDSFilterItem("Small", "Small"),
			new SelectionFDSFilterItem("Medium", "Medium"),
			new SelectionFDSFilterItem("Large", "Large"),
			new SelectionFDSFilterItem("Huge", "Huge"),
			new SelectionFDSFilterItem("Gargantuan", "Gargantuan"));
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

}