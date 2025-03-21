/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.item.selector.web.internal;

import com.liferay.asset.categories.item.selector.AssetCategoryTreeNodeItemSelectorCriterion;
import com.liferay.asset.categories.item.selector.AssetCategoryTreeNodeItemSelectorReturnType;
import com.liferay.asset.categories.item.selector.web.internal.constants.AssetCategoryItemSelectorWebKeys;
import com.liferay.asset.categories.item.selector.web.internal.display.context.SelectAssetCategoryTreeNodeDisplayContext;
import com.liferay.asset.categories.item.selector.web.internal.display.context.SelectAssetVocabularyDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.language.Language;

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
 * @author Rubén Pulido
 */
@Component(
	property = "item.selector.view.order:Integer=100",
	service = ItemSelectorView.class
)
public class AssetCategoryTreeNodeItemSelectorView
	implements ItemSelectorView<AssetCategoryTreeNodeItemSelectorCriterion> {

	@Override
	public Class<AssetCategoryTreeNodeItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return AssetCategoryTreeNodeItemSelectorCriterion.class;
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
		return _language.get(locale, "source");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			AssetCategoryTreeNodeItemSelectorCriterion
				assetCategoryTreeNodeItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher("/select_asset_vocabulary.jsp");

		SelectAssetCategoryTreeNodeDisplayContext
			selectAssetCategoryLevelDisplayContext =
				new SelectAssetCategoryTreeNodeDisplayContext(
					(HttpServletRequest)servletRequest, itemSelectedEventName,
					portletURL);

		servletRequest.setAttribute(
			AssetCategoryItemSelectorWebKeys.
				SELECT_ASSET_CATEGORY_TREE_NODE_ITEM_SELECTOR_DISPLAY_CONTEXT,
			selectAssetCategoryLevelDisplayContext);

		SelectAssetVocabularyDisplayContext
			selectAssetVocabularyDisplayContext =
				new SelectAssetVocabularyDisplayContext(
					(HttpServletRequest)servletRequest, portletURL);

		servletRequest.setAttribute(
			AssetCategoryItemSelectorWebKeys.
				SELECT_ASSET_VOCABULARY_DISPLAY_CONTEXT,
			selectAssetVocabularyDisplayContext);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new AssetCategoryTreeNodeItemSelectorReturnType());

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.asset.categories.item.selector.web)"
	)
	private ServletContext _servletContext;

}