/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.criteria.internal.info.item;

import com.liferay.item.selector.BaseItemSelectorCriterionHandler;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorCriterionHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ItemSelectorCriterionHandler.class)
public class InfoItemItemSelectorCriterionHandler
	extends BaseItemSelectorCriterionHandler<InfoItemItemSelectorCriterion> {

	@Override
	public Class<InfoItemItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return InfoItemItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorView<InfoItemItemSelectorCriterion>>
		getItemSelectorViews(ItemSelectorCriterion itemSelectorCriterion) {

		return InfoItemItemSelectorCriterionHandlerUtil.
			getFilteredItemSelectorViews(
				itemSelectorCriterion,
				super.getItemSelectorViews(itemSelectorCriterion));
	}

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

}