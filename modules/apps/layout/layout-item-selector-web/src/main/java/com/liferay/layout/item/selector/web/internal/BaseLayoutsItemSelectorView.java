/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.LayoutItemSelectorReturnType;
import com.liferay.layout.item.selector.criterion.LayoutItemSelectorCriterion;
import com.liferay.layout.item.selector.view.LayoutItemSelectorView;
import com.liferay.layout.item.selector.web.internal.constants.LayoutsItemSelectorWebKeys;
import com.liferay.layout.item.selector.web.internal.display.context.LayoutItemSelectorViewDisplayContext;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;

/**
 * @author Roberto Díaz
 */
public abstract class BaseLayoutsItemSelectorView
	implements ItemSelectorView<LayoutItemSelectorCriterion>,
			   LayoutItemSelectorView {

	public static final String LAYOUT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT =
		"LAYOUT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT";

	@Override
	public Class<LayoutItemSelectorCriterion> getItemSelectorCriterionClass() {
		return LayoutItemSelectorCriterion.class;
	}

	public abstract ServletContext getServletContext();

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public abstract boolean isPrivateLayout();

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			LayoutItemSelectorCriterion layoutItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/layouts.jsp");

		LayoutItemSelectorViewDisplayContext
			layoutItemSelectorViewDisplayContext =
				new LayoutItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest,
					layoutItemSelectorCriterion, portletURL,
					itemSelectedEventName, isPrivateLayout());

		servletRequest.setAttribute(
			LayoutsItemSelectorWebKeys.
				LAYOUT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			layoutItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new LayoutItemSelectorReturnType(),
				new URLItemSelectorReturnType(),
				new UUIDItemSelectorReturnType()));

}