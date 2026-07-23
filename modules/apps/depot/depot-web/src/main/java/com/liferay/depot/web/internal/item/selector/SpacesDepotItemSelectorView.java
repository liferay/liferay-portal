/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bryan Engler
 */
@Component(
	property = "item.selector.view.order:Integer=500",
	service = ItemSelectorView.class
)
public class SpacesDepotItemSelectorView extends BaseDepotItemSelectorView {

	@Override
	public boolean isVisible(
		GroupItemSelectorCriterion groupItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		return super.isVisible(groupItemSelectorCriterion, themeDisplay);
	}

	@Override
	protected void customizeGroupItemSelectorCriterion(
		GroupItemSelectorCriterion groupItemSelectorCriterion) {

		groupItemSelectorCriterion.setDepotEntryType(DepotConstants.TYPE_SPACE);
	}

	@Override
	protected String getTitleLanguageKey() {
		return "spaces";
	}

}