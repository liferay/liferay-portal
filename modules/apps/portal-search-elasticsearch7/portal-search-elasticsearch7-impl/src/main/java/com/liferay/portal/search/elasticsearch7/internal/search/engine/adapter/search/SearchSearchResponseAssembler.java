/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import org.elasticsearch.action.search.SearchResponse;

/**
 * @author Michael C. Han
 */
public interface SearchSearchResponseAssembler {

	public void assemble(
		String searchRequestString, SearchResponse searchResponse,
		SearchSearchRequest searchSearchRequest,
		SearchSearchResponse searchSearchResponse);

}