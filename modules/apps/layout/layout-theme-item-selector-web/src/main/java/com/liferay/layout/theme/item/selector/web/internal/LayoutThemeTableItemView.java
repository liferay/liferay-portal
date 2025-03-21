/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.theme.item.selector.web.internal;

import com.liferay.item.selector.TableItemView;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.search.TextSearchEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Stefan Tanasie
 */
public class LayoutThemeTableItemView implements TableItemView {

	public LayoutThemeTableItemView(
		Theme theme, HttpServletRequest httpServletRequest) {

		_theme = theme;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public List<String> getHeaderNames() {
		return ListUtil.fromArray("name", "author");
	}

	@Override
	public List<SearchEntry> getSearchEntries(Locale locale) {
		List<SearchEntry> searchEntries = new ArrayList<>();

		TextSearchEntry nameTextSearchEntry = new TextSearchEntry();

		nameTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand table-cell-minw-200");
		nameTextSearchEntry.setName(_theme.getName());

		searchEntries.add(nameTextSearchEntry);

		TextSearchEntry authorTextSearchEntry = new TextSearchEntry();

		authorTextSearchEntry.setCssClass(
			"table-cell-expand-smaller table-cell-minw-150");
		authorTextSearchEntry.setName(_getAuthor());

		searchEntries.add(authorTextSearchEntry);

		return searchEntries;
	}

	private String _getAuthor() {
		PluginPackage selPluginPackage = _theme.getPluginPackage();

		if ((selPluginPackage != null) &&
			Validator.isNotNull(selPluginPackage.getAuthor())) {

			return LanguageUtil.format(
				_httpServletRequest, "by-x", selPluginPackage.getAuthor());
		}

		return StringPool.DASH;
	}

	private final HttpServletRequest _httpServletRequest;
	private final Theme _theme;

}