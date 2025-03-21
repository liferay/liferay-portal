/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.result;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultManager;
import com.liferay.portal.kernel.search.result.SearchResultTranslator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author André de Oliveira
 */
@Component(service = SearchResultTranslator.class)
public class SearchResultTranslatorImpl implements SearchResultTranslator {

	@Override
	public List<SearchResult> translate(
		Hits hits, Locale locale, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		List<SearchResult> searchResults = new ArrayList<>();

		for (Document document : hits.getDocs()) {
			try {
				SearchResult searchResult =
					_searchResultManager.createSearchResult(document);

				int index = searchResults.indexOf(searchResult);

				if (index < 0) {
					searchResults.add(searchResult);
				}
				else {
					searchResult = searchResults.get(index);
				}

				String version = document.get(Field.VERSION);

				if (Validator.isNotNull(version)) {
					searchResult.addVersion(version);
				}

				_searchResultManager.updateSearchResult(
					searchResult, document, locale, portletRequest,
					portletResponse);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					long entryClassPK = GetterUtil.getLong(
						document.get(Field.ENTRY_CLASS_PK));

					_log.warn(
						"Search index is stale and contains entry {" +
							entryClassPK + "}",
						exception);
				}
			}
		}

		return searchResults;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchResultTranslatorImpl.class);

	@Reference
	private SearchResultManager _searchResultManager;

}