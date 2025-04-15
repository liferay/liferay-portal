/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.vocabulary.item.selector.web.internal;

import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorCriterion;
import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;

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
 * @author Lourdes Fernández Besada
 */
@Component(service = ItemSelectorView.class)
public class AssetVocabularyItemSelectorView
	implements ItemSelectorView<AssetVocabularyItemSelectorCriterion> {

	@Override
	public Class<AssetVocabularyItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return AssetVocabularyItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "vocabularies");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			AssetVocabularyItemSelectorCriterion
				assetVocabularyItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			assetVocabularyItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new AssetVocabularyItemSelectorViewDescriptor(
				assetVocabularyItemSelectorCriterion,
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new AssetVocabularyItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<AssetVocabularyItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}