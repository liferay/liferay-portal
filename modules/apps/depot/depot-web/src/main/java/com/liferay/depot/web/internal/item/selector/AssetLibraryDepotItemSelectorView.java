/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bryan Engler
 */
@Component(
	property = "item.selector.view.order:Integer=400",
	service = ItemSelectorView.class
)
public class AssetLibraryDepotItemSelectorView
	extends BaseDepotItemSelectorView<GroupItemSelectorCriterion> {

	@Override
	public Class<GroupItemSelectorCriterion> getItemSelectorCriterionClass() {
		return GroupItemSelectorCriterion.class;
	}

	@Override
	protected void customizeGroupItemSelectorCriterion(
		GroupItemSelectorCriterion groupItemSelectorCriterion) {
	}

	@Override
	protected String getTitleLanguageKey() {
		return "asset-libraries";
	}

}