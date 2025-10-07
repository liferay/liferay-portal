/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal;

import com.liferay.item.selector.TableItemView;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.taglib.search.DateSearchEntry;
import com.liferay.taglib.search.TextSearchEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jonathan McCann
 */
public class ObjectDefinitionTableItemView implements TableItemView {

	public ObjectDefinitionTableItemView(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	@Override
	public List<String> getHeaderNames() {
		return ListUtil.fromArray("label", "scope", "modified-date");
	}

	@Override
	public List<SearchEntry> getSearchEntries(Locale locale) {
		List<SearchEntry> searchEntries = new ArrayList<>();

		TextSearchEntry labelTextSearchEntry = new TextSearchEntry();

		labelTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand table-cell-minw-200");
		labelTextSearchEntry.setName(
			HtmlUtil.escape(_objectDefinition.getLabel(locale)));

		searchEntries.add(labelTextSearchEntry);

		TextSearchEntry scopeTextSearchEntry = new TextSearchEntry();

		scopeTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand table-cell-minw-200");
		scopeTextSearchEntry.setName(
			HtmlUtil.escape(_objectDefinition.getScope()));

		searchEntries.add(scopeTextSearchEntry);

		DateSearchEntry modifiedDateTextDateSearchEntry = new DateSearchEntry();

		modifiedDateTextDateSearchEntry.setCssClass(
			"table-cell-expand-smallest table-cell-ws-nowrap");
		modifiedDateTextDateSearchEntry.setDate(
			_objectDefinition.getModifiedDate());

		searchEntries.add(modifiedDateTextDateSearchEntry);

		return searchEntries;
	}

	private final ObjectDefinition _objectDefinition;

}