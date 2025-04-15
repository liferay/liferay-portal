/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.request;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.rankings.web.internal.display.context.RankingEntryDisplayContext;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingFields;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Wade Cao
 */
public class SearchRankingRequest {

	public SearchRankingRequest(
		HttpServletRequest httpServletRequest, Queries queries,
		RankingIndexName rankingIndexName, Sorts sorts,
		SearchContainer<RankingEntryDisplayContext> searchContainer,
		SearchEngineAdapter searchEngineAdapter) {

		_httpServletRequest = httpServletRequest;
		_queries = queries;
		_rankingIndexName = rankingIndexName;
		_sorts = sorts;
		_searchContainer = searchContainer;
		_searchEngineAdapter = searchEngineAdapter;

		_searchContext = SearchContextFactory.getInstance(httpServletRequest);
	}

	public SearchRankingResponse search() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		BooleanQuery booleanQuery = _getBooleanQuery();

		if (booleanQuery.hasClauses()) {
			searchSearchRequest.setQuery(booleanQuery);
		}
		else {
			searchSearchRequest.setQuery(_queries.matchAll());
		}

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(_rankingIndexName.getIndexName());
		searchSearchRequest.setSelectedFieldNames(StringPool.BLANK);
		searchSearchRequest.setSize(_searchContainer.getDelta());
		searchSearchRequest.setSorts(_getSorts());
		searchSearchRequest.setStart(_searchContainer.getStart());

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchRankingResponse searchRankingResponse =
			new SearchRankingResponse();

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		searchRankingResponse.setSearchHits(searchHits);
		searchRankingResponse.setTotalHits((int)searchHits.getTotalHits());

		return searchRankingResponse;
	}

	private void _addFilterByEverythingScope(BooleanQuery booleanQuery) {
		booleanQuery.addFilterQueryClauses(
			_queries.term(
				RankingFields.SXP_BLUEPRINT_EXTERNAL_REFERENCE_CODE,
				StringPool.BLANK),
			_queries.term(
				RankingFields.GROUP_EXTERNAL_REFERENCE_CODE, StringPool.BLANK));
	}

	private BooleanQuery _getBooleanQuery() {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		String keywords = _searchContext.getKeywords();
		String scope = GetterUtil.getString(
			_httpServletRequest.getParameter("scope"), "all");
		String status = GetterUtil.getString(
			_httpServletRequest.getParameter("status"), "all");

		if (!Validator.isBlank(keywords)) {
			booleanQuery.addMustQueryClauses(
				_queries.booleanQuery(
				).addShouldQueryClauses(
					_queries.term(RankingFields.NAME, keywords),
					_queries.term(RankingFields.ALIASES, keywords)
				));
		}

		if (!Objects.equals(scope, "all")) {
			if (Objects.equals(scope, "blueprint")) {
				booleanQuery.addMustNotQueryClauses(
					_queries.term(
						RankingFields.SXP_BLUEPRINT_EXTERNAL_REFERENCE_CODE,
						StringPool.BLANK));
			}
			else if (Objects.equals(scope, "site")) {
				booleanQuery.addMustNotQueryClauses(
					_queries.term(
						RankingFields.GROUP_EXTERNAL_REFERENCE_CODE,
						StringPool.BLANK));
			}
			else {
				_addFilterByEverythingScope(booleanQuery);
			}
		}

		if (!Objects.equals(status, "all")) {
			booleanQuery.addFilterQueryClauses(
				_queries.term(RankingFields.STATUS, status));
		}

		if (!FeatureFlagManagerUtil.isEnabled("LPD-6368")) {
			_addFilterByEverythingScope(booleanQuery);
		}

		return booleanQuery;
	}

	private String _getOrderByCol() {
		return SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ResultRankingsPortletKeys.RESULT_RANKINGS,
			"search-ranking-order-by-col", RankingFields.QUERY_STRING_KEYWORD);
	}

	private String _getOrderByType() {
		return SearchOrderByUtil.getOrderByType(
			_httpServletRequest, ResultRankingsPortletKeys.RESULT_RANKINGS,
			"search-ranking-order-by-type", "asc");
	}

	private Collection<Sort> _getSorts() {
		List<Sort> sorts = new ArrayList<>();

		SortOrder sortOrder = SortOrder.ASC;

		if (Objects.equals(_getOrderByType(), "desc")) {
			sortOrder = SortOrder.DESC;
		}

		String orderByCol = _getOrderByCol();

		sorts.add(_sorts.field(orderByCol, sortOrder));

		sorts.add(
			_sorts.field(
				RankingFields.GROUP_EXTERNAL_REFERENCE_CODE, SortOrder.ASC));
		sorts.add(
			_sorts.field(
				RankingFields.SXP_BLUEPRINT_EXTERNAL_REFERENCE_CODE,
				SortOrder.ASC));

		if (orderByCol.equals(RankingFields.STATUS)) {
			sorts.add(
				_sorts.field(
					RankingFields.QUERY_STRING_KEYWORD, SortOrder.ASC));
		}
		else if (orderByCol.equals(RankingFields.QUERY_STRING_KEYWORD)) {
			sorts.add(_sorts.field(RankingFields.STATUS, SortOrder.ASC));
		}

		sorts.add(
			_sorts.field(RankingFields.QUERY_STRINGS_KEYWORD, SortOrder.ASC));

		return sorts;
	}

	private final HttpServletRequest _httpServletRequest;
	private final Queries _queries;
	private final RankingIndexName _rankingIndexName;
	private final SearchContainer<RankingEntryDisplayContext> _searchContainer;
	private final SearchContext _searchContext;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final Sorts _sorts;

}