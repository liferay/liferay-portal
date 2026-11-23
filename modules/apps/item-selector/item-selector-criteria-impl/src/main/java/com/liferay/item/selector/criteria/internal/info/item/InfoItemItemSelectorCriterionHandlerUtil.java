/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.criteria.internal.info.item;

import com.liferay.info.item.selector.InfoItemSelectorView;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;

/**
 * @author Jürgen Kappler
 */
public class InfoItemItemSelectorCriterionHandlerUtil {

	public static List<ItemSelectorView<InfoItemItemSelectorCriterion>>
		getFilteredItemSelectorViews(
			ItemSelectorCriterion itemSelectorCriterion,
			List<ItemSelectorView<InfoItemItemSelectorCriterion>>
				itemSelectorViews) {

		InfoItemItemSelectorCriterion infoItemItemSelectorCriterion =
			(InfoItemItemSelectorCriterion)itemSelectorCriterion;

		if (Validator.isNull(infoItemItemSelectorCriterion.getItemType())) {
			return itemSelectorViews;
		}

		return ListUtil.filter(
			itemSelectorViews,
			itemSelectorView -> {
				if (!(itemSelectorView instanceof InfoItemSelectorView)) {
					return false;
				}

				InfoItemSelectorView infoItemSelectorView =
					(InfoItemSelectorView)itemSelectorView;

				return Objects.equals(
					infoItemSelectorView.getClassName(),
					infoItemItemSelectorCriterion.getItemType());
			});
	}

}