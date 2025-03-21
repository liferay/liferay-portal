/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class RoleItemSelectorViewDisplayContext<T extends ItemSelectorCriterion>
	extends SearchContainerManagementToolbarDisplayContext {

	public RoleItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, T itemSelectorCriterion,
		String itemSelectedEventName, SearchContainer<Role> searchContainer,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_itemSelectorCriterion = itemSelectorCriterion;
		_itemSelectedEventName = itemSelectedEventName;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			(String)null
		).buildString();
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public T getItemSelectorCriterion() {
		return _itemSelectorCriterion;
	}

	@Override
	public String getSearchActionURL() {
		return String.valueOf(getPortletURL());
	}

	public SearchContainer<?> getSearchContainer() {
		return searchContainer;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "description"};
	}

	private final String _itemSelectedEventName;
	private final T _itemSelectorCriterion;

}