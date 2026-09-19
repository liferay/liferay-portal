/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class SuggestSearchResponse implements SearchResponse {

	public void addSuggestSearchResult(
		SuggestSearchResult suggestSearchResult) {

		_suggestSearchResultMap.put(
			suggestSearchResult.getName(), suggestSearchResult);
	}

	public Map<String, SuggestSearchResult> getSuggestSearchResultMap() {
		return Collections.unmodifiableMap(_suggestSearchResultMap);
	}

	public Collection<SuggestSearchResult> getSuggestSearchResults() {
		return Collections.unmodifiableCollection(
			_suggestSearchResultMap.values());
	}

	public Collection<String> getSuggesterNames() {
		return Collections.unmodifiableCollection(
			_suggestSearchResultMap.keySet());
	}

	public SuggestSearchResult getSuggesterResult(String name) {
		return _suggestSearchResultMap.get(name);
	}

	private final Map<String, SuggestSearchResult> _suggestSearchResultMap =
		new HashMap<>();

}