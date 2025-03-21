/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.item.selector.RelatedInfoItemCollectionProviderItemSelectorCriterion;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ItemSelectorView.class)
public class RelatedInfoItemCollectionProviderItemSelectorView
	implements ItemSelectorView
		<RelatedInfoItemCollectionProviderItemSelectorCriterion> {

	@Override
	public Class
		<? extends RelatedInfoItemCollectionProviderItemSelectorCriterion>
			getItemSelectorCriterionClass() {

		return RelatedInfoItemCollectionProviderItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "related-items-collection-provider");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			RelatedInfoItemCollectionProviderItemSelectorCriterion
				relatedInfoItemCollectionProviderItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			relatedInfoItemCollectionProviderItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new RelatedInfoItemCollectionProviderItemSelectorViewDescriptor(
				(HttpServletRequest)servletRequest, portletURL,
				_getRelatedInfoItemCollectionProviders(
					relatedInfoItemCollectionProviderItemSelectorCriterion)));
	}

	private List<RelatedInfoItemCollectionProvider<?, ?>>
		_getRelatedInfoItemCollectionProviders(
			RelatedInfoItemCollectionProviderItemSelectorCriterion
				relatedInfoItemCollectionProviderItemSelectorCriterion) {

		List<RelatedInfoItemCollectionProvider<?, ?>>
			itemRelatedItemsProviders = new ArrayList<>();

		List<String> itemTypes =
			relatedInfoItemCollectionProviderItemSelectorCriterion.
				getSourceItemTypes();

		for (String itemType : itemTypes) {
			itemRelatedItemsProviders.addAll(
				ListUtil.filter(
					_infoItemServiceRegistry.getAllInfoItemServices(
						(Class<RelatedInfoItemCollectionProvider<?, ?>>)
							(Class<?>)RelatedInfoItemCollectionProvider.class,
						itemType),
					RelatedInfoItemCollectionProvider::isAvailable));
		}

		return Collections.unmodifiableList(itemRelatedItemsProviders);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new InfoListProviderItemSelectorReturnType());

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<RelatedInfoItemCollectionProviderItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}