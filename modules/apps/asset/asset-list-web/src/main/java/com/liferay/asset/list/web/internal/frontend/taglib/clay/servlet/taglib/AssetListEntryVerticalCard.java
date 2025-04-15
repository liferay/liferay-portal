/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.web.internal.display.context.AssetListDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public class AssetListEntryVerticalCard extends BaseVerticalCard {

	public AssetListEntryVerticalCard(
		AssetListDisplayContext assetListDisplayContext,
		AssetListEntry assetListEntry, RenderRequest renderRequest,
		RowChecker rowChecker) {

		super(assetListEntry, renderRequest, rowChecker);

		_assetListDisplayContext = assetListDisplayContext;
		_assetListEntry = assetListEntry;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return _assetListDisplayContext.getActionDropdownItems(_assetListEntry);
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary";
	}

	@Override
	public String getHref() {
		try {
			return _assetListDisplayContext.getEditURL(_assetListEntry);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
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
			_assetListDisplayContext.getAssetListEntrySegmentsEntryRelsCount(
				_assetListEntry);

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
		return StringPool.BLANK;
	}

	@Override
	public String getSubtitle() {
		return _assetListDisplayContext.getAssetEntrySubtypeLabel(
			_assetListEntry);
	}

	@Override
	public String getTitle() {
		return _assetListEntry.getTitle();
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryVerticalCard.class);

	private final AssetListDisplayContext _assetListDisplayContext;
	private final AssetListEntry _assetListEntry;

}