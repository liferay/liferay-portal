/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.sample.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Carlos Lancha
 */
public class ClaySampleManagementToolbarsDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public ClaySampleManagementToolbarsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (_actionDropdownItems != null) {
			return _actionDropdownItems;
		}

		_actionDropdownItems = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref("#edit");
				dropdownItem.setLabel("Edit");
			}
		).add(
			dropdownItem -> {
				dropdownItem.setIcon("download");
				dropdownItem.setLabel("Download");
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref("#delete");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel("Delete");
				dropdownItem.setQuickAction(true);
			}
		).build();

		return _actionDropdownItems;
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (_creationMenu != null) {
			return _creationMenu;
		}

		_creationMenu = CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#1");
				dropdownItem.setLabel("Sample 1");
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#2");
				dropdownItem.setLabel("Sample 2");
			}
		).addFavoriteDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#3");
				dropdownItem.setLabel("Favorite 1");
			}
		).addFavoriteDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#4");
				dropdownItem.setLabel("Favorite 2");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#5");
				dropdownItem.setLabel("Secondary 1");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#6");
				dropdownItem.setLabel("Secondary 2");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#7");
				dropdownItem.setLabel("Secondary 3");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#8");
				dropdownItem.setLabel("Secondary 4");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#9");
				dropdownItem.setLabel("Secondary 5");
			}
		).addRestDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#10");
				dropdownItem.setLabel("Secondary 6");
			}
		).build();

		_creationMenu.put("maxTotalItems", 8);

		return _creationMenu;
	}

	@Override
	public List<DropdownItem> getFilterNavigationDropdownItems() {
		if (_filterDropdownItems != null) {
			return _filterDropdownItems;
		}

		_filterDropdownItems = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref("#1");
				dropdownItem.setLabel("Filter 1");
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref("#2");
				dropdownItem.setLabel("Filter 2");
			}
		).build();

		return _filterDropdownItems;
	}

	@Override
	public String getFilterNavigationDropdownItemsLabel() {
		return "Filter By";
	}

	@Override
	public List<DropdownItem> getOrderByDropdownItems() {
		if (_orderDropdownItems != null) {
			return _orderDropdownItems;
		}

		_orderDropdownItems = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref("#3");
				dropdownItem.setLabel("Order 1");
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref("#4");
				dropdownItem.setLabel("Order 2");
			}
		).build();

		return _orderDropdownItems;
	}

	@Override
	public String getOrderByDropdownItemsLabel() {
		return "Order By";
	}

	@Override
	public String getSearchActionURL() {
		return "#search-action-url";
	}

	@Override
	public Boolean getSupportsBulkActions() {
		return true;
	}

	@Override
	public List<ViewTypeItem> getViewTypeItems() {
		if (_viewTypeItems != null) {
			return _viewTypeItems;
		}

		_viewTypeItems = new ViewTypeItemList() {
			{
				addCardViewTypeItem(
					viewTypeItem -> {
						viewTypeItem.setActive(true);
						viewTypeItem.setLabel("Card");
					});

				addListViewTypeItem(
					viewTypeItem -> viewTypeItem.setLabel("List"));

				addTableViewTypeItem(
					viewTypeItem -> viewTypeItem.setLabel("Table"));
			}
		};

		return _viewTypeItems;
	}

	@Override
	public Boolean isShowInfoButton() {
		return true;
	}

	private List<DropdownItem> _actionDropdownItems;
	private CreationMenu _creationMenu;
	private List<DropdownItem> _filterDropdownItems;
	private List<DropdownItem> _orderDropdownItems;
	private List<ViewTypeItem> _viewTypeItems;

}