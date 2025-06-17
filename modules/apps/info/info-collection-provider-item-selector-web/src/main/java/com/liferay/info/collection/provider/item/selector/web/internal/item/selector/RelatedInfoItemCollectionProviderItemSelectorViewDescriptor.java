/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Diego Hu
 */
public class RelatedInfoItemCollectionProviderItemSelectorViewDescriptor
	extends BaseItemSelectorViewDescriptor
		<RelatedInfoItemCollectionProvider<?, ?>> {

	public RelatedInfoItemCollectionProviderItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		List<RelatedInfoItemCollectionProvider<?, ?>>
			relatedInfoItemCollectionProviders) {

		super(
			httpServletRequest, portletURL, relatedInfoItemCollectionProviders);
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		RelatedInfoItemCollectionProvider<?, ?>
			relatedInfoItemCollectionProvider) {

		return new RelatedInfoItemCollectionProviderItemDescriptor(
			httpServletRequest, relatedInfoItemCollectionProvider);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new InfoListProviderItemSelectorReturnType();
	}

	public SearchContainer<RelatedInfoItemCollectionProvider<?, ?>>
		getSearchContainer() {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		SearchContainer<RelatedInfoItemCollectionProvider<?, ?>>
			searchContainer = new SearchContainer<>(
				portletRequest, portletURL, null,
				"there-are-no-related-items-collection-providers");

		List<RelatedInfoItemCollectionProvider<?, ?>>
			filteredRelatedInfoItemCollectionProviders = new ArrayList<>(
				infoCollectionProviders);

		if (Validator.isNotNull(getSelectedItemType())) {
			filteredRelatedInfoItemCollectionProviders = ListUtil.filter(
				filteredRelatedInfoItemCollectionProviders,
				relatedInfoItemCollectionProvider -> Objects.equals(
					relatedInfoItemCollectionProvider.
						getCollectionItemClassName(),
					getSelectedItemType()));
		}

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			filteredRelatedInfoItemCollectionProviders = ListUtil.filter(
				filteredRelatedInfoItemCollectionProviders,
				relatedInfoItemCollectionProvider -> {
					String label = StringUtil.toLowerCase(
						relatedInfoItemCollectionProvider.getLabel(
							themeDisplay.getLocale()));

					return label.contains(StringUtil.toLowerCase(keywords));
				});
		}

		searchContainer.setResultsAndTotal(
			filteredRelatedInfoItemCollectionProviders);

		return searchContainer;
	}

}