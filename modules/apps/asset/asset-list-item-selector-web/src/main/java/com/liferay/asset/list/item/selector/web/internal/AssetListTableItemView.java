/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.item.selector.web.internal.display.context.AssetListEntryItemSelectorDisplayContext;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.item.selector.TableItemView;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.search.IconSearchEntry;
import com.liferay.taglib.search.TextSearchEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class AssetListTableItemView implements TableItemView {

	public AssetListTableItemView(
		AssetListEntry assetListEntry,
		AssetListEntryItemSelectorDisplayContext
			assetListEntryItemSelectorDisplayContext) {

		_assetListEntry = assetListEntry;
		_assetListEntryItemSelectorDisplayContext =
			assetListEntryItemSelectorDisplayContext;
	}

	@Override
	public List<String> getHeaderNames() {
		return ListUtil.fromArray(
			"", "name", "type", "item-type", "subtype", "variations", "usages",
			"modified");
	}

	@Override
	public List<SearchEntry> getSearchEntries(Locale locale) {
		List<SearchEntry> searchEntries = new ArrayList<>();

		IconSearchEntry iconSearchEntry = new IconSearchEntry();

		if (_assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			iconSearchEntry.setIcon("bolt");
		}
		else {
			iconSearchEntry.setIcon("list");
		}

		searchEntries.add(iconSearchEntry);

		TextSearchEntry nameTextSearchEntry = new TextSearchEntry();

		nameTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand text-truncate");
		nameTextSearchEntry.setName(
			HtmlUtil.escape(_assetListEntry.getTitle()));

		searchEntries.add(nameTextSearchEntry);

		TextSearchEntry typeTextSearchEntry = new TextSearchEntry();

		typeTextSearchEntry.setCssClass("text-truncate");
		typeTextSearchEntry.setName(
			LanguageUtil.get(
				locale, HtmlUtil.escape(_assetListEntry.getTypeLabel())));

		searchEntries.add(typeTextSearchEntry);

		TextSearchEntry itemTypeTextSearchEntry = new TextSearchEntry();

		itemTypeTextSearchEntry.setCssClass("table-cell-expand text-truncate");
		itemTypeTextSearchEntry.setName(
			_assetListEntryItemSelectorDisplayContext.getType(
				_assetListEntry, locale));

		searchEntries.add(itemTypeTextSearchEntry);

		TextSearchEntry subtypeTextSearchEntry = new TextSearchEntry();

		subtypeTextSearchEntry.setCssClass("table-cell-expand text-truncate");

		String subtype = _assetListEntryItemSelectorDisplayContext.getSubtype(
			_assetListEntry);

		if (Validator.isNull(subtype)) {
			subtype = StringPool.DASH;
		}

		subtypeTextSearchEntry.setName(subtype);

		searchEntries.add(subtypeTextSearchEntry);

		TextSearchEntry variationsTextSearchEntry = new TextSearchEntry();

		variationsTextSearchEntry.setCssClass("text-truncate");
		variationsTextSearchEntry.setName(
			String.valueOf(
				_assetListEntryItemSelectorDisplayContext.
					getAssetListEntryVariationsCount(_assetListEntry)));

		searchEntries.add(variationsTextSearchEntry);

		TextSearchEntry usagesTextSearchEntry = new TextSearchEntry();

		usagesTextSearchEntry.setCssClass("text-truncate");
		usagesTextSearchEntry.setName(
			String.valueOf(
				_assetListEntryItemSelectorDisplayContext.
					getAssetListEntryUsageCount(_assetListEntry)));

		searchEntries.add(usagesTextSearchEntry);

		TextSearchEntry modifiedSearchEntry = new TextSearchEntry();

		modifiedSearchEntry.setCssClass("table-cell-expand text-truncate");

		Date modifiedDate = _assetListEntry.getModifiedDate();

		if (Objects.nonNull(modifiedDate)) {
			modifiedSearchEntry.setName(
				LanguageUtil.format(
					locale, "x-ago",
					LanguageUtil.getTimeDescription(
						locale,
						System.currentTimeMillis() - modifiedDate.getTime(),
						true)));
		}

		searchEntries.add(modifiedSearchEntry);

		return searchEntries;
	}

	private final AssetListEntry _assetListEntry;
	private final AssetListEntryItemSelectorDisplayContext
		_assetListEntryItemSelectorDisplayContext;

}