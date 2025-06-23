/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.item.selector.web.internal;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.item.selector.TableItemView;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.taglib.search.TextSearchEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class InfoFieldTableItemView implements TableItemView {

	public InfoFieldTableItemView(InfoField<?> infoField) {
		_infoField = infoField;
	}

	@Override
	public List<String> getHeaderNames() {
		return ListUtil.fromArray("name", "type", "mandatory", "localizable");
	}

	@Override
	public List<SearchEntry> getSearchEntries(Locale locale) {
		List<SearchEntry> searchEntries = new ArrayList<>();

		TextSearchEntry nameTextSearchEntry = new TextSearchEntry();

		nameTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand text-truncate");
		nameTextSearchEntry.setName(
			HtmlUtil.escape(_infoField.getLabel(locale)));

		searchEntries.add(nameTextSearchEntry);

		TextSearchEntry typeTextSearchEntry = new TextSearchEntry();

		InfoFieldType infoFieldType = _infoField.getInfoFieldType();

		typeTextSearchEntry.setCssClass("text-truncate");
		typeTextSearchEntry.setName(infoFieldType.getLabel(locale));

		searchEntries.add(typeTextSearchEntry);

		TextSearchEntry mandatoryTextSearchEntry = new TextSearchEntry();

		if (_infoField.isRequired()) {
			mandatoryTextSearchEntry.setName(LanguageUtil.get(locale, "yes"));
		}
		else {
			mandatoryTextSearchEntry.setName(LanguageUtil.get(locale, "no"));
		}

		searchEntries.add(mandatoryTextSearchEntry);

		TextSearchEntry localizableTextSearchEntry = new TextSearchEntry();

		if (_infoField.isLocalizable()) {
			localizableTextSearchEntry.setName(LanguageUtil.get(locale, "yes"));
		}
		else {
			localizableTextSearchEntry.setName(LanguageUtil.get(locale, "no"));
		}

		searchEntries.add(localizableTextSearchEntry);

		return searchEntries;
	}

	private final InfoField<?> _infoField;

}