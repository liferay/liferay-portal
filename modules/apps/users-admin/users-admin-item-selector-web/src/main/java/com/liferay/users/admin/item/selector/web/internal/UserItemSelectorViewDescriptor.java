/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class UserItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<User> {

	public UserItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, boolean multipleSelection,
		SearchContainer<User> searchContainer) {

		_multipleSelection = multipleSelection;
		_searchContainer = searchContainer;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public ItemDescriptor getItemDescriptor(User user) {
		return new UserItemDescriptor(_themeDisplay, user);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"first-name", "last-name", "screen-name"};
	}

	@Override
	public SearchContainer<User> getSearchContainer() throws PortalException {
		return _searchContainer;
	}

	@Override
	public TableItemView getTableItemView(User user) {
		return new UserTableItemView(user, _multipleSelection);
	}

	@Override
	public boolean isMultipleSelection() {
		return _multipleSelection;
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final boolean _multipleSelection;
	private final SearchContainer<User> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}