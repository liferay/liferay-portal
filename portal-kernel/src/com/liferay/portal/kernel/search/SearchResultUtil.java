/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.result.SearchResultTranslator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class SearchResultUtil {

	public static List<SearchResult> getSearchResults(
		Hits hits, Locale locale) {

		return getSearchResults(hits, locale, null, null);
	}

	public static List<SearchResult> getSearchResults(
		Hits hits, Locale locale, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		SearchResultTranslator searchResultTranslator =
			_searchResultTranslatorSnapshot.get();

		return searchResultTranslator.translate(
			hits, locale, portletRequest, portletResponse);
	}

	private static final Snapshot<SearchResultTranslator>
		_searchResultTranslatorSnapshot = new Snapshot<>(
			SearchResultUtil.class, SearchResultTranslator.class);

}