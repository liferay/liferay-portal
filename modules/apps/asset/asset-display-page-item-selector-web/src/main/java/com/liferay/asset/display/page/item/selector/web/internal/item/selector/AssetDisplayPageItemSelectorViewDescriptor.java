/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.item.selector;

import com.liferay.asset.display.page.item.selector.web.internal.display.context.AssetDisplayPagesItemSelectorViewDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletException;

/**
 * @author Eudaldo Alonso
 */
public class AssetDisplayPageItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<LayoutPageTemplateEntry> {

	public AssetDisplayPageItemSelectorViewDescriptor(
		AssetDisplayPagesItemSelectorViewDisplayContext
			assetDisplayPagesItemSelectorViewDisplayContext) {

		_assetDisplayPagesItemSelectorViewDisplayContext =
			assetDisplayPagesItemSelectorViewDisplayContext;
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new AssetDisplayPageItemDescriptor(layoutPageTemplateEntry);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new AssetEntryItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name", "create-date"};
	}

	@Override
	public SearchContainer<LayoutPageTemplateEntry> getSearchContainer()
		throws PortalException {

		try {
			return _assetDisplayPagesItemSelectorViewDisplayContext.
				getAssetDisplayPageSearchContainer();
		}
		catch (PortletException portletException) {
			throw new PortalException(portletException);
		}
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final AssetDisplayPagesItemSelectorViewDisplayContext
		_assetDisplayPagesItemSelectorViewDisplayContext;

}