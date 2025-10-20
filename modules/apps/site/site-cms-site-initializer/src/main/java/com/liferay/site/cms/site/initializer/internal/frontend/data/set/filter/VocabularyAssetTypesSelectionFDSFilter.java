/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Noor Najjar
 */
@Component(service = FDSFilter.class)
public class VocabularyAssetTypesSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	public VocabularyAssetTypesSelectionFDSFilter(
		List<Map<String, String>> assetTypes) {

		_assetTypes = assetTypes;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "assetTypes";
	}

	@Override
	public String getItemKey() {
		return "assetTypes.type";
	}

	@Override
	public String getLabel() {
		return "asset-types";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			new ArrayList<>();

		for (Map<String, String> assetType : _assetTypes) {
			selectionFDSFilterItems.add(
				new SelectionFDSFilterItem(
					assetType.get("label"),
					String.valueOf(assetType.get("value"))));
		}

		return selectionFDSFilterItems;
	}

	private final List<Map<String, String>> _assetTypes;

}