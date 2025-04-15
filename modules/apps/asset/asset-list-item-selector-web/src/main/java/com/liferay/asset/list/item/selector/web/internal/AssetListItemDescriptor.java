/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal;

import com.liferay.asset.list.item.selector.web.internal.display.context.AssetListEntryItemSelectorDisplayContext;
import com.liferay.asset.list.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.AssetListEntryVerticalCard;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class AssetListItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public AssetListItemDescriptor(
		AssetListEntry assetListEntry,
		AssetListEntryItemSelectorDisplayContext
			assetListEntryItemSelectorDisplayContext,
		HttpServletRequest httpServletRequest) {

		_assetListEntry = assetListEntry;
		_assetListEntryItemSelectorDisplayContext =
			assetListEntryItemSelectorDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
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
			"classNameId",
			String.valueOf(PortalUtil.getClassNameId(AssetListEntry.class))
		).put(
			"classPK", _assetListEntry.getAssetListEntryId()
		).put(
			"itemSubtype", _assetListEntry.getAssetEntrySubtype()
		).put(
			"itemType", _assetListEntry.getAssetEntryType()
		).put(
			"title", _assetListEntry.getTitle()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		String type = _assetListEntryItemSelectorDisplayContext.getType(
			_assetListEntry, _themeDisplay.getLocale());

		String subtype = _assetListEntryItemSelectorDisplayContext.getSubtype(
			_assetListEntry);

		if (Validator.isNull(subtype)) {
			return type;
		}

		return type + " - " + subtype;
	}

	@Override
	public String getTitle(Locale locale) {
		return _assetListEntry.getTitle();
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new AssetListEntryVerticalCard(
			_assetListEntry, _assetListEntryItemSelectorDisplayContext,
			renderRequest, rowChecker);
	}

	private final AssetListEntry _assetListEntry;
	private final AssetListEntryItemSelectorDisplayContext
		_assetListEntryItemSelectorDisplayContext;
	private final ThemeDisplay _themeDisplay;

}