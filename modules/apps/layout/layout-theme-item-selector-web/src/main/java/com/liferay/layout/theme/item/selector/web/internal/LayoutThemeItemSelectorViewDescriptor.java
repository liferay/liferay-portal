/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.theme.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.theme.item.selector.web.internal.display.context.LayoutThemeItemSelectorDisplayContext;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Theme;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Stefan Tanasie
 */
public class LayoutThemeItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<Theme> {

	public LayoutThemeItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest,
		LayoutThemeItemSelectorDisplayContext
			layoutThemeItemSelectorDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_layoutThemeItemSelectorDisplayContext =
			layoutThemeItemSelectorDisplayContext;
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"icon", "descriptive", "list"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(Theme theme) {
		return new LayoutThemeItemDescriptor(theme, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	@Override
	public SearchContainer<Theme> getSearchContainer() {
		return _layoutThemeItemSelectorDisplayContext.
			getThemesSearchContainer();
	}

	@Override
	public TableItemView getTableItemView(Theme theme) {
		return new LayoutThemeTableItemView(theme, _httpServletRequest);
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutThemeItemSelectorDisplayContext
		_layoutThemeItemSelectorDisplayContext;

}