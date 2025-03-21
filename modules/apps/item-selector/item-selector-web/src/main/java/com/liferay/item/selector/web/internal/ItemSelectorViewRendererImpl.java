/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewRenderer;
import com.liferay.taglib.util.PortalIncludeUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Iván Zaera
 */
public class ItemSelectorViewRendererImpl implements ItemSelectorViewRenderer {

	public ItemSelectorViewRendererImpl(
		ItemSelectorView<ItemSelectorCriterion> itemSelectorView,
		ItemSelectorCriterion itemSelectorCriterion, PortletURL portletURL,
		String itemSelectedEventName, boolean search) {

		_itemSelectorView = itemSelectorView;
		_itemSelectorCriterion = itemSelectorCriterion;
		_portletURL = portletURL;
		_itemSelectedEventName = itemSelectedEventName;
		_search = search;
	}

	@Override
	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	@Override
	public ItemSelectorCriterion getItemSelectorCriterion() {
		return _itemSelectorCriterion;
	}

	@Override
	public ItemSelectorView<ItemSelectorCriterion> getItemSelectorView() {
		return _itemSelectorView;
	}

	@Override
	public PortletURL getPortletURL() {
		return _portletURL;
	}

	@Override
	public void renderHTML(PageContext pageContext)
		throws IOException, ServletException {

		PortalIncludeUtil.include(
			pageContext,
			new PortalIncludeUtil.HTMLRenderer() {

				@Override
				public void renderHTML(
						HttpServletRequest httpServletRequest,
						HttpServletResponse httpServletResponse)
					throws IOException, ServletException {

					_itemSelectorView.renderHTML(
						httpServletRequest, httpServletResponse,
						_itemSelectorCriterion, _portletURL,
						_itemSelectedEventName, _search);
				}

			});
	}

	private final String _itemSelectedEventName;
	private final ItemSelectorCriterion _itemSelectorCriterion;
	private final ItemSelectorView<ItemSelectorCriterion> _itemSelectorView;
	private final PortletURL _portletURL;
	private final boolean _search;

}