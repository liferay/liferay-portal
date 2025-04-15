/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.item.selector.web.internal.display.context.AssetListEntryItemSelectorDisplayContext;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AssetListEntryVerticalCard extends BaseVerticalCard {

	public AssetListEntryVerticalCard(
		AssetListEntry assetListEntry,
		AssetListEntryItemSelectorDisplayContext
			assetListEntryItemSelectorDisplayContext,
		RenderRequest renderRequest, RowChecker rowChecker) {

		super(null, renderRequest, rowChecker);

		_assetListEntry = assetListEntry;
		_assetListEntryItemSelectorDisplayContext =
			assetListEntryItemSelectorDisplayContext;
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary";
	}

	@Override
	public String getIcon() {
		if (_assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			return "bolt";
		}

		return "list";
	}

	@Override
	public String getInputValue() {
		return null;
	}

	@Override
	public List<LabelItem> getLabels() {
		int assetListEntrySegmentsEntryRelsCount =
			_assetListEntryItemSelectorDisplayContext.
				getAssetListEntrySegmentsEntryRelsCount(_assetListEntry);

		if (assetListEntrySegmentsEntryRelsCount > 0) {
			return LabelItemListBuilder.add(
				labelItem -> {
					labelItem.setDisplayType("info");
					labelItem.setLabel(
						LanguageUtil.format(
							themeDisplay.getLocale(), "x-variations",
							new String[] {
								String.valueOf(
									assetListEntrySegmentsEntryRelsCount)
							}));
				}
			).build();
		}

		return LabelItemListBuilder.add(
			labelItem -> labelItem.setLabel(
				LanguageUtil.get(themeDisplay.getLocale(), "no-variations"))
		).build();
	}

	@Override
	public String getStickerIcon() {
		return "filter";
	}

	@Override
	public String getSubtitle() {
		String type = _assetListEntryItemSelectorDisplayContext.getType(
			_assetListEntry, themeDisplay.getLocale());

		String subtype = _assetListEntryItemSelectorDisplayContext.getSubtype(
			_assetListEntry);

		if (Validator.isNull(subtype)) {
			return type;
		}

		return type + " - " + subtype;
	}

	@Override
	public String getTitle() {
		return _assetListEntry.getTitle();
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	private final AssetListEntry _assetListEntry;
	private final AssetListEntryItemSelectorDisplayContext
		_assetListEntryItemSelectorDisplayContext;

}