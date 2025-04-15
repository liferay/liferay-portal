/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnTypeResolverHandler;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.knowledge.base.item.selector.KBAttachmentItemSelectorCriterion;
import com.liferay.knowledge.base.item.selector.web.internal.constants.KBItemSelectorWebKeys;
import com.liferay.knowledge.base.item.selector.web.internal.display.context.KBAttachmentItemSelectorViewDisplayContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

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
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = ItemSelectorView.class)
public class KBAttachmentItemSelectorView
	implements ItemSelectorView<KBAttachmentItemSelectorCriterion> {

	@Override
	public Class<KBAttachmentItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return KBAttachmentItemSelectorCriterion.class;
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
			locale, KBAttachmentItemSelectorView.class);

		return ResourceBundleUtil.getString(
			resourceBundle, "article-attachments");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			KBAttachmentItemSelectorCriterion kbAttachmentItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/kb_article_attachments.jsp");

		KBAttachmentItemSelectorViewDisplayContext
			kbAttachmentItemSelectorViewDisplayContext =
				new KBAttachmentItemSelectorViewDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName,
					_itemSelectorReturnTypeResolverHandler,
					kbAttachmentItemSelectorCriterion, this, portletURL,
					search);

		servletRequest.setAttribute(
			KBItemSelectorWebKeys.
				KB_ATTACHMENT_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT,
			kbAttachmentItemSelectorViewDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.unmodifiableList(
			ListUtil.fromArray(
				new FileEntryItemSelectorReturnType(),
				new URLItemSelectorReturnType()));

	@Reference
	private ItemSelectorReturnTypeResolverHandler
		_itemSelectorReturnTypeResolverHandler;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.knowledge.base.item.selector.web)"
	)
	private ServletContext _servletContext;

}