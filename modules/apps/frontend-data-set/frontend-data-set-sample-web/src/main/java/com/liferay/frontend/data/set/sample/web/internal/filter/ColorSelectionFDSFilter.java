/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Cikos
 */
@Component(
	property = {
		"frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
		"frontend.data.set.name=" + FDSSampleFDSNames.HIDDEN_EXCLUDE_TOGGLE
	},
	service = FDSFilter.class
)
public class ColorSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "color";
	}

	@Override
	public String getLabel() {
		return "color";
	}

	@Override
	public Map<String, Object> getPreloadedData() {
		return HashMapBuilder.<String, Object>put(
			"exclude", false
		).put(
			"selectedItems",
			ListUtil.fromArray(
				new SelectionFDSFilterItem("Blue", "Blue"),
				new SelectionFDSFilterItem("Green", "Green"),
				new SelectionFDSFilterItem("Yellow", "Yellow"))
		).build();
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem("Blue", "Blue"),
			new SelectionFDSFilterItem("Green", "Green"),
			new SelectionFDSFilterItem("Red", "Red"),
			new SelectionFDSFilterItem("Yellow", "Yellow"));
	}

}