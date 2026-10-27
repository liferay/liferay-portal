/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.item.selector.DepotGroupItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(
	property = "item.selector.view.order:Integer=600",
	service = ItemSelectorView.class
)
public class DesignLibraryDepotItemSelectorView
	extends BaseDepotItemSelectorView<DepotGroupItemSelectorCriterion> {

	@Override
	public Class<DepotGroupItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return DepotGroupItemSelectorCriterion.class;
	}

	@Override
	public boolean isVisible(
		DepotGroupItemSelectorCriterion depotGroupItemSelectorCriterion,
		ThemeDisplay themeDisplay) {

		if ((depotGroupItemSelectorCriterion.getDepotEntryType() !=
				DepotConstants.TYPE_DESIGN_LIBRARY) ||
			!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-57283")) {

			return false;
		}

		return super.isVisible(depotGroupItemSelectorCriterion, themeDisplay);
	}

	@Override
	protected void customizeGroupItemSelectorCriterion(
		GroupItemSelectorCriterion groupItemSelectorCriterion) {
	}

	@Override
	protected String getTitleLanguageKey() {
		return "design-libraries";
	}

}