/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.item.selector;

import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletURL;

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
 * @author Eudaldo Alonso
 */
@Component(service = ItemSelectorView.class)
public class StyleBookEntryItemSelectorView
	implements ItemSelectorView<StyleBookEntryItemSelectorCriterion> {

	@Override
	public Class<StyleBookEntryItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return StyleBookEntryItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "style-books");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			StyleBookEntryItemSelectorCriterion
				styleBookEntryItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			styleBookEntryItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new StyleBookEntryItemSelectorViewDescriptor(
				_frontendTokenDefinitionRegistry,
				(HttpServletRequest)servletRequest, portletURL,
				styleBookEntryItemSelectorCriterion,
				_styleBookEntryLocalService));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new UUIDItemSelectorReturnType());

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<StyleBookEntryItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}