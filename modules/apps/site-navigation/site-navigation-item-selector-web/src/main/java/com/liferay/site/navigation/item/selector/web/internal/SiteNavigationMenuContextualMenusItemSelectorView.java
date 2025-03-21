/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.language.Language;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorCriterion;
import com.liferay.site.navigation.item.selector.SiteNavigationMenuItemSelectorReturnType;
import com.liferay.site.navigation.item.selector.web.internal.constants.SiteNavigationItemSelectorWebKeys;
import com.liferay.site.navigation.item.selector.web.internal.display.context.SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext;

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
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "item.selector.view.order:Integer=400",
	service = ItemSelectorView.class
)
public class SiteNavigationMenuContextualMenusItemSelectorView
	implements ItemSelectorView<SiteNavigationMenuItemSelectorCriterion> {

	@Override
	public Class<SiteNavigationMenuItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return SiteNavigationMenuItemSelectorCriterion.class;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "contextual-menus");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			SiteNavigationMenuItemSelectorCriterion
				siteNavigationMenuItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/view_site_navigation_menu_contextual_menus.jsp");

		SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext
			siteNavigationMenuContextualMenusItemSelectorViewDisplayContext =
				new SiteNavigationMenuContextualMenusItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName);

		servletRequest.setAttribute(
			SiteNavigationItemSelectorWebKeys.
				SITE_NAVIGATION_MENU_CONTEXTUAL_MENUS_ITEM_SELECTOR_DISPLAY_CONTEXT,
			siteNavigationMenuContextualMenusItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new SiteNavigationMenuItemSelectorReturnType());

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.navigation.item.selector.web)"
	)
	private ServletContext _servletContext;

}