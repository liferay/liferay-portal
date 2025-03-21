/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.item.selector;

import com.liferay.asset.publisher.web.internal.display.context.ItemSelectorViewDisplayContext;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class SitesItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<Group> {

	public SitesItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest,
		ItemSelectorViewDisplayContext itemSelectorViewDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_itemSelectorViewDisplayContext = itemSelectorViewDisplayContext;
	}

	@Override
	public ItemDescriptor getItemDescriptor(Group group) {
		return new SitesItemDescriptor(group, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new InfoListProviderItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name", "type"};
	}

	public SearchContainer<Group> getSearchContainer() {
		try {
			return _itemSelectorViewDisplayContext.getGroupSearch();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public boolean isShowSearch() {
		return _itemSelectorViewDisplayContext.isShowSearch();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitesItemSelectorViewDescriptor.class);

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelectorViewDisplayContext
		_itemSelectorViewDisplayContext;

}