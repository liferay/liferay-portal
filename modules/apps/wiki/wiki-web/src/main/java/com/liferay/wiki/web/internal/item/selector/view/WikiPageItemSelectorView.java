/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector.view;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.wiki.item.selector.WikiPageItemSelectorCriterion;
import com.liferay.wiki.item.selector.WikiPageTitleItemSelectorReturnType;
import com.liferay.wiki.item.selector.WikiPageURLItemSelectorReturnType;
import com.liferay.wiki.item.selector.constants.WikiItemSelectorViewConstants;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.web.internal.item.selector.constants.WikiItemSelectorWebKeys;
import com.liferay.wiki.web.internal.item.selector.view.display.context.WikiPageItemSelectorViewDisplayContext;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "item.selector.view.key=" + WikiItemSelectorViewConstants.ITEM_SELECTOR_VIEW_KEY,
	service = ItemSelectorView.class
)
public class WikiPageItemSelectorView
	implements ItemSelectorView<WikiPageItemSelectorCriterion> {

	@Override
	public Class<WikiPageItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return WikiPageItemSelectorCriterion.class;
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
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, WikiPageItemSelectorView.class);

		return ResourceBundleUtil.getString(resourceBundle, "wiki-pages");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			WikiPageItemSelectorCriterion wikiPageItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/item/selector/wiki_pages.jsp");

		WikiPageItemSelectorViewDisplayContext
			wikiPageItemSelectorViewDisplayContext =
				new WikiPageItemSelectorViewDisplayContext(
					wikiPageItemSelectorCriterion, this, _wikiNodeLocalService,
					_itemSelectorReturnTypeResolverHandler,
					itemSelectedEventName, search, portletURL);

		servletRequest.setAttribute(
			WikiItemSelectorWebKeys.
				WIKI_PAGE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			wikiPageItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new WikiPageURLItemSelectorReturnType(),
				new WikiPageTitleItemSelectorReturnType()));

	@Reference
	private ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.wiki.web)")
	private ServletContext _servletContext;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

}