/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector.view;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.wiki.item.selector.WikiAttachmentItemSelectorCriterion;
import com.liferay.wiki.item.selector.constants.WikiItemSelectorViewConstants;
import com.liferay.wiki.web.internal.item.selector.constants.WikiItemSelectorWebKeys;
import com.liferay.wiki.web.internal.item.selector.view.display.context.WikiAttachmentItemSelectorViewDisplayContext;

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
 * @author Iván Zaera
 * @author Roberto Díaz
 */
@Component(
	property = "item.selector.view.key=" + WikiItemSelectorViewConstants.ITEM_SELECTOR_VIEW_KEY,
	service = ItemSelectorView.class
)
public class WikiAttachmentItemSelectorView
	implements ItemSelectorView<WikiAttachmentItemSelectorCriterion> {

	@Override
	public Class<WikiAttachmentItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return WikiAttachmentItemSelectorCriterion.class;
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
		return _language.get(locale, "page-attachments");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			WikiAttachmentItemSelectorCriterion
				wikiAttachmentItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				"/item/selector/wiki_page_attachments.jsp");

		WikiAttachmentItemSelectorViewDisplayContext
			wikiAttachmentItemSelectorViewDisplayContext =
				new WikiAttachmentItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName,
					_itemSelectorReturnTypeResolverHandler, portletURL, search,
					wikiAttachmentItemSelectorCriterion, this);

		servletRequest.setAttribute(
			WikiItemSelectorWebKeys.
				WIKI_ATTACHMENT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			wikiAttachmentItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new FileEntryItemSelectorReturnType());

	@Reference
	private ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.wiki.web)")
	private ServletContext _servletContext;

}