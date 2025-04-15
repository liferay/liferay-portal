/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.item.selector;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.collection.provider.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.InfoCollectionProviderVerticalCard;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class InfoCollectionProviderItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public InfoCollectionProviderItemDescriptor(
		HttpServletRequest httpServletRequest,
		InfoCollectionProvider<?> infoCollectionProvider,
		InfoItemServiceRegistry infoItemServiceRegistry) {

		_httpServletRequest = httpServletRequest;
		_infoCollectionProvider = infoCollectionProvider;
		_infoItemServiceRegistry = infoItemServiceRegistry;
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
				if (!(_infoCollectionProvider instanceof
						SingleFormVariationInfoCollectionProvider)) {

					return null;
				}

				SingleFormVariationInfoCollectionProvider<?>
					singleFormVariationInfoCollectionProvider =
						(SingleFormVariationInfoCollectionProvider<?>)
							_infoCollectionProvider;

				return singleFormVariationInfoCollectionProvider.
					getFormVariationKey();
			}
		).put(
			"itemType", _infoCollectionProvider.getCollectionItemClassName()
		).put(
			"key", _infoCollectionProvider.getKey()
		).put(
			"title",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return _infoCollectionProvider.getLabel(
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

		return new InfoCollectionProviderVerticalCard(
			_infoCollectionProvider, _infoItemServiceRegistry, renderRequest,
			rowChecker);
	}

	private final HttpServletRequest _httpServletRequest;
	private final InfoCollectionProvider<?> _infoCollectionProvider;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;

}