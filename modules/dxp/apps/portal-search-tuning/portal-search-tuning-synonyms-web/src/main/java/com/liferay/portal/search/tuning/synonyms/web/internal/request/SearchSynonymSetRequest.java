/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.display.context.SynonymSetDisplayContext;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFields;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Adam Brandizzi
 */
public class SearchSynonymSetRequest {

	public SearchSynonymSetRequest(
		SynonymSetIndexName synonymSetIndexName,
		HttpServletRequest httpServletRequest, Queries queries, Sorts sorts,
		SearchContainer<SynonymSetDisplayContext> searchContainer,
		SearchEngineAdapter searchEngineAdapter) {

		_synonymSetIndexName = synonymSetIndexName;
		_httpServletRequest = httpServletRequest;
		_queries = queries;
		_sorts = sorts;
		_searchContainer = searchContainer;
		_searchEngineAdapter = searchEngineAdapter;

		_searchContext = SearchContextFactory.getInstance(httpServletRequest);
	}

	public SearchSynonymSetResponse search() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(_synonymSetIndexName.getIndexName());
		searchSearchRequest.setPreferLocalCluster(false);
		searchSearchRequest.setQuery(_getQuery());
		searchSearchRequest.setSize(_searchContainer.getDelta());
		searchSearchRequest.setSorts(_getSorts());
		searchSearchRequest.setStart(_searchContainer.getStart());

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchSynonymSetResponse searchRankingResponse =
			new SearchSynonymSetResponse();

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		searchRankingResponse.setSearchHits(searchHits);
		searchRankingResponse.setTotalHits((int)searchHits.getTotalHits());

		return searchRankingResponse;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, SynonymsPortletKeys.SYNONYMS,
			SynonymSetFields.SYNONYMS_KEYWORD);

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, SynonymsPortletKeys.SYNONYMS, "asc");

		return _orderByType;
	}

	private Query _getQuery() {
		String keywords = _searchContext.getKeywords();

		if (!Validator.isBlank(keywords)) {
			return _queries.match(SynonymSetFields.SYNONYMS, keywords);
		}

		return _queries.matchAll();
	}

	private Collection<Sort> _getSorts() {
		SortOrder sortOrder = SortOrder.ASC;

		if (Objects.equals(_getOrderByType(), "desc")) {
			sortOrder = SortOrder.DESC;
		}

		return Arrays.asList(_sorts.field(_getOrderByCol(), sortOrder));
	}

	private final HttpServletRequest _httpServletRequest;
	private String _orderByCol;
	private String _orderByType;
	private final Queries _queries;
	private final SearchContainer<SynonymSetDisplayContext> _searchContainer;
	private final SearchContext _searchContext;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final Sorts _sorts;
	private final SynonymSetIndexName _synonymSetIndexName;

}