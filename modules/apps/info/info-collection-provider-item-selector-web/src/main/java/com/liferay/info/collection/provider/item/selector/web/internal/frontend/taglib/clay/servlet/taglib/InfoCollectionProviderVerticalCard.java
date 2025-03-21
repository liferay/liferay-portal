/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.collection.provider.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.info.collection.provider.BetaInfoCollectionProvider;
import com.liferay.info.collection.provider.DeprecatedInfoCollectionProvider;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class InfoCollectionProviderVerticalCard extends BaseVerticalCard {

	public InfoCollectionProviderVerticalCard(
		InfoCollectionProvider<?> infoCollectionProvider,
		InfoItemServiceRegistry infoItemServiceRegistry,
		RenderRequest renderRequest, RowChecker rowChecker) {

		super(null, renderRequest, rowChecker);

		_infoCollectionProvider = infoCollectionProvider;
		_infoItemServiceRegistry = infoItemServiceRegistry;
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary";
	}

	@Override
	public String getIcon() {
		return "list";
	}

	@Override
	public String getInputValue() {
		return null;
	}

	@Override
	public List<LabelItem> getLabels() {
		if (_infoCollectionProvider instanceof BetaInfoCollectionProvider) {
			return LabelItemListBuilder.add(
				labelItem -> {
					labelItem.setDisplayType("info");
					labelItem.setLabel(
						LanguageUtil.get(themeDisplay.getLocale(), "beta"));
				}
			).build();
		}

		if (_infoCollectionProvider instanceof
				DeprecatedInfoCollectionProvider<?>) {

			return LabelItemListBuilder.add(
				labelItem -> {
					labelItem.setDisplayType("warning");
					labelItem.setLabel(
						LanguageUtil.get(
							themeDisplay.getLocale(), "deprecated"));
				}
			).build();
		}

		return super.getLabels();
	}

	@Override
	public String getStickerIcon() {
		if (_infoCollectionProvider instanceof FilteredInfoCollectionProvider) {
			return "filter";
		}

		return super.getStickerIcon();
	}

	@Override
	public String getSubtitle() {
		String subtitle = _getSubtitle();

		if (Validator.isNull(subtitle)) {
			return _getTitle();
		}

		return _getTitle() + " - " + subtitle;
	}

	@Override
	public String getTitle() {
		return _infoCollectionProvider.getLabel(themeDisplay.getLocale());
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	private String _getSubtitle() {
		String className = _infoCollectionProvider.getCollectionItemClassName();

		if (Validator.isNull(className) ||
			!(_infoCollectionProvider instanceof
				SingleFormVariationInfoCollectionProvider)) {

			return StringPool.BLANK;
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		if (infoItemFormVariationsProvider == null) {
			return StringPool.BLANK;
		}

		SingleFormVariationInfoCollectionProvider<?>
			singleFormVariationInfoCollectionProvider =
				(SingleFormVariationInfoCollectionProvider<?>)
					_infoCollectionProvider;

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				themeDisplay.getScopeGroupId(),
				singleFormVariationInfoCollectionProvider.
					getFormVariationKey());

		if (infoItemFormVariation == null) {
			return StringPool.BLANK;
		}

		return infoItemFormVariation.getLabel(themeDisplay.getLocale());
	}

	private String _getTitle() {
		String className = _infoCollectionProvider.getCollectionItemClassName();

		if (Validator.isNull(className)) {
			return StringPool.BLANK;
		}

		return ResourceActionsUtil.getModelResource(
			themeDisplay.getLocale(), className);
	}

	private final InfoCollectionProvider<?> _infoCollectionProvider;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;

}