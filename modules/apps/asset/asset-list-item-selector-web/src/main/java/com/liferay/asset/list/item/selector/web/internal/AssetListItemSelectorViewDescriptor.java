/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal;

import com.liferay.asset.list.item.selector.web.internal.display.context.AssetListEntryItemSelectorDisplayContext;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class AssetListItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<AssetListEntry> {

	public AssetListItemSelectorViewDescriptor(
		AssetListEntryItemSelectorDisplayContext
			assetListEntryItemSelectorDisplayContext,
		HttpServletRequest httpServletRequest) {

		_assetListEntryItemSelectorDisplayContext =
			assetListEntryItemSelectorDisplayContext;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon", "descriptive", "list"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(AssetListEntry assetListEntry) {
		return new AssetListItemDescriptor(
			assetListEntry, _assetListEntryItemSelectorDisplayContext,
			_httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new InfoListItemSelectorReturnType();
	}

	@Override
	public SearchContainer<AssetListEntry> getSearchContainer() {
		return _assetListEntryItemSelectorDisplayContext.getSearchContainer();
	}

	@Override
	public TableItemView getTableItemView(AssetListEntry assetListEntry) {
		return new AssetListTableItemView(
			assetListEntry, _assetListEntryItemSelectorDisplayContext);
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final AssetListEntryItemSelectorDisplayContext
		_assetListEntryItemSelectorDisplayContext;
	private final HttpServletRequest _httpServletRequest;

}