/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorCriterionHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.web.internal.item.selector.MediaItemSelectorCriterion;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

/**
 * @author Iván Zaera
 */
public class MediaItemSelectorCriterionHandler
	implements ItemSelectorCriterionHandler<MediaItemSelectorCriterion> {

	@Override
	public Class<MediaItemSelectorCriterion> getItemSelectorCriterionClass() {
		return MediaItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorView<MediaItemSelectorCriterion>>
		getItemSelectorViews(
			MediaItemSelectorCriterion mediaItemSelectorCriterion) {

		return ListUtil.fromArray(new MediaItemSelectorView());
	}

}