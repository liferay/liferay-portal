/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.portlet;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class LocalizedItemSelectorRendering {

	public static LocalizedItemSelectorRendering get(
		PortletRequest portletRequest) {

		return (LocalizedItemSelectorRendering)portletRequest.getAttribute(
			LocalizedItemSelectorRendering.class.getName());
	}

	public LocalizedItemSelectorRendering(
		Locale locale, ItemSelectorRendering itemSelectorRendering) {

		_locale = locale;
		_itemSelectorRendering = itemSelectorRendering;

		for (ItemSelectorViewRenderer itemSelectorViewRenderer :
				itemSelectorRendering.getItemSelectorViewRenderers()) {

			add(itemSelectorViewRenderer);
		}
	}

	public void add(ItemSelectorViewRenderer itemSelectorViewRenderer) {
		ItemSelectorView<?> itemSelectorView =
			itemSelectorViewRenderer.getItemSelectorView();

		String title = itemSelectorView.getTitle(_locale);

		ItemSelectorViewRenderer previousItemSelectorViewRenderer =
			_itemSelectorViewRenderers.put(title, itemSelectorViewRenderer);

		if (previousItemSelectorViewRenderer != null) {
			_navigationItems.removeIf(
				navigationItem -> title.equals(
					String.valueOf(navigationItem.get("label"))));
		}

		_navigationItems.add(
			navigationItem -> {
				navigationItem.setHref(
					itemSelectorViewRenderer.getPortletURL());
				navigationItem.setLabel(title);

				String selectedTab = _itemSelectorRendering.getSelectedTab();

				if (selectedTab.equals(title) ||
					(Validator.isNull(selectedTab) &&
					 _navigationItems.isEmpty())) {

					navigationItem.setActive(true);

					_activeNavigationItem = navigationItem;
					_selectedNavigationItemLabel = title;
				}
			});
	}

	public NavigationItem getActiveNavigationItem() {
		return _activeNavigationItem;
	}

	public List<NavigationItem> getNavigationItems() {
		return _navigationItems;
	}

	public ItemSelectorViewRenderer getSelectedItemSelectorViewRenderer() {
		return _itemSelectorViewRenderers.get(_selectedNavigationItemLabel);
	}

	public VerticalNavItemList getVerticalNavItemList() {
		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (NavigationItem navigationItem : getNavigationItems()) {
			verticalNavItemList.add(
				verticalNavItem -> {
					String name = GetterUtil.getString(
						navigationItem.get("label"));

					verticalNavItem.setActive(
						GetterUtil.getBoolean(navigationItem.get("active")));
					verticalNavItem.setHref(
						GetterUtil.getString(navigationItem.get("href")));
					verticalNavItem.setLabel(name);
					verticalNavItem.setId(name);
				});
		}

		return verticalNavItemList;
	}

	public void store(PortletRequest portletRequest) {
		portletRequest.setAttribute(
			LocalizedItemSelectorRendering.class.getName(), this);
	}

	private NavigationItem _activeNavigationItem;
	private final ItemSelectorRendering _itemSelectorRendering;
	private final Map<String, ItemSelectorViewRenderer>
		_itemSelectorViewRenderers = new HashMap<>();
	private final Locale _locale;
	private final NavigationItemList _navigationItems =
		new NavigationItemList();
	private String _selectedNavigationItemLabel;

}