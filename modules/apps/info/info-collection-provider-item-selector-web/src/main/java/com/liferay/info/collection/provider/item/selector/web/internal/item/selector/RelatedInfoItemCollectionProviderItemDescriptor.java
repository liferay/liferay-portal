/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.collection.provider.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.RelatedInfoItemCollectionProviderVerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Diego Hu
 */
public class RelatedInfoItemCollectionProviderItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public RelatedInfoItemCollectionProviderItemDescriptor(
		HttpServletRequest httpServletRequest,
		RelatedInfoItemCollectionProvider<?, ?>
			relatedInfoItemCollectionProvider) {

		_httpServletRequest = httpServletRequest;
		_relatedInfoItemCollectionProvider = relatedInfoItemCollectionProvider;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"itemSubtype",
			() -> {
				if (!(_relatedInfoItemCollectionProvider instanceof
						SingleFormVariationInfoCollectionProvider)) {

					return null;
				}

				SingleFormVariationInfoCollectionProvider<?>
					singleFormVariationInfoCollectionProvider =
						(SingleFormVariationInfoCollectionProvider<?>)
							_relatedInfoItemCollectionProvider;

				return singleFormVariationInfoCollectionProvider.
					getFormVariationKey();
			}
		).put(
			"itemType",
			_relatedInfoItemCollectionProvider.getCollectionItemClassName()
		).put(
			"key", _relatedInfoItemCollectionProvider.getKey()
		).put(
			"sourceItemType",
			_relatedInfoItemCollectionProvider.getSourceItemClassName()
		).put(
			"title",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _relatedInfoItemCollectionProvider.getLabel(
					themeDisplay.getLocale());
			}
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return null;
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new RelatedInfoItemCollectionProviderVerticalCard(
			renderRequest, _relatedInfoItemCollectionProvider, rowChecker);
	}

	private final HttpServletRequest _httpServletRequest;
	private final RelatedInfoItemCollectionProvider<?, ?>
		_relatedInfoItemCollectionProvider;

}