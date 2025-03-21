/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector;

import com.liferay.depot.web.internal.application.controller.DepotApplicationController;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.item.selector.ItemSelectorViewRendererCustomizer;
import com.liferay.item.selector.PortletItemSelectorView;
import com.liferay.item.selector.criteria.asset.criterion.AssetEntryItemSelectorCriterion;
import com.liferay.item.selector.criteria.audio.criterion.AudioItemSelectorCriterion;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.item.selector.criteria.video.criterion.VideoItemSelectorCriterion;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ItemSelectorViewRendererCustomizer.class)
public class DepotItemSelectorViewRendererCustomizer
	implements ItemSelectorViewRendererCustomizer {

	@Override
	public ItemSelectorViewRenderer customizeItemSelectorViewRenderer(
		ItemSelectorViewRenderer itemSelectorViewRenderer) {

		ItemSelectorCriterion itemSelectorCriterion =
			itemSelectorViewRenderer.getItemSelectorCriterion();

		if (itemSelectorCriterion instanceof AssetEntryItemSelectorCriterion) {
			AssetEntryItemSelectorCriterion assetEntryItemSelectorCriterion =
				(AssetEntryItemSelectorCriterion)itemSelectorCriterion;

			return new DepotItemSelectorViewRenderer(
				assetEntryItemSelectorCriterion.getTypeSelection(),
				_depotApplicationController, itemSelectorViewRenderer, _portal,
				Collections.emptyList(), _servletContext);
		}

		ItemSelectorView<ItemSelectorCriterion> itemSelectorView =
			itemSelectorViewRenderer.getItemSelectorView();

		if (!(itemSelectorView instanceof PortletItemSelectorView)) {
			return itemSelectorViewRenderer;
		}

		PortletItemSelectorView<? extends ItemSelectorCriterion>
			portletItemSelectorView =
				(PortletItemSelectorView<?>)itemSelectorView;

		List<String> portletIds = portletItemSelectorView.getPortletIds();

		if (ListUtil.isEmpty(portletIds)) {
			return itemSelectorViewRenderer;
		}

		return new DepotItemSelectorViewRenderer(
			StringPool.BLANK, _depotApplicationController,
			itemSelectorViewRenderer, _portal, portletIds, _servletContext);
	}

	@Override
	public Collection<Class<? extends ItemSelectorCriterion>>
		getSupportedItemSelectorCriterionClasses() {

		return _supportedItemSelectorCriterionClasses;
	}

	private static final List<Class<? extends ItemSelectorCriterion>>
		_supportedItemSelectorCriterionClasses = Arrays.asList(
			AssetEntryItemSelectorCriterion.class,
			AudioItemSelectorCriterion.class, FileItemSelectorCriterion.class,
			ImageItemSelectorCriterion.class,
			InfoCollectionProviderItemSelectorCriterion.class,
			InfoItemItemSelectorCriterion.class,
			LayoutItemSelectorCriterion.class,
			VideoItemSelectorCriterion.class);

	@Reference
	private DepotApplicationController _depotApplicationController;

	@Reference
	private Portal _portal;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.depot.web)")
	private ServletContext _servletContext;

}